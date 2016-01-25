/*********************************************************************************************
 *
 *
 * 'IGraphics.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaPair;
import msi.gama.util.file.GamaFile;

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

	public abstract int getDisplayWidthInPixels();

	public abstract int getDisplayHeightInPixels();

	public abstract Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] gridValueMatrix,
		final boolean isTextured, final boolean isTriangulated, final boolean isGrayScaled, final boolean isShowText,
		Color gridColor, final Envelope3D cellSize, String name);

	public abstract Rectangle2D drawFile(final IScope scope, final GamaFile filecheck, final Color color,
		final ILocation locationInModelUnits, final ILocation sizeInModelUnits,
		final GamaPair<Double, GamaPoint> rotate3D);

	public abstract Rectangle2D drawFile(final IScope scope, final GamaFile filecheck, final Color color,
		final ILocation locationInModelUnits, final ILocation sizeInModelUnits, GamaPair<Double, GamaPoint> rotates3D,
		GamaPair<Double, GamaPoint> rotates3DInit);

	public abstract Rectangle2D drawImage(final IScope scope, final BufferedImage img,
		final ILocation locationInModelUnits, final ILocation sizeInModelUnits, Color gridColor, final Double angle,
		boolean isDynamic, String name);

	public abstract Rectangle2D drawString(final String string, final Color stringColor, ILocation locationInModelUnits,
		Double heightInModelUnits, Font font, final Double angle, final Boolean bitmap);

	public abstract Rectangle2D drawGamaShape(final IScope scope, final IShape geometry, final Color color,
		final boolean fill, final Color border, final boolean rounded);

	public abstract Rectangle2D drawChart(final IScope scope, BufferedImage chart, Double z);

	public abstract void initFor(IDisplaySurface surface);

	public abstract void setOpacity(double i);

	public abstract void fillBackground(Color bgColor, double opacity);

	// public abstract void setQualityRendering(boolean quality);

	// void setHighlightColor(Color h);

	public abstract void beginDrawingLayers();

	public abstract void beginDrawingLayer(ILayer layer);

	//
	// public abstract int getEnvironmentWidth();
	//
	// public abstract int getEnvironmentHeight();

	public abstract double getyRatioBetweenPixelsAndModelUnits();

	public abstract double getxRatioBetweenPixelsAndModelUnits();

	public abstract void endDrawingLayer(ILayer layer);

	public abstract void endDrawingLayers();

	public abstract void beginHighlight();

	public abstract void endHighlight();

	public double getXOffsetInPixels();

	public double getYOffsetInPixels();

	public interface OpenGL extends IGraphics {

		public abstract Rectangle2D drawDEM(IScope scope, final BufferedImage dem, final BufferedImage texture,
			final Double z_factor);

	}

	/**
	 * @return
	 */
	public abstract Double getZoomLevel();

}