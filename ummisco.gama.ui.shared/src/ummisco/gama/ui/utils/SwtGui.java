/*******************************************************************************************************
 *
 * SwtGui.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import static ummisco.gama.ui.utils.ViewsHelper.hideView;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
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
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IRuntimeExceptionHandler;
import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.ImageUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.ExperimentOutputManager;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.ISimulationStateProvider;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.ActionDescription;
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
import ummisco.gama.ui.parameters.GamaWizard;
import ummisco.gama.ui.parameters.GamaWizardDialog;
import ummisco.gama.ui.parameters.GamaWizardPage;
import ummisco.gama.ui.resources.GamaColors;

/**
 * Written by drogoul Modified on 6 mai 2011
 *
 * @todo Description
 *
 */
public class SwtGui implements IGui {

	static {
		DEBUG.OFF();
	}

	/** The all tests running. */
	public volatile static boolean ALL_TESTS_RUNNING;

	/** The highlighted agent. */
	private IAgent highlightedAgent;

	/** The mouse location in model. */
	private GamaPoint mouseLocationInModel;

	static {
		// GamaFonts.setLabelFont(PreferencesHelper.BASE_BUTTON_FONT.getValue());
		PreferencesHelper.initialize();
		ImageUtils.getCachedGC();
	}

	/**
	 * Instantiates a new swt gui.
	 */
	public SwtGui() {}

	@Override
	public boolean confirmClose(final IExperimentPlan exp) {
		if (exp == null || !GamaPreferences.Runtime.CORE_ASK_CLOSING.getValue()) return true;
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
		if (g.isReported() || GAMA.getFrontmostController() != null && GAMA.getFrontmostController().isDisposing())
			return;
		final IRuntimeExceptionHandler handler = getRuntimeExceptionHandler();
		if (!handler.isRunning()) { handler.start(); }
		handler.offer(g);
		g.setReported();
	}

	@Override
	public void displayErrors(final IScope scope, final List<GamaRuntimeException> exceptions) {
		if (exceptions == null) {
			hideView(ERROR_VIEW_ID);
		} else {
			final IGamaView.Error v = (Error) showView(scope, ERROR_VIEW_ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			if (v != null) { v.displayErrors(); }
		}
	}

	@Override
	public IGamaView.Test openTestView(final IScope scope, final boolean allTests) {
		ALL_TESTS_RUNNING = allTests;
		final IGamaView.Test v = (Test) showView(scope, TEST_VIEW_ID, null, IWorkbenchPage.VIEW_ACTIVATE);
		if (v != null) { v.startNewTestSequence(allTests); }
		return v;
	}

	@Override
	public void displayTestsResults(final IScope scope, final CompoundSummary<?, ?> summary) {
		final IGamaView.Test v = (Test) WorkbenchHelper.getPage().findView(TEST_VIEW_ID);
		if (v != null) { v.addTestResult(summary); }
	}

	@Override
	public void endTestDisplay() {
		final IGamaView.Test v = (Test) WorkbenchHelper.getPage().findView(TEST_VIEW_ID);
		if (v != null) { v.finishTestSequence(); }
		WorkbenchHelper.refreshNavigator();
	}

	@Override
	public void clearErrors(final IScope scope) {
		final IRuntimeExceptionHandler handler = getRuntimeExceptionHandler();
		handler.clearErrors();
	}

	/**
	 * Internal show view.
	 *
	 * @param viewId
	 *            the view id
	 * @param secondaryId
	 *            the secondary id
	 * @param code
	 *            the code
	 * @return the object
	 */
	private Object internalShowView(final String viewId, final String secondaryId, final int code) {
		if (GAMA.getFrontmostController() != null && GAMA.getFrontmostController().isDisposing()) return null;
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
			final Transfer[] transfers = { textTransfer };
			final Object[] data = { text };
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
			if (o instanceof IGamaView) return (IGamaView) o;
			o = GamaRuntimeException.error("Impossible to open view " + viewId, GAMA.getRuntimeScope());
		}
		if (o instanceof Throwable) {
			GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.create((Exception) o, GAMA.getRuntimeScope()),
					false);
		}
		return null;
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
	public IDisplaySurface createDisplaySurfaceFor(final LayeredDisplayOutput output, final Object... args) {
		IDisplaySurface surface = null;
		final String keyword = output.getData().getDisplayType();
		final DisplayDescription creator = DISPLAYS.get(keyword);
		if (creator == null)
			throw GamaRuntimeException.error("Display " + keyword + " is not defined anywhere.", output.getScope());
		surface = creator.create(output, args);
		surface.outputReloaded();
		return surface;
	}

	@Override
	public Iterable<IDisplaySurface> getAllDisplaySurfaces() { return ViewsHelper.allDisplaySurfaces(); }

