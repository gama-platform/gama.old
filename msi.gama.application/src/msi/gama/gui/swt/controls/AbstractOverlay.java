package msi.gama.gui.swt.controls;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * The class AbstractOverlay
 * 
 * 26 Aug: took the decision to hide the overlays when the view is detached, as to avoid nasty problems of z-position,
 * non-existing Move and Resize events, etc. on detached views. This is a workaround for Issue 548.
 * 
 * @author drogoul
 * @since 19 aug. 2013
 * 
 */
public abstract class AbstractOverlay {

	private final Shell popup;
	private final Control control;
	private boolean isHidden = true;
	private final LayeredDisplayView view;

	// ACTIONS ON THE POPUP

	Runnable doHide = new Runnable() {

		@Override
		public void run() {
			hide();
		}
	};

	Runnable doDisplay = new Runnable() {

		@Override
		public void run() {
			display();
		}
	};

	Runnable doResize = new Runnable() {

		@Override
		public void run() {
			relocate();
			resize();
		}
	};

	protected void run(Runnable r) {
		GuiUtils.run(r);
	}

	// PART LISTENER

	private final IPartListener2 pl2 = new IPartListener2() {

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {

			IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) ) {
				// GuiUtils.debug("Part " + partRef.getTitle() + " activated -> should display overlay");
				run(doDisplay);
			}
			// else run(doHide)
			;
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPartReference partRef) {
			// GuiUtils.debug("Part " + partRef.getTitle() + " brought to top -> nothing");
		}

		@Override
		public void partClosed(final IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) ) {
				// GuiUtils.debug("Part " + partRef.getTitle() + " closed -> should close overlay");
				close();
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) && !view.getComponent().isVisible() ) {
				// GuiUtils.debug(view.getPartName() +
				// " disactivated && component is not visible -> should hide overlay");
				run(doHide);
			}
		}

		@Override
		public void partOpened(final IWorkbenchPartReference partRef) {}

		@Override
		public void partHidden(final IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) ) {
				// GuiUtils.debug("Part " + partRef.getTitle() + " hidden -> should hide overlay");
				run(doHide);
			}
		}

		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			if ( view.equals(part) ) {
				// GuiUtils.debug("Part " + partRef.getTitle() + " visible -> should display overlay");
				run(doDisplay);
			}
			// else {
			// run(doHide);
			// }
		}

		@Override
		public void partInputChanged(final IWorkbenchPartReference partRef) {}
	};

	public AbstractOverlay(final LayeredDisplayView view) {
		this.view = view;
		IPartService ps = (IPartService) ((IWorkbenchPart) view).getSite().getService(IPartService.class);
		ps.addPartListener(pl2);
		final Composite c = view.getComponent();
		popup = new Shell(view.getSite().getShell(), SWT.TOOL | SWT.NO_TRIM | SWT.NO_FOCUS);
		popup.setLayout(new FillLayout());
		popup.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		control = createControl();
		control.setLayoutData(null);
		popup.setAlpha(140);
		popup.layout();
		// c.addListener(SWT.Move, new Listener() {
		//
		// @Override
		// public void handleEvent(final Event event) {
		// GuiUtils.debug(view.getPartName() + " == surface moved -> should move overlay");
		// run(doResize);
		// }
		// });
		c.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				GuiUtils.debug(view.getPartName() + " == surface resized -> should resize overlay");
				run(doResize);
			}
		});
		c.addListener(SWT.Close, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				GuiUtils.debug(view.getPartName() + " == surface closed -> should hide overlay");
				run(doHide);
			}
		});
		// c.addListener(SWT.Deactivate, new Listener() {
		//
		// @Override
		// public void handleEvent(final Event event) {
		// GuiUtils.debug(view.getPartName() + " == surface closed -> should hide overlay");
		// run(doHide);
		// }
		// });
		// c.addListener(SWT.Hide, new Listener() {
		//
		// @Override
		// public void handleEvent(final Event event) {
		// GuiUtils.debug(view.getPartName() + " == surface hidden -> should hide overlay");
		// run(doHide);
		// }
		// });

	}

	protected abstract Control createControl();

	protected abstract void populateControl();

	protected abstract Point getLocation();

	protected abstract Point getSize();

	protected Control getControl() {
		return control;
	}

	public Shell getPopup() {
		return popup;
	}

	protected LayeredDisplayView getView() {
		return view;
	}

	public void update() {
		if ( isHidden() ) { return; }
		if ( popup.isDisposed() ) { return; }
		populateControl();
	}

	public void display() {
		if ( isHidden() ) { return; }
		// We first verify that the popup is still ok
		if ( popup.isDisposed() ) { return; }
		update();
		relocate();
		resize();
		if ( !popup.isVisible() )
			popup.setVisible(true);
	}

	public void relocate() {
		if ( isHidden() )
			return;
		if ( !popup.isDisposed() ) {
			popup.setLocation(getLocation());
		}
	}

	public void resize() {
		if ( isHidden() ) { return; }
		if ( !popup.isDisposed() ) {
			final Point size = getSize();
			popup.setSize(popup.computeSize(size.x, size.y));
		}
	}

	public void hide() {
		if ( !popup.isDisposed() && popup.isVisible() ) {
			// GuiUtils.debug("set visible(false) sent to popup of " + getClass().getSimpleName());
			popup.setSize(0, 0);
			popup.update();
			popup.setVisible(false);
			// GuiUtils.debug("Is the popup visible ? " + popup.isVisible());
		}
	}

	public void close() {
		if ( !popup.isDisposed() ) {
			IPartService ps = (IPartService) ((IWorkbenchPart) view).getSite().getService(IPartService.class);
			ps.removePartListener(pl2);
			// Remove other listeners too ?
			popup.dispose();

		}
	}

	protected boolean isHidden() {
		// AD: Temporary fix for Issue 548. When a view is detached, the overlays are not displayed
		return isHidden || viewIsDetached();
	}

	private boolean viewIsDetached() {
		// Uses the trick from http://eclipsesource.com/blogs/2010/06/23/tip-how-to-detect-that-a-view-was-detached/
		IWorkbenchPartSite site = view.getSite();
		if ( site == null ) { return false; }
		Shell shell = site.getShell();
		if ( shell == null ) { return false; }
		String text = shell.getText();
		return text == null || text.isEmpty();
	}

	public final void toggle() {
		setHidden(!isHidden);
	}

	protected final void setHidden(final boolean hidden) {
		isHidden = hidden;
		if ( isHidden ) {
			hide();
		} else if ( !viewIsDetached() ) {
			// No need to compute these if the view is detached
			// relocate();
			// resize();
			display();
		}
	}

}
