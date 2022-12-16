/*******************************************************************************************************
 *
 * ExperimentController.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import msi.gama.common.interfaces.IGui;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ExperimentController.
 */
public class ExperimentController implements IExperimentController {

	/** The scope. */
	IScope scope;

	/**
	 * Alive. Flag indicating that the scheduler is running (it should be alive unless the application is shutting down)
	 */
	protected volatile boolean experimentAlive = true;

	/**
	 * Paused. Flag indicating that the experiment is set to pause (used in stepping the experiment)
	 **/
	protected volatile boolean paused = true;

	/** AcceptingCommands. A flag indicating that the command thread is accepting commands */
	protected volatile boolean acceptingCommands = true;

	/** The lock. Used to pause the experiment */
	protected final Semaphore lock = new Semaphore(1);

	/** The execution thread. */
	private final Thread executionThread = new Thread(() -> {
		while (experimentAlive) { step(); }
	}, "Front end scheduler");

	/** The experiment. */
	private final IExperimentPlan experiment;

	/** The agent. */
	private IExperimentAgent agent;

	/** The disposing. */
	private boolean disposing;

	/** The commands. */
	protected volatile ArrayBlockingQueue<Integer> commands;

	/** The command thread. */
	private final Thread commandThread = new Thread(() -> {
		while (acceptingCommands) {
			try {
				processUserCommand(commands.take());
			} catch (final Exception e) {}
		}
	}, "Front end controller");

	/**
	 * Instantiates a new experiment controller.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public ExperimentController(final IExperimentPlan experiment) {
		commands = new ArrayBlockingQueue<>(10);
		this.experiment = experiment;
		executionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		try {
			lock.acquire();
		} catch (final InterruptedException e) {}
		commandThread.start();
		executionThread.start();
	}

	@Override
	public boolean isDisposing() { return disposing; }

	@Override
	public IExperimentPlan getExperiment() { return experiment; }

	/**
	 * Offer.
	 *
	 * @param command
	 *            the command
	 */
	private void offer(final int command) {
		if (experiment == null || isDisposing()) return;
		commands.offer(command);
	}

	/**
	 * Process user command.
	 *
	 * @param command
	 *            the command
	 */
	private void processUserCommand(final int command) {
		final IScope scope = getScope();
		switch (command) {
			case _CLOSE:
				scope.getGui().updateExperimentState(scope, IGui.NONE);
				// scope.getGui().getStatus().neutralStatus(scope, "No simulation running");
				break;
			case _OPEN:
				scope.getGui().updateExperimentState(scope, IGui.NOTREADY);
				try {
					new Thread(() -> experiment.open()).start();
				} catch (final Exception e) {
					DEBUG.ERR("Error when opening the experiment: " + e.getMessage());
					closeExperiment(e);
				}
				break;
			case _START:
				try {
					start();
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} finally {
					scope.getGui().updateExperimentState(scope, IGui.RUNNING);
				}
				break;
			case _PAUSE:
				if (!disposing) { scope.getGui().updateExperimentState(scope, IGui.PAUSED); }
				pause();
				break;
			case _STEP:
				scope.getGui().updateExperimentState(scope, IGui.PAUSED);
				stepByStep();
				break;
			case _BACK:
				scope.getGui().updateExperimentState(scope, IGui.PAUSED);
				stepBack();
				break;
			case _RELOAD:
				scope.getGui().updateExperimentState(scope, IGui.NOTREADY);
				try {
					final boolean wasRunning = !isPaused() && !experiment.isAutorun();
					pause();
					scope.getGui().getStatus().waitStatus(scope, "Reloading...");
					experiment.reload();
					if (wasRunning) {
						processUserCommand(_START);
					} else {
						scope.getGui().getStatus().informStatus(scope, "Experiment reloaded");
					}
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} catch (final Throwable e) {
					closeExperiment(GamaRuntimeException.create(e, scope));
				} finally {
					scope.getGui().updateExperimentState(scope);
				}
				break;
		}
	}

	@Override
	public void userPause() {
		// TODO Should maybe be done directly (so as to pause immediately)
		offer(_PAUSE);
	}

	@Override
	public void directPause() {
		processUserCommand(_PAUSE);
	}

	@Override
	public void userStep() {
		offer(_STEP);
	}

	@Override
	public void userStepBack() {
		offer(_BACK);
	}

	@Override
	public void userReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		offer(_RELOAD);
	}

	@Override
	public void directOpenExperiment() {
		processUserCommand(_OPEN);
	}

	@Override
	public void userStart() {
		offer(_START);
	}

	@Override
	public void userOpen() {
		offer(_OPEN);
	}

	@Override
	public void dispose() {
		scope = null;
		agent = null;
		if (experiment != null) {
			try {
				pause();
				getScope().getGui().updateExperimentState(getScope(), IGui.NOTREADY);
				getScope().getGui().closeDialogs(getScope());
				// Dec 2015 This method is normally now called from
				// ExperimentPlan.dispose()
				getScope().getGui().updateExperimentState(getScope(), IGui.NONE);
			} finally {
				acceptingCommands = false;
				experimentAlive = false;
				lock.release();
				if (commandThread != null && commandThread.isAlive()) {
					commands.offer(_CLOSE);
					// processUserCommand(_CLOSE);
				}
			}
		}
	}

	@Override
	public void startPause() {
		if (isPaused()) {
			userStart();
		} else {
			userPause();
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
		if (e != null) { getScope().getGui().getStatus().errorStatus(getScope(), e.getMessage()); }
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
			} else if (agent.getSpecies().isAutorun()) { userStart(); }
		} catch (final Throwable e) {
			if (scope != null && scope.interrupted()) {} else if (!(e instanceof GamaRuntimeException)) {
				GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
			}
		}
	}

	/**
	 * Step by step.
	 */
	public void stepByStep() {
		pause();
		lock.release();
	}

	/**
	 * Start.
	 */
	public void start() {
		paused = false;
		lock.release();
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
				this.pause();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pause.
	 */
	private void pause() {
		paused = true;
	}

	/**
	 * Step back.
	 */
	// TODO : c'est moche .....
	private void stepBack() {
		pause();
		experiment.getAgent().backward(getScope());// ?? scopes[0]);
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	IScope getScope() { return scope == null ? experiment.getExperimentScope() : scope; }

}
