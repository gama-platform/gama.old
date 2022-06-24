/*******************************************************************************************************
 *
 * ExperimentController.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import msi.gama.common.interfaces.IGui;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.job.ManualExperimentJob; 
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentController;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ExperimentController.
 */
public class ServerExperimentController implements IExperimentController {

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
//	private final Thread executionThread =
//			new Thread(() -> { while (experimentAlive) { step(); } }, "Front end scheduler");

	public MyRunnable executionThread;
	/**
	 * The Class OwnRunnable.
	 */
	public class MyRunnable implements Runnable {

		/** The sim. */
		final ManualExperimentJob mexp;

		/**
		 * Instantiates a new own runnable.
		 *
		 * @param s the s
		 */
		MyRunnable(final ManualExperimentJob s) {
			mexp = s;
		}

		/**
		 * Run.
		 */
		@Override
		public void run() { 
			while (experimentAlive) {
				if (mexp.simulator.isInterrupted()) {
					break;
				}
				final SimulationAgent sim = mexp.simulator.getSimulation();
			 
				final IScope scope = sim == null ? GAMA.getRuntimeScope() : sim.getScope();
				if (Cast.asBool(scope, mexp.endCondition.value(scope))) {
					if(!mexp.endCond.equals("")) {mexp.socket.send("finish");}
					break;
				} 
				step();
			} 
		}
	}
	
	public  ManualExperimentJob _job;
	/** The experiment. */
	private IExperimentPlan experiment;

	/** The agent. */
	private IExperimentAgent agent;

	/** The disposing. */
	private boolean disposing;

	/** The commands. */
	protected volatile ArrayBlockingQueue<Integer> commands;

	/** The command thread. */
	private Thread commandThread = new Thread(() -> {
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
	public ServerExperimentController(ManualExperimentJob j) { 
		commands = new ArrayBlockingQueue<>(10);
		_job=j;
		executionThread = new MyRunnable(j);
//		this.experiment = experiment;
//		executionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		try {
			lock.acquire();
		} catch (final InterruptedException e) {}
		commandThread.start();
//		executionThread.start();
	}

	@Override
	public boolean isDisposing() { return disposing; }

	@Override
	public IExperimentPlan getExperiment() { return experiment; }
	
	public void setExperiment(final IExperimentPlan exp) { this.experiment=exp; }
	
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
	 * @param command the command
	 */
	private void processUserCommand(final int command) {
		switch (command) {
		case _OPEN:
			try {
				_job.loadAndBuildWithJson();
////				this.getSimulation().getExperimentPlan().open();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException
					| GamaHeadlessException e) {
				e.printStackTrace();
			}
//			scope.getGui().updateExperimentState(scope, IGui.NOTREADY);
//			try {
//				load();
//				simulator.setup(experimentName, this.seed, params);
//
////				this.getSimulation().getExperimentPlan().open();
////				new Thread(() -> experiment.open()).start();
//			} catch (final Exception e) {
////				DEBUG.ERR("Error when opening the experiment: " + e.getMessage());
//				closeExperiment(e);
//			}
			break;
		case _START:
			try {
				start();
			} catch (final GamaRuntimeException e) {
				e.printStackTrace();
				closeExperiment(e);
			} finally {
//				scope.getGui().updateExperimentState(scope, IGui.RUNNING);
			}
			break;
		case _PAUSE:
//			if (!disposing) {
//				scope.getGui().updateExperimentState(scope, IGui.PAUSED);
//			}
			pause();
			break;
		case _STEP:
//			scope.getGui().updateExperimentState(scope, IGui.PAUSED);
			stepByStep();
			break;
		case _BACK:
//			scope.getGui().updateExperimentState(scope, IGui.PAUSED);
//			stepBack();
			pause();
			getExperiment().getAgent().backward(getScope());// ?? scopes[0]);
			break;
		case _RELOAD:
//			scope.getGui().updateExperimentState(scope, IGui.NOTREADY);
//			try {
//				lock.acquire();
//			} catch (final InterruptedException e) {
//				e.printStackTrace();
//			}
			try {
//				final boolean wasRunning = !isPaused() && !this.getSimulation().getExperimentPlan().isAutorun();

//				scope.getGui().getStatus().waitStatus("Reloading...");
//				this.getSimulation().getExperimentPlan().getAgent().dispose();
//				initParam(params);
				experiment.dispose();
				_job.simulator.dispose();

//				this.getSimulation().getExperimentPlan().open();
//				this.getSimulation().getExperimentPlan().reload();

//				this.loadAndBuild(params);

				_job.loadAndBuildWithJson();
				executionThread=null;
				commandThread.interrupt();
				commandThread = null;
				executionThread = new MyRunnable(_job);
				commandThread = new Thread(() -> {
					while (acceptingCommands) {
						try {
							processUserCommand(commands.take());
						} catch (final Exception e) {
						}
					}
				}, "Front end controller");
				commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
				try {
					lock.acquire();
				} catch (final InterruptedException e) {}
				experimentAlive=true;acceptingCommands=true;
				disposing = false;
				commandThread.start();
//				_job.simulator.setup(_job.experimentName, _job.seed, _job.params, _job);
				_job.server.getDefaultApp().processorQueue.execute(executionThread);
				_job.socket.send("reload");
//				if (wasRunning) {
//					processUserCommand(_START);
//				} else {
////					scope.getGui().getStatus().informStatus("Experiment reloaded");
//				}
			} catch (final GamaRuntimeException e) {
				e.printStackTrace();
				closeExperiment(e);
			} catch (final Throwable e) {
				closeExperiment(GamaRuntimeException.create(e, scope));
			} finally {
//				scope.getGui().updateExperimentState(scope);
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
			} finally {
				acceptingCommands = false;
				experimentAlive = false;
				lock.release();
				getScope().getGui().updateExperimentState(getScope(), IGui.NONE);
				if (commandThread != null && commandThread.isAlive()) { commands.offer(-1); }
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
		if (e != null) { getScope().getGui().getStatus().errorStatus(e.getMessage()); }
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
				scope.setInterrupted();
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
//			if (scope == null) return;
//			if (!scope.step(agent).passed()) {
//				scope.setInterrupted();
//				this.pause();
//			}
			_job.doStep();

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
