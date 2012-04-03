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
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_POLYGON;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.*;
//import java.awt.Point;
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
import com.vividsolutions.jts.geom.Point;
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

public class JOGLAWTDisplayGraphics implements IGraphics {

	boolean ready = false;
	private Graphics2D g2;
	private Rectangle clipping;
	private final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	private final Ellipse2D oval = new Ellipse2D.Double(0, 0, 1, 1);
	private final Line2D line = new Line2D.Double();
	private double currentAlpha = 1;
	private int displayWidth, displayHeight, curX = 0, curY = 0, curWidth = 5,
			curHeight = 5, offsetX = 0, offsetY = 0;
	private double currentXScale = 1, currentYScale = 1;
	// private static RenderingHints rendering;
	private static final Font defaultFont = new Font("Helvetica", Font.PLAIN,
			12);

	// OpenGL memeber
	private GL myGl;
	private GLU myGlu;

	// Each geometry drawn is stored in a tab.
	public ArrayList<MyGeometry> myGeometries = new ArrayList<MyGeometry>();
	// Use to compute the bound enveloppe but it should be available for the
	// environment.
	public float environmentMaxBound_X = 0.0f;
	public float environmentMaxBound_Y = 0.0f;
	// Scale rate will be computed gien the bound dimension.
	public float scale_rate = 0.0f;
	// By default all the geometry are drawn in this z plan (-10.0f).
	public float z = -10.0f;
	// Rectangle representing the bound
	public Rectangle clipBounds;

	// Handle openg gl primitive.
	public MyGraphics graphicsGLUtils;

	static {

		QUALITY_RENDERING.put(KEY_RENDERING, VALUE_RENDER_QUALITY);
		QUALITY_RENDERING.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY);
		QUALITY_RENDERING.put(KEY_ALPHA_INTERPOLATION,
				VALUE_ALPHA_INTERPOLATION_QUALITY);
		QUALITY_RENDERING.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
		QUALITY_RENDERING.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

