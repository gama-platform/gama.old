/*******************************************************************************************************
 *
 * ExperimentJob.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.job;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import msi.gama.common.interfaces.IGui;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.RichOutput;
import msi.gama.headless.listener.GamaWebSocketServer;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import msi.gaml.compilation.GAML;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types; 

/**
 * The Class ExperimentJob.
 */
public class ManualExperimentJob extends ExperimentJob implements IExperimentController {
	protected WebSocketServer server;
	protected WebSocket socket;
//	public boolean paused = false;
	public boolean stepping = false;
//	public Thread internalThread;
	public GamaJsonList params;
	/** The scope. */
	IScope scope;
	/**
	 * Alive. Flag indicating that the scheduler is running (it should be alive
	 * unless the application is shutting down)
	 */
	protected volatile boolean experimentAlive = true;

	/**
	 * Paused. Flag indicating that the experiment is set to pause (used in stepping
	 * the experiment)
	 **/
	protected volatile boolean paused = true;

	/**
	 * AcceptingCommands. A flag indicating that the command thread is accepting
	 * commands
	 */
	protected volatile boolean acceptingCommands = true;

	/** The lock. Used to pause the experiment */
	protected final Semaphore lock = new Semaphore(1);

	/** The execution thread. */
//	private final Thread executionThread = new Thread(() -> {
//		while (experimentAlive) {
//			step();
//		}
//	}, "Front end scheduler");
	public MyRunnable executionThread;

