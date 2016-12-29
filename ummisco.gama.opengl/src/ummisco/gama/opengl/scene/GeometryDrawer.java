/*********************************************************************************************
 *
 * 'GeometryDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;

import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.util.GamaPair;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.jts.JTSDrawer;
import ummisco.gama.opengl.utils.GLUtilLight;

/**
 *
 * The class GeometryDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */
public class GeometryDrawer extends ObjectDrawer<GeometryObject> {

	public GeometryDrawer(final JOGLRenderer r) {
		super(r);
	}

	@Override
	protected void _draw(final GL2 gl, final GeometryObject geometry) {

		// apply the rotation
		final GamaPair<Double, GamaPoint> rot = geometry.attributes.rotation;

		if (rot != null) {
			final GamaPoint loc = geometry.getLocation();
			// AD Change to a negative rotation to fix Issue #1514
			final Double angle = -rot.key;
			final GamaPoint axis = rot.value;

			gl.glTranslated(loc.x, JOGLRenderer.Y_FLAG * loc.y, loc.z);
			gl.glRotated(angle, axis.x, axis.y, axis.z);
			GLUtilLight.NotifyOpenGLRotation(gl, angle, axis, renderer.data);
			gl.glTranslated(-loc.x, -JOGLRenderer.Y_FLAG * loc.y, -loc.z);
		}

		final double height = geometry.getHeight();
		final Color color = geometry.getColor();
		final Color border = geometry.getBorder();
		final IShape.Type type = geometry.getType();
		final JTSDrawer drawer = renderer.getJTSDrawer();
		final Double alpha = geometry.getAlpha();

		switch (type) {
			case SPHERE:
				drawer.drawSphere(gl, geometry);
				break;
			case CONE:
				drawer.drawCone3D(gl, geometry);
				break;
			case TEAPOT:
				drawer.drawTeapot(gl, geometry);
				break;
			case PYRAMID:
				drawer.drawPyramid(gl, geometry);
				break;
			case POLYLINECYLINDER:
				drawer.drawMultiLineCylinder(gl, geometry.geometry, color, alpha, height);
				break;
			case LINECYLINDER:
				drawer.drawLineCylinder(gl, geometry.geometry, color, alpha, height);
				break;
			case POLYGON:
			case ENVIRONMENT:
			case POLYHEDRON:
			case CUBE:
			case BOX:
			case CYLINDER:
			case GRIDLINE:

				if (height > 0) {
					drawer.drawPolyhedre(gl, (Polygon) geometry.geometry, color, alpha, geometry.isFilled(), height,
							true, border, geometry, geometry.getZ_fighting_id());
				} else {
					drawer.drawPolygon(gl, (Polygon) geometry.geometry, color, alpha, geometry.isFilled(), border,
							geometry, true, geometry.getZ_fighting_id());
				}
				break;
			case LINESTRING:
			case LINEARRING:
			case PLAN:
			case POLYPLAN:
				if (height > 0) {
					drawer.drawPlan(gl, (LineString) geometry.geometry, color, border, alpha, height, geometry);
				} else {
					drawer.drawLineString(gl, (LineString) geometry.geometry, JOGLRenderer.getLineWidth(), color,
							alpha);
				}
				break;
			case POINT:
				drawer.drawPoint(gl, (Point) geometry.geometry, 0, 10, renderer.getMaxEnvDim() / 1000, color, alpha);
				break;
			case MULTIPOLYGON:
			case MULTILINESTRING:
			default:
				if (geometry.geometry instanceof GeometryCollection) {
					drawer.drawGeometryCollection(gl, (GeometryCollection) geometry.geometry, color, alpha,
							geometry.isFilled() || renderer.data.isTriangulation(), border, geometry, height,
							geometry.getZ_fighting_id());
				}
		}

		if (rot != null) {
			final GamaPoint loc = geometry.getLocation();
			final Double angle = -rot.key;
			final GamaPoint axis = rot.value;
			gl.glTranslated(loc.x, JOGLRenderer.Y_FLAG * loc.y, loc.z);
			final LayeredDisplayData data = renderer.data;
			// loc in opengl coordinates (y already multiplied by Y_FLAG)
			gl.glRotated(-angle, axis.x, axis.y, axis.z);
			GLUtilLight.NotifyOpenGLRotation(gl, -angle, axis, renderer.data);
			gl.glTranslated(-loc.x, -JOGLRenderer.Y_FLAG * loc.y, -loc.z);

		}

	}

}