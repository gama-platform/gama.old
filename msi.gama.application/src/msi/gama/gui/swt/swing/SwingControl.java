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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;

public abstract class SwingControl extends Composite {

	// Whether to print debugging information regarding size propagation
	// and layout.
	static final boolean verboseSizeLayout = false;

	public static final String SWT_PARENT_PROPERTY_KEY = "org.eclipse.albireo.swtParent";

	private final Listener settingsListener = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			handleSettingsChange();
		}
	};
	final /* private */ Display display;
	private Composite layoutDeferredAncestor;

	// The width of the border to keep around the embedded AWT frame.
	private int borderWidth;

	// The immediate holder of the embedded AWT frame. It is == this if
	// borderWidth == 0, or a different Composite if borderWidth != 0.
	private Composite borderlessChild;

	private Frame frame;
	private RootPaneContainer rootPaneContainer;
	private JComponent swingComponent;
	private boolean populated = false;

	// ========================================================================
	// Constructors

	/**
	 * Constructs a new embedded Swing control, given its parent
	 * and a style value describing its behavior and appearance.
	 * <p>
	 * This method must be called from the SWT event thread.
	 * <p>
	 * The style value is either one of the style constants defined in
	 * class <code>SWT</code> which is applicable to instances of this
	 * class, or must be built by <em>bitwise OR</em>'ing together
	 * (that is, using the <code>int</code> "|" operator) two or more
	 * of those <code>SWT</code> style constants. The class description
	 * lists the style constants that are applicable to the class.
	 * Style bits are also inherited from superclasses.
	 * </p>
	 * <p>
	 * The styles SWT.EMBEDDED and SWT.NO_BACKGROUND will be added
	 * to the specified style. Usually, no other style bits are needed.
	 *
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 *                </ul>
	 *
	 * @see Widget#getStyle
	 */
	public SwingControl(final Composite parent, final int style) {
		super(parent, style | ((style & SWT.BORDER) == 0 ? SWT.EMBEDDED : 0) | SWT.NO_BACKGROUND);
		setLayout(new FillLayout());
		display = getDisplay();

		display.addListener(SWT.Settings, settingsListener);

		// Avoid a layout() on the outer Controls until we have been able to
		// determine our preferred size - which takes a roundtrip to the AWT
		// thread and back.
		layoutDeferredAncestor = getLayoutAncestor();
		if ( layoutDeferredAncestor != null ) {
			layoutDeferredAncestor.setLayoutDeferred(true);
			// Ensure populate() is called nevertheless.
			ThreadingHandler.getInstance().asyncExec(display, new Runnable() {

				@Override
				public void run() {
					populate();
				}
			});
		}

		// Get the width of the border, i.e. the margins of this Composite.
		// If style contains SWT.BORDER, it is platform dependent (2 pixels on
		// Linux/gtk); otherwise it must be 0.
		borderWidth = getBorderWidth();
		if ( (style & SWT.BORDER) != 0 ) {
			// Fix for BR #91896
			// <https://bugs.eclipse.org/bugs/show_bug.cgi?id=91896>:
			// Since the SWT_AWT.new_Frame creates a low-level connection
			// between the handle of its argument Composite and the Frame
			// it creates, and this low-level connection propagates size
			// from the Composite to the Frame automatically, it ignores
			// the border. Work around it by creating an intermediate
			// Composite.
			borderlessChild = new Composite(this, style & ~SWT.BORDER | SWT.EMBEDDED | SWT.NO_BACKGROUND) {

				/**
				 * Overridden.
				 */
				@Override
				public Rectangle getClientArea() {
					assert Display.getCurrent() != null; // On SWT event thread
					final Rectangle rect = super.getClientArea();
					SwingControl.this.assignInitialClientArea(rect);
					return rect;
				}

				/**
				 * Overridden to return false and prevent any focus change
				 * if the embedded Swing component is not focusable.
				 */
				@Override
				public boolean forceFocus() {
					checkWidget();
					return handleFocusOperation(new RunnableWithResult() {

						@Override
						public void run() {
							boolean success;
							if ( isDisposed() ) {
								success = false;
							} else {
								success = superForceFocus();
								success = postProcessForceFocus(success);
							}
							setResult(new Boolean(success));
						}
					});
				}

				/**
				 * Overridden to return false and prevent any focus change
				 * if the embedded Swing component is not focusable.
				 */
				@Override
				public boolean setFocus() {
					checkWidget();
					return handleFocusOperation(new RunnableWithResult() {

						@Override
						public void run() {
							final boolean success = isDisposed() ? false : superSetFocus();
							setResult(new Boolean(success));
						}
					});
				}

				private boolean superSetFocus() {
					return super.setFocus();
				}

				private boolean superForceFocus() {
					return super.forceFocus();
				}

			};
		} else {
			// If no border is needed, there is no need to create another
			// Composite.
			assert borderWidth == 0;
			borderlessChild = this;
		}

		// Clean up on dispose
		addListener(SWT.Dispose, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				handleDispose();
			}
		});

		initCleanResizeListener();
	}

	// ========================================================================
	// Initialization

	/**
	 * Populates the embedded composite with the Swing component.
	 * <p>
	 * This method must be called from the
	 * SWT event thread.
	 * <p>
	 * The Swing component will be created by calling {@link #createSwingComponent()}. The creation is
	 * scheduled asynchronously on the AWT event thread. This method does not wait for completion of this
	 * asynchronous task, so it may return before createSwingComponent() is complete.
	 * <p>
	 * The Swing component is created inside a standard Swing containment hierarchy, rooted in
	 * a {@link javax.swing.RootPaneContainer}. Clients can override {@link #addRootPaneContainer(Frame)}
	 * to provide their own root pane container implementation.
	 * <p>
	 * The steps above happen only on the first call to this method; subsequent calls have no effect.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 *                </ul>
	 */
	protected void populate() {
		if ( isDisposed() ) { return; }

		if ( !populated ) {
			populated = true;
			createFrame();
			scheduleComponentCreation();
		}
	}

	protected void createFrame() {
		assert Display.getCurrent() != null; // On SWT event thread

		// Make sure Awt environment is initialized.
		AwtEnvironment.getInstance(display);

		frame = SWT_AWT.new_Frame(borderlessChild);

		if ( verboseSizeLayout ) {
			ComponentDebugging.addComponentSizeDebugListeners(frame);
		}

		initializeFocusManagement();
		initKeystrokeManagement();
		initFirstResizeActions();

		if ( HIDE_SWING_POPUPS_ON_SWT_SHELL_BOUNDS_CHANGE ) {
			getShell().addControlListener(shellControlListener);
		}

	}

	protected void scheduleComponentCreation() {
		assert frame != null;

		final Color foreground = getForeground();
		final Color background = getBackground();
		final Font font = getFont();
		final FontData[] fontData = font.getFontData();

		// Create AWT/Swing components on the AWT thread. This is
		// especially necessary to avoid an AWT leak bug (Sun bug 6411042).
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				// If the client is using the now-obsolete javax.swing.DefaultFocusManager
				// class under Windows, the default focus traversal policy may be changed from
				// LayoutFocusTraversalPolicy (set by the L&F) to LegacyGlueFocusTraversalPolicy.
				// The latter policy causes stack overflow errors when setting focus on a JApplet
				// in an embedded frame, so we force the policy to LayoutFocusTraversalPolicy here
				// and later when the JApplet is created. It is especially important to do this since
				// the Eclipse workbench code itself uses DefaultFocusManager (see
				// org.eclipse.ui.internal.handlers.WidgetMethodHandler).
				// TODO: can this be queried from the L&F?
				final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
				if ( kfm.getDefaultFocusTraversalPolicy().getClass()
					.getName() == "javax.swing.LegacyGlueFocusTraversalPolicy" ) {
					kfm.setDefaultFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
				}
				if ( frame.getFocusTraversalPolicy() != null && frame.getFocusTraversalPolicy().getClass()
					.getName() == "javax.swing.LegacyGlueFocusTraversalPolicy" ) {
					frame.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
				}

				rootPaneContainer = addRootPaneContainer(frame);
				initPopupMenuSupport(rootPaneContainer.getRootPane());

				// The color of the frame is visible during redraws. Use the
				// same color, to reduce flickering, and set it as soon as possible
				setComponentBackground(frame, background, true);

				swingComponent = createSwingComponent();
				if ( swingComponent != null ) {
					// Pass on color and font values
					// The color of the content Pane is visible permanently.
					setComponentForeground(rootPaneContainer.getContentPane(), foreground, true);
					setComponentBackground(rootPaneContainer.getContentPane(), background, true);
					setComponentFont(font, fontData, true);

					rootPaneContainer.getRootPane().getContentPane().add(swingComponent);
					swingComponent.putClientProperty(SWT_PARENT_PROPERTY_KEY, SwingControl.this);
					// frame.setFocusable(true);
				}

				// Invoke hooks, for use by the application.
				afterComponentCreatedAWTThread();
				try {
					ThreadingHandler.getInstance().asyncExec(display, new Runnable() {

						@Override
						public void run() {

							// Propagate focus to Swing, if necesssary
							if ( focusHandler != null ) {
								focusHandler.activateEmbeddedFrame();
							}

							// Now that the preferred size is known, enable
							// the layout on the layoutable ancestor.
							if ( layoutDeferredAncestor != null && !layoutDeferredAncestor.isDisposed() ) {
								layoutDeferredAncestor.layout();
								layoutDeferredAncestor.setLayoutDeferred(false);
							}
							// Invoke hooks, for use by the application.
							afterComponentCreatedSWTThread();
						}
					});
				} catch (final SWTException e) {
					if ( e.code == SWT.ERROR_WIDGET_DISPOSED ) {
						return;
					} else {
						throw e;
					}
				}
			}
		});
	}

	/**
	 * Adds a root pane container to the embedded AWT frame. Override this to provide your own
	 * {@link javax.swing.RootPaneContainer} implementation. In most cases, it is not necessary
	 * to override this method.
	 * <p>
	 * This method is called from the AWT event thread.
	 * <p>
	 * If you are defining your own root pane container, make sure that there is at least one
	 * heavyweight (AWT) component in the frame's containment hierarchy; otherwise, event
	 * processing will not work correctly. See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4982522
	 * for more information.
	 *
	 * @param frame the frame to which the root pane container is added
	 * @return a non-null Swing component
	 */
	protected RootPaneContainer addRootPaneContainer(final Frame frame) {
		assert EventQueue.isDispatchThread(); // On AWT event thread
		assert frame != null;

		// It is important to set up the proper top level components in the frame:
		// 1) For Swing to work properly, Sun documents that there must be an implementor of
		// javax.swing.RootPaneContainer at the top of the component hierarchy.
		// 2) For proper event handling
		// an AWT frame must contain a heavyweight component (see
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4982522)
		// 3) The Swing implementation further narrows the options by expecting that the
		// top of the hierarchy be a JFrame, JDialog, JWindow, or JApplet. See
		// javax.swing.PopupFactory or javax.swing.SwingUtilities.convertPoint,
		// for example.
		// 4) Trying to add a JFrame, JDialog, or JWindow as child to a Frame
		// yields an exception "adding a window to a container". However,
		// JApplet is lucky since it inherits from Panel, not from Window.
		//
		// All this drives the choice of JApplet for the top level Swing component. It is the
		// only single component that satisfies all the above. This does not imply that
		// we have a true applet; in particular, there is no notion of an applet lifecycle in this
		// context.
		final JApplet applet = new ToplevelPanel();

		if ( Platform.isWin32() ) {
			// Avoid stack overflows by ensuring correct focus traversal policy
			// (see comments in scheduleComponentCreation() for details)
			applet.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
		}

		frame.add(applet);
		return applet;
	}

	/**
	 * The top-level java.awt.Panel, added as child of the frame.
	 */
	private class ToplevelPanel extends JApplet {

		// Overridden from JApplet.
		@Override
		protected JRootPane createRootPane() {
			final JRootPane rootPane = new ToplevelRootPane();
			rootPane.setOpaque(true);
			return rootPane;
		}
	}

	/**
	 * The top-level javax.swing.JRootPane, added as child of the toplevel
	 * Panel.
	 */
	private class ToplevelRootPane extends JRootPane {

		// Keep the sizes cache up to date.
		// The JRootPane, not the JApplet, is the "validation root",
		// as determined by RepaintManager.addInvalidComponent().
		// It is here that we can intercept the relevant
		// invalidate()/validateTree() calls.
		@Override
		protected void validateTree() {
			super.validateTree();
			// JRootPane returns a wrong value for getMaximumSize(),
			// namely [0,2147483647] instead of [2147483647,2147483647],
			// so we use the content pane's maximum size instead.
			updateCachedAWTSizes(getMinimumSize(), getPreferredSize(), getContentPane().getMaximumSize());
		}
	}

	/**
	 * Creates the embedded Swing component. This method is called from the AWT event thread.
	 * <p>
	 * Implement this method to provide the Swing component that will be shown inside this composite.
	 * The returned component will be added to the Swing content pane. At least one component must
	 * be created by this method; null is not a valid return value.
	 *
	 * @return a non-null Swing component
	 */
	protected abstract JComponent createSwingComponent();

	/**
	 * This callback is invoked after the embedded Swing component has been
	 * added to this control.
	 * <p>
	 * This method is executed on the AWT thread.
	 */
	protected void afterComponentCreatedAWTThread() {}

	/**
	 * This callback is invoked after the embedded Swing component has been
	 * added to this control.
	 * <p>
	 * This method is executed on the SWT thread.
	 */
	protected void afterComponentCreatedSWTThread() {}

	// ========================================================================
	// Accessors

	/**
	 * Returns the Swing component contained in this control. This method may be
	 * called from any thread.
	 * @return The embedded Swing component, or <code>null</code> if it has not
	 *         yet been initialized.
	 */
	public /* final */ JComponent getSwingComponent() {
		return swingComponent;
	}

	/**
	 * Returns the root of the AWT component hierarchy; this is the top-level
	 * parent of the embedded Swing component. This method may be called from
	 * any thread.
	 * @return An AWT container, usually a Window, or <code>null</code> if the
	 *         initialization is not yet complete.
	 */
	public /* final */ Container getAWTHierarchyRoot() {
		// Intentionally leaving out checkWidget() call. This method may be called from the
		// AWT thread. We still check for disposal, however
		if ( isDisposed() ) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		return frame;
	}

	// ========================================================================
	// Size management

	// Outside this control (on the SWT side) the size management protocol
	// consists of the computeSize() method (bottom-up size propagation)
	// and of the two setBounds() methods (top-down size propagation).
	//
	// Inside this control (on the AWT side) the size management protocol
	// consists of the getPreferredSize(), getMinimumSize(), getMaximumSize()
	// methods (bottom-up size propagation) and of the layout() method
	// (top-down size propagation).
	//
	// We connect these two protocols.
	//
	// One cannot call swingComponent.getPreferredSize()/getMinimumSize()/
	// getMaximumSize() outside the AWT event thread - this would lead to
	// deadlocks. Therefore we use a cache of their values; this cache
	// can be accessed from any thread (with 'synchronized', of course).
	//
	// We change and access the three sizes atomically at once, so that
	// they are consistent with each other.

	private Dimension cachedMinSize = new Dimension(0, 0);
	private Dimension cachedPrefSize = new Dimension(0, 0);
	private Dimension cachedMaxSize = new Dimension(0, 0);
	// Since the swingComponent is not already initialized in the constructor,
	// this control initially has no notion of what its preferred size could
	// be. This variable is
	// - 0 initially,
	// - 1 after the sizes have been set from the AWT side,
	// - 2 after these sizes have been taken into account by the SWT side.
	private int cachedSizesInitialized = 0;

	// Work around against a bug observed with the RelayoutExampleView on
	// Windows with JDK 1.6: The SWT_AWT.new_Frame method executes this code:
	// parent.getDisplay().asyncExec(new Runnable() {
	// public void run () {
	// if (parent.isDisposed()) return;
	// final Rectangle clientArea = parent.getClientArea();
	// EventQueue.invokeLater(new Runnable () {
	// public void run () {
	// frame.setSize (clientArea.width, clientArea.height);
	// frame.validate ();
	// }
	// });
	// }
	// });
	// This code overwrites the size of the frame with a size that is not
	// valid any more!
	static final boolean INITIAL_CLIENT_AREA_WORKAROUND =
		// This code is found in SWT_AWT.new_Frame for gtk, motif, win32.
		Platform.isGtk() || Platform.isMotif() || Platform.isWin32();
	private Rectangle initialClientArea;

	/*
	 * Overridden (javadoc inherited).
	 */
	@Override
	public Rectangle getClientArea() {
		checkWidget();

		assert Display.getCurrent() != null; // On SWT event thread
		final Rectangle rect = super.getClientArea();
		if ( borderlessChild == this ) {
			assignInitialClientArea(rect);
		}
		return rect;
	}

	/**
	 * Invoked from borderlessChild's override of getClientArea().
	 */
	void assignInitialClientArea(final Rectangle rect) {
		if ( INITIAL_CLIENT_AREA_WORKAROUND && initialClientArea == null ) {
			synchronized (this) {
				if ( cachedSizesInitialized >= 1 ) {
					rect.width = cachedPrefSize.width;
					rect.height = cachedPrefSize.height;
				}
			}
			// We don't want to clobber arbitrary Rectangle objects, only the
			// one use by the SWT_AWT inner class.
			final Exception e = new Exception();
			e.fillInStackTrace();
			final StackTraceElement[] stack = e.getStackTrace();
			if ( stack.length >= 3 && stack[2].getClassName().startsWith("org.eclipse.swt.awt.SWT_AWT$") ) {
				initialClientArea = rect;
			}
		}
	}

	// We have bidirectional size propagation, from AWT to SWT, and from
	// SWT to AWT. To minimize pointless notification, we inhibit propagation
	// in this situation:
	// AWT rootpane.validate() ---> SWT layout() -|-> AWT frame.setBounds.
	//
	// When more than one SwingControl is involved, the situation is more
	// complicated:
	// AWT rootpane1.validate() ---> SWT layout() -|-> AWT frame1.setBounds.
	// ---> AWT frame2.setBounds.
	// The notification from SWT to the AWT frame that triggered the layout is
	// inhibited. The notification from SWT to another AWT frame frame2 is
	// passed through, however - except if frame2 has been validated since
	// then. In the latter case, the SWT layout potentially used outdated
	// sizes from frame2; it *must*not* clobber the new size of frame2.
	//
	// In order to determine the "since then", we need a clock that runs in the
	// AWT thread.

	/**
	 * The current "time" of the AWT thread. It is incremented when any
	 * SwingControl is validated. It's an integer modulo 2^32 (wraps around).
	 * Accessed only from the AWT thread. Therefore copies of this integer
	 * have to be compared with <code>a - b < 0</code> rather than
	 * <code>a < b</code>.
	 */
	private static int currentAWTTime;

	/**
	 * The time at which this component's JRootPane was last validated.
	 * Accessed from the AWT thread and the SWT thread, therefore 'volatile'
	 * (could also be protected by a lock).
	 */
	volatile int lastValidatedAWTTime;

	/**
	 * When running in the SWT thread on behalf of a notification from the AWT
	 * thread, this variable keeps track of the AWT-time that was in effect
	 * when this notification was sent.
	 * The map key is an SWT thread, belonging to a Display. There can be
	 * several of them, therefore a Map.
	 * Accessed only from the SWT thread(s).
	 */
	static Map /* <Thread,Integer> */ onBehalfAWTTimes =
		Collections.synchronizedMap(new HashMap /* <Thread,Integer> */ ());

	/**
	 * Given the minimum, preferred, and maximum sizes of the Swing
	 * component, this method stores them in the cache and updates
	 * this control accordingly.
	 */
	protected void updateCachedAWTSizes(final Dimension min, final Dimension pref, final Dimension max) {
		assert EventQueue.isDispatchThread(); // On AWT event thread
		if ( verboseSizeLayout ) {
			System.err.println("AWT thread: updated component sizes: " + min + " <= " + pref + " <= " + max);
		}

		// Increment and memoize the current AWT time.
		lastValidatedAWTTime = ++currentAWTTime;

		boolean mustNotify;

		synchronized (this) {
			mustNotify = cachedSizesInitialized == 0;
			if ( !mustNotify ) {
				mustNotify = !(min.equals(cachedMinSize) && pref.equals(cachedPrefSize) && max.equals(cachedMaxSize));
			}
			if ( cachedSizesInitialized == 0 ) {
				cachedSizesInitialized = 1;
			}
			cachedMinSize = min;
			cachedPrefSize = pref;
			cachedMaxSize = max;

			/**
			 * Part of a workaround, see {@link #getClientArea()}.
			 */
			if ( INITIAL_CLIENT_AREA_WORKAROUND && initialClientArea != null ) {
				initialClientArea.width = cachedPrefSize.width;
				initialClientArea.height = cachedPrefSize.height;
			}
		}

		if ( mustNotify ) {
			// Preferred (and min/max) sizes are available for the AWT
			// component for the first time. Layout the composite so that those
			// sizes can be taken into account.
			final int onBehalfAWTTime = lastValidatedAWTTime;
			ThreadingHandler.getInstance().asyncExec(display, new Runnable() {

				@Override
				public void run() {
					if ( verboseSizeLayout ) {
						System.err.println("AWT->SWT thread: Laying out after size update");
					}
					if ( !isDisposed() ) {
						try {
							onBehalfAWTTimes.put(Thread.currentThread(), new Integer(onBehalfAWTTime));
							// Augment the three sizes by 2*borderWidth, avoiding
							// integer overflow.
							final Point minSize =
								new Point(Math.min(min.width, Integer.MAX_VALUE - 2 * borderWidth) + 2 * borderWidth,
									Math.min(min.height, Integer.MAX_VALUE - 2 * borderWidth) + 2 * borderWidth);
							final Point prefSize =
								new Point(Math.min(pref.width, Integer.MAX_VALUE - 2 * borderWidth) + 2 * borderWidth,
									Math.min(pref.height, Integer.MAX_VALUE - 2 * borderWidth) + 2 * borderWidth);
							final Point maxSize =
								new Point(Math.min(max.width, Integer.MAX_VALUE - 2 * borderWidth) + 2 * borderWidth,
									Math.min(max.height, Integer.MAX_VALUE - 2 * borderWidth) + 2 * borderWidth);
							// Augment the three sizes, avoiding integer overflow.
							notePreferredSizeChanged(minSize, prefSize, maxSize);
						} finally {
							onBehalfAWTTimes.remove(Thread.currentThread());
						}
					}
				}
			});
		}
	}

	/**
	 * Retrieves the minimum, preferred, and maximum sizes of the Swing
	 * component, if they are already available.
	 * @param min Output parameter for the Swing component's minimum size.
	 * @param pref Output parameter for the Swing component's preferred size.
	 * @param max Output parameter for the Swing component's maximum size.
	 * @return true if the sizes were available and the output parameters are
	 *         filled
	 */
	protected boolean getCachedAWTSizes(final Dimension min, final Dimension pref, final Dimension max) {
		synchronized (this) {
			if ( cachedSizesInitialized >= 1 ) {
				min.setSize(cachedMinSize);
				pref.setSize(cachedPrefSize);
				max.setSize(cachedMaxSize);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * True if setting the size of this control on the SWT side automatically
	 * resizes the frame and posts a COMPONENT_RESIZED event for the frame to
	 * the AWT event queue.
	 * On platforms where you are not sure, set this constant to false.
	 */
	static final boolean AUTOMATIC_SET_AWT_SIZE = Platform.isGtk() || Platform.isCarbon();

	/**
	 * This class represents a queue of requests to set the AWT frame's size.
	 * Multiple requests are automatically merged, by using the last among
	 * the specified sizes, and the OR of the preconditions.
	 */
	class SetAWTSizeQueue implements Runnable {

		// True while a request is pending.
		private boolean pending /* = false */;
		// If pending:
		// The AWT time of the notification that triggered this request
		// (null means unknown, i.e. execute the request unconditionally).
		private Integer onBehalfAWTTime;
		// If pending:
		// The size to which the frame shall be resized.
		private int width;
		private int height;
		// True while processing the last request (after it was dequeued).
		private boolean processing /* = false */;

		/**
		 * Creates an empty queue.
		 */
		SetAWTSizeQueue() {
			pending = false;
		}

		/**
		 * Enqueues a request to this queue.
		 * @param onBehalfAWTTime The AWT time of the notification that
		 *            triggered this request, or null.
		 * @param width The size to which the frame shall be resized.
		 * @param height The size to which the frame shall be resized.
		 * @return true if this queue needs to be started as a Runnable
		 */
		synchronized boolean enqueue(Integer onBehalfAWTTime, final int width, final int height) {
			assert Display.getCurrent() != null; // On SWT event thread
			if ( verboseSizeLayout ) {
				System.err
					.println("SWT thread: Preparing to set size: " + width + " x " + height + " for " + swingComponent);
			}
			// During the processing of a request, lastValidatedAWTTime is
			// unreliable: it gets incremented to a value past the
			// currentAWTTime, but this does not mean that we should drop this
			// request.
			if ( processing ) {
				onBehalfAWTTime = null;
			}
			final boolean wasPending = this.pending;
			// Shortcut to avoid posting a Runnable that has no effect.
			// (lastValidatedAWTTime can only increase until the Runnable is
			// actually run.)
			final boolean effective = onBehalfAWTTime == null || lastValidatedAWTTime - onBehalfAWTTime.intValue() < 0;
			if ( wasPending || effective ) {
				// Use the last specified size.
				this.width = width;
				this.height = height;
				// Use the OR of the old onBehalfAWTTime and the new onBehalfAWTTime.
				if ( wasPending ) {
					this.onBehalfAWTTime = this.onBehalfAWTTime == null || onBehalfAWTTime == null ? null
						: this.onBehalfAWTTime.intValue() - onBehalfAWTTime.intValue() < 0 ? onBehalfAWTTime
							: this.onBehalfAWTTime;
				} else {
					this.onBehalfAWTTime = onBehalfAWTTime;
				}
				this.pending = true;
				return !wasPending;
			} else {
				// Avoid posting a Runnable that has no effect.
				return false;
			}
		}

		/**
		 * Returns the enqueued request and removes it from the queue.
		 * @return The size to which the frame shall be resized, or null if
		 *         if does not need to be resized after all.
		 */
		private synchronized Dimension dequeue() {
			assert EventQueue.isDispatchThread(); // On AWT event thread
			if ( pending ) {
				pending = false;
				// Compare the AWT time of the notification with the
				// time at which the rootpane was last validated.
				if ( onBehalfAWTTime == null || lastValidatedAWTTime - onBehalfAWTTime.intValue() < 0 ) {
					processing = true;
					return new Dimension(width, height);
				}
			}
			return null;
		}

		// Implementation of Runnable.
		@Override
		public void run() {
			assert EventQueue.isDispatchThread(); // On AWT event thread
			assert !processing;
			for ( ;; ) {
				final Dimension size = dequeue();
				if ( size == null ) {
					break;
				}
				// Set the frame's (and thus also the rootpane's) size.
				if ( verboseSizeLayout ) {
					System.err.println("SWT->AWT thread: Setting size: " + size.width + " x " + size.height + " for " +
						swingComponent);
				}
				if ( frame != null ) {
					frame.setBounds(0, 0, Math.max(size.width, 0), Math.max(size.height, 0));
					frame.validate();
				}
				// Test if another request was enqueued (from the SWT thread)
				// while we were processing this one.
				synchronized (this) {
					processing = false;
					if ( !pending ) {
						break;
					}
				}
				// While this thread was resizing the frame, the SWT thread
				// enqueued another request.
			}
			assert !processing;
		}
	}

	private final SetAWTSizeQueue setAWTSizeQueue = new SetAWTSizeQueue();

	/**
	 * Propagate the width and height from SWT to the AWT frame.
	 * Only used if !AUTOMATIC_SET_AWT_SIZE.
	 */
	private void setAWTSize(final int width, final int height) {
		assert Display.getCurrent() != null; // On SWT event thread
		// Get the AWT time of the notification that triggered this processing.
		final Integer onBehalfAWTTime = (Integer) onBehalfAWTTimes.get(Thread.currentThread());
		if ( setAWTSizeQueue.enqueue(onBehalfAWTTime, width, height) ) {
			// Switch to the AWT thread.
			EventQueue.invokeLater(setAWTSizeQueue);
		}
	}

	/**
	 * Called when the SWT layout or client code assigns a size and position to this Control.
	 */
	protected void handleSetBounds(final int width, final int height) {
		assert Display.getCurrent() != null; // On SWT event thread
		populate();
		if ( verboseSizeLayout ) {
			System.err.println("SWT thread: setBounds called: " + width + " x " + height);
		}
		// If the size of the frame automatically tracks the size on the SWT side,
		// we don't need to do it explicitly.
		// But it's nevertheless needed for the initial display sometimes, see below.
		// TODO: research the initial content display problem further
		if ( !AUTOMATIC_SET_AWT_SIZE || Platform.isGtk() && Platform.JAVA_VERSION < Platform.javaVersion(1, 6, 0) ) {
			// Pass on the desired size to the embedded component, but only if it could
			// be reasonably calculated (i.e. we have cached preferred sizes) and if
			// computeSize took it into account.
			// If, however, the SWT side specifies a size for the component while
			// cachedSizesInitialized < 2 (i.e. usually, before the Swing component
			// is created), it is tempting to store the size given here and use it
			// when the Swing component is created later. But this leads to
			// flickering: If the size could not been reasonably calculated or if
			// computeSize did not take it into account, the values are always
			// derived from the default size 64x64 of SWT Composites. Ignore these
			// values then! It is better than to use them.
			//
			// Do not optimize with the cachedSizesInitialized flag on GTK/Java5 or
			// win32 since this may prevent the initial contents of the control
			// from being displayed. Testcases: EmbeddedJTableView, TestResizeView.
			// TODO: research the initial content display problem further
			synchronized (this) {
				if ( cachedSizesInitialized >= 2 ||
					Platform.isGtk() && Platform.JAVA_VERSION < Platform.javaVersion(1, 6, 0) || Platform.isWin32() ) {
					setAWTSize(Math.max(width - 2 * borderWidth, 0), Math.max(height - 2 * borderWidth, 0));
				}
			}
		}
	}

	// This is called by the layout when it wants to know the size preferences
	// of this control.
	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to use the preferred, minimum, and maximum sizes of
	 * the embedded Swing component.
	 * <p>
	 * This method is part of the size propagation from AWT to SWT.
	 */
	@Override
	public Point computeSize(final int widthHint, final int heightHint, final boolean changed) {
		checkWidget();

		final Dimension min = new Dimension();
		final Dimension pref = new Dimension();
		final Dimension max = new Dimension();
		final boolean initialized = getCachedAWTSizes(min, pref, max);

		if ( !initialized ) {
			if ( verboseSizeLayout ) {
				System.err.println("SWT thread: Uninitialized AWT sizes for " + swingComponent);
			}
			return super.computeSize(widthHint, heightHint, changed);
		} else {
			synchronized (this) {
				assert cachedSizesInitialized >= 1;
				cachedSizesInitialized = 2;
			}
			int width = widthHint == SWT.DEFAULT ? pref.width
				: widthHint < min.width ? min.width : widthHint > max.width ? max.width : widthHint;
			// Augment by 2*borderWidth, avoiding integer overflow.
			width = Math.min(width, Integer.MAX_VALUE - 2 * borderWidth) + 2 * borderWidth;
			int height = heightHint == SWT.DEFAULT ? pref.height
				: heightHint < min.width ? min.height : heightHint > max.width ? max.height : heightHint;
			// Augment by 2*borderWidth, avoiding integer overflow.
			height = Math.min(height, Integer.MAX_VALUE - 2 * borderWidth) + 2 * borderWidth;
			if ( verboseSizeLayout ) {
				System.err.println("SWT thread: Computed size: " + width + " x " + height + " for " + swingComponent);
			}
			return new Point(width, height);
		}
	}

	/**
	 * Returns the uppermost parent of this control that is influenced by size
	 * changes of this control. It is usually on this ancestor control that
	 * you want to call <code>layout()</code> when the preferred size of this
	 * control has changed.
	 * 
	 * @return the parent, grandparent, or other ancestor of this control, or
	 *         <code>null</code>
	 * @see #preferredSizeChanged(Point, Point, Point)
	 */
	public abstract Composite getLayoutAncestor();

		/**
		 * Called when the preferred sizes of this control, as computed by
		 * AWT, have changed.
		 */
		/* private */ void notePreferredSizeChanged(final Point minSize, final Point prefSize, final Point maxSize) {
		preferredSizeChanged(minSize, prefSize, maxSize);
		firePreferredSizeChangedEvent(minSize, prefSize, maxSize);
	}

	// TODO: remove this method and just leave the listener for advanced users?
	/**
	 * Called when the preferred sizes of this control, as computed by
	 * AWT, have changed. This method
	 * should update the size of this SWT control and of other SWT controls
	 * in the window (as appropriate).
	 * <p>
	 * This method is a more flexible alternative to {@link #getLayoutAncestor()}.
	 * You should implement that method to return null if you are overriding this method.
	 * A still more flexible way to be informed of preferred size changes is to install
	 * a {@link SizeListener} with {@link #addSizeListener(SizeListener)}.
	 * <p>
	 * This method is part of the size propagation from AWT to SWT.
	 * It is called on the SWT event thread.
	 * <p>
	 * The default implementation of this method calls
	 * <code>getLayoutableAncestor().layout()</code>, if getLayoutableAncestor() returns
	 * a non-null. Otherwise, it does nothing.
	 * <p>
	 * The parameters <var>minPoint</var>, <var>prefPoint</var>,
	 * <var>maxPoint</var> can usually be ignored: It is often enough to rely on the
	 * {@link #layout()} method.
	 * @param minSize The new minimum size for this control, as reported by
	 *            AWT, plus the border width on each side.
	 * @param prefSize The new preferred size for this control, as reported by
	 *            AWT, plus the border width on each side.
	 * @param maxSize The new maximum size for this control, as reported by
	 *            AWT, plus the border width on each side.
	 */
	protected void preferredSizeChanged(final Point minSize, final Point prefSize, final Point maxSize) {
		final Composite ancestor = getLayoutAncestor();
		if ( ancestor != null ) {
			// Not just ancestor.layout().
			// It is important to tell the Layout that the preferences have
			// changed. Objects such as org.eclipse.swt.layout.GridData (for
			// GridLayout) cache the last width and height. We must flush this
			// cached geometry.
			ancestor.layout(new Control[] { borderlessChild });
		}
	}

	// This is called by the layout when it assigns a size and position to this
	// Control.
	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to propagate the size to the embedded Swing component.
	 * <p>
	 * This method is part of the size propagation from SWT to AWT.
	 */
	@Override
	public void setBounds(final Rectangle rect) {
		checkWidget();
		handleSetBounds(rect.width, rect.height);
		super.setBounds(rect);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to propagate the size to the embedded Swing component.
	 * <p>
	 * This method is part of the size propagation from SWT to AWT.
	 */
	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		checkWidget();
		handleSetBounds(width, height);
		super.setBounds(x, y, width, height);
	}

	// ========================================================================
	// Resizing with less flickering

	// This listener clears garbage during resizing, making it look much
	// cleaner. The garbage is due to use of the sun.awt.noerasebackground
	// property, so this is done only under Windows.

	private CleanResizeListener cleanResizeListener /* = null */;

	/**
	 * Returns true if a particular mechanism for resizing with less flicker is
	 * enabled.
	 */
	public boolean isCleanResizeEnabled() {
		return cleanResizeListener != null;
	}

	/**
	 * Specifies whether the particular mechanism for resizing with less flicker
	 * should be enabled or not.
	 * <p>
	 * For this setting to be useful, a background colour should have been
	 * � * set that approximately matches the window's contents.
	 * <p>
	 * By default, this setting is enabled on Windows with JDK 1.5 or older, and
	 * disabled otherwise.
	 */
	public void setCleanResizeEnabled(final boolean enabled) {
		if ( enabled != isCleanResizeEnabled() ) {
			if ( enabled ) {
				cleanResizeListener = new CleanResizeListener();
				borderlessChild.addControlListener(cleanResizeListener);
			} else {
				borderlessChild.removeControlListener(cleanResizeListener);
				cleanResizeListener = null;
			}
		}
	}

	private void initCleanResizeListener() {
		// On Windows:
		// - In JDK 1.4 and 1.5: It indeed avoids most of the "garbage". But
		// if the background colour is not aligned with the contents of the
		// window (like here: background grey, contents dark green), the
		// cleaning is visually more disturbing than the garbage. This is
		// especially noticeable when you click with the mouse in the above
		// test view.
		// - In JDK 1.6: There is much less "garbage"; the repaint is quicker.
		// The CleanResizeListener's effect is mostly visible as flickering.
		if ( Platform.isWin32() && Platform.JAVA_VERSION < Platform.javaVersion(1, 6, 0) ) {
			setCleanResizeEnabled(true);
		}
	}

	// ========================================================================
	// Font management

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to propagate the font to the embedded Swing component.
	 */
	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		final FontData[] fontData = font.getFontData();
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				setComponentFont(font, fontData, false);
			}
		});
	}

	private void updateDefaultFont(final Font swtFont, final FontData[] swtFontData) {
		assert EventQueue.isDispatchThread(); // On AWT event thread

		final java.awt.Font awtFont = LookAndFeelHandler.getInstance().propagateSwtFont(swtFont, swtFontData);
		if ( awtFont == null ) { return; }

		// Allow subclasses to react to font change if necessary.
		updateAwtFont(awtFont);

		if ( swingComponent != null ) {
			// Allow components to update their UI based on new font
			// TODO: should the update method be called on the root pane instead?
			final Container contentPane = swingComponent.getRootPane().getContentPane();
			SwingUtilities.updateComponentTreeUI(contentPane);
		}
	}

	protected void setComponentFont(final Font swtFont, final FontData[] swtFontData, final boolean preserveDefaults) {
		assert EventQueue.isDispatchThread();

		final ResourceConverter converter = ResourceConverter.getInstance();
		final java.awt.Font awtFont = converter.convertFont(swtFont, swtFontData);

		// Allow subclasses to react to font change if necessary.
		updateAwtFont(awtFont);

		if ( rootPaneContainer != null ) {
			final Container contentPane = rootPaneContainer.getContentPane();
			if ( !contentPane.getFont().equals(awtFont) || !preserveDefaults ) {
				contentPane.setFont(awtFont);
			}
		}
	}

	/**
	 * Performs custom updates to newly set fonts. This method is called whenever a change
	 * to the system font through the system settings (i.e. control panel) is detected.
	 * <p>
	 * This method is called from the AWT event thread.
	 * <p>
	 * In most cases it is not necessary to override this method. Normally, the implementation
	 * of this class will automatically propogate font changes to the embedded Swing components
	 * through Swing's Look and Feel support. However, if additional
	 * special processing is necessary, it can be done inside this method.
	 *
	 * @param newFont New AWT font
	 */
	protected void updateAwtFont(final java.awt.Font newFont) {
		// Do nothing by default; subclasses can override to insert behavior
	}

	private void handleSettingsChange() {
		final Font newFont = getDisplay().getSystemFont();
		final FontData[] newFontData = newFont.getFontData();
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				updateDefaultFont(newFont, newFontData);
			}
		});
	}

	private void handleDispose() {
		if ( focusHandler != null ) {
			focusHandler.dispose();
		}
		if ( borderlessChild != this ) {
			borderlessChild.dispose();
		}
		display.removeListener(SWT.Settings, settingsListener);
	}

	// ============================= Painting =============================

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to propagate the background color change to the embedded AWT
	 * component.
	 */
	@Override
	public void setBackground(final Color background) {
		super.setBackground(background);

		if ( rootPaneContainer != null ) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					setComponentBackground(rootPaneContainer.getContentPane(), background, false);
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to propagate the foreground color change to the embedded AWT
	 * component.
	 */
	@Override
	public void setForeground(final Color foreground) {
		super.setForeground(foreground);

		if ( rootPaneContainer != null ) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					setComponentForeground(rootPaneContainer.getContentPane(), foreground, false);
				}
			});
		}
	}

	protected void setComponentForeground(final Component component, final Color foreground,
		final boolean preserveDefaults) {
		assert EventQueue.isDispatchThread();
		assert component != null;

		LookAndFeelHandler.getInstance().propagateSwtForeground(component, foreground, preserveDefaults);
	}

	protected void setComponentBackground(final Component component, final Color background,
		final boolean preserveDefaults) {
		assert EventQueue.isDispatchThread();
		assert component != null;

		LookAndFeelHandler.getInstance().propagateSwtBackground(component, background, preserveDefaults);
	}

	// ============================= Focus Management =============================
	private FocusHandler focusHandler;
	private boolean isSwtTabOrderExtended = true;
	private boolean isAWTPermanentFocusLossForced = true;

	protected void initializeFocusManagement() {
		assert frame != null;
		assert Display.getCurrent() != null; // On SWT event thread

		// final GlobalFocusHandler handler = AwtEnvironment.getInstance(display).getGlobalFocusHandler();
		// focusHandler = new FocusHandler(this, handler, borderlessChild, frame);
	}

	/**
	 * Configures the SwingControl's participation in SWT traversals. See {@link #isSwtTabOrderExtended}
	 * for more information.
	 * 
	 * @param isSwtTabOrderExtended
	 */
	public void setSwtTabOrderExtended(final boolean isSwtTabOrderExtended) {
		this.isSwtTabOrderExtended = isSwtTabOrderExtended;
	}

	/**
	 * Returns whether the SWT tab order is configured to extend to the
	 * child Swing components inside this SwingControl.
	 * <p>
	 * If this method returns true,
	 * then when traversing (e.g. with the tab key) forward into this SwingControl,
	 * AWT focus will be set to the first component in the frame, as determined
	 * by the AWT {@link FocusTraversalPolicy}. Similarly,
	 * when traversing backward into this SwingControl, AWT focus will be set to
	 * the last component in the frame.
	 * <p>
	 * If this method returns false, then the SwingControl participates in the tab order
	 * only as a single opaque element. Focus on a child component will then be determined
	 * completely by the AWT focus subsystem, independent of any current SWT traversal state.
	 * This normally means that focus will move to the most recently focused
	 * Swing component within the embedded frame.
	 * 
	 * @return true if the child componets are SWT traversal participants. false otherwise.
	 */
	public boolean isSwtTabOrderExtended() {
		return isSwtTabOrderExtended;
	}

	/**
	 * Returns whether a permanent focus lost event is forced on a SwingControl when focus moves to
	 * another SWT component within the same shell. See {@link #setAWTPermanentFocusLossForced(boolean)}
	 * for more information.
	 * 
	 * @return boolean
	 */
	public boolean isAWTPermanentFocusLossForced() {
		return isAWTPermanentFocusLossForced;
	}

	/**
	 * Controls whether a permanent focus lost event is forced on a SwingControl when focus moves to
	 * another SWT component within the same shell. Normally, when an AWT frame loses focus to another
	 * window, it only receives a temporary focus lost event. This can cause unexpected results when
	 * the AWT window is embedded in a SWT shell and should really act more like a composite widget
	 * within that shell. If this property is set to <code>true</code> (the default), then
	 * permanent focus loss is synthesized.
	 * <p>
	 * For more information on permanent/temporary focus loss,
	 * see the <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/awt/doc-files/FocusSpec.html">AWT
	 * Focus Subsystem</a> spec. For an example of the type of problem solved by keeping this property
	 * <code>true</code>, see <a href="http://bugs.eclipse.org/60967">bug 60967</a>.
	 * 
	 * @param isAWTPermanentFocusLossForced - <code>true</code> to enable the forcing of permanent
	 *            focus loss. <code>false</code> to disable it.
	 */
	public void setAWTPermanentFocusLossForced(final boolean isAWTPermanentFocusLossForced) {
		this.isAWTPermanentFocusLossForced = isAWTPermanentFocusLossForced;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to return false and prevent any focus change if the embedded Swing component is
	 * not focusable.
	 * <p>
	 * Note: If the Swing component has not yet been created, then this method will temporarily set focus
	 * on its parent SWT {@link Composite}. After the Swing component is created, and if it is focusable, focus
	 * will be transferred to it.
	 */
	@Override
	public boolean setFocus() {
		checkWidget();

		if ( borderlessChild == this ) {
			return handleFocusOperation(new RunnableWithResult() {

				@Override
				public void run() {
					final boolean success = isDisposed() ? false : superSetFocus();
					setResult(new Boolean(success));
				}
			});
		} else {
			return borderlessChild.setFocus();
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to return false and prevent any focus change if the embedded Swing component is
	 * not focusable.
	 * <p>
	 * Note: If the Swing component has not yet been created, then this method will temporarily set focus
	 * on its parent SWT {@link Composite}. After the Swing component is created, and if it is focusable, focus
	 * will be transferred to it.
	 */
	@Override
	public boolean forceFocus() {
		checkWidget();

		if ( borderlessChild == this ) {
			return handleFocusOperation(new RunnableWithResult() {

				@Override
				public void run() {
					boolean success;
					if ( isDisposed() ) {
						success = false;
					} else {
						success = superForceFocus();
						// Handle the return value
						success = postProcessForceFocus(success);
					}
					setResult(new Boolean(success));
				}
			});
		} else {
			return borderlessChild.forceFocus();
		}

	}

	/**
	 * Postprocess the super.forceFocus() result.
	 */
	protected boolean postProcessForceFocus(boolean result) {
		if ( focusHandler != null ) {
			result = focusHandler.handleForceFocus(result);
		}
		return result;
	}

	/**
	 * Common focus setting/forcing code. Since this may be called for the SwingControl or
	 * a borderless child component, and it may be called for setting or forcing focus,
	 * the actual code to change focus is passed in a runnable
	 * 
	 * @param focusSetter - invoked to set or force focus
	 * @return the result of running the focus setter, or true if it was deferred
	 */
	protected boolean handleFocusOperation(final RunnableWithResult focusSetter) {
		assert Display.getCurrent() != null; // On SWT event thread

		// TODO: find a reasonable way to return false when nothing is focusable in the swingComponent.
		// It needs to be done without transferring to the AWT thread.

		if ( swingComponent != null ) {
			focusSetter.run();
			return ((Boolean) focusSetter.getResult()).booleanValue();
		} else {
			// Fail if there is no underlying swing component
			return false;
		}
	}

	private boolean superSetFocus() {
		return super.setFocus();
	}

	private boolean superForceFocus() {
		return super.forceFocus();
	}

	// ============================= Events and Listeners =============================

	private final List sizeListeners = new ArrayList();

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the embedded Swing control has changed its size
	 * preferences.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SizeListener
	 * @see #removeSizeListener(SizeListener)
	 */
	public void addSizeListener(final SizeListener listener) {
		checkWidget();
		if ( listener == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		sizeListeners.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the embedded swing control has changed its size
	 * preferences.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SizeListener
	 * @see #addSizeListener(SizeListener)
	 */
	public void removeSizeListener(final SizeListener listener) {
		checkWidget();
		if ( listener == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		sizeListeners.remove(listener);
	}

	protected void firePreferredSizeChangedEvent(final Point minSize, final Point prefSize, final Point maxSize) {
		assert Display.getCurrent() != null; // On SWT event thread
		final SizeEvent event = new SizeEvent(this, minSize, prefSize, maxSize);
		for ( final Iterator iterator = sizeListeners.iterator(); iterator.hasNext(); ) {
			final SizeListener listener = (SizeListener) iterator.next();
			listener.preferredSizeChanged(event);
		}
	}

	// ==================== Swing Popup Management ================================

	private static final boolean HIDE_SWING_POPUPS_ON_SWT_SHELL_BOUNDS_CHANGE = Platform.isWin32(); // Win32: all JDKs

	// Dismiss Swing popups when the main window is moved or resized. (It would be
	// better to dismiss popups whenever the titlebar or trim is clicked, but
	// there does not seem to be a way. This is the best we can do)
	//
	// This is particularly important when the Swing popup overlaps an edge of the
	// containing SWT shell. If (on win32) the shell is moved, the overlapping
	// popup will not move which looks very strange.
	private final ControlListener shellControlListener = new ControlListener() {

		@Override
		public void controlMoved(final ControlEvent e) {
			scheduleHide();
		}

		@Override
		public void controlResized(final ControlEvent e) {
			scheduleHide();
		}

		private void scheduleHide() {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					AwtEnvironment.getInstance(display).hidePopups();
				}
			});
		}
	};

	// ============================= SWT Popup Management =============================

	// ------------------------ Displaying a popup menu ------------------------

	/**
	 * Returns the popup menu to be used on a given component.
	 * <p>
	 * The default implementation walks up the component hierarchy, looking
	 * for popup menus registered with {@link SwtPopupRegistry#setMenu} and as
	 * fallback at the popup menu registered on this <code>Control</code>.
	 * <p>
	 * This method can be overridden, to achieve dynamic popup menus.
	 * @param component The component on which a popup event was received.
	 * @param x The x coordinate, relative to the component's top left corner,
	 *            of the mouse cursor when the event occurred.
	 * @param y The y coordinate, relative to the component's top left corner,
	 *            of the mouse cursor when the event occurred.
	 * @param xAbsolute The x coordinate, relative to this control's top left
	 *            corner, of the mouse cursor when the event occurred.
	 * @param yAbsolute The y coordinate, relative to this control's top left
	 *            corner, of the mouse cursor when the event occurred.
	 */
	public Menu getMenu(final java.awt.Component component, final int x, final int y, final int xAbsolute,
		final int yAbsolute) {
		checkWidget();

		Menu menu = SwtPopupRegistry.getInstance().findMenu(component, x, y, xAbsolute, yAbsolute);
		if ( menu == null ) {
			// Fallback: The menu set through the SWT API on this Control.
			menu = getMenu();
		}
		return menu;
	}

	protected void initPopupMenuSupport(final javax.swing.JRootPane root) {
		SwtPopupHandler.getInstance().monitorAwtComponent(root);
	}

	@Override
	public String toString() {
		return super.toString() + " [frame=" + (frame != null ? frame.getName() : "null") + "]";
	}

	// ============================= Keystroke Management =============================

	Set consumedKeystrokes = new HashSet();

	/**
	 * Initializes keystroke management for this control.
	 */
	protected void initKeystrokeManagement() {
		assert Display.getCurrent() != null; // On SWT event thread

		// Platform-specific default consumed keystrokes
		if ( Platform.isWin32() ) {
			// Shift-F10 is normally used to display a context popup menu.
			// When this happens in Windows and inside of a Swing component,
			// the consumption of the key is unknown to SWT. As a result,
			// when SWT passes control to the default windows windowProc,
			// Windows will handle the Shift-F10 like it handles F10 and Alt
			// (alone); it will shift keyboard focus to the main menu bar.
			// This will interfere with the Swing popup menu by removing
			// its focus. Prevents the default windows behavior by
			// consuming the released keystroke event for Shift-F10, so that
			// the Swing context menu, if any. can be properly used.
			//
			// TODO: This is really l&f-dependent. Find a way to query the l&f for the popup key
			addConsumedKeystroke(new SwtKeystroke(SWT.KeyUp, SWT.F10, SWT.SHIFT));
		}

		borderlessChild.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent e) {
				handleKeyEvent(SWT.KeyDown, e);
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				handleKeyEvent(SWT.KeyUp, e);
			}
		});

	}

	protected void handleKeyEvent(final int type, final KeyEvent e) {
		assert Display.getCurrent() != null; // On SWT event thread
		final SwtKeystroke key = new SwtKeystroke(type, e);
		if ( consumedKeystrokes.contains(key) ) {
			// System.err.println("Capturing key " + key);
			e.doit = false;
		}
	}

	/**
	 * Returns the set of keystrokes which will be consumed by this control. See
	 * {@link #addConsumedKeystroke(SwtKeystroke)} for more information.
	 * 
	 * @return Set the keystrokes configured to be consumed.
	 */
	public Set getConsumedKeystrokes() {
		checkWidget();
		return Collections.unmodifiableSet(consumedKeystrokes);
	}

	/**
	 * Configures a SWT keystroke to be consumed automatically by this control
	 * whenever it is detected.
	 * <p>
	 * This method can be used to block a SWT keystroke from being propagated
	 * both to the embedded Swing component and to the native window system.
	 * By consuming a keystroke, you can avoid conflicts in key handling between
	 * the Swing component and the rest of the application.
	 * 
	 * @param key the keystroke to consume.
	 */
	public void addConsumedKeystroke(final SwtKeystroke key) {
		checkWidget();
		consumedKeystrokes.add(key);
	}

	/**
	 * Removes a SWT keystroke from the set of keystrokes to be consumed by this control.
	 * See {@link #addConsumedKeystroke(SwtKeystroke)} for more information.
	 * 
	 * @return <code>true</code> if a keystroke was successfully removed from the set.
	 */
	public boolean removeConsumedKeystroke(final SwtKeystroke key) {
		checkWidget();
		return consumedKeystrokes.remove(key);
	}

	// ============================= Post-first resize actions =============================

	// Swing components created in createSwingComponent are not always
	// initialized properly because the embedded frame does not have its
	// bounds set early enough. This can happen when the
	// component tries to do initialization with an invokeLater() call.
	// In a normal Swing environment that would delay the initialization until
	// after the frame and its child components had a real size, but not so in this
	// environment. SWT_AWT sets the frame size inside an invokeLater, which
	// is itself nested inside a syncExec. So the bounds can be set after any
	// invokeLater() in called as part of createSwingComponent().

	protected void initFirstResizeActions() {
		frame.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				scrollTextFields(frame);
				// We care about only the first resize
				frame.removeComponentListener(this);
			}
		});
	}

	// Scroll all the text fields (JTextComponent) so that the caret will be visible
	// when they are focused.
	protected void scrollTextFields(final Component c) {
		if ( c instanceof JTextComponent ) {
			final JTextComponent tc = (JTextComponent) c;
			if ( tc.getDocument() != null && tc.getDocument().getLength() > 0 ) {
				// Reset the caret position to force a scroll of
				// the text component to the proper place
				final int position = tc.getCaretPosition();
				final int tempPosition = position > 0 ? 0 : 1;
				tc.setCaretPosition(tempPosition);
				tc.setCaretPosition(position);
			}
		} else if ( c instanceof Container ) {
			final Component[] children = ((Container) c).getComponents();
			for ( int i = 0; i < children.length; i++ ) {
				scrollTextFields(children[i]);
			}
		}
	}
}
