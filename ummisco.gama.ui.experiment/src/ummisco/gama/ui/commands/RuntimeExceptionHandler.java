/*********************************************************************************************
 *
 * 'RuntimeExceptionHandler.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import msi.gama.common.interfaces.IRuntimeExceptionHandler;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class RuntimeExceptionHandler extends Job implements IRuntimeExceptionHandler {

	public RuntimeExceptionHandler() {
		super("Runtime error collector");
	}

	volatile BlockingQueue<GamaRuntimeException> incomingExceptions = new LinkedBlockingQueue<>();
	volatile List<GamaRuntimeException> cleanExceptions = new ArrayList<>();
	volatile boolean running;
	volatile int remainingTime = 5000;

	@Override
	public void offer(final GamaRuntimeException ex) {
		remainingTime = 5000;
		incomingExceptions.offer(ex);
	}

	@Override
	public void clearErrors() {
		incomingExceptions.clear();
		cleanExceptions.clear();
		updateUI(null);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		while (running) {
			while (incomingExceptions.isEmpty() && running && remainingTime > 0) {
				try {
					Thread.sleep(500);
					remainingTime -= 500;
				} catch (final InterruptedException e) {
					return Status.OK_STATUS;
				}
			}
			if (!running)
				return Status.CANCEL_STATUS;
			if (remainingTime <= 0) {
				stop();
				return Status.OK_STATUS;
			}
			process();
		}
		return Status.OK_STATUS;
	}

	@Override
	public void stop() {
		running = false;
	}

	private void process() {
		final ArrayList<GamaRuntimeException> array = new ArrayList<>(incomingExceptions);
		// DEBUG.LOG("Processing " + array.size() + " exceptions");
		incomingExceptions.clear();

		if (GamaPreferences.Runtime.CORE_REVEAL_AND_STOP.getValue()) {
			final GamaRuntimeException firstEx = array.get(0);
			GAMA.getGui().editModel(null, firstEx.getEditorContext());
			firstEx.setReported();
			if (GamaPreferences.Runtime.CORE_SHOW_ERRORS.getValue()) {
				final List<GamaRuntimeException> newList = new ArrayList<>();
				newList.add(firstEx);
				updateUI(newList);
			}

		} else if (GamaPreferences.Runtime.CORE_SHOW_ERRORS.getValue()) {
			final ArrayList<GamaRuntimeException> oldExcp = new ArrayList<>(cleanExceptions);
			for (final GamaRuntimeException newEx : array) {
				if (oldExcp.size() == 0) {
					oldExcp.add(newEx);
				} else {
					boolean toAdd = true;
					for (final GamaRuntimeException oldEx : oldExcp.toArray(new GamaRuntimeException[0])) {
						if (oldEx.equivalentTo(newEx)) {
							if (oldEx != newEx)
								oldEx.addAgents(newEx.getAgentsNames());
							toAdd = false;
						}
					}
					if (toAdd)
						oldExcp.add(newEx);

				}
			}
			updateUI(oldExcp);
		}

	}

	public void updateUI(final List<GamaRuntimeException> newExceptions) {
		if (newExceptions != null) {
			for (final GamaRuntimeException exception : new ArrayList<GamaRuntimeException>(newExceptions)) {
				if (exception.isInvalid()) {
					newExceptions.remove(exception);
				}
			}
			cleanExceptions = newExceptions;
		}

		GAMA.getGui().displayErrors(null, newExceptions);
	}

	@Override
	public void start() {
		running = true;
		schedule();

	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void remove(final GamaRuntimeException obj) {
		cleanExceptions.remove(obj);
	}

	@Override
	public List<GamaRuntimeException> getCleanExceptions() {
		return cleanExceptions;
	}

}
