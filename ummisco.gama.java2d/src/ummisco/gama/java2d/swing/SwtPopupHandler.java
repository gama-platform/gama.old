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
package ummisco.gama.java2d.swing;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.util.WeakHashMap;

import javax.swing.JComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import ummisco.gama.ui.utils.PlatformHelper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SwtPopupHandler {

	// Gtk will not display popup menus that are parented to the
	// main shell. Instead we need to create and open a second
	// shell to own the popup. Note that this means the SWT menu
	// needs to be created by the client with the parent returned by
	// AwtEnvironment#getSwtPopupParent.
	private static final boolean CREATE_POPUP_PARENT_SHELL = PlatformHelper.isGtk();

	// Gtk will not display popup menus that are made visible by an
	// AWT mouse click (mouse pressed event), unless we wait until
	// 1) the mouse has been released and
	// 2) the mouse event for entering the parent shell has been processed
	// This flag enables the delay
	// TODO: this flag effectively changes the popup trigger from mouse down to
	// mouse up (can it be avoided?)
	private static final boolean DELAY_MOUSE_DOWN_SWT_POPUP_TRIGGERS = PlatformHelper.isGtk();

	// TODO: in some relatively rare cases, on GTK, SWT popups are not dismissed
	// It can happen if you try to dismiss it with a left click while
	// rapidly moving the mouse. A second click will dismiss it.

	// TODO: SWT popups cannot be attached to JLabels.
	// Do we need to use ILOG's findComponentAt() method?

	// TODO: On windows, the AWT/Swing cursor is retained after showing the SWT
	// popup
	// Setting CREATE_POPUP_PARENT_SHELL to true might be a solution, but we
	// also
	// need general cursor support which might also solve this problem.

	private boolean pendingSwtPopup = false;

	// Listen to AWT mouse events and show SWT popups as necessary
	private final java.awt.event.AWTEventListener popupEventListener = event -> {
		if (event instanceof MouseEvent) {
			final MouseEvent me = (MouseEvent) event;
			switch (me.getID()) {
			case java.awt.event.MouseEvent.MOUSE_PRESSED:
				boolean isTrigger = me.isPopupTrigger();

				if (DELAY_MOUSE_DOWN_SWT_POPUP_TRIGGERS && CREATE_POPUP_PARENT_SHELL && isTrigger) {
					// We must delay the Swt popup here. Otherwise the parent
					// shell will not be
					// properly opened if the user clicks, then drags, then
					// releases the mouse.

					// System.err.println("delaying any SWT popup display");
					pendingSwtPopup = true;
					isTrigger = false;
				}
				if (isTrigger) {
					handlePopupTrigger(me);
				}
				break;

			case java.awt.event.MouseEvent.MOUSE_RELEASED:
				// TODO: can both conditions below ever be true on the same
				// event?
				if (pendingSwtPopup) {
					// Now handle any previously delayed popups
					// if (pendingSwtPopup) {
					// System.err.println("handling any delayed SWT popup
					// display");
					// }
					handlePopupTrigger(me);
					pendingSwtPopup = false;
				}
				if (me.isPopupTrigger()) {
					handlePopupTrigger(me);
				}
				break;

			case java.awt.event.MouseEvent.MOUSE_CLICKED:
				// TODO: is MOUSE_CLICKED a valid trigger point?
				if (me.isPopupTrigger()) {
					handlePopupTrigger(me);
				}
				break;
			}
		}
	};

	// The set of toolkits to which the popupEventListener has already been
	// added.
	private final WeakHashMap /* java.awt.Toolkit -> Boolean */ popupSupportedToolkits = new WeakHashMap();

	public void monitorAwtComponent(final Component component) {
		assert EventQueue.isDispatchThread();

		// Ensure the toolkit has the popupEventListener attached.
		final java.awt.Toolkit toolkit = component.getToolkit();
		synchronized (popupSupportedToolkits) {
			if (popupSupportedToolkits.get(toolkit) == null) {
				toolkit.addAWTEventListener(popupEventListener, java.awt.AWTEvent.MOUSE_EVENT_MASK);
				popupSupportedToolkits.put(toolkit, Boolean.TRUE);
			}
		}
	}

	protected SwingControl getSwtParent(final Component c) {
		// Return the SwingControl, if any, associated with the component
		if (c instanceof JComponent) {
			final JComponent jc = (JComponent) c;
			return (SwingControl) jc.getClientProperty(SwingControl.SWT_PARENT_PROPERTY_KEY);
		} else {
			return null;
		}
	}

	protected void handlePopupTrigger(final MouseEvent event) {
		assert EventQueue.isDispatchThread();

		final java.awt.Component component = (java.awt.Component) event.getSource();
		int x = event.getX();
		int y = event.getY();
		// Climb up until the we find the SwingControl mapped to one of our
		// parents
		java.awt.Component parent = component;
		while (parent != null && getSwtParent(parent) == null) {
			x += parent.getX();
			y += parent.getY();
			parent = parent.getParent();
		}
		if (parent != null) {
			final SwingControl swtParent = getSwtParent(parent);
			final int xAbsolute = x;
			final int yAbsolute = y;
			final java.awt.Component subcomp = (Component) event.getSource();
			showPopupMenu(swtParent, subcomp, x, y, xAbsolute, yAbsolute);
		}
	}

	// Trigger the display of the popup menu.
	protected void showPopupMenu(final SwingControl swtParent, final java.awt.Component component, final int x,
			final int y, final int xAbsolute, final int yAbsolute) {
		assert EventQueue.isDispatchThread();

		Display display;
		try {
			display = swtParent.getDisplay();
		} catch (final SWTException e) {
			if (e.code == SWT.ERROR_WIDGET_DISPOSED) {
				return;
			} else {
				throw e;
			}
		}
		final Runnable task = () -> {
			try {
				if (!swtParent.isDisposed()) {
					showPopupMenuSWTThread(swtParent, component, x, y, xAbsolute, yAbsolute);
				}
			} catch (final Throwable e) {
				e.printStackTrace();
			}
		};
		try {
			ThreadingHandler.getInstance().asyncExec(display, task);
		} catch (final SWTException e) {
			if (e.code == SWT.ERROR_DEVICE_DISPOSED) {
				return;
			} else {
				throw e;
			}
		}
	}

	// Trigger the display of the popup menu from the SWT thread.
	protected void showPopupMenuSWTThread(final SwingControl swtParent, final java.awt.Component component, final int x,
			final int y, final int xAbsolute, final int yAbsolute) {
		assert Display.getCurrent() != null;

		final Menu menu = swtParent.getMenu(component, x, y, xAbsolute, yAbsolute);
		final Display display = swtParent.getDisplay();
		if (menu != null) {
			final Shell popupParent = AwtEnvironment.getInstance(display).getSwtPopupParent(swtParent);

			if (DELAY_MOUSE_DOWN_SWT_POPUP_TRIGGERS) {
				// Install a listener that waits until the popup parent receives
				// a MouseEnter event before displaying the menu. If we don't
				// wait
				// for this event, the menu is not displayed.
				popupParent.addListener(SWT.MouseEnter, new Listener() {

					@Override
					public void handleEvent(final Event e) {
						if (CREATE_POPUP_PARENT_SHELL) {
							// Install a listener to hide the (created) popup
							// parent once the menu is
							// dismissed. (Don't count on 0x0 sized shells to be
							// invisible without this)
							menu.addListener(SWT.Hide, new Listener() {

								@Override
								public void handleEvent(final Event e) {
									if (!popupParent.isDisposed()) {
										// System.err.println("hiding popup
										// parent");
										popupParent.setVisible(false);
									}
									// Clean up
									menu.removeListener(SWT.Hide, this);
								}
							});
						}

						if (!menu.isDisposed()) {
							menu.setVisible(true);
						}
						// Clean up
						popupParent.removeListener(SWT.MouseEnter, this);
					}
				});
			}

			if (CREATE_POPUP_PARENT_SHELL) {
				// Display the created parent shell at the current cursor
				// location
				// System.err.println("*** opening popup parent" +
				// display.getCursorLocation());
				popupParent.setLocation(display.getCursorLocation());
				popupParent.open();
				// The menu is made visible in the listener above
			}
			if (!DELAY_MOUSE_DOWN_SWT_POPUP_TRIGGERS) {
				// If we are not waiting for MouseEnter, then open the menu
				// right here
				menu.setVisible(true);
			}
		}
	}

	// ========================================================================
	// Singleton design pattern

	private static SwtPopupHandler theHandler = new SwtPopupHandler();

	/**
	 * Returns the currently active singleton of this class.
	 */
	public static SwtPopupHandler getInstance() {
		return theHandler;
	}

	/**
	 * Replaces the singleton of this class.
	 * 
	 * @param instance
	 *            An instance of this class or of a customized subclass.
	 */
	public static void setInstance(final SwtPopupHandler instance) {
		theHandler = instance;
	}

}
