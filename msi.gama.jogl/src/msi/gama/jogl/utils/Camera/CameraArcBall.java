package msi.gama.jogl.utils.Camera;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;

public class CameraArcBall extends AbstractCamera {

	
	private double envWidth;
	private double envHeight;
	
	private double radius;
	
	private ILocation upVector;
	private double _orientation;
	
	public double _sensivity;
	
	public CameraArcBall(JOGLAWTGLRenderer joglawtglRenderer) {
		super(joglawtglRenderer);
		
		_orientation = 1.0;
    	_phi = 90.0;
        _theta = 0.0;
        _sensivity = 0.4;
		_keyboardSensivity= 4;

		setUpVector(new GamaPoint(0.0, 1.0,0.0));
		rotation();
	}
	
	public CameraArcBall(double xPos, double yPos, double zPos, double xLPos,
			double yLPos, double zLPos, JOGLAWTGLRenderer renderer) {
		super(xPos, yPos, zPos, xLPos, yLPos, zLPos, renderer);
		_position.x= xPos;
		_position.y= yPos;
		_position.z= zPos;
		
		_target.x= xLPos;
		_target.y= yLPos;
		_target.z= zLPos;
	}
	
	@Override
	public void UpdateCamera(GL gl, GLU glu, int width, int height) {

		if ( height == 0 ) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		// FIXME: need to see the influence of the different parameter.
		glu.gluPerspective(45.0f, aspect, 0.1f, getMaxDim() * 100);
		glu.gluLookAt(this._position.getX(), this._position.getY(), this._position.getZ(), 
					this._target.getX(), this._target.getY(),this._target.getZ(), 
					getUpVector().getX(), getUpVector().getY(), getUpVector().getZ());
	}
	
	public void rotation()
	{
		if( _theta > 360 ) _theta = 0;
	    if( _theta < 0 ) _theta = 360;

	    if( _phi > 360 ) _phi = 0;
	    if( _phi < 0 ) _phi = 360;
		
		double cosAngle = Math.cos(_theta*Math.PI/180.f);
		double sinAngle = Math.sin(_theta*Math.PI/180.f);
		
		_position.x = radius*sinAngle*Math.sin(_phi*Math.PI/180.f);
		_position.z = radius*cosAngle*Math.sin(_phi*Math.PI/180.f);
		_position.y = radius*Math.cos(_phi*Math.PI/180.f);
		
		if(_phi>180 && _phi <360) 
    	{
    		_orientation = -1;		
    		setUpVector(new GamaPoint(0.0, _orientation,0.0));
    	}
		else{
			_orientation = 1;		
    		setUpVector(new GamaPoint(0.0, _orientation,0.0));
		}		
	}
	
	@Override
	public void initializeCamera(double envWidth, double envHeight) {

		this.envWidth = envWidth;
		this.envHeight = envHeight;

		if ( envWidth > envHeight ) {
			setMaxDim(envWidth);
		} else {
			setMaxDim(envHeight);
		}

		radius = getMaxDim() * INIT_Z_FACTOR;
		
		if ( isModelCentered ) {
			_position.x = 0;
			_target.x = 0;
			_position.y = 0;
			_target.y = 0;
			_position.z = radius;
			_target.z = 0;
		} else {
			_position.x = envWidth / 2;
			_target.x = envWidth / 2;
			_position.y = -envWidth / 2;
			_target.y = -envWidth / 2;
			_position.z = radius;
			_target.z = 0;
		}
	}
	
	public void initialize3DCamera(double envWidth, double envHeight) 
	{		
		radius = getMaxDim() * INIT_Z_FACTOR;

		if ( isModelCentered ) {
			_position.x = 0;
			_target.x = 0;
			_position.y = -envHeight  * 1.75+envHeight/2;
			_target.y = -envHeight * 0.5+envHeight/2;
			_position.z = radius;
			_target.z = 0;
		}
		else{
			_position.x = envWidth / 2;
			_target.x = envWidth / 2;
			_position.y = -envHeight  * 1.75;
			_target.y = -envHeight * 0.5;
			_position.z = getMaxDim();
			_target.z = 0;
		}
	}
	
