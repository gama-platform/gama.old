/*******************************************************************************************************
 *
 * OpenGLDisplayView.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jogamp.newt.Window;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.PlatformHelper;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.FLAGS;
import ummisco.gama.ui.views.displays.LayeredDisplayView;

/**
 * Class OpenGLLayeredDisplayView.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
public class OpenGLDisplayView extends LayeredDisplayView {

	{
		DEBUG.OFF();
	}

	/** The id. */
	public static String ID = "msi.gama.application.view.OpenGLDisplayView";

	@Override
	public SWTOpenGLDisplaySurface getDisplaySurface() { return (SWTOpenGLDisplaySurface) super.getDisplaySurface(); }

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {
		final SWTOpenGLDisplaySurface surface =
				(SWTOpenGLDisplaySurface) GAMA.getGui().createDisplaySurfaceFor(getOutput(), parent);
		surfaceComposite = surface.renderer.getCanvas();
		// synchronizer.setSurface(getDisplaySurface());
		surface.outputReloaded();
		return surfaceComposite;
	}

	/**
	 * Gets the GL canvas.
	 *
	 * @return the GL canvas
	 */
	protected GamaGLCanvas getGLCanvas() { return (GamaGLCanvas) surfaceComposite; }

	/**
	 * Checks if is open GL.
	 *
	 * @return true, if is open GL
	 */
	@Override
	public boolean isOpenGL() { return true; }

	@Override
	public Control[] getZoomableControls() {
		// surfaceComposite is a GamaGLCanvas which contains a sub-canvas : this one should have the keyboard/mouse
		// focus
		return surfaceComposite.getChildren();
	}

	@Override
	public boolean forceOverlayVisibility() {
		final SWTOpenGLDisplaySurface surface = getDisplaySurface();
		return surface != null && surface.getROIDimensions() != null;
	}

	// /**
	// * Gets the multi listener.
	// *
	// * @return the multi listener
	// */
	@Override
	public IDisposable getMultiListener() {
		if (FLAGS.USE_NATIVE_OPENGL_WINDOW)
			return new NEWTLayeredDisplayMultiListener(decorator, getDisplaySurface(), getGLCanvas().getNEWTWindow());
		return super.getMultiListener();
	}

	/**
	 * Hide canvas.
	 */
	@Override
	public void hideCanvas() {
		getGLCanvas().setVisible(false);
	}

	/**
	 * Show canvas.
	 */
	@Override
	public void showCanvas() {
		getGLCanvas().setVisible(true);
		// Maybe only necessary on macOS ? Prevents JOGL views to move over Java2D views created before
		if (PlatformHelper.isMac()) { getGLCanvas().reparentWindow(); }
	}

	/**
	 * Show canvas.
	 */
	@Override
	public void focusCanvas() {
		getGLCanvas().setFocus();
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		getSurfaceComposite().forceFocus();
	}

	@Override
	public ICameraHelper getCameraHelper() { return getDisplaySurface().renderer.getCameraHelper(); }

	@Override
	public boolean hasCameras() {
		return true;
	}

	@Override
	public boolean is2D() {
		return false;
	}

}
