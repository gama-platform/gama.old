/*********************************************************************************************
 *
 *
 * 'AbstractDisplayGraphics.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.display;

import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.layers.OverlayLayer;

public abstract class AbstractDisplayGraphics implements IGraphics {

	protected final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	protected static final GamaPoint origin = new GamaPoint(0, 0);
	protected double currentAlpha = 1;
	public final LayeredDisplayData data;
	protected final IDisplaySurface surface;
	protected boolean highlight = false;

	protected ILayer currentLayer;

	public AbstractDisplayGraphics(final IDisplaySurface surface) {
		this.surface = surface;
		data = surface.getData();
	}

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
	public void setOpacity(final double alpha) {
		// 1 means opaque ; 0 means transparent
		currentAlpha = alpha;
	}

	protected final double xFromModelUnitsToPixels(final double mu) {
		return getXOffsetInPixels()
				+ getxRatioBetweenPixelsAndModelUnits() * mu /* + 0.5 */;
	}

	protected final double yFromModelUnitsToPixels(final double mu) {
		return getYOffsetInPixels() + getyRatioBetweenPixelsAndModelUnits() * mu;
	}

	protected final double wFromModelUnitsToPixels(final double mu) {
		return getxRatioBetweenPixelsAndModelUnits() * mu;
	}

	protected final double hFromModelUnitsToPixels(final double mu) {
		return getyRatioBetweenPixelsAndModelUnits() * mu;
	}

	@Override
	public double getxRatioBetweenPixelsAndModelUnits() {
		if (currentLayer == null) {
			return getDisplayWidth() / data.getEnvWidth();
		}
		return currentLayer.getSizeInPixels().x / data.getEnvWidth();
	}

	@Override
	public double getyRatioBetweenPixelsAndModelUnits() {
		if (currentLayer == null) {
			return getDisplayHeight() / data.getEnvHeight();
		} else if (currentLayer instanceof OverlayLayer) {
			return getxRatioBetweenPixelsAndModelUnits();
		} else {
			return currentLayer.getSizeInPixels().y / data.getEnvHeight();
		}
	}

	@Override
	public double getXOffsetInPixels() {
		return currentLayer == null ? origin.x : currentLayer.getPositionInPixels().getX();
	}

	@Override
	public double getYOffsetInPixels() {
		return currentLayer == null ? origin.y : currentLayer.getPositionInPixels().getY();
	}

	@Override
	public boolean beginDrawingLayers() {
		return true;
	}

	@Override
	public void beginDrawingLayer(final ILayer layer) {
		currentLayer = layer;
	}

	@Override
	public void endDrawingLayer(final ILayer layer) {
		currentLayer = null;
	}

	@Override
	public void endDrawingLayers() {
	}

	@Override
	public Double getZoomLevel() {
		return data.getZoomLevel();
	}

	@Override
	public IDisplaySurface getSurface() {
		return surface;
	}

	@Override
	public int getViewWidth() {
		return surface.getWidth();
	}

	@Override
	public int getViewHeight() {
		return surface.getHeight();
	}

	@Override
	public int getDisplayWidth() {
		return (int) surface.getDisplayWidth();
	}

	@Override
	public int getDisplayHeight() {
		return (int) surface.getDisplayHeight();
	}

	public int getLayerWidth() {
		return currentLayer == null ? getDisplayWidth() : currentLayer.getSizeInPixels().x;
	}

	public int getLayerHeight() {
		return currentLayer == null ? getDisplayHeight() : currentLayer.getSizeInPixels().y;
	}

	@Override
	public Envelope getVisibleRegion() {
		return surface.getVisibleRegionForLayer(currentLayer);
	}

}