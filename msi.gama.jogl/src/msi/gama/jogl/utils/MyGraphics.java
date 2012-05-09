package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLES;

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

	public void MyGraphics() {

	}

	public void DrawLine(GL gl, GLU glu, MyGeometry geometry, float size) {
		// FIXME: Should test that vertices is initialized before to draw
		gl.glLineWidth(size);
		gl.glBegin(GL.GL_LINES);
		for (int j = 0; j < geometry.vertices.length - 1; j++) {
			gl.glVertex3f((float) ((geometry.vertices[j].x)),
					(float) ((geometry.vertices[j].y)),
					(float) ((geometry.vertices[j].z)));
			gl.glVertex3f((float) ((geometry.vertices[j + 1].x)),
					(float) ((geometry.vertices[j + 1].y)),
					(float) ((geometry.vertices[j + 1].z)));
		}
		gl.glEnd();

	}

	public void DrawCircle(GL gl, GLU glu, float x, float y, float z,
			int numPoints, float radius) {

		TessellCallBack tessCallback = new TessellCallBack(gl, glu);

		GLUtessellator tobj = glu.gluNewTess();
		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);

		float angle;
		double tempPolygon[][] = new double[100][3];
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);

			tempPolygon[k][0] = (float) (x + (Math.cos(angle)) * radius);
			tempPolygon[k][1] = (float) (y + (Math.sin(angle)) * radius);
			tempPolygon[k][2] = z;
		}

		for (int k = 0; k < numPoints; k++) {
			glu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glLineWidth(1.1f);
		gl.glBegin(GL.GL_LINES);
		float xBegin, xEnd, yBegin, yEnd;
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);
			xBegin = (float) (x + (Math.cos(angle)) * radius);
			yBegin = (float) (y + (Math.sin(angle)) * radius);
			angle = (float) ((k + 1) * 2 * Math.PI / numPoints);
			xEnd = (float) (x + (Math.cos(angle)) * radius);
			yEnd = (float) (y + (Math.sin(angle)) * radius);
			gl.glVertex3f(xBegin, yBegin, z);
			gl.glVertex3f(xEnd, yEnd, z);
		}
		gl.glEnd();

	}

	public void DrawGeometry(GL gl, GLU glu, MyGeometry geometry, float z_offset) {

		TessellCallBack tessCallback = new TessellCallBack(gl, glu);

		GLUtessellator tobj = glu.gluNewTess();
		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);

		int curPolyGonNumPoints = geometry.vertices.length;
		double tempPolygon[][] = new double[curPolyGonNumPoints][3];

		// Convert vertices as a list of double for
		// gluTessVertex
		for (int j = 0; j < curPolyGonNumPoints; j++) {
			tempPolygon[j][0] = (float) (geometry.vertices[j].x);
			tempPolygon[j][1] = (float) (geometry.vertices[j].y);
			tempPolygon[j][2] = (float) (geometry.vertices[j].z + z_offset);
		}

		for (int j = 0; j < curPolyGonNumPoints; j++) {
			glu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}
		// gl.glNormal3f(0.0f, 1.0f, 0.0f);

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);

		// FIXME: This add a black line around the polygon.
		// For a better visual quality but we should check the cost of it.
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		this.DrawLine(gl, glu, geometry, 1.0f);

	}

	public void Draw3DQuads(GL gl, GLU glu, MyGeometry geometry, float z_offset) {
		int curPolyGonNumPoints = geometry.vertices.length;
		for (int j = 0; j < curPolyGonNumPoints; j++) {
			int k = (j + 1) % curPolyGonNumPoints;
			gl.glBegin(GL_QUADS);
			if (j == 3) {
				gl.glNormal3f(0.0f, 0.0f, 1.0f);
			}
			if (j == 0) {
				gl.glNormal3f(-1.0f, 0.0f, 0.0f);
			}
			if (j == 1) {
				gl.glNormal3f(0.0f, 0.0f, -1.0f);
			}

			if (j == 2) {
				gl.glNormal3f(1.0f, 0.0f, 0.0f);
			}

			Vertex[] vertices = new Vertex[4];
			for (int i = 0; i < 4; i++) {
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

			for (int i = 0; i < 4; i++) {
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
			// gl.glNormal3f((float)normal.x, (float)normal.y, (float)normal.z);
			gl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
			gl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
			gl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
			gl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);

			gl.glEnd();
		}

	}

	public void DrawOpenGLHelloWorldShape(GL gl) {

		float red = (float) (Math.random()) * 1;
		float green = (float) (Math.random()) * 1;
		float blue = (float) (Math.random()) * 1;

		gl.glColor3f(red, green, blue);
		// ----- Render a quad -----
		gl.glBegin(GL_POLYGON); // draw using quads
		gl.glVertex3f(-1.0f, 1.0f, 0.0f);
		gl.glVertex3f(1.0f, 1.0f, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glEnd();
	}

	public void DrawJTSGeometry(GL gl, GLU glu, Geometry geometry, Color c) {

		// System.out.println("DrawJTSGraphics:" + geometry.getGeometryType());
		for (int i = 0; i < geometry.getNumGeometries(); i++) {

			if (geometry.getGeometryType() == "MultiPolygon") {
				MultiPolygon polygons = (MultiPolygon) geometry;
				DrawMultiPolygon(gl, glu, polygons,c);
			}

			else if (geometry.getGeometryType() == "Polygon") {
				Polygon polygon = (Polygon) geometry;
				DrawPolygon(gl, glu, polygon,c);
			}

			else if (geometry.getGeometryType() == "MultiLineString") {
				MultiLineString lines = (MultiLineString) geometry;
				DrawMultiLineString(gl, lines,c);
			}

			else if (geometry.getGeometryType() == "LineString") {
				LineString line = (LineString) geometry;
				DrawLineString(gl, line, 1.2f,c);
			}

			else if (geometry.getGeometryType() == "Point") {
				Point point = (Point) geometry;
				DrawPoint(gl, glu, point, 10, 10,c);
			}
		}
	}

	public void DrawMultiPolygon(GL gl, GLU glu, MultiPolygon polygons, Color c) {

		TessellCallBack tessCallback = new TessellCallBack(gl, glu);

		GLUtessellator tobj = glu.gluNewTess();
		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		int numExtPoints;
		int N = polygons.getNumGeometries();


		// for each polygon of a multipolygon, get each point coordinates.
		for (int i = 0; i < N; i++) {
			gl.glColor3f((float) c.getRed() / 255,
					(float) c.getGreen() / 255,
					(float) c.getBlue() / 255);
			Polygon p = (Polygon) polygons.getGeometryN(i);
			DrawPolygon(gl, glu, p,c);
		}

	}

	public void DrawPolygon(GL gl, GLU glu, Polygon p,Color c) {

		gl.glColor3f((float) c.getRed() / 255,
				(float) c.getGreen() / 255,
				(float) c.getBlue() / 255);
		
		TessellCallBack tessCallback = new TessellCallBack(gl, glu);

		GLUtessellator tobj = glu.gluNewTess();
		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback)

		int numExtPoints = p.getExteriorRing().getNumPoints();

		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);

		double tempPolygon[][] = new double[numExtPoints][3];

		// Convert vertices as a list of double for
		// gluTessVertex
		for (int j = 0; j < numExtPoints; j++) {
			tempPolygon[j][0] = (float) (float) (p.getExteriorRing().getPointN(
					j).getX());
			tempPolygon[j][1] = -(float) (p.getExteriorRing().getPointN(j)
					.getY());
			tempPolygon[j][2] = (float) (0);
		}

		for (int j = 0; j < numExtPoints; j++) {
			glu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}
		// gl.glNormal3f(0.0f, 1.0f, 0.0f);

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);

		// Draw contour
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINES);
		for (int j = 0; j < numExtPoints - 1; j++) {
			gl.glLineWidth(1.0f);
			gl.glVertex3f((float) ((p.getExteriorRing().getPointN(j).getX())),
					-(float) ((p.getExteriorRing().getPointN(j).getY())), 0.0f);
			gl.glVertex3f(
					(float) ((p.getExteriorRing().getPointN(j + 1).getX())),
					-(float) ((p.getExteriorRing().getPointN(j + 1).getY())),
					0.0f);
		}
		gl.glEnd();
	}

	public void DrawMultiLineString(GL gl, MultiLineString lines, Color c) {

		// get the number of line in the multiline.
		int N = lines.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for (int i = 0; i < N; i++) {
			
			gl.glColor3f((float) c.getRed() / 255,
					(float) c.getGreen() / 255,
					(float) c.getBlue() / 255);

			LineString l = (LineString) lines.getGeometryN(i);
			int numPoints = l.getNumPoints();
			MyGeometry curGeometry = new MyGeometry(numPoints);
			// gl.glLineWidth(size);
			gl.glBegin(GL.GL_LINES);
			for (int j = 0; j < numPoints - 1; j++) {
				gl.glVertex3f((float) ((l.getPointN(j).getX())),
						-(float) ((l.getPointN(j).getY())), (float) (0));
				gl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
						-(float) ((l.getPointN(j + 1).getY())), (float) (0));
			}
			gl.glEnd();
		}
	}

	public void DrawLineString(GL gl, LineString line, float size, Color c) {

		gl.glColor3f((float) c.getRed() / 255,
				(float) c.getGreen() / 255,
				(float) c.getBlue() / 255);
		int numPoints = line.getNumPoints();
		gl.glLineWidth(size);
		gl.glBegin(GL.GL_LINES);
		for (int j = 0; j < numPoints - 1; j++) {
			gl.glVertex3f((float) ((line.getPointN(j).getX())),
					(float) ((line.getPointN(j).getY())), (float) ((0)));
			gl.glVertex3f((float) ((line.getPointN(j + 1).getX())),
					(float) ((line.getPointN(j + 1).getY())), (float) ((0)));
		}
		gl.glEnd();

	}

	public void DrawPoint(GL gl, GLU glu, Point point, int numPoints,
			float radius, Color c) {

		gl.glColor3f((float) c.getRed() / 255,
				(float) c.getGreen() / 255,
				(float) c.getBlue() / 255);
		
		TessellCallBack tessCallback = new TessellCallBack(gl, glu);

		GLUtessellator tobj = glu.gluNewTess();
		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);

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
			glu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glLineWidth(1.1f);
		gl.glBegin(GL.GL_LINES);
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
			gl.glVertex3f(xBegin, yBegin, 0);
			gl.glVertex3f(xEnd, yEnd, 0);
		}
		gl.glEnd();

	}

}
