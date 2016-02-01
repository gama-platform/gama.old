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
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import com.vividsolutions.jts.awt.*;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.operators.Maths;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

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
	private boolean highlight;
	private static final Font defaultFont = new Font("Helvetica", Font.BOLD, 12);
	IDisplaySurface surface;

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
		this.surface = surface;
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
		final boolean isTriangulated, final boolean isGrayScaled, final boolean isShowText, final GamaColor gridColor,
		final Envelope3D cellSize, final String name) {
		DrawingAttributes attributes = new DrawingAttributes(new GamaPoint(0, 0), null, gridColor);
		attributes.setDynamic(true);
		return drawImage(img, attributes);
	}

	@Override
	public Rectangle2D drawFile(final GamaGeometryFile file, final DrawingAttributes attributes) {
		IScope scope = surface.getDisplayScope();
		GamaShape shape = (GamaShape) file.getGeometry(scope);
		if ( shape == null ) { return null; }
		GamaPair<Double, GamaPoint> rot = attributes.rotation;
		Double rotation = rot == null ? null : rot.key;
		// System.out.println("Old centroid " + shape.getInnerGeometry().getCentroid());
		// shape = shape.translatedTo(scope, attributes.location);
		// System.out.println("Centroid after translation" + shape.getInnerGeometry().getCentroid());
		shape = new GamaShape(shape, null, rotation, attributes.location, attributes.size, true);
		// System.out.println("New centroid " + shape.getInnerGeometry().getCentroid());
		GamaColor c = attributes.color;
		return drawShape(shape, new DrawingAttributes(new GamaPoint((Coordinate) shape.getLocation()), c, c));
	}

	@Override
	public Rectangle2D drawImage(final BufferedImage img, final DrawingAttributes attributes) {
		final AffineTransform saved = renderer.getTransform();
		double curX, curY;
		if ( attributes.location == null ) {
			curX = xOffsetInPixels;
			curY = yOffsetInPixels;
		} else {
			curX = xFromModelUnitsToPixels(attributes.location.getX());
			curY = yFromModelUnitsToPixels(attributes.location.getY());
		}
		double curWidth, curHeight;
		if ( attributes.size == null ) {
			curWidth = widthOfLayerInPixels;
			curHeight = heightOfLayerInPixels;
		} else {
			curWidth = wFromModelUnitsToPixels(attributes.size.getX());
			curHeight = hFromModelUnitsToPixels(attributes.size.getY());
		}
		if ( attributes.rotation != null && attributes.rotation.key != null ) {
			renderer.rotate(Maths.toRad * attributes.rotation.key, curX + curWidth / 2, curY + curHeight / 2);
		}
		renderer.drawImage(img, (int) Math.round(curX), (int) Math.round(curY), (int) curWidth, (int) curHeight, null);
		if ( attributes.border != null ) {
			drawGridLine(img, attributes.border);
		}
		renderer.setTransform(saved);
		rect.setRect(curX, curY, curWidth, curHeight);
		if ( highlight ) {
			highlightRectangleInPixels(rect);
		}
		return rect.getBounds2D();
	}

	/**
	 * Method drawString.
	 * @param string String
	 * @param stringColor Color
	 * @param angle Integer
	 * @param z float (has no effect in 2D)
	 */

	@Override
	public Rectangle2D drawString(final String string, final DrawingAttributes attributes) {
		// Multiline: Issue #780
		if ( string.contains("\n") ) {
			Rectangle2D result = new Rectangle2D.Double();
			for ( String s : string.split("\n") ) {
				Rectangle2D r = drawString(s, attributes);
				attributes.location.setY(attributes.location.getY() + r.getHeight());
				result.add(r);
			}
			return result;
		}
		renderer.setColor(highlight ? data.getHighlightColor() : attributes.color);
		double curX, curY, curZ;
		if ( attributes.location == null ) {
			curX = xOffsetInPixels;
			curY = yOffsetInPixels;
			// curZ = 0;
		} else {
			curX = xFromModelUnitsToPixels(attributes.location.getX());
			curY = yFromModelUnitsToPixels(attributes.location.getY());
		}
		renderer.setFont(attributes.font);
		final AffineTransform saved = renderer.getTransform();
		if ( attributes.rotation != null && attributes.rotation.key != null ) {
			final Rectangle2D r = renderer.getFontMetrics().getStringBounds(string, renderer);
			renderer.rotate(Maths.toRad * attributes.rotation.key, curX + r.getWidth() / 2, curY + r.getHeight() / 2);
		}
		renderer.drawString(string, (int) curX, (int) curY);
		renderer.setTransform(saved);
		return renderer.getFontMetrics().getStringBounds(string, renderer);

	}

	@Override
	public Rectangle2D drawShape(final IShape geometry, final DrawingAttributes attributes) {
		if ( geometry == null ) { return null; }
		Geometry geom = geometry.getInnerGeometry();
		final Shape s = sw.toShape(geom);
		try {
			final Rectangle2D r = s.getBounds2D();
			renderer.setColor(highlight ? data.getHighlightColor() : attributes.color);
			if ( geom instanceof Lineal || geom instanceof Puntal ? false : !attributes.empty ) {
				renderer.fill(s);
				if ( attributes.hasBorder && attributes.border != null ) {
					renderer.setColor(highlight ? data.getHighlightColor() : attributes.border);
				}
			}
			if ( attributes.hasBorder && attributes.border != null ) {
				renderer.draw(s);
			}
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

	// @Override
	// public Rectangle2D drawFile(final IScope scope, final GamaFile filecheck, final Color color,
	// final ILocation locationInModelUnits, final ILocation sizeInModelUnits,
	// final GamaPair<Double, GamaPoint> rotates3d, final GamaPair<Double, GamaPoint> rotates3dInit) {
	// return drawFile(scope, filecheck, color, locationInModelUnits, sizeInModelUnits, rotates3d);
	// }

	/**
	 * Method is2D()
	 * @see msi.gama.common.interfaces.IGraphics#is2D()
	 */
	@Override
	public boolean is2D() {
		return true;
	}

}
