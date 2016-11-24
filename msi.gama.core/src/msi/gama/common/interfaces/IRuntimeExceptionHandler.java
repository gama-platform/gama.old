/*********************************************************************************************
 *
 * 'IRuntimeExceptionHandler.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.util.List;

import msi.gama.runtime.exceptions.GamaRuntimeException;

public interface IRuntimeExceptionHandler {

	void start();

	void stop();

	void clearErrors();

	void offer(final GamaRuntimeException ex);

	void remove(GamaRuntimeException obj);

	List<GamaRuntimeException> getCleanExceptions();

	boolean isRunning();

}
