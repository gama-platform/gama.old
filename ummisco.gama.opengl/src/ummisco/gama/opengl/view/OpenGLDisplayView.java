/*******************************************************************************************************
 *
 * OpenGLDisplayView.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.FLAGS;
import ummisco.gama.opengl.renderer.helpers.CameraHelper;
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

	@Override
	public List<String> getCameraNames() { return new ArrayList<>(CameraHelper.PRESETS.keySet()); }

	@Override
	public void toggleFullScreen() {
		hideCanvas();
		super.toggleFullScreen();
		getGLCanvas().reparentWindow();
	}

	/**
	 * Hide canvas.
	 */
	@Override
	public void hideCanvas() {
		getGLCanvas().setWindowVisible(false);
	}

	/**
	 * Show canvas.
	 */
	@Override
	public void showCanvas() {
		getGLCanvas().setWindowVisible(true);
	}

	/**
	 * Show canvas.
	 */
	@Override
	public void focusCanvas() {
		getGLCanvas().setFocus();
	}

}
