package msi.gama.jogl.utils;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_UP;

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
import javax.swing.SwingUtilities;

import com.sun.opengl.util.BufferUtil;

import msi.gama.jogl.utils.Camera.Camera;


public class MyListener implements KeyListener, MouseListener,
		MouseMotionListener, MouseWheelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Camera myCamera;
	private JOGLAWTGLRenderer myRenderer;

	//To handle mouse event	
	int lastxPressed;
	int lastyPressed;
	
	private boolean isMacOS = false;
	
	//picking
	public boolean isPickedPressed = false;
	public final Point mousePosition;
	private final IntBuffer selectBuffer = BufferUtil.newIntBuffer(1024);// will store information
	
	//ROI Drawing
	public boolean enableROIDrawing=false;

	public MyListener(Camera camera) {
		myCamera = camera;
		detectMacOS();	
		mousePosition = new Point(0,0);
	}

	public MyListener(Camera camera, JOGLAWTGLRenderer renderer){
		myCamera = camera;
		myRenderer = renderer;
		detectMacOS();
		mousePosition = new Point(0,0);
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		
		if ( isArcBallOn(mouseEvent) && myCamera.isModelCentered) {
			if (SwingUtilities.isRightMouseButton(mouseEvent)) {
				myRenderer.reset();
			}
		} else {
			//myCamera.PrintParam();
			//System.out.println( "x:" + mouseEvent.getX() + " y:" + mouseEvent.getY());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		//Arcball
		
	
		if ( ((isArcBallOn(mouseEvent)) && myCamera.isModelCentered) || myRenderer.displaySurface.SelectRectangle) {
			//Arcball is not working with picking.
			if(!myRenderer.displaySurface.Picking){
				if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
					myRenderer.startDrag(mouseEvent.getPoint());
				}
			}	
			enableROIDrawing =true;
		} 
		
		lastxPressed = mouseEvent.getX();
		lastyPressed = mouseEvent.getY();
			
		
		//Picking mode
		if(myRenderer.displaySurface.Picking){
			//Activate Picking when press and right click and if in Picking mode
			if(SwingUtilities.isRightMouseButton(mouseEvent)){
				isPickedPressed = true;	
			}
			mousePosition.x = mouseEvent.getX();
			mousePosition.y = mouseEvent.getY();
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
		if (myRenderer.displaySurface.SelectRectangle){
			enableROIDrawing = false;
		}
		

	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {

		
		if ( ((isArcBallOn(mouseEvent)) && myCamera.isModelCentered) || myRenderer.displaySurface.SelectRectangle){
			if(!myRenderer.displaySurface.Picking){
				if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
					myRenderer.drag(mouseEvent.getPoint());
					mousePosition.x = mouseEvent.getX();
					mousePosition.y = mouseEvent.getY();
				}
			}
		} else {
			//check the difference between the current x and the last x position
			int diffx = mouseEvent.getX() - lastxPressed; 
			// check the difference between the current y and the last y position
			int diffy = mouseEvent.getY() - lastyPressed; 
			// set lastx to the current x position
			lastxPressed = mouseEvent.getX(); 
			// set lastyPressed to the current y position
			lastyPressed = mouseEvent.getY(); 
			

			double speed = 0.035;

			// Decrease the speed of the translation if z is negative.
			if (myCamera.getZPos() < 0) {
				speed = (speed / Math.abs(myCamera.getZPos()) * 2);
			} else {
				speed = (speed * Math.abs(myCamera.getZPos()) / 4);
			}
			// camera.PrintParam();
			//myCamera.moveXYPlan(diffx, diffy,speed);
			myCamera.moveXYPlan2(diffx, diffy, myCamera.getZPos(),this.myRenderer.getWidth(), this.myRenderer.getHeight());
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

		//System.out.println("x:"+arg0.getX()+ " y:" +arg0.getY());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
//		if (arg0.getWheelRotation() < 0) {// Move Up
//			myCamera.zPos -= 0.5;
//			myCamera.zLPos -= 0.5;
//			//myCamera.moveForward(0.1);
//			// camera.look(10);
//		} else {// Move down
//			myCamera.zPos += 0.5;
//			myCamera.zLPos += 0.5;
//			//myCamera.moveForward(-0.1);
//		}
		
		float incrementalZoomStep=(float) myCamera.zPos/10;
		if (arg0.getWheelRotation() < 0) {// Move Up

			myCamera.zPos -=incrementalZoomStep;
			myCamera.zLPos -=incrementalZoomStep;
			//myCamera.moveForward(incrementalStep);
			//myCamera.look(10);
		} else {// Move down
 
			myCamera.zPos +=incrementalZoomStep;
			myCamera.zLPos +=incrementalZoomStep;
			//myCamera.moveForward(-incrementalStep);
			//myCamera.look(10);
		}

	}


	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_LEFT: // player turns left (scene rotates right)
			myCamera.strafeLeft(0.1);
			myCamera.look(10);
			break;
		case VK_RIGHT: // player turns right (scene rotates left)
			myCamera.strafeRight(0.1);
			myCamera.look(10);
			break;
		case VK_UP:
			//myCamera.cameraZPosition += 0.1;
			//myCamera.cameraLZPosition += 0.1;
			myCamera.moveForward(0.1);
			myCamera.look(10);
			break;
		case VK_DOWN:
			//myWorld.cameraZPosition -= 0.1;
			//myWorld.cameraLZPosition -= 0.1;
			myCamera.moveForward(-0.1);
			myCamera.look(10);
			break;
		case KeyEvent.VK_PAGE_UP:
			myCamera.pitchDown(0.05);
			myCamera.look(10);
			break;
		case KeyEvent.VK_PAGE_DOWN:
			myCamera.pitchUp(0.05);
			myCamera.look(10);
			break;
		case KeyEvent.VK_HOME:
			myCamera.yawLeft(0.01);
			myCamera.look(10);
			break;
		case KeyEvent.VK_END:
			myCamera.yawRight(0.01);
			myCamera.look(10);
			break;
		case VK_I:
			//myCamera.InitParam();
			break;
		case VK_H:
			//myCamera.Init3DView();
			break;
		}		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	// add for check Meta Button pressed in MAC Os or Ctrl in Window OS
	private boolean checkCtrlKeyDown(MouseEvent mouseEvent){
		boolean specicalKeyDown = false;
		if(isMacOS)
			specicalKeyDown = mouseEvent.isMetaDown();
		else
			specicalKeyDown = mouseEvent.isControlDown();
		return specicalKeyDown;
	}
	
	private boolean detectMacOS(){
		String os = System.getProperty("os.name");
		if("Mac OS X".equals(os)){
			isMacOS = true;
		}
		return isMacOS;
	}
	
	private boolean isArcBallOn(MouseEvent mouseEvent){
			
		if(checkCtrlKeyDown(mouseEvent) || myRenderer.displaySurface.Arcball ==true){
			if(mouseEvent.isShiftDown()==false){
				return true;
			}
			
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	//Picking method
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

		
		this.myCamera.UpdateCamera(gl, glu, width, height);
		//FIXME: Comment GL_MODELVIEW to debug3D picking (redraw the model when clicking)
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

}
