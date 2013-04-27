package msi.gama.kernel.simulation;

import java.util.*;
import msi.gama.common.interfaces.IStepable;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.GamaHelper;

public abstract class AbstractScheduler implements IScheduler {

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
	/* Actions that should be run by the scheduler at the beginning/end of a cycle, or when the scheduler is disposed. */
	List<GamaHelper>[] actions = null;
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
		actions = null;
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
			if ( a != null && alive ) {
				a.step(scope);
			}
		}
		executeActions(scope, END);
		executeActions(scope, ONE_SHOT);
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
			while (!agentsToInit.isEmpty()) {
				IAgent[] toInit = agentsToInit.toArray(new IAgent[agentsToInit.size()]);
				agentsToInit.clear();
				for ( int i = 0, n = toInit.length; i < n; i++ ) {
					IAgent a = toInit[i];
					if ( !a.dead() ) {
						init(a, scope);
					}
				}
			}
		} finally {
			inInitSequence = false;
		}
	}

	private void executeActions(final IScope scope, final int type) {
		if ( actions != null ) {
			List<GamaHelper> list = actions[type];
			if ( list != null ) {
				for ( GamaHelper action : list ) {
					action.run(scope);
				}
				if ( type == ONE_SHOT ) {
					actions[ONE_SHOT] = null;
				}
			}
		}
	}

	@Override
	public void removeAction(final GamaHelper haltAction) {
		if ( actions == null ) { return; }
		for ( List<GamaHelper> list : actions ) {
			if ( list != null && list.remove(haltAction) ) { return; }
		}
	}

	private void insertAction(final GamaHelper action, final int type) {
		if ( action == null ) { return; }
		if ( actions == null ) {
			actions = new ArrayList[4];
		}
		List<GamaHelper> list = actions[type];
		if ( list == null ) {
			list = new ArrayList();
			actions[type] = list;
		}
		list.add(action);
	}

	@Override
	public void insertDisposeAction(final GamaHelper action) {
		insertAction(action, DISPOSE);
	}

	@Override
	public void insertEndAction(final GamaHelper action) {
		insertAction(action, END);
	}

	public void insertOneShotAction(final GamaHelper action) {
		insertAction(action, ONE_SHOT);
	}

	@Override
	public synchronized void executeOneAction(final GamaHelper action) {
		if ( paused || on_user_hold ) {
			IScope scope = GAMA.obtainNewScope();
			action.run(scope);
			GAMA.releaseScope(scope);
		} else {
			insertOneShotAction(action);
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
