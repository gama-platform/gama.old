package msi.gama.jogl.utils.Camera;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import msi.gama.metamodel.shape.*;

public class Camera {

	public final static double INIT_Z_FACTOR = 1.5;

	public double xPos;
	public double yPos;
	private double zPos;

	public double xLPos;
	public double yLPos;
	private double zLPos;

	private double pitch;
	private double yaw;

	private double maxDim;
	private double envWidth;
	private double envHeight;

	private ILocation upVector;
	// Draw the model on 0,0,0 coordinate
	public boolean isModelCentered = true;

	public Camera() {
		setxPos(0);
		setyPos(0);
		setzPos(0);

		xLPos = 0;
		yLPos = 0;
		setzLPos(10);
		setUpVector(new GamaPoint(0.0, 1.0, 0.0));
	}

	public Camera(double xPos, double yPos, double zPos, double xLPos, double yLPos, double zLPos) {
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

	public void updatePosition(double xPos, double yPos, double zPos) {
		this.setxPos(xPos);
		this.setyPos(yPos);
		this.setzPos(zPos);
	}

	public void lookPosition(double xLPos, double yLPos, double zLPos) {
		this.xLPos = xLPos;
		this.yLPos = yLPos;
		this.setzLPos(zLPos);
	}

	// FIXME: Has been replace by moveXYPlan2 should be remove once every model works well with moveXYPlan2
	// Move in the XY plan by changing camera pos and look pos.
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

	public void strafeLeft(double magnitude) {
		double pitchTemp = pitch;
		pitch = 0;
		yaw = yaw - 0.5 * Math.PI;
		moveForward(magnitude);
		pitch = pitchTemp;
		yaw = yaw + 0.5 * Math.PI;
	}

	public void strafeRight(double magnitude) {
		double pitchTemp = pitch;
		pitch = 0;

		yaw = yaw + 0.5 * Math.PI;
		moveForward(magnitude);
		yaw = yaw - 0.5 * Math.PI;

		pitch = pitchTemp;
	}

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

	public double getXPos() {
		return getxPos();
	}

	public double getYPos() {
		return getyPos();
	}

	public double getXLPos() {
		return xLPos;
	}

	public double getYLPos() {
		return yLPos;
	}

	public double getZLPos() {
		return getzLPos();
	}

	public double getPitch() {
		return pitch;
	}

	public double getYaw() {
		return yaw;
	}

	public double getzPos() {
		return zPos;
	}

	public void setzPos(double zPos) {
		this.zPos = zPos;
	}

	public double getxPos() {
		return xPos;
	}

	public void setxPos(double xPos) {
		this.xPos = xPos;
	}

	public double getyPos() {
		return yPos;
	}

	public void setyPos(double yPos) {
		this.yPos = yPos;
	}

	public double getxLPos() {
		return xLPos;
	}

	public void setxLPos(double xLPos) {
		this.xLPos = xLPos;
	}

	public double getyLPos() {
		return yLPos;
	}

	public void setyLPos(double yLPos) {
		this.yLPos = yLPos;
	}

	public double getzLPos() {
		return zLPos;
	}

	public void setzLPos(double zLPos) {
		this.zLPos = zLPos;
	}

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

	public void PrintParam() {
		System.out.println("xPos:" + getxPos() + " yPos:" + getyPos() + " zPos:" + getzPos());
		System.out.println("xLPos:" + xLPos + " yLPos:" + yLPos + " zLPos:" + getzLPos());
		System.out.println("yaw:" + yaw + " picth:" + pitch);
	}

	/* --------------------------- */

	/* -------------- Pitch and Yaw commands --------------- */

	public void pitchUp(double amount) {
		this.pitch += amount;
	}

	public void pitchDown(double amount) {
		this.pitch -= amount;
	}

	public void yawRight(double amount) {
		this.yaw += amount;
	}

	public void yawLeft(double amount) {
		this.yaw -= amount;
	}

	public ILocation getUpVector() {
		return upVector;
	}

	public void setUpVector(ILocation upVector) {
		this.upVector = upVector;
	}

	public double getMaxDim() {
		return maxDim;
	}

	public void setMaxDim(double maxDim) {
		this.maxDim = maxDim;
	}

	/* ---------------------------------------------------- */

}
