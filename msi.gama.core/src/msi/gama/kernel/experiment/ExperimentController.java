/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.ExperimentController.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.concurrent.ArrayBlockingQueue;

import msi.gama.common.interfaces.IGui;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

public class ExperimentController implements Runnable, IExperimentController {

	private final IExperimentPlan experiment;
	private boolean disposing;
	protected volatile ArrayBlockingQueue<Integer> commands;
	public volatile Thread commandThread;
	protected volatile boolean running = true;
	private final ExperimentScheduler scheduler;

	public ExperimentController(final IExperimentPlan experiment) {
		this.scheduler = new ExperimentScheduler(experiment);
		commands = new ArrayBlockingQueue<>(10);
		this.experiment = experiment;
	}

	private void launchCommandThread() {
		if (commandThread != null) { return; }
		if (experiment.isHeadless()) {
			commandThread = null;
		} else {
			commandThread = new Thread(this, "Front end controller");
			commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
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
				if (i == null) { throw new InterruptedException("Internal error. Please retry"); }
				processUserCommand(i);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		commandThread = null;
	}

	public void offer(final int command) {
		if (isDisposing()) { return; }
		if (commandThread == null || !commandThread.isAlive()) {
			processUserCommand(command);
		} else {
			commands.offer(command);
		}
	}

	protected void processUserCommand(final int command) {
		final IScope scope = experiment.getExperimentScope();
		switch (command) {
			case IExperimentController._OPEN:

				experiment.getExperimentScope().getGui().updateExperimentState(scope, IGui.NOTREADY);
				try {
					launchCommandThread();
					// Needs to run in the controller thread
					if (commandThread == null) {
						experiment.open();
					} else {
						new Thread(() -> experiment.open()).start();
						;
					}
				} catch (final Exception e) {
					DEBUG.ERR("Error when opening the experiment: " + e.getMessage());
					closeExperiment(e);
				}
				break;
			case IExperimentController._START:
				try {
					scheduler.start();
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} finally {
					experiment.getExperimentScope().getGui().updateExperimentState(scope, IGui.RUNNING);
				}
				break;
			case IExperimentController._PAUSE:
				experiment.getExperimentScope().getGui().updateExperimentState(scope, IGui.PAUSED);
				scheduler.pause();
				break;
			case IExperimentController._STEP:
				experiment.getExperimentScope().getGui().updateExperimentState(scope, IGui.PAUSED);
				scheduler.stepByStep();
				break;
			case IExperimentController._BACK:
				experiment.getExperimentScope().getGui().updateExperimentState(scope, IGui.PAUSED);
				scheduler.stepBack();
				break;
			case IExperimentController._RELOAD:
				experiment.getExperimentScope().getGui().updateExperimentState(scope, IGui.NOTREADY);
				try {
					final boolean wasRunning = !scheduler.paused && !experiment.isAutorun();
					scheduler.pause();
					GAMA.getGui().getStatus(scope).waitStatus("Reloading...");
					experiment.reload();
					if (wasRunning) {
						processUserCommand(IExperimentController._START);
					} else {
						experiment.getExperimentScope().getGui().getStatus(scope).informStatus("Experiment reloaded");
					}
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} catch (final Throwable e) {
					closeExperiment(GamaRuntimeException.create(e, experiment.getExperimentScope()));
				} finally {
					experiment.getExperimentScope().getGui().updateExperimentState(scope);
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
		if (experiment == null) { return; }
		offer(IExperimentController._STEP);
	}

	@Override
	public void stepBack() {
		if (experiment == null) { return; }
		offer(IExperimentController._BACK);
	}

	@Override
	public void userReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		if (experiment == null) { return; }
		// GAMA.getGui().openSimulationPerspective(null, null);
		offer(IExperimentController._RELOAD);
	}

	@Override
	public void directOpenExperiment() {
		processUserCommand(IExperimentController._OPEN);
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
			// DEBUG.OUT("Contoller.dipose BEGIN");
			final IScope scope = experiment.getExperimentScope();
			try {
				scheduler.pause();
				experiment.getExperimentScope().getGui().updateExperimentState(scope, IGui.NOTREADY);
				experiment.getExperimentScope().getGui().closeDialogs(scope);
				// Dec 2015 This method is normally now called from
				// ExperimentPlan.dispose()
				// experiment.dispose();
				// experiment = null;
			} finally {
				running = false;
				scheduler.dispose();
				experiment.getExperimentScope().getGui().updateExperimentState(scope, IGui.NONE);
				if (commandThread != null && commandThread.isAlive()) {
					commands.offer(-1);
				}
				// DEBUG.OUT("Contoller.dipose END");
			}
		}
	}

	@Override
	public void startPause() {
		if (experiment == null) {} else if (scheduler.paused) {
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
		// DEBUG.LOG("CloseExperiment : disposing = true");
		if (e != null) {
			GAMA.getGui().getStatus(getExperiment().getExperimentScope()).errorStatus(e.getMessage());
		}

		experiment.dispose(); // will call own dispose() later
	}

	@Override
	public ExperimentScheduler getScheduler() {
		return scheduler;
	}

}
