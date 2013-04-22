package msi.gama.kernel.simulation;

import java.util.*;
import msi.gama.common.interfaces.IStepable;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.IScheduledAction;

public abstract class AbstractScheduler implements IScheduler {

	protected final SimulationClock clock;
	private static final int BEGIN = 0;
	private static final int END = 1;
	private static final int DISPOSE = 2;

	// Flag indicating that the simulation has been launched and is alive (maybe paused)
	public volatile boolean alive = false;
	// Flag indicating that the simulation is set to pause (it should be alive unless a reload has
	// been requested)
	public volatile boolean paused = true;
	// Flag indicating that the simulation is set to step (temporary flag)
	public volatile boolean stepped = false;
	// Flag indicating that the thread is set to be on hold, waiting for a user input
	public volatile boolean on_user_hold = false;
	/* Actions that should be run by the scheduler at the beginning/end of a cycle, or when the scheduler is disposed. */
	Map<Integer, List<IScheduledAction>> actions = null;
	/* The agents that need to be initialized */
	private final List<IStepable> agentsToInit;
	/* Whether or not the scheduler is in its initialization sequence */
	private boolean inInitSequence = true;
	/* The agent that owns this scheduler */
	protected final IAgent owner;

	protected AbstractScheduler(final IAgent owner) {
		this.owner = owner;
		paused = true;
		agentsToInit = new GamaList();
		clock = new SimulationClock();
	}

	@Override
	public void dispose() {
		executeActions(GAMA.getDefaultScope(), DISPOSE);
		alive = false;
		on_user_hold = false;
		agentsToInit.clear();
		if ( actions != null ) {
			actions.clear();
		}
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		clock.beginCycle();
		executeActions(scope, BEGIN);
		// Another strategy would be to run these agents hierarchically (all the level 1 first, then
		// all the level 2, etc.). Maybe we can propose this strategy.
		GamaList<IAgent> listOfAgentsToSchedule = GamaList.with(owner);
		owner.computeAgentsToSchedule(scope, listOfAgentsToSchedule);
		for ( IAgent a : listOfAgentsToSchedule ) {
			if ( a != null ) {
				a.step(scope);
			}
		}
		executeActions(scope, END);
		clock.step();
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
		try {
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
				init(scope);
			}
		} finally {
			inInitSequence = false;
		}
	}

	private void executeActions(final IScope scope, final Integer type) {
		if ( actions != null ) {
			List<IScheduledAction> list = actions.get(type);
			if ( list != null ) {
				for ( Iterator<IScheduledAction> iter = list.iterator(); iter.hasNext(); ) {
					IScheduledAction action = iter.next();
					action.execute(scope);
					if ( action.isOneShot() ) {
						iter.remove();
					}
				}
			}
		}
	}

	@Override
	public void removeAction(final IScheduledAction haltAction) {
		if ( actions == null ) { return; }
		for ( List<IScheduledAction> list : actions.values() ) {
			if ( list.remove(haltAction) ) { return; }
		}
	}

	private void insertAction(final IScheduledAction action, final Integer type) {
		if ( action == null ) { return; }
		if ( actions == null ) {
			actions = new LinkedHashMap();
		}
		List<IScheduledAction> list = actions.get(type);
		if ( list == null ) {
			list = new ArrayList();
			actions.put(type, list);
		}
		list.add(action);
	}

	@Override
	public void insertDisposeAction(final IScheduledAction action) {
		insertAction(action, DISPOSE);
	}

	@Override
	public void insertEndAction(final IScheduledAction action) {
		insertAction(action, END);
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
