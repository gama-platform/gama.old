/*******************************************************************************************************
 *
 * GamaGLAnimator.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import com.jogamp.opengl.FPSCounter;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;

import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.THREADS;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Single Thread Animator (with target FPS)
 *
 * @author Alexis Drogoul, loosely adapted from (aqd@5star.com.tw)
 */
public class GamaGLAnimator implements Runnable, GLAnimatorControl, GLAnimatorControl.UncaughtExceptionHandler {

	/** The cap FPS. */
	protected volatile boolean capFPS = GamaPreferences.Displays.OPENGL_CAP_FPS.getValue();

	/** The target FPS. */
	protected volatile int targetFPS = GamaPreferences.Displays.OPENGL_FPS.getValue();

	/** The animator thread. */
	protected final Thread animatorThread;

	/** The drawable. */
	private final GLAutoDrawable drawable;

	/** The stop requested. */
	protected volatile boolean stopRequested = false;

	/** The fps update frames interval. */
	private int fpsUpdateFramesInterval = 50;
	/** The fps total duration. */
	private long fpsStartTime, fpsLastUpdateTime, fpsLastPeriod, fpsTotalDuration;

	/** The fps total frames. */
	private int fpsTotalFrames;
	/** The fps total. */
	private float fpsLast, fpsTotal;

	@Override
	public void resetFPSCounter() {
		long fpsStartTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime()); // overwrite startTime to real init one
		fpsLastUpdateTime = fpsStartTime;
		fpsLastPeriod = 0;
		fpsTotalFrames = 0;
		fpsLast = 0f;
		fpsTotal = 0f;
		fpsLastPeriod = 0;
		fpsTotalDuration = 0;
	}

	@Override
	public int getUpdateFPSFrames() { return fpsUpdateFramesInterval; }

	@Override
	public long getFPSStartTime() { return fpsStartTime; }

	@Override
	public long getLastFPSUpdateTime() { return fpsLastUpdateTime; }

	@Override
	public long getLastFPSPeriod() { return fpsLastPeriod; }

	@Override
	public float getLastFPS() { return fpsLast; }

	@Override
	public int getTotalFPSFrames() { return fpsTotalFrames; }

	@Override
	public long getTotalFPSDuration() { return fpsTotalDuration; }

	@Override
	public float getTotalFPS() { return fpsTotal; }

	@Override
	public void setUpdateFPSFrames(final int frames, final PrintStream out) {
		fpsUpdateFramesInterval = frames;
	}

	/**
	 * Instantiates a new single thread GL animator.
	 *
	 * @param window
	 *            the drawable
	 */
	public GamaGLAnimator(final GLAutoDrawable window) {
		this.drawable = window;
		window.setAnimator(this);
		this.animatorThread = new Thread(this, "Animator thread");
		GamaPreferences.Displays.OPENGL_FPS.onChange(newValue -> targetFPS = newValue);
		setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, null);
	}

	@Override
	public boolean isStarted() { return animatorThread.isAlive(); }

	@Override
	public Thread getThread() { return animatorThread; }

	@Override
	public boolean start() {
		this.stopRequested = false;
		this.animatorThread.start();
		fpsStartTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public boolean stop() {
		this.stopRequested = true;
		if (WorkbenchHelper.isDisplayThread()) return true;
		try {
			this.animatorThread.join();
		} catch (final InterruptedException e) {} finally {
			this.stopRequested = false;
		}
		return true;
	}

	@Override
	public boolean isAnimating() { return true; }

	@Override
	public boolean isPaused() { return false; }

	@Override
	public boolean pause() {
		return false;
	}

	@Override
	public boolean resume() {
		return true;
	}

	@Override
	public void add(final GLAutoDrawable drawable) {}

	@Override
	public void remove(final GLAutoDrawable drawable) {}

	@Override
	public void run() {
		// while (!window.isRealized()) {}
		while (!stopRequested) {
			try {
				// if (isARM() || PlatformHelper.isLinux()) {
				WorkbenchHelper.run(() -> { if (drawable.isRealized()) { drawable.display(); } });
				// } else if (drawable.isRealized()) { drawable.display(); }
				if (capFPS) {
					final long frameDuration = 1000 / targetFPS;
					final long timeSleep = frameDuration - fpsLastPeriod;
					if (timeSleep >= 0) { THREADS.WAIT(timeSleep); }
				}
			} catch (final RuntimeException ex) {
				uncaughtException(this, drawable, ex);
			}
			tickFPS();
		}
	}

	@Override
	public UncaughtExceptionHandler getUncaughtExceptionHandler() { return this; }

	@Override
	public void setUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {}

	@Override
	public void uncaughtException(final GLAnimatorControl animator, final GLAutoDrawable drawable,
			final Throwable cause) {
		DEBUG.ERR("Uncaught exception in animator & drawable:" + cause.getMessage());
		cause.printStackTrace();

	}

	/**
	 * Increases total frame count and updates values if feature is enabled and update interval is reached.<br>
	 *
	 * Shall be called by actual FPSCounter implementing renderer, after display a new frame.
	 *
	 */
	public final void tickFPS() {
		fpsTotalFrames++;
		if (fpsUpdateFramesInterval > 0 && fpsTotalFrames % fpsUpdateFramesInterval == 0) {
			final long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
			fpsLastPeriod = now - fpsLastUpdateTime;
			fpsLastPeriod = Math.max(fpsLastPeriod, 1); // div 0
			fpsLast = fpsUpdateFramesInterval * 1000f / fpsLastPeriod;
			fpsTotalDuration = now - fpsStartTime;
			fpsTotalDuration = Math.max(fpsTotalDuration, 1); // div 0
			fpsTotal = fpsTotalFrames * 1000f / fpsTotalDuration;
			fpsLastUpdateTime = now;
			if (DEBUG.IS_ON()) {
				// StringBuilder sb = new StringBuilder();
				String fpsLastS = String.valueOf(fpsLast);
				fpsLastS = fpsLastS.substring(0, fpsLastS.indexOf('.') + 2);
			}
		}
	}

}