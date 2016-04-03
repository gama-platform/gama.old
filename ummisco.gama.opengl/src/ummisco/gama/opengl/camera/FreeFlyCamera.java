/*********************************************************************************************
 *
 *
 * 'FreeFlyCamera.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.camera;

import java.awt.Point;
import org.eclipse.swt.SWT;
import com.jogamp.opengl.glu.GLU;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayData;
import msi.gaml.operators.Maths;
import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.JOGLRenderer;

public class FreeFlyCamera extends AbstractCamera {

	private static final GamaPoint up = new GamaPoint(0.0f, 0.0f, 1.0f);
	protected final GamaPoint forward = new GamaPoint(0, 0, 0);
	private final GamaPoint left = new GamaPoint(0, 0, 0);
	private final double speed = getRenderer().getMaxEnvDim()*0.0001;
	
	private boolean shift_pressed = false;

	public FreeFlyCamera(final JOGLRenderer renderer) {
		super(renderer);
		reset();
	}

	protected void updateCartesianCoordinatesFromAngles() {
		if ( phi > 89 ) {
			this.phi = 89;
		} else if ( phi < -89 ) {
			this.phi = -89;
		}
		double factorP = phi * Maths.toRad;
		double factorT = theta * Maths.toRad;
		double r_temp = FastMath.cos(factorP);
		forward.setLocation(r_temp * FastMath.cos(factorT), r_temp * FastMath.sin(factorT), FastMath.sin(factorP));
		left.setLocation(GamaPoint.crossProduct(up, forward).normalized());
		target.setLocation(forward.plus(position));
	}

	@Override
	public void animate() {
		super.animate();
		if ( isForward() ) {
			if ( shift_pressed ) {
				this.phi = phi - -get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				position.setLocation(position.plus(forward.times(speed * 200))); // go forward
			}
		}
		if ( isBackward() ) {
			if ( shift_pressed ) {
				this.phi = phi - get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				position.setLocation(position.minus(forward.times(speed * 200))); // go backward
			}
		}
		if ( isStrafeLeft() ) {
			if ( shift_pressed ) {
				this.theta = theta - -get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				position.setLocation(position.plus(left.times(speed * 200))); // move on the right
			}
		}
		if ( isStrafeRight() ) {
			if ( shift_pressed ) {
				this.theta = theta - get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				position.setLocation(position.minus(left.times(speed * 200))); // move on the left
			}
		}

		target.setLocation(position.plus(forward));
	}

	@Override
	public void upPosition(final double xPos, final double yPos, final double zPos) {
		// Not allowed for this camera
	}

	public void followAgent(final IAgent a, final GLU glu) {
		ILocation l = a.getLocation();
		position.setLocation(l.getX(), l.getY(), l.getZ());
		glu.gluLookAt(0, 0, (float) (getRenderer().getMaxEnvDim() * 1.5), 0, 0, 0, 0.0f, 0.0f, 1.0f);
	}

	@Override
	public void reset() {
		upVector.setLocation(up);
		LayeredDisplayData data = getRenderer().data;
		double envWidth = data.getEnvWidth();
		double envHeight = data.getEnvHeight();
		position.setLocation(envWidth / 2, -envHeight * 1.75, getRenderer().getMaxEnvDim());
		target.setLocation(envWidth / 2, -envHeight * 0.5, 0);
		this.phi = -45;
		this.theta = 90;
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public Double zoomLevel() {
		return getRenderer().getMaxEnvDim() * INIT_Z_FACTOR / position.getZ();
	}

	@Override
	public void zoom(final boolean in) {
		float step = FastMath.abs(getPosition().getZ() != 0 ? (float) position.getZ() / 10 : 0.1f);
		GamaPoint vector = forward.times(speed * 800 + step);
		position.setLocation(getPosition().plus(in ? vector : vector.negated()));
		target.setLocation(forward.plus(getPosition()));
		getRenderer().data.setZoomLevel(zoomLevel());
	}

	@Override
	protected void zoomRoi(final Envelope3D env) {
		int width = (int) env.getWidth();
		int height = (int) env.getHeight();
		double maxDim = width > height ? width : height;
		updatePosition(env.centre().x, env.centre().y, maxDim * 1.5);
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public void zoomFocus(final IShape shape) {
		double centerX = shape.getLocation().getX();
		double centerY = shape.getLocation().getY();
		double centerZ = shape.getLocation().getZ();
		double extent = shape.getEnvelope().maxExtent();
		updatePosition(centerX, -centerY, extent * 2 + centerZ + getRenderer().getMaxEnvDim() / 100);
		lookPosition(centerX, -centerY, -(extent * 2));
	}

	@Override
	public void mouseMove(final org.eclipse.swt.events.MouseEvent e) {
		super.mouseMove(e);
		if ( (e.stateMask & SWT.BUTTON_MASK) == 0 ) { return; }
		if ( (shift(e) || alt(e)) && isViewInXYPlan() ) {
			getMousePosition().x = e.x;
			getMousePosition().y = e.y;
			// GL2 gl = GLContext.getCurrent().getGL().getGL2();
			getRenderer().defineROI(firstMousePressedPosition, getMousePosition());
		} else {
			int horizMovement = e.x - getLastMousePressedPosition().x;
			int vertMovement = e.y - getLastMousePressedPosition().y;
			lastMousePressedPosition = new Point(e.x, e.y);
			this.theta = theta - horizMovement * get_sensivity();
			this.phi = phi - vertMovement * get_sensivity();
			updateCartesianCoordinatesFromAngles();
		}
	}

	// @Override
	// protected boolean canSelectOnRelease(final MouseEvent arg0) {
	// return arg0.isShiftDown() || arg0.isAltDown();
	// }

	@Override
	protected boolean canSelectOnRelease(final org.eclipse.swt.events.MouseEvent arg0) {
		return shift(arg0) || alt(arg0);
	}

	@Override
	public void dump() {
		System.out.println("xPos:" + position.x + " yPos:" + position.y + " zPos:" + position.z);
		System.out.println("xLPos:" + target.x + " yLPos:" + target.y + " zLPos:" + target.z);
		System.out.println("_forwardX:" + forward.x + " _forwardY:" + forward.y + " _forwardZ:" + forward.z);
		System.out.println("_phi : " + phi + " _theta : " + theta);
	}

	@Override
	public boolean isViewInXYPlan() {
		return phi >= -89 && phi < -85;

	}
	
	@Override
	protected void Shift_pressed(boolean value) {
		shift_pressed = value;
		drawRotationHelper();
	}

}
