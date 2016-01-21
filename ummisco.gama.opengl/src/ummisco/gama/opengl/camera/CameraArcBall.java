/*********************************************************************************************
 *
 *
 * 'CameraArcBall.java', in plugin 'msi.gama.jogl2', is part of the source code of the
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
import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gaml.operators.Maths;
import ummisco.gama.opengl.JOGLRenderer;

public class CameraArcBall extends AbstractCamera {

	private double radius;
	private double horizInertia;
	private double vertInertia = 0;

	private double velocityHoriz;
	private double velocityVert = 0;

	// inertia parameter
	private final double damping = 0.9;
	private final double amplitude = 0.3;
	private boolean enableInertia = false;
	private boolean arcBallInertia = false;
	private boolean moveInertia = false;

	public CameraArcBall(final JOGLRenderer joglawtglRenderer) {
		super(joglawtglRenderer);
		phi = 90.00;
		theta = 360.00;
		updateCartesianCoordinatesFromAngles();
	}

	// Use when the alt+right/left is pressed (rotate the camera upvector around z axis).
	public void rotateCameraUpVectorOnZ(final boolean clock) {
		upPosition(Math.cos(Math.PI / 2 + curZRotation), Math.sin(Math.PI / 2 + curZRotation), upVector.z);
		if ( clock ) {
			curZRotation = curZRotation - Math.PI / 64;
		} else {
			curZRotation = curZRotation + Math.PI / 64;
		}
	}

	protected void updateCartesianCoordinatesFromAngles() {
		theta = theta % 360;
		phi = phi % 360;
		double factorT = theta * factor;
		double factorP = phi * factor;
		double cosT = Math.cos(factorT);
		double sinT = Math.sin(factorT);
		double cosP = Math.cos(factorP);
		double sinP = Math.sin(factorP);
		position.setLocation(radius * sinT * sinP + target.x, radius * cosP + target.y,
			radius * cosT * sinP + target.z);
	}

	@Override
	public void updateSphericalCoordinatesFromLocations() {
		double x = position.x - target.x;
		double y = position.y - target.y;
		double z = position.z - target.z;
		radius = Math.sqrt(x * x + y * y + z * z);
		theta = Maths.toDeg * Math.atan2(x, z);
		phi = Maths.toDeg * Math.acos(y / radius);
		if ( upVector.getY() == -1 ) {
			phi = 360.0 - phi;
			theta = 180 + theta;
		}
	}

	// public void followAgent(IAgent a) {
	//
	// ILocation l = a.getGeometry().getLocation();
	// Envelope env = a.getGeometry().getEnvelope();
	//
	// double xPos = l.getX() - myRenderer.displaySurface.getEnvWidth() / 2;
	// double yPos = -(l.getY() - myRenderer.displaySurface.getEnvHeight() / 2);
	//
	// double zPos = env.maxExtent() * 2 + l.getZ();
	// double zLPos = -(env.maxExtent() * 2);
	//
	// updatePosition(xPos, yPos, zPos);
	// lookPosition(xPos, yPos, zLPos);
	//
	// }

	@Override
	public void resetCamera(final double envWidth, final double envHeight, final boolean threeD) {
		radius = getRenderer().getMaxEnvDim() * INIT_Z_FACTOR;
		target.setLocation(envWidth / 2, -envHeight / 2, 0);
		phi = threeD ? 135.0 : 90.0;
		theta = 360.00;
		updateCartesianCoordinatesFromAngles();
	}

	// Move in the XY plan by changing camera pos and look pos.
	private void moveXYPlan2(final double diffx, final double diffy, final double z, final double w, final double h) {

		double translationValue = 0;

		translationValue = Math.abs(diffx) * ((z + 1) / w);

		if ( diffx > 0 ) {// move right
			updatePosition(position.x - translationValue, position.y, position.z);
			lookPosition(target.x - translationValue, target.y, target.z);
		} else {// move left
			updatePosition(position.x + translationValue, position.y, position.z);
			lookPosition(target.x + translationValue, target.y, target.z);
		}

		translationValue = Math.abs(diffy) * Math.abs((z + 1) / h);

		if ( diffy > 0 ) {// move down
			updatePosition(position.x, position.y + translationValue, position.z);
			lookPosition(target.x, target.y + translationValue, target.z);
		} else {// move up
			updatePosition(position.x, position.y - translationValue, position.z);
			lookPosition(target.x, target.y - translationValue, target.z);
		}

	}

	@Override
	public void animate() {

		double translation = 2 * (Math.abs(position.z) + 1) / getRenderer().getHeight();
		if ( isForward() ) {
			if ( isShiftKeyDown() ) {
				phi = phi - -get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				updatePosition(position.x, position.y - translation, position.z);
				lookPosition(target.x, target.y - translation, target.z);

			}
		}
		if ( isBackward() ) {
			if ( isShiftKeyDown() ) {
				phi = phi - get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				updatePosition(position.x, position.y + translation, position.z);
				lookPosition(target.x, target.y + translation, target.z);
			}
		}
		if ( isStrafeLeft() ) {
			if ( isShiftKeyDown() ) {
				theta = theta - -get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				if ( isAltKeyDown() && isViewIn2DPlan() ) {
					rotateCameraUpVectorOnZ(true);

				} else {
					updatePosition(position.x + translation, position.y, position.z);
					lookPosition(target.x + translation, target.y, target.z);
				}
			}
		}
		if ( isStrafeRight() ) {
			if ( isShiftKeyDown() ) {
				theta = theta - get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				if ( isAltKeyDown() && isViewIn2DPlan() ) {
					rotateCameraUpVectorOnZ(false);
				} else {
					updatePosition(position.x - translation, position.y, position.z);
					lookPosition(target.x - translation, target.y, target.z);
				}
			}
		}
	}

	@Override
	public Double zoomLevel() {
		return getRenderer().getMaxEnvDim() * INIT_Z_FACTOR / radius;
	}

	@Override
	public void zoom(final boolean in) {
		
		//FIXME the old version (before the GAMA1.7 release (not working not so bad)) see issue #1477
		double step = radius != 0d ? radius / 10d : 0.1d;

		//double step = radius / getRenderer().getMaxEnvDim() * (INIT_Z_FACTOR + GamaPreferences.OPENGL_ZOOM.getValue());

		// double step = radius != 0d ? radius / 10d : 0.1d;
		radius = radius + (in ? -step : step);
		getRenderer().data.setZoomLevel(zoomLevel());
		// .newZoomLevel(zoomLevel());
		// dump();
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	protected void zoomRoi(final Envelope3D env) {
		int width = (int) env.getWidth();
		int height = (int) env.getHeight();
		radius = 1.5 * (width > height ? width : height);
		target.setLocation(env.centre());
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public void zoomFocus(final double centerX, final double centerY, final double centerZ, final double extent) {
		velocityHoriz = 0;
		velocityVert = 0;
		final double zPos;

		if ( extent == 0 ) {
			zPos = centerZ + getRenderer().getMaxEnvDim() / 10;
		}

		else {
			zPos = extent * 1.5;
		}
		radius = zPos;
		updateCartesianCoordinatesFromAngles();
		updatePosition(centerX, -centerY, zPos);
		lookPosition(centerX, -centerY, 0);
	}

	@Override
	public void mouseMove(final org.eclipse.swt.events.MouseEvent e) {
		super.mouseMove(e);
		if ( (e.stateMask & SWT.BUTTON_MASK) == 0 ) { return; }

		Point newPoint = new Point(e.x, e.y);
		enableInertia = false;
		if ( isArcBallOn(e) ) {
			arcBallInertia = true;
		} else {
			if ( lastMousePressedPosition == null ) {
				horizInertia = 0;
				vertInertia = 0;
			} else {
				horizInertia = newPoint.x - lastMousePressedPosition.x;
				vertInertia = newPoint.y - lastMousePressedPosition.y;
			}
			velocityHoriz = horizInertia;
			velocityVert = vertInertia;
			moveInertia = true;
		}

		if ( isArcBallOn(e) ) {

			// check the difference between the current x and the last x position
			int horizMovement = e.x - lastMousePressedPosition.x;
			// check the difference between the current y and the last y position
			int vertMovement = e.y - lastMousePressedPosition.y;

			horizInertia = newPoint.x - lastMousePressedPosition.x;
			vertInertia = newPoint.y - lastMousePressedPosition.y;
			velocityHoriz = horizInertia;
			velocityVert = vertInertia;
			lastMousePressedPosition = newPoint;
			theta = theta - horizMovement * get_sensivity();
			phi = phi - vertMovement * get_sensivity();
			updateCartesianCoordinatesFromAngles();

		}
		// ROI Is enabled only if the view is in a 2D plan.
		// else if ( myRenderer.displaySurface.selectRectangle && IsViewIn2DPlan() ) {

		else if ( (shift(e) || alt(e)) && isViewIn2DPlan() ) {
			getMousePosition().x = e.x;
			getMousePosition().y = e.y;
			getRenderer().defineROI(firstMousePressedPosition, getMousePosition());
		} else {
			// check the difference between the current x and the last x position
			int diffx = newPoint.x - lastMousePressedPosition.x;
			// check the difference between the current y and the last y position
			int diffy = newPoint.y - lastMousePressedPosition.y;
			lastMousePressedPosition = newPoint;

			// double speed = 0.035;

			// Decrease the speed of the translation if z is negative.
			// if ( position.z < 0 ) {
			// speed = speed / Math.abs(position.z) * 2;
			// } else {
			// speed = speed * position.z / 4;
			// }

			moveXYPlan2(diffx, diffy, position.z, getRenderer().getWidth(), getRenderer().getHeight());
		}

		// PrintParam();
	}

	@Override
	public void mouseDown(final org.eclipse.swt.events.MouseEvent arg0) {
		zeroVelocity();
		super.mouseDown(arg0);
	}

	//
	// @Override
	// public void mousePressed(final MouseEvent arg0) {
	// zeroVelocity();
	// super.mousePressed(arg0);
	// }

	// @Override
	// protected boolean canSelectOnRelease(final MouseEvent arg0) {
	// return true;
	// }

	@Override
	protected boolean canSelectOnRelease(final org.eclipse.swt.events.MouseEvent arg0) {
		return true;
	}

	@Override
	public void mouseUp(final org.eclipse.swt.events.MouseEvent arg0) {
		enableInertia = true;
		super.mouseUp(arg0);
	}

	@Override
	public boolean isViewIn2DPlan() {
		return phi > 85 && phi < 95 && theta > -5 && theta < 5;
	}

	@Override
	public void doInertia() {
		if ( enableInertia ) {
			if ( arcBallInertia ) {
				velocityHoriz = velocityHoriz * damping;
				velocityVert = velocityVert * damping;
				theta = theta - velocityHoriz * amplitude;
				phi = phi - velocityVert * amplitude;
				updateCartesianCoordinatesFromAngles();
				if ( Math.abs(velocityHoriz) < 0.01 || Math.abs(velocityVert) < 0.01 ) {
					velocityHoriz = 0;
					velocityVert = 0;
					enableInertia = false;
					arcBallInertia = false;
				}
			}
			if ( moveInertia ) {
				velocityHoriz = velocityHoriz * damping;
				velocityVert = velocityVert * damping;

				moveXYPlan2(velocityHoriz, velocityVert, position.z, getRenderer().getWidth(),
					getRenderer().getHeight());

				if ( Math.abs(velocityHoriz) < 0.01 || Math.abs(velocityVert) < 0.01 ) {
					velocityHoriz = 0;
					velocityVert = 0;
					enableInertia = false;
					moveInertia = false;
				}
			}

		}
	}

	@Override
	public void zeroVelocity() {
		velocityHoriz = 0;
		velocityVert = 0;
	}

}// End of Class CameraArcBall
