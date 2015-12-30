/*********************************************************************************************
 *
 *
 * 'AwtEnvironment.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAS Institute Inc. - initial API and implementation
 * ILOG S.A. - initial API and implementation
 *******************************************************************************/
package msi.gama.gui.swt.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.JPopupMenu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import gnu.trove.map.hash.THashMap;

/**
 * An environment to enable the proper display of AWT/Swing windows within a SWT or RCP
 * application. This class extends the base {@link org.eclipse.swt.awt.SWT_AWT Eclipse SWT/AWT integration} support by
 * <ul>
 * <li>Using the platform-specific system Look and Feel.
 * <li>more later...
 * </ul>
 * <p>
 * This class is most helpful to applications which create new AWT/Swing windows (e.g. dialogs) rather than those which embed AWT/Swing components in SWT windows. For support specific to embedding
 * AWT/Swing components see {@link SwingControl}.
 * <p>
 * There is at most one instance of this class per SWT {@link org.eclipse.swt.widgets.Display Display}. In most applications this means that there is exactly one instance for the entire application.
 * <p>
 * An instance of this class can be obtained with the static {@link #getInstance(Display)} method.
 */
public final class AwtEnvironment {

	// ======================= Instances of this class =======================

	// Map from Display to AwtEnvironment.
	// This does not need to be a WeakHashMap: Display instances don't go away
	// silently; they are disposed, and we install a Dispose listener.
	private static Map<Display, AwtEnvironment> /* Display -> AwtEnvironment */ environmentMap = new THashMap();

