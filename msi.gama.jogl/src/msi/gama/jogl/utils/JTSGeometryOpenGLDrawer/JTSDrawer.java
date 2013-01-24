package msi.gama.jogl.utils.JTSGeometryOpenGLDrawer;

import static javax.media.opengl.GL.*;

import java.awt.Color;
import java.util.Iterator;
import javax.media.opengl.GL;
import javax.media.opengl.glu.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.jogl.utils.*;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

public class JTSDrawer {

	// OpenGL member
	private final GL myGl;
	private final GLU myGlu;
	public TessellCallBack tessCallback;
	private final GLUtessellator tobj;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	JTSVisitor visitor;

	// FIXME: Is it better to declare an objet polygon here than in
	// DrawMultiPolygon??
	Polygon curPolygon;
	int numExtPoints;
	int numGeometries;

	double tempPolygon[][];
	double temp[];

	// Use for JTS triangulation
	IList<IShape> triangles;
	Iterator<IShape> it;

	// USe to inverse y composaant
	public int yFlag;

	public JTSDrawer(final JOGLAWTGLRenderer gLRender) {

		myGl = gLRender.gl;
		myGlu = gLRender.glu;
		myGLRender = gLRender;
		tessCallback = new TessellCallBack(myGl, myGlu);
		tobj = myGlu.gluNewTess();

		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);

		visitor = new JTSVisitor(myGl);

		yFlag = -1;

