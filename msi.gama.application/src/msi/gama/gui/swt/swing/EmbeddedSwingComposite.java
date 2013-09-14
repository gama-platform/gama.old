/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.swing.experimental.core.OutputSynchronizer;
import msi.gaml.operators.Maths;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

/**
 * A SWT composite widget for embedding Swing components in a SWT composite within an RCP or
 * standalone-SWT application. The Eclipse platform provides limited support for embedding Swing
 * components through {@link org.eclipse.swt.awt.SWT_AWT}. This class extends that support by
 * <ul>
 * <li>Using the platform-specific system Look and Feel.
 * <li>Ensuring AWT modal dialogs are modal across the SWT application.
 * <li>Reducing flicker, especially on window resizes
 * <li>Allowing Tab Traversal to and from the Embedded Frame
 * <li>Dismissing most Pop-Up Menus when focus leaves the AWT frame.
 * <li>Synchronizing Font Changes from system settings
 * <li>Working around various AWT/Swing bugs
 * </ul>
 * <P>
 * If, rather than embedding Swing components, you are integrating with Swing by opening Swing dialogs, see the
 * {@link AwtEnvironment} class.
 * <p>
 * This is an abstract that is normally used by extending it and implementing the {@link #createSwingComponent()}
 * method. For example,
 * 
 * <pre>
 * embeddedComposite = new EmbeddedSwingComposite(parent, SWT.NONE) {
 * 
 * 	protected JComponent createSwingComponent() {
 * 		scrollPane = new JScrollPane();
 * 		table = new JTable();
 * 		scrollPane.setViewportView(table);
 * 		return scrollPane;
 * 	}
 * };
 * embeddedComposite.populate();
 * </pre>
 * 
 * <p>
 * The Swing component is created inside a standard Swing containment hierarchy, rooted in a
 * {@link javax.swing.RootPaneContainer}. The root pane container is placed inside an AWT frame, as returned by
 * {@link org.eclipse.swt.awt.SWT_AWT#new_Frame(Composite)}
 * <p>
 * <b>Note:</b> When you mix components from Swing/AWT and SWT toolkits, there will be two UI event threads, one for
 * AWT, one for SWT. Most SWT APIs require that you call them from the SWT thread. Swing has similar restrictions though
 * it does not enforce them as much as SWT.
 * <p>
 * Applications need to be aware of the current thread, and, where necessary, schedule tasks to run on another thread.
 * This has always been required in the pure Swing or SWT environments, but when mixing Swing and SWT, more of this
 * scheduling will be necessary.
 * <p>
 * To schedule work on the AWT event thread, you can use:
 * <ul>
 * <li>{@link javax.swing.SwingUtilities#invokeLater(Runnable)}
 * <li>{@link javax.swing.SwingUtilities#invokeAndWait(Runnable)}
 * </ul>
 * <p>
 * (or similar methods in {@link java.awt.EventQueue})
 * <p>
 * To schedule work on the SWT event thread, use:
 * <ul>
 * <li>{@link org.eclipse.swt.widgets.Display#asyncExec(Runnable)}
 * <li>{@link org.eclipse.swt.widgets.Display#syncExec(Runnable)}
 * </ul>
 * Of course, as in single-toolkit environments, long-running tasks should be offloaded from either UI thread to a
 * background thread. The Eclipse jobs API can be used for this purpose.
 */
public abstract class EmbeddedSwingComposite extends Composite {

	/**
	 * The Class AwtContext.
	 */
	private static class AwtContext {

		/** The frame. */
		private final Frame frame;

		/** The swing component. */
		private JComponent swingComponent;

		/**
		 * Instantiates a new awt context.
		 * 
		 * @param frame the frame
		 */
		AwtContext(final Frame frame) {
			assert frame != null;
			this.frame = frame;
		}

		/**
		 * Gets the frame.
		 * 
		 * @return the frame
		 */
		Frame getFrame() {
			return frame;
		}

		/**
		 * Sets the swing component.
		 * 
		 * @param swingComponent the new swing component
		 */
		void setSwingComponent(final JComponent swingComponent) {
			this.swingComponent = swingComponent;
		}

		/**
		 * Gets the swing component.
		 * 
		 * @return the swing component
		 */
		JComponent getSwingComponent() {
			return swingComponent;
		}

	}

	/** The current system font. */
	private Font currentSystemFont;

	/** The awt context. */
	private AwtContext awtContext;

	/** The awt handler. */
	private AwtFocusHandler awtHandler;

