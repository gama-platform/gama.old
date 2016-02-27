/*********************************************************************************************
 *
 *
 * 'IDisplaySurface.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import com.vividsolutions.jts.geom.Envelope;
// import msi.gama.common.interfaces.IDisplaySurface.IZoomListener;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.*;
import msi.gama.outputs.LayeredDisplayData.DisplayDataListener;
import msi.gama.outputs.layers.IEventLayerListener;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 26 nov. 2009
 *
 * @todo Description
 *
 */
public interface IDisplaySurface extends DisplayDataListener /* extends IPerspectiveListener, IPartListener */ {

	static final String SNAPSHOT_FOLDER_NAME = "snapshots";
	static final double MIN_ZOOM_FACTOR = 0.1;
	static final int MAX_ZOOM_FACTOR = 4;

	public interface OpenGL extends IDisplaySurface/* , IZoomListener */ {

		/**
		 * @return the position of the camera
		 */
		ILocation getCameraPosition();

		void setPaused(boolean flag);

		void selectAgent(IAgent agent);

		void selectSeveralAgents(Collection<IAgent> shapes);

	}
	//
	// public interface IZoomListener {
	//
	// public void newZoomLevel(double zoomLevel);
	// }

	public static final double SELECTION_SIZE = 5; // pixels
	public static final int MAX_SIZE = Integer.MAX_VALUE; // pixels

	BufferedImage getImage();

	void dispose();

	/** Asks the surface to update its display, optionaly forcing it to do so (if it is paused, for instance) **/
	void updateDisplay(boolean force);

	/**
	 * @param displaySurfaceMenu
	 */
	void setSWTMenuManager(Object displaySurfaceMenu);

	// int[] computeBoundsFrom(int width, int height);

	boolean resizeImage(int width, int height, boolean force);

	void zoomIn();

	void zoomOut();

	void zoomFit();

	ILayerManager getManager();

	void focusOn(IShape geometry);

	// void canBeUpdated(boolean ok);

	void runAndUpdate(Runnable r);

	void snapshot();

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

	// public abstract void setZoomListener(IZoomListener listener);

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

	// boolean getQualityRendering();

	IScope getDisplayScope();

	IDisplayOutput getOutput();

	LayeredDisplayData getData();

	/**
	 * @return
	 */
	boolean isDisposed();

	/**
	 *
	 */
	void layersChanged();

	void acquireLock();

	void releaseLock();

	public void addListener(IEventLayerListener e);

	public void removeListener(IEventLayerListener e);

	/**
	 * @return
	 */
	Collection<IEventLayerListener> getLayerListeners();

	/**
	 * @param currentLayer
	 * @return
	 */
	Envelope getVisibleRegionForLayer(ILayer currentLayer);

	/**
	 * @return
	 */
	int getFPS();

}
