/*******************************************************************************************************
 *
 * MultithreadGLAnimator.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import java.util.Timer;
import java.util.TimerTask;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.AnimatorBase;

import msi.gama.common.preferences.GamaPreferences;

/**
 * Simple Animator (with target FPS). Includes a complete copy of FPSAnimator because many of the methods (incl.
 * display()) are marked final
 *
 * @author AD
 */
public class MultithreadGLAnimator extends AnimatorBase implements GLAnimatorControl.UncaughtExceptionHandler {

	/** The timer. */
	private Timer timer = null;
	
	/** The task. */
	private MainTask task = null;
	
	/** The fps. */
	private final int fps;
	
	/** The schedule at fixed rate. */
	private final boolean scheduleAtFixedRate;
	
	/** The is animating. */
	private boolean isAnimating; // MainTask feedback
	
	/** The pause issued. */
	private volatile boolean pauseIssued; // MainTask trigger
	
	/** The stop issued. */
	private volatile boolean stopIssued; // MainTask trigger
	
	/** The wait for stopped condition. */
	private final Condition waitForStoppedCondition = this::isStarted;
	
	/** The wait for resume condition. */
	private final Condition waitForResumeCondition = () -> !drawablesEmpty && !isAnimating && isStarted();
	
	/** The wait for started added condition. */
	private final Condition waitForStartedAddedCondition = () -> !isStarted() || !isAnimating;
	
	/** The wait for started empty condition. */
	private final Condition waitForStartedEmptyCondition = () -> !isStarted() || isAnimating;
	
	/** The wait for paused condition. */
	private final Condition waitForPausedCondition = () -> isStarted() && isAnimating;

	/**
	 * Instantiates a new multithread GL animator.
	 *
	 * @param drawable the drawable
	 */
	public MultithreadGLAnimator(final GLAutoDrawable drawable) {
		this.fps = GamaPreferences.Displays.OPENGL_CAP_FPS.getValue() ? GamaPreferences.Displays.OPENGL_FPS.getValue()
				: 1000;
		if (drawable != null) { add(drawable); }
		this.scheduleAtFixedRate = false;
	}

	@Override
	protected String getBaseName(final String prefix) {
		return "FPS" + prefix + "Animator";
	}

	/**
	 * The Class MainTask.
	 */
	class MainTask extends TimerTask {
		
		/** The just started. */
		private boolean justStarted;
		
		/** The already stopped. */
		private boolean alreadyStopped;
		
		/** The already paused. */
		private boolean alreadyPaused;

		/**
		 * Instantiates a new main task.
		 */
		public MainTask() {}

		/**
		 * Start.
		 *
		 * @param timer the timer
		 */
		public void start(final Timer timer) {
			fpsCounter.resetFPSCounter();
			pauseIssued = false;
			stopIssued = false;
			isAnimating = false;

			justStarted = true;
			alreadyStopped = false;
			alreadyPaused = false;

			final long period = 0 < fps ? (long) (1000.0f / fps) : 1; // 0 -> 1: IllegalArgumentException: Non-positive
																		// period
			if (scheduleAtFixedRate) {
				timer.scheduleAtFixedRate(this, 0, period);
			} else {
				timer.schedule(this, 0, period);
			}
		}

		/**
		 * Checks if is active.
		 *
		 * @return true, if is active
		 */
		public boolean isActive() {
			return !alreadyStopped && !alreadyPaused;
		}

