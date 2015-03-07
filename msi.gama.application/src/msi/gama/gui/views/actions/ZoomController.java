/**
 * Created by drogoul, 9 févr. 2015
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.IToolbarDecoratedView.Zoomable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Control;

/**
 * Class ZoomController.
 * 
 * @author drogoul
 * @since 9 févr. 2015
 * 
 */
public class ZoomController {

	Zoomable view;
	private final GestureListener gl = new GestureListener() {

		@Override
		public void gesture(final GestureEvent ge) {
			if ( ge.detail == SWT.GESTURE_MAGNIFY ) {
				if ( ge.magnification > 1.0 ) {
					view.zoomIn();
				} else if ( ge.magnification < 1.0 ) {
					view.zoomOut();
				}
			}

		}
	};
	//
	// private final MouseWheelListener ml = new MouseWheelListener() {
	//
	// @Override
	// public void mouseScrolled(final MouseEvent e) {
	// if ( e.count > 0 ) {
	// view.zoomIn();
	// } else {
	// view.zoomOut();
	// }
	// }
	// };

	private final MouseListener ml = new MouseAdapter() {

		@Override
		public void mouseDoubleClick(final MouseEvent e) {
			view.zoomFit();
		}
	};

	/**
	 * @param view
	 */
	public ZoomController(final Zoomable view) {
		this.view = view;
	}

	public void install(final GamaToolbarSimple tb) {
		tb.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				Control[] controls = view.getZoomableControls();
				for ( Control c : controls ) {
					if ( c != null ) {
						c.addGestureListener(gl);
						c.addMouseListener(ml);
						// c.addMouseWheelListener(ml);
						// once installed the listener removes itself from the toolbar
						tb.removeControlListener(this);
					}
				}
			}

		});
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMIN.getCode(), "Zoom in", "Zoom in", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.zoomIn();
			}
		});
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMFIT.getCode(), "Zoom fit", "Zoom to fit view", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.zoomFit();
			}

		});

		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMOUT.getCode(), "Zoom out", "Zoom out", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.zoomOut();
			}
		});

	}

	/**
	 * @param tb
	 */
	public void install(final GamaToolbar2 tb) {

		tb.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				Control[] controls = view.getZoomableControls();
				for ( Control c : controls ) {
					if ( c != null ) {
						c.addGestureListener(gl);
						c.addMouseListener(ml);
						// c.addMouseWheelListener(ml);
						// once installed the listener removes itself from the toolbar
						tb.removeControlListener(this);
					}
				}
			}

		});
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMIN.getCode(), "Zoom in", "Zoom in", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.zoomIn();
			}
		}, SWT.RIGHT);
		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMFIT.getCode(), "Zoom fit", "Zoom to fit view", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.zoomFit();
			}

		}, SWT.RIGHT);

		tb.button(IGamaIcons.DISPLAY_TOOLBAR_ZOOMOUT.getCode(), "Zoom out", "Zoom out", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				view.zoomOut();
			}
		}, SWT.RIGHT);

	}

}
