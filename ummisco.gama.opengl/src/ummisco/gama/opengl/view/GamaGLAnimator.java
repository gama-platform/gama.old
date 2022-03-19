/*******************************************************************************************************
 *
 * SingleThreadGLAnimator.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import static msi.gama.runtime.PlatformHelper.isARM;

import java.io.PrintStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.FPSCounter;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLAnimatorControl.UncaughtExceptionHandler;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.PlatformHelper;
import ummisco.gama.dev.utils.DEBUG;
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
	private final GLWindow window;

	/** The stop requested. */
	protected volatile boolean stopRequested = false;

	/**
	 * Instantiates a new single thread GL animator.
	 *
	 * @param window
	 *            the drawable
	 */
	public GamaGLAnimator(final GLWindow window) {
		this.window = window;
		window.setAnimator(this);
		this.animatorThread = new Thread(this, "Animator thread");
		GamaPreferences.Displays.OPENGL_FPS.onChange(newValue -> targetFPS = newValue);
		setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, null);
	}

	public void displayGL() {
		if (isARM())
			WorkbenchHelper.run(() -> window.display());
		else
			window.display();
		if (capFPS) {
			final long timeSleep = 1000 / targetFPS - getLastFPSPeriod();
			try {
				if (timeSleep >= 0) { Thread.sleep(timeSleep); }
			} catch (final InterruptedException e) {}
		}
	}

	@Override
	public boolean isStarted() {
		return animatorThread.isAlive();
	}

	@Override
	public Thread getThread() {
		return animatorThread;
	}

	@Override
	public boolean start() {
		this.stopRequested = false;
		this.animatorThread.start();
		return true;
	}

	@Override
	public boolean stop() {
		this.stopRequested = true;
		if (PlatformHelper.isARM() && WorkbenchHelper.isDisplayThread()) return true;
		try {
			this.animatorThread.join();
		} catch (final InterruptedException e) {} finally {
			this.stopRequested = false;
		}
		return true;
	}

	@Override
	public boolean isAnimating() {
		return true;
	}

	@Override
	public boolean isPaused() {
		return false;
	}

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
		while (!window.isRealized()) {}
		while (!stopRequested) {
			try {
				if (isARM())
					WorkbenchHelper.run(() -> window.display());
				else
					window.display();
				if (capFPS) {
					final long timeSleep = 1000 / targetFPS - getLastFPSPeriod();
					if (timeSleep >= 0) { Thread.sleep(timeSleep); }
				}
			} catch (final InterruptedException | RuntimeException ex) {
				uncaughtException(this, window, ex);
			}
		}
	}

	@Override
	public UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return this;
	}

	@Override
	public void setUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {}

	@Override
	public void uncaughtException(final GLAnimatorControl animator, final GLAutoDrawable drawable,
			final Throwable cause) {
		DEBUG.ERR("Uncaught exception in animator & drawable:" + cause.getMessage());
		cause.printStackTrace();

	}

	@Override
	public void setUpdateFPSFrames(final int frames, final PrintStream out) {
		window.setUpdateFPSFrames(frames, out);
	}

	@Override
	public void resetFPSCounter() {
		window.resetFPSCounter();
	}

	@Override
	public int getUpdateFPSFrames() {
		return window.getUpdateFPSFrames();
	}

	@Override
	public long getFPSStartTime() {
		return window.getFPSStartTime();
	}

	@Override
	public long getLastFPSUpdateTime() {
		return window.getLastFPSUpdateTime();
	}

	@Override
	public long getLastFPSPeriod() {
		return window.getLastFPSPeriod();
	}

	@Override
	public float getLastFPS() {
		return window.getLastFPS();
	}

	@Override
	public int getTotalFPSFrames() {
		return window.getTotalFPSFrames();
	}

	@Override
	public long getTotalFPSDuration() {
		return window.getTotalFPSDuration();
	}

	@Override
	public float getTotalFPS() {
		return window.getTotalFPS();
	}

}