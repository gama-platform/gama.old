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

package msi.gama.gui.displays;

import static java.awt.RenderingHints.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.IGraphics;
import msi.gaml.operators.Maths;
import org.jfree.chart.JFreeChart;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

/**
 * 
 * Simplifies the drawing of circles, rectangles, and so forth. Rectangles are generally faster to
 * draw than circles. The Displays should take care of layouts while objects that wish to be drawn
 * as a shape need only call the appropriate method.
 * <p>
 * 
 * @author Nick Collier, Alexis Drogoul, Patrick Taillandier
 * @version $Revision: 1.13 $ $Date: 2010-03-19 07:12:24 $
 */

public class AWTDisplayGraphics implements IGraphics {

	boolean ready = false;
	private Graphics2D g2;
	private Rectangle clipping;
	private final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	private final Ellipse2D oval = new Ellipse2D.Double(0, 0, 1, 1);
	private final Line2D line = new Line2D.Double();
	private double currentAlpha = 1;
	private int displayWidth, displayHeight, curX = 0, curY = 0, curWidth = 5, curHeight = 5,
		offsetX = 0, offsetY = 0;
	private double currentXScale = 1, currentYScale = 1;
	// private static RenderingHints rendering;
	private static final Font defaultFont = new Font("Helvetica", Font.PLAIN, 12);

	static {

		// System.setProperty("sun.java2d.ddscale", "true");
		// System.setProperty("sun.java2d.accthreshold", "0");
		// System.setProperty("sun.java2d.allowrastersteal", "true");
		// System.setProperty("sun.java2d.opengl", "true");
		// System.setProperty("apple.awt.graphics.UseQuartz", "true");
		QUALITY_RENDERING.put(KEY_RENDERING, VALUE_RENDER_QUALITY);
		QUALITY_RENDERING.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY);
		QUALITY_RENDERING.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
		QUALITY_RENDERING.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
		QUALITY_RENDERING.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

