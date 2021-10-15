/*******************************************************************************************************
 *
 * AbstractCamera.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.camera;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import com.jogamp.opengl.GLRunnable;
import com.jogamp.opengl.glu.GLU;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.PlatformHelper;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.FLAGS;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.renderer.helpers.CameraHelper;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.utils.DPIHelper;

/**
 * The Class AbstractCamera.
 */
public abstract class AbstractCamera implements ICamera {

	static {
		DEBUG.OFF();
	}

	/** The renderer. */
	protected final IOpenGLRenderer renderer;

	/** The glu. */
	private final GLU glu;

	/** The initialized. */
	protected boolean initialized;

	/** The mouse position. */
	// Mouse
	private final GamaPoint mousePosition = new GamaPoint(0, 0);

	/** The last mouse pressed position. */
	protected final GamaPoint lastMousePressedPosition = new GamaPoint(0, 0);

	/** The first mouse pressed position. */
	protected final GamaPoint firstMousePressedPosition = new GamaPoint(0, 0);

	/** The firsttime mouse down. */
	protected boolean firsttimeMouseDown = true;

	/** The camera interaction. */
	protected boolean cameraInteraction = true;

	/** The position. */
	protected final GamaPoint position = new GamaPoint(0, 0, 0);

	/** The target. */
	protected final GamaPoint target = new GamaPoint(0, 0, 0);

	/** The up vector. */
	protected final GamaPoint upVector = new GamaPoint(0, 0, 0);

	/** The initial up vector. */
	protected GamaPoint initialPosition, initialTarget, initialUpVector;

	/** The theta. */
	protected double theta;

	/** The phi. */
	protected double phi;

	/** The flipped. */
	protected boolean flipped = false;
	// protected double upVectorAngle;

	/** The keyboard sensivity. */
	private final double _keyboardSensivity = 1d;

	/** The sensivity. */
	private final double _sensivity = 1;

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

	/** The z corrector. */
	protected double zCorrector = 1d;

	/** The use num keys. */
	private final boolean useNumKeys = GamaPreferences.Displays.OPENGL_NUM_KEYS_CAM.getValue();

	/** The Constant UP_Z. */
	private static final GamaPoint UP_Z = new GamaPoint(0, 0, 1);

	/**
	 * Instantiates a new abstract camera.
	 *
	 * @param renderer2
	 *            the renderer 2
	 */
	public AbstractCamera(final IOpenGLRenderer renderer2) {
		this.renderer = renderer2;
		// LayeredDisplayData data = renderer.getData();
		// if (!data.isCameraUpVectorDefined() && !data.getCameraOrientation().equals(UP_Z)) {
		setUpVector(0.0, 1.0, 0.0);
		// }
		glu = new GLU();
	}

	@Override
	public void applyPreset(final String name) {
		final CameraPreset p = CameraHelper.PRESETS.get(name);
		if (p != null) {
			target.setLocation(renderer.getEnvWidth() / 2, -renderer.getEnvHeight() / 2, 0);
			p.applyTo(this);
			flipped = false;
			initialized = false;
			update();
			getRenderer().getData().setZoomLevel(1d, true, true);
		}
	}

	@Override
	public void updatePosition() {
		position.setLocation(renderer.getData().getCameraPos());
	}

	@Override
	public void updateTarget() {
		target.setLocation(renderer.getData().getCameraTarget());
	}

	@Override
	public void updateOrientation() {
		// DEBUG.OUT("Upvector updatd as " + upVector);
		upVector.setLocation(renderer.getData().getCameraOrientation());
	}

	@Override
	public void update() {
		final LayeredDisplayData data = renderer.getData();
		cameraInteraction = !data.cameraInteractionDisabled();
		updateSphericalCoordinatesFromLocations();
		if (initialized) {
			// if (flipped) {
			// setUpVector(-(-Math.cos(theta * Maths.toRad) * Math.cos(phi * Maths.toRad)),
			// -(-Math.sin(theta * Maths.toRad) * Math.cos(phi * Maths.toRad)), -Math.sin(phi * Maths.toRad));
			// } else {
			// setUpVector(-Math.cos(theta * Maths.toRad) * Math.cos(phi * Maths.toRad),
			// -Math.sin(theta * Maths.toRad) * Math.cos(phi * Maths.toRad), Math.sin(phi * Maths.toRad));
			// }
			drawRotationHelper();
		}

		initialized = true;
	}

	/**
	 * Draw rotation helper.
	 */
	protected abstract void drawRotationHelper();

	@Override
	public void setPosition(final double xPos, final double yPos, final double zPos) {
		position.setLocation(xPos, yPos, zPos);
		getRenderer().getData().setCameraPos(new GamaPoint(position));
	}

