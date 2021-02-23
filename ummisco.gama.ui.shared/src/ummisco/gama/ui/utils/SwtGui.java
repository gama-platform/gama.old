/*******************************************************************************************************
 *
 * ummisco.gama.ui.utils.SwtGui.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.application.workbench.PerspectiveHelper.SimulationPerspectiveDescriptor;
import msi.gama.common.interfaces.IConsoleDisplayer;
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
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.ExperimentOutputManager;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.ISimulationStateProvider;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.compilation.Symbol;
import msi.gaml.statements.test.CompoundSummary;
import msi.gaml.statements.test.TestExperimentSummary;
import ummisco.gama.dev.utils.DEBUG;
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

	static {
		DEBUG.ON();
	}

	public volatile static boolean ALL_TESTS_RUNNING;

	private IAgent highlightedAgent;
	private ILocation mouseLocationInModel;

	static {
		// GamaFonts.setLabelFont(PreferencesHelper.BASE_BUTTON_FONT.getValue());
		PreferencesHelper.initialize();
	}

	public SwtGui() {}

	@Override
	public boolean confirmClose(final IExperimentPlan exp) {
		if (exp == null || !GamaPreferences.Runtime.CORE_ASK_CLOSING.getValue()) { return true; }
		PerspectiveHelper.switchToSimulationPerspective();
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
					// DEBUG.LOG("Opening view " + viewId + " " + second);
					result[0] = page.showView(viewId, second, code);
				}
			} catch (final Exception e) {
				result[0] = e;
			}
		});
		return result[0];
	}

	@Override
	public boolean copyToClipboard(final String text) {
		WorkbenchHelper.asyncRun(() -> {
			final Clipboard clipboard = new Clipboard(WorkbenchHelper.getDisplay());
			final TextTransfer textTransfer = TextTransfer.getInstance();
			final Transfer[] transfers = new Transfer[] { textTransfer };
			final Object[] data = new Object[] { text };
			clipboard.setContents(data, transfers);
			clipboard.dispose();
		});
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
	public final boolean openSimulationPerspective(final IModel model, final String experimentName) {
		return PerspectiveHelper.openSimulationPerspective(model, experimentName);
	}

	@Override
	public DisplayDescription getDisplayDescriptionFor(final String name) {
		return DISPLAYS.get(name);
	}

	@Override
	public IDisplaySurface getDisplaySurfaceFor(final LayeredDisplayOutput output, final Object... args) {
		IDisplaySurface surface = null;
		final String keyword = output.getData().getDisplayType();
		final DisplayDescription creator = DISPLAYS.get(keyword);
		if (creator != null) {
			surface = creator.create(output, args);
			surface.outputReloaded();
		} else {
			throw GamaRuntimeException.error("Display " + keyword + " is not defined anywhere.", output.getScope());
		}
		return surface;
	}

	@Override
	public Map<String, Object> openUserInputDialog(final IScope scope, final String title,
			final List<IParameter> parameters, final GamaFont font) {
		final IMap<String, Object> result = GamaMapFactory.createUnordered();
		for (final IParameter p : parameters) {
			result.put(p.getName(), p.getInitialValue(scope));
		}
		WorkbenchHelper.run(() -> {
			final EditorsDialog dialog = new EditorsDialog(scope, WorkbenchHelper.getShell(), parameters, title, font);
			if (dialog.open() == Window.OK) {
				result.putAll(dialog.getValues());
			}
		});
		return result;
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
			// hideScreen();
			final IOpenGLInitializer initializer = WorkbenchHelper.getService(IOpenGLInitializer.class);
			if (initializer != null && !initializer.isDone()) {
				initializer.run();
			}
			WorkbenchHelper.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
			final ExperimentAgent agent = exp.getAgent();
			final ExperimentOutputManager manager = (ExperimentOutputManager) agent.getOutputManager();
			Symbol layout = manager.getLayout();
			if (layout == null) {
				layout = manager;
			}
			final Boolean keepTabs = layout.getFacetValue(scope, "tabs", true);
			final Boolean keepToolbars = layout.getFacetValue(scope, "toolbars", null);
			final Boolean showParameters = layout.getFacetValue(scope, "parameters", null);
			final Boolean showConsoles = layout.getFacetValue(scope, "consoles", null);
			final Boolean showNavigator = layout.getFacetValue(scope, "navigator", null);
			final Boolean showControls = layout.getFacetValue(scope, "controls", null);
			final Boolean keepTray = layout.getFacetValue(scope, "tray", null);
			boolean showEditors;
			if (layout.hasFacet("editors")) {
				showEditors = layout.getFacetValue(scope, "editors", false);
			} else {
				showEditors = !GamaPreferences.Modeling.EDITOR_PERSPECTIVE_HIDE.getValue();
			}
			WorkbenchHelper.runInUI("Arranging views", 0, (m) -> {
				WorkbenchHelper.getPage().setEditorAreaVisible(showEditors);
				if (showConsoles != null && !showConsoles) {
					WorkbenchHelper.hideView(IGui.CONSOLE_VIEW_ID);
					WorkbenchHelper.hideView(IGui.INTERACTIVE_CONSOLE_VIEW_ID);
				} else {
					getConsole().showConsoleView(exp.getAgent());
				}
				if (showParameters != null && !showParameters) {
					WorkbenchHelper.hideView(IGui.PARAMETER_VIEW_ID);
				} else {
					updateParameterView(scope, exp);
				}
				if (showNavigator != null && !showNavigator) {
					WorkbenchHelper.hideView(IGui.NAVIGATOR_VIEW_ID);
				}
				if (showControls != null) {
					WorkbenchHelper.getWindow().setCoolBarVisible(showControls);
				}
				if (keepTray != null) {
					PerspectiveHelper.showBottomTray(WorkbenchHelper.getWindow(), keepTray);
				}

				final SimulationPerspectiveDescriptor sd = PerspectiveHelper.getActiveSimulationPerspective();
				if (sd != null) {
					sd.keepTabs(keepTabs);
					sd.keepToolbars(keepToolbars);
					sd.keepControls(showControls);
					sd.keepTray(keepTray);
				}
			});

		}

	}

	/**
	 * Method cleanAfterExperiment()
	 *
	 * @see msi.gama.common.interfaces.IGui#cleanAfterExperiment(msi.gama.kernel.experiment.IExperimentPlan)
	 */
	@Override
	public void cleanAfterExperiment() {
		WorkbenchHelper.hideView(PARAMETER_VIEW_ID);
		hideMonitorView();
		getConsole().eraseConsole(true);
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
			final IWorkbenchPage page = WorkbenchHelper.getPage();
			final IViewReference[] views = page.getViewReferences();

			for (final IViewReference view : views) {
				final IViewPart part = view.getView(false);
				if (part instanceof IGamaView) {
					DEBUG.OUT("Closing " + view.getId());
					((IGamaView) part).close(scope);
				}
			}
			if (openModelingPerspective) {
				DEBUG.OUT("Deleting simulation perspective and opening immediately the modeling perspective = "
						+ immediately);
				PerspectiveHelper.deleteCurrentSimulationPerspective();
				PerspectiveHelper.openModelingPerspective(immediately, false);
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
		// DEBUG.OUT("STATE: " + forcedState);
		final ISourceProviderService service = WorkbenchHelper.getService(ISourceProviderService.class);
		final ISimulationStateProvider stateProvider = (ISimulationStateProvider) service
				.getSourceProvider("ummisco.gama.ui.experiment.SimulationRunningState");
		if (stateProvider != null) {
			WorkbenchHelper.run(() -> stateProvider.updateStateTo(forcedState));
		}
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
	public IConsoleDisplayer getConsole() {
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
	public void applyLayout(final IScope scope, final Object layout) {
		final IDisplayLayoutManager manager = WorkbenchHelper.getService(IDisplayLayoutManager.class);
		if (manager != null) {
			manager.applyLayout(layout);
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
	public boolean isInDisplayThread() {
		return EventQueue.isDispatchThread() || Display.getCurrent() != null;
	}

}
