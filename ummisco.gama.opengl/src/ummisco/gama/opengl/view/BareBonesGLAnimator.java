/*******************************************************************************************************
 *
 * BareBonesGLAnimator.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import java.io.PrintStream;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;

import ummisco.gama.dev.utils.DEBUG;

/**
 * Simple Animator (with target FPS)
 *
 * @author AqD (aqd@5star.com.tw)
 */
public class BareBonesGLAnimator implements Runnable, GLAnimatorControl, GLAnimatorControl.UncaughtExceptionHandler {

	static {
		DEBUG.OFF();
	}

	/** The animator thread. */
	protected final Thread animatorThread;

	/** The drawable. */
	protected final GLAutoDrawable drawable;

	/** The stop requested. */
	protected volatile boolean stopRequested = false;

	/**
	 * Instantiates a new single thread GL animator.
	 *
	 * @param drawable
	 *            the drawable
	 */
	public BareBonesGLAnimator(final GLAutoDrawable drawable) {
		this.drawable = drawable;
		drawable.setAnimator(this);
		this.animatorThread = new Thread(this, "Animator thread");
	}

	@Override
	public void setUpdateFPSFrames(final int frames, final PrintStream out) {}

	@Override
	public void resetFPSCounter() {}

	@Override
	public int getUpdateFPSFrames() { return 0; }

	@Override
	public long getFPSStartTime() { return 0; }

	@Override
	public long getLastFPSUpdateTime() { return 0; }

	@Override
	public long getLastFPSPeriod() { return 0; }

	@Override
	public float getLastFPS() { return 0; }

	@Override
	public int getTotalFPSFrames() { return 0; }

	@Override
	public long getTotalFPSDuration() { return 0; }

	@Override
	public float getTotalFPS() { return 0; }

	@Override
	public boolean isStarted() { return this.animatorThread.isAlive(); }

	@Override
	public boolean isAnimating() { return true; }

	@Override
	public boolean isPaused() { return false; }

	@Override
	public Thread getThread() { return this.animatorThread; }

	@Override
	public boolean start() {
		this.stopRequested = false;
		this.animatorThread.start();
		return true;
	}

	@Override
	public boolean stop() {
		this.stopRequested = true;
		try {
			this.animatorThread.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.stopRequested = false;
		}
		return true;
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
		while (!drawable.isRealized()) {}
		while (!stopRequested) { drawable.display(); }
	}

	@Override
	public UncaughtExceptionHandler getUncaughtExceptionHandler() { return this; }

	@Override
	public void setUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {}

	@Override
	public void uncaughtException(final GLAnimatorControl animator, final GLAutoDrawable drawable,
			final Throwable cause) {
		DEBUG.ERR("Uncaught exception in animator & drawable:");
		cause.printStackTrace();

	}
}