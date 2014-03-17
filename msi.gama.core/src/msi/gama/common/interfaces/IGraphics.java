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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;

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
		final boolean isTextured, final boolean isTriangulated, final boolean isShowText, Color gridColor,
		final double cellSize, String name);

	public abstract Rectangle2D drawImage(final IScope scope, final BufferedImage img,
		final ILocation locationInModelUnits, final ILocation sizeInModelUnits, Color gridColor, final Double angle,
		boolean isDynamic, String name);

	public abstract Rectangle2D drawString(final String string, final Color stringColor,
		ILocation locationInModelUnits, Double heightInModelUnits, String fontName, Integer styleName,
		final Double angle, final Boolean bitmap);

	public abstract Rectangle2D drawGamaShape(final IScope scope, final IShape geometry, final Color color,
		final boolean fill, final Color border, final boolean rounded);

	public abstract Rectangle2D drawChart(final IScope scope, BufferedImage chart, Double z);

	public abstract void initFor(IDisplaySurface surface);

	public abstract void setOpacity(double i);

	public abstract void fillBackground(Color bgColor, double opacity);

	public abstract void setQualityRendering(boolean quality);

	void setHighlightColor(int[] rgb);

	public abstract void beginDrawingLayers();

	public abstract void beginDrawingLayer(ILayer layer);

	public abstract int getEnvironmentWidth();

	public abstract int getEnvironmentHeight();

	public abstract double getyRatioBetweenPixelsAndModelUnits();

	public abstract double getxRatioBetweenPixelsAndModelUnits();

	public abstract void endDrawingLayer(ILayer layer);

	public abstract void endDrawingLayers();

	public abstract void beginHighlight();

	public abstract void endHighlight();

	public interface OpenGL extends IGraphics {

		public abstract Rectangle2D drawDEM(IScope scope, final BufferedImage dem, final BufferedImage texture,
			final Double z_factor);

	}

}