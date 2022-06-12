/*******************************************************************************************************
 *
 * IGui.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
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

	/** The error. */
	int ERROR = 0;

	/** The wait. */
	int WAIT = 1;

	/** The inform. */
	int INFORM = 2;

	/** The neutral. */
	int NEUTRAL = 3;

	/** The user. */
	int USER = 4;

	/** The displays. */
	Map<String, DisplayDescription> DISPLAYS = GamaMapFactory.createOrdered();

	/** The monitor view id. */
	String MONITOR_VIEW_ID = "msi.gama.application.view.MonitorView";

	/** The interactive console view id. */
	String INTERACTIVE_CONSOLE_VIEW_ID = "msi.gama.application.view.InteractiveConsoleView";

	/** The agent view id. */
	String AGENT_VIEW_ID = "msi.gama.application.view.AgentInspectView";

	/** The table view id. */
	String TABLE_VIEW_ID = "msi.gama.application.view.TableAgentInspectView";

	/** The layer view id. */
	String LAYER_VIEW_ID = "msi.gama.application.view.LayeredDisplayView";

	/** The gl layer view id. */
	String GL_LAYER_VIEW_ID = "msi.gama.application.view.OpenGLDisplayView";

	/** The gl layer view id2. */
	String GL_LAYER_VIEW_ID2 = "msi.gama.application.view.OpenGLDisplayView2";

	/** The gl layer view id3. */
	String GL_LAYER_VIEW_ID3 = "msi.gama.application.view.WebDisplayView";

	/** The error view id. */
	String ERROR_VIEW_ID = "msi.gama.application.view.ErrorView";

	/** The test view id. */
	String TEST_VIEW_ID = "msi.gama.application.view.TestView";

	/** The parameter view id. */
	String PARAMETER_VIEW_ID = "msi.gama.application.view.ParameterView";

	/** The navigator view id. */
	String NAVIGATOR_VIEW_ID = "msi.gama.gui.view.GamaNavigator";

	/** The navigator lightweight decorator id. */
	String NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID = "msi.gama.application.decorator";

	/** The console view id. */
	String CONSOLE_VIEW_ID = "msi.gama.application.view.ConsoleView";

	/** The user control view id. */
	String USER_CONTROL_VIEW_ID = "msi.gama.views.userControlView";

	/** The paused. */
	String PAUSED = "STOPPED";

	/** The finished. */
	String FINISHED = "FINISHED";

	/** The running. */
	String RUNNING = "RUNNING";

	/** The notready. */
	String NOTREADY = "NOTREADY";

	/** The none. */
	String NONE = "NONE";

	/** The perspective modeling id. */
	String PERSPECTIVE_MODELING_ID = "msi.gama.application.perspectives.ModelingPerspective";

	/**
	 * Gets the status.
	 *
	 * @param scope
	 *            the scope
	 * @return the status
	 */
	IStatusDisplayer getStatus();

	/**
	 * Gets the console.
	 *
	 * @return the console
	 */
	IConsoleDisplayer getConsole();

	/**
	 * Show view.
	 *
	 * @param scope
	 *            the scope
	 * @param viewId
	 *            the view id
	 * @param name
	 *            the name
	 * @param code
	 *            the code
	 * @return the i gama view
	 */
	IGamaView showView(IScope scope, String viewId, String name, int code);

	/**
	 * Tell.
	 *
	 * @param message
	 *            the message
	 */
	void tell(String message);

	/**
	 * Error.
	 *
	 * @param error
	 *            the error
	 */
	void error(String error);

	/**
	 * Show parameter view.
	 *
	 * @param scope
	 *            the scope
	 * @param exp
	 *            the exp
	 */
	void showParameterView(IScope scope, IExperimentPlan exp);

	/**
	 * Clear errors.
	 *
	 * @param scope
	 *            the scope
	 */
	void clearErrors(IScope scope);

	/**
	 * Runtime error.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 */
	void runtimeError(final IScope scope, GamaRuntimeException g);

	/**
	 * Confirm close.
	 *
	 * @param experiment
	 *            the experiment
	 * @return true, if successful
	 */
	boolean confirmClose(IExperimentPlan experiment);

	/**
	 * Copy to clipboard.
	 *
	 * @param text
	 *            the text
	 * @return true, if successful
	 */
	boolean copyToClipboard(String text);

	/**
	 * Open simulation perspective.
	 *
	 * @param model
	 *            the model
	 * @param experimentId
	 *            the experiment id
	 * @return true, if successful
	 */
	boolean openSimulationPerspective(IModel model, String experimentId);

	/**
	 * Gets the all display surfaces.
	 *
	 * @return the all display surfaces
	 */
	Iterable<IDisplaySurface> getAllDisplaySurfaces();

	/**
	 * Gets the frontmost display surface.
	 *
	 * @return the frontmost display surface
	 */
	IDisplaySurface getFrontmostDisplaySurface();

	/**
	 * Creates the display surface for.
	 *
	 * @param output
	 *            the output
	 * @param args
	 *            the args
	 * @return the i display surface
	 */
	IDisplaySurface createDisplaySurfaceFor(final LayeredDisplayOutput output, final Object... args);

	/**
	 * Open user input dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param parameters
	 *            the parameters
	 * @param font
	 *            the font
	 * @param color
	 *            the color
	 * @return the map
	 */
	Map<String, Object> openUserInputDialog(IScope scope, String title, List<IParameter> parameters, GamaFont font,
			GamaColor color);

	/**
	 * Open wizard.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param finish
	 *            the finish
	 * @param pages
	 *            the pages
	 * @return the i map
	 */
	IMap<String, IMap<String, Object>> openWizard(IScope scope, String title, ActionDescription finish,
			IList<IMap<String, Object>> pages);

	/**
	 * Open user input dialog confirm.
	 *
	 * @param scope
	 *            the scope
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @return the boolean
	 */
	Boolean openUserInputDialogConfirm(final IScope scope, final String title, final String message);

	/**
	 * Open user control panel.
	 *
	 * @param scope
	 *            the scope
	 * @param panel
	 *            the panel
	 */
	void openUserControlPanel(IScope scope, UserPanelStatement panel);

	/**
	 * Close dialogs.
	 *
	 * @param scope
	 *            the scope
	 */
	void closeDialogs(IScope scope);

	/**
	 * Gets the highlighted agent.
	 *
	 * @return the highlighted agent
	 */
	IAgent getHighlightedAgent();

	/**
	 * Sets the highlighted agent.
	 *
	 * @param a
	 *            the new highlighted agent
	 */
	void setHighlightedAgent(IAgent a);

	/**
	 * Sets the selected agent.
	 *
	 * @param a
	 *            the new selected agent
	 */
	void setSelectedAgent(IAgent a);

	/**
	 * Update parameter view.
	 *
	 * @param scope
	 *            the scope
	 * @param exp
	 *            the exp
	 */
	void updateParameterView(IScope scope, IExperimentPlan exp);

	/**
	 * Prepare for experiment.
	 *
	 * @param scope
	 *            the scope
	 * @param exp
	 *            the exp
	 */
	void prepareForExperiment(IScope scope, IExperimentPlan exp);

	/**
	 * Clean after experiment.
	 */
	void cleanAfterExperiment();

	/**
	 * Edits the model.
	 *
	 * @param scope
	 *            the scope
	 * @param eObject
	 *            the e object
	 */
	void editModel(IScope scope, Object eObject);

	/**
	 * Run model.
	 *
	 * @param object
	 *            the object
	 * @param exp
	 *            the exp
	 */
	void runModel(final Object object, final String exp);

	/**
	 * Update speed display.
	 *
	 * @param scope
	 *            the scope
	 * @param d
	 *            the d
	 * @param notify
	 *            the notify
	 */
	void updateSpeedDisplay(IScope scope, Double d, boolean notify);

	/**
	 * Gets the meta data provider.
	 *
	 * @return the meta data provider
	 */
	IFileMetaDataProvider getMetaDataProvider();

	/**
	 * Close simulation views.
	 *
	 * @param scope
	 *            the scope
	 * @param andOpenModelingPerspective
	 *            the and open modeling perspective
	 * @param immediately
	 *            the immediately
	 */
	void closeSimulationViews(IScope scope, boolean andOpenModelingPerspective, boolean immediately);

	/**
	 * Gets the display description for.
	 *
	 * @param name
	 *            the name
	 * @return the display description for
	 */
	DisplayDescription getDisplayDescriptionFor(final String name);

	/**
	 * Gets the experiment state.
	 *
	 * @param uid
	 *            the uid
	 * @return the experiment state
	 */
	String getExperimentState(String uid);

	/**
	 * Update experiment state.
	 *
	 * @param scope
	 *            the scope
	 * @param state
	 *            the state
	 */
	void updateExperimentState(IScope scope, String state);

	/**
	 * Update experiment state.
	 *
	 * @param scope
	 *            the scope
	 */
	void updateExperimentState(IScope scope);

	/**
	 * Update view title.
	 *
	 * @param output
	 *            the output
	 * @param agent
	 *            the agent
	 */
	void updateViewTitle(IDisplayOutput output, SimulationAgent agent);

	/**
	 * Open welcome page.
	 *
	 * @param b
	 *            the b
	 */
	void openWelcomePage(boolean b);

	/**
	 * Update decorator.
	 *
	 * @param string
	 *            the string
	 */
	void updateDecorator(String string);

	/**
	 * Run.
	 *
	 * @param taskName
	 *            the task name
	 * @param opener
	 *            the opener
	 * @param asynchronous
	 *            the asynchronous
	 */
	void run(String taskName, Runnable opener, boolean asynchronous);

	/**
	 * Sets the focus on.
	 *
	 * @param o
	 *            the new focus on
	 */
	void setFocusOn(IShape o);

	/**
	 * Apply layout.
	 *
	 * @param scope
	 *            the scope
	 * @param layout
	 *            the layout
	 */
	void applyLayout(IScope scope, Object layout);

	/**
	 * Display errors.
	 *
	 * @param scope
	 *            the scope
	 * @param newExceptions
	 *            the new exceptions
	 */
	void displayErrors(IScope scope, List<GamaRuntimeException> newExceptions);

	/**
	 * Gets the mouse location in model.
	 *
	 * @return the mouse location in model
	 */
	GamaPoint getMouseLocationInModel();

	/**
	 * Sets the mouse location in model.
	 *
	 * @param modelCoordinates
	 *            the new mouse location in model
	 */
	void setMouseLocationInModel(GamaPoint modelCoordinates);

	/**
	 * Gets the gaml label provider.
	 *
	 * @return the gaml label provider
	 */
	IGamlLabelProvider getGamlLabelProvider();

	/**
	 * Exit.
	 */
	void exit();

	// Tests

	/**
	 * Open test view.
	 *
	 * @param scope
	 *            the scope
	 * @param remainOpen
	 *            the remain open
	 * @return the i gama view. test
	 */
	IGamaView.Test openTestView(IScope scope, boolean remainOpen);

	/**
	 * Display tests results.
	 *
	 * @param scope
	 *            the scope
	 * @param summary
	 *            the summary
	 */
	void displayTestsResults(IScope scope, CompoundSummary<?, ?> summary);

	/**
	 * End test display.
	 */
	void endTestDisplay();

	/**
	 * Run headless tests.
	 *
	 * @param model
	 *            the model
	 * @return the list
	 */
	List<TestExperimentSummary> runHeadlessTests(final Object model);

	/**
	 * Refresh navigator.
	 */
	void refreshNavigator();

	/**
	 * Checks if is in display thread.
	 *
	 * @return true, if is in display thread
	 */
	boolean isInDisplayThread();

	/**
	 * Checks if is hi DPI.
	 *
	 * @return true, if is hi DPI
	 */
	default boolean isHiDPI() { return false; }

	/**
	 * Checks if is synchronized.
	 *
	 * @return true, if is synchronized
	 */
	// boolean isSynchronized();

}
