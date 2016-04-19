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
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gaml.operators.Maths;
import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.JOGLRenderer;

public class CameraArcBall extends AbstractCamera {

	private double radius;

	private final boolean isDrawingRotateHelper = GamaPreferences.DRAW_ROTATE_HELPER.getValue();

	public CameraArcBall(final JOGLRenderer joglawtglRenderer) {
		super(joglawtglRenderer);
		reset();
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
		final double factorT = theta * Maths.toRad;
		final double factorP = phi * Maths.toRad;
		final double cosT = FastMath.cos(factorT);
		final double sinT = FastMath.sin(factorT);
		final double cosP = FastMath.cos(factorP);
		final double sinP = FastMath.sin(factorP);
		position.setLocation(radius * cosT * sinP + target.x, radius * sinT * sinP + target.y,
				radius * cosP + target.z);
	}

	@Override
	public void updateSphericalCoordinatesFromLocations() {
		final double x = position.x - target.x;
		final double y = position.y - target.y;
		final double z = position.z - target.z;

		radius = FastMath.sqrt(x * x + y * y + z * z);
		theta = Maths.toDeg * FastMath.atan2(y, x);
		phi = Maths.toDeg * FastMath.acos(z / radius);
	}

	public void translateCameraFromScreenPlan(final double x_translation_in_screen,
			final double y_translation_in_screen) {

		final double theta_vect_x = -FastMath.sin(theta * Maths.toRad);
		final double theta_vect_y = FastMath.cos(theta * Maths.toRad);
		final double theta_vect_z = 0;
		final double theta_vect_ratio = x_translation_in_screen
				/ (theta_vect_x * theta_vect_x + theta_vect_y * theta_vect_y + theta_vect_z * theta_vect_z);
		final double theta_vect_x_norm = theta_vect_x * theta_vect_ratio;
		final double theta_vect_y_norm = theta_vect_y * theta_vect_ratio;
		final double theta_vect_z_norm = theta_vect_z * theta_vect_ratio;

		upPosition(
				-FastMath.cos(theta * Maths.toRad) * FastMath.cos(phi * Maths.toRad)
						* FastMath.cos(upVectorAngle * Maths.toRad)
						- FastMath.sin(theta * Maths.toRad) * FastMath.sin(upVectorAngle * Maths.toRad),
				-FastMath.sin(theta * Maths.toRad) * FastMath.cos(phi * Maths.toRad)
						* FastMath.cos(upVectorAngle * Maths.toRad
								+ FastMath.cos(theta * Maths.toRad) * FastMath.sin(upVectorAngle * Maths.toRad)),
				FastMath.sin(phi * Maths.toRad) * FastMath.cos(upVectorAngle * Maths.toRad));

		final double phi_vect_x = FastMath.cos(theta * Maths.toRad) * FastMath.cos(phi * Maths.toRad);
		final double phi_vect_y = FastMath.sin(theta * Maths.toRad) * FastMath.cos(phi * Maths.toRad);
		final double phi_vect_z = -FastMath.sin(phi * Maths.toRad);
		final double phi_vect_ratio = y_translation_in_screen
				/ (phi_vect_x * phi_vect_x + phi_vect_y * phi_vect_y + phi_vect_z * phi_vect_z);
		final double phi_vect_x_norm = phi_vect_x * phi_vect_ratio;
		final double phi_vect_y_norm = phi_vect_y * phi_vect_ratio;
		final double phi_vect_z_norm = phi_vect_z * phi_vect_ratio;

		final double x_translation_in_world = theta_vect_x_norm + phi_vect_x_norm;
		final double y_translation_in_world = theta_vect_y_norm + phi_vect_y_norm;
		final double z_translation_in_world = theta_vect_z_norm + phi_vect_z_norm;

		// double zoom = zoomLevel()*4; // the factor 4 makes the translation a
		// bit slower. Maybe a future change of this value to make it more
		// "mathematics" should be better.
		updatePosition(position.x - x_translation_in_world * radius / 1000,
				position.y - y_translation_in_world * radius / 1000,
				position.z - z_translation_in_world * radius / 1000);
		lookPosition(target.x - x_translation_in_world * radius / 1000,
				target.y - y_translation_in_world * radius / 1000, target.z - z_translation_in_world * radius / 1000);

		updateSphericalCoordinatesFromLocations();
	}

