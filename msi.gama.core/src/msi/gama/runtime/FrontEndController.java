/*********************************************************************************************
 * 
 * 
 * 'FrontEndController.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.runtime;

import java.util.concurrent.ArrayBlockingQueue;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class FrontEndController implements Runnable {

	public final static String PAUSED = "STOPPED";
	public final static String RUNNING = "RUNNING";
	public final static String NOTREADY = "NOTREADY";
	public final static String NONE = "NONE";
	public static final int _OPEN = 0;
	public static final int _START = 1;
	public static final int _STEP = 2;
	public static final int _PAUSE = 3;
	public static final int _STOP = 4;
	public static final int _CLOSE = 5;
	public static final int _RELOAD = 6;
	public static final int _NEXT = 7;

	public ISimulationStateProvider state = null;
	private volatile IExperimentPlan experiment = null;
	protected volatile ArrayBlockingQueue<Integer> commands;
	public volatile Thread commandThread;
	protected volatile boolean running = true;
	private final FrontEndScheduler scheduler;

	public FrontEndController(final FrontEndScheduler scheduler) {
		this.scheduler = scheduler;
		commands = new ArrayBlockingQueue(10);
		if ( GuiUtils.isInHeadLessMode() ) {
			commandThread = null;
		} else {
			commandThread = new Thread(this, "Front end controller");
			commandThread.start();
		}
	}

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
	}

	public void offer(final int command) {
		if ( commandThread == null || !commandThread.isAlive() ) {
			processUserCommand(command);
		} else {
			commands.offer(command);
		}
	}

	protected void processUserCommand(final int command) {
		switch (command) {
			case _OPEN:
				// Needs to run in the controller thread
				updateSimulationState(NOTREADY);
				try {
					experiment.open();
				} catch (final Exception e) {
					// GuiUtils.debug("Error when opening the experiment: " + e.getMessage());
					closeExperiment(e);
				} finally {
					updateSimulationState();
				}
				break;
			case _START:
				try {
					scheduler.on_user_hold = false;
					scheduler.start();
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} finally {
					updateSimulationState(RUNNING);
				}
				break;
			case _PAUSE:
				updateSimulationState(PAUSED);
				scheduler.pause();
				break;
			case _STEP:
				updateSimulationState(PAUSED);
				scheduler.stepByStep();
				break;
			case _RELOAD:
				updateSimulationState(NOTREADY);
				try {
					final boolean wasRunning = !scheduler.paused && !GamaPreferences.CORE_AUTO_RUN.getValue();
					scheduler.pause();
					GuiUtils.waitStatus("Reloading...");
					experiment.reload();
					if ( wasRunning ) {
						processUserCommand(_START);
					} else {
						GuiUtils.informStatus("Experiment reloaded");
					}
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} catch (final Exception e) {
					closeExperiment(GamaRuntimeException.create(e));
				} finally {
					updateSimulationState();
				}
				break;
		}
	}

	public void userPause() {
		// TODO Should maybe be done directly (so as to pause immediately)
		offer(_PAUSE);
	}

	public void directPause() {
		processUserCommand(_PAUSE);
	}

	public void userStep() {
		if ( experiment == null ) { return; }
		offer(_STEP);
	}

	public void userInterrupt() {
		if ( experiment != null ) {
			IModel m = experiment.getModel();
			GuiUtils.neutralStatus("No simulation running");
			closeExperiment(/* GamaRuntimeException.warning("Interrupted by user") */);

			if ( m != null ) {
				m.dispose();
			}
			GuiUtils.openModelingPerspective();
		}
	}

	public void userReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		if ( experiment == null ) { return; }
		offer(_RELOAD);
	}

	public void directOpenExperiment() {
		processUserCommand(_OPEN);
	}

	public void directReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		if ( experiment == null ) { return; }
		processUserCommand(_RELOAD);
	}

	public void userStart() {
		offer(_START);
	}

	public void closeExperiment() {
		if ( experiment != null ) {
			try {
				scheduler.pause();
				updateSimulationState(NOTREADY);
				GuiUtils.closeDialogs();
				experiment.dispose();
				experiment = null;
			} finally {
				scheduler.wipe();
				updateSimulationState(NONE);
			}
		}
	}

	public void startPause() {
		if ( experiment == null ) {
			return;
		} else if ( scheduler.paused ) {
			userStart();
		} else {
			userPause();
		}
	}

	public void closeExperiment(final Exception e) {
		GuiUtils.errorStatus(e.getMessage());
		closeExperiment();
	}

	/**
	 * 
	 * Simulation state related utilities for Eclipse GUI
	 * 
	 */

	public String getFrontmostSimulationState() {
		if ( experiment == null ) {
			return NONE;
		} else if ( scheduler.paused ) { return PAUSED; }
		return RUNNING;
	}

	public void updateSimulationState(final String forcedState) {
		if ( state != null ) {
			GuiUtils.run(new Runnable() {

				@Override
				public void run() {
					state.updateStateTo(forcedState);
				}
			});
		}
	}

	public void updateSimulationState() {
		updateSimulationState(getFrontmostSimulationState());
	}

	public void newHeadlessExperiment(final IExperimentPlan newExperiment) {
		if ( newExperiment == null ) {
			System.out.println("No experiment available.");
			return;
		}
		experiment = newExperiment;
		try {
			experiment.open();
		} catch (final Exception e) {
			System.out.println("Error when opening the experiment: " + e.getMessage());
		}
	}

	public void newExperiment(final IExperimentPlan newExperiment) {
		if ( newExperiment == null ) {
			System.out.println("No experiment available.");
			return;
		}
		if ( experiment != null ) {		
			closeExperiment();
		}
		experiment = newExperiment;
	}
	
	public void newExperiment(final String id, final IModel model) {
		if (GAMA.getControllers().size() > 0) {
			for (FrontEndController s : GAMA.getControllers().values()) {
				s.closeExperiment();
				s.shutdown();
			}
			GAMA.getControllers().clear();
		}
		final IExperimentPlan newExperiment = model.getExperiment(id);
		if ( newExperiment == null ) { return; }
		GuiUtils.openSimulationPerspective();
		// FIXME Useless
		if ( newExperiment == experiment && experiment != null ) {
			userReload();
			return;
		}
		if ( experiment != null ) {
			final IModel m = experiment.getModel();
			if ( !m.getFilePath().equals(model.getFilePath()) ) {
				if ( !verifyClose() ) { return; }
				closeExperiment();
				m.dispose();
			} else if ( !id.equals(experiment.getName()) ) {
				if ( !verifyClose() ) { return; }
				closeExperiment();
			} else {
				if ( !verifyClose() ) { return; }
				closeExperiment();
			}
		}
		experiment = newExperiment;
		// experiment.open();
		offer(_OPEN);
	}

	private boolean verifyClose() {
		if ( experiment == null ) { return true; }
		// TODO boolean wasRunning = !scheduler.paused;
		scheduler.pause();
		return GuiUtils.confirmClose(experiment);
	}

	public void shutdown() {
		scheduler.dispose();
		running = false;
	}

	public FrontEndScheduler getScheduler() {
		return scheduler;
	}

}
