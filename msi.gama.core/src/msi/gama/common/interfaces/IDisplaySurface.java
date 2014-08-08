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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;

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
		GamaPoint getCameraPosition();

	}

	public interface IZoomListener {

		public void newZoomLevel(double zoomLevel);
	}

	public static final double SELECTION_SIZE = 5; // pixels
	public static final int MAX_SIZE = Integer.MAX_VALUE; // pixels

	BufferedImage getImage();

	void dispose();

	void updateDisplay();

	void forceUpdateDisplay(); // toggling off synchronization

	int[] computeBoundsFrom(int width, int height);

	boolean resizeImage(int width, int height, boolean force);

	// void outputChanged(final double env_width, final double env_height, final
	// IDisplayOutput output);

	void zoomIn();

	void zoomOut();

	void zoomFit();

	ILayerManager getManager();

	// void fireSelectionChanged(Object a);

	void focusOn(IShape geometry);

	boolean canBeUpdated();

	void canBeUpdated(boolean ok);

	void setBackgroundColor(Color background);

	void setPaused(boolean b);

	public boolean isPaused();

	public void setQualityRendering(boolean quality);

	void setSynchronized(boolean checked);

	void setAutoSave(boolean autosave, int x, int y);

	void initOutput3D(final boolean output3D, final ILocation output3DNbCycles);

	void setSnapshotFileName(String string);

	void snapshot();

	/**
	 * @param swtNavigationPanel
	 *            FIXME Create an interface for the navigqtion panel
	 */
	// void setNavigator(Object swtNavigationPanel);

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
	 * Post-constructor that initializes the surface
	 * 
	 * @param w
	 * @param h
	 * @param layerDisplayOutput
	 */
	void initialize(IScope scope, double w, double h, LayeredDisplayOutput layerDisplayOutput);

	public void outputChanged(IScope scope, final double env_width, final double env_height,
		final LayeredDisplayOutput output);

	/**
	 * 
	 * @return an Array of size 3 containing the red, green and blue components
	 */
	int[] getHighlightColor();

	void setHighlightColor(int[] rgb);

	public void addMouseListener(MouseListener e);

	public void removeMouseListener(MouseListener e);

	double getEnvWidth();

	double getEnvHeight();

	public abstract int getDisplayWidth();

	public abstract int getDisplayHeight();

	public abstract void setZoomListener(IZoomListener listener);

	public GamaPoint getModelCoordinates();

	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
		final Point positionInPixels);

	public IList<IAgent> selectAgent(final int x, final int y);

	public boolean isSynchronized();

	void followAgent(IAgent a);

	/**
	 * @return the current zoom level (between 0 and 1).
	 */
	double getZoomLevel();

	void setSize(int x, int y);

	boolean getQualityRendering();

	IScope getDisplayScope();

}
