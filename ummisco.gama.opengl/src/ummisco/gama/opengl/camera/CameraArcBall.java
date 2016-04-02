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
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayData;
import msi.gaml.operators.Maths;
import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.JOGLRenderer;

public class CameraArcBall extends AbstractCamera {

	private double radius;
	
	private boolean mouse_left_pressed = false;
	private boolean ctrl_pressed = false;
	private boolean shift_pressed = false;
	private boolean alt_pressed = false;
	
	private boolean isDrawingRotateHelper = GamaPreferences.DRAW_ROTATE_HELPER.getValue();

	public CameraArcBall(final JOGLRenderer joglawtglRenderer) {
		super(joglawtglRenderer);
		phi = 0.00;
		theta = -90.00;
		upVectorAngle = 0.0;
		updateCartesianCoordinatesFromAngles();
	}

	protected void updateCartesianCoordinatesFromAngles() {
		theta = theta % 360;
		phi = phi % 360;

		if (phi <= 0) {
			phi = 0.1;
		}
		if (phi >= 180) {
			phi = 179.9;
		}
		double factorT = theta * Maths.toRad;
		double factorP = phi * Maths.toRad;
		double cosT = FastMath.cos(factorT);
		double sinT = FastMath.sin(factorT);
		double cosP = FastMath.cos(factorP);
		double sinP = FastMath.sin(factorP);
		position.setLocation(radius * cosT * sinP + target.x, radius * sinT * sinP + target.y,
			radius * cosP + target.z);
	}

	@Override
	public void updateSphericalCoordinatesFromLocations() {
		double x = position.x - target.x;
		double y = position.y - target.y;
		double z = position.z - target.z;

		radius = FastMath.sqrt(x * x + y * y + z * z);
		theta = Maths.toDeg * FastMath.atan2(y, x);
		phi = Maths.toDeg * FastMath.acos(z / radius);
	}
	
	public void translateCameraFromScreenPlan(final double x_translation_in_screen, final double y_translation_in_screen) {

		double theta_vect_x = -FastMath.sin(theta*Maths.toRad);
		double theta_vect_y = FastMath.cos(theta*Maths.toRad);
		double theta_vect_z = 0;
		double theta_vect_ratio = x_translation_in_screen / (theta_vect_x*theta_vect_x + theta_vect_y*theta_vect_y + theta_vect_z*theta_vect_z);
		double theta_vect_x_norm = theta_vect_x * theta_vect_ratio;
		double theta_vect_y_norm = theta_vect_y * theta_vect_ratio;
		double theta_vect_z_norm = theta_vect_z * theta_vect_ratio;
		
		upPosition(-FastMath.cos(theta*Maths.toRad)*FastMath.cos(phi*Maths.toRad)*FastMath.cos(upVectorAngle*Maths.toRad) - FastMath.sin(theta*Maths.toRad)*FastMath.sin(upVectorAngle*Maths.toRad),
				-FastMath.sin(theta*Maths.toRad)*FastMath.cos(phi*Maths.toRad)*FastMath.cos(upVectorAngle*Maths.toRad + FastMath.cos(theta*Maths.toRad)*FastMath.sin(upVectorAngle*Maths.toRad)),
				FastMath.sin(phi*Maths.toRad)*FastMath.cos(upVectorAngle*Maths.toRad));
		
		double phi_vect_x = FastMath.cos(theta*Maths.toRad)*FastMath.cos(phi*Maths.toRad);
		double phi_vect_y = FastMath.sin(theta*Maths.toRad)*FastMath.cos(phi*Maths.toRad);
		double phi_vect_z = -FastMath.sin(phi*Maths.toRad); 
		double phi_vect_ratio = y_translation_in_screen / (phi_vect_x*phi_vect_x + phi_vect_y*phi_vect_y + phi_vect_z*phi_vect_z);
		double phi_vect_x_norm = phi_vect_x * phi_vect_ratio;
		double phi_vect_y_norm = phi_vect_y * phi_vect_ratio;
		double phi_vect_z_norm = phi_vect_z * phi_vect_ratio;
		
		double x_translation_in_world = theta_vect_x_norm + phi_vect_x_norm;
		double y_translation_in_world = theta_vect_y_norm + phi_vect_y_norm;
		double z_translation_in_world = theta_vect_z_norm + phi_vect_z_norm;
		
		
		//double zoom = zoomLevel()*4; // the factor 4 makes the translation a bit slower. Maybe a future change of this value to make it more "mathematics" should be better.
		updatePosition(position.x - x_translation_in_world*radius/1000,position.y - y_translation_in_world*radius/1000,position.z - z_translation_in_world*radius/1000);
		lookPosition(target.x - x_translation_in_world*radius/1000,target.y - y_translation_in_world*radius/1000,target.z - z_translation_in_world*radius/1000);
		
		updateSphericalCoordinatesFromLocations();
	}
	
