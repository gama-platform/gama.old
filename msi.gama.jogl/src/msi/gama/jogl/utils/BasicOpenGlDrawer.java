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

import java.awt.Color;


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
		myGl.glTranslated(geometry.offSet.x, -geometry.offSet.y, 0.0f);

		for (int i = 0; i < geometry.geometry.getNumGeometries(); i++) {

			if (geometry.geometry.getGeometryType() == "MultiPolygon") {
				myJTSDrawer.DrawMultiPolygon(geometry);
			}

			else if (geometry.geometry.getGeometryType() == "Polygon") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPolyhedre(geometry,true);
				} else {
					myJTSDrawer.DrawPolygon(geometry, geometry.z,true);
				}
			}
			else if (geometry.geometry.getGeometryType() == "MultiLineString") {
				myJTSDrawer.DrawMultiLineString(geometry);
			}

			else if (geometry.geometry.getGeometryType() == "LineString") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPlan(geometry,true);
				}else{
					myJTSDrawer.DrawLineString(geometry, 1.2f);
				}
			}

			else if (geometry.geometry.getGeometryType() == "Point") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawSphere(geometry);
				}else{
					myJTSDrawer.DrawPoint(geometry, 10, ((JOGLAWTDisplayGraphics) myGLRender.displaySurface.openGLGraphics).maxEnvDim/1000);
				}	
			}
		}
		myGl.glTranslated(-geometry.offSet.x, geometry.offSet.y, 0.0f);
	}
	
    /**
     * Draw a geometry with a specific color
     * @param geometry
     */
	public void DrawJTSGeometry(MyJTSGeometry geometry, Color c) {

		myGl.glTranslated(geometry.offSet.x, -geometry.offSet.y, 0.0f);
		
		for (int i = 0; i < geometry.geometry.getNumGeometries(); i++) {

			if (geometry.geometry.getGeometryType() == "MultiPolygon") {
				myJTSDrawer.DrawMultiPolygon(geometry);
			}

			else if (geometry.geometry.getGeometryType() == "Polygon") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPolyhedre(geometry,true);
				} else {
					myJTSDrawer.DrawPolygon(geometry, geometry.z,true);
				}
			}
			else if (geometry.geometry.getGeometryType() == "MultiLineString") {
				myJTSDrawer.DrawMultiLineString(geometry);
			}

			else if (geometry.geometry.getGeometryType() == "LineString") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPlan(geometry,true);
				}else{
					myJTSDrawer.DrawLineString(geometry,1.2f);
				}
			}

			else if (geometry.geometry.getGeometryType() == "Point") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawSphere(geometry);
				}else{
					myJTSDrawer.DrawPoint(geometry, 10, geometry.height);
				}
				
			}
		}
		
		myGl.glTranslated(-geometry.offSet.x, geometry.offSet.y, 0.0f);
	}
	
}
