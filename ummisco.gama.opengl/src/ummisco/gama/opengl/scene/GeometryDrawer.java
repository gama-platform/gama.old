/*********************************************************************************************
 *
 * 'Geometryjava, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import static msi.gama.common.util.GeometryUtils.GEOMETRY_FACTORY;
import static msi.gama.common.util.GeometryUtils.applyToInnerGeometries;
import static msi.gama.common.util.GeometryUtils.getHolesNumber;
import static msi.gama.common.util.GeometryUtils.getTypeOf;
import static msi.gama.common.util.GeometryUtils.getYNegatedCoordinates;
import static msi.gama.common.util.GeometryUtils.triangulationSimple;

import java.awt.Color;
import java.util.Arrays;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.common.GamaPreferences;
import msi.gama.common.util.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.utils.GLUtilLight;

/**
 *
 * The class Geometry
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */
public class GeometryDrawer extends ObjectDrawer<GeometryObject> {

	private static final GamaPoint Z_UP = new GamaPoint(0, 0, 1);
	private static final int POINT_SEGMENTS = 10;
	private static final double POINT_THETA = 2 * Math.PI / POINT_SEGMENTS;
	private static final double POINT_COS = Math.cos(POINT_THETA);
	private static final double POINT_SIN = Math.sin(POINT_THETA);

	final ICoordinates pointVertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(10, 3);
	final ICoordinates planFaceVertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(5, 3);
	final ICoordinates pyramidFaceVertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(4, 3);
	final ICoordinates polyhedronFaceVertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(5, 3);
	final GamaPoint polyhedronNormal = new GamaPoint();
	final GamaPoint pyramidTop = new GamaPoint();
	final double pointCoords[] = new double[30];

	public GeometryDrawer(final JOGLRenderer r) {
		super(r);
	}

	protected boolean applyRotation(final GL2 gl, final GamaPoint location, final Double angle, final GamaPoint axis) {
		// Applying the rotation

		if (angle != null) {
			final GamaPoint loc = location;
			// AD Change to a negative rotation to fix Issue #1514
			gl.glPushMatrix();
			gl.glTranslated(loc.x, -loc.y, loc.z);
			gl.glRotated(-angle, axis.x, axis.y, axis.z);
			GLUtilLight.NotifyOpenGLRotation(gl, angle, axis, renderer.data);
			gl.glTranslated(-loc.x, loc.y, -loc.z);
			return true;
		}

		return false;
	}

	protected void cancelRotation(final GL2 gl, final Double angle, final GamaPoint axis) {
		GLUtilLight.NotifyOpenGLRotation(gl, -angle, axis, renderer.data);
		gl.glPopMatrix();
	}

	@Override
	protected void _draw(final GL2 gl, final GeometryObject object) {
		final Double angle = object.getRotationAngle();
		final GamaPoint axis = object.getRotationAxis();
		final boolean rotated = applyRotation(gl, object.getLocation(), angle, axis);
		try {
			final boolean triangulated = renderer.data.isTriangulation();
			final boolean filled = object.isFilled() && !triangulated;
			final boolean textured = object.isTextured() && !triangulated;
			final boolean solid = filled || textured;
			final Color border = object.getBorder() == null && !solid ? object.getColor() : object.getBorder();
			final double height = object.getHeight() == null ? 0d : object.getHeight();
			final Geometry geom = object.getGeometry();
			final Texture faceTexture = object.getAlternateTexture(gl, renderer);
			final IShape.Type type = object.getType();
			drawGeometry(gl, geom, textured, solid, border, height, faceTexture, type);
		} finally {
			if (rotated) {
				cancelRotation(gl, angle, axis);
			}

		}
	}

