/*********************************************************************************************
 *
 * 'Controller.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.control;

/**
 * Strategy object for 
 * @author Jesse
 * @since 1.1.0
 *
 *
 *
 * @source $URL$
 */
public interface Controller {

    /**
     * Performs the "ok" function.  Applies the change of CRS
     *
     */
    void handleOk();

    /**
     * Called when Chooser is no longer necessary.  
     */
    void handleClose();

}
