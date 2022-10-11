/*******************************************************************************************************
 *
 * IStatusDisplayer.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;

/**
 * The Interface IStatusDisplayer.
 */
public interface IStatusDisplayer {

	/**
	 * Resume status.
	 */
	void resumeStatus(IScope scope);

	/**
	 * Wait status.
	 *
	 * @param string the string
	 */
	void waitStatus(String string, IScope scope);

	/**
	 * Inform status.
	 *
	 * @param string the string
	 */
	void informStatus(String string, IScope scope);

	/**
	 * Error status.
	 *
	 * @param message the message
	 */
	void errorStatus(String message, IScope scope);

	/**
	 * Sets the sub status completion.
	 *
	 * @param status the new sub status completion
	 */
	void setSubStatusCompletion(double status, IScope scope);

	/**
	 * Sets the status.
	 *
	 * @param msg the msg
	 * @param color the color
	 */
	void setStatus(String msg, GamaColor color, IScope scope);

	/**
	 * Inform status.
	 *
	 * @param message the message
	 * @param icon the icon
	 */
	void informStatus(String message, String icon, IScope scope);

	/**
	 * Sets the status.
	 *
	 * @param msg the msg
	 * @param icon the icon
	 */
	void setStatus(String msg, String icon, IScope scope);

	/**
	 * Begin sub status.
	 *
	 * @param name the name
	 */
	void beginSubStatus(String name, IScope scope);

	/**
	 * End sub status.
	 *
	 * @param name the name
	 */
	void endSubStatus(String name, IScope scope);

	/**
	 * Neutral status.
	 *
	 * @param string the string
	 */
	void neutralStatus(String string, IScope scope);

}
