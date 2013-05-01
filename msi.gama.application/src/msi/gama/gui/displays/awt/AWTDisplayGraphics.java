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

package msi.gama.gui.displays.awt;

import static java.awt.RenderingHints.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
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
 * 29/04/2013: Deep revision to simplify the interface due to the changes in draw/aspects
 * 
 * @author Nick Collier, Alexis Drogoul, Patrick Taillandier
 * @version $Revision: 1.13 $ $Date: 2010-03-19 07:12:24 $
 */

public class AWTDisplayGraphics implements IGraphics {

	int[] highlightColor = GuiUtils.defaultHighlight;
	boolean ready = false;
	private Graphics2D g2;
	// private Rectangle clipping;
	private final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);

	private double currentAlpha = 1;
	private int widthOfDisplayInPixels, heightOfDisplayInPixels;
	private int xOffsetInPixels = 0;
	private int yOffsetInPixels = 0;
	private int widthOfCurrentLayerInPixels = 0;
	private int heightOfCurrentLayerInPixels = 0;
	private int widthOfEnvironmentInModelUnits = 0;
	private int heightOfEnvironmentInModelUnits = 0;
	private final PointTransformation fromModelUnitsToPixels = new PointTransformation() {

		@Override
		public void transform(final Coordinate c, final Point2D p) {
			int xp = xFromModelUnitsToPixels(c.x);
			int yp = yFromModelUnitsToPixels(c.y);
			p.setLocation(xp, yp);
		}
	};
	private final ShapeWriter sw = new ShapeWriter(fromModelUnitsToPixels);
	private double xRatioBetweenPixelsAndModelUnits;
	private double yRatioBetweenPixelsAndModelUnits;
	private static final Font defaultFont = new Font("Helvetica", Font.PLAIN, 12);

	static {

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

	private final int xFromModelUnitsToPixels(double length) {
		return xOffsetInPixels + (int) (xRatioBetweenPixelsAndModelUnits * length /* + 0.5 */);
	}

	private final int yFromModelUnitsToPixels(double length) {
		return yOffsetInPixels + (int) (yRatioBetweenPixelsAndModelUnits * length /* + 0.5 */);
	}

	private final int wFromModelUnitsToPixels(double length) {
		return (int) (xRatioBetweenPixelsAndModelUnits * length * +0.5);
	}

	private final int hFromModelUnitsToPixels(double length) {
		return (int) (yRatioBetweenPixelsAndModelUnits * length + 0.5);
	}

	public AWTDisplayGraphics(final BufferedImage image, int env_width, int env_height) {
		this(image.getWidth(), image.getHeight(), env_width, env_height);
		setGraphics((Graphics2D) image.getGraphics());
	}

	public AWTDisplayGraphics(final int width, final int height, int env_width, int env_height) {
		setDisplayDimensionsInPixels(width, height);
		widthOfEnvironmentInModelUnits = env_width;
		heightOfEnvironmentInModelUnits = env_height;
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
	public int getDisplayWidthInPixels() {
		return widthOfDisplayInPixels;
	}

	/**
	 * Method getDisplayHeight.
	 * @return int
	 */
	@Override
	public int getDisplayHeightInPixels() {
		return heightOfDisplayInPixels;
	}

	/**
	 * Method setDisplayDimensions.
	 * @param width int
	 * @param height int
	 */
	@Override
	public void setDisplayDimensionsInPixels(final int width, final int height) {
		widthOfDisplayInPixels = width;
		heightOfDisplayInPixels = height;
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

	/**
	 * Method drawImage.
	 * @param img Image
	 * @param angle Integer
	 * @param smooth boolean
	 * @param name String
	 * @param z float (has no effet in java 2D)
	 */
	@Override
	public Rectangle2D drawImage(IScope scope, BufferedImage img, ILocation locationInModelUnits,
		ILocation sizeInModelUnits, Color gridColor, Integer angle, Double z, boolean isDynamic) {
		AffineTransform saved = g2.getTransform();
		int curX, curY;
		if ( locationInModelUnits == null ) {
			curX = xOffsetInPixels;
			curY = yOffsetInPixels;
		} else {
			curX = xFromModelUnitsToPixels(locationInModelUnits.getX());
			curY = yFromModelUnitsToPixels(locationInModelUnits.getY());
		}
		int curWidth, curHeight;
		if ( sizeInModelUnits == null ) {
			curWidth = widthOfCurrentLayerInPixels;
			curHeight = heightOfCurrentLayerInPixels;
		} else {
			curWidth = wFromModelUnitsToPixels(sizeInModelUnits.getX());
			curHeight = hFromModelUnitsToPixels(sizeInModelUnits.getY());
		}
		if ( angle != null ) {
			g2.rotate(Maths.toRad * angle, curX + curWidth / 2, curY + curHeight / 2);
		}
		g2.drawImage(img, curX, curY, curWidth, curHeight, null);
		if ( gridColor != null ) {
			drawGrid(img, gridColor);
		}
		g2.setTransform(saved);
		rect.setRect(curX, curY, curWidth, curHeight);
		return rect.getBounds2D();
	}

	/**
	 * Method drawChart.
	 * @param chart JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final IScope scope, final JFreeChart chart, final Double z) {
		BufferedImage im = chart.createBufferedImage(widthOfCurrentLayerInPixels, heightOfCurrentLayerInPixels);
		return drawImage(scope, im, new GamaPoint(0, 0), null, null, 0, z, true);
	}

	/**
	 * Method drawString.
	 * @param string String
	 * @param stringColor Color
	 * @param angle Integer
	 * @param z float (has no effect in 2D)
	 */
	@Override
	public Rectangle2D drawString(String string, Color stringColor, ILocation locationInModelUnits,
		java.lang.Double heightInModelUnits, String fontName, Integer styleName, Integer angle, Double z) {
		setDrawingColor(stringColor);
		int curX, curY;
		if ( locationInModelUnits == null ) {
			curX = xOffsetInPixels;
			curY = yOffsetInPixels;
		} else {
			curX = xFromModelUnitsToPixels(locationInModelUnits.getX());
			curY = yFromModelUnitsToPixels(locationInModelUnits.getY());
		}
		int curHeight;
		if ( heightInModelUnits == null ) {
			curHeight = heightOfCurrentLayerInPixels;
		} else {
			curHeight = hFromModelUnitsToPixels(heightInModelUnits);
		} // FIXME Optimize by keeping the current values
		int style = styleName == null ? Font.PLAIN : styleName;
		Font f = new Font(fontName, style, curHeight);
		g2.setFont(f);
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
	 * @param geometry GamaShape
	 * @param color Color
	 * @param fill boolean
	 * @param angle Integer
	 * @param rounded boolean (not yet implemented in JAVA 2D)
	 */
	@Override
	public Rectangle2D drawGamaShape(final IScope scope, final IShape geometry, final Color color, final boolean fill,
		final Color border, final Integer angle, final boolean rounded) {
		Geometry geom = null;
		if ( geometry == null ) { return null; }
		ITopology topo = scope.getTopology();
		// Necessary to check in case the scope has been erased (in cases of reload)
		if ( topo != null && topo.isTorus() ) {
			geom = topo.returnToroidalGeom(geometry.getInnerGeometry());
		} else {
			geom = geometry.getInnerGeometry();
		}
		boolean f = geom instanceof LineString || geom instanceof MultiLineString ? false : fill;
		return drawShape(color, sw.toShape(geom), f, border, angle);
	}

	/**
	 * Method drawShape.
	 * @param c Color
	 * @param s Shape
	 * @param fill boolean
	 * @param angle Integer
	 */
	public Rectangle2D drawShape(final Color c, final Shape s, final boolean fill, final Color border,
		final Integer angle) {
		try {
			Rectangle2D r = s.getBounds2D();
			AffineTransform saved = g2.getTransform();
			if ( angle != null ) {
				g2.rotate(Maths.toRad * angle, r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
			}
			setDrawingColor(c);
			if ( fill ) {
				g2.fill(s);
				setDrawingColor(border);
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
	public void fillBackground(final Color bgColor, final double opacity) {
		setOpacity(opacity);
		g2.setColor(bgColor);
		g2.fillRect(0, 0, widthOfDisplayInPixels, heightOfDisplayInPixels);
	}

	public void drawGrid(final BufferedImage image, final Color lineColor) {
		final Line2D line = new Line2D.Double();
		// The image contains the dimensions of the grid.
		double stepx = (double) widthOfCurrentLayerInPixels / image.getWidth();
		for ( double step = 0.0, end = widthOfCurrentLayerInPixels; step < end + 1; step += stepx ) {
			line.setLine(step, 0, step, heightOfCurrentLayerInPixels);
			drawShape(lineColor, line, false, null, null);
		}
		line.setLine(widthOfCurrentLayerInPixels - 1, 0, widthOfCurrentLayerInPixels - 1,
			heightOfCurrentLayerInPixels - 1);
		drawShape(lineColor, line, false, null, null);
		double stepy = (double) heightOfCurrentLayerInPixels / image.getHeight();
		for ( double step = 0.0, end = heightOfCurrentLayerInPixels; step < end + 1; step += stepy ) {
			line.setLine(0, step, widthOfCurrentLayerInPixels, step);
			drawShape(lineColor, line, false, null, null);
		}
		line.setLine(0, heightOfCurrentLayerInPixels - 1, widthOfCurrentLayerInPixels - 1,
			heightOfCurrentLayerInPixels - 1);
		drawShape(lineColor, line, false, null, null);

	}

	@Override
	public int[] getHighlightColor() {
		return highlightColor;
	}

	@Override
	public void setHighlightColor(final int[] rgb) {
		highlightColor = rgb;
	}

	@Override
	public void highlightRectangleInPixels(final IAgent a, final Rectangle2D r) {
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(5));
		Color old = g2.getColor();
		g2.setColor(new Color(highlightColor[0], highlightColor[1], highlightColor[2]));
		g2.draw(r);
		g2.setStroke(oldStroke);
		g2.setColor(old);
	}

	@Override
	public void beginDrawingLayers() {}

	@Override
	public void newLayer(ILayer layer) {
		xOffsetInPixels = layer.getPositionInPixels().x;
		yOffsetInPixels = layer.getPositionInPixels().y;
		widthOfCurrentLayerInPixels = layer.getSizeInPixels().x;
		heightOfCurrentLayerInPixels = layer.getSizeInPixels().y;
		xRatioBetweenPixelsAndModelUnits =
			(double) widthOfCurrentLayerInPixels / (double) widthOfEnvironmentInModelUnits;
		yRatioBetweenPixelsAndModelUnits =
			(double) heightOfCurrentLayerInPixels / (double) heightOfEnvironmentInModelUnits;

	}

	public Graphics2D getGraphics2D() {
		return g2;
	}

	@Override
	public int getEnvironmentWidth() {
		return widthOfEnvironmentInModelUnits;
	}

	@Override
	public int getEnvironmentHeight() {
		return heightOfEnvironmentInModelUnits;
	}

}