	/**
	 * The Class OwnRunnable.
	 */
	public static class MyRunnable implements Runnable {

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
			while (mexp.experimentAlive) {
				if (mexp.simulator.isInterrupted()) {
					break;
				}
				final SimulationAgent sim = mexp.simulator.getSimulation();
				final IScope scope = sim == null ? GAMA.getRuntimeScope() : sim.getScope();
				if (Cast.asBool(scope, mexp.endCondition.value(scope))) {
					break;
				}
				mexp.step();
			}
		}
	}

	/** The disposing. */
	private boolean disposing;

	/** The commands. */
	protected volatile ArrayBlockingQueue<Integer> commands;

	/** The command thread. */
	private final Thread commandThread = new Thread(() -> {
		while (acceptingCommands) {
			try {
				processUserCommand(commands.take());
			} catch (final Exception e) {
			}
		}
	}, "Front end controller");

	public ManualExperimentJob(ExperimentJob j, WebSocketServer gamaWebSocketServer, WebSocket sk, final GamaJsonList p) {
		super(j);
		server = gamaWebSocketServer;
		socket = sk;
		params = p;
		commands = new ArrayBlockingQueue<>(10);
//		this.experiment = experiment;
		executionThread = new MyRunnable(this);
//		executionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
		try {
			lock.acquire();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		commandThread.start();
//		executionThread.start();
	}

	public boolean isDisposing() {
		return disposing;
	}

	@Override
	public IExperimentPlan getExperiment() {
		return this.getSimulation().getExperimentPlan();
	}

	@Override
	public void doStep() {
		this.step = simulator.step();
	}

	/**
	 * Offer.
	 *
	 * @param command the command
	 */
	private void offer(final int command) {
		if (this.getSimulation().getExperimentPlan() == null || isDisposing())
			return;
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
				this.loadAndBuild(params);
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
			executionThread=null;
			executionThread = new MyRunnable(this);
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			try {
//				final boolean wasRunning = !isPaused() && !this.getSimulation().getExperimentPlan().isAutorun();
				pause();
//				scope.getGui().getStatus().waitStatus("Reloading...");
				this.getSimulation().getExperimentPlan().getAgent().dispose();
				initParam(params);

				this.getSimulation().getExperimentPlan().open();
//				this.getSimulation().getExperimentPlan().reload();
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
//		agent = null;
		if (this.getSimulation().getExperimentPlan() != null) {
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
				if (commandThread != null && commandThread.isAlive()) {
					commands.offer(-1);
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
	 * @param e the e
	 */
	public void closeExperiment(final Exception e) {
		disposing = true;
		if (e != null) {
			getScope().getGui().getStatus().errorStatus(e.getMessage());
		}
		this.getSimulation().getExperimentPlan().dispose(); // will call own dispose() later
	}

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */

	public boolean isPaused() {
		return paused;
	}

	/**
	 * Schedule.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 */
	public void schedule(final ExperimentAgent agent) {
//		this.agent = agent;
		scope = agent.getScope();
		try {
			if (!scope.init(agent).passed()) {
				scope.setInterrupted();
			} else if (agent.getSpecies().isAutorun()) {
				userStart();
			}
		} catch (final Throwable e) {
			if (scope != null && scope.interrupted()) {
			} else if (!(e instanceof GamaRuntimeException)) {
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
//			IScope scope=this.getSimulation().getExperimentPlan().getAgent().getScope();
//			if (scope == null)
//				return;
//			if (!scope.step(this.getSimulation().getExperimentPlan().getAgent()).passed()) {
//				scope.setInterrupted();
//				this.pause();
//			}else {
//				exportVariables();
//			}
			doStep();
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
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	IScope getScope() {
		return scope == null ? this.getSimulation().getExperimentPlan().getExperimentScope() : scope;
	}

	public void loadAndBuild(GamaJsonList p) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException, GamaHeadlessException {

		this.load();
		this.setup();
//		initParam(p);
		simulator.setup(experimentName, this.seed, p);
		initEndContion("");

	}

	public void initParam(GamaJsonList p) {
		params = p;
		if (params != null) {
			final ExperimentPlan curExperiment = (ExperimentPlan) simulator.getExperimentPlan();
			for (var O : ((GamaJsonList) params).listValue(null, Types.MAP, false)) {
				IMap<String, Object> m = (IMap<String, Object>) O;
				curExperiment.setParameterValueByTitle(curExperiment.getExperimentScope(), m.get("name").toString(),
						m.get("value"));
			}
		}
	}

	// Initialize the enCondition
	public void initEndContion(String cond) {
		if (cond == null || "".equals(cond)) {
			endCondition = IExpressionFactory.FALSE_EXPR;
		} else {
			endCondition = GAML.getExpressionFactory().createExpr(cond, simulator.getModel().getDescription());
			// endCondition = GAML.compileExpression(untilCond, simulator.getSimulation(),
			// true);
		}
		if (endCondition.getGamlType() != Types.BOOL)
			throw GamaRuntimeException.error("The until condition of the experiment should be a boolean",
					simulator.getSimulation().getScope());
	}

	@Override
	public void exportVariables() {
		final int size = this.listenedVariables.length;
		if (size == 0)
			return;
		for (int i = 0; i < size; i++) {
			final ListenedVariable v = this.listenedVariables[i];
			if (this.step % v.frameRate == 0) {
				final RichOutput out = simulator.getRichOutput(v);
				if (out == null || out.getValue() == null) {
				} else if (out.getValue() instanceof BufferedImage) {
					try {
						BufferedImage bi = (BufferedImage) out.getValue();
						ByteArrayOutputStream out1 = new ByteArrayOutputStream();
						ImageIO.write(bi, "png", out1);

						byte[] array1 = out1.toByteArray();
						byte[] array2 = { (byte) 0 };
						byte[] array3 = { (byte) i };
						byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length + array3.length);
						System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
						System.arraycopy(array3, 0, joinedArray, array1.length + array2.length, array3.length);

						ByteBuffer byteBuffer = ByteBuffer.wrap(joinedArray);
						if (!socket.isClosing() && !socket.isClosed())
							socket.send(byteBuffer);
//						server.broadcast(byteBuffer);
						out1.close();
						byteBuffer.clear();

					} catch (IOException e) {
						e.printStackTrace();
					}
//					v.setValue(writeImageInFile((BufferedImage) out.getValue(), v.getName(), v.getPath()), step,
//							out.getType());
				} else {
					byte[] array1 = (out.getName() + ": " + out.getValue().toString()).getBytes();
					byte[] array2 = { (byte) 1 };
					byte[] array3 = { (byte) i };
					byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length + array3.length);
					System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
					System.arraycopy(array3, 0, joinedArray, array1.length + array2.length, array3.length);

					ByteBuffer byteBuffer = ByteBuffer.wrap(joinedArray);
					if (!socket.isClosing() && !socket.isClosed())
						socket.send(byteBuffer);
					v.setValue(out.getValue(), out.getStep(), out.getType());
				}
			} else {
				v.setValue(null, this.step);
			}
		}
//		if (this.outputFile != null) {
//			this.outputFile.writeResultStep(this.step, this.listenedVariables);
//		}

	}

}
