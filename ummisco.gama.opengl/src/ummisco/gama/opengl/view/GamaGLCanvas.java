/*******************************************************************************************************
 *
 * GamaGLCanvas.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import java.io.PrintStream;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jogamp.common.util.locks.RecursiveLock;
import com.jogamp.nativewindow.NativeSurface;
import com.jogamp.newt.Window;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.swt.NewtCanvasSWT;
import com.jogamp.opengl.FPSCounter;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLRunnable;

import msi.gama.runtime.PlatformHelper;
import ummisco.gama.opengl.camera.IMultiListener;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.ui.bindings.IDelegateEventsToParent;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class GamaGLCanvas.
 */
public class GamaGLCanvas extends Composite implements GLAutoDrawable, IDelegateEventsToParent, FPSCounter {

	/** The canvas. */
	final Control canvas;

	/** The window. */
	final GLWindow window;

	/** The detached. */
	protected boolean detached = false;

	/**
	 * Instantiates a new gama GL canvas.
	 *
	 * @param parent
	 *            the parent
	 * @param renderer
	 *            the renderer
	 */
	public GamaGLCanvas(final Composite parent, final IOpenGLRenderer renderer) {
		super(parent, SWT.NONE);
		parent.setLayout(new FillLayout());
		this.setLayout(new FillLayout());
		final var cap = defineCapabilities();

		window = GLWindow.create(cap);
		canvas = new NewtCanvasSWT(this, SWT.NONE, window);
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				/* Detached views have not title! */
				if (PlatformHelper.isMac()) {
					final var isDetached = parent.getShell().getText().length() == 0;
					if (isDetached) {
						if (!detached) {
							reparentWindow();
							detached = true;
						}

					} else if (detached) {
						reparentWindow();
						detached = false;
					}
				}
			}
		});
		window.setAutoSwapBufferMode(true);

		window.addGLEventListener(renderer);
		final var animator = new GamaGLAnimator(window);
		renderer.setCanvas(this);
		addDisposeListener(e -> new Thread(() -> {
			animator.stop();
		}).start());
	}


	/**
	 * Define capabilities.
	 *
	 * @return the GL capabilities
	 * @throws GLException
	 *             the GL exception
	 */
	private GLCapabilities defineCapabilities() throws GLException {
		final var profile = GLProfile.getDefault();
		final var cap = new GLCapabilities(profile);
		cap.setDepthBits(24);
		cap.setDoubleBuffered(true);
		cap.setHardwareAccelerated(true);
		cap.setSampleBuffers(true);
		cap.setAlphaBits(8);
		cap.setNumSamples(8);
		return cap;
	}

	@Override
	public void setRealized(final boolean realized) {
		window.setRealized(realized);
	}

	@Override
	public boolean isRealized() {
		return window.isRealized();
	}

	@Override
	public int getSurfaceWidth() {
		return window.getSurfaceWidth();
	}

	@Override
	public int getSurfaceHeight() {
		return window.getSurfaceHeight();
	}

	@Override
	public boolean isGLOriented() {
		return window.isGLOriented();
	}

	@Override
	public void swapBuffers() throws GLException {
		window.swapBuffers();
	}

	@Override
	public GLCapabilitiesImmutable getChosenGLCapabilities() {
		return window.getChosenGLCapabilities();
	}

	@Override
	public GLCapabilitiesImmutable getRequestedGLCapabilities() {
		return window.getRequestedGLCapabilities();
	}

	@Override
	public GLProfile getGLProfile() {
		return window.getGLProfile();
	}

	@Override
	public NativeSurface getNativeSurface() {
		return window.getNativeSurface();
	}

	@Override
	public long getHandle() {
		return window.getHandle();
	}

	@Override
	public GLDrawableFactory getFactory() {
		return window.getFactory();
	}

	@Override
	public GLDrawable getDelegatedDrawable() {
		return window.getDelegatedDrawable();
	}

	@Override
	public GLContext getContext() {
		return window.getContext();
	}

	@Override
	public GLContext setContext(final GLContext newCtx, final boolean destroyPrevCtx) {
		return window.setContext(newCtx, destroyPrevCtx);
	}

	@Override
	public void addGLEventListener(final GLEventListener listener) {
		window.addGLEventListener(listener);
	}

	@Override
	public void addGLEventListener(final int index, final GLEventListener listener) throws IndexOutOfBoundsException {
		window.addGLEventListener(index, listener);
	}

	@Override
	public int getGLEventListenerCount() {
		return window.getGLEventListenerCount();
	}

	@Override
	public boolean areAllGLEventListenerInitialized() {
		return window.areAllGLEventListenerInitialized();
	}

	@Override
	public GLEventListener getGLEventListener(final int index) throws IndexOutOfBoundsException {
		return window.getGLEventListener(index);
	}

	@Override
	public boolean getGLEventListenerInitState(final GLEventListener listener) {
		return window.getGLEventListenerInitState(listener);
	}

	@Override
	public void setGLEventListenerInitState(final GLEventListener listener, final boolean initialized) {
		window.setGLEventListenerInitState(listener, initialized);
	}

	@Override
	public GLEventListener disposeGLEventListener(final GLEventListener listener, final boolean remove) {
		return window.disposeGLEventListener(listener, remove);
	}

	@Override
	public GLEventListener removeGLEventListener(final GLEventListener listener) {
		return window.removeGLEventListener(listener);
	}

	@Override
	public void setAnimator(final GLAnimatorControl animatorControl) throws GLException {
		window.setAnimator(animatorControl);
	}

	@Override
	public GLAnimatorControl getAnimator() {
		return window.getAnimator();
	}

	@Override
	public Thread setExclusiveContextThread(final Thread t) throws GLException {
		return window.setExclusiveContextThread(t);
	}

	@Override
	public Thread getExclusiveContextThread() {
		return window.getExclusiveContextThread();
	}

	@Override
	public boolean invoke(final boolean wait, final GLRunnable glRunnable) throws IllegalStateException {
		return window.invoke(wait, glRunnable);
	}

	@Override
	public boolean invoke(final boolean wait, final List<GLRunnable> glRunnables) throws IllegalStateException {
		return window.invoke(wait, glRunnables);
	}

	@Override
	public void flushGLRunnables() {
		window.flushGLRunnables();
	}

	@Override
	public void destroy() {
		window.destroy();
	}

	@Override
	public void display() {
		window.display();
	}

	@Override
	public void setAutoSwapBufferMode(final boolean enable) {
		window.setAutoSwapBufferMode(enable);
	}

	@Override
	public boolean getAutoSwapBufferMode() {
		return window.getAutoSwapBufferMode();
	}

	@Override
	public void setContextCreationFlags(final int flags) {
		window.setContextCreationFlags(flags);
	}

	@Override
	public int getContextCreationFlags() {
		return window.getContextCreationFlags();
	}

	@Override
	public GLContext createContext(final GLContext shareWith) {
		return window.createContext(shareWith);
	}

	@Override
	public GL getGL() {
		return window.getGL();
	}

	@Override
	public GL setGL(final GL gl) {
		return window.setGL(gl);
	}

	@Override
	public Object getUpstreamWidget() {
		return window.getUpstreamWidget();
	}

	@Override
	public RecursiveLock getUpstreamLock() {
		return window.getUpstreamLock();
	}

	@Override
	public boolean isThreadGLCapable() {
		return window.isThreadGLCapable();
	}

	/**
	 * Gets the NEWT window.
	 *
	 * @return the NEWT window
	 */
	public Window getNEWTWindow() {
		// if (FLAGS.USE_NATIVE_OPENGL_WINDOW)
		return window;
		// return null;
	}

	/**
	 * Reparent window.
	 */
	public void reparentWindow() {
		// if (!FLAGS.USE_NATIVE_OPENGL_WINDOW) return;
		final Window w = window;
		w.setVisible(false);
		w.setFullscreen(true);
		w.setFullscreen(false);
		w.setVisible(true);
	}

	/**
	 * Sets the window visible.
	 *
	 * @param b
	 *            the new window visible
	 */
	public void setWindowVisible(final boolean b) {
		// if (!FLAGS.USE_NATIVE_OPENGL_WINDOW) return;
		final Window w = window;
		w.setVisible(b);
	}

	@Override
	public boolean setFocus() {
		return canvas.setFocus();
	}

	/**
	 * Adds the camera listeners.
	 *
	 * @param camera
	 *            the camera
	 */
	public void addCameraListeners(final IMultiListener camera) {

		WorkbenchHelper.asyncRun(() -> {
			if (isDisposed() || canvas.isDisposed()) { return; }
			canvas.addKeyListener(camera);
			canvas.addMouseListener(camera);
			canvas.addMouseMoveListener(camera);
			canvas.addMouseWheelListener(camera);
			canvas.addMouseTrackListener(camera);
			addKeyListener(camera);
			addMouseListener(camera);
			addMouseMoveListener(camera);
			addMouseWheelListener(camera);
			addMouseTrackListener(camera);
			window.addKeyListener(camera);
			window.addMouseListener(camera);
		});
	}

	/**
	 * Removes the camera listeners.
	 *
	 * @param camera
	 *            the camera
	 */
	public void removeCameraListeners(final IMultiListener camera) {
		WorkbenchHelper.asyncRun(() -> {
			if (isDisposed() || canvas.isDisposed()) { return; }
			canvas.removeKeyListener(camera);
			canvas.removeMouseListener(camera);
			canvas.removeMouseMoveListener(camera);
			canvas.removeMouseWheelListener(camera);
			canvas.removeMouseTrackListener(camera);
			removeKeyListener(camera);
			removeMouseListener(camera);
			removeMouseMoveListener(camera);
			removeMouseWheelListener(camera);
			removeMouseTrackListener(camera);
			window.removeKeyListener(camera);
			window.removeMouseListener(camera);
		});
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
