/*******************************************************************************************************
 *
 * SwitchToHandler.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * The Class PerspectiveSwitchLock. Ancestor of the "perspective switch" commands, synchronized on the shared static
 * variable isRunning
 */
public abstract class SwitchToHandler extends AbstractHandler {

	/** The is locked. */
	private static boolean isRunning;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (isRunning) return false;
		try {
			isRunning = true;
			execute();
		} finally {
			isRunning = false;
		}
		return true;
	}

	/**
	 * Execute.
	 */
	protected abstract void execute();

	@Override
	public boolean isEnabled() { return super.isEnabled() && !isRunning; }

}
