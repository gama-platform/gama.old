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
package msi.gama.gui.swt;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.controls.StatusControlContribution;
import msi.gama.gui.swt.dialogs.ExceptionDetailsDialog;
import msi.gama.gui.swt.swing.OutputSynchronizer;
import msi.gama.gui.views.*;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.compilation.GamaClassLoader;
import msi.gaml.types.IType;
import org.apache.log4j.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

/**
 * Written by drogoul Modified on 6 mai 2011
 * 
 * @todo Description
 * 
 */
public class SwtGui implements IGui {

	private IAgent highlightedAgent;

	static {
		if ( !GuiUtils.isInHeadLessMode() ) {
			System.out.println("Configuring user interface access through SWT");
			GuiUtils.setSwtGui(new SwtGui());
		} else {
			System.out.println("Configuring HEADLESS MODE");
		}
	}

	protected SwtGui() {}

	public static final Color COLOR_ERROR = new Color(Display.getDefault(), 0xF4, 0x00, 0x15);
	public static final Color COLOR_OK = new Color(Display.getDefault(), 0x55, 0x8E, 0x1B);
	public static final Color COLOR_WARNING = new Color(Display.getDefault(), 0xFD, 0xA6, 0x00);
	public static final Color COLOR_NEUTRAL = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	private static Font expandFont;
	private static Font bigFont;
	private static Font smallFont;
	private static Font labelFont;
	public static final String PERSPECTIVE_MODELING_ID = "msi.gama.application.perspectives.ModelingPerspective";
	public static final String PERSPECTIVE_SIMULATION_ID = "msi.gama.application.perspectives.SimulationPerspective";
	public static final String PERSPECTIVE_HPC_ID = "msi.gama.hpc.HPCPerspectiveFactory";
	private static Font unitFont;
	public static final GridData labelData = new GridData(SWT.END, SWT.CENTER, false, false);
	private static Logger log;
	private static Status status = new Status();
	private Tell tell = new Tell();
	private Error error = new Error();
	private Views views = new Views();
	private static ConsoleView console = null;
	private static final StringBuilder consoleBuffer = new StringBuilder(2000);
	private static int dialogReturnCode;

	public static Label createLeftLabel(final Composite parent, final String title) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
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
						view.setRefreshRate(actionId);
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

	static class Status implements Runnable {

		Thread runThread;
		final BlockingQueue<Message> messages;
		private volatile StatusControlContribution control;

		private class Message {

			String message = "";
			// Color color = COLOR_OK;
			int code = IGui.INFORM;

			Message(final String msg, final int s) {
				message = msg;
				// color = c;
				code = s;
			}
		}

		Status() {
			messages = new LinkedBlockingQueue(4);
		}

		public void setMessage(final String s, final int status) {
			messages.offer(new Message(s, status));
		}

