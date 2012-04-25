package msi.gama.jogl.utils;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class Camera {
	public double xPos;
	public double yPos;
	public double zPos;

	public double xLPos;
	public double yLPos;
	public double zLPos;

	private double pitch;
	private double yaw;

	public Camera() {
		setxPos(0);
		setyPos(0);
		setzPos(0);

		xLPos = 0;
		yLPos = 0;
		setzLPos(10);
	}

	public Camera(double xPos, double yPos, double zPos, double xLPos,
			double yLPos, double zLPos) {
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

	// Move in the XY plan by changing camera pos and look pos.
	public void moveXYPlan(double diffx, double diffy, double speed) {

		if (Math.abs(diffx) > Math.abs(diffy)) {// Move X
			speed = Math.abs(diffx) * speed;
			if (diffx > 0) {// move right
				this.updatePosition(this.getXPos() - speed, this.getYPos(),
						this.getZPos());
				this.lookPosition(this.getXLPos() - speed, this.getYLPos(),
						this.getZLPos());
			} else {// move left
				this.updatePosition(this.getXPos() + speed, this.getYPos(),
						this.getZPos());
				this.lookPosition(this.getXLPos() + speed, this.getYLPos(),
						this.getZLPos());
			}
		} else if (Math.abs(diffx) < Math.abs(diffy)) { // Move Y
			speed = Math.abs(diffy) * speed;
			if (diffy > 0) {// move down
				this.updatePosition(this.getXPos(), this.getYPos() + speed,
						this.getZPos());
				this.lookPosition(this.getXLPos(), this.getYLPos() + speed,
						this.getZLPos());
			} else {// move up
				this.updatePosition(this.getXPos(), this.getYPos() - speed,
						this.getZPos());
				this.lookPosition(this.getXLPos(), this.getYLPos() - speed,
						this.getZLPos());
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
		yaw = yaw - (0.5 * Math.PI);
		moveForward(magnitude);
		pitch = pitchTemp;
		yaw = yaw + (0.5 * Math.PI);
	}

	public void strafeRight(double magnitude) {
		double pitchTemp = pitch;
		pitch = 0;

		yaw = yaw + (0.5 * Math.PI);
		moveForward(magnitude);
		yaw = yaw - (0.5 * Math.PI);

		pitch = pitchTemp;
	}

	public void look(double distanceAway) {
		if (pitch > 1.0)
			pitch = 0.99;

		if (pitch < -1.0)
			pitch = -0.99;

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

	public double getZPos() {
		return getzPos();
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

	public void UpdateCamera(GL gl, int width, int height) {

		GLU glu = new GLU();
		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(this.getXPos(), this.getYPos(), this.getZPos(),
				this.getXLPos(), this.getYLPos(), this.getZLPos(), 0.0, 1.0,
				0.0);

	}

	public void InitializeCamera(float envWidth, float envHeight) {

		this.yaw = -1.5;
		this.pitch = 0.0f;
		
		float scale_rate, maxDim;

		if (envWidth > envHeight) {
			scale_rate = 10 / envWidth;
			maxDim = envWidth;
		} else {
			scale_rate = 10 / envHeight;
			maxDim = envHeight;
		}
		this.setxPos(envWidth / 2 * scale_rate);
		this.setxLPos(envWidth / 2 * scale_rate);
		this.setyPos(-envHeight / 2 * scale_rate);
		this.setyLPos(-envHeight / 2 * scale_rate);
		this.PrintParam();
		// FIXME: This need to be normalize
		this.setzPos(maxDim / 50 + 5.0f);
		this.setzLPos(0.0f);

	}
	
	
	public void Initialize3DCamera(float envWidth, float envHeight) {

		this.yaw = -1.5;
		this.pitch = 1.5f;
		
		float scale_rate, maxDim;

		if (envWidth > envHeight) {
			scale_rate = 10 / envWidth;
			maxDim = envWidth;
		} else {
			scale_rate = 10 / envHeight;
			maxDim = envHeight;
		}
		this.setxPos(envWidth / 2 * scale_rate);
		this.setxLPos(envWidth / 2 * scale_rate);
		this.setyPos(-envHeight  * scale_rate);
		this.setyLPos(-envHeight / 2 * scale_rate);
		this.PrintParam();
		// FIXME: This need to be normalize
		this.setzPos((maxDim / 50 + 5.0f));
		this.setzLPos(10.0f);

	}

	public void PrintParam() {
		System.out.println("xPos:" + getxPos() + " yPos:" + getyPos()
				+ " zPos:" + getzPos());
		System.out.println("xLPos:" + xLPos + " yLPos:" + yLPos + " zLPos:"
				+ getzLPos());
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

	/* ---------------------------------------------------- */

}
