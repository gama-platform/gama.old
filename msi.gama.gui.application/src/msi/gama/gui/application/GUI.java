/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application;

import java.io.*;
import java.util.concurrent.*;
import msi.gama.gui.application.views.*;
import msi.gama.gui.util.ExceptionDetailsDialog;
import msi.gama.gui.util.events.GamaSelectionListener;
import msi.gama.interfaces.IDisplayOutput;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.outputs.OutputManager;
import org.apache.log4j.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;

/**
 * Written by drogoul Modified on 6 mai 2011
 * 
 * @todo Description
 * 
 */
public class GUI {

	public static final Color COLOR_OK = new Color(Display.getDefault(), 0x55, 0x8E, 0x1B);
	public static final Color COLOR_WARNING = new Color(Display.getDefault(), 0xFD, 0xA6, 0x00);
	public static final Color COLOR_ERROR = new Color(Display.getDefault(), 0xF4, 0x00, 0x15);
	public static final Color normal_bg = Display.getDefault().getSystemColor(
		SWT.COLOR_WIDGET_BACKGROUND);
	public static final Color changed_bg = Display.getDefault().getSystemColor(
		SWT.COLOR_INFO_BACKGROUND);
	public static final GridData labelData = new GridData(SWT.END, SWT.CENTER, false, false);

	public static Image speciesImage = ImageDescriptor.createFromFile(GUI.class,
		"/icons/display_species.png").createImage();
	public static Image agentImage = ImageDescriptor.createFromFile(GUI.class,
		"/icons/display_agents.png").createImage();
	public static Image editImage = ImageDescriptor.createFromFile(GUI.class,
		"/icons/button_edit.png").createImage();
	public static Image experimentMenuImage = ImageDescriptor.createFromFile(GUI.class,
		"/icons/menu_run.png").createImage();
	public static Image noExperimentImage = ImageDescriptor.createFromFile(GUI.class,
		"/icons/menu_experiment_error.png").createImage();
	public static Image expand = AbstractUIPlugin.imageDescriptorFromPlugin(
		"msi.gama.gui.application", "/icons/small_button_plus.png").createImage();
	public static Image collapse = AbstractUIPlugin.imageDescriptorFromPlugin(
		"msi.gama.gui.application", "/icons/small_button_minus.png").createImage();
	public static Image close = AbstractUIPlugin.imageDescriptorFromPlugin(
		"msi.gama.gui.application", "/icons/small_button_close.png").createImage();
	public static Image pause = AbstractUIPlugin.imageDescriptorFromPlugin(
		"msi.gama.gui.application", "/icons/small_button_pause.png").createImage();
	public static Image play = AbstractUIPlugin.imageDescriptorFromPlugin(
		"msi.gama.gui.application", "/icons/small_button_play.png").createImage();
	public static Image lock = AbstractUIPlugin.imageDescriptorFromPlugin(
		"msi.gama.gui.application", "/icons/small_button_lock.png").createImage();
	public static Image unlock = AbstractUIPlugin.imageDescriptorFromPlugin(
		"msi.gama.gui.application", "/icons/small_button_unlock.png").createImage();

	private static Logger log;
	private static Status status = new Status();
	private static Tell tell = null;
	private static Error error = null;
	private static Views views = null;
	private static ConsoleView console = null;
	private static final StringBuilder consoleBuffer = new StringBuilder();
	private static int dialogReturnCode;
	public static final Font labelFont;
	public static final Font expandFont;
	public static final Font unitFont;

	static {
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setStyle(SWT.BOLD);
		labelFont = new Font(Display.getDefault(), fd);
		expandFont = new Font(Display.getDefault(), fd);
		fd.setStyle(SWT.ITALIC);
		unitFont = new Font(Display.getDefault(), fd);

		// fd.setStyle(SWT.ITALIC | SWT.BOLD);
		// fd.setHeight(11);

	}

	/** The Constants perspective ids */
	public static final String PERSPECTIVE_SIMULATION_ID =
		"msi.gama.gui.application.perspectives.SimulationPerspective";
	public static final String PERSPECTIVE_MODELING_ID =
		"msi.gama.gui.application.perspectives.ModelingPerspective";
	public static final String PERSPECTIVE_BATCH_ID =
		"msi.gama.gui.application.perspectives.BatchPerspective";

