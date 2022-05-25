/*******************************************************************************************************
 *
 * IDisplaySurface.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
// import msi.gama.common.interfaces.IDisplaySurface.IZoomListener;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayData.DisplayDataListener;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.IEventLayerListener;
import msi.gama.runtime.IScope.IGraphicsScope;
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

	/** The snapshot folder name. */
	String SNAPSHOT_FOLDER_NAME = "snapshots";

	/** The min zoom factor. */
	double MIN_ZOOM_FACTOR = 0.1;

	/** The max zoom factor. */
	int MAX_ZOOM_FACTOR = 10;

	/** The selection size. */
	double SELECTION_SIZE = 5; // pixels

	/**
	 * This sub-interface represents display surfaces relying on OpenGL
	 *
	 * @author drogoul
	 *
	 */
	public interface OpenGL extends IDisplaySurface {

		/**
		 * Gets the ROI dimensions.
		 *
		 * @return the ROI dimensions
		 */
		Envelope3D getROIDimensions();

		/**
		 * Sets the paused.
		 *
		 * @param flag
		 *            the new paused
		 */
		void setPaused(boolean flag);

		/**
		 * Select agent.
		 *
		 * @param attributes
		 *            the attributes
		 */
		void selectAgent(final DrawingAttributes attributes);

		/**
		 * Selection in.
		 *
		 * @param env
		 *            the env
		 */
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
	BufferedImage getImage(int width, int height);

	/**
	 * Asks the surface to update its display, optionnaly forcing it to do so (if it is paused, for instance)
	 **/
	void updateDisplay(boolean force);

	/**
	 * Sets a concrete menu manager to be used for displaying menus on this surface
	 *
	 * @param displaySurfaceMenu
	 *            an object, normally instance of DisplaySurfaceMenu
	 */
	void setMenuManager(Object displaySurfaceMenu);

	/**
	 * Zoom in.
	 */
	void zoomIn();

	/**
	 * Zoom out.
	 */
	void zoomOut();

	/**
	 * Zoom fit.
	 */
	void zoomFit();

	/**
	 * Gets the manager.
	 *
	 * @return the manager
	 */
	ILayerManager getManager();

	/**
	 * Focus on.
	 *
	 * @param geometry
	 *            the geometry
	 */
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

	/**
	 * Gets the env width.
	 *
	 * @return the env width
	 */
	double getEnvWidth();

	/**
	 * Gets the env height.
	 *
	 * @return the env height
	 */
	double getEnvHeight();

	/**
	 * Gets the display width.
	 *
	 * @return the display width
	 */
	double getDisplayWidth();

	/**
	 * Gets the display height.
	 *
	 * @return the display height
	 */
	double getDisplayHeight();

	/**
	 * Gets the model coordinates.
	 *
	 * @return the model coordinates
	 */
	GamaPoint getModelCoordinates();

	/**
	 * Gets the model coordinates from.
	 *
	 * @param xOnScreen
	 *            the x on screen
	 * @param yOnScreen
	 *            the y on screen
	 * @param sizeInPixels
	 *            the size in pixels
	 * @param positionInPixels
	 *            the position in pixels
	 * @return the model coordinates from
	 */
	GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
			final Point positionInPixels);

	/**
	 * Select agent.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the collection
	 */
	Collection<IAgent> selectAgent(final int x, final int y);

	/**
	 * @return the current zoom level (between 0 and 1).
	 */
	double getZoomLevel();

	/**
	 * Sets the size.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	void setSize(int x, int y);

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	LayeredDisplayOutput getOutput();

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	LayeredDisplayData getData();

	/**
	 * Layers changed.
	 */
	void layersChanged();

	/**
	 * Adds the listener.
	 *
	 * @param e
	 *            the e
	 */
	void addListener(IEventLayerListener e);

	/**
	 * Removes the listener.
	 *
	 * @param e
	 *            the e
	 */
	void removeListener(IEventLayerListener e);

	/**
	 * Gets the layer listeners.
	 *
	 * @return the layer listeners
	 */
	Collection<IEventLayerListener> getLayerListeners();

	/**
	 * Gets the visible region for layer.
	 *
	 * @param currentLayer
	 *            the current layer
	 * @return the visible region for layer
	 */
	Envelope getVisibleRegionForLayer(ILayer currentLayer);

	/**
	 * Gets the fps.
	 *
	 * @return the fps
	 */
	int getFPS();

	/**
	 * @return true if the surface is considered as "realized" (i.e. displayed on the UI)
	 */
	// boolean isRealized();

	/**
	 * @return true if the surface has been "rendered" (i.e. all the layers have been displayed)
	 */
	// boolean isRendered();

	/**
	 * @return true if the surface has been 'disposed' already
	 */
	boolean isDisposed();

	/**
	 * @return
	 */
	void getModelCoordinatesInfo(StringBuilder receiver);

	/**
	 * Dispatch key event.
	 *
	 * @param character
	 *            the character
	 */
	void dispatchKeyEvent(char character);

	/**
	 * Dispatch mouse event.
	 *
	 * @param swtEventType
	 *            the swt event type
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	void dispatchMouseEvent(int swtEventType, int x, int y);

	/**
	 * Sets the mouse position.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	void setMousePosition(int x, int y);

	/**
	 * Dragged to.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	void draggedTo(int x, int y);

	/**
	 * Select agents around mouse.
	 */
	void selectAgentsAroundMouse();

	/**
	 * Compute font.
	 *
	 * @param f
	 *            the f
	 * @return the font
	 */
	default Font computeFont(final Font f) {
		return f;
	}

	/**
	 * Can trigger contextual menu.
	 *
	 * @return true, if successful
	 */
	default boolean canTriggerContextualMenu() {
		return !getManager().hasMouseMenuEventLayer();
	}

	/**
	 * Sets the display synchronizer.
	 *
	 * @param layeredDisplaySynchronizer
	 *            the new display synchronizer
	 */
	// default void setDisplaySynchronizer(final IDisplaySynchronizer layeredDisplaySynchronizer) {}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	@Override
	IGraphicsScope getScope();

	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visible
	 */
	boolean isVisible();

}
