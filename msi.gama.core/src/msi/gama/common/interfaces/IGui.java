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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.core.resources.IResource;

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
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.compilation.ast.ISyntacticElement;
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

	/** The null point. */
	GamaPoint NULL_POINT = new GamaPoint.Immutable();

	/** The label provider. */
	IGamlLabelProvider NULL_LABEL_PROVIDER = new IGamlLabelProvider() {

		@Override
		public String getText(final ISyntacticElement element) {
			return "";
		}

		@Override
		public Object getImage(final ISyntacticElement element) {
			return null;
		}
	};

	/** The null metadata provider. */
	IFileMetaDataProvider NULL_METADATA_PROVIDER = new IFileMetaDataProvider() {

		@Override
		public void storeMetaData(final IResource file, final IGamaFileMetaData data, final boolean immediately) {}

		@Override
		public IGamaFileMetaData getMetaData(final Object element, final boolean includeOutdated,
				final boolean immediately) {
			return new IGamaFileMetaData() {

				@Override
				public boolean hasFailed() {
					return false;
				}

				@Override
				public String toPropertyString() {
					return "";
				}

				@Override
				public void setModificationStamp(final long modificationStamp) {}

				@Override
				public Object getThumbnail() { return ""; }

				@Override
				public String getSuffix() { return ""; }

				@Override
				public void appendSuffix(final StringBuilder sb) {}

				@Override
				public long getModificationStamp() { return 0; }

				@Override
				public String getDocumentation() { return ""; }
			};
		}

	};

	/** The null status displayer. */
	IStatusDisplayer NULL_STATUS_DISPLAYER = new IStatusDisplayer() {};

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
	default IStatusDisplayer getStatus() { return NULL_STATUS_DISPLAYER; }

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
	default IGamaView showView(final IScope scope, final String viewId, final String name, final int code) {
		return null;
	}

	/**
	 * Tell.
	 *
	 * @param message
	 *            the message
	 */
	default void tell(final String message) {
		openMessageDialog(GAMA.getRuntimeScope(), message);
	}

	/**
	 * Open message dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param error
	 *            the error
	 */
	void openMessageDialog(IScope scope, String error);

	/**
	 * Error.
	 *
	 * @param error
	 *            the error
	 */
	default void error(final String error) {
		openErrorDialog(GAMA.getRuntimeScope(), error);
	}

	/**
	 * Open error dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param error
	 *            the error
	 */
	void openErrorDialog(IScope scope, String error);

	/**
	 * Show parameter view.
	 *
	 * @param scope
	 *            the scope
	 * @param exp
	 *            the exp
	 */
	default void showAndUpdateParameterView(final IScope scope, final IExperimentPlan exp) {}

	/**
	 * Clear errors.
	 *
	 * @param scope
	 *            the scope
	 */
	default void clearErrors(final IScope scope) {}

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
	default boolean confirmClose(final IExperimentPlan experiment) {
		return true;
	}

	/**
	 * Copy to clipboard.
	 *
	 * @param text
	 *            the text
	 * @return true, if successful
	 */
	default boolean copyToClipboard(final String text) {
		return false;
	}

	/**
	 * Open simulation perspective.
	 *
	 * @param model
	 *            the model
	 * @param experimentId
	 *            the experiment id
	 * @return true, if successful
	 */
	default boolean openSimulationPerspective(final IModel model, final String experimentId) {
		return true;
	}

	/**
	 * Gets the all display surfaces.
	 *
	 * @return the all display surfaces
	 */
	default Iterable<IDisplaySurface> getAllDisplaySurfaces() { return Collections.EMPTY_LIST; }

	/**
	 * Gets the frontmost display surface.
	 *
	 * @return the frontmost display surface
	 */
	default IDisplaySurface getFrontmostDisplaySurface() { return null; }

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
			GamaColor color, Boolean showTitle);

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
	default Boolean openUserInputDialogConfirm(final IScope scope, final String title, final String message) {
		return true;
	}

	/**
	 * Open user control panel.
	 *
	 * @param scope
	 *            the scope
	 * @param panel
	 *            the panel
	 */
	default void openUserControlPanel(final IScope scope, final UserPanelStatement panel) {}

	/**
	 * Close dialogs.
	 *
	 * @param scope
	 *            the scope
	 */
	default void closeDialogs(final IScope scope) {}

	/**
	 * Gets the highlighted agent.
	 *
	 * @return the highlighted agent
	 */
	default IAgent getHighlightedAgent() { return null; }

	/**
	 * Sets the highlighted agent.
	 *
	 * @param a
	 *            the new highlighted agent
	 */
	default void setHighlightedAgent(final IAgent a) {}

	/**
	 * Sets the selected agent.
	 *
	 * @param a
	 *            the new selected agent
	 */
	default void setSelectedAgent(final IAgent a) {}

	/**
	 * Clean after experiment.
	 */
	default void cleanAfterExperiment() {}

	/**
	 * Edits the model.
	 *
	 * @param scope
	 *            the scope
	 * @param eObject
	 *            the e object
	 */
	default void editModel(final IScope scope, final Object eObject) {}

	/**
	 * Run model.
	 *
	 * @param object
	 *            the object
	 * @param exp
	 *            the exp
	 */
	default void runModel(final Object object, final String exp) {}

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
	default void updateSpeedDisplay(final IScope scope, final Double d, final boolean notify) {}

	/**
	 * Gets the meta data provider.
	 *
	 * @return the meta data provider
	 */
	default IFileMetaDataProvider getMetaDataProvider() { return NULL_METADATA_PROVIDER; }

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
	default void closeSimulationViews(final IScope scope, final boolean andOpenModelingPerspective,
			final boolean immediately) {}

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
	default void updateExperimentState(final IScope scope, final String state) {}

	/**
	 * Update experiment state.
	 *
	 * @param scope
	 *            the scope
	 */
	default void updateExperimentState(final IScope scope) {}

	/**
	 * Update view title.
	 *
	 * @param output
	 *            the output
	 * @param agent
	 *            the agent
	 */
	default void updateViewTitle(final IDisplayOutput output, final SimulationAgent agent) {}

	/**
	 * Open welcome page.
	 *
	 * @param b
	 *            the b
	 */
	default void openWelcomePage(final boolean b) {}

	/**
	 * Update decorator.
	 *
	 * @param string
	 *            the string
	 */
	default void updateDecorator(final String string) {}

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
	default void setFocusOn(final IShape o) {}

	/**
	 * Apply layout.
	 *
	 * @param scope
	 *            the scope
	 * @param layout
	 *            the layout
	 */
	default void applyLayout(final IScope scope, final Object layout) {}

	/**
	 * Gets the mouse location in model.
	 *
	 * @return the mouse location in model
	 */
	default GamaPoint getMouseLocationInModel() { return NULL_POINT; }

	/**
	 * Sets the mouse location in model.
	 *
	 * @param modelCoordinates
	 *            the new mouse location in model
	 */
	default void setMouseLocationInModel(final GamaPoint modelCoordinates) {}

	/**
	 * Gets the gaml label provider.
	 *
	 * @return the gaml label provider
	 */
	default IGamlLabelProvider getGamlLabelProvider() { return NULL_LABEL_PROVIDER; }

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
	default IGamaView.Test openTestView(final IScope scope, final boolean remainOpen) {
		return null;
	}

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
	default void endTestDisplay() {}

	/**
	 * Run headless tests.
	 *
	 * @param model
	 *            the model
	 * @return the list
	 */
	default List<TestExperimentSummary> runHeadlessTests(final Object model) {
		return null;
	}

	/**
	 * Refresh navigator.
	 */
	default void refreshNavigator() {}

	/**
	 * Checks if is in display thread.
	 *
	 * @return true, if is in display thread
	 */
	default boolean isInDisplayThread() { return false; }

	/**
	 * Checks if is hi DPI.
	 *
	 * @return true, if is hi DPI
	 */
	default boolean isHiDPI() { return false; }

	/**
	 * Regular update for the monitors
	 *
	 * @param scope
	 */
	default void updateParameterView(final IScope scope) {}

	/**
	 * Arrange experiment views.
	 *
	 * @param myScope
	 *            the my scope
	 * @param experimentPlan
	 *            the experiment plan
	 * @param keepTabs
	 *            the keep tabs
	 * @param keepToolbars
	 *            the keep toolbars
	 * @param showParameters
	 *            the show parameters
	 * @param showConsoles
	 *            the show consoles
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
	 */
	default void arrangeExperimentViews(final IScope myScope, final IExperimentPlan experimentPlan,
			final Boolean keepTabs, final Boolean keepToolbars, final Boolean showConsoles, final Boolean showNavigator,
			final Boolean showControls, final Boolean keepTray, final Supplier<GamaColor> color,
			final boolean showEditors) {}

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
	default void displayErrors(final IScope scope, final List<GamaRuntimeException> exceptions, final boolean reset) {}

	/**
	 * Hide parameters.
	 */
	default void hideParameters() {}

	/**
	 * Checks if is synchronized.
	 *
	 * @return true, if is synchronized
	 */
	// boolean isSynchronized();

}
