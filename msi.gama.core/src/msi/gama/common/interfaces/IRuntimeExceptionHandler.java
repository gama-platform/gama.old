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