	public static Label createLeftLabel(final Composite parent, final String title) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setFont(GUI.labelFont);
		label.setText(title);
		return label;
	}

	private static class Views {

		static final int close1 = -10, /* reset = -9, */update = -7, none = -6;

		static class ViewAction implements Runnable {

			int action;
			IDisplayOutput output;

			ViewAction(final IDisplayOutput out, final int act) {
				action = act;
				output = out;

			}

			@Override
			public void run() {
				if ( action == none ) { return; }
				IWorkbenchPage page = getPage();
				if ( page == null ) { return; } // Closing the workbench
				final IViewReference ref =
					page.findViewReference(output.getViewId(),
						output.isUnique() ? null : output.getName());
				if ( ref == null ) { return; }
				final IGamaView view = (IGamaView) ref.getView(true);
				if ( view == null ) { return; }
				switch (action) {
					case close1:
						try {
							view.getSite().getPage().hideView(view);
						} catch (Exception e) {
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
						view.setRefreshRate(action);
				}

			}

		}

		void close(final IDisplayOutput out) {
			// debug("View to close : " + out.getViewId() + " : " +
			// (out.isUnique() ? "" : out.getName()));
			GUI.run(new ViewAction(out, close1));
		}

		void update(final IDisplayOutput out) {
			GUI.run(new ViewAction(out, update));
		}

		// void reset(final IDisplayOutput out) {
		// GUI.run(new ViewAction(out, reset));
		// }

		void refresh(final IDisplayOutput out, final int rate) {
			GUI.run(new ViewAction(out, rate));
		}
	}

	static class Status implements Runnable {

		Thread runThread;
		final BlockingQueue<Message> messages;
		private volatile Label control;

		private static class Message {

			String message = "";
			Color color = COLOR_OK;

			Message(final String s, final Color c) {
				message = s;
				color = c;
			}
		}

		Status() {
			messages = new LinkedBlockingQueue(4);
		}

		public void setMessage(final String s, final Color c) {
			messages.offer(new Message(s, c));
		}

		@Override
		public void run() {
			while (true) {

				try {
					final Message m = messages.take();
					if ( m == null || m.message == null ) { return; }
					GUI.run(new Runnable() {

						@Override
						public void run() {
							if ( !control.isDisposed() ) {
								if ( control.getBackground() != m.color ) {
									control.setBackground(m.color);
								}
								control.setText(m.message);
							}
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

		public void setControl(final Label l) {
			control = l;
			if ( runThread == null ) {
				runThread = new Thread(this, "Status thread");
				runThread.start();
			}
		}
	}

	static class Tell implements Runnable {

		String message;

		public void setMessage(final String mes) {
			message = mes;
			GUI.run(this);
		}

		@Override
		public void run() {
			MessageDialog.openInformation(getShell(), "Message from model: ", message);
		}
	}

	static class Error implements Runnable {

		String message;

		public void setMessage(final String mes) {
			message = mes;
			GUI.run(this);
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

	public static void trace(final String msg) {
		log.trace(msg);
	}

	public static void debug(final String msg) {
		log.debug(msg);
	}

	public static void info(final String msg) {
		log.info(msg);
	}

	public static void warn(final String msg) {
		log.warn(msg);
	}

	public static void setWorkbenchWindowTitle(final String title) {
		getWindow().getShell().setText(title);
	}

	private static final boolean openPerspective(final String perspectiveId) {
		final IWorkbenchPage activePage = getPage(perspectiveId);
		IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
		final IPerspectiveDescriptor descriptor = reg.findPerspectiveWithId(perspectiveId);
		final IPerspectiveDescriptor currentDescriptor = activePage.getPerspective();

		if ( currentDescriptor == descriptor ) { return true; }
		if ( descriptor != null ) {
			run(new Runnable() {

				@Override
				public void run() {
					activePage.setPerspective(descriptor);
				}
			});
			return true;
		}
		return false;
	}

	public static final boolean openModelingPerspective() {
		return openPerspective(PERSPECTIVE_MODELING_ID);
	}

	public static IPerspectiveDescriptor getCurrentPerspective() {
		return getPage(null).getPerspective();
	}

	public static final boolean openSimulationPerspective() {
		return openPerspective(PERSPECTIVE_SIMULATION_ID);
	}

	public static final boolean openBatchPerspective() {
		return openPerspective(PERSPECTIVE_BATCH_ID);
	}

	public static IWorkbenchPage getPage(final String perspectiveId) {
		IWorkbenchPage p = getPage();
		if ( p == null && perspectiveId != null ) {
			try {
				p = getWindow().openPage(perspectiveId, null);

			} catch (WorkbenchException e) {
				e.printStackTrace();
			}
		}
		return p;
	}

	public static IWorkbenchPage getPage() {
		IWorkbenchWindow w = getWindow();
		if ( w == null ) { return null; }
		IWorkbenchPage p = w.getActivePage();
		return p;
	}

	public static IWorkbenchWindow getWindow() {
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if ( w == null ) {
			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if ( windows != null && windows.length > 0 ) { return windows[0]; }
		}
		return w;
	}

	public static Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	public static Shell getShell() {
		return getDisplay().getActiveShell();
	}

	public static boolean isSimulationPerspective() {
		return getCurrentPerspective().getId().equals(PERSPECTIVE_SIMULATION_ID) ? true : false;
	}

	public static boolean isModelingPerspective() {
		return getCurrentPerspective().getId().equals(PERSPECTIVE_MODELING_ID) ? true : false;
	}

	public static boolean isBatchPerspective() {
		return getCurrentPerspective().getId().equals(PERSPECTIVE_BATCH_ID) ? true : false;
	}

	public static boolean isFrontmostDisplayPaused() {
		if ( GAMA.getFrontmostSimulation().isPaused() ) { return true; }
		IWorkbenchPart part = getPage().getActivePart();
		if ( part instanceof IGamaView && ((IGamaView) part).getOutput().isPaused() ) { return true; }
		return false;
	}

	public static void togglePerspective() {
		if ( isSimulationPerspective() || isBatchPerspective() ) {
			openModelingPerspective();
		} else {
			openSimulationPerspective();
		}
	}

	public static void asyncRun(final Runnable r) {
		Display d = getDisplay();
		if ( d != null && !d.isDisposed() ) {
			d.asyncExec(r);
		}
	}

	public static void run(final Runnable r) {
		Display d = getDisplay();
		if ( d != null && !d.isDisposed() ) {
			d.syncExec(r);
		}
	}

	public static void prepareFor(final boolean isGui) {
		if ( isGui ) {
			tell = new Tell();
			error = new Error();
			views = new Views();
		} else {
			status = null;
		}
	}

	public static void tell(final String msg) {
		if ( tell != null ) {
			tell.setMessage(msg);
		}
	}

	public static void error(final String err) {
		if ( error != null ) {
			error.setMessage(err);
		}
	}

	public static void informStatus(final String msg) {
		status.setMessage(msg, COLOR_OK);
	}

	public static void waitStatus(final String msg) {
		status.setMessage(msg, COLOR_WARNING);
	}

	public static void errorStatus(final String msg) {
		status.setMessage(msg, COLOR_ERROR);
	}

	public static void setStatusControl(final Label l) {
		status.setControl(l);
	}

	private static int dialog(final Dialog dialog) {
		run(new Runnable() {

			@Override
			public void run() {
				dialog.setBlockOnOpen(true);
				setReturnCode(dialog.open());
			}
		});
		return dialogReturnCode;
	}

	public static void raise(final Throwable e) {
		informConsole(e);
		run(new Runnable() {

			@Override
			public void run() {
				Shell s = getShell();
				if ( s == null ) { return; }
				final ExceptionDetailsDialog d =
					new ExceptionDetailsDialog(getShell(), "Gama", null, e.getMessage(), e,
						Activator.getDefault());
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

	public static void debugConsole(final long cycle, final String msg) {
		writeToConsole("(cycle : " + cycle + ") " + msg + sep);
	}

	private static String sep = System.getProperty("line.separator");

	public static void informConsole(final String msg) {
		writeToConsole(msg + sep);
	}

	public static void informConsole(final Throwable e) {
		StringWriter s = new StringWriter();
		PrintWriter pw = new PrintWriter(s);
		e.printStackTrace(pw);
	}

	public static void eraseConsole() {
		if ( console != null ) {
			run(new Runnable() {

				@Override
				public void run() {
					if ( console != null ) {
						console.setText("");
					}
				}
			});
		}
	}

	public static void updateViewOf(final IDisplayOutput output) {
		if ( views != null ) {
			views.update(output);
		}
	}

	public static void setViewRateOf(final IDisplayOutput output, final int refresh) {
		if ( views != null ) {
			views.refresh(output, refresh);
		}
	}

	public static void closeViewOf(final IDisplayOutput output) {
		if ( views != null ) {
			views.close(output);
		}
	}

	public static IWorkbenchPart showView(final String viewId, final String secondaryId) {
		final Object[] result = new Object[1];
		GUI.run(new Runnable() {

			@Override
			public void run() {
				try {
					result[0] =
						getPage().showView(viewId, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
				} catch (PartInitException e) {
					result[0] = e;
				}
			}
		});
		Object o = result[0];
		if ( o instanceof IWorkbenchPart ) {
			((IPartService) ((IWorkbenchPart) o).getSite().getService(IPartService.class))
				.addPartListener(new GamaPartListener());
			if ( o instanceof GamaSelectionListener ) {
				GAMA.getExperiment().getOutputManager()
					.addGamaSelectionListener((GamaSelectionListener) o);

			}
			return (IWorkbenchPart) o;
		}
		GAMA.reportError(new GamaRuntimeException((Exception) o));
		return null;
	}

	public static void showParameterView(final IExperiment exp) {
		final ExperimentParametersView view =
			(ExperimentParametersView) showView(ExperimentParametersView.ID, null);
		run(new Runnable() {

			@Override
			public void run() {
				view.addItem(exp);
			}
		});
	}

	public static void hideParameterView() {
		hideView(ExperimentParametersView.ID);
	}

	public static void stopIfCancelled() throws InterruptedException {
		Thread.yield(); // let another thread have some time perhaps to stop this one.
		if ( Thread.currentThread().isInterrupted() ) { throw new InterruptedException("Cancelled"); }
	}

	public static void showMonitorView() {
		showView(MonitorView.ID, null);
	}

	public static ErrorView showErrorView() {
		return (ErrorView) showView(ErrorView.ID, null);
	}

	public static void hideMonitorView() {
		MonitorView m = (MonitorView) hideView(MonitorView.ID);
		if ( m != null ) {
			m.reset();
		}
	}

	public static IWorkbenchPart hideView(final String id) {
		final IWorkbenchPart[] parts = new IWorkbenchPart[1];
		run(new Runnable() {

			@Override
			public void run() {
				IWorkbenchPage activePage = getPage();
				if ( activePage == null ) { return; } // Closing the workbench
				IWorkbenchPart part = activePage.findView(id);
				if ( part != null && activePage.isPartVisible(part) ) {
					activePage.hideView((IViewPart) part);
					parts[0] = part;
				}
			}
		});
		return parts[0];
	}

	public static void showConsoleView() {
		console = (ConsoleView) showView(ConsoleView.ID, null);
		eraseConsole();
		if ( consoleBuffer.length() > 0 ) {
			console.append(consoleBuffer.toString());
			consoleBuffer.setLength(0);
		}
	}

	public static class GamaPartListener implements IPartListener {

		@Override
		public void partActivated(final IWorkbenchPart partRef) {

			ViewSourceProvider state =
				(ViewSourceProvider) ((ISourceProviderService) getWindow().getService(
					ISourceProviderService.class)).getSourceProvider(ViewSourceProvider.var);
			state.changeState();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partClosed(final IWorkbenchPart partRef) {
			// Passe 4 fois dedans !!!

			if ( partRef instanceof IGamaView ) {
				IExperiment s = GAMA.getExperiment();
				if ( s == null ) { return; }
				OutputManager m = s.getOutputManager();
				if ( m != null && partRef instanceof GamaSelectionListener ) {
					m.removeGamaSelectionListener((GamaSelectionListener) partRef);
				}
				// m.unscheduleOutput(((IGamaView) partRef).getOutput());
				// GuiOutputManager g = m.getDisplayOutputManager();
				// if ( g != null ) {
				// g.removeDisplayOutput(((IGamaView) partRef).getOutput());
				// }
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partDeactivated(final IWorkbenchPart partRef) {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partOpened(final IWorkbenchPart partRef) {
			// TODO Auto-generated method stub

		}

		// IContextActivation simulationContext;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
		 */
		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {}
	}

}
