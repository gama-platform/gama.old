package msi.gama.jogl.utils;

import static javax.media.opengl.GL.*;
import java.awt.Color;
import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.TessellCallBack;
import msi.gama.metamodel.shape.GamaPoint;
import com.sun.opengl.util.GLUT;
import com.vividsolutions.jts.geom.*;

public class MyGraphics {

	// OpenGL member
	private final GL myGl;
	private final GLU myGlu;
	private final TessellCallBack tessCallback;
	private final GLUtessellator tobj;
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

	double alpha = 1.0d;

	public MyGraphics(final JOGLAWTGLRenderer gLRender) {

		myGl = gLRender.gl;
		myGlu = gLRender.glu;
		myGLRender = gLRender;
		basicDrawer = new BasicOpenGlDrawer(myGLRender);
		displayListHandler = new DisplayListHandler(myGl, myGlu, myGLRender);
		vertexArrayHandler = new VertexArrayHandler(myGl, myGlu, myGLRender);
		tessCallback = new TessellCallBack(myGl, myGlu);
		tobj = myGlu.gluNewTess();

		glut = new GLUT();

		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback)

	}

	final GamaPoint offset = new GamaPoint(0, 0, 0);
	final GamaPoint scale = new GamaPoint(1, 1, 1);

	public void DrawXYZAxis(final double size) {
		// FIXME Define as static constants

		myGl.glColor4d(0.0d, 0.0d, 0.0d, 1.0d);
		drawString("1:" + String.valueOf(size), size, size, 0.0d, 0.0, offset, scale, Color.black);
		// X Axis
		drawString("x", 1.2f * size, 0.0d, 0.0d, 0.0, offset, scale, Color.black);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4d(1.0d, 0, 0, 1.0d);
		myGl.glVertex3d(0, 0, 0);
		myGl.glVertex3d(size, 0, 0);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3d(1.0d * size, 0.05d * size, 0.0d);
		myGl.glVertex3d(1.0d * size, -0.05d * size, 0.0d);
		myGl.glVertex3d(1.1f * size, 0.0d, 0.0d);
		myGl.glEnd();

		// Y Axis
		drawString("y", 0.0d, 1.2f * size, 0.0d, 0.0, offset, scale, Color.black);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4d(0, 1.0d, 0, 1.0d);
		myGl.glVertex3d(0, 0, 0);
		myGl.glVertex3d(0, size, 0);
		myGl.glEnd();
		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3d(-0.05d * size, 1.0d * size, 0.0d);
		myGl.glVertex3d(0.05d * size, 1.0d * size, 0.0d);
		myGl.glVertex3d(0.0d, 1.1f * size, 0.0d);
		myGl.glEnd();

		// Z Axis
		myGl.glRasterPos3d(0.0d, 0.0d, 1.2f * size);
		drawString("z", 0.0d, 0.0d, 1.2f * size, 0.0, offset, scale, Color.black);
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4d(0, 0, 1.0d, 1.0d);
		myGl.glVertex3d(0, 0, 0);
		myGl.glVertex3d(0, 0, size);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3d(0.0d, 0.05d * size, 1.0d * size);
		myGl.glVertex3d(0.0d, -0.05d * size, 1.0d * size);
		myGl.glVertex3d(0.0d, 0.0d, 1.1f * size);
		myGl.glEnd();

	}

	public void DrawZValue(final double pos, final float value) {
		drawString("z:" + String.valueOf(value), pos, pos, 0.0d, 0.0d, offset, scale, Color.black);
	}

	public void drawString(final String string, final double x, final double y, final double z, final double z_layer,
		final GamaPoint offset, final GamaPoint scale, final Color color) {
		myGl.glTranslated(offset.x, -offset.y, 0.0);
		// TODO Scale en Z
		myGl.glScaled(scale.x, scale.y, 1);

		// Need to disable blending when drawing glutBitmapString;
		myGl.glDisable(GL_BLEND);
		myGl.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 1.0d);
		myGl.glRasterPos3d(x, y, z + z_layer);
		myGl.glScaled(8.0d, 8.0d, 8.0d);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, string);
		// glut.glutBitmapString(GLUT.BITMAP_8_BY_13, string);
		myGl.glScaled(0.125d, 0.125d, 0.125d);
		// myGl.glEnable(GL_BLEND);
		// TODO Scale en Z
		myGl.glScaled(1 / scale.x, 1 / scale.y, 1);
		myGl.glTranslated(-offset.x, offset.y, 0.0);

	}

	void DrawTorus(double innerRadius, double outterRadius, int sides, int rings) {
		GLUT glut = new GLUT();
		glut.glutSolidTorus(innerRadius, outterRadius, sides, rings);
	}

	public void DrawArcBall() {

		// DrawXYZAxis(1.0d);
		double innerRadius = 0.075d;
		double outterRadius = 1.0d;
		myGl.glColor3d(0.0d, 0.0d, 1.0d);
		DrawTorus(innerRadius, outterRadius, 100, 100);

		myGl.glRotated(90, 0.0d, 1.0d, 0.0d);
		myGl.glColor3d(1.0d, 0.0d, 0.0d);
		DrawTorus(innerRadius, outterRadius, 100, 100);
		myGl.glRotated(90, 0.0d, -1.0d, 0.0d);

		myGl.glRotated(90, 1.0d, 0.0d, 0.0d);
		myGl.glColor3d(0.0d, 1.0d, 0.0d);
		DrawTorus(innerRadius, outterRadius, 100, 100);
	}

	// ////////////////////////////////////// Rectangle with curved corner ////////////////////////////

	public void DrawRoundRectangle(final Polygon p) {

		double width = p.getEnvelopeInternal().getWidth();
		double height = p.getEnvelopeInternal().getHeight();

		myGl.glTranslated(p.getCentroid().getX(), -p.getCentroid().getY(), 0.0d);
		DrawRectangle(width, height * 0.8, p.getCentroid());
		DrawRectangle(width * 0.8, height, p.getCentroid());
		DrawRoundCorner(width, height, width * 0.1, height * 0.1, 5);
		myGl.glTranslated(-p.getCentroid().getX(), p.getCentroid().getY(), 0.0d);

	}

	void DrawRectangle(double width, double height, Point point) {
		myGl.glBegin(GL_POLYGON); // draw using quads
		myGl.glVertex3d(-width / 2, height / 2, 0.0d);
		myGl.glVertex3d(width / 2, height / 2, 0.0d);
		myGl.glVertex3d(width / 2, -height / 2, 0.0d);
		myGl.glVertex3d(-width / 2, -height / 2, 0.0d);
		myGl.glEnd();
	}

	void DrawFan(double radius, double x, double y, int or_x, int or_y, int timestep) {
		myGl.glBegin(GL_TRIANGLE_FAN); // upper right
		myGl.glVertex3d(or_x * x, or_y * y, 0.0d);
		for ( int i = 0; i <= timestep; i++ ) {
			double anglerad = Math.PI / 2 * i / timestep;
			double xi = Math.cos(anglerad) * radius;
			double yi = Math.sin(anglerad) * radius;
			myGl.glVertex3d(or_x * (x + xi), y + yi, 0.0d);
		}
		myGl.glEnd();
	}

	void DrawRoundCorner(double width, double height, double x_radius, double y_radius, int nbPoints) {

		double xc = width / 2 * 0.8;
		double yc = height / 2 * 0.8;
		// Enhancement implement DrawFan(radius, xc, yc, 10);

		myGl.glBegin(GL_TRIANGLE_FAN); // upper right
		myGl.glVertex3d(xc, yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			myGl.glVertex3d(xc + xi, yc + yi, 0.0d);
		}
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLE_FAN); // upper right

		myGl.glVertex3d(xc, -yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			myGl.glVertex3d(xc + xi, -(yc + yi), 0.0d);
		}
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLE_FAN); // upper left

		myGl.glVertex3d(-xc, yc, 0.0d);
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			myGl.glVertex3d(-(xc + xi), yc + yi, 0.0d);
		}
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLE_FAN);
		myGl.glVertex3d(-xc, -yc, 0.0d); // down left
		for ( int i = 0; i <= nbPoints; i++ ) {
			double anglerad = Math.PI / 2 * i / nbPoints;
			double xi = Math.cos(anglerad) * x_radius;
			double yi = Math.sin(anglerad) * y_radius;
			myGl.glVertex3d(-(xc + xi), -(yc + yi), 0.0d);
		}
		myGl.glEnd();
	}

}
