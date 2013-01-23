/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.interfaces;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import org.jfree.chart.JFreeChart;
import com.vividsolutions.jts.geom.Geometry;

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

	/**
	 * Method setGraphics.
	 * @param g Graphics2D
	 */
	public abstract void setGraphics(final Graphics2D g);

	/**
	 * Method getDisplayWidth.
	 * @return int
	 */
	public abstract int getDisplayWidth();

	/**
	 * Method getDisplayHeight.
	 * @return int
	 */
	public abstract int getDisplayHeight();

	/**
	 * Method setDisplayDimensions.
	 * @param width int
	 * @param height int
	 */
	public abstract void setDisplayDimensions(final int width, final int height);

	/**
	 * Method setFont.
	 * @param font Font
	 */
	public abstract void setFont(final Font font);

	/**
	 * Method getXScale.
	 * @return double
	 */
	public abstract double getXScale();

	/**
	 * Method setXScale.
	 * @param scale double
	 */
	public abstract void setXScale(final double scale);

	/**
	 * Method getYScale.
	 * @return double
	 */
	public abstract double getYScale();

	/**
	 * Method setYScale.
	 * @param scale double
	 */
	public abstract void setYScale(final double scale);

	/**
	 * Method setDrawingCoordinates.
	 * @param x double
	 * @param y double
	 */
	public abstract void setDrawingCoordinates(final double x, final double y);

	/**
	 * Method setDrawingDimensions.
	 * @param width int
	 * @param height int
	 */
	public abstract void setDrawingDimensions(final int width, final int height);

	/**
	 * Method drawImage.
	 * @param scope IScope
	 * @param img Image
	 * @param angle Integer
	 * @param String name
	 * @param z float
	 */
	public abstract Rectangle2D drawImage(final IScope scope, final BufferedImage img,
		final Integer angle, final String name, float z);

	/**
	 * Method drawCircle.
	 * @param scope IScope
	 * @param c Color
	 * @param fill boolean
	 * @param angle Integer
	 * @param height: height of the circle if using opengl and defining a z value
	 *            (e.g: draw shape: circle z:1;)
	 */
	public abstract Rectangle2D drawCircle(final IScope scope, final Color c, final boolean fill,
		final Color border, final Integer angle, final float height);

	/**
	 * Method drawTriangle.
	 * @param scope IScope
	 * @param c Color
	 * @param fill boolean
	 * @param angle Integer
	 * @param height: height of the triangle if using opengl and defining a z value
	 *            (e.g: draw shape: trinagle z:1;)
	 */
	public abstract Rectangle2D drawTriangle(final IScope scope, final Color c, final boolean fill,
		final Color border, final Integer angle, final float height);

	/**
	 * Method drawLine.
	 * @param c Color
	 * @param toX double
	 * @param toY double
	 */
	public abstract Rectangle2D drawLine(final Color c, final double toX, final double toY);

	/**
	 * Method drawRectangle.
	 * @param scope IScope
	 * @param color Color
	 * @param fill boolean
	 * @param angle Integer
	 * @param height: height of the rectangle if using opengl and defining a z value
	 *            (e.g: draw shape: square size:2 color: global_color z:2;)
	 */
	public abstract Rectangle2D drawRectangle(final IScope scope, final Color color,
		final boolean fill, final Color border, final Integer angle, final float height);

	/**
	 * @param agent 
	 * @return
	 *         Method drawString.
	 * @param string String
	 * @param stringColor Color
	 * @param angle Integer
	 * @param z float
	 */
	public abstract Rectangle2D drawString(IAgent agent, final String string, final Color stringColor,
		final Integer angle, final float z);

	/**
	 * Method drawGeometry.
	 * @param scope IScope
	 * @param geometry Geometry
	 * @param color Color
	 * @param fill boolean
	 * @param angle Integer
	 * @param z float
	 */
	public abstract Rectangle2D drawGeometry(final IScope scope, final Geometry geometry,
		final Color color, final boolean fill, final Color border, final Integer angle);

	public abstract void setDrawingOffset(final int x, final int y);

	public abstract Rectangle2D drawChart(JFreeChart chart);

	public abstract void setOpacity(double i);

	public abstract boolean isReady();

	public abstract void fill(Color bgColor, double opacity);

	public abstract void setQualityRendering(boolean quality);

	public abstract void setClipping(Rectangle imageClipBounds);

	public abstract Rectangle getClipping();

	/**
	 * @param img
	 * @param angle
	 * @param smooth
	 * @param name
	 * @return
	 */
	// FIXME: Why defining 2 method drawImage
	Rectangle2D drawImage(final IScope scope, BufferedImage img, Integer angle, boolean smooth,
		String name, float z);

	/**
	 * Draw grid as line
	 * @param image
	 * @param lineColor
	 */
	public void drawGrid(BufferedImage image, Color lineColor, Point point);

	/**
	 * 
	 * @return an Array of size 3 containing the red, green and blue components
	 */
	int[] getHighlightColor();

	void setHighlightColor(int[] rgb);

	public abstract void highlight(Rectangle2D r);

	/*
	 * For IGraphics implementations that support the notion of layers in a way or another...
	 * Indicates that the painting of all layers is about to begin.
	 */
	public abstract void initLayers();

	/*
	 * For IGraphics implementations that support the notion of layers in a way or another...
	 * Sets the z value of the new created layer.
	 * Set wether or not the current layer is static(will bve drawn only once) or dynamic.
	 */
	public abstract void newLayer(double zLayerValue, Boolean refresh);

	// /*
	// * Return the type of the IGraphics (e.g Java2D or OpenGl)
	// *
	// */
	// public abstract boolean isOpenGL();

	/*
	 * Define if the tesselation is used or the Gama Triangulation (work only in Opengl)
	 */
	public boolean useTesselation(boolean useTesselation);

	/*
	 * Define if the value of the ambiant light (work only in Opengl)
	 */
	public void setAmbiantLight(float lightValue);

}