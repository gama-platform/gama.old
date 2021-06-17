/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IGui.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.statements.test.CompoundSummary;
import msi.gaml.statements.test.TestExperimentSummary;

/**
 * The interface IGui. Represents objects that act on behalf of a concrete GUI implementation (RCP, Headless, etc.)
 *
 * @author drogoul
 * @since 18 dec. 2011
 *
 */
public interface IGui {

	int ERROR = 0;
	int WAIT = 1;
	int INFORM = 2;
	int NEUTRAL = 3;
	int USER = 4;

	Map<String, DisplayDescription> DISPLAYS = GamaMapFactory.createUnordered();
	String MONITOR_VIEW_ID = "msi.gama.application.view.MonitorView";
	String INTERACTIVE_CONSOLE_VIEW_ID = "msi.gama.application.view.InteractiveConsoleView";
	String AGENT_VIEW_ID = "msi.gama.application.view.AgentInspectView";
	String TABLE_VIEW_ID = "msi.gama.application.view.TableAgentInspectView";
	String LAYER_VIEW_ID = "msi.gama.application.view.LayeredDisplayView";
	String GL_LAYER_VIEW_ID = "msi.gama.application.view.OpenGLDisplayView";
	String GL_LAYER_VIEW_ID2 = "msi.gama.application.view.OpenGLDisplayView2";
	String GL_LAYER_VIEW_ID3 = "msi.gama.application.view.WebDisplayView";

	String ERROR_VIEW_ID = "msi.gama.application.view.ErrorView";
	String TEST_VIEW_ID = "msi.gama.application.view.TestView";
	String PARAMETER_VIEW_ID = "msi.gama.application.view.ParameterView";

	String NAVIGATOR_VIEW_ID = "msi.gama.gui.view.GamaNavigator";
	String NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID = "msi.gama.application.decorator";
	String CONSOLE_VIEW_ID = "msi.gama.application.view.ConsoleView";
	String USER_CONTROL_VIEW_ID = "msi.gama.views.userControlView";

	String PAUSED = "STOPPED";
	String FINISHED = "FINISHED";
	String RUNNING = "RUNNING";
	String NOTREADY = "NOTREADY";

	String NONE = "NONE";
	String PERSPECTIVE_MODELING_ID = "msi.gama.application.perspectives.ModelingPerspective";

	IStatusDisplayer getStatus(IScope scope);

	IConsoleDisplayer getConsole();

	IGamaView showView(IScope scope, String viewId, String name, int code);

	void tell(String message);

	void error(String error);

	void showParameterView(IScope scope, IExperimentPlan exp);

	void clearErrors(IScope scope);

	void runtimeError(final IScope scope, GamaRuntimeException g);

	boolean confirmClose(IExperimentPlan experiment);

	boolean copyToClipboard(String text);

	boolean openSimulationPerspective(IModel model, String experimentId);

	Iterable<IDisplaySurface> getAllDisplaySurfaces();

	IDisplaySurface createDisplaySurfaceFor(final LayeredDisplayOutput output, final Object... args);

	Map<String, Object> openUserInputDialog(IScope scope, String title, List<IParameter> parameters, GamaFont font);

	IMap<String, IMap<String, Object>> openWizard(IScope scope, String title, ActionDescription finish,
			IList<IMap<String, Object>> pages);

	Boolean openUserInputDialogConfirm(final IScope scope, final String title, final String message);

	void openUserControlPanel(IScope scope, UserPanelStatement panel);

	void closeDialogs(IScope scope);

	IAgent getHighlightedAgent();

	void setHighlightedAgent(IAgent a);

	void setSelectedAgent(IAgent a);

	void updateParameterView(IScope scope, IExperimentPlan exp);

	void prepareForExperiment(IScope scope, IExperimentPlan exp);

	void cleanAfterExperiment();

	void editModel(IScope scope, Object eObject);

	void runModel(final Object object, final String exp);

	void updateSpeedDisplay(IScope scope, Double d, boolean notify);

	IFileMetaDataProvider getMetaDataProvider();

	void closeSimulationViews(IScope scope, boolean andOpenModelingPerspective, boolean immediately);

	DisplayDescription getDisplayDescriptionFor(final String name);

	String getExperimentState(String uid);

	void updateExperimentState(IScope scope, String state);

	void updateExperimentState(IScope scope);

	void updateViewTitle(IDisplayOutput output, SimulationAgent agent);

	void openWelcomePage(boolean b);

	void updateDecorator(String string);

	void run(String taskName, Runnable opener, boolean asynchronous);

	void setFocusOn(IShape o);

	void applyLayout(IScope scope, Object layout);

	void displayErrors(IScope scope, List<GamaRuntimeException> newExceptions);

	ILocation getMouseLocationInModel();

	void setMouseLocationInModel(ILocation modelCoordinates);

	IGamlLabelProvider getGamlLabelProvider();

	void exit();

	void openInteractiveConsole(IScope scope);

	// Tests

	IGamaView.Test openTestView(IScope scope, boolean remainOpen);

	void displayTestsResults(IScope scope, CompoundSummary<?, ?> summary);

	void endTestDisplay();

	List<TestExperimentSummary> runHeadlessTests(final Object model);

	/**
	 * Tries to put the frontmost display in full screen mode or in normal view mode if it is already in full screen
	 *
	 * @return true if the toggle has succeeded
	 */
	boolean toggleFullScreenMode();

	void refreshNavigator();

	boolean isInDisplayThread();

}
