package msi.gama.kernel.experiment;

import static msi.gama.kernel.experiment.IExperimentSpecies._INIT;
import static msi.gama.kernel.experiment.IExperimentSpecies._PAUSE;
import static msi.gama.kernel.experiment.IExperimentSpecies._RELOAD;
import static msi.gama.kernel.experiment.IExperimentSpecies._START;
import static msi.gama.kernel.experiment.IExperimentSpecies._STEP;
import static msi.gama.kernel.experiment.IExperimentSpecies._STOP;

import java.util.concurrent.ArrayBlockingQueue;

import msi.gama.common.util.GuiUtils;
import msi.gama.outputs.OutputSynchronizer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class ExperimentGUIHelper implements Runnable {

	ExperimentAgent agent;
	IExperimentSpecies experiment;
	protected volatile ArrayBlockingQueue<Integer> commands;
	public volatile Thread experimentThread;
	protected volatile boolean isOpen;

	ExperimentGUIHelper(ExperimentAgent agent) {
		this.agent = agent;
		experiment = agent.getSpecies();
		commands = new ArrayBlockingQueue(10);
		isOpen = true;
		experimentThread = new Thread(this, "Experiment " + agent.getName());
		experimentThread.start();
	}

	@Override
	public void run() {
		while (isOpen) {
			try {
				Integer i = commands.take();
				if ( i == null ) { throw new InterruptedException("Internal error. Shutting down the simulation"); }
				processUserCommand(i);
			} catch (InterruptedException e) {
				GAMA.closeCurrentExperimentOnException(new GamaRuntimeException("Cancelled"));
			}
		}
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void interrupt() {
		experimentThread.interrupt();
	}

	public void offer(int command) {
		commands.offer(command);
	}

	protected void processUserCommand(final int command) {
		switch (command) {
			case _INIT:
				GuiUtils.debug("ExperimentGUIHelper.processUserCommand: INIT");
				GAMA.updateSimulationState(GAMA.NOTREADY);
				try {
					OutputSynchronizer.waitForViewsToBeClosed();
					agent.userInitExperiment();
				} catch (GamaRuntimeException e) {
					GAMA.closeCurrentExperimentOnException(e);
				} catch (Exception e) {
					GAMA.closeCurrentExperimentOnException(new GamaRuntimeException(e));
				} finally {
					GAMA.updateSimulationState();
				}
				break;
			case _START:
				GuiUtils.debug("ExperimentGUIHelper.processUserCommand: START");
				try {
					agent.startSimulation();
				} catch (GamaRuntimeException e) {
					GAMA.closeCurrentExperimentOnException(e);
				} finally {
					GAMA.updateSimulationState(GAMA.RUNNING);
				}
				break;
			case _PAUSE:
				GuiUtils.debug("ExperimentGUIHelper.processUserCommand: PAUSE");
				GAMA.updateSimulationState(GAMA.PAUSED);
				agent.userPauseExperiment();
				break;
			case _STEP:
				GuiUtils.debug("ExperimentGUIHelper.processUserCommand: STEP");
				GAMA.updateSimulationState(GAMA.PAUSED);
				agent.userStepExperiment();
				break;
			case _STOP:
				GuiUtils.debug("ExperimentGUIHelper.processUserCommand: STOP");
				GAMA.updateSimulationState(GAMA.NONE);
				agent.userStopExperiment();
				break;
			case _RELOAD:
				GuiUtils.debug("ExperimentGUIHelper.processUserCommand: RELOAD");
				GAMA.updateSimulationState(GAMA.NOTREADY);
				try {
				agent.userReloadExperiment(true);
				} catch (GamaRuntimeException e) {
					GAMA.closeCurrentExperimentOnException(e);
				} catch (Exception e) {
					GAMA.closeCurrentExperimentOnException(new GamaRuntimeException(e));
				} finally {
					GAMA.updateSimulationState();
				}
				break;
		}
	}

}
