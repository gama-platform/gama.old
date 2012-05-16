package msi.gama.jogl.utils;



import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import com.sun.opengl.util.texture.*;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import javax.vecmath.Vector3f;

public class MyGraphics {
	
	// OpenGL member
	private GL myGl;
	private GLU myGlu;
	private TessellCallBack tessCallback;
	private GLUtessellator tobj;
	
	//FIXME: Is it better to declare an objet polygon here than in DrawMultiPolygon??
	Polygon curPolygon;
	int numExtPoints;
	int numGeometries;
	
	//FIXME: Create to avoir creating int i,j each framerate.
	int i,j;
	
	float alpha = 0.5f;

	public  MyGraphics(final GL gl, final GLU glu) {
		
		myGl = gl;
		myGlu = glu;
		tessCallback = new TessellCallBack(gl, glu);
		tobj = glu.gluNewTess();

	}

	public void DrawLine(MyGeometry geometry, float size) {
		// FIXME: Should test that vertices is initialized before to draw
		myGl.glLineWidth(size);
		myGl.glBegin(GL.GL_LINES);
		for (j = 0; j < geometry.vertices.length - 1; j++) {
			myGl.glVertex3f((float) ((geometry.vertices[j].x)),
					(float) ((geometry.vertices[j].y)),
					(float) ((geometry.vertices[j].z)));
			myGl.glVertex3f((float) ((geometry.vertices[j + 1].x)),
					(float) ((geometry.vertices[j + 1].y)),
					(float) ((geometry.vertices[j + 1].z)));
		}
		myGl.glEnd();

	}

	public void DrawCircle(float x, float y, float z,
			int numPoints, float radius) {
		
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);

