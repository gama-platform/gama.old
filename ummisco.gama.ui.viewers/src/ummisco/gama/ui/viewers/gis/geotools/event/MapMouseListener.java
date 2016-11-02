/*********************************************************************************************
 *
 * 'MapMouseListener.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.event;

/**
 * Interface for classes that listen to JMapPaneMouseEvents
 *
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 */
public interface MapMouseListener {
    
    /**
     * Respond to a mouse click event received from the map pane
     *
     * @param ev the mouse event
     */
    public void onMouseClicked(MapMouseEvent ev);

    /**
     * Respond to a mouse dragged event received from the map pane
     *
     * @param ev the mouse event
     */
    public void onMouseDragged(MapMouseEvent ev);

    /**
     * Respond to a mouse entered event received from the map pane
     *
     * @param ev the mouse event
     */
    public void onMouseEntered(MapMouseEvent ev);

    /**
     * Respond to a mouse exited event received from the map pane
     *
     * @param ev the mouse event
     */
    public void onMouseExited(MapMouseEvent ev);

    /**
     * Respond to a mouse movement event received from the map pane
     *
     * @param ev the mouse event
     */
    public void onMouseMoved(MapMouseEvent ev);

    /**
     * Respond to a mouse button press event received from the map pane
     *
     * @param ev the mouse event
     */
    public void onMousePressed(MapMouseEvent ev);

    /**
     * Respond to a mouse button release event received from the map pane
     *
     * @param ev the mouse event
     */
    public void onMouseReleased(MapMouseEvent ev);

    /**
     * Respond to a mouse wheel scroll event received from the map pane
     *
     * @param ev the mouse event
     */
    public void onMouseWheelMoved(MapMouseEvent ev);

}
