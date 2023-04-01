/*******************************************************************************************************
 *
 * RuntimeExceptionHandler.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IRuntimeExceptionHandler;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.THREADS;

/**
 * The Class RuntimeExceptionHandler.
 */
public class RuntimeExceptionHandler extends Job implements IRuntimeExceptionHandler {

	static {
		// DEBUG.ON();
	}

	/**
	 * Instantiates a new runtime exception handler.
	 */
	public RuntimeExceptionHandler() {
		super("Runtime error collector");
	}

	/** The incoming exceptions. */
	volatile BlockingQueue<GamaRuntimeException> incomingExceptions = new LinkedBlockingQueue<>();

	/** The clean exceptions. */
	volatile List<GamaRuntimeException> cleanExceptions = new ArrayList<>();

	/** The running. */
	volatile boolean running;

	/** The remaining time. */
	volatile int remainingTime = 5000;

	@Override
	public void offer(final GamaRuntimeException ex) {
		if (ex == null) return;
		DEBUG.LOG("Adding exception " + ex.getAllText());

		remainingTime = 5000;
		incomingExceptions.offer(ex);
	}

	@Override
	public void clearErrors() {
		incomingExceptions.clear();
		cleanExceptions.clear();
		updateUI(null, true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		while (running) {
			while (incomingExceptions.isEmpty() && running && remainingTime > 0) {
				if (!THREADS.WAIT(500)) return Status.OK_STATUS;
				remainingTime -= 500;
			}
			if (!running) return Status.CANCEL_STATUS;
			if (remainingTime <= 0) {
				stop();
				return Status.OK_STATUS;
			}
			// final Multimap<ITopLevelAgent, GamaRuntimeException> array =
			// Multimaps.index(incomingExceptions, @Nullable GamaRuntimeException::getTopLevelAgent);
			final boolean reset[] = { true };
			StreamEx.of(incomingExceptions).nonNull().filter(each -> each.getTopLevelAgent() != null)
					.groupingBy(GamaRuntimeException::getTopLevelAgent).forEach((root, list) -> {
						// DEBUG.LOG("Processing exceptions for " + root);
						if (GamaPreferences.Runtime.CORE_REVEAL_AND_STOP.getValue()) {
							final GamaRuntimeException firstEx = Iterables.getFirst(list, null);
							if (GamaPreferences.Runtime.CORE_ERRORS_EDITOR_LINK.getValue()) {
								GAMA.getGui().editModel(null, firstEx.getEditorContext());
							}
							firstEx.setReported();
							if (GamaPreferences.Runtime.CORE_SHOW_ERRORS.getValue()) {
								final List<GamaRuntimeException> exceptions = new ArrayList<>();
								exceptions.add(firstEx);
								updateUI(exceptions, reset[0]);
								reset[0] = false;
							}
						} else if (GamaPreferences.Runtime.CORE_SHOW_ERRORS.getValue()) {
							final ArrayList<GamaRuntimeException> oldExcp = new ArrayList<>(cleanExceptions);
							for (final GamaRuntimeException newEx : list) {
								if (oldExcp.size() == 0) {
									oldExcp.add(newEx);
								} else {
									boolean toAdd = true;
									for (final GamaRuntimeException oldEx : oldExcp
											.toArray(new GamaRuntimeException[oldExcp.size()])) {
										if (oldEx.equivalentTo(newEx)) {
											if (oldEx != newEx) { oldEx.addAgents(newEx.getAgentsNames()); }
											toAdd = false;
										}
									}
									if (toAdd) { oldExcp.add(newEx); }

								}
							}
							updateUI(oldExcp, true);
						}
					});
			incomingExceptions.clear();
		}

		return Status.OK_STATUS;
	}

	@Override
	public void stop() {
		running = false;
	}

	/**
	 * Update UI.
	 *
	 * @param newExceptions
	 *            the new exceptions
	 */
	public void updateUI(final List<GamaRuntimeException> newExceptions, final boolean reset) {
		if (newExceptions != null) {
			newExceptions.removeIf(GamaRuntimeException::isInvalid);
			cleanExceptions = new ArrayList<>(newExceptions);
		}

		GAMA.getGui().displayErrors(null, newExceptions, reset);
	}

	@Override
	public void start() {
		running = true;
		// Reinits remainingTime (issue found while working in #3641 : two executions in a row would lead to the second
		// one not reporting any error)
		remainingTime = 5000;
		schedule();

	}

	@Override
	public boolean isRunning() { return running; }

	@Override
	public void remove(final GamaRuntimeException obj) {
		cleanExceptions.remove(obj);
	}

	@Override
	public List<GamaRuntimeException> getCleanExceptions() { return cleanExceptions; }

}
