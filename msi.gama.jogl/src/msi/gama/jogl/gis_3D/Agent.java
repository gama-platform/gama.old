package msi.gama.jogl.gis_3D;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import msi.gama.jogl.utils.TessellCallBack;

public class Agent {

	public double xPos;
	public double yPos;
	public double zPos;

	private double pitch;
	private double yaw;
	private float radius;

	float red, green, blue = 0.0f;

	public Agent(double xPos, double yPos, double zPos) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;

		this.pitch = 0;
		this.yaw = 0;
		this.radius=0.025f;
	}

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

	public void updatePosition(double xPos, double yPos, double zPos) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
	}

	public void draw(GL gl, GLU glu) {

		TessellCallBack tessCallback = new TessellCallBack(gl, glu);

		GLUtessellator tobj = glu.gluNewTess();
		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
		
		
		float x = (float) this.xPos;
		float y = (float)this.yPos;
		float z = (float)this.zPos;
		
		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);

		int curPolyGonNumPoints = 12;

		float angle;
		double tempPolygon[][] = new double[100][3];
		for (int k = 0; k < curPolyGonNumPoints; k++) {
			angle = (float) (k * 2 * Math.PI / curPolyGonNumPoints);

			tempPolygon[k][0] = (float) (x + (Math.cos(angle)) * radius);
			tempPolygon[k][1] = (float) (y + (Math.sin(angle)) * radius);
			tempPolygon[k][2] = z;
		}

		for (int j = 0; j < curPolyGonNumPoints; j++) {
			glu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);
	}

}
