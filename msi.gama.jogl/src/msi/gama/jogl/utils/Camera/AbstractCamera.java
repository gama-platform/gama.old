package msi.gama.jogl.utils.Camera;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Camera.Arcball.Vector3D;
import msi.gama.metamodel.shape.ILocation;



public abstract class AbstractCamera implements KeyListener, MouseListener, 
							MouseMotionListener, MouseWheelListener{
	
	
	protected JOGLAWTGLRenderer myRenderer;
	
	// Draw the model on 0,0,0 coordinate
	public boolean isModelCentered = true;
	
	protected boolean isMacOS = false;
	
	protected final IntBuffer selectBuffer = BufferUtil.newIntBuffer(1024);// will store information

	// picking
	public boolean isPickedPressed = false;
	public final Point mousePosition;

	// ROI Drawing
	public boolean enableROIDrawing = false;
	
	protected double maxDim;
	
	// To handle mouse event
	public int lastxPressed;
	public int lastyPressed;
	
	protected Vector3D _position;
	protected Vector3D _target;
	
	public final static double INIT_Z_FACTOR = 1.5;
	
	public AbstractCamera(JOGLAWTGLRenderer renderer) {
		myRenderer = renderer;
		
    	_position = new Vector3D();
    	_target = new Vector3D();
		
		detectMacOS();
		mousePosition = new Point(0, 0);
	}
	
	public AbstractCamera(double xPos, double yPos, double zPos,
				double xLPos, double yLPos, double zLPos,JOGLAWTGLRenderer renderer) {
		myRenderer = renderer;
		detectMacOS();
		mousePosition = new Point(0, 0);
	}
	
	public void updatePosition(double xPos, double yPos, double zPos) {}

	public void lookPosition(double xLPos, double yLPos, double zLPos) {}
	
	public void moveXYPlan(double diffx, double diffy, double speed) {}
	
	public void moveXYPlan2(double diffx, double diffy, double z, double w, double h) {}
	
	public void moveForward(double magnitude) {}
	
	public void strafeLeft(double magnitude) {}
	
	public void strafeRight(double magnitude) {}
	
	public void look(double distanceAway) {}

	/* -------Get commands--------- */

	public Vector3D getPosition()
	{
		return _position;
	}
	
	public Vector3D getTarget()
	{
		return _target;
	}
	
	public double getPitch() {
		return 0;}

	public double getYaw() {
		return 0;}

	public void UpdateCamera(GL gl, GLU glu, int width, int height) {}

	public void initializeCamera(double envWidth, double envHeight) {}

	public void initialize3DCamera(double envWidth, double envHeight) {}

	public void PrintParam() {}

	/* --------------------------- */

	/* -------------- Pitch and Yaw commands --------------- */

	public void pitchUp(double amount) {}

	public void pitchDown(double amount) {}

	public void yawRight(double amount) {}

	public void yawLeft(double amount) {}

	public ILocation getUpVector() {
		return null;}

	public void setUpVector(ILocation upVector) {}
	
	
	/*---------------------------------------*/
	/*------------------ Events controls ---------------------*/
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {}

	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	// add for check Meta Button pressed in MAC Os or Ctrl in Window OS
	protected boolean checkCtrlKeyDown(MouseEvent mouseEvent) {
		boolean specicalKeyDown = false;
		if ( isMacOS ) {
			specicalKeyDown = mouseEvent.isMetaDown();
		} else {
			specicalKeyDown = mouseEvent.isControlDown();
		}
		return specicalKeyDown;
	}

	protected boolean detectMacOS() {
		String os = System.getProperty("os.name");
		if ( "Mac OS X".equals(os) ) {
			isMacOS = true;
		}
		return isMacOS;
	}

	protected boolean isArcBallOn(MouseEvent mouseEvent) {
		return false;}
	
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

	public void circleCamera(double envWidth, double envHeight, double x,
			double y, double xlpos, double ylpos) {
		// TODO Auto-generated method stub
		
	}
	
	public double getMaxDim() {
		return maxDim;
	}
	
	public void setMaxDim(double maxDim) {
		this.maxDim = maxDim;
	}

}
