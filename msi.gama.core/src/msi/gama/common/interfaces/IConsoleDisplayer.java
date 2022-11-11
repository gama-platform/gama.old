/*******************************************************************************************************
 *
 * IConsoleDisplayer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.util.GamaColor;

/**
 * The Interface IConsoleDisplayer.
 */
public interface IConsoleDisplayer {

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
