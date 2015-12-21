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
import msi.gama.common.interfaces.*;
import msi.gama.outputs.LayeredDisplayData;

public abstract class AbstractDisplayGraphics implements IGraphics {

	protected final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	protected double currentAlpha = 1;
	protected double widthOfLayerInPixels;
	protected double heightOfLayerInPixels;
	protected int xOffsetInPixels;
	protected int yOffsetInPixels;
	protected double xRatioBetweenPixelsAndModelUnits;
	protected double yRatioBetweenPixelsAndModelUnits;
	final protected double widthOfDisplayInPixels;
	final protected double heightOfDisplayInPixels;
	final protected double widthOfEnvironmentInModelUnits;
	final protected double heightOfEnvironmentInModelUnits;
	final protected LayeredDisplayData data;

	public AbstractDisplayGraphics(final IDisplaySurface surface) {
		widthOfEnvironmentInModelUnits = surface.getEnvWidth();
		heightOfEnvironmentInModelUnits = surface.getEnvHeight();
		widthOfDisplayInPixels = surface.getDisplayWidth();
		heightOfDisplayInPixels = surface.getDisplayHeight();
		xRatioBetweenPixelsAndModelUnits = widthOfDisplayInPixels / widthOfEnvironmentInModelUnits;
		yRatioBetweenPixelsAndModelUnits = heightOfDisplayInPixels / heightOfEnvironmentInModelUnits;
		data = surface.getData();
		initFor(surface);
	}

	@Override
	public void initFor(final IDisplaySurface surface) {

		// setQualityRendering(surface.getQualityRendering());
	}

	@Override
	public int getDisplayWidthInPixels() {
		return (int) widthOfDisplayInPixels;
	}

	@Override
	public int getDisplayHeightInPixels() {
		return (int) heightOfDisplayInPixels;
	}

	@Override
	public void setOpacity(final double alpha) {
		// 1 means opaque ; 0 means transparent
		// if ( IntervalSize.isZeroWidth(alpha, currentAlpha) ) { return; }
		currentAlpha = alpha;
	}

	protected final double xFromModelUnitsToPixels(final double mu) {
		return xOffsetInPixels + xRatioBetweenPixelsAndModelUnits * mu /* + 0.5 */;
	}

	protected final double yFromModelUnitsToPixels(final double mu) {
		return yOffsetInPixels + yRatioBetweenPixelsAndModelUnits * mu;
	}

	protected final double wFromModelUnitsToPixels(final double mu) {
		return xRatioBetweenPixelsAndModelUnits * mu;
	}

	protected final double hFromModelUnitsToPixels(final double mu) {
		return yRatioBetweenPixelsAndModelUnits * mu;
	}

	// protected double wFromPixelsToModelUnits(final int px) {
	// return px / xRatioBetweenPixelsAndModelUnits;
	// }
	//
	// protected double hFromPixelsToModelUnits(final int py) {
	// return py / yRatioBetweenPixelsAndModelUnits;
	// }

	// protected double xFromPixelsToModelUnits(final int px) {
	// double mu = (px - xOffsetInPixels) / xRatioBetweenPixelsAndModelUnits;
	// return mu;
	// }
	//
	// protected double yFromPixelsToModelUnits(final int px) {
	// double mu = (px - yOffsetInPixels) / yRatioBetweenPixelsAndModelUnits;
	// return mu;
	// }

	@Override
	public double getxRatioBetweenPixelsAndModelUnits() {
		return xRatioBetweenPixelsAndModelUnits;
	}

	@Override
	public double getyRatioBetweenPixelsAndModelUnits() {
		return yRatioBetweenPixelsAndModelUnits;
	}

	@Override
	public double getXOffsetInPixels() {
		return xOffsetInPixels;
	}

	@Override
	public double getYOffsetInPixels() {
		return yOffsetInPixels;
	}

	@Override
	public void beginDrawingLayers() {}

	@Override
	public void beginDrawingLayer(final ILayer layer) {
		xOffsetInPixels = layer.getPositionInPixels().x;
		yOffsetInPixels = layer.getPositionInPixels().y;
		widthOfLayerInPixels = layer.getSizeInPixels().x;
		heightOfLayerInPixels = layer.getSizeInPixels().y;
		xRatioBetweenPixelsAndModelUnits = widthOfLayerInPixels / widthOfEnvironmentInModelUnits;
		yRatioBetweenPixelsAndModelUnits = heightOfLayerInPixels / heightOfEnvironmentInModelUnits;
	}

	@Override
	public void endDrawingLayer(final ILayer layer) {
		xRatioBetweenPixelsAndModelUnits = widthOfDisplayInPixels / widthOfEnvironmentInModelUnits;
		yRatioBetweenPixelsAndModelUnits = heightOfDisplayInPixels / heightOfEnvironmentInModelUnits;
	}

	/**
	 * Method getZoomLevel()
	 * @see msi.gama.common.interfaces.IGraphics#getZoomLevel()
	 */
	@Override
	public Double getZoomLevel() {
		return data.getZoomLevel();
	}

}