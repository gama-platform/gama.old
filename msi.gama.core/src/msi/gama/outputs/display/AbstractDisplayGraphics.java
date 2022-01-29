/*******************************************************************************************************
 *
 * AbstractDisplayGraphics.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.display;

import java.awt.geom.Rectangle2D;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.layers.OverlayLayer;

/**
 * The Class AbstractDisplayGraphics.
 */
public abstract class AbstractDisplayGraphics implements IGraphics {

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

	public RandomUtils getRandom() {
		return random;
	}

}