	public void drawGeometry(final GL2 gl, final Geometry geom, final boolean textured, final boolean solid,
			final Color border, final double height, final Texture faceTexture, final IShape.Type type) {
		switch (type) {
			case SPHERE:
				drawSphere(gl, geom, solid, height, border);
				break;
			case CONE:
				drawCone3D(gl, geom, solid, height, border);
				break;
			case TEAPOT:
				drawTeapot(gl, geom, solid, height, border);
				break;
			case PYRAMID:
				drawPyramid(gl, geom, solid, height, border);
				break;
			case POLYLINECYLINDER:
				drawMultiLineCylinder(gl, geom, solid, height, border);
				break;
			case LINECYLINDER:
				drawLineCylinder(gl, geom, solid, height, border);
				break;
			case POLYGON:
			case ENVIRONMENT:
			case POLYHEDRON:
			case CUBE:
			case BOX:
			case CYLINDER:
			case GRIDLINE:
				drawPolygon(gl, geom, solid, textured, height, border, faceTexture);
				break;
			case LINESTRING:
			case LINEARRING:
			case PLAN:
			case POLYPLAN:
				drawLineString(gl, geom, solid, height, border);
				break;
			case POINT:
				drawPoint(gl, geom, solid, renderer.getMaxEnvDim() / 800d, border);
				break;
			default:
				applyToInnerGeometries(geom, (g) -> {
					drawGeometry(gl, g, textured, solid, border, height, faceTexture, getTypeOf(g));
				});
		}
	}

	private ICoordinates getCoordinates(final Geometry g) {
		return getYNegatedCoordinates(g);
	}

	public void drawPolygon(final GL2 gl, final Geometry polygon, final boolean solid, final boolean textured,
			final double height, final Color border, final Texture faceTexture) {
		if (height > 0)
			this.drawPolyhedron(gl, polygon, solid, textured, height, border, faceTexture);
		else {
			// Front face
			this.drawPolygon(gl, polygon, solid, textured, border, true);
			// Back face (to avoid face culling in case of planar polygons)
			// if (!GamaPreferences.ONLY_VISIBLE_FACES.getValue())
			// this.drawPolygon(gl, polygon, solid, textured, border, false);
		}

	}

	public void drawPolyhedron(final GL2 gl, final Geometry polygon, final boolean solid, final boolean textured,
			final double height, final Color border, final Texture faceTexture) {
		final ICoordinates vertices = getCoordinates(polygon);
		final boolean hasHoles = getHolesNumber(polygon) > 0;
		// Draw bottom
		drawPolygon(gl, polygon, solid, textured, hasHoles ? border : null, false);
		// Compute z-up vector (normal to the bottom if the coordinates were to be drawn clockwise)
		vertices.getNormal(true, height, polyhedronNormal);
		// Translate to the new z position
		gl.glPushMatrix();
		gl.glTranslated(polyhedronNormal.x, polyhedronNormal.y, polyhedronNormal.z);
		// Draw top
		drawPolygon(gl, polygon, solid, textured, hasHoles ? border : null, true);
		gl.glPopMatrix();
		if (faceTexture != null) {
			// faceTexture.enable(gl);
			faceTexture.bind(gl);
		}
		// gl.glActiveTexture(GL.GL_TEXTURE1);

		// Draw faces

		vertices.visit((pj, pk) -> {
			polyhedronFaceVertices.replaceWith(pk.x, pk.y, pk.z, pk.x + polyhedronNormal.x, pk.y + polyhedronNormal.y,
					pk.z + polyhedronNormal.z, pj.x + polyhedronNormal.x, pj.y + polyhedronNormal.y,
					pj.z + polyhedronNormal.z, pj.x, pj.y, pj.z, pk.x, pk.y, pk.z);
			_rectangle(gl, polyhedronFaceVertices, solid, true, true, border);
		});

	}

	private void drawPolygon(final GL2 gl, final Geometry p, final boolean solid, final boolean textured,
			final Color border, final boolean clockwise) {
		final ICoordinates vertices = getCoordinates(p);
		// Geometries are normally represented clockwise. If the programmer needs to represent a back face, it is
		// specified by clockwise = false; drawBackFace tells whether the two faces of the polygon need to be drawn or
		// not

		handleNormal(gl, vertices, clockwise);
		final boolean hasHoles = getHolesNumber(p) > 0;
		if (solid) {
			if (vertices.size() == 4 && !hasHoles) {
				_triangle(gl, vertices, solid, clockwise, false, null);
			} else if (vertices.size() == 5 && !hasHoles) {
				_rectangle(gl, vertices, solid, clockwise, false, null);
			} else if (textured) {
				for (final Polygon tri : triangulationSimple(null, p)) {
					_triangle(gl, getCoordinates(tri), solid, clockwise, false, null);
				}
			} else {
				GLU.gluTessBeginPolygon(tobj, null);
				GLU.gluTessBeginContour(tobj);
				vertices.visit(tessDrawer, -1, clockwise);
				GLU.gluTessEndContour(tobj);
				applyToInnerGeometries(p, geom -> {
					GLU.gluTessBeginContour(tobj);
					getCoordinates(geom).visit(tessDrawer, -1, !clockwise);
					GLU.gluTessEndContour(tobj);
				});
				GLU.gluTessEndPolygon(tobj);
			}
		}
		if (border != null) {
			_contour(gl, vertices, border);
			applyToInnerGeometries(p, ring -> _contour(gl, getCoordinates(ring), border));
		}
	}

