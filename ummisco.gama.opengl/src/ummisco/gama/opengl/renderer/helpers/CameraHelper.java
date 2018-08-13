package ummisco.gama.opengl.renderer.helpers;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

import com.jogamp.opengl.swt.GLCanvas;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import ummisco.gama.opengl.camera.CameraArcBall;
import ummisco.gama.opengl.camera.FreeFlyCamera;
import ummisco.gama.opengl.camera.ICamera;
import ummisco.gama.opengl.renderer.JOGLRenderer;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class CameraHelper extends AbstractRendererHelper implements ICamera {
	public final static GamaPoint UNDEFINED = new GamaPoint();
	public final static Point NULL_POINT = new Point();
	public static Map<String, CameraPreset> PRESETS = new LinkedHashMap<>();
	ICamera camera;

	static {
		PRESETS.put("Choose...", (c) -> {});
		PRESETS.put("From top", (c) -> {
			c.setPosition(c.getTarget().x, c.getTarget().y, c.getRenderer().getMaxEnvDim() * c.getInitialZFactor());
			c.setUpVector(0, 1, 0);
		});
		PRESETS.put("From left", (c) -> {
			c.setPosition(c.getTarget().x - c.getRenderer().getEnvWidth() * c.getInitialZFactor(), c.getTarget().y, 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up left", (c) -> {
			c.setPosition(c.getTarget().x - c.getRenderer().getEnvWidth() * c.getInitialZFactor(), c.getTarget().y,
					c.getRenderer().getMaxEnvDim() * c.getInitialZFactor());
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From right", (c) -> {
			c.setPosition(c.getTarget().x + c.getRenderer().getEnvWidth() * c.getInitialZFactor(), c.getTarget().y, 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up right", (c) -> {
			c.setPosition(c.getTarget().x + c.getRenderer().getEnvWidth() * c.getInitialZFactor(), c.getTarget().y,
					c.getRenderer().getMaxEnvDim() * c.getInitialZFactor());
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From front", (c) -> {
			c.setPosition(c.getTarget().x, c.getTarget().y - c.getRenderer().getEnvHeight() * c.getInitialZFactor(), 0);
			c.setUpVector(0, 0, 1);
		});
		PRESETS.put("From up front", (c) -> {
			c.setPosition(c.getTarget().x, c.getTarget().y - c.getRenderer().getEnvHeight() * c.getInitialZFactor(),
					c.getRenderer().getMaxEnvDim() * c.getInitialZFactor());
			c.setUpVector(0, 0, 1);
		});

	}

	public CameraHelper(final JOGLRenderer renderer) {
		super(renderer);
	}

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
		if (camera != null) {
			camera.applyPreset(value);
		}
	}

	@Override
	public void updatePosition() {
		if (camera != null) {
			camera.updatePosition();
		}
	}

	@Override
	public void updateOrientation() {
		if (camera != null) {
			camera.updateOrientation();
		}
	}

	@Override
	public void updateTarget() {
		if (camera != null) {
			camera.updateTarget();
		}
	}

	@Override
	public void zoom(final double value) {
		if (camera != null) {
			camera.zoom(value);
		}
	}

	@Override
	public void update() {
		if (camera != null) {
			camera.update();
		}
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		if (camera != null) {
			camera.keyPressed(e);
		}
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (camera != null) {
			camera.keyReleased(e);
		}
	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		if (camera != null) {
			camera.mouseDoubleClick(e);
		}

	}

	@Override
	public void mouseDown(final MouseEvent e) {
		if (camera != null) {
			camera.mouseDown(e);
		}

	}

	@Override
	public void mouseUp(final MouseEvent e) {
		if (camera != null) {
			camera.mouseUp(e);
		}
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		if (camera != null) {
			camera.mouseMove(e);
		}

	}

	@Override
	public void mouseEnter(final MouseEvent e) {
		if (camera != null) {
			camera.mouseEnter(e);
		}

	}

	@Override
	public void mouseExit(final MouseEvent e) {
		if (camera != null) {
			camera.mouseExit(e);
		}

	}

	@Override
	public void mouseHover(final MouseEvent e) {
		if (camera != null) {
			camera.mouseHover(e);
		}

	}

	@Override
	public void mouseScrolled(final MouseEvent e) {
		if (camera != null) {
			camera.mouseScrolled(e);
		}

	}

	@Override
	public GamaPoint getPosition() {
		if (camera != null) { return camera.getPosition(); }
		return UNDEFINED;
	}

	@Override
	public GamaPoint getOrientation() {
		if (camera != null) { return camera.getOrientation(); }
		return UNDEFINED;
	}

	@Override
	public GamaPoint getTarget() {
		if (camera != null) { return camera.getTarget(); }
		return UNDEFINED;
	}

	@Override
	public Point getMousePosition() {
		if (camera != null) { return camera.getMousePosition(); }
		return NULL_POINT;
	}

	@Override
	public Point getLastMousePressedPosition() {
		if (camera != null) { return camera.getLastMousePressedPosition(); }
		return NULL_POINT;
	}

	public void hook() {
		final GLCanvas canvas = getCanvas();
		WorkbenchHelper.asyncRun(() -> {
			canvas.addKeyListener(this);
			canvas.addMouseListener(this);
			canvas.addMouseMoveListener(this);
			canvas.addMouseWheelListener(this);
			canvas.addMouseTrackListener(this);
		});
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
		if (camera != null) {
			camera.animate();
		}

	}

	@Override
	public Double zoomLevel() {
		if (camera != null) { return camera.zoomLevel(); }
		return 1d;
	}

	@Override
	public void zoomFocus(final IShape shape) {
		if (camera != null) {
			camera.zoomFocus(shape);
		}

	}

	@Override
	public void zoom(final boolean in) {
		if (camera != null) {
			camera.zoom(in);
		}

	}

	@Override
	public void zoomRoi(final Envelope3D env) {
		if (camera != null) {
			camera.zoomRoi(env);
		}
	}

	@Override
	public void toggleStickyROI() {
		if (camera != null) {
			camera.toggleStickyROI();
		}
	}

	@Override
	public boolean isROISticky() {
		if (camera != null) { return camera.isROISticky(); }
		return false;
	}

	@Override
	public void setPosition(final double x, final double d, final double e) {
		if (camera != null) {
			camera.setPosition(x, d, e);
		}
	}

	@Override
	public void setUpVector(final double i, final double j, final double k) {
		if (camera != null) {
			camera.setUpVector(i, j, k);
		}

	}

	@Override
	public double getDistance() {
		if (camera != null) { return camera.getDistance(); }
		return 0d;
	}

	public void dispose() {
		final GLCanvas canvas = getCanvas();
		WorkbenchHelper.asyncRun(() -> {
			if (canvas == null || canvas.isDisposed()) { return; }
			canvas.removeKeyListener(this);
			canvas.removeMouseListener(this);
			canvas.removeMouseMoveListener(this);
			canvas.removeMouseWheelListener(this);
			canvas.removeMouseTrackListener(this);
		});
		camera = null;
	}

	@Override
	public void setInitialZFactorCorrector(final double factor) {
		if (camera != null) {
			camera.setInitialZFactorCorrector(factor);
		}

	}

	@Override
	public void setDistance(final double distance) {
		if (camera != null) {
			camera.setDistance(distance);

		}

	}

	@Override
	public void updateCartesianCoordinatesFromAngles() {
		if (camera != null) {
			camera.updateCartesianCoordinatesFromAngles();
		}
	}

}
