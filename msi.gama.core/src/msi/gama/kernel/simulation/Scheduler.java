/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.simulation;

import java.util.List;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IOutputManager;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;

// TODO change to SimulationScheduler or SimulationControler or GlobalScheduler
public class Scheduler implements IScheduler {

	protected final ISimulation simulation;
	public final Thread executionThread;

	// Flag indicating that the simulation has been launched and is alive (maybe paused)
	public volatile boolean alive = false;
	// Flag indicating that the simulation is set to pause (it should be alive unless a reload has
	// been requested)
	public volatile boolean paused = true;
	// Flag indicating that the simulation is set to step (temporary flag)
	public volatile boolean stepped = false;

	/** Actions that should be run by the scheduler at the beginning of a cycle. */
	private final List<IScheduledAction> beginActions;
	private int beginActionsNumber;
	/** Actions that should be run by the scheduler at the end of a cycle. */
	private final List<IScheduledAction> endActions;
	private int endActionsNumber;
	private final List<IAgent> agentsToInit;

	private boolean inInitSequence = true;

	private static int threadCount = 0;

	private IAgent world;

	protected Scheduler(final ISimulation sim) {
		simulation = sim;
		paused = true;
		threadCount++;
		executionThread = new Thread(null, this, "Scheduler execution thread #" + threadCount);
		beginActions = new GamaList();
		endActions = new GamaList();
		agentsToInit = new GamaList();
		SimulationClock.reset();
	}

	@Override
	public void run() {
		IOutputManager m = GAMA.getExperiment().getOutputManager();
		while (alive) {
			try {
				IScheduler.SCHEDULER_AUTHORIZATION.acquire();

				if ( !paused && alive ) {
					step(simulation.getExecutionScope());
				}
				if ( alive ) {
					m.step(simulation.getExecutionScope());
				}
				if ( alive ) {
					m.updateOutputs();
				}

				paused = stepped ? true : paused;
				stepped = false;

				if ( !paused && alive ) {
					IScheduler.SCHEDULER_AUTHORIZATION.release();
				}
			} catch (GamaRuntimeException e) {
				GAMA.reportError(e);
				alive = false;
				continue;
			} catch (InterruptedException e) {
				alive = false;
				continue;
			}
		}
	}

	@Override
	public void dispose() {
		alive = false;
		executionThread.interrupt();
		try {
			executionThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		beginActions.clear();
		beginActionsNumber = 0;
		endActions.clear();
		endActionsNumber = 0;
		agentsToInit.clear();
	}

	@Override
	public void stepByStep() {
		stepped = true;
		paused = false;
		alive = true;
		IScheduler.SCHEDULER_AUTHORIZATION.release();
		startThread();
	}

	@Override
	public void start() {
		alive = true;
		paused = false;
		stepped = false;
		IScheduler.SCHEDULER_AUTHORIZATION.release();
		startThread();
	}

	private void startThread() {
		if ( !executionThread.isAlive() ) {
			executionThread.start();
		}
	}

	public void pause() {
		alive = true;
		paused = true;
		stepped = false;
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		SimulationClock.resetStepLength();
		GuiUtils.informStatus("step " + SimulationClock.getCycle());
		executeBeginActions(scope);
		// Another strategy would be to run these agents hierarchically (all the level 1 first, then
		// all the level 2, etc.). Maybe we can propose this strategy.
		GamaList<IAgent> listOfAgentsToSchedule = GamaList.with(world);
		world.computeAgentsToSchedule(scope, listOfAgentsToSchedule);
		for ( IAgent a : listOfAgentsToSchedule ) {
			if ( a != null ) {
				a.step(scope);
			}
		}
		executeEndActions(scope);
		SimulationClock.step();
	}

	@Override
	public boolean inInitSequence() {
		return inInitSequence;
	}

	protected void enterInitSequence() throws GamaRuntimeException, InterruptedException {
		executeAgentsToInit(simulation.getExecutionScope());
		inInitSequence = false;
		world = simulation.getWorld();
	}

	private void executeEndActions(final IScope scope) throws GamaRuntimeException {
		if ( endActionsNumber > 0 ) {
			ScheduledAction[] actions = endActions.toArray(new ScheduledAction[endActionsNumber]);
			for ( int i = 0, n = endActionsNumber; i < n; i++ ) {
				actions[i].execute(scope);
			}
		}
	}

	private void executeBeginActions(final IScope scope) throws GamaRuntimeException {
		if ( beginActionsNumber > 0 ) {
			ScheduledAction[] actions =
				beginActions.toArray(new ScheduledAction[beginActionsNumber]);
			for ( int i = 0, n = beginActionsNumber; i < n; i++ ) {
				actions[i].execute(scope);
			}
		}
	}

	private void executeAgentsToInit(final IScope scope) throws GamaRuntimeException,
		InterruptedException {
		int total = agentsToInit.size();
		IAgent[] toInit = new IAgent[total];
		agentsToInit.toArray(toInit);
		agentsToInit.clear();
		for ( int i = 0, n = toInit.length; i < n; i++ ) {
			GuiUtils.stopIfCancelled();
			// GUI.debug("Executing the init section of " + toInit[i]);
			IAgent a = toInit[i];
			if ( !a.dead() ) {
				init(a, scope);
			}
		}
		// Recursive call
		if ( !agentsToInit.isEmpty() ) {
			executeAgentsToInit(scope);
		}
	}

	public void init(final IAgent agent, final IScope scope) throws GamaRuntimeException {
		scope.push(agent);
		try {
			agent.init(scope);
		} finally {
			scope.pop(agent);
		}
	}

	@Override
	public void removeAction(final IScheduledAction haltAction) {
		if ( beginActionsNumber > 0 && beginActions.remove(haltAction) ) {
			beginActionsNumber--;
		} else if ( endActionsNumber > 0 && endActions.remove(haltAction) ) {
			endActionsNumber--;
		}
	}

	private IScheduledAction insertActionIn(final Object target, final String method,
		final List<IScheduledAction> list) {
		final IScheduledAction action = GamaCompiler.buildAction(target, method);
		list.add(action);
		return action;
	}

	@Override
	public void insertEndAction(final IScheduledAction action) {
		if ( action == null ) { return; }
		endActionsNumber++;
		endActions.add(action);
	}

	@Override
	public IScheduledAction insertEndAction(final Object target, final String method) {
		endActionsNumber++;
		return insertActionIn(target, method, endActions);
	}

	@Override
	public IScheduledAction insertBeginAction(final Object target, final String method) {
		beginActionsNumber++;
		return insertActionIn(target, method, beginActions);
	}

	@Override
	public void insertAgentToInit(final IAgent entity) throws GamaRuntimeException {
		if ( inInitSequence ) {
			agentsToInit.add(entity);
		} else {
			if ( !entity.dead() ) {
				init(entity, simulation.getExecutionScope());
			}
		}
	}

}