	/** The settings listener. */
	private final Listener settingsListener = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			handleSettingsChange();
		}
	};

	// This listener helps ensure that Swing popup menus are properly dismissed
	// when
	// a menu item off the SWT main menu bar is shown.
	/** The menu listener. */
	private final Listener menuListener = new Listener() {

		@Override
		public void handleEvent(final Event event) {
			assert awtHandler != null;

			awtHandler.postHidePopups();
		}
	};

	protected String outputName;
	public boolean isOpenGL;

	// private final IDisplayOutput displayOutput = null;

	/**
	 * Constructs a new instance of this class given its parent and a style value describing its
	 * behavior and appearance.
	 * <p>
	 * This method must be called from the SWT event thread.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
	 * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
	 * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * <p>
	 * The styles SWT.EMBEDDED and SWT.NO_BACKGROUND will be added to the specified style. Usually, no other style bits
	 * are needed.
	 * 
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 *                </ul>
	 * @see Widget#getStyle
	 */
	public EmbeddedSwingComposite(final Composite parent, final int style) {
		super(parent, style | SWT.EMBEDDED | SWT.NO_BACKGROUND);
		getDisplay().addListener(SWT.Settings, settingsListener);
		setLayout(new FillLayout());
		currentSystemFont = getFont();

		// addKeyListener(keyListener);
		// this.displayOutput = displayOutput;
	}

	/**
	 * Populates the embedded composite with the Swing component.
	 * <p>
	 * This method must be called from the SWT event thread.
	 * <p>
	 * The Swing component will be created by calling {@link #createSwingComponent()}. The creation is scheduled
	 * asynchronously on the AWT event thread. This method does not wait for completion of this asynchronous task, so it
	 * may return before createSwingComponent() is complete.
	 * <p>
	 * The Swing component is created inside a standard Swing containment hierarchy, rooted in a
	 * {@link javax.swing.RootPaneContainer}. Clients can override {@link #addRootPaneContainer(Frame)} to provide their
	 * own root pane container implementation.
	 * <p>
	 * This method can be called multiple times for a single instance. If an embedded frame exists from a previous call,
	 * it is disposed.
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li> <li>
	 *                ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 *                </ul>
	 */
	public void populate() {
		checkWidget();
		createFrame();
		scheduleComponentCreation();
	}

	/**
	 * Creates the embedded Swing component. This method is called from the AWT event thread.
	 * <p>
	 * Implement this method to provide the Swing component that will be shown inside this composite. The returned
	 * component will be added to the Swing content pane. At least one component must be created by this method; null is
	 * not a valid return value.
	 * 
	 * @return a non-null Swing component
	 */
	protected abstract JComponent createSwingComponent();

	/**
	 * Adds a root pane container to the embedded AWT frame. Override this to provide your own
	 * {@link javax.swing.RootPaneContainer} implementation. In most cases, it is not necessary to
	 * override this method.
	 * <p>
	 * This method is called from the AWT event thread.
	 * <p>
	 * If you are defining your own root pane container, make sure that there is at least one heavyweight (AWT)
	 * component in the frame's containment hierarchy; otherwise, event processing will not work correctly. See
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4982522 for more information.
	 * 
	 * @param frame the frame to which the root pane container is added
	 * @return a non-null Swing component
	 */
	private RootPaneContainer addRootPaneContainer(final Frame frame) {
		assert EventQueue.isDispatchThread(); // On AWT event thread
		assert frame != null;

		// It is important to set up the proper top level components in the
		// frame:
		// 1) For Swing to work properly, Sun documents that there must be an implementor of
		// javax.swing.RootPaneContainer at the top of the component hierarchy.
		// 2) For proper event handling there must be a heavyweight an AWT frame must contain a heavyweight component
		// (see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4982522)
		// 3) The Swing implementation further narrows the options by expecting that the top of the hierarchy be a
		// JFrame, JDialog, JWindow, or JApplet. See javax.swing.PopupFactory. All this drives the choice of JApplet for
		// the top level Swing component. It is the only single component that satisfies all the above. This does not
		// imply that we have a true applet; in particular, there is no notion of an applet lifecycle in this context.
		final JApplet applet = new JApplet();
		// final JWindow applet = new JWindow();

		// In JRE 1.4, the JApplet makes itself a focus cycle root. This
		// interferes with the focus handling installed on the parent frame, so
		// change it back to a non-root here.
		// TODO: consider moving the focus policy from the Frame down to the JApplet
		applet.setFocusCycleRoot(false);

		frame.add(applet);

		return applet;
	}

	/**
	 * Performs custom updates to newly set fonts. This method is called whenever a change to the
	 * system font through the system settings (i.e. control panel) is detected.
	 * <p>
	 * This method is called from the AWT event thread.
	 * <p>
	 * In most cases it is not necessary to override this method. Normally, the implementation of this class will
	 * automatically propogate font changes to the embedded Swing components through Swing's Look and Feel support.
	 * However, if additional special processing is necessary, it can be done inside this method.
	 * 
	 * @param newFont New AWT font
	 */
	private void updateAwtFont(final java.awt.Font newFont) {}

	/**
	 * Returns the embedded AWT frame. The returned frame is the root of the AWT containment
	 * hierarchy for the embedded Swing component. This method can be called from any thread.
	 * 
	 * @return the embedded frame
	 */
	public Frame getFrame() {
		// Intentionally leaving out checkWidget() call. This may need to be
		// called from
		// within user's
		// createSwingComponent() method. Accessing from a non-SWT thread is OK,
		// but we
		// still check
		// for disposal
		if ( getDisplay() == null || isDisposed() ) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}

		return awtContext != null ? awtContext.getFrame() : null;
	}

	/**
	 * Creates the frame.
	 */
	private void createFrame() {
		assert SwtGui.getDisplay() != null; // On SWT event thread

		// Make sure Awt environment is initialized.
		AwtEnvironment.getInstance(getDisplay());

		if ( awtContext != null ) {
			final Frame oldFrame = awtContext.getFrame();
			// Schedule disposal of old frame on AWT thread so that there are no
			// problems
			// with
			// already-scheduled operations that have not completed.
			// Note: the implementation of Frame.dispose() would schedule the
			// use of the
			// AWT
			// thread even if it was not done here, but it uses invokeAndWait()
			// which is
			// prone to deadlock (and not necessary for this case).
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					oldFrame.dispose();
				}
			});
		}
		final Frame frame = SWT_AWT.new_Frame(this);
		awtContext = new AwtContext(frame);

		// Glue the two frameworks together. Do this before anything is added to
		// the frame
		// so that all necessary listeners are in place.
		createFocusHandlers();

		// This listener clears garbage during resizing, making it looker much
		// cleaner
		addControlListener(new CleanResizeListener());
	}

	/**
	 * Creates the focus handlers.
	 */
	private void createFocusHandlers() {
		assert awtContext != null;
		assert SwtGui.getDisplay() != null; // On SWT event thread

		final Frame frame = awtContext.getFrame();
		awtHandler = new AwtFocusHandler(frame);
		final SwtFocusHandler swtHandler = new SwtFocusHandler(this);
		awtHandler.setSwtHandler(swtHandler);
		swtHandler.setAwtHandler(awtHandler);

		// Ensure that AWT popups are dimissed whenever a SWT menu is shown
		getDisplay().addFilter(SWT.Show, menuListener);

		final EmbeddedChildFocusTraversalPolicy policy = new EmbeddedChildFocusTraversalPolicy(awtHandler);
		frame.setFocusTraversalPolicy(policy);
	}

	/**
	 * Schedule component creation.
	 */
	private void scheduleComponentCreation() {
		assert awtContext != null;

		// Create AWT/Swing components on the AWT thread. This is
		// especially necessary to avoid an AWT leak bug (6411042).
		final AwtContext currentContext = awtContext;
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				final RootPaneContainer container = addRootPaneContainer(currentContext.getFrame());
				final JComponent swingComponent = createSwingComponent();
				currentContext.setSwingComponent(swingComponent);
				container.getRootPane().getContentPane().add(swingComponent);
				setComponentFont();
				if ( !isOpenGL ) {
					// Deferred to the OpenGL renderer to signify its initialization
					// see JOGLAWTGLRendered.init()
					OutputSynchronizer.decInitializingViews(outputName);
				}
			}
		});
	}

	/**
	 * Sets the component font.
	 */
	private void setComponentFont() {
		assert currentSystemFont != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		final JComponent swingComponent = awtContext != null ? awtContext.getSwingComponent() : null;
		if ( swingComponent != null && !currentSystemFont.getDevice().isDisposed() ) {
			GuiUtils.run(new Runnable() {

				@Override
				public void run() {
					final FontData fontData = currentSystemFont.getFontData()[0];

					// AWT font sizes assume a 72 dpi resolution, always. The
					// true
					// screen
					// resolution must be
					// used to convert the platform font size into an AWT point
					// size
					// that matches
					// when displayed.
					final int resolution = Toolkit.getDefaultToolkit().getScreenResolution();
					final int awtFontSize = Maths.round((double) fontData.getHeight() * resolution / 72.0);

					// The style constants for SWT and AWT map exactly, and since they are int constants, they should
					// never change. So, the SWT style is passed through as the AWT style.
					final java.awt.Font awtFont =
						new java.awt.Font(fontData.getName(), fontData.getStyle(), awtFontSize);

					// Update the look and feel defaults to use new font.
					updateLookAndFeel(awtFont);

					// Allow subclasses to react to font change if necessary.
					updateAwtFont(awtFont);

				}
			});

			// Allow components to update their UI based on new font
			// TODO: should the update method be called on the root pane
			// instead?
			final Container contentPane = swingComponent.getRootPane().getContentPane();
			SwingUtilities.updateComponentTreeUI(contentPane);
		}
	}

	/**
	 * Update look and feel.
	 * 
	 * @param awtFont the awt font
	 */
	private void updateLookAndFeel(final java.awt.Font awtFont) {
		assert awtFont != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// The FontUIResource class marks the font as replaceable by the look
		// and feel
		// implementation if font settings are later changed.
		final FontUIResource fontResource = new FontUIResource(awtFont);

		// Assign the new font to the relevant L&F font properties. These are
		// the properties that are initially assigned to the system font
		// under the Windows look and feel.
		// TODO: It's possible that other platforms will need other assignments.
		// TODO: This does not handle fonts other than the "system" font.
		// Other fonts may change, and the Swing L&F may not be adjusting.

		UIManager.put("Button.font", fontResource); //$NON-NLS-1$
		UIManager.put("CheckBox.font", fontResource); //$NON-NLS-1$
		UIManager.put("ComboBox.font", fontResource); //$NON-NLS-1$
		UIManager.put("EditorPane.font", fontResource); //$NON-NLS-1$
		UIManager.put("Label.font", fontResource); //$NON-NLS-1$
		UIManager.put("List.font", fontResource); //$NON-NLS-1$
		UIManager.put("Panel.font", fontResource); //$NON-NLS-1$
		UIManager.put("ProgressBar.font", fontResource); //$NON-NLS-1$
		UIManager.put("RadioButton.font", fontResource); //$NON-NLS-1$
		UIManager.put("ScrollPane.font", fontResource); //$NON-NLS-1$
		UIManager.put("TabbedPane.font", fontResource); //$NON-NLS-1$
		UIManager.put("Table.font", fontResource); //$NON-NLS-1$
		UIManager.put("TableHeader.font", fontResource); //$NON-NLS-1$
		UIManager.put("TextField.font", fontResource); //$NON-NLS-1$
		UIManager.put("TextPane.font", fontResource); //$NON-NLS-1$
		UIManager.put("TitledBorder.font", fontResource); //$NON-NLS-1$
		UIManager.put("ToggleButton.font", fontResource); //$NON-NLS-1$
		UIManager.put("TreeFont.font", fontResource); //$NON-NLS-1$
		UIManager.put("ViewportFont.font", fontResource); //$NON-NLS-1$
	}

	/**
	 * Handle settings change.
	 */
	private void handleSettingsChange() {
		Font newFont;
		try {
			newFont = getDisplay().getSystemFont();
			if ( !newFont.equals(currentSystemFont) ) {
				currentSystemFont = newFont;
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						// setComponentFont();
					}
				});
			}
		} catch (Exception e) {}

	}

	/**
	 * Checks if is focusable.
	 * 
	 * @return true, if is focusable
	 */
	private boolean isFocusable() {
		if ( awtContext == null ) { return false; }
		final JComponent swingComponent = awtContext.getSwingComponent();
		return swingComponent != null && swingComponent.isFocusable();
	}

	@Override
	public boolean setFocus() {
		checkWidget();

		if ( !isFocusable() ) { return false; }
		return super.setFocus();
	}

	@Override
	public boolean forceFocus() {
		checkWidget();

		if ( !isFocusable() ) { return false; }
		return super.forceFocus();
	}

	@Override
	public void dispose() {
		if ( !isDisposed() ) {
			getDisplay().removeListener(SWT.Settings, settingsListener);
			getDisplay().removeFilter(SWT.Show, menuListener);
			super.dispose();
		}
	}

	/**
	 * Get the swing component encapsulated by this composite.
	 * 
	 * @return the swing component encapsulated by this Composite.
	 */
	public JComponent getSwingComponent() {
		return awtContext != null ? awtContext.getSwingComponent() : null;
	}

}
