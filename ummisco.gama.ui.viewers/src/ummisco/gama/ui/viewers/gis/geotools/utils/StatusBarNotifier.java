/*********************************************************************************************
 *
 * 'StatusBarNotifier.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.utils;

import org.eclipse.jface.window.ApplicationWindow;
import org.geotools.geometry.DirectPosition2D;

import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseAdapter;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseEvent;
import ummisco.gama.ui.viewers.gis.geotools.event.MapPaneAdapter;
import ummisco.gama.ui.viewers.gis.geotools.event.MapPaneEvent;

/**
 * The notifier for the statusbar. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class StatusBarNotifier {
    private final ApplicationWindow applicationWindow;
    private MapMouseAdapter mouseListener;
    private MapPaneAdapter mapPaneListener;

    public StatusBarNotifier( ApplicationWindow applicationWindow, SwtMapPane mapPane ) {
        this.applicationWindow = applicationWindow;

        createListeners();

        mapPane.addMouseListener(mouseListener);
        mapPane.addMapPaneListener(mapPaneListener);
    }

    /**
     * Initialize the mouse and map bounds listeners
     */
    private void createListeners() {
        mouseListener = new MapMouseAdapter(){

            @Override
            public void onMouseMoved( MapMouseEvent ev ) {
                displayCoords(ev.getMapPosition());
            }

            @Override
            public void onMouseExited( MapMouseEvent ev ) {
                clearCoords();
            }
        };

        mapPaneListener = new MapPaneAdapter(){

            @Override
            public void onDisplayAreaChanged( MapPaneEvent ev ) {
            }

            @Override
            public void onResized( MapPaneEvent ev ) {
            }

            @Override
            public void onRenderingStarted( MapPaneEvent ev ) {
                applicationWindow.setStatus("rendering...");
            }

            @Override
            public void onRenderingStopped( MapPaneEvent ev ) {
                applicationWindow.setStatus("");
            }

            @Override
            public void onRenderingProgress( MapPaneEvent ev ) {
            }

        };
    }

    /**
     * Format and display the coordinates of the given position
     *
     * @param mapPos mouse cursor position (world coords)
     */
    public void displayCoords( DirectPosition2D mapPos ) {
        if (mapPos != null) {
            applicationWindow.setStatus(String.format("  %.2f %.2f", mapPos.x, mapPos.y));
        }
    }

    public void clearCoords() {
        applicationWindow.setStatus("");
    }

}
