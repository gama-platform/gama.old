package msi.gama.kernel.simulation;

import java.util.List;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;

public abstract class AbstractScheduler implements IScheduler {

	protected final ISimulation simulation;

	// Flag indicating that the simulation has been launched and is alive (maybe paused)
	public volatile boolean alive = false;
	// Flag indicating that the simulation is set to pause (it should be alive unless a reload has
	// been requested)
	public volatile boolean paused = true;
	// Flag indicating that the simulation is set to step (temporary flag)
	public volatile boolean stepped = false;

	// Flag indicating that the thread is set to be on hold, waiting for a user input
	public volatile boolean on_user_hold = false;

	/** Actions that should be run by the scheduler at the beginning of a cycle. */
	private final List<IScheduledAction> beginActions;
	private int beginActionsNumber;
	/** Actions that should be run by the scheduler at the end of a cycle. */
	private final List<IScheduledAction> endActions;
	private int endActionsNumber;
	private final List<IAgent> agentsToInit;

	private boolean inInitSequence = true;

	private IAgent world;

	private List<ISchedulerListener> listeners; 

	protected AbstractScheduler(final ISimulation sim) {
		simulation = sim;
		paused = true;
		beginActions = new GamaList();
		endActions = new GamaList();
		agentsToInit = new GamaList();
		listeners = new GamaList<ISchedulerListener>();
		SimulationClock.reset();
	}

	@Override
	public void dispose() {
		alive = false;
		on_user_hold = false;
		beginActions.clear();
		beginActionsNumber = 0;
		endActions.clear();
		endActionsNumber = 0;
		agentsToInit.clear();
		
		for (ISchedulerListener l : listeners) {
			l.schedulerDisposed();
		}
		listeners.clear();
	}

	@Override
	public void addListener(ISchedulerListener l) {
		listeners.add(l);
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		SimulationClock.beginCycle();
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
				if ( actions[i].isOneShot() ) {
					removeAction(actions[i]);
				}
			}
		}
	}

	private void executeBeginActions(final IScope scope) throws GamaRuntimeException {
		if ( beginActionsNumber > 0 ) {
			ScheduledAction[] actions =
				beginActions.toArray(new ScheduledAction[beginActionsNumber]);
			for ( int i = 0, n = beginActionsNumber; i < n; i++ ) {
				actions[i].execute(scope);
				if ( actions[i].isOneShot() ) {
					removeAction(actions[i]);
				}
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
		} catch (Exception e) {
			GAMA.reportError(new GamaRuntimeException(e));
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

	@Override
	public void insertEndAction(final IScheduledAction action) {
		if ( action == null ) { return; }
		endActionsNumber++;
		endActions.add(action);
	}

	@Override
	public synchronized void executeOneAction(final IScheduledAction action) {
		if ( paused || on_user_hold ) {
			IScope scope = simulation.obtainNewScope();
			action.execute(scope);
			// TODO outputs update ?
			simulation.releaseScope(scope);
		} else {
			action.setOneShot(true);
			insertEndAction(action);
		}
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

	@Override
	public void setUserHold(final boolean hold) {
		on_user_hold = hold;
	}

	@Override
	public boolean isUserHold() {
		return on_user_hold;
	}

	public abstract void pause();
}
