package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COMPILE;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import com.sun.opengl.util.GLUT;

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
	//Use to draw Bitmap String
    private final GLUT glut;
	
	// need to have the GLRenderer to enable texture mapping.
    public JOGLAWTGLRenderer myGLRender;

	// FIXME: Is it better to declare an objet polygon here than in
	// DrawMultiPolygon??
	Polygon curPolygon;
	int numExtPoints;
	int numGeometries;
	
	double tempPolygon[][];
	double temp[];

	// FIXME: Create to avoid creating int i,j each framerate.
	int i, j;

	float alpha = 1.0f;

	// Display List Id
	private int listId;
	private int firstList;

	public MyGraphics(final GL gl, final GLU glu,final JOGLAWTGLRenderer gLRender ) {

		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		tessCallback = new TessellCallBack(myGl, myGlu);
		tobj = glu.gluNewTess();
		
		glut = new GLUT();
		
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback)

	}


	public void DrawCircle(float x, float y, float z, int numPoints,
			float radius) {

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);

		float angle;
		tempPolygon	 = new double[100][3];
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
		myGl.glColor4f(0.0f, 0.0f, 0.0f, alpha);
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


	public void Draw3DQuads(Polygon p, Color c, float z_offset) {
		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);

		int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();
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
			// FIXME; change double to float in Vertex
			vertices[0].x = (float) p.getExteriorRing().getPointN(j).getX();
			vertices[0].y = -(float) p.getExteriorRing().getPointN(j).getY();
			vertices[0].z = z_offset;

			vertices[1].x = (float) p.getExteriorRing().getPointN(k).getX();
			vertices[1].y = -(float) p.getExteriorRing().getPointN(k).getY();
			vertices[1].z = z_offset;

			vertices[2].x = (float) p.getExteriorRing().getPointN(k).getX();
			vertices[2].y = -(float) p.getExteriorRing().getPointN(k).getY();
			vertices[2].z = 0;

			vertices[3].x = (float) p.getExteriorRing().getPointN(j).getX();
			vertices[3].y = -(float) p.getExteriorRing().getPointN(j).getY();
			vertices[3].z = 0;

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
			// myGl.glNormal3f((float)normal.x, (float)normal.y,
			// (float)normal.z);
			myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
			myGl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
			myGl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
			myGl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);

			myGl.glEnd();
		}

	}

	public void DrawJTSGeometry(MyJTSGeometry geometry) {

		//System.out.println("DrawJTSGraphics:" + geometry.geometry.getGeometryType());
		
		
		for (i = 0; i < geometry.geometry.getNumGeometries(); i++) {

			if (geometry.geometry.getGeometryType() == "MultiPolygon") {
				MultiPolygon polygons = (MultiPolygon) geometry.geometry;
				DrawMultiPolygon(polygons,geometry.z,geometry.color,geometry.fill,geometry.angle,geometry.elevation);
			}

			else if (geometry.geometry.getGeometryType() == "Polygon") {
				Polygon polygon = (Polygon) geometry.geometry;
				
				if(geometry.elevation>0){
					Draw3DPolygon(curPolygon,geometry.z,geometry.color, geometry.elevation,geometry.angle);
				}
				else{
					DrawPolygon(polygon, geometry.z, geometry.color,geometry.fill,geometry.isTextured,geometry.angle);	
				}
				
			}

			else if (geometry.geometry.getGeometryType() == "MultiLineString") {
				MultiLineString lines = (MultiLineString) geometry.geometry;
				DrawMultiLineString(lines, geometry.z,geometry.color);
			}

			else if (geometry.geometry.getGeometryType() == "LineString") {
				LineString line = (LineString) geometry.geometry;
				DrawLineString(line,geometry.z, 1.2f, geometry.color);
			}

			else if (geometry.geometry.getGeometryType() == "Point") {
				Point point = (Point) geometry.geometry;
				DrawPoint(point,geometry.z, 10, 10, geometry.color);
			}
		}
	}



	public void DrawMultiPolygon(MultiPolygon polygons, float z,Color c,boolean fill, Integer angle,float elevation) {

		numGeometries = polygons.getNumGeometries();
		// System.out.println("Draw MultiPolygon:"+numGeometries);

		// for each polygon of a multipolygon, get each point coordinates.
		for (i = 0; i < numGeometries; i++) {
			myGl.glColor4f((float) c.getRed() / 255,
					(float) c.getGreen() / 255, (float) c.getBlue() / 255,
					alpha);
			curPolygon = (Polygon) polygons.getGeometryN(i);
				
			if(elevation>0){
				Draw3DPolygon(curPolygon,z,c, elevation,angle);
			}
			else{
				DrawPolygon(curPolygon, z, c ,fill,false,angle);	
			}			
		}
	}



	public void DrawPolygon(Polygon p,float z, Color c,boolean fill,boolean isTextured, Integer angle) {
	
		//FIXME: Angle rotation is not implemented yet
		
		if(fill == true){
		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);
		numExtPoints = p.getExteriorRing().getNumPoints();
		//System.out.println("Draw Polygon with Tessellation :"+numExtPoints);

		myGl.glNormal3f(0.0f, 0.0f, 1.0f);
		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);

		tempPolygon = new double[numExtPoints][3];
		//Convert vertices as a list of double for gluTessVertex
		for (j = 0; j < numExtPoints; j++) {
			tempPolygon[j][0] = (float) (float) (p.getExteriorRing().getPointN(
					j).getX());
			tempPolygon[j][1] = -(float) (p.getExteriorRing().getPointN(j)
					.getY());
			tempPolygon[j][2] = z;
		}

		for (j = 0; j < numExtPoints; j++) {	
			myGlu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}
		

		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);
		

		myGl.glColor4f(0.0f, 0.0f, 0.0f, alpha);
        DrawPolygonContour(p, c, z);

		}
		//fill = false. Draw only the contour of the polygon.
		else{
			myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
					(float) c.getBlue() / 255, alpha);	
			DrawPolygonContour(p, c, z);
		}
		

		//FIXME: Need to check that the polygon is a quad
		if(isTextured){
			myGl.glEnable(GL.GL_TEXTURE_2D);
			// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL
			// context's state.
			myGLRender.textures[2].enable();
			// Binds this texture to the current GL context.
			myGLRender.textures[2].bind();
			
			if(angle!=0){
				myGl.glTranslatef ((float)p.getCentroid().getX(),-(float)p.getCentroid().getY(),0.0f); 
				//FIXME:Check counterwise or not, and do we rotate around the center or around a point.
				myGl.glRotatef(-angle,0.0f,0.0f,1.0f);
				myGl.glTranslatef (-(float)p.getCentroid().getX(),+(float)p.getCentroid().getY(),0.0f);
				myGl.glBegin(GL_QUADS);
	
				// Front Face
				myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureBottom);
				myGl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), -p.getExteriorRing().getPointN(0).getY(), z); // bottom-left of the texture and
														// quad
				myGl.glTexCoord2f(myGLRender.textureRight, myGLRender.textureBottom);
				myGl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), -p.getExteriorRing().getPointN(1).getY(), z); // bottom-right of the texture and
														// quad
				myGl.glTexCoord2f(myGLRender.textureRight, myGLRender.textureTop);
				myGl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), -p.getExteriorRing().getPointN(2).getY(), z); // top-right of the texture and quad
				myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureTop);
				myGl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), -p.getExteriorRing().getPointN(3).getY(), z); // top-left of the texture and quad
				
				myGl.glEnd();
				myGl.glTranslatef ((float)p.getCentroid().getX(),-(float)p.getCentroid().getY(),0.0f); 
				myGl.glRotatef(angle,0.0f,0.0f,1.0f);
				myGl.glTranslatef (-(float)p.getCentroid().getX(),+(float)p.getCentroid().getY(),0.0f);
			}
			else{
				myGl.glBegin(GL_QUADS);

				// Front Face
				myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureBottom);
				myGl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), -p.getExteriorRing().getPointN(0).getY(), z); // bottom-left of the texture and
														// quad
				myGl.glTexCoord2f(myGLRender.textureRight, myGLRender.textureBottom);
				myGl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), -p.getExteriorRing().getPointN(1).getY(), z); // bottom-right of the texture and
														// quad
				myGl.glTexCoord2f(myGLRender.textureRight, myGLRender.textureTop);
				myGl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), -p.getExteriorRing().getPointN(2).getY(), z); // top-right of the texture and quad
				myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureTop);
				myGl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), -p.getExteriorRing().getPointN(3).getY(), z); // top-left of the texture and quad
				
				myGl.glEnd();
			}
				
		    myGl.glDisable(GL.GL_TEXTURE_2D);
			
		}
		

		
	}



	public void DrawPolygonContour(Polygon p, Color c, float z) {
		// Draw contour
		myGl.glBegin(GL.GL_LINES);
		numExtPoints = p.getExteriorRing().getNumPoints();
		for (j = 0; j < numExtPoints - 1; j++) {
			myGl.glLineWidth(1.0f);
			myGl.glVertex3f(
					(float) ((p.getExteriorRing().getPointN(j).getX())),
					-(float) ((p.getExteriorRing().getPointN(j).getY())), z);
			myGl.glVertex3f(
					(float) ((p.getExteriorRing().getPointN(j + 1).getX())),
					-(float) ((p.getExteriorRing().getPointN(j + 1).getY())), z);
		}
		myGl.glEnd();
	}


	public void Draw3DPolygon(Polygon p, float z,Color c, float z_offset,Integer angle) {

		DrawPolygon(p, z, c,true,false,angle);
		DrawPolygon(p, z+z_offset, c,true,false,angle);
		//FIXME : Will be wrong if angle =!0
		Draw3DQuads(p, c, z+z_offset);

	}

	public void DrawMultiLineString(MultiLineString lines, float z,Color c) {

		// get the number of line in the multiline.
		numGeometries = lines.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for (i = 0; i < numGeometries; i++) {

			myGl.glColor4f((float) c.getRed() / 255,
					(float) c.getGreen() / 255, (float) c.getBlue() / 255,
					alpha);

			LineString l = (LineString) lines.getGeometryN(i);
			int numPoints = l.getNumPoints();

			// myGl.glLineWidth(size);
			myGl.glBegin(GL.GL_LINES);
			for (j = 0; j < numPoints - 1; j++) {
				myGl.glVertex3f((float) ((l.getPointN(j).getX())),
						-(float) ((l.getPointN(j).getY())), z);
				myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
						-(float) ((l.getPointN(j + 1).getY())), z);
			}
			myGl.glEnd();
		}
	}

	public void DrawLineString(LineString line, float z, float size, Color c) {

		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);
		int numPoints = line.getNumPoints();
		myGl.glLineWidth(size);
		myGl.glBegin(GL.GL_LINES);
		for (j = 0; j < numPoints - 1; j++) {
			myGl.glVertex3f((float) ((line.getPointN(j).getX())),
					-(float) ((line.getPointN(j).getY())), z);
			myGl.glVertex3f((float) ((line.getPointN(j + 1).getX())),
					-(float) ((line.getPointN(j + 1).getY())), z);
		}
		myGl.glEnd();

	}

	public void DrawPoint(Point point, float z, int numPoints, float radius, Color c) {

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

	/**
	 * Create the display list for each JTS geometries (one list per geometry)
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildDisplayLists(ArrayList<MyJTSGeometry> myJTSGeometries) {

		// Build n lists, and returns handle for the first list
		firstList = myGl.glGenLists(myJTSGeometries.size());
		listId = firstList;
		Iterator<MyJTSGeometry> it = myJTSGeometries.iterator();
		while (it.hasNext()) {
			MyJTSGeometry curGeometry = it.next();			
			myGl.glNewList(listId, GL_COMPILE);
			DrawJTSGeometry(curGeometry);
			myGl.glEndList();
			listId = listId + 1;;
		}
	}
	
	public void DrawDisplayList(int nbDisplayList) {
		for (int i = 0; i <= nbDisplayList; i++) {
			myGl.glColor3f((float)Math.random(), (float)Math.random(), (float)Math.random());
			myGl.glCallList(i);
		}	
	}
	
	public void DeleteDisplayLists(int nbDisplayList){
		myGl.glDeleteLists(firstList,nbDisplayList);
	}
	
	
	/**
	 * Create the display list for each JTS geometries (one list per geometry)
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildImageDisplayLists(ArrayList<MyImage> myImages) {

		// Build n lists, and returns handle for the first list
		firstList = myGl.glGenLists(myImages.size());
		listId = firstList;
		Iterator<MyImage> it = myImages.iterator();
		int id = 0;
		while (it.hasNext()) {
			MyImage curImage = it.next();			
			myGl.glNewList(listId, GL_COMPILE);
			myGLRender.DrawTexture(curImage);
			myGl.glEndList();
			listId = listId + 1;
			id++;
		}
	}
	
	public void DrawImageDisplayList(int nbDisplayList) {
		for (int i = 0; i <= nbDisplayList; i++) {
			myGl.glCallList(i);
		}	
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
	
	public void DrawString(final String string, final float x, final float y, final float z) {

		// Need to disable blending when drawing glutBitmapString;
		myGl.glDisable(GL_BLEND);
		myGl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		myGl.glRasterPos3f(x, y, z);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, string);
		// myGl.glEnable(GL_BLEND);


	}

}
