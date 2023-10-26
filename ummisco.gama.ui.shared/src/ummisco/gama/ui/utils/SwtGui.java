/*******************************************************************************************************
 *
 * SwtGui.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import static ummisco.gama.ui.utils.ViewsHelper.hideView;
import static ummisco.gama.ui.utils.WorkbenchHelper.getClipboard;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.ICommandService;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.application.workbench.SimulationPerspectiveDescriptor;
import msi.gama.common.interfaces.IConsoleDisplayer;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGamaView.Error;
import msi.gama.common.interfaces.IGamaView.Parameters;
import msi.gama.common.interfaces.IGamaView.Test;
import msi.gama.common.interfaces.IGamaView.User;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IRuntimeExceptionHandler;
import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.display.AbstractDisplayGraphics;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.server.CommandExecutor;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.statements.test.CompoundSummary;
import msi.gaml.statements.test.TestExperimentSummary;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.dialogs.Messages;
import ummisco.gama.ui.interfaces.IDisplayLayoutManager;
import ummisco.gama.ui.interfaces.IModelRunner;
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
		DEBUG.ON();
	}

	/** The all tests running. */
	public volatile static boolean ALL_TESTS_RUNNING;

	/** The highlighted agent. */
	private IAgent highlightedAgent;

	/** The mouse location in model. */
	private GamaPoint mouseLocationInModel, mouseLocationInDisplay;

	/** The parameters view. */
	private final IGamaView.Parameters[] parametersView = new IGamaView.Parameters[1];

	static {
		PreferencesHelper.initialize();
		AbstractDisplayGraphics.getCachedGC();
	}

	/**
	 * Instantiates a new swt gui.
	 */
	public SwtGui() {}

	@Override
	public boolean confirmClose(final IExperimentPlan exp) {
		if (exp == null || !GamaPreferences.Runtime.CORE_ASK_CLOSING.getValue()) return true;
		PerspectiveHelper.switchToSimulationPerspective();
		return Messages.modalQuestion("Close simulation confirmation", "Do you want to close experiment '"
				+ exp.getName() + "' of model '" + exp.getModel().getName() + "' ?");
	}

	@Override
	public void openMessageDialog(final IScope scope, final String msg) {
		Messages.tell(msg);
	}

	@Override
	public void openErrorDialog(final IScope scope, final String err) {
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

	/**
	 * Display errors.
	 *
	 * @param scope
	 *            the scope
	 * @param exceptions
	 *            the exceptions
	 * @param reset
	 *            the reset
	 */
	@Override
	public void displayErrors(final IScope scope, final List<GamaRuntimeException> exceptions, final boolean reset) {
		if (exceptions == null) {
			// DEBUG.OUT("Hiding errors view");
			hideView(ERROR_VIEW_ID);
		} else {
			// DEBUG.OUT("Showing errors view with new exceptions");
			final IGamaView.Error v = (Error) showView(scope, ERROR_VIEW_ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			if (v != null) { v.displayErrors(reset); }
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

	/** The transfers. */
	static Transfer[] transfers = { TextTransfer.getInstance() };

	@Override
	public boolean copyToClipboard(final String text) {
		if (getClipboard() == null || text == null) return false;
		WorkbenchHelper.asyncRun(() -> { getClipboard().setContents(new String[] { text }, transfers); });
		return true;
	}

	@Override
	public String copyTextFromClipboard() {
		if (getClipboard() == null) return null;
		return (String) WorkbenchHelper.run(() -> getClipboard().getContents(TextTransfer.getInstance()));
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

	/**
	 * Gets the frontmost display surface.
	 *
	 * @return the frontmost display surface
	 */
	@Override
	public IDisplaySurface getFrontmostDisplaySurface() { return ViewsHelper.frontmostDisplaySurface(); }

	@Override
	public Map<String, Object> openUserInputDialog(final IScope scope, final String title,
			final List<IParameter> parameters, final GamaFont font, final GamaColor color, final Boolean showTitle) {
		final IMap<String, Object> result = GamaMapFactory.createUnordered();
		for (final IParameter p : parameters) { result.put(p.getName(), p.getInitialValue(scope)); }
		WorkbenchHelper.run(() -> {
			final EditorsDialog dialog = new EditorsDialog(scope, null, parameters, title, font, color, showTitle);
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

	/**
	 * Update parameters.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 17 août 2023
	 */
	@Override
	public void updateParameters() {

		WorkbenchHelper.run(() -> {
			boolean showIt = GAMA.getExperiment().hasParametersOrUserCommands()
					&& !PerspectiveHelper.isModelingPerspective() && PerspectiveHelper.showParameters();
			if (showIt) {
				parametersView[0] = (Parameters) showView(GAMA.getRuntimeScope(), PARAMETER_VIEW_ID, null,
						IWorkbenchPage.VIEW_ACTIVATE);
			} else {
				parametersView[0] = (Parameters) ViewsHelper.findView(PARAMETER_VIEW_ID, null, false);
			}
			if (parametersView[0] != null) {
				parametersView[0].topLevelAgentChanged(GAMA.getCurrentTopLevelAgent());
				parametersView[0].updateItemValues(false);
			}
		});
	}

	/**
	 * Regular update for the monitors
	 *
	 * @param scope
	 */
	@Override
	public void updateParameterView(final IScope scope) {
		if (parametersView[0] instanceof IGamaView gv) { gv.update(null); }
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

	/**
	 * Arrange experiment views.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param exp
	 *            the exp
	 * @param keepTabs
	 *            the keep tabs
	 * @param keepToolbars
	 *            the keep toolbars
	 * @param showConsoles
	 *            the show consoles
	 * @param showParameters
	 *            the show parameters
	 * @param showNavigator
	 *            the show navigator
	 * @param showControls
	 *            the show controls
	 * @param keepTray
	 *            the keep tray
	 * @param color
	 *            the color
	 * @param showEditors
	 *            the show editors
	 * @date 14 août 2023
	 */
	@Override
	public void arrangeExperimentViews(final IScope scope, final IExperimentPlan exp, final Boolean keepTabs,
			final Boolean keepToolbars, final Boolean showConsoles, final Boolean showParameters,
			final Boolean showNavigator, final Boolean showControls, final Boolean keepTray,
			final Supplier<GamaColor> color, final boolean showEditors) {

		WorkbenchHelper.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
		WorkbenchHelper.runInUI("Arranging views", 0, m -> {
			WorkbenchHelper.getWindow().updateActionBars();

			// To solve issue #3697
			ICommandService hs = WorkbenchHelper.getService(ICommandService.class);
			hs.refreshElements("msi.gama.application.commands.SynchronizeExperiment", null);

			WorkbenchHelper.getPage().setEditorAreaVisible(showEditors);
			getConsole().toggleConsoleViews(exp.getAgent(), showConsoles == null || showConsoles);
			if (showNavigator != null && !showNavigator) { hideView(IGui.NAVIGATOR_VIEW_ID); }
			if (showControls != null) { WorkbenchHelper.getWindow().setCoolBarVisible(showControls); }
			if (keepTray != null) { PerspectiveHelper.showBottomTray(WorkbenchHelper.getWindow(), keepTray); }

			final SimulationPerspectiveDescriptor sd = PerspectiveHelper.getActiveSimulationPerspective();
			if (sd != null) {
				sd.showParameters(showParameters == null || showParameters);
				sd.showConsoles(showConsoles == null || showConsoles);
				sd.keepTabs(keepTabs);
				sd.keepToolbars(keepToolbars);
				sd.keepControls(showControls);
				sd.keepTray(keepTray);
				sd.setBackground(() -> {
					GamaColor c = color.get();
					return c == null ? null : GamaColors.toSwtColor(c);
				});
			}
		});
	}

	/**
	 * Initialize open GL.
	 */
	// private static void initializeOpenGL() {
	// final IOpenGLInitializer initializer = WorkbenchHelper.getService(IOpenGLInitializer.class);
	// if (initializer != null && !initializer.isDone()) { new Thread(initializer).start(); }
	// }

	/**
	 * Method cleanAfterExperiment()
	 *
	 * @see msi.gama.common.interfaces.IGui#cleanAfterExperiment(msi.gama.kernel.experiment.IExperimentPlan)
	 */
	@Override
	public void cleanAfterExperiment() {
		hideParameters();
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

	@Override
	public void hideParameters() {
		hideView(PARAMETER_VIEW_ID);
		parametersView[0] = null;
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
	public void updateSpeedDisplay(final IScope scope, final Double minimumCycleDuration,
			final Double maximumCycleDuration, final boolean notify) {
		final ISpeedDisplayer speedStatus = WorkbenchHelper.getService(ISpeedDisplayer.class);
		if (speedStatus != null) {
			WorkbenchHelper.asyncRun(() -> {
				speedStatus.setMaximum(maximumCycleDuration);
				speedStatus.setInit(minimumCycleDuration, notify);
			});

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
	public void closeSimulationViews(final IScope scope, final boolean openModelingPerspective,
			final boolean immediately) {
		WorkbenchHelper.run(() -> {
			final IWorkbenchPage page = WorkbenchHelper.getPage();
			final IViewReference[] views = page.getViewReferences();

			for (final IViewReference view : views) {
				final IViewPart part = view.getView(false);
				if (part instanceof IGamaView) { ((IGamaView) part).close(scope); }
			}
			if (openModelingPerspective) {
				DEBUG.OUT("Deleting simulation perspective and opening immediately the modeling perspective = "
						+ immediately);
				PerspectiveHelper.deleteCurrentSimulationPerspective();
				PerspectiveHelper.openModelingPerspective(immediately, false);
			}

			// getStatus().neutralStatus("No simulation running");
		});

	}

	@Override
	public void updateViewTitle(final IDisplayOutput out, final SimulationAgent agent) {
		WorkbenchHelper.run(() -> {
			final IViewPart part = ViewsHelper.findView(out.getViewId(), out.isUnique() ? null : out.getName(), true);
			if (part instanceof IGamaView) { ((IGamaView) part).changePartNameWithSimulation(agent); }
		});

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
	public GamaPoint getMouseLocationInDisplay() { return mouseLocationInDisplay; }

	@Override
	public void setMouseLocationInModel(final GamaPoint location) { mouseLocationInModel = location; }

	@Override
	public void setMouseLocationInDisplay(final GamaPoint location) { mouseLocationInDisplay = location; }

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

	@Override
	public boolean isHiDPI() {
		int zoom = WorkbenchHelper.run(() -> WorkbenchHelper.getDisplay().getPrimaryMonitor().getZoom());
		return zoom > 100;
	}

	@Override
	public Map<String, ISocketCommand> getServerCommands() { return CommandExecutor.getDefaultCommands(); }

}
