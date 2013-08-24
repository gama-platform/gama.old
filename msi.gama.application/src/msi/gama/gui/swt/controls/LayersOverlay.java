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

		Point origin;
		boolean moving;

		private void checkAction(final int x, final int y) {
			int w = LayersOverlay.super.getPopup().getSize().x;
			if ( x < w && w - x < 20 ) {
				moving = false;
				LayersOverlay.super.getPopup().setCursor(size);
			} else {
				moving = true;
				LayersOverlay.super.getPopup().setCursor(move);
			}
		}

		@Override
		public void handleEvent(final Event e) {
			switch (e.type) {
				case SWT.MouseEnter:
					LayersOverlay.super.getPopup().setActive();
					// LayersOverlay.super.getPopup().setActive();
					break;
				case SWT.MouseExit:
					// LayersOverlay.super.getPopup().setDragDetect(false);
					break;
				case SWT.MouseDown:
					origin = new Point(e.x, e.y);
					checkAction(e.x, e.y);
					break;
				case SWT.MouseUp:
					origin = null;
					break;
				case SWT.MouseHover:
					checkAction(e.x, e.y);
					break;
				case SWT.MouseDoubleClick:
					reset();
					break;
				case SWT.MouseMove:
					if ( origin == null ) {
						checkAction(e.x, e.y);
					} else if ( moving ) {
						Point p = SwtGui.getDisplay().map(LayersOverlay.super.getPopup(), null, e.x, e.y);
						changeLocationTo(p.x - origin.x, p.y - origin.y);
					} else if ( !moving ) {
						Point p = SwtGui.getDisplay().map(LayersOverlay.super.getPopup(), null, e.x, e.y);
						changeWidthTo(p.x - LayersOverlay.super.getPopup().getLocation().x);
					}
					break;
			}
		}
	};

	Shell nonTransparentShell;
	Point customLocation;
	Integer customWidth;

	public LayersOverlay(final LayeredDisplayView view) {
		super(view);
		super.getPopup().addListener(SWT.MouseDown, l);
		super.getPopup().addListener(SWT.MouseUp, l);
		super.getPopup().addListener(SWT.MouseMove, l);
		super.getPopup().addListener(SWT.MouseDoubleClick, l);
		super.getPopup().addListener(SWT.MouseHover, l);
		super.getPopup().addListener(SWT.MouseEnter, l);
		super.getPopup().addListener(SWT.MouseExit, l);
		nonTransparentShell = new Shell(super.getPopup(), SWT.TOOL | SWT.NO_TRIM);
		nonTransparentShell.setLayout(new FillLayout());
		nonTransparentShell.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
	}

	@Override
	protected Control createControl() {
		return getView().getComponent();
	}

	@Override
	protected void populateControl() {}

	@Override
	protected Point getLocation() {
		if ( customLocation == null ) { return getView().getLayersOverlayPosition(); }
		return customLocation;
	}

	@Override
	protected Point getSize() {
		Point p = getView().getLayersOverlaySize();
		if ( customWidth == null ) { return p; }
		return new Point(customWidth, p.y);
	}

	@Override
	public Shell getPopup() {
		return nonTransparentShell;
	}

	@Override
	public void display() {
		if ( isHidden() ) { return; }
		super.display();
		if ( nonTransparentShell.isDisposed() ) { return; }
		nonTransparentShell.setVisible(true);
	}

	@Override
	public void relocate() {
		super.relocate();
		if ( nonTransparentShell.isDisposed() ) { return; }
		nonTransparentShell.setLocation(super.getPopup().getLocation());
	}

	@Override
	public void resize() {
		// if ( isHidden() ) { return; }
		super.resize();
		if ( super.getPopup().isDisposed() ) { return; }
		// nonTransparentShell.setLocation(super.getPopup().getLocation());
		nonTransparentShell.setSize(nonTransparentShell.computeSize(customWidth == null ? SWT.DEFAULT : customWidth,
			SWT.DEFAULT));
		// nonTransparentShell.pack();
		super.getPopup().setSize(nonTransparentShell.getSize().x, super.getPopup().getSize().y);
	}

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
		if ( !nonTransparentShell.isDisposed() ) {
			nonTransparentShell.setVisible(false);
		}
	}

	@Override
	public void close() {
		super.close();
		if ( !nonTransparentShell.isDisposed() ) {
			nonTransparentShell.dispose();
		}
	}
}
