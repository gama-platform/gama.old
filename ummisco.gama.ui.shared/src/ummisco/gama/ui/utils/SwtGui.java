/*********************************************************************************************
 *
 * 'SwtGui.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import gnu.trove.map.hash.THashMap;
import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.common.interfaces.IConsoleDisplayer;
import msi.gama.common.interfaces.IDisplayCreator;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGamaView.Error;
import msi.gama.common.interfaces.IGamaView.Parameters;
import msi.gama.common.interfaces.IGamaView.Test;
import msi.gama.common.interfaces.IGamaView.User;
import msi.gama.common.interfaces.IGamlLabelProvider;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IRuntimeExceptionHandler;
import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.ISimulationStateProvider;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.statements.test.CompoundSummary;
import msi.gaml.statements.test.TestExperimentSummary;
import msi.gaml.types.IType;
import ummisco.gama.ui.dialogs.Messages;
import ummisco.gama.ui.interfaces.IDisplayLayoutManager;
import ummisco.gama.ui.interfaces.IModelRunner;
import ummisco.gama.ui.interfaces.IOpenGLInitializer;
import ummisco.gama.ui.interfaces.IRefreshHandler;
import ummisco.gama.ui.interfaces.ISpeedDisplayer;
import ummisco.gama.ui.interfaces.IUserDialogFactory;
import ummisco.gama.ui.parameters.EditorsDialog;

/**
 * Written by drogoul Modified on 6 mai 2011
 *
 * @todo Description
 *
 */
public class SwtGui implements IGui {

	public volatile static boolean ALL_TESTS_RUNNING;

	private IAgent highlightedAgent;
	private ILocation mouseLocationInModel;

	static {
		// GamaFonts.setLabelFont(PreferencesHelper.BASE_BUTTON_FONT.getValue());
		PreferencesHelper.initialize();
	}

	public SwtGui() {
		updateExperimentState(null, NONE);
	}

	@Override
	public void debug(final String msg) {
		System.err.println(msg);
	}

