/*********************************************************************************************
 *
 *
 * 'GeometryDrawer.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.*;
import msi.gama.metamodel.shape.*;
import msi.gama.util.GamaPair;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.jts.JTSDrawer;
import ummisco.gama.opengl.utils.Vertex;

/**
 *
 * The class GeometryDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */
public class GeometryDrawer extends ObjectDrawer<GeometryObject> {

	JTSDrawer jtsDrawer;

	public GeometryDrawer(final JOGLRenderer r) {
		super(r);
		jtsDrawer = new JTSDrawer(r);
	}

	@Override
	protected void _draw(final GL2 gl, final GeometryObject geometry) {
		GamaPair<Double, GamaPoint> rot = geometry.attributes.rotation;

		if ( rot != null ) {
			GamaPoint loc = geometry.getLocation();
			// AD Change to a negative rotation to fix Issue #1514
			Double rotation = -rot.key;
			GamaPoint axis = rot.value;
			gl.glTranslated(loc.x, -loc.y, loc.z);
			gl.glRotated(rotation, axis.x, axis.y, axis.z);
			gl.glTranslated(-loc.x, loc.y, -loc.z);
		}
		double height = geometry.getHeight();
		Color color = geometry.getColor();
		Color border = geometry.getBorder();
		IShape.Type type = geometry.getType();
		switch (type) {
			case MULTIPOLYGON:
				jtsDrawer.drawMultiPolygon(gl, (MultiPolygon) geometry.geometry, color, geometry.getAlpha(),
					geometry.isFilled(), border, geometry, height, geometry.getZ_fighting_id());
				break;
			case SPHERE:
				jtsDrawer.drawSphere(gl, geometry);
				break;
			case CONE:
				jtsDrawer.drawCone3D(gl, geometry);
				break;
			case TEAPOT:
				jtsDrawer.drawTeapot(gl, geometry);
				break;
			case PYRAMID:
				jtsDrawer.drawPyramid(gl, geometry);
				break;
			case POLYLINECYLINDER:
				jtsDrawer.drawMultiLineCylinder(gl, geometry.geometry, color, geometry.getAlpha(), height);
				break;
			case LINECYLINDER:
				jtsDrawer.drawLineCylinder(gl, geometry.geometry, color, geometry.getAlpha(), height);
				break;
			case POLYGON:
			case ENVIRONMENT:
			case POLYHEDRON:
			case CUBE:
			case BOX:
			case CYLINDER:
			case GRIDLINE:

				if ( height > 0 ) {
					jtsDrawer.drawPolyhedre(gl, (Polygon) geometry.geometry, color, geometry.getAlpha(),
						geometry.isFilled(), height, true, geometry.getBorder(), geometry, geometry.getZ_fighting_id());
				} else {
					if ( jtsDrawer.renderer.getComputeNormal() ) {
						int norm_dir = 1;
						Vertex[] vertices = jtsDrawer.getExteriorRingVertices((Polygon) geometry.geometry);
						if ( !jtsDrawer.isClockwise(vertices) ) {
							norm_dir = -1;
						}
						jtsDrawer.drawPolygon(gl, (Polygon) geometry.geometry, color, geometry.getAlpha(),
							geometry.isFilled(), geometry.getBorder(), geometry, true, geometry.getZ_fighting_id(),
							norm_dir);
					} else {
						jtsDrawer.drawPolygon(gl, (Polygon) geometry.geometry, color, geometry.getAlpha(),
							geometry.isFilled(), geometry.getBorder(), geometry, true, geometry.getZ_fighting_id(), -1);
					}

				}
				break;
			case MULTILINESTRING:
				jtsDrawer.drawMultiLineString(gl, (MultiLineString) geometry.geometry, 0, color, geometry.getAlpha(),
					height);
				break;
			case LINESTRING:
			case LINEARRING:
			case PLAN:
			case POLYPLAN:
				if ( height > 0 ) {
					jtsDrawer.drawPlan(gl, (LineString) geometry.geometry, 0, color, geometry.getAlpha(), height, 0,
						true);
				} else {
					jtsDrawer.drawLineString(gl, (LineString) geometry.geometry, 0, renderer.getLineWidth(), color,
						geometry.getAlpha());
				}
				break;
			case POINT:
				jtsDrawer.drawPoint(gl, (Point) geometry.geometry, 0, 10, renderer.getMaxEnvDim() / 1000, color,
					geometry.getAlpha());
				break;
			default:
				if ( geometry.geometry instanceof GeometryCollection ) {
					jtsDrawer.drawGeometryCollection(gl, (GeometryCollection) geometry.geometry, color,
						geometry.getAlpha(), geometry.isFilled(), geometry.getBorder(), geometry, height,
						geometry.getZ_fighting_id(), 0);
				}

		}
	}
}