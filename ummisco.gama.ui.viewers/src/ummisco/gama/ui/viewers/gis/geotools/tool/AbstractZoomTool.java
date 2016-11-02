/*********************************************************************************************
 *
 * 'AbstractZoomTool.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.tool;

import org.eclipse.swt.SWT;


/**
 * Abstract base class for the zoom-in and zoom-out tools. Provides getter / setter
 * methods for the zoom increment.
 * 
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 */

public abstract class AbstractZoomTool extends CursorTool {
    /** The default zoom increment */
    public static final double DEFAULT_ZOOM_FACTOR = 1.5;

    /** The working zoom increment */
    protected double zoom;

    /**
     * Constructs a new abstract zoom tool. To activate the tool only on certain
     * mouse events provide a single mask, e.g. {@link SWT#BUTTON1}, or
     * a combination of multiple SWT-masks.
     *
     * @param triggerButtonMask Mouse button which triggers the tool's activation
     * or {@value #ANY_BUTTON} if the tool is to be triggered by any button
     */
     public AbstractZoomTool(int triggerButtonMask) {
         super(triggerButtonMask);
         setZoom(DEFAULT_ZOOM_FACTOR);
     }

     /**
      * Constructs a new abstract zoom tool which is triggered by any mouse button.
      */
     public AbstractZoomTool() {
         this(CursorTool.ANY_BUTTON);
     }

    
    /**
     * Get the current areal zoom increment. 
     * 
     * @return the current zoom increment as a double
     */
    public double getZoom() {
        return zoom;
    }
    
    /**
     * Set the zoom increment
     * 
     * @param newZoom the new zoom increment; values &lt;= 1.0
     * will be ignored
     * 
     * @return the previous zoom increment
     */
    public double setZoom(double newZoom) {
        double old = zoom;
        if (newZoom > 1.0d) {
            zoom = newZoom;
        }
        return old;
    }

}
