/*********************************************************************************************
 *
 * 'MapAction.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;

/**
 * Base class for map pane actions.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * @since 2.7
 *
 *
 *
 * @source $URL$
 */
public abstract class MapAction extends Action {

    protected SwtMapPane mapPane;

    public MapAction( String toolName, String toolTip, Image image ) {
        if (toolName != null) {
            setText(toolName);
        }
        if (toolTip != null) {
            setToolTipText(toolTip);
        }
        if (image != null) {
            setImageDescriptor(ImageDescriptor.createFromImage(image));
        }
    }

    public abstract void run();

    /**
     * Set the right {@link SwtMapPane map pane} to the action.
     * 
     * @param mapPane the map pane to use.
     */
    public void setMapPane( SwtMapPane mapPane ) {
        this.mapPane = mapPane;
    }

    /**
     * Getter for the current {@link SwtMapPane map pane}.
     * 
     * @return the current map pane.
     */
    public SwtMapPane getMapPane() {
        return mapPane;
    }
}
