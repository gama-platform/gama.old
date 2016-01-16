/*********************************************************************************************
 *
 *
 * 'GuiUtils.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.util;

import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import msi.gama.common.interfaces.*;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.kernel.experiment.IExperimentPlan;
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
 * The class GuiUtils. A static bridge to the SWT environment. The actual dependency on SWT is
 * represented by an instance of IGui, which must be initialize when the UI plugin launches.
 *
 * @author drogoul
 * @since 18 dec. 2011
 *
 */
public class GuiUtils {

	static IGui gui;

	public static final String MONITOR_VIEW_ID = "msi.gama.application.view.MonitorView";
	public static final String AGENT_VIEW_ID = "msi.gama.application.view.AgentInspectView";
	public static final String TABLE_VIEW_ID = "msi.gama.application.view.TableAgentInspectView";
	public static final String LAYER_VIEW_ID = "msi.gama.application.view.LayeredDisplayView";
	public static final String WEB_VIEW_ID = "msi.gama.application.view.WebDisplayView";
	public static final String ERROR_VIEW_ID = "msi.gama.application.view.ErrorView";
	public static final String PARAMETER_VIEW_ID = "msi.gama.application.view.ParameterView";

	public static final String GRAPHSTREAM_VIEW_ID = "msi.gama.networks.ui.GraphstreamView";
	public static final String HPC_PERSPECTIVE_ID = "msi.gama.hpc.HPCPerspectiveFactory";

	private static boolean headlessMode = false;

	public static boolean isInHeadLessMode() {
		return headlessMode;
	}

	/**
	 * Method called by headless builder to change the GUI Mode
	 * @see ModelFactory
	 */

	public static void setHeadLessMode() {
		headlessMode = true;
	}

	/**
	 * Method called by the UI plugin to initialize the SWT environment to talk with.
	 * @param gui an instance of IGui
	 */
	public static void setSwtGui(final IGui gui) {
		System.out.println("Configuring GAMA User Interface using " + gui.getName());
		GuiUtils.gui = gui;
	}

	public static void waitStatus(final String string) {
		if ( gui != null ) {
			gui.setStatus(string, IGui.WAIT);
		}
	}

	public static void informStatus(final String string) {
		if ( gui != null ) {
			gui.setStatus(string, IGui.INFORM);
		} else {
			System.out.println(string);
		}
	}

	public static void errorStatus(final String error) {
		if ( gui != null ) {
			gui.setStatus(error, IGui.ERROR);
		} else {
			System.out.println("Status:" + error);
		}
	}

	public static void neutralStatus(final String message) {
		if ( gui != null ) {
			gui.setStatus(message, IGui.NEUTRAL);
		} else {
			System.out.println("Status:" + message);
		}
	}

	public static void setStatus(final String message, final GamaColor color) {
		if ( gui != null ) {
			if ( message == null ) {
				gui.resumeStatus();
			} else {
				gui.setStatus(message, color);
			}
		} else {
			System.out.println("Status:" + message);
		}

	}

	public static void beginSubStatus(final String n) {
		if ( gui != null ) {
			gui.beginSubStatus(n);
		}
	}

	public static void endSubStatus(final String n) {
		if ( gui != null ) {
			gui.endSubStatus(n);
		}
	}

	public static void updateSubStatusCompletion(final double n) {
		gui.setSubStatusCompletion(n);
	}

	/**
	 *
	 * See IWorkbenchPage.XXX for the code
	 * @param viewId
	 * @param string
	 * @return
	 */
	public static IGamaView showView(final String viewId, final String name, final int code) {
		if ( gui != null ) { return gui.showView(viewId, name, code); }
		return null;
	}

	public static Object showWebEditor(final String url, final String html) {
		if ( gui != null ) { return gui.showWebEditor(url, html); }
		return null;
	}

	/**
	 * @param ex
	 */
	public static void raise(final Throwable ex) {
		if ( gui != null ) {
			gui.raise(ex);
		} else {
			ex.printStackTrace();
		}
	}

	public static void error(final String error) {
		if ( gui != null ) {
			gui.error(error);
		} else {
			System.out.println(error);
		}
	}

	public static void tell(final String message) {
		if ( gui != null ) {
			gui.tell(message);
		} else {
			System.out.println(message);
		}
	}

	public static void asyncRun(final Runnable block) {
		if ( gui != null ) {
			gui.asyncRun(block);
		} else {
			block.run();
		}
	}

	public static void showParameterView(final IExperimentPlan exp) {
		if ( gui != null ) {
			gui.showParameterView(exp);
		}
	}

	public static void informConsole(final String s) {
		if ( gui != null ) {
			gui.informConsole(s);
		} else {
			System.out.println(s);
		}
	}

	/**
	 * @param cycle
	 * @param s
	 */
	public static void debugConsole(final int cycle, final String s) {
		if ( gui != null ) {
			gui.debugConsole(cycle, s);
		} else {
			System.out.println(s);
		}
	}

	public static void run(final Runnable block) {
		if ( gui != null ) {
			gui.run(block);
		} else {
			block.run();
		}
	}

	public static void warn(final String string) {
		if ( gui != null ) {
			gui.warn(string);
		} else {
			System.out.println(string);
		}
	}

	public static void debug(final String string) {
		if ( gui != null ) {
			gui.debug(string);
		} else {
			System.out.println(string);
		}
	}

