package msi.gama.jogl.utils.Camera;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.media.opengl.glu.GLU;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Camera.Arcball.Vector3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;

public class FreeFlyCamera extends AbstractCamera {

	private static final Vector3D up = new Vector3D(0.0f, 0.0f, 1.0f);
	private final Vector3D left = new Vector3D();
	private final double speed = 0.04;

	public FreeFlyCamera(final JOGLAWTGLRenderer renderer) {
		super(renderer);
		this.phi = 0.0;
		this.theta = 0.0;
	}

	public FreeFlyCamera(final double xPos, final double yPos, final double zPos, final double xLPos,
		final double yLPos, final double zLPos, final JOGLAWTGLRenderer renderer) {
		super(xPos, yPos, zPos, xLPos, yLPos, zLPos, renderer);
	}

	protected void update() {
		if ( phi > 89 ) {
			this.phi = 89;
		} else if ( phi < -89 ) {
			this.phi = -89;
		}
		double factorP = phi * factor;
		double factorT = theta * factor;
		double r_temp = Math.cos(factorP);
		forward.set(r_temp * Math.cos(factorT), r_temp * Math.sin(factorT), Math.sin(factorP));
		left.set(Vector3D.crossProduct(up, forward).normalize());
		target.set(forward.add(position));
	}

	@Override
	protected void animate() {
		if ( isForward() ) {
			if ( isShiftKeyDown() ) {
				this.phi = phi - -get_keyboardSensivity() * get_sensivity();
				update();
			} else {
				position.set(position.add(forward.scalarMultiply(speed * 200))); // go forward
			}
		}
		if ( isBackward() ) {
			if ( isShiftKeyDown() ) {
				this.phi = phi - get_keyboardSensivity() * get_sensivity();
				update();
			} else {
				position.set(position.subtract(forward.scalarMultiply(speed * 200))); // go backward
			}
		}
		if ( isStrafeLeft() ) {
			if ( isShiftKeyDown() ) {
				this.theta = theta - -get_keyboardSensivity() * get_sensivity();
				update();
			} else {
				position.set(position.add(left.scalarMultiply(speed * 200))); // move on the right
			}
		}
		if ( isStrafeRight() ) {
			if ( isShiftKeyDown() ) {
				this.theta = theta - get_keyboardSensivity() * get_sensivity();
				update();
			} else {
				position.set(position.subtract(left.scalarMultiply(speed * 200))); // move on the left
			}
		}

		target.set(position.add(forward));
	}

	@Override
	protected void makeGluLookAt(final GLU glu) {
		glu.gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, 0.0f, 0.0f, 1.0f);
	}

	public void followAgent(final IAgent a, final GLU glu) {
		ILocation l = a.getLocation();
		position.set(l.getX(), l.getY(), l.getZ());
		glu.gluLookAt(0, 0, (float) (maxDim * 1.5), 0, 0, 0, 0.0f, 0.0f, 1.0f);
	}

	@Override
	public void resetCamera(final double envWidth, final double envHeight, final boolean threeD) {
		super.resetCamera(envWidth, envHeight, threeD);
		position.set(envWidth / 2, -envHeight * 1.75, getMaxDim());
		target.set(envWidth / 2, -envHeight * 0.5, 0);
		this.phi = -45;
		this.theta = 90;
		update();
	}

	@Override
	public Double zoomLevel() {
		return getMaxDim() * INIT_Z_FACTOR / position.getZ();
	}

	@Override
	public void zoom(final boolean in) {
		float step = Math.abs(getPosition().getZ() != 0 ? (float) position.getZ() / 10 : 0.1f);
		Vector3D vector = forward.scalarMultiply(speed * 800 + step);
		position.set(getPosition().add(in ? vector : vector.negate()));
		target.set(forward.add(getPosition()));
		getRenderer().displaySurface.setZoomLevel(zoomLevel());
	}

	@Override
	protected void zoomRoi() {
		int width = Math.abs(region[0] - region[2]);
		int height = Math.abs(region[1] - region[3]);
		double maxDim = width > height ? width : height;
		updatePosition(getRoiCenter().x, getRoiCenter().y, maxDim * 1.5);
		update();
	}

	@Override
	public void setRegionOfInterest(final Point origin, final Point end, final Vector3D worldCoordinates) {
		region[0] = origin.x;
		region[1] = end.y;
		region[2] = origin.x;
		region[3] = end.y;
		int roiWidth = Math.abs(end.x - origin.x);
		int roiHeight = Math.abs(end.y - origin.y);
		if ( region[0] == region[2] && region[1] == region[3] ) {
			getRoiCenter().setLocation(worldCoordinates.x, worldCoordinates.y);

		} else if ( region[0] < region[2] && region[1] > region[3] ) {
			getRoiCenter().setLocation(worldCoordinates.x - roiWidth / 2, worldCoordinates.y + roiHeight / 2);
		} else if ( region[0] < region[2] && region[1] < region[3] ) {
			getRoiCenter().setLocation(worldCoordinates.x - roiWidth / 2, worldCoordinates.y - roiHeight / 2);
		} else if ( region[0] > region[2] && region[1] < region[3] ) {
			getRoiCenter().setLocation(worldCoordinates.x + roiWidth / 2, worldCoordinates.y - roiHeight / 2);
		} else if ( region[0] > region[2] && region[1] > region[3] ) {
			getRoiCenter().setLocation(worldCoordinates.x + roiWidth / 2, worldCoordinates.y + roiHeight / 2);
		}

	}

	@Override
	public void zoomFocus(final double centerX, final double centerY, final double centerZ, final double extent) {
		updatePosition(centerX, centerY, extent * 2 + centerZ + getRenderer().env_width / 100);
		lookPosition(centerX, centerY, -(extent * 2));
	}

	@Override
	public void mouseDragged(final MouseEvent arg0) {
		if ( (arg0.isShiftDown() || arg0.isAltDown()) && isViewIn2DPlan() ) {
			getMousePosition().x = arg0.getX();
			getMousePosition().y = arg0.getY();
			setEnableROIDrawing(true);
			getRenderer().drawROI();
		} else {
			int horizMovement = arg0.getX() - getLastMousePressedPosition().x;
			int vertMovement = arg0.getY() - getLastMousePressedPosition().y;
			lastMousePressedPosition = arg0.getPoint();
			this.theta = theta - horizMovement * get_sensivity();
			this.phi = phi - vertMovement * get_sensivity();
			update();
		}
	}

	@Override
	protected boolean canSelectOnRelease(final MouseEvent arg0) {
		return arg0.isShiftDown() || arg0.isAltDown();
	}

	@Override
	public void dump() {
		System.out.println("xPos:" + position.x + " yPos:" + position.y + " zPos:" + position.z);
		System.out.println("xLPos:" + target.x + " yLPos:" + target.y + " zLPos:" + target.z);
		System.out.println("_forwardX:" + forward.x + " _forwardY:" + forward.y + " _forwardZ:" + forward.z);
		System.out.println("_phi : " + phi + " _theta : " + theta);
	}

	@Override
	public boolean isViewIn2DPlan() {
		return phi >= -89 && phi < -85;

	}

}
