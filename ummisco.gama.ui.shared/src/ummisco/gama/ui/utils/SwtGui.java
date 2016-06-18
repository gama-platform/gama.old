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
package ummisco.gama.ui.utils;

import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.WorkbenchPlugin;
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
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGamaView.Console;
import msi.gama.common.interfaces.IGamaView.Error;
import msi.gama.common.interfaces.IGamaView.Parameters;
import msi.gama.common.interfaces.IGamaView.User;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ISpeedDisplayer;
import msi.gama.common.interfaces.IUpdaterTarget;
import msi.gama.common.util.AbstractGui;
import msi.gama.gui.metadata.FileMetaDataProvider;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
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
import msi.gaml.types.IType;
import ummisco.gama.ui.dialogs.Messages;
import ummisco.gama.ui.menus.GamaColorMenu;
import ummisco.gama.ui.menus.MenuAction;
import ummisco.gama.ui.parameters.EditorsDialog;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.GamaPreferencesView;

/**
 * Written by drogoul Modified on 6 mai 2011
 *
 * @todo Description
 *
 */
public class SwtGui extends AbstractGui {

	private IAgent highlightedAgent;
	private static URL HOME_URL;

	private static IAgentMenuFactory agentMenuFactory;
	private static IUserDialogFactory userDialogFactory;

	public static void setAgentMenuFactory(final IAgentMenuFactory factory) {
		agentMenuFactory = factory;
	}

	public static IAgentMenuFactory getAgentMenuFactory() {
		return agentMenuFactory;
	}

	public static void setUserDialogMenuFactory(final IUserDialogFactory factory) {
		userDialogFactory = factory;
	}

	public static interface IAgentMenuFactory {

		void fillPopulationSubMenu(final Menu menu, final Collection<IAgent> species, final ILocation userLocation,
				final MenuAction... actions);
	}

	public static interface IUserDialogFactory {

		void openUserDialog(IScope scope, UserPanelStatement panel);

		void closeUserDialog();
	}

	public static URL getWelcomePageURL() {
		if (HOME_URL == null)
			try {
				HOME_URL = FileLocator
						.toFileURL(Platform.getBundle("ummisco.gama.ui.shared").getEntry("/welcome/welcome.html"));
			} catch (final IOException e) {
				e.printStackTrace();
			}
		return HOME_URL;
	}

	// Needed by RCP for displaying the simulation state
	public static ISimulationStateProvider state = null;

	private static ThreadedUpdater<IStatusMessage> status = new ThreadedUpdater<>("Status refresh");
	static ISpeedDisplayer speedStatus;
	private final StringBuilder consoleBuffer = new StringBuilder(2000);

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

	public static final Entry<GamaFont> BASE_BUTTON_FONT = GamaPreferences
			.create("base_button_font", "Font of buttons (applies to new buttons)",
					new GamaFont(GamaFonts.baseFont, SWT.BOLD, GamaFonts.baseSize), IType.FONT)
			.in(GamaPreferences.UI).group("Fonts")
			.addChangeListener(new GamaPreferences.IPreferenceChangeListener<GamaFont>() {

				@Override
				public boolean beforeValueChange(final GamaFont newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final GamaFont newValue) {
					GamaFonts.setLabelFont(newValue);
				}
			});

	static {
		GamaFonts.setLabelFont(BASE_BUTTON_FONT.getValue());
	}

	@Override
	public IGamaView findView(final IDisplayOutput output) {
		return (IGamaView) WorkbenchHelper.findView(output.getViewId(), output.isUnique() ? null : output.getName(),
				true);
	}

	public static IGamaView getInteractiveConsole() {
		return (IGamaView) WorkbenchHelper.findView(INTERACTIVE_CONSOLE_VIEW_ID, null, true);
	}

	@Override
	public void debug(final String msg) {
		WorkbenchPlugin.log(msg);
	}

