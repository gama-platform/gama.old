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

	public void DrawMultiPolygon(final MultiPolygon polygons, final double z_layer, final Color c, final double alpha,
		final boolean fill, final Color border, final Integer angle, final double height, final boolean rounded) {

		numGeometries = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for ( int i = 0; i < numGeometries; i++ ) {
			curPolygon = (Polygon) polygons.getGeometryN(i);

			if ( height > 0 ) {
				DrawPolyhedre(curPolygon, z_layer, c, alpha, fill, height, angle, false, border, rounded);
			} else {
				DrawPolygon(curPolygon, z_layer, c, alpha, fill, border, false, angle, true, rounded);
			}
		}
	}

	public void DrawPolygon(final Polygon p, final double z_layer, final Color c, final double alpha,
		final boolean fill, final Color border, final boolean isTextured, final Integer angle,
		final boolean drawPolygonContour, final boolean rounded) {

		// Set z_layer
		if ( z_layer != 0 ) {
			myGl.glTranslated(0.0d, 0.0d, z_layer);
		}

		myGl.glNormal3d(0.0d, 0.0d, 1.0d);

		if ( fill == true ) {

			myGl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255, alpha);

			// FIXME:This does not draw the whole. p.getInteriorRingN(n)
			numExtPoints = p.getExteriorRing().getNumPoints();

			// Draw rectangle with curved corner (only work for rectangle)
			if ( rounded == true ) {
				myGLRender.graphicsGLUtils.DrawRoundRectangle(p);
			} else {
				if ( myGLRender.getTessellation() ) {
					DrawTesselatedPolygon(p);
					if ( drawPolygonContour == true ) {
						DrawPolygonContour(p, border);
					}
				}
				// use JTS triangulation on simplified geometry (DouglasPeucker)
				// FIXME: not working with a z_layer value!!!!
				else {
					DrawTriangulatedPolygon(p, myGLRender.JTSTriangulation);
					myGl.glColor4d(0.0d, 0.0d, 0.0d, alpha);
					if ( drawPolygonContour == true ) {
						DrawPolygonContour(p, border);
					}
				}
			}
		}
		// fill = false. Draw only the contour of the polygon.
		else {
			// if no border has been define draw empty shape with their original color
			if ( border.equals(Color.black) ) {
				DrawPolygonContour(p, c);
			} else {
				DrawPolygonContour(p, border);
			}
		}

		// FIXME: Need to check that the polygon is a quad
		if ( isTextured ) {
			DrawTexturedPolygon(p, angle);
		}

		if ( z_layer != 0 ) {
			myGl.glTranslated(0.0d, 0.0d, -z_layer);
		}

	}

	void DrawTesselatedPolygon(final Polygon p) {

		myGlu.gluTessBeginPolygon(tobj, null);

		// Exterior contour
		myGlu.gluTessBeginContour(tobj);

		tempPolygon = new double[numExtPoints][3];
		// Convert vertices as a list of double for gluTessVertex
		for ( int j = 0; j < numExtPoints; j++ ) {
			tempPolygon[j][0] = p.getExteriorRing().getPointN(j).getX();
			tempPolygon[j][1] = yFlag * p.getExteriorRing().getPointN(j).getY();

			if ( Double.isNaN(p.getExteriorRing().getPointN(j).getCoordinate().z) == true ) {
				tempPolygon[j][2] = 0.0d;
			} else {
				tempPolygon[j][2] = 0.0d + p.getExteriorRing().getPointN(j).getCoordinate().z;
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
				tempPolygon[j][0] = p.getInteriorRingN(i).getPointN(j).getX();
				tempPolygon[j][1] = yFlag * p.getInteriorRingN(i).getPointN(j).getY();

				if ( Double.isNaN(p.getInteriorRingN(i).getPointN(j).getCoordinate().z) == true ) {
					tempPolygon[j][2] = 0.0d;
				} else {
					tempPolygon[j][2] = 0.0d + p.getInteriorRingN(i).getPointN(j).getCoordinate().z;
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
						Point pt1 = GeometryUtils.factory.createPoint(closestSeg.getCoordinates()[0]);
						Point pt2 = GeometryUtils.factory.createPoint(closestSeg.getCoordinates()[1]);

						double dist1 = pt.distance(pt1);
						double dist2 = pt.distance(pt2);
						// FIXME: Work only for geometry
						coord.z =
							(1 - dist1 / closestSeg.getLength()) * closestSeg.getCoordinates()[0].z +
								(1 - dist2 / closestSeg.getLength()) * closestSeg.getCoordinates()[1].z;
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

		/*
		 * if ( angle != 0 ) {
		 * myGl.glTranslatef((double) p.getCentroid().getX(), yFlag *
		 * (double) p.getCentroid().getY(), 0.0d);
		 * // FIXME:Check counterwise or not, and do we rotate around the
		 * // center or around a point.
		 * myGl.glRotatef(-angle, 0.0d, 0.0d, 1.0d);
		 * myGl.glTranslatef(-(double) p.getCentroid().getX(), +(double) p.getCentroid().getY(),
		 * 0.0d);
		 * 
		 * DrawTexturedQuad(p);
		 * 
		 * myGl.glTranslatef((double) p.getCentroid().getX(), -(double) p.getCentroid().getY(), 0.0d);
		 * myGl.glRotatef(angle, 0.0d, 0.0d, 1.0d);
		 * myGl.glTranslatef(-(double) p.getCentroid().getX(), -yFlag *
		 * (double) p.getCentroid().getY(), 0.0d);
		 * } else {
		 */
		DrawTexturedQuad(p);
		// }

		myGl.glDisable(GL.GL_TEXTURE_2D);
	}

	void DrawTexturedQuad(final Polygon p) {
		myGl.glBegin(GL_QUADS);

		myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureBottom);
		myGl.glVertex3d(p.getExteriorRing().getPointN(0).getX(), yFlag * p.getExteriorRing().getPointN(0).getY(), 0.0d);

		myGl.glTexCoord2f(myGLRender.textureRight, myGLRender.textureBottom);
		myGl.glVertex3d(p.getExteriorRing().getPointN(1).getX(), yFlag * p.getExteriorRing().getPointN(1).getY(), 0.0d);

		myGl.glTexCoord2f(myGLRender.textureRight, myGLRender.textureTop);
		myGl.glVertex3d(p.getExteriorRing().getPointN(2).getX(), yFlag * p.getExteriorRing().getPointN(2).getY(), 0.0d);

		myGl.glTexCoord2f(myGLRender.textureLeft, myGLRender.textureTop);
		myGl.glVertex3d(p.getExteriorRing().getPointN(3).getX(), yFlag * p.getExteriorRing().getPointN(3).getY(), 0.0d);

		myGl.glEnd();
	}

	public void DrawPolygonContour(final Polygon p, final Color border) {

		// Draw Exterior ring
		myGl.glLineWidth(1.0f);

		myGl.glBegin(GL.GL_LINES);
		myGl.glColor4d((double) border.getRed() / 255, (double) border.getGreen() / 255,
			(double) border.getBlue() / 255, 1.0d);
		p.getExteriorRing().apply(visitor);
		myGl.glEnd();

		// Draw Interior ring
		for ( int i = 0; i < p.getNumInteriorRing(); i++ ) {
			myGl.glBegin(GL.GL_LINES);
			myGl.glColor4d((double) border.getRed() / 255, (double) border.getGreen() / 255,
				(double) border.getBlue() / 255, 1.0d);
			p.getInteriorRingN(i).apply(visitor);
			myGl.glEnd();
		}
	}

	void SetLine(final Point src, final Point dest, final double z, final boolean hasZValue) {
		if ( hasZValue == false ) {
			myGl.glVertex3d(src.getX(), yFlag * src.getY(), z);
			myGl.glVertex3d(dest.getX(), yFlag * dest.getY(), z);
		} else {
			myGl.glVertex3d(src.getX(), yFlag * src.getY(), z + src.getCoordinate().z);
			myGl.glVertex3d(dest.getX(), yFlag * dest.getY(), z + dest.getCoordinate().z);
		}
	}

	public void DrawPolyhedre(final Polygon p, final double z, final Color c, final double alpha, final boolean fill,
		final double height, final Integer angle, final boolean drawPolygonContour, final Color border,
		final boolean rounded) {

		DrawPolygon(p, z, c, alpha, fill, border, false, angle, drawPolygonContour, rounded);
		DrawPolygon(p, z + height, c, alpha, fill, border, false, angle, drawPolygonContour, rounded);
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
	public void DrawFaces(Polygon p, Color c, double alpha, boolean fill, Color b, double z_layer, double height,
		boolean drawPolygonContour, boolean drawNormal) {

		// Set z_layer
		myGl.glTranslated(0.0d, 0.0d, z_layer);

		myGl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255, alpha);
		double elevation = 0.0d;

		if ( Double.isNaN(p.getExteriorRing().getPointN(0).getCoordinate().z) == false ) {
			elevation = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();

		for ( int j = 0; j < curPolyGonNumPoints; j++ ) {

			int k = (j + 1) % curPolyGonNumPoints;

			// Build the 4 vertices of the face.
			Vertex[] vertices = new Vertex[4];
			for ( int i = 0; i < 4; i++ ) {
				vertices[i] = new Vertex();
			}
			// FIXME; change double to double in Vertex
			vertices[0].x = p.getExteriorRing().getPointN(j).getX();
			vertices[0].y = yFlag * p.getExteriorRing().getPointN(j).getY();
			vertices[0].z = elevation + height;

			vertices[1].x = p.getExteriorRing().getPointN(k).getX();
			vertices[1].y = yFlag * p.getExteriorRing().getPointN(k).getY();
			vertices[1].z = elevation + height;

			vertices[2].x = p.getExteriorRing().getPointN(k).getX();
			vertices[2].y = yFlag * p.getExteriorRing().getPointN(k).getY();
			vertices[2].z = elevation;

			vertices[3].x = p.getExteriorRing().getPointN(j).getX();
			vertices[3].y = yFlag * p.getExteriorRing().getPointN(j).getY();
			vertices[3].z = elevation;

			// Compute the normal of the quad (for the moment only give 3 point
			// of the quad, to be enhance for non plan polygon)
			double[] normal = CalculateNormal(vertices[2], vertices[1], vertices[0]);

			if ( fill ) {
				myGl.glBegin(GL.GL_QUADS);

				myGl.glNormal3dv(normal, 0);

				myGl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
				myGl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
				myGl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
				myGl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);

				myGl.glEnd();
			}

			if ( drawPolygonContour == true || fill == false ) {

				myGl.glColor4d((double) b.getRed() / 255, (double) b.getGreen() / 255, (double) b.getBlue() / 255,
					alpha);

				myGl.glBegin(GL.GL_LINES);

				myGl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
				myGl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);

				myGl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
				myGl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);

				myGl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
				myGl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);

				myGl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);
				myGl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);

				myGl.glEnd();

				myGl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255,
					alpha);
			}

			if ( drawNormal == true ) {
				myGl.glBegin(GL.GL_LINES);
				myGl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
				myGl.glVertex3d(vertices[0].x + normal[0] * 2, vertices[0].y + normal[1] * 2, vertices[0].z +
					normal[2] * 2);
				myGl.glEnd();
			}
		}

		myGl.glTranslated(0.0d, 0.0d, (float) -z_layer);

	}

	public void DrawMultiLineString(final MultiLineString lines, final double z, final Color c, final double alpha,
		final double height) {

		// get the number of line in the multiline.
		numGeometries = lines.getNumGeometries();

		// FIXME: Why setting the color here?
		myGl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255, alpha);

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

	public void DrawLineString(final LineString line, final double z, final double size, final Color c,
		final double alpha) {

		myGl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255, alpha);

		int numPoints = line.getNumPoints();

		myGl.glLineWidth((float) size);

		// Add z value (if the whole line as a z value (add_z)
		/*
		 * if (Double.isNaN (line.getCoordinate().z) == false) {
		 * z = z + (double) line.getCoordinate().z; }
		 */

		// FIXME: this will draw a 3d line if the z value of each point has been
		// set thanks to add_z_pt but if
		myGl.glBegin(GL.GL_LINES);
		for ( int j = 0; j < numPoints - 1; j++ ) {

			if ( Double.isNaN(line.getPointN(j).getCoordinate().z) == true ) {
				myGl.glVertex3d(line.getPointN(j).getX(), yFlag * line.getPointN(j).getY(), z);

			} else {
				myGl.glVertex3d(line.getPointN(j).getX(), yFlag * line.getPointN(j).getY(), z +
					line.getPointN(j).getCoordinate().z);
			}
			if ( Double.isNaN(line.getPointN(j + 1).getCoordinate().z) == true ) {
				myGl.glVertex3d(line.getPointN(j + 1).getX(), yFlag * line.getPointN(j + 1).getY(), z);
			} else {
				myGl.glVertex3d(line.getPointN(j + 1).getX(), yFlag * line.getPointN(j + 1).getY(),
					z + line.getPointN(j + 1).getCoordinate().z);
			}

		}
		myGl.glEnd();

	}

	public void DrawPlan(final LineString l, double z, final Color c, final double alpha, final double height,
		final Integer angle, final boolean drawPolygonContour) {

		DrawLineString(l, z, 1.2f, c, alpha);
		DrawLineString(l, z + height, 1.2f, c, alpha);

		// Draw a quad
		myGl.glColor4d(c.getRed() / 255, c.getGreen() / 255, c.getBlue() / 255, alpha);
		int numPoints = l.getNumPoints();

		// Add z value
		if ( Double.isNaN(l.getCoordinate().z) == false ) {
			z = z + l.getCoordinate().z;
		}

		for ( int j = 0; j < numPoints - 1; j++ ) {
			myGl.glBegin(GL.GL_QUADS);
			myGl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z);
			myGl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z);

			myGl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z + height);

			myGl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z + height);

			myGl.glEnd();
		}

		if ( drawPolygonContour == true ) {
			myGl.glColor4d(0.0d, 0.0d, 0.0d, alpha);
			for ( int j = 0; j < numPoints - 1; j++ ) {
				myGl.glBegin(GL.GL_LINES);
				myGl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z);
				myGl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z);

				myGl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z);
				myGl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z + height);

				myGl.glVertex3d(l.getPointN(j + 1).getX(), yFlag * l.getPointN(j + 1).getY(), z + height);
				myGl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z + height);

				myGl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z + height);
				myGl.glVertex3d(l.getPointN(j).getX(), yFlag * l.getPointN(j).getY(), z);

				myGl.glEnd();
			}
			myGl.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, alpha);
		}
	}

	public void DrawPoint(final Point point, double z, final int numPoints, final double radius, final Color c,
		final double alpha) {

		myGl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255, alpha);

		myGlu.gluTessBeginPolygon(tobj, null);
		myGlu.gluTessBeginContour(tobj);
		// FIXME: Does not work for Point.
		// Add z value
		if ( Double.isNaN(point.getCoordinate().z) == false ) {
			z = z + point.getCoordinate().z;
		}

		double angle;
		double tempPolygon[][] = new double[100][3];
		for ( int k = 0; k < numPoints; k++ ) {
			angle = k * 2 * Math.PI / numPoints;

			tempPolygon[k][0] = point.getCoordinate().x + Math.cos(angle) * radius;
			tempPolygon[k][1] = yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			tempPolygon[k][2] = z;
		}

		for ( int k = 0; k < numPoints; k++ ) {
			myGlu.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		myGlu.gluTessEndContour(tobj);
		myGlu.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		myGl.glColor4d(0.0d, 0.0d, 0.0d, alpha);
		myGl.glLineWidth(1.1f);
		myGl.glBegin(GL.GL_LINES);
		double xBegin, xEnd, yBegin, yEnd;
		for ( int k = 0; k < numPoints; k++ ) {
			angle = k * 2 * Math.PI / numPoints;
			xBegin = point.getCoordinate().x + Math.cos(angle) * radius;
			yBegin = yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			angle = (k + 1) * 2 * Math.PI / numPoints;
			xEnd = point.getCoordinate().x + Math.cos(angle) * radius;
			yEnd = yFlag * (point.getCoordinate().y + Math.sin(angle) * radius);
			myGl.glVertex3d(xBegin, yBegin, z);
			myGl.glVertex3d(xEnd, yEnd, z);
		}
		myGl.glEnd();

	}

	public void DrawSphere(final ILocation location, final double z_layer, final double radius, final Color c,
		final double alpha) {

		myGl.glTranslated(location.getX(), yFlag * location.getY(), z_layer + location.getZ());
		myGl.glColor4d((double) c.getRed() / 255, (double) c.getGreen() / 255, (double) c.getBlue() / 255, alpha);

		GLUquadric quad = myGlu.gluNewQuadric();
		myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
		myGlu.gluSphere(quad, radius, slices, stacks);
		myGlu.gluDeleteQuadric(quad);
		myGl.glTranslated(-location.getX(), -yFlag * location.getY(), -(z_layer + location.getZ()));

	}

	public void DrawShape(final IShape shape, final boolean showTriangulation) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();

		if ( showTriangulation ) {

			if ( Double.isNaN(polygon.getExteriorRing().getPointN(0).getCoordinate().z) == true ) {
				myGl.glBegin(GL.GL_LINES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), 0.0d);

				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), 0.0d);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), 0.0d);

				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), 0.0d);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
				myGl.glEnd();
			} else {
				myGl.glBegin(GL.GL_LINES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing().getPointN(0)
					.getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing().getPointN(0)
					.getCoordinate().z);

				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing().getPointN(1)
					.getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing().getPointN(2)
					.getCoordinate().z);

				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing().getPointN(2)
					.getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing().getPointN(0)
					.getCoordinate().z);
				myGl.glEnd();

			}
		} else {
			if ( Double.isNaN(polygon.getExteriorRing().getPointN(0).getCoordinate().z) == true ) {

				myGl.glBegin(GL_TRIANGLES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), 0.0d);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), 0.0d);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), 0.0d);
				myGl.glEnd();
			} else {
				myGl.glBegin(GL_TRIANGLES); // draw using triangles
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(0).getX(), yFlag *
					polygon.getExteriorRing().getPointN(0).getY(), polygon.getExteriorRing().getPointN(0)
					.getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(1).getX(), yFlag *
					polygon.getExteriorRing().getPointN(1).getY(), polygon.getExteriorRing().getPointN(1)
					.getCoordinate().z);
				myGl.glVertex3d(polygon.getExteriorRing().getPointN(2).getX(), yFlag *
					polygon.getExteriorRing().getPointN(2).getY(), polygon.getExteriorRing().getPointN(2)
					.getCoordinate().z);
				myGl.glEnd();
			}

		}
	}

	/*
	 * Return 9 array with the 3 vertex coordinates of the traingle
	 */
	public double[] GetTriangleVertices(final IShape shape) {

		Polygon polygon = (Polygon) shape.getInnerGeometry();
		double[] vertices = new double[9];
		for ( int i = 0; i < 3; i++ ) {
			vertices[i * 3] = polygon.getExteriorRing().getPointN(0).getX();
			vertices[i * 3 + 1] = yFlag * polygon.getExteriorRing().getPointN(0).getY();
			vertices[i * 3 + 2] = 0.0d;
		}
		return vertices;
	}

	// Calculate the normal, from three points on a surface
	protected double[] CalculateNormal(final Vertex pointA, final Vertex pointB, final Vertex pointC) {
		// Step 1
		// build two vectors, one pointing from A to B, the other pointing from
		// A to C
		double[] vector1 = new double[3];
		double[] vector2 = new double[3];

		vector1[0] = pointB.x - pointA.x;
		vector2[0] = pointC.x - pointA.x;

		vector1[1] = pointB.y - pointA.y;
		vector2[1] = pointC.y - pointA.y;

		vector1[2] = pointB.z - pointA.z;
		vector2[2] = pointC.z - pointA.z;

		// Step 2
		// do the cross product of these two vectors to find the normal
		// of the surface

		double[] normal = new double[3];
		normal[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1];
		normal[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2];
		normal[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0];

		// Step 3
		// "normalise" the normal (make sure it has length of one)

		double total = 0.0d;
		for ( int i = 0; i < 3; i++ ) {
			total += normal[i] * normal[i];
		}
		double length = Math.sqrt(total);

		for ( int i = 0; i < 3; i++ ) {
			normal[i] /= length;
		}

		// done
		return normal;
	}

}