	/**
	 * Sets the target.
	 *
	 * @param xLPos
	 *            the x L pos
	 * @param yLPos
	 *            the y L pos
	 * @param zLPos
	 *            the z L pos
	 */
	public void setTarget(final double xLPos, final double yLPos, final double zLPos) {
		target.setLocation(xLPos, yLPos, zLPos);
		getRenderer().getData().setCameraLookPos(new GamaPoint(target));
	}

	@Override
	public void setUpVector(final double xPos, final double yPos, final double zPos) {
		upVector.setLocation(xPos, yPos, zPos);
		// DEBUG.OUT("Upvector modified as " + upVector);
		getRenderer().getData().setCameraOrientation(new GamaPoint(upVector));
	}

	/* -------Get commands--------- */

	@Override
	public GamaPoint getPosition() { return position; }

	@Override
	public GamaPoint getTarget() { return target; }

	@Override
	public GamaPoint getOrientation() { return upVector; }

	@Override
	public void animate() {
		glu.gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, upVector.x, upVector.y,
				upVector.z);
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
		runnable.run(renderer.getCanvas());
		// renderer.getDrawable().invoke(false, runnable);
	}

	/**
	 * Method mouseScrolled()
	 *
	 * @see org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseScrolled(final MouseEvent e) {
		invokeOnGLThread(drawable -> {
			if (cameraInteraction) { internalMouseScrolled(e.count); }
			return false;
		});

	}

	@Override
	public final void mouseWheelMoved(final com.jogamp.newt.event.MouseEvent e) {
		invokeOnGLThread(drawable -> {
			if (cameraInteraction) { internalMouseScrolled((int) e.getRotation()[1]); }
			return false;
		});
	}

	/**
	 * Internal mouse scrolled.
	 *
	 * @param e
	 *            the e
	 */
	protected void internalMouseScrolled(final int count) {
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
		if (FLAGS.USE_NATIVE_OPENGL_WINDOW) return nb;
		return DPIHelper.autoScaleUp(nb);
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
		mousePosition.x = x;
		mousePosition.y = y;
		setCtrlPressed(isCtrl);
		setShiftPressed(isShift);
	}

	/**
	 * Method mouseEnter()
	 *
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseEnter(final org.eclipse.swt.events.MouseEvent e) {}

	@Override
	public final void mouseEntered(final com.jogamp.newt.event.MouseEvent e) {}

	/**
	 * Method mouseExit()
	 *
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseExit(final org.eclipse.swt.events.MouseEvent e) {}

	@Override
	public final void mouseExited(final com.jogamp.newt.event.MouseEvent e) {}

	/**
	 * Method mouseHover()
	 *
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseHover(final org.eclipse.swt.events.MouseEvent e) {}

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
				getRenderer().getSurface().zoomFit();
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
			if (renderer.getOpenGLHelper().mouseInROI(lastMousePressedPosition)) {
				renderer.getSurface().selectionIn(renderer.getOpenGLHelper().getROIEnvelope());
			} else if (renderer.getSurface().canTriggerContextualMenu()) {
				renderer.getPickingHelper().setPicking(true);
			}
		} else if (button == 2 && cameraInteraction) { // mouse wheel
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
		if (canSelectOnRelease(isShift) && isViewInXYPlan() && isShift) { finishROISelection(); }
		if (button == 1) { setMouseLeftPressed(false); }

	}

	/**
	 * Start ROI.
	 *
	 * @param e
	 *            the e
	 */
	private void startROI() {
		renderer.getOpenGLHelper().defineROI(new GamaPoint(firstMousePressedPosition), new GamaPoint(mousePosition));
		ROICurrentlyDrawn = true;
	}

	/**
	 * Finish ROI selection.
	 */
	void finishROISelection() {
		if (ROICurrentlyDrawn) {
			final Envelope3D env = renderer.getOpenGLHelper().getROIEnvelope();
			if (env != null) { renderer.getSurface().selectionIn(env); }
		}
	}

	/**
	 * Can select on release.
	 *
	 * @param arg0
	 *            the arg 0
	 * @return true, if successful
	 */
	protected abstract boolean canSelectOnRelease(boolean isShift);
	//
	// protected void dump() {
	// DEBUG.LOG("xPos:" + position.x + " yPos:" + position.y + "
	// zPos:" + position.z);
	// DEBUG.LOG("xLPos:" + target.x + " yLPos:" + target.y + " zLPos:"
	// + target.z);
	// DEBUG.LOG("upX" + upVector.x + " upY:" + upVector.y + " upZ:" +
	// upVector.z);
	// DEBUG.LOG("_phi " + phi + " _theta " + theta);
	// }

	@Override
	public GamaPoint getMousePosition() { return mousePosition; }

	/**
	 * Checks if is view in XY plan.
	 *
	 * @return true, if is view in XY plan
	 */
	public boolean isViewInXYPlan() {
		return true;
		// return phi > 170 || phi < 10;// && theta > -5 && theta < 5;
	}

	@Override
	public GamaPoint getLastMousePressedPosition() { return lastMousePressedPosition; }

	/**
	 * Gets the keyboard sensivity.
	 *
	 * @return the keyboard sensivity
	 */
	protected double getKeyboardSensivity() { return _keyboardSensivity; }

	/**
	 * Gets the sensivity.
	 *
	 * @return the sensivity
	 */
	protected double getSensivity() { return _sensivity; }

	/**
	 * Checks if is forward.
	 *
	 * @return true, if is forward
	 */
	protected boolean isForward() { return goesForward; }

	/**
	 * Checks if is backward.
	 *
	 * @return true, if is backward
	 */
	protected boolean isBackward() { return goesBackward; }

	/**
	 * Checks if is strafe left.
	 *
	 * @return true, if is strafe left
	 */
	protected boolean isStrafeLeft() { return strafeLeft; }

	/**
	 * Checks if is strafe right.
	 *
	 * @return true, if is strafe right
	 */
	protected boolean isStrafeRight() { return strafeRight; }

	/**
	 * Gets the renderer.
	 *
	 * @return the renderer
	 */
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
				switch (e.keyCode) {
					case SWT.ARROW_LEFT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction) { AbstractCamera.this.strafeLeft = true; }
						break;
					case SWT.ARROW_RIGHT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction) { AbstractCamera.this.strafeRight = true; }
						break;
					case SWT.ARROW_UP:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction) { AbstractCamera.this.goesForward = true; }
						break;
					case SWT.ARROW_DOWN:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						if (cameraInteraction) { AbstractCamera.this.goesBackward = true; }
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

		invokeOnGLThread(drawable -> {
			if (!keystoneMode) {
				switch (e.getKeySymbol()) {
					// We need to register here all the keystrokes used in the Workbench and on the view, as they might
					// be caught by the NEWT key listener. Those dedicated to modelling are left over for the moment
					// (like CTRL+SHIFT+H)
					// First the global keystrokes
					case com.jogamp.newt.event.KeyEvent.VK_ESCAPE:
						GAMA.getGui().toggleFullScreenMode();
						break;
					case 'p':
					case 'P':
						if (isControlDown(e)) {
							if (e.isShiftDown()) {
								GAMA.stepFrontmostExperiment();
							} else {
								GAMA.startPauseFrontmostExperiment();
							}
						}
						break;
					case 'R':
					case 'r':
						if (isControlDown(e)) {
							if (e.isShiftDown()) {
								GAMA.relaunchFrontmostExperiment();
							} else {
								GAMA.reloadFrontmostExperiment();
							}
						}
						break;
					case 'X':
					case 'x':
						if (isControlDown(e) && e.isShiftDown()) { GAMA.closeAllExperiments(true, false); }
						break;

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
				switch (e.getKeyCode()) {
					// Finally the keystrokes for the display itself
					case com.jogamp.newt.event.KeyEvent.VK_LEFT:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction) { AbstractCamera.this.strafeLeft = true; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_RIGHT:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction) { AbstractCamera.this.strafeRight = true; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_UP:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction) { AbstractCamera.this.goesForward = true; }
						break;
					case com.jogamp.newt.event.KeyEvent.VK_DOWN:
						setCtrlPressed(isControlDown(e));
						if (cameraInteraction) { AbstractCamera.this.goesBackward = true; }
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
	protected void resetPivot() {}

	/**
	 * Quick left turn.
	 */
	protected void quickLeftTurn() {}

	/**
	 * Quick right turn.
	 */
	protected void quickRightTurn() {}

	/**
	 * Quick up turn.
	 */
	protected void quickUpTurn() {}

	/**
	 * Quick down turn.
	 */
	protected void quickDownTurn() {}

	/**
	 * Activate keystone mode.
	 */
	protected final void activateKeystoneMode() {
		if (!keystoneMode) {
			getRenderer().getSurface().zoomFit();
			getRenderer().getKeystoneHelper().startDrawHelper();
		} else {
			getRenderer().getKeystoneHelper().stopDrawHelper();
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
	 * Sets the target.
	 *
	 * @param centre
	 *            the new target
	 */
	public void setTarget(final GamaPoint centre) {
		setTarget(centre.x, centre.y, centre.z);

	}

	/**
	 * Sets the position.
	 *
	 * @param centre
	 *            the new position
	 */
	public void setPosition(final GamaPoint centre) {
		setPosition(centre.x, centre.y, centre.z);

	}

	@Override
	public void setInitialZFactorCorrector(final double corrector) { zCorrector = corrector; }

	/**
	 * Gets the initial Z factor.
	 *
	 * @return the initial Z factor
	 */
	public double getInitialZFactor() {
		if (renderer.getData().isDrawEnv()) return 1.46 / zCorrector;
		return 1.2 / zCorrector;
	}

}
