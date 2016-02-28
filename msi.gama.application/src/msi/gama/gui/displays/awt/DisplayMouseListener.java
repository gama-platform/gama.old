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

	private final Java2DDisplaySurface surface;
	private Point mousePosition;
	private boolean dragging;

	DisplayMouseListener(final Java2DDisplaySurface surface) {
		this.surface = surface;
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if ( SwingUtilities.isLeftMouseButton(e) ) {
			dragging = true;
			final Point p = e.getPoint();
			if ( getMousePosition() == null ) {
				setMousePosition(new Point(surface.getWidth() / 2, surface.getHeight() / 2));
			}
			Point origin = surface.getOrigin();
			surface.setOrigin(origin.x + p.x - getMousePosition().x, origin.y + p.y - getMousePosition().y);
			setMousePosition(p);
			surface.updateDisplay(true);
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		// we need the mouse position so that after zooming
		// that position of the image is maintained
		setMousePosition(e.getPoint());
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		final boolean zoomIn = e.getWheelRotation() < 0;
		setMousePosition(e.getPoint());
		Point p = new Point(getMousePosition().x, getMousePosition().y);
		double zoomFactor =
			surface.applyZoom(zoomIn ? 1.0 + surface.getZoomIncrement() : 1.0 - surface.getZoomIncrement());
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
			surface.updateDisplay(true);
			dragging = false;

		}

	}

	/**
	 * @return the mousePosition
	 */
	public Point getMousePosition() {
		return mousePosition;
	}

	/**
	 * @param mousePosition the mousePosition to set
	 */
	public void setMousePosition(Point mousePosition) {
		this.mousePosition = mousePosition;
	}

}