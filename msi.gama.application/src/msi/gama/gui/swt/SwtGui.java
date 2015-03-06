/*********************************************************************************************
 * 
 * 
 * 'SwtGui.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt;

import gnu.trove.map.hash.THashMap;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import msi.gama.common.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.controls.StatusControlContribution;
import msi.gama.gui.swt.dialogs.ExceptionDetailsDialog;
import msi.gama.gui.swt.swing.OutputSynchronizer;
import msi.gama.gui.viewers.html.HtmlViewer;
import msi.gama.gui.views.*;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.compilation.GamaClassLoader;
import msi.gaml.types.IType;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Written by drogoul Modified on 6 mai 2011
 * 
 * @todo Description
 * 
 */
public class SwtGui implements IGui {

	private IAgent highlightedAgent;
	public static boolean MOUSE_DOWN;

	static {
		if ( !GuiUtils.isInHeadLessMode() ) {
			GuiUtils.setSwtGui(new SwtGui());
		} else {
			System.out.println("Configuring HEADLESS MODE");
		}
	}

	// protected SwtGui() {
	// getDisplay().addFilter(SWT.MouseDown, new Listener() {
	//
	// @Override
	// public void handleEvent(final Event event) {
	// MOUSE_DOWN = true;
	// }
	// });
	// getDisplay().addFilter(SWT.MouseUp, new Listener() {
	//
	// @Override
	// public void handleEvent(final Event event) {
	// MOUSE_DOWN = false;
	// }
	// });
	// }

	private static Font expandFont;
	// private static Font bigFont;
	private static Font smallFont;
	private static Font smallNavigFont;
	private static Font smallNavigLinkFont;
	private static Font labelFont;
	private static Font navigRegularFont;
	private static Font navigFileFont;
	private static Font parameterEditorsFont;
	private static Font navigResourceFont;
	private static Font navigHeaderFont;
	public static final String PERSPECTIVE_MODELING_ID = "msi.gama.application.perspectives.ModelingPerspective";
	public static final String PERSPECTIVE_SIMULATION_ID = "msi.gama.application.perspectives.SimulationPerspective";
	public static final String PERSPECTIVE_HPC_ID = "msi.gama.hpc.HPCPerspectiveFactory";
	private static Font unitFont;
	public static final GridData labelData = new GridData(SWT.END, SWT.CENTER, false, false);
	// private static Logger log;
	private static ThreadedStatusUpdater status = new ThreadedStatusUpdater();
	private static ISpeedDisplayer speedStatus;
	private Tell tell = new Tell();
	private Error error = new Error();
	private Views views = new Views();
	private ConsoleView console = null;
	private final StringBuilder consoleBuffer = new StringBuilder(2000);
	private static int dialogReturnCode;
	private static final List<IDisplaySurface> surfaces = new ArrayList();
	private static IPartListener2 partListener;

	public static Label createLeftLabel(final Composite parent, final String title) {
		final Label label = new Label(parent, SWT.NONE | SWT.WRAP);
		GridData d = new GridData(SWT.END, SWT.CENTER, false, true);
		label.setLayoutData(d);
		label.setFont(getLabelfont());
		label.setText(title);
		return label;
	}

	private class Views {

		static final int close1 = -10, /* reset = -9, */update = -7, none = -6;

		class ViewAction implements Runnable {

			int actionId;
			IDisplayOutput output;

			ViewAction(final IDisplayOutput out, final int act) {
				actionId = act;
				output = out;

			}

			@Override
			public void run() {
				if ( actionId == none ) { return; }
				final IWorkbenchPage page = getPage();
				if ( page == null ) { return; } // Closing the workbench
				final IViewReference ref =
					page.findViewReference(output.getViewId(), output.isUnique() ? null : output.getName());
				if ( ref == null ) { return; }
				final IViewPart part = ref.getView(true);
				if ( !(part instanceof IGamaView) ) { return; }
				final IGamaView view = (IGamaView) part;
				switch (actionId) {
					case close1:
						try {
							((IViewPart) view).getSite().getPage().hideView((IViewPart) view);
							// ((IViewPart) view).dispose();
						} catch (final Exception e) {
							e.printStackTrace();
						}
						break;
					case update:
						view.update(output);
						break;
					// case reset:
					// view.reset();
					// break;
					case none:
						break;
					default:
						// if ( view instanceof IToolbarDecoratedView.Pausable ) {
						// ((IToolbarDecoratedView.Pausable) view).setRefreshRate(actionId);
						// }
				}

			}

		}

