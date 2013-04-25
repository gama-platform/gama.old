package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COMPILE;
import static javax.media.opengl.GL.GL_FILL;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LINE;
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL.GL_TRIANGLE_FAN;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import javax.vecmath.Vector3f;

import msi.gama.common.util.GeometryUtils;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.TessellCallBack;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.IList;

public class MyGraphics {

	// OpenGL member
	private GL myGl;
	private GLU myGlu;
	private TessellCallBack tessCallback;
	private GLUtessellator tobj;
	// Use to draw Bitmap String
	private final GLUT glut;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;
	
	public BasicOpenGlDrawer basicDrawer;
	
	public DisplayListHandler displayListHandler;
	
	public VertexArrayHandler vertexArrayHandler;

	// FIXME: Is it better to declare an objet polygon here than in
	// DrawMultiPolygon??
	Polygon curPolygon;
	int numExtPoints;
	int numGeometries;

	double tempPolygon[][];
	double temp[];

	float alpha = 1.0f;

	public MyGraphics( final JOGLAWTGLRenderer gLRender) {

		myGl = gLRender.gl;
		myGlu = gLRender.glu;
		myGLRender = gLRender;
		basicDrawer= new BasicOpenGlDrawer(myGLRender);
		displayListHandler = new DisplayListHandler(myGl, myGlu, myGLRender);
		vertexArrayHandler =  new VertexArrayHandler(myGl, myGlu, myGLRender);
		tessCallback = new TessellCallBack(myGl, myGlu);
		tobj = myGlu.gluNewTess();

		glut = new GLUT();

		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback)

	}

	public void DrawXYZAxis(final float size) {

		myGl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		DrawString("1:" + String.valueOf(size), size, size, 0.0f);
		// X Axis
		DrawString("x", 1.2f * size, 0.0f, 0.0f);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4f(1.0f, 0, 0, 1.0f);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(size, 0, 0);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(1.0f * size, 0.05f * size, 0.0f);
		myGl.glVertex3f(1.0f * size, -0.05f * size, 0.0f);
		myGl.glVertex3f(1.1f * size, 0.0f, 0.0f);
		myGl.glEnd();

		// Y Axis
		DrawString("y", 0.0f, 1.2f * size, 0.0f);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4f(0, 1.0f, 0, 1.0f);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(0, size, 0);
		myGl.glEnd();
		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(-0.05f * size, 1.0f * size, 0.0f);
		myGl.glVertex3f(0.05f * size, 1.0f * size, 0.0f);
		myGl.glVertex3f(0.0f, 1.1f * size, 0.0f);
		myGl.glEnd();

		// Z Axis
		myGl.glRasterPos3f(0.0f, 0.0f, 1.2f * size);
		DrawString("z", 0.0f, 0.0f, 1.2f * size);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4f(0, 0, 1.0f, 1.0f);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(0, 0, size);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(0.0f, 0.05f * size, 1.0f * size);
		myGl.glVertex3f(0.0f, -0.05f * size, 1.0f * size);
		myGl.glVertex3f(0.0f, 0.0f, 1.1f * size);
		myGl.glEnd();

	}

	public void DrawZValue(final float pos, final float value) {
		DrawString("z:" + String.valueOf(value), pos, pos, 0.0f);
	}

	public void DrawString(final String string, final float x, final float y,
			final float z) {

		// Need to disable blending when drawing glutBitmapString;
		myGl.glDisable(GL_BLEND);
		myGl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		myGl.glRasterPos3f(x, y, z);
		myGl.glScalef(8.0f, 8.0f, 8.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, string);
		//glut.glutBitmapString(GLUT.BITMAP_8_BY_13, string);
		myGl.glScalef(0.125f, 0.125f, 0.125f);
		//myGl.glEnable(GL_BLEND);

	}
	
	void DrawTorus(float innerRadius, float outterRadius,int sides, int rings){
		GLUT glut= new GLUT();
		glut.glutSolidTorus(innerRadius,outterRadius,sides,rings);
	}
	
	public void DrawArcBall(){
		
		//DrawXYZAxis(1.0f);
		float innerRadius= 0.075f;
		float outterRadius= 1.0f;
		myGl.glColor3f(0.0f, 0.0f, 1.0f);
		DrawTorus(innerRadius,outterRadius,100,100);
		
		myGl.glRotatef(90,0.0f, 1.0f, 0.0f);
		myGl.glColor3f(1.0f, 0.0f, 0.0f);
		DrawTorus(innerRadius,outterRadius,100,100);
		myGl.glRotatef(90,0.0f, -1.0f, 0.0f);
		
		myGl.glRotatef(90,1.0f, 0.0f, 0.0f);
		myGl.glColor3f(0.0f, 1.0f, 0.0f);
		DrawTorus(innerRadius,outterRadius,100,100);
	}
	
	
	
	
	//////////////////////////////////////// Rectangle with curved corner ////////////////////////////
	
	 public void DrawRoundRectangle(final Polygon p){
		 
		double width= p.getEnvelopeInternal().getWidth();
	    double height = p.getEnvelopeInternal().getHeight(); 
		 		
		myGl.glTranslated(p.getCentroid().getX(), -p.getCentroid().getY(), 0.0f);
			DrawRectangle(width, height*0.8, p.getCentroid());	
			DrawRectangle(width*0.8, height, p.getCentroid());
			DrawRoundCorner(width,height, width*0.1,height*0.1 , 5);
		myGl.glTranslated(-p.getCentroid().getX(), p.getCentroid().getY(), 0.0f);
		
		
	 }
	 
	 void DrawRectangle(double width, double height, Point point){
		 myGl.glBegin(GL_POLYGON); // draw using quads
		 myGl.glVertex3d(-width/2, height/2, 0.0f);
		 myGl.glVertex3d(width/2,height/2, 0.0f);
		 myGl.glVertex3d(width/2,-height/2 , 0.0f);
		 myGl.glVertex3d(-width/2, -height/2 , 0.0f);
		 myGl.glEnd();	 
	 }
	 
	 void DrawFan(double radius, double x, double y, int or_x, int or_y, int timestep){
		 myGl.glBegin(GL_TRIANGLE_FAN);     	//upper right
		 myGl.glVertex3d(or_x*x, or_y*y, 0.0f);
		 for (int i=0;i<=timestep;i++){
			 double anglerad = (Math.PI/2 * i)/timestep;
			 double xi = Math.cos(anglerad) * radius; 
			 double yi = Math.sin(anglerad) * radius;
			 myGl.glVertex3d(or_x*(x + xi), (y + yi), 0.0f);
		 }
	     myGl.glEnd(); 
	 }
	 
	 void DrawRoundCorner(double width, double height , double x_radius, double y_radius, int nbPoints){
		 
		 double xc = (width/2)*0.8;
		 double yc = (height/2)*0.8;
		 //Enhancement implement DrawFan(radius, xc, yc, 10);
		 
		 myGl.glBegin(GL_TRIANGLE_FAN);     	//upper right
		 myGl.glVertex3d(xc, yc, 0.0f);
		 for (int i=0;i<=nbPoints;i++){
			 double anglerad = (Math.PI/2 * i)/nbPoints;
			 double xi = Math.cos(anglerad) * x_radius; 
			 double yi = Math.sin(anglerad) * y_radius;
			 myGl.glVertex3d((xc + xi), (yc + yi), 0.0f);
		 }
	     myGl.glEnd();
	     
	     myGl.glBegin(GL_TRIANGLE_FAN);     	// upper right
	     
	     myGl.glVertex3d(xc, -yc, 0.0f);
		 for (int i=0;i<=nbPoints;i++){
			 double anglerad = (Math.PI/2 * i)/nbPoints;
			 double xi = Math.cos(anglerad) * x_radius; 
			 double yi = Math.sin(anglerad) * y_radius;
			 myGl.glVertex3d((xc + xi), -(yc + yi), 0.0f);
		 }
	     myGl.glEnd();
	     
	     myGl.glBegin(GL_TRIANGLE_FAN);     	//upper left
	     
	     myGl.glVertex3d(-xc, yc, 0.0f);
		 for (int i=0;i<=nbPoints;i++){
			 double anglerad = (Math.PI/2 * i)/nbPoints;
			 double xi = Math.cos(anglerad) * x_radius; 
			 double yi = Math.sin(anglerad) * y_radius;
			 myGl.glVertex3d(-(xc + xi), (yc + yi), 0.0f);
		 }
	     myGl.glEnd();
	     
	     myGl.glBegin(GL_TRIANGLE_FAN);
	     myGl.glVertex3d(-xc, -yc, 0.0f);   //down left
		 for (int i=0;i<=nbPoints;i++){
			 double anglerad = (Math.PI/2 * i)/nbPoints;
			 double xi = Math.cos(anglerad) * x_radius; 
			 double yi = Math.sin(anglerad) * y_radius;
			 myGl.glVertex3d(-(xc + xi), -(yc + yi), 0.0f);
		 }
	     myGl.glEnd(); 
	 }

}
