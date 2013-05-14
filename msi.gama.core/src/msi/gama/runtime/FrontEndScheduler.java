package msi.gama.runtime;

import java.util.*;
import java.util.concurrent.Semaphore;
import msi.gama.common.interfaces.IStepable;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class FrontEndScheduler implements Runnable {

	public volatile boolean alive = true;
	// Flag indicating that the simulation is set to pause (it should be alive unless the application is shutting down)
	public volatile boolean paused = true;
	// Flag indicating that the thread is set to be on hold, waiting for a user input
	public volatile boolean on_user_hold = false;
	/* The stepables that need to be stepped */
	private final Map<IStepable, IScope> toStep = new LinkedHashMap();
	private Thread executionThread;
	private volatile Semaphore lock = new Semaphore(1);

	public FrontEndScheduler() {
		if ( !GuiUtils.isInHeadLessMode() ) {
			executionThread = new Thread(null, this, "Front end scheduler");
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			startThread();
		} else {
			executionThread = null;
		}
	}

	private void startThread() {
		if ( executionThread == null ) {
			step();
		} else if ( !executionThread.isAlive() ) {
			try {
				executionThread.start();
			} catch (final Exception e) {
				GuiUtils.error("FrontEndScheduler.startThread : reloading thread due to an internal error");
				executionThread = new Thread(null, this, "Front end scheduler");
				executionThread.start();
			}
		}
	}

	private IStepable[] stepables = null;
	private IScope[] scopes = null;

	public void step() {
		if ( paused /* || toStep.isEmpty() */) {
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				alive = false;
				return;
			}
		}

		synchronized (toStep) {
			stepables = toStep.keySet().toArray(new IStepable[toStep.size()]);
			scopes = toStep.values().toArray(new IScope[toStep.size()]);
		}
		for ( int i = 0; i < stepables.length; i++ ) {
			final IScope scope = scopes[i];
			try {
				if ( scope.interrupted() ) {
					// GuiUtils.debug("FrontEndScheduler.step : removal of " + entry.getKey());
					toStep.remove(stepables[i]);
					if ( toStep.isEmpty() ) {
						pause();
					}
				} else {
					// GuiUtils.debug("FrontEndScheduler.step : step of " + entry.getKey());
					stepables[i].step(scope);
				}
			} catch (final GamaRuntimeException e) {
				if ( scope.interrupted() ) {
					GuiUtils.debug("Exception in experiment interruption: " + e.getMessage());
				} else {
					GAMA.reportError(e);
				}
			}
		}

		// for ( Iterator<Map.Entry<IStepable, IScope>> it = toStep.entrySet().iterator(); it.hasNext(); ) {
		// Map.Entry<IStepable, IScope> entry = it.next();
		// IScope scope = entry.getValue();
		// try {
		// if ( scope.interrupted() ) {
		// // GuiUtils.debug("FrontEndScheduler.step : removal of " + entry.getKey());
		// it.remove();
		// if ( toStep.isEmpty() ) {
		// pause();
		// }
		// } else {
		// // GuiUtils.debug("FrontEndScheduler.step : step of " + entry.getKey());
		// entry.getKey().step(scope);
		// }
		// } catch (GamaRuntimeException e) {
		// if ( scope.interrupted() ) {
		// GuiUtils.debug("Exception in experiment interruption: " + e.getMessage());
		// } else {
		// GAMA.reportError(e);
		// }
		// }
		// }
	}

	@Override
	public void run() {
		while (alive) {
			step();
		}
	}

	public void setUserHold(final boolean hold) {
		on_user_hold = hold;
	}

	public void stepByStep() {
		paused = true;
		lock.release();
		startThread();
	}

	public void start() {
		paused = false;
		lock.release();
		startThread();
	}

	public void pause() {
		paused = true;
	}

	public void schedule(final IStepable stepable, final IScope scope) {
		synchronized (toStep) {
			toStep.put(stepable, scope);
		}
		// We first init the stepable before it is scheduled
		// GuiUtils.debug("FrontEndScheduler.schedule " + stepable);
		try {
			stepable.init(scope);
		} catch (final Exception e) {
			GuiUtils.debug("WARNING :: Exception in front end scheduler: " + e.getMessage());
			if ( scope != null && scope.interrupted() ) {
				synchronized (toStep) {
					// GuiUtils.debug("FrontEndScheduler.schedule : Removal of " + stepable);
					toStep.remove(stepable);
				}
			} else {
				throw GamaRuntimeException.create(e);
			}
		}

	}

	public void dispose() {
		alive = false;
		toStep.clear();
	}

	public void unschedule(final IStepable scheduler) {
		synchronized (toStep) {
			toStep.remove(scheduler);
		}
	}

}
