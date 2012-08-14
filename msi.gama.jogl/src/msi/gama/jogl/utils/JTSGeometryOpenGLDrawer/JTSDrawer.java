package msi.gama.jogl.utils.JTSGeometryOpenGLDrawer;

import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.Color;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLUtessellator;


import msi.gama.common.util.GeometryUtils;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Vertex;
import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;
import msi.gama.util.IList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;


public class JTSDrawer {
	
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
	boolean useTessellation = false	;
	
	//Use for JTS triangulation
	IList<IShape> triangles;
	Iterator<IShape> it;
	
	public JTSDrawer (final GL gl, final GLU glu,
			final JOGLAWTGLRenderer gLRender){
		
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
		
	
	public void DrawMultiPolygon(MyJTSGeometry geometry) {

		MultiPolygon polygons = (MultiPolygon) geometry.geometry;
		numGeometries = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for (int i = 0; i < numGeometries; i++) {
			//FIXME: why setting the color?
			myGl.glColor4f((float) geometry.color.getRed() / 255,
					(float) geometry.color.getGreen() / 255, (float) geometry.color.getBlue() / 255,
					geometry.alpha);
			curPolygon = (Polygon) polygons.getGeometryN(i);

			if (geometry.height > 0) {
				DrawPolyhedre(geometry,false);
			} else {
				DrawPolygon(geometry, geometry.z,true);
			}
		}
	}
		
	public void DrawPolygon(MyJTSGeometry geometry, float z_layer,boolean drawPolygonContour) {

		// FIXME: Angle rotation is not implemented yet

		//Set z_layer
		myGl.glTranslatef(0.0f, 0.0f, z_layer);
		Polygon p = (Polygon) geometry.geometry;

		if (geometry.fill == true) {
			myGl.glColor4f((float) geometry.color.getRed() / 255,
					(float) geometry.color.getGreen() / 255, (float) geometry.color.getBlue() / 255,
					geometry.alpha);
			
			//FIXME:This does not draw the whole. p.getInteriorRingN(n)
			numExtPoints = p.getExteriorRing().getNumPoints();

			// System.out.println("Draw Polygon with Tessellation :"+numExtPoints);

			myGl.glNormal3f(0.0f, 0.0f, 1.0f);

			if (useTessellation) {
				myGlu.gluTessBeginPolygon(tobj, null);
				myGlu.gluTessBeginContour(tobj);

				tempPolygon = new double[numExtPoints][3];
				
				if(p.isSimple()){
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
				myGl.glColor4f(0.0f, 0.0f, 0.0f, geometry.alpha);
				if(drawPolygonContour ==true){
					DrawPolygonContour(p, geometry.color, 0.0f);
				}
				}
				else{
					System.out.println("the geometry is not simple!!!!");
				}
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
				myGl.glColor4f(0.0f, 0.0f, 0.0f, geometry.alpha);
				if(drawPolygonContour ==true){
				DrawPolygonContour(p, geometry.color, 0.0f);
				}
			}
		}
		// fill = false. Draw only the contour of the polygon.
		else {
			myGl.glColor4f((float) geometry.color.getRed() / 255,
					(float) geometry.color.getGreen() / 255, (float) geometry.color.getBlue() / 255,
					geometry.alpha);
			DrawPolygonContour(p, geometry.color, 0.0f);
		}

		// FIXME: Need to check that the polygon is a quad
		if (geometry.isTextured) {
			myGl.glEnable(GL.GL_TEXTURE_2D);
			// Enables this texture's target (e.g., GL_TEXTURE_2D) in the current GL context's state.
			myGLRender.textures[2].enable();
			// Binds this texture to the current GL context.
			myGLRender.textures[2].bind();

			if (geometry.angle != 0) {
				myGl.glTranslatef((float) p.getCentroid().getX(), -(float) p
						.getCentroid().getY(), 0.0f);
				// FIXME:Check counterwise or not, and do we rotate around the
				// center or around a point.
				myGl.glRotatef(-geometry.angle, 0.0f, 0.0f, 1.0f);
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
				myGl.glRotatef(geometry.angle, 0.0f, 0.0f, 1.0f);
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

		
	public void DrawPolyhedre(MyJTSGeometry geometry,boolean drawPolygonContour) {
  
		Polygon p = (Polygon) geometry.geometry;
		DrawPolygon(geometry, geometry.z,drawPolygonContour);
		DrawPolygon(geometry, geometry.z + geometry.height,drawPolygonContour);
		// FIXME : Will be wrong if angle =!0
		DrawFaces(p, geometry.color, geometry.alpha,geometry.z + geometry.height,drawPolygonContour,false);

	}
	
	
	/**
	 * Given a polygon this will draw the different faces of the 3D polygon.
	 * @param p:Base polygon
	 * @param c: color
	 * @param height: height of the polygon
	 */
	public void DrawFaces(Polygon p, Color c, float alpha, float height,boolean drawPolygonContour , boolean drawNormal) {
		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);
		float elevation=0.0f;
		
		//FIXME: Only works if the base is in the XY axes.
		if(String.valueOf(p.getExteriorRing().getPointN(0).getCoordinate().z).equals("NaN") == false){
			elevation = (float) (p.getExteriorRing().getPointN(0).getCoordinate().z);
		}


		int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();

		for (int j = 0; j < curPolyGonNumPoints; j++) {

			int k = (j + 1) % curPolyGonNumPoints;

			//Build the 4 vertices of the face.
			Vertex[] vertices = new Vertex[4];
			for (int i = 0; i < 4; i++) {
				vertices[i] = new Vertex();
			}
			// FIXME; change double to float in Vertex
			vertices[0].x = (float) p.getExteriorRing().getPointN(j).getX();
			vertices[0].y = -(float) p.getExteriorRing().getPointN(j).getY();
			vertices[0].z = elevation+height;

			vertices[1].x = (float) p.getExteriorRing().getPointN(k).getX();
			vertices[1].y = -(float) p.getExteriorRing().getPointN(k).getY();
			vertices[1].z = elevation+height;

			vertices[2].x = (float) p.getExteriorRing().getPointN(k).getX();
			vertices[2].y = -(float) p.getExteriorRing().getPointN(k).getY();
			vertices[2].z = elevation;

			vertices[3].x = (float) p.getExteriorRing().getPointN(j).getX();
			vertices[3].y = -(float) p.getExteriorRing().getPointN(j).getY();
			vertices[3].z = elevation;

			// Compute the normal of the quad (for the moment only give 3 point of the quad, to be enhance for non plan polygon)
			float [] normal = CalculateNormal(vertices[2], vertices[1], vertices[0]);
			
			myGl.glBegin(GL.GL_QUADS);
			
			myGl.glNormal3fv(normal,0);
			
			myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
			myGl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
			myGl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
			myGl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);
			
			myGl.glEnd();
			

            if(drawPolygonContour == true){
            	myGl.glColor4f(0.0f, 0.0f,0.0f,alpha);
            	
            	myGl.glBegin(GL.GL_LINES);
            	
            	myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
    			myGl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
    			
    			myGl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
    			myGl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
    			
    			myGl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
    			myGl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);
    			
    			myGl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);
    			myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);

            	myGl.glEnd();
            	
            	myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
        				(float) c.getBlue() / 255, alpha);
			}
            