		MEDIUM_RENDERING.put(KEY_RENDERING, VALUE_RENDER_QUALITY);
		MEDIUM_RENDERING.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED);
		MEDIUM_RENDERING.put(KEY_ALPHA_INTERPOLATION,
				VALUE_ALPHA_INTERPOLATION_QUALITY);
		MEDIUM_RENDERING.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
		MEDIUM_RENDERING.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

		SPEED_RENDERING.put(KEY_RENDERING, VALUE_RENDER_SPEED);
		SPEED_RENDERING.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED);
		SPEED_RENDERING.put(KEY_ALPHA_INTERPOLATION,
				VALUE_ALPHA_INTERPOLATION_SPEED);
		SPEED_RENDERING.put(KEY_INTERPOLATION,
				VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		SPEED_RENDERING.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);

	}

	private final PointTransformation pt = new PointTransformation() {

		@Override
		public void transform(final Coordinate c, final Point2D p) {
			int xp = offsetX + (int) (currentXScale * c.x + 0.5);
			int yp = offsetY + (int) (currentYScale * c.y + 0.5);
			p.setLocation(xp, yp);
		}
	};
	private final ShapeWriter sw = new ShapeWriter(pt);

	/**
	 * Constructor for DisplayGraphics.
	 * 
	 * @param BufferedImage
	 *            image
	 */
	JOGLAWTDisplayGraphics(final BufferedImage image) {
		System.out.println("JOGLAWTDisplayGraphics(image) constructor");
		setDisplayDimensions(image.getWidth(), image.getHeight());
		setGraphics((Graphics2D) image.getGraphics());

	}

	/**
	 * Constructor for OpenGL DisplayGraphics. Based on the constructor used for Java2D
	 * 
	 * @param BufferedImage
	 *            image
	 * @param GL
	 *            gl
	 * @param GLU
	 * 			  glu
	 */
	public JOGLAWTDisplayGraphics(final BufferedImage image, GL gl, GLU glu) {
		setDisplayDimensions(image.getWidth(), image.getHeight());
		setGraphics((Graphics2D) image.getGraphics());
		myGl = gl;
		myGlu = glu;
		graphicsGLUtils = new MyGraphics();
	}
	
	
	/**
	 * Constructor for OpenGL DisplayGraphics. Simplify for opengl display (We don't need the image as a parameter).

	 * @param GL
	 *            gl
	 * @param GLU
	 * 			  glu
	 */
	public JOGLAWTDisplayGraphics(GL gl, GLU glu) {
		myGl = gl;
		myGlu = glu;
		graphicsGLUtils = new MyGraphics();
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
		g2 = g;
		setQualityRendering(false);
		g2.setFont(defaultFont);
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		if (g2 != null) {
			g2.setRenderingHints(quality ? QUALITY_RENDERING : SPEED_RENDERING);
		}
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
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				(float) alpha));
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
		g2.setFont(font);
	}

	/**
	 * Method getXScale.
	 * 
	 * @return double
	 */
	@Override
	public double getXScale() {
		return currentXScale;
	}

	/**
	 * Method setXScale.
	 * 
	 * @param scale
	 *            double
	 */
	@Override
	public void setXScale(final double scale) {
		this.currentXScale = scale;
	}

	/**
	 * Method getYScale.
	 * 
	 * @return double
	 */
	@Override
	public double getYScale() {
		return currentYScale;
	}

	/**
	 * Method setYScale.
	 * 
	 * @param scale
	 *            double
	 */
	@Override
	public void setYScale(final double scale) {
		this.currentYScale = scale;
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
		if (g2 != null && g2.getColor() != c) {
			g2.setColor(c);
		}
	}

	// private final AffineTransform at = new AffineTransform();

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
		AffineTransform saved = g2.getTransform();
		if (angle != null) {
			g2.rotate(Maths.toRad * angle, curX + curWidth / 2, curY
					+ curHeight / 2);
		}
		g2.drawImage(img, curX, curY, curWidth, curHeight, null);
		g2.setTransform(saved);
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
		Graphics2D g3 = (Graphics2D) g2.create();
		chart.draw(g3, rect);
		g3.dispose();
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
		oval.setFrame(curX, curY, curWidth, curWidth);
		return drawShape(c, oval, fill, angle);
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
		return drawShape(c, p0, fill, angle);
	}

	/**
	 * Method drawLine.
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
		line.setLine(curX, curY, toX + offsetX, toY + offsetY);
		return drawShape(c, line, false, null);
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
		System.out.println("JOGLDisplayGraphics::drawRectangle");
		rect.setFrame(curX, curY, curWidth, curHeight);
		return drawShape(color, rect, fill, angle);
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
		AffineTransform saved = g2.getTransform();
		if (angle != null) {
			Rectangle2D r = g2.getFontMetrics().getStringBounds(string, g2);
			g2.rotate(Maths.toRad * angle, curX + r.getWidth() / 2,
					curY + r.getHeight() / 2);
		}
		g2.drawString(string, curX, curY);
		g2.setTransform(saved);
		return g2.getFontMetrics().getStringBounds(string, g2);
	}

	/**
	 * Method drawGeometry.
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
		System.out.println("DisplayGraphics::drawGeometry");
		boolean f = geometry instanceof LineString
				|| geometry instanceof MultiLineString ? false : fill;

		drawGeometryGL(geometry, color);
		return drawShape(color, sw.toShape(geometry), f, angle);
	}

	/**
	 * Method drawShape.
	 * 
	 * @param c
	 *            Color
	 * @param s
	 *            Shape
	 * @param fill
	 *            boolean
	 * @param angle
	 *            Integer
	 */
	@Override
	public Rectangle2D drawShape(final Color c, final Shape s,
			final boolean fill, final Integer angle) {
		try {
			Rectangle2D r = s.getBounds2D();
			AffineTransform saved = g2.getTransform();
			if (angle != null) {
				g2.rotate(Maths.toRad * angle, r.getX() + r.getWidth() / 2,
						r.getY() + r.getHeight() / 2);
			}

			setDrawingColor(c);
			if (fill) {
				g2.fill(s);
				setDrawingColor(Color.black);
			}
			g2.draw(s);
			g2.setTransform(saved);
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void fill(final Color bgColor, final double opacity) {
		setOpacity(opacity);
		g2.setColor(bgColor);
		g2.fillRect(0, 0, displayWidth, displayHeight);
	}

	@Override
	public void setClipping(final Rectangle imageClipBounds) {
		clipping = imageClipBounds;
		g2.setClip(imageClipBounds);
	}

	@Override
	public Rectangle getClipping() {
		return clipping;
	}

	/**
	 * Draw a given JTS Geometry inside an openGl context
	 * 
	 * @param geometry
	 *            The geometry to draw
	 * @param color
	 *            color of the geometry.
	 */
	public void drawGeometryGL(final Geometry geometry, final Color color) {
		System.out.println("drawGeometryGL: " + geometry.getGeometryType()
				+ " geometry.getNumGeometries()" + geometry.getNumGeometries());

		// update bound property.
		if (environmentMaxBound_X < geometry.getCentroid().getCoordinate().x) {
			environmentMaxBound_X = (float) geometry.getCentroid()
					.getCoordinate().x;
		}
		if (environmentMaxBound_Y < geometry.getCentroid().getCoordinate().y) {
			environmentMaxBound_Y = (float) geometry.getCentroid()
					.getCoordinate().y;
		}

		if (environmentMaxBound_X > environmentMaxBound_Y) {
			scale_rate = 10 / environmentMaxBound_X;
		} else {
			scale_rate = 10 / environmentMaxBound_Y;
		}

		// A geometry can contain several geometry
		for (int i = 0; i < geometry.getNumGeometries(); i++) {

			if (geometry.getGeometryType() == "MultiPolygon") {
				MultiPolygon polygons = (MultiPolygon) geometry;
				AddMultiPolygonInGeometries(polygons, color);
			}

			if (geometry.getGeometryType() == "Polygon") {
				Polygon polygon = (Polygon) geometry;

				// AddPolygonInGeometries(polygon, color);

			}

			if (geometry.getGeometryType() == "Point") {
				Point point = (Point) geometry;
				AddPointInGeometries(point, color);

			}

		}
	}

	private void AddMultiPolygonInGeometries(MultiPolygon polygons, Color color) {

		int N = polygons.getNumGeometries();

		// for each polygon of a multipolygon, get each point coordinates.
		for (int i = 0; i < N; i++) {

			Polygon p = (Polygon) polygons.getGeometryN(i);
			int numExtPoints = p.getExteriorRing().getNumPoints();
			MyGeometry curGeometry = new MyGeometry(numExtPoints);

			// Get exterior ring (Be sure not to exceed the
			// number of point of the exterior ring)

			for (int j = 0; j < numExtPoints; j++) {
				curGeometry.vertices[j].x = (float) ((p.getExteriorRing()
						.getPointN(j).getX()));
				// WARNING: Opengl Y axes is inversed!!!
				curGeometry.vertices[j].y = -(float) ((p.getExteriorRing()
						.getPointN(j).getY()));
				curGeometry.vertices[j].z = z;
				curGeometry.vertices[j].u = 6.0f + (float) j;
				curGeometry.vertices[j].v = 0.0f + (float) j;
			}
			curGeometry.color = color;
			curGeometry.type = "MultiPolygon";

			this.myGeometries.add(curGeometry);
		}
	}

	private void AddPolygonInGeometries(Polygon polygon, Color color) {

		int numExtPoints = polygon.getExteriorRing().getNumPoints();
		MyGeometry curGeometry = new MyGeometry(numExtPoints);
		for (int j = 0; j < numExtPoints; j++) {
			curGeometry.vertices[j].x = (float) ((polygon.getExteriorRing()
					.getPointN(j).getX()));
			// WARNING: Opengl Y axes is inversed!!!
			curGeometry.vertices[j].y = -(float) ((polygon.getExteriorRing()
					.getPointN(j).getY()));
			curGeometry.vertices[j].z = z;
			curGeometry.vertices[j].u = 6.0f + (float) j;
			curGeometry.vertices[j].v = 0.0f + (float) j;
		}
		curGeometry.color = color;
		curGeometry.type = "Polygon";
		this.myGeometries.add(curGeometry);
	}

	private void AddPointInGeometries(Point point, Color color) {
		MyGeometry curGeometry = new MyGeometry(1);
		curGeometry.vertices[0].x = (float) point.getCoordinate().x;
		// WARNING: Opengl Y axes is inversed!!!
		curGeometry.vertices[0].y = -(float) point.getCoordinate().y;
		curGeometry.vertices[0].z = z;
		curGeometry.vertices[0].u = 6.0f;
		curGeometry.vertices[0].v = 0.0f;
		curGeometry.color = color;
		curGeometry.type = "Point";

		this.myGeometries.add(curGeometry);

	}

	public void DrawMyGeometries() {

		Iterator it = this.myGeometries.iterator();

		while (it.hasNext()) {
			MyGeometry curGeometry = (MyGeometry) it.next();
			myGl.glColor3f(curGeometry.color.getRed(),
					curGeometry.color.getGreen(), curGeometry.color.getBlue());
			if (curGeometry.type == "MultiPolygon"
					|| curGeometry.type == "Polygon") {
				graphicsGLUtils.DrawNormalizeGeometry(myGl, myGlu, curGeometry,
						0.0f, scale_rate);
			} else if (curGeometry.type == "Point") {
				graphicsGLUtils.DrawNormalizeCircle(myGl, myGlu,
						curGeometry.vertices[0].x, curGeometry.vertices[0].y,
						curGeometry.vertices[0].z, 10, 5, scale_rate);
			}

		}
	}

	public Rectangle DrawBounds() {

		clipBounds = new Rectangle();
		clipBounds.x = 0;
		clipBounds.y = 0;
		clipBounds.width = (int) environmentMaxBound_X;
		clipBounds.height = (int) environmentMaxBound_Y;
		//System.out.println("clipBounds.getCenterX()" + clipBounds.getCenterX()
			//	+ "clipBounds.getCenterY()" + clipBounds.getCenterY());

		float alpha = 0.9f;
		myGl.glColor4f(1.0f, 1.0f, 1.0f, alpha);

		MyGeometry backgroundGeometry = new MyGeometry(4);

		backgroundGeometry.vertices[0].x = (float) (clipBounds.x);
		// WARNING: Opengl Y axes is inversed!!!
		backgroundGeometry.vertices[0].y = -(float) (clipBounds.y);
		backgroundGeometry.vertices[0].z = z;
		backgroundGeometry.vertices[0].u = 6.0f;
		backgroundGeometry.vertices[0].v = 0.0f;

		backgroundGeometry.vertices[1].x = (float) ((clipBounds.x + clipBounds.width));
		// WARNING: Opengl Y axes is inversed!!!
		backgroundGeometry.vertices[1].y = -(float) (clipBounds.y);
		backgroundGeometry.vertices[1].z = z;
		backgroundGeometry.vertices[1].u = 6.0f;
		backgroundGeometry.vertices[1].v = 0.0f;

		backgroundGeometry.vertices[2].x = (float) ((clipBounds.x + clipBounds.width));
		// WARNING: Opengl Y axes is inversed!!!
		backgroundGeometry.vertices[2].y = -(float) ((clipBounds.y + clipBounds.height));
		backgroundGeometry.vertices[2].z = z;
		backgroundGeometry.vertices[2].u = 6.0f;
		backgroundGeometry.vertices[2].v = 0.0f;

		backgroundGeometry.vertices[3].x = (float) (clipBounds.x);
		// WARNING: Opengl Y axes is inversed!!!
		backgroundGeometry.vertices[3].y = -(float) ((clipBounds.y + clipBounds.height));
		backgroundGeometry.vertices[3].z = z;
		backgroundGeometry.vertices[3].u = 6.0f;
		backgroundGeometry.vertices[3].v = 0.0f;

		graphicsGLUtils.DrawNormalizeGeometry(myGl, myGlu, backgroundGeometry,
				0.0f, scale_rate);
		return clipBounds;

	}

	public void DrawOpenGLHelloWorldShape() {

		float red = (float) (Math.random()) * 1;
		float green = (float) (Math.random()) * 1;
		float blue = (float) (Math.random()) * 1;

		myGl.glColor3f(red, green, blue);
		// ----- Render a triangle -----
		myGl.glTranslatef(-1.5f, 0.0f, -6.0f); // translate left and into the
												// screen

		myGl.glBegin(GL_TRIANGLES); // draw using triangles
		myGl.glVertex3f(0.0f, 1.0f, 0.0f);
		myGl.glVertex3f(-1.0f, -1.0f, 0.0f);
		myGl.glVertex3f(1.0f, -1.0f, 0.0f);
		myGl.glEnd();

		// ----- Render a quad -----

		// translate right, relative to the previous translation
		myGl.glTranslatef(3.0f, 0.0f, 0.0f);

		myGl.glBegin(GL_POLYGON); // draw using quads
		myGl.glVertex3f(-1.0f, 1.0f, 0.0f);
		myGl.glVertex3f(1.0f, 1.0f, 0.0f);
		myGl.glVertex3f(0.0f, 0.0f, 0.0f);
		myGl.glVertex3f(-1.0f, -1.0f, 0.0f);
		myGl.glEnd();

	}

}