	public double getRadius()
	{
		return this.radius;
	}
	
	public void setRadius(double r)
	{
		this.radius = r;
	}
	
	@Override
	public ILocation getUpVector() {
		return upVector;
	}
	@Override
	public void setUpVector(ILocation upVector) {
		this.upVector = upVector;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {	
		if ( arg0.getWheelRotation() < 0 ) {// Move Up
			myRenderer.displaySurface.zoomIn();
		} else {// Move down
			myRenderer.displaySurface.zoomOut();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		if ( isArcBallOn(arg0) && isModelCentered || myRenderer.displaySurface.selectRectangle ) 
		{
			//check the difference between the current x and the last x position
			int horizMovement = arg0.getX()- lastxPressed;
			// check the difference between the current y and the last y position
			int vertMovement = arg0.getY()  - lastyPressed; 
			
			// set lastx to the current x position
			lastxPressed = arg0.getX() ; 
			// set lastyPressed to the current y position
			lastyPressed = arg0.getY();
			
			_theta -= horizMovement*_sensivity;					
			_phi -= vertMovement*_sensivity;
			
			rotation();
		}
		else
		{
			// check the difference between the current x and the last x position
			int diffx = arg0.getX() - lastxPressed;
			// check the difference between the current y and the last y position
			int diffy = arg0.getY() - lastyPressed;
			// set lastx to the current x position
			lastxPressed = arg0.getX();
			// set lastyPressed to the current y position
			lastyPressed = arg0.getY();

			double speed = 0.035;

			// Decrease the speed of the translation if z is negative.
			if ( _position.getZ() < 0 ) {
				speed = speed / Math.abs(_position.getZ()) * 2;
			} else {
				speed = speed * Math.abs(_position.getZ()) / 4;
			}
			// camera.PrintParam();
			// moveXYPlan(diffx, diffy,speed);
			moveXYPlan2(diffx, diffy, _position.getZ(), this.myRenderer.getWidth(),
				this.myRenderer.getHeight());
		}
		PrintParam();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {
//		PrintParam();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if ( isArcBallOn(arg0) && isModelCentered ) {
			if ( SwingUtilities.isRightMouseButton(arg0) ) {
				myRenderer.reset();
			}
		} else {
			// myCamera.PrintParam();
			// System.out.println( "x:" + mouseEvent.getX() + " y:" + mouseEvent.getY());
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		lastxPressed = arg0.getX();
		lastyPressed = arg0.getY();
		
		//Picking mode
		if(myRenderer.displaySurface.picking)
		{
			//Activate Picking when press and right click and if in Picking mode
			if(SwingUtilities.isRightMouseButton(arg0))
				isPickedPressed = true;	
			
			mousePosition.x = arg0.getX();
			mousePosition.y = arg0.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if ( myRenderer.displaySurface.selectRectangle ) {
			enableROIDrawing = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
		case VK_LEFT: 
			strafeLeft = true;
			shiftKeyDown = checkShiftKeyDown(arg0);
			break;
		case VK_RIGHT: 
			strafeRight = true;
			shiftKeyDown = checkShiftKeyDown(arg0);
			break;
		case VK_UP:
			forward = true;
			shiftKeyDown = checkShiftKeyDown(arg0);
			break;
		case VK_DOWN:
			backward = true;
			shiftKeyDown = checkShiftKeyDown(arg0);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
		case VK_LEFT: // player turns left (scene rotates right)
			strafeLeft = false;
			break;
		case VK_RIGHT: // player turns right (scene rotates left)
			strafeRight = false;
			break;
		case VK_UP:
			forward = false;
			break;
		case VK_DOWN:
			backward = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	public double getMaxDim() {
		return maxDim;}
}
