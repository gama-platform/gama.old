/*********************************************************************************************
 *
 * 'CameraArcBall.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.camera;

import java.awt.Point;

import org.eclipse.swt.SWT;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gaml.operators.Maths;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.ui.bindings.GamaKeyBindings;

public class CameraArcBall extends AbstractCamera {

	private double distance;

	private final boolean isDrawingRotateHelper = GamaPreferences.Displays.DRAW_ROTATE_HELPER.getValue();

	public CameraArcBall(final Abstract3DRenderer renderer) {
		super(renderer);
	}

	private void updateCartesianCoordinatesFromAngles() {
		theta = theta % 360;
		phi = phi % 360;

		if (phi <= 0) {
			phi = 0.001;
		} else if (phi >= 180) {
			phi = 179.999;
		}
		final double factorT = theta * Maths.toRad;
		final double factorP = phi * Maths.toRad;
		final double cosT = Math.cos(factorT);
		final double sinT = Math.sin(factorT);
		final double cosP = Math.cos(factorP);
		final double sinP = Math.sin(factorP);
		setPosition(distance * cosT * sinP + target.x, distance * sinT * sinP + target.y, distance * cosP + target.z);
	}

	@Override
	public void updateSphericalCoordinatesFromLocations() {

		final GamaPoint p = position.minus(target);
		distance = p.norm();

		theta = Maths.toDeg * Math.atan2(p.y, p.x);
		// See issue on camera_pos
		if (theta == 0) {
			theta = -90;
		}
		phi = Maths.toDeg * Math.acos(p.z / distance);
	}

	private void translateCameraFromScreenPlan(final double x_translation_in_screen,
			final double y_translation_in_screen) {

		final double theta_vect_x = -Math.sin(theta * Maths.toRad);
		final double theta_vect_y = Math.cos(theta * Maths.toRad);
		final double theta_vect_z = 0;
		final double theta_vect_ratio = x_translation_in_screen
				/ (theta_vect_x * theta_vect_x + theta_vect_y * theta_vect_y + theta_vect_z * theta_vect_z);
		final double theta_vect_x_norm = theta_vect_x * theta_vect_ratio;
		final double theta_vect_y_norm = theta_vect_y * theta_vect_ratio;
		final double theta_vect_z_norm = theta_vect_z * theta_vect_ratio;

		setUpVector(-Math.cos(theta * Maths.toRad) * Math.cos(phi * Maths.toRad),
				-Math.sin(theta * Maths.toRad) * Math.cos(phi * Maths.toRad), Math.sin(phi * Maths.toRad));

		final double phi_vect_x = Math.cos(theta * Maths.toRad) * Math.cos(phi * Maths.toRad);
		final double phi_vect_y = Math.sin(theta * Maths.toRad) * Math.cos(phi * Maths.toRad);
		final double phi_vect_z = -Math.sin(phi * Maths.toRad);
		final double phi_vect_ratio =
				y_translation_in_screen / (phi_vect_x * phi_vect_x + phi_vect_y * phi_vect_y + phi_vect_z * phi_vect_z);
		final double phi_vect_x_norm = phi_vect_x * phi_vect_ratio;
		final double phi_vect_y_norm = phi_vect_y * phi_vect_ratio;
		final double phi_vect_z_norm = phi_vect_z * phi_vect_ratio;

		final double x_translation_in_world = theta_vect_x_norm + phi_vect_x_norm;
		final double y_translation_in_world = theta_vect_y_norm + phi_vect_y_norm;
		final double z_translation_in_world = theta_vect_z_norm + phi_vect_z_norm;

		setPosition(position.x - x_translation_in_world * distance / 1000,
				position.y - y_translation_in_world * distance / 1000,
				position.z - z_translation_in_world * distance / 1000);
		setTarget(target.x - x_translation_in_world * distance / 1000, target.y - y_translation_in_world * distance / 1000,
				target.z - z_translation_in_world * distance / 1000);

		updateSphericalCoordinatesFromLocations();
	}

	@Override
	protected void resetPivot() {
		final LayeredDisplayData data = getRenderer().data;
		final double envWidth = data.getEnvWidth();
		final double envHeight = data.getEnvHeight();
		final double translate_x = target.x - envWidth / 2d;
		final double translate_y = target.y + envHeight / 2d;
		final double translate_z = target.z;
		setTarget(envWidth / 2d, -envHeight / 2d, 0);
		setPosition(position.x - translate_x, position.y - translate_y, position.z - translate_z);
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
			if (phi + 30 < 180) {
				phi += 30;
			} else {
				phi = 360 - phi - 30;
				flipped = false;
				theta += 180;
			}
		} else {
			if (phi - 30 > 0) {
				phi -= 30;
			} else {
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
			if (phi - 30 > 0) {
				phi -= 30;
			} else {
				phi = -phi + 30;
				flipped = false;
				theta += 180;
			}
		} else {
			if (phi + 30 < 180) {
				phi += 30;
			} else {
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
	public void initialize() {
		final LayeredDisplayData data = getRenderer().data;
		flipped = false;
		initialized = false;
		if (initialPosition == null) {
			if (data.isCameraPosDefined()) {
				updatePosition();
				if (data.isCameraLookAtDefined()) {
					updateTarget();
				} else {
					final double envWidth = data.getEnvWidth();
					final double envHeight = data.getEnvHeight();
					distance = getRenderer().getMaxEnvDim() * INIT_Z_FACTOR;
					setTarget(envWidth / 2d, -envHeight / 2d, 0);
					phi = 0;
					theta = -90.00;
				}
				if (data.isCameraUpVectorDefined()) {
					updateOrientation();
				}
				updateSphericalCoordinatesFromLocations();
			} else {
				final double envWidth = data.getEnvWidth();
				final double envHeight = data.getEnvHeight();
				distance = getRenderer().getMaxEnvDim() * INIT_Z_FACTOR;
				setTarget(envWidth / 2d, -envHeight / 2d, 0);
				phi = 0;
				theta = -90.00;
				updateCartesianCoordinatesFromAngles();
				// update();
			}
			initialPosition = new GamaPoint(position);
			initialTarget = new GamaPoint(target);
			initialUpVector = new GamaPoint(upVector);
		} else {
			data.setCameraPos(initialPosition);
			data.setCameraLookPos(initialTarget);
			data.setCameraUpVector(initialUpVector, true);
			// update();
		}
	}

	// @Override
	// public void reset() {
	// final LayeredDisplayData data = getRenderer().data;
	// final double envWidth = data.getEnvWidth();
	// final double envHeight = data.getEnvHeight();
	// // final boolean threeD = data.isOutput3D();
	// radius = getRenderer().getMaxEnvDim() * INIT_Z_FACTOR;
	// setTarget(envWidth / 2, -envHeight / 2, 0);
	// phi = true ? 135.0 : 0.0;
	// theta = -90.00;
	// flipped = false;
	// updateCartesianCoordinatesFromAngles();
	// // initialized = false;
	// update();
	//
	// }

	@Override
	public void animate() {

		if (cameraInteraction) {
			// And we animate it if the keyboard is invoked
			if (isForward()) {
				if (ctrlPressed) {
					if (flipped) {
						if (phi - getKeyboardSensivity() * getSensivity() > 0) {
							phi -= getKeyboardSensivity() * getSensivity();
						} else {
							phi = -phi + getKeyboardSensivity() * getSensivity();
							flipped = false;
							theta += 180;
						}
					} else {
						if (phi + getKeyboardSensivity() * getSensivity() < 180) {
							phi += getKeyboardSensivity() * getSensivity();
						} else {
							phi = 360 - phi - getKeyboardSensivity() * getSensivity();
							flipped = true;
							theta += 180;
						}
					}
					updateCartesianCoordinatesFromAngles();
				} else {
					if (flipped) {
						translateCameraFromScreenPlan(0.0, getKeyboardSensivity() * getSensivity() /** radius/1000.0 */
						);
					} else {
						translateCameraFromScreenPlan(0.0, -getKeyboardSensivity() * getSensivity() /** radius/1000.0 */
						);
					}

				}
			}
			if (isBackward()) {
				if (ctrlPressed) {
					if (flipped) {
						if (phi + getKeyboardSensivity() * getSensivity() < 180) {
							phi += getKeyboardSensivity() * getSensivity();
						} else {
							phi = 360 - phi - getKeyboardSensivity() * getSensivity();
							flipped = false;
							theta += 180;
						}
					} else {
						if (phi - getKeyboardSensivity() * getSensivity() > 0) {
							phi -= getKeyboardSensivity() * getSensivity();
						} else {
							phi = -phi + getKeyboardSensivity() * getSensivity();
							flipped = true;
							theta += 180;
						}
					}
					updateCartesianCoordinatesFromAngles();
				} else {
					if (flipped) {
						translateCameraFromScreenPlan(0.0, -getKeyboardSensivity() * getSensivity() /** radius/1000.0 */
						);
					} else {
						translateCameraFromScreenPlan(0.0, getKeyboardSensivity() * getSensivity() /** radius/1000.0 */
						);
					}
				}
			}
			if (isStrafeLeft()) {
				if (ctrlPressed) {
					if (flipped) {
						theta = theta + -getKeyboardSensivity() * getSensivity();
					} else {
						theta = theta - -getKeyboardSensivity() * getSensivity();
					}
					updateCartesianCoordinatesFromAngles();
				} else {
					if (flipped) {
						translateCameraFromScreenPlan(getKeyboardSensivity() * getSensivity() /** radius/1000.0 */
								, 0.0);
					} else {
						translateCameraFromScreenPlan(-getKeyboardSensivity() * getSensivity() /** radius/1000.0 */
								, 0.0);
					}
				}
			}
			if (isStrafeRight()) {
				if (ctrlPressed) {
					if (flipped) {
						theta = theta + getKeyboardSensivity() * getSensivity();
					} else {
						theta = theta - getKeyboardSensivity() * getSensivity();
					}
					updateCartesianCoordinatesFromAngles();
				} else {
					if (flipped) {
						translateCameraFromScreenPlan(-getKeyboardSensivity() * getSensivity() /** radius/1000.0 */
								, 0.0);
					} else {
						translateCameraFromScreenPlan(getKeyboardSensivity() * getSensivity() /** radius/1000.0 */
								, 0.0);
					}
				}
			}
		}
		// First we position the camera ???
		super.animate();
	}

	@Override
	public Double zoomLevel() {
		return getRenderer().getMaxEnvDim() * INIT_Z_FACTOR / distance;
	}

	@Override
	public void zoom(final double level) {
		distance = getRenderer().getMaxEnvDim() * INIT_Z_FACTOR / level;
		updateCartesianCoordinatesFromAngles();
	}

	@Override
	public void zoom(final boolean in) {
		if (keystoneMode) { return; }
		final double step = distance != 0d ? distance / 10d * GamaPreferences.Displays.OPENGL_ZOOM.getValue() : 0.1d;
		distance = distance + (in ? -step : step);
		getRenderer().data.setZoomLevel(zoomLevel(), true);
	}

	@Override
	public void zoomRoi(final Envelope3D env) {
		final int width = (int) env.getWidth();
		final int height = (int) env.getHeight();
		distance = 1.5 * (width > height ? width : height);
		// y is already negated
		setTarget(env.centre());
		getRenderer().data.setZoomLevel(zoomLevel(), true);
	}

	@Override
	public void zoomFocus(final IShape shape) {
		final ILocation p = shape.getLocation();
		final double extent = shape.getEnvelope().maxExtent();
		if (extent == 0) {
			distance = p.getZ() + getRenderer().getMaxEnvDim() / 10;
		} else {
			distance = extent * 1.5;
		}
		// y is NOT negated in IShapes
		setTarget(p.getCentroid().yNegated());
		getRenderer().data.setZoomLevel(zoomLevel(), true);
	}

	@Override
	public void internalMouseMove(final org.eclipse.swt.events.MouseEvent e) {

		// Do it before the mouse position is newly set (in super.internalMouseMove)
		if (keystoneMode) {
			final int selectedCorner = getRenderer().getKeystone().getCornerSelected();
			if (selectedCorner != -1) {
				final GamaPoint origin = getNormalizedCoordinates(getMousePosition().x, getMousePosition().y);
				GamaPoint p = getNormalizedCoordinates(e.x, e.y);
				final GamaPoint translation = origin.minus(p).yNegated();
				p = getRenderer().getKeystone().getKeystoneCoordinates(selectedCorner).plus(-translation.x,
						translation.y, 0);
				getRenderer().getKeystone().setKeystoneCoordinates(selectedCorner, p);
			} else {
				final int cornerSelected = hoverOnKeystone(e);
				getRenderer().getKeystone().setCornerHovered(cornerSelected);
			}
			super.internalMouseMove(e);
			return;
		}

		super.internalMouseMove(e);
		if ((e.stateMask & SWT.BUTTON_MASK) == 0) { return; }
		final Point newPoint = new Point(e.x, e.y);
		if (GamaKeyBindings.ctrl(e)) {
			final int horizMovement = e.x - lastMousePressedPosition.x;
			final int vertMovement = e.y - lastMousePressedPosition.y;
			// if (flipped) {
			// horizMovement = -horizMovement;
			// vertMovement = -vertMovement;
			// }

			final double horizMovement_real = horizMovement;
			final double vertMovement_real = vertMovement;

			lastMousePressedPosition = newPoint;
			theta = theta - horizMovement_real * getSensivity();

			if (flipped) {
				if (vertMovement_real > 0) {
					// down drag : phi increase
					if (phi + vertMovement_real * getSensivity() < 180) {
						phi += vertMovement_real * getSensivity();
					} else {
						phi = +360 + phi - vertMovement_real * getSensivity();
						flipped = !flipped;
						theta += 180;
					}
				} else {
					// up drag : phi decrease
					if (phi - -vertMovement_real * getSensivity() > 0) {
						phi -= -vertMovement_real * getSensivity();
					} else {
						phi = -phi + -vertMovement_real * getSensivity();
						flipped = !flipped;
						theta += 180;
					}
				}
			} else {
				if (vertMovement_real > 0) {
					// down drag : phi decrease
					if (phi - vertMovement_real * getSensivity() > 0) {
						phi -= vertMovement_real * getSensivity();
					} else {
						phi = -phi + vertMovement_real * getSensivity();
						flipped = !flipped;
						theta += 180;
					}
				} else {
					// up drag : phi increase
					if (phi + -vertMovement_real * getSensivity() < 180) {
						phi += -vertMovement_real * getSensivity();
					} else {
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

			final double horizMovement_real = horizMovement;
			final double vertMovement_real = vertMovement;

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
