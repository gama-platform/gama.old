/*******************************************************************************************************
 *
 * ExperimentScheduler.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;

import msi.gama.common.interfaces.IStepable;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ExperimentScheduler.
 */
public class ExperimentScheduler implements Runnable {

	/**
	 * The Class Stepable.
	 */
	static class Stepped {

		/**
		 * Instantiates a new stepable.
		 *
		 * @param stepable
		 *            the I scope.
		 * @param scope
		 *            the scope.
		 */
		public Stepped(final IStepable stepable, final IScope scope) {
			this.stepable = stepable;
			this.scope = scope;
		}

		/** The I scope. */
		IStepable stepable;

		/** The scope. */
		IScope scope;
	}

	/** The alive. */
	public volatile boolean alive = true;
	// Flag indicating that the experiment is set to pause (it should be alive
	/** The paused. */
	// unless the application is shutting down)
	public volatile boolean paused = true;

	/** The to step. */
	/* The stepables that need to be stepped */
	private final Set<Stepped> toStep = new CopyOnWriteArraySet<>();
	// private final Map<IStepable, IScope> toStep = new MapMaker().makeMap();

	/** The to stop. */
	private volatile Set<IStepable> toStop = new HashSet<>();

	/** The execution thread. */
	private Thread executionThread;

	/** The lock. */
	volatile Semaphore lock = new Semaphore(1);

	/** The experiment. */
	final IExperimentPlan experiment;

	/**
	 * Instantiates a new experiment scheduler.
	 *
	 * @param experiment
	 *            the experiment
	 */
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

	/**
	 * Start thread.
	 */
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

	/**
	 * Step.
	 */
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
			toStep.forEach(s -> { if (!s.scope.step(s.stepable).passed()) { toStop.add(s.stepable); } });
			// }
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clean.
	 */
	private void clean() {
		if (toStop.isEmpty()) return;
		synchronized (toStop) {
			for (final IStepable s : toStop) {
				Stepped found = null;
				for (final Stepped st : toStep) { if (st.stepable == s) { found = st; } }
				if (found != null) {
					IScope scope = found.scope;
					if (scope != null && !scope.interrupted()) { scope.setInterrupted(); }
					toStep.remove(found);
				}
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

	/**
	 * Step by step.
	 */
	public void stepByStep() {
		paused = true;
		lock.release();
		startThread();
	}

	/**
	 * Step back.
	 */
	// TODO : c'est moche .....
	public void stepBack() {
		paused = true;
		// lock.release();
		experiment.getAgent().backward(experiment.getExperimentScope());// ?? scopes[0]);
	}

	/**
	 * Start.
	 */
	public void start() {
		paused = false;
		lock.release();
		startThread();
	}

	/**
	 * Pause.
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * Schedule.
	 *
	 * @param stepable
	 *            the stepable
	 * @param scope
	 *            the scope
	 */
	public void schedule(final IStepable stepable, final IScope scope) {
		boolean replaced = false;
		for (Stepped st : toStep) {
			if (st.stepable == stepable) {
				st.scope = scope;
				replaced = true;
				break;
			}
		}
		if (!replaced) { toStep.add(new Stepped(stepable, scope)); }
		// We first init the stepable before it is scheduled
		// DEBUG.OUT("ExperimentScheduler.schedule " + stepable);
		try {
			if (!scope.init(stepable).passed()) { toStop.add(stepable); }
		} catch (final Throwable e) {
			if (scope != null && scope.interrupted()) {
				toStop.add(stepable);
			} else if (!(e instanceof GamaRuntimeException)) {
				GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
			}
		}

	}

	/**
	 * Wipe.
	 */
	public synchronized void wipe() {
		synchronized (toStop) {
			toStop.clear();
		}
		synchronized (toStep) {
			toStep.clear();
		}
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		alive = false;
		wipe();
		lock.release();
	}

}
