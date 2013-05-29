package msi.gama.jogl.utils.Camera;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.shape.*;

public class Camera extends AbstractCamera {

	private double pitch;
	private double yaw;

	private double envWidth;
	private double envHeight;

	private ILocation upVector;
	




	public Camera(JOGLAWTGLRenderer joglawtglRenderer) {
		super(joglawtglRenderer);
		
		_target.z = 10;
		_keyboardSensivity= 4;

		setUpVector(new GamaPoint(0.0, 1.0, 0.0));
	}

	public Camera(double xPos, double yPos, double zPos, double xLPos, double yLPos, double zLPos, JOGLAWTGLRenderer joglawtglRenderer)  {
		super(xPos, yPos, zPos, xLPos, yLPos, zLPos, joglawtglRenderer);
		
		_position.x= xPos;
		_position.y= yPos;
		_position.z= zPos;
		
		_target.x= xLPos;
		_target.y= yLPos;
		_target.z= zLPos;
		
		_keyboardSensivity= 4;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	
	@Override
	public void updatePosition(double xPos, double yPos, double zPos) {
		_position.x= xPos;
		_position.y= yPos;
		_position.z= zPos;
	}

	@Override
	public void lookPosition(double xLPos, double yLPos, double zLPos) {
		_target.x= xLPos;
		_target.y= yLPos;
		_target.z= zLPos;
	}

	// FIXME: Has been replace by moveXYPlan2 should be remove once every model works well with moveXYPlan2
	// Move in the XY plan by changing camera pos and look pos.
	@Override
	public void moveXYPlan(double diffx, double diffy, double speed) {

		// System.out.println("diffx" + diffx + "diffy" + diffy + "speed" +speed);
		// System.out.println("before");
		// System.out.println("this._position.getX()" + this._position.getX() + "this.getYPos()" + this.getYPos());
		// System.out.println("this._target.getX()" + this._target.getX() + "this.getYPos()" + this._target.getY());
		if ( Math.abs(diffx) > Math.abs(diffy) ) {// Move X
			speed = Math.abs(diffx) * speed;
			if ( diffx > 0 ) {// move right
				this.updatePosition(_position.getX() - speed, _position.getY(), _position.getZ());
				this.lookPosition(_target.getX() - speed, _target.getY(), _target.getZ());
			} else {// move left
				this.updatePosition(_position.getX() + speed, _position.getY(), _position.getZ());
				this.lookPosition(_target.getX() + speed, _target.getY(), _target.getZ());
			}
		} else if ( Math.abs(diffx) < Math.abs(diffy) ) { // Move Y
			speed = Math.abs(diffy) * speed;
			if ( diffy > 0 ) {// move down
				this.updatePosition(_position.getX(), _position.getY() + speed, _position.getZ());
				this.lookPosition(_target.getX(), _target.getY() + speed, _target.getZ());
			} else {// move up
				this.updatePosition(_position.getX(), _position.getY() - speed, _position.getZ());
				this.lookPosition(_target.getX(), _target.getY() - speed, _target.getZ());
			}

		}
	}

	// Move in the XY plan by changing camera pos and look pos.
	@Override
	public void moveXYPlan2(double diffx, double diffy, double z, double w, double h) {

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
				updatePosition(_position.getX(), _position.getY()+ translationValue, _position.getZ());
				this.lookPosition(_target.getX(), _target.getY() + translationValue, _target.getZ());
			} else {// move up
				updatePosition(_position.getX(), _position.getY()- translationValue, _position.getZ());
				lookPosition(_target.getX(), _target.getY() - translationValue, _target.getZ());
			}
		}
	}

	// Moves the entity forward according to its pitch and yaw and the
	// magnitude.
	@Override
	public void moveForward(double magnitude) {
		double xCurrent = this._position.getX();
		double yCurrent = this._position.getY();
		double zCurrent = this._position.getZ();

		// Spherical coordinates maths
		double xMovement = magnitude * Math.cos(pitch) * Math.cos(yaw);
		double yMovement = magnitude * Math.sin(pitch);
		double zMovement = magnitude * Math.cos(pitch) * Math.sin(yaw);

		double xNew = xCurrent + xMovement;
		double yNew = yCurrent + yMovement;
		double zNew = zCurrent + zMovement;

		updatePosition(xNew, yNew, zNew);
	}
	
	@Override
	public void strafeLeft(double magnitude) {
		double pitchTemp = pitch;
		pitch = 0;
		yaw = yaw - 0.5 * Math.PI;
		moveForward(magnitude);
		pitch = pitchTemp;
		yaw = yaw + 0.5 * Math.PI;
	}

	@Override
	public void strafeRight(double magnitude) {
		double pitchTemp = pitch;
		pitch = 0;

		yaw = yaw + 0.5 * Math.PI;
		moveForward(magnitude);
		yaw = yaw - 0.5 * Math.PI;

		pitch = pitchTemp;
	}
	
	@Override
	public void look(double distanceAway) {
		if ( pitch > 1.0 ) {
			pitch = 0.99;
		}

		if ( pitch < -1.0 ) {
			pitch = -0.99;
		}

		moveForward(10);

		double xLook = _position.getX();
		double yLook = _position.getY();
		double zLook = _position.getZ();

		moveForward(-10);

		lookPosition(xLook, yLook, zLook);
	}

	/* -------Get commands--------- */
	@Override
	public double getPitch() {
		return pitch;
	}
	
	@Override
	public double getYaw() {
		return yaw;
	}
	
	@Override
	public void UpdateCamera(GL gl, GLU glu, int width, int height) {

		if ( height == 0 ) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		// FIXME: need to see the influence of the different parameter.
		glu.gluPerspective(45.0f, aspect, 0.1f, getMaxDim() * 100);
		glu.gluLookAt(this._position.getX(), this._position.getY(), this._position.getZ(), this._target.getX(), this._target.getY(),
			this._target.getZ(), getUpVector().getX(), getUpVector().getY(), getUpVector().getZ());

		animate();
	}
	
  	@Override
    public void animate()
    {
    	 	if (this.forward) 
    			moveXYPlan2(0, _keyboardSensivity, _position.getZ(), this.myRenderer.getWidth(),
    						this.myRenderer.getHeight());
    	    if (this.backward)
    	    	moveXYPlan2(0, -_keyboardSensivity, _position.getZ(), this.myRenderer.getWidth(),
    						this.myRenderer.getHeight());  
    	    if (this.strafeLeft)
    	    	moveXYPlan2(_keyboardSensivity, 0, _position.getZ(), this.myRenderer.getWidth(),
    						this.myRenderer.getHeight());    	    
    	    if (this.strafeRight)
    	    	moveXYPlan2(-_keyboardSensivity, 0, _position.getZ(), this.myRenderer.getWidth(),
    						this.myRenderer.getHeight());  
    }
	
	@Override
	public void initializeCamera(double envWidth, double envHeight) {

		this.yaw = 0.0f;
		this.pitch = 0.0f;
		this.envWidth = envWidth;
		this.envHeight = envHeight;

		if ( envWidth > envHeight ) {
			setMaxDim(envWidth);
		} else {
			setMaxDim(envHeight);
		}

		if ( isModelCentered ) {
			_position.x = 0;
			_target.x = 0;
			_position.y = 0;
			_target.y = 0;
			_position.z = getMaxDim() * INIT_Z_FACTOR;
			_target.z = 0;
		} else {
			_position.x = envWidth / 2;
			_target.x = envWidth / 2;
			_position.y = -envWidth / 2;
			_target.y = -envWidth / 2;
			_position.z = getMaxDim() * INIT_Z_FACTOR;
			_target.z = 0;
		}
//		 this.PrintParam();
	}
	
	@Override	
	public void circleCamera(double envWidth, double envHeight, double x, double y, double xlpos, double ylpos ) {

		this.yaw = 0.0f;
		this.pitch = 0.0f;
		this.envWidth = envWidth;
		this.envHeight = envHeight;

		if ( envWidth > envHeight ) {
			setMaxDim(envWidth);
		} else {
			setMaxDim(envHeight);
		}

		if ( isModelCentered ) {
			_position.x = x;
			_target.x = xlpos;
			_position.y = y;
			_target.y = ylpos;
			_position.z = getMaxDim() * INIT_Z_FACTOR;
			_target.z = 0;
		} else {
			_position.x = envWidth / 2 + x;
			_target.x = envWidth / 2;
			_position.y = -(envHeight / 2+y);
			_target.y = -envHeight / 2;
			_position.z = getMaxDim() * INIT_Z_FACTOR;
			_target.z = 0;
		}
		// this.PrintParam();
	}

	public void initialize3DCamera(double envWidth, double envHeight) {

		this.yaw = -1.5f;
		this.pitch = 0.5f;

		if ( isModelCentered ) {
			_position.x = 0;
			_target.x = 0;
			_position.y = -envHeight  * 1.75+envHeight/2;
			_target.y = -envHeight * 0.5+envHeight/2;
			_position.z = getMaxDim() * INIT_Z_FACTOR;
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

		// this.PrintParam();
	}
	@Override
	public void PrintParam() {
		System.out.println("xPos:" + _position.x + " yPos:" + _position.y  + " zPos:" + _position.z );
		System.out.println("xLPos:" + _target.x + " yLPos:" + _target.y + " zLPos:" + _target.z);
		System.out.println("yaw:" + yaw + " picth:" + pitch);
	}

	/* --------------------------- */

	/* -------------- Pitch and Yaw commands --------------- */
	@Override
	public void pitchUp(double amount) {
		this.pitch += amount;
	}
	@Override
	public void pitchDown(double amount) {
		this.pitch -= amount;
	}
	@Override
	public void yawRight(double amount) {
		this.yaw += amount;
	}
	@Override
	public void yawLeft(double amount) {
		this.yaw -= amount;
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
	protected boolean isArcBallOn(MouseEvent mouseEvent) {
		if ( checkCtrlKeyDown(mouseEvent) || myRenderer.displaySurface.arcball == true ) {
			if ( mouseEvent.isShiftDown() == false ) {
				return true;
			}

			else {
				return false;
			}
		} else {
			return false;
		}
	}

	/* ---------------------------------------------------- */
	/*------------------ Events controls ---------------------*/
	
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

		if ( isArcBallOn(arg0) && isModelCentered || myRenderer.displaySurface.selectRectangle ) {
//			if ( !myRenderer.displaySurface.picking ) {
				if ( SwingUtilities.isLeftMouseButton(arg0) ) {
					myRenderer.drag(arg0.getPoint());
					mousePosition.x = arg0.getX();
					mousePosition.y = arg0.getY();
//				}
			}
		} else {
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
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

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
		// Arcball

		if ( isArcBallOn(arg0) && isModelCentered || myRenderer.displaySurface.selectRectangle ) {
			// Arcball is not working with picking.
//			if ( !myRenderer.displaySurface.picking ) {
				if ( SwingUtilities.isLeftMouseButton(arg0) ) {
					myRenderer.startDrag(arg0.getPoint());
				}
//			}
			enableROIDrawing = true;
		}

		lastxPressed = arg0.getX();
		lastyPressed = arg0.getY();

		// Picking mode
		if ( myRenderer.displaySurface.picking ) {
			// Activate Picking when press and right click and if in Picking mode
			if ( SwingUtilities.isRightMouseButton(arg0) ) {
				isPickedPressed = true;
			}
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
			ctrlKeyDown = checkCtrlKeyDown(arg0);
			break;
		case VK_RIGHT: 
			strafeRight = true;
			ctrlKeyDown = checkCtrlKeyDown(arg0);
			break;
		case VK_UP:
			forward = true;
			ctrlKeyDown = checkCtrlKeyDown(arg0);
			break;
		case VK_DOWN:
			backward = true;
			ctrlKeyDown = checkCtrlKeyDown(arg0);
			break;
		case KeyEvent.VK_PAGE_UP:
			pitchDown(0.05);
			look(10);
			break;
		case KeyEvent.VK_PAGE_DOWN:
			pitchUp(0.05);
			look(10);
			break;
		case KeyEvent.VK_HOME:
			yawLeft(0.01);
			look(10);
			break;
		case KeyEvent.VK_END:
			yawRight(0.01);
			look(10);
			break;
		case VK_I:
			// InitParam();
			break;
		case VK_H:
			// Init3DView();
			break;
	}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
		case VK_LEFT: // player turns left (scene rotates right)
			strafeLeft = false;
			ctrlKeyDown = checkCtrlKeyDown(arg0);
			break;
		case VK_RIGHT: // player turns right (scene rotates left)
			strafeRight = false;
			ctrlKeyDown = checkCtrlKeyDown(arg0);
			break;
		case VK_UP:
			forward = false;
			ctrlKeyDown = checkCtrlKeyDown(arg0);
			break;
		case VK_DOWN:
			backward = false;
			ctrlKeyDown = checkCtrlKeyDown(arg0);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	


}
