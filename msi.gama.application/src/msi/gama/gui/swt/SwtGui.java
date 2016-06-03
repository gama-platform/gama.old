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

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;
import org.eclipse.ui.part.FileEditorInput;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.GamaPreferences;
import msi.gama.common.GamaPreferences.Entry;
import msi.gama.common.GamaPreferences.IPreferenceChangeListener;
import msi.gama.common.IStatusMessage;
import msi.gama.common.StatusMessage;
import msi.gama.common.SubTaskMessage;
import msi.gama.common.UserStatusMessage;
import msi.gama.common.interfaces.IDisplayCreator;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IEditorFactory;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ISpeedDisplayer;
import msi.gama.common.util.AbstractGui;
import msi.gama.gui.navigator.FileMetaDataProvider;
import msi.gama.gui.navigator.NavigatorBaseLighweightDecorator;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.parameters.EditorsDialog;
import msi.gama.gui.parameters.UserControlDialog;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.commands.ArrangeDisplayViews;
import msi.gama.gui.swt.commands.GamaColorMenu;
import msi.gama.gui.swt.controls.SWTChartEditor.SWTUtils;
import msi.gama.gui.swt.controls.StatusControlContribution;
import msi.gama.gui.swt.dialogs.ExceptionDetailsDialog;
import msi.gama.gui.viewers.html.HtmlViewer;
import msi.gama.gui.views.ConsoleView;
import msi.gama.gui.views.ErrorView;
import msi.gama.gui.views.ExperimentParametersView;
import msi.gama.gui.views.GamaViewPart;
import msi.gama.gui.views.InteractiveConsoleView;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.gui.views.MonitorView;
import msi.gama.gui.views.UserControlView;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.ISimulationStateProvider;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.operators.IUnits;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 mai 2011
 *
 * @todo Description
 *
 */
public class SwtGui extends AbstractGui {

	private IAgent highlightedAgent;
	// public static boolean MOUSE_DOWN;

	static {
		if ( !GAMA.isInHeadLessMode() ) {
			// GAMA.setGui(new SwtGui());
			// WorkaroundForIssue1358.install();
		} else {
			System.out.println("Configuring HEADLESS MODE");
		}
	}

	// Needed by RCP for displaying the simulation state
	public static ISimulationStateProvider state = null;

	private static Font expandFont;
	private static Font smallFont;
	private static Font smallNavigFont;
	private static Font smallNavigLinkFont;
	private static Font labelFont;
	private static Font navigRegularFont;
	private static Font navigFileFont;
	private static Font parameterEditorsFont;
	private static Font navigResourceFont;
	private static Font navigHeaderFont;

	private static Font unitFont;
	public static final GridData labelData = new GridData(SWT.END, SWT.CENTER, false, false);
	// private static Logger log;
	private static ThreadedUpdater<IStatusMessage> status = new ThreadedUpdater<>("Status refresh");
	static ISpeedDisplayer speedStatus;
	private Tell tell = new Tell();
	private Error error = new Error();
	// private Views views = new Views();
	ConsoleView console = null;
	private final StringBuilder consoleBuffer = new StringBuilder(2000);
	private static int dialogReturnCode;
	static final List<IDisplaySurface> surfaces = new ArrayList();
	private static IPartListener2 partListener;

	public static final Entry<Color> SHAPEFILE_VIEWER_FILL = GamaPreferences
		.create("shapefile.viewer.background", "Default shapefile viewer fill color", Color.LIGHT_GRAY, IType.COLOR)
		.in(GamaPreferences.UI).group("Viewers (settings effective for new viewers)");

	public static final Entry<Color> SHAPEFILE_VIEWER_LINE_COLOR = GamaPreferences
		.create("shapefile.viewer.line.color", "Default shapefile viewer line color", Color.black, IType.COLOR)
		.in(GamaPreferences.UI).group("Viewers (settings effective for new viewers)");

	public static final Entry<Color> ERROR_TEXT_COLOR = GamaPreferences
		.create("error.text.color", "Text color of errors in error view",
			GamaColors.toAwtColor(IGamaColors.ERROR.inactive()), IType.COLOR)
		.in(GamaPreferences.EXPERIMENTS).group("Errors");

	public static final Entry<Color> WARNING_TEXT_COLOR = GamaPreferences
		.create("warning.text.color", "Text color of warnings in error view",
			GamaColors.toAwtColor(IGamaColors.WARNING.inactive()), IType.COLOR)
		.in(GamaPreferences.EXPERIMENTS).group("Errors");

