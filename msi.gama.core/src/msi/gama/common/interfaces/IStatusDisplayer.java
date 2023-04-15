/*******************************************************************************************************
 *
 * IStatusDisplayer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;

/**
 * The Interface IStatusDisplayer.
 *
 * Changed AD 11/11/22: puts IScope first like everywhere in the code...
 */
public interface IStatusDisplayer {

	/**
	 * Resume status.
	 */
	default void resumeStatus(final IScope scope) {}

	/**
	 * Wait status.
	 *
	 * @param string
	 *            the string
	 */
	default void waitStatus(final IScope scope, final String string) {}

	/**
	 * Inform status.
	 *
	 * @param string
	 *            the string
	 */
	default void informStatus(final IScope scope, final String message) {}

	/**
	 * Error status.
	 *
	 * @param message
	 *            the message
	 */
	default void errorStatus(final IScope scope, final String message) {}

	/**
	 * Sets the sub status completion.
	 *
	 * @param status
	 *            the new sub status completion
	 */
	default void setSubStatusCompletion(final IScope scope, final double status) {}

	/**
	 * Sets the status.
	 *
	 * @param msg
	 *            the msg
	 * @param color
	 *            the color
	 */
	default void setStatus(final IScope scope, final String msg, final GamaColor color) {}

	/**
	 * Inform status.
	 *
	 * @param message
	 *            the message
	 * @param icon
	 *            the icon
	 */
	default void informStatus(final IScope scope, final String message, final String icon) {}

	/**
	 * Sets the status.
	 *
	 * @param msg
	 *            the msg
	 * @param icon
	 *            the icon
	 */
	default void setStatus(final IScope scope, final String msg, final String icon) {}

	/**
	 * Begin sub status.
	 *
	 * @param name
	 *            the name
	 */
	default void beginSubStatus(final IScope scope, final String name) {}

	/**
	 * End sub status.
	 *
	 * @param name
	 *            the name
	 */
	default void endSubStatus(final IScope scope, final String name) {}

	/**
	 * Neutral status.
	 *
	 * @param string
	 *            the string
	 */
	default void neutralStatus(final IScope scope, final String string) {}

}
