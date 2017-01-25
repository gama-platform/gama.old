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

import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import static msi.gama.common.geometry.GeometryUtils.applyToInnerGeometries;
import static msi.gama.common.geometry.GeometryUtils.getHolesNumber;
import static msi.gama.common.geometry.GeometryUtils.getTypeOf;
import static msi.gama.common.geometry.GeometryUtils.getYNegatedCoordinates;
import static msi.gama.common.geometry.GeometryUtils.simplifiedTriangulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.preferences.GamaPreferences;
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
	private final LoadingCache<Polygon, Collection<Polygon>> TRIANGULATION_CACHE =
			CacheBuilder.newBuilder().initialCapacity(5000).maximumSize(5000).expireAfterAccess(2, TimeUnit.SECONDS)
					.build(new CacheLoader<Polygon, Collection<Polygon>>() {

						@Override
						public Collection<Polygon> load(final Polygon polygon) throws Exception {
							final List<Polygon> TRIANGLES = new ArrayList<>();
							simplifiedTriangulation(polygon, TRIANGLES);
							return TRIANGLES;
						}
					});

	final ICoordinates pointVertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(10, 3);
	final ICoordinates quadVertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(5, 3);
	final ICoordinates triangleVertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(4, 3);
	final GamaPoint tempTopPoint = new GamaPoint();

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
			GLUtilLight.NotifyOpenGLRotation(gl, -angle, axis, renderer.data);
			gl.glTranslated(-loc.x, loc.y, -loc.z);
			return true;
		}
		return false;
	}

	protected void cancelRotation(final GL2 gl, final Double angle, final GamaPoint axis) {
		GLUtilLight.NotifyOpenGLRotation(gl, angle, axis, renderer.data);
		gl.glPopMatrix();
	}

	@Override
	protected void _draw(final GL2 gl, final GeometryObject object) {
		final Double angle = object.getRotationAngle();
		final GamaPoint axis = object.getRotationAxis();
		final boolean rotated = applyRotation(gl, object.getLocation(), angle, axis);
		try {
			final boolean solid = filled || textured;
			final Color border = object.getBorder() == null && !solid ? object.getColor() : object.getBorder();
			final double height = Objects.firstNonNull(object.getHeight(), 0d);
			final Geometry geom = object.getGeometry();
			final Texture faceTexture = object.getAlternateTexture(gl, renderer);
			final IShape.Type type = object.getType();
			drawGeometry(geom, solid, border, height, faceTexture, type);
		} finally {
			if (rotated) {
				cancelRotation(gl, angle, axis);
			}

		}
	}

	public void drawGeometry(final Geometry geom, final boolean solid, final Color border, final double height,
			final Texture faceTexture, final IShape.Type type) {
		switch (type) {
			case SPHERE:
				drawSphere(geom, solid, height, border);
				break;
			case CONE:
				drawCone3D(geom, solid, height, border);
				break;
			case TEAPOT:
				drawTeapot(geom, solid, height, border);
				break;
			case PYRAMID:
				drawPyramid(geom, solid, height, border);
				break;
			case POLYLINECYLINDER:
				drawMultiLineCylinder(geom, solid, height, border);
				break;
			case LINECYLINDER:
				drawLineCylinder(geom, solid, height, border);
				break;
			case POLYGON:
			case ENVIRONMENT:
			case POLYHEDRON:
			case CUBE:
			case BOX:
			case CYLINDER:
			case GRIDLINE:
				drawPolygon(geom, solid, height, border, faceTexture);
				break;
			case LINESTRING:
			case LINEARRING:
			case PLAN:
			case POLYPLAN:
				drawLineString(geom, solid, height, border);
				break;
			case POINT:
				drawPoint(geom, solid, renderer.getMaxEnvDim() / 800d, border);
				break;
			default:
				applyToInnerGeometries(geom, (g) -> {
					drawGeometry(g, solid, border, height, faceTexture, getTypeOf(g));
				});
		}
	}

	private ICoordinates getCoordinates(final Geometry g) {
		return getYNegatedCoordinates(g);
	}

	public void drawPolygon(final Geometry polygon, final boolean solid, final double height, final Color border,
			final Texture faceTexture) {
		if (polygon instanceof Polygon)
			if (height > 0)
				drawPolyhedron(polygon, solid, height, border, faceTexture);
			else {
				drawPolygon((Polygon) polygon, solid, border, true);
			}

	}

	public void drawPolyhedron(final Geometry polygon, final boolean solid, final double height, final Color border,
			final Texture faceTexture) {
		final GL2 gl = renderer.getGL();
		final ICoordinates vertices = getCoordinates(polygon);
		final boolean hasHoles = getHolesNumber(polygon) > 0;
		// Draw bottom
		drawPolygon((Polygon) polygon, solid, hasHoles ? border : null, false);
		// Compute z-up vector (normal to the bottom if the coordinates were to be drawn clockwise)
		vertices.getNormal(true, height, tempTopPoint);
		// Translate to the new z position
		try {
			gl.glPushMatrix();
			gl.glTranslated(tempTopPoint.x, tempTopPoint.y, tempTopPoint.z);
			// Draw top
			drawPolygon((Polygon) polygon, solid, hasHoles ? border : null, true);
		} finally {
			gl.glPopMatrix();
		}
		if (faceTexture != null) {
			renderer.setCurrentTexture(faceTexture);
		}
		// Draw faces
		vertices.visit((pj, pk) -> {
			quadVertices.replaceWith(pk.x, pk.y, pk.z, pk.x + tempTopPoint.x, pk.y + tempTopPoint.y,
					pk.z + tempTopPoint.z, pj.x + tempTopPoint.x, pj.y + tempTopPoint.y, pj.z + tempTopPoint.z, pj.x,
					pj.y, pj.z, pk.x, pk.y, pk.z);
			_shape(quadVertices, 4, solid, true, true, border);
		});

	}

	private void drawPolygon(final Polygon p, final boolean solid, final Color border, final boolean clockwise) {
		final ICoordinates vertices = getCoordinates(p);
		// Geometries are normally represented clockwise. If the programmer needs to represent a back face, it is
		// specified by clockwise = false;
		if (solid) {
			_normal(vertices, clockwise);
			final boolean hasHoles = getHolesNumber(p) > 0;
			final int size = vertices.size();
			if (hasHoles || size > 5) {
				for (final Polygon tri : TRIANGULATION_CACHE.apply(p)) {
					_shape(getCoordinates(tri), 3, solid, clockwise, false, null);
				}
			} else
				_shape(vertices, size - 1, solid, clockwise, false, null);
		}
		if (border != null) {
			_contour(vertices, border);
			applyToInnerGeometries(p, ring -> _contour(getCoordinates(ring), border));
		}
	}

	public void drawLineString(final Geometry p, final boolean solid, final double height, final Color border) {
		if (height > 0)
			drawPlan(p, solid, height, border);
		else
			_line(getCoordinates(p), -1, false);
	}

	private void drawPlan(final Geometry p, final boolean solid, final double height, final Color border) {
		getCoordinates(p).visit((pj, pk) -> {
			quadVertices.replaceWith(pk.x, pk.y, pk.z, pk.x, pk.y, pk.z + height, pj.x, pj.y, pj.z + height, pj.x, pj.y,
					pj.z, pk.x, pk.y, pk.z);
			_shape(quadVertices, 4, solid, true, true, border);
		});
	}

	public void drawPoint(final Geometry point, final boolean solid, final double height, final Color border) {
		final Coordinate p = point.getCoordinate();
		double x = height;
		double y = 0;
		for (int ii = 0; ii < 10; ii++) {
			final double t = x;
			x = POINT_COS * x - POINT_SIN * y;
			y = POINT_SIN * t + POINT_COS * y;
			pointVertices.replaceWith(ii, x + p.x, y - p.y, 0);
		}
		_shape(pointVertices, -1, solid, true, true, border);
	}

	// //////////////////////////////SPECIAL 3D SHAPE DRAWER
	// //////////////////////////////////////////////////////////////////////////////////

	public void drawPyramid(final Geometry p, final boolean solid, final double height, final Color border) {
		final GL2 gl = renderer.getGL();
		final ICoordinates vertices = getCoordinates(p);
		try {
			gl.glPushMatrix();
			gl.glTranslated(0, 0, vertices.at(0).z);
			_shape(vertices, 4, solid, false, true, null);
			vertices.getNormal(true, height, tempTopPoint);
			vertices.addCenterTo(tempTopPoint);
			vertices.visit((pj, pk) -> {
				triangleVertices.replaceWith(tempTopPoint.x, tempTopPoint.y, tempTopPoint.z, pk.x, pk.y, pk.z, pj.x,
						pj.y, pj.z, tempTopPoint.x, tempTopPoint.y, tempTopPoint.z);
				_shape(triangleVertices, 3, solid, true, true, border);
			});
		} finally {
			gl.glPopMatrix();
		}
	}

	public void drawSphere(final Geometry p, final boolean solid, final double height, final Color border) {
		final ICoordinates vertices = getCoordinates(p);
		final double z = vertices.at(0).z;
		final GL2 gl = renderer.getGL();
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

		final int slices = GamaPreferences.OpenGL.DISPLAY_SLICE_NUMBER.getValue();
		final int stacks = slices;

		glu.gluSphere(quad, height, slices, stacks);
		if (border != null) {
			renderer.setCurrentColor(border);
			glu.gluQuadricTexture(quad, false);
			glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
			glu.gluSphere(quad, height, slices, stacks);
		}
		glu.gluDeleteQuadric(quad);
		gl.glPopMatrix();
	}

	public void drawMultiLineCylinder(final Geometry g, final boolean solid, final double height, final Color border) {
		final int numGeometries = g.getNumGeometries();
		for (int i = 0; i < numGeometries; i++) {
			final Geometry gg = g.getGeometryN(i);
			drawLineCylinder(gg, solid, height, border);
		}
	}

	public void drawTeapot(final Geometry p, final boolean solid, final double height, final Color border) {
		final ICoordinates vertices = getCoordinates(p);
		final GL2 gl = renderer.getGL();
		try {
			gl.glPushMatrix();
			final Coordinate centroid = vertices.getCenter();
			gl.glTranslated(centroid.x, centroid.y, centroid.z);
			gl.glRotated(90, 1.0, 0.0, 0.0);
			final GLUT glut = renderer.getGlut();
			if (solid) {
				glut.glutSolidTeapot(height);
				if (border != null) {
					renderer.setCurrentColor(border);
					glut.glutWireTeapot(height);
				}
			} else
				glut.glutWireTeapot(height);
		} finally {
			gl.glPopMatrix();
		}
	}

	public void drawLineCylinder(final Geometry g, final boolean solid, final double height, final Color border) {

		if (!(g instanceof LineString))
			return;
		final GL2 gl = renderer.getGL();
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
			final int slices = GamaPreferences.OpenGL.DISPLAY_SLICE_NUMBER.getValue();
			final int stacks = slices;
			glu.gluCylinder(quad, height, height, distance, slices, stacks);
			if (border != null) {
				renderer.setCurrentColor(border);
				glu.gluQuadricTexture(quad, false);
				glu.gluQuadricDrawStyle(quad, GLU.GLU_LINE);
				glu.gluCylinder(quad, height, height, distance, slices, stacks);
			}
			glu.gluDeleteQuadric(quad);
			gl.glPopMatrix();
		}

	}

	public void drawCone3D(final Geometry p, final boolean solid, final double height, final Color border) {
		final GL2 gl = renderer.getGL();
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
		final int slices = GamaPreferences.OpenGL.DISPLAY_SLICE_NUMBER.getValue();
		final GLUT glut = renderer.getGlut();
		if (solid) {
			glut.glutSolidCone(radius, height, slices, slices);
			if (border != null) {
				renderer.setCurrentColor(border);
				glut.glutWireCone(radius, height, slices, slices);
			}
		} else {
			glut.glutWireCone(radius, height, slices, slices);
		}

		gl.glPopMatrix();
	}

}