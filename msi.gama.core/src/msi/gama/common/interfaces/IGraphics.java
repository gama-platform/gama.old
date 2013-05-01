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
import java.util.Collection;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaFile;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.jfree.chart.JFreeChart;

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

	public abstract void setGraphics(final Graphics2D g);

	public abstract int getDisplayWidthInPixels();

	public abstract int getDisplayHeightInPixels();

	public abstract void setDisplayDimensionsInPixels(final int width, final int height);

	// public abstract void setCurrentLayerOffsetAndSizeInModelUnits(Rectangle2D.Double box);

	public abstract Rectangle2D drawImage(final IScope scope, final BufferedImage img,
		final ILocation locationInModelUnits, final ILocation sizeInModelUnits, Color gridColor, final Integer angle,
		Double z, boolean isDynamic);

	public abstract Rectangle2D drawString(final String string, final Color stringColor,
		ILocation locationInModelUnits, Double heightInModelUnits, String fontName, Integer styleName,
		final Integer angle, final Double z);

	public abstract Rectangle2D drawGamaShape(final IScope scope, final IShape geometry, final Color color,
		final boolean fill, final Color border, final Integer angle, final boolean rounded);

	public abstract Rectangle2D drawChart(final IScope scope, JFreeChart chart, Double z);

	public abstract void highlightRectangleInPixels(IAgent a, Rectangle2D r);

	public abstract void setOpacity(double i);

	public abstract boolean isReady();

	public abstract void fillBackground(Color bgColor, double opacity);

	public abstract void setQualityRendering(boolean quality);

	/**
	 * 
	 * @return an Array of size 3 containing the red, green and blue components
	 */
	int[] getHighlightColor();

	void setHighlightColor(int[] rgb);

	/*
	 * For IGraphics implementations that support the notion of layers in a way or another...
	 * Indicates that the painting of all layers is about to begin.
	 */
	public abstract void beginDrawingLayers();

	/*
	 * For IGraphics implementations that support the notion of layers in a way or another...
	 * Sets the z value of the new created layer.
	 * Set wether or not the current layer is static(will bve drawn only once) or dynamic.
	 */
	public abstract void newLayer(ILayer layer);

	public interface OpenGL extends IGraphics {

		/*
		 * Define if the tesselation is used or the Gama Triangulation (work only in Opengl)
		 */
		public boolean useTesselation(boolean useTesselation);

		/*
		 * Define if the value of the ambient light (work only in Opengl)
		 */
		public void setAmbientLightValue(GamaColor lightValue);

		/*
		 * Define if polygon are drawn in solid(true) or as outlines (work only in Opengl)
		 */
		public boolean setPolygonMode(boolean polygonMode);

		/*
		 * Define if polygon are drawn in solid(true) or as outlines (work only in Opengl)
		 */
		public abstract void drawDEM(final GamaFile demFileName, final GamaFile textureFileName);

		/*
		 * Define the camera position (work only in Opengl)
		 */
		public abstract void setCameraPosition(final ILocation camPos);

		/*
		 * Define the camera position (work only in Opengl)
		 */
		public abstract void setCameraLookPosition(final ILocation camPos);
		
		/*
		 * Define the camera position (work only in Opengl)
		 */
		public abstract void setCameraUpVector(final ILocation upVector);

		public abstract void cleanStrings();

		public abstract void cleanCollections();

		public abstract void cleanImages();

		public abstract void cleanGeometries();

		public void addCollectionInCollections(SimpleFeatureCollection myCollection, Color color);

		public Collection getJTSGeometries();

		public double getMaxEnvDim();

		public void drawMyJTSGeometries(boolean picking);

		public Collection getMyJTSStaticGeometries();

		public void drawMyJTSStaticGeometries(boolean picking);

		public Collection getImages();

		public Collection getStrings();

		public Collection getCollections();

		public void drawMyStrings();

		public void drawCollection();

		public void drawMyImages(boolean picking);

		public void setPickedObjectIndex(int endPicking);

		public double getEnvWidth();

		public double getEnvHeight();

		public void setPolygonTriangulated(boolean b);

	}

	public abstract int getEnvironmentWidth();

	public abstract int getEnvironmentHeight();

}