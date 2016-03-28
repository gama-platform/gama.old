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
import java.util.Collection;
import org.eclipse.swt.SWT;
import com.jogamp.common.nio.Buffers;
import com.jogamp.nativewindow.swt.SWTAccessor;
import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.JOGLRenderer;

// import java.awt.event.*;

public abstract class AbstractCamera implements ICamera {

	private JOGLRenderer renderer;
	protected final IntBuffer selectBuffer = Buffers.newDirectIntBuffer(1024);// will store information

	// picking
	private boolean isPickedPressed = false;

	// Mouse
	private Point mousePosition;
	protected Point lastMousePressedPosition;
	protected Point firstMousePressedPosition;
	protected boolean firsttimeMouseDown = true;

	protected final GamaPoint position = new GamaPoint(0, 0, 0);
	protected final GamaPoint target = new GamaPoint(0, 0, 0);
	protected final GamaPoint upVector = new GamaPoint(0, 0, 0);

	protected double theta;
	protected double phi;

	private final double _keyboardSensivity = 4.0;
	private final double _sensivity = 1;

	// Mouse and keyboard state
	private boolean goesForward;
	private boolean goesBackward;
	private boolean strafeLeft;
	private boolean strafeRight;
	// private final boolean ctrlKeyDown = false;
	private boolean shiftKeyDown = false;
	private boolean altKeyDown = false;

	public AbstractCamera(final JOGLRenderer renderer) {
		setRenderer(renderer);
		setMousePosition(new Point(0, 0));
		upPosition(0.0, 1.0, 0.0);
	}

	public void updateSphericalCoordinatesFromLocations() {
	}

	@Override
	public void update() {
		LayeredDisplayData data = renderer.data;
		if ( data.isCameraLock() ) {
			ILocation cameraPos = data.getCameraPos();
			if ( cameraPos != LayeredDisplayData.getNoChange() ) {
				updatePosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
			}
			ILocation camLookPos = data.getCameraLookPos();
			if ( camLookPos != LayeredDisplayData.getNoChange() ) {
				lookPosition(camLookPos.getX(), camLookPos.getY(), camLookPos.getZ());
			}
			
			if ( phi < 360 && phi > 180 ) {
				upPosition(0, 0, -1);
			} else {
				upPosition(0,0,1);
			}
			
			updateSphericalCoordinatesFromLocations();
			drawRotationHelper();
		}

	}

	public void updatePosition(final double xPos, final double yPos, final double zPos) {
		position.setLocation(xPos, yPos, zPos);
	}

	public void lookPosition(final double xLPos, final double yLPos, final double zLPos) {
		target.setLocation(xLPos, yLPos, zLPos);
	}

	public void upPosition(final double xPos, final double yPos, final double zPos) {
		upVector.setLocation(xPos, yPos, zPos);
	}
	
	protected void drawRotationHelper() {
		
	}

	/* -------Get commands--------- */

	@Override
	public GamaPoint getPosition() {
		return position;
	}

	@Override
	public void animate() {
		renderer.getGlu().gluLookAt(position.x, position.y, position.z, target.x, target.y, target.z, upVector.x, upVector.y,
			upVector.z);
	}




	/*------------------ Events controls ---------------------*/

	/**
	 * Method mouseScrolled()
	 * @see org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseScrolled(final org.eclipse.swt.events.MouseEvent e) {
		zoom(e.count > 0);
	}

	/**
	 * Method mouseMove()
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseMove(final org.eclipse.swt.events.MouseEvent e) {
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;
	}

	/**
	 * Method mouseEnter()
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseEnter(final org.eclipse.swt.events.MouseEvent e) {}

	/**
	 * Method mouseExit()
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseExit(final org.eclipse.swt.events.MouseEvent e) {}

	/**
	 * Method mouseHover()
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseHover(final org.eclipse.swt.events.MouseEvent e) {}

	/**
	 * Method mouseDoubleClick()
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(final org.eclipse.swt.events.MouseEvent e) {
		// Already taken in charge by the ZoomListener in the view
		// getRenderer().displaySurface.zoomFit();
	}

	/**
	 * Method mouseDown()
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(final org.eclipse.swt.events.MouseEvent e) {
		if ( firsttimeMouseDown ) {
			firstMousePressedPosition = new Point(e.x, e.y);
			firsttimeMouseDown = false;
		}
		lastMousePressedPosition = new Point(e.x, e.y);
		// Activate Picking when press and right click
		if ( e.button == 3 ) {
			this.isPickedPressed = true;
			getRenderer().setPicking(true);
			// myRenderer.drawPickableObjects();
		} else {
			if ( shift(e) || alt(e) ) {
				getMousePosition().x = e.x;
				getMousePosition().y = e.y;
				renderer.defineROI(firstMousePressedPosition, getMousePosition());
			} else {
				getRenderer().setPicking(false);
			}
		}
		getMousePosition().x = e.x;
		getMousePosition().y = e.y;
	}

	/**
	 * Method mouseUp()
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(final org.eclipse.swt.events.MouseEvent e) {
		firsttimeMouseDown = true;
		if ( canSelectOnRelease(e) && isViewIn2DPlan() ) {
			if ( alt(e) ) {
				final Envelope3D env = renderer.getROIEnvelope();

				if ( env != null ) {
					env.init(env.getMinX(), env.getMaxX(), -env.getMinY(), -env.getMaxY());
					Collection<IAgent> shapes = GAMA.run(new InScope<Collection<IAgent>>() {

						@Override
						public Collection<IAgent> run(final IScope scope) {
							return scope.getTopology().getSpatialIndex().allInEnvelope(scope, env.centre(), env,
								new Different(), true);
						}
					});
					// System.out.println("Envelope : " + env);

					renderer.getSurface().selectSeveralAgents(shapes);
				}
			} else if ( shift(e) ) {
				final Envelope3D env = renderer.getROIEnvelope();
				zoomRoi(env);
			}
			renderer.cancelROI();
		}

	}

	protected abstract void zoomRoi(Envelope3D env);

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

	protected static boolean alt(final org.eclipse.swt.events.MouseEvent e) {
		return (e.stateMask & SWT.ALT) != 0;
	}

	protected static boolean alt(final org.eclipse.swt.events.KeyEvent e) {
		return (e.stateMask & SWT.ALT) != 0;
	}

	protected boolean isArcBallOn(final org.eclipse.swt.events.MouseEvent e) {
		if ( ctrl(e) && getRenderer().data.isArcBallDragOn() ) { return false; }
		if ( ctrl(e) || getRenderer().data.isArcBallDragOn() ) { return true; }
		return false;
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * First pass pepare select buffer for select mode by clearing it,
	 * prepare openGL to select mode and tell it where should draw
	 * object by using gluPickMatrix() method
	 * @return if returned value is true that mean the picking is enabled
	 */
	@Override
	public boolean beginPicking(final GL2 gl) {
		if ( !isPickedPressed ) { return false; }
		GLU glu = renderer.getGlu();

		// 1. Selecting buffer
		selectBuffer.clear(); // prepare buffer for new objects
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);// add buffer to openGL

