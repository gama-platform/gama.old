/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IDisplaySurface.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
// import msi.gama.common.interfaces.IDisplaySurface.IZoomListener;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayData.DisplayDataListener;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.IEventLayerListener;
import msi.gaml.statements.draw.DrawingAttributes;

/**
 * Class IDisplaySurface. Represents a concrete object on which layers can be drawn on screen. Instances of subclasses
 * are the 'display's of GAMA (java2D, openGL, image)
 * 
 * Written by A. Drogoul
 * 
 * @since26 nov. 2009
 *
 */
public interface IDisplaySurface extends DisplayDataListener, IScoped, IDisposable {

	static final String SNAPSHOT_FOLDER_NAME = "snapshots";
	static final double MIN_ZOOM_FACTOR = 0.1;
	static final int MAX_ZOOM_FACTOR = 10;
	public static final double SELECTION_SIZE = 5; // pixels
	public static final int MAX_SIZE = Integer.MAX_VALUE; // pixels

	/**
	 * This sub-interface represents display surfaces relying on OpenGL
	 * 
	 * @author drogoul
	 *
	 */
	public interface OpenGL extends IDisplaySurface {

		Envelope3D getROIDimensions();

		void setPaused(boolean flag);

		void selectAgent(final DrawingAttributes attributes);

		void selectionIn(Envelope3D env);

	}

	/**
	 * Returns a BufferedImage that captures the current state of the surface on screen.
	 * 
	 * @param width
	 *            the desired width of the image
	 * @param height
	 *            the desired height of the image
	 * @return a BufferedImage of size {width, height} with all layers drawn on it
	 */
	public BufferedImage getImage(int width, int height);

	/**
	 * Asks the surface to update its display, optionnaly forcing it to do so (if it is paused, for instance)
	 **/
	public void updateDisplay(boolean force);

	/**
	 * Sets a concrete menu manager to be used for displaying menus on this surface
	 * 
	 * @param displaySurfaceMenu
	 *            an object, normally instance of DisplaySurfaceMenu
	 */
	public void setMenuManager(Object displaySurfaceMenu);

	void zoomIn();

	void zoomOut();

	void zoomFit();

	ILayerManager getManager();

	void focusOn(IShape geometry);

	/**
	 * Run the runnable in argument and refresh the output
	 * 
	 * @param r
	 */
	void runAndUpdate(Runnable r);

	/**
	 * @return the width of the panel
	 */
	int getWidth();

	/**
	 * @return the height of the panel
	 */
	int getHeight();

	/**
	 * Whatever is needed to do when the simulation has been reloaded.
	 *
	 * @param layerDisplayOutput
	 */
	void outputReloaded();

	double getEnvWidth();

	double getEnvHeight();

	public abstract double getDisplayWidth();

	public abstract double getDisplayHeight();

	public ILocation getModelCoordinates();

	public ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
			final Point positionInPixels);

	public Collection<IAgent> selectAgent(final int x, final int y);

	void followAgent(IAgent a);

	/**
	 * @return the current zoom level (between 0 and 1).
	 */
	double getZoomLevel();

	void setSize(int x, int y);

	LayeredDisplayOutput getOutput();

	LayeredDisplayData getData();

	void layersChanged();

	public void addListener(IEventLayerListener e);

	public void removeListener(IEventLayerListener e);

	Collection<IEventLayerListener> getLayerListeners();

	Envelope getVisibleRegionForLayer(ILayer currentLayer);

	int getFPS();

	/**
	 * @return true if the surface is considered as "realized" (i.e. displayed on the UI)
	 */
	boolean isRealized();

	/**
	 * @return true if the surface has been "rendered" (i.e. all the layers have been displayed)
	 */
	boolean isRendered();

	/**
	 * @return true if the surface has been 'disposed' already
	 */
	boolean isDisposed();

	/**
	 * @return
	 */
	void getModelCoordinatesInfo(StringBuilder receiver);

	void dispatchKeyEvent(char character);

	void dispatchMouseEvent(int swtEventType);

	void setMousePosition(int x, int y);

	void draggedTo(int x, int y);

	void selectAgentsAroundMouse();

}
