/*******************************************************************************************************
 *
 * CameraHelper.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.helpers;

import static msi.gama.util.GamaListFactory.createWithoutCasting;

import java.nio.FloatBuffer;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLRunnable;
import com.jogamp.opengl.glu.GLU;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.PlatformHelper;
import msi.gaml.operators.Maths;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.camera.IMultiListener;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.utils.ViewsHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.ICameraHelper;

/**
 * The Class CameraHelper.
 */
public class CameraHelper extends AbstractRendererHelper implements IMultiListener, ICameraHelper {

	static {
		DEBUG.ON();
	}

	/** The glu. */
	private final GLU glu;

	/** The initialized. */
	protected boolean initialized;

	/** The mouse position. */
	// Mouse
	private final GamaPoint mousePosition = new GamaPoint(0, 0);

	/** The position in the world. */
	private final GamaPoint positionInTheWorld = new GamaPoint();

	/** The last mouse pressed position. */
	protected final GamaPoint lastMousePressedPosition = new GamaPoint(0, 0);

	/** The first mouse pressed position. */
	protected final GamaPoint firstMousePressedPosition = new GamaPoint(0, 0);

	/** The firsttime mouse down. */
	protected boolean firsttimeMouseDown = true;

	/** The theta. */
	protected double theta;

	/** The phi. */
	protected double phi;

	/** The flipped. */
	protected boolean flipped = false;

	/** The up. */
	protected final GamaPoint up = new GamaPoint();

	/** The goes forward. */
	// Mouse and keyboard state
	private boolean goesForward;

	/** The goes backward. */
	private boolean goesBackward;

	/** The strafe left. */
	private boolean strafeLeft;

	/** The strafe right. */
	private boolean strafeRight;

	/** The ROI currently drawn. */
	private volatile boolean ROICurrentlyDrawn = false;

	/** The ctrl pressed. */
	protected boolean ctrlPressed = false;

	/** The shift pressed. */
	protected boolean shiftPressed = false;

	/** The keystone mode. */
	protected boolean keystoneMode = false;

	/** The use num keys. */
	private final boolean useNumKeys = GamaPreferences.Displays.OPENGL_NUM_KEYS_CAM.getValue();

	/** The roi envelope. */
	Envelope3D roiEnvelope;

	/** The is ROI sticky. */
	private boolean isROISticky;

	/** The are arrow keys redefined. */
	final boolean areArrowKeysRedefined;

	/**
	 * Instantiates a new abstract camera.
	 *
	 * @param renderer2
	 *            the renderer 2
	 */
	public CameraHelper(final IOpenGLRenderer renderer2) {
		super(renderer2);
		areArrowKeysRedefined = renderer2.getSurface().isArrowRedefined();
		glu = new GLU();
		applyPreset(data.getCameraName());
		initialize();
		update();
	}

	@Override
	public void initialize() {
		flipped = false;
		initialized = false;
		data.resetCamera();
		updateSphericalCoordinatesFromLocations();
	}

	/**
	 * Update cartesian coordinates from angles.
	 */
	// @Override
	public void updateCartesianCoordinatesFromAngles() {
		theta = theta % 360;
		phi = phi % 360;
		if (phi <= 0) {
			phi = 0.001;
		} else if (phi >= 180) { phi = 179.999; }
		final double factorT = theta * Maths.toRad;
		final double factorP = phi * Maths.toRad;
		final double cosT = Math.cos(factorT);
		final double sinT = Math.sin(factorT);
		final double cosP = Math.cos(factorP);
		final double sinP = Math.sin(factorP);
		final double radius = data.getCameraDistance();
		GamaPoint target = getTarget();
		data.setCameraPos(new GamaPoint(radius * cosT * sinP + target.x, radius * sinT * sinP + target.y,
				radius * cosP + target.z));
	}

	/**
	 * Update spherical coordinates from locations.
	 */
	public void updateSphericalCoordinatesFromLocations() {
		final GamaPoint p = getPosition();
		final GamaPoint t = getTarget();
		// final GamaPoint p = getPosition().minus(getTarget());
		// setDistance(p.norm());

		theta = Maths.toDeg * Math.atan2(p.y - t.y, p.x - t.x);
		// See issue on camera_pos
		if (theta == 0) { theta = -90; }
		phi = Maths.toDeg * Math.acos((p.z - t.z) / data.getCameraDistance());
	}

	/**
	 * Translate camera from screen plan.
	 *
	 * @param xTranslationOnScreen
	 *            the x translation in screen
	 * @param yTranslationOnScreen
	 *            the y translation in screen
	 */
	private void translateCameraFromScreenPlan(final double xTranslationOnScreen, final double yTranslationOnScreen) {

		final double theta_vect_x = -Math.sin(theta * Maths.toRad);
		final double theta_vect_y = Math.cos(theta * Maths.toRad);
		final double theta_vect_ratio =
				xTranslationOnScreen / (theta_vect_x * theta_vect_x + theta_vect_y * theta_vect_y);
		final double theta_vect_x_norm = theta_vect_x * theta_vect_ratio;
		final double theta_vect_y_norm = theta_vect_y * theta_vect_ratio;

		final double phi_vect_x = Math.cos(theta * Maths.toRad) * Math.cos(phi * Maths.toRad);
		final double phi_vect_y = Math.sin(theta * Maths.toRad) * Math.cos(phi * Maths.toRad);
		final double phi_vect_z = -Math.sin(phi * Maths.toRad);
		final double phi_vect_ratio =
				yTranslationOnScreen / (phi_vect_x * phi_vect_x + phi_vect_y * phi_vect_y + phi_vect_z * phi_vect_z);
		final double phi_vect_x_norm = phi_vect_x * phi_vect_ratio;
		final double phi_vect_y_norm = phi_vect_y * phi_vect_ratio;

		final double x_translation_in_world = theta_vect_x_norm + phi_vect_x_norm;
		final double y_translation_in_world = theta_vect_y_norm + phi_vect_y_norm;
		GamaPoint position = getPosition();
		GamaPoint target = getTarget();
		Double distance = data.getCameraDistance();
		data.setCameraPos(new GamaPoint(position.x - x_translation_in_world * distance / 1000,
				position.y - y_translation_in_world * distance / 1000, position.z));
		data.setCameraTarget(new GamaPoint(target.x - x_translation_in_world * distance / 1000,
				target.y - y_translation_in_world * distance / 1000, target.z));

		updateSphericalCoordinatesFromLocations();
	}