	public static final Entry<Color> IMAGE_VIEWER_BACKGROUND = GamaPreferences
		.create("image.viewer.background", "Default image viewer background color", Color.white, IType.COLOR)
		.in(GamaPreferences.UI).group("Viewers (settings effective for new viewers)");

	static FontData baseData = getDisplay().getSystemFont().getFontData()[0];
	static String baseFont = baseData.getName();
	static int baseSize = 11;

	public static final Entry<GamaFont> BASE_BUTTON_FONT = GamaPreferences
		.create("base_button_font", "Font of buttons (applies to new buttons)",
			new GamaFont(baseFont, SWT.BOLD, baseSize), IType.FONT)
		.in(GamaPreferences.UI).group("Fonts")
		.addChangeListener(new GamaPreferences.IPreferenceChangeListener<GamaFont>() {

			@Override
			public boolean beforeValueChange(final GamaFont newValue) {
				return true;
			}

			@Override
			public void afterValueChange(final GamaFont newValue) {
				final FontData fd = SWTUtils.toSwtFontData(SwtGui.getDisplay(), newValue, true);
				setLabelFont(new Font(getDisplay(), fd));
			}
		});

	static final QualifiedName updateProperty = new QualifiedName("msi.gama.application", "update");

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

	@Override
	public IGamaView findView(final IDisplayOutput output) {
		final IWorkbenchPage page = getPage();
		if ( page == null ) { return null; } // Closing the workbench
		final IViewReference ref =
			page.findViewReference(output.getViewId(), output.isUnique() ? null : output.getName());
		if ( ref == null ) { return null; }
		final IViewPart part = ref.getView(true);
		if ( !(part instanceof IGamaView) ) { return null; }
		return (IGamaView) part;
	}

	@Override
	public IGamaView getInteractiveConsole() {
		final IWorkbenchPage page = getPage();
		if ( page == null ) { return null; } // Closing the workbench
		final IViewReference ref = page.findViewReference(this.INTERACTIVE_CONSOLE_VIEW_ID);
		if ( ref == null ) { return null; }
		final IViewPart part = ref.getView(true);
		if ( !(part instanceof IGamaView) ) { return null; }
		return (IGamaView) part;
	}

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
		if ( exp == null || !GamaPreferences.CORE_ASK_CLOSING.getValue() ) { return true; }
		openSimulationPerspective(true);
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
		if ( GAMA.getFrontmostController() != null && GAMA.getFrontmostController().isDisposing() ) { return; }

		// if ( g != null ) {
		// g.printStackTrace();
		// }
		if ( GamaPreferences.CORE_SHOW_ERRORS.getValue() ) {
			final ErrorView v = (ErrorView) showView(ErrorView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
			if ( v != null ) {
				asyncRun(new Runnable() {

					@Override
					public void run() {
						v.addNewError(g);
					}
				});
			}
		}
	}

	private void clearErrors() {
		// debug("Closing Error View");
		final IViewReference ref = getPage().findViewReference(ErrorView.ID);
		if ( ref == null ) { return; }
		final ErrorView v = (ErrorView) ref.getPart(false);

		if ( v == null ) { return; }
		run(new Runnable() {

			@Override
			public void run() {
				v.clearErrors();
				// v.close();
			}
		});
	}

	@Override
	public void setStatus(final String msg, final int code) {
		status.updateWith(new StatusMessage(msg, code));
	}

	@Override
	public void setStatus(final String msg, final String icon) {
		setStatusInternal(msg, null, icon);
	}

	@Override
	public void setStatusInternal(final String msg, final GamaColor color) {
		setStatusInternal(msg, color, null);
	}

	@Override
	public void setStatusInternal(final String msg, final GamaColor color, final String icon) {
		status.updateWith(new UserStatusMessage(msg, color, icon));
	}

	@Override
	public void resumeStatus() {
		status.resume();
	}

