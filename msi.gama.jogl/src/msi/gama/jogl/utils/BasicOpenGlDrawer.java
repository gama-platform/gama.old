package msi.gama.jogl.utils;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.jogl.JOGLAWTDisplayGraphics;
import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;


public class BasicOpenGlDrawer {

	// OpenGL member
	private GL myGl;
	private GLU myGlu;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	private JTSDrawer myJTSDrawer;

	public BasicOpenGlDrawer(final GL gl, final GLU glu,
			final JOGLAWTGLRenderer gLRender) {

		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		myJTSDrawer= new JTSDrawer(myGl,myGlu,myGLRender);
	}

    /**
     * Draw a geometry
     * @param geometry
     */
	public void DrawJTSGeometry(MyJTSGeometry geometry) {

		for (int i = 0; i < geometry.geometry.getNumGeometries(); i++) {

			if (geometry.geometry.getGeometryType() == "MultiPolygon") {
				myJTSDrawer.DrawMultiPolygon((MultiPolygon) geometry.geometry, geometry.z, geometry.color,
						geometry.alpha,geometry.fill, geometry.angle, geometry.height);
			}

			else if (geometry.geometry.getGeometryType() == "Polygon") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPolyhedre((Polygon) geometry.geometry, geometry.z, geometry.color,
							geometry.alpha,geometry.height, geometry.angle,true);
				} else {
					myJTSDrawer.DrawPolygon((Polygon) geometry.geometry, geometry.z, geometry.color,
							geometry.alpha,geometry.fill, geometry.isTextured, geometry.angle,true);
				}
			}
			else if (geometry.geometry.getGeometryType() == "MultiLineString") {
				myJTSDrawer.DrawMultiLineString((MultiLineString) geometry.geometry, geometry.z, geometry.color,geometry.alpha,geometry.height);
			}

			else if (geometry.geometry.getGeometryType() == "LineString") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPlan((LineString) geometry.geometry,geometry.z,geometry.color,geometry.alpha,geometry.height,0,true);
				}else{
					myJTSDrawer.DrawLineString((LineString) geometry.geometry, geometry.z, 1.2f, geometry.color,geometry.alpha);
				}
			}

			else if (geometry.geometry.getGeometryType() == "Point") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawSphere((Point) geometry.geometry, geometry.z, ((JOGLAWTDisplayGraphics) myGLRender.displaySurface.openGLGraphics).maxEnvDim/1000, geometry.color,geometry.alpha);
				}else{
					myJTSDrawer.DrawPoint((Point) geometry.geometry, geometry.z, 10, ((JOGLAWTDisplayGraphics) myGLRender.displaySurface.openGLGraphics).maxEnvDim/1000, geometry.color,geometry.alpha);
				}
				
			}
		}
	}
	
}
