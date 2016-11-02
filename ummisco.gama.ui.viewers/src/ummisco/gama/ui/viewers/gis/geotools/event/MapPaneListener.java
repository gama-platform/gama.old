/*********************************************************************************************
 *
 * 'MapPaneListener.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.event;

/**
 * Implemented by classes that wish to receive MapPaneEvents
 *
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 */
public interface MapPaneListener {

    /**
     * Called by the map pane when a new map context has been set
     *
     * @param ev the event
     */
    public void onNewContext(MapPaneEvent ev);

    /**
     * Called by the map pane when a new renderer has been set
     *
     * @param ev the event
     */
    public void onNewRenderer(MapPaneEvent ev);

    /**
     * Called by the map pane when it has been resized
     *
     * @param ev the event
     */
    public void onResized(MapPaneEvent ev);

    /**
     * Called by the map pane when its display area has been
     * changed e.g. by zooming or panning
     *
     * @param ev the event
     */
    public void onDisplayAreaChanged(MapPaneEvent ev);

    /**
     * Called by the map pane when it has started rendering features
     * 
     * @param ev the event
     */
    public void onRenderingStarted(MapPaneEvent ev);

    /**
     * Called by the map pane when it has stopped rendering features
     *
     * @param ev the event
     */
    public void onRenderingStopped(MapPaneEvent ev);

    /**
     * Called by the map pane when it is rendering features. The
     * event will be carrying data: a floating point value between
     * 0 and 1 indicating rendering progress.
     * 
     * @param ev the event
     */
    public void onRenderingProgress(MapPaneEvent ev);

}
