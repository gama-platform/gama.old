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
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.AWTDisplayGraphics;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.controls.StatusControlContribution;
import msi.gama.gui.swt.dialogs.ExceptionDetailsDialog;
import msi.gama.gui.views.*;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaClassLoader;
import org.apache.log4j.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Written by drogoul Modified on 6 mai 2011
 * 
 * @todo Description
 * 
 */
public class SwtGui implements IGui {

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	static {
		System.out.println("Configuring user interface access through SWT");
		GuiUtils.setSwtGui(new SwtGui());
	}

	private SwtGui() {}

	public static final GridData labelData = new GridData(SWT.END, SWT.CENTER, false, false);
	private static Logger log;
	private static Status status = new Status();
	private Tell tell = null;
	private Error error = null;
	private Views views = null;
	private static ConsoleView console = null;
	private static final StringBuilder consoleBuffer = new StringBuilder();
	private static int dialogReturnCode;
	public static Image speciesImage = getImageDescriptor("/icons/display_species.png")
		.createImage();
	public static Image agentImage = getImageDescriptor("/icons/display_agents.png").createImage();
	public static Image editImage = getImageDescriptor("/icons/button_edit.png").createImage();
	public static Image experimentMenuImage = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID,
		"/icons/menu_run.png").createImage();
	public static Image noExperimentImage = getImageDescriptor("/icons/menu_experiment_error.png")
		.createImage();
	public static Image expand = getImageDescriptor("/icons/small_button_plus.png").createImage();
	public static Image collapse = getImageDescriptor("/icons/small_button_minus.png")
		.createImage();
	public static Image thumb = getImageDescriptor("/icons/knobNormal.png").createImage();
	public static Image thumb_over = getImageDescriptor("/icons/knobHover.png").createImage();
	public static Image thumb_blue = getImageDescriptor("/icons/knobNormal_blue.png").createImage();
	public static Image thumb_over_blue = getImageDescriptor("/icons/knobHover_blue.png")
		.createImage();
	public static Image line = getImageDescriptor("/icons/trackFill.png").createImage();
	public static Image line_left = getImageDescriptor("/icons/trackCapLeft.png").createImage();
	public static Image line_right = getImageDescriptor("/icons/trackCapRight.png").createImage();
	public static Image close = getImageDescriptor("/icons/small_button_close.png").createImage();
	public static Image pause = getImageDescriptor("/icons/small_button_pause.png").createImage();
	public static Image play = getImageDescriptor("/icons/small_button_play.png").createImage();
	public static Image lock = getImageDescriptor("/icons/small_button_lock.png").createImage();
	public static Image unlock = getImageDescriptor("/icons/small_button_unlock.png").createImage();

	public static Label createLeftLabel(final Composite parent, final String title) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		label.setFont(labelFont);
		label.setText(title);
		return label;
	}

	private class Views {

		static final int close1 = -10, /* reset = -9, */update = -7, none = -6;

		class ViewAction implements Runnable {

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
				IViewPart part = ref.getView(true);
				if ( !(part instanceof IGamaView) ) { return; }
				final IGamaView view = (IGamaView) part;
				switch (action) {
					case close1:
						try {
							((IViewPart) view).getSite().getPage().hideView((IViewPart) view);
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
				} catch (InterruptedException e) {
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
	public boolean confirmClose(final IExperiment exp) {
		return MessageDialog.openQuestion(getShell(), "Close simulation confirmation",
			"Do you want to close experiment '" + exp.getName() + "' of model '" +
				exp.getModel().getName() + "' ?");
	}

	@Override
	public void setWorkbenchWindowTitle(final String title) {
		getWindow().getShell().setText(title);
	}

	@Override
	public void prepareFor(final boolean isGui) {
		if ( isGui ) {
			tell = new Tell();
			error = new Error();
			views = new Views();
		} else {
			status = null;
		}
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
		g.printStackTrace();
		final ErrorView v = (ErrorView) GuiUtils.showView(ErrorView.ID, null);
		if ( v != null ) {
			GuiUtils.asyncRun(new Runnable() {

				@Override
				public void run() {
					v.addNewError(g);
				}
			});
		}
	}

	@Override
	public void clearErrors() {
		ErrorView v = (ErrorView) getPage().findView(ErrorView.ID);
		if ( v == null ) { return; }
		v.clearErrors();
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
				Shell s = getShell();
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
		StringWriter s = new StringWriter();
		PrintWriter pw = new PrintWriter(s);
		e.printStackTrace(pw);
	}

	private void eraseConsole() {
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

	@Override
	public void showParameterView(final IExperiment exp) {

		run(new Runnable() {

			@Override
			public void run() {
				try {
					ExperimentParametersView view =
						(ExperimentParametersView) getPage().showView(ExperimentParametersView.ID,
							null, IWorkbenchPage.VIEW_VISIBLE);
					view.addItem(exp);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}

		});
	}

	private Object internalShowView(final String viewId, final String secondaryId) {
		final Object[] result = new Object[1];
		run(new Runnable() {

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
		return result[0];
	}

	@Override
	public IGamaView showView(final String viewId, final String secondaryId) {

		Object o = internalShowView(viewId, secondaryId);
		if ( o instanceof IWorkbenchPart ) {
			((IPartService) ((IWorkbenchPart) o).getSite().getService(IPartService.class))
				.addPartListener(new SwtGui.GamaPartListener());
			if ( o instanceof GamaSelectionListener ) {
				GAMA.getExperiment().getOutputManager()
					.addGamaSelectionListener((GamaSelectionListener) o);

			}
			if ( o instanceof IGamaView ) { return (IGamaView) o; }
			o = new GamaRuntimeException("Impossible to open view " + viewId);
		}
		if ( o instanceof Exception ) {
			GAMA.reportError(new GamaRuntimeException((Exception) o));
		}
		return null;
	}

	@Override
	public void stopIfCancelled() throws InterruptedException {
		Thread.yield(); // let another thread have some time perhaps to stop this one.
		if ( Thread.currentThread().isInterrupted() ) { throw new InterruptedException("Cancelled"); }
	}

	@Override
	public void hideMonitorView() {
		MonitorView m = (MonitorView) hideView(MonitorView.ID);
		if ( m != null ) {
			m.reset();
		}
	}

	@Override
	public void showConsoleView() {
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

			// ViewSourceProvider state =
			// (ViewSourceProvider) ((ISourceProviderService) getWindow().getService(
			// ISourceProviderService.class)).getSourceProvider(ViewSourceProvider.var);
			// state.changeState();

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
				IOutputManager m = s.getOutputManager();
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

	public static final Color COLOR_ERROR = new Color(Display.getDefault(), 0xF4, 0x00, 0x15);

	public static final Color COLOR_OK = new Color(Display.getDefault(), 0x55, 0x8E, 0x1B);

	public static final Color COLOR_WARNING = new Color(Display.getDefault(), 0xFD, 0xA6, 0x00);

	public static final Font expandFont;

	public static final Font labelFont;

	public static final String PERSPECTIVE_MODELING_ID =
		"msi.gama.application.perspectives.ModelingPerspective";

	/** The Constants perspective ids */
	public static final String PERSPECTIVE_SIMULATION_ID =
		"msi.gama.application.perspectives.SimulationPerspective";

	public static final Font unitFont;

	static {
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setStyle(1 << 0 /* SWT.BOLD */);
		labelFont = new Font(Display.getDefault(), fd);
		expandFont = new Font(Display.getDefault(), fd);
		fd.setStyle(1 << 1 /* SWT.ITALIC */);
		unitFont = new Font(Display.getDefault(), fd);
	}

	@Override
	public void asyncRun(final Runnable r) {
		Display d = getDisplay();
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
		IWorkbenchWindow w = getWindow();
		if ( w == null ) { return null; }
		IWorkbenchPage p = w.getActivePage();
		return p;
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

	public static Shell getShell() {
		return getDisplay().getActiveShell();
	}

	public static IWorkbenchWindow getWindow() {
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

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
				IWorkbenchPage activePage = getPage();
				if ( activePage == null ) { return; } // Closing the workbench
				IWorkbenchPart part = activePage.findView(id);
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

	private final boolean openPerspective(final String perspectiveId) {
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
					// debug("Perspective " + perspectiveId + " open ");
				}
			});
			return true;
		}
		return false;
	}

	@Override
	public final boolean openSimulationPerspective() {
		return openPerspective(PERSPECTIVE_SIMULATION_ID);
	}

	@Override
	public void run(final Runnable r) {
		Display d = getDisplay();
		if ( d != null && !d.isDisposed() ) {
			d.syncExec(r);
		}
	}

	@Override
	public void togglePerspective() {
		if ( isSimulationPerspective() ) {
			openModelingPerspective();
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

	/**
	 * @see msi.gama.common.interfaces.IGui#createDisplay(msi.gama.outputs.layers.IDisplayLayer,
	 *      double, double, msi.gama.common.interfaces.IGraphics)
	 */
	// @Override
	// public IDisplay createDisplay(final IDisplayLayer layer, final double w, final double h,
	// final IGraphics g) {
	// return DisplayManager.createDisplay(layer, w, h, g);
	// }

	/**
	 * @see msi.gama.common.interfaces.IGui#newGraphics(int, int)
	 */
	@Override
	public IGraphics newGraphics(final int width, final int height) {
		// TODO Hook OpenGL here
		return new AWTDisplayGraphics(width, height);
	}

	@Override
	public IDisplaySurface getDisplaySurfaceFor(final String keyword,
		final IDisplayOutput layerDisplayOutput, final double w, final double h) {
		// FIXME Raw dynamic version -- the map needs to be created and cached somewhere

		Map<String, Class> displayClasses = new HashMap();

		IConfigurationElement[] config =
			Platform.getExtensionRegistry().getConfigurationElementsFor("gama.display");
		for ( IConfigurationElement e : config ) {
			final String pluginKeyword = e.getAttribute("keyword");
			final String pluginClass = e.getAttribute("class");
			// final Class<IDisplaySurface> displayClass = .
			final String pluginName = e.getContributor().getName();
			System.out.println("Display found in " + pluginName + " with keyword " + pluginKeyword +
				" and class " + pluginClass);
			ClassLoader cl =
				GamaClassLoader.getInstance().addBundle(Platform.getBundle(pluginName));
			try {
				displayClasses.put(pluginKeyword, cl.loadClass(pluginClass));
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}

		Class<IDisplaySurface> clazz = displayClasses.get(keyword);
		if ( clazz == null ) { throw new GamaRuntimeException("Display " + keyword +
			" is not defined anywhere."); }
		try {
			IDisplaySurface surface = clazz.newInstance();
			debug("Instantiating " + clazz.getSimpleName() + " to produce a " + keyword +
				" display");
			surface.initialize(w, h, layerDisplayOutput);
			return surface;
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		// FIXME HACK
		return null;
	}
}
