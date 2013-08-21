package msi.gama.gui.views;

import msi.gama.common.interfaces.IDisplaySurface.IZoomListener;
import msi.gama.gui.views.actions.ZoomIndicatorItem;

/**
 * A view that may be zoomed.
 * Zooms are made in an asynchronous way (that is, not blocking call).
 * 
 * @author Samuel Thiriot
 * 
 */
public interface IViewWithZoom extends IZoomListener {

	public void zoomToFit();

	public void zoomIn();

	public void zoomOut();

	/**
	 * Switch between the 2D view and the 3D View
	 */
	public void toggleView();

	/**
	 * Switch between Picking or rendering only mode.
	 */
	// public void togglePicking();

	/**
	 * Activate the arcball View
	 */
	public void toggleArcball();

	/**
	 * Activate the select rectangle View
	 */
	public void toggleSelectRectangle();

	/**
	 * Activate the triangulation View
	 */
	public void toggleTriangulation();

	/**
	 * Split species layer in 3D
	 */
	public void toggleSplitLayer();

	/**
	 * Rotate the mode l around z axes
	 */
	public void toggleRotation();

	/*
	 * Switch between ArcBall and FreeFly Cameras
	 */
	public void toggleCamera();

	public void snapshot();

	/**
	 * If true, the view should block the simulation when refresh is called.
	 * Else it may work in a "best-effort" way (display as soon as possible)
	 * @param synchro
	 */
	public void setSynchronized(boolean synchro);

	/**
	 * Enable to load a .Shp file and display it
	 */
	public void addShapeFile();

	public void setIndicator(ZoomIndicatorItem indicator);

}
