/*******************************************************************************************************
 *
 * DefaultExperimentController.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.concurrent.ArrayBlockingQueue;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExperimentStateListener;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ExperimentController.
 */
public class DefaultExperimentController extends AbstractExperimentController {

	/** The execution thread. */

	/** The agent. */
	private IExperimentAgent agent;

	/** The r. */

	/** The command thread. */
	private final Thread executionThread;

	/**
	 * Instantiates a new experiment controller.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public DefaultExperimentController(final IExperimentPlan experiment) {
		commands = new ArrayBlockingQueue<>(10);
		this.experiment = experiment;
		executionThread = new Thread(() -> { while (experimentAlive) { step(); } }, "Front end scheduler");
		executionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		try {
			lock.acquire();
		} catch (final InterruptedException e) {}
		commandThread.start();
		executionThread.start();
	}

	/**
	 * Process user command.
	 *
	 * @param command
	 *            the command
	 */
	@Override
	protected void processUserCommand(final ExperimentCommand command) {
		final IScope scope = getScope();
		switch (command) {
			case _CLOSE:
				GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_NONE);
				break;
			case _OPEN:
				GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_NOTREADY);
				try {
					new Thread(() -> experiment.open()).start();
				} catch (final Exception e) {
					DEBUG.ERR("Error when opening the experiment: " + e.getMessage());
					closeExperiment(e);
				}
				break;
			case _START:
				try {
					paused = false;
					lock.release();
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} finally {
					GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_RUNNING);
				}
				break;
			case _PAUSE:
				paused = true;
				if (!disposing) { GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_PAUSED); }
				break;
			case _STEP:
				GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_PAUSED);
				paused = true;
				lock.release();
				break;
			case _BACK:
				GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_PAUSED);
				paused = true;
				experiment.getAgent().backward(getScope());// ?? scopes[0]);
				break;
			case _RELOAD:
				GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_NOTREADY);
				try {
					final boolean wasRunning = !isPaused() && !experiment.isAutorun();
					paused = true;
					scope.getGui().getStatus().waitStatus(scope, "Reloading...");
					experiment.reload();
					if (wasRunning) {
						processUserCommand(ExperimentCommand._START);
					} else {
						scope.getGui().getStatus().informStatus(scope, "Experiment reloaded");
					}
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} catch (final Throwable e) {
					closeExperiment(GamaRuntimeException.create(e, scope));
				} finally {
					GAMA.updateExperimentState(experiment);
				}
				break;
		}
	}

	@Override
	public void dispose() {
		scope = null;
		agent = null;
		if (experiment != null) {
			try {
				paused = true;
				GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_NOTREADY);
				getScope().getGui().closeDialogs(getScope());
				// Dec 2015 This method is normally now called from
				// ExperimentPlan.dispose()
				GAMA.updateExperimentState(experiment, IExperimentStateListener.STATE_NONE);
			} finally {
				acceptingCommands = false;
				experimentAlive = false;
				lock.release();
				if (commandThread != null && commandThread.isAlive()) { commands.offer(ExperimentCommand._CLOSE); }
			}
		}
	}

	@Override
	public void close() {
		closeExperiment(null);
	}

	/**
	 * Close experiment.
	 *
	 * @param e
	 *            the e
	 */
	public void closeExperiment(final Exception e) {
		disposing = true;
		if (e != null) { getScope().getGui().getStatus().errorStatus(scope, e.getMessage()); }
		experiment.dispose(); // will call own dispose() later
	}

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */
	@Override
	public boolean isPaused() { return paused; }

	/**
	 * Schedule.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 */
	@Override
	public void schedule(final ExperimentAgent agent) {
		this.agent = agent;
		scope = agent.getScope();
		try {
			if (!scope.init(agent).passed()) {
				scope.setDisposeStatus();
			} else if (agent instanceof TestAgent || agent.getSpecies().isAutorun()) { asynchronousStart(); }
		} catch (final Throwable e) {
			if (scope != null && scope.interrupted()) {} else if (!(e instanceof GamaRuntimeException)) {
				GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
			}
		}
	}

	/**
	 * Step.
	 */
	protected void step() {
		if (paused) {
			try {
				lock.acquire();
			} catch (InterruptedException e) {
				experimentAlive = false;
			}
		}
		try {
			if (scope == null) return;
			if (!scope.step(agent).passed()) {
				scope.setDisposeStatus();
				paused = true;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

}
