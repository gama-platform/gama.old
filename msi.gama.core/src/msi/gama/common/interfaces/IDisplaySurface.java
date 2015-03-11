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

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 26 nov. 2009
 * 
 * @todo Description
 * 
 */
public interface IDisplaySurface /* extends IPerspectiveListener, IPartListener */{

	public interface OpenGL extends IDisplaySurface {

		/**
		 * Switch between 2D and 3D view (Only with Opengl view)
		 */
		void toggleView();

		/**
		 * Activate the picking mode (Only with Opengl view)
		 */
		void togglePicking();

		/**
		 * Activate arcball view (Only with Opengl view)
		 */
		void toggleArcball();

		/**
		 * Activate inertia mode (Only with Opengl view)
		 */
		void toggleInertia();

		/**
		 * Activate select rectangle tool (Only with Opengl view)
		 */
		void toggleSelectRectangle();

		/**
		 * Show the triangulation (Only with Opengl view)
		 */
		void toggleTriangulation();

		/**
		 * Split species layer in 3D
		 */
		void toggleSplitLayer();

		/**
		 * Split species layer in 3D
		 */
		void toggleRotation();

		// @Override
		// public IGraphics.OpenGL getIGraphics();

		void toggleCamera();

		/**
		 * @return the position of the camera
		 */
		ILocation getCameraPosition();

		public boolean isLayerSplitted();

		public boolean isRotationOn();

		public boolean isCameraSwitched();

		public boolean isArcBallDragOn();

		public boolean isTriangulationOn();

		public boolean isInertiaOn();

		void setPaused(boolean flag);

	}

	public interface IZoomListener {

		public void newZoomLevel(double zoomLevel);
	}

	public static final double SELECTION_SIZE = 5; // pixels
	public static final int MAX_SIZE = Integer.MAX_VALUE; // pixels

	BufferedImage getImage();

	void dispose();

	/** Asks the surface to update its display, optionnaly forcing it to do so (if it is paused, for instance) **/
	void updateDisplay(boolean force);

	int[] computeBoundsFrom(int width, int height);

	boolean resizeImage(int width, int height, boolean force);

	void zoomIn();

	void zoomOut();

	void zoomFit();

	ILayerManager getManager();

	void focusOn(IShape geometry);

	void canBeUpdated(boolean ok);

	void waitForUpdateAndRun(Runnable r);

	void setBackground(Color background);

	public void setQualityRendering(boolean quality);

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
	 * @return the width of the image (bitmap)
	 */
	int getImageWidth();

	/**
	 * @return the height of the image (bitmap)
	 */
	int getImageHeight();

	/**
	 * Sets the origin (top left corner) of the image in the panel
	 * 
	 * @param i
	 * @param j
	 */
	void setOrigin(int i, int j);

	/**
	 * Returns the x coordinate of the origin (top left corner of the image in
	 * the panel)
	 * 
	 * @return
	 */
	int getOriginX();

	/**
	 * Returns the y coordinate of the origin (top left corner of the image in
	 * the panel)
	 * 
	 * @return
	 */

	int getOriginY();

	/**
	 * Whatever is needed to do when the simulation has been reloaded.
	 * 
	 * @param layerDisplayOutput
	 */
	void outputReloaded();

	/**
	 * 
	 * @return an Array of size 3 containing the red, green and blue components
	 */
	Color getHighlightColor();

	void setHighlightColor(Color h);

	public void addMouseListener(MouseListener e);

	public void removeMouseListener(MouseListener e);

	double getEnvWidth();

	double getEnvHeight();

	public abstract int getDisplayWidth();

	public abstract int getDisplayHeight();

	public abstract void setZoomListener(IZoomListener listener);

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

	boolean getQualityRendering();

	IScope getDisplayScope();

	IDisplayOutput getOutput();

}