	@Override
	public boolean confirmClose(final IExperimentPlan exp) {
		if (exp == null || !GamaPreferences.CORE_ASK_CLOSING.getValue()) {
			return true;
		}
		openSimulationPerspective(true);
		return Messages.question("Close simulation confirmation", "Do you want to close experiment '" + exp.getName()
				+ "' of model '" + exp.getModel().getName() + "' ?");
	}

	@Override
	public void tell(final String msg) {
		Messages.tell(msg);
	}

	@Override
	public void error(final String err) {
		Messages.error(err);
	}

	@Override
	public void runtimeError(final GamaRuntimeException g) {
		if (GAMA.getFrontmostController() != null && GAMA.getFrontmostController().isDisposing()) {
			return;
		}

		if (GamaPreferences.CORE_SHOW_ERRORS.getValue()) {
			final IGamaView.Error v = (Error) showView(ERROR_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
			if (v != null) {
				WorkbenchHelper.asyncRun(new Runnable() {

					@Override
					public void run() {
						v.addNewError(g);
					}
				});
			}
		}
	}

	private void clearErrors() {
		final IGamaView v = (IGamaView) WorkbenchHelper.findView(ERROR_VIEW_ID, null, false);
		if (v == null) {
			return;
		}
		v.reset();
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
	public void informStatus(final String string, final String icon) {
		status.updateWith(new StatusMessage(string, IGui.INFORM, icon));
	}

	private void setStatusInternal(final String msg, final GamaColor color, final String icon) {
		status.updateWith(new UserStatusMessage(msg, color, icon));
	}

	@Override
	public void resumeStatus() {
		status.resume();
	}

	public static void setStatusControl(final IUpdaterTarget l) {
		status.setTarget(l, null);
	}

	private void writeToConsole(final String msg, final ITopLevelAgent root, final GamaColor color) {
		final IGamaView.Console console = (Console) showView(CONSOLE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
		if (console != null) {
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
		writeToConsole("(cycle : " + cycle + ") " + msg + sep, root, color);
	}

	private static String sep = System.getProperty("line.separator");

	@Override
	public void informConsole(final String msg, final ITopLevelAgent root) {
		this.informConsole(msg, root, null);
	}

	@Override
	public void informConsole(final String msg, final ITopLevelAgent root, final GamaColor color) {
		writeToConsole(msg + sep, root, color);
	}

	@Override
	public void eraseConsole(final boolean setToNull) {
		final IGamaView console = (IGamaView) WorkbenchHelper.findView(CONSOLE_VIEW_ID, null, false);
		if (console != null) {
			run(new Runnable() {

				@Override
				public void run() {
					console.reset();

				}
			});
		}
	}

	private Object internalShowView(final String viewId, final String secondaryId, final int code) {
		if (GAMA.getFrontmostController() != null && GAMA.getFrontmostController().isDisposing()) {
			return null;
		}
		final Object[] result = new Object[1];
		run(new Runnable() {

			@Override
			public void run() {
				try {
					final IWorkbenchPage page = WorkbenchHelper.getPage();
					if (page != null) {
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

	public static boolean isInternetReachable() {

		// AD 11/10/13 : see Issue 679
		// Too many problems with Linux for the moment. Reverse this if a
		// definitive solution is found.
		// if ( Platform.getOS().equals(Platform.OS_LINUX) ||
		// Platform.getWS().equals(Platform.WS_GTK) ) { return false; }

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

	@Override
	public void openWelcomePage(final boolean ifEmpty) {
		if (ifEmpty && WorkbenchHelper.getPage().getActiveEditor() != null) {
			return;
		}
		if (ifEmpty && !GamaPreferences.CORE_SHOW_PAGE.getValue()) {
			return;
		}
		showWeb2Editor(getWelcomePageURL());
	}

	public static void showWeb2Editor(final URL url) {

		// get the workspace
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// create the path to the file
		final IPath location = new Path(url.getPath());

		// try to get the IFile (returns null if it could not be found in the
		// workspace)
		final IFile file = workspace.getRoot().getFileForLocation(location);
		IEditorInput input;
		if (file == null) {
			// not found in the workspace, get the IFileStore (external files)
			final IFileStore fileStore = EFS.getLocalFileSystem().getStore(location);
			input = new FileStoreEditorInput(fileStore);

		} else {
			input = new FileEditorInput(file);
		}

		try {
			WorkbenchHelper.getPage().openEditor(input, "msi.gama.application.browser");
		} catch (final PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// SwtGui.showWeb2Editor(HOME_URL);

	}

	@Override
	public IGamaView showView(final String viewId, final String secondaryId, final int code) {

		Object o = internalShowView(viewId, secondaryId, code);
		if (o instanceof IWorkbenchPart) {
			if (o instanceof IGamaView) {
				return (IGamaView) o;
			}
			o = GamaRuntimeException.error("Impossible to open view " + viewId);
		}
		if (o instanceof Exception) {
			GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.create((Exception) o, GAMA.getRuntimeScope()),
					false);
		}
		return null;
	}

	public void hideMonitorView() {
		final IGamaView m = (IGamaView) WorkbenchHelper.findView(MONITOR_VIEW_ID, null, false);
		if (m != null) {
			m.reset();
			WorkbenchHelper.hideView(MONITOR_VIEW_ID);
		}
	}

	@Override
	public void showConsoleView(final ITopLevelAgent agent) {
		final IGamaView.Console console = (Console) showView(CONSOLE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
		if (consoleBuffer.length() > 0 && console != null) {
			console.append(consoleBuffer.toString(), agent, null);
			consoleBuffer.setLength(0);
		}
		final IGamaView.Console icv = (Console) showView(INTERACTIVE_CONSOLE_VIEW_ID, null,
				IWorkbenchPage.VIEW_VISIBLE);
		if (icv != null)
			icv.append(null, agent, null);
	}

	@Override
	public void runModel(final IModel model, final String exp) {
		GAMA.runGuiExperiment(exp, model);
	}

	@Override
	public final boolean openSimulationPerspective(final boolean immediately) {
		final IModel model = GAMA.getModel();
		if (model == null)
			return false;
		final IExperimentPlan p = GAMA.getExperiment();
		if (p == null)
			return false;
		final String id = p.getName();
		return openSimulationPerspective(model, id, immediately);
	}

	@Override
	public final boolean openSimulationPerspective(final IModel model, final String experimentName,
			final boolean immediately) {
		if (model == null)
			return false;
		final String name = PerspectiveHelper.getNewPerspectiveName(model.getName(), experimentName);
		return PerspectiveHelper.openPerspective(name, immediately, false);

	}

	public static GamaPreferences.Entry<String> COLOR_MENU_SORT = GamaPreferences
			.create("menu.colors.sort", "Sort colors menu by", "RGB value", IType.STRING)
			.among(GamaColorMenu.SORT_NAMES).activates("menu.colors.reverse", "menu.colors.group")
			.in(GamaPreferences.UI).group("Menus").addChangeListener(new IPreferenceChangeListener<String>() {

				@Override
				public boolean beforeValueChange(final String newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final String pref) {
					if (pref.equals(GamaColorMenu.SORT_NAMES[0])) {
						GamaColorMenu.colorComp = GamaColorMenu.byRGB;
					} else if (pref.equals(GamaColorMenu.SORT_NAMES[1])) {
						GamaColorMenu.colorComp = GamaColorMenu.byName;
					} else if (pref.equals(GamaColorMenu.SORT_NAMES[2])) {
						GamaColorMenu.colorComp = GamaColorMenu.byBrightness;
					} else {
						GamaColorMenu.colorComp = GamaColorMenu.byLuminescence;
					}
					GamaColorMenu.instance.reset();
				}
			});
	public static GamaPreferences.Entry<Boolean> COLOR_MENU_REVERSE = GamaPreferences
			.create("menu.colors.reverse", "Reverse order", false, IType.BOOL).in(GamaPreferences.UI).group("Menus")
			.addChangeListener(new IPreferenceChangeListener<Boolean>() {

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
	public static GamaPreferences.Entry<Boolean> COLOR_MENU_GROUP = GamaPreferences
			.create("menu.colors.group", "Group colors", false, IType.BOOL).in(GamaPreferences.UI).group("Menus")
			.addChangeListener(new IPreferenceChangeListener<Boolean>() {

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
						mgr.setEnabled(NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID, newValue);
					} catch (final CoreException e) {
						e.printStackTrace();
					}

				}
			});

	@Override
	public void run(final Runnable r) {
		WorkbenchHelper.run(r);
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
		if (creator != null) {
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
				final EditorsDialog dialog = new EditorsDialog(scope, WorkbenchHelper.getShell(), initialValues, types,
						title);
				result.putAll(dialog.open() == Window.OK ? dialog.getValues() : initialValues);
			}
		});
		return result;
	}

	public void openUserControlDialog(final IScope scope, final UserPanelStatement panel) {
		run(new Runnable() {

			@Override
			public void run() {
				if (userDialogFactory != null) {
					userDialogFactory.openUserDialog(scope, panel);
				}
			}
		});

	}

	@Override
	public void openUserControlPanel(final IScope scope, final UserPanelStatement panel) {
		run(new Runnable() {

			@Override
			public void run() {
				IGamaView.User part = null;
				part = (User) showView(USER_CONTROL_VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
				if (part != null) {
					part.initFor(scope, panel);
				}
				scope.setOnUserHold(true);
				try {
					WorkbenchHelper.getPage().showView(USER_CONTROL_VIEW_ID);
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
				if (userDialogFactory != null) {
					userDialogFactory.closeUserDialog();
				}
				WorkbenchHelper.hideView(USER_CONTROL_VIEW_ID);

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
		if (eObject instanceof String) {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IFile file = workspace.getRoot().getFile(new Path((String) eObject));
			editModel(file);
		} else if (eObject instanceof IFile) {
			final IFile file = (IFile) eObject;
			if (!file.exists()) {
				debug("File " + file.getFullPath().toString() + " does not exist in the workspace");
				return;
			}
			try {
				final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
						.getDefaultEditor(file.getName());
				WorkbenchHelper.getPage().openEditor(new FileEditorInput(file), desc.getId());
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
				if (!exp.hasParametersOrUserCommands()) {
					return;
				}
				final IGamaView.Parameters view = (Parameters) showView(PARAMETER_VIEW_ID, null,
						IWorkbenchPage.VIEW_ACTIVATE);
				view.addItem(exp);
				view.updateItemValues();

			}

		});
	}

	@Override
	public void showParameterView(final IExperimentPlan exp) {

		run(new Runnable() {

			@Override
			public void run() {
				if (!exp.hasParametersOrUserCommands()) {
					return;
				}
				final IGamaView.Parameters view = (Parameters) showView(PARAMETER_VIEW_ID, null,
						IWorkbenchPage.VIEW_VISIBLE);
				view.addItem(exp);
			}

		});
	}

	// public static Font getBigfont() {
	// if ( bigFont == null ) {
	// initFonts();
	// }
	// return bigFont;
	// }

	/**
	 * Method setSelectedAgent()
	 * 
	 * @see msi.gama.common.interfaces.IGui#setSelectedAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void setSelectedAgent(final IAgent a) {
		WorkbenchHelper.asyncRun(new Runnable() {

			@Override
			public void run() {
				if (WorkbenchHelper.getPage() == null) {
					return;
				}
				// final IViewReference r =
				// getPage().findViewReference(scope.getGui().AGENT_VIEW_ID,
				// "");
				// if ( r == null ) {
				if (a == null) {
					return;
				}
				try {
					final InspectDisplayOutput output = new InspectDisplayOutput(a);
					output.launch(a.getScope());
				} catch (final GamaRuntimeException g) {
					g.addContext("In opening the agent inspector");
					GAMA.reportError(GAMA.getRuntimeScope(), g, false);
				}
				final IViewReference r = WorkbenchHelper.getPage().findViewReference(IGui.AGENT_VIEW_ID, "");
				if (r != null) {
					WorkbenchHelper.getPage().bringToTop(r.getPart(true));
				}
				// }
				// AgentInspectView v =
				// (AgentInspectView) showView(scope.getGui().AGENT_VIEW_ID,
				// null, IWorkbenchPage.VIEW_VISIBLE);
				// v.inspectAgent(a);
			}
		});
	}

	@Override
	public void prepareForSimulation(final SimulationAgent agent) {
		clearErrors();
	}

	@Override
	public void prepareForExperiment(final IExperimentPlan exp) {
		if (exp.isGui()) {
			if (openGLStartupSequence != null) {
				openGLStartupSequence.run();
				openGLStartupSequence = null;
			}
			WorkbenchHelper.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
			updateParameterView(exp);
			showConsoleView(exp.getAgent());
		} else {
			status = null;
		}
	}

	/**
	 * Method cleanAfterExperiment()
	 * 
	 * @see msi.gama.common.interfaces.IGui#cleanAfterExperiment(msi.gama.kernel.experiment.IExperimentPlan)
	 */
	@Override
	public void cleanAfterExperiment(final IExperimentPlan exp) {
		WorkbenchHelper.hideView(PARAMETER_VIEW_ID);
		hideMonitorView();
		eraseConsole(true);
		final IGamaView icv = getInteractiveConsole();
		if (icv != null)
			icv.reset();
	}

	/**
	 * Method cleanAfterSimulation()
	 * 
	 * @see msi.gama.common.interfaces.IGui#cleanAfterSimulation()
	 */
	@Override
	public void cleanAfterSimulation() {
		setSelectedAgent(null);
		setHighlightedAgent(null);
		status.resume();
		// AD: Fix for issue #1342 -- verify that it does not break something
		// else in the dynamics of closing/opening
		closeDialogs();
	}

	/**
	 * Method waitForViewsToBeInitialized()
	 * 
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
	 * 
	 * @see msi.gama.common.interfaces.IGui#getFirstDisplaySurface()
	 */
	public static IDisplaySurface getFirstDisplaySurface() {
		final IViewReference[] viewRefs = WorkbenchHelper.getPage().getViewReferences();
		for (final IViewReference ref : viewRefs) {
			final IWorkbenchPart part = ref.getPart(false);
			if (part instanceof IGamaView.Display) {
				return ((IGamaView.Display) part).getDisplaySurface();
			}
		}
		return null;
	}

	public static void setSpeedControl(final ISpeedDisplayer d) {
		speedStatus = d;
	}

	/**
	 * Method updateSpeedDisplay()
	 * 
	 * @see msi.gama.common.interfaces.IGui#updateSpeedDisplay(java.lang.Double)
	 */
	@Override
	public void updateSpeedDisplay(final Double d, final boolean notify) {
		if (speedStatus != null) {
			WorkbenchHelper.asyncRun(new Runnable() {

				@Override
				public void run() {
					speedStatus.setInit(d, notify);
				}
			});

		}
	}

	/**
	 * Method setStatus()
	 * 
	 * @see msi.gama.common.interfaces.IGui#setStatus(java.lang.String)
	 */
	@Override
	public void setSubStatusCompletion(final double s) {
		status.updateWith(new SubTaskMessage(s));
	}

	/**
	 * Method beginSubStatus()
	 * 
	 * @see msi.gama.common.interfaces.IGui#beginSubStatus(java.lang.String)
	 */
	@Override
	public void beginSubStatus(final String name) {
		status.updateWith(new SubTaskMessage(name, true));
	}

	/**
	 * Method endSubStatus()
	 * 
	 * @see msi.gama.common.interfaces.IGui#endSubStatus(java.lang.String)
	 */
	@Override
	public void endSubStatus(final String name) {
		status.updateWith(new SubTaskMessage(name, false));
	}

	/**
	 * Method getMetaDataProvider()
	 * 
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

				for (final IViewReference view : views) {
					final IViewPart part = view.getView(false);
					if (part instanceof IGamaView) {
						((IGamaView) part).close();

					}
				}
				if (openModelingPerspective) {

					PerspectiveHelper.openModelingPerspective(immediately);

				}
				neutralStatus("No simulation running");
			}
		});

	}

	@Override
	public String getFrontmostSimulationState() {
		final IExperimentController controller = GAMA.getFrontmostController();
		if (controller == null) {
			return NONE;
		} else if (controller.getScheduler().paused) {
			return PAUSED;
		}
		return RUNNING;
	}

	@Override
	public void updateSimulationState(final String forcedState) {
		if (state != null) {
			WorkbenchHelper.asyncRun(new Runnable() {

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

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.common.interfaces.IGui#getViews(java.lang.String,
	 * java.lang.String)
	 */
	// @Override
	// public Set<String> getViews(final String modelName, final String
	// expeName) {
	// final String key = modelName + expeName;
	// final Set<String> ids = MODEL_VIEWS.get(key);
	// return ids == null ? Collections.EMPTY_SET : ids;
	// }

	static Runnable openGLStartupSequence;

	/**
	 * @param runnable
	 */
	public static void setOpenGLStartupSequence(final Runnable runnable) {
		openGLStartupSequence = runnable;
	}
	//
	// @Override
	// public void applyLayout(final int layout) {
	// if ( layout == IUnits.none )
	// return;
	// asyncRun(new Runnable() {
	//
	// @Override
	// public void run() {
	// ArrangeDisplayViews.execute(layout);
	//
	// }
	// });
	//
	// }

	@Override
	public void updateViewTitle(final IDisplayOutput out, final SimulationAgent agent) {
		final IGamaView part = this.findView(out);
		if (part != null)
			this.run(new Runnable() {

				@Override
				public void run() {
					part.changePartNameWithSimulation(agent);

				}

			});

	}

	private static volatile boolean isRequesting;

	public static void requestUserAttention(final IGamaView part, final String tempMessage) {
		if (isRequesting)
			return;
		// rate at which the title will change in milliseconds
		final int rateOfChange = 200;
		final int numberOfTimes = 2;

		// flash n times and thats it
		final String orgText = part.getPartName();

		for (int x = 0; x < numberOfTimes; x++) {
			WorkbenchHelper.getDisplay().timerExec(2 * rateOfChange * x - rateOfChange, new Runnable() {

				@Override
				public void run() {
					isRequesting = true;
					part.setName(tempMessage);
				}
			});
			WorkbenchHelper.getDisplay().timerExec(2 * rateOfChange * x, new Runnable() {

				@Override
				public void run() {
					part.setName(orgText);
					isRequesting = false;
				}
			});
		}
	}

	@Override
	public void updateDecorator(final String id) {
		WorkbenchHelper.asyncRun(new Runnable() {

			@Override
			public void run() {
				WorkbenchHelper.getWorkbench().getDecoratorManager().update("msi.gama.application.decorator");
			}
		});

	}

	@Override
	public void setRestartRequiredAfterPreferenceSet() {
		GamaPreferencesView.setRestartRequired();

	}

	@Override
	public boolean confirm(final String title, final String message) {
		return Messages.confirm(title, message);
	}

	@Override
	public void openModelingPerspective(final boolean immediately) {
		PerspectiveHelper.openModelingPerspective(immediately);

	}

	@Override
	public boolean isSimulationPerspective() {
		return PerspectiveHelper.isSimulationPerspective();
	}

	@Override
	public void asyncRun(final Runnable runnable) {
		WorkbenchHelper.asyncRun(runnable);

	}

	@Override
	public void cleanUpUI() {
		CleanupHelper.run();
	}

}