		void close(final IDisplayOutput out) {
			// debug("View to close : " + out.getViewId() + " : " +
			// (out.isUnique() ? "" : out.getName()));
			run(new ViewAction(out, close1));
		}

		void update(final IDisplayOutput out) {
			run(new ViewAction(out, update));
		}

		// void reset(final IDisplayOutput out) {
		// GUI.run(new ViewAction(out, reset));
		// }

		void refresh(final IDisplayOutput out, final int rate) {
			run(new ViewAction(out, rate));
		}
	}

	class Tell implements Runnable {

		String message;

		public void setMessage(final String mes) {
			message = mes;
			SwtGui.this.run(this);
		}

		@Override
		public void run() {
			MessageDialog.openInformation(getShell(), "Message from model: ", message);
		}
	}

	class Error implements Runnable {

		String message;

		public void setMessage(final String mes) {
			message = mes;
			SwtGui.this.run(this);
		}

		@Override
		public void run() {
			MessageDialog.openError(getShell(), "Error", message);
		}
	}

	// static {
	// log = Logger.getLogger("gama");
	// change the level to disable log display
	// log.setLevel(Level.DEBUG);
	// ALL > TRACE > DEBUG > INFO > WARN > ERROR > FATAL > OFF
	// }

	@Override
	public void debug(final String msg) {
		// System.out.println("[GAMA " + Thread.currentThread().getName() + "] " + msg);
		WorkbenchPlugin.log(msg);
		// log.debug(msg);
	}

	@Override
	public void debug(final Exception e) {
		WorkbenchPlugin.log(e);
	}

	@Override
	public void warn(final String msg) {
		System.err.println("[GAMA " + Thread.currentThread().getName() + "] " + msg);
	}

	@Override
	public boolean confirmClose(final IExperimentPlan exp) {
		if ( !GamaPreferences.CORE_ASK_CLOSING.getValue() ) { return true; }
		return MessageDialog.openQuestion(getShell(), "Close simulation confirmation",
			"Do you want to close experiment '" + exp.getName() + "' of model '" + exp.getModel().getName() + "' ?");
	}

	@Override
	public void setWorkbenchWindowTitle(final String title) {
		run(new Runnable() {

			@Override
			public void run() {
				getWindow().getShell().setText(title);
			}
		});

	}

	@Override
	public void tell(final String msg) {
		if ( tell != null ) {
			tell.setMessage(msg);
		}
	}

	@Override
	public void error(final String err) {
		if ( error != null ) {
			error.setMessage(err);
		}
	}

