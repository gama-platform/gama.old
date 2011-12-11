/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.kernel;

import java.util.List;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.ScheduledAction;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.stack.ExecutionStack;
import msi.gama.outputs.OutputManager;
import msi.gama.util.GamaList;

// TODO change to SimulationScheduler or SimulationControler or GlobalScheduler
public class Scheduler implements Runnable {

	protected final ISimulation simulation;
	public final Thread executionThread;

	// Flag indicating that the simulation has been launched and is alive (maybe paused)
	public volatile boolean alive = false;
	// Flag indicating that the simulation is set to pause (it should be alive unless a reload has
	// been requested)
	public volatile boolean paused = true;
	// Flag indicating that the simulation is set to step (temporary flag)
	public volatile boolean stepped = false;
	/** Number of cycles already done. */
	private int cycle;
	/** The time. */
	private int time = 0; // default
	/** The time step. */
	private int timeStep = 1; // default
	/** Actions that should be run by the scheduler at the beginning of a cycle. */
	private final List<ScheduledAction> beginActions;
	private int beginActionsNumber;
	/** Actions that should be run by the scheduler at the end of a cycle. */
	private final List<ScheduledAction> endActions;
	private int endActionsNumber;
	private final List<IAgent> agentsToInit;
	private final IScope executionStack, outputStack, globalStack;
	private final List<IScope> stackPool;

	private boolean inInitSequence = true;

	private static int threadCount = 0;

	private IAgent world;

	protected Scheduler(final ISimulation sim) {
		simulation = sim;
		paused = true;
		stackPool = new GamaList();
		executionStack = obtainNewStack();
		outputStack = obtainNewStack();
		globalStack = obtainNewStack();
		threadCount++;
		executionThread = new Thread(null, this, "Scheduler execution thread #" + threadCount);
		beginActions = new GamaList();
		endActions = new GamaList();
		agentsToInit = new GamaList();
	}

	@Override
	public void run() {
		OutputManager m = GAMA.getExperiment().getOutputManager();
		while (alive) {
			try {
				GAMA.SCHEDULER_AUTHORIZATION.acquire();

				if ( !paused && alive ) {
					step();
				}
				if ( alive ) {
					m.step(getExecutionScope(), getCycle());
				}
				if ( alive ) {
					m.updateOutputs();
				}

				paused = stepped ? true : paused;
				stepped = false;

				if ( !paused && alive ) {
					GAMA.SCHEDULER_AUTHORIZATION.release();
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

	public void stepByStep() {
		stepped = true;
		paused = false;
		alive = true;
		GAMA.SCHEDULER_AUTHORIZATION.release();
		startThread();
	}

	public void start() {
		alive = true;
		paused = false;
		stepped = false;
		GAMA.SCHEDULER_AUTHORIZATION.release();
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

	public void step() throws GamaRuntimeException {
		long startTime = System.currentTimeMillis();
		GUI.informStatus("step " + cycle);
		executeBeginActions(executionStack);
		// Another strategy would be to run these agents hierarchically (all the level 1 first, then
		// all the level 2, etc.). Maybe we can propose this strategy.
		GamaList<IAgent> listOfAgentsToSchedule = GamaList.with(world);
		world.computeAgentsToSchedule(executionStack, listOfAgentsToSchedule);
		for ( IAgent a : listOfAgentsToSchedule ) {
			if ( a != null ) {
				a.step(executionStack);
			}
		}
		executeEndActions(executionStack);
		increaseTime();
		stepLength = System.currentTimeMillis() - startTime;
	}

	private volatile long stepLength = 0;

	public long getStepLength() {
		return stepLength;
	}

	public boolean inInitSequence() {
		return inInitSequence;
	}

	protected void enterInitSequence() throws GamaRuntimeException, InterruptedException {
		executeAgentsToInit(getExecutionScope());
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
			GUI.stopIfCancelled();
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

	private void increaseTime() {
		time += timeStep;
		cycle++;
	}

	public void removeAction(final ScheduledAction haltAction) {
		if ( beginActionsNumber > 0 && beginActions.remove(haltAction) ) {
			beginActionsNumber--;
		} else if ( endActionsNumber > 0 && endActions.remove(haltAction) ) {
			endActionsNumber--;
		}
	}

	private ScheduledAction insertActionIn(final Object target, final String method,
		final List<ScheduledAction> list) {
		final ScheduledAction action = ScheduledAction.newInstance(target, method);
		list.add(action);
		return action;
	}

	public void insertEndAction(final ScheduledAction action) {
		if ( action == null ) { return; }
		endActionsNumber++;
		endActions.add(action);
	}

	public ScheduledAction insertEndAction(final Object target, final String method) {
		endActionsNumber++;
		return insertActionIn(target, method, endActions);
	}

	public ScheduledAction insertBeginAction(final Object target, final String method) {
		beginActionsNumber++;
		return insertActionIn(target, method, beginActions);
	}

	public void insertAgentToInit(final IAgent entity) throws GamaRuntimeException {
		if ( inInitSequence ) {
			agentsToInit.add(entity);
		} else {
			if ( !entity.dead() ) {
				init(entity, executionStack);
			}
		}
	}

	public final long getCycle() {
		return cycle;
	}

	public final int getTime() {
		return time;
	}

	public final void setTime(final int newBeginTime) {
		time = newBeginTime;
	}

	public final int getStep() {
		return timeStep;
	}

	public void setStep(final int t) {
		timeStep = t <= 0 ? 1 : t; // not allowed to have negative or null steps
	}

	public IScope getExecutionScope() {
		return executionStack;
	}

	public IScope getOutputStack() {
		return outputStack;
	}

	public IScope getGlobalStack() {
		return globalStack;
	}

	public IScope obtainNewStack() {
		if ( stackPool.isEmpty() ) { return new ExecutionStack(simulation); }
		return stackPool.remove(stackPool.size() - 1);
	}

	public void releaseStack(final IScope scope) {
		stackPool.add(scope);
	}

}
