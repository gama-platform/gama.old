/*******************************************************************************************************
 *
 * CameraHelper.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.helpers;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.ICamera;
import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.opengl.camera.AbstractCamera;
import ummisco.gama.opengl.camera.IMultiListener;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;

/**
 * The Class CameraHelper.
 */
public class CameraHelper extends AbstractRendererHelper implements ICamera, IMultiListener {

	/** The Constant UNDEFINED. */
	public final static GamaPoint UNDEFINED = new GamaPoint();

	/** The Constant NULL_POINT. */
	public final static GamaPoint NULL_POINT = new GamaPoint();

	/** The camera. */
	AbstractCamera camera;

	/**
	 * Instantiates a new camera helper.
	 *
	 * @param renderer
	 *            the renderer
	 */
	public CameraHelper(final IOpenGLRenderer renderer) {
		super(renderer);
	}

	/**
	 * Setup camera.
	 */
	public final void setupCamera() {
		camera = new AbstractCamera(getRenderer());
		camera.initialize();
		camera.update();

	}

	/**
	 * Apply preset.
	 *
	 * @param value
	 *            the value
	 */
	// @Override
	public void applyPreset(final String value) {
		if (camera != null) { camera.applyPreset(value); }
	}

	@Override
	public void zoom(final double value) {
		if (camera != null) { camera.zoom(value); }
	}

	@Override
	public void update() {
		if (camera != null) { camera.update(); }
	}

	/**
	 * Key pressed.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyPressed(final KeyEvent e) {
		if (camera != null) { camera.keyPressed(e); }
	}

	/**
	 * Key released.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyReleased(final KeyEvent e) {
		if (camera != null) { camera.keyReleased(e); }
	}

	/**
	 * Mouse double click.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		if (camera != null) { camera.mouseDoubleClick(e); }

	}

	/**
	 * Mouse down.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseDown(final MouseEvent e) {
		if (camera != null) { camera.mouseDown(e); }

	}

	/**
	 * Mouse up.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseUp(final MouseEvent e) {
		if (camera != null) { camera.mouseUp(e); }
	}

	/**
	 * Mouse move.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseMove(final MouseEvent e) {
		if (camera != null) { camera.mouseMove(e); }

	}

	/**
	 * Mouse enter.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseEnter(final MouseEvent e) {
		if (camera != null) { camera.mouseEnter(e); }

	}

	/**
	 * Mouse exit.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseExit(final MouseEvent e) {
		if (camera != null) { camera.mouseExit(e); }

	}

	/**
	 * Mouse hover.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseHover(final MouseEvent e) {
		if (camera != null) { camera.mouseHover(e); }

	}

	/**
	 * Mouse scrolled.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseScrolled(final MouseEvent e) {
		if (camera != null) { camera.mouseScrolled(e); }

	}

	@Override
	public GamaPoint getPosition() {
		if (camera != null) return camera.getPosition();
		return UNDEFINED;
	}

	/**
	 * Gets the orientation.
	 *
	 * @return the orientation
	 */
	@Override
	public GamaPoint getOrientation() {
		if (camera != null) return camera.getOrientation();
		return UNDEFINED;
	}

	@Override
	public GamaPoint getTarget() {
		if (camera != null) return camera.getTarget();
		return UNDEFINED;
	}

	@Override
	public GamaPoint getMousePosition() {
		if (camera != null) return camera.getMousePosition();
		return NULL_POINT;
	}

	@Override
	public GamaPoint getLastMousePressedPosition() {
		if (camera != null) return camera.getLastMousePressedPosition();
		return NULL_POINT;
	}

	/**
	 * Hook.
	 */
	public void hook() {
		getCanvas().addCameraListeners(this);
	}

	@Override
	public void initialize() {
		if (camera != null) {
			camera.initialize();
		} else {
			setupCamera();
		}
	}

	@Override
	public void animate() {
		if (camera != null) { camera.animate(); }

	}

	@Override
	public Double zoomLevel() {
		if (camera != null) return camera.zoomLevel();
		return 1d;
	}

	@Override
	public void zoomFocus(final Envelope3D env) {
		if (camera != null) { camera.zoomFocus(env); }
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		getCanvas().removeCameraListeners(this);
		camera = null;
	}

	/**
	 * Mouse clicked.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseClicked(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseClicked(e); }
	}

	/**
	 * Mouse entered.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseEntered(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseEntered(e); }
	}

	/**
	 * Mouse exited.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseExited(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseExited(e); }
	}

	/**
	 * Mouse pressed.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mousePressed(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mousePressed(e); }
	}

	/**
	 * Mouse released.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseReleased(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseReleased(e); }
	}

	/**
	 * Mouse moved.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseMoved(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseMoved(e); }
	}

	/**
	 * Mouse dragged.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseDragged(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseDragged(e); }
	}

	/**
	 * Mouse wheel moved.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseWheelMoved(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseWheelMoved(e); }
	}

	/**
	 * Key pressed.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyPressed(final com.jogamp.newt.event.KeyEvent e) {
		if (camera != null) { camera.keyPressed(e); }
	}

	/**
	 * Key released.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyReleased(final com.jogamp.newt.event.KeyEvent e) {
		if (camera != null) { camera.keyReleased(e); }
	}

	/**
	 * Zoom.
	 *
	 * @param b
	 *            the b
	 */
	public void zoom(final boolean b) {
		if (camera != null) { camera.zoom(b); }
	}

}
