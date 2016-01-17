/*********************************************************************************************
 *
 *
 * 'FrontEndScheduler.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.runtime;

import java.util.*;
import java.util.concurrent.Semaphore;
import gnu.trove.set.hash.THashSet;
import msi.gama.common.interfaces.IStepable;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.TOrderedHashMap;

public class FrontEndScheduler implements Runnable {

	public volatile boolean alive = true;
	// Flag indicating that the simulation is set to pause (it should be alive unless the application is shutting down)
	public volatile boolean paused = true;
	// Flag indicating that the thread is set to be on hold, waiting for a user input
	public volatile boolean on_user_hold = false;
	/* The stepables that need to be stepped */
	private final Map<IStepable, IScope> toStep = new TOrderedHashMap();
	private volatile Set<IStepable> toStop = new THashSet();
	private Thread executionThread;
	volatile Semaphore lock = new Semaphore(1);
	final IExperimentPlan experiment;

	public FrontEndScheduler(final IExperimentPlan experiment) {
		this.experiment = experiment;
		if ( !experiment.isHeadless() ) {
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
				e.printStackTrace();
				GamaRuntimeException ee = GamaRuntimeException.create(e, experiment.getExperimentScope());
				ee.addContext("Error in front end scheduler. Reloading thread, but it would be safer to reload GAMA");
				experiment.getExperimentScope().getGui().raise(ee);
				executionThread = new Thread(null, this, "Front end scheduler");
				executionThread.start();
			}
		}
	}

	private IStepable[] stepables = null;
	private IScope[] scopes = null;

	public void step() {
		if ( !experiment.isHeadless() && paused ) {
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				alive = false;
				return;
			}
		}

		// scope.getGui().debug("FrontEndScheduler.step");
		stepables = toStep.keySet().toArray(new IStepable[toStep.size()]);
		scopes = toStep.values().toArray(new IScope[toStep.size()]);
		for ( int i = 0; i < stepables.length; i++ ) {
			final IScope scope = scopes[i];
			try {
				// scope.getGui().debug("FrontEndScheduler.step : stepping " + stepables[i]);
				if ( !scope.step(stepables[i]) ) {
					// scope.getGui().debug("FrontEndScheduler.step : removal of " + stepables[i]);
					toStop.add(stepables[i]);
				}
			} catch (final Exception e) {
				e.printStackTrace();
				// if ( scope.interrupted() ) {
				//
				// // scope.getGui().debug("Exception in experiment interruption: " + e.getMessage());
				// } else {
				// // GAMA.reportError(e, true);
				// }
			}
		}
	}

	private void clean() {
		if ( toStop.isEmpty() ) { return; }
		synchronized (toStop) {
			for ( final IStepable s : toStop ) {
				final IScope scope = toStep.get(s);
				if ( scope != null && !scope.interrupted() ) {
					// scope.getGui().debug("FrontEndScheduler.clean : Interrupting " + scope);
					scope.setInterrupted(true);
				}
				toStep.remove(s);
				// scope.getGui().debug("FrontEndScheduler.clean : Removed " + s);
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
		// scope.getGui().debug("FrontEndScheduler.schedule " + stepable);
		try {
			if ( !scope.init(stepable) ) {
				toStop.add(stepable);
			}
			// else {
			// paused = false;
			// }
		} catch (final Exception e) {
			// scope.getGui().debug("WARNING :: Exception in front end scheduler: " + e.getMessage());
			e.printStackTrace();
			if ( scope != null && scope.interrupted() ) {
				toStop.add(stepable);
			} else {
				GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
			}
		}

	}

	public synchronized void wipe() {
		synchronized (toStop) {
			toStop.clear();
		}
		synchronized (toStep) {
			toStep.clear();
		}
	}

	public void dispose() {
		alive = false;
		wipe();
	}

	public void removeStepable(final String s) {

		Set<IStepable> beRemoved = new THashSet();
		for ( IStepable ss : toStep.keySet() ) {
			if ( ss.toString().contains(s) ) {

				final IScope scope = toStep.get(ss);
				if ( !scope.interrupted() ) {
					scope.setInterrupted(true);
				}
				beRemoved.add(ss);
			}

		}
		for ( IStepable ss : beRemoved ) {
			toStep.remove(ss);
			toStop.remove(ss);
		}
		// System.out.println(toStep.keySet());
		// System.out.println(s.toString());
	}

	public void unschedule(final IStepable scheduler) {
		if ( toStep.containsKey(scheduler) ) {
			toStep.get(scheduler).setInterrupted(true);
			// toStop.add(scheduler);
		}
	}

}
