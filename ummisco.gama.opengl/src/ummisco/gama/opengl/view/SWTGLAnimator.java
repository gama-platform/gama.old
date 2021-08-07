/*********************************************************************************************
 *
 * 'SWTGLAnimator.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.view;

import java.util.Timer;
import java.util.TimerTask;

import com.jogamp.common.ExceptionUtils;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.AnimatorBase;

import msi.gama.common.preferences.GamaPreferences;

/**
 * Simple Animator (with target FPS). Includes a complete copy of FPSAnimator because many of the methods (incl.
 * display()) are marked final
 *
 * @author AD
 */
public class SWTGLAnimator extends AnimatorBase implements GLAnimatorControl.UncaughtExceptionHandler {

	private Timer timer = null;
	private MainTask task = null;
	private int fps;
	private final boolean scheduleAtFixedRate;
	private boolean isAnimating; // MainTask feedback
	private volatile boolean pauseIssued; // MainTask trigger
	private volatile boolean stopIssued; // MainTask trigger

	public SWTGLAnimator(final GLAutoDrawable drawable) {
		this.fps = GamaPreferences.Displays.OPENGL_FPS.getValue();
		if (drawable != null) { add(drawable); }
		this.scheduleAtFixedRate = false;
	}

	@Override
	protected String getBaseName(final String prefix) {
		return "FPS" + prefix + "Animator";
	}

	/**
	 * @param fps
	 * @throws GLException
	 *             if the animator has already been started
	 */
	public final void setFPS(final int fps) throws GLException {
		if (isStarted()) throw new GLException("Animator already started.");
		this.fps = fps;
	}

	public final int getFPS() {
		return fps;
	}

	class MainTask extends TimerTask {
		private boolean justStarted;
		private boolean alreadyStopped;
		private boolean alreadyPaused;

		public MainTask() {}

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

		public boolean isActive() {
			return !alreadyStopped && !alreadyPaused;
		}

		@Override
		public final String toString() {
			return "Task[thread " + animThread + ", stopped " + alreadyStopped + ", paused " + alreadyPaused
					+ " pauseIssued " + pauseIssued + ", stopIssued " + stopIssued + " -- started " + isStarted()
					+ ", animating " + isAnimatingImpl() + ", paused " + isPaused() + ", drawable " + drawables.size()
					+ ", drawablesEmpty " + drawablesEmpty + "]";
		}

