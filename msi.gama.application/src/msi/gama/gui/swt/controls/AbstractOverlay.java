package msi.gama.gui.swt.controls;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
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

	public static Color BLACK = SwtGui.getDisplay().getSystemColor(SWT.COLOR_BLACK);
	public static Color WHITE = SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE);

	private final Shell popup;
	private boolean isHidden = true;
	private final LayeredDisplayView view;
	private final Shell parentShell;
	// protected final Shell slidingShell;
	final boolean createExtraInfo;

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

	protected void run(final Runnable r) {
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

	OverlayListener listener = new OverlayListener();
	protected final MouseListener toggleListener = new MouseAdapter() {

		@Override
		public void mouseUp(final MouseEvent e) {
			setHidden(true);
		}

	};

	class OverlayListener implements ShellListener, ControlListener {

		/**
		 * Method controlMoved()
		 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
		 */
		@Override
		public void controlMoved(final ControlEvent e) {
			relocate();
			resize();
		}

		/**
		 * Method controlResized()
		 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
		 */
		@Override
		public void controlResized(final ControlEvent e) {
			relocate();
			resize();
		}

		/**
		 * Method shellActivated()
		 * @see org.eclipse.swt.events.ShellListener#shellActivated(org.eclipse.swt.events.ShellEvent)
		 */
		@Override
		public void shellActivated(final ShellEvent e) {}

		/**
		 * Method shellClosed()
		 * @see org.eclipse.swt.events.ShellListener#shellClosed(org.eclipse.swt.events.ShellEvent)
		 */
		@Override
		public void shellClosed(final ShellEvent e) {
			close();
		}

		/**
		 * Method shellDeactivated()
		 * @see org.eclipse.swt.events.ShellListener#shellDeactivated(org.eclipse.swt.events.ShellEvent)
		 */
		@Override
		public void shellDeactivated(final ShellEvent e) {}

		/**
		 * Method shellDeiconified()
		 * @see org.eclipse.swt.events.ShellListener#shellDeiconified(org.eclipse.swt.events.ShellEvent)
		 */
		@Override
		public void shellDeiconified(final ShellEvent e) {}

		/**
		 * Method shellIconified()
		 * @see org.eclipse.swt.events.ShellListener#shellIconified(org.eclipse.swt.events.ShellEvent)
		 */
		@Override
		public void shellIconified(final ShellEvent e) {}
	}

	public AbstractOverlay(final LayeredDisplayView view, final boolean createExtraInfo) {
		this.createExtraInfo = createExtraInfo;
		this.view = view;
		IPartService ps = (IPartService) ((IWorkbenchPart) view).getSite().getService(IPartService.class);
		ps.addPartListener(pl2);
		final Composite c = view.getComponent();
		parentShell = c.getShell();
		popup = new Shell(parentShell, SWT.NO_TRIM | SWT.NO_FOCUS);
		// slidingShell = new Shell(parentShell, SWT.NO_TRIM);
		// slidingShell.setBackground(BLACK);
		// slidingShell.setAlpha(40);
		// slidingShell.addMouseTrackListener(new MouseTrackAdapter() {
		//
		// @Override
		// public void mouseHover(final MouseEvent e) {
		// slidingShell.setVisible(false);
		// setHidden(false);
		// }
		//
		// @Override
		// public void mouseExit(final MouseEvent e) {
		// slidingShell.setVisible(false);
		// }
		//
		// });

		// slidingShell.addMouseListener(new MouseAdapter() {
		//
		// @Override
		// public void mouseUp(final MouseEvent e) {
		// slidingShell.setVisible(false);
		// setHidden(false);
		// }
		//
		// @Override
		// public void mouseDown(final MouseEvent e) {
		// // slidingShell.setVisible(false);
		// // toggle();
		// }
		//
		// @Override
		// public void mouseDoubleClick(final MouseEvent e) {
		// slidingShell.setVisible(false);
		// }
		//
		// });
		// slidingShell.addFocusListener(new FocusAdapter() {
		//
		// @Override
		// public void focusLost(final FocusEvent e) {
		// slidingShell.setVisible(false);
		// }
		//
		// });
		popup.setAlpha(140);
		FillLayout layout = new FillLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 10;
		popup.setLayout(layout);
		popup.setBackground(BLACK);
		createPopupControl();
		// Control control = createControl();
		// control.setLayoutData(null);
		popup.setAlpha(140);
		popup.layout();
		parentShell.addShellListener(listener);
		parentShell.addControlListener(listener);
		c.addControlListener(listener);
		// popup.addMouseListener(toggleListener);
	}

	// public void appear() {
	// slidingShell.setVisible(true);
	// slidingShell.setActive();
	// }

	protected void createPopupControl() {};

	protected abstract Point getLocation();

	protected abstract Point getSize();

	public Shell getPopup() {
		return popup;
	}

	protected LayeredDisplayView getView() {
		return view;
	}

	public void update() {}

	public void display() {
		if ( isHidden() ) { return; }
		// We first verify that the popup is still ok
		if ( popup.isDisposed() ) { return; }
		update();
		relocate();
		resize();
		if ( !popup.isVisible() ) {
			popup.setVisible(true);
		}
	}

	public void relocate() {
		if ( isHidden() ) { return; }
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
			// slide(false);
			popup.setSize(0, 0);
			popup.update();
			popup.setVisible(false);
			// GuiUtils.debug("Is the popup visible ? " + popup.isVisible());
		}
	}

	public boolean isDisposed() {
		return popup.isDisposed() || viewIsDetached();
	}

	public void close() {
		if ( !popup.isDisposed() ) {
			Composite c = view.getComponent();
			if ( c != null && !c.isDisposed() ) {
				c.removeControlListener(listener);
			}
			IPartService ps = (IPartService) ((IWorkbenchPart) view).getSite().getService(IPartService.class);
			if ( ps != null ) {
				ps.removePartListener(pl2);
			}
			if ( !parentShell.isDisposed() ) {
				parentShell.removeControlListener(listener);
				parentShell.removeShellListener(listener);
			}
			popup.dispose();
		}
	}

	protected boolean isHidden() {
		// AD: Temporary fix for Issue 548. When a view is detached, the overlays are not displayed
		return isDisposed() || isHidden;
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

	public final void setHidden(final boolean hidden) {
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

	/**
	 * allows the window to be animated.
	 * 
	 * @param reverse
	 *            if false, it goes right->left. if true, it goes left->right
	 */
	public void slide(final boolean reverse) {
		slide(reverse, 0);
	}

	/**
	 * allows the window to be animated.
	 * 
	 * @param reverse
	 *            if false, it goes right->left. if true, it goes left->right
	 * @param speed
	 *            how much time to wait between "frames" in the sliding animation.
	 */
	public void slide(final boolean reverse, final int speed) {
		final int rate = 4;
		final int direction = reverse ? rate : -rate;

		run(new Runnable() {

			@Override
			public void run() {
				for ( int i = 0; i <= popup.getBounds().width / rate; i++ ) {
					popup.setBounds(popup.getLocation().x, popup.getLocation().y, popup.getBounds().width + direction,
						popup.getBounds().height);
					if ( speed > 0 ) {
						try {
							Thread.sleep(speed);
						} catch (InterruptedException e) {}
					}
					update();
				}
			}
		});
	}

}
