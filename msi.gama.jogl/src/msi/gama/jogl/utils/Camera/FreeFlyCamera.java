package msi.gama.jogl.utils.Camera;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Camera.Arcball.Vector3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.AbstractTopology;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.runtime.GAMA;

public class FreeFlyCamera extends AbstractCamera {
	
	public Vector3D _forward;
	public Vector3D _left;
	
	public double _speed;
	public double _sensivity;
	
	
	public FreeFlyCamera(JOGLAWTGLRenderer renderer)
    {      
    	super(renderer);
    	_forward = new Vector3D();
    	_left = new Vector3D();
    	
    	_phi = 0.0;
        _theta = 0.0;
        
        _speed = 0.04;
        _sensivity = 0.4;
        _keyboardSensivity= 4;
        
        forward = false;
        backward = false;
        strafeLeft = false;
        strafeRight = false;
    }
	
	public FreeFlyCamera(double xPos, double yPos, double zPos, double xLPos,
			double yLPos, double zLPos, JOGLAWTGLRenderer renderer) {
		super(xPos, yPos, zPos, xLPos, yLPos, zLPos, renderer);
		// TODO Auto-generated constructor stub
	}
	
  	public void vectorsFromAngles()
    {
    	Vector3D up = new Vector3D(0.0f,0.0f,1.0f); 
    	
	    if (_phi >89)
	        _phi = 89;
	    else if (_phi <-89)
	        _phi = -89;

	    double r_temp = Math.cos(_phi*Math.PI/180.f);
	    _forward.z = Math.sin(_phi*Math.PI/180.f);
	    _forward.x = r_temp*Math.cos(_theta*Math.PI/180.f);
	    _forward.y = r_temp*Math.sin((_theta*Math.PI)/180.f);
	    
	    _left = Vector3D.crossProduct(up, _forward);
	    _left.normalize();
	    
	//calculate the target of the camera
	    _target = _forward.add(_position.x, _position.y, _position.z);
	    
    }
    
  	@Override
    public void animate()
    {
    	 	if (this.forward) 
    	 	{
    	 		if(shiftKeyDown)
    	 		{				
    	 			_phi -= -_keyboardSensivity*_sensivity; 	 			
    	 			vectorsFromAngles();
    	 		}
    	 		else
    	 			_position = _position.add(_forward.scalarMultiply(_speed*200)); //go forward
    	 	}
    	    if (this.backward){
    	    	if(shiftKeyDown)
    	 		{				
    	 			_phi -= _keyboardSensivity*_sensivity;   	 			
    	 			vectorsFromAngles();
    	 		}
    	 		else
    	 			_position = _position.subtract(_forward.scalarMultiply(_speed*200)); //go backward
    	    }
    	    if (this.strafeLeft){
    	    	if(shiftKeyDown)
    	 		{
    	 			_theta -= -_keyboardSensivity*_sensivity;					
    	 			vectorsFromAngles();
    	 		}
    	 		else
    	    	_position = _position.add(_left.scalarMultiply(_speed*200)); //move on the right
    	    }
    	    if (this.strafeRight){
    	    	if(shiftKeyDown)
    	 		{
    	 			_theta -= _keyboardSensivity*_sensivity;					
    	 			vectorsFromAngles();
    	 		}
    	 		else
    	    	_position = _position.subtract(_left.scalarMultiply(_speed*200)); //move on the left
    	    }
  	    
    	    _target = _position.add(_forward.x,_forward.y,_forward.z);
    }
    
    @Override
    public void UpdateCamera(GL gl,GLU glu, int width, int height)
    {	
		if ( height == 0 ) {
			height = 1; // prevent divide by zero
		}
    	float aspect = (float) width / height;
    	    	
		glu.gluPerspective(45.0f, aspect, 0.1f, maxDim*10);


		glu.gluLookAt(_position.x,_position.y,_position.z,
				_target.x,_target.y,_target.z,
				0.0f,0.0f,1.0f);
		animate(); 	
    }
    
	public void followAgent(IAgent a, GLU glu) {
		ILocation l = a.getLocation();
		_position.x = l.getX();
		_position.y = l.getY();
		_position.z = l.getZ();
		glu.gluLookAt(0,0,(float) (maxDim*1.5),
				0,0,0,
				0.0f,0.0f,1.0f);
	}
    
    @Override
  	public void initializeCamera(double envWidth, double envHeight) {
		if (envWidth > envHeight) {
			maxDim = envWidth;
		} else {
			maxDim = envHeight;
		}

		_position.x = envWidth / 2;
		_target.x = envWidth / 2;
		_position.y = -envHeight  * 1.75;
		_target.y = -envHeight * 0.5;
		_position.z = getMaxDim();
		_target.z = 0;
		
		
		_phi = -45;
		_theta = 90;
		vectorsFromAngles();
	}
  	
    @Override
  	public void initialize3DCamera(double envWidth, double envHeight) {
		if (envWidth > envHeight) {
			maxDim = envWidth;
		} else {
			maxDim = envHeight;
		}
		
		_position.x = envWidth / 2;
		_target.x = envWidth / 2;
		_position.y = -envHeight  * 1.75;
		_target.y = -envHeight * 0.5;
		_position.z = getMaxDim();
		_target.z = 0;
		
		_phi = -45;
		_theta = 90;
		vectorsFromAngles();		
	}
    
    @Override
    public Vector3D getForward()
    {
    	return this._forward;
    }
    
    @Override
    public double getSpeed()
    {
    	return this._speed;
    }
    
