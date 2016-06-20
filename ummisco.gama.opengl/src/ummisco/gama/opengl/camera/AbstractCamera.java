/*********************************************************************************************
 *
 *
 * 'AbstractCamera.java', in plugin 'msi.gama.jogl2', is part of the source code of the
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
import org.eclipse.swt.events.MouseEvent;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLRunnable;

import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.LayeredDisplayData;
import msi.gaml.operators.Maths;
import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.ui.bindings.GamaKeyBindings;

public abstract class AbstractCamera implements ICamera {

	private JOGLRenderer renderer;

	// picking
	// private boolean isPickedPressed = false;

	// Mouse
	private Point mousePosition;
	protected Point lastMousePressedPosition = new Point(0, 0);
	protected Point firstMousePressedPosition;
	protected boolean firsttimeMouseDown = true;
	protected boolean cameraInteraction = true;

	protected final GamaPoint position = new GamaPoint(0, 0, 0);
	protected final GamaPoint target = new GamaPoint(0, 0, 0);
	protected final GamaPoint upVector = new GamaPoint(0, 0, 0);

	protected double theta;
	protected double phi;
	protected boolean flipped = false;
	protected double upVectorAngle;

	private final double _keyboardSensivity = 4.0;
	private final double _sensivity = 1;

	// Mouse and keyboard state
	private boolean goesForward;
	private boolean goesBackward;
	private boolean strafeLeft;
	private boolean strafeRight;

	private volatile boolean ROICurrentlyDrawn = false;
	private volatile boolean isROISticky = false;

	protected boolean ctrlPressed = false;
	protected boolean shiftPressed = false;

	public AbstractCamera(final JOGLRenderer renderer) {
		setRenderer(renderer);
		setMousePosition(new Point(0, 0));
		upVectorAngle = 0.0;
		upPosition(0.0, 1.0, 0.0);
	}

	public void updateSphericalCoordinatesFromLocations() {
	}

	@Override
	public void toggleStickyROI() {
		isROISticky = !isROISticky;
	}

	@Override
	public boolean isROISticky() {
		return isROISticky;
	}

	@Override
	public void update() {
		final LayeredDisplayData data = renderer.data;
		cameraInteraction = !data.cameraInteractionDisabled();
		if (data.isCameraLock()) {
			final ILocation cameraPos = data.getCameraPos();
			if (cameraPos != LayeredDisplayData.getNoChange()) {
				updatePosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
			}
			final ILocation camLookPos = data.getCameraLookPos();
			if (camLookPos != LayeredDisplayData.getNoChange()) {
				lookPosition(camLookPos.getX(), camLookPos.getY(), camLookPos.getZ());
			}
			final ILocation camLookUpVector = data.getCameraUpVector();
			if (camLookUpVector != LayeredDisplayData.getNoChange()) {
				upPosition(camLookUpVector.getX(), camLookUpVector.getY(), camLookUpVector.getZ());
			}
			if (cameraInteraction) { // cameraInteractionDisabled is true when
										// the camera_interaction facet is
										// turned to false.
				if (flipped)
					upPosition(
							-(-FastMath.cos(theta * Maths.toRad) * FastMath.cos(phi * Maths.toRad)
									* FastMath.cos(upVectorAngle * Maths.toRad)
									- FastMath.sin(theta * Maths.toRad) * FastMath.sin(upVectorAngle * Maths.toRad)),
							-(-FastMath.sin(theta * Maths.toRad) * FastMath.cos(phi * Maths.toRad)
									* FastMath.cos(upVectorAngle * Maths.toRad + FastMath.cos(theta * Maths.toRad)
											* FastMath.sin(upVectorAngle * Maths.toRad))),
							-(FastMath.sin(phi * Maths.toRad) * FastMath.cos(upVectorAngle * Maths.toRad)));
				else
					upPosition(
							-FastMath.cos(theta * Maths.toRad) * FastMath.cos(phi * Maths.toRad)
									* FastMath.cos(upVectorAngle * Maths.toRad)
									- FastMath.sin(theta * Maths.toRad) * FastMath.sin(upVectorAngle * Maths.toRad),
							-FastMath.sin(theta * Maths.toRad) * FastMath.cos(phi * Maths.toRad)
									* FastMath.cos(upVectorAngle * Maths.toRad + FastMath.cos(theta * Maths.toRad)
											* FastMath.sin(upVectorAngle * Maths.toRad)),
							FastMath.sin(phi * Maths.toRad) * FastMath.cos(upVectorAngle * Maths.toRad));
				drawRotationHelper();
			}
			updateSphericalCoordinatesFromLocations();
		}
	}

	protected abstract void drawRotationHelper();

	public void updatePosition(final double xPos, final double yPos, final double zPos) {
		position.setLocation(xPos, yPos, zPos);
	}

	public void lookPosition(final double xLPos, final double yLPos, final double zLPos) {
		target.setLocation(xLPos, yLPos, zLPos);
	}

	public void upPosition(final double xPos, final double yPos, final double zPos) {
		upVector.setLocation(xPos, yPos, zPos);
	}

	/* -------Get commands--------- */

	@Override
	public GamaPoint getPosition() {
		return position;
	}

	@Override
	public GamaPoint getTarget() {
		return target;
	}

	@Override
	public GamaPoint getOrientation() {
		return upVector;
	}

	@Override
	public void animate() {
		renderer.getGlu().gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, upVector.x,
				upVector.y, upVector.z);
	}

	/*------------------ Events controls ---------------------*/

	final void setShiftPressed(final boolean value) {
		shiftPressed = value;
	}

	final void setCtrlPressed(final boolean value) {
		ctrlPressed = value;
	}

	protected void setMouseLeftPressed(final boolean b) {
		// TODO Auto-generated method stub

	}

	/**
	 * Method mouseScrolled()
	 * 
	 * @see org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseScrolled(final MouseEvent e) {
		renderer.getDrawable().invoke(false, new GLRunnable() {

			@Override
			public boolean run(final GLAutoDrawable drawable) {
				if (cameraInteraction) {
					internalMouseScrolled(e);
				}
				return false;
			}
		});

	}

	protected void internalMouseScrolled(final MouseEvent e) {
		zoom(e.count > 0);
	}

	/**
	 * Method mouseMove()
	 * 
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseMove(final org.eclipse.swt.events.MouseEvent e) {

		renderer.getDrawable().invoke(false, new GLRunnable() {

			@Override
			public boolean run(final GLAutoDrawable drawable) {
				if (cameraInteraction) {
					internalMouseMove(e);
				}
				return false;
			}

		});

	}

	protected void internalMouseMove(final MouseEvent e) {
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;
		setCtrlPressed(GamaKeyBindings.ctrl(e));
		setShiftPressed(GamaKeyBindings.shift(e));
	}

	/**
	 * Method mouseEnter()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseEnter(final org.eclipse.swt.events.MouseEvent e) {
	}

	/**
	 * Method mouseExit()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseExit(final org.eclipse.swt.events.MouseEvent e) {
	}

	/**
	 * Method mouseHover()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseHover(final org.eclipse.swt.events.MouseEvent e) {
	}

	/**
	 * Method mouseDoubleClick()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseDoubleClick(final org.eclipse.swt.events.MouseEvent e) {
		// Already taken in charge by the ZoomListener in the view
		// getRenderer().displaySurface.zoomFit();
	}

	/**
	 * Method mouseDown()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseDown(final org.eclipse.swt.events.MouseEvent e) {
		renderer.getDrawable().invoke(false, new GLRunnable() {

			@Override
			public boolean run(final GLAutoDrawable drawable) {
				if (cameraInteraction) {
					internalMouseDown(e);
				}
				return false;
			}
		});

	}

	protected void internalMouseDown(final MouseEvent e) {
		// System.out.println("Detecting mouse down in camera");
		if (firsttimeMouseDown) {
			firstMousePressedPosition = new Point(e.x, e.y);
			firsttimeMouseDown = false;
		}
		lastMousePressedPosition = new Point(e.x, e.y);
		// Activate Picking when press and right click
		// if (e.button == 3) {
		// if (renderer.mouseInROI(lastMousePressedPosition)) {
		// renderer.getSurface().selectionIn(renderer.getROIEnvelope());
		// }
		// else {
		// renderer.getPickingState().setPicking(true);
		// }

		// } else
		if (e.button == 2) { // mouse wheel
			resetPivot();
		} else {
			if (GamaKeyBindings.shift(e) && isViewInXYPlan()) {
				startROI(e);
			}
			// else {
			// renderer.getPickingState().setPicking(false);
			// }
		}
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;

		setMouseLeftPressed(e.button == 1 ? true : false);
		setCtrlPressed(e.button == 1 ? GamaKeyBindings.ctrl(e) : false);
		setShiftPressed(e.button == 1 ? GamaKeyBindings.shift(e) : false);

	}

	/**
	 * Method mouseUp()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public final void mouseUp(final org.eclipse.swt.events.MouseEvent e) {

		renderer.getDrawable().invoke(false, new GLRunnable() {

			@Override
			public boolean run(final GLAutoDrawable drawable) {
				if (cameraInteraction) {
					internalMouseUp(e);
				}
				return false;
			}
		});

	}

	protected void internalMouseUp(final MouseEvent e) {

		firsttimeMouseDown = true;
		if (canSelectOnRelease(e) && isViewInXYPlan()) {
			if (GamaKeyBindings.shift(e)) {
				finishROISelection();
			}
		}
		if (e.button == 1)
			setMouseLeftPressed(false);

	}

	private void startROI(final org.eclipse.swt.events.MouseEvent e) {
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;
		renderer.defineROI(firstMousePressedPosition, getMousePosition());
		ROICurrentlyDrawn = true;
	}

	private void finishROISelection() {
		if (ROICurrentlyDrawn) {
			final Envelope3D env = renderer.getROIEnvelope();
			if (env != null) {
				renderer.getSurface().selectionIn(env);
			}
		}
	}

	protected abstract boolean canSelectOnRelease(org.eclipse.swt.events.MouseEvent arg0);
	//
	// protected void dump() {
	// System.out.println("xPos:" + position.x + " yPos:" + position.y + "
	// zPos:" + position.z);
	// System.out.println("xLPos:" + target.x + " yLPos:" + target.y + " zLPos:"
	// + target.z);
	// System.out.println("upX" + upVector.x + " upY:" + upVector.y + " upZ:" +
	// upVector.z);
	// System.out.println("_phi " + phi + " _theta " + theta);
	// }

	@Override
	public Point getMousePosition() {
		return mousePosition;
	}

	protected void setMousePosition(final Point mousePosition) {
		this.mousePosition = mousePosition;
	}

	public boolean isViewInXYPlan() {
		return true;
		// return phi > 170 || phi < 10;// && theta > -5 && theta < 5;
	}

	@Override
	public Point getLastMousePressedPosition() {
		return lastMousePressedPosition;
	}

	protected double getKeyboardSensivity() {
		return _keyboardSensivity;
	}

	protected double getSensivity() {
		return _sensivity;
	}

	protected boolean isForward() {
		return goesForward;
	}

	protected boolean isBackward() {
		return goesBackward;
	}

	protected boolean isStrafeLeft() {
		return strafeLeft;
	}

	protected boolean isStrafeRight() {
		return strafeRight;
	}

	protected JOGLRenderer getRenderer() {
		return renderer;
	}

	protected void setRenderer(final JOGLRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Method keyPressed()
	 * 
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public final void keyPressed(final org.eclipse.swt.events.KeyEvent e) {

		renderer.getDrawable().invoke(false, new GLRunnable() {

			@Override
			public boolean run(final GLAutoDrawable drawable) {
				if (cameraInteraction) {
					switch (e.keyCode) {
					case SWT.ARROW_LEFT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						AbstractCamera.this.strafeLeft = true;
						break;
					case SWT.ARROW_RIGHT:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						AbstractCamera.this.strafeRight = true;
						break;
					case SWT.ARROW_UP:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						AbstractCamera.this.goesForward = true;
						break;
					case SWT.ARROW_DOWN:
						setCtrlPressed(GamaKeyBindings.ctrl(e));
						AbstractCamera.this.goesBackward = true;
						break;
					case SWT.SPACE:
						resetPivot();
						break;
					case SWT.CTRL:
						setCtrlPressed(!firsttimeMouseDown);
						break;
					case SWT.COMMAND:
						setCtrlPressed(!firsttimeMouseDown);
						break;
					// case SWT.SHIFT:
					// setShiftPressed(true);
					// break;
					}
					switch (e.character) {
					case '+':
						zoom(true);
						break;
					case '-':
						zoom(false);
						break;
					case '4':
						quickLeftTurn();
						break;
					case '6':
						quickRightTurn();
						break;
					case '8':
						quickUpTurn();
						break;
					case '2':
						quickDownTurn();
						break;
					default:
						return true;
					}
				}
				return false;
			}
		});
	}

	protected void resetPivot() {
	}

	protected void quickLeftTurn() {
	}

	protected void quickRightTurn() {
	}

	protected void quickUpTurn() {
	}

	protected void quickDownTurn() {
	}

	/**
	 * Method keyReleased()
	 * 
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public final void keyReleased(final org.eclipse.swt.events.KeyEvent e) {

		renderer.getDrawable().invoke(false, new GLRunnable() {

			@Override
			public boolean run(final GLAutoDrawable drawable) {
				if (cameraInteraction) {
					switch (e.keyCode) {
					case SWT.ARROW_LEFT: // player turns left (scene rotates
											// right)
						strafeLeft = false;
						break;
					case SWT.ARROW_RIGHT: // player turns right (scene rotates
											// left)
						strafeRight = false;
						break;
					case SWT.ARROW_UP:
						goesForward = false;
						break;
					case SWT.ARROW_DOWN:
						goesBackward = false;
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
			}
		});
	}

}
