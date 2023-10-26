/*******************************************************************************************************
 *
 * GamaServerExperimentController.java, in msi.gama.headless, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.server;

import java.io.IOException;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.kernel.experiment.AbstractExperimentController;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExperimentStateListener;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import msi.gama.util.file.json.Jsoner;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ExperimentController.
 */
public class GamaServerExperimentController extends AbstractExperimentController {

	/** The parameters. */
	final GamaJsonList parameters;

	/** The stop condition. */
	final String stopCondition;

	/** The execution thread. */
	public MyRunnable executionThread;

	/**
	 * The Class OwnRunnable.
	 */
	public class MyRunnable implements Runnable {

		/** The sim. */
		final GamaServerExperimentJob mexp;

		/**
		 * Instantiates a new own runnable.
		 *
		 * @param s
		 *            the s
		 */
		MyRunnable(final GamaServerExperimentJob s) {
			mexp = s;
		}

		/**
		 * Run.
		 */
		@Override
		public void run() {
			try {

				while (experimentAlive) {
					if (mexp.simulator.isInterrupted()) { break; }
					final SimulationAgent sim = mexp.simulator.getSimulation();
					final IExperimentAgent exp = sim == null ? null : sim.getExperiment();
					final IScope scope = sim == null ? GAMA.getRuntimeScope() : sim.getScope();
					if (Cast.asBool(scope, exp.getStopCondition().value(scope))) {
						if (!"".equals(stopCondition)) {
							mexp.socket
									.send(Jsoner
											.serialize(
													new CommandResponse(GamaServerMessage.Type.SimulationEnded, "",
															(IMap<String, Object>) mexp.simulator.getExperimentPlan()
																	.getAgent().getAttribute("%%playCommand%%"),
															false)));
						}
						break;
					}
					step();
				}
			} catch (Exception e) {
				DEBUG.OUT(e);
			}
		}
	}

	/** The job. */
	public GamaServerExperimentJob _job;

	/** The socket. */
	private final WebSocket socket;

	/** The redirect console. */
	public final boolean redirectConsole;

	/** The redirect status. */
	public final boolean redirectStatus;

	/** The redirect dialog. */
	public final boolean redirectDialog;

	/** The redirect runtime. */
	public final boolean redirectRuntime;

	/**
	 * Instantiates a new experiment controller.
	 *
	 * @param socket
	 *
	 * @param experiment
	 *            the experiment
	 */
	public GamaServerExperimentController(final GamaServerExperimentJob j, final GamaJsonList parameters,
			final String stopCondition, final WebSocket sock, final boolean console, final boolean status,
			final boolean dialog, final boolean runtime) {
		_job = j;
		socket = sock;
		redirectConsole = console;
		this.parameters = parameters;
		this.stopCondition = stopCondition;
		redirectStatus = status;
		redirectDialog = dialog;
		redirectRuntime = runtime;
		executionThread = new MyRunnable(j);

		commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		try {
			lock.acquire();
		} catch (final InterruptedException e) {}
		commandThread.start();
	}

	/**
	 * Process user command.
	 *
	 * @param command
	 *            the command
	 */
	@Override
	protected void processUserCommand(final ExperimentCommand command) {
		switch (command) {
			case _OPEN:
				try {
					_job.loadAndBuildWithJson(parameters, stopCondition);
				} catch (IOException | GamaHeadlessException e) {
					DEBUG.OUT(e);
					GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
					// socket.send(Jsoner.serialize(new GamaServerMessage(GamaServerMessageType.SimulationError, e)));
				}
				break;
			case _START:
				paused = false;
				lock.release();
				break;
			case _PAUSE:
				paused = true;
				break;
			case _STEP:
				paused = true;
				lock.release();
				break;
			case _BACK:
				paused = true;
				experiment.getAgent().backward(getScope());
				break;
			case _RELOAD:

				try {

					experiment.dispose();
					_job.simulator.dispose();

					_job.loadAndBuildWithJson(parameters, stopCondition);
					executionThread = null;
					commandThread.interrupt();
					commandThread = null;
					executionThread = new MyRunnable(_job);
					commandThread = new Thread(() -> {
						while (acceptingCommands) {
							try {
								processUserCommand(commands.take());
							} catch (final Exception e) {}
						}
					}, "Front end controller");
					commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
					try {
						lock.acquire();
					} catch (final InterruptedException e) {}
					experimentAlive = true;
					acceptingCommands = true;
					disposing = false;
					commandThread.start();
					_job.server.execute(executionThread);

				} catch (final GamaRuntimeException e) {
					e.printStackTrace();
					closeExperiment(e);
					GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
					// socket.send(Jsoner.serialize(new GamaServerMessage(GamaServerMessageType.SimulationError, e)));
				} catch (final Throwable e) {
					closeExperiment(GamaRuntimeException.create(e, scope));
					GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
					// socket.send(Jsoner.serialize(new GamaServerMessage(GamaServerMessageType.SimulationError, e)));

				} finally {
					// scope.getGui().updateExperimentState(scope);
				}
				break;
		}
	}

	@Override
	public void dispose() {
		scope = null;
		if (experiment != null) {
			try {
				paused = true;
				GAMA.updateExperimentState(experiment, IExperimentStateListener.State.NOTREADY);
				getScope().getGui().closeDialogs(getScope());
				// Dec 2015 This method is normally now called from
				// ExperimentPlan.dispose()
			} finally {
				acceptingCommands = false;
				experimentAlive = false;
				lock.release();
				GAMA.updateExperimentState(experiment, IExperimentStateListener.State.NONE);
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
		scope = agent.getScope();
		scope.setData("socket", socket);
		scope.setData("exp_id", _job.getExperimentID());
		scope.setData("console", redirectConsole);
		scope.setData("status", redirectStatus);
		scope.setData("dialog", redirectDialog);
		scope.setData("runtime", redirectRuntime);
		try {
			if (!scope.init(agent).passed()) { scope.setDisposeStatus(); } // else if (agent.getSpecies().isAutorun()) {
																			// userStart(); }
		} catch (final Throwable e) {
			if (scope != null && scope.interrupted()) {} else if (!(e instanceof GamaRuntimeException)) {
				GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
			}
		}
	}

	/**
	 * Step.
	 */
	public void step() {
		if (paused) {
			try {
				lock.acquire();
			} catch (InterruptedException e) {
				experimentAlive = false;
			}
		}
		try {
			_job.doStep();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pause.
	 */
	public void pause() {
		paused = true;
	}

}
