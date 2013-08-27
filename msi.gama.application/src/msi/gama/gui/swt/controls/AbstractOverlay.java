package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

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
	private final Listener hide = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			hide();
		}
	};
	private final Listener resize = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			relocate();
			resize();
		}
	};
	private final MouseMoveListener move = new MouseMoveListener() {

		@Override
		public void mouseMove(final MouseEvent e) {
			display();
		}
	};

	public AbstractOverlay(final LayeredDisplayView view) {
		this.view = view;

		IPartService ps = (IPartService) ((IWorkbenchPart) view).getSite().getService(IPartService.class);
		ps.addPartListener(new IPartListener2() {

			@Override
			public void partActivated(final IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if ( view.equals(part) ) {
					display();
				}
			}

			@Override
			public void partBroughtToTop(final IWorkbenchPartReference partRef) {}

			@Override
			public void partClosed(final IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if ( view.equals(part) ) {
					close();
				}
			}

			@Override
			public void partDeactivated(final IWorkbenchPartReference partRef) {
				// IWorkbenchPart part = partRef.getPart(false);
				// if ( view.equals(part) ) {
				// hide();
				// }
			}

			@Override
			public void partOpened(final IWorkbenchPartReference partRef) {}

			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if ( view.equals(part) ) {
					hide();
				}
			}

			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if ( view.equals(part) ) {
					display();
				}
			}

			@Override
			public void partInputChanged(final IWorkbenchPartReference partRef) {}
		});

		final Composite c = view.getComponent();
		popup = new Shell(c.getShell(), SWT.NO_TRIM | SWT.NO_FOCUS);
		popup.setLayout(new FillLayout());
		popup.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		control = createControl();
		control.setLayoutData(null);
		popup.setAlpha(140);
		popup.layout();
		c.addMouseMoveListener(move);
		c.addListener(SWT.Move, resize);
		c.addListener(SWT.Resize, resize);
		c.addListener(SWT.Close, hide);
		// c.addListener(SWT.Deactivate, hide);
		c.addListener(SWT.Hide, hide);

	}

	protected abstract Control createControl();

	protected abstract void populateControl();

	protected abstract Point getLocation();

	protected abstract Point getSize();

	protected Control getControl() {
		return control;
	}

	// private void reparentWithShell(final Shell shell) {
	// if ( popup.isReparentable() ) {
	// Shell oldShell = (Shell) popup.getParent();
	// oldShell.removeListener(SWT.Resize, resize);
	// oldShell.removeListener(SWT.Move, resize);
	// popup.setParent(shell);
	// shell.addListener(SWT.Resize, resize);
	// shell.addListener(SWT.Move, resize);
	// }
	//
	// }

	public Shell getPopup() {
		return popup;
	}

	protected LayeredDisplayView getView() {
		return view;
	}

	public void display() {
		if ( isHidden() ) { return; }
		// We first verify that the popup is still ok
		if ( popup.isDisposed() ) { return; }
		// We then verify that the shell has not changed (i.e. the view has not been reparented)
		// if ( !view.getSite().getShell().equals(popup.getParent()) ) {
		// reparentWithShell(view.getSite().getShell());
		// }
		// if ( viewIsDetached() ) {
		// relocate();
		// resize();
		// }
		populateControl();
		popup.setVisible(true);
		// popup.set
	}

	public void relocate() {
		// Relocation is done even if the view is set to be hidden as display() will not change the location
		// if (isHidden())
		if ( !popup.isDisposed() ) {
			popup.setLocation(getLocation());
		}
	}

	public void resize() {
		// Resizing is done even if the overlay is set to be hidden as display() will not change the size
		// if ( isHidden ) { return; }
		if ( !popup.isDisposed() ) {
			final Point size = getSize();
			popup.setSize(popup.computeSize(size.x, size.y));
		}
	}

	public void hide() {
		if ( !popup.isDisposed() ) {
			popup.setVisible(false);
		}
	}

	public void close() {
		if ( !popup.isDisposed() ) {
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
			relocate();
			resize();
			display();
		}
	}

}
