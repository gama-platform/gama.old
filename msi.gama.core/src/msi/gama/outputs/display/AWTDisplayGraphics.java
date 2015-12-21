/*********************************************************************************************
 *
 *
 * 'AWTDisplayGraphics.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/

package msi.gama.outputs.display;

import static java.awt.RenderingHints.*;
import java.awt.*;
import java.awt.Point;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.runtime.IScope;
import msi.gaml.operators.Maths;

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
	private final LayeredDisplayData data;
	private boolean highlight;
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
		data = surface.getData();
		renderer = g2;
		renderer.setFont(defaultFont);

	}

	@Override
	public void beginDrawingLayers() {
		renderer.setRenderingHints(data.isAntialias() ? QUALITY_RENDERING : SPEED_RENDERING);
	}

	@Override
	public void setOpacity(final double alpha) {
		super.setOpacity(alpha);
		renderer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
	}

	/**
	 * Implements PointTransformation.transform
	 * @see com.vividsolutions.jts.awt.PointTransformation#transform(com.vividsolutions.jts.geom.Coordinate, java.awt.geom.Point2D)
	 */
	@Override
	public void transform(final Coordinate c, final Point2D p) {
		p.setLocation(xFromModelUnitsToPixels(c.x), yFromModelUnitsToPixels(c.y));
	}

	@Override
	public Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] gridValueMatrix,
		final boolean isTextured, final boolean isTriangulated, final boolean isGrayScaled, final boolean isShowText,
		final Color gridColor, final double cellSize, final String name) {
		return drawImage(scope, img, null, null, gridColor, null, true, "grid");
	}

	@Override
	public Rectangle2D drawImage(final IScope scope, final BufferedImage img, final ILocation locationInModelUnits,
		final ILocation sizeInModelUnits, final Color gridColor, final Double angle, final boolean isDynamic,
		final String name) {
		final AffineTransform saved = renderer.getTransform();
		double curX, curY;
		if ( locationInModelUnits == null ) {
			curX = xOffsetInPixels;
			curY = yOffsetInPixels;
		} else {
			curX = xFromModelUnitsToPixels(locationInModelUnits.getX());
			curY = yFromModelUnitsToPixels(locationInModelUnits.getY());
		}
		double curWidth, curHeight;
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
		renderer.drawImage(img, (int) Math.round(curX), (int) Math.round(curY), (int) curWidth, (int) curHeight, null);
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
	public Rectangle2D drawChart(final IScope scope, final BufferedImage chart, final Double z) {
		return drawImage(scope, chart, new GamaPoint(0, 0), null, null, 0d, true, "");
	}

	/**
	 * Method drawString.
	 * @param string String
	 * @param stringColor Color
	 * @param angle Integer
	 * @param z float (has no effect in 2D)
	 */

	// AD WARNING Experimental / Not used for now -- see Issue 779
	public Rectangle2D drawMultiLineString(final String string, final Color stringColor,
		final ILocation locationInModelUnits, final java.lang.Double heightInModelUnits, final String fontName,
		final Integer styleName, final Double angle, final Double z, final Boolean bitmap) {
		renderer.setColor(highlight ? data.getHighlightColor() : stringColor);
		double curX, curY;
		if ( locationInModelUnits == null ) {
			curX = xOffsetInPixels;
			curY = yOffsetInPixels;
		} else {
			curX = xFromModelUnitsToPixels(locationInModelUnits.getX());
			curY = yFromModelUnitsToPixels(locationInModelUnits.getY());
		}
		double curHeight, curWidth;

		if ( heightInModelUnits == null ) {
			curWidth = widthOfLayerInPixels;
			curHeight = heightOfLayerInPixels;
		} else {
			// FIXME
			curWidth = wFromModelUnitsToPixels(50);
			curHeight = hFromModelUnitsToPixels(heightInModelUnits);
		}
		final int style = styleName == null ? Font.PLAIN : styleName;
		final Font f = new Font(fontName, style, (int) curHeight);
		renderer.setFont(f);
		final AffineTransform saved = renderer.getTransform();
		if ( angle != null ) {
			final Rectangle2D r = renderer.getFontMetrics().getStringBounds(string, renderer);
			renderer.rotate(Maths.toRad * angle, curX + r.getWidth() / 2, curY + r.getHeight() / 2);
		}
		Point pen = new Point((int) curX, (int) curY);
		LineBreakMeasurer measurer =
			new LineBreakMeasurer(new AttributedString(string).getIterator(), renderer.getFontRenderContext());
		while (true) {
			TextLayout layout = measurer.nextLayout((int) curWidth);
			if ( layout == null ) {
				break;
			}
			pen.y += layout.getAscent();
			double dx = 0;
			if ( layout.isLeftToRight() ) {
				dx = curWidth - layout.getAdvance();
			}
			layout.draw(renderer, pen.x + (float) dx, pen.y);
			pen.y += layout.getDescent() + layout.getLeading();
		}
		return new Rectangle2D.Double(curX, curY, curWidth, pen.y - curY);
	}

	@Override
	public Rectangle2D drawString(final String string, final Color stringColor, final ILocation locationInModelUnits,
		final java.lang.Double heightInModelUnits, final Font font, final Double angle, final Boolean bitmap) {
		renderer.setColor(highlight ? data.getHighlightColor() : stringColor);
		double curX, curY, curZ;
		if ( locationInModelUnits == null ) {
			curX = xOffsetInPixels;
			curY = yOffsetInPixels;
			// curZ = 0;
		} else {
			curX = xFromModelUnitsToPixels(locationInModelUnits.getX());
			curY = yFromModelUnitsToPixels(locationInModelUnits.getY());
			// curZ = yFromModelUnitsToPixels(locationInModelUnits.getZ());
		}
		double curHeight;
		if ( heightInModelUnits == null ) {
			curHeight = heightOfLayerInPixels;
			// GuiUtils.debug("AWTDisplayGraphics.drawString " + string + " " + curHeight);
		} else {
			curHeight = hFromModelUnitsToPixels(heightInModelUnits);
			// GuiUtils.debug("AWTDisplayGraphics.drawString " + string + " " + curHeight);

		} // FIXME Optimize by keeping the current values
			// final int style = styleName == null ? Font.PLAIN : styleName;
			// final Font f = new Font(fontName, style, curHeight);
		renderer.setFont(font.deriveFont((float) curHeight));
		final AffineTransform saved = renderer.getTransform();
		if ( angle != null ) {
			final Rectangle2D r = renderer.getFontMetrics().getStringBounds(string, renderer);
			renderer.rotate(Maths.toRad * angle, curX + r.getWidth() / 2, curY + r.getHeight() / 2);
		}
		renderer.drawString(string, (int) curX, (int) curY);
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
		final Color border, final boolean rounded) {
		if ( geometry == null ) { return null; }
		Envelope e = geometry.getEnvelope();
		// System.out.println("Original geometry width:" + e.getWidth() + " height: " + e.getHeight());
		final ITopology topo = scope.getTopology();
		Rectangle2D result = null;
		if ( topo != null && topo.isTorus() ) {
			java.util.List<Geometry> geoms = topo.listToroidalGeometries(geometry.getInnerGeometry());
			for ( Geometry g : geoms ) {
				Rectangle2D r = drawSimpleShape(scope, g, color, fill, border, rounded);
				if ( result == null ) {
					result = r;
				}
			}
		} else {
			result = drawSimpleShape(scope, geometry.getInnerGeometry(), color, fill, border, rounded);
		}
		return result;
		// Geometry geom = geometry.getInnerGeometry();
		// // Necessary to check in case the scope has been erased (in cases of reload)
		// if ( topo != null && topo.isTorus() ) {
		// geom = topo.listToroidalGeometries(geom);
		// }
	}

	private Rectangle2D drawSimpleShape(final IScope scope, final Geometry geom, final Color color, final boolean fill,
		final Color border, final boolean rounded) {
		final Shape s = sw.toShape(geom);
		try {
			final Rectangle2D r = s.getBounds2D();
			// System.out.println("width : " + r.getWidth() + " height: " + r.getHeight());
			// System.out.println("Value of 1 pixel: " + 1d / getyRatioBetweenPixelsAndModelUnits());
			// final AffineTransform saved = renderer.getTransform();
			// if ( angle != null ) {
			// renderer.rotate(Maths.toRad * angle, r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
			// }
			renderer.setColor(highlight ? data.getHighlightColor() : color);
			if ( geom instanceof Lineal || geom instanceof Puntal ? false : fill ) {
				renderer.fill(s);
				if ( border != null ) {
					renderer.setColor(highlight ? data.getHighlightColor() : border);
				}
			}
			if ( border != null ) {
				renderer.draw(s);
			}
			// renderer.setTransform(saved);
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
		renderer.fillRect(0, 0, (int) widthOfDisplayInPixels, (int) heightOfDisplayInPixels);
	}

	public void drawGridLine(final BufferedImage image, final Color lineColor) {
		final Line2D line = new Line2D.Double();
		renderer.setColor(lineColor);
		// The image contains the dimensions of the grid.
		final double stepx = widthOfLayerInPixels / image.getWidth();
		for ( double step = 0.0, end = widthOfLayerInPixels; step < end + 1; step += stepx ) {
			line.setLine(xOffsetInPixels + step, yOffsetInPixels, xOffsetInPixels + step,
				yOffsetInPixels + heightOfLayerInPixels);
			renderer.draw(line);
		}
		line.setLine(xOffsetInPixels + widthOfLayerInPixels - 1, yOffsetInPixels,
			xOffsetInPixels + widthOfLayerInPixels - 1, yOffsetInPixels + heightOfLayerInPixels - 1);
		renderer.draw(line);
		final double stepy = heightOfLayerInPixels / image.getHeight();
		for ( double step = 0.0, end = heightOfLayerInPixels; step < end + 1; step += stepy ) {
			line.setLine(xOffsetInPixels, yOffsetInPixels + step, xOffsetInPixels + widthOfLayerInPixels,
				yOffsetInPixels + step);
			renderer.draw(line);
		}
		line.setLine(xOffsetInPixels, yOffsetInPixels + heightOfLayerInPixels - 1,
			xOffsetInPixels + widthOfLayerInPixels - 1, yOffsetInPixels + heightOfLayerInPixels - 1);
		renderer.draw(line);

	}

	private void highlightRectangleInPixels(final Rectangle2D r) {
		if ( r == null ) { return; }
		final Stroke oldStroke = renderer.getStroke();
		renderer.setStroke(new BasicStroke(5));
		final Color old = renderer.getColor();
		renderer.setColor(data.getHighlightColor());
		renderer.draw(r);
		renderer.setStroke(oldStroke);
		renderer.setColor(old);
	}

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
