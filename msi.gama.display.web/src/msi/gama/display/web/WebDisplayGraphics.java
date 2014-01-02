/**
 * Created by drogoul, 2 janv. 2014
 * 
 */
package msi.gama.display.web;

import java.awt.Color;
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
	 * @see msi.gama.common.interfaces.IGraphics#drawGrid(msi.gama.runtime.IScope, java.awt.image.BufferedImage, double[], boolean, boolean, boolean, msi.gama.metamodel.shape.ILocation, msi.gama.metamodel.shape.ILocation, java.awt.Color, java.lang.Integer, java.lang.Double, double, java.lang.String)
	 */
	@Override
	public Rectangle2D drawGrid(IScope scope, BufferedImage img, double[] gridValueMatrix, boolean isTextured,
		boolean isTriangulated, boolean isShowText, ILocation locationInModelUnits, ILocation sizeInModelUnits,
		Color gridColor, Integer angle, Double z, double cellSize, String name) {
		return null;
	}

	/**
	 * Method drawImage()
	 * @see msi.gama.common.interfaces.IGraphics#drawImage(msi.gama.runtime.IScope, java.awt.image.BufferedImage, msi.gama.metamodel.shape.ILocation, msi.gama.metamodel.shape.ILocation, java.awt.Color, java.lang.Integer, java.lang.Double, boolean, java.lang.String)
	 */
	@Override
	public Rectangle2D drawImage(IScope scope, BufferedImage img, ILocation locationInModelUnits,
		ILocation sizeInModelUnits, Color gridColor, Integer angle, Double z, boolean isDynamic, String name) {
		return null;
	}

	/**
	 * Method drawString()
	 * @see msi.gama.common.interfaces.IGraphics#drawString(java.lang.String, java.awt.Color, msi.gama.metamodel.shape.ILocation, java.lang.Double, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Double, java.lang.Boolean)
	 */
	@Override
	public Rectangle2D drawString(String string, Color stringColor, ILocation locationInModelUnits,
		Double heightInModelUnits, String fontName, Integer styleName, Integer angle, Double z, Boolean bitmap) {
		return null;
	}

	/**
	 * Method drawGamaShape()
	 * @see msi.gama.common.interfaces.IGraphics#drawGamaShape(msi.gama.runtime.IScope, msi.gama.metamodel.shape.IShape, java.awt.Color, boolean, java.awt.Color, java.lang.Integer, boolean)
	 */
	@Override
	public Rectangle2D drawGamaShape(IScope scope, IShape geometry, Color color, boolean fill, Color border,
		Integer angle, boolean rounded) {
		return null;
	}

	/**
	 * Method initFor()
	 * @see msi.gama.common.interfaces.IGraphics#initFor(msi.gama.common.interfaces.IDisplaySurface)
	 */
	@Override
	public void initFor(IDisplaySurface surface) {}

	/**
	 * Method setOpacity()
	 * @see msi.gama.common.interfaces.IGraphics#setOpacity(double)
	 */
	@Override
	public void setOpacity(double i) {}

	/**
	 * Method fillBackground()
	 * @see msi.gama.common.interfaces.IGraphics#fillBackground(java.awt.Color, double)
	 */
	@Override
	public void fillBackground(Color bgColor, double opacity) {}

	/**
	 * Method setQualityRendering()
	 * @see msi.gama.common.interfaces.IGraphics#setQualityRendering(boolean)
	 */
	@Override
	public void setQualityRendering(boolean quality) {}

	/**
	 * Method setHighlightColor()
	 * @see msi.gama.common.interfaces.IGraphics#setHighlightColor(int[])
	 */
	@Override
	public void setHighlightColor(int[] rgb) {}

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
	public void beginDrawingLayer(ILayer layer) {}

	/**
	 * Method getEnvironmentWidth()
	 * @see msi.gama.common.interfaces.IGraphics#getEnvironmentWidth()
	 */
	@Override
	public int getEnvironmentWidth() {
		return 0;
	}

	/**
	 * Method getEnvironmentHeight()
	 * @see msi.gama.common.interfaces.IGraphics#getEnvironmentHeight()
	 */
	@Override
	public int getEnvironmentHeight() {
		return 0;
	}

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
	public void endDrawingLayer(ILayer layer) {}

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

}
