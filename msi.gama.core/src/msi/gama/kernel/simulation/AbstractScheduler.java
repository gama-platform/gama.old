package msi.gama.kernel.simulation;

import java.util.List;
import msi.gama.common.interfaces.IStepable;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;

public abstract class AbstractScheduler implements IScheduler {

	// protected final ISimulationAgent simulation;

	protected final SimulationClock clock;

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
	private final List<IStepable> agentsToInit;
	private boolean inInitSequence = true;
	/**
	 * The agent that owns this scheduler
	 */
	protected final IAgent owner;

	private int disposeActionsNumber;

	private final List<IScheduledAction> disposeActions;

	protected AbstractScheduler(final IAgent owner) {
		this.owner = owner;
		paused = true;
		beginActions = new GamaList();
		endActions = new GamaList();
		disposeActions = new GamaList();
		agentsToInit = new GamaList();
		clock = new SimulationClock();
	}

	@Override
	public void dispose() {
		executeDisposeActions(GAMA.getDefaultScope());
		alive = false;
		on_user_hold = false;
		beginActions.clear();
		beginActionsNumber = 0;
		endActions.clear();
		endActionsNumber = 0;
		agentsToInit.clear();
		disposeActions.clear();
		disposeActionsNumber = 0;
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		clock.beginCycle();
		executeBeginActions(scope);
		// Another strategy would be to run these agents hierarchically (all the level 1 first, then
		// all the level 2, etc.). Maybe we can propose this strategy.
		GamaList<IAgent> listOfAgentsToSchedule = GamaList.with(owner);
		owner.computeAgentsToSchedule(scope, listOfAgentsToSchedule);
		for ( IAgent a : listOfAgentsToSchedule ) {
			if ( a != null ) {
				a.step(scope);
			}
		}
		executeEndActions(scope);
		clock.step();
	}

	private void init(final IAgent agent, final IScope scope) throws GamaRuntimeException {
		scope.push(agent);
		try {
			agent.init(scope);
		} finally {
			scope.pop(agent);
		}
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		inInitSequence = true;
		executeAgentsToInit(scope);
		inInitSequence = false;
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
			ScheduledAction[] actions = beginActions.toArray(new ScheduledAction[beginActionsNumber]);
			for ( int i = 0, n = beginActionsNumber; i < n; i++ ) {
				actions[i].execute(scope);
				if ( actions[i].isOneShot() ) {
					removeAction(actions[i]);
				}
			}
		}
	}

	private void executeDisposeActions(final IScope scope) throws GamaRuntimeException {
		if ( disposeActionsNumber > 0 ) {
			ScheduledAction[] actions = disposeActions.toArray(new ScheduledAction[disposeActionsNumber]);
			for ( int i = 0, n = disposeActionsNumber; i < n; i++ ) {
				actions[i].execute(scope);
				if ( actions[i].isOneShot() ) {
					removeAction(actions[i]);
				}
			}
		}
	}

	private void executeAgentsToInit(final IScope scope) throws GamaRuntimeException {
		int total = agentsToInit.size();
		IAgent[] toInit = new IAgent[total];
		agentsToInit.toArray(toInit);
		agentsToInit.clear();
		for ( int i = 0, n = toInit.length; i < n; i++ ) {
			// GuiUtils.stopIfCancelled();
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

	@Override
	public void removeAction(final IScheduledAction haltAction) {
		if ( beginActionsNumber > 0 && beginActions.remove(haltAction) ) {
			beginActionsNumber--;
		} else if ( endActionsNumber > 0 && endActions.remove(haltAction) ) {
			endActionsNumber--;
		} else if ( disposeActionsNumber > 0 && disposeActions.remove(haltAction) ) {
			disposeActionsNumber--;
		}

	}

	@Override
	public void insertDisposeAction(final IScheduledAction action) {
		if ( action == null ) { return; }
		disposeActionsNumber++;
		disposeActions.add(action);
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
			IScope scope = GAMA.obtainNewScope();
			action.execute(scope);
			// TODO outputs update ?
			GAMA.releaseScope(scope);
		} else {
			action.setOneShot(true);
			insertEndAction(action);
		}
	}

	@Override
	public void insertAgentToInit(final IAgent entity, final IScope scope) throws GamaRuntimeException {
		if ( inInitSequence ) {
			agentsToInit.add(entity);
		} else {
			if ( !entity.dead() ) {
				init(entity, scope);
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

	@Override
	public SimulationClock getClock() {
		return clock;
	}
}
