/*********************************************************************************************
 *
 * 'ValueChangedEvent.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.control;

import org.eclipse.swt.widgets.Control;

/**
 * An event published when the value of a control derived from {@code JValueField}
 * changes.
 *
 * @see JValueField
 * @see ValueChangedListener
 *
 * @author Michael Bedward
 * @author Andrea Antonello (www.hydrologis.com)
 * @since 2.7
 *
 *
 *
 * @source $URL$
 */
public class ValueChangedEvent<T> {

    private Control source;
    private T newValue;

    /**
     * Create a value changed event.
     * 
     * @param source the control holding the value.
     * @param newValue the updated value.
     */
    public ValueChangedEvent( Control source, T newValue ) {
        this.newValue = newValue;
        this.source = source;
    }

    /**
     * Get the control that invoked this event.
     *
     * @return the invoking control.
     */
    public Control getSource() {
        return source;
    }

    /**
     * Get the updated value.
     *
     * @return the updated value.
     */
    public T getValue() {
        return newValue;
    }
}