		@Override
		public void run() {
			UncaughtAnimatorException caughtException = null;

			if (justStarted) {
				justStarted = false;
				synchronized (MultithreadGLAnimator.this) {
					animThread = Thread.currentThread();
					isAnimating = true;
					if (drawablesEmpty) {
						pauseIssued = true; // isAnimating:=false @ pause below
					} else {
						pauseIssued = false;
						setDrawablesExclCtxState(exclusiveContext); // may re-enable exclusive context
					}
					MultithreadGLAnimator.this.notifyAll();
				}
			}
			if (!pauseIssued && !stopIssued) { // RUN
				try {
					display();
				} catch (final UncaughtAnimatorException dre) {
					caughtException = dre;
					stopIssued = true;
				}
			} else if (pauseIssued && !stopIssued) { // PAUSE
				this.cancel();
				if (!alreadyPaused) { // PAUSE
					alreadyPaused = true;
					if (exclusiveContext && !drawablesEmpty) {
						setDrawablesExclCtxState(false);
						try {
							displayGL(); // propagate exclusive context -> off!
						} catch (final UncaughtAnimatorException dre) {
							caughtException = dre;
							stopIssued = true;
						}
					}
					if (null == caughtException) {
						synchronized (MultithreadGLAnimator.this) {
							isAnimating = false;
							MultithreadGLAnimator.this.notifyAll();
						}
					}
				}
			}
			if (stopIssued) { // STOP incl. immediate exception handling of 'displayCaught'
				this.cancel();

				if (!alreadyStopped) {
					alreadyStopped = true;
					if (exclusiveContext && !drawablesEmpty) {
						setDrawablesExclCtxState(false);
						try {
							display(); // propagate exclusive context -> off!
						} catch (final UncaughtAnimatorException dre) {
							if (null == caughtException) {
								caughtException = dre;
							} else {
								dre.printStackTrace();
							}
						}
					}
					boolean flushGLRunnables = false;
					boolean throwCaughtException = false;
					synchronized (MultithreadGLAnimator.this) {
						isAnimating = false;
						if (null != caughtException) {
							flushGLRunnables = true;
							throwCaughtException = !handleUncaughtException(caughtException);
						}
						animThread = null;
						MultithreadGLAnimator.this.notifyAll();
					}
					if (flushGLRunnables) { flushGLRunnables(); }
					if (throwCaughtException) throw caughtException;
				}
			}
		}
	}

	@Override
	public final synchronized boolean isAnimating() {
		return animThread != null && isAnimating;
	}

	@Override
	public final synchronized boolean isPaused() {
		return animThread != null && pauseIssued;
	}

	/** The timer no. */
	static int timerNo = 0;

	@Override
	public final synchronized boolean start() {
		if (null != timer || null != task || isStarted()) return false;
		timer = new Timer(getThreadName() + "-" + baseName + "-Timer" + timerNo++);
		task = new MainTask();
		task.start(timer);

		final boolean res =
				finishLifecycleAction(drawablesEmpty ? waitForStartedEmptyCondition : waitForStartedAddedCondition,
						POLLP_WAIT_FOR_FINISH_LIFECYCLE_ACTION);
		if (drawablesEmpty) {
			task.cancel();
			task = null;
		}
		return res;
	}

	/**
	 * Stops this FPSAnimator. Due to the implementation of the FPSAnimator it is not guaranteed that the FPSAnimator
	 * will be completely stopped by the time this method returns.
	 */
	@Override
	public final synchronized boolean stop() {
		if (null == timer || !isStarted()) return false;
		final boolean res;
		if (null == task) {
			// start/resume case w/ drawablesEmpty
			res = true;
		} else {
			stopIssued = true;
			res = finishLifecycleAction(waitForStoppedCondition, POLLP_WAIT_FOR_FINISH_LIFECYCLE_ACTION);
		}

		if (null != task) {
			task.cancel();
			task = null;
		}
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
		animThread = null;
		return res;
	}

	@Override
	public final synchronized boolean pause() {
		if (!isStarted() || pauseIssued) return false;
		final boolean res;
		if (null == task) {
			// start/resume case w/ drawablesEmpty
			res = true;
		} else {
			pauseIssued = true;
			res = finishLifecycleAction(waitForPausedCondition, POLLP_WAIT_FOR_FINISH_LIFECYCLE_ACTION);
		}

		if (null != task) {
			task.cancel();
			task = null;
		}
		return res;
	}

	@Override
	public final synchronized boolean resume() {
		if (!isStarted() || !pauseIssued) return false;
		final boolean res;
		if (drawablesEmpty) {
			res = true;
		} else {
			if (null != task) {
				task.cancel();
				task = null;
			}
			task = new MainTask();
			task.start(timer);
			res = finishLifecycleAction(waitForResumeCondition, POLLP_WAIT_FOR_FINISH_LIFECYCLE_ACTION);
		}
		return res;
	}

	/**
	 * Display GL.
	 */
	protected void displayGL() {
		this.isAnimating = true;
		try {
			if (drawables.get(0).isRealized()) { super.display(); }
		} catch (final RuntimeException ex) {
			// DEBUG.ERR("Exception in OpenGL:" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			this.isAnimating = false;
		}
	}

	@Override
	public void uncaughtException(final GLAnimatorControl animator, final GLAutoDrawable drawable,
			final Throwable cause) {
		// DEBUG.ERR("Uncaught exception in animator & drawable:");
		cause.printStackTrace();

	}
}