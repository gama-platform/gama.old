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

	private final Shell popup;
	private final Label popupText;
	private boolean isHidden = false;
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
	private final MouseMoveListener move = new MouseMoveListener() {

		@Override
		public void mouseMove(final MouseEvent e) {
			display();
		}
	};

	private final LayeredDisplayView view;

	public Overlay(final LayeredDisplayView view) {
		popup = new Shell(SwtGui.getDisplay().getActiveShell(), SWT.TOOL | SWT.NO_TRIM);
		popup.setLayout(new FillLayout());
		popup.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		popupText = new Label(popup, SWT.NONE);
		popupText.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		popup.setAlpha(140);
		popup.layout();
		this.view = view;
		Composite c = view.getComponent();
		c.addMouseMoveListener(move);
		c.addListener(SWT.Move, resize);
		c.addListener(SWT.Resize, resize);
		c.addListener(SWT.Close, hide);
		// c.addListener(SWT.Deactivate, hide);
		c.addListener(SWT.Hide, hide);
	}

	public void display() {
		if ( isHidden ) { return; }
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
		// We set the text of the popup
		popupText.setText(s);
		popup.setVisible(true);
	}

	public void resize() {
		if ( isHidden ) { return; }
		popup.setLocation(view.getOverlayPosition());
		final Point size = view.getOverlaySize();
		popup.setSize(popup.computeSize(size.x, size.y));
		popup.setVisible(true);
	}

	public void hide() {
		if ( !popup.isDisposed() ) {
			popup.setVisible(false);
		}
	}

	public void close() {
		popup.dispose();
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(final boolean hidden) {
		isHidden = hidden;
		if ( isHidden ) {
			hide();
		} else {
			resize();
			display();
		}
	}
}
