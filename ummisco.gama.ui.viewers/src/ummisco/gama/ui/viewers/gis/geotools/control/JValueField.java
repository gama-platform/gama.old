/*********************************************************************************************
 *
 * 'JValueField.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.control;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Abstract base class for text field controls that work with a simple
 * value such as {@code JIntegerField}.
 *
 * @author Michael Bedward
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public abstract class JValueField extends Text {

    private final Set<ValueChangedListener> listeners;

    public JValueField( Composite parent, int style ) {
        super(parent, style);
        listeners = new HashSet<ValueChangedListener>();
    }

    /**
     * Register a new value changed listener. 
     *
     * @param listener the listener to register.
     */
    public void addValueChangedListener( ValueChangedListener listener ) {
        listeners.add(listener);
    }

    /**
     * Remove the given listener.
     *
     * @param listener the listener to unregister.
     */
    public void removeValueChangedListener( ValueChangedListener listener ) {
        listeners.remove(listener);
    }

    /**
     * Notify listeners of a value change.
     *
     * @param ev the event with details of the value change.
     */
    protected void fireValueChangedEvent( ValueChangedEvent< ? > ev ) {
        for( ValueChangedListener listener : listeners ) {
            listener.onValueChanged(ev);
        }
    }

}
