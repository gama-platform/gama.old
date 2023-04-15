/*******************************************************************************************************
 *
 * IRuntimeExceptionHandler.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.util.List;

import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Interface IRuntimeExceptionHandler.
 */
public interface IRuntimeExceptionHandler {

	/**
	 * Start.
	 */
	void start();

	/**
	 * Stop.
	 */
	void stop();

	/**
	 * Clear errors.
	 */
	void clearErrors();

	/**
	 * Offer.
	 *
	 * @param ex the ex
	 */
	void offer(final GamaRuntimeException ex);

	/**
	 * Removes the.
	 *
	 * @param obj the obj
	 */
	void remove(GamaRuntimeException obj);

	/**
	 * Gets the clean exceptions.
	 *
	 * @return the clean exceptions
	 */
	List<GamaRuntimeException> getCleanExceptions();

	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	boolean isRunning();

}
