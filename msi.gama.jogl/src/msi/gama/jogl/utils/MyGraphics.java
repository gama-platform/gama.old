package msi.gama.jogl.utils;


import static javax.media.opengl.GL.GL_QUADS;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

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

	public void DrawLine(GL gl, GLU glu, MyGeometry geometry) {
		// FIXME: Should test that vertices is initialized before to draw
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

	}
	
	public void DrawNormalizeCircle(GL gl, GLU glu, float x, float y, float z,
			int numPoints, float radius,float scale) {

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

			tempPolygon[k][0] = (float) (x + (Math.cos(angle)) * radius)*scale;
			tempPolygon[k][1] = (float) (y + (Math.sin(angle)) * radius)*scale;
			tempPolygon[k][2] = z;
		}

		for (int k = 0; k < numPoints; k++) {
			glu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);

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

	}
	
	
	public void DrawNormalizeGeometry(GL gl, GLU glu, MyGeometry geometry, float z_offset,float scale) {

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
			tempPolygon[j][0] = (float) (geometry.vertices[j].x*scale);
			tempPolygon[j][1] = (float) (geometry.vertices[j].y*scale);
			tempPolygon[j][2] = (float) ((geometry.vertices[j].z + z_offset)*scale);
		}

		for (int j = 0; j < curPolyGonNumPoints; j++) {
			glu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}
		// gl.glNormal3f(0.0f, 1.0f, 0.0f);

		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);

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
			
			//Compute the normal of the quad
			 Vector3f normal = new Vector3f(0.0f,0.0f,0.0f);
			
			
			 for (int i=0; i<4; i++)
			 {
			 int i1 = (i+1)%4;
			 normal.x += (vertices[i].y - vertices[i1].y)
			 *(vertices[i].z + vertices[i1].z);
			 normal.y += (vertices[i].z - vertices[i1].z)
			 *(vertices[i].x + vertices[i1].x);
			 normal.z += (vertices[i].x - vertices[i1].x)
			 *(vertices[i].y + vertices[i1].y);
			 }
			 normal.normalize(normal);
			 //FIXME: The normal is not wel computed.
			// gl.glNormal3f((float)normal.x, (float)normal.y, (float)normal.z);
			 gl.glVertex3f(vertices[0].x, vertices[0].y,vertices[0].z);
			 gl.glVertex3f(vertices[1].x, vertices[1].y,vertices[1].z);
			 gl.glVertex3f(vertices[2].x, vertices[2].y,vertices[2].z);
			 gl.glVertex3f(vertices[3].x, vertices[3].y,vertices[3].z);
			 			 
			gl.glEnd();
		}
	}

}