	/**
	 * Apply preset.
	 *
	 * @param name
	 *            the name
	 */
	public void applyPreset(final String name) {
		// data.setCameraNameFromUser(name);
		flipped = false;
		initialized = false;
		update();
		data.setZoomLevel(zoomLevel(), true);
	}

	/**
	 * Update.
	 */
	public void update() {
		// data.transferToData();
		updateSphericalCoordinatesFromLocations();
		if (initialized) { drawRotationHelper(); }
		initialized = true;
	}

	/* -------Get commands--------- */

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public GamaPoint getPosition() { return data.getCameraPos(); }

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public GamaPoint getTarget() { return data.getCameraTarget(); }

	/**
	 * Gets the orientation.
	 *
	 * @return the orientation
	 */
	public GamaPoint getOrientation() { return up; }

	/**
	 * Animate.
	 */
	public void animate() {

		if (!data.isCameraLocked()) {
			// And we animate it if the keyboard is invoked
			if (goesForward) {
				if (ctrlPressed) {
					if (flipped) {
						if (phi - getKeyboardSensivity() > 0) {
							phi -= getKeyboardSensivity();
						} else {
							phi = -phi + getKeyboardSensivity();
							flipped = false;
							theta += 180;
						}
					} else if (phi + getKeyboardSensivity() < 180) {
						phi += getKeyboardSensivity();
					} else {
						phi = 360 - phi - getKeyboardSensivity();
						flipped = true;
						theta += 180;
					}
					updateCartesianCoordinatesFromAngles();
				} else if (flipped) {
					translateCameraFromScreenPlan(0.0, getKeyboardSensivity());
				} else {
					translateCameraFromScreenPlan(0.0, -getKeyboardSensivity());
				}
			}
			if (goesBackward) {
				if (ctrlPressed) {
					if (flipped) {
						if (phi + getKeyboardSensivity() < 180) {
							phi += getKeyboardSensivity();
						} else {
							phi = 360 - phi - getKeyboardSensivity();
							flipped = false;
							theta += 180;
						}
					} else if (phi - getKeyboardSensivity() > 0) {
						phi -= getKeyboardSensivity();
					} else {
						phi = -phi + getKeyboardSensivity();
						flipped = true;
						theta += 180;
					}
					updateCartesianCoordinatesFromAngles();
				} else if (flipped) {
					translateCameraFromScreenPlan(0.0, -getKeyboardSensivity());
				} else {
					translateCameraFromScreenPlan(0.0, getKeyboardSensivity());
				}
			}
			if (strafeLeft) {
				if (ctrlPressed) {
					if (flipped) {
						theta = theta + -getKeyboardSensivity();
					} else {
						theta = theta - -getKeyboardSensivity();
					}
					updateCartesianCoordinatesFromAngles();
				} else if (flipped) {
					translateCameraFromScreenPlan(getKeyboardSensivity(), 0.0);
				} else {
					translateCameraFromScreenPlan(-getKeyboardSensivity(), 0.0);
				}
			}
			if (strafeRight) {
				if (ctrlPressed) {
					if (flipped) {
						theta = theta + getKeyboardSensivity();
					} else {
						theta = theta - getKeyboardSensivity();
					}
					updateCartesianCoordinatesFromAngles();
				} else if (flipped) {
					translateCameraFromScreenPlan(-getKeyboardSensivity(), 0.0);
				} else {
					translateCameraFromScreenPlan(getKeyboardSensivity(), 0.0);
				}
			}
		}

		// Completely recomputes the up-vector
		double tr = theta * Maths.toRad;
		double pr = phi * Maths.toRad;
		GamaPoint position = data.getCameraPos();
		GamaPoint target = data.getCameraTarget();
		double cp = Math.cos(pr);
		up.setLocation(-Math.cos(tr) * cp, -Math.sin(tr) * cp, Math.sin(pr));
		if (flipped) { up.negate(); }
		// DEBUG.OUT(
		// "Position " + position.rounded() + " target " + target.rounded() + " up " + up + " flipped " + flipped);
		glu.gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, up.x, up.y, up.z);
	}

	/*------------------ Events controls ---------------------*/

	/**
	 * Sets the shift pressed.
	 *
	 * @param value
	 *            the new shift pressed
	 */
	final void setShiftPressed(final boolean value) { shiftPressed = value; }

	/**
	 * Sets the ctrl pressed.
	 *
	 * @param value
	 *            the new ctrl pressed
	 */
	final void setCtrlPressed(final boolean value) { ctrlPressed = value; }

	/**
	 * Sets the mouse left pressed.
	 *
	 * @param b
	 *            the new mouse left pressed
	 */
	protected void setMouseLeftPressed(final boolean b) {}

	/**
	 * Invoke on GL thread.
	 *
	 * @param runnable
	 *            the runnable
	 */
	protected void invokeOnGLThread(final GLRunnable runnable) {
		// Fixing issue #2224
		// runnable.run(renderer.getCanvas());
		renderer.getCanvas().invoke(false, runnable);
	}

	/**
	 * Method mouseScrolled()
	 *
	 * @see org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseScrolled(final MouseEvent e) {
		invokeOnGLThread(drawable -> {
			if (!data.isCameraLocked()) { internalMouseScrolled(e.count); }
			return false;
		});

	}

	@Override
	public final void mouseWheelMoved(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			if (!data.isCameraLocked()) { internalMouseScrolled((int) e.getRotation()[1]); }
			return false;
		});
	}

	/**
	 * Internal mouse scrolled.
	 *
	 * @param e
	 *            the e
	 */
	protected final void internalMouseScrolled(final int count) {
		zoom(count > 0);
	}

	/**
	 * Method mouseMove()
	 *
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseMove(final org.eclipse.swt.events.MouseEvent e) {

		invokeOnGLThread(drawable -> {
			internalMouseMove(autoScaleUp(e.x), autoScaleUp(e.y), e.button, (e.stateMask & SWT.BUTTON_MASK) != 0,
					GamaKeyBindings.ctrl(e), GamaKeyBindings.shift(e));
			return false;
		});

	}

	/**
	 * Auto scale up.
	 *
	 * @param nb
	 *            the nb
	 * @return the int
	 */
	private int autoScaleUp(final int nb) {
		return nb;
		// return DPIHelper.autoScaleUp(nb);
	}

	@Override
	public final void mouseMoved(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			internalMouseMove(autoScaleUp(e.getX()), autoScaleUp(e.getY()), e.getButton(), e.getButton() > 0,
					isControlDown(e), e.isShiftDown());
			return false;
		});
	}

	/**
	 * Checks if is control down.
	 *
	 * @param e
	 *            the e
	 * @return true, if is control down
	 */
	private boolean isControlDown(final com.jogamp.newt.event.MouseEvent e) {
		return e.isControlDown() || PlatformHelper.isMac() && e.isMetaDown();
	}

	/**
	 * Checks if is control down.
	 *
	 * @param e
	 *            the e
	 * @return true, if is control down
	 */
	private boolean isControlDown(final com.jogamp.newt.event.KeyEvent e) {
		return e.isControlDown() || PlatformHelper.isMac() && e.isMetaDown();
	}

	@Override
	public final void mouseDragged(final com.jogamp.newt.event.MouseEvent e) {
		mouseMoved(e);
	}

	/**
	 * Internal mouse move.
	 *
	 * @param x
	 *            the x already scaled
	 * @param y
	 *            the y already scaled
	 * @param button
	 *            the button 0 for no activity
	 * @param isCtrl
	 *            the is ctrl
	 * @param isShift
	 *            the is shift
	 */
	protected void internalMouseMove(final int x, final int y, final int button, final boolean buttonPressed,
			final boolean isCtrl, final boolean isShift) {

		// Do it before the mouse position is newly set
		if (keystoneMode) {
			final int selectedCorner = getRenderer().getKeystoneHelper().getCornerSelected();
			if (selectedCorner != -1) {
				final GamaPoint origin = getNormalizedCoordinates(getMousePosition().x, getMousePosition().y);
				GamaPoint p = getNormalizedCoordinates(x, y);
				final GamaPoint translation = origin.minus(p).yNegated();
				p = getRenderer().getKeystoneHelper().getKeystoneCoordinates(selectedCorner).plus(-translation.x,
						translation.y, 0);
				getRenderer().getKeystoneHelper().setKeystoneCoordinates(selectedCorner, p);
			} else {
				final int cornerSelected = hoverOnKeystone(x, y);
				getRenderer().getKeystoneHelper().setCornerHovered(cornerSelected);
			}
			mousePosition.x = x;
			mousePosition.y = y;
			computeMouseLocationInTheWorld();
			setCtrlPressed(isCtrl);
			setShiftPressed(isShift);
			return;
		}
		mousePosition.x = x;
		mousePosition.y = y;
		computeMouseLocationInTheWorld();
		setCtrlPressed(isCtrl);
		setShiftPressed(isShift);

		if (!buttonPressed) return;
		final GamaPoint newPoint = new GamaPoint(x, y);
		if (!data.isCameraLocked() && isCtrl) {
			final int horizMovement = (int) (newPoint.x - lastMousePressedPosition.x);
			final int vertMovement = (int) (newPoint.y - lastMousePressedPosition.y);
			// if (flipped) {
			// horizMovement = -horizMovement;
			// vertMovement = -vertMovement;
			// }

			final double horizMovement_real = horizMovement;
			final double vertMovement_real = vertMovement;

			lastMousePressedPosition.setLocation(newPoint);
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
				} else // up drag : phi decrease
				if (phi - -vertMovement_real * getSensivity() > 0) {
					phi -= -vertMovement_real * getSensivity();
				} else {
					phi = -phi + -vertMovement_real * getSensivity();
					flipped = !flipped;
					theta += 180;
				}
			} else if (vertMovement_real > 0) {
				// down drag : phi decrease
				if (phi - vertMovement_real * getSensivity() > 0) {
					phi -= vertMovement_real * getSensivity();
				} else {
					phi = -phi + vertMovement_real * getSensivity();
					flipped = !flipped;
					theta += 180;
				}
			} else // up drag : phi increase
			if (phi + -vertMovement_real * getSensivity() < 180) {
				phi += -vertMovement_real * getSensivity();
			} else {
				phi = +360 + phi - vertMovement_real * getSensivity();
				flipped = !flipped;
				theta += 180;
			}

			// phi = phi - vertMovement_real * get_sensivity();
			updateCartesianCoordinatesFromAngles();
		} else if (shiftPressed && isViewInXYPlan()) {
			getMousePosition().x = x;
			getMousePosition().y = y;
			defineROI(new GamaPoint(firstMousePressedPosition.x, firstMousePressedPosition.y));
		} else if (mouseInROI(new GamaPoint(getMousePosition().x, getMousePosition().y))) {
			GamaPoint p = getWorldPositionOfMouse();
			p = p.minus(roiEnvelope.centre());
			roiEnvelope.translate(p.x, p.y);
		} else if (!data.isCameraLocked()) {
			int horizMovement = (int) (x - lastMousePressedPosition.x);
			int vertMovement = (int) (y - lastMousePressedPosition.y);
			if (flipped) {
				horizMovement = -horizMovement;
				vertMovement = -vertMovement;
			}

			final double horizMovement_real = horizMovement;
			final double vertMovement_real = vertMovement;

			translateCameraFromScreenPlan(horizMovement_real, vertMovement_real);

			lastMousePressedPosition.setLocation(newPoint);
		}

	}

	/**
	 * Method mouseDoubleClick()
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseDoubleClick(final org.eclipse.swt.events.MouseEvent e) {
		// Already taken in charge by the ZoomListener in the view
		if (keystoneMode) {
			final int x = autoScaleUp(e.x);
			final int y = autoScaleUp(e.y);
			final int corner = clickOnKeystone(x, y);
			if (corner != -1) { getRenderer().getKeystoneHelper().resetCorner(corner); }
		}
	}

	@Override
	public final void mouseClicked(final com.jogamp.newt.event.MouseEvent e) {
		if (e.getClickCount() == 2) {
			if (keystoneMode) {
				final int x = autoScaleUp(e.getX());
				final int y = autoScaleUp(e.getY());
				final int corner = clickOnKeystone(x, y);
				if (corner != -1) { getRenderer().getKeystoneHelper().resetCorner(corner); }
			} else {
				invokeOnGLThread(drawable -> {
					getRenderer().getSurface().zoomFit();
					return false;
				});
			}
		}
	}

	/**
	 * Method mouseDown()
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseDown(final org.eclipse.swt.events.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			final int x = autoScaleUp(e.x);
			final int y = autoScaleUp(e.y);
			internalMouseDown(x, y, e.button, GamaKeyBindings.ctrl(e), GamaKeyBindings.shift(e));
			return false;
		});

	}

	@Override
	public final void mousePressed(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			final int x = autoScaleUp(e.getX());
			final int y = autoScaleUp(e.getY());
			internalMouseDown(x, y, e.getButton(), isControlDown(e), e.isShiftDown());
			return false;
		});
	}

	/**
	 * Gets the normalized coordinates.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the normalized coordinates
	 */
	protected GamaPoint getNormalizedCoordinates(final double x, final double y) {
		final double xCoordNormalized = x / getRenderer().getWidth();
		double yCoordNormalized = y / getRenderer().getHeight();
		if (!renderer.useShader()) { yCoordNormalized = 1 - yCoordNormalized; }
		return new GamaPoint(xCoordNormalized, yCoordNormalized);
	}

	/**
	 * Click on keystone.
	 *
	 * @param e
	 *            the e
	 * @return the int
	 */
	private int clickOnKeystone(final int x, final int y) {
		return renderer.getKeystoneHelper().cornerSelected(new GamaPoint(x, y));
	}

	/**
	 * Hover on keystone.
	 *
	 * @param e
	 *            the e
	 * @return the int
	 */
	protected int hoverOnKeystone(final int x, final int y) {
		// return the number of the corner clicked. Return -1 if no click on
		// keystone. Return 10 if click on the center.
		// final GamaPoint p = getNormalizedCoordinates(e);
		return renderer.getKeystoneHelper().cornerHovered(new GamaPoint(x, y));
	}

	/**
	 * Internal mouse down.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	final void internalMouseDown(final int x, final int y, final int button, final boolean isCtrl,
			final boolean isShift) {

		if (firsttimeMouseDown) {
			firstMousePressedPosition.setLocation(x, y, 0);
			firsttimeMouseDown = false;
		}
		if (keystoneMode) {
			if (getRenderer().getKeystoneHelper().getCornerSelected() != -1) {
				getRenderer().getKeystoneHelper().setCornerSelected(-1);
				return;
			}
			final int cornerSelected = clickOnKeystone(x, y);
			if (cornerSelected != -1) { getRenderer().getKeystoneHelper().setCornerSelected(cornerSelected); }
		}

		lastMousePressedPosition.setLocation(x, y, 0);
		// Activate Picking when press and right click
		if (button == 3 && !keystoneMode) {
			if (mouseInROI(lastMousePressedPosition)) {
				renderer.getSurface().selectionIn(getROIEnvelope());
			} else if (renderer.getSurface().canTriggerContextualMenu()) {
				renderer.getPickingHelper().setPicking(true);
			}
		} else if (button == 2 && !data.isCameraLocked()) { // mouse wheel
			resetPivot();
		} else if (isShift && isViewInXYPlan()) { startROI(); }
		// else {
		// renderer.getPickingState().setPicking(false);
		// }
		getMousePosition().x = x;
		getMousePosition().y = y;

		setMouseLeftPressed(button == 1);
		setCtrlPressed(isCtrl);
		setShiftPressed(isShift);

	}

	/**
	 * Method mouseUp()
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseUp(final org.eclipse.swt.events.MouseEvent e) {

		invokeOnGLThread(drawable -> {
			// if (cameraInteraction) {
			internalMouseUp(e.button, GamaKeyBindings.shift(e));
			// }
			return false;
		});

	}

	@Override
	public final void mouseReleased(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			// if (cameraInteraction) {
			internalMouseUp(e.getButton(), e.isShiftDown());
			// }
			return false;
		});
	}

	/**
	 * Internal mouse up.
	 *
	 * @param e
	 *            the e
	 */
	protected void internalMouseUp(final int button, final boolean isShift) {
		firsttimeMouseDown = true;
		if (isViewInXYPlan() && isShift) { finishROISelection(); }
		if (button == 1) { setMouseLeftPressed(false); }

	}

	/**
	 * Start ROI.
	 *
	 * @param e
	 *            the e
	 */
	private void startROI() {
		defineROI(new GamaPoint(firstMousePressedPosition));
		ROICurrentlyDrawn = true;
	}

	/**
	 * Finish ROI selection.
	 */
	void finishROISelection() {
		if (ROICurrentlyDrawn) {
			final Envelope3D env = getROIEnvelope();
			if (env != null) { renderer.getSurface().selectionIn(env); }
		}
	}

	/**
	 * Gets the world position of mouse.
	 *
	 * @return the world position of mouse
	 */
	public GamaPoint getWorldPositionOfMouse() { return positionInTheWorld; }

	/** The pixel depth. */
	FloatBuffer pixelDepth = Buffers.newDirectFloatBuffer(1);

	/**
	 * Gets the world position from the mouse position. Less computationally intensive and more accurate for planar
	 * surfaces. Requires however to be done in the GL context
	 *
	 * @param mouse
	 *            the mouse
	 * @return the world position from
	 */
	public void computeMouseLocationInTheWorld() {

		// getWorldPositionFrom(mousePosition, positionInTheWorld);
		// double distance = data.getCameraDistance();
		// double zFar = data.getzFar();
		// double zNear = data.getzNear();
		OpenGL gl = renderer.getOpenGLHelper();
		final double[] wcoord = new double[4];
		final int[] viewport = gl.viewport;
		final double mvmatrix[] = gl.mvmatrix;
		final double projmatrix[] = gl.projmatrix;
		final int x = (int) Math.round(mousePosition.x), y = (int) Math.round(viewport[3] - mousePosition.y);
		pixelDepth.rewind();
		gl.getGL().glReadPixels(x, y, 1, 1, GL2ES2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, pixelDepth);
		double z = pixelDepth.get(0);

		// DEBUG.OUT("First value retrieved by gluUnproject for Z : " + z + " computing a camera distance of "
		// + (1 - z) * (zFar - zNear) + " while the real one is " + distance);

		// z = Math.min(1, 1 - distance / (2 * (zFar - zNear)));

		// DEBUG.OUT("Value retrieved for Z : " + z + " with camera distance " + data.getCameraDistance());

		if (z == 1d || z == 0d) {
			getWorldPositionFrom(mousePosition, positionInTheWorld);
		} else {
			glu.gluUnProject(x, y, z, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
			positionInTheWorld.setLocation(wcoord[0], wcoord[1], 0);
		}
	}

	/**
	 * Different method from above, as it might be used in non openGL contexts
	 *
	 * @param mouse
	 * @return
	 */
	public GamaPoint getWorldPositionFrom(final GamaPoint mouse, final GamaPoint result) {
		final GamaPoint camLoc = getPosition();
		OpenGL gl = renderer.getOpenGLHelper();
		if (gl == null) return new GamaPoint();
		final double[] wcoord = new double[4];
		final double x = (int) mouse.x, y = gl.viewport[3] - (int) mouse.y;
		glu.gluUnProject(x, y, 0.1, gl.mvmatrix, 0, gl.projmatrix, 0, gl.viewport, 0, wcoord, 0);
		result.setLocation(wcoord[0], wcoord[1], wcoord[2]);
		glu.gluUnProject(x, y, 0.9, gl.mvmatrix, 0, gl.projmatrix, 0, gl.viewport, 0, wcoord, 0);
		result.setLocation(wcoord[0] - result.x, wcoord[1] - result.y, wcoord[2] - result.z).normalize();
		final double distance = camLoc.z / -result.z;
		return result.setLocation(result.x * distance + camLoc.x, result.y * distance + camLoc.y, 0);
	}

	//
	// protected void dump() {
	// DEBUG.LOG("xPos:" + position.x + " yPos:" + position.y + "
	// zPos:" + position.z);
	// DEBUG.LOG("xLPos:" + target.x + " yLPos:" + target.y + " zLPos:"
	// + target.z);
	// DEBUG.LOG("_phi " + phi + " _theta " + theta);
	// }

	/**
	 * Gets the mouse position.
	 *
	 * @return the mouse position
	 */
	public GamaPoint getMousePosition() { return mousePosition; }

	/**
	 * Checks if is view in XY plan.
	 *
	 * @return true, if is view in XY plan
	 */
	private boolean isViewInXYPlan() {
		return true;
		// return phi > 170 || phi < 10;// && theta > -5 && theta < 5;
	}

	/**
	 * Gets the last mouse pressed position.
	 *
	 * @return the last mouse pressed position
	 */
	public GamaPoint getLastMousePressedPosition() { return lastMousePressedPosition; }

	/**
	 * Gets the keyboard sensivity.
	 *
	 * @return the keyboard sensivity
	 */
	protected double getKeyboardSensivity() { return GamaPreferences.Displays.OPENGL_KEYBOARD.getValue(); }

	/**
	 * Gets the sensivity.
	 *
	 * @return the sensivity
	 */
	protected double getSensivity() { return GamaPreferences.Displays.OPENGL_MOUSE.getValue(); }

	/**
	 * Gets the renderer.
	 *
	 * @return the renderer
	 */
	@Override
	public IOpenGLRenderer getRenderer() { return renderer; }

	/**
	 * Method keyPressed()
	 *
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public final void keyPressed(final org.eclipse.swt.events.KeyEvent e) {

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				boolean cameraInteraction = !data.isCameraLocked();
				// setShiftPressed(GamaKeyBindings.shift(e));

				switch (e.keyCode) {
					case SWT.ARROW_LEFT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction
								&& (!areArrowKeysRedefined || GamaKeyBindings.ctrl(e) || GamaKeyBindings.shift(e))) {
							CameraHelper.this.strafeLeft = true;
						}
						break;
					case SWT.ARROW_RIGHT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction
								&& (!areArrowKeysRedefined || GamaKeyBindings.ctrl(e) || GamaKeyBindings.shift(e))) {
							CameraHelper.this.strafeRight = true;
						}
						break;
					case SWT.ARROW_UP:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction
								&& (!areArrowKeysRedefined || GamaKeyBindings.ctrl(e) || GamaKeyBindings.shift(e))) {
							CameraHelper.this.goesForward = true;
						}
						break;
					case SWT.ARROW_DOWN:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction
								&& (!areArrowKeysRedefined || GamaKeyBindings.ctrl(e) || GamaKeyBindings.shift(e))) {
							CameraHelper.this.goesBackward = true;
						}
						break;
					case SWT.SPACE:
						if (cameraInteraction) { resetPivot(); }
						break;
					case SWT.CTRL:
						setCtrlPressed(!firsttimeMouseDown);
						break;
					case SWT.COMMAND:
						setCtrlPressed(!firsttimeMouseDown);
						break;
				}
				switch (e.character) {
					case '+':
						if (cameraInteraction) { zoom(true); }
						break;
					case '-':
						if (cameraInteraction) { zoom(false); }
						break;
					case '4':
						if (cameraInteraction && useNumKeys) { quickLeftTurn(); }
						break;
					case '6':
						if (cameraInteraction && useNumKeys) { quickRightTurn(); }
						break;
					case '8':
						if (cameraInteraction && useNumKeys) { quickUpTurn(); }
						break;
					case '2':
						if (cameraInteraction && useNumKeys) { quickDownTurn(); }
						break;
					case 'k':
						if (!GamaKeyBindings.ctrl(e)) { activateKeystoneMode(); }
						break;
					default:
						return true;
				}
			} else if (e.character == 'k' && !GamaKeyBindings.ctrl(e)) { activateKeystoneMode(); }
			return true;
		});
	}

	@Override
	public final void keyPressed(final com.jogamp.newt.event.KeyEvent e) {

		// MOVED OUTSIDE OF THE GL THREAD (needs to be run in the SWT thread)
		switch (e.getKeySymbol()) {
			// We need to register here all the keystrokes used in the Workbench and on the view, as they might
			// be caught by the NEWT key listener. Those dedicated to modelling are left over for the moment
			// (like CTRL+SHIFT+H)
			// First the global keystrokes
			case com.jogamp.newt.event.KeyEvent.VK_ESCAPE:
				if (!getRenderer().getSurface().isEscRedefined()) { ViewsHelper.toggleFullScreenMode(); }
				return;
			case 'p':
			case 'P':
				if (isControlDown(e)) {
					if (e.isShiftDown()) {
						GAMA.stepFrontmostExperiment();
					} else {
						GAMA.startPauseFrontmostExperiment();
					}
				}
				return;
			case 'R':
			case 'r':
				if (isControlDown(e)) {
					if (e.isShiftDown()) {
						GAMA.relaunchFrontmostExperiment();
					} else {
						GAMA.reloadFrontmostExperiment();
					}
				}
				return;
			case 'X':
			case 'x':
				if (isControlDown(e) && e.isShiftDown()) { GAMA.closeAllExperiments(true, false); }
		}

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				boolean cameraInteraction = !data.isCameraLocked();
				switch (e.getKeySymbol()) {
					case com.jogamp.newt.event.KeyEvent.VK_SPACE:
						if (cameraInteraction) { resetPivot(); }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_CONTROL:
						// The press and release of these keys does not seem to work. Caught after
						setCtrlPressed(!firsttimeMouseDown);
						break;
					case com.jogamp.newt.event.KeyEvent.VK_META:
						// The press and release of these keys does not seem to work. Caught after
						setCtrlPressed(!firsttimeMouseDown);
						break;
				}
				// setShiftPressed(e.isShiftDown());
				switch (e.getKeyCode()) {
					// Finally the keystrokes for the display itself
					case com.jogamp.newt.event.KeyEvent.VK_LEFT:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction && (!areArrowKeysRedefined || isControlDown(e) || e.isShiftDown())) {
							CameraHelper.this.strafeLeft = true;
						}
						break;
					case com.jogamp.newt.event.KeyEvent.VK_RIGHT:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction && (!areArrowKeysRedefined || isControlDown(e) || e.isShiftDown())) {
							CameraHelper.this.strafeRight = true;
						}
						break;
					case com.jogamp.newt.event.KeyEvent.VK_UP:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction && (!areArrowKeysRedefined || isControlDown(e) || e.isShiftDown())) {
							CameraHelper.this.goesForward = true;
						}
						break;
					case com.jogamp.newt.event.KeyEvent.VK_DOWN:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction && (!areArrowKeysRedefined || isControlDown(e) || e.isShiftDown())) {
							CameraHelper.this.goesBackward = true;
						}
						break;
				}

				switch (e.getKeyChar()) {
					case 0:
						setCtrlPressed(e.isControlDown() || PlatformHelper.isMac() && e.isMetaDown());
						setShiftPressed(e.isShiftDown());
						break;
					case '+':
						if (cameraInteraction) { zoom(true); }
						break;
					case '-':
						if (cameraInteraction) { zoom(false); }
						break;
					case '4':
						if (cameraInteraction && useNumKeys) { quickLeftTurn(); }
						break;
					case '6':
						if (cameraInteraction && useNumKeys) { quickRightTurn(); }
						break;
					case '8':
						if (cameraInteraction && useNumKeys) { quickUpTurn(); }
						break;
					case '2':
						if (cameraInteraction && useNumKeys) { quickDownTurn(); }
						break;
					case 'k':
						if (!isControlDown(e)) { activateKeystoneMode(); }
						break;
					default:
						return true;
				}
			} else if (e.getKeyChar() == 'k' && !isControlDown(e)) { activateKeystoneMode(); }
			return true;
		});
	}

	/**
	 * Reset pivot.
	 */
	protected void resetPivot() {
		// final LayeredDisplayData data = data;
		// final double envWidth = data.getEnvWidth();
		// final double envHeight = data.getEnvHeight();
		// GamaPoint position = getDefinition().getLocation();
		// GamaPoint target = getDefinition().getTarget();
		// final double translate_x = target.x - envWidth / 2d;
		// final double translate_y = target.y + envHeight / 2d;
		// final double translate_z = target.z;
		// setTarget(envWidth / 2d, -envHeight / 2d, 0);
		// setPosition(position.x - translate_x, position.y - translate_y, position.z - translate_z);
		data.resetCamera();
		updateSphericalCoordinatesFromLocations();
	}

	/**
	 * Quick left turn.
	 */
	protected void quickLeftTurn() {
		theta -= 30;
		updateCartesianCoordinatesFromAngles();
	}

	/**
	 * Quick right turn.
	 */
	protected void quickRightTurn() {
		theta += 30;
		updateCartesianCoordinatesFromAngles();
	}

	/**
	 * Quick up turn.
	 */
	protected void quickUpTurn() {
		if (flipped) {
			if (phi + 30 < 180) {
				phi += 30;
			} else {
				phi = 360 - phi - 30;
				flipped = false;
				theta += 180;
			}
		} else if (phi - 30 > 0) {
			phi -= 30;
		} else {
			phi = -phi + 30;
			flipped = true;
			theta += 180;
		}
		updateCartesianCoordinatesFromAngles();
	}

	/**
	 * Quick down turn.
	 */
	protected void quickDownTurn() {
		if (flipped) {
			if (phi - 30 > 0) {
				phi -= 30;
			} else {
				phi = -phi + 30;
				flipped = false;
				theta += 180;
			}
		} else if (phi + 30 < 180) {
			phi += 30;
		} else {
			phi = 360 - phi - 30;
			flipped = true;
			theta += 180;
		}
		updateCartesianCoordinatesFromAngles();
	}

	/**
	 * Activate keystone mode.
	 */
	protected final void activateKeystoneMode() {
		if (!keystoneMode) {
			getRenderer().getSurface().zoomFit();
			getRenderer().getKeystoneHelper().startDrawHelper();
		} else {
			String def = IKeyword.KEYSTONE + ": "
					+ createWithoutCasting(Types.POINT, data.getKeystone().toCoordinateArray()).serialize(false);
			getRenderer().getKeystoneHelper().stopDrawHelper();
			WorkbenchHelper.copy(def);
		}
		keystoneMode = !keystoneMode;
	}

	/**
	 * Method keyReleased()
	 *
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public final void keyReleased(final org.eclipse.swt.events.KeyEvent e) {

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				boolean cameraInteraction = !data.isCameraLocked();
				switch (e.keyCode) {
					case SWT.ARROW_LEFT: // turns left (scene rotates right)
						if (cameraInteraction) { strafeLeft = false; }
						break;
					case SWT.ARROW_RIGHT: // turns right (scene rotates left)
						if (cameraInteraction) { strafeRight = false; }
						break;
					case SWT.ARROW_UP:
						if (cameraInteraction) { goesForward = false; }
						break;
					case SWT.ARROW_DOWN:
						if (cameraInteraction) { goesBackward = false; }
						break;
					case SWT.CTRL:
						setCtrlPressed(false);
						break;
					case SWT.COMMAND:
						setCtrlPressed(false);
						break;
					case SWT.SHIFT:
						setShiftPressed(false);
						finishROISelection();
						break;
					default:
						return true;
				}
			}
			return false;
		});
	}

	@Override
	public final void keyReleased(final com.jogamp.newt.event.KeyEvent e) {

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				if (e.getKeyChar() == 0) {
					setCtrlPressed(!isControlDown(e));
					setShiftPressed(!e.isShiftDown());
					return true;
				}
				boolean cameraInteraction = !data.isCameraLocked();
				switch (e.getKeyCode()) {

					case com.jogamp.newt.event.KeyEvent.VK_LEFT: // turns left (scene rotates right)
						if (cameraInteraction) { strafeLeft = false; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_RIGHT: // turns right (scene rotates left)
						if (cameraInteraction) { strafeRight = false; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_UP:
						if (cameraInteraction) { goesForward = false; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_DOWN:
						if (cameraInteraction) { goesBackward = false; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_CONTROL:
						setCtrlPressed(false);
						break;
					case com.jogamp.newt.event.KeyEvent.VK_META:
						setCtrlPressed(false);
						break;
					case com.jogamp.newt.event.KeyEvent.VK_SHIFT:
						setShiftPressed(false);
						finishROISelection();
						break;
					default:
						return true;
				}
			}
			return false;
		});
	}

	/**
	 * Zoom level.
	 *
	 * @return the double
	 */
	public Double zoomLevel() {
		return getMaxEnvDim() * data.getCameraDistanceCoefficient() / data.getCameraDistance();
	}

	/**
	 * Zoom.
	 *
	 * @param level
	 *            the level
	 */
	public void zoom(final double level) {
		data.setCameraDistance(getMaxEnvDim() * data.getCameraDistanceCoefficient() / level);
		updateCartesianCoordinatesFromAngles();
		/**
		 * Zoom.
		 *
		 * @param in
		 *            the in
		 */
	}

	/**
	 * Zoom.
	 *
	 * @param in
	 *            the in
	 */
	// @Override
	public void zoom(final boolean in) {
		if (keystoneMode) return;
		Double distance = data.getCameraDistance();
		final double step = distance != 0d ? distance / 10d * GamaPreferences.Displays.OPENGL_ZOOM.getValue() : 0.1d;
		data.setCameraDistance(distance + (in ? -step : step));
		// zoom(zoomLevel());
		data.setZoomLevel(zoomLevel(), true);
	}

	/**
	 * Zoom focus.
	 *
	 * @param env
	 *            the env
	 */
	public void zoomFocus(final Envelope3D env) {

		// REDO it entirely
		final double extent = env.maxExtent();
		if (extent == 0) {
			data.setCameraDistance(env.getMaxZ() + getMaxEnvDim() / 10);
		} else {
			data.setCameraDistance(extent * 1.5);
		}
		final GamaPoint centre = env.centre();
		// we suppose y is already negated
		data.setCameraTarget(new GamaPoint(centre.x, centre.y, centre.z));
		data.setZoomLevel(zoomLevel(), true);
		/**
		 * Draw rotation helper.
		 */
	}

	/**
	 * Draw rotation helper.
	 */
	protected void drawRotationHelper() {
		renderer.getOpenGLHelper().setRotationMode(ctrlPressed && !data.isCameraLocked());
		/**
		 * Sets the distance.
		 *
		 * @param distance
		 *            the new distance
		 */
	}

	/**
	 * Hook.
	 */
	public void hook() {
		getCanvas().addCameraListeners(this);
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		getCanvas().removeCameraListeners(this);
	}

	@Override
	public Collection<String> getCameraNames() { return data.getCameraNames(); }

	@Override
	public void setCameraName(final String p) {
		data.setCameraNameFromUser(p);
		applyPreset(p);
	}

	@Override
	public String getCameraName() { return data.getCameraName(); }

	@Override
	public boolean isCameraLocked() { return data.isCameraLocked(); }

	@Override
	public void toggleCamera() {
		data.setCameraLocked(!data.isCameraLocked());
	}

	@Override
	public String getCameraDefinition() {
		StringBuilder text = new StringBuilder(IKeyword.CAMERA).append(" 'default' ").append(IKeyword.LOCATION)
				.append(": ").append(new GamaPoint(data.getCameraPos()).yNegated().withPrecision(4).serialize(false));
		text.append(" ").append(IKeyword.TARGET).append(": ")
				.append(new GamaPoint(data.getCameraTarget()).yNegated().withPrecision(4).serialize(false)).append(";");
		return text.toString();
	}

	/**
	 * Toogle ROI.
	 */
	public void toogleROI() {
		isROISticky = !isROISticky;
	}

	/**
	 * Checks if is sticky ROI.
	 *
	 * @return true, if is sticky ROI
	 */
	public boolean isStickyROI() { return isROISticky; }

	/**
	 * Gets the ROI envelope.
	 *
	 * @return the ROI envelope
	 */
	public Envelope3D getROIEnvelope() { return roiEnvelope; }

	/**
	 * Cancel ROI.
	 */
	public void cancelROI() {
		if (isROISticky) return;
		roiEnvelope = null;
	}

	/**
	 * Define ROI.
	 *
	 * @param mouseStart
	 *            the mouse start
	 * @param mouseEnd
	 *            the mouse end
	 */
	public void defineROI(final GamaPoint mouseStart) {
		final GamaPoint start = getWorldPositionFrom(mouseStart, new GamaPoint());

		roiEnvelope =
				Envelope3D.of(start.x, positionInTheWorld.x, start.y, positionInTheWorld.y, 0, getMaxEnvDim() / 20d);
	}

	/**
	 * Mouse in ROI.
	 *
	 * @param mousePosition
	 *            the mouse position
	 * @return true, if successful
	 */
	public boolean mouseInROI(final GamaPoint mousePosition) {
		final Envelope3D env = getROIEnvelope();
		if (env == null) return false;
		final GamaPoint p = getWorldPositionFrom(mousePosition, new GamaPoint());
		return env.contains(p);
	}

}
