/*********************************************************************************************
 *
 *
 * 'ActionExecuter.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.GamaHelper;

public class ActionExecuter {

	// FIXME This class has no more interest. Should be better divided into (1) initialization mechanisms (in agents &
	// populations?); (2) Action-running mechanisms in another explicit class.

	private static final int BEGIN = 0;
	private static final int END = 1;
	private static final int DISPOSE = 2;
	private static final int ONE_SHOT = 3;

	final List<GamaHelper>[] actions = new List[4];
	// protected final IStepable owner;
	protected final IScope scope;
	protected volatile boolean alive = true;

	public ActionExecuter(final IScope scope) {
		// this.owner = owner;
		this.scope = scope.copy();
	}

	// public boolean isAlive() {
	// return alive;
	// }
	//
	// @Override
	// public String toString() {
	// return "Scheduler of " + owner;
	// }

	// public void reset() {
	// actions = null;
	// }

	public void dispose() {
		executeActions(DISPOSE);
		// WARNING: if the scope is not marked as "interrupted", this will result in an endless loop
		// FIXME Provide a safer and shorter access to the controller...
		// if ( !scope.getExperiment().getSpecies().getController().getScheduler().paused ) {
		// while (alive) {
		// try {
		// Give it a chance to cleanup before being disposed
		// step(scope);
		// Thread.sleep(100);
		// } catch (final Exception e) {
		// alive = false;
		// } finally {}
		// }
		// }
	}

	// @Override
	// public boolean step(final IScope scope) throws GamaRuntimeException {
	// if ( owner != null && alive ) {
	// executeActions(scope, BEGIN);
	// alive = scope.step(owner);
	// if ( alive ) {
	// executeActions(scope, END);
	// executeActions(scope, ONE_SHOT);
	// }
	// }
	// return alive;
	// }
	//
	// @Override
	// public boolean init(final IScope scope) throws GamaRuntimeException {
	// return scope.init(owner);
	// }

	// public void executeActions(final IScope scope, final int type) {
	// if ( actions != null && !scope.interrupted() ) {
	// final List<GamaHelper> list = actions[type];
	// if ( list != null ) {
	// for ( final GamaHelper action : list ) {
	// if ( !scope.interrupted() ) {
	// action.run(scope);
	// }
	// }
	// if ( type == ONE_SHOT ) {
	// actions[ONE_SHOT] = null;
	// }
	// }
	// }
	// }

	public void removeAction(final GamaHelper haltAction) {
		if ( actions == null ) { return; }
		for ( final List<GamaHelper> list : actions ) {
			if ( list != null && list.remove(haltAction) ) { return; }
		}
	}

	private GamaHelper insertAction(final GamaHelper action, final int type) {
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

	public void executeEndActions() {
		executeActions(END);
	}

	public void executeDisposeActions() {
		executeActions(DISPOSE);
	}

	public void executeOneShotActions() {
		executeActions(ONE_SHOT);
		actions[ONE_SHOT] = null;
	}

	private void executeActions(final int type) {
		if ( actions[type] == null || actions[type].isEmpty() ) { return; }
		for ( GamaHelper action : actions[type] ) {
			if ( !scope.interrupted() ) {
				action.run(scope);
			}
		}
	}

	public synchronized void executeOneAction(final GamaHelper action) {
		ExperimentScheduler sche =
			scope.getSimulationScope().getExperiment().getSpecies().getController().getScheduler();
		if ( sche.paused || sche.on_user_hold ) {
			action.run(scope);
		} else {
			insertOneShotAction(action);
		}
	}

	/**
	 *
	 */
	public void executeBeginActions() {
		executeActions(BEGIN);
	}

}
