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

	private static final int BEGIN = 0;
	private static final int END = 1;
	private static final int DISPOSE = 2;
	private static final int ONE_SHOT = 3;

	final List<GamaHelper>[] actions = new List[4];
	protected final IScope scope;

	public ActionExecuter(final IScope scope) {
		this.scope = scope.copy();
	}

	public void dispose() {
		executeActions(DISPOSE);
	}

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
		if ( scope.interrupted() ) { return; }
		executeActions(END);
	}

	public void executeDisposeActions() {
		executeActions(DISPOSE);
	}

	public void executeOneShotActions() {
		if ( scope.interrupted() ) { return; }
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

	public void executeBeginActions() {
		if ( scope.interrupted() ) { return; }
		executeActions(BEGIN);
	}

}
