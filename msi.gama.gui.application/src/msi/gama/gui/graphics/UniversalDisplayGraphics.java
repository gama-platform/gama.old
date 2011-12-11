/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */

package msi.gama.gui.graphics;

import static java.awt.RenderingHints.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import msi.gama.util.MathUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.JFreeChart;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

/**
 * A Wrapper around a ViewPortGraphics (itself a wrapper around a AWT Graphics2D or a SWT GC)..
 * Simplifies the drawing of circles, rectangles, and so forth. Rectangles are generally faster to
 * draw than circles. The Displays should take care of layouts while objects that wish to be drawn
 * as a shape need only call the appropriate method.
 * <p>
 * 
 * @author Nick Collier, Alexis Drogoul, Patrick Taillandier
 * @version $Revision: 1.13 $ $Date: 2010-03-19 07:12:24 $
 */

public class UniversalDisplayGraphics implements IGraphics {

	/**
	 * Field g2.
	 */
	private IInternalGraphics univGC;
	/**
	 * Field rect.
	 */
	private final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	/**
	 * Field oval.
	 */
	private final Ellipse2D oval = new Ellipse2D.Double(0, 0, 1, 1);
	/**
	 * Field line.
	 */
	private final Line2D line = new Line2D.Double();
	private double currentAlpha = 1;
	private int displayWidth, displayHeight, curX = 0, curY = 0, curWidth = 5, curHeight = 5,
		offsetX = 0, offsetY = 0;
	private double xScale = 1, yScale = 1;
	private static RenderingHints rendering;
	/**
	 * Field defaultFont.
	 */
	private static final Font defaultFont = new Font("Helvetica", Font.PLAIN, 12);

	private final ShapeWriter sw = new ShapeWriter(new PointTransformation() {

		@Override
		public void transform(final Coordinate c, final Point2D p) {
			int xp = offsetX + (int) (xScale * c.x + 0.5);
			int yp = offsetY + (int) (yScale * c.y + 0.5);
			p.setLocation(xp, yp);
		}
	});
	private boolean ready = false;

	/**
	 * Constructor for DisplayGraphics.
	 * @param width int
	 * @param height int
	 */
	protected UniversalDisplayGraphics(final int width, final int height) {
		displayWidth = width;
		displayHeight = height;
		offsetX = 0;
		offsetY = 0;
		System.setProperty("sun.java2d.ddscale", "true");
		System.setProperty("sun.java2d.accthreshold", "0");
		System.setProperty("sun.java2d.allowrastersteal", "true");
		System.setProperty("sun.java2d.opengl", "true");
		System.setProperty("apple.awt.graphics.UseQuartz", "false");
		rendering = new RenderingHints(null);
		rendering.put(KEY_RENDERING, VALUE_RENDER_SPEED);
		rendering.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_SPEED);
		rendering.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED);
		rendering.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		rendering.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
	}

	/**
	 * Method setGraphics.
	 * @param g Graphics2D
	 */
	@Override
	public void setGraphics(final Graphics2D g) {
		ready = true;
		g.setRenderingHints(rendering);
		if ( univGC != null ) {
			univGC.dispose();
		}
		univGC = new InternalAWTGraphics(g);
		univGC.setFont(defaultFont);
	}

	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public void setGraphics(final GC g, final Display display) {
		ready = true;
		g.setAdvanced(true);
		g.setAntialias(SWT.OFF);
		g.setTextAntialias(SWT.ON);
		if ( univGC != null ) {
			univGC.dispose();
		}
		univGC = new InternalSWTGraphics(g, display);
		univGC.setFont(defaultFont);
		// setDisplayDimensions(g.getClipping().width, g.getClipping().height); // / ???
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
		univGC.setOpacity(currentAlpha);
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
		univGC.setFont(font);
	}

	/**
	 * Method getXScale.
	 * @return double
	 */
	@Override
	public double getXScale() {
		return xScale;
	}

	/**
	 * Method setXScale.
	 * @param scale double
	 */
	@Override
	public void setXScale(final double scale) {
		this.xScale = scale;
	}

	/**
	 * Method getYScale.
	 * @return double
	 */
	@Override
	public double getYScale() {
		return yScale;
	}

	/**
	 * Method setYScale.
	 * @param scale double
	 */
	@Override
	public void setYScale(final double scale) {
		this.yScale = scale;
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
		if ( univGC.getColor() != c ) {
			univGC.setColor(c);
		}
	}

	/**
	 * Method setDrawingRotation.
	 * @param angle int
	 */
	private void setDrawingRotation(final int angle) {
		univGC.rotate(MathUtils.toRad * angle, curX + (curWidth >> 1), curY + (curHeight >> 1));
	}

	/**
	 * Method resetDrawingRotation.
	 */

	private void resetDrawingRotation() {
		univGC.setTransform(IInternalGraphics.AFFINE_TRANSFORM);
	}

	/**
	 * Method drawImage.
	 * @param img Image
	 * @param angle Integer
	 */
	@Override
	public Rectangle2D drawImage(final BufferedImage img, final Integer angle) {
		if ( angle != null ) {
			setDrawingRotation(angle);
		}
		univGC.drawImage(img, curX, curY, curWidth, curHeight, null);
		if ( angle != null ) {
			resetDrawingRotation();
		}
		return new Rectangle2D.Double(curX, curY, curWidth, curHeight);
	}

	/**
	 * Method drawChart.
	 * @param chart JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final JFreeChart chart) {
		rect.setRect(curX, curY, curWidth, curHeight);
		return drawImage(chart.createBufferedImage(curWidth, curHeight), 0);
		// chart.draw(g2, rect);
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
		/*
		 * p0.moveTo(curX + curWidth, curY);
		 * p0.lineTo(curX - curWidth, curY - curHeight);
		 * p0.lineTo(curX - curWidth, curY + curHeight);
		 */
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
		if ( angle != null ) {
			setDrawingRotation(angle);
		}
		univGC.drawString(string, curX, curY);
		if ( angle != null ) {
			resetDrawingRotation();
		}
		Rectangle2D r = univGC.getStringBounds(string);
		return new Rectangle2D.Double(curX, curY, r.getWidth(), r.getHeight());
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
		return drawShape(color, sw.toShape(geometry), fill, angle);
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
		if ( angle != null ) {
			setDrawingRotation(angle);
		}
		setDrawingColor(c);
		if ( fill ) {
			univGC.fill(s);
			setDrawingColor(Color.black);
		}
		univGC.draw(s);
		if ( angle != null ) {
			resetDrawingRotation();
		}
		return s.getBounds2D();
	}

	@Override
	public void erase(final Color bgColor) {
		univGC.erase(bgColor, displayWidth, displayHeight);
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		// NOTHING FOR THE MOMENT. NEEDS TO BE DONE, THOUGH...
	}

	@Override
	public void setClipping(final Rectangle imageClipBounds) {
		// g2.setClip(imageClipBounds);
	}

	@Override
	public Rectangle getClipping() {
		return null;
	}

}
