/*********************************************************************************************
 *
 * 'GeometryDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
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
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaPair;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.jts.JTSDrawer;
import ummisco.gama.opengl.utils.GLUtilGLContext;

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
			
			GLUtilGLContext.TranslateContext(gl, new double[] {loc.x, JOGLRenderer.Y_FLAG * loc.y, loc.z}, renderer.data);
			GLUtilGLContext.RotateContext(gl, new double[] {axis.x, axis.y, axis.z}, angle, renderer.data);
			GLUtilGLContext.TranslateContext(gl, new double[] {-loc.x, - JOGLRenderer.Y_FLAG * loc.y, -loc.z}, renderer.data);
		}
		
		final double height = geometry.getHeight();
		final Color color = geometry.getColor();
		final Color border = geometry.getBorder();
		final IShape.Type type = geometry.getType();
		
//		// find the translation vector
//		double z = 0.0;
//		final Polygon p = (Polygon) geometry.geometry;
//		if (Double.isNaN(p.getCoordinate().z) == false) {
//			z = p.getExteriorRing().getPointN(0).getCoordinate().z;
//		}
//		float[] translationVector = new float[] {(float)p.getCentroid().getX(), (float)(renderer.yFlag * p.getCentroid().getY()), (float)z};
//		// translate the light
//		getJtsDrawer().translateAllLights(gl, translationVector);
		
		switch (type) {
		case MULTIPOLYGON:
			getJtsDrawer().drawMultiPolygon(gl, (MultiPolygon) geometry.geometry, color, geometry.getAlpha(),
					geometry.isFilled() || renderer.data.isTriangulation(), border, geometry, height,
					geometry.getZ_fighting_id());
			break;
		case SPHERE:
			getJtsDrawer().drawSphere(gl, geometry);
			break;
		case CONE:
			getJtsDrawer().drawCone3D(gl, geometry);
			break;
		case TEAPOT:
			getJtsDrawer().drawTeapot(gl, geometry);
			break;
		case PYRAMID:
			getJtsDrawer().drawPyramid(gl, geometry);
			break;
		case POLYLINECYLINDER:
			getJtsDrawer().drawMultiLineCylinder(gl, geometry.geometry, color, geometry.getAlpha(), height);
			break;
		case LINECYLINDER:
			getJtsDrawer().drawLineCylinder(gl, geometry.geometry, color, geometry.getAlpha(), height);
			break;
		case POLYGON:
		case ENVIRONMENT:
		case POLYHEDRON:
		case CUBE:
		case BOX:
		case CYLINDER:
		case GRIDLINE:

			if (height > 0) {
				getJtsDrawer().drawPolyhedre(gl, (Polygon) geometry.geometry, color, geometry.getAlpha(),
						geometry.isFilled(), height, true, geometry.getBorder(), geometry, geometry.getZ_fighting_id());
			} else {
				if (getJtsDrawer().renderer.getComputeNormal()) {
					int norm_dir = 1;
//					final Vertex[] vertices = getJtsDrawer().getExteriorRingVertices((Polygon) geometry.geometry);
//					if (!JTSDrawer.isClockwise(vertices)) {
//						norm_dir = -1;
//					}
					getJtsDrawer().drawPolygon(gl, (Polygon) geometry.geometry, color, geometry.getAlpha(),
							geometry.isFilled(), geometry.getBorder(), geometry, true, geometry.getZ_fighting_id(),
							norm_dir);
				} else {
					getJtsDrawer().drawPolygon(gl, (Polygon) geometry.geometry, color, geometry.getAlpha(),
							geometry.isFilled(), geometry.getBorder(), geometry, true, geometry.getZ_fighting_id(), -1);
				}

			}
			break;
		case MULTILINESTRING:
			getJtsDrawer().drawMultiLineString(gl, (MultiLineString) geometry.geometry, 0, color, geometry.getBorder(),
					geometry.getAlpha(), height, geometry);
			break;
		case LINESTRING:
		case LINEARRING:
		case PLAN:
		case POLYPLAN:
			if (height > 0) {
				getJtsDrawer().drawPlan(gl, (LineString) geometry.geometry, 0, color, geometry.getBorder(),
						geometry.getAlpha(), height, 0, true, geometry);
			} else {
				getJtsDrawer().drawLineString(gl, (LineString) geometry.geometry, 0, JOGLRenderer.getLineWidth(), color,
						geometry.getAlpha());
			}
			break;
		case POINT:
			getJtsDrawer().drawPoint(gl, (Point) geometry.geometry, 0, 10, renderer.getMaxEnvDim() / 1000, color,
					geometry.getAlpha());
			break;
		default:
			if (geometry.geometry instanceof GeometryCollection) {
				getJtsDrawer().drawGeometryCollection(gl, (GeometryCollection) geometry.geometry, color,
						geometry.getAlpha(), geometry.isFilled(), geometry.getBorder(), geometry, height,
						geometry.getZ_fighting_id(), 0);
			}

		}
		
		if (rot != null) {
			final GamaPoint loc = geometry.getLocation();
			final Double angle = -rot.key;
			final GamaPoint axis = rot.value;
			GLUtilGLContext.TranslateContext(gl, new double[] {loc.x, JOGLRenderer.Y_FLAG * loc.y, loc.z}, renderer.data);
			GLUtilGLContext.RotateContext(gl, new double[] {axis.x, axis.y, axis.z}, -angle, renderer.data);
			GLUtilGLContext.TranslateContext(gl, new double[] {-loc.x, - JOGLRenderer.Y_FLAG * loc.y, -loc.z}, renderer.data);

		}
		
	}

	JTSDrawer getJtsDrawer() {
		return renderer.getJTSDrawer();
	}

}