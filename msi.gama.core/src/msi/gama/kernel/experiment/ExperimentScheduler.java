/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.ExperimentScheduler.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import msi.gama.common.interfaces.IStepable;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import ummisco.gama.dev.utils.DEBUG;

public class ExperimentScheduler implements Runnable {

	public volatile boolean alive = true;
	// Flag indicating that the experiment is set to pause (it should be alive
	// unless the application is shutting down)
	public volatile boolean paused = true;
	/* The stepables that need to be stepped */
	private final Map<IStepable, IScope> toStep = GamaMapFactory.create();
	private volatile Set<IStepable> toStop = new HashSet<>();
	private Thread executionThread;
	volatile Semaphore lock = new Semaphore(1);
	final IExperimentPlan experiment;

	ExperimentScheduler(final IExperimentPlan experiment) {
		this.experiment = experiment;
		if (!experiment.isHeadless()) {
			executionThread = new Thread(null, this, "Front end scheduler");
			executionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			startThread();
		} else {
			executionThread = null;
		}
	}

	private void startThread() {
		if (executionThread == null) {
			step();
		} else if (!executionThread.isAlive()) {
			try {
				executionThread.start();
			} catch (final Throwable e) {
				e.printStackTrace();
				final GamaRuntimeException ee = GamaRuntimeException.create(e, experiment.getExperimentScope());
				ee.addContext("Error in front end scheduler. Reloading thread, but it would be safer to reload GAMA");
				DEBUG.LOG(ee.getMessage());
				executionThread = new Thread(null, this, "Front end scheduler");
				executionThread.start();
			}
		}
	}

	public void step() {
		if (!experiment.isHeadless() && paused) {
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				alive = false;
				return;
			}
		}
		try {
			// synchronized (toStep) {
			toStep.forEach((stepable, scope) -> {
				if (!scope.step(stepable).passed()) { toStop.add(stepable); }
			});
			// }
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	private void clean() {
		if (toStop.isEmpty()) return;
		synchronized (toStop) {
			for (final IStepable s : toStop) {
				final IScope scope = toStep.get(s);
				if (scope != null && !scope.interrupted()) { scope.setInterrupted(); }
				toStep.remove(s);
			}
			if (toStep.isEmpty()) { this.pause(); }
			toStop.clear();
		}
	}

	@Override
	public void run() {
		while (alive) {
			step();
			clean();
		}
	}

	// public void setUserHold(final boolean hold) {
	// on_user_hold = hold;
	// }

	public void stepByStep() {
		paused = true;
		lock.release();
		startThread();
	}

	// TODO : c'est moche .....
	public void stepBack() {
		paused = true;
		// lock.release();
		experiment.getAgent().backward(experiment.getExperimentScope());// ?? scopes[0]);
	}

	public void start() {
		paused = false;
		lock.release();
		startThread();
	}

	public void pause() {
		paused = true;
	}

	public void schedule(final IStepable stepable, final IScope scope) {
		if (toStep.containsKey(stepable)) { toStep.remove(stepable); }
		toStep.put(stepable, scope);
		// We first init the stepable before it is scheduled
		// DEBUG.OUT("ExperimentScheduler.schedule " + stepable);
		try {
			if (!scope.init(stepable).passed()) { toStop.add(stepable); }
		} catch (final Throwable e) {
			if (scope != null && scope.interrupted()) {
				toStop.add(stepable);
			} else {
				if (!(e instanceof GamaRuntimeException)) {
					GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
				}
			}
		}

	}

	public synchronized void wipe() {
		synchronized (toStop) {
			toStop.clear();
		}
		synchronized (toStep) {
			toStep.clear();
		}
	}

	public void dispose() {
		alive = false;
		wipe();
		lock.release();
	}

}