	@Override
	protected void resetPivot() {
		LayeredDisplayData data = getRenderer().data;
		double envWidth = data.getEnvWidth();
		double envHeight = data.getEnvHeight();
		double translate_x = target.x - envWidth / 2;
		double translate_y = target.y + envHeight / 2;
		double translate_z = target.z;
		target.setLocation(envWidth / 2, -envHeight / 2, 0);
		position.setLocation(position.x-translate_x,position.y-translate_y,position.z-translate_z);
		updateSphericalCoordinatesFromLocations();
	}
	
	@Override
	protected void quickLeftTurn() {
		theta -= 30;
		updateCartesianCoordinatesFromAngles();
	}
	
	@Override
	protected void quickRightTurn() {
		theta += 30;
		updateCartesianCoordinatesFromAngles();
	}
	
	@Override
	protected void quickUpTurn() {
		if (flipped) {
			if (phi + 30 < 180)
				phi += 30;
			else {
				phi = 360-phi - 30;
				flipped = false;
				theta += 180;
			}
		}
		else {
			if (phi - 30 > 0)
				phi -= 30;
			else {
				phi = - phi + 30;
				flipped = true;
				theta += 180;
			}
		}
		updateCartesianCoordinatesFromAngles();
	}
	
	@Override
	protected void quickDownTurn() {
		if (flipped) {
			if (phi - 30 > 0)
				phi -= 30;
			else {
				phi = - phi + 30;
				flipped = false;
				theta += 180;
			}
		}
		else {
			if (phi + 30 < 180)
				phi += 30;
			else {
				phi = 360-phi - 30;
				flipped = true;
				theta += 180;
			}
		}
		updateCartesianCoordinatesFromAngles();
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
	public void reset() {
		LayeredDisplayData data = getRenderer().data;
		double envWidth = data.getEnvWidth();
		double envHeight = data.getEnvHeight();
		final boolean threeD = data.isOutput3D();
		radius = getRenderer().getMaxEnvDim() * INIT_Z_FACTOR;
		target.setLocation(envWidth / 2, -envHeight / 2, 0);
		phi = threeD ? 135.0 : 0.0;
		theta = -90.00;
		upVectorAngle = 0.0;
		flipped = false;
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public void animate() {
		// First we position the camera ???
		super.animate();
		// And we animate it if the keyboard is invoked
		double translation = 2 * (FastMath.abs(position.z) + 1) / getRenderer().getHeight();
		if ( isForward() ) {
			if ( ctrl_pressed ) {
				if (flipped) {
					if (phi - get_keyboardSensivity() * get_sensivity() > 0)
						phi -= get_keyboardSensivity() * get_sensivity();
					else {
						phi = - phi + get_keyboardSensivity() * get_sensivity();
						flipped = false;
						theta += 180;
					}
				}
				else {
					if (phi + get_keyboardSensivity() * get_sensivity() < 180)
						phi += get_keyboardSensivity() * get_sensivity();
					else {
						phi = 360-phi - get_keyboardSensivity() * get_sensivity();
						flipped = true;
						theta += 180;
					}
				}
				updateCartesianCoordinatesFromAngles();
			} else {
				if (flipped)
					translateCameraFromScreenPlan(0.0,get_keyboardSensivity() * get_sensivity() /**radius/1000.0*/);
				else
					translateCameraFromScreenPlan(0.0,-get_keyboardSensivity() * get_sensivity() /**radius/1000.0*/);

			}
		}
		if ( isBackward() ) {
			if ( ctrl_pressed ) {
				if (flipped) {
					if (phi + get_keyboardSensivity() * get_sensivity() < 180)
						phi += get_keyboardSensivity() * get_sensivity();
					else {
						phi = 360-phi - get_keyboardSensivity() * get_sensivity();
						flipped = false;
						theta += 180;
					}
				}
				else {
					if (phi - get_keyboardSensivity() * get_sensivity() > 0)
						phi -= get_keyboardSensivity() * get_sensivity();
					else {
						phi = - phi + get_keyboardSensivity() * get_sensivity();
						flipped = true;
						theta += 180;
					}
				}
				updateCartesianCoordinatesFromAngles();
			} else {
				if (flipped)
					translateCameraFromScreenPlan(0.0,-get_keyboardSensivity() * get_sensivity() /**radius/1000.0*/);
				else
					translateCameraFromScreenPlan(0.0,get_keyboardSensivity() * get_sensivity() /**radius/1000.0*/);
			}
		}
		if ( isStrafeLeft() ) {
			if ( ctrl_pressed ) {
				if (flipped)
					theta = theta + -get_keyboardSensivity() * get_sensivity();
				else
					theta = theta - -get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				if (flipped)
					translateCameraFromScreenPlan(get_keyboardSensivity() * get_sensivity() /**radius/1000.0*/,0.0);
				else
					translateCameraFromScreenPlan(-get_keyboardSensivity() * get_sensivity() /**radius/1000.0*/,0.0);
			}
		}
		if ( isStrafeRight() ) {
			if ( ctrl_pressed ) {
				if (flipped)
					theta = theta + get_keyboardSensivity() * get_sensivity();
				else
					theta = theta - get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				if (flipped)
					translateCameraFromScreenPlan(- get_keyboardSensivity() * get_sensivity() /**radius/1000.0*/,0.0);
				else
					translateCameraFromScreenPlan(get_keyboardSensivity() * get_sensivity() /**radius/1000.0*/,0.0);
			}
		}
	}

	@Override
	public Double zoomLevel() {
		return getRenderer().getMaxEnvDim() * INIT_Z_FACTOR / radius;
	}

	@Override
	public void zoom(final boolean in) {
		double step = radius != 0d ? radius / 10d * GamaPreferences.OPENGL_ZOOM.getValue() : 0.1d;
		radius = radius + (in ? -step : step);
		getRenderer().data.setZoomLevel(zoomLevel());
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
	public void zoomFocus(final IShape shape) {
		ILocation p = shape.getLocation();
		double extent = shape.getEnvelope().maxExtent();
		final double zPos;
		if ( extent == 0 ) {
			zPos = p.getZ() + getRenderer().getMaxEnvDim() / 10;
		} else {
			zPos = extent * 1.5;
		}
		radius = zPos;
		updateCartesianCoordinatesFromAngles();
		updatePosition(p.getX(), -p.getY(), zPos);
		lookPosition(p.getX(), -p.getY(), 0);
	}

	@Override
	public void mouseMove(final org.eclipse.swt.events.MouseEvent e) {
		super.mouseMove(e);
		if ( (e.stateMask & SWT.BUTTON_MASK) == 0 ) {return; }
		Point newPoint = new Point(e.x, e.y);
		if ( isArcBallOn(e) ) {
			int horizMovement = e.x - lastMousePressedPosition.x;
			int vertMovement = e.y - lastMousePressedPosition.y;
//			if (flipped) {
//				horizMovement = -horizMovement;
//				vertMovement = -vertMovement;
//			}
			
			double horizMovement_real = horizMovement*FastMath.cos(upVectorAngle*Maths.toRad) - vertMovement*FastMath.sin(upVectorAngle*Maths.toRad);
			double vertMovement_real = vertMovement*FastMath.cos(upVectorAngle*Maths.toRad) + horizMovement*FastMath.sin(upVectorAngle*Maths.toRad);

			lastMousePressedPosition = newPoint;
			theta = theta - horizMovement_real * get_sensivity();
			
			if (flipped) {
				if (vertMovement_real > 0) {
					// down drag : phi increase
					if (phi + vertMovement_real * get_sensivity() < 180)
						phi += vertMovement_real * get_sensivity();
					else {
						phi = +360+phi - vertMovement_real * get_sensivity();
						flipped = !flipped;
						theta += 180;
					}
				}
				else {
					// up drag : phi decrease
					if (phi - -vertMovement_real * get_sensivity() > 0)
						phi -= -vertMovement_real * get_sensivity();		
					else {
						phi = - phi + -vertMovement_real * get_sensivity();
						flipped = !flipped;
						theta += 180;
					}
				}
			}
			else {
				if (vertMovement_real > 0) {
					// down drag : phi decrease
					if (phi - vertMovement_real * get_sensivity() > 0)
						phi -= vertMovement_real * get_sensivity();		
					else {
						phi = - phi + vertMovement_real * get_sensivity();
						flipped = !flipped;
						theta += 180;
					}
				}
				else {
					// up drag : phi increase
					if (phi + -vertMovement_real * get_sensivity() < 180)
						phi += -vertMovement_real * get_sensivity();
					else {
						phi = +360+phi - vertMovement_real * get_sensivity();
						flipped = !flipped;
						theta += 180;
					}
				}
			}			
			
			//phi = phi - vertMovement_real * get_sensivity();
			updateCartesianCoordinatesFromAngles();
		}
		else if ( (shift_pressed || alt_pressed ) && isViewInXYPlan() ) {
			getMousePosition().x = e.x;
			getMousePosition().y = e.y;
			getRenderer().defineROI(firstMousePressedPosition, getMousePosition());
		} else {
			
			int horizMovement = e.x - lastMousePressedPosition.x;
			int vertMovement = e.y - lastMousePressedPosition.y;
			if (flipped) {
				horizMovement = -horizMovement;
				vertMovement = -vertMovement;
			}
			
			double horizMovement_real = horizMovement*FastMath.cos(upVectorAngle*Maths.toRad) - vertMovement*FastMath.sin(upVectorAngle*Maths.toRad);
			double vertMovement_real = vertMovement*FastMath.cos(upVectorAngle*Maths.toRad) + horizMovement*FastMath.sin(upVectorAngle*Maths.toRad);
			
			translateCameraFromScreenPlan(horizMovement_real,vertMovement_real);
			
			lastMousePressedPosition = newPoint;
		}

	}


	@Override
	protected boolean canSelectOnRelease(final org.eclipse.swt.events.MouseEvent arg0) {
		return true;
	}
	
	@Override
	protected void Mouse_left_pressed(boolean value) {
		mouse_left_pressed = value;
		drawRotationHelper();
	}
	
	@Override
	protected void Ctrl_pressed(boolean value) {
		ctrl_pressed = value;
		drawRotationHelper();
	}
	
	@Override
	protected void Shift_pressed(boolean value) {
		shift_pressed = value;
		drawRotationHelper();
	}
	
	@Override
	protected void Alt_pressed(boolean value) {
		alt_pressed = value;
		drawRotationHelper();
	}
	
	@Override
	protected void drawRotationHelper() {
		if (isDrawingRotateHelper) {
			if (ctrl_pressed) {
				getRenderer().startDrawRotationHelper(target);
			}
			else {
				getRenderer().stopDrawRotationHelper();
			}
		}
	}

}// End of Class CameraArcBall