	public static void runtimeError(final GamaRuntimeException g) {
		if ( gui != null ) {
			gui.runtimeError(g);
		} else {
			System.out.println(g.getMessage());
		}
	}

	public static IEditorFactory getEditorFactory() {
		if ( gui != null ) { return gui.getEditorFactory(); }
		return null;
	}

	public static boolean confirmClose(final IExperimentPlan experiment) {
		if ( gui != null ) { return gui.confirmClose(experiment); }
		return true;
	}

	public static void prepareForExperiment(final IExperimentPlan exp) {
		if ( gui != null ) {
			gui.prepareForExperiment(exp);
		}
	}

	public static void prepareForSimulation(final SimulationAgent agent) {
		if ( gui != null ) {
			gui.prepareForSimulation(agent);
		}
	}

	public static void cleanAfterExperiment(final IExperimentPlan exp) {
		// GAMA.getClock().setDelayFromExperiment(0);
		if ( gui != null ) {
			gui.cleanAfterExperiment(exp);
		}
	}

	public static void cleanAfterSimulation() {
		if ( gui != null ) {
			gui.cleanAfterSimulation();
		}
	}

	public static void showConsoleView() {
		if ( gui != null ) {
			gui.showConsoleView();
		}
	}

	public static void setWorkbenchWindowTitle(final String string) {
		if ( gui != null ) {
			gui.setWorkbenchWindowTitle(string);
		}
	}

	public static IGamaView findView(final IDisplayOutput output) {
		if ( gui != null ) { return gui.findView(output); }
		return null;
	}

	public static void hideView(final String viewId) {
		if ( gui != null ) {
			gui.hideView(viewId);
		}
	}

	public static boolean isModelingPerspective() {
		return gui == null ? false : gui.isModelingPerspective();
	}

	public static void openModelingPerspective(final boolean immediately) {
		if ( gui != null ) {
			gui.openModelingPerspective(immediately);
		}
	}

	public static boolean isSimulationPerspective() {
		return gui == null ? false : gui.isSimulationPerspective();
	}

	public static void togglePerspective(final boolean immediately) {
		if ( gui != null ) {
			gui.togglePerspective(immediately);
		}
	}

	public static void openSimulationPerspective(final boolean immediately) {
		if ( gui != null ) {
			gui.openSimulationPerspective(immediately);
		}
	}

	/**
	 * @param layerDisplayOutput
	 * @param w
	 * @param h
	 * @return
	 */
	public static IDisplaySurface getDisplaySurfaceFor(final LayeredDisplayOutput layerDisplayOutput) {
		return gui != null ? gui.getDisplaySurfaceFor(layerDisplayOutput) : null;
	}

	public static Map<String, Object> openUserInputDialog(final String title, final Map<String, Object> initialValues,
		final Map<String, IType> types) {
		if ( gui == null ) { return initialValues; }
		return gui.openUserInputDialog(title, initialValues, types);
	}

	public static void openUserControlPanel(final IScope scope, final UserPanelStatement panel) {
		if ( gui == null ) { return; }
		gui.openUserControlPanel(scope, panel);
	}

	public static void closeDialogs() {
		if ( gui == null ) { return; }
		gui.closeDialogs();
	}

	// TODO Transform this into a list
	public static IAgent getHighlightedAgent() {
		if ( gui == null ) { return null; }
		return gui.getHighlightedAgent();
	}

	public static void setHighlightedAgent(final IAgent a) {
		if ( gui == null ) { return; }
		gui.setHighlightedAgent(a);
	}

	public static void setSelectedAgent(final IAgent a) {
		if ( gui == null ) { return; }
		gui.setSelectedAgent(a);
	}

	public static void editModel(final Object eObject) {
		if ( gui == null ) { return; }
		gui.editModel(eObject);
	}

	public static void runModel(final Object object, final String exp) {
		if ( gui == null ) { return; }
		try {
			gui.runModel(object, exp);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static void updateParameterView(final IExperimentPlan exp) {
		if ( gui == null ) { return; }
		gui.updateParameterView(exp);
	}

	public static void waitForViewsToBeInitialized() {
		if ( gui == null ) { return; }
		gui.waitForViewsToBeInitialized();
	}

	/**
	 * @param e
	 */
	public static void debug(final Exception e) {
		if ( gui == null ) {
			e.printStackTrace();
		} else {
			gui.debug(e);
		}
	}

	/**
	 * @param d in milliseconds
	 */
	public static void updateSpeedDisplay(final Double d, final boolean notify) {
		if ( gui == null ) { return; }
		gui.updateSpeedDisplay(d, notify);
	}

	public static IFileMetaDataProvider getMetaDataProvider() {
		if ( gui == null ) { return null; }
		return gui.getMetaDataProvider();
	}

	public static void wipeExperiments() {
		if ( gui != null ) {
			gui.wipeExperiments();
		}
	}

	/**
	 *
	 */
	public static void closeSimulationViews(final boolean andOpenModelingPerspective) {
		if ( gui != null ) {
			gui.closeSimulationViews(andOpenModelingPerspective);
		}
	}

	/**
	 * @param type
	 * @return
	 */
	public static DisplayDescription getDisplayDescriptionFor(final String type) {
		if ( gui != null ) { return gui.getDisplayDescriptionFor(type); }
		return null;
	}

}