		// Pass below is very similar to refresh method in GLrenderer
		// 2. Take the viewport attributes,
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		int width = viewport[2]; // get width and
		int height = viewport[3]; // height from viewport

		// 3. Prepare openGL for rendering in select mode
		gl.glRenderMode(GL2.GL_SELECT);

		/*
		 * The application must redefine the viewing volume so that it renders only a small
		 * area around the place where the mouse was clicked. In order to do that it is
		 * necessary to set the matrix mode to GL_PROJECTION. Afterwards, the application
		 * should push the current matrix to save the normal rendering mode settings.
		 * Next initialise the matrix
		 */

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		/*
		 * Define the viewing volume so that rendering is done only in a small area around
		 * the cursor. gluPickMatrix method restrict the area where openGL will drawing objects
		 *
		 * OpenGL has a different origin for its window coordinates than the operation system.
		 * The second parameter provides for the conversion between the two systems, i.e. it
		 * transforms the origin from the upper left corner, into the bottom left corner
		 */
		glu.gluPickMatrix(getMousePosition().x, height - getMousePosition().y, 4, 4, viewport, 0);

		// FIXME Why do we have to call updatePerspective() here ?
		renderer.updatePerspective(gl);
		// Comment GL_MODELVIEW to debug3D picking (redraw the model when clicking)
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// 4. After this pass you must draw Objects

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and return its index
	 * @return name of selected object
	 */
	@Override
	public int endPicking(final GL2 gl) {
		if ( !isPickedPressed ) { return -1; }
		this.isPickedPressed = false;// no further iterations
		int selectedIndex;

		// 5. When you back to Render mode gl.glRenderMode() methods return number of hits
		int howManyObjects = gl.glRenderMode(GL2.GL_RENDER);

		// 6. Restore to normal settings
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		// 7. Seach the select buffer to find the nearest object

		// code below derive which ocjects is nearest from monitor
		//
		if ( howManyObjects > 0 ) {
			// simple searching algorithm
			selectedIndex = selectBuffer.get(3);
			int mindistance = CmnFastMath.abs(selectBuffer.get(1));
			for ( int i = 0; i < howManyObjects; i++ ) {

				if ( mindistance < CmnFastMath.abs(selectBuffer.get(1 + i * 4)) ) {

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

	public boolean isViewIn2DPlan() {
		return phi > 85 && phi < 95 && theta > -5 && theta < 5;
	}


	@Override
	public Point getLastMousePressedPosition() {
		return lastMousePressedPosition;
	}

	protected double get_keyboardSensivity() {
		return _keyboardSensivity;
	}

	protected double get_sensivity() {
		return _sensivity;
	}

	protected boolean isShiftKeyDown() {
		return shiftKeyDown;
	}

	protected boolean isAltKeyDown() {
		return altKeyDown;
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
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(final org.eclipse.swt.events.KeyEvent e) {
		this.shiftKeyDown = shift(e);
		this.altKeyDown = alt(e);
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
		}

	}
	
	protected void resetPivot() {}
	protected void quickLeftTurn() {}
	protected void quickRightTurn() {}
	protected void quickUpTurn() {}
	protected void quickDownTurn() {}

	/**
	 * Method keyReleased()
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(final org.eclipse.swt.events.KeyEvent e) {

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
		}

	}

}
