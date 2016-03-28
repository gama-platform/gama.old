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

	public CameraArcBall(final JOGLRenderer joglawtglRenderer) {
		super(joglawtglRenderer);
		phi = 90.00;
		theta = 360.00;
		updateCartesianCoordinatesFromAngles();
	}

	protected void updateCartesianCoordinatesFromAngles() {
		theta = theta % 360;
		phi = phi % 360;

		if (phi == 0) {
			phi = 0.1;
		}
		if (phi == 180) {
			phi = 179.9;
		}
		double factorT = theta * Maths.toRad;
		double factorP = phi * Maths.toRad;
		double cosT = FastMath.cos(factorT);
		double sinT = FastMath.sin(factorT);
		double cosP = FastMath.cos(factorP);
		double sinP = FastMath.sin(factorP);
		position.setLocation(radius * sinT * sinP + target.x, radius * cosP + target.y,
			radius * cosT * sinP + target.z);
		
//		if ( phi < 360 && phi > 180 ) {
//			upPosition(-FastMath.sin(theta)*FastMath.cos(phi),FastMath.sin(phi),-FastMath.cos(theta)*FastMath.cos(phi));
//		} else {
//			upPosition(FastMath.sin(theta)*FastMath.cos(phi),-FastMath.sin(phi),FastMath.cos(theta)*FastMath.cos(phi));
//		}
	}

	@Override
	public void updateSphericalCoordinatesFromLocations() {
		double x = position.x - target.x;
		double y = position.y - target.y;
		double z = position.z - target.z;

		radius = FastMath.sqrt(x * x + y * y + z * z);
		theta = Maths.toDeg * FastMath.atan2(x, z);
		phi = Maths.toDeg * FastMath.acos(y / radius);
		
//		if ( phi < 360 && phi > 180 ) {
//			upPosition(-FastMath.sin(theta)*FastMath.cos(phi),FastMath.sin(phi),-FastMath.cos(theta)*FastMath.cos(phi));
//		} else {
//			upPosition(FastMath.sin(theta)*FastMath.cos(phi),-FastMath.sin(phi),FastMath.cos(theta)*FastMath.cos(phi));
//		}
	}
	
	public void translateCameraFromScreenPlan(final double x_translation_in_screen, final double y_translation_in_screen) {
		double theta_vect_z = -FastMath.sin(theta*Maths.toRad);
		double theta_vect_x = FastMath.cos(theta*Maths.toRad);
		double theta_vect_y = 0;
		double theta_vect_ratio = x_translation_in_screen / (theta_vect_x*theta_vect_x + theta_vect_y*theta_vect_y + theta_vect_z*theta_vect_z);
		double theta_vect_x_norm = theta_vect_x * theta_vect_ratio;
		double theta_vect_y_norm = theta_vect_y * theta_vect_ratio;
		double theta_vect_z_norm = theta_vect_z * theta_vect_ratio;
		
		double phi_vect_z = FastMath.cos(theta*Maths.toRad)*FastMath.cos(phi*Maths.toRad);
		double phi_vect_x = FastMath.sin(theta*Maths.toRad)*FastMath.cos(phi*Maths.toRad);
		double phi_vect_y = -FastMath.sin(phi*Maths.toRad); 
		double phi_vect_ratio = y_translation_in_screen / (phi_vect_x*phi_vect_x + phi_vect_y*phi_vect_y + phi_vect_z*phi_vect_z);
		double phi_vect_x_norm = phi_vect_x * phi_vect_ratio;
		double phi_vect_y_norm = phi_vect_y * phi_vect_ratio;
		double phi_vect_z_norm = phi_vect_z * phi_vect_ratio;
		
		double x_translation_in_world = theta_vect_x_norm + phi_vect_x_norm;
		double y_translation_in_world = theta_vect_y_norm + phi_vect_y_norm;
		double z_translation_in_world = theta_vect_z_norm + phi_vect_z_norm;
		
		double zoom = zoomLevel()*4; // the factor 4 makes the translation a bit slower. Maybe a future change of this value to make it more "mathematics" should be better.
		updatePosition(position.x - x_translation_in_world/zoom,position.y - y_translation_in_world/zoom,position.z - z_translation_in_world/zoom);
		lookPosition(target.x - x_translation_in_world/zoom,target.y - y_translation_in_world/zoom,target.z - z_translation_in_world/zoom);
		
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
		getRenderer().currentZRotation = getRenderer().currentZRotation - 10;
	}
	
	@Override
	protected void quickRightTurn() {
		getRenderer().currentZRotation = getRenderer().currentZRotation + 10;
	}
	
	@Override
	protected void quickUpTurn() {
		phi -= 30;
		updateCartesianCoordinatesFromAngles();
	}
	
	@Override
	protected void quickDownTurn() {
		phi += 30;
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
		phi = threeD ? 135.0 : 90.0;
		theta = 360.00;
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public void animate() {
		// First we position the camera ???
		super.animate();
		// And we animate it if the keyboard is invoked
		double translation = 2 * (FastMath.abs(position.z) + 1) / getRenderer().getHeight();
		if ( isForward() ) {
			if ( isShiftKeyDown() ) {
				phi = phi - -get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				translateCameraFromScreenPlan(0.0,-get_keyboardSensivity() * get_sensivity());

			}
		}
		if ( isBackward() ) {
			if ( isShiftKeyDown() ) {
				phi = phi - get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				translateCameraFromScreenPlan(0.0,get_keyboardSensivity() * get_sensivity());
			}
		}
		if ( isStrafeLeft() ) {
			if ( isShiftKeyDown() ) {
				theta = theta - -get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				if ( isAltKeyDown() /*&& isViewIn2DPlan()*/ ) {
					getRenderer().currentZRotation = getRenderer().currentZRotation - 1;
				} else {
					translateCameraFromScreenPlan(-get_keyboardSensivity() * get_sensivity(),0.0);
				}
			}
		}
		if ( isStrafeRight() ) {
			if ( isShiftKeyDown() ) {
				theta = theta - get_keyboardSensivity() * get_sensivity();
				updateCartesianCoordinatesFromAngles();
			} else {
				if ( isAltKeyDown() /*&& isViewIn2DPlan()*/ ) {
					getRenderer().currentZRotation = getRenderer().currentZRotation + 1;
				} else {
					translateCameraFromScreenPlan(get_keyboardSensivity() * get_sensivity(),0.0);
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
		if ( (e.stateMask & SWT.BUTTON_MASK) == 0 ) {getRenderer().stopDrawRotationHelper(); return; }
		Point newPoint = new Point(e.x, e.y);
		if ( isArcBallOn(e) ) {
			int horizMovement = e.x - lastMousePressedPosition.x;
			int vertMovement = e.y - lastMousePressedPosition.y;
			lastMousePressedPosition = newPoint;
			theta = theta - horizMovement * get_sensivity();
			phi = phi - vertMovement * get_sensivity();
			updateCartesianCoordinatesFromAngles();
			getRenderer().startDrawRotationHelper(target);
		}
		else if ( (shift(e) || alt(e)) && isViewIn2DPlan() ) {
			getMousePosition().x = e.x;
			getMousePosition().y = e.y;
			getRenderer().defineROI(firstMousePressedPosition, getMousePosition());
		} else {
			
			int horizMovement = e.x - lastMousePressedPosition.x;
			int vertMovement = e.y - lastMousePressedPosition.y;
			
			translateCameraFromScreenPlan(horizMovement,vertMovement);
			
			lastMousePressedPosition = newPoint;
		}

	}


	@Override
	protected boolean canSelectOnRelease(final org.eclipse.swt.events.MouseEvent arg0) {
		return true;
	}

}// End of Class CameraArcBall
