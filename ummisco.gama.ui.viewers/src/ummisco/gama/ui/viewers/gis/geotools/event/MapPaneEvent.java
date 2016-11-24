/*********************************************************************************************
 *
 * 'MapPaneEvent.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.event;

import java.util.EventObject;

import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;

/**
 * An event class used by {@code SwtMapPane} to signal changes of
 * state to listeners.
 *
 * 
 * @author Michael Bedward
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class MapPaneEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    /**
     * Type of MapPane event
     */
    public static enum Type {
        /**
         * The map pane has set a new context.
         */
        NEW_CONTEXT,

        /**
         * The map pane has set a new renderer.
         */
        NEW_RENDERER,

        /**
         * The map pane has been resized.
         */
        PANE_RESIZED,

        /**
         * The display area has been changed. This can
         * include both changes in bounds and in the
         * coordinate reference system.
         */
        DISPLAY_AREA_CHANGED,

        /**
         * The map pane has started rendering features.
         */
        RENDERING_STARTED,

        /**
         * The map pane has stopped rendering features.
         */
        RENDERING_STOPPED,

        /**
         * The map pane is rendering features. The event
         * will carry data that can be retrieved as a floating
         * point value between 0 and 1.
         */
        RENDERING_PROGRESS;
    }

    /** Type of mappane event */
    private Type type;

    /** Data associated with some event types */
    private Object data;

    /**
     * Constructor for an event with no associated data.
     *
     * @param source the map pane issuing this event.
     * @param type the type of event.
     */
    public MapPaneEvent( SwtMapPane source, Type type ) {
        super(source);
        this.type = type;
    }

    /**
     * Constructor for an event with associated data. The new event
     * object takes ownership of the data object.
     *
     * @param source the map pane issuing this event.
     * @param type the type of event.
     * @param data the event data.
     */
    public MapPaneEvent( SwtMapPane source, Type type, Object data ) {
        super(source);
        this.type = type;
        this.data = data;
    }

    /**
     * Get the type of this event.
     * 
     * @return event type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the data associated with this event, if any.
     *
     * @return event data or <code>null</code> if not applicable.
     */
    public Object getData() {
        return data;
    }
}
