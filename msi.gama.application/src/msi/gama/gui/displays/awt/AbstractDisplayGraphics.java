package msi.gama.gui.displays.awt;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

public abstract class AbstractDisplayGraphics implements IGraphics {

	protected Color highlightColor = new Color(GuiUtils.defaultHighlight[0], GuiUtils.defaultHighlight[1],
		GuiUtils.defaultHighlight[2]);
	protected final Rectangle2D rect = new Rectangle2D.Double(0, 0, 1, 1);
	protected double currentAlpha = 1;
	protected Integer widthOfLayerInPixels;
	protected Integer heightOfLayerInPixels;
	protected Integer xOffsetInPixels;
	protected Integer yOffsetInPixels;
	protected Double xRatioBetweenPixelsAndModelUnits;
	protected Double yRatioBetweenPixelsAndModelUnits;
	protected final Integer widthOfDisplayInPixels;
	protected final Integer heightOfDisplayInPixels;
	protected final Integer widthOfEnvironmentInModelUnits;
	protected final Integer heightOfEnvironmentInModelUnits;

	public AbstractDisplayGraphics(IDisplaySurface surface) {
		widthOfEnvironmentInModelUnits = (int) surface.getEnvWidth();
		heightOfEnvironmentInModelUnits = (int) surface.getEnvHeight();
		widthOfDisplayInPixels = surface.getDisplayWidth();
		heightOfDisplayInPixels = surface.getDisplayHeight();
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
	public abstract void beginHighlight();

	@Override
	public abstract void endHighlight();

	//
	// @Override
	// public int[] getHighlightColor() {
	// return highlightColor;
	// }

	@Override
	public void setQualityRendering(final boolean quality) {}

	@Override
	public void setHighlightColor(final int[] rgb) {
		highlightColor = new Color(rgb[0], rgb[1], rgb[2]);
	}

	@Override
	public void setOpacity(final double alpha) {
		// 1 means opaque ; 0 means transparent
		if ( IntervalSize.isZeroWidth(alpha, currentAlpha) ) { return; }
		currentAlpha = alpha;
	}

	public double getCurrentAlpha() {
		return currentAlpha;
	}

	protected final int xFromModelUnitsToPixels(double mu) {
		return xOffsetInPixels + (int) (xRatioBetweenPixelsAndModelUnits * mu /* + 0.5 */);
	}

	protected final int yFromModelUnitsToPixels(double mu) {
		return yOffsetInPixels + (int) (yRatioBetweenPixelsAndModelUnits * mu /* + 0.5 */);
	}

	protected final int wFromModelUnitsToPixels(double mu) {
		return (int) (xRatioBetweenPixelsAndModelUnits * mu /* +0.5 */);
	}

	protected final int hFromModelUnitsToPixels(double mu) {
		return (int) (yRatioBetweenPixelsAndModelUnits * mu /* + 0.5 */);
	}

	protected double wFromPixelsToModelUnits(int px) {
		return px / xRatioBetweenPixelsAndModelUnits;
	}

	protected double hFromPixelsToModelUnits(int px) {
		return px / yRatioBetweenPixelsAndModelUnits;
	}

	protected double xFromPixelsToModelUnits(int px) {
		double mu = (px - xOffsetInPixels) / xRatioBetweenPixelsAndModelUnits;
		return mu;
	}

	protected double yFromPixelsToModelUnits(int px) {
		double mu = (px - yOffsetInPixels) / yRatioBetweenPixelsAndModelUnits;
		return mu;
	}

	@Override
	public int getEnvironmentWidth() {
		return widthOfEnvironmentInModelUnits;
	}

	@Override
	public int getEnvironmentHeight() {
		return heightOfEnvironmentInModelUnits;
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
	public void beginDrawingLayers() {}

	@Override
	public void beginDrawingLayer(ILayer layer) {
		xOffsetInPixels = layer.getPositionInPixels().x;
		yOffsetInPixels = layer.getPositionInPixels().y;
		widthOfLayerInPixels = layer.getSizeInPixels().x;
		heightOfLayerInPixels = layer.getSizeInPixels().y;
		xRatioBetweenPixelsAndModelUnits = (double) widthOfLayerInPixels / (double) widthOfEnvironmentInModelUnits;
		yRatioBetweenPixelsAndModelUnits = (double) heightOfLayerInPixels / (double) heightOfEnvironmentInModelUnits;
	}

	@Override
	public void endDrawingLayer(ILayer layer) {
		xOffsetInPixels = null;
		yOffsetInPixels = null;
		widthOfLayerInPixels = null;
		heightOfLayerInPixels = null;
		xRatioBetweenPixelsAndModelUnits = (double) widthOfDisplayInPixels / (double) widthOfEnvironmentInModelUnits;
		yRatioBetweenPixelsAndModelUnits = (double) heightOfDisplayInPixels / (double) heightOfEnvironmentInModelUnits;
	}

}