	public static void setStatusControl(final StatusControlContribution l) {
		status.setTarget(l, null);
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

	private void writeToConsole(final String msg, final ITopLevelAgent root, final GamaUIColor color) {
		if ( console != null ) {
			console.append(msg, root, color);
		} else {
			consoleBuffer.append(msg);
		}
	}

	@Override
	public void debugConsole(final int cycle, final String msg, final ITopLevelAgent root) {
		this.debugConsole(cycle, msg, root, null);
	}

	@Override
	public void debugConsole(final int cycle, final String msg, final ITopLevelAgent root, final GamaColor color) {
		writeToConsole("(cycle : " + cycle + ") " + msg + sep, root, GamaColors.get(color));
	}

	private static String sep = System.getProperty("line.separator");

	@Override
	public void informConsole(final String msg, final ITopLevelAgent root) {
		this.informConsole(msg, root, null);
	}

	@Override
	public void informConsole(final String msg, final ITopLevelAgent root, final GamaColor color) {
		writeToConsole(msg + sep, root, GamaColors.get(color));
	}

	public void informConsole(final Throwable e) {
		final StringWriter s = new StringWriter();
		final PrintWriter pw = new PrintWriter(s);
		e.printStackTrace(pw);
	}

	@Override
	public void eraseConsole(final boolean setToNull) {
		if ( console != null ) {
			run(new Runnable() {

				@Override
				public void run() {
					if ( console != null ) {
						console.clearText();
						if ( setToNull ) {
							console = null;
						}
					}
				}
			});
		}
	}

	private Object internalShowView(final String viewId, final String secondaryId, final int code) {
		if ( GAMA.getFrontmostController() != null && GAMA.getFrontmostController().isDisposing() ) { return null; }
		final Object[] result = new Object[1];
		run(new Runnable() {

			@Override
			public void run() {
				try {
					final IWorkbenchPage page = getPage();
					if ( page != null ) {
						page.zoomOut();
						result[0] = page.showView(viewId, secondaryId, code);
					}
				} catch (final Exception e) {
					result[0] = e;
				}
			}
		});
		return result[0];
	}

	static final IEditorInput input = new IEditorInput() {

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
		// if ( Platform.getOS().equals(Platform.OS_LINUX) || Platform.getWS().equals(Platform.WS_GTK) ) { return false; }

		try {
			final URL url = new URL("http://gama-platform.org");
			// open a connection to that source
			final HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			// trying to retrieve data from the source. If there
			// is no connection, this line will fail
			urlConnect.setConnectTimeout(2000);
			final Object objData = urlConnect.getContent();
		} catch (final UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// @Override
	public static Object showWeb2Editor(final String url, final String html) {
		if ( url != null && url.contains("http") ) {
			if ( !isInternetReachable() ) { return null; }
		}

		final Object[] result = new Object[1];
		getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				try {
					getPage().zoomOut();
					final IEditorPart p = getPage().findEditor(input);
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
			final HtmlViewer be = (HtmlViewer) result[0];
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
			GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.create((Exception) o, GAMA.getRuntimeScope()),
				false);
		}
		return null;
	}

	public void hideMonitorView() {
		final MonitorView m = (MonitorView) hideView(MonitorView.ID);
		if ( m != null ) {
			m.reset();
		}
	}

	@Override
	public void showConsoleView(final ITopLevelAgent agent) {
		console = (ConsoleView) showView(CONSOLE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
		// console.setExecutorAgent(agent);
		if ( consoleBuffer.length() > 0 ) {
			console.append(consoleBuffer.toString(), agent, null);
			consoleBuffer.setLength(0);
		}
		final InteractiveConsoleView icv =
			(InteractiveConsoleView) showView(INTERACTIVE_CONSOLE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
		if ( icv != null )
			icv.setExecutorAgent(agent);
	}

	static void initFonts() {

		final GamaFont font = BASE_BUTTON_FONT.getValue();
		FontData fd = new FontData(font.getName(), font.getSize(), font.getStyle());
		labelFont = new Font(getDisplay(), fd);
		final FontData fd2 = new FontData(fd.getName(), fd.getHeight(), SWT.BOLD);
		expandFont = new Font(Display.getDefault(), fd2);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.ITALIC);
		unitFont = new Font(Display.getDefault(), fd);
		smallNavigLinkFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight() + 1, SWT.BOLD);
		// bigFont = new Font(Display.getDefault(), fd);
		navigHeaderFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight() - 1, SWT.NORMAL);
		smallFont = new Font(Display.getDefault(), fd);
		smallNavigFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.NORMAL);
		parameterEditorsFont = new Font(Display.getDefault(), fd);
		navigFileFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.NORMAL);
		navigRegularFont = new Font(Display.getDefault(), fd);
		fd = new FontData(fd.getName(), fd.getHeight(), SWT.ITALIC);
		navigResourceFont = new Font(Display.getDefault(), fd);
	}

	private static void setLabelFont(final Font f) {
		if ( labelFont == null ) {
			labelFont = f;
			return;
		} else {
			// ???
			// labelFont.dispose();
			labelFont = f;
		}
	}

	@Override
	public void asyncRun(final Runnable r) {
		final Display d = getDisplay();
		if ( d != null && !d.isDisposed() ) {
			d.asyncExec(r);
		}
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
		return currentPerspectiveId.equals(PERSPECTIVE_MODELING_ID);
	}

	@Override
	public boolean isSimulationPerspective() {
		return isSimulationPerspective(currentPerspectiveId);
	}

	private boolean isSimulationPerspective(final String perspectiveId) {
		return perspectiveId.contains(PERSPECTIVE_SIMULATION_FRAGMENT);
	}

	@Override
	public final boolean openModelingPerspective(final boolean immediately) {
		return openPerspective(PERSPECTIVE_MODELING_ID, immediately, true);
	}

	private void cleanPerspectives() {
		final IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
		for ( final IPerspectiveDescriptor desc : reg.getPerspectives() ) {
			if ( desc.getId().contains(PERSPECTIVE_SIMULATION_FRAGMENT) &&
				!desc.getId().equals(PERSPECTIVE_SIMULATION_ID) ) {
				reg.deletePerspective(desc);
			}
		}
	}

	public static PerspectiveRegistry getPerspectiveRegistry() {
		return (PerspectiveRegistry) PlatformUI.getWorkbench().getPerspectiveRegistry();
	}

	static PerspectiveDescriptor getSimulationDescriptor() {
		return (PerspectiveDescriptor) getPerspectiveRegistry().findPerspectiveWithId(PERSPECTIVE_SIMULATION_ID);
	}

	static void dirtySavePerspective(final SimulationPerspectiveDescriptor sp) {
		try {
			final Field descField = PerspectiveRegistry.class.getDeclaredField("descriptors");
			descField.setAccessible(true);
			final Map m = (Map) descField.get(getPerspectiveRegistry());
			m.put(sp.getId(), sp);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class SimulationPerspectiveDescriptor extends PerspectiveDescriptor {

		SimulationPerspectiveDescriptor(final String id) {
			super(id, id, getSimulationDescriptor());
			dirtySavePerspective(this);
		}

		@Override
		public IPerspectiveFactory createFactory() {
			try {
				return (IPerspectiveFactory) getSimulationDescriptor().getConfigElement()
					.createExecutableExtension(IWorkbenchRegistryConstants.ATT_CLASS);
			} catch (final CoreException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean hasCustomDefinition() {
			return true;
		}

		@Override
		public boolean isPredefined() {
			return true;
		}

		@Override
		public IConfigurationElement getConfigElement() {
			return getSimulationDescriptor().getConfigElement();
		}

		@Override
		public String getDescription() {
			return "Perspective for " + getId();
		}

		@Override
		public String getOriginalId() {
			return getId();
		}

		// @Override
		// public String getLabel() {
		// return getSimulationDescriptor().getLabel();
		// }

		@Override
		public String getPluginId() {
			return getSimulationDescriptor().getPluginId();
		}

	}

	private String currentPerspectiveId = IGui.PERSPECTIVE_MODELING_ID;

	private IPerspectiveDescriptor findOrBuildPerspectiveWithId(final String id) {
		IPerspectiveDescriptor tempDescriptor = getPerspectiveRegistry().findPerspectiveWithId(id);
		if ( tempDescriptor == null ) {
			tempDescriptor = new SimulationPerspectiveDescriptor(id);
		}
		return tempDescriptor;
	}

	private final boolean openPerspective(final String perspectiveId, final boolean immediately,
		final boolean withAutoSave) {
		if ( perspectiveId.equals(currentPerspectiveId) )
			return true;
		System.out.println("Trying to open perspective " + perspectiveId + " ");

		// System.out.println("Cleaning perspectives");
		// cleanPerspectives();
		final IWorkbenchPage activePage = getPage(perspectiveId);
		final IPerspectiveDescriptor oldDescriptor = activePage.getPerspective();
		final IPerspectiveDescriptor descriptor = findOrBuildPerspectiveWithId(perspectiveId);

		final Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					activePage.setPerspective(descriptor);
				} catch (final NullPointerException e) {
					System.err.println(
						"NPE in WorkbenchPage.setPerspective(). See Issue #1602. Working around the bug in e4...");
					activePage.setPerspective(descriptor);
				}
				activateAutoSave(withAutoSave);
				if ( isSimulationPerspective(currentPerspectiveId) && isSimulationPerspective(perspectiveId) ) {
					activePage.closePerspective(oldDescriptor, false, false);
				}
				// if ( isSimulationPerspective(currentPerspectiveId) ) {
				// final IContextService contextService = getWindow().getService(IContextService.class);
				// contextService.activateContext("msi.gama.application.simulation.context", expression, true);
				// }
				currentPerspectiveId = perspectiveId;
				System.out.println("Perspective " + perspectiveId + " opened ");
			}
		};
		if ( immediately ) {
			run(r);
		} else {
			asyncRun(r);
		}
		return true;
	}

	// Expression expression = new PerspectiveExpression();
	//
	// private class PerspectiveExpression extends Expression {
	//
	// @Override
	// public EvaluationResult evaluate(final IEvaluationContext context) throws CoreException {
	// return EvaluationResult.TRUE;
	// }
	//
	// @Override
	// public void collectExpressionInfo(final ExpressionInfo info) {
	// super.collectExpressionInfo(info);
	// info.markDefaultVariableAccessed();
	// }
	//
	// }

	public final static IPerspectiveDescriptor getActivePerspective() {
		final IWorkbenchPage activePage = getPage();
		final IPerspectiveDescriptor currentDescriptor = activePage.getPerspective();
		return currentDescriptor;

	}

	public final static String getActivePerspectiveName() {
		return getActivePerspective().getId();

	}

	@Override
	public void runModel(final IModel model, final String exp) {
		GAMA.runGuiExperiment(exp, model);
	}

	@Override
	public final boolean openSimulationPerspective(final boolean immediately) {
		final IModel model = GAMA.getModel();
		if ( model == null )
			return false;
		final IExperimentPlan p = GAMA.getExperiment();
		if ( p == null )
			return false;
		final String id = p.getName();
		return openSimulationPerspective(model, id, immediately);
	}

	@Override
	public final boolean openSimulationPerspective(final IModel model, final String experimentName,
		final boolean immediately) {
		if ( model == null )
			return false;
		final String name = getNewPerspectiveName(model, experimentName);
		return openPerspective(name, immediately, false);

	}

	public static void activateAutoSave(final boolean activate) {
		System.out.println("auto-save activated: " + activate);
		Workbench.getInstance().setEnableAutoSave(activate);
		// ApplicationWorkbenchAdvisor.CONFIGURER.setSaveAndRestore(activate);
	}

	public final boolean openBatchPerspective(final boolean immediately) {
		return openPerspective(PERSPECTIVE_HPC_ID, immediately, false);
	}

	public static GamaPreferences.Entry<String> COLOR_MENU_SORT =
		GamaPreferences.create("menu.colors.sort", "Sort colors menu by", "RGB value", IType.STRING)
			.among(GamaColorMenu.SORT_NAMES).activates("menu.colors.reverse", "menu.colors.group")
			.in(GamaPreferences.UI).group("Menus").addChangeListener(new IPreferenceChangeListener<String>() {

				@Override
				public boolean beforeValueChange(final String newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final String pref) {
					if ( pref.equals(GamaColorMenu.SORT_NAMES[0]) ) {
						GamaColorMenu.colorComp = GamaColorMenu.byRGB;
					} else if ( pref.equals(GamaColorMenu.SORT_NAMES[1]) ) {
						GamaColorMenu.colorComp = GamaColorMenu.byName;
					} else if ( pref.equals(GamaColorMenu.SORT_NAMES[2]) ) {
						GamaColorMenu.colorComp = GamaColorMenu.byBrightness;
					} else {
						GamaColorMenu.colorComp = GamaColorMenu.byLuminescence;
					}
					GamaColorMenu.instance.reset();
				}
			});
	public static GamaPreferences.Entry<Boolean> COLOR_MENU_REVERSE =
		GamaPreferences.create("menu.colors.reverse", "Reverse order", false, IType.BOOL).in(GamaPreferences.UI)
			.group("Menus").addChangeListener(new IPreferenceChangeListener<Boolean>() {

				@Override
				public boolean beforeValueChange(final Boolean newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Boolean pref) {
					GamaColorMenu.reverse = pref ? -1 : 1;
					GamaColorMenu.instance.reset();
				}
			});
	public static GamaPreferences.Entry<Boolean> COLOR_MENU_GROUP =
		GamaPreferences.create("menu.colors.group", "Group colors", false, IType.BOOL).in(GamaPreferences.UI)
			.group("Menus").addChangeListener(new IPreferenceChangeListener<Boolean>() {

				@Override
				public boolean beforeValueChange(final Boolean newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Boolean pref) {
					GamaColorMenu.breakdown = pref;
					GamaColorMenu.instance.reset();
				}
			});
	public static final Entry<Boolean> NAVIGATOR_METADATA = GamaPreferences
		.create("navigator.metadata", "Display metadata of data and GAML files in navigator", true, IType.BOOL)
		.in(GamaPreferences.UI).group("Navigator").addChangeListener(new IPreferenceChangeListener<Boolean>() {

			@Override
			public boolean beforeValueChange(final Boolean newValue) {
				return true;
			}

			@Override
			public void afterValueChange(final Boolean newValue) {
				final IDecoratorManager mgr = PlatformUI.getWorkbench().getDecoratorManager();
				try {
					mgr.setEnabled(NavigatorBaseLighweightDecorator.ID, newValue);
				} catch (final CoreException e) {
					e.printStackTrace();
				}

			}
		});

	@Override
	public void run(final Runnable r) {
		final Display d = getDisplay();
		if ( d != null && !d.isDisposed() ) {
			d.syncExec(r);
		}
	}

	public static String getNewPerspectiveName(final IModel model, final String experiment) {
		return PERSPECTIVE_SIMULATION_FRAGMENT + ":" + model.getName() + ":" + experiment;
	}

	@Override
	public void togglePerspective(final boolean immediately) {
		System.out.println("Toggling perspective immediately" + immediately);
		if ( isSimulationPerspective() ) {
			openModelingPerspective(immediately);
		} else {
			openSimulationPerspective(immediately);
		}
	}

	/**
	 * @see msi.gama.common.interfaces.IGui#getEditorFactory()
	 */
	@Override
	public IEditorFactory getEditorFactory() {
		return EditorFactory.getInstance();
	}

	@Override
	public DisplayDescription getDisplayDescriptionFor(final String name) {
		return (DisplayDescription) DISPLAYS.get(name);
	}

	@Override
	public IDisplaySurface getDisplaySurfaceFor(final LayeredDisplayOutput output) {
		IDisplaySurface surface = null;
		final String keyword = output.getData().getDisplayType();
		final IDisplayCreator creator = DISPLAYS.get(keyword);
		if ( creator != null ) {
			surface = creator.create(output);
			surface.outputReloaded();
		} else {
			throw GamaRuntimeException.error("Display " + keyword + " is not defined anywhere.", output.getScope());
		}
		return surface;
	}

	@Override
	public Map<String, Object> openUserInputDialog(final IScope scope, final String title,
		final Map<String, Object> initialValues, final Map<String, IType> types) {
		final Map<String, Object> result = new THashMap();
		run(new Runnable() {

			@Override
			public void run() {
				final EditorsDialog dialog = new EditorsDialog(scope, getShell(), initialValues, types, title);
				result.putAll(dialog.open() == Window.OK ? dialog.getValues() : initialValues);
			}
		});
		return result;
	}

	public void openUserControlDialog(final IScope scope, final UserPanelStatement panel) {
		run(new Runnable() {

			@Override
			public void run() {
				final UserControlDialog dialog = new UserControlDialog(getShell(), panel.getUserCommands(),
					"[" + scope.getAgentScope().getName() + "] " + panel.getName(), scope);
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
					part.initFor(scope, panel.getUserCommands(), "[" + scope.getAgentScope().getName() + " in " +
						scope.getSimulationScope().getName() + "] " + panel.getName());
				}
				System.out.println("Setting " + scope.getName() + " on user hold");
				scope.setOnUserHold(true);
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
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IFile file = workspace.getRoot().getFile(new Path((String) eObject));
			editModel(file);
		} else if ( eObject instanceof IFile ) {
			final IFile file = (IFile) eObject;
			if ( !file.exists() ) {
				debug("File " + file.getFullPath().toString() + " does not exist in the workspace");
				return;
			}
			try {
				final IEditorDescriptor desc =
					PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
				getPage().openEditor(new FileEditorInput(file), desc.getId());
			} catch (final PartInitException e) {
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
					final ExperimentParametersView view = (ExperimentParametersView) getPage()
						.showView(ExperimentParametersView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
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
					final ExperimentParametersView view = (ExperimentParametersView) getPage()
						.showView(ExperimentParametersView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
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

	/**
	 * Method setSelectedAgent()
	 * @see msi.gama.common.interfaces.IGui#setSelectedAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void setSelectedAgent(final IAgent a) {
		asyncRun(new Runnable() {

			@Override
			public void run() {
				if ( getPage() == null ) { return; }
				// final IViewReference r = getPage().findViewReference(scope.getGui().AGENT_VIEW_ID, "");
				// if ( r == null ) {
				if ( a == null ) { return; }
				try {
					final InspectDisplayOutput output = new InspectDisplayOutput(a);
					output.launch(a.getScope());
				} catch (final GamaRuntimeException g) {
					g.addContext("In opening the agent inspector");
					GAMA.reportError(GAMA.getRuntimeScope(), g, false);
				}
				final IViewReference r = getPage().findViewReference(IGui.AGENT_VIEW_ID, "");
				if ( r != null ) {
					getPage().bringToTop(r.getPart(true));
				}
				// }
				// AgentInspectView v =
				// (AgentInspectView) showView(scope.getGui().AGENT_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
				// v.inspectAgent(a);
			}
		});
	}

	@Override
	public void prepareForSimulation(final SimulationAgent agent) {
		clearErrors();
		// if ( !agent.getExperiment().getSpecies().isBatch() ) {
		// showConsoleView(agent);
		// // resetMonitorView();
		// } else {
		// if ( console == null ) {
		// showConsoleView(agent);
		// }
		// }
	}

	@Override
	public void prepareForExperiment(final IExperimentPlan exp) {
		if ( exp.isGui() ) {
			if ( openGLStartupSequence != null ) {
				openGLStartupSequence.run();
				openGLStartupSequence = null;
			}
			// showConsoleView();
			setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
			updateParameterView(exp);
			tell = new Tell();
			error = new Error();
			// views = new Views();
			// OutputSynchronizer.waitForViewsToBeClosed();
			// hqnghi:
			// TODO in case of multi controllers, open an experiment cause "closing-reopen" many times displays,
			// TODO so waitForViewsToBeClosed only with mono controller
			// if ( GAMA.getControllers().size() == 0 ) {
			// OutputSynchronizer.waitForViewsToBeClosed();
			// }
			// end-hqnghi
			showConsoleView(exp.getAgent());
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
		hideView(IGui.PARAMETER_VIEW_ID);
		hideMonitorView();
		eraseConsole(true);
		final InteractiveConsoleView icv =
			(InteractiveConsoleView) showView(INTERACTIVE_CONSOLE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
		if ( icv != null )
			icv.setExecutorAgent(null);
	}

	/**
	 * Method cleanAfterSimulation()
	 * @see msi.gama.common.interfaces.IGui#cleanAfterSimulation()
	 */
	@Override
	public void cleanAfterSimulation() {
		setSelectedAgent(null);
		setHighlightedAgent(null);
		status.resume();
		// AD: Fix for issue #1342 -- verify that it does not break something else in the dynamics of closing/opening
		closeDialogs();
	}

	/**
	 * Method waitForViewsToBeInitialized()
	 * @see msi.gama.common.interfaces.IGui#waitForViewsToBeInitialized()
	 */
	// @Override
	// public void waitForViewsToBeInitialized() {
	// // OutputSynchronizer.waitForViewsToBeInitialized();
	// }

	@Override
	public void runModel(final Object object, final String exp) throws CoreException {
		error("Impossible to run the model. The XText environment has not been launched");
	}

	/**
	 * Method getFirstDisplaySurface()
	 * @see msi.gama.common.interfaces.IGui#getFirstDisplaySurface()
	 */
	public static IDisplaySurface getFirstDisplaySurface() {
		final IViewReference[] viewRefs = getPage().getViewReferences();
		for ( final IViewReference ref : viewRefs ) {
			final IWorkbenchPart part = ref.getPart(false);
			if ( part instanceof LayeredDisplayView ) {

			return ((LayeredDisplayView) part).getDisplaySurface(); }
		}
		return null;
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
		final IWorkbenchPage page = getPage();
		if ( page != null ) { return page.getActiveEditor(); }
		return null;
	}

	public static IWorkbenchPart getActivePart() {
		final IWorkbenchPage page = getPage();
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

	/**
	 * Method getMetaDataProvider()
	 * @see msi.gama.common.interfaces.IGui#getMetaDataProvider()
	 */
	@Override
	public IFileMetaDataProvider getMetaDataProvider() {
		return FileMetaDataProvider.getInstance();
	}

	@Override
	public void closeSimulationViews(final boolean openModelingPerspective, final boolean immediately) {
		run(new Runnable() {

			@Override
			public void run() {
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final IViewReference[] views = page.getViewReferences();

				for ( final IViewReference view : views ) {
					final IViewPart part = view.getView(false);
					if ( part instanceof IGamaView ) {
						((IGamaView) part).close();

					}
				}
				if ( openModelingPerspective ) {

					openModelingPerspective(immediately);

				}
				setStatus("No simulation running", IGui.NEUTRAL);
			}
		});

	}

	@Override
	public String getFrontmostSimulationState() {
		final IExperimentController controller = GAMA.getFrontmostController();
		if ( controller == null ) {
			return NONE;
		} else if ( controller.getScheduler().paused ) { return PAUSED; }
		return RUNNING;
	}

	@Override
	public void updateSimulationState(final String forcedState) {
		if ( state != null ) {
			asyncRun(new Runnable() {

				@Override
				public void run() {
					state.updateStateTo(forcedState);
				}
			});
		}
	}

	@Override
	public void updateSimulationState() {
		updateSimulationState(getFrontmostSimulationState());
	}

	static GamaColor[] SIMULATION_COLORS =
		new GamaColor[] { IGamaColors.BLUE.toGamaColor(), IGamaColors.OK.toGamaColor(),
			IGamaColors.NEUTRAL.toGamaColor(), IGamaColors.WARNING.toGamaColor(), IGamaColors.BROWN.toGamaColor() };

	/**
	 * @param index
	 * @return
	 */
	@Override
	public GamaColor getColorForSimulationNumber(final int index) {
		return SIMULATION_COLORS[index % 5];
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.common.interfaces.IGui#registerView(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void registerView(final String modelName, final String expeName, final String viewName) {
		final String key = modelName + expeName;
		Set<String> ids = MODEL_VIEWS.get(key);
		if ( ids == null ) {
			ids = new HashSet<>();
			MODEL_VIEWS.put(key, ids);
		}
		ids.add(viewName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.common.interfaces.IGui#getViews(java.lang.String, java.lang.String)
	 */
	@Override
	public Set<String> getViews(final String modelName, final String expeName) {
		final String key = modelName + expeName;
		final Set<String> ids = MODEL_VIEWS.get(key);
		return ids == null ? Collections.EMPTY_SET : ids;
	}

	static Runnable openGLStartupSequence;

	/**
	 * @param runnable
	 */
	public static void setOpenGLStartupSequence(final Runnable runnable) {
		openGLStartupSequence = runnable;
	}

	@Override
	public void applyLayout(final int layout) {
		if ( layout == IUnits.none )
			return;
		asyncRun(new Runnable() {

			@Override
			public void run() {
				ArrangeDisplayViews.execute(layout);

			}
		});

	}

	@Override
	public void updateViewTitle(final IDisplayOutput out, final SimulationAgent agent) {
		final IGamaView part = this.findView(out);
		if ( part != null )
			this.run(new Runnable() {

				@Override
				public void run() {
					part.changePartNameWithSimulation(agent);

				}

			});

	}

	private static volatile boolean isRequesting;

	public static void requestUserAttention(final GamaViewPart part, final String tempMessage) {
		if ( isRequesting )
			return;
		// rate at which the title will change in milliseconds
		final int rateOfChange = 200;
		final int numberOfTimes = 2;

		// flash n times and thats it
		final String orgText = part.getPartName();

		for ( int x = 0; x < numberOfTimes; x++ ) {
			getDisplay().timerExec(2 * rateOfChange * x - rateOfChange, new Runnable() {

				@Override
				public void run() {
					isRequesting = true;
					part.setName(tempMessage);
				}
			});
			getDisplay().timerExec(2 * rateOfChange * x, new Runnable() {

				@Override
				public void run() {
					part.setName(orgText);
					isRequesting = false;
				}
			});
		}
	}

}
