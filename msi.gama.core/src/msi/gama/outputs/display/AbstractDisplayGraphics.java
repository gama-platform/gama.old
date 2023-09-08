/*******************************************************************************************************
 *
 * AbstractDisplayGraphics.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.display;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.runtime.GAMA;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class AbstractDisplayGraphics.
 */
public abstract class AbstractDisplayGraphics implements IGraphics {

	/** The cached GC. */
	private static GraphicsConfiguration cachedGC;

	/**
	 * Gets the cached GC.
	 *
	 * @return the cached GC
	 */
	public static GraphicsConfiguration getCachedGC() {
		if (cachedGC == null) {
			DEBUG.OUT("Creating cached Graphics Configuration");
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			DEBUG.OUT("Local Graphics Environment selected");
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			DEBUG.OUT("Default Graphics Device selected");
			cachedGC = gd.getDefaultConfiguration();
			DEBUG.OUT("Default Graphics Configuration selected");
		}
		return cachedGC;
	}

	/**
	 * Creates the compatible image.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param forOpenGL
	 *            the for open GL
	 * @return the buffered image
	 */
	public static BufferedImage createCompatibleImage(final int width, final int height) {
		BufferedImage newImage = null;
		if (GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless()) {
			newImage = new BufferedImage(width > 0 ? width : 1024, height > 0 ? height : 1024,
					BufferedImage.TYPE_INT_ARGB);
		} else {
			newImage = getCachedGC().createCompatibleImage(width, height);
		}
		return newImage;
	}

	/**
	 * To compatible image.
	 *
	 * @param image
	 *            the image
	 * @return the buffered image
	 */
	public static BufferedImage toCompatibleImage(final BufferedImage image) {
		// if image is already compatible and optimized for current system settings, simply return it
		if (GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless()
				|| image.getColorModel().equals(getCachedGC().getColorModel()))
			return image;
		// image is not optimized, so create a new image that is
		final BufferedImage newImage =
				getCachedGC().createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
		final Graphics2D g2d = (Graphics2D) newImage.getGraphics();
		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		// return the new optimized image
		return newImage;
	}

	/** The rect. */
	protected final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);

	/** The Constant origin. */
	protected static final GamaPoint origin = new GamaPoint(0, 0);

	/** The current layer alpha. */
	protected double currentLayerAlpha = 1;

	/** The data. */
	public LayeredDisplayData data;

	/** The surface. */
	protected IDisplaySurface surface;

	/** The highlight. */
	public boolean highlight = false;

	/** The random number generator specific to this graphics. See Issue #3250. */
	private final RandomUtils random = new RandomUtils();

	/** The current layer. */
	protected ILayer currentLayer;

	@Override
	public void setDisplaySurface(final IDisplaySurface surface) {
		this.surface = surface;
		data = surface.getData();
	}

	@Override
	public boolean isNotReadyToUpdate() { return surface.isDisposed(); }

	@Override
	public void dispose() {
		currentLayer = null;
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
	public void setAlpha(final double alpha) {
		// 1 means opaque ; 0 means transparent
		currentLayerAlpha = alpha;
	}

	/**
	 * X from model units to pixels.
	 *
	 * @param mu
	 *            the mu
	 * @return the double
	 */
	protected final double xFromModelUnitsToPixels(final double mu) {
		return getXOffsetInPixels() + getxRatioBetweenPixelsAndModelUnits() * mu /* + 0.5 */;
	}

	/**
	 * Y from model units to pixels.
	 *
	 * @param mu
	 *            the mu
	 * @return the double
	 */
	protected final double yFromModelUnitsToPixels(final double mu) {
		return getYOffsetInPixels() + getyRatioBetweenPixelsAndModelUnits() * mu;
	}

	@Override
	public double getAbsoluteRatioBetweenPixelsAndModelsUnits() {
		return Math.min(getyRatioBetweenPixelsAndModelUnits(), getxRatioBetweenPixelsAndModelUnits());

		// return Math.min(surface.getHeight() / data.getEnvHeight(), surface.getWidth() / data.getEnvWidth());
	}

	/**
	 * W from model units to pixels.
	 *
	 * @param mu
	 *            the mu
	 * @return the double
	 */
	protected final double wFromModelUnitsToPixels(final double mu) {
		return getxRatioBetweenPixelsAndModelUnits() * mu;
	}

	/**
	 * H from model units to pixels.
	 *
	 * @param mu
	 *            the mu
	 * @return the double
	 */
	protected final double hFromModelUnitsToPixels(final double mu) {
		return getyRatioBetweenPixelsAndModelUnits() * mu;
	}

	@Override
	public double getxRatioBetweenPixelsAndModelUnits() {
		if (currentLayer == null) return getDisplayWidth() / data.getEnvWidth();
		return currentLayer.getData().getSizeInPixels().x / data.getEnvWidth();
	}

	@Override
	public double getyRatioBetweenPixelsAndModelUnits() {
		if (currentLayer == null) return getDisplayHeight() / data.getEnvHeight();
		if (currentLayer instanceof OverlayLayer) return getxRatioBetweenPixelsAndModelUnits();
		return currentLayer.getData().getSizeInPixels().y / data.getEnvHeight();
	}

	@Override
	public double getXOffsetInPixels() {
		return currentLayer == null ? origin.x : currentLayer.getData().getPositionInPixels().getX();
	}

	@Override
	public double getYOffsetInPixels() {
		return currentLayer == null ? origin.y : currentLayer.getData().getPositionInPixels().getY();
	}

	@Override
	public boolean beginDrawingLayers() {
		return true;
	}

	@Override
	public void beginOverlay(final OverlayLayer layer) {}

	@Override
	public void endOverlay() {}

	@Override
	public void beginDrawingLayer(final ILayer layer) {
		currentLayer = layer;

	}

	@Override
	public void endDrawingLayer(final ILayer layer) {
		currentLayer = null;
	}

	@Override
	public void endDrawingLayers() {}

	@Override
	public Double getZoomLevel() { return data.getZoomLevel(); }

	@Override
	public IDisplaySurface getSurface() { return surface; }

	@Override
	public int getViewWidth() { return surface.getWidth(); }

	@Override
	public int getViewHeight() { return surface.getHeight(); }

	@Override
	public int getDisplayWidth() { return (int) surface.getDisplayWidth(); }

	@Override
	public int getDisplayHeight() { return (int) surface.getDisplayHeight(); }

	/**
	 * Gets the layer width.
	 *
	 * @return the layer width
	 */
	public int getLayerWidth() {
		return currentLayer == null ? getDisplayWidth() : currentLayer.getData().getSizeInPixels().x;
	}

	/**
	 * Gets the layer height.
	 *
	 * @return the layer height
	 */
	public int getLayerHeight() {
		return currentLayer == null ? getDisplayHeight() : currentLayer.getData().getSizeInPixels().y;
	}

	@Override
	public Envelope getVisibleRegion() { return surface.getVisibleRegionForLayer(currentLayer); }

	@Override
	public RandomUtils getRandom() { return random; }

}