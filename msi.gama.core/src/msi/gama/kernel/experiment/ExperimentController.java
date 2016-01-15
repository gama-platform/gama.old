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
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class ExperimentController implements Runnable, IExperimentController {

	private final IExperimentPlan experiment;
	private boolean disposing;
	protected volatile ArrayBlockingQueue<Integer> commands;
	public volatile Thread commandThread;
	protected volatile boolean running = true;
	private final FrontEndScheduler scheduler;

	public ExperimentController(final IExperimentPlan experiment) {
		this.scheduler = new FrontEndScheduler();
		commands = new ArrayBlockingQueue(10);
		this.experiment = experiment;
	}

	private void launchCommandThread() {
		if ( commandThread != null ) { return; }
		if ( GuiUtils.isInHeadLessMode() ) {
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
				if ( i == null ) { throw new InterruptedException("Internal error. Please retry"); }
				processUserCommand(i);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		commandThread = null;
	}

	public void offer(final int command) {
		if ( isDisposing() ) { return; }
		if ( commandThread == null || !commandThread.isAlive() ) {
			processUserCommand(command);
		} else {
			commands.offer(command);
		}
	}

	protected void processUserCommand(final int command) {
		switch (command) {
			case IExperimentController._OPEN:

				GAMA.updateSimulationState(GAMA.NOTREADY);
				try {
					launchCommandThread();
					// Needs to run in the controller thread
					if ( commandThread == null ) {
						experiment.open();
					} else {
						new Thread(new Runnable() {

							@Override
							public void run() {
								experiment.open();
							}
						}).start();;
					}
				} catch (final Exception e) {
					// GuiUtils.debug("Error when opening the experiment: " + e.getMessage());
					closeExperiment(e);
				} finally {
					GAMA.updateSimulationState();
				}
				break;
			case IExperimentController._START:
				try {
					scheduler.on_user_hold = false;
					scheduler.start();
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} finally {
					GAMA.updateSimulationState(GAMA.RUNNING);
				}
				break;
			case IExperimentController._PAUSE:
				GAMA.updateSimulationState(GAMA.PAUSED);
				scheduler.pause();
				break;
			case IExperimentController._STEP:
				GAMA.updateSimulationState(GAMA.PAUSED);
				scheduler.stepByStep();
				break;
			case IExperimentController._RELOAD:
				GAMA.updateSimulationState(GAMA.NOTREADY);
				try {
					final boolean wasRunning = !scheduler.paused && !GamaPreferences.CORE_AUTO_RUN.getValue();
					scheduler.pause();
					GuiUtils.waitStatus("Reloading...");
					experiment.reload();
					if ( wasRunning ) {
						processUserCommand(IExperimentController._START);
					} else {
						GuiUtils.informStatus("Experiment reloaded");
					}
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} catch (final Exception e) {
					closeExperiment(GamaRuntimeException.create(e));
				} finally {
					GAMA.updateSimulationState();
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
		if ( experiment == null ) { return; }
		offer(IExperimentController._STEP);
	}

	// @Override
	// public void userInterrupt() {
	// if ( experiment != null ) {
	// IModel m = experiment.getModel();
	// GuiUtils.neutralStatus("No simulation running");
	// dispose(/* GamaRuntimeException.warning("Interrupted by user") */);
	//
	// if ( m != null ) {
	// m.dispose();
	// }
	// GuiUtils.wipeExperiments();
	// }
	// }

	@Override
	public void userReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		if ( experiment == null ) { return; }
		offer(IExperimentController._RELOAD);
	}

	@Override
	public void directOpenExperiment() {
		processUserCommand(IExperimentController._OPEN);
	}

	public void directReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		if ( experiment == null ) { return; }
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
		if ( experiment != null ) {
//			System.out.println("Contoller.dipose BEGIN");
			try {
				scheduler.pause();
				GAMA.updateSimulationState(GAMA.NOTREADY);
				GuiUtils.closeDialogs();
				// Dec 2015 This method is normally now called from ExperimentPlan.dispose()
				// experiment.dispose();
				// experiment = null;
			} finally {
				running = false;
				scheduler.wipe();
				scheduler.dispose();
				GAMA.updateSimulationState(GAMA.NONE);
//				System.out.println("Contoller.dipose END");
			}
		}
	}

	@Override
	public void startPause() {
		if ( experiment == null ) {
			return;
		} else if ( scheduler.paused ) {
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
//		System.out.println("CloseExperiment : disposing = true");
		if ( e != null ) {
			GuiUtils.errorStatus(e.getMessage());
		}

		experiment.dispose(); // will call own dispose() later
	}

	// public void newHeadlessExperiment(final IExperimentPlan newExperiment) {
	// if ( newExperiment == null ) {
	// System.out.println("No experiment available.");
	// return;
	// }
	// experiment = newExperiment;
	// try {
	// experiment.open();
	// } catch (final Exception e) {
	// System.out.println("Error when opening the experiment: " + e.getMessage());
	// }
	// }
	//
	// public void newExperiment(final IExperimentPlan newExperiment) {
	// if ( newExperiment == null ) {
	// System.out.println("No experiment available.");
	// return;
	// }
	//// if ( experiment != null ) {
	//// closeExperiment();
	//// }
	// experiment = newExperiment;
	// }

	// public void shutdown() {
	// scheduler.dispose();
	// running = false;
	// }

	@Override
	public FrontEndScheduler getScheduler() {
		return scheduler;
	}

}
