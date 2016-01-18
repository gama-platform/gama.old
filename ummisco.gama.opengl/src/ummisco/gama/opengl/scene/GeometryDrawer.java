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

import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.jts.*;
import ummisco.gama.opengl.utils.*;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;
import com.vividsolutions.jts.geom.*;

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
			if(geometry.rotate3D != null){
				gl.glTranslated(geometry.agent.getLocation().getX(), -geometry.agent.getLocation().getY(), geometry.agent.getLocation().getZ());
				gl.glRotatef(geometry.rotate3D.key.floatValue() , (float) geometry.rotate3D.value.x, (float) geometry.rotate3D.value.y, (float) geometry.rotate3D.value.z);	
				gl.glTranslated(-geometry.agent.getLocation().getX(), geometry.agent.getLocation().getY(), -geometry.agent.getLocation().getZ());
			}
			
			switch (geometry.type) {
				case MULTIPOLYGON:
					jtsDrawer.drawMultiPolygon((MultiPolygon) geometry.geometry, geometry.getColor(),
						geometry.getAlpha(), geometry.fill, geometry.border, geometry.isTextured, geometry,
						geometry.height, geometry.rounded, geometry.getZ_fighting_id());
					break;
				case SPHERE:
					jtsDrawer.drawSphere(geometry);
					break;
				case CONE:
					jtsDrawer.drawCone3D(geometry);
					break;
				case TEAPOT:
					jtsDrawer.drawTeapot(geometry);				
					break;
				case PYRAMID:
					jtsDrawer.drawPyramid(geometry);
					break;
				case POLYLINECYLINDER:
					jtsDrawer.DrawMultiLineCylinder(geometry.geometry, geometry.getColor(), geometry.getAlpha(),
						geometry.height);
					break;
				case LINECYLINDER:
					jtsDrawer.drawLineCylinder(geometry.geometry, geometry.getColor(), geometry.getAlpha(),
						geometry.height);
					break;
				case POLYGON:
				case ENVIRONMENT:
				case POLYHEDRON:
				case CUBE:
				case BOX:
				case CYLINDER:
				case GRIDLINE:
					if(geometry.asset3Dmodel != null){			
						gl.glTranslated(geometry.agent.getLocation().getX(), -geometry.agent.getLocation().getY(), geometry.agent.getLocation().getZ());
						geometry.asset3Dmodel.draw(gl);
						gl.glTranslated(-geometry.agent.getLocation().getX(), geometry.agent.getLocation().getY(), -geometry.agent.getLocation().getZ());
					}
					else{
						if ( geometry.height > 0 ) {
							jtsDrawer.DrawPolyhedre((Polygon) geometry.geometry, geometry.getColor(), geometry.getAlpha(),
								geometry.fill, geometry.height, true, geometry.border, geometry.isTextured, geometry,
								geometry.rounded, geometry.getZ_fighting_id());
						} else {
							if ( jtsDrawer.renderer.getComputeNormal() ) {
								int norm_dir = 1;
								Vertex[] vertices = jtsDrawer.getExteriorRingVertices((Polygon) geometry.geometry);
								if ( !jtsDrawer.IsClockwise(vertices) ) {
									norm_dir = -1;
								}
								jtsDrawer.DrawPolygon((Polygon) geometry.geometry, geometry.getColor(),
									geometry.getAlpha(), geometry.fill, geometry.border, geometry.isTextured, geometry,
									true, geometry.rounded, geometry.getZ_fighting_id(), norm_dir);
							} else {
								jtsDrawer.DrawPolygon((Polygon) geometry.geometry, geometry.getColor(),
									geometry.getAlpha(), geometry.fill, geometry.border, geometry.isTextured, geometry,
									true, geometry.rounded, geometry.getZ_fighting_id(), -1);
							}
	
						}
					}
					break;
				case MULTILINESTRING:
					jtsDrawer.DrawMultiLineString((MultiLineString) geometry.geometry, 0, geometry.getColor(),
						geometry.getAlpha(), geometry.height);
					break;
				case LINESTRING:
				case LINEARRING:
				case PLAN:
				case POLYPLAN:
					if ( geometry.height > 0 ) {
						jtsDrawer.drawPlan((LineString) geometry.geometry, 0, geometry.getColor(), geometry.getAlpha(),
							geometry.height, 0, true);
					} else {
						jtsDrawer.drawLineString((LineString) geometry.geometry, 0, 1.2f, geometry.getColor(),
							geometry.getAlpha());
					}
					break;
				case POINT:
					jtsDrawer.DrawPoint((Point) geometry.geometry, 0, 10, renderer.getMaxEnvDim() / 1000,
						geometry.getColor(), geometry.getAlpha());
					break;
				default:
					if ( geometry.geometry instanceof GeometryCollection ) {
						jtsDrawer.drawGeometryCollection((GeometryCollection) geometry.geometry, geometry.getColor(),
							geometry.getAlpha(), geometry.fill, geometry.border, geometry.isTextured, geometry,
							geometry.height, geometry.rounded, geometry.getZ_fighting_id(), 0);
					}

			}
	}
}