	public void drawLineString(final GL2 gl, final Geometry p, final boolean solid, final double height,
			final Color border) {
		if (height > 0)
			drawPlan(gl, p, solid, height, border);
		else
			_line(gl, getCoordinates(p), -1, false);
	}

	private void drawPlan(final GL2 gl, final Geometry p, final boolean solid, final double height,
			final Color border) {
		getCoordinates(p).visit((pj, pk) -> {
			// Explicitly create a ring
			planFaceVertices.replaceWith(pk.x, pk.y, pk.z, pk.x, pk.y, pk.z + height, pj.x, pj.y, pj.z + height, pj.x,
					pj.y, pj.z, pk.x, pk.y, pk.z);
			_rectangle(gl, planFaceVertices, solid, true, true, border);
			// To avoid face culling
			// if (!GamaPreferences.ONLY_VISIBLE_FACES.getValue())
			// _rectangle(gl, ring, solid, false, true, border);
		});
	}

	public void drawPoint(final GL2 gl, final Geometry point, final boolean solid, final double height,
			final Color border) {
		final Coordinate p = point.getCoordinate();
		double x = height;
		double y = 0;
		Arrays.fill(pointCoords, p.z);
		for (int ii = 0; ii < 10; ii++) {
			final double t = x;
			x = POINT_COS * x - POINT_SIN * y;
			y = POINT_SIN * t + POINT_COS * y;
			pointCoords[ii * 3] = x + p.x;
			pointCoords[ii * 3 + 1] = y - p.y;
		}
		pointVertices.replaceWith(pointCoords);
		if (solid) {
			gl.glBegin(gl.GL_POLYGON);
			pointVertices.visit(pointDrawer, -1, true);
			gl.glEnd();
		}
		_contour(gl, pointVertices, border);
	}

