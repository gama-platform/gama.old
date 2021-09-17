/*******************************************************************************************************
 *
 * WorkbenchHelper.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import java.util.function.Consumer;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import msi.gama.application.workspace.WorkspaceModelsManager;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class WorkbenchHelper.
 */
public class WorkbenchHelper {

	/** The Constant NULL. */
	static final Object NULL = new Object();

	/** The Constant SERVICES. */
	public final static LoadingCache<Class<?>, Object> SERVICES =
			CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Object>() {

				@Override
				public Object load(final Class<?> key) throws Exception {
					final Object o = getWorkbench().getService(key);
					if (o == null) return NULL;
					return o;
				}
			});

	/** The Constant GAMA_NATURE. */
	public final static String GAMA_NATURE = WorkspaceModelsManager.GAMA_NATURE; // NO_UCD (unused code)

	/** The Constant XTEXT_NATURE. */
	public final static String XTEXT_NATURE = WorkspaceModelsManager.XTEXT_NATURE; // NO_UCD (unused code)

	/** The Constant PLUGIN_NATURE. */
	public final static String PLUGIN_NATURE = WorkspaceModelsManager.PLUGIN_NATURE;

	/** The Constant TEST_NATURE. */
	public final static String TEST_NATURE = WorkspaceModelsManager.TEST_NATURE;

	/** The Constant BUILTIN_NATURE. */
	public final static String BUILTIN_NATURE = WorkspaceModelsManager.BUILTIN_NATURE;

	/** The clipboard. */
	private static Clipboard CLIPBOARD;

	/** The Constant TRANSFERS. */
	private final static Transfer[] TRANSFERS = { TextTransfer.getInstance() };

	/**
	 * Checks if is display thread.
	 *
	 * @return true, if is display thread
	 */
	public static boolean isDisplayThread() {
		Display d = getDisplay();
		if (d == null) { d = Display.getCurrent(); }
		if (d == null) return false;
		return d.getThread() == Thread.currentThread();
	}

	/**
	 * Gets the clipboard.
	 *
	 * @return the clipboard
	 */
	public static Clipboard getClipboard() {
		if (CLIPBOARD == null) { CLIPBOARD = new Clipboard(getDisplay()); }
		return CLIPBOARD;
	}

	/**
	 * Async run.
	 *
	 * @param r
	 *            the r
	 */
	public static void asyncRun(final Runnable r) {
		final Display d = getDisplay();
		if (d != null && !d.isDisposed()) {
			d.asyncExec(r);
		} else {
			r.run();
		}
	}

	/**
	 * Run.
	 *
	 * @param r
	 *            the r
	 */
	public static void run(final Runnable r) {
		final Display d = getDisplay();
		if (d == null || d.isDisposed() || d.getThread() == Thread.currentThread()) {
			r.run();
		} else {
			d.syncExec(r);
		}
	}

	/**
	 * Gets the display.
	 *
	 * @return the display
	 */
	public static Display getDisplay() { return getWorkbench().getDisplay(); }

	/**
	 * Gets the page.
	 *
	 * @return the page
	 */
	public static IWorkbenchPage getPage() {
		final IWorkbenchWindow w = getWindow();
		if (w == null) return null;
		return w.getActivePage();
	}

	/**
	 * Gets the shell.
	 *
	 * @return the shell
	 */
	public static Shell getShell() {

		return getDisplay().getActiveShell();
	}

	/**
	 * Gets the window.
	 *
	 * @return the window
	 */
	public static WorkbenchWindow getWindow() {
		WorkbenchWindow w = null;
		try {
			w = (WorkbenchWindow) getWorkbench().getActiveWorkbenchWindow();
		} catch (final Exception e) {
			DEBUG.ERR("SWT bug: Window not found ");
		}
		if (w == null) {
			final IWorkbenchWindow[] windows = getWorkbench().getWorkbenchWindows();
			if (windows != null && windows.length > 0) return (WorkbenchWindow) windows[0];
		}
		return w;
	}

	/**
	 * Gets the workbench.
	 *
	 * @return the workbench
	 */
	public static IWorkbench getWorkbench() { return PlatformUI.getWorkbench(); }

	/**
	 * Sets the workbench window title.
	 *
	 * @param title
	 *            the new workbench window title
	 */
	public static void setWorkbenchWindowTitle(final String title) {
		asyncRun(() -> { if (WorkbenchHelper.getShell() != null) { WorkbenchHelper.getShell().setText(title); } });

	}

	/**
	 * Gets the service.
	 *
	 * @param <T>
	 *            the generic type
	 * @param class1
	 *            the class 1
	 * @return the service
	 */
	@SuppressWarnings ("unchecked")
	public static <T> T getService(final Class<T> class1) {
		final Object o = SERVICES.getUnchecked(class1);
		if (o == NULL) {
			SERVICES.invalidate(class1);
			return null;
		}
		return (T) o;
	}

	/**
	 * Copy.
	 *
	 * @param o
	 *            the o
	 */
	public static void copy(final String o) {
		final Runnable r = () -> getClipboard().setContents(new String[] { o }, TRANSFERS);
		// if (isDisplayThread()) {
		// r.run();
		// } else {
		asyncRun(r);
		// }
	}

	/**
	 * Obtain full screen shell.
	 *
	 * @param id
	 *            the id
	 * @return the shell
	 */
	public static Shell obtainFullScreenShell(final int id) {
		final Monitor[] monitors = WorkbenchHelper.getDisplay().getMonitors();
		int monitorId = id;
		if (monitorId < 0) { monitorId = 0; }
		if (monitorId > monitors.length - 1) { monitorId = monitors.length - 1; }
		final Rectangle bounds = monitors[monitorId].getBounds();

		final Shell fullScreenShell = new Shell(WorkbenchHelper.getDisplay(), SWT.NO_TRIM | SWT.ON_TOP);
		fullScreenShell.setBounds(bounds);
		final FillLayout fl = new FillLayout();
		fl.marginHeight = 0;
		fl.marginWidth = 0;
		fl.spacing = 0;
		// final GridLayout gl = new GridLayout(1, true);
		// gl.horizontalSpacing = 0;
		// gl.marginHeight = 0;
		// gl.marginWidth = 0;
		// gl.verticalSpacing = 0;
		fullScreenShell.setLayout(fl);
		return fullScreenShell;
	}

	/**
	 * Display size of.
	 *
	 * @param composite
	 *            the composite
	 * @return the rectangle
	 */
	public static Rectangle displaySizeOf(final Control composite) {
		final Rectangle[] result = new Rectangle[1];
		run(() -> result[0] = getDisplay().map(composite, null, composite.getBounds()));
		return result[0];
	}

	/**
	 * Run command.
	 *
	 * @param string
	 *            the string
	 * @return true, if successful
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static boolean runCommand(final String string) throws ExecutionException {
		return runCommand(string, null);
	}

	/**
	 * Execute command.
	 *
	 * @param string
	 *            the string
	 * @return true, if successful
	 */
	public static boolean executeCommand(final String string) {
		try {
			return runCommand(string, null);
		} catch (final ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Run command.
	 *
	 * @param string
	 *            the string
	 * @param event
	 *            the event
	 * @return true, if successful
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static boolean runCommand(final String string, final Event event) throws ExecutionException {
		final Command c = getCommand(string);
		final IHandlerService handlerService = getService(IHandlerService.class);
		final ExecutionEvent e = handlerService.createExecutionEvent(c, event);
		return runCommand(c, e);
	}

	/**
	 * Run command.
	 *
	 * @param c
	 *            the c
	 * @param event
	 *            the event
	 * @return true, if successful
	 * @throws ExecutionException
	 *             the execution exception
	 */
	public static boolean runCommand(final Command c, final ExecutionEvent event) throws ExecutionException {
		if (c.isEnabled()) {
			try {
				c.executeWithChecks(event);
				return true;
			} catch (NotDefinedException | NotEnabledException | NotHandledException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Gets the command.
	 *
	 * @param string
	 *            the string
	 * @return the command
	 */
	public static Command getCommand(final String string) {
		final ICommandService service = getService(ICommandService.class);
		return service.getCommand(string);
	}

	/**
	 * Run in UI.
	 *
	 * @param title
	 *            the title
	 * @param scheduleTime
	 *            the schedule time
	 * @param run
	 *            the run
	 */
	public static void runInUI(final String title, final int scheduleTime, final Consumer<IProgressMonitor> run) {
		final UIJob job = new UIJob(title) {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {

				run.accept(monitor);
				return Status.OK_STATUS;
			}

		};
		job.schedule(scheduleTime);
	}

	/**
	 * Gets the main toolbar.
	 *
	 * @return the main toolbar
	 */
	public static CoolBar getMainToolbar() {
		CoolBarManager toolbarManager = getWindow().getCoolBarManager();
		return toolbarManager.getControl();
	}

}
