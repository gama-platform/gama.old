/*******************************************************************************************************
 *
 * msi.gama.outputs.display.AWTDisplayGraphics.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.outputs.display;

import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_SPEED;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_RENDER_SPEED;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.locationtech.jts.awt.PointTransformation;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Lineal;
import org.locationtech.jts.geom.Puntal;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.outputs.layers.charts.ChartOutput;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.IField;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.MeshDrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;

/**
 *
 * Simplifies the drawing of circles, rectangles, and so forth. Rectangles are generally faster to draw than circles.
 * The Displays should take care of layouts while objects that wish to be drawn as a shape need only call the
 * appropriate method.
 * <p>
 *
 * 29/04/2013: Deep revision to simplify the interface due to the changes in draw/aspects
 *
 * @author Nick Collier, Alexis Drogoul, Patrick Taillandier
 * @version $Revision: 1.13 $ $Date: 2010-03-19 07:12:24 $
 */

public class AWTDisplayGraphics extends AbstractDisplayGraphics {

	private Graphics2D currentRenderer, overlayRenderer, normalRenderer;
	private Rectangle2D temporaryEnvelope = null;

	private static final Font defaultFont = new Font("Helvetica", Font.BOLD, 12);

	private class Java2DPointTransformation implements PointTransformation {

		double xOffset, yOffset, xRatio, yRatio;

		@Override
		public void transform(final Coordinate src, final Point2D dest) {
			dest.setLocation(xOffset + xRatio * src.x, yOffset + yRatio * src.y);

		}

		public void adapt() {
			xOffset = getXOffsetInPixels();
			yOffset = getYOffsetInPixels();
			xRatio = getxRatioBetweenPixelsAndModelUnits();
			yRatio = getyRatioBetweenPixelsAndModelUnits();
		}

	}

	private final Java2DPointTransformation pf = new Java2DPointTransformation();
	private final ShapeWriter sw = new ShapeWriter(pf);

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

