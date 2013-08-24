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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gaml.operators.Maths;
import org.jfree.chart.JFreeChart;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;

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

public class AWTDisplayGraphics extends AbstractDisplayGraphics implements PointTransformation {

	private final Graphics2D renderer;
	private final ShapeWriter sw = new ShapeWriter(this);

	private static final Font defaultFont = new Font("Helvetica", Font.BOLD, 12);

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
		SPEED_RENDERING.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

	}

	public AWTDisplayGraphics(final IDisplaySurface surface, final Graphics2D g2) {
		super(surface);
		renderer = g2;
		// setQualityRendering(true); // Turned on by default
		renderer.setFont(defaultFont);
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		renderer.setRenderingHints(quality ? QUALITY_RENDERING : SPEED_RENDERING);
	}

	@Override
	public void setOpacity(final double alpha) {
		super.setOpacity(alpha);
		renderer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
	}

	/**
	 * Implements PointTransformation.transform
	 * @see com.vividsolutions.jts.awt.PointTransformation#transform(com.vividsolutions.jts.geom.Coordinate,
	 *      java.awt.geom.Point2D)
	 */
	@Override
	public void transform(final Coordinate c, final Point2D p) {
		p.setLocation(xFromModelUnitsToPixels(c.x), yFromModelUnitsToPixels(c.y));
	}

	@Override
	public Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] gridValueMatrix,
		final boolean isTextured, final boolean isTriangulated, final boolean isShowText,
		final ILocation locationInModelUnits, final ILocation sizeInModelUnits, final Color gridColor,
		final Integer angle, final Double z, final boolean isDynamic, final int cellSize) {
		return drawImage(scope, img, locationInModelUnits, sizeInModelUnits, gridColor, angle, z, isDynamic, "grid");
	}

	@Override
	public Rectangle2D drawImage(final IScope scope, final BufferedImage img, final ILocation locationInModelUnits,
		final ILocation sizeInModelUnits, final Color gridColor, final Integer angle, final Double z,
		final boolean isDynamic, final String name) {
		final AffineTransform saved = renderer.getTransform();
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
			curWidth = widthOfLayerInPixels;
			curHeight = heightOfLayerInPixels;
		} else {
			curWidth = wFromModelUnitsToPixels(sizeInModelUnits.getX());
			curHeight = hFromModelUnitsToPixels(sizeInModelUnits.getY());
		}
		if ( angle != null ) {
			renderer.rotate(Maths.toRad * angle, curX + curWidth / 2, curY + curHeight / 2);
		}
		renderer.drawImage(img, curX, curY, curWidth, curHeight, null);
		if ( gridColor != null ) {
			drawGridLine(img, gridColor);
		}
		renderer.setTransform(saved);
		rect.setRect(curX, curY, curWidth, curHeight);
		if ( highlight ) {
			highlightRectangleInPixels(rect);
		}
		return rect.getBounds2D();
	}

	/**
	 * Method drawChart.Simply creates an image from the chart and displays it.
	 * @param chart JFreeChart
	 */
	@Override
	public Rectangle2D drawChart(final IScope scope, final JFreeChart chart, final Double z) {
		final BufferedImage im = chart.createBufferedImage(widthOfLayerInPixels, heightOfLayerInPixels);
		return drawImage(scope, im, new GamaPoint(0, 0), null, null, 0, z, true, "");
	}

	/**
	 * Method drawString.
	 * @param string String
	 * @param stringColor Color
	 * @param angle Integer
	 * @param z float (has no effect in 2D)
	 */
	@Override
	public Rectangle2D drawString(final String string, final Color stringColor, final ILocation locationInModelUnits,
		final java.lang.Double heightInModelUnits, final String fontName, final Integer styleName, final Integer angle,
		final Double z,final Boolean bitmap) {
		renderer.setColor(highlight ? highlightColor : stringColor);
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
			curHeight = heightOfLayerInPixels;
			// GuiUtils.debug("AWTDisplayGraphics.drawString  " + string + " " + curHeight);
		} else {
			curHeight = hFromModelUnitsToPixels(heightInModelUnits);
			// GuiUtils.debug("AWTDisplayGraphics.drawString  " + string + " " + curHeight);

		} // FIXME Optimize by keeping the current values
		final int style = styleName == null ? Font.PLAIN : styleName;
		final Font f = new Font(fontName, style, curHeight);
		renderer.setFont(f);
		final AffineTransform saved = renderer.getTransform();
		if ( angle != null ) {
			final Rectangle2D r = renderer.getFontMetrics().getStringBounds(string, renderer);
			renderer.rotate(Maths.toRad * angle, curX + r.getWidth() / 2, curY + r.getHeight() / 2);
		}
		renderer.drawString(string, curX, curY);
		renderer.setTransform(saved);
		return renderer.getFontMetrics().getStringBounds(string, renderer);
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
		if ( geometry == null ) { return null; }
		final ITopology topo = scope.getTopology();
		Geometry geom = geometry.getInnerGeometry();
		// Necessary to check in case the scope has been erased (in cases of reload)
		if ( topo != null && topo.isTorus() ) {
			geom = topo.returnToroidalGeom(geom);
		}
		final Shape s = sw.toShape(geom);
		try {
			final Rectangle2D r = s.getBounds2D();
			final AffineTransform saved = renderer.getTransform();
			if ( angle != null ) {
				renderer.rotate(Maths.toRad * angle, r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
			}
			renderer.setColor(highlight ? highlightColor : color);
			if ( geom instanceof Lineal || geom instanceof Puntal ? false : fill ) {
				renderer.fill(s);
				renderer.setColor(highlight ? highlightColor : border);
			}
			renderer.draw(s);
			renderer.setTransform(saved);
			return r;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void fillBackground(final Color bgColor, final double opacity) {
		setOpacity(opacity);
		renderer.setColor(bgColor);
		renderer.fillRect(0, 0, widthOfDisplayInPixels, heightOfDisplayInPixels);
	}

	public void drawGridLine(final BufferedImage image, final Color lineColor) {
		final Line2D line = new Line2D.Double();
		renderer.setColor(lineColor);
		// The image contains the dimensions of the grid.
		final double stepx = (double) widthOfLayerInPixels / image.getWidth();
		for ( double step = 0.0, end = widthOfLayerInPixels; step < end + 1; step += stepx ) {
			line.setLine(xOffsetInPixels + step, yOffsetInPixels, xOffsetInPixels + step, yOffsetInPixels +
				heightOfLayerInPixels);
			renderer.draw(line);
		}
		line.setLine(xOffsetInPixels + widthOfLayerInPixels - 1, yOffsetInPixels, xOffsetInPixels +
			widthOfLayerInPixels - 1, yOffsetInPixels + heightOfLayerInPixels - 1);
		renderer.draw(line);
		final double stepy = (double) heightOfLayerInPixels / image.getHeight();
		for ( double step = 0.0, end = heightOfLayerInPixels; step < end + 1; step += stepy ) {
			line.setLine(xOffsetInPixels, yOffsetInPixels + step, xOffsetInPixels + widthOfLayerInPixels,
				yOffsetInPixels + step);
			renderer.draw(line);
		}
		line.setLine(xOffsetInPixels, yOffsetInPixels + heightOfLayerInPixels - 1, xOffsetInPixels +
			widthOfLayerInPixels - 1, yOffsetInPixels + heightOfLayerInPixels - 1);
		renderer.draw(line);

	}

	private void highlightRectangleInPixels(final Rectangle2D r) {
		if ( r == null ) { return; }
		final Stroke oldStroke = renderer.getStroke();
		renderer.setStroke(new BasicStroke(5));
		final Color old = renderer.getColor();
		renderer.setColor(highlightColor);
		renderer.draw(r);
		renderer.setStroke(oldStroke);
		renderer.setColor(old);
	}

	private boolean highlight;

	@Override
	public void beginHighlight() {
		highlight = true;
	}

	@Override
	public void endHighlight() {
		highlight = false;
	}

	@Override
	public void endDrawingLayers() {}

}
