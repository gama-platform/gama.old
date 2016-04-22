/*********************************************************************************************
 *
 *
 * 'ExperimentController.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.concurrent.ArrayBlockingQueue;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGui;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class ExperimentController implements Runnable, IExperimentController {

	private final IExperimentPlan experiment;
	private boolean disposing;
	protected volatile ArrayBlockingQueue<Integer> commands;
	public volatile Thread commandThread;
	protected volatile boolean running = true;
	private final ExperimentScheduler scheduler;

	public ExperimentController(final IExperimentPlan experiment) {
		this.scheduler = new ExperimentScheduler(experiment);
		commands = new ArrayBlockingQueue(10);
		this.experiment = experiment;
	}

	private void launchCommandThread() {
		if (commandThread != null) {
			return;
		}
		if (experiment.isHeadless()) {
			commandThread = null;
		} else {
			commandThread = new Thread(this, "Front end controller");
			commandThread.start();
		}

	}

	@Override
	public boolean isDisposing() {
		return disposing;
	}

	@Override
	public IExperimentPlan getExperiment() {
		return experiment;
	}

	@Override
	public void run() {
		while (running) {
			try {
				final Integer i = commands.take();
				if (i == null) {
					throw new InterruptedException("Internal error. Please retry");
				}
				processUserCommand(i);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		commandThread = null;
	}

	public void offer(final int command) {
		if (isDisposing()) {
			return;
		}
		if (commandThread == null || !commandThread.isAlive()) {
			processUserCommand(command);
		} else {
			commands.offer(command);
		}
	}

	protected void processUserCommand(final int command) {
		switch (command) {
		case IExperimentController._OPEN:

			experiment.getExperimentScope().getGui().updateSimulationState(IGui.NOTREADY);
			try {
				launchCommandThread();
				// Needs to run in the controller thread
				if (commandThread == null) {
					experiment.open();
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							experiment.open();
						}
					}).start();
					;
				}
			} catch (final Exception e) {
				// scope.getGui().debug("Error when opening the experiment: " +
				// e.getMessage());
				closeExperiment(e);
			} finally {
				// AD : Moved to OutputSynchronizer
				// GAMA.updateSimulationState();
			}
			break;
		case IExperimentController._START:
			try {
				// scheduler.on_user_hold = false;
				scheduler.start();
			} catch (final GamaRuntimeException e) {
				closeExperiment(e);
			} finally {
				experiment.getExperimentScope().getGui().updateSimulationState(IGui.RUNNING);
			}
			break;
		case IExperimentController._PAUSE:
			experiment.getExperimentScope().getGui().updateSimulationState(IGui.PAUSED);
			scheduler.pause();
			break;
		case IExperimentController._STEP:
			experiment.getExperimentScope().getGui().updateSimulationState(IGui.PAUSED);
			scheduler.stepByStep();
			break;
		case IExperimentController._RELOAD:
			experiment.getExperimentScope().getGui().updateSimulationState(IGui.NOTREADY);
			try {
				final boolean wasRunning = !scheduler.paused && !GamaPreferences.CORE_AUTO_RUN.getValue();
				scheduler.pause();
				GAMA.getGui().waitStatus("Reloading...");
				experiment.reload();
				if (wasRunning) {
					processUserCommand(IExperimentController._START);
				} else {
					experiment.getExperimentScope().getGui().informStatus("Experiment reloaded");
				}
			} catch (final GamaRuntimeException e) {
				closeExperiment(e);
			} catch (final Exception e) {
				closeExperiment(GamaRuntimeException.create(e, experiment.getExperimentScope()));
			} finally {
				experiment.getExperimentScope().getGui().updateSimulationState();
			}
			break;
		}
	}

	@Override
	public void userPause() {
		// TODO Should maybe be done directly (so as to pause immediately)
		offer(IExperimentController._PAUSE);
	}

	@Override
	public void directPause() {
		processUserCommand(IExperimentController._PAUSE);
	}

	@Override
	public void userStep() {
		if (experiment == null) {
			return;
		}
		offer(IExperimentController._STEP);
	}

	@Override
	public void userReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		if (experiment == null) {
			return;
		}
		offer(IExperimentController._RELOAD);
	}

	@Override
	public void directOpenExperiment() {
		processUserCommand(IExperimentController._OPEN);
	}

	public void directReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		if (experiment == null) {
			return;
		}
		processUserCommand(IExperimentController._RELOAD);
	}

	@Override
	public void userStart() {
		offer(IExperimentController._START);
	}

	@Override
	public void userOpen() {
		offer(_OPEN);
	}

	@Override
	public void dispose() {
		if (experiment != null) {
			// System.out.println("Contoller.dipose BEGIN");
			try {
				scheduler.pause();
				experiment.getExperimentScope().getGui().updateSimulationState(IGui.NOTREADY);
				experiment.getExperimentScope().getGui().closeDialogs();
				// Dec 2015 This method is normally now called from
				// ExperimentPlan.dispose()
				// experiment.dispose();
				// experiment = null;
			} finally {
				running = false;
				scheduler.dispose();
				experiment.getExperimentScope().getGui().updateSimulationState(IGui.NONE);
				if (commandThread != null && commandThread.isAlive()) {
					commands.offer(-1);
				}
				// System.out.println("Contoller.dipose END");
			}
		}
	}

	@Override
	public void startPause() {
		if (experiment == null) {
			return;
		} else if (scheduler.paused) {
			userStart();
		} else {
			userPause();
		}
	}

	@Override
	public void close() {
		closeExperiment(null);
	}

	public void closeExperiment(final Exception e) {
		disposing = true;
		// System.out.println("CloseExperiment : disposing = true");
		if (e != null) {
			GAMA.getGui().errorStatus(e.getMessage());
		}

		experiment.dispose(); // will call own dispose() later
	}

	@Override
	public ExperimentScheduler getScheduler() {
		return scheduler;
	}

}
