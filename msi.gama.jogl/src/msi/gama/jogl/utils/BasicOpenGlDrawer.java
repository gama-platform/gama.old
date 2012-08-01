package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COMPILE;
import static javax.media.opengl.GL.GL_QUADS;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.Color;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import javax.vecmath.Vector3f;

import msi.gama.common.util.GeometryUtils;
import msi.gama.jogl.JOGLAWTDisplayGraphics;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;
import msi.gama.util.IList;

public class BasicOpenGlDrawer {

	// OpenGL member
	private GL myGl;
	private GLU myGlu;
	private TessellCallBack tessCallback;
	private GLUtessellator tobj;


	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	// FIXME: Is it better to declare an objet polygon here than in
	// DrawMultiPolygon??
	Polygon curPolygon;
	int numExtPoints;
	int numGeometries;

	double tempPolygon[][];
	double temp[];

	//use glut tesselation or JTS tesselation
	boolean useTessellation = true	;
	
	
	
	//Use for JTS triangulation
	IList<IShape> triangles;
	Iterator<IShape> it;
	//public static GeometryFactory factory = new GeometryFactory();
	
	
	private JTSDrawer myJTSDrawer;

	public BasicOpenGlDrawer(final GL gl, final GLU glu,
			final JOGLAWTGLRenderer gLRender) {

		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		tessCallback = new TessellCallBack(myGl, myGlu);
		tobj = glu.gluNewTess();
		
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		myGlu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		
		myJTSDrawer= new JTSDrawer(myGl,myGlu,myGLRender);
		
		//FIXME: Need to understand why when using erroCallback there is a out of memory problem.
		//myGlu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback)

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
				myJTSDrawer.DrawPoint((Point) geometry.geometry, geometry.z, 10, 10, geometry.color,geometry.alpha);
			}
		}
	}
	
}
