/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

/**
 * The class Popup.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class Overlay {

	private final Shell popup = new Shell(SwtGui.getDisplay(), SWT.ON_TOP | SWT.NO_TRIM);
	private final Label popupText = new Label(popup, SWT.NONE);
	private final Listener hide = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			hide();
		}
	};

	private final Listener resize = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			resize();
		}
	};

	{
		popup.setLayout(new FillLayout());
		popup.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		popupText.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		popup.setAlpha(140);
	}

	class MouseListener implements MouseTrackListener, MouseMoveListener {

		@Override
		public void mouseEnter(final MouseEvent e) {
			display();
			isVisible = true;
		}

		@Override
		public void mouseExit(final MouseEvent e) {
			// hide();
			// isVisible = false;
		}

		@Override
		public void mouseHover(final MouseEvent e) {
			display();
			isVisible = true;
		}

		/**
		 * Method mouseMove()
		 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
		 */
		@Override
		public void mouseMove(final MouseEvent e) {
			display();
			isVisible = true;
		}

	};

	private boolean isVisible;
	private final LayeredDisplayView view;

	/*
	 * 
	 */
	public Overlay(final LayeredDisplayView view) {
		this.view = view;
		MouseListener ml = new MouseListener();
		Composite c = view.getComponent();
		c.addMouseTrackListener(ml);
		c.addMouseMoveListener(ml);
		// final Shell parent = provider.getControllingShell();
		c.addListener(SWT.Move, resize);
		c.addListener(SWT.Resize, resize);
		c.addListener(SWT.Close, hide);
		c.addListener(SWT.Deactivate, hide);
		c.addListener(SWT.Hide, hide);
		// for ( final Widget c : controls ) {
		// if ( c == null ) {
		// continue;
		// }
		// final TypedListener typedListener = new TypedListener(mtl);
		// c.addListener(SWT.MouseEnter, typedListener);
		// c.addListener(SWT.MouseExit, typedListener);
		// c.addListener(SWT.MouseHover, typedListener);
		// }
	}

	public void display() {
		// We first verify that the popup is still ok
		final Shell c = view.getSite().getShell();
		if ( c == null || c.isDisposed() ) {
			hide();
			return;
		}
		// We then grab the text and hide if it is null or empty
		String s = view.getOverlayText();
		if ( s == null || s.isEmpty() ) {
			hide();
			return;
		}

		// We set the background of the popup by asking the provider
		// popupText.setBackground(provider.getPopupBackground());

		// We fix the max. width to 400
		// final int maxPopupWidth = 400;

		// We compute the width of the text (+ 5 pixels to accomodate for the border)
		// final GC gc = new GC(popupText);
		// final int textWidth = gc.textExtent(s).x + 5;
		// gc.dispose();
		// GuiUtils.debug("Popup.display: textWidth = " + textWidth);
		// We grab the location of the popup on the display

		// We set the text of the popup
		popupText.setText(s);

		// We ask the popup to compute its actual size given the width and to display itself
		// final Point newPopupSize = popup.computeSize(popupWidth, SWT.DEFAULT);
		// popup.setSize(newPopupSize);
		// resize();
		popup.layout();
		popup.setVisible(true);
		// popup.open();
	}

	public void resize() {
		final Point point = view.getComponent().toDisplay(view.getComponent().getLocation());
		final Point size = view.getComponent().getSize();
		popup.setLocation(point.x, point.y + size.y - 16);
		popup.setSize(popup.computeSize(size.x, 16));
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void hide() {
		if ( !popup.isDisposed() ) {
			popup.setVisible(false);
		}
	}

	/**
	 * 
	 */
	public void close() {
		isVisible = false;
		popup.dispose();
	}
}