	@Override
	protected void resetPivot() {
		final LayeredDisplayData data = getRenderer().data;
		final double envWidth = data.getEnvWidth();
		final double envHeight = data.getEnvHeight();
		final double translate_x = target.x - envWidth / 2;
		final double translate_y = target.y + envHeight / 2;
		final double translate_z = target.z;
		target.setLocation(envWidth / 2, -envHeight / 2, 0);
		position.setLocation(position.x - translate_x, position.y - translate_y, position.z - translate_z);
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
				phi = 360 - phi - 30;
				flipped = false;
				theta += 180;
			}
		} else {
			if (phi - 30 > 0)
				phi -= 30;
			else {
				phi = -phi + 30;
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
				phi = -phi + 30;
				flipped = false;
				theta += 180;
			}
		} else {
			if (phi + 30 < 180)
				phi += 30;
			else {
				phi = 360 - phi - 30;
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
		final LayeredDisplayData data = getRenderer().data;
		final double envWidth = data.getEnvWidth();
		final double envHeight = data.getEnvHeight();
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
		if (cameraInteraction) {
			// And we animate it if the keyboard is invoked
			final double translation = 2 * (FastMath.abs(position.z) + 1) / getRenderer().getHeight();
			if (isForward()) {
				if (ctrlPressed) {
					if (flipped) {
						if (phi - getKeyboardSensivity() * getSensivity() > 0)
							phi -= getKeyboardSensivity() * getSensivity();
						else {
							phi = -phi + getKeyboardSensivity() * getSensivity();
							flipped = false;
							theta += 180;
						}
					} else {
						if (phi + getKeyboardSensivity() * getSensivity() < 180)
							phi += getKeyboardSensivity() * getSensivity();
						else {
							phi = 360 - phi - getKeyboardSensivity() * getSensivity();
							flipped = true;
							theta += 180;
						}
					}
					updateCartesianCoordinatesFromAngles();
				} else {
					if (flipped)
						translateCameraFromScreenPlan(0.0, getKeyboardSensivity()
								* getSensivity() /** radius/1000.0 */
						);
					else
						translateCameraFromScreenPlan(0.0, -getKeyboardSensivity()
								* getSensivity() /** radius/1000.0 */
						);

				}
			}
			if (isBackward()) {
				if (ctrlPressed) {
					if (flipped) {
						if (phi + getKeyboardSensivity() * getSensivity() < 180)
							phi += getKeyboardSensivity() * getSensivity();
						else {
							phi = 360 - phi - getKeyboardSensivity() * getSensivity();
							flipped = false;
							theta += 180;
						}
					} else {
						if (phi - getKeyboardSensivity() * getSensivity() > 0)
							phi -= getKeyboardSensivity() * getSensivity();
						else {
							phi = -phi + getKeyboardSensivity() * getSensivity();
							flipped = true;
							theta += 180;
						}
					}
					updateCartesianCoordinatesFromAngles();
				} else {
					if (flipped)
						translateCameraFromScreenPlan(0.0, -getKeyboardSensivity()
								* getSensivity() /** radius/1000.0 */
						);
					else
						translateCameraFromScreenPlan(0.0, getKeyboardSensivity()
								* getSensivity() /** radius/1000.0 */
						);
				}
			}
			if (isStrafeLeft()) {
				if (ctrlPressed) {
					if (flipped)
						theta = theta + -getKeyboardSensivity() * getSensivity();
					else
						theta = theta - -getKeyboardSensivity() * getSensivity();
					updateCartesianCoordinatesFromAngles();
				} else {
					if (flipped)
						translateCameraFromScreenPlan(
								getKeyboardSensivity()
										* getSensivity() /** radius/1000.0 */
								, 0.0);
					else
						translateCameraFromScreenPlan(
								-getKeyboardSensivity()
										* getSensivity() /** radius/1000.0 */
								, 0.0);
				}
			}
			if (isStrafeRight()) {
				if (ctrlPressed) {
					if (flipped)
						theta = theta + getKeyboardSensivity() * getSensivity();
					else
						theta = theta - getKeyboardSensivity() * getSensivity();
					updateCartesianCoordinatesFromAngles();
				} else {
					if (flipped)
						translateCameraFromScreenPlan(
								-getKeyboardSensivity()
										* getSensivity() /** radius/1000.0 */
								, 0.0);
					else
						translateCameraFromScreenPlan(
								getKeyboardSensivity()
										* getSensivity() /** radius/1000.0 */
								, 0.0);
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
		final double step = radius != 0d ? radius / 10d * GamaPreferences.OPENGL_ZOOM.getValue() : 0.1d;
		radius = radius + (in ? -step : step);
		getRenderer().data.setZoomLevel(zoomLevel());
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public void zoomRoi(final Envelope3D env) {
		final int width = (int) env.getWidth();
		final int height = (int) env.getHeight();
		radius = 1.5 * (width > height ? width : height);
		// y is already negated
		target.setLocation(env.centre());
		updateCartesianCoordinatesFromAngles();
		// getRenderer().cancelROI();
	}

	@Override
	public void zoomFocus(final IShape shape) {
		final ILocation p = shape.getLocation();
		final double extent = shape.getEnvelope().maxExtent();
		if (extent == 0) {
			radius = p.getZ() + getRenderer().getMaxEnvDim() / 10;
		} else {
			radius = extent * 1.5;
		}
		// y is NOT negated in IShapes
		target.setLocation(p.getCentroid().yNegated());
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public void internalMouseMove(final org.eclipse.swt.events.MouseEvent e) {

		super.internalMouseMove(e);
		if ((e.stateMask & SWT.BUTTON_MASK) == 0) {
			return;
		}
		final Point newPoint = new Point(e.x, e.y);
		if (ctrl(e)) {
			final int horizMovement = e.x - lastMousePressedPosition.x;
			final int vertMovement = e.y - lastMousePressedPosition.y;
			// if (flipped) {
			// horizMovement = -horizMovement;
			// vertMovement = -vertMovement;
			// }

			final double horizMovement_real = horizMovement * FastMath.cos(upVectorAngle * Maths.toRad)
					- vertMovement * FastMath.sin(upVectorAngle * Maths.toRad);
			final double vertMovement_real = vertMovement * FastMath.cos(upVectorAngle * Maths.toRad)
					+ horizMovement * FastMath.sin(upVectorAngle * Maths.toRad);

			lastMousePressedPosition = newPoint;
			theta = theta - horizMovement_real * getSensivity();

			if (flipped) {
				if (vertMovement_real > 0) {
					// down drag : phi increase
					if (phi + vertMovement_real * getSensivity() < 180)
						phi += vertMovement_real * getSensivity();
					else {
						phi = +360 + phi - vertMovement_real * getSensivity();
						flipped = !flipped;
						theta += 180;
					}
				} else {
					// up drag : phi decrease
					if (phi - -vertMovement_real * getSensivity() > 0)
						phi -= -vertMovement_real * getSensivity();
					else {
						phi = -phi + -vertMovement_real * getSensivity();
						flipped = !flipped;
						theta += 180;
					}
				}
			} else {
				if (vertMovement_real > 0) {
					// down drag : phi decrease
					if (phi - vertMovement_real * getSensivity() > 0)
						phi -= vertMovement_real * getSensivity();
					else {
						phi = -phi + vertMovement_real * getSensivity();
						flipped = !flipped;
						theta += 180;
					}
				} else {
					// up drag : phi increase
					if (phi + -vertMovement_real * getSensivity() < 180)
						phi += -vertMovement_real * getSensivity();
					else {
						phi = +360 + phi - vertMovement_real * getSensivity();
						flipped = !flipped;
						theta += 180;
					}
				}
			}

			// phi = phi - vertMovement_real * get_sensivity();
			updateCartesianCoordinatesFromAngles();
		} else if (shiftPressed && isViewInXYPlan()) {
			getMousePosition().x = e.x;
			getMousePosition().y = e.y;
			getRenderer().defineROI(firstMousePressedPosition, getMousePosition());
		} else if (getRenderer().mouseInROI(getMousePosition())) {
			GamaPoint p = getRenderer().getRealWorldPointFromWindowPoint(getMousePosition());
			p = p.minus(getRenderer().getROIEnvelope().centre());
			getRenderer().getROIEnvelope().translate(p.x, p.y);

		} else {

			int horizMovement = e.x - lastMousePressedPosition.x;
			int vertMovement = e.y - lastMousePressedPosition.y;
			if (flipped) {
				horizMovement = -horizMovement;
				vertMovement = -vertMovement;
			}

			final double horizMovement_real = horizMovement * FastMath.cos(upVectorAngle * Maths.toRad)
					- vertMovement * FastMath.sin(upVectorAngle * Maths.toRad);
			final double vertMovement_real = vertMovement * FastMath.cos(upVectorAngle * Maths.toRad)
					+ horizMovement * FastMath.sin(upVectorAngle * Maths.toRad);

			translateCameraFromScreenPlan(horizMovement_real, vertMovement_real);

			lastMousePressedPosition = newPoint;
		}

	}

	@Override
	protected boolean canSelectOnRelease(final org.eclipse.swt.events.MouseEvent arg0) {
		return true;
	}

	@Override
	protected void drawRotationHelper() {
		if (isDrawingRotateHelper) {
			if (ctrlPressed) {
				getRenderer().startDrawRotationHelper(target);
			} else {
				getRenderer().stopDrawRotationHelper();
			}
		}
	}

}// End of Class CameraArcBall
