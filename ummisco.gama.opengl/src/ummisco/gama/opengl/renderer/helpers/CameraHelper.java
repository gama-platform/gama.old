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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.opengl.camera.CameraArcBall;
import ummisco.gama.opengl.camera.FreeFlyCamera;
import ummisco.gama.opengl.camera.ICamera;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;

/**
 * The Class CameraHelper.
 */
public class CameraHelper extends AbstractRendererHelper implements ICamera {

	/** The Constant UNDEFINED. */
	public final static GamaPoint UNDEFINED = new GamaPoint();

	/** The Constant NULL_POINT. */
	public final static GamaPoint NULL_POINT = new GamaPoint();

	/** The presets. */
	public static Map<String, CameraPreset> PRESETS = new LinkedHashMap<>();

	/** The camera. */
	ICamera camera;

	static {
		PRESETS.put("Choose...", c -> {});
		PRESETS.put("From top", c -> {
			c.setPosition(c.getTarget().x, c.getTarget().y, c.getRenderer().getMaxEnvDim() * c.getInitialZFactor());
			c.setUpVector(0, 1, 0);
		});
		PRESETS.put("From left", c -> {
			c.setPosition(c.getTarget().x - c.getRenderer().getEnvWidth() * c.getInitialZFactor(), c.getTarget().y, 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up left", c -> {
			c.setPosition(c.getTarget().x - c.getRenderer().getEnvWidth() * c.getInitialZFactor(), c.getTarget().y,
					c.getRenderer().getMaxEnvDim() * c.getInitialZFactor());
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From right", c -> {
			c.setPosition(c.getTarget().x + c.getRenderer().getEnvWidth() * c.getInitialZFactor(), c.getTarget().y, 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up right", c -> {
			c.setPosition(c.getTarget().x + c.getRenderer().getEnvWidth() * c.getInitialZFactor(), c.getTarget().y,
					c.getRenderer().getMaxEnvDim() * c.getInitialZFactor());
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From front", c -> {
			c.setPosition(c.getTarget().x, c.getTarget().y - c.getRenderer().getEnvHeight() * c.getInitialZFactor(), 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up front", c -> {
			c.setPosition(c.getTarget().x, c.getTarget().y - c.getRenderer().getEnvHeight() * c.getInitialZFactor(),
					c.getRenderer().getMaxEnvDim() * c.getInitialZFactor());
			c.setUpVector(0, 0, 1);
		});

	}

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
		if (!getData().isArcBallCamera()) {
			camera = new FreeFlyCamera(getRenderer());
		} else {
			camera = new CameraArcBall(getRenderer());
		}
		camera.initialize();
		camera.update();

	}

	@Override
	public void applyPreset(final String value) {
		if (camera != null) { camera.applyPreset(value); }
	}

	@Override
	public void updatePosition() {
		if (camera != null) { camera.updatePosition(); }
	}

	@Override
	public void updateOrientation() {
		if (camera != null) { camera.updateOrientation(); }
	}

	@Override
	public void updateTarget() {
		if (camera != null) { camera.updateTarget(); }
	}

	@Override
	public void zoom(final double value) {
		if (camera != null) { camera.zoom(value); }
	}

	@Override
	public void update() {
		if (camera != null) { camera.update(); }
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		if (camera != null) { camera.keyPressed(e); }
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (camera != null) { camera.keyReleased(e); }
	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		if (camera != null) { camera.mouseDoubleClick(e); }

	}

	@Override
	public void mouseDown(final MouseEvent e) {
		if (camera != null) { camera.mouseDown(e); }

	}

	@Override
	public void mouseUp(final MouseEvent e) {
		if (camera != null) { camera.mouseUp(e); }
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		if (camera != null) { camera.mouseMove(e); }

	}

	@Override
	public void mouseEnter(final MouseEvent e) {
		if (camera != null) { camera.mouseEnter(e); }

	}

	@Override
	public void mouseExit(final MouseEvent e) {
		if (camera != null) { camera.mouseExit(e); }

	}

	@Override
	public void mouseHover(final MouseEvent e) {
		if (camera != null) { camera.mouseHover(e); }

	}

	@Override
	public void mouseScrolled(final MouseEvent e) {
		if (camera != null) { camera.mouseScrolled(e); }

	}

	@Override
	public GamaPoint getPosition() {
		if (camera != null) return camera.getPosition();
		return UNDEFINED;
	}

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

	@Override
	public void mouseClicked(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseClicked(e); }
	}

	@Override
	public void mouseEntered(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseEntered(e); }
	}

	@Override
	public void mouseExited(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseExited(e); }
	}

	@Override
	public void mousePressed(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mousePressed(e); }
	}

	@Override
	public void mouseReleased(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseReleased(e); }
	}

	@Override
	public void mouseMoved(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseMoved(e); }
	}

	@Override
	public void mouseDragged(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseDragged(e); }
	}

	@Override
	public void mouseWheelMoved(final com.jogamp.newt.event.MouseEvent e) {
		if (camera != null) { camera.mouseWheelMoved(e); }
	}

	@Override
	public void keyPressed(final com.jogamp.newt.event.KeyEvent e) {
		if (camera != null) { camera.keyPressed(e); }
	}

	@Override
	public void keyReleased(final com.jogamp.newt.event.KeyEvent e) {
		if (camera != null) { camera.keyReleased(e); }
	}

}
