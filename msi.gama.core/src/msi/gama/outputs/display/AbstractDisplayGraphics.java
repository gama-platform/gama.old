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

public abstract class AbstractDisplayGraphics implements IGraphics {

	protected final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	protected double currentAlpha = 1;
	protected int widthOfLayerInPixels;
	protected int heightOfLayerInPixels;
	protected int xOffsetInPixels;
	protected int yOffsetInPixels;
	protected double xRatioBetweenPixelsAndModelUnits;
	protected double yRatioBetweenPixelsAndModelUnits;
	final protected int widthOfDisplayInPixels;
	final protected int heightOfDisplayInPixels;
	final protected double widthOfEnvironmentInModelUnits;
	final protected double heightOfEnvironmentInModelUnits;

	public AbstractDisplayGraphics(final IDisplaySurface surface) {
		widthOfEnvironmentInModelUnits = surface.getEnvWidth();
		heightOfEnvironmentInModelUnits = surface.getEnvHeight();
		widthOfDisplayInPixels = surface.getDisplayWidth();
		heightOfDisplayInPixels = surface.getDisplayHeight();
		xRatioBetweenPixelsAndModelUnits = widthOfDisplayInPixels / widthOfEnvironmentInModelUnits;
		yRatioBetweenPixelsAndModelUnits = heightOfDisplayInPixels / heightOfEnvironmentInModelUnits;
		initFor(surface);
	}

	@Override
	public void initFor(final IDisplaySurface surface) {

		// setQualityRendering(surface.getQualityRendering());
	}

	@Override
	public int getDisplayWidthInPixels() {
		return widthOfDisplayInPixels;
	}

	@Override
	public int getDisplayHeightInPixels() {
		return heightOfDisplayInPixels;
	}

	@Override
	public void setOpacity(final double alpha) {
		// 1 means opaque ; 0 means transparent
		// if ( IntervalSize.isZeroWidth(alpha, currentAlpha) ) { return; }
		currentAlpha = alpha;
	}

	protected final int xFromModelUnitsToPixels(final double mu) {
		return xOffsetInPixels + (int) (xRatioBetweenPixelsAndModelUnits * mu /* + 0.5 */);
	}

	protected final int yFromModelUnitsToPixels(final double mu) {
		return yOffsetInPixels + (int) (yRatioBetweenPixelsAndModelUnits * mu /* + 0.5 */);
	}

	protected final int wFromModelUnitsToPixels(final double mu) {
		return (int) (xRatioBetweenPixelsAndModelUnits * mu /* +0.5 */);
	}

	protected final int hFromModelUnitsToPixels(final double mu) {
		return (int) (yRatioBetweenPixelsAndModelUnits * mu /* + 0.5 */);
	}

	protected double wFromPixelsToModelUnits(final int px) {
		return px / xRatioBetweenPixelsAndModelUnits;
	}

	protected double hFromPixelsToModelUnits(final int py) {
		return py / yRatioBetweenPixelsAndModelUnits;
	}

	protected double xFromPixelsToModelUnits(final int px) {
		double mu = (px - xOffsetInPixels) / xRatioBetweenPixelsAndModelUnits;
		return mu;
	}

	protected double yFromPixelsToModelUnits(final int px) {
		double mu = (px - yOffsetInPixels) / yRatioBetweenPixelsAndModelUnits;
		return mu;
	}

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

}