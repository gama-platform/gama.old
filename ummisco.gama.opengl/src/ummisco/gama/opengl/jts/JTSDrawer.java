/*********************************************************************************************
 *
 * 'JTSDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.jts;

import java.awt.Color;

import javax.vecmath.Vector3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.common.util.GeometryUtils;
import msi.gama.common.util.GeometryUtils.GamaCoordinateSequence;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.scene.GeometryObject;
import ummisco.gama.opengl.utils.GLUtilNormal;

public class JTSDrawer {

	private static final float[] RECT_TEX_COORDS = { 0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f };
	private static final float[] TRIANGLE_TEX_COORDS = { 0f, 1f, 1f, 0f, 1f, 1f };
	private static final float[] FACE_TEX_COORDS = { 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f };

	private final GLUT glut;
	private final GLUtessellator tobj;
	public final JOGLRenderer renderer;
	final static CoordinateFilter pointDrawer =
			point -> GLContext.getCurrentGL().getGL2().glVertex3d(point.x, JOGLRenderer.Y_FLAG * point.y, point.z);
	final static GamaCoordinateSequence.Visitor sequenceDrawer =
			(x, y, z) -> GLContext.getCurrentGL().getGL2().glVertex3d(x, y, z);

	public JTSDrawer(final JOGLRenderer gLRender) {
		glut = new GLUT();
		renderer = gLRender;
		final TessellCallBack tessCallback = new TessellCallBack();
		tobj = GLU.gluNewTess();

		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
	}

	public void dispose() {
		GLU.gluDeleteTess(tobj);
	}

	public void setColor(final GL2 gl, final Color c, final double alpha) {
		if (c == null) { return; }
		renderer.setCurrentColor(gl, c, alpha);
	}

	public void drawGeometryCollection(final GL2 gl, final GeometryCollection geoms, final Color c, final double alpha,
			final boolean fill, final Color border, final GeometryObject object, final double height,
			final double z_fighting_value) {

		final int numGeometries = geoms.getNumGeometries();

		for (int i = 0; i < numGeometries; i++) {
			final Geometry geom = geoms.getGeometryN(i);
			if (geom instanceof Polygon) {
				final Polygon curPolygon = (Polygon) geom;
				if (height > 0) {
					drawPolyhedre(gl, curPolygon, c, alpha, fill, height, false, border, object, z_fighting_value);
				} else {
					drawPolygon(gl, curPolygon, c, alpha, fill, border, object, true, z_fighting_value);
				}
			} else if (geom instanceof LineString) {
				final LineString l = (LineString) geom;
				if (height > 0) {
					drawPlan(gl, l, c, border, alpha, height, object);
				} else {
					drawLineString(gl, l, JOGLRenderer.getLineWidth(), c, alpha);
				}
			}
		}
	}

	public void drawPolygon(final GL2 gl, final Polygon p, final Color c, final double alpha, final boolean fill,
			final Color border, final GeometryObject object, final boolean drawPolygonContour,
			final double z_fighting_value) {

		final int p_norm_dir = GeometryUtils.isClockWise(p) ? 1 : -1;

		final GamaPoint[] vertices = getExteriorRingVertices(p);

		GLUtilNormal.HandleNormal(vertices, p_norm_dir, renderer);
		if (!object.isTextured()) {
			if (fill) {
				setColor(gl, c, alpha);
				drawTesselatedPolygon(gl, p, p_norm_dir, c, alpha);
				setColor(gl, Color.black, alpha);
				if (drawPolygonContour) {
					drawPolygonContour(gl, p, border, alpha, z_fighting_value);
				}
			} else {
				drawPolygonContour(gl, p, border == null ? c : border, alpha, z_fighting_value);
			}
		} else {
			final Texture texture = object.getTexture(gl, renderer, 0);
			if (texture != null) {
				drawTexturedPolygon(gl, p, texture, p_norm_dir);
			}
			if (drawPolygonContour) {
				drawPolygonContour(gl, p, border, alpha, z_fighting_value);
			}
		}
	}

	public void drawTesselatedPolygon(final GL2 gl, final Polygon p, final int norm_dir, final Color c,
			final double alpha) {

		GLU.gluTessBeginPolygon(tobj, null);

		// Exterior contour
		GLU.gluTessBeginContour(tobj);

		final GamaPoint[] vertices = getExteriorRingVertices(p);
		final double[] normal = GLUtilNormal.CalculateNormal(vertices[0], vertices[1], vertices[2], norm_dir);
		if (renderer.data.isDraw_norm()){
			GLUtilNormal.drawNormal(vertices, renderer, gl, normal);	
		}	
		GLU.gluTessNormal(tobj, normal[0], normal[1], normal[2]);

		for (final GamaPoint pp : GeometryUtils.getCoordinates(p)) {
			final double[] doubles = { pp.x, JOGLRenderer.Y_FLAG * pp.y, pp.z };
			GLU.gluTessVertex(tobj, doubles, 0, doubles);
		}
		GLU.gluTessEndContour(tobj);

		// interior contour
		for (int i = 0; i < p.getNumInteriorRing(); i++) {
			GLU.gluTessBeginContour(tobj);
			final LineString ls = p.getInteriorRingN(i);
			for (final GamaPoint pp : GeometryUtils.getCoordinates(ls)) {
				final double[] doubles = { pp.x, JOGLRenderer.Y_FLAG * pp.y, pp.z };
				GLU.gluTessVertex(tobj, doubles, 0, doubles);
			}
			GLU.gluTessEndContour(tobj);
		}

		GLU.gluTessEndPolygon(tobj);
	}

	private void drawTexturedPolygon(final GL2 gl, final Polygon p, final Texture texture, final int norm_dir) {
		texture.enable(gl);
		texture.bind(gl);
		setColor(gl, Color.white, 1);

		if (p.getNumPoints() > 5) {
			gl.glBegin(GL.GL_TRIANGLES); // draw using triangles
			for (final Polygon tri : GeometryUtils.triangulationSimple(null, p)) {
				final Coordinate[] coords = tri.getCoordinates();
				for (int i = 0; i < 3; i++) {
					gl.glTexCoord2f(TRIANGLE_TEX_COORDS[i * 2], TRIANGLE_TEX_COORDS[i * 2 + 1]);
					gl.glVertex3d(coords[i].x, JOGLRenderer.Y_FLAG * coords[i].y, coords[i].z);
				}
			}
		} else {

			final GamaPoint[] vertices = this.getExteriorRingVertices(p);
			GLUtilNormal.HandleNormal(vertices, norm_dir, renderer);
			gl.glBegin(GL2ES3.GL_QUADS);
			for (int i = 0; i < 4; i++) {
				final Coordinate c = p.getExteriorRing().getCoordinateN(i);
				gl.glTexCoord2f(RECT_TEX_COORDS[i * 2], RECT_TEX_COORDS[i * 2 + 1]);
				gl.glVertex3d(c.x, JOGLRenderer.Y_FLAG * c.y, c.z);
			}

		}
		gl.glEnd();
		texture.disable(gl);
	}

	private void drawPolygonContour(final GL2 gl, final Polygon p, final Color border, final double alpha,
			final double z_fighting_value) {
		if (border == null) { return; }
		final boolean zFighting = renderer.data.isZ_fighting();

		if (zFighting) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			gl.glPolygonOffset(0.0f, -(float) (z_fighting_value * 1.1));
		}
		gl.glBegin(zFighting ? GL2.GL_POLYGON : GL2.GL_LINE_STRIP);
		setColor(gl, border, alpha);
		p.getExteriorRing().apply(pointDrawer);
		gl.glEnd();

		if (p.getNumInteriorRing() > 0) {
			// Draw Interior ring
			for (int i = 0; i < p.getNumInteriorRing(); i++) {
				gl.glBegin(zFighting ? GL2.GL_POLYGON : GL2.GL_LINE_STRIP);
				p.getInteriorRingN(i).apply(pointDrawer);
				gl.glEnd();
			}
		}
		if (zFighting)
			gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
		if (!renderer.data.isTriangulation()) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		}
	}

	public void drawPolyhedre(final GL2 gl, final Polygon p, final Color c, final double alpha, final boolean fill,
			final double height, final boolean drawContour, final Color border, final GeometryObject object,
			final Double z_fighting_value) {

		final int faceNormDir = -1;
		final GamaPoint[] vertices = getExteriorRingVertices(p);
		final boolean isCW = isClockwise(vertices);
		final int p_norm_dir = isCW ? -1 : 1;

		drawPolygon(gl, p, c, alpha, fill, border, object, drawContour, z_fighting_value);
		final double[] vectorNormal = calculatePolygonNormal(p, isCW);

		gl.glPushMatrix();
		gl.glTranslated(-vectorNormal[0] * height, -vectorNormal[1] * height, -vectorNormal[2] * height);
		final float[][] buffer = translatePositionalLights(gl, new float[] { (float) (-vectorNormal[0] * height),
				(float) (-vectorNormal[1] * height), (float) (-vectorNormal[2] * height) });
		drawPolygon(gl, p, c, alpha, fill, border, object, drawContour, z_fighting_value);

		final Texture texture = object.getTexture(gl, renderer, object.hasSeveralTextures() ? 1 : 0);

		drawFaces(gl, p, c, alpha, fill, border, texture, height, drawContour, -faceNormDir, isCW);

		revertTranslatePositionalLights(gl, buffer);
		gl.glPopMatrix();
	}

	// //////////////////////////////FACE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	private void drawFaces(final GL2 gl, final Polygon p, final Color c, final double alpha, final boolean fill,
			final Color b, final Texture texture, final double height, final boolean drawPolygonContour,
			final int norm_dir, final boolean clockwise) {
		Color fillColor = c;
		final boolean textured = texture != null;
		final TextureCoords tc = textured ? texture.getImageTexCoords() : null;
		if (textured) {
			texture.enable(gl);
			texture.bind(gl);
			fillColor = Color.white;
		}

		final double elevation = p.getExteriorRing().getCoordinateN(0).z;
		final int curPolyGonNumPoints = p.getExteriorRing().getNumPoints();
		for (int j = 0; j < curPolyGonNumPoints; j++) {
			final int k = (j + 1) % curPolyGonNumPoints;
			final GamaPoint[] vertices = getFaceVertices(p, j, k, elevation, height, clockwise);
			if (fill || texture != null)
				GLUtilNormal.HandleNormal(vertices, norm_dir, renderer);
			gl.glBegin(GL2ES3.GL_QUADS);
			setColor(gl, fillColor, 1);
			// Set the color to white to avoid color and texture mixture
			// 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f,

			if (textured) {
				gl.glTexCoord2f(tc.left(), tc.bottom());
			}
			gl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
			if (textured) {
				gl.glTexCoord2f(tc.right(), tc.bottom());
			}
			gl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
			if (textured) {
				gl.glTexCoord2f(tc.right(), tc.top());
			}
			gl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
			if (textured) {
				gl.glTexCoord2f(tc.left(), tc.top());
			}
			gl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);
			gl.glEnd();

			if (drawPolygonContour || !fill) {
				setColor(gl, b, alpha);
				gl.glBegin(GL.GL_LINE_STRIP);
				gl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
				gl.glVertex3d(vertices[1].x, vertices[1].y, vertices[1].z);
				gl.glVertex3d(vertices[2].x, vertices[2].y, vertices[2].z);
				gl.glVertex3d(vertices[3].x, vertices[3].y, vertices[3].z);
				gl.glVertex3d(vertices[0].x, vertices[0].y, vertices[0].z);
				gl.glEnd();
			}

		}
		if (texture != null)
			texture.disable(gl);
	}

	private double[] calculatePolygonNormal(final Polygon p, final Boolean clockwise) {
		// Get 3 vertices of the initial polygon.
		final GamaPoint[] verticesP = new GamaPoint[3];
		for (int i = 0; i < 3; i++) {
			verticesP[i] = new GamaPoint();
		}

		verticesP[0].x = p.getExteriorRing().getPointN(0).getX();
		verticesP[0].y = JOGLRenderer.Y_FLAG * p.getExteriorRing().getPointN(0).getY();
		verticesP[0].z = p.getExteriorRing().getPointN(0).getCoordinate().z;

		verticesP[1].x = p.getExteriorRing().getPointN(1).getX();
		verticesP[1].y = JOGLRenderer.Y_FLAG * p.getExteriorRing().getPointN(1).getY();
		verticesP[1].z = p.getExteriorRing().getPointN(1).getCoordinate().z;

		verticesP[2].x = p.getExteriorRing().getPointN(2).getX();
		verticesP[2].y = JOGLRenderer.Y_FLAG * p.getExteriorRing().getPointN(2).getY();
		verticesP[2].z = p.getExteriorRing().getPointN(2).getCoordinate().z;
		final int multiplier = clockwise == null || clockwise != isClockwise(verticesP) ? -1 : 1;
		final double[] normal = GLUtilNormal.CalculateNormal(verticesP[0], verticesP[1], verticesP[2], multiplier);

		return normal;
	}

	private GamaPoint[] getFaceVertices(final Polygon p, final int j, final int k, final double elevation,
			final double height, final boolean clockwise) {

		final double[] vectorNormal = calculatePolygonNormal(p, clockwise);
		// Build the 4 vertices of the face.
		final GamaPoint[] vertices = new GamaPoint[4];
		for (int i = 0; i < 4; i++) {
			vertices[i] = new GamaPoint();
		}

		// reverse the list of points in the exterior ring if the "real" ring
		// (with the Y_FLAG) is not Clockwise.
		final Point[] pointList = new Point[p.getExteriorRing().getNumPoints()];
		if (isClockwise(getExteriorRingVertices(p))) {
			for (int i = 0; i < p.getExteriorRing().getNumPoints(); i++) {
				pointList[p.getExteriorRing().getNumPoints() - i - 1] = p.getExteriorRing().getPointN(i);
			}
		} else {
			for (int i = 0; i < p.getExteriorRing().getNumPoints(); i++) {
				pointList[i] = p.getExteriorRing().getPointN(i);
			}
		}

		vertices[0].x = pointList[j].getX() + vectorNormal[0] * height;
		vertices[0].y = JOGLRenderer.Y_FLAG * pointList[j].getY() + vectorNormal[1] * height;
		vertices[0].z = pointList[j].getCoordinate().z + vectorNormal[2] * height;

		vertices[1].x = pointList[k].getX() + vectorNormal[0] * height;
		vertices[1].y = JOGLRenderer.Y_FLAG * pointList[k].getY() + vectorNormal[1] * height;
		vertices[1].z = pointList[k].getCoordinate().z + vectorNormal[2] * height;

		vertices[2].x = pointList[k].getX();
		vertices[2].y = JOGLRenderer.Y_FLAG * pointList[k].getY();
		vertices[2].z = pointList[k].getCoordinate().z;

		vertices[3].x = pointList[j].getX();
		vertices[3].y = JOGLRenderer.Y_FLAG * pointList[j].getY();
		vertices[3].z = pointList[j].getCoordinate().z;

		return vertices;

	}

	public GamaPoint[] getExteriorRingVertices(final Polygon p) {
		// Build the n vertices of the facet of the polygon.
		final GamaPoint[] vertices = new GamaPoint[p.getExteriorRing().getNumPoints() - 1];
		for (int i = 0; i < p.getExteriorRing().getNumPoints() - 1; i++) {
			vertices[i] = new GamaPoint();
			vertices[i].x = p.getExteriorRing().getPointN(i).getX();
			vertices[i].y = JOGLRenderer.Y_FLAG * p.getExteriorRing().getPointN(i).getY();
			vertices[i].z = p.getExteriorRing().getPointN(i).getCoordinate().z;
			if (Double.isNaN(vertices[i].z)) {
				vertices[i].z = 0.0d;
			}
		}
		return vertices;
	}

	// ////////////////////////////// LINE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	public void drawLineString(final GL2 gl, final LineString line, final float size, final Color c,
			final double alpha) {

		setColor(gl, c, alpha);

		final int numPoints = line.getNumPoints();

		gl.glLineWidth(size);

		gl.glBegin(GL.GL_LINES);
		for (int j = 0; j < numPoints - 1; j++) {

			gl.glVertex3d(line.getPointN(j).getX(), JOGLRenderer.Y_FLAG * line.getPointN(j).getY(),
					line.getPointN(j).getCoordinate().z);

			gl.glVertex3d(line.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * line.getPointN(j + 1).getY(),
					line.getPointN(j + 1).getCoordinate().z);

		}
		gl.glEnd();

	}

	public void drawPlan(final GL2 gl, final LineString l, final Color c, final Color b, final double alpha,
			final double height, final GeometryObject object) {
		if (!object.isTextured()) {
			drawLineString(gl, l, JOGLRenderer.getLineWidth(), c, alpha);
		}

		// Draw a quad
		setColor(gl, c, alpha);
		final int numPoints = l.getNumPoints();

		// Add z value
		final double z = l.getCoordinate().z;

		for (int j = 0; j < numPoints - 1; j++) {

			final GamaPoint[] vertices = new GamaPoint[3];
			for (int i = 0; i < 3; i++) {
				vertices[i] = new GamaPoint();
			}
			vertices[0].x = l.getPointN(j).getX();
			vertices[0].y = JOGLRenderer.Y_FLAG * l.getPointN(j).getY();
			vertices[0].z = z;

			vertices[1].x = l.getPointN(j + 1).getX();
			vertices[1].y = JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY();
			vertices[1].z = z;

			vertices[2].x = l.getPointN(j + 1).getX();
			vertices[2].y = JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY();
			vertices[2].z = z + height;
			GLUtilNormal.HandleNormal(vertices, 1, renderer);

			if (object.isTextured()) {
				final Texture texture = object.getTexture(gl, renderer, 0);
				setColor(gl, Color.white, 1);
				if (texture != null) {
					texture.enable(gl);
					texture.bind(gl);
				}
				setColor(gl, Color.white, 1);
				// GLUtilGLContext.SetCurrentColor(gl, new float[] { 1.0f, 1.0f,
				// 1.0f });
				gl.glBegin(GL2ES3.GL_QUADS);
				gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex3d(l.getPointN(j).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j).getY(), z);
				gl.glTexCoord2f(1.0f, 1.0f);
				;
				gl.glVertex3d(l.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY(), z);
				gl.glTexCoord2f(1.0f, 0.0f);
				;
				gl.glVertex3d(l.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY(), z + height);
				gl.glTexCoord2f(0.0f, 0.0f);
				gl.glVertex3d(l.getPointN(j).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j).getY(), z + height);
				gl.glEnd();

				if (texture != null) {
					texture.disable(gl);
				}
			} else {
				gl.glBegin(GL2ES3.GL_QUADS);
				gl.glVertex3d(l.getPointN(j).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j).getY(), z);
				gl.glVertex3d(l.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY(), z);
				gl.glVertex3d(l.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY(), z + height);
				gl.glVertex3d(l.getPointN(j).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j).getY(), z + height);
				gl.glEnd();
			}

		}

		// if (drawPolygonContour == true) {
		setColor(gl, b, alpha);

		for (int j = 0; j < numPoints - 1; j++) {
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3d(l.getPointN(j).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j).getY(), z);
			gl.glVertex3d(l.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY(), z);

			gl.glVertex3d(l.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY(), z);
			gl.glVertex3d(l.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY(), z + height);

			gl.glVertex3d(l.getPointN(j + 1).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j + 1).getY(), z + height);
			gl.glVertex3d(l.getPointN(j).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j).getY(), z + height);

			gl.glVertex3d(l.getPointN(j).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j).getY(), z + height);
			gl.glVertex3d(l.getPointN(j).getX(), JOGLRenderer.Y_FLAG * l.getPointN(j).getY(), z);

			gl.glEnd();
		}
		setColor(gl, b, alpha);
		// }
	}

	public void drawPoint(final GL2 gl, final Point point, final double z_ordinate, final int numPoints,
			final double radius, final Color c, final double alpha) {
		double z = z_ordinate;
		setColor(gl, c, alpha);

		GLU.gluTessBeginPolygon(tobj, null);
		GLU.gluTessBeginContour(tobj);
		// FIXME: Does not work for Point.
		// Add z value
		if (Double.isNaN(point.getCoordinate().z) == false) {
			z = z + point.getCoordinate().z;
		}

		double angle;
		final double tempPolygon[][] = new double[100][3];
		for (int k = 0; k < numPoints; k++) {
			angle = k * 2 * CmnFastMath.PI / numPoints;

			tempPolygon[k][0] = point.getCoordinate().x + FastMath.cos(angle) * radius;
			tempPolygon[k][1] = JOGLRenderer.Y_FLAG * (point.getCoordinate().y + FastMath.sin(angle) * radius);
			tempPolygon[k][2] = z;
		}

		for (int k = 0; k < numPoints; k++) {
			GLU.gluTessVertex(tobj, tempPolygon[k], 0, tempPolygon[k]);
		}

		GLU.gluTessEndContour(tobj);
		GLU.gluTessEndPolygon(tobj);

		// Add a line around the circle
		// FIXME/ Check the cost of this line
		setColor(gl, Color.black, alpha);
		// GLUtilGLContext.SetCurrentColor(gl, 0.0f, 0.0f, 0.0f, (float)
		// (alpha * c.getAlpha() / 255.0));
		// gl.glLineWidth(renderer.getLineWidth());
		gl.glBegin(GL.GL_LINES);
		double xBegin, xEnd, yBegin, yEnd;
		for (int k = 0; k < numPoints; k++) {
			angle = k * 2 * CmnFastMath.PI / numPoints;
			xBegin = point.getCoordinate().x + FastMath.cos(angle) * radius;
			yBegin = JOGLRenderer.Y_FLAG * (point.getCoordinate().y + FastMath.sin(angle) * radius);
			angle = (k + 1) * 2 * CmnFastMath.PI / numPoints;
			xEnd = point.getCoordinate().x + FastMath.cos(angle) * radius;
			yEnd = JOGLRenderer.Y_FLAG * (point.getCoordinate().y + FastMath.sin(angle) * radius);
			gl.glVertex3d(xBegin, yBegin, z);
			gl.glVertex3d(xEnd, yEnd, z);
		}
		gl.glEnd();

	}

	public float[][] translatePositionalLights(final GL2 gl, final float[] translation) {
		// this function translate all the positional lights, and returns an
		// array of {lightId,x,y,z,w}.

		// compute the number of position to memorize
		int positionalLightNumber = 0;
		for (int id = 0; id < 8; id++) {
			final int lightId = GLLightingFunc.GL_LIGHT0 + id;
			if (gl.glIsEnabled(lightId)) {
				final float[] position = new float[4];
				gl.glGetLightfv(lightId, GLLightingFunc.GL_POSITION, position, 0);
				if (position[3] == 1) {
					positionalLightNumber++;
				}
			}
		}

		// translate the light and store the old position in the buffer to
		// return
		final float[][] result = new float[positionalLightNumber][5];
		positionalLightNumber = 0;
		for (int id = 0; id < 8; id++) {
			final int lightId = GLLightingFunc.GL_LIGHT0 + id;
			if (gl.glIsEnabled(lightId)) {
				final float[] position = new float[4];
				gl.glGetLightfv(lightId, GLLightingFunc.GL_POSITION, position, 0);
				if (position[3] == 1) {
					result[positionalLightNumber][0] = lightId;
					for (int idx = 0; idx < position.length; idx++) {
						result[positionalLightNumber][idx + 1] = position[idx];
					}
					gl.glLightfv(lightId, GLLightingFunc.GL_POSITION, new float[] { position[0] - translation[0],
							position[1] - translation[1], position[2] - translation[2], 1 }, 0);
					positionalLightNumber++;
				}
			}
		}

		// return the buffer. This buffer has to be given to the function
		// revertTranslatePositionalLights
		return result;
	}

	public void revertTranslatePositionalLights(final GL2 gl, final float[][] initPosBuffer) {
		for (final float[] pos : initPosBuffer) {
			gl.glLightfv((int) pos[0], GLLightingFunc.GL_POSITION, new float[] { pos[1], pos[2], pos[3], pos[4] }, 0);
		}
	}

	// //////////////////////////////SPECIAL 3D SHAPE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	public void drawRotationHelper(final GL2 gl, final GamaPoint pos, final double distance) {
		// TODO
		if (gl == null) { return; }
		final int slices = 32;
		final int stacks = 32;
		final GLUT glut = new GLUT();
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glPushMatrix();
		gl.glTranslated(pos.x, pos.y, pos.z);
		setColor(gl, Color.gray, 1);
		glut.glutSolidSphere(5.0 * (distance / 500), slices, stacks);
		setColor(gl, Color.gray, 0.1);
		glut.glutSolidSphere(49.0 * (distance / 500), slices, stacks);
		setColor(gl, Color.gray, 1);
		glut.glutWireSphere(50.0 * (distance / 500), slices / 2, stacks / 2);
		gl.glPopMatrix();
		gl.glEnable(GL2.GL_LIGHTING);
	}

	public void drawROIHelper(final GL2 gl, final Envelope3D envelope) {
		final GLUT glut = new GLUT();
		if (envelope == null)
			return;
		final GamaPoint pos = envelope.centre();
		final double width = envelope.getWidth();
		final double height = envelope.getHeight();
		final double z = Math.max(2, renderer.getMaxEnvDim() / 100);
		// TODO
		if (gl == null) { return; }
		gl.glPushMatrix();
		gl.glTranslated(pos.x, pos.y, pos.z);
		gl.glScaled(width, height, z);
		renderer.setCurrentColor(gl, 0, 0.5, 0, 0.15);
		glut.glutSolidCube(0.99f);
		setColor(gl, Color.gray, 1);
		glut.glutWireCube(1f);
		gl.glPopMatrix();
	}

	public void drawSphere(final GL2 gl, final GeometryObject g) {
		double z = 0.0;
		final Polygon p = (Polygon) g.geometry;
		if (Double.isNaN(p.getCoordinate().z) == false) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glPushMatrix();
		gl.glTranslated(p.getCentroid().getX(), JOGLRenderer.Y_FLAG * p.getCentroid().getY(), z);

		final float[][] buffer = translatePositionalLights(gl, new float[] { (float) p.getCentroid().getX(),
				(float) (JOGLRenderer.Y_FLAG * p.getCentroid().getY()), (float) z });
		final Color c = g.getColor();
		setColor(gl, c, g.getAlpha());
		Texture t = null;
		if (g.isTextured()) {

			if (g.hasSeveralTextures()) {
				t = g.getTexture(gl, renderer, 1);
			} else {
				t = g.getTexture(gl, renderer, 0);
			}
			t.enable(gl);
			t.bind(gl);
			setColor(gl, Color.white, 1);
			// GLUtilGLContext.SetCurrentColor(gl, 1.0f, 1.0f, 1.0f, 1.0f);
		}

		final GLU glu = renderer.getGlu();

		final GLUquadric quad = glu.gluNewQuadric();

		if (g.isTextured()) {
			glu.gluQuadricTexture(quad, true);
		}

		if (!renderer.data.isTriangulation()) {

			glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		} else {
			glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
		}
		glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);

		final int slices = 16;
		final int stacks = 16;

		glu.gluSphere(quad, g.getHeight(), slices, stacks);
		glu.gluDeleteQuadric(quad);
		if (t != null) {
			t.disable(gl);
		}

		gl.glPopMatrix();
		revertTranslatePositionalLights(gl, buffer);
	}

	public void drawCone3D(final GL2 gl, final GeometryObject g) {
		// Add z value (Note: getCentroid does not return a z value)
		double z = 0.0;
		final Polygon p = (Polygon) g.geometry;
		if (Double.isNaN(p.getCoordinate().z) == false) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glPushMatrix();
		gl.glTranslated(p.getCentroid().getX(), JOGLRenderer.Y_FLAG * p.getCentroid().getY(), z);
		final float[][] buffer = translatePositionalLights(gl, new float[] { (float) p.getCentroid().getX(),
				(float) (JOGLRenderer.Y_FLAG * p.getCentroid().getY()), (float) z });
		final Color c = g.getColor();
		setColor(gl, c, g.getAlpha());

		// compute the size of the base : we find the max distance between the x
		// coordinates.
		float minX = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		final Coordinate[] coordinates = g.geometry.getCoordinates();
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].x < minX)
				minX = (float) coordinates[i].x;
			if (coordinates[i].x > maxX)
				maxX = (float) coordinates[i].x;
		}
		final float radius = (maxX - minX) / 2;

		if (!renderer.data.isTriangulation()) {
			glut.glutSolidCone(radius, g.getAttributes().getDepth(), 10, 10);
		} else {
			glut.glutWireCone(radius, g.getAttributes().getDepth(), 10, 10);
		}

		gl.glPopMatrix();
		revertTranslatePositionalLights(gl, buffer);
	}

	public void drawTeapot(final GL2 gl, final GeometryObject g) {

		double z = 0.0;
		final Polygon p = (Polygon) g.geometry;
		if (!Double.isNaN(p.getCoordinate().z)) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glPushMatrix();
		gl.glTranslated(p.getCentroid().getX(), JOGLRenderer.Y_FLAG * p.getCentroid().getY(), z);
		final float[][] buffer = translatePositionalLights(gl, new float[] { (float) p.getCentroid().getX(),
				(float) (JOGLRenderer.Y_FLAG * p.getCentroid().getY()), (float) z });
		setColor(gl, g.getColor(), g.getAlpha());
		// FIXME : apply the rotation also to the light
		gl.glRotated(90, 1.0, 0.0, 0.0);
		glut.glutSolidTeapot(g.getHeight());
		gl.glRotated(-90, 1.0, 0.0, 0.0);
		gl.glPopMatrix();
		revertTranslatePositionalLights(gl, buffer);
	}

	public void drawPyramid(final GL2 gl, final GeometryObject g) {

		double z = 0.0;
		final Polygon p = (Polygon) g.geometry;
		if (Double.isNaN(p.getCoordinate().z) == false) {
			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
		}

		gl.glPushMatrix();
		gl.glTranslated(0, 0, z);
		setColor(gl, g.getColor(), g.getAlpha());
		pyramidSkeleton(gl, p, g.getHeight(), g.getColor(), g.getAlpha(), g);
		// border
		if (g.getBorder() != null) {
			setColor(gl, g.getBorder(), g.getAlpha());
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			pyramidSkeleton(gl, p, g.getHeight(), g.getBorder(), g.getAlpha(), g);
			if (!renderer.data.isTriangulation()) {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			}
		}
		gl.glPopMatrix();
	}

	public void drawMultiLineCylinder(final GL2 gl, final Geometry g, final Color c, final double alpha,
			final double height) {
		// get the number of line in the multiline.
		final MultiLineString lines = (MultiLineString) g;
		final int numGeometries = lines.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for (int i = 0; i < numGeometries; i++) {
			final Geometry gg = lines.getGeometryN(i);
			drawLineCylinder(gl, gg, c, alpha, height);

		}
	}

	public void drawLineCylinder(final GL2 gl, final Geometry g, final Color c, final double alpha,
			final double height) {
		double z = 0.0;

		final Geometry gg = g;

		if (Double.isNaN(gg.getCoordinate().z) == false) {
			z = gg.getCentroid().getCoordinate().z;
		}
		if (gg instanceof Point) {
			// drawSphere(g, z);
			return;
		}
		final LineString l = (LineString) gg;

		for (int i = 0; i <= l.getNumPoints() - 2; i++) {

			if (Double.isNaN(l.getCoordinate().z) == false) {
				z = l.getPointN(i).getCoordinate().z;
			}

			final double x_length = l.getPointN(i + 1).getX() - l.getPointN(i).getX();
			final double y_length = l.getPointN(i + 1).getY() - l.getPointN(i).getY();
			final double z_length = l.getPointN(i + 1).getCoordinate().z - l.getPointN(i).getCoordinate().z;

			final double distance = FastMath.sqrt(x_length * x_length + y_length * y_length + z_length * z_length);

			gl.glPushMatrix();
			gl.glTranslated(l.getPointN(i).getX(), JOGLRenderer.Y_FLAG * l.getPointN(i).getY(), z);
			final float[][] buffer = translatePositionalLights(gl, new float[] { (float) l.getPointN(i).getX(),
					(float) (JOGLRenderer.Y_FLAG * l.getPointN(i).getY()), (float) z });
			Vector3d d;
			if (Double.isNaN(l.getCoordinate().z) == false) {
				d = new Vector3d((l.getPointN(i + 1).getX() - l.getPointN(i).getX()) / distance,
						-(l.getPointN(i + 1).getY() - l.getPointN(i).getY()) / distance,
						(l.getPointN(i + 1).getCoordinate().z - l.getPointN(i).getCoordinate().z) / distance);
			} else {
				d = new Vector3d((l.getPointN(i + 1).getX() - l.getPointN(i).getX()) / distance,
						-(l.getPointN(i + 1).getY() - l.getPointN(i).getY()) / distance, 0);
			}

			final Vector3d z_up = new Vector3d(0, 0, 1);

			final Vector3d a = new Vector3d();
			a.cross(z_up, d);

			double omega = FastMath.acos(z_up.dot(d));
			omega = omega * 180 / CmnFastMath.PI;
			// FIXME : apply the rotation also to the light
			gl.glRotated(omega, a.x, a.y, a.z);

			setColor(gl, c, alpha);
			final GLU myGlu = renderer.getGlu();
			final GLUquadric quad = myGlu.gluNewQuadric();
			if (!renderer.data.isTriangulation()) {
				myGlu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
			} else {
				myGlu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
			}
			myGlu.gluQuadricNormals(quad, GLU.GLU_FLAT);
			myGlu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
			final int slices = 16;
			final int stacks = 16;
			myGlu.gluCylinder(quad, height, height, distance, slices, stacks);
			myGlu.gluDeleteQuadric(quad);

			// gl.glRotated(-omega, a.x, a.y, a.z);
			// gl.glTranslated(-l.getPointN(i).getX(), -JOGLRenderer.Y_FLAG *
			// l.getPointN(i).getY(), -z);
			gl.glPopMatrix();
			revertTranslatePositionalLights(gl, buffer);
		}

	}

	public GamaPoint[] getPyramidfaceVertices(final Polygon p, final int i, final int j, final double size, final int x,
			final int y) {
		final GamaPoint[] vertices = new GamaPoint[3];
		for (int i1 = 0; i1 < 3; i1++) {
			vertices[i1] = new GamaPoint();
		}

		vertices[0].x = p.getExteriorRing().getPointN(i).getX();
		vertices[0].y = JOGLRenderer.Y_FLAG * p.getExteriorRing().getPointN(i).getY();
		vertices[0].z = 0.0d;

		vertices[1].x = p.getExteriorRing().getPointN(j).getX();
		vertices[1].y = JOGLRenderer.Y_FLAG * p.getExteriorRing().getPointN(j).getY();
		vertices[1].z = 0.0d;

		vertices[2].x = p.getExteriorRing().getPointN(i).getX() + size / 2 * x;
		vertices[2].y = JOGLRenderer.Y_FLAG * (p.getExteriorRing().getPointN(i).getY() + size / 2 * y);
		vertices[2].z = size;
		return vertices;
	}

	public void pyramidSkeleton(final GL2 gl, final Polygon p, final double size, final Color c, final double alpha,
			final GeometryObject g) {
		// set the chosen color to the opengl context
		setColor(gl, c, alpha);
		// GLUtilGLContext.SetCurrentColor(gl, c.getRed() / 255.0f, c.getGreen()
		// / 255.0f, c.getBlue() / 255.0f,
		// (float) alpha);

		GamaPoint[] vertices;
		int p_norm_dir = -1;

		vertices = getExteriorRingVertices(p);

		if (isClockwise(vertices) == (JOGLRenderer.Y_FLAG == 1)) {
			p_norm_dir = 1;
		} else {
			p_norm_dir = -1;
		}

		GLUtilNormal.HandleNormal(vertices, p_norm_dir, renderer);
		final Coordinate coords[] = p.getExteriorRing().getCoordinates();

		if (g.isTextured()) {
			final Texture texture = g.getTexture(gl, renderer, 0);
			setColor(gl, Color.white, 1);
			// GLUtilGLContext.SetCurrentColor(gl, 1.0f, 1.0f, 1.0f, 1.0f);
			if (texture != null) {
				texture.enable(gl);
				texture.bind(gl);
			}
			setColor(gl, Color.white, 1);
			// GLUtilGLContext.SetCurrentColor(gl, 1.0f, 1.0f, 1.0f, 1.0f);
		}

		gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
		gl.glPolygonOffset(0.0f, (float) 1.1);

		gl.glBegin(GL2ES3.GL_QUADS);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(coords[0].x, JOGLRenderer.Y_FLAG * coords[0].y, coords[0].z);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(coords[1].x, JOGLRenderer.Y_FLAG * coords[1].y, coords[1].z);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(coords[2].x, JOGLRenderer.Y_FLAG * coords[2].y, coords[2].z);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3d(coords[3].x, JOGLRenderer.Y_FLAG * coords[3].y, coords[3].z);
		gl.glEnd();

		vertices = getPyramidfaceVertices(p, 0, 1, size, 1, -1);
		if (isClockwise(vertices) == (JOGLRenderer.Y_FLAG == 1)) {
			p_norm_dir = 1;
		} else {
			p_norm_dir = -1;
		}
		GLUtilNormal.HandleNormal(vertices, p_norm_dir, renderer);

		final double[] norm = calculatePolygonNormal(p, null);
		norm[0] = norm[0] * size + p.getCentroid().getX();
		norm[1] = norm[1] * size + JOGLRenderer.Y_FLAG * p.getCentroid().getY();
		norm[2] = norm[2] * size + p.getCentroid().getCoordinate().z;

		gl.glPolygonOffset(0.0f, -(float) 1.1);

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(coords[0].x, JOGLRenderer.Y_FLAG * coords[0].y, coords[0].z);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(coords[1].x, JOGLRenderer.Y_FLAG * coords[1].y, coords[1].z);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(norm[0], norm[1], norm[2]);
		gl.glEnd();

		vertices = getPyramidfaceVertices(p, 1, 2, size, 1, 1);
		if (isClockwise(vertices) == (JOGLRenderer.Y_FLAG == 1)) {
			p_norm_dir = 1;
		} else {
			p_norm_dir = -1;
		}
		GLUtilNormal.HandleNormal(vertices, p_norm_dir, renderer);

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(coords[1].x, JOGLRenderer.Y_FLAG * coords[1].y, coords[1].z);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(coords[2].x, JOGLRenderer.Y_FLAG * coords[2].y, coords[2].z);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(norm[0], norm[1], norm[2]);
		gl.glEnd();

		vertices = getPyramidfaceVertices(p, 2, 3, size, -1, 1);
		if (isClockwise(vertices) == (JOGLRenderer.Y_FLAG == 1)) {
			p_norm_dir = 1;
		} else {
			p_norm_dir = -1;
		}
		GLUtilNormal.HandleNormal(vertices, p_norm_dir, renderer);

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(coords[2].x, JOGLRenderer.Y_FLAG * coords[2].y, coords[2].z);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(coords[3].x, JOGLRenderer.Y_FLAG * coords[3].y, coords[3].z);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(norm[0], norm[1], norm[2]);
		gl.glEnd();

		vertices = getPyramidfaceVertices(p, 3, 0, size, -1, -1);
		if (isClockwise(vertices) == (JOGLRenderer.Y_FLAG == 1)) {
			p_norm_dir = 1;
		} else {
			p_norm_dir = -1;
		}
		GLUtilNormal.HandleNormal(vertices, p_norm_dir, renderer);

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(coords[3].x, JOGLRenderer.Y_FLAG * coords[3].y, coords[3].z);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(coords[0].x, JOGLRenderer.Y_FLAG * coords[0].y, coords[0].z);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(norm[0], norm[1], norm[2]);
		gl.glEnd();

		gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);

		if (g.isTextured()) {
			final Texture texture = g.getTexture(gl, renderer, 0);
			if (texture != null) {
				texture.disable(gl);
			}
		}

	}

	public static boolean isClockwise(final GamaPoint[] vertices) {
		double sum = 0.0;
		for (int i = 0; i < vertices.length; i++) {
			final GamaPoint v1 = vertices[i];
			final GamaPoint v2 = vertices[(i + 1) % vertices.length];
			sum += (v2.x - v1.x) * (v2.y + v1.y);
		}
		return sum > 0.0;
	}

}