		@Override
		public void run() {
			UncaughtAnimatorException caughtException = null;

			if (justStarted) {
				justStarted = false;
				synchronized (SWTGLAnimator.this) {
					animThread = Thread.currentThread();
					if (DEBUG) {
						System.err.println("FPSAnimator start/resume:" + Thread.currentThread() + ": " + toString());
					}
					isAnimating = true;
					if (drawablesEmpty) {
						pauseIssued = true; // isAnimating:=false @ pause below
					} else {
						pauseIssued = false;
						setDrawablesExclCtxState(exclusiveContext); // may re-enable exclusive context
					}
					SWTGLAnimator.this.notifyAll(); // Wakes up 'waitForStartedCondition' sync -and resume from pause or
													// drawablesEmpty
					if (DEBUG) { System.err.println("FPSAnimator P1:" + Thread.currentThread() + ": " + toString()); }
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
				if (DEBUG) {
					System.err.println("FPSAnimator pausing: " + alreadyPaused + ", " + Thread.currentThread() + ": "
							+ toString());
				}
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
						synchronized (SWTGLAnimator.this) {
							isAnimating = false;
							SWTGLAnimator.this.notifyAll();
						}
					}
				}
			}
			if (stopIssued) { // STOP incl. immediate exception handling of 'displayCaught'
				if (DEBUG) {
					System.err.println("FPSAnimator stopping: " + alreadyStopped + ", " + Thread.currentThread() + ": "
							+ toString());
				}
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
								System.err
										.println("FPSAnimator.setExclusiveContextThread: caught: " + dre.getMessage());
								dre.printStackTrace();
							}
						}
					}
					boolean flushGLRunnables = false;
					boolean throwCaughtException = false;
					synchronized (SWTGLAnimator.this) {
						if (DEBUG) {
							System.err.println("FPSAnimator stop " + Thread.currentThread() + ": " + toString());
							if (null != caughtException) {
								System.err.println("Animator caught: " + caughtException.getMessage());
								caughtException.printStackTrace();
							}
						}
						isAnimating = false;
						if (null != caughtException) {
							flushGLRunnables = true;
							throwCaughtException = !handleUncaughtException(caughtException);
						}
						animThread = null;
						SWTGLAnimator.this.notifyAll();
					}
					if (flushGLRunnables) { flushGLRunnables(); }
					if (throwCaughtException) throw caughtException;
				}
			}
		}
	}

	private final boolean isAnimatingImpl() {
		return animThread != null && isAnimating;
	}

	@Override
	public final synchronized boolean isAnimating() {
		return animThread != null && isAnimating;
	}

	@Override
	public final synchronized boolean isPaused() {
		return animThread != null && pauseIssued;
	}

	static int timerNo = 0;

	@Override
	public final synchronized boolean start() {
		if (null != timer || null != task || isStarted()) return false;
		timer = new Timer(getThreadName() + "-" + baseName + "-Timer" + timerNo++);
		task = new MainTask();
		if (DEBUG) {
			System.err
					.println("FPSAnimator.start() START: " + task + ", " + Thread.currentThread() + ": " + toString());
		}
		task.start(timer);

		final boolean res =
				finishLifecycleAction(drawablesEmpty ? waitForStartedEmptyCondition : waitForStartedAddedCondition,
						POLLP_WAIT_FOR_FINISH_LIFECYCLE_ACTION);
		if (DEBUG) {
			System.err.println("FPSAnimator.start() END: " + task + ", " + Thread.currentThread() + ": " + toString());
		}
		if (drawablesEmpty) {
			task.cancel();
			task = null;
		}
		return res;
	}

	private final Condition waitForStartedAddedCondition = () -> !isStarted() || !isAnimating;
	private final Condition waitForStartedEmptyCondition = () -> !isStarted() || isAnimating;

	/**
	 * Stops this FPSAnimator. Due to the implementation of the FPSAnimator it is not guaranteed that the FPSAnimator
	 * will be completely stopped by the time this method returns.
	 */
	@Override
	public final synchronized boolean stop() {
		if (null == timer || !isStarted()) return false;
		if (DEBUG) {
			System.err.println("FPSAnimator.stop() START: " + task + ", " + Thread.currentThread() + ": " + toString());
		}
		final boolean res;
		if (null == task) {
			// start/resume case w/ drawablesEmpty
			res = true;
		} else {
			stopIssued = true;
			res = finishLifecycleAction(waitForStoppedCondition, POLLP_WAIT_FOR_FINISH_LIFECYCLE_ACTION);
		}

		if (DEBUG) {
			System.err.println("FPSAnimator.stop() END: " + task + ", " + Thread.currentThread() + ": " + toString());
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

	private final Condition waitForStoppedCondition = this::isStarted;

	@Override
	public final synchronized boolean pause() {
		if (!isStarted() || pauseIssued) return false;
		if (DEBUG) {
			System.err
					.println("FPSAnimator.pause() START: " + task + ", " + Thread.currentThread() + ": " + toString());
		}
		final boolean res;
		if (null == task) {
			// start/resume case w/ drawablesEmpty
			res = true;
		} else {
			pauseIssued = true;
			res = finishLifecycleAction(waitForPausedCondition, POLLP_WAIT_FOR_FINISH_LIFECYCLE_ACTION);
		}

		if (DEBUG) {
			System.err.println("FPSAnimator.pause() END: " + task + ", " + Thread.currentThread() + ": " + toString());
		}
		if (null != task) {
			task.cancel();
			task = null;
		}
		return res;
	}

	private final Condition waitForPausedCondition = () -> isStarted() && isAnimating;

	@Override
	public final synchronized boolean resume() {
		if (!isStarted() || !pauseIssued) return false;
		if (DEBUG) { System.err.println("FPSAnimator.resume() START: " + Thread.currentThread() + ": " + toString()); }
		final boolean res;
		if (drawablesEmpty) {
			res = true;
		} else {
			if (null != task) {
				if (DEBUG) {
					System.err.println("FPSAnimator.resume() Ops: !pauseIssued, but task != null: " + toString());
					ExceptionUtils.dumpStack(System.err);
				}
				task.cancel();
				task = null;
			}
			task = new MainTask();
			task.start(timer);
			res = finishLifecycleAction(waitForResumeCondition, POLLP_WAIT_FOR_FINISH_LIFECYCLE_ACTION);
		}
		if (DEBUG) {
			System.err.println("FPSAnimator.resume() END: " + task + ", " + Thread.currentThread() + ": " + toString());
		}
		return res;
	}

	private final Condition waitForResumeCondition = () -> !drawablesEmpty && !isAnimating && isStarted();

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

	/**
	 * Method uncaughtException()
	 *
	 * @see com.jogamp.opengl.GLAnimatorControl.UncaughtExceptionHandler#uncaughtException(com.jogamp.opengl.GLAnimatorControl,
	 *      com.jogamp.opengl.GLAutoDrawable, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final GLAnimatorControl animator, final GLAutoDrawable drawable,
			final Throwable cause) {

		// DEBUG.ERR("Uncaught exception in animator & drawable:");
		cause.printStackTrace();

	}
}