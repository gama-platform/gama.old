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

	public final static double INIT_Z_FACTOR = 1.5;

	public double xPos;
	public double yPos;
	private double zPos;

	public double xLPos;
	public double yLPos;
	private double zLPos;

	private double pitch;
	private double yaw;

	private double envWidth;
	private double envHeight;

	private ILocation upVector;




	public Camera(JOGLAWTGLRenderer joglawtglRenderer) {
		super(joglawtglRenderer);
		setxPos(0);
		setyPos(0);
		setzPos(0);

		xLPos = 0;
		yLPos = 0;
		setzLPos(10);
		setUpVector(new GamaPoint(0.0, 1.0, 0.0));
	}

	public Camera(double xPos, double yPos, double zPos, double xLPos, double yLPos, double zLPos, JOGLAWTGLRenderer joglawtglRenderer)  {
		super(zLPos, zLPos, zLPos, zLPos, zLPos, zLPos, joglawtglRenderer);
		this.setxPos(xPos);
		this.setyPos(yPos);
		this.setzPos(zPos);
		this.xLPos = xLPos;
		this.yLPos = yLPos;
		this.setzLPos(zLPos);
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	
	@Override
	public void updatePosition(double xPos, double yPos, double zPos) {
		this.setxPos(xPos);
		this.setyPos(yPos);
		this.setzPos(zPos);
	}

	@Override
	public void lookPosition(double xLPos, double yLPos, double zLPos) {
		this.xLPos = xLPos;
		this.yLPos = yLPos;
		this.setzLPos(zLPos);
	}

	// FIXME: Has been replace by moveXYPlan2 should be remove once every model works well with moveXYPlan2
	// Move in the XY plan by changing camera pos and look pos.
	@Override
	public void moveXYPlan(double diffx, double diffy, double speed) {

		// System.out.println("diffx" + diffx + "diffy" + diffy + "speed" +speed);
		// System.out.println("before");
		// System.out.println("this.getXPos()" + this.getXPos() + "this.getYPos()" + this.getYPos());
		// System.out.println("this.getXLPos()" + this.getXLPos() + "this.getYPos()" + this.getYLPos());
		if ( Math.abs(diffx) > Math.abs(diffy) ) {// Move X
			speed = Math.abs(diffx) * speed;
			if ( diffx > 0 ) {// move right
				this.updatePosition(this.getXPos() - speed, this.getYPos(), this.getzPos());
				this.lookPosition(this.getXLPos() - speed, this.getYLPos(), this.getZLPos());
			} else {// move left
				this.updatePosition(this.getXPos() + speed, this.getYPos(), this.getzPos());
				this.lookPosition(this.getXLPos() + speed, this.getYLPos(), this.getZLPos());
			}
		} else if ( Math.abs(diffx) < Math.abs(diffy) ) { // Move Y
			speed = Math.abs(diffy) * speed;
			if ( diffy > 0 ) {// move down
				this.updatePosition(this.getXPos(), this.getYPos() + speed, this.getzPos());
				this.lookPosition(this.getXLPos(), this.getYLPos() + speed, this.getZLPos());
			} else {// move up
				this.updatePosition(this.getXPos(), this.getYPos() - speed, this.getzPos());
				this.lookPosition(this.getXLPos(), this.getYLPos() - speed, this.getZLPos());
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
				updatePosition(getXPos() - translationValue, getYPos(), getzPos());
				lookPosition(getXLPos() - translationValue, getYLPos(), getZLPos());
			} else {// move left
				updatePosition(getXPos() + translationValue, getYPos(), getzPos());
				lookPosition(getXLPos() + translationValue, getYLPos(), getZLPos());
			}
		} else if ( Math.abs(diffx) < Math.abs(diffy) ) { // Move Y

			translationValue = Math.abs(diffy) * ((z + 1) / h);

			if ( diffy > 0 ) {// move down
				updatePosition(getXPos(), getYPos() + translationValue, getzPos());
				this.lookPosition(getXLPos(), getYLPos() + translationValue, getZLPos());
			} else {// move up
				updatePosition(getXPos(), getYPos() - translationValue, getzPos());
				lookPosition(getXLPos(), getYLPos() - translationValue, getZLPos());
			}
		}
	}

	// Moves the entity forward according to its pitch and yaw and the
	// magnitude.
	@Override
	public void moveForward(double magnitude) {
		double xCurrent = this.getxPos();
		double yCurrent = this.getyPos();
		double zCurrent = this.getzPos();

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

		double xLook = getxPos();
		double yLook = getyPos();
		double zLook = getzPos();

		moveForward(-10);

		lookPosition(xLook, yLook, zLook);
	}

	/* -------Get commands--------- */
	@Override
	public double getXPos() {
		return getxPos();
	}
	@Override
	public double getYPos() {
		return getyPos();
	}
	@Override
	public double getXLPos() {
		return xLPos;
	}
	@Override
	public double getYLPos() {
		return yLPos;
	}
	@Override
	public double getZLPos() {
		return getzLPos();
	}
	@Override
	public double getPitch() {
		return pitch;
	}
	
	@Override
	public double getYaw() {
		return yaw;
	}
	
	@Override
	public double getzPos() {
		return zPos;
	}
	
	@Override
	public void setzPos(double zPos) {
		this.zPos = zPos;
	}
	@Override
	public double getxPos() {
		return xPos;
	}
	@Override
	public void setxPos(double xPos) {
		this.xPos = xPos;
	}
	@Override
	public double getyPos() {
		return yPos;
	}
	@Override
	public void setyPos(double yPos) {
		this.yPos = yPos;
	}
	@Override
	public double getxLPos() {
		return xLPos;
	}
	@Override
	public void setxLPos(double xLPos) {
		this.xLPos = xLPos;
	}
	@Override
	public double getyLPos() {
		return yLPos;
	}
	@Override
	public void setyLPos(double yLPos) {
		this.yLPos = yLPos;
	}
	@Override
	public double getzLPos() {
		return zLPos;
	}
	@Override
	public void setzLPos(double zLPos) {
		this.zLPos = zLPos;
	}
	@Override
	public void UpdateCamera(GL gl, GLU glu, int width, int height) {

		if ( height == 0 ) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		// FIXME: need to see the influence of the different parameter.
		glu.gluPerspective(45.0f, aspect, 0.1f, getMaxDim() * 100);
		glu.gluLookAt(this.getXPos(), this.getYPos(), this.getzPos(), this.getXLPos(), this.getYLPos(),
			this.getZLPos(), getUpVector().getX(), getUpVector().getY(), getUpVector().getZ());

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
			this.setxPos(0);
			this.setxLPos(0);
			this.setyPos(0);
			this.setyLPos(0);
			this.setzPos(getMaxDim() * INIT_Z_FACTOR);
			this.setzLPos(0.0f);
		} else {
			this.setxPos(envWidth / 2);
			this.setxLPos(envWidth / 2);
			this.setyPos(-envHeight / 2);
			this.setyLPos(-envHeight / 2);
			this.setzPos(getMaxDim() * INIT_Z_FACTOR);
			this.setzLPos(0.0f);
		}
		// this.PrintParam();
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
			this.setxPos(x);
			this.setxLPos(xlpos);
			this.setyPos(y);
			this.setyLPos(ylpos);
			this.setzPos(getMaxDim() * INIT_Z_FACTOR);
			this.setzLPos(0.0f);
		} else {
			this.setxPos(envWidth / 2 + x );
			this.setxLPos(envWidth / 2);
			this.setyPos(-(envHeight / 2+y));
			this.setyLPos(-envHeight / 2);
			this.setzPos(getMaxDim() * INIT_Z_FACTOR);
			this.setzLPos(0.0f);
		}
		// this.PrintParam();
	}

	public void initialize3DCamera(double envWidth, double envHeight) {

		this.yaw = -1.5f;
		this.pitch = 0.5f;

		if ( isModelCentered ) {
			this.setxPos(0);
			this.setxLPos(0);
			this.setyPos(-envHeight * 1.75 + envHeight / 2);
			this.setyLPos(-envHeight * 0.5 + envHeight / 2);
			this.setzPos(getMaxDim() * INIT_Z_FACTOR);
			this.setzLPos(0);

		} else {
			this.setxPos(envWidth / 2);
			this.setxLPos(envWidth / 2);
			this.setyPos(-envHeight * 1.75);
			this.setyLPos(-envHeight * 0.5);
			this.setzPos(getMaxDim());
			this.setzLPos(0);
		}

		// this.PrintParam();
	}
	@Override
	public void PrintParam() {
		System.out.println("xPos:" + getxPos() + " yPos:" + getyPos() + " zPos:" + getzPos());
		System.out.println("xLPos:" + xLPos + " yLPos:" + yLPos + " zLPos:" + getzLPos());
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
	public double getMaxDim() {
		return maxDim;
	}
	@Override
	public void setMaxDim(double maxDim) {
		this.maxDim = maxDim;
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
			if ( !myRenderer.displaySurface.picking ) {
				if ( SwingUtilities.isLeftMouseButton(arg0) ) {
					myRenderer.drag(arg0.getPoint());
					mousePosition.x = arg0.getX();
					mousePosition.y = arg0.getY();
				}
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
			if ( getzPos() < 0 ) {
				speed = speed / Math.abs(getzPos()) * 2;
			} else {
				speed = speed * Math.abs(getzPos()) / 4;
			}
			// camera.PrintParam();
			// moveXYPlan(diffx, diffy,speed);
			moveXYPlan2(diffx, diffy, getzPos(), this.myRenderer.getWidth(),
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
			if ( !myRenderer.displaySurface.picking ) {
				if ( SwingUtilities.isLeftMouseButton(arg0) ) {
					myRenderer.startDrag(arg0.getPoint());
				}
			}
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
		case VK_LEFT: // player turns left (scene rotates right)
			strafeLeft(0.1);
			look(10);
			break;
		case VK_RIGHT: // player turns right (scene rotates left)
			strafeRight(0.1);
			look(10);
			break;
		case VK_UP:
			// cameraZPosition += 0.1;
			// cameraLZPosition += 0.1;
			moveForward(0.1);
			look(10);
			break;
		case VK_DOWN:
			// myWorld.cameraZPosition -= 0.1;
			// myWorld.cameraLZPosition -= 0.1;
			moveForward(-0.1);
			look(10);
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
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	


}
