/*********************************************************************************************
 *
 * 'AwtDialogListener.java, in plugin ummisco.gama.java2d, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import ummisco.gama.ui.utils.PlatformHelper;

/**
 * A listener that insures the proper modal behavior of Swing dialogs when
 * running within a SWT environment. When initialized, it blocks and unblocks
 * SWT input as modal Swing dialogs are shown and hidden.
 *
 * @see SwtInputBlocker
 */
public class AwtDialogListener implements AWTEventListener, ComponentListener, WindowFocusListener {

	private static boolean verboseModalityHandling = false;

	protected static boolean USING_ALWAYS_ON_TOP = PlatformHelper.isGtk()
			&& PlatformHelper.JAVA_VERSION >= PlatformHelper.javaVersion(1, 5, 0);
	private static boolean alwaysOnTopMethodsInitialized = false;
	private static Method setAlwaysOnTopMethod = null;
	private static Method isAlwaysOnTopMethod = null;

	// modalDialogs should be accessed only from the AWT thread, so no
	// synchronization is needed.
	private final List<Dialog> modalDialogs = new ArrayList<>();
	private final Display display;

	/**
	 * Registers this object as an AWT event listener so that Swing dialogs have
	 * the proper modal behavior in the containing SWT environment. This is
	 * called automatically when you construct a {@link SwingControl}, and it
	 * need not be called separately in that case.
	 * 
	 * @param shell
	 */
	public AwtDialogListener(final Display display) {
		assert display != null;

		// In some cases, we use Window.setAlwaysOnTop to keep modal AWT dialogs
		// visible
		if (USING_ALWAYS_ON_TOP) {
			getAlwaysOnTopMethods();
		}

		this.display = display;
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.WINDOW_EVENT_MASK);
	}

	protected void getAlwaysOnTopMethods() {
		// These methods is only needed for Gtk, Reflection is used to allow
		// compilation
		// against JDK 1.4
		if (!alwaysOnTopMethodsInitialized) {
			alwaysOnTopMethodsInitialized = true;
			try {
				setAlwaysOnTopMethod = Window.class.getMethod("setAlwaysOnTop", new Class[] { boolean.class });
				isAlwaysOnTopMethod = Window.class.getMethod("isAlwaysOnTop", new Class[] {});
			} catch (final NoSuchMethodException e) {
				handleAlwaysOnTopException(e);
			}
		}
	}

	protected void setAlwaysOnTop(final Window window, final boolean onTop) {
		assert setAlwaysOnTopMethod != null;
		try {
			if (verboseModalityHandling) {
				System.err.println("Calling setAlwaysOnTop(" + onTop + ") for " + window);
			}
			setAlwaysOnTopMethod.invoke(window, new Object[] { new Boolean(onTop) });
		} catch (final IllegalAccessException e) {
			handleAlwaysOnTopException(e);
		} catch (final InvocationTargetException e) {
			handleAlwaysOnTopException(e);
		}
	}

	protected boolean isAlwaysOnTop(final Window window) {
		assert isAlwaysOnTopMethod != null;
		try {
			if (verboseModalityHandling) {
				System.err.println("Calling isAlwaysOnTop() for " + window);
			}
			final Object result = isAlwaysOnTopMethod.invoke(window, new Object[] {});
			return ((Boolean) result).booleanValue();
		} catch (final IllegalAccessException e) {
			handleAlwaysOnTopException(e);
			return false;
		} catch (final InvocationTargetException e) {
			handleAlwaysOnTopException(e);
			return false;
		}
	}

	protected void handleAlwaysOnTopException(final Exception e) {
		if (verboseModalityHandling) {
			e.printStackTrace();
		}
	}

	private void handleRemovedDialog(final Dialog awtDialog, final boolean removeListener) {
		assert awtDialog != null;
		assert modalDialogs != null;
		assert display != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		if (verboseModalityHandling) {
			System.err.println("Remove dialog: " + awtDialog);
		}
		if (removeListener) {
			awtDialog.removeComponentListener(this);
			if (USING_ALWAYS_ON_TOP) {
				awtDialog.removeWindowFocusListener(this);
			}
		}
		// Note: there is no isModal() check here because the dialog might
		// have been changed from modal to non-modal after it was opened. In
		// this case
		// the currently visible dialog would still act modal and we'd need to
		// unblock
		// SWT here when it goes away.
		if (modalDialogs.remove(awtDialog)) {
			ThreadingHandler.getInstance().asyncExec(display, () -> SwtInputBlocker.unblock());
		}
	}

	private void handleAddedDialog(final Dialog awtDialog) {
		assert awtDialog != null;
		assert modalDialogs != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		if (verboseModalityHandling) {
			System.err.println("Add dialog: " + awtDialog);
		}

		// Don't block if
		// 1) the the dialog has already triggered a block
		// 2) the dialog is not modal, or
		// 3) the dialog is not (yet?) visible
		// It's not clear why/when case 3 would happen, but it has been reported
		// and the consequences
		// are severe if the check is not in place.
		if (modalDialogs.contains(awtDialog) || !awtDialog.isModal() || !awtDialog.isVisible()) {
			return;
		}
		modalDialogs.add(awtDialog);
		awtDialog.addComponentListener(this);

		// In some cases (e.g. GTK), we need to use the Window.setAlwaysOnTop
		// method to force modal AWT dialogs in front of any SWT shells.
		// Otherwise, they
		// are easily hidden when clicking on the parent shell. It might be
		// possible to
		// remove this code if we could successfully move the SWT shell back in
		// the z-order,
		// but there is an open bug (on GTK) on Shell.moveBelow.
		// See note in SwtInputBlocker.activateListener
		// We use a listener to keep the always-on-top behavior enabled only
		// while the
		// dialog has focus. If the dialog is already always-on-top, we don't
		// add a listener.
		// TODO: we don't handle the case where always-on-top status is changed
		// while the dialog
		// is visible.
		if (USING_ALWAYS_ON_TOP && !isAlwaysOnTop(awtDialog)) {
			awtDialog.addWindowFocusListener(this);
		}

		ThreadingHandler.getInstance().asyncExec(display, () -> SwtInputBlocker.block(AwtDialogListener.this));
	}

	void requestFocus() {
		// TODO: in early testing this did not always bring the dialog to the
		// top
		// under some Linux desktops/window managers (e.g. metacity under
		// GNOME).
		// Re-test with recent changes
		EventQueue.invokeLater(() -> {
			assert modalDialogs != null;

			final int size = modalDialogs.size();
			if (size > 0) {
				final Dialog awtDialog = modalDialogs.get(size - 1);
				Component focusOwner = awtDialog.getMostRecentFocusOwner();
				if (verboseModalityHandling) {
					System.err.println("Bringing to front, focusOwner=" + focusOwner);
				}

				if (focusOwner == null) {
					focusOwner = awtDialog; // try the dialog itself in this
											// case
				}
				try {
					// In one case, a call to requestFocus() alone does not
					// bring the AWT dialog to the top. This happens if the
					// dialog is given a null parent frame. When opened, the
					// dialog
					// can be hidden by the SWT window even when it obtains
					// focus.
					// Calling toFront() solves the problem.
					focusOwner.requestFocus();
					awtDialog.toFront();
				} catch (final NullPointerException e) {
					// Some dialogs (e.g. Windows page setup and print dialogs
					// on JDK 1.5+) throw an NPE on
					// requestFocus(). There's no way to check ahead of time, so
					// just swallow the NPE here.
				}
			}
		});
	}

	private void handleOpenedWindow(final WindowEvent event) {
		assert event != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		final Window window = event.getWindow();
		if (window instanceof Dialog) {
			handleAddedDialog((Dialog) window);
		}
	}

	private void handleClosedWindow(final WindowEvent event) {
		assert event != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// Dispose-based close
		final Window window = event.getWindow();
		if (window instanceof Dialog) {
			// Remove dialog and component listener
			handleRemovedDialog((Dialog) window, true);
		}
	}

	private void handleClosingWindow(final WindowEvent event) {
		assert event != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// System-based close
		final Window window = event.getWindow();
		if (window instanceof Dialog) {
			final Dialog dialog = (Dialog) window;
			// Defer until later. Bad things happen if
			// handleRemovedDialog() is called directly from
			// this event handler. The Swing dialog does not close
			// properly and its modality remains in effect.
			EventQueue.invokeLater(() -> handleRemovedDialog(dialog, true));
		}
	}

	public void dispose() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	}

	// ================== Implementation of AWTEventListener ==================
	// This listener is permanently attached to the AWT Toolkit.

	@Override
	public void eventDispatched(final AWTEvent event) {
		assert event != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		switch (event.getID()) {
		case WindowEvent.WINDOW_OPENED:
			handleOpenedWindow((WindowEvent) event);
			break;

		case WindowEvent.WINDOW_CLOSED:
			handleClosedWindow((WindowEvent) event);
			break;

		case WindowEvent.WINDOW_CLOSING:
			handleClosingWindow((WindowEvent) event);
			break;

		default:
			break;
		}
	}

	// ================= Implementation of ComponentListener =================
	// This listener is attached to modal Dialog instances while they are
	// shown.

	@Override
	public void componentHidden(final ComponentEvent e) {
		assert e != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		if (verboseModalityHandling) {
			System.err.println("Component hidden");
		}
		final Object obj = e.getSource();
		if (obj instanceof Dialog) {
			// Remove dialog but keep listener in place so that we know if/when
			// it is set visible
			handleRemovedDialog((Dialog) obj, false);
		}
	}

	@Override
	public void componentShown(final ComponentEvent e) {
		assert e != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		if (verboseModalityHandling) {
			System.err.println("Component shown");
		}
		final Object obj = e.getSource();
		if (obj instanceof Dialog) {
			handleAddedDialog((Dialog) obj);
		}
	}

	@Override
	public void componentResized(final ComponentEvent e) {
		// Ignore event
	}

	@Override
	public void componentMoved(final ComponentEvent e) {
		// Ignore event
	}

	// ================ Implementation of WindowFocusListener ================
	// This listener is attached to modal Dialog instances that are not already
	// set to AlwaysOnTop, while they are shown, if USING_ALWAYS_ON_TOP is true.

	@Override
	public void windowGainedFocus(final WindowEvent e) {
		assert USING_ALWAYS_ON_TOP;
		setAlwaysOnTop(e.getWindow(), true);
	}

	@Override
	public void windowLostFocus(final WindowEvent e) {
		assert USING_ALWAYS_ON_TOP;
		setAlwaysOnTop(e.getWindow(), false);
	}

}