            if (drawNormal == true){
				myGl.glBegin(GL.GL_LINES);
				myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
				myGl.glVertex3f(vertices[0].x+normal[0]*2, vertices[0].y+normal[1]*2, vertices[0].z+normal[2]*2);		
				myGl.glEnd();
			}
		}

	}
	
	
	public void DrawMultiLineString(MyJTSGeometry geometry) {

		MultiLineString lines = (MultiLineString) geometry.geometry;
		// get the number of line in the multiline.
		numGeometries = lines.getNumGeometries();
		
		//FIXME: Why setting the color here?
		myGl.glColor4f((float) geometry.color.getRed() / 255,
				(float) geometry.color.getGreen() / 255, (float) geometry.color.getBlue() / 255,
				geometry.alpha);
		
		// for each line of a multiline, get each point coordinates.
		for (int i = 0; i < numGeometries; i++) {

			
			LineString l = (LineString) lines.getGeometryN(i);
			if (geometry.height > 0) {
				DrawPlan(geometry,true);
			} else {
				DrawLineString(geometry, 1.2f);
			}			
		}
	}
	
	public void DrawLineString(MyJTSGeometry geometry,float size) {

		myGl.glColor4f((float) geometry.color.getRed() / 255, (float) geometry.color.getGreen() / 255,
				(float) geometry.color.getBlue() / 255, geometry.alpha);
		LineString line = (LineString) geometry.geometry;
		int numPoints = line.getNumPoints();
		myGl.glLineWidth(size);
		
		//Add z value			
		if(String.valueOf(line.getCoordinate().z).equals("NaN") == false){
			geometry.z =  geometry.z + (float) line.getCoordinate().z;
		}
		
		myGl.glBegin(GL.GL_LINES);
		for (int j = 0; j < numPoints - 1; j++) {
			myGl.glVertex3f((float) ((line.getPointN(j).getX())),
					-(float) ((line.getPointN(j).getY())), geometry.z);
			myGl.glVertex3f((float) ((line.getPointN(j + 1).getX())),
					-(float) ((line.getPointN(j + 1).getY())), geometry.z);
		}
		myGl.glEnd();

	}
	

	
	public void DrawPlan(MyJTSGeometry geometry,boolean drawPolygonContour) {

		DrawLineString(geometry, 1.2f);
		DrawLineString(geometry,1.2f);
		
		
		//Draw a quad 
		myGl.glColor4f((float) geometry.color.getRed() / 255, (float) geometry.color.getGreen() / 255,
				(float) geometry.color.getBlue() / 255, geometry.alpha);
		LineString l = (LineString) geometry.geometry;
		int numPoints = l.getNumPoints();
		
		//Add z value			
		if(String.valueOf(l.getCoordinate().z).equals("NaN") == false){
			geometry.z =  geometry.z + (float) l.getCoordinate().z;
		}
		
		for (int j = 0; j < numPoints - 1; j++) {
		myGl.glBegin(GL.GL_QUADS);
		myGl.glVertex3f((float) ((l.getPointN(j).getX())),
				-(float) ((l.getPointN(j).getY())), geometry.z);
		myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
				-(float) ((l.getPointN(j + 1).getY())), geometry.z);
		
		myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
				-(float) ((l.getPointN(j + 1).getY())), geometry.z+geometry.height);
		
		myGl.glVertex3f((float) ((l.getPointN(j).getX())),
				-(float) ((l.getPointN(j).getY())), geometry.z+geometry.height);
		
		myGl.glEnd();
		}
		
		if(drawPolygonContour == true){
			myGl.glColor4f(0.0f, 0.0f,0.0f,geometry.alpha);
			for (int j = 0; j < numPoints - 1; j++) {
				myGl.glBegin(GL.GL_LINES);
				myGl.glVertex3f((float) ((l.getPointN(j).getX())),
						-(float) ((l.getPointN(j).getY())), geometry.z);
				myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
						-(float) ((l.getPointN(j + 1).getY())), geometry.z);
				
				myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
						-(float) ((l.getPointN(j + 1).getY())), geometry.z);
				myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
						-(float) ((l.getPointN(j + 1).getY())), geometry.z+geometry.height);
				
				myGl.glVertex3f((float) ((l.getPointN(j + 1).getX())),
						-(float) ((l.getPointN(j + 1).getY())), geometry.z+geometry.height);
				myGl.glVertex3f((float) ((l.getPointN(j).getX())),
						-(float) ((l.getPointN(j).getY())), geometry.z+geometry.height);
				
				myGl.glVertex3f((float) ((l.getPointN(j).getX())),
						-(float) ((l.getPointN(j).getY())), geometry.z+geometry.height);
				myGl.glVertex3f((float) ((l.getPointN(j).getX())),
						-(float) ((l.getPointN(j).getY())), geometry.z);
				
				myGl.glEnd();
				}
			myGl.glColor4f((float) geometry.color.getRed() / 255, (float) geometry.color.getGreen() / 255,
					(float) geometry.color.getBlue() / 255, geometry.alpha);
		}
	}
	
	public void DrawPoint(MyJTSGeometry geometry, int numPoints, float radius) {

		myGl.glColor4f((float) geometry.color.getRed() / 255, (float) geometry.color.getGreen() / 255,
				(float) geometry.color.getBlue() / 255, geometry.alpha);
		
		Point point = (Point) geometry.geometry;

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);
		//FIXME: Does not work for Point.
		//Add z value			
		if(String.valueOf(point.getCoordinate().z).equals("NaN") == false){
			geometry.z =  geometry.z + (float) point.getCoordinate().z;
		}

		float angle;
		double tempPolygon[][] = new double[100][3];
		for (int k = 0; k < numPoints; k++) {
			angle = (float) (k * 2 * Math.PI / numPoints);

			tempPolygon[k][0] = (float) (point.getCoordinate().x + (Math
					.cos(angle)) * radius);
			tempPolygon[k][1] = -(float) (point.getCoordinate().y + (Math
					.sin(angle)) * radius);
			tempPolygon[k][2] = geometry.z;
		}

		for (int k = 0; k < numPoints; k++) {
			myGlu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		myGl.glColor4f(0.0f, 0.0f, 0.0f, geometry.alpha);
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
			myGl.glVertex3f(xBegin, yBegin, geometry.z);
			myGl.glVertex3f(xEnd, yEnd, geometry.z);
		}
		myGl.glEnd();

	}
	
	public void DrawSphere(MyJTSGeometry geometry) {

		Point point = (Point) geometry.geometry;
		myGl.glTranslated(point.getCoordinate().x, -point.getCoordinate().y, geometry.z);
		myGl.glColor4f((float) geometry.color.getRed() / 255, (float) geometry.color.getGreen() / 255,
				(float) geometry.color.getBlue() / 255, geometry.alpha);

		GLUquadric quad = myGlu.gluNewQuadric();
		myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
        myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
        final int slices = 16;
        final int stacks = 16;
        myGlu.gluSphere(quad, geometry.height, slices, stacks);
        myGlu.gluDeleteQuadric(quad);
        myGl.glTranslated(-point.getCoordinate().x, point.getCoordinate().y, -geometry.z);

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
	
	// Calculate the normal, from three points on a surface
    protected float [] CalculateNormal(Vertex pointA, Vertex pointB, Vertex pointC)
    {
       // Step 1
       // build two vectors, one pointing from A to B, the other pointing from
       // A to C
       float [] vector1 = new float[3];
       float [] vector2 = new float[3];
       
      
          vector1[0] = pointB.x - pointA.x;
          vector2[0] = pointC.x - pointA.x;
          
          vector1[1] = pointB.y - pointA.y;
          vector2[1] = pointC.y - pointA.y;
          
          vector1[2] = pointB.z - pointA.z;
          vector2[2] = pointC.z - pointA.z;
       
       
       // Step 2
       // do the cross product of these two vectors to find the normal
       // of the surface
       
       float [] normal = new float[3];
       normal[0] = (vector1[1] * vector2[2]) - (vector1[2] * vector2[1]);
       normal[1] = (vector1[2] * vector2[0]) - (vector1[0] * vector2[2]);
       normal[2] = (vector1[0] * vector2[1]) - (vector1[1] * vector2[0]);
       
       // Step 3
       // "normalise" the normal (make sure it has length of one)
       
       float total = 0.0f;
       for (int i = 0; i < 3; i++)
       {
          total += (normal[i] * normal[i]);
       }
       float length = (float)Math.sqrt(total);
       
       for (int i = 0; i < 3; i++)
       {
          normal[i] /= length;
       }
             
       // done
       return normal;
    }
	
	


}
