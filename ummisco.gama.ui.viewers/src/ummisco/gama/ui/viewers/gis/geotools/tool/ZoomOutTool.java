/*********************************************************************************************
 *
 * 'ZoomOutTool.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;

import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseEvent;

/**
 * A zoom-out tool for JMapPane.
 * <p>
 * For mouse clicks, the display will be zoomed-out such that the
 * map centre is the position of the mouse click and the map
 * width and height are calculated as:
 * 
 * <pre>
 *    {@code len = len.old * z}
 * </pre>
 * 
 * where {@code z} is the linear zoom increment (>= 1.0)
 * 
 * @author Michael Bedward
 * @author Andrea Antonello (www.hydrologis.com)
 * @since 2.6
 *
 *
 *
 * @source $URL$
 */
public class ZoomOutTool extends AbstractZoomTool {

	/** Tool name */
	public static final String TOOL_NAME = "Zoom out";
	/** Tool tip text */
	public static final String TOOL_TIP = "Click to zoom out centered on cursor position";

	/**
	 * Constructs a new zoom out tool. To activate the tool only on certain
	 * mouse events provide a single mask, e.g. {@link SWT#BUTTON1}, or
	 * a combination of multiple SWT-masks.
	 *
	 * @param triggerButtonMask Mouse button which triggers the tool's activation
	 *            or {@value #ANY_BUTTON} if the tool is to be triggered by any button
	 */
	public ZoomOutTool(final int triggerButtonMask) {
		super(triggerButtonMask);
	}

	/**
	 * Constructs a new zoom out tool which is triggered by any mouse button.
	 */
	public ZoomOutTool() {
		this(CursorTool.ANY_BUTTON);
	}

	/**
	 * Zoom out by the currently set increment, with the map
	 * centred at the location (in world coords) of the mouse
	 * click
	 *
	 * @param ev the mouse event
	 */
	@Override
	public void onMouseClicked(final MapMouseEvent ev) {

		if ( !isTriggerMouseButton(ev) ) { return; }

		final Rectangle paneArea = getMapPane().getBounds();
		final DirectPosition2D mapPos = ev.getMapPosition();

		final double scale = getMapPane().getWorldToScreenTransform().getScaleX();
		final double newScale = scale / zoom;

		final DirectPosition2D corner = new DirectPosition2D(mapPos.getX() - 0.5d * paneArea.width / newScale,
			mapPos.getY() + 0.5d * paneArea.height / newScale);

		final Envelope2D newMapArea = new Envelope2D();
		newMapArea.setFrameFromCenter(mapPos, corner);
		getMapPane().setDisplayArea(newMapArea);
	}

	@Override
	public boolean canDraw() {
		return false;
	}

	@Override
	public boolean canMove() {
		return false;
	}
}
