/*******************************************************************************************************
 *
 * IGraphics.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.outputs.layers.charts.ChartOutput;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.MeshDrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;

/**
 * Written by drogoul Modified on 22 janv. 2011
 *
 * @todo Description
 *
 */
public interface IGraphics {

	/**
	 * Gets the random number generator specific to this graphics. See Issue #3250.
	 *
	 * @return the random
	 */
	RandomUtils getRandom();

	/**
	 * The Interface ThreeD.
	 */
	public interface ThreeD extends IGraphics {

		/**
		 * Checks if is 2d.
		 *
		 * @return true, if is 2d
		 */
		@Override
		default boolean is2D() {
			return false;
		}

		/**
		 * Gets the camera pos.
		 *
		 * @return the camera pos
		 */
		GamaPoint getCameraPos();

		/**
		 * Gets the camera target.
		 *
		 * @return the camera target
		 */
		GamaPoint getCameraTarget();

		/**
		 * Gets the camera orientation.
		 *
		 * @return the camera orientation
		 */
		GamaPoint getCameraOrientation();
	}

	/** The quality rendering. */
	RenderingHints QUALITY_RENDERING = new RenderingHints(null);

	/** The speed rendering. */
	RenderingHints SPEED_RENDERING = new RenderingHints(null);

	/** The medium rendering. */
	RenderingHints MEDIUM_RENDERING = new RenderingHints(null);

	/**
	 * Sets the display surface.
	 *
	 * @param surface
	 *            the new display surface
	 */
	void setDisplaySurface(final IDisplaySurface surface);

	/**
	 * Gets the display width.
	 *
	 * @return the display width
	 */
	int getDisplayWidth();

	/**
	 * Gets the display height.
	 *
	 * @return the display height
	 */
	int getDisplayHeight();

	/**
	 * Draw file.
	 *
	 * @param file
	 *            the file
	 * @param attributes
	 *            the attributes
	 * @return the rectangle 2 D
	 */
	Rectangle2D drawAsset(IAsset asset, DrawingAttributes attributes);

	/**
	 * Draw field.
	 *
	 * @param values
	 *            the values
	 * @param attributes
	 *            the attributes
	 * @return the rectangle 2 D
	 */
	Rectangle2D drawField(final IField values, final MeshDrawingAttributes attributes);

	/**
	 * Draw image.
	 *
	 * @param img
	 *            the img
	 * @param attributes
	 *            the attributes
	 * @return the rectangle 2 D
	 */
	Rectangle2D drawImage(final BufferedImage img, final DrawingAttributes attributes);

	/**
	 * Draw chart.
	 *
	 * @param chart
	 *            the chart
	 * @return the rectangle 2 D
	 */
	Rectangle2D drawChart(ChartOutput chart);

	/**
	 * Draw string.
	 *
	 * @param string
	 *            the string
	 * @param attributes
	 *            the attributes
	 * @return the rectangle 2 D
	 */
	Rectangle2D drawString(final String string, final TextDrawingAttributes attributes);

	/**
	 * Draw shape.
	 *
	 * @param shape
	 *            the shape
	 * @param attributes
	 *            the attributes
	 * @return the rectangle 2 D
	 */
	Rectangle2D drawShape(final Geometry shape, final DrawingAttributes attributes);

	/**
	 * Sets the alpha.
	 *
	 * @param alpha
	 *            the new alpha
	 */
	void setAlpha(double alpha);

	/**
	 * Fill background.
	 *
	 * @param bgColor
	 *            the bg color
	 */
	void fillBackground(Color bgColor);

	/**
	 * Begin drawing layers.
	 *
	 * @return true, if successful
	 */
	boolean beginDrawingLayers();

	/**
	 * Begin drawing layer.
	 *
	 * @param layer
	 *            the layer
	 */
	void beginDrawingLayer(ILayer layer);

	/**
	 * Begin overlay.
	 *
	 * @param layer
	 *            the layer
	 */
	void beginOverlay(OverlayLayer layer);

	/**
	 * End overlay.
	 */
	void endOverlay();

	/**
	 * Gets the y ratio between pixels and model units.
	 *
	 * @return the y ratio between pixels and model units
	 */
	double getyRatioBetweenPixelsAndModelUnits();

	/**
	 * Gets the x ratio between pixels and model units.
	 *
	 * @return the x ratio between pixels and model units
	 */
	double getxRatioBetweenPixelsAndModelUnits();

	/**
	 * Gets the absolute ratio between pixels and models units.
	 *
	 * @return the absolute ratio between pixels and models units
	 */
	double getAbsoluteRatioBetweenPixelsAndModelsUnits();

	/**
	 * Gets the visible region.
	 *
	 * @return the visible region
	 */
	/*
	 * Returns the region of the current layer (in model units) that is visible on screen
	 */
	Envelope getVisibleRegion();

	/**
	 * End drawing layer.
	 *
	 * @param layer
	 *            the layer
	 */
	void endDrawingLayer(ILayer layer);

	/**
	 * End drawing layers.
	 */
	void endDrawingLayers();

	/**
	 * Begin highlight.
	 */
	void beginHighlight();

	/**
	 * End highlight.
	 */
	void endHighlight();

	/**
	 * Gets the x offset in pixels.
	 *
	 * @return the x offset in pixels
	 */
	double getXOffsetInPixels();

	/**
	 * Gets the y offset in pixels.
	 *
	 * @return the y offset in pixels
	 */
	double getYOffsetInPixels();

	/**
	 * Gets the zoom level.
	 *
	 * @return the zoom level
	 */
	Double getZoomLevel();

	/**
	 * Checks if is 2d.
	 *
	 * @return true, if is 2d
	 */
	default boolean is2D() {
		return true;
	}

	/**
	 * Gets the view width.
	 *
	 * @return the view width
	 */
	int getViewWidth();

	/**
	 * Gets the view height.
	 *
	 * @return the view height
	 */
	int getViewHeight();

	/**
	 * Gets the surface.
	 *
	 * @return the surface
	 */
	IDisplaySurface getSurface();

	/**
	 * Gets the max env dim.
	 *
	 * @return the max env dim
	 */
	default double getMaxEnvDim() { return getSurface().getData().getMaxEnvDim(); }

	/**
	 * Gets the env width.
	 *
	 * @return the env width
	 */
	default double getEnvWidth() { return getSurface().getData().getEnvWidth(); }

	/**
	 * Gets the env height.
	 *
	 * @return the env height
	 */
	default double getEnvHeight() { return getSurface().getData().getEnvHeight(); }

	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Cannot draw.
	 *
	 * @return true, if successful
	 */
	// boolean cannotDraw();

	/**
	 * Checks if is not ready to update.
	 *
	 * @return true, if is not ready to update
	 */
	boolean isNotReadyToUpdate();

	/**
	 * Ask the IGraphics instance to accumulate temporary envelopes
	 *
	 * @param env
	 */
	default void accumulateTemporaryEnvelope(final Rectangle2D env) {}

	/**
	 * Gets the and wipe temporary envelope.
	 *
	 * @return the and wipe temporary envelope
	 */
	default Rectangle2D getAndWipeTemporaryEnvelope() { return null; }

}