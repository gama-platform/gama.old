/*********************************************************************************************
 *
 *
 * 'IGui.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.types.IType;

/**
 * The class IGui.
 *
 * @author drogoul
 * @since 18 d�c. 2011
 *
 */
public interface IGui {

	public static final int ERROR = 0;
	public static final int WAIT = 1;
	public static final int INFORM = 2;
	public static final int NEUTRAL = 3;
	public static final int USER = 4;
	public static final String PLUGIN_ID = "msi.gama.application";
	public static final Map<String, IDisplayCreator> DISPLAYS = new THashMap();
	public static final String PERSPECTIVE_MODELING_ID = "msi.gama.application.perspectives.ModelingPerspective";
	public static final String PERSPECTIVE_SIMULATION_ID = "msi.gama.application.perspectives.SimulationPerspective";
	public static final String PERSPECTIVE_HPC_ID = "msi.gama.hpc.HPCPerspectiveFactory";
	public static final String MONITOR_VIEW_ID = "msi.gama.application.view.MonitorView";
	public static final String AGENT_VIEW_ID = "msi.gama.application.view.AgentInspectView";
	public static final String TABLE_VIEW_ID = "msi.gama.application.view.TableAgentInspectView";
	public static final String LAYER_VIEW_ID = "msi.gama.application.view.LayeredDisplayView";
	public static final String WEB_VIEW_ID = "msi.gama.application.view.WebDisplayView";
	public static final String ERROR_VIEW_ID = "msi.gama.application.view.ErrorView";
	public static final String PARAMETER_VIEW_ID = "msi.gama.application.view.ParameterView";
	public static final String HEADLESSPARAM_ID = "msi.gama.application.view.HeadlessParam";
	public static final String HEADLESS_CHART_ID = "msi.gama.hpc.gui.HeadlessChart";

	public static final String GRAPHSTREAM_VIEW_ID = "msi.gama.networks.ui.GraphstreamView";
	public static final String HPC_PERSPECTIVE_ID = "msi.gama.hpc.HPCPerspectiveFactory";

	public final static String PAUSED = "STOPPED";
	public final static String RUNNING = "RUNNING";
	public final static String NOTREADY = "NOTREADY";
	public final static String NONE = "NONE";

	void setSubStatusCompletion(double status);

	void setStatus(String error, int code);

	void setStatus(String msg, GamaColor color);

	void beginSubStatus(String name);

	void endSubStatus(String name);

	void run(Runnable block);

	void asyncRun(Runnable block);

	void raise(Throwable ex);

	IGamaView showView(String viewId, String name, int code);

	void tell(String message);

	void error(String error);

	void showParameterView(IExperimentPlan exp);

	void debugConsole(int cycle, String s);

	void informConsole(String s);

	// void updateViewOf(IDisplayOutput output);

	void debug(String string);

	void warn(String string);

	void runtimeError(GamaRuntimeException g);

	IEditorFactory getEditorFactory();

	boolean confirmClose(IExperimentPlan experiment);

	void showConsoleView();

	void setWorkbenchWindowTitle(String string);

	// void closeViewOf(IDisplayOutput out);

	IGamaView hideView(String viewId);

	IGamaView findView(final IDisplayOutput output);

	boolean isModelingPerspective();

	boolean openModelingPerspective(boolean immediately);

	boolean isSimulationPerspective();

	void togglePerspective(boolean immediately);

	boolean openSimulationPerspective(boolean immediately);

	IDisplaySurface getDisplaySurfaceFor(LayeredDisplayOutput layerDisplayOutput);

	Map<String, Object> openUserInputDialog(String title, Map<String, Object> initialValues, Map<String, IType> types);

	void openUserControlPanel(IScope scope, UserPanelStatement panel);

	void closeDialogs();

	IAgent getHighlightedAgent();

	void setHighlightedAgent(IAgent a);

	void setSelectedAgent(IAgent a);

	void updateParameterView(IExperimentPlan exp);

	void prepareForExperiment(IExperimentPlan exp);

	void cleanAfterExperiment(IExperimentPlan exp);

	void prepareForSimulation(SimulationAgent sim);

	void cleanAfterSimulation();

	void waitForViewsToBeInitialized();

	void debug(Exception e);

	void editModel(Object eObject);

	public abstract void runModel(final Object object, final String exp) throws CoreException;

	public abstract void runModel(final IModel object, final String exp);

	void updateSpeedDisplay(Double d, boolean notify);

	/**
	 * @param url
	 * @param html
	 * @return
	 */
	Object showWebEditor(String url, String html);

	/**
	 * @return
	 */
	String getName();

	/**
	 *
	 */
	void resumeStatus();

	IFileMetaDataProvider getMetaDataProvider();

	/**
	 *
	 */
	void wipeExperiments();

	/**
	 *
	 */
	void closeSimulationViews(boolean andOpenModelingPerspective);

	public DisplayDescription getDisplayDescriptionFor(final String name);

	/**
	 * @param string
	 */
	void waitStatus(String string);

	/**
	 * @param string
	 */
	void informStatus(String string);

	/**
	 * @param message
	 */
	void errorStatus(String message);

	/**
	 * @return
	 */
	String getFrontmostSimulationState();

	/**
	 * @param notready
	 */
	void updateSimulationState(String state);

	/**
	 *
	 */
	void updateSimulationState();

}
