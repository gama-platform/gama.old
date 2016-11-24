/*********************************************************************************************
 *
 * 'PanTool.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseEvent;

/**
 * A map panning tool for {@link SwtMapPane}.
 * 
 * <p>
 * Allows the user to drag the map
 * with the mouse.
 * 
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 */
public class PanTool extends CursorTool {

	/** Tool name */
	public static final String TOOL_NAME = "Pan";
	/** Tool tip text */
	public static final String TOOL_TIP = "Click and drag to pan";

	private Point panePos;
	boolean panning;

	/**
	 * Constructs a new pan tool. To activate the tool only on certain
	 * mouse events provide a single mask, e.g. {@link SWT#BUTTON1}, or
	 * a combination of multiple SWT-masks.
	 *
	 * @param triggerButtonMask Mouse button which triggers the tool's activation
	 *            or {@value #ANY_BUTTON} if the tool is to be triggered by any button
	 */
	public PanTool(final int triggerButtonMask) {
		super(triggerButtonMask);

		panning = false;
	}

	/**
	 * Constructs a new pan tool which is triggered by any mouse button.
	 */
	public PanTool() {
		this(CursorTool.ANY_BUTTON);
	}

	/**
	 * Respond to a mouse button press event from the map mapPane. This may
	 * signal the start of a mouse drag. Records the event's window position.
	 * @param ev the mouse event
	 */
	@Override
	public void onMousePressed(final MapMouseEvent ev) {

		if ( !isTriggerMouseButton(ev) ) { return; }

		panePos = ev.getPoint();
		panning = true;
	}

	/**
	 * Respond to a mouse dragged event. Calls {@link org.geotools.swing.JMapPane#moveImage()}
	 * @param ev the mouse event
	 */
	@Override
	public void onMouseDragged(final MapMouseEvent ev) {
		if ( panning ) {
			final Point pos = ev.getPoint();
			if ( !pos.equals(panePos) ) {
				getMapPane().moveImage(pos.x - panePos.x, pos.y - panePos.y);
				panePos = pos;
			}
		}
	}

	/**
	 * If this button release is the end of a mouse dragged event, requests the
	 * map mapPane to repaint the display
	 * @param ev the mouse event
	 */
	@Override
	public void onMouseReleased(final MapMouseEvent ev) {
		if ( panning ) {
			panning = false;
			getMapPane().redraw();
		}
	}

	@Override
	public boolean canDraw() {
		return false;
	}

	@Override
	public boolean canMove() {
		return true;
	}
}
