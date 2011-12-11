/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.graphics;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
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
	 * @param img Image
	 * @param angle Integer
	 */
	public abstract Rectangle2D drawImage(final BufferedImage img, final Integer angle);

	/**
	 * Method drawCircle.
	 * @param c Color
	 * @param fill boolean
	 * @param angle Integer
	 */
	public abstract Rectangle2D drawCircle(final Color c, final boolean fill, final Integer angle);

	/**
	 * Method drawTriangle.
	 * @param c Color
	 * @param fill boolean
	 * @param angle Integer
	 */
	public abstract Rectangle2D drawTriangle(final Color c, final boolean fill, final Integer angle);

	/**
	 * Method drawLine.
	 * @param c Color
	 * @param toX double
	 * @param toY double
	 */
	public abstract Rectangle2D drawLine(final Color c, final double toX, final double toY);

	/**
	 * Method drawRectangle.
	 * @param color Color
	 * @param fill boolean
	 * @param angle Integer
	 */
	public abstract Rectangle2D drawRectangle(final Color color, final boolean fill,
		final Integer angle);

	/**
	 * @return
	 *         Method drawString.
	 * @param string String
	 * @param stringColor Color
	 * @param angle Integer
	 */
	public abstract Rectangle2D drawString(final String string, final Color stringColor,
		final Integer angle);

	/**
	 * Method drawGeometry.
	 * @param geometry Geometry
	 * @param color Color
	 * @param fill boolean
	 * @param angle Integer
	 */
	public abstract Rectangle2D drawGeometry(final Geometry geometry, final Color color,
		final boolean fill, final Integer angle);

	/**
	 * Method drawShape.
	 * @param c Color
	 * @param s Shape
	 * @param fill boolean
	 * @param angle Integer
	 */
	public abstract Rectangle2D drawShape(final Color c, final Shape s, final boolean fill,
		final Integer angle);

	public abstract void setDrawingOffset(final int x, final int y);

	public abstract Rectangle2D drawChart(JFreeChart chart);

	void setGraphics(GC g, Display display);

	public abstract void setOpacity(double i);

	public abstract boolean isReady();

	public abstract void erase(Color bgColor);

	public abstract void setQualityRendering(boolean quality);

	public abstract void setClipping(Rectangle imageClipBounds);

	public abstract Rectangle getClipping();

}