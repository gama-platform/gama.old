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
	private volatile Set<IStepable> toStop = new HashSet();
	private Thread executionThread;
	volatile Semaphore lock = new Semaphore(1);

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
		if ( !GuiUtils.isInHeadLessMode()&& paused ) {
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				alive = false;
				return;
			}
		}

		// GuiUtils.debug("FrontEndScheduler.step");
		stepables = toStep.keySet().toArray(new IStepable[toStep.size()]);
		scopes = toStep.values().toArray(new IScope[toStep.size()]);
		for ( int i = 0; i < stepables.length; i++ ) {
			final IScope scope = scopes[i];
			try {
				// GuiUtils.debug("FrontEndScheduler.step : stepping " + stepables[i]);
				if ( !scope.step(stepables[i]) ) {
					// GuiUtils.debug("FrontEndScheduler.step : removal of " + stepables[i]);
					toStop.add(stepables[i]);
				}
			} catch (final Exception e) {
				e.printStackTrace();
				if ( scope.interrupted() ) {
					
					GuiUtils.debug("Exception in experiment interruption: " + e.getMessage());
				} else {
					//GAMA.reportError(e, true);
				}
			}
		}
	}

	private void clean() {
		if ( toStop.isEmpty() ) { return; }
		synchronized (toStop) {
			for ( final IStepable s : toStop ) {
				final IScope scope = toStep.get(s);
				if ( !scope.interrupted() ) {
					// GuiUtils.debug("FrontEndScheduler.clean : Interrupting " + scope);
					scope.setInterrupted(true);
				}
				toStep.remove(s);
				// GuiUtils.debug("FrontEndScheduler.clean : Removed " + s);
				// s.dispose();
			}
			if ( toStep.isEmpty() ) {
				this.pause();
			}
			toStop.clear();
		}
	}

	@Override
	public void run() {
		while (alive) {
			step();
			clean();
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
		if ( toStep.containsKey(stepable) ) {
			toStep.remove(stepable);
		}
		toStep.put(stepable, scope);
		// We first init the stepable before it is scheduled
		// GuiUtils.debug("FrontEndScheduler.schedule " + stepable);
		try {
			if ( !scope.init(stepable) ) {
				toStop.add(stepable);
			}
			// else {
			// paused = false;
			// }
		} catch (final Exception e) {
			// GuiUtils.debug("WARNING :: Exception in front end scheduler: " + e.getMessage());
			e.printStackTrace();
			if ( scope != null && scope.interrupted() ) {
				toStop.add(stepable);
			} else {
				GAMA.reportError(GamaRuntimeException.create(e), true);
			}
		}

	}

	public void dispose() {
		alive = false;
		toStop.clear();
		toStep.clear();
	}

	public void unschedule(final IStepable scheduler) {
		if ( toStep.containsKey(scheduler) ) {
			toStep.get(scheduler).setInterrupted(true);
			// toStop.add(scheduler);
		}
	}

}
