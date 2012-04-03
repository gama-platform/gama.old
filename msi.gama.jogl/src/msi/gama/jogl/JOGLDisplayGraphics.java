/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

package msi.gama.jogl;

import static java.awt.RenderingHints.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.jogl.utils.MyGeometry;
import msi.gama.jogl.utils.MyGraphics;
import msi.gaml.operators.Maths;
import org.jfree.chart.JFreeChart;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

/**
 * 
 * Simplifies the drawing of circles, rectangles, and so forth. Rectangles are
 * generally faster to draw than circles. The Displays should take care of
 * layouts while objects that wish to be drawn as a shape need only call the
 * appropriate method.
 * <p>
 * 
 * @author Nick Collier, Alexis Drogoul, Patrick Taillandier
 * @version $Revision: 1.13 $ $Date: 2010-03-19 07:12:24 $
 */

public class JOGLDisplayGraphics implements IGraphics {

	// OpenGL memeber
	private GL myGl;
	private GLU myGlu;
	
	//Each geomtry drawn is stored in a tab.
	public ArrayList<MyGeometry> myGeometries= new ArrayList<MyGeometry>();
	
	
	// Handle openg gl primitive.
	public MyGraphics graphicsGLUtils;
	
	/**
	 * Constructor for DisplayGraphics.
	 * 
	 * @param BufferedImage
	 *            image
	 */
	JOGLDisplayGraphics(final BufferedImage image) {
		System.out.println("JOGLAWTDisplayGraphics(image) constructor");
		setDisplayDimensions(image.getWidth(), image.getHeight());
		setGraphics((Graphics2D) image.getGraphics());

	}

	/**
	 * Constructor for OpenGL DisplayGraphics.
	 * 
	 * @param width
	 *            int
	 * @param height
	 *            int
	 * @param GL
	 *            gl
	 */
	public JOGLDisplayGraphics(final BufferedImage image, GL gl, GLU glu) {
		setDisplayDimensions(image.getWidth(), image.getHeight());
		setGraphics((Graphics2D) image.getGraphics());
		myGl = gl;
		myGlu= glu;
		graphicsGLUtils = new MyGraphics();
	}
	
	@Override
	public void setGraphics(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getDisplayWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDisplayHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDisplayDimensions(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFont(Font font) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getXScale() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setXScale(double scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getYScale() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setYScale(double scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDrawingCoordinates(double x, double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDrawingDimensions(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle2D drawImage(BufferedImage img, Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D drawCircle(Color c, boolean fill, Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D drawTriangle(Color c, boolean fill, Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D drawLine(Color c, double toX, double toY) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D drawRectangle(Color color, boolean fill, Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D drawString(String string, Color stringColor,
			Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D drawGeometry(Geometry geometry, Color color,
			boolean fill, Integer angle) {
		drawGeometryGL(geometry, color);
		return null;
	}

	@Override
	public Rectangle2D drawShape(Color c, Shape s, boolean fill, Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDrawingOffset(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle2D drawChart(JFreeChart chart) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOpacity(double i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void fill(Color bgColor, double opacity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setQualityRendering(boolean quality) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClipping(Rectangle imageClipBounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle getClipping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D drawImage(BufferedImage img, Integer angle,
			boolean smooth) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void drawGeometryGL(final Geometry geometry, final Color color) {
		System.out.println("Trying to draw in open gl the geometry: "
				+ geometry.getGeometryType() + " geometry.getNumGeometries()"
				+ geometry.getNumGeometries());
					
		
		// A geometry can contain several geometry
		for (int i = 0; i < geometry.getNumGeometries(); i++) {

			if (geometry.getGeometryType() == "MultiPolygon") {
				MultiPolygon polygons = (MultiPolygon) geometry;
				AddPolygonInGeometries(polygons);
			}

			if (geometry.getGeometryType() == "Polygon") {

				Polygon polygon = (Polygon) geometry;
			}

		}
	}

	private void AddPolygonInGeometries(MultiPolygon polygons) {


		int N = polygons.getNumGeometries();
		float scale_rate=0.01f;
		float z=-5.0f;

		// for each polygon of a multipolygon, get each point coordinates.
		for (int i = 0; i < N; i++) {

			Polygon p = (Polygon) polygons.getGeometryN(i);

			int numExtPoints = p.getExteriorRing().getNumPoints();
			MyGeometry curGeometry= new MyGeometry(numExtPoints);
			
			// Get exterior ring (Be sure not to exceed the
			// number of point of the exterior ring)
			for (int j = 0; j < numExtPoints; j++) {
				curGeometry.vertices[j].x = (float) ((p
						.getExteriorRing().getPointN(j).getX()) * scale_rate);
				curGeometry.vertices[j].y = (float) ((p
						.getExteriorRing().getPointN(j).getY()) * scale_rate);
				curGeometry.vertices[j].z = z;
				curGeometry.vertices[j].u = 6.0f + (float) j;
				curGeometry.vertices[j].v = 0.0f + (float) j;
			}
			System.out.println("DisplayGraphics::AddPolygonInGeometries");
			this.myGeometries.add(curGeometry);
		}
	}
	
	
	public void DrawMyGeometries(){

		System.out.println("I have to draw " + myGeometries.size() + " geometries" );
		Iterator it=this.myGeometries.iterator();

        while(it.hasNext())
        {
        	MyGeometry curGeometry= (MyGeometry) it.next();
        	graphicsGLUtils.DrawGeometry(myGl, myGlu, curGeometry, 0.0f);
        }
	}



}
