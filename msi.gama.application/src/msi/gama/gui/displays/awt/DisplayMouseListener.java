/**
 * Created by drogoul, 22 févr. 2016
 *
 */
package msi.gama.gui.displays.awt;

import java.awt.Point;
import java.awt.event.*;
import javax.swing.SwingUtilities;
import msi.gaml.operators.fastmaths.FastMath;

class DisplayMouseListener extends MouseAdapter {

	/**
	 *
	 */
	private final AWTJava2DDisplaySurface2 surface;
	public Point mousePosition;

	/**
	 * @param awtJava2DDisplaySurface2
	 */
	DisplayMouseListener(final AWTJava2DDisplaySurface2 awtJava2DDisplaySurface2) {
		surface = awtJava2DDisplaySurface2;
	}

	boolean dragging;

	@Override
	public void mouseDragged(final MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			dragging = true;
			surface.canBeUpdated = false;
			final Point p = e.getPoint();
			if ( mousePosition == null ) {
				mousePosition = new Point(surface.getWidth() / 2, surface.getHeight() / 2);
			}
			Point origin = surface.getOrigin();
			surface.setOrigin(origin.x + p.x - mousePosition.x, origin.y + p.y - mousePosition.y);
			mousePosition = p;
			surface.repaint();
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		// we need the mouse position so that after zooming
		// that position of the image is maintained
		mousePosition = e.getPoint();
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		final boolean zoomIn = e.getWheelRotation() < 0;
		mousePosition = e.getPoint();
		Point p = new Point(mousePosition.x, mousePosition.y);
		double zoomFactor = surface.applyZoom(zoomIn ? 1.0 + surface.zoomIncrement : 1.0 - surface.zoomIncrement);
		Point origin = surface.getOrigin();
		double newx = FastMath.round(zoomFactor * (p.x - origin.x) - p.x + surface.getWidth() / 2d);
		double newy = FastMath.round(zoomFactor * (p.y - origin.y) - p.y + surface.getHeight() / 2d);
		surface.centerOnDisplayCoordinates(new Point((int) newx, (int) newy));
		surface.updateDisplay(true);
	}

	@Override
	public void mouseClicked(final MouseEvent evt) {
		if ( evt.getClickCount() == 2 ) {
			surface.zoomFit();
		} else if ( evt.isControlDown() || evt.isMetaDown() || evt.isPopupTrigger() ) {
			surface.selectAgents(evt.getX(), evt.getY());
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if ( dragging ) {
			surface.canBeUpdated = true;
			surface.updateDisplay(true);
			dragging = false;

		}

	}

}