	/**
	 * Gets the frontmost display surface.
	 *
	 * @return the frontmost display surface
	 */
	@Override
	public IDisplaySurface getFrontmostDisplaySurface() { return ViewsHelper.frontmostDisplaySurface(); }

	@Override
	public Map<String, Object> openUserInputDialog(final IScope scope, final String title,
			final List<IParameter> parameters, final GamaFont font, final GamaColor color) {
		final IMap<String, Object> result = GamaMapFactory.createUnordered();
		for (final IParameter p : parameters) { result.put(p.getName(), p.getInitialValue(scope)); }
		WorkbenchHelper.run(() -> {
			final EditorsDialog dialog = new EditorsDialog(scope, null, parameters, title, font, color);
			if (dialog.open() == Window.OK) { result.putAll(dialog.getValues()); }
		});
		return result;
	}

	@Override
	public IMap<String, IMap<String, Object>> openWizard(final IScope scope, final String title,
			final ActionDescription finish, final IList<IMap<String, Object>> pages) {
		final IMap<String, IMap<String, Object>> result = GamaMapFactory.create();
		final IList<GamaWizardPage> wizardPages = GamaListFactory.create();
		for (IMap<String, Object> l : pages) {
			GamaFont f = (GamaFont) l.get(IKeyword.FONT);
			String t = (String) l.get(IKeyword.TITLE);
			String d = (String) l.get(IKeyword.DESCRIPTION);
			@SuppressWarnings ("unchecked") List<IParameter> ps = (List<IParameter>) l.get(IKeyword.PARAMETERS);
			wizardPages.add(new GamaWizardPage(scope, ps, t, d, f));

		}

		WorkbenchHelper.run(() -> {
			final GamaWizard wizard = new GamaWizard(title, finish, wizardPages);
			GamaWizardDialog wizardDialog = new GamaWizardDialog(WorkbenchHelper.getShell(), wizard);
			if (wizardDialog.open() == Window.OK) { result.putAll(wizardDialog.getValues()); }
		});
		return result;
	}

	/*
	 * @Override public Map<String, Object> openWizard(final IScope scope, final String title, final List<IParameter>
	 * parameters, final GamaFont font) { final IMap<String, Object> result = GamaMapFactory.createUnordered(); for
	 * (final IParameter p : parameters) { result.put(p.getName(), p.getInitialValue(scope)); } WorkbenchHelper.run(()
	 * -> { final EditorsDialog dialog = new EditorsDialog(scope, WorkbenchHelper.getShell(), parameters, title, font);
	 * if (dialog.open() == Window.OK) { result.putAll(dialog.getValues()); } }); return result; }
	 */

	@Override
	public Boolean openUserInputDialogConfirm(final IScope scope, final String title, final String message) {
		final List<Boolean> result = new ArrayList<>();
		WorkbenchHelper.run(() -> { result.add(Messages.confirm(title, message)); });
		return result.isEmpty() ? false : result.get(0);
	}

