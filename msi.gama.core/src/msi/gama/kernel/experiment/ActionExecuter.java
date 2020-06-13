/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.ActionExecuter.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.ArrayList;
import java.util.List;

import msi.gama.runtime.IScope;
import msi.gaml.statements.IExecutable;

public class ActionExecuter {

	private static final int BEGIN = 0;
	private static final int END = 1;
	private static final int DISPOSE = 2;
	private static final int ONE_SHOT = 3;

	@SuppressWarnings ("unchecked") final List<IExecutable>[] actions = new List[4];
	protected final IScope scope;

	public ActionExecuter(final IScope scope) {
		this.scope = scope.copy("of ActionExecuter");
	}

	private IExecutable insertAction(final IExecutable action, final int type) {
		List<IExecutable> list = actions[type];
		if (list == null) {
			list = new ArrayList<>();
			actions[type] = list;
		}
		if (list.add(action)) { return action; }
		return null;
	}

	public IExecutable insertDisposeAction(final IExecutable action) {
		return insertAction(action, DISPOSE);
	}

	public IExecutable insertEndAction(final IExecutable action) {
		return insertAction(action, END);
	}

	public IExecutable insertOneShotAction(final IExecutable action) {
		return insertAction(action, ONE_SHOT);
	}

	public void executeEndActions() {
		if (scope.interrupted()) { return; }
		executeActions(END);
	}

	public void executeDisposeActions() {
		executeActions(DISPOSE);
	}

	public void executeOneShotActions() {
		if (scope.interrupted()) { return; }
		try {
			executeActions(ONE_SHOT);
		} finally {
			actions[ONE_SHOT] = null;
		}
	}

	private void executeActions(final int type) {
		if (actions[type] == null) { return; }
		final int size = actions[type].size();
		if (size == 0) { return; }
		final IExecutable[] array = actions[type].toArray(new IExecutable[size]);
		for (final IExecutable action : array) {
			if (!scope.interrupted()) {
				action.executeOn(scope);
			}
		}
	}

	public synchronized void executeOneAction(final IExecutable action) {
		final boolean paused = scope.isPaused();
		if (paused) {
			action.executeOn(scope);
		} else {
			insertOneShotAction(action);
		}
	}

	public void executeBeginActions() {
		if (scope.interrupted()) { return; }
		executeActions(BEGIN);
	}

}