	@Override
	public void runtimeError(final GamaRuntimeException g) {
		// if ( g != null ) {
		// g.printStackTrace();
		// }
		if ( GamaPreferences.CORE_SHOW_ERRORS.getValue() ) {
			final ErrorView v = (ErrorView) showView(ErrorView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
			if ( v != null ) {
				GuiUtils.asyncRun(new Runnable() {

					@Override
					public void run() {
						v.addNewError(g);
					}
				});
			}
		}
	}

	private void clearErrors() {
		final ErrorView v = (ErrorView) getPage().findView(ErrorView.ID);
		if ( v == null ) { return; }
		run(new Runnable() {

			@Override
			public void run() {
				v.clearErrors();
			}
		});
	}

	@Override
	public void setStatus(final String msg, final int code) {
		status.updateWith(new StatusMessage(msg, code));
	}

	public static void setStatusControl(final StatusControlContribution l) {
		status.setTarget(l);
	}

	private int dialog(final Dialog dialog) {
		run(new Runnable() {

			@Override
			public void run() {
				dialog.setBlockOnOpen(true);
				setReturnCode(dialog.open());
			}
		});
		return dialogReturnCode;
	}

	@Override
	public void raise(final Throwable e) {
		informConsole(e);
		run(new Runnable() {

			@Override
			public void run() {
				final Shell s = getShell();
				if ( s == null ) { return; }
				final ExceptionDetailsDialog d =
					new ExceptionDetailsDialog(getShell(), "Gama", null, e.getMessage(), e);
				dialog(d);
			}
		});
	}

	private static void setReturnCode(final int i) {
		dialogReturnCode = i;
	}

	private void writeToConsole(final String msg) {
		if ( console != null ) {
			console.append(msg);
		} else {
			consoleBuffer.append(msg);
		}
	}

	@Override
	public void debugConsole(final int cycle, final String msg) {
		writeToConsole("(cycle : " + cycle + ") " + msg + sep);
	}

	private static String sep = System.getProperty("line.separator");

	@Override
	public void informConsole(final String msg) {
		writeToConsole(msg + sep);
	}

	public void informConsole(final Throwable e) {
		final StringWriter s = new StringWriter();
		final PrintWriter pw = new PrintWriter(s);
		e.printStackTrace(pw);
	}

	private void eraseConsole(final boolean setToNull) {
		if ( console != null ) {
			run(new Runnable() {

				@Override
				public void run() {
					if ( console != null ) {
						console.setText("");
						if ( setToNull ) {
							console = null;
						}
					}
				}
			});
		}
	}

	@Override
	public void updateViewOf(final IDisplayOutput output) {
		if ( views != null ) {
			views.update(output);
		}
	}

	public void setViewRateOf(final IDisplayOutput output, final int refresh) {
		if ( views != null ) {
			views.refresh(output, refresh);
		}
	}

	@Override
	public void closeViewOf(final IDisplayOutput output) {
		if ( views != null ) {
			views.close(output);
		}
	}

	private Object internalShowView(final String viewId, final String secondaryId, final int code) {
		final Object[] result = new Object[1];
		run(new Runnable() {

			@Override
			public void run() {
				try {
					getPage().zoomOut();
					result[0] = getPage().showView(viewId, secondaryId, code);
				} catch (final PartInitException e) {
					result[0] = e;
				}
			}
		});
		return result[0];
	}

	public static final IEditorInput input = new IEditorInput() {

		@Override
		public Object getAdapter(final Class adapter) {
			return null;
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public IPersistableElement getPersistable() {
			return null;
		}

		@Override
		public String getToolTipText() {
			return "";
		}
	};

	public static boolean isInternetReachable() {

		// AD 11/10/13 : see Issue 679
		// Too many problems with Linux for the moment. Reverse this if a definitive solution is found.
		if ( Platform.getOS().equals(Platform.OS_LINUX) || Platform.getWS().equals(Platform.WS_GTK) ) { return false; }

		try {
			URL url = new URL("http://gama-platform.org");
			// open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			// trying to retrieve data from the source. If there
			// is no connection, this line will fail
			urlConnect.setConnectTimeout(2000);
			Object objData = urlConnect.getContent();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Object showWebEditor(final String url, final String html) {
		if ( url != null && url.contains("http") ) {
			if ( !isInternetReachable() ) { return null; }
		}

		final Object[] result = new Object[1];
		run(new Runnable() {

			@Override
			public void run() {
				try {
					getPage().zoomOut();
					IEditorPart p = getPage().findEditor(input);
					if ( p == null ) {
						result[0] = getPage().openEditor(input, "msi.gama.application.browser", true);
					} else {
						result[0] = p;
					}
				} catch (final PartInitException e) {
					result[0] = e;
				}
			}
		});
		if ( result[0] instanceof HtmlViewer ) {
			HtmlViewer be = (HtmlViewer) result[0];
			if ( url != null ) {
				be.setUrl(url);
			} else if ( html != null ) {
				be.setHtml(html);
			}
		}
		return result[0];

	}

	@Override
	public IGamaView showView(final String viewId, final String secondaryId, final int code) {

		Object o = internalShowView(viewId, secondaryId, code);
		if ( o instanceof IWorkbenchPart ) {
			if ( o instanceof IGamaView ) { return (IGamaView) o; }
			o = GamaRuntimeException.error("Impossible to open view " + viewId);
		}
		if ( o instanceof Exception ) {
			GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.create((Exception) o), false);
		}
		return null;
	}

	public static IPartListener2 getPartListener() {
		if ( partListener == null ) {
			partListener = new GamaPartListener();
		}
		return partListener;
	}

	public void hideMonitorView() {
		final MonitorView m = (MonitorView) hideView(MonitorView.ID);
		if ( m != null ) {
			m.reset();
		}
	}

	public void resetMonitorView() {
		run(new Runnable() {

			@Override
			public void run() {
				final IWorkbenchPage activePage = getPage();
				if ( activePage == null ) { return; } // Closing the workbench
				final IWorkbenchPart part = activePage.findView(MonitorView.ID);
				if ( part != null && part instanceof MonitorView && activePage.isPartVisible(part) ) {
					((MonitorView) part).reset();
				}
			}
		});

	}

	@Override
	public void showConsoleView() {
		console = (ConsoleView) showView(ConsoleView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
		eraseConsole(false);
		if ( consoleBuffer.length() > 0 ) {
			console.append(consoleBuffer.toString());
			consoleBuffer.setLength(0);
		}
	}

	public static class GamaPartListener implements IPartListener2 {

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {
			// IWorkbenchPart part = partRef.getPart(false);
			// if ( part instanceof IGamaView ) {
			// ((IGamaView) part).showToolbars(true);
			// }
		}

		@Override
		public void partClosed(final IWorkbenchPartReference partRef) {
			if ( partRef.getPart(false) instanceof IGamaView ) {
				final IExperimentPlan s = GAMA.getExperiment();
				if ( s == null ) { return; }
				final IOutputManager m = s.getSimulationOutputs();
				if ( m != null ) {
					m.removeOutput(((IGamaView) partRef.getPart(false)).getOutput());
				}
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPartReference partRef) {
			// IWorkbenchPart part = partRef.getPart(false);
			// if ( part instanceof IGamaView ) {
			// ((IGamaView) part).showToolbars(false);
			// }

		}

		@Override
		public void partOpened(final IWorkbenchPartReference partRef) {
			if ( partRef.getPart(false) instanceof LayeredDisplayView ) {
				LayeredDisplayView view = (LayeredDisplayView) partRef.getPart(false);
				surfaces.add(view.getDisplaySurface());
				view.fixSize();
			}
			if ( partRef.getId().equals("org.eclipse.ui.views.ContentOutline") ) {
				IWorkbenchPart part = partRef.getPart(false);
				part.setFocus();
			}

		}

		@Override
		public void partBroughtToTop(final IWorkbenchPartReference part) {}

		/**
		 * Method partHidden()
		 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partHidden(final IWorkbenchPartReference partRef) {}

		/**
		 * Method partVisible()
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {}

		/**
		 * Method partInputChanged()
		 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partInputChanged(final IWorkbenchPartReference partRef) {}
	}

	static void initFonts() {
		FontData baseData = getDisplay().getSystemFont().getFontData()[0];
		String baseFont = baseData.name;
		int baseSize = 11;
		FontData fd = new FontData(baseFont, baseSize, SWT.BOLD);
		labelFont = new Font(Display.getDefault(), fd);
		expandFont = new Font(Display.getDefault(), fd);
		fd = new FontData(baseFont, baseSize, SWT.ITALIC);
		unitFont = new Font(Display.getDefault(), fd);
		smallNavigLinkFont = new Font(Display.getDefault(), fd);
		fd = new FontData(baseFont, baseSize + 1, SWT.BOLD);
		// bigFont = new Font(Display.getDefault(), fd);
		navigHeaderFont = new Font(Display.getDefault(), fd);
		fd = new FontData(baseFont, baseSize, SWT.NORMAL);
		smallFont = new Font(Display.getDefault(), fd);
		smallNavigFont = new Font(Display.getDefault(), fd);
		fd = new FontData(baseFont, baseSize, SWT.NORMAL);
		parameterEditorsFont = new Font(Display.getDefault(), fd);
		navigFileFont = new Font(Display.getDefault(), fd);
		fd = new FontData(baseFont, baseSize, SWT.NORMAL);
		navigRegularFont = new Font(Display.getDefault(), fd);
		fd = new FontData(baseFont, baseSize, SWT.ITALIC);
		navigResourceFont = new Font(Display.getDefault(), fd);
	}

	@Override
	public void asyncRun(final Runnable r) {
		final Display d = getDisplay();
		if ( d != null && !d.isDisposed() ) {
			d.asyncExec(r);
		}
	}

	public static IPerspectiveDescriptor getCurrentPerspective() {
		return getPage(null).getPerspective();
	}

	public static Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	public static IWorkbenchPage getPage() {
		final IWorkbenchWindow w = getWindow();
		if ( w == null ) { return null; }
		final IWorkbenchPage p = w.getActivePage();
		return p;
	}

	public static IWorkbenchPage getPage(final String perspectiveId) {
		IWorkbenchPage p = getPage();
		if ( p == null && perspectiveId != null ) {
			try {
				p = getWindow().openPage(perspectiveId, null);

			} catch (final WorkbenchException e) {
				e.printStackTrace();
			}
		}
		return p;
	}

	public static Shell getShell() {
		return getDisplay().getActiveShell();
	}

	public static IWorkbenchWindow getWindow() {
		final IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if ( w == null ) {
			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if ( windows != null && windows.length > 0 ) { return windows[0]; }
		}
		return w;
	}

	@Override
	public IGamaView hideView(final String id) {
		final IGamaView[] parts = new IGamaView[1];
		run(new Runnable() {

			@Override
			public void run() {
				final IWorkbenchPage activePage = getPage();
				if ( activePage == null ) { return; } // Closing the workbench
				final IWorkbenchPart part = activePage.findView(id);
				if ( part != null && part instanceof IGamaView && activePage.isPartVisible(part) ) {
					activePage.hideView((IViewPart) part);
					parts[0] = (IGamaView) part;
				}
			}
		});
		return parts[0];
	}

	@Override
	public boolean isModelingPerspective() {
		return getCurrentPerspective().getId().equals(PERSPECTIVE_MODELING_ID);
	}

	@Override
	public boolean isSimulationPerspective() {
		return getCurrentPerspective().getId().equals(PERSPECTIVE_SIMULATION_ID);
	}

	@Override
	public final boolean openModelingPerspective() {
		return openPerspective(PERSPECTIVE_MODELING_ID);
	}

	public final boolean openPerspective(final String perspectiveId) {
		loadPerspectives();
		final IWorkbenchPage activePage = getPage(perspectiveId);
		final IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
		final IPerspectiveDescriptor descriptor = reg.findPerspectiveWithId(perspectiveId);
		final IPerspectiveDescriptor currentDescriptor = activePage.getPerspective();

		if ( currentDescriptor != null && currentDescriptor.equals(descriptor) ) { return true; }
		if ( descriptor != null ) {
			run(new Runnable() {

				@Override
				public void run() {
					activePage.setPerspective(descriptor);
					debug("Perspective " + perspectiveId + " open ");

				}
			});
			return true;
		}
		return false;
	}

	public final IPerspectiveDescriptor getActivePerspective() {
		final IWorkbenchPage activePage = getPage();
		final IPerspectiveDescriptor currentDescriptor = activePage.getPerspective();
		return currentDescriptor;

	}

	public final String getActivePerspectiveName() {
		return getActivePerspective().getId();

	}

	static final Map<String, Class> perspectiveClasses = new THashMap();

	public final boolean loadPerspectives() {
		if ( !perspectiveClasses.isEmpty() ) { return true; }

		final IConfigurationElement[] config =
			Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.ui.perspectives");
		for ( final IConfigurationElement e : config ) {
			final String pluginID = e.getAttribute("id");
			final String pluginClass = e.getAttribute("class");
			final String pluginName = e.getContributor().getName();
			// Check if is a gama perspective...
			if ( pluginID.contains("msi.gama") ) {
				final ClassLoader cl = GamaClassLoader.getInstance().addBundle(Platform.getBundle(pluginName));
				try {
					perspectiveClasses.put(pluginID, cl.loadClass(pluginClass));
				} catch (final ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				System.out.println("Gama perspective " + pluginID + " is loaded");
			}
		}

		return false; // openPerspective(I);
	}

	@Override
	public final boolean openSimulationPerspective() {
		return openPerspective(PERSPECTIVE_SIMULATION_ID);
	}

	public final boolean openBatchPerspective() {
		return openPerspective(PERSPECTIVE_HPC_ID);
	}

	String currentPerspectiveId = null;

	@Override
	public void run(final Runnable r) {
		final Display d = getDisplay();
		if ( d != null && !d.isDisposed() ) {
			d.syncExec(r);
		}
	}

	@Override
	public void togglePerspective() {
		if ( isSimulationPerspective() ) {
			openModelingPerspective();
			// } else if ( isModelingPerspective() ) {
			// openHeadlessPerspective();
		} else {
			openSimulationPerspective();
		}
	}

	/**
	 * @see msi.gama.common.interfaces.IGui#getEditorFactory()
	 */
	@Override
	public IEditorFactory getEditorFactory() {
		return EditorFactory.getInstance();
	}

	static final Map<String, Class> displayClasses = new THashMap();

	@Override
	public IDisplaySurface getDisplaySurfaceFor(final IScope scope, final String keyword,
		final LayeredDisplayOutput layerDisplayOutput, final double w, final double h, final Object ... args) {

		IDisplaySurface surface = null;
		final IDisplayCreator creator = DISPLAYS.get(keyword);
		if ( creator != null ) {
			surface = creator.create(args);
			surface.initialize(scope, w, h, layerDisplayOutput);
		} else {
			throw GamaRuntimeException.error("Display " + keyword + " is not defined anywhere.", scope);
		}
		return surface;
	}

	@Override
	public Map<String, Object> openUserInputDialog(final String title, final Map<String, Object> initialValues,
		final Map<String, IType> types) {
		final Map<String, Object> result = new THashMap();
		run(new Runnable() {

			@Override
			public void run() {
				final EditorsDialog dialog = new EditorsDialog(getShell(), initialValues, types, title);
				result.putAll(dialog.open() == Window.OK ? dialog.getValues() : initialValues);
			}
		});
		return result;
	}

	public void openUserControlDialog(final IScope scope, final UserPanelStatement panel) {
		run(new Runnable() {

			@Override
			public void run() {
				final UserControlDialog dialog =
					new UserControlDialog(getShell(), panel.getUserCommands(), "[" + scope.getAgentScope().getName() +
						"] " + panel.getName(), scope);
				dialog.open();
			}
		});

	}

	@Override
	public void openUserControlPanel(final IScope scope, final UserPanelStatement panel) {
		run(new Runnable() {

			@Override
			public void run() {
				UserControlView part = null;
				try {
					part = (UserControlView) getPage().showView(UserControlView.ID, null, IWorkbenchPage.VIEW_CREATE);
				} catch (final PartInitException e) {
					e.printStackTrace();
				}
				if ( part != null ) {
					part.initFor(scope, panel.getUserCommands(),
						"[" + scope.getAgentScope().getName() + "] " + panel.getName());
				}
				GAMA.controller.getScheduler().setUserHold(true);
				try {
					getPage().showView(UserControlView.ID);
				} catch (final PartInitException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void closeDialogs() {

		run(new Runnable() {

			@Override
			public void run() {
				final UserControlDialog d = UserControlDialog.current;
				if ( d != null ) {
					d.close();
				}
				hideView(UserControlView.ID);

			}

		});

	}

	@Override
	public IAgent getHighlightedAgent() {
		return highlightedAgent;
	}

	@Override
	public void setHighlightedAgent(final IAgent a) {
		highlightedAgent = a;
	}

	@Override
	public void editModel(final Object eObject) {
		if ( eObject instanceof String ) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IFile file = workspace.getRoot().getFile(new Path((String) eObject));
			editModel(file);
		} else if ( eObject instanceof IFile ) {
			IFile file = (IFile) eObject;
			if ( !file.exists() ) {
				GuiUtils.debug("File " + file.getFullPath().toString() + " does not exist in the workspace");
				return;
			}
			try {
				IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
				getPage().openEditor(new FileEditorInput(file), desc.getId());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void updateParameterView(final IExperimentPlan exp) {

		run(new Runnable() {

			@Override
			public void run() {
				if ( exp.getParametersEditors() == null && exp.getUserCommands().isEmpty() ) { return; }
				try {
					final ExperimentParametersView view =
						(ExperimentParametersView) getPage().showView(ExperimentParametersView.ID, null,
							IWorkbenchPage.VIEW_VISIBLE);
					// if ( view.getExperiment() != exp ) {
					view.addItem(exp);
					// }
					view.updateItemValues();
				} catch (final PartInitException e) {
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public void showParameterView(final IExperimentPlan exp) {

		run(new Runnable() {

			@Override
			public void run() {
				if ( exp.getParametersEditors() == null && exp.getUserCommands().isEmpty() ) { return; }
				try {
					final ExperimentParametersView view =
						(ExperimentParametersView) getPage().showView(ExperimentParametersView.ID, null,
							IWorkbenchPage.VIEW_VISIBLE);
					view.addItem(exp);
				} catch (final PartInitException e) {
					e.printStackTrace();
				}
			}

		});
	}

	public static Font getLabelfont() {
		if ( labelFont == null ) {
			initFonts();
		}
		return labelFont;
	}

	// public static Font getBigfont() {
	// if ( bigFont == null ) {
	// initFonts();
	// }
	// return bigFont;
	// }

	public static Font getSmallFont() {
		if ( smallFont == null ) {
			initFonts();
		}
		return smallFont;
	}

	public static Font getExpandfont() {
		if ( expandFont == null ) {
			initFonts();
		}
		return expandFont;
	}

	public static Font getParameterEditorsFont() {
		if ( parameterEditorsFont == null ) {
			initFonts();
		}
		return parameterEditorsFont;
	}

	public static Font getNavigFolderFont() {
		if ( navigRegularFont == null ) {
			initFonts();
		}
		return navigRegularFont;
	}

	public static Font getNavigLinkFont() {
		if ( smallNavigLinkFont == null ) {
			initFonts();
		}
		return smallNavigLinkFont;
	}

	public static Font getNavigFileFont() {
		if ( navigFileFont == null ) {
			initFonts();
		}
		return navigFileFont;
	}

	public static Font getNavigSmallFont() {
		if ( smallNavigFont == null ) {
			initFonts();
		}
		return smallNavigFont;
	}

	public static Font getNavigHeaderFont() {
		if ( navigHeaderFont == null ) {
			initFonts();
		}
		return navigHeaderFont;
	}

	public static Font getResourceFont() {
		if ( navigResourceFont == null ) {
			initFonts();
		}
		return navigResourceFont;
	}

	public static Font getUnitFont() {
		if ( unitFont == null ) {
			initFonts();
		}
		return unitFont;
	}

	// public static Color getOkColor() {
	// if ( COLOR_OK == null ) {
	// initColors();
	// }
	// return COLOR_OK;
	//
	// }
	//
	// public static Color getOkColorInactive() {
	// if ( COLOR_OK_INACTIVE == null ) {
	// initColors();
	// }
	// return COLOR_OK_INACTIVE;
	// }
	//
	// public static Color getImportedErrorColor() {
	// if ( COLOR_IMPORTED == null ) {
	// initColors();
	// }
	// return COLOR_IMPORTED;
	// }
	//
	// public static Color getErrorColor() {
	// if ( COLOR_ERROR == null ) {
	// initColors();
	// }
	// return COLOR_ERROR;
	// }
	//
	// public static Color getWarningColor() {
	// if ( COLOR_WARNING == null ) {
	// initColors();
	// }
	// return COLOR_WARNING;
	// }
	//
	// public static Color getNeutralColor() {
	// if ( COLOR_NEUTRAL == null ) {
	// initColors();
	// }
	// return COLOR_NEUTRAL;
	// }
	//
	// public static Color getNeutralColorInactive() {
	// if ( COLOR_NEUTRAL_INACTIVE == null ) {
	// initColors();
	// }
	// return COLOR_NEUTRAL_INACTIVE;
	// }
	//
	// public static Color getDarkGrayColor() {
	// if ( COLOR_GRAY_LABEL == null ) {
	// initColors();
	// }
	// return COLOR_GRAY_LABEL;
	// }

	// @Override
	// public void cycleDisplayViews(final Set<String> names) {
	// final Set<String> names2 = new HashSet(names);
	// run(new Runnable() {
	//
	// @Override
	// public void run() {
	// for ( final String name : names2 ) {
	// final IViewReference r = getPage().findViewReference(GuiUtils.LAYER_VIEW_ID, name);
	// if ( r != null ) {
	// final IViewPart p = r.getView(false);
	// // GuiUtils.debug("SwtGui.cycleDisplayViews().bringToTop: " + name);
	// getPage().activate(p);
	// }
	// }
	//
	// }
	// });

	// }

	/**
	 * Method setSelectedAgent()
	 * @see msi.gama.common.interfaces.IGui#setSelectedAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void setSelectedAgent(final IAgent a) {
		asyncRun(new Runnable() {

			@Override
			public void run() {
				final IViewReference r = getPage().findViewReference(GuiUtils.AGENT_VIEW_ID, "");
				if ( r == null ) {
					if ( a == null ) { return; }
					try {
						new InspectDisplayOutput("Inspector", InspectDisplayOutput.INSPECT_AGENT).launch();
					} catch (final GamaRuntimeException g) {
						g.addContext("In opening the agent inspector");
						GAMA.reportError(GAMA.getRuntimeScope(), g, false);
					}
				}
				AgentInspectView v =
					(AgentInspectView) showView(GuiUtils.AGENT_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
				v.inspectAgent(a);
			}
		});
	}

	@Override
	public void prepareForSimulation(final SimulationAgent agent) {
		clearErrors();
		if ( !agent.getExperiment().getSpecies().isBatch() ) {
			showConsoleView();
			resetMonitorView();
		} else {
			if ( console == null ) {
				showConsoleView();
			}
		}
	}

	@Override
	public void prepareForExperiment(final IExperimentPlan exp) {
		if ( exp.isGui() ) {
			// showConsoleView();
			setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
			updateParameterView(exp);
			tell = new Tell();
			error = new Error();
			views = new Views();
			// OutputSynchronizer.waitForViewsToBeClosed();
			// hqnghi:
			// TODO in case of multi controllers, open an experiment cause "closing-reopen" many times displays,
			// TODO so waitForViewsToBeClosed only with mono controller
			if ( GAMA.getControllers().size() == 0 ) {
				OutputSynchronizer.waitForViewsToBeClosed();
			}
			// end-hqnghi
		} else {
			status = null;
		}
	}

	/**
	 * Method cleanAfterExperiment()
	 * @see msi.gama.common.interfaces.IGui#cleanAfterExperiment(msi.gama.kernel.experiment.IExperimentPlan)
	 */
	@Override
	public void cleanAfterExperiment(final IExperimentPlan exp) {
		// setSelectedAgent(null);
		// setHighlightedAgent(null);
		hideView(GuiUtils.PARAMETER_VIEW_ID);
		hideMonitorView();
		eraseConsole(true);
	}

	/**
	 * Method cleanAfterSimulation()
	 * @see msi.gama.common.interfaces.IGui#cleanAfterSimulation()
	 */
	@Override
	public void cleanAfterSimulation() {
		setSelectedAgent(null);
		setHighlightedAgent(null);
		surfaces.clear();
		// clearErrors();
		// hideMonitorView();
		// eraseConsole(true);

	}

	/**
	 * Method waitForViewsToBeInitialized()
	 * @see msi.gama.common.interfaces.IGui#waitForViewsToBeInitialized()
	 */
	@Override
	public void waitForViewsToBeInitialized() {
		OutputSynchronizer.waitForViewsToBeInitialized();
	}

	@Override
	public void runModel(final Object object, final String exp) throws CoreException {
		error("Impossible to run the model. The XText environment has not been launched");
	}

	/**
	 * Method getFirstDisplaySurface()
	 * @see msi.gama.common.interfaces.IGui#getFirstDisplaySurface()
	 */
	public static IDisplaySurface getFirstDisplaySurface() {
		if ( surfaces.isEmpty() ) { return null; }
		if ( surfaces.size() > 1 ) { return null; }
		return surfaces.get(0);
	}

	public static void setSpeedControl(final ISpeedDisplayer d) {
		speedStatus = d;
	}

	/**
	 * Method updateSpeedDisplay()
	 * @see msi.gama.common.interfaces.IGui#updateSpeedDisplay(java.lang.Double)
	 */
	@Override
	public void updateSpeedDisplay(final Double d, final boolean notify) {
		if ( speedStatus != null ) {
			asyncRun(new Runnable() {

				@Override
				public void run() {
					speedStatus.setInit(d, notify);
				}
			});

		}
	}

	/**
	 * Method setStatus()
	 * @see msi.gama.common.interfaces.IGui#setStatus(java.lang.String)
	 */
	@Override
	public void setSubStatusCompletion(final double s) {
		status.updateWith(new SubTaskMessage(s));
	}

	/**
	 * Method beginSubStatus()
	 * @see msi.gama.common.interfaces.IGui#beginSubStatus(java.lang.String)
	 */
	@Override
	public void beginSubStatus(final String name) {
		status.updateWith(new SubTaskMessage(name, true));
	}

	/**
	 * Method endSubStatus()
	 * @see msi.gama.common.interfaces.IGui#endSubStatus(java.lang.String)
	 */
	@Override
	public void endSubStatus(final String name) {
		status.updateWith(new SubTaskMessage(name, false));
	}

	/**
	 * Method getName()
	 * @see msi.gama.common.interfaces.IGui#getName()
	 */
	@Override
	public String getName() {
		return "SWT-based UI";
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchPage page = getPage();
		if ( page != null ) { return page.getActiveEditor(); }
		return null;
	}

	public static IWorkbenchPart getActivePart() {
		IWorkbenchPage page = getPage();
		if ( page != null ) { return page.getActivePart(); }
		return null;
	}

	/**
	 * Adapt the specific object to the specified class, supporting the
	 * IAdaptable interface as well.
	 */
	public static <T> T adaptTo(final Object o, final Class<T> cl) {
		return adaptTo(o, cl, cl);
	}

	/**
	 * Adapt the specific object to the specified classes, supporting the
	 * IAdaptable interface as well.
	 * 
	 * @param o
	 *            the object.
	 * @param actualType
	 *            the actual type that must be returned.
	 * @param adapterType
	 *            the adapter type to check for.
	 */
	public static <T> T adaptTo(Object o, final Class<T> actualType, final Class<?> adapterType) {
		if ( actualType.isInstance(o) ) {
			return actualType.cast(o);
		} else if ( o instanceof IAdaptable ) {
			o = ((IAdaptable) o).getAdapter(adapterType);
			if ( actualType.isInstance(o) ) { return actualType.cast(o); }
		}
		return null;
	}

}
