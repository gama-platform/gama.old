/*******************************************************************************************************
 *
 * AWTDisplayGraphics.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.locationtech.jts.awt.PointTransformation;
import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Lineal;
import org.locationtech.jts.geom.Puntal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IAsset;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.outputs.layers.charts.ChartOutput;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.matrix.GamaField;
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

public class AWTDisplayGraphics extends AbstractDisplayGraphics implements ImageObserver {

	/** The normal renderer. */
	private Graphics2D currentRenderer, overlayRenderer, normalRenderer;

	/** The temporary envelope. */
	private Rectangle2D temporaryEnvelope = null;

	/** The Constant defaultFont. */
	private static final Font defaultFont = new Font("Helvetica", Font.BOLD, 12);

	/**
	 * The Class Java2DPointTransformation.
	 */
	private class Java2DPointTransformation implements PointTransformation {

		/** The y ratio. */
		double xOffset, yOffset, xRatio, yRatio;

		@Override
		public void transform(final Coordinate src, final Point2D dest) {
			dest.setLocation(xOffset + xRatio * src.x, yOffset + yRatio * src.y);

		}

		/**
		 * Adapt.
		 */
		public void adapt() {
			xOffset = getXOffsetInPixels();
			yOffset = getYOffsetInPixels();
			xRatio = getxRatioBetweenPixelsAndModelUnits();
			yRatio = getyRatioBetweenPixelsAndModelUnits();
		}

	}

	/** The pf. */
	private final Java2DPointTransformation pf = new Java2DPointTransformation();

	/** The sw. */
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

	/**
	 * Instantiates a new AWT display graphics.
	 *
	 * @param g2
	 *            the g 2
	 */
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
	public void setAlpha(final double alpha) {
		super.setAlpha(alpha);
		currentRenderer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
	}

	@Override
	public Rectangle2D drawField(final IField fieldValues, final MeshDrawingAttributes attributes) {
		final List<?> textures = attributes.getTextures();
		// if (textures == null) return null;
		if (textures != null) {
			final Object image = textures.get(0);
			if (image instanceof IImageProvider im) return drawAsset(im, attributes);
			if (image instanceof BufferedImage bi) return drawImage(bi, attributes);
		}
		if (!(fieldValues instanceof GamaField gf)) return null;
		GamaField flatten = gf.flatten(null, attributes.getColorProvider());
		// AD Attempt to provide smoothing but it doesnt work as expected
		// double[] data = attributes.getSmoothProvider().smooth(flatten.numCols, flatten.numRows, flatten.getMatrix(),
		// flatten.getNoData(null), attributes.getSmooth());
		// System.arraycopy(data, 0, flatten.getMatrix(), 0, data.length);
		attributes.setSize(null);
		return drawImage(flatten.getImage(null), attributes);

	}

	@Override
	public Rectangle2D drawAsset(final IAsset file, final DrawingAttributes attributes) {
		final IScope scope = surface.getScope();
		if (file instanceof IImageProvider im)
			return drawImage(im.getImage(scope, attributes.useCache(), false), attributes);
		if (!(file instanceof GamaGeometryFile)) return null;
		IShape shape = Cast.asGeometry(scope, file);
		if (shape == null) return null;
		final AxisAngle rotation = attributes.getRotation();
		shape = new GamaShape(shape, null, rotation, attributes.getLocation(), attributes.getSize(), true);
		final GamaColor c = attributes.getColor();
		return drawShape(shape.getInnerGeometry(),
				new ShapeDrawingAttributes(shape.getLocation(), c, c, (IShape.Type) null));
	}

	/** The image transform. */
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

		BufferedImage after = img;
		Point2D.Double point = new Point2D.Double(curWidth, curHeight);
		try {
			Map<Point2D, BufferedImage> sizes = cache.get(img);
			if (sizes.containsKey(point)) {
				after = sizes.get(point);
			} else {
				after = resize(img, (int) Math.round(curWidth), (int) Math.round(curHeight));
			}
		} catch (ExecutionException e) {}

		// imageTransform.scale(curWidth / img.getWidth(), curHeight / img.getHeight());
		currentRenderer.drawImage(after, imageTransform, null);
		// currentRenderer.drawImage(img, (int) FastMath.round(curX), (int) FastMath.round(curY), (int) curWidth,
		// (int) curHeight, null);
		if (attributes.getBorder() != null) { drawGridLine(img, attributes.getBorder()); }
		// currentRenderer.setTransform(saved);
		rect.setRect(curX, curY, curWidth, curHeight);
		if (highlight) { highlightRectangleInPixels(rect); }
		return rect.getBounds2D();
	}

	/**
	 * Resize.
	 *
	 * @param img
	 *            the img
	 * @param newW
	 *            the new W
	 * @param newH
	 *            the new H
	 * @return the buffered image
	 */
	public static BufferedImage resize(final BufferedImage img, final int newW, final int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	/** The cache. */
	@SuppressWarnings ({ "unchecked",
			"unused" }) static LoadingCache<BufferedImage, Map<Point2D, BufferedImage>> cache =
					CacheBuilder.<BufferedImage, Map<Point2D, BufferedImage>> newBuilder()
							.expireAfterAccess(30, TimeUnit.SECONDS).removalListener(notif -> {
								Map<Point2D, BufferedImage> map = (Map<Point2D, BufferedImage>) notif.getValue();
								map.forEach((a, b) -> { b.flush(); });
							}).build(new CacheLoader<>() {

								@Override
								public Map<Point2D, BufferedImage> load(final BufferedImage arg0) throws Exception {
									return new HashMap<>();
								}
							});

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

	/**
	 * Sets the font.
	 *
	 * @param f
	 *            the new font
	 */
	private void setFont(final Font f) {
		final Font font = surface == null ? f : surface.computeFont(f);
		currentRenderer.setFont(font);
	}

	@Override
	public Rectangle2D drawShape(final Geometry geometry, final DrawingAttributes attributes) {
		if (geometry == null) return null;
		if (geometry instanceof GeometryCollection) {
			final Rectangle2D result = new Rectangle2D.Double();
			GeometryUtils.applyToInnerGeometries(geometry, g -> result.add(drawShape(g, attributes)));
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
	public void fillBackground(final Color bgColor) {
		setAlpha(1);
		currentRenderer.setColor(bgColor);
		currentRenderer.fillRect(0, 0, (int) surface.getDisplayWidth(), (int) surface.getDisplayHeight());
	}

	/**
	 * Draw grid line.
	 *
	 * @param image
	 *            the image
	 * @param lineColor
	 *            the line color
	 */
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

	/**
	 * Highlight rectangle in pixels.
	 *
	 * @param r
	 *            the r
	 */
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

	/**
	 * Sets the graphics 2 D.
	 *
	 * @param g
	 *            the new graphics 2 D
	 */
	public void setGraphics2D(final Graphics2D g) {
		normalRenderer = g;
		currentRenderer = g;
		if (g != null) { setFont(defaultFont); }
	}

	/**
	 * Sets the untranslated graphics 2 D.
	 *
	 * @param g
	 *            the new untranslated graphics 2 D
	 */
	public void setUntranslatedGraphics2D(final Graphics2D g) { overlayRenderer = g; }

	// @Override
	// public boolean cannotDraw() {
	// return false;
	// }

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

	/** The chart rect. */
	final Rectangle2D chartRect = new Rectangle2D.Double();

	/** The draw chart. */
	boolean drawChart = true;

	@Override
	public Rectangle2D drawChart(final ChartOutput chart) {
		if (!drawChart) return chartRect;
		final BufferedImage im =
				chart.getImage(getLayerWidth(), getLayerHeight(), getSurface().getData().isAntialias());
		drawChart = currentRenderer.drawImage(im, (int) getXOffsetInPixels(), (int) getYOffsetInPixels(), this);
		return chartRect;
	}

	@Override
	public boolean imageUpdate(final Image img, final int flags, final int x, final int y, final int width,
			final int height) {
		drawChart = (flags & ALLBITS) == 0;
		return drawChart;
	}
}