		@Override
		public void run() {
			while (true) {

				try {
					final Message m = messages.take();
					if ( m == null || m.message == null ) { return; }
					GuiUtils.run(new Runnable() {

						@Override
						public void run() {
							if ( !control.isDisposed() ) {
								control.setText(m.message, m.code);
							}
						}
					});
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

		public void setControl(final StatusControlContribution l) {
			control = l;
			if ( runThread == null ) {
				runThread = new Thread(this, "Status thread");
				runThread.start();
			}
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

	static {
		log = Logger.getLogger("gama");
		// change the level to disable log display
		log.setLevel(Level.DEBUG);
		// ALL > TRACE > DEBUG > INFO > WARN > ERROR > FATAL > OFF
	}

	@Override
	public void debug(final String msg) {
		log.debug(msg);
	}

	@Override
	public void warn(final String msg) {
		log.warn(msg);
	}

	@Override
	public boolean confirmClose(final IExperimentSpecies exp) {
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
		status.setMessage(msg, code);
	}

	public static void setStatusControl(final StatusControlContribution l) {
		status.setControl(l);
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

	private static void writeToConsole(final String msg) {
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
					result[0] = getPage().showView(viewId, secondaryId, code);
				} catch (final PartInitException e) {
					result[0] = e;
				}
			}
		});
		return result[0];
	}

	@Override
	public IGamaView showView(final String viewId, final String secondaryId, final int code) {

		Object o = internalShowView(viewId, secondaryId, code);
		if ( o instanceof IWorkbenchPart ) {
			IPartService ps = (IPartService) ((IWorkbenchPart) o).getSite().getService(IPartService.class);
			ps.addPartListener(SwtGui.getPartListener());
			if ( o instanceof IGamaView ) { return (IGamaView) o; }
			o = GamaRuntimeException.error("Impossible to open view " + viewId);
		}
		if ( o instanceof Exception ) {
			GAMA.reportError(GamaRuntimeException.create((Exception) o), false);
		}
		return null;
	}

	private static IPartListener partListener;

	private static IPartListener getPartListener() {
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

	public static class GamaPartListener implements IPartListener {

		// TODO implement IPartListener2 to be notified when views are hidden
		@Override
		public void partActivated(final IWorkbenchPart partRef) {}

		@Override
		public void partClosed(final IWorkbenchPart partRef) {
			if ( partRef instanceof IGamaView ) {
				final IExperimentSpecies s = GAMA.getExperiment();
				if ( s == null ) { return; }
				final IOutputManager m = s.getSimulationOutputs();
				if ( m != null ) {
					m.removeOutput(((IGamaView) partRef).getOutput());
				}
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPart partRef) {}

		@Override
		public void partOpened(final IWorkbenchPart partRef) {
			if ( partRef instanceof LayeredDisplayView ) {
				LayeredDisplayView view = (LayeredDisplayView) partRef;
				view.fixSize();
			}
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {}
	}

	static void initFonts() {
		final FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setStyle(1 << 0 /* SWT.BOLD */);
		labelFont = new Font(Display.getDefault(), fd);
		expandFont = new Font(Display.getDefault(), fd);
		fd.setStyle(1 << 1 /* SWT.ITALIC */);
		unitFont = new Font(Display.getDefault(), fd);
		fd.setStyle(1 << 0 /* SWT.BOLD */);
		fd.setHeight(14);
		fd.setName("Helvetica");
		bigFont = new Font(Display.getDefault(), fd);
		fd.setStyle(SWT.NORMAL);
		fd.setHeight(10);
		fd.setName("Geneva");
		smallFont = new Font(Display.getDefault(), fd);
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

	static final Map<String, Class> perspectiveClasses = new HashMap();

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

	public final boolean changePerspective() {
		System.out.println("change perspective " + this.currentPerspectiveId);
		if ( currentPerspectiveId == PERSPECTIVE_SIMULATION_ID ) {
			this.currentPerspectiveId = PERSPECTIVE_HPC_ID;
			return openPerspective(PERSPECTIVE_HPC_ID);
		} else {
			if ( currentPerspectiveId == PERSPECTIVE_MODELING_ID ) {
				this.currentPerspectiveId = PERSPECTIVE_SIMULATION_ID;
				return openPerspective(PERSPECTIVE_SIMULATION_ID);
			} else {
				this.currentPerspectiveId = PERSPECTIVE_MODELING_ID;
				return openPerspective(PERSPECTIVE_MODELING_ID);
			}
		}
	}

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

	static final Map<String, Class> displayClasses = new HashMap();

	@Override
	public IDisplaySurface getDisplaySurfaceFor(final String keyword, final LayeredDisplayOutput layerDisplayOutput,
		final double w, final double h, final Object ... args) {

		IDisplaySurface surface = null;
		final IDisplayCreator creator = displays.get(keyword);
		if ( creator != null ) {
			surface = creator.create(args);
			surface.initialize(w, h, layerDisplayOutput);
		} else {
			throw GamaRuntimeException.error("Display " + keyword + " is not defined anywhere.");
		}
		return surface;
	}

	@Override
	public Map<String, Object> openUserInputDialog(final String title, final Map<String, Object> initialValues,
		final Map<String, IType> types) {
		final Map<String, Object> result = new HashMap();
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
	public void openEditorAndSelect(final Object eObject) {}

	@Override
	public void updateParameterView(final IExperimentSpecies exp) {

		run(new Runnable() {

			@Override
			public void run() {
				if ( exp.getParametersEditors() == null ) { return; }
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
	public void showParameterView(final IExperimentSpecies exp) {

		run(new Runnable() {

			@Override
			public void run() {
				if ( exp.getParametersEditors() == null ) { return; }
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

	public static Font getBigfont() {
		if ( bigFont == null ) {
			initFonts();
		}
		return bigFont;
	}

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

	public static Font getUnitFont() {
		if ( unitFont == null ) {
			initFonts();
		}
		return unitFont;
	}

	@Override
	public void cycleDisplayViews(final Set<String> names) {
		final Set<String> names2 = new HashSet(names);
		run(new Runnable() {

			@Override
			public void run() {
				for ( final String name : names2 ) {
					final IViewReference r = getPage().findViewReference(GuiUtils.LAYER_VIEW_ID, name);
					if ( r != null ) {
						final IViewPart p = r.getView(false);
						// GuiUtils.debug("SwtGui.cycleDisplayViews().bringToTop: " + name);
						getPage().activate(p);
					}
				}

			}
		});

	}

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
						new InspectDisplayOutput("Agent inspector", InspectDisplayOutput.INSPECT_AGENT).launch();
					} catch (final GamaRuntimeException g) {
						g.addContext("In opening the agent inspector");
						GAMA.reportError(g, false);
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
	public void prepareForExperiment(final IExperimentSpecies exp) {
		if ( exp.isGui() ) {
			// showConsoleView();
			setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
			updateParameterView(exp);
			tell = new Tell();
			error = new Error();
			views = new Views();
			OutputSynchronizer.waitForViewsToBeClosed();
		} else {
			status = null;
		}
	}

	/**
	 * Method cleanAfterExperiment()
	 * @see msi.gama.common.interfaces.IGui#cleanAfterExperiment(msi.gama.kernel.experiment.IExperimentSpecies)
	 */
	@Override
	public void cleanAfterExperiment(final IExperimentSpecies exp) {
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

}
