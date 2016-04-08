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
import java.nio.IntBuffer;

import org.eclipse.swt.SWT;

import com.jogamp.common.nio.Buffers;
import com.jogamp.nativewindow.swt.SWTAccessor;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.LayeredDisplayData;
import msi.gaml.operators.Maths;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.JOGLRenderer;

public abstract class AbstractCamera implements ICamera {

	private JOGLRenderer renderer;
	protected final IntBuffer selectBuffer = Buffers.newDirectIntBuffer(1024);

	// picking
	private boolean isPickedPressed = false;

	// Mouse
	private Point mousePosition;
	protected Point lastMousePressedPosition;
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
	
	private boolean ROICurrentlyDrawn = false;

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
			if (cameraInteraction) { // cameraInteractionDisabled is true when the camera_interaction facet is turned to false.
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
	public void animate() {
		renderer.getGlu().gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, upVector.x,
				upVector.y, upVector.z);
	}

	/*------------------ Events controls ---------------------*/

	final void setShiftPressed(final boolean value) {
		shiftPressed = value;
		drawRotationHelper();
	}

	final void setCtrlPressed(final boolean value) {
		ctrlPressed = value;
		drawRotationHelper();
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
	public void mouseScrolled(final org.eclipse.swt.events.MouseEvent e) {
		zoom(e.count > 0);
	}

	/**
	 * Method mouseMove()
	 * 
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseMove(final org.eclipse.swt.events.MouseEvent e) {
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;
	}

	/**
	 * Method mouseEnter()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseEnter(final org.eclipse.swt.events.MouseEvent e) {
	}

	/**
	 * Method mouseExit()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseExit(final org.eclipse.swt.events.MouseEvent e) {
	}

	/**
	 * Method mouseHover()
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseHover(final org.eclipse.swt.events.MouseEvent e) {
	}

	/**
	 * Method mouseDoubleClick()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(final org.eclipse.swt.events.MouseEvent e) {
		// Already taken in charge by the ZoomListener in the view
		// getRenderer().displaySurface.zoomFit();
	}

	/**
	 * Method mouseDown()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(final org.eclipse.swt.events.MouseEvent e) {
		if (cameraInteraction) {
			if (firsttimeMouseDown) {
				firstMousePressedPosition = new Point(e.x, e.y);
				firsttimeMouseDown = false;
			}
			lastMousePressedPosition = new Point(e.x, e.y);
			// Activate Picking when press and right click
			if (e.button == 3) {
				this.isPickedPressed = true;
				getRenderer().setPicking(true);
				// myRenderer.drawPickableObjects();
			} else if (e.button == 2) { // mouse wheel
				resetPivot();
			} else {
				if ((shift(e)) && isViewInXYPlan()) {
					startROI(e);
				} else {
					getRenderer().setPicking(false);
				}
			}
			getMousePosition().x = e.x;
			getMousePosition().y = e.y;
	
			setMouseLeftPressed((e.button == 1) ? true : false);
			setCtrlPressed((e.button == 1) ? ctrl(e) : false);
			setShiftPressed((e.button == 1) ? shift(e) : false);
		}
	}

	/**
	 * Method mouseUp()
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(final org.eclipse.swt.events.MouseEvent e) {
		if (cameraInteraction) {
			firsttimeMouseDown = true;
			if (canSelectOnRelease(e) && isViewInXYPlan() && shift(e)) {
				finishROISelection();
			}
			if (e.button == 1)
				setMouseLeftPressed(false);
		}
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
			renderer.cancelROI();
		}
	}

	protected abstract boolean canSelectOnRelease(org.eclipse.swt.events.MouseEvent arg0);

	protected static boolean ctrl(final org.eclipse.swt.events.MouseEvent e) {
		return SWTAccessor.isOSX && (e.stateMask & SWT.COMMAND) != 0 || (e.stateMask & SWT.CTRL) != 0;
	}

	protected static boolean ctrl(final org.eclipse.swt.events.KeyEvent e) {
		return SWTAccessor.isOSX && (e.stateMask & SWT.COMMAND) != 0 || (e.stateMask & SWT.CTRL) != 0;
	}

	protected static boolean shift(final org.eclipse.swt.events.MouseEvent e) {
		return (e.stateMask & SWT.SHIFT) != 0;
	}

	protected static boolean shift(final org.eclipse.swt.events.KeyEvent e) {
		return (e.stateMask & SWT.SHIFT) != 0;
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * First pass pepare select buffer for select mode by clearing it, prepare
	 * openGL to select mode and tell it where should draw object by using
	 * gluPickMatrix() method
	 * 
	 * @return if returned value is true that mean the picking is enabled
	 */
	@Override
	public boolean beginPicking(final GL2 gl) {
		if (!isPickedPressed) {
			return false;
		}
		final GLU glu = renderer.getGlu();

		// 1. Selecting buffer
		selectBuffer.clear(); // prepare buffer for new objects
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);// add buffer
																	// to openGL

		// Pass below is very similar to refresh method in GLrenderer
		// 2. Take the viewport attributes,
		final int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		final int width = viewport[2]; // get width and
		final int height = viewport[3]; // height from viewport

		// 3. Prepare openGL for rendering in select mode
		gl.glRenderMode(GL2.GL_SELECT);

		/*
		 * The application must redefine the viewing volume so that it renders
		 * only a small area around the place where the mouse was clicked. In
		 * order to do that it is necessary to set the matrix mode to
		 * GL_PROJECTION. Afterwards, the application should push the current
		 * matrix to save the normal rendering mode settings. Next initialise
		 * the matrix
		 */

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		/*
		 * Define the viewing volume so that rendering is done only in a small
		 * area around the cursor. gluPickMatrix method restrict the area where
		 * openGL will drawing objects
		 *
		 * OpenGL has a different origin for its window coordinates than the
		 * operation system. The second parameter provides for the conversion
		 * between the two systems, i.e. it transforms the origin from the upper
		 * left corner, into the bottom left corner
		 */
		glu.gluPickMatrix(getMousePosition().x, height - getMousePosition().y, 4, 4, viewport, 0);

		// FIXME Why do we have to call updatePerspective() here ?
		renderer.updatePerspective(gl);
		// Comment GL_MODELVIEW to debug3D picking (redraw the model when
		// clicking)
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// 4. After this pass you must draw Objects

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and
	 * return its index
	 * 
	 * @return name of selected object
	 */
	@Override
	public int endPicking(final GL2 gl) {
		if (!isPickedPressed) {
			return -1;
		}
		this.isPickedPressed = false;// no further iterations
		int selectedIndex;

		// 5. When you back to Render mode gl.glRenderMode() methods return
		// number of hits
		final int howManyObjects = gl.glRenderMode(GL2.GL_RENDER);

		// 6. Restore to normal settings
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		// 7. Seach the select buffer to find the nearest object

		// code below derive which ocjects is nearest from monitor
		//
		if (howManyObjects > 0) {
			// simple searching algorithm
			selectedIndex = selectBuffer.get(3);
			int mindistance = CmnFastMath.abs(selectBuffer.get(1));
			for (int i = 0; i < howManyObjects; i++) {

				if (mindistance < CmnFastMath.abs(selectBuffer.get(1 + i * 4))) {

					mindistance = CmnFastMath.abs(selectBuffer.get(1 + i * 4));
					selectedIndex = selectBuffer.get(3 + i * 4);

				}

			}
			// end of searching
		} else {
			selectedIndex = -2;// return -2 of there was no hits
		}

		return selectedIndex;
	}

	protected void dump() {
		System.out.println("xPos:" + position.x + " yPos:" + position.y + " zPos:" + position.z);
		System.out.println("xLPos:" + target.x + " yLPos:" + target.y + " zLPos:" + target.z);
		System.out.println("upX" + upVector.x + " upY:" + upVector.y + " upZ:" + upVector.z);
		System.out.println("_phi " + phi + " _theta " + theta);
	}

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
	public void keyPressed(final org.eclipse.swt.events.KeyEvent e) {
		// this.shiftKeyDown = shift(e);
		// this.altKeyDown = alt(e);
		// this.ctrlKeyDown = ctrl(e);
//		setCtrlPressed(ctrl(e));
//		setAltPressed(alt(e));
//		setShiftPressed(shift(e));
		if (cameraInteraction) {
			switch (e.keyCode) {
			case SWT.ARROW_LEFT:
				this.strafeLeft = true;
				break;
			case SWT.ARROW_RIGHT:
				this.strafeRight = true;
				break;
			case SWT.ARROW_UP:
				this.goesForward = true;
				break;
			case SWT.ARROW_DOWN:
				this.goesBackward = true;
				break;
			case SWT.SPACE:
				resetPivot();
				return;
			case SWT.CTRL:
				setCtrlPressed(true);
				break;
			case SWT.COMMAND:
				setCtrlPressed(true);
				break;
			case SWT.SHIFT:
				setShiftPressed(true);
				break;
			}
			switch (e.character) {
			case '+':
				zoom(true);
				return;
			case '-':
				zoom(false);
				return;
			case '4':
				quickLeftTurn();
				return;
			case '6':
				quickRightTurn();
				return;
			case '8':
				quickUpTurn();
				return;
			case '2':
				quickDownTurn();
				return;
			case 'f':
				flipView();
				return;
			}
		}

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

	protected void flipView() {
		flipped = !flipped;
	}

	/**
	 * Method keyReleased()
	 * 
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(final org.eclipse.swt.events.KeyEvent e) {
//		setCtrlPressed(ctrl(e));
//		setAltPressed(alt(e));
//		setShiftPressed(shift(e));
		if (cameraInteraction) {
			switch (e.keyCode) {
			case SWT.ARROW_LEFT: // player turns left (scene rotates right)
				this.strafeLeft = false;
				break;
			case SWT.ARROW_RIGHT: // player turns right (scene rotates left)
				this.strafeRight = false;
				break;
			case SWT.ARROW_UP:
				this.goesForward = false;
				break;
			case SWT.ARROW_DOWN:
				this.goesBackward = false;
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
			}
		}

	}

}
