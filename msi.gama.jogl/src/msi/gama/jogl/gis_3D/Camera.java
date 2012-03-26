package msi.gama.jogl.gis_3D;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class Camera {
	private double xPos;
	private double yPos;
	private double zPos;

	private double xLPos;
	private double yLPos;
	private double zLPos;

	private double pitch;
	private double yaw;

	public Camera() {
		xPos = 0;
		yPos = 0;
		zPos = 0;

		xLPos = 0;
		yLPos = 0;
		zLPos = 10;
	}

	public Camera(double xPos, double yPos, double zPos, double xLPos,
			double yLPos, double zLPos) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;

		this.xLPos = xLPos;
		this.yLPos = yLPos;
		this.zLPos = zLPos;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public void updatePosition(double xPos, double yPos, double zPos) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
	}

	public void lookPosition(double xLPos, double yLPos, double zLPos) {
		this.xLPos = xLPos;
		this.yLPos = yLPos;
		this.zLPos = zLPos;
	}

	// Move in the XY plan by changing camera pos and look pos.
	public void moveXYPlan(double diffx, double diffy, double speed) {
		
		if (Math.abs(diffx) > Math.abs(diffy)) {// Move X
			speed=Math.abs(diffx)*speed;
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
			speed=Math.abs(diffy)*speed;
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
		double xCurrent = this.xPos;
		double yCurrent = this.yPos;
		double zCurrent = this.zPos;

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

		double xLook = xPos;
		double yLook = yPos;
		double zLook = zPos;

		moveForward(-10);

		lookPosition(xLook, yLook, zLook);
	}

	/* -------Get commands--------- */

	public double getXPos() {
		return xPos;
	}

	public double getYPos() {
		return yPos;
	}

	public double getZPos() {
		return zPos;
	}

	public double getXLPos() {
		return xLPos;
	}

	public double getYLPos() {
		return yLPos;
	}

	public double getZLPos() {
		return zLPos;
	}

	public double getPitch() {
		return pitch;
	}

	public double getYaw() {
		return yaw;
	}
	
	public void UpdateCamera(GL gl,int width, int height){
		
		GLU glu = new GLU();
		if (height == 0) {
			height = 1; // prevent divide by zero
		}
		float aspect = (float) width / height;
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);
		glu.gluLookAt(this.getXPos(), this.getYPos(), this.getZPos(),
				this.getXLPos(), this.getYLPos(), this.getZLPos(), 0.0,
				1.0, 0.0);
		
	}

	public void InitParam() {

//		this.yaw = -1.5;
//		this.pitchDown(0);
//		this.moveForward(0);
//		this.look(10);

		this.yaw = -1.5;
		this.pitch=0.0f;
		this.xPos=-0.1f;
		this.xLPos=0.6f;
		this.yPos=0.0f;
		this.yLPos=0.0f;
		this.zPos=1.0f;
		this.zLPos=-10.0f;
	}

	public void Init3DView() {

		this.yaw = -1.5;
		this.pitch=0.5f;
		this.xPos=0.0f;
		this.xLPos=0.5f;
		this.yPos=-5.0f;
		this.yLPos=0.0f;
		this.zPos=1.0f;
		this.zLPos=-5.0f;
	}

	public void PrintParam() {
		System.out.println("xPos:" + xPos + " yPos:" + yPos + " zPos:" + zPos);
		System.out.println("xLPos:" + xLPos + " yLPos:" + yLPos + " zLPos:"
				+ zLPos);
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