	/**
	 * Returns the single instance of AwtEnvironment for the given display. On
	 * the first call to this method, the necessary initialization to allow
	 * AWT/Swing code to run properly within an Eclipse application is done.
	 * This initialization includes setting the approprite look and feel and
	 * registering the necessary listeners to ensure proper behavior of modal
	 * dialogs.
	 * <p>
	 * The first call to this method must occur before any AWT/Swing APIs are called.
	 *
	 * @param display
	 * the non-null SWT display
	 * @return the AWT environment
	 * @exception IllegalArgumentException
	 * <ul>
	 * <li>ERROR_NULL_ARGUMENT - if the display is null</li>
	 * </ul>
	 */
	public static AwtEnvironment getInstance(final Display display) {
		// For now assume a single display. If necessary, this implementation
		// can be changed to create multiple environments for multiple display
		// applications.
		if ( display == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		synchronized (environmentMap) {
			AwtEnvironment instance = environmentMap.get(display);
			if ( instance == null ) {
				instance = new AwtEnvironment(display);
				environmentMap.put(display, instance);
				ThreadingHandler.getInstance().asyncExec(display, new Runnable() {

					@Override
					public void run() {
						installDisposeHandler(display);
					}
				});
			}
			return instance;
		}
	}

	static private void installDisposeHandler(final Display display) {
		if ( !display.isDisposed() ) {
			display.addListener(SWT.Dispose, new Listener() {

				@Override
				public void handleEvent(final Event event) {

					removeInstance(display);
				}
			});
		}
	}

	static private void removeInstance(final Display display) {
		synchronized (environmentMap) {
			AwtEnvironment instance = environmentMap.remove(display);
			if ( instance != null ) {
				instance.dispose();
			}
		}
	}

	// ============================= Constructor =============================

	private final Display display;
	// private final AwtDialogListener dialogListener;
	private final GlobalFocusHandler globalFocusHandler;

	// Private constructor - clients use getInstance() to obtain instances
	private AwtEnvironment(final Display display) {
		assert display != null;

		this.display = display;

		/*
		 * This property removes a large amount of flicker from embedded swing
		 * components in JDK 1.4 and 1.5. Ideally it would be set lazily,
		 * but since its value is read once and cached by AWT, it needs
		 * to be set before any AWT/Swing APIs are called.
		 * This setting is no longer needed in JDK 1.6.
		 */
		// TODO: this is effective only on Windows.
		System.setProperty("sun.awt.noerasebackground", "true"); //$NON-NLS-1$//$NON-NLS-2$
		// Disabling OpenGL pipeline in order to tackle Issue #869
		System.setProperty("sun.java2d.opengl", "False");

		/*
		 * It's important to wait for the L&F to be set so that any subsequent calls
		 * to SwingControl.createFrame() will be return a frame with the proper L&F (note
		 * that createFrame() happens on the SWT thread).
		 *
		 * The calls to syncExec and invokeAndWait are safe because
		 * the first call AwtEnvironment.getInstance should happen
		 * before any (potential deadlocking) activity occurs on the
		 * AWT thread.
		 */
		// final Font[] initialFont = new Font[1];
		// display.syncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		// initialFont[0] = display.getSystemFont();
		// }
		// });
		// final Font swtFont = initialFont[0];
		// final FontData[] swtFontData = swtFont.getFontData();
		// try {
		// EventQueue.invokeAndWait(new Runnable() {
		//
		// @Override
		// public void run() {
		// setLookAndFeel();
		// LookAndFeelHandler.getInstance().propagateSwtFont(swtFont, swtFontData);
		// if ( FocusHandler.verboseKFHEvents ) {
		// FocusDebugging.enableKeyboardFocusManagerLogging();
		// }
		// }
		// });
		// } catch (InterruptedException e) {
		// SWT.error(SWT.ERROR_FAILED_EXEC, e);
		// } catch (InvocationTargetException e) {
		// SWT.error(SWT.ERROR_FAILED_EXEC, e.getTargetException());
		// }

		// Listen for AWT modal dialogs to make them modal application-wide
		// dialogListener = new AwtDialogListener(display);

		// Dismiss AWT popups when SWT menus are shown
		initSwingPopupsDismissal();

		globalFocusHandler = new GlobalFocusHandler(display);

		// ADDITION

		Listener listenerHover = new Listener() {

			@Override
			public void handleEvent(final Event event) {
				System.out.println("HOVER:  " + event.toString());
			}
		};
		Listener listenerEnter = new Listener() {

			@Override
			public void handleEvent(final Event event) {
				System.out.println("ENTER:  " + event.toString());
			}
		};
		Listener listenerExit = new Listener() {

			@Override
			public void handleEvent(final Event event) {
				System.out.println("EXIT:  " + event.toString());
			}
		};
		display.addFilter(SWT.MouseHover, listenerHover);
		display.addFilter(SWT.MouseEnter, listenerEnter);
		display.addFilter(SWT.MouseExit, listenerExit);

		// ADDITION

	}

	void dispose() {
		// dialogListener.dispose();
		if ( popupParent != null ) {
			popupParent.setVisible(false);
			popupParent.dispose();
		}
		globalFocusHandler.dispose();
	}

	// ======================= Look&Feel initialization =======================
	// Mostly delegated to the LookAndFeelHandler.

	// private static boolean isLookAndFeelInitialized = false;

	// static private void setLookAndFeel() {
	// assert EventQueue.isDispatchThread(); // On AWT event thread
	//
	// if ( !isLookAndFeelInitialized ) {
	// isLookAndFeelInitialized = true;
	// try {
	// LookAndFeelHandler.getInstance().setLookAndFeel();
	// } catch (ClassNotFoundException e) {
	// SWT.error(SWT.ERROR_NOT_IMPLEMENTED, e);
	// } catch (InstantiationException e) {
	// SWT.error(SWT.ERROR_NOT_IMPLEMENTED, e);
	// } catch (IllegalAccessException e) {
	// SWT.error(SWT.ERROR_NOT_IMPLEMENTED, e);
	// } catch (UnsupportedLookAndFeelException e) {
	// SWT.error(SWT.ERROR_NOT_IMPLEMENTED, e);
	// }
	// }
	// }

	// ==================== Swing Popup Management ================================
	// (Note there are no known problems with AWT popups (java.awt.PopupMenu), so this code
	// ignores them)

	/*
	 * Dismiss AWT popups when SWT menus are shown (not needed in JDK1.6)
	 */

	private static final boolean HIDE_SWING_POPUPS_ON_SWT_MENU_OPEN =
		Platform.isGtk() && Platform.JAVA_VERSION < Platform.javaVersion(1, 6, 0) || // GTK: pre-Java1.6
			Platform.isWin32(); // Win32: all JDKs

	private void initSwingPopupsDismissal() {
		if ( HIDE_SWING_POPUPS_ON_SWT_MENU_OPEN ) {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					display.addFilter(SWT.Show, menuListener);
				}
			});
		}
	}

	// This listener helps ensure that Swing popup menus are properly dismissed when
	// a menu item off the SWT main menu bar (or tool bar) is shown.
	private final Listener menuListener = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					hidePopups();
				}
			});
		}
	};

	// Returns true if any popup has been hidden
	protected boolean hidePopups() {
		boolean result = false;
		List popups = new ArrayList();
		assert EventQueue.isDispatchThread(); // On AWT event thread

		Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
		if ( window == null ) { return false; }

		// Look for popups inside the frame's component hierarchy.
		// Lightweight popups will be found here.
		findContainedPopups(window, popups);

		// Also look for popups in the frame's window hierachy.
		// Heavyweight popups will be found here.
		findOwnedPopups(window, popups);

		// System.err.println("Hiding popups, count=" + popups.size());
		for ( Iterator iter = popups.iterator(); iter.hasNext(); ) {
			Component popup = (Component) iter.next();
			if ( popup.isVisible() ) {
				result = true;
				popup.setVisible(false);
			}
		}
		return result;
	}

	protected void findOwnedPopups(final Window window, final List popups) {
		assert window != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		Window[] ownedWindows = window.getOwnedWindows();
		for ( int i = 0; i < ownedWindows.length; i++ ) {
			findContainedPopups(ownedWindows[i], popups);
			findOwnedPopups(ownedWindows[i], popups);
		}
	}

	protected void findContainedPopups(final Container container, final List popups) {
		assert container != null;
		assert popups != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		Component[] components = container.getComponents();
		for ( int i = 0; i < components.length; i++ ) {
			Component c = components[i];
			// JPopupMenu is a container, so check for it first
			if ( c instanceof JPopupMenu ) {
				popups.add(c);
			} else if ( c instanceof Container ) {
				findContainedPopups((Container) c, popups);
			}
		}
	}

	// =========================== Other useful API ===========================

	// -------------------------- Modal AWT Dialogs --------------------------

	/**
	 * Invokes the given runnable in the AWT event thread while blocking user
	 * input on the SWT event thread. The SWT event thread will remain blocked
	 * until the runnable task completes, at which point this method will
	 * return.
	 * <p>
	 * This method is useful for displayng modal AWT/Swing dialogs from the SWT event thread. The modal AWT/Swing dialog will always block input across the whole application, but not until it appears.
	 * By calling this method, it is guaranteed that SWT input is blocked immediately, even before the AWT/Swing dialog appears.
	 * <p>
	 * To avoid unnecessary flicker, AWT/Swing dialogs should have their parent set to a frame returned by {@link #createDialogParentFrame()}.
	 * <p>
	 * This method must be called from the SWT event thread.
	 *
	 * @param runnable
	 * the code to schedule on the AWT event thread
	 * @exception IllegalArgumentException
	 * <ul>
	 * <li>ERROR_NULL_ARGUMENT - if the runnable is null</li>
	 * </ul>
	 * @exception SWTException
	 * <ul>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 * </ul>
	 */
	// public void invokeAndBlockSwt(final Runnable runnable) {
	// assert display != null;
	//
	// /*
	// * This code snippet is based on the following thread on
	// * news.eclipse.platform.swt:
	// * http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg24234.html
	// */
	// if ( runnable == null ) {
	// SWT.error(SWT.ERROR_NULL_ARGUMENT);
	// return;
	// }
	// if ( !display.equals(Display.getCurrent()) ) {
	// SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
	// }
	//
	// // Switch to the AWT thread...
	// EventQueue.invokeLater(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// // do swing work...
	// runnable.run();
	// } finally {
	// ThreadingHandler.getInstance().asyncExec(display, new Runnable() {
	//
	// @Override
	// public void run() {
	// // Unblock SWT
	// SwtInputBlocker.unblock();
	// }
	// });
	// }
	// }
	// });

	// Prevent user input on SWT components
	// SwtInputBlocker.block(dialogListener);
	// }

	/**
	 * Creates an AWT frame suitable as a parent for AWT/Swing dialogs.
	 * <p>
	 * This method must be called from the SWT event thread. There must be an active shell associated with the environment's display.
	 * <p>
	 * The created frame is a non-visible child of the active shell and will be disposed when that shell is disposed.
	 * <p>
	 * See {@link #createDialogParentFrame(Shell)} for more details.
	 *
	 * @return a {@link java.awt.Frame} to be used for parenting dialogs
	 * @exception SWTException
	 * <ul>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 * </ul>
	 * @exception IllegalStateException
	 * if the current display has no shells
	 */
	public Frame createDialogParentFrame() {
		if ( !display.equals(Display.getCurrent()) ) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}
		Shell parent = display.getActiveShell();
		if ( parent == null ) { throw new IllegalStateException("No Active Shell"); //$NON-NLS-1$
		}
		return createDialogParentFrame(parent);
	}

	/**
	 * Creates an AWT frame suitable as a parent for AWT/Swing dialogs.
	 * <p>
	 * This method must be called from the SWT event thread. There must be an active shell associated with the environment's display.
	 * <p>
	 * The created frame is a non-visible child of the given shell and will be disposed when that shell is disposed.
	 * <p>
	 * This method is useful for creating a frame to parent any AWT/Swing dialogs created for use inside a SWT application. A modal AWT/Swing dialogs will behave better if its parent is set to the
	 * returned frame rather than to null or to an independently created {@link java.awt.Frame}.
	 * <p>
	 * The frame is positioned such that its child AWT dialogs are centered over the given parent shell's position <i>when this method is called</i>. If the parent frame is later moved, the child will
	 * no longer be properly positioned. For best results, create a new frame with this method immediately before creating and displaying each child AWT/Swing dialog.
	 * <p>
	 * As with any AWT window, the returned frame must be explicitly disposed.
	 *
	 * @param parent - the SWT parent shell of the shell that will contain the returned frame
	 * @return a {@link java.awt.Frame} to be used for parenting dialogs
	 * @exception SWTException
	 * <ul>
	 * <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 * </ul>
	 * @exception IllegalStateException
	 * if the current display has no shells
	 */
	public Frame createDialogParentFrame(final Shell parent) {
		if ( parent == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
			return null;
		}
		if ( !display.equals(Display.getCurrent()) ) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}

		// SWT.ON_TOP worked great for AWT print/page setup dialogs, but not for
		// other dialogs, so it has been removed.
		final Shell shell = new Shell(parent, SWT.NO_TRIM | SWT.APPLICATION_MODAL);

		Composite composite = new Composite(shell, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(composite);

		// Position and size the shell and embedded composite. This ensures that
		// any child dialogs will be shown in the proper position, relative to the
		// parent shell.
		shell.setLocation(parent.getLocation());

		// On Gtk, if the embedded frame is never made visible, its child dialog
		// will not be positioned correctly on the screen. (The frame's
		// getLocationOnScreen() method will always return 0,0). To work around
		// this problem, temporarily make the shell (and frame) visible. To
		// avoid flicker, temporarily set the size to 0.
		// (Note: the shell location must be correctly set before this will work)
		if ( Platform.isGtk() ) {
			shell.setSize(0, 0);
			shell.setVisible(true);
			shell.setVisible(false);
		}

		shell.setSize(parent.getSize());
		shell.setLayout(new FillLayout());
		shell.layout();

		// Clean up the shell that was created above on dispose of the frame
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(final WindowEvent e) {
				if ( !display.isDisposed() ) {
					ThreadingHandler.getInstance().asyncExec(display, new Runnable() {

						@Override
						public void run() {
							shell.dispose();
						}
					});
				}
			}
		});

		return frame;
	}

	// ------------------------ Displaying SWT popups ------------------------

	// Lazily created holder for an SWT popup.
	private Shell popupParent;

	/**
	 * Returns a suitable parent shell for a SWT menu attached to a Swing control.
	 * Use the return value from this method to create any SWT menus that
	 * are used in calls to {@link SwtPopupRegistry#setMenu(Component, boolean, org.eclipse.swt.widgets.Menu)}.
	 * Otherwise, the popup menu may not display on some platforms.
	 *
	 * @param control the SwingControl that owns the AWT component which will have
	 * a menu attached.
	 * @return
	 */
	public Shell getSwtPopupParent(final SwingControl control) {
		if ( Platform.isGtk() ) {
			if ( true && popupParent == null ) {
				// System.err.println("*** Creating separate popup parent shell");
				popupParent = new Shell(display, SWT.NO_TRIM | SWT.NO_FOCUS | SWT.ON_TOP);
				popupParent.setSize(0, 0);
			}
			return popupParent;
		} else {
			return control.getShell();
		}
	}

	// ----------------------- Focus Handling ------------------------------------------

	protected GlobalFocusHandler getGlobalFocusHandler() {
		return globalFocusHandler;
	}

}
