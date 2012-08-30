package msi.gama.jogl.utils;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.jogl.JOGLAWTDisplayGraphics;
import msi.gama.jogl.utils.GraphicDataType.MyCollection;
import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;

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
		myJTSDrawer = new JTSDrawer(myGl, myGlu, myGLRender);
		
	}

	/**
	 * Draw a geometry
	 * 
	 * @param geometry
	 */
	public void DrawJTSGeometry(MyJTSGeometry geometry) {
		myGl.glTranslated(geometry.offSet.x, -geometry.offSet.y, 0.0f);

		for (int i = 0; i < geometry.geometry.getNumGeometries(); i++) {
			if (geometry.geometry.getGeometryType() == "MultiPolygon") {
				myJTSDrawer.DrawMultiPolygon((MultiPolygon) geometry.geometry,
						geometry.z, geometry.color, geometry.alpha,
						geometry.fill, geometry.angle, geometry.height);
			}

			else if (geometry.geometry.getGeometryType() == "Polygon") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPolyhedre((Polygon) geometry.geometry,
							geometry.z, geometry.color, geometry.alpha,
							geometry.height, geometry.angle, true);
				} else {
					myJTSDrawer.DrawPolygon((Polygon) geometry.geometry,
							geometry.z, geometry.color, geometry.alpha,
							geometry.fill, geometry.isTextured, geometry.angle,
							true);
				}
			} else if (geometry.geometry.getGeometryType() == "MultiLineString") {
				myJTSDrawer.DrawMultiLineString(
						(MultiLineString) geometry.geometry, geometry.z,
						geometry.color, geometry.alpha, geometry.height);
			}

			else if (geometry.geometry.getGeometryType() == "LineString") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPlan((LineString) geometry.geometry,
							geometry.z, geometry.color, geometry.alpha,
							geometry.height, 0, true);
				} else {
					myJTSDrawer.DrawLineString((LineString) geometry.geometry,
							geometry.z, 1.2f, geometry.color, geometry.alpha);
				}
			}

			else if (geometry.geometry.getGeometryType() == "Point") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawSphere((Point) geometry.geometry,
							geometry.z, geometry.height, geometry.color,
							geometry.alpha);
				} else {
					myJTSDrawer
							.DrawPoint(
									(Point) geometry.geometry,
									geometry.z,
									10,
									((JOGLAWTDisplayGraphics) myGLRender.displaySurface.openGLGraphics).maxEnvDim / 1000,
									geometry.color, geometry.alpha);
				}
			}
		}
		myGl.glTranslated(-geometry.offSet.x, geometry.offSet.y, 0.0f);
	}

	/**
	 * Draw a geometry with a specific color
	 * 
	 * @param geometry
	 */
	public void DrawJTSGeometry(MyJTSGeometry geometry, Color c) {

		myGl.glTranslated(geometry.offSet.x, -geometry.offSet.y, 0.0f);

		for (int i = 0; i < geometry.geometry.getNumGeometries(); i++) {

			if (geometry.geometry.getGeometryType() == "MultiPolygon") {
				myJTSDrawer.DrawMultiPolygon((MultiPolygon) geometry.geometry,
						geometry.z, c, geometry.alpha, geometry.fill,
						geometry.angle, geometry.height);
			}

			else if (geometry.geometry.getGeometryType() == "Polygon") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPolyhedre((Polygon) geometry.geometry,
							geometry.z, c, geometry.alpha, geometry.height,
							geometry.angle, true);
				} else {
					myJTSDrawer.DrawPolygon((Polygon) geometry.geometry,
							geometry.z, c, geometry.alpha, geometry.fill,
							geometry.isTextured, geometry.angle, true);
				}
			} else if (geometry.geometry.getGeometryType() == "MultiLineString") {
				myJTSDrawer.DrawMultiLineString(
						(MultiLineString) geometry.geometry, geometry.z, c,
						geometry.alpha, geometry.height);
			}

			else if (geometry.geometry.getGeometryType() == "LineString") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawPlan((LineString) geometry.geometry,
							geometry.z, c, geometry.alpha, geometry.height, 0,
							true);
				} else {
					myJTSDrawer.DrawLineString((LineString) geometry.geometry,
							geometry.z, 1.2f, c, geometry.alpha);
				}
			}

			else if (geometry.geometry.getGeometryType() == "Point") {
				if (geometry.height > 0) {
					myJTSDrawer.DrawSphere((Point) geometry.geometry,
							geometry.z, geometry.height, c, geometry.alpha);
				} else {
					myJTSDrawer.DrawPoint((Point) geometry.geometry,
							geometry.z, 10, geometry.height, c, geometry.alpha);
				}

			}
		}

		myGl.glTranslated(-geometry.offSet.x, geometry.offSet.y, 0.0f);
	}
	
	public void DrawSimpleFeatureCollection(MyCollection collection) {
		
		myGl.glTranslated(-collection.collection.getBounds().centre().x, +collection.collection.getBounds().centre().y, 0.0f);
		

		// Iterate throught all the collection
		SimpleFeatureIterator iterator = collection.collection.features();

		/*Color color = new Color((int) Math.random() * 255,
				(int) Math.random() * 255, (int) Math.random() * 255);*/
		
		Color color= Color.red;

		while (iterator.hasNext()) {

			SimpleFeature feature = (SimpleFeature) iterator.next();

			Geometry sourceGeometry = (Geometry) feature.getDefaultGeometry();

			if (sourceGeometry.getGeometryType() == "MultiPolygon") {
				myJTSDrawer.DrawMultiPolygon((MultiPolygon) sourceGeometry,
						0.0f, color, 1.0f, true, 0, 0.0f);
			}

			else if (sourceGeometry.getGeometryType() == "Polygon") {
				myJTSDrawer.DrawPolygon((Polygon) sourceGeometry, 0.0f, color,
						1.0f, true, false, 0, true);
			} else if (sourceGeometry.getGeometryType() == "MultiLineString") {
				myJTSDrawer.DrawMultiLineString(
						(MultiLineString) sourceGeometry, 0.0f, color, 1.0f,
						0.0f);
			}

			else if (sourceGeometry.getGeometryType() == "LineString") {
				myJTSDrawer.DrawLineString((LineString) sourceGeometry, 0.0f,
						1.0f, color, 1.0f);
			}

			else if (sourceGeometry.getGeometryType() == "Point") {
				myJTSDrawer.DrawPoint((Point) sourceGeometry, 0.0f, 10, 10,
						color, 1.0f);
			}
		}
		
		myGl.glTranslated(collection.collection.getBounds().centre().x, -collection.collection.getBounds().centre().y, 0.0f);

	}

}
