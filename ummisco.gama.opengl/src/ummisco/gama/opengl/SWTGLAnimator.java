/*********************************************************************************************
 *
 * 'SWTGLAnimator.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import java.io.PrintStream;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.IPreferenceChangeListener;

/**
 * Simple Animator (with target FPS)
 *
 * @author AqD (aqd@5star.com.tw)
 */
public class SWTGLAnimator implements Runnable, GLAnimatorControl, GLAnimatorControl.UncaughtExceptionHandler {

	static int FRAME_PER_SECOND = GamaPreferences.Displays.OPENGL_FPS.getValue();
	protected int targetFPS = FRAME_PER_SECOND;
	protected final Thread animatorThread;
	protected final GLAutoDrawable drawable;

	protected volatile boolean stopRequested = false;
	protected volatile boolean pauseRequested = false;
	protected volatile boolean animating = false;
	protected volatile long starting_time;

	protected int frames = 0;

	public SWTGLAnimator(final GLAutoDrawable drawable) {
		GamaPreferences.Displays.OPENGL_FPS.addChangeListener(new IPreferenceChangeListener<Integer>() {

			@Override
			public boolean beforeValueChange(final Integer newValue) {
				return true;
			}

			@Override
			public void afterValueChange(final Integer newValue) {
				targetFPS = newValue;

			}
		});
		this.drawable = drawable;
		drawable.setAnimator(this);
		this.animatorThread = new Thread(this, "Animator thread");
		this.animatorThread.setDaemon(true);
	}

	@Override
	public void setUpdateFPSFrames(final int frames, final PrintStream out) {
		//
	}

	@Override
	public void resetFPSCounter() {
		//
	}

	@Override
	public int getUpdateFPSFrames() {
		return 0;
	}

	@Override
	public long getFPSStartTime() {
		return 0;
	}

	@Override
	public long getLastFPSUpdateTime() {
		return 0;
	}

	@Override
	public long getLastFPSPeriod() {
		return 0;
	}

	@Override
	public float getLastFPS() {
		final float result = (float) frames / ((System.currentTimeMillis() - starting_time) / 1000);
		// frames = 0;
		return result;
	}

	@Override
	public int getTotalFPSFrames() {
		return 0;
	}

	@Override
	public long getTotalFPSDuration() {
		return 0;
	}

	@Override
	public float getTotalFPS() {
		final float result = (float) frames / ((System.currentTimeMillis() - starting_time) / 1000);
		// frames = 0;
		return result;
	}

	@Override
	public boolean isStarted() {
		return this.animatorThread.isAlive();
	}

	@Override
	public boolean isAnimating() {
		return this.animating && !pauseRequested;
	}

	@Override
	public boolean isPaused() {
		return isStarted() && pauseRequested;
	}

	@Override
	public Thread getThread() {
		return this.animatorThread;
	}

	@Override
	public boolean start() {
		this.stopRequested = false;
		this.pauseRequested = false;
		this.animatorThread.start();
		starting_time = System.currentTimeMillis();
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
		pauseRequested = true;
		return true;
	}

	@Override
	public boolean resume() {
		pauseRequested = false;
		return true;
	}

	@Override
	public void add(final GLAutoDrawable drawable) {
		// // this.autoDrawableList.addIfAbsent(drawable);
		// if ( this.drawable != null ) {
		// remove(this.drawable);
		// }
		// this.drawable = drawable;
		// drawable.setAnimator(this);
	}

	@Override
	public void remove(final GLAutoDrawable drawable) {
		// this.autoDrawableList.remove(drawable);
		// if ( this.drawable == drawable ) {
		// this.drawable = null;
		// drawable.setAnimator(null);
		// }
	}

	@Override
	public void run() {
		final long frameDuration = 1000 / this.targetFPS;
		while (!this.stopRequested) {
			if (!pauseRequested) {
				final long timeBegin = System.currentTimeMillis();
				this.displayGL();
				frames++;
				final long timeUsed = System.currentTimeMillis() - timeBegin;
				final long timeSleep = frameDuration - timeUsed;
				if (timeSleep >= 0) {
					try {
						Thread.sleep(timeSleep);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	protected void displayGL() {
		this.animating = true;
		try {
			if (drawable.isRealized()) {
				drawable.display();
			}
		} catch (final RuntimeException ex) {
			System.out.println("Exception in OpenGL:" + ex.getMessage());
		} finally {
			this.animating = false;
		}
	}

	/**
	 * Method getUncaughtExceptionHandler()
	 * 
	 * @see com.jogamp.opengl.GLAnimatorControl#getUncaughtExceptionHandler()
	 */
	@Override
	public UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return this;
	}

	/**
	 * Method setUncaughtExceptionHandler()
	 * 
	 * @see com.jogamp.opengl.GLAnimatorControl#setUncaughtExceptionHandler(com.jogamp.opengl.GLAnimatorControl.UncaughtExceptionHandler)
	 */
	@Override
	public void setUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {}

	/**
	 * Method uncaughtException()
	 * 
	 * @see com.jogamp.opengl.GLAnimatorControl.UncaughtExceptionHandler#uncaughtException(com.jogamp.opengl.GLAnimatorControl,
	 *      com.jogamp.opengl.GLAutoDrawable, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final GLAnimatorControl animator, final GLAutoDrawable drawable,
			final Throwable cause) {
		System.out.println("Uncaught exception in animator & drawable:");
		cause.printStackTrace();

	}
}