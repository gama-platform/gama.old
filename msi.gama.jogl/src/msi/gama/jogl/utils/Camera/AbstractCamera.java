package msi.gama.jogl.utils.Camera;

import java.awt.*;
import java.awt.event.*;
import java.nio.IntBuffer;
import java.util.Iterator;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import msi.gama.common.util.GuiUtils;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Camera.Arcball.Vector3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import com.sun.opengl.util.BufferUtil;

public abstract class AbstractCamera implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	protected JOGLAWTGLRenderer myRenderer;

	protected boolean isMacOS = false;

	protected final IntBuffer selectBuffer = BufferUtil.newIntBuffer(1024);// will store information

	// picking
	public boolean isPickedPressed = false;
	public Point mousePosition;

	// ROI Drawing
	public boolean enableROIDrawing = false;

	protected double maxDim;

	// To handle mouse event
	public int lastxPressed;
	public int lastyPressed;

	protected Vector3D _position;
	protected Vector3D _target;

	public double _theta;
	public double _phi;

	public final static double INIT_Z_FACTOR = 1.5;
	public double _keyboardSensivity;

	public boolean forward, backward, strafeLeft, strafeRight;
	public boolean ctrlKeyDown = false;
	public boolean shiftKeyDown = false;

	public double velocityHoriz, velocityVert = 0;

	public int _orientation;

	public AbstractCamera(final JOGLAWTGLRenderer renderer) {
		myRenderer = renderer;

		_position = new Vector3D();
		_target = new Vector3D();

		detectMacOS();
		mousePosition = new Point(0, 0);
	}

	public AbstractCamera(final double xPos, final double yPos, final double zPos, final double xLPos,
		final double yLPos, final double zLPos, final JOGLAWTGLRenderer renderer) {
		myRenderer = renderer;
		detectMacOS();
		mousePosition = new Point(0, 0);
	}

	public void updatePosition(final double xPos, final double yPos, final double zPos) {
		_position.x = xPos;
		_position.y = yPos;
		_position.z = zPos;
	}

	public void lookPosition(final double xLPos, final double yLPos, final double zLPos) {
		_target.x = xLPos;
		_target.y = yLPos;
		_target.z = zLPos;
	}

	public void setPosition(final Vector3D position) {
		this._position = position;
	}

	public void setTarget(final Vector3D target) {
		this._target = target;
	}

	public void setZPosition(final double z) {}

	public void vectorsFromAngles() {}

	public void moveXYPlan(final double diffx, final double diffy, final double speed) {}

	// Move in the XY plan by changing camera pos and look pos.
	public void moveXYPlan2(final double diffx, final double diffy, final double z, final double w, final double h) {

		double translationValue = 0;

		if ( Math.abs(diffx) > Math.abs(diffy) ) {// Move X

			translationValue = Math.abs(diffx) * ((z + 1) / w);

			if ( diffx > 0 ) {// move right
				updatePosition(_position.getX() - translationValue, _position.getY(), _position.getZ());
				lookPosition(_target.getX() - translationValue, _target.getY(), _target.getZ());
			} else {// move left
				updatePosition(_position.getX() + translationValue, _position.getY(), _position.getZ());
				lookPosition(_target.getX() + translationValue, _target.getY(), _target.getZ());
			}
		} else if ( Math.abs(diffx) < Math.abs(diffy) ) { // Move Y

			translationValue = Math.abs(diffy) * ((z + 1) / h);

			if ( diffy > 0 ) {// move down
				updatePosition(_position.getX(), _position.getY() + translationValue, _position.getZ());
				this.lookPosition(_target.getX(), _target.getY() + translationValue, _target.getZ());
			} else {// move up
				updatePosition(_position.getX(), _position.getY() - translationValue, _position.getZ());
				lookPosition(_target.getX(), _target.getY() - translationValue, _target.getZ());
			}
		}
	}

	public void moveForward(final double magnitude) {}

	public void strafeLeft(final double magnitude) {}

	public void strafeRight(final double magnitude) {}

	public void look(final double distanceAway) {}

	public void animate() {}

	/* -------Get commands--------- */

	public Vector3D getPosition() {
		return _position;
	}

	public Vector3D getTarget() {
		return _target;
	}

	public Vector3D getForward() {
		return null;
	}

	public Double getSpeed() {
		return null;
	}

	public void UpdateCamera(final GL gl, final GLU glu, final int width, final int height) {}

	public void initializeCamera(final double envWidth, final double envHeight) {}

	public void initialize3DCamera(final double envWidth, final double envHeight) {}

	/* -------------- Pitch and Yaw commands --------------- */

	public void pitchUp(final double amount) {}

	public void pitchDown(final double amount) {}

	public void yawRight(final double amount) {}

	public void yawLeft(final double amount) {}

	public double getPitch() {
		return 0;
	}

	public double getYaw() {
		return 0;
	}

	public ILocation getUpVector() {
		return null;
	}

	public void setUpVector(final ILocation upVector) {}

	/*---------------------------------------*/
	/*------------------ Events controls ---------------------*/

	@Override
	public void mouseWheelMoved(final MouseWheelEvent arg0) {}

	@Override
	public void mouseDragged(final MouseEvent arg0) {}

	@Override
	public void mouseMoved(final MouseEvent arg0) {
		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {}

	@Override
	public void mouseEntered(final MouseEvent arg0) {}

	@Override
	public void mouseExited(final MouseEvent arg0) {}

	@Override
	public void mousePressed(final MouseEvent arg0) {}

	@Override
	public void mouseReleased(final MouseEvent arg0) {}

	@Override
	public void keyPressed(final KeyEvent arg0) {}

	@Override
	public void keyReleased(final KeyEvent arg0) {}

	@Override
	public void keyTyped(final KeyEvent arg0) {}

	// add for check Meta Button pressed in MAC Os or Ctrl in Window OS
	protected boolean checkCtrlKeyDown(final MouseEvent mouseEvent) {
		boolean specicalKeyDown = false;
		if ( isMacOS ) {
			specicalKeyDown = mouseEvent.isMetaDown();
		} else {
			specicalKeyDown = mouseEvent.isShiftDown();
		}
		return specicalKeyDown;
	}

	// add for check Meta Button pressed in MAC Os or Ctrl in Window OS
	protected boolean checkCtrlKeyDown(final KeyEvent event) {
		boolean specicalKeyDown = false;
		if ( isMacOS ) {
			specicalKeyDown = event.isMetaDown();
		} else {
			specicalKeyDown = event.isShiftDown();
		}
		return specicalKeyDown;
	}

	protected boolean checkShiftKeyDown(final KeyEvent event) {
		return event.isShiftDown();
	}

	protected boolean checkShiftKeyDown(final MouseEvent event) {
		return event.isShiftDown();
	}

	protected boolean detectMacOS() {
		String os = System.getProperty("os.name");
		if ( "Mac OS X".equals(os) ) {
			isMacOS = true;
		}
		return isMacOS;
	}

	protected boolean isArcBallOn(final MouseEvent mouseEvent) {
		if ( checkCtrlKeyDown(mouseEvent) || myRenderer.displaySurface.arcball == true ) {
			return true;
		} else {
			return false;
		}
	}

	// Picking method
	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * First pass pepare select buffer for select mode by clearing it,
	 * prepare openGL to select mode and tell it where should draw
	 * object by using gluPickMatrix() method
	 * @return if returned value is true that mean the picking is enabled
	 */
	public boolean beginPicking(final GL gl) {
		if ( !isPickedPressed ) { return false; }
		GLU glu = new GLU();

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
		gl.glRenderMode(GL.GL_SELECT);

		/*
		 * The application must redefine the viewing volume so that it renders only a small
		 * area around the place where the mouse was clicked. In order to do that it is
		 * necessary to set the matrix mode to GL_PROJECTION. Afterwards, the application
		 * should push the current matrix to save the normal rendering mode settings.
		 * Next initialise the matrix
		 */

		gl.glMatrixMode(GL.GL_PROJECTION);
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
		glu.gluPickMatrix(mousePosition.x, height - mousePosition.y, 4, 4, viewport, 0);

		this.UpdateCamera(gl, glu, width, height);
		// FIXME: Comment GL_MODELVIEW to debug3D picking (redraw the model when clicking)
		gl.glMatrixMode(GL.GL_MODELVIEW);
		// 4. After this pass you must draw Objects

		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	/**
	 * After drawing we have to calculate which object was nearest screen and return its index
	 * @return name of selected object
	 */
	public int endPicking(final GL gl) {
		if ( !isPickedPressed ) { return -1; }
		isPickedPressed = false;// no further iterations
		int selectedIndex;

		// 5. When you back to Render mode gl.glRenderMode() methods return number of hits
		int howManyObjects = gl.glRenderMode(GL.GL_RENDER);

		// 6. Restore to normal settings
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		// 7. Seach the select buffer to find the nearest object

		// code below derive which ocjects is nearest from monitor
		//
		if ( howManyObjects > 0 ) {
			// simple searching algorithm
			selectedIndex = selectBuffer.get(3);
			int mindistance = Math.abs(selectBuffer.get(1));
			for ( int i = 0; i < howManyObjects; i++ ) {

				if ( mindistance < Math.abs(selectBuffer.get(1 + i * 4)) ) {

					mindistance = Math.abs(selectBuffer.get(1 + i * 4));
					selectedIndex = selectBuffer.get(3 + i * 4);

				}

			}
			// end of searching
		} else {
			selectedIndex = -1;// return -1 of there was no hits
		}

		return selectedIndex;
	}

	public double getMaxDim() {
		return maxDim;
	}

	public void setMaxDim(final double maxDim) {
		this.maxDim = maxDim;
	}

	public void followAgent(final IAgent a) {}

	public Double getRadius() {
		return null;
	}

	public void setRadius(final double r) {}

	public void PrintParam() {
		System.out.println("xPos:" + _position.x + " yPos:" + _position.y + " zPos:" + _position.z);
		System.out.println("xLPos:" + _target.x + " yLPos:" + _target.y + " zLPos:" + _target.z);
		System.out.println("_phi " + _phi + " _theta " + _theta);
	}

	public void rotation() {
		// TODO Auto-generated method stub
	}

	public void buildMenus(final Iterator<IAgent> agents) {
		myRenderer.displaySurface.agentsMenu.removeAll();

		final Menu macroMenu = new Menu("Species ");
		myRenderer.displaySurface.agentsMenu.add(macroMenu);

		final Menu allMenu = new Menu("Selected");
		macroMenu.add(allMenu);

		final MenuItem inspectItem = new MenuItem("Inspect Selected");
		ActionListener menuListener = null;
		menuListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				while (agents.hasNext()) {
					IAgent agent = agents.next();
					GuiUtils.setSelectedAgent(agent);
				}
			}

		};
		inspectItem.addActionListener(menuListener);
		allMenu.add(inspectItem);

		myRenderer.displaySurface.agentsMenu
			.show(myRenderer.displaySurface, this.mousePosition.x, this.mousePosition.y);
	}

	public abstract boolean IsViewIn2DPlan();

	public void inertia() {}
}
