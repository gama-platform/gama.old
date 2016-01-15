/*********************************************************************************************
 *
 *
 * 'WebDisplayGraphics.java', in plugin 'msi.gama.display.web', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.display.web;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;

/**
 * Class WebDisplayGraphics.
 *
 * @author drogoul
 * @since 2 janv. 2014
 *
 */
public class WebDisplayGraphics implements IGraphics {

	/**
	 * Method getDisplayWidthInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getDisplayWidthInPixels()
	 */
	@Override
	public int getDisplayWidthInPixels() {
		return 0;
	}

	/**
	 * Method getDisplayHeightInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getDisplayHeightInPixels()
	 */
	@Override
	public int getDisplayHeightInPixels() {
		return 0;
	}

	/**
	 * Method drawGrid()
	 * @see msi.gama.common.interfaces.IGraphics#drawGrid(msi.gama.runtime.IScope, java.awt.image.BufferedImage, double[], boolean, boolean, boolean, msi.gama.metamodel.shape.ILocation,
	 * msi.gama.metamodel.shape.ILocation, java.awt.Color, java.lang.Integer, java.lang.Double, double, java.lang.String)
	 */
	@Override
	public Rectangle2D drawGrid(final IScope scope, final BufferedImage img, final double[] gridValueMatrix,
		final boolean isTextured, final boolean isTriangulated, final boolean isGrayScaled, final boolean isShowText,
		final Color gridColor, final double cellSize, final String name) {
		return null;
	}

	/**
	 * Method drawImage()
	 * @see msi.gama.common.interfaces.IGraphics#drawImage(msi.gama.runtime.IScope, java.awt.image.BufferedImage, msi.gama.metamodel.shape.ILocation, msi.gama.metamodel.shape.ILocation,
	 * java.awt.Color, java.lang.Integer, java.lang.Double, boolean, java.lang.String)
	 */
	@Override
	public Rectangle2D drawImage(final IScope scope, final BufferedImage img, final ILocation locationInModelUnits,
		final ILocation sizeInModelUnits, final Color gridColor, final Double angle, final boolean isDynamic,
		final String name) {
		return null;
	}

	/**
	 * Method drawString()
	 * @see msi.gama.common.interfaces.IGraphics#drawString(java.lang.String, java.awt.Color, msi.gama.metamodel.shape.ILocation, java.lang.Double, java.lang.String, java.lang.Integer,
	 * java.lang.Integer, java.lang.Double, java.lang.Boolean)
	 */
	@Override
	public Rectangle2D drawString(final String string, final Color stringColor, final ILocation locationInModelUnits,
		final Double heightInModelUnits, final Font font, final Double angle, final Boolean bitmap) {
		return null;
	}

	/**
	 * Method drawGamaShape()
	 * @see msi.gama.common.interfaces.IGraphics#drawGamaShape(msi.gama.runtime.IScope, msi.gama.metamodel.shape.IShape, java.awt.Color, boolean, java.awt.Color, java.lang.Integer, boolean)
	 */
	@Override
	public Rectangle2D drawGamaShape(final IScope scope, final IShape geometry, final Color color, final boolean fill,
		final Color border, final boolean rounded) {
		System.out.println("WebDisplaydrawGamaShape");
		return null;
	}

	/**
	 * Method initFor()
	 * @see msi.gama.common.interfaces.IGraphics#initFor(msi.gama.common.interfaces.IDisplaySurface)
	 */
	@Override
	public void initFor(final IDisplaySurface surface) {}

	/**
	 * Method setOpacity()
	 * @see msi.gama.common.interfaces.IGraphics#setOpacity(double)
	 */
	@Override
	public void setOpacity(final double i) {}

	/**
	 * Method fillBackground()
	 * @see msi.gama.common.interfaces.IGraphics#fillBackground(java.awt.Color, double)
	 */
	@Override
	public void fillBackground(final Color bgColor, final double opacity) {}

	/**
	 * Method beginDrawingLayers()
	 * @see msi.gama.common.interfaces.IGraphics#beginDrawingLayers()
	 */
	@Override
	public void beginDrawingLayers() {}

	/**
	 * Method beginDrawingLayer()
	 * @see msi.gama.common.interfaces.IGraphics#beginDrawingLayer(msi.gama.common.interfaces.ILayer)
	 */
	@Override
	public void beginDrawingLayer(final ILayer layer) {}

	// /**
	// * Method getEnvironmentWidth()
	// * @see msi.gama.common.interfaces.IGraphics#getEnvironmentWidth()
	// */
	// @Override
	// public int getEnvironmentWidth() {
	// return 0;
	// }
	//
	// /**
	// * Method getEnvironmentHeight()
	// * @see msi.gama.common.interfaces.IGraphics#getEnvironmentHeight()
	// */
	// @Override
	// public int getEnvironmentHeight() {
	// return 0;
	// }

	/**
	 * Method getyRatioBetweenPixelsAndModelUnits()
	 * @see msi.gama.common.interfaces.IGraphics#getyRatioBetweenPixelsAndModelUnits()
	 */
	@Override
	public double getyRatioBetweenPixelsAndModelUnits() {
		return 0;
	}

	/**
	 * Method getxRatioBetweenPixelsAndModelUnits()
	 * @see msi.gama.common.interfaces.IGraphics#getxRatioBetweenPixelsAndModelUnits()
	 */
	@Override
	public double getxRatioBetweenPixelsAndModelUnits() {
		return 0;
	}

	/**
	 * Method endDrawingLayer()
	 * @see msi.gama.common.interfaces.IGraphics#endDrawingLayer(msi.gama.common.interfaces.ILayer)
	 */
	@Override
	public void endDrawingLayer(final ILayer layer) {}

	/**
	 * Method endDrawingLayers()
	 * @see msi.gama.common.interfaces.IGraphics#endDrawingLayers()
	 */
	@Override
	public void endDrawingLayers() {}

	/**
	 * Method beginHighlight()
	 * @see msi.gama.common.interfaces.IGraphics#beginHighlight()
	 */
	@Override
	public void beginHighlight() {}

	/**
	 * Method endHighlight()
	 * @see msi.gama.common.interfaces.IGraphics#endHighlight()
	 */
	@Override
	public void endHighlight() {}

	/**
	 * Method drawChart()
	 * @see msi.gama.common.interfaces.IGraphics#drawChart(msi.gama.runtime.IScope, msi.gama.common.interfaces.JFreeChart, java.lang.Double)
	 */
	@Override
	public Rectangle2D drawChart(final IScope scope, final BufferedImage chart, final Double z) {
		return null;
	}

	/**
	 * Method getXOffsetInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getXOffsetInPixels()
	 */
	@Override
	public double getXOffsetInPixels() {
		return 0;
	}

	/**
	 * Method getYOffsetInPixels()
	 * @see msi.gama.common.interfaces.IGraphics#getYOffsetInPixels()
	 */
	@Override
	public double getYOffsetInPixels() {
		return 0;
	}

	/**
	 * Method getZoomLevel()
	 * @see msi.gama.common.interfaces.IGraphics#getZoomLevel()
	 */
	@Override
	public Double getZoomLevel() {
		return null;
	}

}
