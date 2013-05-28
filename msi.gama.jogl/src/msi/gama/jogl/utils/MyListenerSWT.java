package msi.gama.jogl.utils;

import java.awt.Point;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import com.sun.opengl.util.BufferUtil;
import msi.gama.jogl.utils.Camera.Camera;

public class MyListenerSWT implements Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Camera myCamera;
	private JOGLSWTGLRenderer myRenderer;

	//To handle mouse event	
	int lastxPressed;
	int lastyPressed;
	
	private boolean isMacOS = false;
	
	public boolean drag = false;
	
	//picking
	public boolean isPickedPressed = false;
	public final Point mousePosition;
	private final IntBuffer selectBuffer = BufferUtil.newIntBuffer(1024);// will store information
	
	//ROI Drawing
	public boolean enableROIDrawing=false;

	public MyListenerSWT(Camera camera) {
		myCamera = camera;
		detectMacOS();	
		mousePosition = new Point(0,0);
	}

	public MyListenerSWT(Camera camera, JOGLSWTGLRenderer renderer){
		myCamera = camera;
		myRenderer = renderer;
		detectMacOS();
		mousePosition = new Point(0,0);
	}
	
	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
				switch (event.type) 
				{
				case SWT.KeyDown:
					switch(event.keyCode)
					{
					case SWT.ARROW_LEFT: 
						myCamera.strafeLeft(0.1);
						myCamera.look(10);
						break;
					case SWT.ARROW_RIGHT: 
						myCamera.strafeRight(0.1);
						myCamera.look(10);
						break;
					case SWT.ARROW_UP:
						myCamera.moveForward(0.1);
						myCamera.look(10);
						break;
					case SWT.ARROW_DOWN:
						myCamera.moveForward(-0.1);
						myCamera.look(10);
						break;
					case 'q': 
						break;
					case 'd': 
						break;
					case 'z':
						break;
					case 's':
						break;
					case SWT.PAGE_UP:
						break;
					case SWT.PAGE_DOWN:
						break;
					case SWT.HOME:
						break;
					case SWT.END:
						break;
					case 'I':
						break;
					case 'H':
						break;
					}
					break;
				case SWT.MouseDown:
						drag = true;
						//count number of click on mouse
						if(event.count==1)//single click
						{	
							//set the camera to initial position
							if ( isArcBallOn(event) && myCamera.isModelCentered) {
								if (event.button== 3) {
									myRenderer.reset();
								}
							} else {
								// myCamera.PrintParam();
								// System.out.println( "x:" + mouseEvent.getX() + " y:" + mouseEvent.getY());
							}
							
						}	
						// Arcball

						if ( isArcBallOn(event) && myCamera.isModelCentered || myRenderer.displaySurface.selectRectangle ) {
							// Arcball is not working with picking.
							if ( !myRenderer.displaySurface.picking ) {
								if (event.button==1) {
									myRenderer.startDrag(new Point(event.x, event.y));
								}
							}
							enableROIDrawing = true;
						}

						lastxPressed = event.x;
						lastyPressed = event.y;

						// Picking mode
						if ( myRenderer.displaySurface.picking ) {
							// Activate Picking when press and right click and if in Picking mode
							if (event.button==3) {
								isPickedPressed = true;
							}
							mousePosition.x = event.x;
							mousePosition.y = event.y;

						}
							
					break;
				case SWT.MouseUp:
					drag = false;
					if ( myRenderer.displaySurface.selectRectangle ) {
						enableROIDrawing = false;
					}
					break;
				case SWT.MouseMove:
						if(drag)
						{
							if ( isArcBallOn(event) && myCamera.isModelCentered || myRenderer.displaySurface.selectRectangle ) {
								if ( !myRenderer.displaySurface.picking ) {
									if ( (event.stateMask & SWT.BUTTON1) != 0) {
										myRenderer.drag(new Point(event.x, event.y));
										mousePosition.x = event.x;
										mousePosition.y = event.y;
									}
								}
							} else {
								// check the difference between the current x and the last x position
								int diffx = event.x - lastxPressed;
								// check the difference between the current y and the last y position
								int diffy = event.y - lastyPressed;
								// set lastx to the current x position
								lastxPressed = event.x;
								// set lastyPressed to the current y position
								lastyPressed = event.y;

								double speed = 0.035;

								// Decrease the speed of the translation if z is negative.
								if ( myCamera.getPosition().getZ() < 0 ) {
									speed = speed / Math.abs(myCamera.getPosition().getZ()) * 2;
								} else {
									speed = speed * Math.abs(myCamera.getPosition().getZ()) / 4;
								}
								// camera.PrintParam();
								// myCamera.moveXYPlan(diffx, diffy,speed);
								myCamera.moveXYPlan2(diffx, diffy, myCamera.getPosition().getZ(), this.myRenderer.getWidth(),
									this.myRenderer.getHeight());
							}
						}
					break;
				case SWT.MouseVerticalWheel:
						if(event.count>0)
						{
							myRenderer.displaySurface.zoomIn();		
						}
						else{
							myRenderer.displaySurface.zoomOut();		
						}
					break;
				}	
		
	}
	
	// add for check Meta Button pressed in MAC Os or Ctrl in Window OS
	private boolean checkCtrlKeyDown(Event e){
		boolean specicalKeyDown = false;
		if(isMacOS)
			specicalKeyDown = (e.stateMask & SWT.ALT) != 0;
		else
			specicalKeyDown = (e.stateMask & SWT.CTRL) != 0;
		return specicalKeyDown;
	}
	
	private boolean detectMacOS(){
		String os = System.getProperty("os.name");
		if("Mac OS X".equals(os)){
			isMacOS = true;
		}
		return isMacOS;
	}
	
	private boolean isArcBallOn(Event e){
		
		if(checkCtrlKeyDown(e) || myRenderer.displaySurface.arcball ==true){
			if((e.stateMask & SWT.SHIFT) == 0){
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

		this.myCamera.UpdateCamera(gl, glu, width, height);
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

}
