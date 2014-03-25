package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

/**
 * The class Popup.
 * 
 * @author drogoul
 * @since 19 aug. 2013
 * 
 */
public class LayersOverlay extends AbstractOverlay {

	static Cursor size = new Cursor(SwtGui.getDisplay(), SWT.CURSOR_SIZEWE);
	static Cursor move = new Cursor(SwtGui.getDisplay(), SWT.CURSOR_SIZEALL);
	Listener l = new Listener() {

		// Point origin;

		// boolean moving, sizing;

		// private void checkAction(final int x, final int y) {
		// int w = LayersOverlay.super.getPopup().getSize().x;
		// if ( x < w && w - x < 20 ) {
		// // moving = false;
		// // sizing = true;
		// LayersOverlay.super.getPopup().setCursor(size);
		// } else {
		// // moving = true;
		// // sizing = false;
		// LayersOverlay.super.getPopup().setCursor(move);
		// }
		// }

		@Override
		public void handleEvent(final Event e) {
			switch (e.type) {
				case SWT.MouseEnter:
					LayersOverlay.super.getPopup().setActive();
					break;
				case SWT.MouseExit:
					// LayersOverlay.super.getPopup().setDragDetect(false);
					break;
				case SWT.MouseDown:
					// origin = new Point(e.x, e.y);
					// checkAction(e.x, e.y);
					break;
				case SWT.MouseUp:
					// origin = null;
					// if ( !sizing ) {// FIXME PROBLEM !!
					setHidden(true);
					// }
					break;
				case SWT.MouseHover:
					// checkAction(e.x, e.y);
					break;
				case SWT.MouseDoubleClick:
					// reset();
					break;
				case SWT.MouseMove:
					// if ( origin == null ) {
					// checkAction(e.x, e.y);
					// } else if ( moving ) {
					// Point p = SwtGui.getDisplay().map(LayersOverlay.super.getPopup(), null, e.x, e.y);
					// changeLocationTo(p.x - origin.x, p.y - origin.y);
					// } else if ( sizing ) {
					// Point p = SwtGui.getDisplay().map(LayersOverlay.super.getPopup(), null, e.x, e.y);
					// changeWidthTo(p.x - LayersOverlay.super.getPopup().getLocation().x);
					// }
					break;
			}
		}
	};

	Shell innerShell;
	Point customLocation;
	Integer customWidth;

	public LayersOverlay(final LayeredDisplayView view) {
		super(view, false);
		super.getPopup().addListener(SWT.MouseDown, l);
		super.getPopup().addListener(SWT.MouseUp, l);
		super.getPopup().addListener(SWT.MouseMove, l);
		super.getPopup().addListener(SWT.MouseDoubleClick, l);
		super.getPopup().addListener(SWT.MouseHover, l);
		super.getPopup().addListener(SWT.MouseEnter, l);
		super.getPopup().addListener(SWT.MouseExit, l);
		innerShell = new Shell(super.getPopup(), SWT.NO_TRIM);
		innerShell.setLayout(new FillLayout());
		innerShell.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
	}

	@Override
	protected Point getLocation() {
		if ( customLocation == null ) {
			Composite surfaceComposite = getView().getComponent();
			return surfaceComposite.toDisplay(surfaceComposite.getLocation());
		}
		return customLocation;
	}

	@Override
	protected Point getSize() {
		Composite surfaceComposite = getView().getComponent();
		Point s = surfaceComposite.getSize();
		Point p = new Point(s.x / 3, s.y - 32);
		if ( customWidth == null ) { return p; }
		return new Point(customWidth, p.y);
	}

	@Override
	public Shell getPopup() {
		return innerShell;
	}

	@Override
	public void display() {
		if ( isHidden() ) { return; }
		super.display();
		if ( innerShell.isDisposed() ) { return; }
		innerShell.setVisible(true);
	}

	@Override
	public void relocate() {
		if ( isHidden() ) { return; }
		if ( innerShell.isDisposed() ) { return; }
		super.relocate();
		innerShell.setLocation(super.getPopup().getLocation());
	}

	public void resizeInnerShell() {
		innerShell.setSize(innerShell.computeSize(getSize().x, SWT.DEFAULT));
	}

	@Override
	public void resize() {
		// fail fast
		if ( isHidden() ) { return; }
		if ( super.getPopup().isDisposed() ) { return; }
		// We gather the size imposed by the view, if any
		Point size = getSize();
		// We then ask the control to compute its ideal size
		final Point controlSize = innerShell.computeSize(customWidth == null ? SWT.DEFAULT : customWidth, SWT.DEFAULT);
		int w = Math.max(size.x, controlSize.x);
		int h = controlSize.y;
		if ( customLocation == null ) {
			Point componentSize = getView().getComponent().getSize();
			// AD we dont want the overlay to be wider than the view if the overlay is linked with it
			w = Math.min(w, componentSize.x);
			h = Math.min(h, componentSize.y);
		}
		innerShell.setSize(innerShell.computeSize(w, h));
		super.getPopup().setSize(w, size.y);
	}

	//
	// @Override
	// public void appear() {
	// Composite surfaceComposite = getView().getComponent();
	// Point loc = surfaceComposite.toDisplay(surfaceComposite.getLocation());
	// Point s = surfaceComposite.getSize();
	// Point p = new Point(10, s.y);
	// slidingShell.setBounds(loc.x, loc.y, p.x, p.y);
	// super.appear();
	// }

	protected void changeLocationTo(final int x, final int y) {
		customLocation = new Point(x, y);
		relocate();
	}

	protected void changeWidthTo(final int w) {
		customWidth = w;
		resize();
	}

	protected void reset() {
		customLocation = null;
		customWidth = null;
		relocate();
		resize();
	}

	@Override
	public void hide() {
		super.hide();
		if ( !innerShell.isDisposed() ) {
			innerShell.setVisible(false);
		}
	}

	@Override
	public void close() {
		super.close();
		if ( !innerShell.isDisposed() ) {
			innerShell.dispose();
		}
	}

}
