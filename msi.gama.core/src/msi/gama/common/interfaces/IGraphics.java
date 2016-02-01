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
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

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
		final boolean isTriangulated, final boolean isGrayScaled, final boolean isShowText, GamaColor gridColor,
		final Envelope3D cellSize, String name);

	public abstract Rectangle2D drawFile(final GamaGeometryFile file, final DrawingAttributes attributes);
	//
	// public abstract Rectangle2D drawFile(final IScope scope, final GamaFile filecheck, final Color color,
	// final ILocation locationInModelUnits, final ILocation sizeInModelUnits, GamaPair<Double, GamaPoint> rotates3D,
	// GamaPair<Double, GamaPoint> rotates3DInit);

	public abstract Rectangle2D drawImage(final BufferedImage img, final DrawingAttributes attributes);

	public abstract Rectangle2D drawString(final String string, final DrawingAttributes attributes);

	public abstract Rectangle2D drawShape(final IShape shape, final DrawingAttributes attributes);

	public abstract void initFor(IDisplaySurface surface);

	public abstract void setOpacity(double i);

	public abstract void fillBackground(Color bgColor, double opacity);

	public abstract void beginDrawingLayers();

	public abstract void beginDrawingLayer(ILayer layer);

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

	public abstract Double getZoomLevel();

	public abstract boolean is2D();

}