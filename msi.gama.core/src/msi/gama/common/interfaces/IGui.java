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

import gnu.trove.map.hash.THashMap;
import java.util.Map;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.types.IType;
import org.eclipse.core.runtime.CoreException;

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
	public static final String PLUGIN_ID = "msi.gama.application";
	public static final Map<String, IDisplayCreator> DISPLAYS = new THashMap();

	void setSubStatusCompletion(double status);

	void setStatus(String error, int code);

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

	void updateViewOf(IDisplayOutput output);

	void debug(String string);

	void warn(String string);

	void runtimeError(GamaRuntimeException g);

	IEditorFactory getEditorFactory();

	boolean confirmClose(IExperimentPlan experiment);

	void showConsoleView();

	void setWorkbenchWindowTitle(String string);

	void closeViewOf(IDisplayOutput out);

	IGamaView hideView(String viewId);

	boolean isModelingPerspective();

	boolean openModelingPerspective();

	boolean isSimulationPerspective();

	void togglePerspective();

	boolean openSimulationPerspective();

	IDisplaySurface getDisplaySurfaceFor(IScope scope, String keyword, LayeredDisplayOutput layerDisplayOutput,
		double w, double h, Object ... args);

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

}
