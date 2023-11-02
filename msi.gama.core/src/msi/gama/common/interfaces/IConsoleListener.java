/*******************************************************************************************************
 *
 * IConsoleListener.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.util.GamaColor;

/**
 * The listener interface for receiving IConsole events.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @see IConsoleEvent
 * @date 2 nov. 2023
 */
public interface IConsoleListener {

	/**
	 * Adds a console listener. Does nothing by default.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 2 nov. 2023
	 */
	default void addConsoleListener(final IConsoleListener console) {}

	/**
	 * Removes a console listener. Does nothing by default.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param console
	 *            the console
	 * @date 2 nov. 2023
	 */
	default void removeConsoleListener(final IConsoleListener console) {}

	/**
	 * Debug console.
	 *
	 * @param cycle
	 *            the cycle
	 * @param s
	 *            the s
	 * @param root
	 *            the root
	 * @param color
	 *            the color
	 */
	default void debugConsole(final int cycle, final String s, final ITopLevelAgent root, final GamaColor color) {
		informConsole("(cycle: " + String.valueOf(cycle) + ") " + s, root, color);
	}

	/**
	 * Debug console.
	 *
	 * @param cycle
	 *            the cycle
	 * @param s
	 *            the s
	 * @param root
	 *            the root
	 */
	default void debugConsole(final int cycle, final String s, final ITopLevelAgent root) {
		debugConsole(cycle, s, root, null);
	}

	/**
	 * Inform console.
	 *
	 * @param s
	 *            the s
	 * @param root
	 *            the root
	 * @param color
	 *            the color
	 */
	void informConsole(String s, ITopLevelAgent root, GamaColor color);

	/**
	 * Inform console.
	 *
	 * @param s
	 *            the s
	 * @param root
	 *            the root
	 */
	default void informConsole(final String s, final ITopLevelAgent root) {
		informConsole(s, root, null);
	}

	/**
	 * Show console view.
	 *
	 * @param agent
	 *            the agent
	 */
	default void toggleConsoleViews(final ITopLevelAgent agent, final boolean show) {}

	/**
	 * Erase console.
	 *
	 * @param setToNull
	 *            the set to null
	 */
	default void eraseConsole(final boolean setToNull) {}
}