    public void setZPosition(double z)
    {
    	this._position.z = z;
    }
	
    
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		
		float incrementalZoomStep;
		// Check if Z is not equal to 0 (avoid being block on z=0)
		if ( _position.getZ() != 0 ) {
			incrementalZoomStep = (float) _position.getZ() / 10;
		} else {
			incrementalZoomStep = 0.1f;
		}
		if(arg0.getWheelRotation() > 0)
		{
			_position = _position.subtract(_forward.scalarMultiply(_speed*800+Math.abs(incrementalZoomStep))); //on recule
			myRenderer.displaySurface.setZoomLevel(myRenderer.camera.getMaxDim() * INIT_Z_FACTOR / _position.getZ());
			_target = _forward.add(_position.x, _position.y, _position.z); //comme on a bougé, on recalcule la cible fixée par la caméra
		}
		else
		{
			_position = _position.add(_forward.scalarMultiply(_speed*800+Math.abs(incrementalZoomStep))); //on avance
			myRenderer.displaySurface.setZoomLevel(myRenderer.camera.getMaxDim() * INIT_Z_FACTOR / _position.getZ());
			_target = _forward.add(_position.x, _position.y, _position.z); //comme on a bougé, on recalcule la cible fixée par la caméra
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(myRenderer.displaySurface.selectRectangle &&  IsViewIn2DPlan())
		{
			mousePosition.x = arg0.getX() ; 
			mousePosition.y = arg0.getY() ; 
			enableROIDrawing = true;
			myRenderer.DrawROI();
		}
		else
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
			
			vectorsFromAngles();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if ( myRenderer.displaySurface.selectRectangle) 
		{
			Point point = myRenderer.GetRealWorldPointFromWindowPoint(new Point(arg0.getX(),arg0.getY()));

			mousePosition.x = arg0.getX() ; 
			mousePosition.y = arg0.getY() ; 
			enableROIDrawing = true;
			myRenderer.DrawROI();
			myRenderer.roiCenter.setLocation(point.x, point.y);
			System.out.println("roiCenter x : "+myRenderer.roiCenter.x+" roiCenter y: "+myRenderer.roiCenter.y);
			System.out.println("roi_List x1 : "+myRenderer.roi_List.get(0)+" roi_List y1: "+myRenderer.roi_List.get(1)+"roi_List x2 : "+myRenderer.roi_List.get(2)+" roi_List y2: "+myRenderer.roi_List.get(3));
			Iterator<IShape> shapes = GAMA.getSimulation().getTopology().getSpatialIndex().allInEnvelope(new GamaPoint(myRenderer.roiCenter.x,-myRenderer.roiCenter.y), new Envelope(myRenderer.roiCenter.x-4,myRenderer.roiCenter.x+4,-myRenderer.roiCenter.y-4,-myRenderer.roiCenter.y+4),  new Different(), true);
			final Iterator<IAgent> agents = AbstractTopology.toAgents(shapes);
			
			buildMenus(agents);
			
			enableROIDrawing = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
//		if ( isArcBallOn(arg0) && isModelCentered ) {
//			if (arg0.getButton() ==3) {
//				myRenderer.reset();
//			}
//		} else {
//			// myCamera.PrintParam();
//			// System.out.println( "x:" + mouseEvent.getX() + " y:" + mouseEvent.getY());
//		}
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
			if(arg0.getButton() ==3)
				isPickedPressed = true;	
			
			mousePosition.x = arg0.getX();
			mousePosition.y = arg0.getY();
		}
		
		myRenderer.GetRealWorldPointFromWindowPoint(new Point(arg0.getX(),arg0.getY()));

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if ( myRenderer.displaySurface.selectRectangle && IsViewIn2DPlan() && enableROIDrawing==true ) {

			GamaPoint p = new GamaPoint(myRenderer.worldCoordinates.x,-myRenderer.worldCoordinates.y,0.0);
			if(!arg0.isAltDown())
			{
				System.out.println("roiCenter x : "+myRenderer.roiCenter.x+" roiCenter y: "+myRenderer.roiCenter.y);
				System.out.println("roi_List x1 : "+myRenderer.roi_List.get(0)+" roi_List y1: "+myRenderer.roi_List.get(1)+"roi_List x2 : "+myRenderer.roi_List.get(2)+" roi_List y2: "+myRenderer.roi_List.get(3));
				Iterator<IShape> shapes = GAMA.getSimulation().getTopology().getSpatialIndex().allInEnvelope(new GamaPoint(myRenderer.roiCenter.x,-myRenderer.roiCenter.y), new Envelope(myRenderer.roi_List.get(0),myRenderer.roi_List.get(2),-myRenderer.roi_List.get(1),-myRenderer.roi_List.get(3)),  new Different(), true);
				final Iterator<IAgent> agents = AbstractTopology.toAgents(shapes);

				buildMenus(agents);
			}
			else
			{
				myRenderer.ROIZoom();
			}
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
	
	@Override
	public void PrintParam() {
		System.out.println("xPos:" + _position.x + " yPos:" + _position.y  + " zPos:" + _position.z );
		System.out.println("xLPos:" + _target.x + " yLPos:" + _target.y + " zLPos:" + _target.z);
		System.out.println("_forwardX:" + _forward.x + " _forwardY:" + _forward.y + " _forwardZ:" + _forward.z);
		System.out.println("_phi : "+_phi+" _theta : "+_theta);

	}

	@Override
	public boolean IsViewIn2DPlan() {
		if(_phi>=-89 && _phi<-85){
			return true;
		}
		else{
			return false;
		}
		
	}
	

}