	// //////////////////////////////SPECIAL 3D SHAPE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	public void drawPyramid(final GL2 gl, final Geometry p, final boolean solid, final double height,
			final Color border) {
		final ICoordinates vertices = getCoordinates(p);
		gl.glPushMatrix();
		gl.glTranslated(0, 0, vertices.at(0).z);
		_rectangle(gl, vertices, solid, false, true, null);
		vertices.getNormal(true, height, pyramidTop);
		vertices.addCenterTo(pyramidTop);
		vertices.visit((pj, pk) -> {
			pyramidFaceVertices.replaceWith(pyramidTop.x, pyramidTop.y, pyramidTop.z, pk.x, pk.y, pk.z, pj.x, pj.y,
					pj.z, pyramidTop.x, pyramidTop.y, pyramidTop.z);
			_triangle(gl, pyramidFaceVertices, solid, true, true, border);
		});
		gl.glPopMatrix();
	}

	public void drawSphere(final GL2 gl, final Geometry p, final boolean solid, final double height,
			final Color border) {
		final ICoordinates vertices = getCoordinates(p);
		final double z = vertices.at(0).z;
		gl.glPushMatrix();
		final Coordinate centroid = vertices.getCenter();
		gl.glTranslated(centroid.x, centroid.y, z);

		final GLU glu = renderer.getGlu();
		final GLUquadric quad = glu.gluNewQuadric();
		if (solid) {
			glu.gluQuadricTexture(quad, true);
		}
		glu.gluQuadricDrawStyle(quad, solid ? GLU.GLU_FILL : GLU.GLU_LINE);
		glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);

		final int slices = GamaPreferences.DISPLAY_SLICE_NUMBER.getValue();
		final int stacks = slices;

		glu.gluSphere(quad, height, slices, stacks);
		if (border != null) {
			renderer.setCurrentColor(gl, border);
			glu.gluQuadricTexture(quad, false);
			glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
			glu.gluSphere(quad, height, slices, stacks);
		}
		glu.gluDeleteQuadric(quad);
		gl.glPopMatrix();
	}

	public void drawMultiLineCylinder(final GL2 gl, final Geometry g, final boolean solid, final double height,
			final Color border) {
		final int numGeometries = g.getNumGeometries();
		for (int i = 0; i < numGeometries; i++) {
			final Geometry gg = g.getGeometryN(i);
			drawLineCylinder(gl, gg, solid, height, border);
		}
	}

	public void drawTeapot(final GL2 gl, final Geometry p, final boolean solid, final double height,
			final Color border) {
		final ICoordinates vertices = getCoordinates(p);
		gl.glPushMatrix();
		final Coordinate centroid = vertices.getCenter();
		gl.glTranslated(centroid.x, centroid.y, centroid.z);
		gl.glRotated(90, 1.0, 0.0, 0.0);
		final GLUT glut = renderer.getGlut();
		if (solid) {
			glut.glutSolidTeapot(height);
			if (border != null) {
				renderer.setCurrentColor(gl, border);
				glut.glutWireTeapot(height);
			}
		} else
			glut.glutWireTeapot(height);
		gl.glRotated(-90, 1.0, 0.0, 0.0);
		gl.glPopMatrix();
	}

	public void drawLineCylinder(final GL2 gl, final Geometry g, final boolean solid, final double height,
			final Color border) {

		if (!(g instanceof LineString))
			return;
		final LineString l = (LineString) g;

		for (int i = 0; i <= l.getNumPoints() - 2; i++) {

			final GamaPoint pi = new GamaPoint(l.getCoordinateN(i));
			final GamaPoint pi1 = new GamaPoint(l.getCoordinateN(i + 1));
			final double distance = pi1.distance3D(pi);

			gl.glPushMatrix();
			gl.glTranslated(pi.x, -pi.y, pi.z);
			final GamaPoint d = pi1.minus(pi).dividedBy(distance);
			final GamaPoint a = new GamaPoint(-d.y, d.x, 0);
			final double omega = Math.acos(GamaPoint.dotProduct(Z_UP, d)) * 180 / Math.PI;
			gl.glRotated(omega, a.x, a.y, a.z);

			final GLU glu = renderer.getGlu();
			final GLUquadric quad = glu.gluNewQuadric();
			if (solid) {
				glu.gluQuadricTexture(quad, true);
			}
			glu.gluQuadricDrawStyle(quad, solid ? GLU.GLU_FILL : GLU.GLU_LINE);
			glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
			glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
			final int slices = GamaPreferences.DISPLAY_SLICE_NUMBER.getValue();
			final int stacks = slices;
			glu.gluCylinder(quad, height, height, distance, slices, stacks);
			if (border != null) {
				renderer.setCurrentColor(gl, border);
				glu.gluQuadricTexture(quad, false);
				glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
				glu.gluCylinder(quad, height, height, distance, slices, stacks);
			}
			glu.gluDeleteQuadric(quad);
			gl.glPopMatrix();
		}

	}

	public void drawCone3D(final GL2 gl, final Geometry p, final boolean solid, final double height,
			final Color border) {
		final ICoordinates vertices = getCoordinates(p);
		final double z = vertices.at(0).z;

		gl.glPushMatrix();
		final Coordinate centroid = vertices.getCenter();
		gl.glTranslated(centroid.x, centroid.y, z);

		// compute the size of the base : we find the max distance between the x
		// coordinates.
		float minX = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		final Coordinate[] coordinates = vertices.toCoordinateArray();
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].x < minX)
				minX = (float) coordinates[i].x;
			if (coordinates[i].x > maxX)
				maxX = (float) coordinates[i].x;
		}
		final float radius = (maxX - minX) / 2;
		final int slices = GamaPreferences.DISPLAY_SLICE_NUMBER.getValue();
		final GLUT glut = renderer.getGlut();
		if (solid) {
			glut.glutSolidCone(radius, height, slices, slices);
			if (border != null) {
				renderer.setCurrentColor(gl, border);
				glut.glutWireCone(radius, height, slices, slices);
			}
		} else {
			glut.glutWireCone(radius, height, slices, slices);
		}

		gl.glPopMatrix();
	}

}