		float angle;
		double tempPolygon[][] = new double[100][3];
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);

			tempPolygon[k][0] = (float) (x + (Math.cos(angle)) * radius);
			tempPolygon[k][1] = (float) (y + (Math.sin(angle)) * radius);
			tempPolygon[k][2] = z;
		}

		for (int k = 0; k < numPoints; k++) {
			myGlu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		myGl.glColor4f(0.0f, 0.0f, 0.0f,alpha);
		myGl.glLineWidth(1.1f);
		myGl.glBegin(GL.GL_LINES);
		float xBegin, xEnd, yBegin, yEnd;
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);
			xBegin = (float) (x + (Math.cos(angle)) * radius);
			yBegin = (float) (y + (Math.sin(angle)) * radius);
			angle = (float) ((k + 1) * 2 * Math.PI / numPoints);
			xEnd = (float) (x + (Math.cos(angle)) * radius);
			yEnd = (float) (y + (Math.sin(angle)) * radius);
			myGl.glVertex3f(xBegin, yBegin, z);
			myGl.glVertex3f(xEnd, yEnd, z);
		}
		myGl.glEnd();

	}

	public void DrawGeometry(MyGeometry geometry, float z_offset) {


		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);

		int curPolyGonNumPoints = geometry.vertices.length;
		double tempPolygon[][] = new double[curPolyGonNumPoints][3];

		// Convert vertices as a list of double for
		// gluTessVertex
		for (j = 0; j < curPolyGonNumPoints; j++) {
			tempPolygon[j][0] = (float) (geometry.vertices[j].x);
			tempPolygon[j][1] = (float) (geometry.vertices[j].y);
			tempPolygon[j][2] = (float) (geometry.vertices[j].z + z_offset);
		}

		for (j = 0; j < curPolyGonNumPoints; j++) {
			myGlu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}
		// myGl.glNormal3f(0.0f, 1.0f, 0.0f);

		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);

		// FIXME: This add a black line around the polygon.
		// For a better visual quality but we should check the cost of it.
		myGl.glColor4f(0.0f, 0.0f, 0.0f,alpha);
		this.DrawLine(geometry, 1.0f);

	}

	public void Draw3DQuads(MyGeometry geometry, float z_offset) {
		int curPolyGonNumPoints = geometry.vertices.length;
		for (j = 0; j < curPolyGonNumPoints; j++) {
			int k = (j + 1) % curPolyGonNumPoints;
			myGl.glBegin(GL.GL_QUADS);
			if (j == 3) {
				myGl.glNormal3f(0.0f, 0.0f, 1.0f);
			}
			if (j == 0) {
				myGl.glNormal3f(-1.0f, 0.0f, 0.0f);
			}
			if (j == 1) {
				myGl.glNormal3f(0.0f, 0.0f, -1.0f);
			}

			if (j == 2) {
				myGl.glNormal3f(1.0f, 0.0f, 0.0f);
			}

			Vertex[] vertices = new Vertex[4];
			for (i = 0; i < 4; i++) {
				vertices[i] = new Vertex();
			}
			vertices[0].x = geometry.vertices[j].x;
			vertices[0].y = geometry.vertices[j].y;
			vertices[0].z = geometry.vertices[j].z + z_offset;

			vertices[1].x = geometry.vertices[k].x;
			vertices[1].y = geometry.vertices[k].y;
			vertices[1].z = geometry.vertices[k].z + z_offset;

			vertices[2].x = geometry.vertices[k].x;
			vertices[2].y = geometry.vertices[k].y;
			vertices[2].z = geometry.vertices[k].z;

			vertices[3].x = geometry.vertices[j].x;
			vertices[3].y = geometry.vertices[j].y;
			vertices[3].z = geometry.vertices[j].z;

			// Compute the normal of the quad
			Vector3f normal = new Vector3f(0.0f, 0.0f, 0.0f);

			for (i = 0; i < 4; i++) {
				int i1 = (i + 1) % 4;
				normal.x += (vertices[i].y - vertices[i1].y)
						* (vertices[i].z + vertices[i1].z);
				normal.y += (vertices[i].z - vertices[i1].z)
						* (vertices[i].x + vertices[i1].x);
				normal.z += (vertices[i].x - vertices[i1].x)
						* (vertices[i].y + vertices[i1].y);
			}
			normal.normalize(normal);
			// FIXME: The normal is not well computed.
			// myGl.glNormal3f((float)normal.x, (float)normal.y, (float)normal.z);
			myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
			myGl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
			myGl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
			myGl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);

			myGl.glEnd();
		}

	}

	public void DrawOpenGLHelloWorldShape() {

		float red = (float) (Math.random()) * 1;
		float green = (float) (Math.random()) * 1;
		float blue = (float) (Math.random()) * 1;

		myGl.glColor4f(red, green, blue,alpha);
		// ----- Render a quad -----
		myGl.glBegin(GL.GL_POLYGON); // draw using quads
		myGl.glVertex3f(-1.0f, 1.0f, 0.0f);
		myGl.glVertex3f(1.0f, 1.0f, 0.0f);
		myGl.glVertex3f(0.0f, 0.0f, 0.0f);
		myGl.glVertex3f(-1.0f, -1.0f, 0.0f);
		myGl.glEnd();
	}

	public void DrawJTSGeometry(Geometry geometry, Color c) {

		// System.out.println("DrawJTSGraphics:" + geometry.getGeometryType());
		for (i = 0; i < geometry.getNumGeometries(); i++) {

			if (geometry.getGeometryType() == "MultiPolygon") {
				MultiPolygon polygons = (MultiPolygon) geometry;
				DrawMultiPolygon(polygons,c);
			}

			else if (geometry.getGeometryType() == "Polygon") {
				Polygon polygon = (Polygon) geometry;
				DrawPolygon(polygon,c);
			}

			else if (geometry.getGeometryType() == "MultiLineString") {
				MultiLineString lines = (MultiLineString) geometry;
				DrawMultiLineString(lines,c);
			}

			else if (geometry.getGeometryType() == "LineString") {
				LineString line = (LineString) geometry;
				DrawLineString(line, 1.2f,c);
			}

			else if (geometry.getGeometryType() == "Point") {
				Point point = (Point) geometry;
				DrawPoint(point, 10, 10,c);
			}
		}
	}

	public void DrawMultiPolygon(MultiPolygon polygons, Color c) {

		numGeometries = polygons.getNumGeometries();
		//System.out.println("Draw MultiPolygon:"+numGeometries);
		
		// for each polygon of a multipolygon, get each point coordinates.
		for (i = 0; i < numGeometries; i++) {
			myGl.glColor4f((float) c.getRed() / 255,
					(float) c.getGreen() / 255,
					(float) c.getBlue() / 255, alpha);
			curPolygon = (Polygon) polygons.getGeometryN(i);
			DrawPolygon(curPolygon,c);
		}

	}

	public void DrawPolygon(Polygon p,Color c) {

		myGl.glColor4f((float) c.getRed() / 255,
				(float) c.getGreen() / 255,
				(float) c.getBlue() / 255,alpha);
		numExtPoints = p.getExteriorRing().getNumPoints();
		//System.out.println("Draw Polygon:"+numExtPoints);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback)


		myGl.glNormal3f(0.0f, 0.0f, 1.0f);
		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);

		double tempPolygon[][] = new double[numExtPoints][3];

		// Convert vertices as a list of double for
		// gluTessVertex
		for (j = 0; j < numExtPoints; j++) {
			tempPolygon[j][0] = (float) (float) (p.getExteriorRing().getPointN(
					j).getX());
			tempPolygon[j][1] = -(float) (p.getExteriorRing().getPointN(j)
					.getY());
			tempPolygon[j][2] = (float) (0);
		}

		for (j = 0; j < numExtPoints; j++) {
			myGlu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}


		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);

		// Draw contour
		myGl.glColor4f(0.0f, 0.0f, 0.0f,alpha);
		myGl.glBegin(GL.GL_LINES);
		for (j = 0; j < numExtPoints - 1; j++) {
			myGl.glLineWidth(1.0f);
			myGl.glVertex3f((float) ((p.getExteriorRing().getPointN(j).getX())),
					-(float) ((p.getExteriorRing().getPointN(j).getY())), 0.0f);
			myGl.glVertex3f(
					(float) ((p.getExteriorRing().getPointN(j + 1).getX())),
					-(float) ((p.getExteriorRing().getPointN(j + 1).getY())),
					0.0f);
		}
		myGl.glEnd();
	}

	public void DrawMultiLineString(MultiLineString lines, Color c) {

		// get the number of line in the multiline.
		numGeometries = lines.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for (i = 0; i < numGeometries; i++) {
			
			myGl.glColor4f((float) c.getRed() / 255,
					(float) c.getGreen() / 255,
					(float) c.getBlue() / 255,alpha);

			LineString l = (LineString) lines.getGeometryN(i);
			int numPoints = l.getNumPoints();
			MyGeometry curGeometry = new MyGeometry(numPoints);
			// myGl.glLineWidth(size);
			myGl.glBegin(GL.GL_LINES);
			for (j = 0; j < numPoints - 1; j++) {
				myGl.glVertex3f((float) ((l.getPointN(j).getX())),
						-(float) ((l.getPointN(j).getY())), (float) (0));
				myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
						-(float) ((l.getPointN(j + 1).getY())), (float) (0));
			}
			myGl.glEnd();
		}
	}

	public void DrawLineString(LineString line, float size, Color c) {

		myGl.glColor4f((float) c.getRed() / 255,
				(float) c.getGreen() / 255,
				(float) c.getBlue() / 255,alpha);
		int numPoints = line.getNumPoints();
		myGl.glLineWidth(size);
		myGl.glBegin(GL.GL_LINES);
		for (j = 0; j < numPoints - 1; j++) {
			myGl.glVertex3f((float) ((line.getPointN(j).getX())),
					(float) ((line.getPointN(j).getY())), (float) ((0)));
			myGl.glVertex3f((float) ((line.getPointN(j + 1).getX())),
					(float) ((line.getPointN(j + 1).getY())), (float) ((0)));
		}
		myGl.glEnd();

	}

	public void DrawPoint(Point point, int numPoints,
			float radius, Color c) {

		myGl.glColor4f((float) c.getRed() / 255,
				(float) c.getGreen() / 255,
				(float) c.getBlue() / 255,alpha);
		

		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);

		float angle;
		double tempPolygon[][] = new double[100][3];
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);

			tempPolygon[k][0] = (float) (point.getCoordinate().x + (Math
					.cos(angle)) * radius);
			tempPolygon[k][1] = (float) (point.getCoordinate().y + (Math
					.sin(angle)) * radius);
			tempPolygon[k][2] = 0;
		}

		for (int k = 0; k < numPoints; k++) {
			myGlu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		myGl.glColor4f(0.0f, 0.0f, 0.0f,alpha);
		myGl.glLineWidth(1.1f);
		myGl.glBegin(GL.GL_LINES);
		float xBegin, xEnd, yBegin, yEnd;
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);
			xBegin = (float) (point.getCoordinate().x + (Math.cos(angle))
					* radius);
			yBegin = (float) (point.getCoordinate().y + (Math.sin(angle))
					* radius);
			angle = (float) ((k + 1) * 2 * Math.PI / numPoints);
			xEnd = (float) (point.getCoordinate().x + (Math.cos(angle))
					* radius);
			yEnd = (float) (point.getCoordinate().y + (Math.sin(angle))
					* radius);
			myGl.glVertex3f(xBegin, yBegin, 0);
			myGl.glVertex3f(xEnd, yEnd, 0);
		}
		myGl.glEnd();

	}

}
