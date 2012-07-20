package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COMPILE;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.Color;
import java.awt.List;
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import javax.vecmath.Vector3f;

import msi.gama.common.util.GeometryUtils;
import msi.gama.jogl.JOGLAWTDisplayGraphics;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;
import msi.gama.util.IList;

public class BasicOpenGlDrawer {

	// OpenGL member
	private GL myGl;
	private GLU myGlu;
	private TessellCallBack tessCallback;
	private GLUtessellator tobj;


	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	// FIXME: Is it better to declare an objet polygon here than in
	// DrawMultiPolygon??
	Polygon curPolygon;
	int numExtPoints;
	int numGeometries;

	double tempPolygon[][];
	double temp[];

	//use glut tesselation or JTS tesselation
	boolean useTessellation = false;
	
	
	
	//Use for JTS triangulation
	IList<IShape> triangles;
	Iterator<IShape> it;
	//public static GeometryFactory factory = new GeometryFactory();

	public BasicOpenGlDrawer(final GL gl, final GLU glu,
			final JOGLAWTGLRenderer gLRender) {

		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		tessCallback = new TessellCallBack(myGl, myGlu);
		tobj = glu.gluNewTess();
		
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		
		//FIXME: Need to understand why when using erroCallback there is a out of memory problem.
		//myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback)

	}

    /**
     * Draw a geometry
     * @param geometry
     */
	public void DrawJTSGeometry(MyJTSGeometry geometry) {


		
		for (int i = 0; i < geometry.geometry.getNumGeometries(); i++) {

			if (geometry.geometry.getGeometryType() == "MultiPolygon") {
				DrawMultiPolygon((MultiPolygon) geometry.geometry, geometry.z, geometry.color,
						geometry.alpha,geometry.fill, geometry.angle, geometry.height);
			}

			else if (geometry.geometry.getGeometryType() == "Polygon") {
				if (geometry.height > 0) {
					DrawPolyhedre((Polygon) geometry.geometry, geometry.z, geometry.color,
							geometry.alpha,geometry.height, geometry.angle);
				} else {
					DrawPolygon((Polygon) geometry.geometry, geometry.z, geometry.color,
							geometry.alpha,geometry.fill, geometry.isTextured, geometry.angle);
				}
			}
			else if (geometry.geometry.getGeometryType() == "MultiLineString") {
				DrawMultiLineString((MultiLineString) geometry.geometry, geometry.z, geometry.color,geometry.alpha);
			}

			else if (geometry.geometry.getGeometryType() == "LineString") {
				DrawLineString((LineString) geometry.geometry, geometry.z, 1.2f, geometry.color,geometry.alpha);
			}

			else if (geometry.geometry.getGeometryType() == "Point") {
				DrawPoint((Point) geometry.geometry, geometry.z, 10, 10, geometry.color,geometry.alpha);
			}
		}
	}

	public void DrawMultiPolygon(MultiPolygon polygons, float z, Color c,float alpha,
			boolean fill, Integer angle, float height) {

		numGeometries = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for (int i = 0; i < numGeometries; i++) {
			//FIXME: why setting the color?
			myGl.glColor4f((float) c.getRed() / 255,
					(float) c.getGreen() / 255, (float) c.getBlue() / 255,
					alpha);
			curPolygon = (Polygon) polygons.getGeometryN(i);

			if (height > 0) {
				DrawPolyhedre(curPolygon, z, c, alpha,height, angle);
			} else {
				DrawPolygon(curPolygon, z, c,alpha, fill, false, angle);
			}
		}
	}

