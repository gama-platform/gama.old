/*
 * GeoTools - The Open Source Java GIS Toolkit
 * http://geotools.org
 *
 * (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package ummisco.gama.ui.viewers.gis.geotools.tool;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;

import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseEvent;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseListener;

/**
 * Receives mouse events from a {@link SwtMapPane} instance, converts them to
 * {@link MapMouseEvent}s, and sends these to the registered listeners.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 * @author Michael Bedward
 *
 *
 *
 * @source $URL$
 */
public class MapToolManager implements MouseListener, MouseMoveListener, MouseWheelListener, MouseTrackListener {

	private final SwtMapPane mapPane;
	private final Set<MapMouseListener> listeners = new HashSet<MapMouseListener>();
	private CursorTool cursorTool;

	/**
	 * Constructor
	 * 
	 * @param pane the map pane that owns this listener
	 */
	public MapToolManager(final SwtMapPane pane) {
		this.mapPane = pane;
	}

	/**
	 * Unset the current cursor tool
	 */
	public void setNoCursorTool() {
		listeners.remove(cursorTool);
		cursorTool = null;
	}

	/**
	 * Set the active cursor tool
	 * 
	 * @param tool the tool to set
	 * @return true if successful; false otherwise
	 * @throws IllegalArgumentException if the tool argument is null
	 */
	public boolean setCursorTool(final CursorTool tool) {
		if ( tool == null ) { throw new IllegalArgumentException("The argument must not be null"); }

		if ( cursorTool != null ) {
			listeners.remove(cursorTool);
		}

		cursorTool = tool;
		cursorTool.setMapPane(mapPane);
		return listeners.add(tool);
	}

	/**
	 * Get the active cursor tool
	 *
	 * @return live reference to the active cursor tool or {@code null} if no
	 *         tool is active
	 */
	public CursorTool getCursorTool() {
		return cursorTool;
	}

	/**
	 * Add a listener for JMapPaneMouseEvents
	 *
	 * @param listener the listener to add
	 * @return true if successful; false otherwise
	 * @throws IllegalArgumentException if the tool argument is null
	 */
	public boolean addMouseListener(final MapMouseListener listener) {
		if ( listener == null ) { throw new IllegalArgumentException("The argument must not be null"); }

		return listeners.add(listener);
	}

	/**
	 * Remove a MapMouseListener from the active listeners
	 *
	 * @param listener the listener to remove
	 * @return true if successful; false otherwise
	 * @throws IllegalArgumentException if the tool argument is null
	 */
	public boolean removeMouseListener(final MapMouseListener listener) {
		if ( listener == null ) { throw new IllegalArgumentException("The argument must not be null"); }
		return listeners.remove(listener);
	}

	@Override
	public void mouseScrolled(final MouseEvent e) {
		final MapMouseEvent ev = convertWheelEvent(e);
		if ( ev != null ) {
			for ( final MapMouseListener listener : listeners ) {
				listener.onMouseWheelMoved(ev);
			}
		}
	}

	@Override
	public void mouseMove(final MouseEvent e) {
		final MapMouseEvent ev = convertEvent(e);
		if ( ev != null ) {
			for ( final MapMouseListener listener : listeners ) {
				if ( isMouseDown ) {
					listener.onMouseDragged(ev);
				} else {
					listener.onMouseMoved(ev);
				}
			}
		}
	}

	@Override
	public void mouseDoubleClick(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	boolean isMouseDown = false;

	@Override
	public void mouseDown(final MouseEvent e) {
		isMouseDown = true;
		final MapMouseEvent ev = convertEvent(e);
		if ( ev != null ) {
			for ( final MapMouseListener listener : listeners ) {
				listener.onMouseClicked(ev);
			}
			for ( final MapMouseListener listener : listeners ) {
				listener.onMousePressed(ev);
			}
		}
	}

	@Override
	public void mouseUp(final MouseEvent e) {
		isMouseDown = false;
		final MapMouseEvent ev = convertEvent(e);
		if ( ev != null ) {
			for ( final MapMouseListener listener : listeners ) {
				listener.onMouseReleased(ev);
			}
		}
	}

	@Override
	public void mouseEnter(final MouseEvent e) {
		final MapMouseEvent ev = convertEvent(e);
		if ( ev != null ) {
			for ( final MapMouseListener listener : listeners ) {
				listener.onMouseEntered(ev);
			}
		}
	}

	@Override
	public void mouseExit(final MouseEvent e) {
		final MapMouseEvent ev = convertEvent(e);
		if ( ev != null ) {
			for ( final MapMouseListener listener : listeners ) {
				listener.onMouseExited(ev);
			}
		}
	}

	@Override
	public void mouseHover(final MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	private MapMouseEvent convertEvent(final MouseEvent e) {
		MapMouseEvent ev = null;
		if ( mapPane.getScreenToWorldTransform() != null ) {
			ev = new MapMouseEvent(mapPane, e, false);
		}

		return ev;
	}

	private MapMouseEvent convertWheelEvent(final MouseEvent e) {
		MapMouseEvent ev = null;
		if ( mapPane.getScreenToWorldTransform() != null ) {
			ev = new MapMouseEvent(mapPane, e, true);
		}

		return ev;
	}

}