	public AWTDisplayGraphics(final Graphics2D g2) {
		setGraphics2D(g2);
		sw.setRemoveDuplicatePoints(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (currentRenderer != null) { currentRenderer.dispose(); }
		if (normalRenderer != null) { normalRenderer.dispose(); }
		if (overlayRenderer != null) { overlayRenderer.dispose(); }
	}

	@Override
	public boolean beginDrawingLayers() {
		currentRenderer.setRenderingHints(data.isAntialias() ? QUALITY_RENDERING : SPEED_RENDERING);
		return true;
	}

	@Override
	public void beginDrawingLayer(final ILayer layer) {
		super.beginDrawingLayer(layer);
		pf.adapt();
	}

	@Override
	public void setOpacity(final double alpha) {
		super.setOpacity(alpha);
		currentRenderer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
	}

	@Override
	public Rectangle2D drawField(final IField fieldValues, final MeshDrawingAttributes attributes) {
		final List<?> textures = attributes.getTextures();
		if (textures == null) return null;
		final Object image = textures.get(0);
		if (image instanceof GamaFile) return drawFile((GamaFile<?, ?>) image, attributes);
		if (image instanceof BufferedImage) return drawImage((BufferedImage) image, attributes);
		return null;
	}

	@Override
	public Rectangle2D drawFile(final GamaFile<?, ?> file, final DrawingAttributes attributes) {
		final IScope scope = surface.getScope();
		if (file instanceof GamaImageFile)
			return drawImage(((GamaImageFile) file).getImage(scope, attributes.useCache()), attributes);
		if (!(file instanceof GamaGeometryFile)) return null;
		IShape shape = Cast.asGeometry(scope, file);
		if (shape == null) return null;
		final AxisAngle rotation = attributes.getRotation();
		shape = new GamaShape(shape, null, rotation, attributes.getLocation(), attributes.getSize(), true);
		final GamaColor c = attributes.getColor();
		return drawShape(shape.getInnerGeometry(),
				new ShapeDrawingAttributes(shape.getLocation().toGamaPoint(), c, c, (IShape.Type) null));
	}

	AffineTransform imageTransform = new AffineTransform();

	@Override
	public Rectangle2D drawImage(final BufferedImage img, final DrawingAttributes attributes) {
		// final AffineTransform saved = currentRenderer.getTransform();
		imageTransform.setToIdentity();
		double curX, curY;
		if (attributes.getLocation() == null) {
			curX = getXOffsetInPixels();
			curY = getYOffsetInPixels();
		} else {
			curX = xFromModelUnitsToPixels(attributes.getLocation().getX());
			curY = yFromModelUnitsToPixels(attributes.getLocation().getY());
		}
		imageTransform.translate(curX, curY);
		double curWidth, curHeight;
		if (attributes.getSize() == null) {
			curWidth = getLayerWidth();
			curHeight = getLayerHeight();
		} else {
			curWidth = wFromModelUnitsToPixels(attributes.getSize().getX());
			curHeight = hFromModelUnitsToPixels(attributes.getSize().getY());
		}

		if (attributes.getAngle() != null) {
			imageTransform.rotate(Maths.toRad * attributes.getAngle(), curWidth / 2d, curHeight / 2d);
			// currentRenderer.rotate(Maths.toRad * attributes.getAngle(), curX + curWidth / 2d, curY + curHeight / 2d);
		}

		imageTransform.scale(curWidth / img.getWidth(), curHeight / img.getHeight());
		currentRenderer.drawImage(img, imageTransform, null);
		// currentRenderer.drawImage(img, (int) FastMath.round(curX), (int) FastMath.round(curY), (int) curWidth,
		// (int) curHeight, null);
		if (attributes.getBorder() != null) { drawGridLine(img, attributes.getBorder()); }
		// currentRenderer.setTransform(saved);
		rect.setRect(curX, curY, curWidth, curHeight);
		if (highlight) { highlightRectangleInPixels(rect); }
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
	 * @param z
	 *            float (has no effect in 2D)
	 */

	@Override
	public Rectangle2D drawString(final String string, final TextDrawingAttributes attributes) {
		// Multiline: Issue #780
		if (string.contains("\n")) {
			final Rectangle2D result = new Rectangle2D.Double();
			for (final String s : string.split("\n")) {
				final Rectangle2D r = drawString(s, attributes);
				attributes.getLocation().setY(attributes.getLocation().getY() + r.getHeight());
				result.add(r);
			}
			return result;
		}
		currentRenderer.setColor(highlight ? data.getHighlightColor() : attributes.getColor());
		double curX, curY;
		// final double curZ;
		if (attributes.getLocation() == null) {
			curX = getXOffsetInPixels();
			curY = getYOffsetInPixels();
			// curZ = 0;
		} else {
			curX = xFromModelUnitsToPixels(attributes.getLocation().getX());
			curY = yFromModelUnitsToPixels(attributes.getLocation().getY());
		}
		setFont(attributes.getFont());
		final Rectangle2D r = currentRenderer.getFontMetrics().getStringBounds(string, currentRenderer);
		final float ascent = currentRenderer.getFontMetrics().getLineMetrics(string, currentRenderer).getAscent();
		final float descent = currentRenderer.getFontMetrics().getLineMetrics(string, currentRenderer).getDescent();
		r.setFrame(r.getX(), r.getY(), r.getWidth(), Math.min(r.getHeight(), ascent + descent));

		final double rWidth = r.getWidth();
		final double rHeight = r.getHeight();
		curX -= rWidth * attributes.anchor.x;
		curY += (rHeight - descent) * attributes.anchor.y;
		final AffineTransform saved = currentRenderer.getTransform();
		if (attributes.getAngle() != null) {
			currentRenderer.rotate(Maths.toRad * attributes.getAngle(), curX + r.getWidth() / 2,
					curY + r.getHeight() / 2);
		}

		currentRenderer.drawString(string, (int) curX, (int) curY);
		// currentRenderer.drawRect((int) curX, (int) (curY - rHeight), (int) rWidth, (int) rHeight);
		currentRenderer.setTransform(saved);
		r.setFrame(curX, curY, r.getWidth(), r.getHeight());
		return r;
	}

	private void setFont(final Font f) {
		final Font font = surface == null ? f : surface.computeFont(f);
		currentRenderer.setFont(font);
	}

	@Override
	public Rectangle2D drawShape(final Geometry geometry, final DrawingAttributes attributes) {
		if (geometry == null) return null;
		if (geometry instanceof GeometryCollection) {
			final Rectangle2D result = new Rectangle2D.Double();
			GeometryUtils.applyToInnerGeometries(geometry, (g) -> result.add(drawShape(g, attributes)));
			return result;
		}
		final boolean isLine = geometry instanceof Lineal || geometry instanceof Puntal;

		GamaColor border = isLine ? attributes.getColor() : attributes.getBorder();
		if (border == null && attributes.isEmpty()) { border = attributes.getColor(); }
		if (highlight) {
			attributes.setFill(GamaColor.getInt(data.getHighlightColor().getRGB()));
			if (border != null) { border = attributes.getColor(); }
		}
		final Shape s = sw.toShape(geometry);
		try {
			final Rectangle2D r = s.getBounds2D();
			currentRenderer.setColor(attributes.getColor());
			if (!isLine && !attributes.isEmpty()) { currentRenderer.fill(s); }
			if (isLine || border != null || attributes.isEmpty()) {
				if (border != null) { currentRenderer.setColor(border); }
				currentRenderer.draw(s);
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
		currentRenderer.setColor(bgColor);
		currentRenderer.fillRect(0, 0, (int) surface.getDisplayWidth(), (int) surface.getDisplayHeight());
	}

	public void drawGridLine(final BufferedImage image, final Color lineColor) {

		// The image contains the dimensions of the grid.
		final double stepx = (double) getLayerWidth() / (double) image.getWidth();
		final double stepy = (double) getLayerHeight() / (double) image.getHeight();
		if (stepx < 2 || stepy < 2) return;
		final Line2D line = new Line2D.Double();
		currentRenderer.setColor(lineColor);
		for (double step = 0.0, end = getLayerWidth(); step < end + 1; step += stepx) {
			line.setLine(getXOffsetInPixels() + step, getYOffsetInPixels(), getXOffsetInPixels() + step,
					getYOffsetInPixels() + getLayerHeight());
			currentRenderer.draw(line);
		}
		line.setLine(getXOffsetInPixels() + getLayerWidth() - 1, getYOffsetInPixels(),
				getXOffsetInPixels() + getLayerWidth() - 1, getYOffsetInPixels() + getLayerHeight() - 1);
		currentRenderer.draw(line);

		for (double step = 0.0, end = getLayerHeight(); step < end + 1; step += stepy) {
			line.setLine(getXOffsetInPixels(), getYOffsetInPixels() + step, getXOffsetInPixels() + getLayerWidth(),
					getYOffsetInPixels() + step);
			currentRenderer.draw(line);
		}
		line.setLine(getXOffsetInPixels(), getYOffsetInPixels() + getLayerHeight() - 1,
				getXOffsetInPixels() + getLayerWidth() - 1, getYOffsetInPixels() + getLayerHeight() - 1);
		currentRenderer.draw(line);

	}

	private void highlightRectangleInPixels(final Rectangle2D r) {
		if (r == null) return;
		final Stroke oldStroke = currentRenderer.getStroke();
		currentRenderer.setStroke(new BasicStroke(5));
		final Color old = currentRenderer.getColor();
		currentRenderer.setColor(data.getHighlightColor());
		currentRenderer.draw(r);
		currentRenderer.setStroke(oldStroke);
		currentRenderer.setColor(old);
	}

	/**
	 * Method is2D()
	 *
	 * @see msi.gama.common.interfaces.IGraphics#is2D()
	 */
	@Override
	public boolean is2D() {
		return true;
	}

	@Override
	public void beginOverlay(final OverlayLayer layer) {
		currentRenderer = overlayRenderer;
		currentRenderer.setColor(layer.getData().getBackgroundColor(getSurface().getScope()));
		final int x = (int) getXOffsetInPixels();
		final int y = (int) getYOffsetInPixels();
		final int w = getLayerWidth();
		final int h = getLayerHeight();
		if (layer.getData().isRounded()) {
			currentRenderer.fillRoundRect(x, y, w, h, 10, 10);
		} else {
			currentRenderer.fillRect(x, y, w, h);
		}
		if (layer.getData().getBorderColor() != null) {
			currentRenderer.setColor(layer.getData().getBorderColor());
			if (layer.getData().isRounded()) {
				currentRenderer.drawRoundRect(x, y, w, h, 10, 10);
			} else {
				currentRenderer.drawRect(x, y, w, h);
			}
		}
	}

	@Override
	public void endOverlay() {
		currentRenderer = normalRenderer;
	}

	public void setGraphics2D(final Graphics2D g) {
		normalRenderer = g;
		currentRenderer = g;
		if (g != null) { setFont(defaultFont); }
	}

	public void setUntranslatedGraphics2D(final Graphics2D g) {
		overlayRenderer = g;
	}

	@Override
	public boolean cannotDraw() {
		return false;
	}

	@Override
	public void accumulateTemporaryEnvelope(final Rectangle2D env) {
		if (temporaryEnvelope == null) {
			temporaryEnvelope = env;
		} else {
			temporaryEnvelope.add(env);
		}
	}

	@Override
	public Rectangle2D getAndWipeTemporaryEnvelope() {
		final Rectangle2D result = temporaryEnvelope;
		temporaryEnvelope = null;
		return result;
	}

	final Rectangle2D chartRect = new Rectangle2D.Double();

	@Override
	public Rectangle2D drawChart(final ChartOutput chart) {

		final BufferedImage im =
				chart.getImage(getLayerWidth(), getLayerHeight(), getSurface().getData().isAntialias());
		currentRenderer.drawImage(im, (int) getXOffsetInPixels(), (int) getYOffsetInPixels(), null);
		return chartRect;
	}

}
