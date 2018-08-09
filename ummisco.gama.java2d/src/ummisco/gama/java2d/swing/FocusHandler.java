/*********************************************************************************************
 *
 * 'FocusHandler.java, in plugin ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import ummisco.gama.ui.utils.PlatformHelper;

public class FocusHandler {

	// =========================== Static variables ===========================

	// Whether to print debugging information regarding focus events.
	public static final boolean verboseFocusEvents = true;
	public static final boolean verboseKFHEvents = false;
	public static final boolean verboseTraverseOut = false;

	// synthesizeWindowActivation method on the frame's class (Win32 only,
	// JRE >= 1.5 only).
	// See <https://bugs.eclipse.org/bugs/show_bug.cgi?id=216431>
	// and <http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4922092>.
	static boolean synthesizeMethodInitialized = false;
	static Method synthesizeMethod = null;

	// ========================================================================

	final Frame frame;
	final Composite borderless; // the Control corresponding to the
								// frame
	final SwingControl swingControl; // either borderless or its parent
	final Display display;
	final GlobalFocusHandler globalHandler;
	boolean pendingTraverseOut = false;
	int pendingTraverseOutSeqNum = 0;
	int currentTraverseOutSeqNum = 0;
	int extraTabCount = 0;
	boolean isFocusedSwt;
	boolean pendingDeactivate = false;

	// Listeners
	final KeyEventDispatcher keyEventDispatcher = new AwtKeyDispatcher();
	final WindowFocusListener awtWindowFocusListener = new AwtWindowFocusListener();
	final FocusListener swtFocusListener = new SwtFocusListener();
	final Listener swtEventFilter = new SwtEventFilter();

	public FocusHandler(final SwingControl swingControl, final GlobalFocusHandler globalHandler,
			final Composite borderless, final Frame frame) {
		this.globalHandler = globalHandler;
		assert Display.getCurrent() != null; // On SWT event thread

		if (verboseFocusEvents) {
			FocusDebugging.addFocusDebugListeners(swingControl, frame);
		}

		this.swingControl = swingControl;
		this.borderless = borderless;
		this.frame = frame;
		display = swingControl.getDisplay();

		getSynthesizeMethod(frame.getClass());

		globalHandler.addEventFilter(swtEventFilter);

		frame.addWindowFocusListener(awtWindowFocusListener);
		// KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);

		borderless.addFocusListener(swtFocusListener);

	}

	public void dispose() {
		globalHandler.removeEventFilter(swtEventFilter);
		frame.removeWindowFocusListener(awtWindowFocusListener);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
		borderless.removeFocusListener(swtFocusListener);
	}

	// ================
	// On a normal change of focus, Swing will turn off any selection
	// in a text field to help indicate focus is lost. This won't happen
	// automatically when transferring to SWT, so turn off the selection
	// manually.
	protected void hideTextSelection() {
		assert EventQueue.isDispatchThread();

		final Component focusOwner = frame.getMostRecentFocusOwner();
		if (focusOwner instanceof JTextComponent) {
			final Caret caret = ((JTextComponent) focusOwner).getCaret();
			if (caret != null) {
				caret.setSelectionVisible(false);
			}
		}
	}

	// =====
	// Embedded frames in win32 do not support traverse out. For seamless
	// embedding, we
	// check for the need to traverse out here and generate the necessary SWT
	// traversal(s).
	// TODO: this should be optional
	protected boolean checkForTraverseOut(final KeyEvent e) {
		assert EventQueue.isDispatchThread();

		// Ignore events outside this frame
		if (frame.isFocused()) {
			final Set<?> traverseForwardKeys = frame.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
			final Set<?> traverseBackwardKeys =
					frame.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
			final AWTKeyStroke key = AWTKeyStroke.getAWTKeyStrokeForEvent(e);

			if (!pendingTraverseOut) {
				// We haven't started to traverse out yet. Check to see if the
				// traversal key has been
				// hit and that we are at the last/first compoent in the
				// traversal group.

				Component limit = frame.getFocusTraversalPolicy().getLastComponent(frame);
				if (traverseForwardKeys.contains(key) && (limit == e.getComponent() || limit == null)) {
					// Tabbing forward from last component in frame (or empty
					// frame)
					pendingTraverseOut = true;
					pendingTraverseOutSeqNum++;
					swtTraverse(SWT.TRAVERSE_TAB_NEXT, 1, false);
					if (verboseTraverseOut) {
						trace("AWT: traversing out (forward)");
					}
					e.consume();
					return true;
				}

				limit = frame.getFocusTraversalPolicy().getFirstComponent(frame);
				if (traverseBackwardKeys.contains(key) && (limit == e.getComponent() || limit == null)) {
					// Tabbing backward from first component in frame
					pendingTraverseOut = true;
					pendingTraverseOutSeqNum++;
					swtTraverse(SWT.TRAVERSE_TAB_PREVIOUS, 1, false);
					if (verboseTraverseOut) {
						trace("AWT: traversing out (backward)");
					}
					e.consume();
					return true;
				}
			} else {
				// We received a keystroke while the traverse out is pending.
				// Record any tabs so that the
				// right number of SWT traversals can be done in the SWT thread.
				// This prevents us from losing
				// tabs when typing them very fast.
				// TODO: this needs to be generalized to all keystrokes, not
				// just traversals
				if (traverseForwardKeys.contains(key)) {
					if (verboseTraverseOut) {
						trace("forward traversal typeahead");
					}
					extraTabCount++;
					e.consume();
					return true;
				} else if (traverseBackwardKeys.contains(key)) {
					if (verboseTraverseOut) {
						trace("backward traversal typeahead");
					}
					extraTabCount--;
					e.consume();
					return true;
				}
			}
		}
		return false;
	}

	protected void processTypeAheadKeys(final int callerSeqNum) {
		if (pendingTraverseOut && extraTabCount != 0) {
			if (callerSeqNum != currentTraverseOutSeqNum + 1) {
				if (verboseTraverseOut) {
					trace("Discarding processTypeAhead request, sequence number out of sync " + callerSeqNum + "!="
							+ (currentTraverseOutSeqNum + 1) + ", extraTabCount=" + extraTabCount);
				}
				return;
			}
			if (verboseTraverseOut) {
				trace("Processing typeahead traversals, count=" + extraTabCount);
			}
			final int direction = extraTabCount > 0 ? SWT.TRAVERSE_TAB_NEXT : SWT.TRAVERSE_TAB_PREVIOUS;
			swtTraverse(direction, Math.abs(extraTabCount), true);
		}
		pendingTraverseOut = false;
		currentTraverseOutSeqNum++;
		extraTabCount = 0;
	}

	protected void swtTraverse(final int direction, final int count, final boolean flushingTypeAhead) {
		assert EventQueue.isDispatchThread();

		ThreadingHandler.getInstance().asyncExec(display, () -> {
			for (int i = 0; i < count; i++) {
				doTraverse(direction, flushingTypeAhead, pendingTraverseOutSeqNum);
			}
		});
	}

	protected void doTraverse(final int direction, final boolean flushingTypeAhead, final int seqNum) {
		assert Display.getCurrent() != null;

		Control focusControl = display.getFocusControl();
		if (verboseTraverseOut) {
			trace("SWT: traversing, control=" + focusControl);
		}
		final SwingControl activeBorderless = globalHandler.getActiveEmbedded();
		if (focusControl == null && activeBorderless != null) {
			focusControl = activeBorderless;
			if (verboseTraverseOut) {
				trace("SWT: current focus control is null; using=" + focusControl);
			}
		}
		if (focusControl != null) {
			final boolean traverse = focusControl.traverse(direction);

			final Control newFocusControl = display.getFocusControl();
			if (traverse && newFocusControl == focusControl && newFocusControl == activeBorderless) {
				// We were unable to traverse anywhere else.
				if (verboseTraverseOut) {
					trace("no-op traverse out, control=" + focusControl);
				}

				// Queue up a request to empty the typeahead buffer. Normally
				// this
				// happens when the AWT frame loses focus, but that won't happen
				// here since
				// we did not traverse to a different SWT control
				if (!flushingTypeAhead) {
					EventQueue.invokeLater(() -> processTypeAheadKeys(seqNum));
				}

				// Traverse to the right component
				// inside the embedded frame, as if coming back from SWT
				// TODO: this case is not covered under Gtk because we rely on
				// Swing's traverse out code
				// so none of this code is ever executed.
				adjustFocusForSwtTraversal(direction);
			}

			// if (verboseTraverseOut && !traverse) {
			// trace("traverse failed, from=" + focusControl);
			// }
		}
	}

	public boolean handleForceFocus(final boolean b) {
		boolean result = b;
		if (PlatformHelper.isWindows()) {
			// On Windows, focus queries are unreliable while traversing
			// SwingControls
			// In some cases this causes forceFocus() to incorrectly return
			// false, causing
			// the SwingControl to be skipped while tabbing. Try to fix it here
			// by resetting
			// the forceFocus result back to true when SWT events indicate we
			// really do have
			// focus.
			if (!result && globalHandler.getActiveWidget() == borderless && isFocusedSwt) {
				// Force focus should have returned true
				result = true;
				if (verboseTraverseOut) {
					trace(" resetting forceFocus return code to true");
				}
			}
		}
		return result;
	}

	// ==== Fix Eclipse bug 216431 on pre-3.4
	// XEmbeddedFrame does not implement the synthesize method, and the Eclipse
	// bug was fixed only for
	// win32, so we do the same in the methods below

	/**
	 * This method duplicates the behavior of recent versions of Windows SWT_AWT when the embedded Composite is
	 * activated or deactivated. It is used here to workaround bugs in earlier (pre-3.4) versions of SWT, and to handle
	 * cases where the Composite is not properly activated/deactivated, even today. See the callers of this method for
	 * more information.
	 * 
	 * @param activate
	 *            <code>true</code> if the embedded frame whould be activated; <code>false</code> otherwise
	 * @return
	 */
	protected void synthesizeWindowActivation(final boolean activate) {
		assert Display.getCurrent() != null; // On SWT event thread
		assert PlatformHelper.isWindows(); // Only done on Windows

		EventQueue.invokeLater(() -> {
			if (synthesizeMethod != null) {
				// synthesizeWindowActivation() is available. Use it. Normally,
				// this is on
				// Java 1.5 and higher.
				try {
					if (synthesizeMethod != null) {
						if (verboseFocusEvents) {
							trace("Calling synthesizeWindowActivation(" + activate + ")");
						}
						synthesizeMethod.invoke(frame, new Object[] { new Boolean(activate) });
					}
				} catch (final IllegalAccessException e1) {
					handleSynthesizeException(e1);
				} catch (final InvocationTargetException e2) {
					handleSynthesizeException(e2);
				}
			} else {
				if (activate) {
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_ACTIVATED));
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_GAINED_FOCUS));
				} else {
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_LOST_FOCUS));
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_DEACTIVATED));
				}
			}
		});
	}

	private void getSynthesizeMethod(final Class<? extends Frame> clazz) {
		if (PlatformHelper.isWindows() && !synthesizeMethodInitialized) {
			synthesizeMethodInitialized = true;
			try {
				synthesizeMethod = clazz.getMethod("synthesizeWindowActivation", new Class[] { boolean.class });
			} catch (final NoSuchMethodException e) {
				handleSynthesizeException(e);
			}
		}
	}

	private void handleSynthesizeException(final Exception e) {
		if (verboseFocusEvents) {
			e.printStackTrace();
		}
	}

	// ====
	// When the embedded frame is activated for any reason, focus will return to
	// the most
	// recently focused component. However, if the activation was a result of a
	// SWT traversal
	// operation, it will make more sense to reset the focus to the first or
	// last component.
	// This is an optional behavior, controlled through
	// SwingControl.setSwtTabOrderExtended

	protected void adjustFocusForSwtTraversal(final int currentSwtTraversal) {
		assert Display.getCurrent() != null;

		if (!swingControl.isSwtTabOrderExtended()) { return; }
		switch (currentSwtTraversal) {

			case SWT.TRAVERSE_TAB_NEXT:
			case SWT.TRAVERSE_ARROW_NEXT:
			case SWT.TRAVERSE_PAGE_NEXT:
				setInitialTraversalFocus(true);
				break;

			case SWT.TRAVERSE_TAB_PREVIOUS:
			case SWT.TRAVERSE_ARROW_PREVIOUS:
			case SWT.TRAVERSE_PAGE_PREVIOUS:
				setInitialTraversalFocus(false);
				break;
		}
	}

	protected void setInitialTraversalFocus(final boolean forward) {
		assert Display.getCurrent() != null;

		EventQueue.invokeLater(() -> {
			Component component;
			if (forward) {
				component = frame.getFocusTraversalPolicy().getFirstComponent(frame);
			} else {
				component = frame.getFocusTraversalPolicy().getLastComponent(frame);
			}
			if (verboseFocusEvents) {
				trace("Setting AWT focus on SWT traversal, forward=" + forward + ", component=" + component);
			}
			if (component != null) {
				component.requestFocus();
			}
		});
	}

	// ====

	/**
	 * Activates the embedded AWT frame, as long as the parent SWT composite has focus and is part of the active SWT
	 * shell.
	 */
	public void activateEmbeddedFrame() {
		assert Display.getCurrent() != null;

		final Shell activeShell = globalHandler.getActiveShell();
		final SwingControl activeBorderless = globalHandler.getActiveEmbedded();

		if (!borderless.isDisposed() &&

		// Make sure that this control is in the active shell, so focus is not
		// stolen from other windows.
		// (Note: display.getActiveShell() is not always accurate here, so we
		// use the static instead)
				activeShell == borderless.getShell() &&

				// Check that this control currently the focus control
				// BUT... Display.getFocusControl is unreliable when another
				// embedded AWT window has recently
				// become active, so to be safe, make sure that no other Swing
				// control has been activated
				// (otherwise we will steal focus from the other active
				// SwingControl)
				borderless == display.getFocusControl()
				&& (activeBorderless == null || activeBorderless == borderless)) {

			EventQueue.invokeLater(() -> {
				// Ideally, we would use frame.toFront() here, but that method
				// is a no-op for sun.awt.EmbeddedFrame. The next best thing
				// is to request focus on the result of getMostRecentFocusOwner
				// which will preserve any existing focus, or otherwise use the
				// initial
				// component.
				final Component component = frame.getMostRecentFocusOwner();
				if (component != null) {
					if (verboseFocusEvents) {
						trace("Manually activating: " + frame + ", focus component=" + component);
					}
					component.requestFocus();
				} else {
					// Nothing can take focus, no point activating the frame.
					if (verboseFocusEvents) {
						trace("Ignoring manual activation; no focusable components in " + frame);
					}
				}
			});
		}
	}

	protected void doActivation(final int swtTraversal) {
		if (swingControl.isDisposed()) { return; }
		if (!swingControl.isFocusControl()) {
			// We've lost focus, don't activate the underlying AWT window
			if (verboseFocusEvents) {
				trace("Focus lost before activating AWT window");
			}
			return;
		}

		// Process deferred de-activation first (Windows only).
		// If a deactivation has been deferred (see SWT.Deactivate case below),
		// handle it here.
		// Ideally we would have already deactivated as soon as some other
		// control gets focus, but
		// the deactivation code does nothing in that case. On the other hand,
		// if we just ignore
		// the deactivation altogether, the subsequent Activate triggered by
		// this event, does
		// nothing and the embedded frame never gets focus. So we do the
		// deactivate right here,
		// just before the activation.
		if (PlatformHelper.isWindows() && pendingDeactivate) {
			synthesizeWindowActivation(false);
			pendingDeactivate = false;
		}

		if (PlatformHelper.isWindows() && synthesizeMethod != null) {
			// Activate the window now
			synthesizeWindowActivation(true);
		}

		adjustFocusForSwtTraversal(swtTraversal);
	}

	// ========== Listener implementations

	protected class SwtEventFilter implements Listener {

		@Override
		public void handleEvent(final Event event) {
			// Handle activation of the SWT.EMBEDDED composite. Track the
			// currently active one.
			if (event.widget == borderless) {
				switch (event.type) {
					case SWT.Activate:
						// The lastSwtTraversal may change before it is used. Save
						// its value for the asyncExecs
						final int swtTraversal = globalHandler.getCurrentSwtTraversal();

						// We use asyncExec to defer the activation and focus
						// setting in the underlying AWT frame.
						// This allows proper handling of the case where focus is
						// briefly
						// set to the Swing control and immediately moved to a SWT
						// component. (The deferred
						// handling will abort if focus has been lost on the Swing
						// control)
						//
						// This case is common when navigating among tabs in an RCP
						// view stack with the
						// left and right arrow keys. Focus is briefly given to the
						// main view component and
						// then it is returned to the view tab for further
						// navigation. If we did not defer
						// the activation, then focus cannot be restored to the view
						// tab.
						display.asyncExec(() -> doActivation(swtTraversal));

						// On windows, the actual activation needs to be deferred
						// until doActivation() to
						// prevent the problem described above, so veto the
						// activation normally done by SWT_AWT.
						if (PlatformHelper.isWindows() && synthesizeMethod != null) {
							if (verboseFocusEvents) {
								trace("Consuming SWT.Activate event: " + event);
							}
							event.type = SWT.None;
						}

						break;

					case SWT.Deactivate:
						// On Windows, when the SwingControl temporarily loses focus
						// to an ancestor, and
						// that ancestor then assigns focus right back to it, the
						// SwingControl receives only a
						// Deactivate event and not a subsequent Activate event.
						// This causes the embedded
						// Swing component to lose focus when, for example, clicking
						// on its parent RCP view tab.
						//
						// To work around this problem, we defer the deactivation
						// of the embedded frame here. See the SWT.Activate case
						// above for processing of the
						// deferred event.
						if (PlatformHelper.isWindows() && synthesizeMethod != null) {
							pendingDeactivate = true;
							// Prevent the SWT_AWT-installed listener from running
							// (and deactivating the frame).
							if (verboseFocusEvents) {
								trace("Consuming SWT.Activate event: " + event);
							}
							event.type = SWT.None;
							break;
						}

				}
			}
		}

	}

	protected class SwtFocusListener implements FocusListener {

		@Override
		public void focusGained(final FocusEvent e) {
			isFocusedSwt = true;
		}

		@Override
		public void focusLost(final FocusEvent e) {
			isFocusedSwt = false;
		}
	}

	protected class AwtWindowFocusListener implements WindowFocusListener {

		@Override
		public void windowGainedFocus(final WindowEvent e) {
			assert !pendingTraverseOut;
			assert extraTabCount == 0;
		}

		@Override
		public void windowLostFocus(final WindowEvent e) {
			if (PlatformHelper.isWindows()) {
				hideTextSelection();
				processTypeAheadKeys(pendingTraverseOutSeqNum);
			}
		}

	}

	protected class AwtKeyDispatcher implements KeyEventDispatcher {

		@Override
		public boolean dispatchKeyEvent(final KeyEvent e) {
			boolean result = false;

			if (PlatformHelper.isWindows()) {
				result = checkForTraverseOut(e);
			}
			return result;
		}

	}

	void trace(final String msg) {
		System.err.println(header() + ' ' + msg);
	}

	String header() {
		return "@" + System.currentTimeMillis() + " " + System.identityHashCode(this);
	}

}
