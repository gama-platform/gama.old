/*********************************************************************************************
 *
 * 'ValueChangedListener.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.control;

/**
 * A listener to work with controls derived from {@code JValueField}.
 *
 * @see JValueField
 * @see ValueChangedEvent
 * 
 * @author Michael Bedward
 *
 *
 *
 * @source $URL$
 */
public interface ValueChangedListener {

    /**
     * Called by the control whose value has just changed.
     *
     * @param ev the event.
     */
    public void onValueChanged( ValueChangedEvent<?> ev );

}
