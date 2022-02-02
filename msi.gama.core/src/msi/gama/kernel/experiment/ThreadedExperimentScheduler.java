/*******************************************************************************************************
 *
 * ThreadedExperimentScheduler.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.concurrent.Semaphore;

import msi.gama.runtime.concurrent.GamaExecutorService;

/**
 * The Class ExperimentScheduler.
 */
public class ThreadedExperimentScheduler extends AbstractExperimentScheduler {

	/** The experiment. */
	protected final IExperimentPlan experiment;

	/**
	 * Alive. Flag indicating that the scheduler is running (it should be alive unless the application is shutting down)
	 */
	protected volatile boolean alive = true;

	/**
	 * Paused. Flag indicating that the experiment is set to pause (used in stepping the experiment)
	 **/
	protected volatile boolean paused = true;

	/** The lock. */
	protected final Semaphore lock = new Semaphore(1);

	/** The execution thread. */
	private final Thread executionThread = new Thread(() -> { while (alive) { step(); } }, "Front end scheduler");

	/**
	 * Instantiates a new experiment scheduler.
	 *
	 * @param experiment
	 *            the experiment
	 */
	ThreadedExperimentScheduler(final IExperimentPlan experiment) {
		this.experiment = experiment;
		executionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		try {
			lock.acquire();
		} catch (final InterruptedException e) {}
		executionThread.start();
	}

	/**
	 * Step by step.
	 */
	@Override
	public void stepByStep() {
		pause();
		lock.release();
	}

	/**
	 * Start.
	 */
	@Override
	public void start() {
		resume();
		lock.release();
	}

	@Override
	protected void step() {
		if (paused) {
			try {
				lock.acquire();
			} catch (InterruptedException e) {
				alive = false;
			}
		}
		super.step();
	}

	@Override
	public void dispose() {
		alive = false;
		super.dispose();
		lock.release();
	}

	@Override
	public boolean paused() {
		return paused;
	}

	/**
	 * Pause.
	 */
	@Override
	public void pause() {
		paused = true;
	}

	/**
	 * Unpause.
	 */
	@Override
	public void resume() {
		paused = false;
	}

	/**
	 * Step back.
	 */
	// TODO : c'est moche .....
	@Override
	public void stepBack() {
		pause();
		experiment.getAgent().backward(experiment.getExperimentScope());// ?? scopes[0]);
	}
}