	public void DrawPolygon(Polygon p, float z_layer, Color c, float alpha, boolean fill,
			boolean isTextured, Integer angle) {

		// FIXME: Angle rotation is not implemented yet
		
		
		//Set z_layer
		myGl.glTranslatef(0.0f, 0.0f, z_layer);

		if (fill == true) {
			myGl.glColor4f((float) c.getRed() / 255,
					(float) c.getGreen() / 255, (float) c.getBlue() / 255,
					alpha);
			
			//FIXME:This does not draw the whole. p.getInteriorRingN(n)
			numExtPoints = p.getExteriorRing().getNumPoints();

			// System.out.println("Draw Polygon with Tessellation :"+numExtPoints);

			myGl.glNormal3f(0.0f, 0.0f, 1.0f);

			if (useTessellation) {
				myGlu.gluTessBeginPolygon(tobj, null);
				myGlu.gluTessBeginContour(tobj);

				tempPolygon = new double[numExtPoints][3];
				// Convert vertices as a list of double for gluTessVertex
				for (int j = 0; j < numExtPoints; j++) {
					tempPolygon[j][0] = (float) (float) (p.getExteriorRing()
							.getPointN(j).getX());
					tempPolygon[j][1] = -(float) (p.getExteriorRing()
							.getPointN(j).getY());
					
					
					if(String.valueOf(p.getExteriorRing().getPointN(j).getCoordinate().z).equals("NaN") == true){
						tempPolygon[j][2] = 0.0f;
					}
					else{
						tempPolygon[j][2] = 0.0f+p.getExteriorRing().getPointN(j).getCoordinate().z;
					}
				}

				for (int j = 0; j < numExtPoints; j++) {
					myGlu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
				}

				myGlu.gluTessEndContour(tobj);
				myGlu.gluTessEndPolygon(tobj);
				myGl.glColor4f(0.0f, 0.0f, 0.0f, alpha);
				DrawPolygonContour(p, c, 0.0f);
			}
			// use JTS triangulation on simplified geometry (DouglasPeucker)
			//FIXME: not working with a z_layer value!!!!
			else {
				double sizeTol = Math.sqrt(p.getArea()) / 100.0;
				Geometry g2 = DouglasPeuckerSimplifier.simplify(p, sizeTol);
				if (g2 instanceof Polygon) {
					p = (Polygon) g2;
				} 			
				//Workaround to compute the z value of each triangle as triangulation
				// create new point during the triangulation that are set with z=NaN
				if (p.getNumPoints() > 4) {
					triangles = GeometryUtils.triangulation(p);
				
					GamaList<Geometry> segments = new GamaList<Geometry>();
					for (int i = 0; i < p.getNumPoints()- 1; i++) {
						Coordinate[] cs = new Coordinate[2];
						cs[0] = p.getCoordinates()[i];
						cs[1] = p.getCoordinates()[i+1];
						segments.add(GeometryUtils.factory.createLineString(cs));
					}
					for (IShape tri : triangles){
						for (int i = 0; i < tri.getInnerGeometry().getNumPoints(); i++) {
							Coordinate coord = tri.getInnerGeometry().getCoordinates()[i];
							if ((coord.z + "").equals("NaN")) {
								Point pt = GeometryUtils.factory.createPoint(coord);
								double distMin = Double.MAX_VALUE;
								Geometry closestSeg = null;
								for (Geometry seg : segments) {
									double dist = seg.distance(pt);
									if (dist < distMin) {
										distMin = dist;
										closestSeg = seg;
									}
								}
								Point pt1 = GeometryUtils.factory.createPoint(closestSeg.getCoordinates()[0]);
								Point pt2 = GeometryUtils.factory.createPoint(closestSeg.getCoordinates()[1]);
								
								double dist1 = pt.distance(pt1);
								double dist2 = pt.distance(pt2);
                                //FIXME: Work only for geometry
								coord.z = (1 - (dist1 / closestSeg.getLength())) * closestSeg.getCoordinates()[0].z + (1 - (dist2 / closestSeg.getLength())) * closestSeg.getCoordinates()[1].z;
								DrawShape(tri,false);	
							}		
						}
					}
				} else if (p.getNumPoints() == 4) {
					triangles = new GamaList<IShape>();
					triangles.add(new GamaShape(p));
				}
				for (IShape tri : triangles){
					DrawShape(tri,false);
					
				}
				myGl.glColor4f(0.0f, 0.0f, 0.0f, alpha);
				
				DrawPolygonContour(p, c, 0.0f);
			}
	
			

		}
		// fill = false. Draw only the contour of the polygon.
		else {
			myGl.glColor4f((float) c.getRed() / 255,
					(float) c.getGreen() / 255, (float) c.getBlue() / 255,
					alpha);
			DrawPolygonContour(p, c, 0.0f);
		}

		// FIXME: Need to check that the polygon is a quad
		if (isTextured) {
			myGl.glEnable(GL.GL_TEXTURE_2D);
			// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL context's state.
			myGLRender.textures[2].enable();
			// Binds this texture to the current GL context.
			myGLRender.textures[2].bind();

			if (angle != 0) {
				myGl.glTranslatef((float) p.getCentroid().getX(), -(float) p
						.getCentroid().getY(), 0.0f);
				// FIXME:Check counterwise or not, and do we rotate around the
				// center or around a point.
				myGl.glRotatef(-angle, 0.0f, 0.0f, 1.0f);
				myGl.glTranslatef(-(float) p.getCentroid().getX(), +(float) p
						.getCentroid().getY(), 0.0f);
				myGl.glBegin(GL_QUADS);

				// Front Face
				myGl.glTexCoord2f(myGLRender.textureLeft,
						myGLRender.textureBottom);
				myGl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), -p
						.getExteriorRing().getPointN(0).getY(), 0.0f); 
				
				myGl.glTexCoord2f(myGLRender.textureRight,
						myGLRender.textureBottom);
				myGl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), -p
						.getExteriorRing().getPointN(1).getY(), 0.0f); 
				
				myGl.glTexCoord2f(myGLRender.textureRight,
						myGLRender.textureTop);
				myGl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), -p
						.getExteriorRing().getPointN(2).getY(), 0.0f); 
				
				myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureTop);
				myGl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), -p
						.getExteriorRing().getPointN(3).getY(), 0.0f); 

				myGl.glEnd();
				myGl.glTranslatef((float) p.getCentroid().getX(), -(float) p
						.getCentroid().getY(), 0.0f);
				myGl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
				myGl.glTranslatef(-(float) p.getCentroid().getX(), +(float) p
						.getCentroid().getY(), 0.0f);
			} else {
				myGl.glBegin(GL_QUADS);

				// Front Face
				myGl.glTexCoord2f(myGLRender.textureLeft,
						myGLRender.textureBottom);
				myGl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), -p
						.getExteriorRing().getPointN(0).getY(), 0.0f); 
				
				myGl.glTexCoord2f(myGLRender.textureRight,
						myGLRender.textureBottom);
				myGl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), -p
						.getExteriorRing().getPointN(1).getY(), 0.0f); 
				
				myGl.glTexCoord2f(myGLRender.textureRight,
						myGLRender.textureTop);
				myGl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), -p
						.getExteriorRing().getPointN(2).getY(), 0.0f); 
				
				myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureTop);
				myGl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), -p
						.getExteriorRing().getPointN(3).getY(), 0.0f); 
				myGl.glEnd();
			}

			myGl.glDisable(GL.GL_TEXTURE_2D);

		}
		
		myGl.glTranslatef(0.0f, 0.0f, -z_layer);

	}

	public void DrawPolygonContour(Polygon p, Color c, float z) {
		// Draw contour
		myGl.glBegin(GL.GL_LINES);
		numExtPoints = p.getExteriorRing().getNumPoints();
		if (p.isEmpty())
			return;
		//If polygon has no z value
		if(String.valueOf(p.getExteriorRing().getPointN(0).getCoordinate().z).equals("NaN") == true){
			for (int j = 0; j < numExtPoints - 1; j++) {
				myGl.glLineWidth(1.0f);
				myGl.glVertex3f(
						(float) ((p.getExteriorRing().getPointN(j).getX())),
						-(float) ((p.getExteriorRing().getPointN(j).getY())), z);
				myGl.glVertex3f(
						(float) ((p.getExteriorRing().getPointN(j + 1).getX())),
						-(float) ((p.getExteriorRing().getPointN(j + 1).getY())), z);
			}
		}
		
		//If the polygon has a z value.
		else{
			for (int j = 0; j < numExtPoints - 1; j++) {
				myGl.glLineWidth(1.0f);
				myGl.glVertex3f(
						(float) ((p.getExteriorRing().getPointN(j).getX())),
						-(float) ((p.getExteriorRing().getPointN(j).getY())),
						z+(float)p.getExteriorRing().getPointN(j).getCoordinate().z);
				myGl.glVertex3f(
						(float) ((p.getExteriorRing().getPointN(j + 1).getX())),
						-(float) ((p.getExteriorRing().getPointN(j + 1).getY())), 
						z+(float) p.getExteriorRing().getPointN(j+1).getCoordinate().z);
			}
		}
		
		
		myGl.glEnd();
	}

	public void DrawPolyhedre(Polygon p, float z, Color c,float alpha, float height,
			Integer angle) {

		DrawPolygon(p, z, c, alpha,true, false, angle);
		DrawPolygon(p, z + height, c,alpha, true, false, angle);
		// FIXME : Will be wrong if angle =!0
		DrawFaces(p, c, alpha,z + height);

	}
	
	/**
	 * Draw a tesselated circle polygon and its contour as a line.
	 * @param x
	 * @param y
	 * @param z
	 * @param numPoints: Number of point of the circle.
	 * @param radius: Radius of the circle.
	 */
	public void DrawCircle(float x, float y, float z, int numPoints,
			float radius) {

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);

		float angle;
		tempPolygon = new double[100][3];
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
		myGl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
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

	/**
	 * Given a polygon this will draw the different faces of the 3D polygon.
	 * @param p:Base polygon
	 * @param c: color
	 * @param height: height of the polygon
	 */
	public void DrawFaces(Polygon p, Color c, float alpha, float height) {
		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);

		int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();

		for (int j = 0; j < curPolyGonNumPoints; j++) {

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
			for (int i = 0; i < 4; i++) {
				vertices[i] = new Vertex();
			}
			// FIXME; change double to float in Vertex
			vertices[0].x = (float) p.getExteriorRing().getPointN(j).getX();
			vertices[0].y = -(float) p.getExteriorRing().getPointN(j).getY();
			vertices[0].z = height;

			vertices[1].x = (float) p.getExteriorRing().getPointN(k).getX();
			vertices[1].y = -(float) p.getExteriorRing().getPointN(k).getY();
			vertices[1].z = height;

			vertices[2].x = (float) p.getExteriorRing().getPointN(k).getX();
			vertices[2].y = -(float) p.getExteriorRing().getPointN(k).getY();
			vertices[2].z = 0;

			vertices[3].x = (float) p.getExteriorRing().getPointN(j).getX();
			vertices[3].y = -(float) p.getExteriorRing().getPointN(j).getY();
			vertices[3].z = 0;

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
			// myGl.glNormal3f((float)normal.x, (float)normal.y,
			// (float)normal.z);
			myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
			myGl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
			myGl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
			myGl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);

			myGl.glEnd();
		}

	}

	public void DrawMultiLineString(MultiLineString lines, float z, Color c,float alpha) {

		// get the number of line in the multiline.
		numGeometries = lines.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for (int i = 0; i < numGeometries; i++) {

			myGl.glColor4f((float) c.getRed() / 255,
					(float) c.getGreen() / 255, (float) c.getBlue() / 255,
					alpha);

			LineString l = (LineString) lines.getGeometryN(i);
			int numPoints = l.getNumPoints();

			// myGl.glLineWidth(size);
			myGl.glBegin(GL.GL_LINES);
			for (int j = 0; j < numPoints - 1; j++) {
				myGl.glVertex3f((float) ((l.getPointN(j).getX())),
						-(float) ((l.getPointN(j).getY())), z);
				myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
						-(float) ((l.getPointN(j + 1).getY())), z);
			}
			myGl.glEnd();
		}
	}

	public void DrawLineString(LineString line, float z, float size, Color c, float alpha) {

		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);
		int numPoints = line.getNumPoints();
		myGl.glLineWidth(size);
		myGl.glBegin(GL.GL_LINES);
		for (int j = 0; j < numPoints - 1; j++) {
			myGl.glVertex3f((float) ((line.getPointN(j).getX())),
					-(float) ((line.getPointN(j).getY())), z);
			myGl.glVertex3f((float) ((line.getPointN(j + 1).getX())),
					-(float) ((line.getPointN(j + 1).getY())), z);
		}
		myGl.glEnd();

	}

	public void DrawPoint(Point point, float z, int numPoints, float radius,
			Color c,float alpha) {

		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);

		float angle;
		double tempPolygon[][] = new double[100][3];
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);

			tempPolygon[k][0] = (float) (point.getCoordinate().x + (Math
					.cos(angle)) * radius);
			tempPolygon[k][1] = -(float) (point.getCoordinate().y + (Math
					.sin(angle)) * radius);
			tempPolygon[k][2] = z;
		}

		for (int k = 0; k < numPoints; k++) {
			myGlu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		myGl.glColor4f(0.0f, 0.0f, 0.0f, alpha);
		myGl.glLineWidth(1.1f);
		myGl.glBegin(GL.GL_LINES);
		float xBegin, xEnd, yBegin, yEnd;
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);
			xBegin = (float) (point.getCoordinate().x + (Math.cos(angle))
					* radius);
			yBegin = -(float) (point.getCoordinate().y + (Math.sin(angle))
					* radius);
			angle = (float) ((k + 1) * 2 * Math.PI / numPoints);
			xEnd = (float) (point.getCoordinate().x + (Math.cos(angle))
					* radius);
			yEnd = -(float) (point.getCoordinate().y + (Math.sin(angle))
					* radius);
			myGl.glVertex3f(xBegin, yBegin, z);
			myGl.glVertex3f(xEnd, yEnd, z);
		}
		myGl.glEnd();

	}
	
	public void DrawShape(IShape shape,boolean showTriangulation) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();
		

		if(showTriangulation){
			
			if(String.valueOf(polygon.getExteriorRing().getPointN(0).getCoordinate().z).equals("NaN") == true){			
			myGl.glBegin(GL.GL_LINES); // draw using triangles
			 myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),
			 -polygon.getExteriorRing().getPointN(0).getY(), 0.0f);
			 myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),
			 -polygon.getExteriorRing().getPointN(1).getY(), 0.0f);
			 
			 myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),
			 -polygon.getExteriorRing().getPointN(1).getY(), 0.0f);
			 myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),
			 -polygon.getExteriorRing().getPointN(2).getY(), 0.0f);
			  
			 myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),
			 -polygon.getExteriorRing().getPointN(2).getY(), 0.0f);
			 myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),
			 -polygon.getExteriorRing().getPointN(0).getY(), 0.0f); 
			 myGl.glEnd();
			}
			else{
				myGl.glBegin(GL.GL_LINES); // draw using triangles
				 myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),
				 -polygon.getExteriorRing().getPointN(0).getY(), 
				 polygon.getExteriorRing().getPointN(0).getCoordinate().z);
				 myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),
				 -polygon.getExteriorRing().getPointN(1).getY(), 
				 polygon.getExteriorRing().getPointN(0).getCoordinate().z);
				 
				 myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(),
				 -polygon.getExteriorRing().getPointN(1).getY(), 
				 polygon.getExteriorRing().getPointN(1).getCoordinate().z);
				 myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),
				 -polygon.getExteriorRing().getPointN(2).getY(), 
				 polygon.getExteriorRing().getPointN(2).getCoordinate().z);
				  
				 myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(),
				 -polygon.getExteriorRing().getPointN(2).getY(), 
				 polygon.getExteriorRing().getPointN(2).getCoordinate().z);
				 myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(),
				 -polygon.getExteriorRing().getPointN(0).getY(), 
				 polygon.getExteriorRing().getPointN(0).getCoordinate().z); 
				 myGl.glEnd();
				
			}
		}
		else{
			if(String.valueOf(polygon.getExteriorRing().getPointN(0).getCoordinate().z).equals("NaN") == true){			
				
			myGl.glBegin(GL_TRIANGLES); // draw using triangles
			myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), -polygon
					.getExteriorRing().getPointN(0).getY(), 0.0f);
			myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), -polygon
					.getExteriorRing().getPointN(1).getY(), 0.0f);
			myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), -polygon
					.getExteriorRing().getPointN(2).getY(), 0.0f);
			myGl.glEnd();
			}
			else{
				myGl.glBegin(GL_TRIANGLES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), -polygon
						.getExteriorRing().getPointN(0).getY(), 
						polygon.getExteriorRing().getPointN(0).getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), -polygon
						.getExteriorRing().getPointN(1).getY(), 
						polygon.getExteriorRing().getPointN(1).getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), -polygon
						.getExteriorRing().getPointN(2).getY(), 
						polygon.getExteriorRing().getPointN(2).getCoordinate().z);
				myGl.glEnd();	
			}
			
		}
	}

	/*
	 * Return 9 array with the 3 vertex coordinates of the traingle
	 */
	public float[] GetTriangleVertices(IShape shape) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();
		float[] vertices = new float[9];
		for (int i = 0; i < 3; i++) {
			vertices[i * 3] = (float) polygon.getExteriorRing().getPointN(0)
					.getX();
			vertices[i * 3 + 1] = (float) -polygon.getExteriorRing()
					.getPointN(0).getY();
			vertices[i * 3 + 2] = 0.0f;
		}
		return vertices;
	}
}