	@Override
	public boolean confirmClose(final IExperimentPlan exp) {
		if (exp == null || !GamaPreferences.Runtime.CORE_ASK_CLOSING.getValue()) { return true; }
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
	public void runtimeError(final IScope scope, final GamaRuntimeException g) {
		if (g.isReported()) { return; }
		if (GAMA.getFrontmostController() != null && GAMA.getFrontmostController().isDisposing()) { return; }
		final IRuntimeExceptionHandler handler = getRuntimeExceptionHandler();
		if (!handler.isRunning()) {
			handler.start();
		}
		handler.offer(g);
		g.setReported();
	}

	@Override
	public void displayErrors(final IScope scope, final List<GamaRuntimeException> exceptions) {
		if (exceptions == null) {
			WorkbenchHelper.hideView(ERROR_VIEW_ID);
		} else {
			final IGamaView.Error v = (Error) showView(scope, ERROR_VIEW_ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			if (v != null) {
				v.displayErrors();
			}
		}
	}

	@Override
	public IGamaView.Test openTestView(final IScope scope, final boolean allTests) {
		ALL_TESTS_RUNNING = allTests;
		final IGamaView.Test v = (Test) showView(scope, TEST_VIEW_ID, null, IWorkbenchPage.VIEW_ACTIVATE);
		if (v != null) {
			v.startNewTestSequence(allTests);
		}
		return v;
	}

	@Override
	public void displayTestsResults(final IScope scope, final CompoundSummary<?, ?> summary) {
		final IGamaView.Test v = (Test) WorkbenchHelper.getPage().findView(TEST_VIEW_ID);
		if (v != null) {
			v.addTestResult(summary);
		}
	}

	@Override
	public void endTestDisplay() {
		final IGamaView.Test v = (Test) WorkbenchHelper.getPage().findView(TEST_VIEW_ID);
		if (v != null) {
			v.finishTestSequence();
		}
		WorkbenchHelper.getService(IRefreshHandler.class).refreshNavigator();
	}

	@Override
	public void clearErrors(final IScope scope) {
		final IRuntimeExceptionHandler handler = getRuntimeExceptionHandler();
		handler.clearErrors();
	}

	private Object internalShowView(final String viewId, final String secondaryId, final int code) {
		if (GAMA.getFrontmostController() != null && GAMA.getFrontmostController().isDisposing()) { return null; }
		final Object[] result = new Object[1];
		WorkbenchHelper.run(() -> {
			try {
				final IWorkbenchPage page = WorkbenchHelper.getPage();
				if (page != null) {
					page.zoomOut();
					final String second = secondaryId == null ? null
							: secondaryId + "@@@" + String.valueOf(System.currentTimeMillis());
					// The goal here is to address #2441 by randomizing the ids of views.
					// System.out.println("Opening view " + viewId + " " + second);
					result[0] = page.showView(viewId, second, code);
				}
			} catch (final Exception e) {
				result[0] = e;
			}
		});
		return result[0];
	}

	public static boolean isInternetReachable() {

		try {
			final URL url = new URL("http://gama-platform.org");
			// open a connection to that source
			final HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
			// trying to retrieve data from the source. If there
			// is no connection, this line will fail
			urlConnect.setConnectTimeout(2000);
			urlConnect.getContent();
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
		WebHelper.openWelcomePage(ifEmpty);
	}

	@Override
	public IGamaView showView(final IScope scope, final String viewId, final String secondaryId, final int code) {

		Object o = internalShowView(viewId, secondaryId, code);
		if (o instanceof IWorkbenchPart) {
			if (o instanceof IGamaView) { return (IGamaView) o; }
			o = GamaRuntimeException.error("Impossible to open view " + viewId, GAMA.getRuntimeScope());
		}
		if (o instanceof Throwable) {
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
	public final boolean openSimulationPerspective(final boolean immediately) {
		final IModel model = GAMA.getModel();
		if (model == null) { return false; }
		final IExperimentPlan p = GAMA.getExperiment();
		if (p == null) { return false; }
		final String id = p.getName();
		return openSimulationPerspective(model, id, immediately);
	}

	@Override
	public final boolean openSimulationPerspective(final IModel model, final String experimentName,
			final boolean immediately) {
		if (model == null) { return false; }
		final String name = PerspectiveHelper.getNewPerspectiveName(model.getName(), experimentName);
		return PerspectiveHelper.openPerspective(name, immediately, false);

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
			final Map<String, Object> initialValues, final Map<String, IType<?>> types) {
		final Map<String, Object> result = new THashMap<>();
		WorkbenchHelper.run(() -> {
			final EditorsDialog dialog =
					new EditorsDialog(scope, WorkbenchHelper.getShell(), initialValues, types, title);
			result.putAll(dialog.open() == Window.OK ? dialog.getValues() : initialValues);
		});
		return result;
	}

	public void openUserControlDialog(final IScope scope, final UserPanelStatement panel) {
		WorkbenchHelper.run(() -> {
			final IUserDialogFactory userDialogFactory = WorkbenchHelper.getService(IUserDialogFactory.class);
			if (userDialogFactory != null) {
				userDialogFactory.openUserDialog(scope, panel);
			}
		});

	}

	@Override
	public void openUserControlPanel(final IScope scope, final UserPanelStatement panel) {
		WorkbenchHelper.run(() -> {
			IGamaView.User part = null;
			part = (User) showView(scope, USER_CONTROL_VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
			if (part != null) {
				part.initFor(scope, panel);
			}
			scope.setOnUserHold(true);
			try {
				WorkbenchHelper.getPage().showView(USER_CONTROL_VIEW_ID);
			} catch (final PartInitException e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void closeDialogs(final IScope scope) {

		WorkbenchHelper.run(() -> {
			final IUserDialogFactory userDialogFactory = WorkbenchHelper.getService(IUserDialogFactory.class);
			if (userDialogFactory != null) {
				userDialogFactory.closeUserDialog();
			}
			WorkbenchHelper.hideView(USER_CONTROL_VIEW_ID);

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

	private IModelRunner getModelRunner() {
		return WorkbenchHelper.getService(IModelRunner.class);
	}

	@Override
	public void editModel(final IScope scope, final Object eObject) {
		final IModelRunner modelRunner = getModelRunner();
		if (modelRunner == null) { return; }
		modelRunner.editModel(eObject);
	}

	@Override
	public List<TestExperimentSummary> runHeadlessTests(final Object model) {
		final IModelRunner modelRunner = getModelRunner();
		if (modelRunner == null) { return null; }
		return modelRunner.runHeadlessTests(model);
	}

	@Override
	public void updateParameterView(final IScope scope, final IExperimentPlan exp) {

		WorkbenchHelper.run(() -> {
			if (!exp.hasParametersOrUserCommands()) { return; }
			final IGamaView.Parameters view =
					(Parameters) showView(scope, PARAMETER_VIEW_ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			view.addItem(exp);
			view.updateItemValues();

		});
	}

	@Override
	public void showParameterView(final IScope scope, final IExperimentPlan exp) {

		WorkbenchHelper.run(() -> {
			if (!exp.hasParametersOrUserCommands()) { return; }
			final IGamaView.Parameters view =
					(Parameters) showView(scope, PARAMETER_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
			if (view != null) {
				view.addItem(exp);
			}
		});
	}

	/**
	 * Method setSelectedAgent()
	 * 
	 * @see msi.gama.common.interfaces.IGui#setSelectedAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void setSelectedAgent(final IAgent a) {
		WorkbenchHelper.asyncRun(() -> {
			if (WorkbenchHelper.getPage() == null) { return; }
			if (a == null) { return; }
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
		});
	}

	@Override
	public void prepareForExperiment(final IScope scope, final IExperimentPlan exp) {
		if (exp.isGui()) {
			final IOpenGLInitializer initializer = WorkbenchHelper.getService(IOpenGLInitializer.class);
			if (initializer != null && !initializer.isDone()) {
				initializer.run();
			}
			WorkbenchHelper.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
			updateParameterView(scope, exp);
			getConsole(scope).showConsoleView(exp.getAgent());
		}
	}

	/**
	 * Method cleanAfterExperiment()
	 * 
	 * @see msi.gama.common.interfaces.IGui#cleanAfterExperiment(msi.gama.kernel.experiment.IExperimentPlan)
	 */
	@Override
	public void cleanAfterExperiment(final IScope scope) {
		WorkbenchHelper.hideView(PARAMETER_VIEW_ID);
		hideMonitorView();
		getConsole(null).eraseConsole(true);
		final IGamaView icv = (IGamaView) WorkbenchHelper.findView(INTERACTIVE_CONSOLE_VIEW_ID, null, false);
		if (icv != null) {
			icv.reset();
		}
		final IRuntimeExceptionHandler handler = getRuntimeExceptionHandler();
		handler.stop();
	}

	private IRuntimeExceptionHandler getRuntimeExceptionHandler() {
		return WorkbenchHelper.getService(IRuntimeExceptionHandler.class);
	}

	@Override
	public void runModel(final Object object, final String exp) {
		final IModelRunner modelRunner = getModelRunner();
		if (modelRunner == null) { return; }
		modelRunner.runModel(object, exp);
	}

	public static List<IDisplaySurface> allDisplaySurfaces() {
		final List<IDisplaySurface> result = new ArrayList<>();
		final IViewReference[] viewRefs = WorkbenchHelper.getPage().getViewReferences();
		for (final IViewReference ref : viewRefs) {
			final IWorkbenchPart part = ref.getPart(false);
			if (part instanceof IGamaView.Display) {
				result.add(((IGamaView.Display) part).getDisplaySurface());
			}
		}
		return result;
	}

	/**
	 * Method updateSpeedDisplay()
	 * 
	 * @see msi.gama.common.interfaces.IGui#updateSpeedDisplay(java.lang.Double)
	 */
	@Override
	public void updateSpeedDisplay(final IScope scope, final Double d, final boolean notify) {
		final ISpeedDisplayer speedStatus = WorkbenchHelper.getService(ISpeedDisplayer.class);
		if (speedStatus != null) {
			WorkbenchHelper.asyncRun(() -> speedStatus.setInit(d, notify));

		}
	}

	/**
	 * Method getMetaDataProvider()
	 * 
	 * @see msi.gama.common.interfaces.IGui#getMetaDataProvider()
	 */
	@Override
	public IFileMetaDataProvider getMetaDataProvider() {
		return WorkbenchHelper.getService(IFileMetaDataProvider.class);
	}

	@Override
	public IGamlLabelProvider getGamlLabelProvider() {
		return WorkbenchHelper.getService(IGamlLabelProvider.class);
	}

	@Override
	public void closeSimulationViews(final IScope scope, final boolean openModelingPerspective,
			final boolean immediately) {
		WorkbenchHelper.run(() -> {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final IViewReference[] views = page.getViewReferences();

			for (final IViewReference view : views) {
				final IViewPart part = view.getView(false);
				if (part instanceof IGamaView) {
					((IGamaView) part).close(scope);

				}
			}
			if (openModelingPerspective) {
				PerspectiveHelper.deleteCurrentSimulationPerspective();
				PerspectiveHelper.openModelingPerspective(immediately);
			}

			getStatus(scope).neutralStatus("No simulation running");
		});

	}

	@Override
	public String getExperimentState(final String uid) {
		final IExperimentController controller = GAMA.getFrontmostController();
		if (controller == null) {
			return NONE;
		} else if (controller.getScheduler().paused) { return PAUSED; }
		return RUNNING;
	}

	@Override
	public void updateExperimentState(final IScope scope, final String forcedState) {
		// System.out.println("STATE: " + forcedState);
		final ISourceProviderService service = WorkbenchHelper.getService(ISourceProviderService.class);
		final ISimulationStateProvider stateProvider = (ISimulationStateProvider) service
				.getSourceProvider("ummisco.gama.ui.experiment.SimulationRunningState");
		// stateProvider.updateStateTo(forcedState);
		if (stateProvider != null) {
			WorkbenchHelper.run(() -> stateProvider.updateStateTo(forcedState));
		}
		/*
		 * WorkbenchHelper.run(() -> { WorkbenchHelper.getWindow().getShell().forceActive(); });
		 */
	}

	@Override
	public void updateExperimentState(final IScope scope) {
		updateExperimentState(scope, getExperimentState(""));
	}

	@Override
	public void updateViewTitle(final IDisplayOutput out, final SimulationAgent agent) {
		WorkbenchHelper.run(() -> {
			final IViewPart part =
					WorkbenchHelper.findView(out.getViewId(), out.isUnique() ? null : out.getName(), true);
			if (part != null && part instanceof IGamaView) {
				((IGamaView) part).changePartNameWithSimulation(agent);
			}
		});

	}

	@Override
	public void updateDecorator(final String id) {
		WorkbenchHelper.asyncRun(() -> WorkbenchHelper.getWorkbench().getDecoratorManager().update(id));

	}

	@Override
	public IStatusDisplayer getStatus(final IScope scope) {
		return WorkbenchHelper.getService(IStatusDisplayer.class);
	}

	@Override
	public IConsoleDisplayer getConsole(final IScope scope) {
		return WorkbenchHelper.getService(IConsoleDisplayer.class);
	}

	@Override
	public void run(final String taskName, final Runnable r, final boolean asynchronous) {

		if (asynchronous) {
			WorkbenchHelper.runInUI(taskName, 0, (m) -> r.run());
		} else {
			WorkbenchHelper.run(r);
		}
	}

	@Override
	public void setFocusOn(final IShape shape) {
		for (final IDisplaySurface surface : this.allDisplaySurfaces()) {
			surface.focusOn(shape);
		}
		GAMA.getExperiment().refreshAllOutputs();
	}

	@Override
	public void applyLayout(final IScope scope, final Object layout, final boolean keepTabs, final boolean keepToolbars,
			final boolean showEditors) {
		final IDisplayLayoutManager manager = WorkbenchHelper.getService(IDisplayLayoutManager.class);
		if (manager != null) {
			manager.applyLayout(layout, keepTabs, keepToolbars, showEditors);
		}
	}

	@Override
	public ILocation getMouseLocationInModel() {
		return mouseLocationInModel;
	}

	@Override
	public void setMouseLocationInModel(final ILocation location) {
		mouseLocationInModel = location;
	}

	@Override
	public void exit() {
		WorkbenchHelper.asyncRun(() -> PlatformUI.getWorkbench().close());

	}

	@Override
	public void openInteractiveConsole(final IScope scope) {
		this.showView(scope, INTERACTIVE_CONSOLE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);

	}

	@Override
	public boolean toggleFullScreenMode() {
		final IViewPart part = WorkbenchHelper.findFrontmostGamaViewUnderMouse();
		if (part instanceof IGamaView.Display) {
			((IGamaView.Display) part).toggleFullScreen();
			return true;
		}
		return false;
	}

	@Override
	public void refreshNavigator() {
		final IRefreshHandler refresh = WorkbenchHelper.getService(IRefreshHandler.class);
		if (refresh != null) {
			refresh.completeRefresh(null);
		}

	}

	@Override
	public void hideScreen() {
		final IDisplayLayoutManager manager = WorkbenchHelper.getService(IDisplayLayoutManager.class);
		if (manager != null) {
			manager.hideScreen();
		}
	}

	@Override
	public void showScreen() {
		final IDisplayLayoutManager manager = WorkbenchHelper.getService(IDisplayLayoutManager.class);
		if (manager != null) {
			manager.showScreen();
		}
	}

}
