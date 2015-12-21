/*********************************************************************************************
 *
 *
 * 'AgentScheduler.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.common.interfaces.IStepable;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.statements.RemoteSequence;

public class AgentScheduler implements IStepable {

	// FIXME This class has no more interest. Should be better divided into (1) initialization mechanisms (in agents &
	// populations?); (2) Action-running mechanisms in another explicit class.

	private static final int BEGIN = 0;
	private static final int END = 1;
	private static final int DISPOSE = 2;
	private static final int ONE_SHOT = 3;

	List<GamaHelper>[] actions = null;
	/* The agents that need to be initialized */
	private final TOrderedHashMap<IStepable, RemoteSequence> stepablesToInit = new TOrderedHashMap();
	/* Whether or not the scheduler is in its initialization sequence */
	private boolean inInitSequence = true;
	/* The stepable scheduled by this scheduler */
	protected final IStepable owner;
	protected final IScope scope;
	protected volatile boolean alive = true;

	public AgentScheduler(final IScope scope, final IStepable owner) {
		this.owner = owner;
		this.scope = scope;
		stepablesToInit.put(owner, null);
		// GuiUtils.debug("AgentScheduler.AgentScheduler: creating scheduler for " + owner);
	}

	public boolean isAlive() {
		return alive;
	}

	// // TODO REMOVE / ONLY FOR DEBUG
	@Override
	public String toString() {
		return "Scheduler of " + owner;
	}

	public void reset() {
		actions = null;
		stepablesToInit.clear();
		inInitSequence = true;
	}

	@Override
	public void dispose() {
		executeActions(scope, DISPOSE);
		// GuiUtils.debug("AgentScheduler.dispose");
		// scope.setInterrupted(true);
		// We wait for the scheduler to become "idle" (i.e. when all the interruptions have become
		// effective) if the global scheduler is not paused.
		// WARNING: if the scope is not marked as "interrupted", this will result in an endless loop
		// FIXME Provide a safer and shorter access to the controller...
		if ( !scope.getExperiment().getSpecies().getController().getScheduler().paused ) {
			while (alive) {
				try {
					// GuiUtils.debug("ExperimentScheduler.dispose: DOING THE LAST STEP(S)");
					// Give it a chance to cleanup before being disposed
					step(scope);
					Thread.sleep(100);
				} catch (final Exception e) {
					// GuiUtils.debug("Interruption experiment exception: " + e.getMessage());
					alive = false;
				}
			}
		}
		// And unschedule the scheduler (although it shouldn't be necessary, as it has been marked as interrupted.
		// GAMA.controller.scheduler.unschedule(this);
		reset();
	}

	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {

		if ( owner != null && alive ) {
			executeActions(scope, BEGIN);
			alive = scope.step(owner);
			if ( alive ) {
				executeActions(scope, END);
				executeActions(scope, ONE_SHOT);
			}
		}
		return alive;
	}

	public void insertAgentToInit(final IScope scope, final IAgent entity, final RemoteSequence sequence)
		throws GamaRuntimeException {
		// if ( entity instanceof SimulationAgent || entity instanceof ExperimentAgent ) {
		// //GuiUtils.debug("AgentScheduler.insertAgentToInit : " + entity);
		// if ( entity.getName().equals("ants_model0") ) {
		// //GuiUtils.debug("AgentScheduler.insertAgentToInit SIMULATION");
		// }
		// }
		if ( inInitSequence ) {
			stepablesToInit.put(entity, sequence);
		} else {
			scope.init(entity);
			// AD: this block added to address Issue #1277

			if ( sequence != null && !sequence.isEmpty() ) {
				Object[] result = new Object[1];
				scope.execute(sequence, entity, null, result);
			}
		}
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		inInitSequence = true;
		try {
			while (!stepablesToInit.isEmpty()) {
				final IStepable[] toInit = stepablesToInit.keySet().toArray(new IStepable[stepablesToInit.size()]);
				final RemoteSequence[] sequences =
					stepablesToInit.values().toArray(new RemoteSequence[stepablesToInit.size()]);
				stepablesToInit.clear();
				for ( int i = 0, n = toInit.length; i < n; i++ ) {
					scope.init(toInit[i]);
					if ( toInit[i] instanceof IAgent && sequences[i] != null && !sequences[i].isEmpty() ) {
						// AD: this block added to address Issue #1277
						// for ( final IAgent remoteAgent : list.iterable(scope) ) {
						Object[] result = new Object[1];
						if ( !scope.execute(sequences[i], (IAgent) toInit[i], null, result) ) {
							break;
						}
						// }
					}
					// if ( !scope.init(toInit[i]) ) {
					// inInitSequence = false;
					// return false;
					// }
				}
			}
		} finally {
			inInitSequence = false;
		}
		return true;
	}

	public void executeActions(final IScope scope, final int type) {
		if ( actions != null ) {
			final List<GamaHelper> list = actions[type];
			if ( list != null ) {
				for ( final GamaHelper action : list ) {
					action.run(scope);
				}
				if ( type == ONE_SHOT ) {
					actions[ONE_SHOT] = null;
				}
			}
		}
	}

	public void removeAction(final GamaHelper haltAction) {
		if ( actions == null ) { return; }
		for ( final List<GamaHelper> list : actions ) {
			if ( list != null && list.remove(haltAction) ) { return; }
		}
	}

	private GamaHelper insertAction(final GamaHelper action, final int type) {
		if ( action == null ) { return null; }
		if ( actions == null ) {
			actions = new ArrayList[4];
		}
		List<GamaHelper> list = actions[type];
		if ( list == null ) {
			list = new ArrayList();
			actions[type] = list;
		}
		if ( list.add(action) ) { return action; }
		return null;
	}

	public GamaHelper insertDisposeAction(final GamaHelper action) {
		return insertAction(action, DISPOSE);
	}

	public GamaHelper insertEndAction(final GamaHelper action) {
		return insertAction(action, END);
	}

	public GamaHelper insertOneShotAction(final GamaHelper action) {
		return insertAction(action, ONE_SHOT);
	}

	public synchronized void executeOneAction(final GamaHelper action) {
		// hqnghi: check if it belong other controller beside default controller
		FrontEndScheduler sche = scope.getSimulationScope().getExperiment().getSpecies().getController().getScheduler();
		// String ctrlName =
		// ((ExperimentPlan) scope.getSimulationScope().getExperiment().getSpecies()).getControllerName();
		// // end-hqnghi

		if ( sche.paused || sche.on_user_hold ) {
			action.run(scope);
		} else {
			insertOneShotAction(action);
		}
	}

}
