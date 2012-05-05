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

import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLES;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLException;
import javax.media.opengl.Threading;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.*;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.Threading;
import com.sun.opengl.util.texture.*;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.jogl.utils.*;
import msi.gaml.operators.Maths;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.jfree.chart.JFreeChart;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.TextureIO;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.quadtree.IntervalSize;
import com.vividsolutions.jts.awt.ShapeReader;

/**
 * 
 * Simplifies the drawing of circles, rectangles, and so forth. Rectangles are
 * generally faster to draw than circles. The Displays should take care of
 * layouts while objects that wish to be drawn as a shape need only call the
 * appropriate method.
 * 
 * @author Nick Collier, Alexis Drogoul, Patrick Taillandier
 * @version $Revision: 1.13 $ $Date: 2010-03-19 07:12:24 $
 */

public class JOGLAWTDisplayGraphics implements IGraphics {

	boolean ready = false;
	private Rectangle clipping;
	private final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	private final Ellipse2D oval = new Ellipse2D.Double(0, 0, 1, 1);
	private final Line2D line = new Line2D.Double();
	private double currentAlpha = 1;
	private int displayWidth, displayHeight, curX = 0, curY = 0, curWidth = 5,
			curHeight = 5, offsetX = 0, offsetY = 0;
	// private static RenderingHints rendering;
	private static final Font defaultFont = new Font("Helvetica", Font.PLAIN,
			12);

	// OpenGL member
	private GL myGl;
	private GLU myGlu;

	// Handle opengl primitive.
	public MyGraphics graphicsGLUtils;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	// Each geometry drawn is stored in a list.
	public ArrayList<MyGeometry> myGeometries = new ArrayList<MyGeometry>();

	// each Image is stored in a list
	public ArrayList<MyImage> myImages = new ArrayList<MyImage>();

	// Define the environment properties.
	public float myWidth, myHeight, myMaxDim;

	// All the geometry are drawn in the same z plan (depend on the sale rate).
	public float z;

	// OpenGL list ID
	private int listID = -1;

	private final PointTransformation pt = new PointTransformation() {

		@Override
		public void transform(final Coordinate c, final Point2D p) {
			//int xp = offsetX + (int) (currentXScale * c.x + 0.5);
			//int yp = offsetY + (int) (currentYScale * c.y + 0.5);
			//p.setLocation(xp, yp);
		}
	};
	private final ShapeWriter sw = new ShapeWriter(pt);

	/**
	 * Constructor for OpenGL DisplayGraphics constructor. Simplify for opengl
	 * display. The environment property are given from the display surface.
	 * 
	 * @param GL
	 *            gl
	 * @param GLU
	 *            glu
	 * @param float env_width
	 * @param float env_height
	 * @param float scale_rate
	 */
	public JOGLAWTDisplayGraphics(final GL gl, final GLU glu,
			JOGLAWTGLRenderer gLRender, float env_width, float env_height) {
		myGl = gl;
		myGlu = glu;
		myWidth = env_width;
		myHeight = env_height;

		if (myWidth > myHeight) {
			myMaxDim = myWidth;
		} else {
			myMaxDim = myHeight;
		}

		z = 0.0f;
		graphicsGLUtils = new MyGraphics();

		myGLRender = gLRender;
	}

	/**
	 * Method setGraphics.
	 * 
	 * @param g
	 *            Graphics2D
	 */
	@Override
	public void setGraphics(final Graphics2D g) {
		ready = true;
		setQualityRendering(false);
	}

