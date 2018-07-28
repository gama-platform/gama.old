/*********************************************************************************************
 *
 * 'IGraphics.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.outputs.layers.charts.ChartOutput;
import msi.gama.util.file.GamaFile;
import msi.gaml.statements.draw.FieldDrawingAttributes;
import msi.gaml.statements.draw.FileDrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;

/**
 * Written by drogoul Modified on 22 janv. 2011
 *
 * @todo Description
 *
 */
public interface IGraphics {

	public static final RenderingHints QUALITY_RENDERING = new RenderingHints(null);
	public static final RenderingHints SPEED_RENDERING = new RenderingHints(null);
	public static final RenderingHints MEDIUM_RENDERING = new RenderingHints(null);

	public void setDisplaySurface(final IDisplaySurface surface);

	public abstract int getDisplayWidth();

	public abstract int getDisplayHeight();

	public abstract Rectangle2D drawFile(GamaFile<?, ?> file, FileDrawingAttributes attributes);

	public abstract Rectangle2D drawField(final double[] values, final FieldDrawingAttributes attributes);

	public abstract Rectangle2D drawImage(final BufferedImage img, final FileDrawingAttributes attributes);

	public abstract Rectangle2D drawChart(ChartOutput chart);

	public abstract Rectangle2D drawString(final String string, final TextDrawingAttributes attributes);

	public abstract Rectangle2D drawShape(final Geometry shape, final ShapeDrawingAttributes attributes);

	public abstract void setOpacity(double i);

	public abstract void fillBackground(Color bgColor, double opacity);

	public abstract boolean beginDrawingLayers();

	public abstract void beginDrawingLayer(ILayer layer);

	public abstract void beginOverlay(OverlayLayer layer);

	public abstract void endOverlay();

	public abstract double getyRatioBetweenPixelsAndModelUnits();

	public abstract double getxRatioBetweenPixelsAndModelUnits();

	/*
	 * Returns the region of the current layer (in model units) that is visible on screen
	 */
	public abstract Envelope getVisibleRegion();

	public abstract void endDrawingLayer(ILayer layer);

	public abstract void endDrawingLayers();

	public abstract void beginHighlight();

	public abstract void endHighlight();

	public double getXOffsetInPixels();

	public double getYOffsetInPixels();

	public abstract Double getZoomLevel();

	public abstract ILocation getCameraPos();

	public abstract ILocation getCameraTarget();

	public abstract ILocation getCameraOrientation();

	public abstract boolean is2D();

	public abstract int getViewWidth();

	public abstract int getViewHeight();

	public IDisplaySurface getSurface();

	public void dispose();

	boolean cannotDraw();

	public abstract boolean isNotReadyToUpdate();

	public int getWidthForOverlay();

	public int getHeightForOverlay();

	/**
	 * Ask the IGraphics instance to accumulate temporary envelopes
	 * 
	 * @param env
	 */
	public default void accumulateTemporaryEnvelope(final Rectangle2D env) {}

	public default Rectangle2D getAndWipeTemporaryEnvelope() {
		return null;
	}

}