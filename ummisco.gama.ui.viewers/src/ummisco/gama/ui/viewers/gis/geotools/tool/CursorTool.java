/*********************************************************************************************
 *
 * 'CursorTool.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.tool;

import org.eclipse.swt.SWT;

import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseAdapter;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseEvent;

/**
 * The base class for map pane cursor tools. Simply adds a getCursor
 * method to the MapToolAdapter
 * 
 * @author Michael Bedward
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public abstract class CursorTool extends MapMouseAdapter {

	/**
	 * Flag indicating that the tool should be triggered whenever any mouse button
	 * is used.
	 */
	public static final int ANY_BUTTON = SWT.BUTTON_MASK;

	private SwtMapPane mapPane;
	private final int triggerButtonMask;

	/**
	 * Constructs a new cursor tool. To activate the tool only on certain
	 * mouse events provide a single mask, e.g. {@link SWT#BUTTON1}, or
	 * a combination of multiple SWT-masks.
	 *
	 * @param triggerButtonMask Mouse button which triggers the tool's activation
	 *            or {@value #ANY_BUTTON} if the tool is to be triggered by any button
	 */
	public CursorTool(final int triggerButtonMask) {
		this.triggerButtonMask = triggerButtonMask;
	}

	/**
	 * Constructs a new cursor tool which is triggered by any mouse button.
	 */
	public CursorTool() {
		this(ANY_BUTTON);
	}

	/**
	 * Set the map pane that this cursor tool is associated with
	 * @param pane the map pane
	 * @throws IllegalArgumentException if mapPane is null
	 */
	public void setMapPane(final SwtMapPane pane) {
		if ( pane == null ) { throw new IllegalArgumentException("The argument must not be null"); }

		this.mapPane = pane;
	}

	/**
	 * Get the map pane that this tool is servicing
	 *
	 * @return the map pane
	 */
	public SwtMapPane getMapPane() {
		return mapPane;
	}

	/**
	 * Get the cursor for this tool. Sub-classes should override this
	 * method to provide a custom cursor.
	 *
	 * @return the default cursor
	 */
	// public Cursor getCursor() {
	// return CursorManager.getInstance().getArrowCursor();
	// }

	/**
	 * Checks if the tool can draw when dragging.
	 * 
	 * @return <code>true</code> if the tool can draw.
	 */
	public abstract boolean canDraw();

	/**
	 * Checks if the tool can move the map when dragging.
	 * 
	 * @return <code>true</code> if the tool can move the map while dragging.
	 */
	public abstract boolean canMove();

	/**
	 * Returns <code>true</code> for any tool which is drawing
	 * while dragging. For tools which are triggered only by a certain
	 * mouse event it might be the case that {@link #canDraw()} is
	 * <code>true</code> while they are actually not active.
	 *
	 * @return <code>true</code> if the tool is drawing while dragging
	 */
	public boolean isDrawing() {
		return canDraw();
	}

	/**
	 * Checks if the tool should be triggered by the event.
	 * @param event event to be checked
	 * @return <code>true</code> if the tool is triggered by the event
	 */
	protected boolean isTriggerMouseButton(final MapMouseEvent event) {
		return triggerButtonMask == ANY_BUTTON || // on mouse move or mouse drag the mouse button field is 0 but the state mask is set
			0 != (triggerButtonMask & event.getStateMask()) || // on mouse click the state mask is 0, but the mouse button field is set
			event.getStateMask() == 0 && ((triggerButtonMask & SWT.BUTTON1) != 0 && event.getMouseButton() == 1 ||
				(triggerButtonMask & SWT.BUTTON2) != 0 && event.getMouseButton() == 2 ||
				(triggerButtonMask & SWT.BUTTON3) != 0 && event.getMouseButton() == 3 ||
				(triggerButtonMask & SWT.BUTTON4) != 0 && event.getMouseButton() == 4 ||
				(triggerButtonMask & SWT.BUTTON5) != 0 && event.getMouseButton() == 5);
	}
}
