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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.swing;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.runtime.GAMA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * An environment to enable the proper display of AWT/Swing windows within a SWT or RCP application.
 * This class extends the base {@link org.eclipse.swt.awt.SWT_AWT Eclipse SWT/AWT integration} support by
 * <ul>
 * <li>Using the platform-specific system Look and Feel.
 * <li>Ensuring AWT modal dialogs are modal across the SWT application.
 * <li>Working around various AWT/Swing bugs
 * </ul>
 * <p>
 * This class is most helpful to applications which create new AWT/Swing windows (e.g. dialogs) rather than those which
 * embed AWT/Swing components in SWT windows. For support specific to embedding AWT/Swing components see
 * {@link EmbeddedSwingComposite}.
 * <p>
 * There is at most one instance of this class per SWT {@link org.eclipse.swt.widgets.Display
 * Display}. In almost all applications this means that there is exactly one instance for the entire application. In
 * fact, the current implementation always limits the number of instances to exactly one.
 * <p>
 * An instance of this class can be obtained with the static {@link #getInstance(Display)} method.
 */
public final class AwtEnvironment {

	static {
		setKeyEventsDispatcher();
	}

	// TODO: add pop-up dismissal and font synchronization support to this
	// level?

	/** The Constant GTK_LOOK_AND_FEEL_NAME. */
	private static final String GTK_LOOK_AND_FEEL_NAME = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"; //$NON-NLS-1$

	/** The instance. */
	private static AwtEnvironment instance = null;

	/** The is look and feel initialized. */
	private static boolean isLookAndFeelInitialized = false;

	/** The display. */
	private final Display display;

	/** The dialog listener. */
	private final AwtDialogListener dialogListener;

	/**
	 * Returns the single instance of AwtEnvironment for the given display. On the first call to
	 * this method, the necessary initialization to allow AWT/Swing code to run properly within an
	 * Eclipse application is done. This initialization includes setting the approprite look and
	 * feel and registering the necessary listeners to ensure proper behavior of modal dialogs.
	 * <p>
	 * The first call to this method must occur before any AWT/Swing APIs are called.
	 * <p>
	 * The current implementation limits the number of instances of AwtEnvironment to one. If this method is called with
	 * a display different to one used on a previous call, {@link UnsupportedOperationException} is thrown.
	 * 
	 * @param display the non-null SWT display
	 * 
	 * @return the AWT environment
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the display is null</li>
	 *                </ul>
	 * @exception UnsupportedOperationException - on attempt to use multiple displays.
	 */
	protected static AwtEnvironment getInstance(final Display display) {
		// For now assume a single display. If necessary, this implementation
		// can be changed to create multiple environments for multiple display
		// applications.
		// TODO: add multiple display support
		if ( display == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if ( instance != null && display != null && !display.equals(instance.display) ) { throw new UnsupportedOperationException(
			"Multiple displays not supported"); }
		synchronized (AwtEnvironment.class) {
			if ( instance == null ) {
				instance = new AwtEnvironment(display);
			}
		}
		return instance;
	}

	// Private constructor - clients use getInstance() to obtain instances
	/**
	 * Instantiates a new awt environment.
	 * 
	 * @param display the display
	 */
	private AwtEnvironment(final Display display) {
		assert display != null;

		/*
		 * This property removes a large amount of flicker from embedded swing components. Ideally
		 * it would not be set until EmbeddedSwingComposite is used, but since its value is read
		 * once and cached by AWT, it needs to be set before any AWT/Swing APIs are called.
		 */
		// TODO: this is effective only on Windows.
		System.setProperty("sun.awt.noerasebackground", "true"); //$NON-NLS-1$//$NON-NLS-2$

		/*
		 * RCP apps always want the standard platform look and feel It's important to wait for the
		 * L&F to be set so that any subsequent calls to createFrame() will be return a frame with
		 * the proper L&F (note that createFrame() happens on the SWT thread).
		 * 
		 * The call to invokeAndWait is safe because the first call AwtEnvironment.getInstance
		 * should happen before any (potential deadlocking) activity occurs on the AWT thread.
		 */
		try {
			EventQueue.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					setSystemLookAndFeel();
				}
			});
		} catch (final InterruptedException e) {
			SWT.error(SWT.ERROR_FAILED_EXEC, e);
		} catch (final InvocationTargetException e) {
			SWT.error(SWT.ERROR_FAILED_EXEC, e);
		}

		this.display = display;

		// Listen for AWT modal dialogs to make them modal application-wide
		dialogListener = new AwtDialogListener(display);
	}

	/**
	 * Invokes the given runnable in the AWT event thread while blocking user input on the SWT event
	 * thread. The SWT event thread will remain blocked until the runnable task completes, at which
	 * point this method will return.
	 * <p>
	 * This method is useful for displayng modal AWT/Swing dialogs from the SWT event thread. The modal AWT/Swing dialog
	 * will always block input across the whole application, but not until it appears. By calling this method, it is
	 * guaranteed that SWT input is blocked immediately, even before the AWT/Swing dialog appears.
	 * <p>
	 * To avoid unnecessary flicker, AWT/Swing dialogs should have their parent set to a frame returned by
	 * {@link #createDialogParentFrame()}.
	 * <p>
	 * This method must be called from the SWT event thread.
	 * 
	 * @param runnable the code to schedule on the AWT event thread
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the runnable is null</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 *                </ul>
	 */
	public void invokeAndBlockSwt(final Runnable runnable) {
		assert display != null;

		/*
		 * This code snippet is based on the following thread on news.eclipse.platform.swt:
		 * http://dev.eclipse.org/newslists/news.eclipse .platform.swt/msg24234.html
		 */
		if ( runnable == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if ( display != SwtGui.getDisplay() ) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}

		// Switch to the AWT thread...
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					// do swing work...
					if ( runnable != null ) {
						runnable.run();
					}
				} finally {
					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							// Unblock SWT
							SwtInputBlocker.unblock();
						}
					});
				}
			}
		});

		// Prevent user input on SWT components
		SwtInputBlocker.block();
	}

	/**
	 * Creates an AWT frame suitable as a parent for AWT/Swing dialogs.
	 * <p>
	 * This method must be called from the SWT event thread. There must be an active shell associated with the
	 * environment's display.
	 * <p>
	 * The created frame is a non-visible child of the active shell and will be disposed when that shell is disposed.
	 * <p>
	 * See {@link #createDialogParentFrame(Shell)} for more details.
	 * 
	 * @return a {@link java.awt.Frame} to be used for parenting dialogs
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 *                </ul>
	 * @exception IllegalStateException if the current display has no shells
	 */
	public Frame createDialogParentFrame() {
		if ( display != SwtGui.getDisplay() ) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}
		final Shell parent = display.getActiveShell();
		if ( parent == null ) { throw new IllegalStateException("No Active Shell"); }
		return createDialogParentFrame(parent);
	}

	/**
	 * Creates an AWT frame suitable as a parent for AWT/Swing dialogs.
	 * <p>
	 * This method must be called from the SWT event thread. There must be an active shell associated with the
	 * environment's display.
	 * <p>
	 * The created frame is a non-visible child of the given shell and will be disposed when that shell is disposed.
	 * <p>
	 * This method is useful for creating a frame to parent any AWT/Swing dialogs created for use inside a SWT
	 * application. A modal AWT/Swing dialogs will flicker less if its parent is set to the returned frame rather than
	 * to null or to an independently created {@link java.awt.Frame}.
	 * 
	 * @param parent the parent
	 * 
	 * @return a {@link java.awt.Frame} to be used for parenting dialogs
	 * 
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the SWT event thread
	 *                </ul>
	 * @exception IllegalStateException if the current display has no shells
	 */
	private Frame createDialogParentFrame(final Shell parent) {
		if ( parent == null ) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if ( display != SwtGui.getDisplay() ) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}
		final Shell shell = new Shell(parent);
		shell.setVisible(false);
		final Composite composite = new Composite(shell, SWT.EMBEDDED);
		return SWT_AWT.new_Frame(composite);
	}

	// Find a shell to use, giving preference to the active shell.
	/**
	 * Gets the shell.
	 * 
	 * @return the shell
	 */
	Shell getShell() {
		Shell shell = display.getActiveShell();
		if ( shell == null ) {
			final Shell[] allShells = display.getShells();
			if ( allShells.length > 0 ) {
				shell = allShells[0];
			}
		}
		return shell;
	}

	/**
	 * Request awt dialog focus.
	 */
	void requestAwtDialogFocus() {
		assert dialogListener != null;

		dialogListener.requestFocus();
	}

	/**
	 * Sets the system look and feel.
	 */
	private void setSystemLookAndFeel() {
		assert EventQueue.isDispatchThread(); // On AWT event thread

		if ( !isLookAndFeelInitialized ) {
			isLookAndFeelInitialized = true;
			try {
				String systemLaf = UIManager.getSystemLookAndFeelClassName();
				final String xplatLaf = UIManager.getCrossPlatformLookAndFeelClassName();

				// Java makes metal the system look and feel if running under a
				// non-gnome Linux desktop. Fix that here, if the RCP itself is
				// running
				// with the GTK windowing system set.
				if ( xplatLaf.equals(systemLaf) && Platform.isGtk() ) {
					systemLaf = GTK_LOOK_AND_FEEL_NAME;
				}
				UIManager.setLookAndFeel(systemLaf);
			} catch (final ClassNotFoundException e) {

				e.printStackTrace();
			} catch (final InstantiationException e) {

				e.printStackTrace();
			} catch (final IllegalAccessException e) {

				e.printStackTrace();
			} catch (final UnsupportedLookAndFeelException e) {

				e.printStackTrace();
			}
		}
	}

	static void reset() {
		instance = null;
	}

	public static void setKeyEventsDispatcher() {

		// TODO : HACK
		// Allow to short-circuit the AWT KeyEventDispatcher and directly calls the corresponding
		// commands in SWT.
		// Needs to be generalized to find the command corresponding to the keybinding or (even
		// better) to send the keystroke to the SWT event loop.
		// TODO : HACK

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(final KeyEvent e) {
				if ( e.getID() == KeyEvent.KEY_PRESSED && e.isMetaDown() ) {
					IHandlerService service =
						(IHandlerService) SwtGui.getWindow().getWorkbench().getService(IHandlerService.class);
					Character c = e.getKeyChar();
					if ( c == 'p' ) {
						String command =
							GAMA.isPaused() ? "msi.gama.application.commands.PlaySimulation"
								: "msi.gama.application.commands.PauseSimulation";

						try {
							service.executeCommand(command, null);
						} catch (Exception e1) {
							e1.printStackTrace();
						}

					} else if ( c == 'P' ) {
						try {
							service.executeCommand("msi.gama.application.commands.StepByStepSimulation", null);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else if ( c == 'R' ) {
						try {
							service.executeCommand("msi.gama.application.commands.ReloadSimulation", null);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				return false;
			}

		});
	}

}