	@Override
	public void openUserControlPanel(final IScope scope, final UserPanelStatement panel) {
		WorkbenchHelper.run(() -> {
			IGamaView.User part = null;
			part = (User) showView(scope, USER_CONTROL_VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
			if (part != null) { part.initFor(scope, panel); }
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
			if (userDialogFactory != null) { userDialogFactory.closeUserDialog(); }
			hideView(USER_CONTROL_VIEW_ID);

		});

	}

	@Override
	public IAgent getHighlightedAgent() { return highlightedAgent; }

	@Override
	public void setHighlightedAgent(final IAgent a) { highlightedAgent = a; }

	/**
	 * Gets the model runner.
	 *
	 * @return the model runner
	 */
	private IModelRunner getModelRunner() { return WorkbenchHelper.getService(IModelRunner.class); }

	@Override
	public void editModel(final IScope scope, final Object eObject) {
		final IModelRunner modelRunner = getModelRunner();
		if (modelRunner == null) return;
		modelRunner.editModel(eObject);
	}

	@Override
	public List<TestExperimentSummary> runHeadlessTests(final Object model) {
		final IModelRunner modelRunner = getModelRunner();
		if (modelRunner == null) return null;
		return modelRunner.runHeadlessTests(model);
	}

	@Override
	public void updateParameterView(final IScope scope, final IExperimentPlan exp) {

		WorkbenchHelper.run(() -> {
			if (!exp.hasParametersOrUserCommands()) return;
			final IGamaView.Parameters view =
					(Parameters) showView(scope, PARAMETER_VIEW_ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			view.addItem(exp);
			view.updateItemValues();

		});
	}

	@Override
	public void showParameterView(final IScope scope, final IExperimentPlan exp) {

		WorkbenchHelper.run(() -> {
			if (!exp.hasParametersOrUserCommands()) return;
			final IGamaView.Parameters view =
					(Parameters) showView(scope, PARAMETER_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
			if (view != null) { view.addItem(exp); }
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
			if (WorkbenchHelper.getPage() == null || a == null) return;
			try {
				final InspectDisplayOutput output = InspectDisplayOutput.inspect(a, null);
				output.launch(a.getScope());
			} catch (final GamaRuntimeException g) {
				g.addContext("In opening the agent inspector");
				GAMA.reportError(GAMA.getRuntimeScope(), g, false);
			}
			final IViewReference r = WorkbenchHelper.getPage().findViewReference(IGui.AGENT_VIEW_ID, "");
			if (r != null) { WorkbenchHelper.getPage().bringToTop(r.getPart(true)); }
		});
	}

	@Override
	public void prepareForExperiment(final IScope scope, final IExperimentPlan exp) {
		// hideScreen();
		final IOpenGLInitializer initializer = WorkbenchHelper.getService(IOpenGLInitializer.class);
		if (initializer != null && !initializer.isDone()) { initializer.run(); }
		WorkbenchHelper.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
		final ExperimentAgent agent = exp.getAgent();
		final ExperimentOutputManager manager = (ExperimentOutputManager) agent.getOutputManager();
		Symbol layout = manager.getLayout();
		if (layout == null) { layout = manager; }
		final Boolean keepTabs = layout.getFacetValue(scope, "tabs", true);
		final Boolean keepToolbars = layout.getFacetValue(scope, "toolbars", null);
		final Boolean showParameters = layout.getFacetValue(scope, "parameters", null);
		final Boolean showConsoles = layout.getFacetValue(scope, "consoles", null);
		final Boolean showNavigator = layout.getFacetValue(scope, "navigator", null);
		final Boolean showControls = layout.getFacetValue(scope, "controls", null);
		final Boolean keepTray = layout.getFacetValue(scope, "tray", null);
		final GamaColor color = layout.getFacetValue(scope, "background", null);
		Color background = color == null ? null : GamaColors.toSwtColor(color);
		boolean showEditors;
		if (layout.hasFacet("editors")) {
			showEditors = layout.getFacetValue(scope, "editors", false);
		} else {
			showEditors = !GamaPreferences.Modeling.EDITOR_PERSPECTIVE_HIDE.getValue();
		}
		WorkbenchHelper.runInUI("Arranging views", 0, m -> {
			WorkbenchHelper.getPage().setEditorAreaVisible(showEditors);
			if (showConsoles != null && !showConsoles) {
				hideView(IGui.CONSOLE_VIEW_ID);
				hideView(IGui.INTERACTIVE_CONSOLE_VIEW_ID);
			} else {
				getConsole().showConsoleView(exp.getAgent());
			}
			if (showParameters != null && !showParameters) {
				hideView(IGui.PARAMETER_VIEW_ID);
			} else {
				updateParameterView(scope, exp);
			}
			if (showNavigator != null && !showNavigator) { hideView(IGui.NAVIGATOR_VIEW_ID); }
			if (showControls != null) { WorkbenchHelper.getWindow().setCoolBarVisible(showControls); }
			if (keepTray != null) { PerspectiveHelper.showBottomTray(WorkbenchHelper.getWindow(), keepTray); }

			final SimulationPerspectiveDescriptor sd = PerspectiveHelper.getActiveSimulationPerspective();
			if (sd != null) {
				sd.keepTabs(keepTabs);
				sd.keepToolbars(keepToolbars);
				sd.keepControls(showControls);
				sd.keepTray(keepTray);
				sd.setBackground(background);
			}
		});

	}

	/**
	 * Method cleanAfterExperiment()
	 *
	 * @see msi.gama.common.interfaces.IGui#cleanAfterExperiment(msi.gama.kernel.experiment.IExperimentPlan)
	 */
	@Override
	public void cleanAfterExperiment() {
		hideView(PARAMETER_VIEW_ID);
		final IGamaView m = (IGamaView) ViewsHelper.findView(MONITOR_VIEW_ID, null, false);
		if (m != null) {
			m.reset();
			hideView(MONITOR_VIEW_ID);
		}
		getConsole().eraseConsole(true);
		final IGamaView icv = (IGamaView) ViewsHelper.findView(INTERACTIVE_CONSOLE_VIEW_ID, null, false);
		if (icv != null) { icv.reset(); }
		final IRuntimeExceptionHandler handler = getRuntimeExceptionHandler();
		handler.stop();
	}

	/**
	 * Gets the runtime exception handler.
	 *
	 * @return the runtime exception handler
	 */
	private IRuntimeExceptionHandler getRuntimeExceptionHandler() {
		return WorkbenchHelper.getService(IRuntimeExceptionHandler.class);
	}

	@Override
	public void runModel(final Object object, final String exp) {
		final IModelRunner modelRunner = getModelRunner();
		if (modelRunner == null) return;
		modelRunner.runModel(object, exp);
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
	public IGamlLabelProvider getGamlLabelProvider() { return WorkbenchHelper.getService(IGamlLabelProvider.class); }

	@Override
	public void closeSimulationViews(final IScope scope, final boolean openModelingPerspective,
			final boolean immediately) {
		WorkbenchHelper.run(() -> {
			final IWorkbenchPage page = WorkbenchHelper.getPage();
			final IViewReference[] views = page.getViewReferences();

			for (final IViewReference view : views) {
				final IViewPart part = view.getView(false);
				if (part instanceof IGamaView) {

					((IGamaView) part).close(scope);
				}
			}
			if (openModelingPerspective) {
				DEBUG.OUT("Deleting simulation perspective and opening immediately the modeling perspective = "
						+ immediately);
				PerspectiveHelper.deleteCurrentSimulationPerspective();
				PerspectiveHelper.openModelingPerspective(immediately, false);
			}

			getStatus().neutralStatus("No simulation running");
		});

	}

	@Override
	public String getExperimentState(final String uid) {
		final IExperimentController controller = GAMA.getFrontmostController();
		if (controller == null) return NONE;
		if (controller.isPaused()) return PAUSED;
		return RUNNING;
	}

	@Override
	public void updateExperimentState(final IScope scope, final String forcedState) {
		// DEBUG.OUT("STATE: " + forcedState);
		final ISourceProviderService service = WorkbenchHelper.getService(ISourceProviderService.class);
		final ISimulationStateProvider stateProvider = (ISimulationStateProvider) service
				.getSourceProvider("ummisco.gama.ui.experiment.SimulationRunningState");
		if (stateProvider != null) { WorkbenchHelper.run(() -> stateProvider.updateStateTo(forcedState)); }
	}

	@Override
	public void updateExperimentState(final IScope scope) {
		updateExperimentState(scope, getExperimentState(""));
	}

	@Override
	public void updateViewTitle(final IDisplayOutput out, final SimulationAgent agent) {
		WorkbenchHelper.run(() -> {
			final IViewPart part = ViewsHelper.findView(out.getViewId(), out.isUnique() ? null : out.getName(), true);
			if (part instanceof IGamaView) { ((IGamaView) part).changePartNameWithSimulation(agent); }
		});

	}

	@Override
	public void updateDecorator(final String id) {
		WorkbenchHelper.asyncRun(() -> WorkbenchHelper.getWorkbench().getDecoratorManager().update(id));

	}

	@Override
	public IStatusDisplayer getStatus() { return WorkbenchHelper.getService(IStatusDisplayer.class); }

	@Override
	public IConsoleDisplayer getConsole() { return WorkbenchHelper.getService(IConsoleDisplayer.class); }

	@Override
	public void run(final String taskName, final Runnable r, final boolean asynchronous) {
		if (asynchronous) {
			WorkbenchHelper.runInUI(taskName, 0, m -> r.run());
		} else {
			WorkbenchHelper.run(r);
		}
	}

	@Override
	public void setFocusOn(final IShape shape) {
		if (shape == null) return;
		for (final IDisplaySurface surface : ViewsHelper.allDisplaySurfaces()) {
			if (shape instanceof ITopLevelAgent) {
				surface.zoomFit();
			} else {
				surface.focusOn(shape);
			}
		}
		GAMA.getExperiment().refreshAllOutputs();
	}

	@Override
	public void applyLayout(final IScope scope, final Object layout) {
		final IDisplayLayoutManager manager = WorkbenchHelper.getService(IDisplayLayoutManager.class);
		if (manager != null) { manager.applyLayout(layout); }
	}

	@Override
	public GamaPoint getMouseLocationInModel() { return mouseLocationInModel; }

	@Override
	public void setMouseLocationInModel(final GamaPoint location) { mouseLocationInModel = location; }

	@Override
	public void exit() {
		WorkbenchHelper.close();
	}

	@Override
	public void refreshNavigator() {
		final IRefreshHandler refresh = WorkbenchHelper.getService(IRefreshHandler.class);
		if (refresh != null) { refresh.completeRefresh(null); }

	}

	@Override
	public boolean isInDisplayThread() { return EventQueue.isDispatchThread() || Display.getCurrent() != null; }

	// @Override
	// public boolean isSynchronized() {
	// IExperimentPlan exp = GAMA.getExperiment();
	// if (exp == null || exp.getAgent() == null) return false;
	// IOutputManager manager = exp.getAgent().getOutputManager();
	// if (manager.isSynchronized()) return true;
	// for (SimulationAgent sim : exp.getAgent().getSimulationPopulation()) {
	// if (sim.getOutputManager().isSynchronized()) return true;
	// }
	// return false;
	// }

}