	@Override
	public void setQualityRendering(final boolean quality) {
	
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	/**
	 * Method setComposite.
	 * 
	 * @param alpha
	 *            AlphaComposite
	 */
	@Override
	public void setOpacity(final double alpha) {
		// 1 means opaque ; 0 means transparent
		if (IntervalSize.isZeroWidth(alpha, currentAlpha)) {
			return;
		}
		currentAlpha = alpha;
	}

	/**
	 * Method getDisplayWidth.
	 * 
	 * @return int
	 */
	@Override
	public int getDisplayWidth() {
		return displayWidth;
	}

	/**
	 * Method getDisplayHeight.
	 * 
	 * @return int
	 */
	@Override
	public int getDisplayHeight() {
		return displayHeight;
	}

	/**
	 * Method setDisplayDimensions.
	 * 
	 * @param width
	 *            int
	 * @param height
	 *            int
	 */
	@Override
	public void setDisplayDimensions(final int width, final int height) {
		displayWidth = width;
		displayHeight = height;
	}

	/**
	 * Method setFont.
	 * 
	 * @param font
	 *            Font
	 */
	@Override
	public void setFont(final Font font) {
		
	}

	/**
	 * Method getXScale.
	 * 
	 * @return double
	 */
	@Override
	public double getXScale() {
		return 1;
	}

	/**
	 * Method setXScale.
	 * 
	 * @param scale
	 *            double
	 */
	@Override
	public void setXScale(final double scale) {
		//this.currentXScale = scale;
	}

	/**
	 * Method getYScale.
	 * 
	 * @return double
	 */
	@Override
	public double getYScale() {
		return 1;
	}

	/**
	 * Method setYScale.
	 * 
	 * @param scale
	 *            double
	 */
	@Override
	public void setYScale(final double scale) {
		//this.currentYScale = scale;
	}

	/**
	 * Method setDrawingCoordinates.
	 * 
	 * @param x
	 *            double
	 * @param y
	 *            double
	 */
	@Override
	public void setDrawingCoordinates(final double x, final double y) {
		curX = (int) x + offsetX;
		curY = (int) y + offsetY;
	}

	/**
	 * Method setDrawingOffset.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	@Override
	public void setDrawingOffset(final int x, final int y) {
		offsetX = x;
		offsetY = y;
	}

	/**
	 * Method setDrawingDimensions.
	 * 
	 * @param width
	 *            int
	 * @param height
	 *            int
	 */
	@Override
	public void setDrawingDimensions(final int width, final int height) {
		curWidth = width;
		curHeight = height;
	}

	/**
	 * Method setDrawingColor.
	 * 
	 * @param c
	 *            Color
	 */
	private void setDrawingColor(final Color c) {
	
	}

	
	////////// draw method ///////////////
	
	/**
	 * Method drawGeometry.Draw a given JTS Geometry inside an openGl context
	 * 
	 * @param geometry
	 *            Geometry
	 * @param color
	 *            Color
	 * @param fill
	 *            boolean
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawGeometry(final Geometry geometry, final Color color,
			final boolean fill, final Integer angle) {
       // System.out.println("DisplayGraphics::drawGeometry: "
		 //+ geometry.getGeometryType());
		AddGeometryInGeometries(geometry, color);
		return sw.toShape(geometry).getBounds2D();
	}
	
	@Override
	public void drawGrid(BufferedImage image, Color lineColor,
			java.awt.Point point) {
			
		GeometryFactory geometryFactory = JTSFactoryFinder
				.getGeometryFactory(null);
		double step;
		for ( int i = 0; i <= image.getWidth();  i++ ) {		
			step = (i / (double) image.getWidth())*(double) image.getWidth();
			Coordinate[] coords = new Coordinate[] {
					new Coordinate(step , 0),
					new Coordinate((step),(image.getWidth()) ) };	
			AddLineInGeometries(geometryFactory.createLineString(coords), lineColor);

		}
	
		for ( int i = 0; i<= image.getHeight(); i++ ) {
			step = (i / (double) image.getHeight())*(double) image.getHeight();			
			Coordinate[] coords = new Coordinate[] {
					new Coordinate(0 , step),
					new Coordinate((image.getHeight()),(step) ) };
			AddLineInGeometries(geometryFactory.createLineString(coords), lineColor);
		}
		
	}
	
	/**
	 * Method drawImage.
	 * 
	 * @param img
	 *            Image
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawImage(final BufferedImage img, final Integer angle,
			final boolean smooth) {

		AddImageInImages(img, curX, curY);
		rect.setRect(curX, curY, curWidth, curHeight);
		return rect.getBounds2D();
	}

	@Override
	public Rectangle2D drawImage(final BufferedImage img, final Integer angle) {
		return drawImage(img, angle, true);
	}

	/**
	 * Method drawChart.
	 * 
	 * @param chart
	 *            JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final JFreeChart chart) {
		rect.setRect(curX, curY, curWidth, curHeight);
		return rect.getBounds2D();
	}

	/**
	 * Method drawCircle.
	 * 
	 * @param c
	 *            Color
	 * @param fill
	 *            boolean
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawCircle(final Color c, final boolean fill,
			final Integer angle) {
		System.out.println("DrawCircle");
		AddCircleInGeometries(curX + curWidth / 2, curY + curWidth / 2, c,
				curWidth);
		oval.setFrame(curX, curY, curWidth, curWidth);
		return oval.getBounds2D();
	}

	@Override
	public Rectangle2D drawTriangle(final Color c, final boolean fill,
			final Integer angle) {
		// curWidth is equal to half the width of the triangle
		final GeneralPath p0 = new GeneralPath();
		// double dist = curWidth / (2 * Math.sqrt(2.0));
		p0.moveTo(curX, curY + curWidth);
		p0.lineTo(curX + curWidth / 2.0, curY);
		p0.lineTo(curX + curWidth, curY + curWidth);
		p0.closePath();
		Rectangle2D r = null;
		return r;
	}

	/**
	 * Method 
.
	 * 
	 * @param c
	 *            Color
	 * @param toX
	 *            double
	 * @param toY
	 *            double
	 */
	@Override
	public Rectangle2D drawLine(final Color c, final double toX,
			final double toY) {

		System.out.println("drawline" + "curX: " +curX+ "toX"+toX+"curY:"+curY+"toY"+toY);
		GeometryFactory geometryFactory = JTSFactoryFinder
				.getGeometryFactory(null);

		Coordinate[] coords = new Coordinate[] {
				new Coordinate(curX , curY),
				new Coordinate((toX),(toY) ) };

		LineString lineString = geometryFactory.createLineString(coords);
		AddLineInGeometries(lineString, c);

		line.setLine(curX, curY, toX + offsetX, toY + offsetY);
		return line.getBounds2D();
	}

	/**
	 * Method drawRectangle.
	 * 
	 * @param color
	 *            Color
	 * @param fill
	 *            boolean
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawRectangle(final Color color, final boolean fill,
			final Integer angle) {
		// System.out.println("JOGLDisplayGraphics::drawRectangle");
		rect.setFrame(curX, curY, curWidth, curHeight);
		return rect.getBounds2D();
	}

	/**
	 * Method drawString.
	 * 
	 * @param string
	 *            String
	 * @param stringColor
	 *            Color
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawString(final String string, final Color stringColor,
			final Integer angle) {
		setDrawingColor(stringColor);
		Rectangle2D r = null;
		return r;
	}
	
	@Override
	public void fill(final Color bgColor, final double opacity) {
		setOpacity(opacity);
	}

	@Override
	public void setClipping(final Rectangle imageClipBounds) {
		clipping = imageClipBounds;
	}

	@Override
	public Rectangle getClipping() {
		return clipping;
	}
	
	/////////////////Add method ////////////////////////////
	
	private void AddGeometryInGeometries(Geometry geometry, Color color) {
		// For each geometry get the type
		// System.out.println("AddGeometryInGeometries: "
		// + geometry.getGeometryType());
		for (int i = 0; i < geometry.getNumGeometries(); i++) {
			Envelope e = geometry.getEnvelopeInternal();
			if (geometry.getGeometryType() == "MultiPolygon") {
				MultiPolygon polygons = (MultiPolygon) geometry;
				AddMultiPolygonInGeometries(polygons, color);
			}

			else if (geometry.getGeometryType() == "Polygon") {
				Polygon polygon = (Polygon) geometry;
				AddPolygonInGeometries(polygon, color);
			}

			else if (geometry.getGeometryType() == "MultiLineString") {
				MultiLineString lines = (MultiLineString) geometry;
				AddMultiLineInGeometries(lines, color);
			}

			else if (geometry.getGeometryType() == "LineString") {
				LineString lines = (LineString) geometry;
				AddLineInGeometries(lines, color);
			}

			else if (geometry.getGeometryType() == "Point") {
				Point point = (Point) geometry;
				AddPointInGeometries(point, color);
			}
		}
	}

	private void AddMultiPolygonInGeometries(final MultiPolygon polygons,
			final Color color) {

		int N = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for (int i = 0; i < N; i++) {

			Polygon p = (Polygon) polygons.getGeometryN(i);
			int numExtPoints = p.getExteriorRing().getNumPoints();
			MyGeometry curGeometry = new MyGeometry(numExtPoints);

			// Get exterior ring (Be sure not to exceed the
			// number of point of the exterior ring)

			for (int j = 0; j < numExtPoints; j++) {
				curGeometry.vertices[j].x = (float) (p.getExteriorRing()
						.getPointN(j).getX());
				curGeometry.vertices[j].y = -(float) (p.getExteriorRing()
						.getPointN(j).getY());
				curGeometry.vertices[j].z = z;
				curGeometry.vertices[j].u = 6.0f + j;
				curGeometry.vertices[j].v = 0.0f + j;
			}
			curGeometry.color = color;
			curGeometry.type = "MultiPolygon";

			this.myGeometries.add(curGeometry);
		}
	}

	private void AddPolygonInGeometries(final Polygon polygon, final Color color) {

		int numExtPoints = polygon.getExteriorRing().getNumPoints();
		MyGeometry curGeometry = new MyGeometry(numExtPoints);
		for (int j = 0; j < numExtPoints; j++) {
			curGeometry.vertices[j].x = (float) (polygon.getExteriorRing()
					.getPointN(j).getX());
			curGeometry.vertices[j].y = -(float) (polygon.getExteriorRing()
					.getPointN(j).getY());
			curGeometry.vertices[j].z = z;
			curGeometry.vertices[j].u = 6.0f + j;
			curGeometry.vertices[j].v = 0.0f + j;
		}
		curGeometry.color = color;
		curGeometry.type = "Polygon";
		this.myGeometries.add(curGeometry);
	}

	private void AddMultiLineInGeometries(final MultiLineString lines,
			final Color color) {
		
		// get the number of line in the multiline.
		int N = lines.getNumGeometries();

		// for each line of a multiline, get each point coordinates.
		for (int i = 0; i < N; i++) {

			LineString l = (LineString) lines.getGeometryN(i);
			int numPoints = l.getNumPoints();
			MyGeometry curGeometry = new MyGeometry(numPoints);
			for (int j = 0; j < numPoints; j++) {

				curGeometry.vertices[j].x = (float) (l.getPointN(j).getX());
				curGeometry.vertices[j].y = -(float) (l.getPointN(j).getY());
				curGeometry.vertices[j].z = z;
				curGeometry.vertices[j].u = 0.0f;
				curGeometry.vertices[j].v = 0.0f;
			}
			curGeometry.color = color;
			curGeometry.type = "MultiLineString";
			this.myGeometries.add(curGeometry);
		}

	}

	private void AddLineInGeometries(final LineString line, final Color color) {

		int numPoints = line.getNumPoints();
		MyGeometry curGeometry = new MyGeometry(numPoints);
		for (int j = 0; j < numPoints; j++) {
			curGeometry.vertices[j].x = (float) (line.getPointN(j).getX());
			curGeometry.vertices[j].y = -(float) (line.getPointN(j).getY());
			curGeometry.vertices[j].z = z;
			curGeometry.vertices[j].u = 0.0f;
			curGeometry.vertices[j].v = 0.0f;
		}
		curGeometry.color = color;
		curGeometry.type = "LineString";
		this.myGeometries.add(curGeometry);

	}

	private void AddPointInGeometries(final Point point, final Color color) {
		MyGeometry curGeometry = new MyGeometry(1);
		curGeometry.vertices[0].x = (float) (point.getCoordinate().x);
		curGeometry.vertices[0].y = -(float) (point.getCoordinate().y);
		curGeometry.vertices[0].z = z;
		curGeometry.vertices[0].u = 6.0f;
		curGeometry.vertices[0].v = 0.0f;
		// A point size is equal to 1% of the max size of the environment.
		curGeometry.size = 0.01f * this.myMaxDim;
		curGeometry.color = color;
		curGeometry.type = "Point";

		this.myGeometries.add(curGeometry);

	}

	private void AddCircleInGeometries(int x, int y, Color color, int size) {
		MyGeometry curGeometry = new MyGeometry(1);
		// FIXME: why we need to use currentScale?
		curGeometry.vertices[0].x = (float) ((float) x);
		curGeometry.vertices[0].y = (float) (-(float) y);
		curGeometry.vertices[0].z = z;
		curGeometry.vertices[0].u = 6.0f;
		curGeometry.vertices[0].v = 0.0f;
		curGeometry.color = color;
		curGeometry.size = (float) (size);
		curGeometry.type = "Circle";
		this.myGeometries.add(curGeometry);

	}

	private void AddImageInImages(final BufferedImage img, int curX, int curY) {

		final MyImage curImage = new MyImage();
		curImage.image = img;
		curImage.x = (float) (curX );
		curImage.y = (float) (curY);
		myGLRender.InitTexture(img);
		this.myImages.add(curImage);		
		
	}

	///////////////// Draw Method ////////////////////
	
	public void DrawMyGeometries() {

		Iterator<MyGeometry> it = this.myGeometries.iterator();
		while (it.hasNext()) {
			MyGeometry curGeometry = (MyGeometry) it.next();
			// Color are define in RGB value (from 0 to 255) openGl use value
			// from 0 to 1.
			myGl.glColor3f((float) curGeometry.color.getRed() / 255,
					(float) curGeometry.color.getGreen() / 255,
					(float) curGeometry.color.getBlue() / 255);
			// System.out.println("R:"+(float)curGeometry.color.getRed()/255+"G:"+(float)curGeometry.color.getGreen()/255+"B:"+(float)curGeometry.color.getBlue()/255);
			if (curGeometry.type == "MultiPolygon"
					|| curGeometry.type == "Polygon") {

				if (myGLRender.displaySurface.ThreeD == true) {
					myGl.glColor3f(0.0f, 0.0f, 1.0f);
					// top face
					float z_offset = 100.0f;
					graphicsGLUtils.DrawGeometry(myGl, myGlu, curGeometry,
							z_offset);
					// base face
					graphicsGLUtils
							.DrawGeometry(myGl, myGlu, curGeometry, 0.0f);
					// all the front-face of the polygons as a verticle quads.
					graphicsGLUtils.Draw3DQuads(myGl, myGlu, curGeometry,
							z_offset);
				} else {
					graphicsGLUtils
							.DrawGeometry(myGl, myGlu, curGeometry, 0.0f);
				}

			} else if (curGeometry.type == "MultiLineString"
					|| curGeometry.type == "LineString") {
				graphicsGLUtils.DrawLine(myGl, myGlu, curGeometry, 1.2f);
			} else if (curGeometry.type == "Point"
					|| curGeometry.type == "Circle") {
				graphicsGLUtils.DrawCircle(myGl, myGlu,
						curGeometry.vertices[0].x, curGeometry.vertices[0].y,
						curGeometry.vertices[0].z, 20, curGeometry.size / 2);

			}

		}
	}

	public void DrawMyImages() {
		//Iterator<Texture> it = myGLRender.myTextures.iterator();
		Iterator<MyImage> it = this.myImages.iterator();
		int id = 0;
		while (it.hasNext()) {
			MyImage curImage = (MyImage) it.next();
			//Texture curText = it.next();
			myGLRender.DrawTexture(id, curImage);
			id++;
		}
	}
	
	public void DrawEnvironmentBounds() {

		float alpha = 0.0f;
		myGl.glColor4f(0.0f, 0.0f, 1.0f, alpha);

		GLUT glut = new GLUT();
		myGl.glRasterPos3f(this.myWidth / 2, this.myHeight * 0.01f, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
				String.valueOf(this.myWidth));

		myGl.glRasterPos3f(this.myWidth * 1.01f, -(this.myHeight / 2), 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
				String.valueOf(this.myHeight));

		MyGeometry backgroundGeometry = new MyGeometry(4);

		backgroundGeometry.vertices[0].x = 0;
		// WARNING: Opengl Y axes is inversed!!!
		backgroundGeometry.vertices[0].y = 0.0f;
		backgroundGeometry.vertices[0].z = z;
		backgroundGeometry.vertices[0].u = 6.0f;
		backgroundGeometry.vertices[0].v = 0.0f;

		backgroundGeometry.vertices[1].x = myWidth;
		// WARNING: Opengl Y axes is inversed!!!
		backgroundGeometry.vertices[1].y = 0.0f;
		backgroundGeometry.vertices[1].z = z;
		backgroundGeometry.vertices[1].u = 6.0f;
		backgroundGeometry.vertices[1].v = 0.0f;

		backgroundGeometry.vertices[2].x = myWidth;
		// WARNING: Opengl Y axes is inversed!!!
		backgroundGeometry.vertices[2].y = -(float) (myHeight);
		backgroundGeometry.vertices[2].z = z;
		backgroundGeometry.vertices[2].u = 6.0f;
		backgroundGeometry.vertices[2].v = 0.0f;

		backgroundGeometry.vertices[3].x = 0.0f;
		// WARNING: Opengl Y axes is inversed!!!
		backgroundGeometry.vertices[3].y = -(float) (myHeight);
		backgroundGeometry.vertices[3].z = z;
		backgroundGeometry.vertices[3].u = 6.0f;
		backgroundGeometry.vertices[3].v = 0.0f;

		graphicsGLUtils.DrawGeometry(myGl, myGlu, backgroundGeometry, 0.0f);
	}

	public void DrawXYZAxis(float size) {
		GLUT glut = new GLUT();

		myGl.glRasterPos3f(size, size, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
				"1:" + String.valueOf(size));
		// X Axis
		myGl.glRasterPos3f(1.2f * size, 0.0f, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "x");
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor3f(size, 0, 0);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(size, 0, 0);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(1.0f * size, 0.05f * size, 0.0f);
		myGl.glVertex3f(1.0f * size, -0.05f * size, 0.0f);
		myGl.glVertex3f(1.1f * size, 0.0f, 0.0f);
		myGl.glEnd();

		// Y Axis
		myGl.glRasterPos3f(0.0f, 1.2f * size, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "y");
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor3f(0, size, 0);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(0, size, 0);
		myGl.glEnd();
		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(-0.05f * size, 1f * size, 0.0f);
		myGl.glVertex3f(0.05f, 1f * size, 0.0f);
		myGl.glVertex3f(0.0f, 1.1f * size, 0.0f);
		myGl.glEnd();

		// Z Axis
		myGl.glRasterPos3f(0.0f, 0.0f, 1.2f * size);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "z");
		myGl.glBegin(GL.GL_LINES);
		myGl.glColor3f(0, 0, size);
		myGl.glVertex3f(0, 0, 0);
		myGl.glVertex3f(0, 0, size);
		myGl.glEnd();

		myGl.glBegin(GL_TRIANGLES);
		myGl.glVertex3f(0.0f, 0.05f * size, 1.0f * size);
		myGl.glVertex3f(0.0f, -0.05f * size, 1.0f * size);
		myGl.glVertex3f(0.0f, 0.0f, 1.1f * size);
		myGl.glEnd();

	}

	public void DrawScale() {
		GLUT glut = new GLUT();
		// Draw Scale
		float y_text_pos = -(this.myHeight + this.myHeight * 0.05f);
		myGl.glRasterPos3f(0.0f, y_text_pos * 0.99f, 0.0f);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "Scale:" + "1");
		myGl.glBegin(GL.GL_LINES);
		myGl.glVertex3f(0, 0 + y_text_pos, 0);
		myGl.glVertex3f(this.myWidth * 0.05f, 0 + y_text_pos, 0);

		myGl.glVertex3f(0, -0.05f + y_text_pos, 0);
		myGl.glVertex3f(0, 0.05f + y_text_pos, 0);

		myGl.glVertex3f(1, -0.05f + y_text_pos, 0);
		myGl.glVertex3f(1, 0.05f + y_text_pos, 0);
		myGl.glEnd();
	}
	
	
	public void CleanGeometries() {
		this.myGeometries.clear();
	}

	public void CleanImages() {
		this.myImages.clear();
		myGLRender.myTextures.clear();
	}
	
	public void draw(GL gl) {

		if (listID == -1) {
			createDisplayList(gl);
		}
		gl.glCallList(listID);

	}

	private void createDisplayList(GL gl) {
		GLU glu = new GLU();
		GLUquadric quadric = glu.gluNewQuadric();
		listID = gl.glGenLists(1);
		gl.glNewList(listID, GL.GL_COMPILE);
		gl.glPushMatrix();
		gl.glColor3d(1, 1, 1);
		glu.gluSphere(quadric, 1 + 0.4, 30, 30);
		gl.glPopMatrix();
		gl.glEndList();
	}



}