		// FIXME: When using erroCallback there is a out of memory problem.
		// myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);//
		// errorCallback)

	}

	public void DrawMultiPolygon(final MultiPolygon polygons, final float z_layer, final Color c,
		final float alpha, final boolean fill, final Color border, final Integer angle,
		final float height) {

		numGeometries = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for ( int i = 0; i < numGeometries; i++ ) {
			curPolygon = (Polygon) polygons.getGeometryN(i);

			if ( height > 0 ) {
				DrawPolyhedre(curPolygon, z_layer, c, alpha, fill, height, angle, false, border);
			} else {
				DrawPolygon(curPolygon, z_layer, c, alpha, fill, border, false, angle, true,false);
			}
		}
	}

	public void DrawPolygon(final Polygon p, final float z_layer, final Color c, final float alpha,
		final boolean fill, final Color border, final boolean isTextured, final Integer angle,
		final boolean drawPolygonContour,final boolean roundCorner) {

		// FIXME: Angle rotation is not implemented yet

		// Set z_layer
		if ( z_layer != 0 ) {
			myGl.glTranslatef(0.0f, 0.0f, z_layer);
		}

		myGl.glNormal3f(0.0f, 0.0f, 1.0f);

		if ( fill == true ) {

			myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);

			// FIXME:This does not draw the whole. p.getInteriorRingN(n)
			numExtPoints = p.getExteriorRing().getNumPoints();

			
			//Draw rectangle with curved corner (only work for rectangle)
			if(roundCorner == false ){
				myGLRender.graphicsGLUtils.DrawRoundRectangle(p);
			}
			else{
			if ( myGLRender.useTessellation ) {
				DrawTesselatedPolygon(p);
				if ( drawPolygonContour == true ) {
					DrawPolygonContour(p, border);
				}
			}
			// use JTS triangulation on simplified geometry (DouglasPeucker)
			// FIXME: not working with a z_layer value!!!!
			else {
				DrawTriangulatedPolygon(p,myGLRender.showTriangulation);
				myGl.glColor4f(0.0f, 0.0f, 0.0f, alpha);
				if ( drawPolygonContour == true ) {
					DrawPolygonContour(p, border);
				}
			}
			}
		}
		// fill = false. Draw only the contour of the polygon.
		else {
			//if no border has been define draw empty shape with their original color
			if(border.equals(Color.black)){
			  DrawPolygonContour(p, c);
			}
			else{
			  DrawPolygonContour(p, border);	
			}
		}

		// FIXME: Need to check that the polygon is a quad
		if ( isTextured ) {
			DrawTexturedPolygon(p, angle);
		}
		if ( z_layer != 0 ) {
			myGl.glTranslatef(0.0f, 0.0f, -z_layer);
		}
	}

	void DrawTesselatedPolygon(final Polygon p) {

		myGlu.gluTessBeginPolygon(tobj, null);

		// Exterior contour
		myGlu.gluTessBeginContour(tobj);

		tempPolygon = new double[numExtPoints][3];
		// Convert vertices as a list of double for gluTessVertex
		for ( int j = 0; j < numExtPoints; j++ ) {
			tempPolygon[j][0] = (float) p.getExteriorRing().getPointN(j).getX();
			tempPolygon[j][1] = yFlag * (float) p.getExteriorRing().getPointN(j).getY();

			if ( Double.isNaN(p.getExteriorRing().getPointN(j).getCoordinate().z) == true ) {
				tempPolygon[j][2] = 0.0f;
			} else {
				tempPolygon[j][2] = 0.0f + p.getExteriorRing().getPointN(j).getCoordinate().z;
			}
		}
 
		for ( int j = 0; j < numExtPoints; j++ ) {
			
			myGlu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
		}

		myGlu.gluTessEndContour(tobj);

		// interior contour

		for ( int i = 0; i < p.getNumInteriorRing(); i++ ) {
			myGlu.gluTessBeginContour(tobj);
			int numIntPoints = p.getInteriorRingN(i).getNumPoints();
			tempPolygon = new double[numIntPoints][3];
			// Convert vertices as a list of double for gluTessVertex
			for ( int j = 0; j < numIntPoints; j++ ) {
				tempPolygon[j][0] = (float) p.getInteriorRingN(i).getPointN(j).getX();
				tempPolygon[j][1] = yFlag * (float) p.getInteriorRingN(i).getPointN(j).getY();

				if ( Double.isNaN(p.getInteriorRingN(i).getPointN(j).getCoordinate().z) == true ) {
					tempPolygon[j][2] = 0.0f;
				} else {
					tempPolygon[j][2] = 0.0f + p.getInteriorRingN(i).getPointN(j).getCoordinate().z;
				}
			}

			for ( int j = 0; j < numIntPoints; j++ ) {
				myGlu.gluTessVertex(tobj, tempPolygon[j], 0, tempPolygon[j]);
			}
			myGlu.gluTessEndContour(tobj);
		}

		myGlu.gluTessEndPolygon(tobj);
	}

	void DrawTriangulatedPolygon(Polygon p, boolean showTriangulation) {
		boolean simplifyGeometry = false;
		if ( simplifyGeometry ) {
			double sizeTol = Math.sqrt(p.getArea()) / 100.0;
			Geometry g2 = DouglasPeuckerSimplifier.simplify(p, sizeTol);
			if ( g2 instanceof Polygon ) {
				p = (Polygon) g2;
			}
		}
		// Workaround to compute the z value of each triangle as triangulation
		// create new point during the triangulation that are set with z=NaN
		if ( p.getNumPoints() > 4 ) {
			triangles = GeometryUtils.triangulation(null, p); // VERIFY NULL SCOPE

			GamaList<Geometry> segments = new GamaList<Geometry>();
			for ( int i = 0; i < p.getNumPoints() - 1; i++ ) {
				Coordinate[] cs = new Coordinate[2];
				cs[0] = p.getCoordinates()[i];
				cs[1] = p.getCoordinates()[i + 1];
				segments.add(GeometryUtils.factory.createLineString(cs));
			}
			for ( IShape tri : triangles ) {
				for ( int i = 0; i < tri.getInnerGeometry().getNumPoints(); i++ ) {
					Coordinate coord = tri.getInnerGeometry().getCoordinates()[i];
					if ( Double.isNaN(coord.z) ) {
						Point pt = GeometryUtils.factory.createPoint(coord);
						double distMin = Double.MAX_VALUE;
						Geometry closestSeg = null;
						for ( Geometry seg : segments ) {
							double dist = seg.distance(pt);
							if ( dist < distMin ) {
								distMin = dist;
								closestSeg = seg;
							}
						}
						Point pt1 =
							GeometryUtils.factory.createPoint(closestSeg.getCoordinates()[0]);
						Point pt2 =
							GeometryUtils.factory.createPoint(closestSeg.getCoordinates()[1]);

						double dist1 = pt.distance(pt1);
						double dist2 = pt.distance(pt2);
						// FIXME: Work only for geometry
						coord.z =
							(1 - dist1 / closestSeg.getLength()) *
								closestSeg.getCoordinates()[0].z +
								(1 - dist2 / closestSeg.getLength()) *
								closestSeg.getCoordinates()[1].z;
						DrawShape(tri, showTriangulation);
					}
				}
			}
		} else if ( p.getNumPoints() == 4 ) {
			triangles = new GamaList<IShape>();
			triangles.add(new GamaShape(p));
		}
		for ( IShape tri : triangles ) {
			DrawShape(tri, showTriangulation);
		}
	}

	void DrawTexturedPolygon(final Polygon p, final int angle) {
		myGl.glEnable(GL.GL_TEXTURE_2D);
		// Enables this texture's target (e.g., GL_TEXTURE_2D) in the
		// current GL context's state.
		myGLRender.textures[2].enable();
		// Binds this texture to the current GL context.
		myGLRender.textures[2].bind();

		if ( angle != 0 ) {
			myGl.glTranslatef((float) p.getCentroid().getX(), yFlag *
				(float) p.getCentroid().getY(), 0.0f);
			// FIXME:Check counterwise or not, and do we rotate around the
			// center or around a point.
			myGl.glRotatef(-angle, 0.0f, 0.0f, 1.0f);
			myGl.glTranslatef(-(float) p.getCentroid().getX(), +(float) p.getCentroid().getY(),
				0.0f);

			DrawTexturedQuad(p);

			myGl.glTranslatef((float) p.getCentroid().getX(), -(float) p.getCentroid().getY(), 0.0f);
			myGl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
			myGl.glTranslatef(-(float) p.getCentroid().getX(), -yFlag *
				(float) p.getCentroid().getY(), 0.0f);
		} else {
			DrawTexturedQuad(p);
		}

		myGl.glDisable(GL.GL_TEXTURE_2D);
	}

	void DrawTexturedQuad(final Polygon p) {
		myGl.glBegin(GL_QUADS);

		myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureBottom);
		myGl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag *
			p.getExteriorRing().getPointN(0).getY(), 0.0f);

		myGl.glTexCoord2f(myGLRender.textureRight, myGLRender.textureBottom);
		myGl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag *
			p.getExteriorRing().getPointN(1).getY(), 0.0f);

		myGl.glTexCoord2f(myGLRender.textureRight, myGLRender.textureTop);
		myGl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag *
			p.getExteriorRing().getPointN(2).getY(), 0.0f);

		myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureTop);
		myGl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag *
			p.getExteriorRing().getPointN(3).getY(), 0.0f);

		myGl.glEnd();
	}

	public void DrawPolygonContour(final Polygon p, final Color border) {

		// Draw Exterior ring
		myGl.glLineWidth(1.0f);

		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4f((float) border.getRed() / 255, (float) border.getGreen() / 255,
			(float) border.getBlue() / 255, 1.0f);
		p.getExteriorRing().apply(visitor);
		myGl.glEnd();

		// Draw Interior ring
		for ( int i = 0; i < p.getNumInteriorRing(); i++ ) {
			myGl.glBegin(GL.GL_LINES);
			myGl.glColor4f((float) border.getRed() / 255, (float) border.getGreen() / 255,
				(float) border.getBlue() / 255, 1.0f);
			p.getInteriorRingN(i).apply(visitor);
		myGl.glEnd();
		}
	}

	void SetLine(final Point src, final Point dest, final float z, final boolean hasZValue) {
		if ( hasZValue == false ) {
			myGl.glVertex3f((float) src.getX(), (float) (yFlag * src.getY()), z);
			myGl.glVertex3f((float) dest.getX(), (float) (yFlag * dest.getY()), z);
		} else {
			myGl.glVertex3f((float) src.getX(), (float) (yFlag * src.getY()),
				(float) (z + src.getCoordinate().z));
			myGl.glVertex3f((float) dest.getX(), (float) (yFlag * dest.getY()),
				(float) (z + dest.getCoordinate().z));
		}
	}

	public void DrawPolyhedre(final Polygon p, final float z, final Color c, final float alpha,
		final boolean fill, final float height, final Integer angle,
		final boolean drawPolygonContour, final Color border) {

		DrawPolygon(p, z, c, alpha, fill, border, false, angle, drawPolygonContour,false);
		DrawPolygon(p, z + height, c, alpha, fill, border, false, angle, drawPolygonContour,false);
		// FIXME : Will be wrong if angle =!0
		DrawFaces(p, c, alpha, fill, border, z, height, drawPolygonContour, false);

	}

	/**
	 * Given a polygon this will draw the different faces of the 3D polygon.
	 * 
	 * @param p
	 *            :Base polygon
	 * @param c
	 *            : color
	 * @param height
	 *            : height of the polygon
	 */
	public void DrawFaces(Polygon p, Color c, float alpha, boolean fill, Color b, float z_layer,
		float height, boolean drawPolygonContour, boolean drawNormal) {

		// Set z_layer
		myGl.glTranslatef(0.0f, 0.0f, z_layer);

		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
			(float) c.getBlue() / 255, alpha);
		float elevation = 0.0f;

		if ( Double.isNaN(p.getExteriorRing().getPointN(0).getCoordinate().z) == false ) {
			elevation = (float) p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();

		for ( int j = 0; j < curPolyGonNumPoints; j++ ) {

			int k = (j + 1) % curPolyGonNumPoints;

			// Build the 4 vertices of the face.
			Vertex[] vertices = new Vertex[4];
			for ( int i = 0; i < 4; i++ ) {
				vertices[i] = new Vertex();
			}
			// FIXME; change double to float in Vertex
			vertices[0].x = (float) p.getExteriorRing().getPointN(j).getX();
			vertices[0].y = yFlag * (float) p.getExteriorRing().getPointN(j).getY();
			vertices[0].z = elevation + height;

			vertices[1].x = (float) p.getExteriorRing().getPointN(k).getX();
			vertices[1].y = yFlag * (float) p.getExteriorRing().getPointN(k).getY();
			vertices[1].z = elevation + height;

			vertices[2].x = (float) p.getExteriorRing().getPointN(k).getX();
			vertices[2].y = yFlag * (float) p.getExteriorRing().getPointN(k).getY();
			vertices[2].z = elevation;

			vertices[3].x = (float) p.getExteriorRing().getPointN(j).getX();
			vertices[3].y = yFlag * (float) p.getExteriorRing().getPointN(j).getY();
			vertices[3].z = elevation;

			// Compute the normal of the quad (for the moment only give 3 point
			// of the quad, to be enhance for non plan polygon)
			float[] normal = CalculateNormal(vertices[2], vertices[1], vertices[0]);

			if ( fill ) {
				myGl.glBegin(GL.GL_QUADS);

				myGl.glNormal3fv(normal, 0);

				myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
				myGl.glVertex3f(vertices[1].x, vertices[1].y, vertices[1].z);
				myGl.glVertex3f(vertices[2].x, vertices[2].y, vertices[2].z);
				myGl.glVertex3f(vertices[3].x, vertices[3].y, vertices[3].z);

				myGl.glEnd();
			}

			if ( drawPolygonContour == true || fill == false ) {
				
				myGl.glColor4f((float) b.getRed() / 255, (float) b.getGreen() / 255, (float) b.getBlue() / 255, alpha);

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

			if ( drawNormal == true ) {
				myGl.glBegin(GL.GL_LINES);
				myGl.glVertex3f(vertices[0].x, vertices[0].y, vertices[0].z);
				myGl.glVertex3f(vertices[0].x + normal[0] * 2, vertices[0].y + normal[1] * 2,
					vertices[0].z + normal[2] * 2);
				myGl.glEnd();
			}
		}

		myGl.glTranslatef(0.0f, 0.0f, -z_layer);

	}

	public void DrawMultiLineString(final MultiLineString lines, final float z, final Color c,
		final float alpha, final float height) {

		// get the number of line in the multiline.
		numGeometries = lines.getNumGeometries();

		// FIXME: Why setting the color here?
		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
			(float) c.getBlue() / 255, alpha);

		// for each line of a multiline, get each point coordinates.
		for ( int i = 0; i < numGeometries; i++ ) {

			LineString l = (LineString) lines.getGeometryN(i);
			if ( height > 0 ) {
				DrawPlan(l, z, c, alpha, height, 0, true);
			} else {
				DrawLineString(l, z, 1.2f, c, alpha);
			}

		}
	}

	public void DrawLineString(final LineString line, final float z, final float size,
		final Color c, final float alpha) {

		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
			(float) c.getBlue() / 255, alpha);

		int numPoints = line.getNumPoints();

		myGl.glLineWidth(size);

		// Add z value (if the whole line as a z value (add_z)
		/*
		 * if (Double.isNaN (line.getCoordinate().z) == false) {
		 * z = z + (float) line.getCoordinate().z; }
		 */

		// FIXME: this will draw a 3d line if the z value of each point has been
		// set thanks to add_z_pt but if
		myGl.glBegin(GL.GL_LINES);
		for ( int j = 0; j < numPoints - 1; j++ ) {

			if ( Double.isNaN(line.getPointN(j).getCoordinate().z) == true ) {
				myGl.glVertex3f((float) line.getPointN(j).getX(), yFlag *
					(float) line.getPointN(j).getY(), z);

			} else {
				myGl.glVertex3f((float) line.getPointN(j).getX(), yFlag *
					(float) line.getPointN(j).getY(), z +
					(float) line.getPointN(j).getCoordinate().z);
			}
			if ( Double.isNaN(line.getPointN(j + 1).getCoordinate().z) == true ) {
				myGl.glVertex3f((float) line.getPointN(j + 1).getX(), yFlag *
					(float) line.getPointN(j + 1).getY(), z);
			} else {
				myGl.glVertex3f((float) line.getPointN(j + 1).getX(), yFlag *
					(float) line.getPointN(j + 1).getY(), z +
					(float) line.getPointN(j + 1).getCoordinate().z);
			}

		}
		myGl.glEnd();

	}

	public void DrawPlan(final LineString l, float z, final Color c, final float alpha,
		final float height, final Integer angle, final boolean drawPolygonContour) {

		DrawLineString(l, z, 1.2f, c, alpha);
		DrawLineString(l, z + height, 1.2f, c, alpha);

		// Draw a quad
		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
			(float) c.getBlue() / 255, alpha);
		int numPoints = l.getNumPoints();

		// Add z value
		if ( Double.isNaN(l.getCoordinate().z) == false ) {
			z = z + (float) l.getCoordinate().z;
		}

		for ( int j = 0; j < numPoints - 1; j++ ) {
			myGl.glBegin(GL.GL_QUADS);
			myGl.glVertex3f((float) l.getPointN(j).getX(), yFlag * (float) l.getPointN(j).getY(), z);
			myGl.glVertex3f((float) l.getPointN(j + 1).getX(), yFlag *
				(float) l.getPointN(j + 1).getY(), z);

			myGl.glVertex3f((float) l.getPointN(j + 1).getX(), yFlag *
				(float) l.getPointN(j + 1).getY(), z + height);

			myGl.glVertex3f((float) l.getPointN(j).getX(), yFlag * (float) l.getPointN(j).getY(),
				z + height);

			myGl.glEnd();
		}

		if ( drawPolygonContour == true ) {
			myGl.glColor4f(0.0f, 0.0f, 0.0f, alpha);
			for ( int j = 0; j < numPoints - 1; j++ ) {
				myGl.glBegin(GL.GL_LINES);
				myGl.glVertex3f((float) l.getPointN(j).getX(), yFlag *
					(float) l.getPointN(j).getY(), z);
				myGl.glVertex3f((float) l.getPointN(j + 1).getX(),
					yFlag * (float) l.getPointN(j + 1).getY(), z);

				myGl.glVertex3f((float) l.getPointN(j + 1).getX(),
					yFlag * (float) l.getPointN(j + 1).getY(), z);
				myGl.glVertex3f((float) l.getPointN(j + 1).getX(),
					yFlag * (float) l.getPointN(j + 1).getY(), z + height);

				myGl.glVertex3f((float) l.getPointN(j + 1).getX(),
					yFlag * (float) l.getPointN(j + 1).getY(), z + height);
				myGl.glVertex3f((float) l.getPointN(j).getX(), yFlag *
					(float) l.getPointN(j).getY(), z + height);

				myGl.glVertex3f((float) l.getPointN(j).getX(), yFlag *
					(float) l.getPointN(j).getY(), z + height);
				myGl.glVertex3f((float) l.getPointN(j).getX(), yFlag *
					(float) l.getPointN(j).getY(), z);

				myGl.glEnd();
			}
			myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
				(float) c.getBlue() / 255, alpha);
		}
	}

	public void DrawPoint(final Point point, float z, final int numPoints, final float radius,
		final Color c, final float alpha) {

		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
			(float) c.getBlue() / 255, alpha);

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);
		// FIXME: Does not work for Point.
		// Add z value
		if ( Double.isNaN(point.getCoordinate().z) == false ) {
			z = z + (float) point.getCoordinate().z;
		}

		float angle;
		double tempPolygon[][] = new double[100][3];
		for ( int k = 0; k < numPoints; k++ ) {
			angle = (float) (k * 2 * Math.PI / numPoints);

			tempPolygon[k][0] = (float) (point.getCoordinate().x + Math.cos(angle) * radius);
			tempPolygon[k][1] =
				yFlag * (float) (point.getCoordinate().y + Math.sin(angle) * radius);
			tempPolygon[k][2] = z;
		}

		for ( int k = 0; k < numPoints; k++ ) {
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
		for ( int k = 0; k < numPoints; k++ ) {
			angle = (float) (k * 2 * Math.PI / numPoints);
			xBegin = (float) (point.getCoordinate().x + Math.cos(angle) * radius);
			yBegin = yFlag * (float) (point.getCoordinate().y + Math.sin(angle) * radius);
			angle = (float) ((k + 1) * 2 * Math.PI / numPoints);
			xEnd = (float) (point.getCoordinate().x + Math.cos(angle) * radius);
			yEnd = yFlag * (float) (point.getCoordinate().y + Math.sin(angle) * radius);
			myGl.glVertex3f(xBegin, yBegin, z);
			myGl.glVertex3f(xEnd, yEnd, z);
		}
		myGl.glEnd();

	}

	public void DrawSphere(final Point point, final float z_layer, final float radius,
		final Color c, final float alpha) {

		if ( Double.isNaN(point.getCoordinate().z) == true ) {
			myGl.glTranslated(point.getCoordinate().x, yFlag * point.getCoordinate().y, z_layer);
		} else {
			myGl.glTranslated(point.getCoordinate().x, yFlag * point.getCoordinate().y, z_layer +
				point.getCoordinate().z);
		}

		myGl.glColor4f((float) c.getRed() / 255, (float) c.getGreen() / 255,
			(float) c.getBlue() / 255, alpha);

		GLUquadric quad = myGlu.gluNewQuadric();
		myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
		myGlu.gluSphere(quad, radius, slices, stacks);
		myGlu.gluDeleteQuadric(quad);
		if ( Double.isNaN(point.getCoordinate().z) == true ) {
			myGl.glTranslated(-point.getCoordinate().x, -yFlag * point.getCoordinate().y, -z_layer);
		} else {
			myGl.glTranslated(-point.getCoordinate().x, -yFlag * point.getCoordinate().y,
				-(z_layer + point.getCoordinate().z));
		}

	}

	public void DrawShape(final IShape shape, final boolean showTriangulation) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();

		if ( showTriangulation ) {

			if ( Double.isNaN(polygon.getExteriorRing().getPointN(0).getCoordinate().z) == true ) {
				myGl.glBegin(GL.GL_LINES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), 0.0f);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), 0.0f);

				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), 0.0f);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), 0.0f);

				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), 0.0f);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), 0.0f);
				myGl.glEnd();
			} else {
				myGl.glBegin(GL.GL_LINES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing()
					.getPointN(0).getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing()
					.getPointN(0).getCoordinate().z);

				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing()
					.getPointN(1).getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing()
					.getPointN(2).getCoordinate().z);

				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing()
					.getPointN(2).getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing()
					.getPointN(0).getCoordinate().z);
				myGl.glEnd();

			}
		} else {
			if ( Double.isNaN(polygon.getExteriorRing().getPointN(0).getCoordinate().z) == true ) {

				myGl.glBegin(GL_TRIANGLES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), 0.0f);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), 0.0f);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), 0.0f);
				myGl.glEnd();
			} else {
				myGl.glBegin(GL_TRIANGLES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing()
					.getPointN(0).getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing()
					.getPointN(1).getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing()
					.getPointN(2).getCoordinate().z);
				myGl.glEnd();
			}

		}
	}

	/*
	 * Return 9 array with the 3 vertex coordinates of the traingle
	 */
	public float[] GetTriangleVertices(final IShape shape) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();
		float[] vertices = new float[9];
		for ( int i = 0; i < 3; i++ ) {
			vertices[i * 3] = (float) polygon.getExteriorRing().getPointN(0).getX();
			vertices[i * 3 + 1] = (float) (yFlag * polygon.getExteriorRing().getPointN(0).getY());
			vertices[i * 3 + 2] = 0.0f;
		}
		return vertices;
	}

	// Calculate the normal, from three points on a surface
	protected float[] CalculateNormal(final Vertex pointA, final Vertex pointB, final Vertex pointC) {
		// Step 1
		// build two vectors, one pointing from A to B, the other pointing from
		// A to C
		float[] vector1 = new float[3];
		float[] vector2 = new float[3];

		vector1[0] = pointB.x - pointA.x;
		vector2[0] = pointC.x - pointA.x;

		vector1[1] = pointB.y - pointA.y;
		vector2[1] = pointC.y - pointA.y;

		vector1[2] = pointB.z - pointA.z;
		vector2[2] = pointC.z - pointA.z;

		// Step 2
		// do the cross product of these two vectors to find the normal
		// of the surface

		float[] normal = new float[3];
		normal[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
		normal[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
		normal[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];

		// Step 3
		// "normalise" the normal (make sure it has length of one)

		float total = 0.0f;
		for ( int i = 0; i < 3; i++ ) {
			total += normal[i] * normal[i];
		}
		float length = (float) Math.sqrt(total);

		for ( int i = 0; i < 3; i++ ) {
			normal[i] /= length;
		}

		// done
		return normal;
	}

}