		MEDIUM_RENDERING.put(KEY_RENDERING, VALUE_RENDER_QUALITY);
		MEDIUM_RENDERING.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED);
		MEDIUM_RENDERING.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
		MEDIUM_RENDERING.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
		MEDIUM_RENDERING.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

		SPEED_RENDERING.put(KEY_RENDERING, VALUE_RENDER_SPEED);
		SPEED_RENDERING.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED);
		SPEED_RENDERING.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED);
		SPEED_RENDERING.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
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

	AWTDisplayGraphics(final BufferedImage image) {
		this(image.getWidth(), image.getHeight());
		setGraphics((Graphics2D) image.getGraphics());
	}

	/**
	 * Constructor for DisplayGraphics.
	 * @param width int
	 * @param height int
	 */
	public AWTDisplayGraphics(final int width, final int height) {
		setDisplayDimensions(width, height);
	}

	/**
	 * Method setGraphics.
	 * @param g Graphics2D
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
		if ( g2 != null ) {
			g2.setRenderingHints(quality ? QUALITY_RENDERING : SPEED_RENDERING);
		}
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	/**
	 * Method setComposite.
	 * @param alpha AlphaComposite
	 */
	@Override
	public void setOpacity(final double alpha) {
		// 1 means opaque ; 0 means transparent
		if ( IntervalSize.isZeroWidth(alpha, currentAlpha) ) { return; }
		currentAlpha = alpha;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
	}

	/**
	 * Method getDisplayWidth.
	 * @return int
	 */
	@Override
	public int getDisplayWidth() {
		return displayWidth;
	}

	/**
	 * Method getDisplayHeight.
	 * @return int
	 */
	@Override
	public int getDisplayHeight() {
		return displayHeight;
	}

	/**
	 * Method setDisplayDimensions.
	 * @param width int
	 * @param height int
	 */
	@Override
	public void setDisplayDimensions(final int width, final int height) {
		displayWidth = width;
		displayHeight = height;
	}

	/**
	 * Method setFont.
	 * @param font Font
	 */
	@Override
	public void setFont(final Font font) {
		g2.setFont(font);
	}

	/**
	 * Method getXScale.
	 * @return double
	 */
	@Override
	public double getXScale() {
		return currentXScale;
	}

	/**
	 * Method setXScale.
	 * @param scale double
	 */
	@Override
	public void setXScale(final double scale) {
		this.currentXScale = scale;
	}

	/**
	 * Method getYScale.
	 * @return double
	 */
	@Override
	public double getYScale() {
		return currentYScale;
	}

	/**
	 * Method setYScale.
	 * @param scale double
	 */
	@Override
	public void setYScale(final double scale) {
		this.currentYScale = scale;
	}

	/**
	 * Method setDrawingCoordinates.
	 * @param x double
	 * @param y double
	 */
	@Override
	public void setDrawingCoordinates(final double x, final double y) {
		curX = (int) x + offsetX;
		curY = (int) y + offsetY;
	}

	/**
	 * Method setDrawingOffset.
	 * @param x int
	 * @param y int
	 */
	@Override
	public void setDrawingOffset(final int x, final int y) {
		offsetX = x;
		offsetY = y;
	}

	/**
	 * Method setDrawingDimensions.
	 * @param width int
	 * @param height int
	 */
	@Override
	public void setDrawingDimensions(final int width, final int height) {
		curWidth = width;
		curHeight = height;
	}

	/**
	 * Method setDrawingColor.
	 * @param c Color
	 */
	private void setDrawingColor(final Color c) {
		if ( g2 != null && g2.getColor() != c ) {
			g2.setColor(c);
		}
	}

	// private final AffineTransform at = new AffineTransform();

	/**
	 * Method drawImage.
	 * @param img Image
	 * @param angle Integer
	 */
	@Override
	public Rectangle2D drawImage(final BufferedImage img, final Integer angle, final boolean smooth) {
		AffineTransform saved = g2.getTransform();
		// RenderingHints hints = g2.getRenderingHints();
		if ( angle != null ) {
			g2.rotate(Maths.toRad * angle, curX + curWidth / 2, curY + curHeight / 2);
		}
		// if ( !smooth ) {
		// g2.setRenderingHints(SPEED_RENDERING);
		// }
		g2.drawImage(img, curX, curY, curWidth, curHeight, null);
		// g2.setRenderingHints(hints);
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
	 * @param chart JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final JFreeChart chart) {
		rect.setRect(curX, curY, curWidth, curHeight);
		// drawImage(chart.createBufferedImage(curWidth, curHeight), null);
		Graphics2D g3 = (Graphics2D) g2.create();
		chart.draw(g3, rect);
		g3.dispose();
		return rect.getBounds2D();
	}

	/**
	 * Method drawCircle.
	 * @param c Color
	 * @param fill boolean
	 * @param angle Integer
	 */
	@Override
	public Rectangle2D drawCircle(final Color c, final boolean fill, final Integer angle) {
		oval.setFrame(curX, curY, curWidth, curWidth);
		return drawShape(c, oval, fill, angle);
	}

	@Override
	public Rectangle2D drawTriangle(final Color c, final boolean fill, final Integer angle) {
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
	 * @param c Color
	 * @param toX double
	 * @param toY double
	 */
	@Override
	public Rectangle2D drawLine(final Color c, final double toX, final double toY) {
		line.setLine(curX, curY, toX + offsetX, toY + offsetY);
		return drawShape(c, line, false, null);
	}

	/**
	 * Method drawRectangle.
	 * @param color Color
	 * @param fill boolean
	 * @param angle Integer
	 */
	@Override
	public Rectangle2D drawRectangle(final Color color, final boolean fill, final Integer angle) {
		rect.setFrame(curX, curY, curWidth, curHeight);
		return drawShape(color, rect, fill, angle);
	}

	/**
	 * Method drawString.
	 * @param string String
	 * @param stringColor Color
	 * @param angle Integer
	 */
	@Override
	public Rectangle2D drawString(final String string, final Color stringColor, final Integer angle) {
		setDrawingColor(stringColor);
		AffineTransform saved = g2.getTransform();
		if ( angle != null ) {
			Rectangle2D r = g2.getFontMetrics().getStringBounds(string, g2);
			g2.rotate(Maths.toRad * angle, curX + r.getWidth() / 2, curY + r.getHeight() / 2);
		}
		g2.drawString(string, curX, curY);
		g2.setTransform(saved);
		return g2.getFontMetrics().getStringBounds(string, g2);
	}

	/**
	 * Method drawGeometry.
	 * @param geometry Geometry
	 * @param color Color
	 * @param fill boolean
	 * @param angle Integer
	 */
	@Override
	public Rectangle2D drawGeometry(final Geometry geometry, final Color color, final boolean fill,
		final Integer angle) {
		boolean f =
			geometry instanceof LineString || geometry instanceof MultiLineString ? false : fill;

		return drawShape(color, sw.toShape(geometry), f, angle);
	}

	/**
	 * Method drawShape.
	 * @param c Color
	 * @param s Shape
	 * @param fill boolean
	 * @param angle Integer
	 */
	@Override
	public Rectangle2D drawShape(final Color c, final Shape s, final boolean fill,
		final Integer angle) {
		Rectangle2D r = s.getBounds2D();

		// if ( clipping != null && !r.intersects(clipping) ) { return; }
		// Graphics2D g3 = (Graphics2D) g2.create();
		AffineTransform saved = g2.getTransform();
		if ( angle != null ) {
			g2.rotate(Maths.toRad * angle, r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() /
				2);
		}
		// g3.setColor(c);
		setDrawingColor(c);
		if ( fill ) {
			g2.fill(s);
			// g3.setColor(Color.black);
			setDrawingColor(Color.black);
		}
		g2.draw(s);
		// g3.dispose();
		g2.setTransform(saved);
		return r;
	}

	// @Override
	// public void setGraphics(final GC gc, final Display displat) {
	// Nothing to do
	//
	// }

	@Override
	public void fill(final Color bgColor, final double opacity) {
		setOpacity(opacity);
		g2.setColor(bgColor);
		// if ( clipping != null ) {
		// g2.fillRect(clipping.x, clipping.y, clipping.width, clipping.height);
		// } else {
		g2.fillRect(0, 0, displayWidth, displayHeight);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.gui.graphics.IGraphics#setClipping(java.awt.Rectangle)
	 */
	@Override
	public void setClipping(final Rectangle imageClipBounds) {
		clipping = imageClipBounds;
		g2.setClip(imageClipBounds);
	}

	@Override
	public Rectangle getClipping() {
		return clipping;
	}

}
