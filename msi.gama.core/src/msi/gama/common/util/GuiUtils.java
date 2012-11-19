/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.util;

import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserPanelStatement;

/**
 * The class GuiUtils. A static bridge to the SWT environment. The actual dependency on SWT is
 * represented by an instance of IGui, which must be initialize when the UI plugin launches.
 * 
 * @author drogoul
 * @since 18 d�c. 2011
 * 
 */
public class GuiUtils {

	static IGui gui;

	public static final String MONITOR_VIEW_ID = "msi.gama.application.view.MonitorView";
	public static final String SPECIES_VIEW_ID = "msi.gama.application.view.SpeciesInspectView";
	public static final String AGENT_VIEW_ID = "msi.gama.application.view.AgentInspectView";
	public static final String DYNAMIC_VIEW_ID =
		"msi.gama.application.view.DynamicAgentInspectView";
	public static final String LAYER_VIEW_ID = "msi.gama.application.view.LayeredDisplayView";
	public static final String ERROR_VIEW_ID = "msi.gama.application.view.ErrorView";
	public static final String PARAMETER_VIEW_ID = "msi.gama.application.view.ParameterView";

	public static final String GRAPHSTREAM_VIEW_ID = "msi.gama.networks.ui.GraphstreamView";
	public static final String HPC_PERSPECTIVE_ID = "msi.gama.hpc.HPCPerspectiveFactory";
	
	public static final String GL_VIEW_ID = "msi.gama.jogl.GLView";
	public static final int[] defaultHighlight = new int[] { 0, 200, 200 };
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

	public static void setGUIMode() {
		headlessMode = false;
	}

	/**
	 * Method called by the UI plugin to initialize the SWT environment to talk with.
	 * @param gui an instance of IGui
	 */
	public static void setSwtGui(final IGui gui) {
		GuiUtils.gui = gui;
	}

	public static void errorStatus(final String error) {
		if ( gui != null ) {
			gui.setStatus(error, IGui.ERROR);
		}
	}

	/**
	 * @param abstractDisplayOutput
	 * @param refresh
	 */
	public static void setViewRateOf(final IDisplayOutput abstractDisplayOutput, final int refresh) {}

	/**
	 * @param viewId
	 * @param string
	 * @return
	 */
	public static IGamaView showView(final String viewId, final String name) {
		if ( gui != null ) { return gui.showView(viewId, name); }
		return null;
	}

	/**
	 * @param ex
	 */
	public static void raise(final Throwable ex) {
		if ( gui != null ) {
			gui.raise(ex);
		}
	}

	public static void stopIfCancelled() throws InterruptedException {
		if ( gui != null ) {
			gui.stopIfCancelled();
		}
	}

	public static void waitStatus(final String string) {
		if ( gui != null ) {
			gui.setStatus(string, IGui.WAIT);
		}
	}

	public static void error(final String error) {
		if ( gui != null ) {
			gui.error(error);
		}
	}

	public static void tell(final String message) {
		if ( gui != null ) {
			gui.tell(message);
		}
	}

	public static void informStatus(final String string) {
		if ( gui != null ) {
			gui.setStatus(string, IGui.INFORM);
		}
	}

	public static void asyncRun(final Runnable block) {
		if ( gui != null ) {
			gui.asyncRun(block);
		}
	}

	public static void showParameterView(final IExperiment exp) {
		if ( gui != null ) {
			gui.showParameterView(exp);
		}
	}

	public static void informConsole(final String s) {
		if ( gui != null ) {
			gui.informConsole(s);
		}
	}

	/**
	 * @param cycle
	 * @param s
	 */
	public static void debugConsole(final int cycle, final String s) {
		if ( gui != null ) {
			gui.debugConsole(cycle, s);
		}
	}

	public static void run(final Runnable block) {
		if ( gui != null ) {
			gui.run(block);
		} else {
			block.run();
		}
	}

	public static void updateViewOf(final IDisplayOutput output) {
		if ( gui != null ) {
			gui.updateViewOf(output);
		}
	}

	public static void warn(final String string) {
		if ( gui != null ) {
			gui.warn(string);
		}
	}

	public static void debug(final String string) {
		if ( gui != null ) {
			gui.debug(string);
		}
	}

	public static void runtimeError(final GamaRuntimeException g) {
		if ( gui != null ) {
			gui.runtimeError(g);
		}
	}

	public static IEditorFactory getEditorFactory() {
		if ( gui != null ) { return gui.getEditorFactory(); }
		return null;
	}

	public static boolean confirmClose(final IExperiment experiment) {
		if ( gui != null ) { return gui.confirmClose(experiment); }
		return true;
	}

	/**
	 * @param b
	 */
	public static void prepareFor(final boolean isGui) {
		if ( gui != null ) {
			gui.prepareFor(isGui);
		}
	}

	public static void showConsoleView() {
		if ( gui != null ) {
			gui.showConsoleView();
		}
	}

	public static void hideMonitorView() {

		if ( gui != null ) {
			gui.hideMonitorView();
		}

	}

	public static void setWorkbenchWindowTitle(final String string) {
		if ( gui != null ) {
			gui.setWorkbenchWindowTitle(string);
		}
	}

	public static void closeViewOf(final IDisplayOutput out) {
		if ( gui != null ) {
			gui.closeViewOf(out);
		}
	}

	public static void hideView(final String viewId) {
		if ( gui != null ) {
			gui.hideView(viewId);
		}
	}

	//
	// public static IDisplay createDisplay(final IDisplayLayer layer, final double w, final double
	// h,
	// final IGraphics g) {
	// if ( gui != null ) { return gui.createDisplay(layer, w, h, g); }
	// return null;
	// }

	
	/**
	 * @return
	 */
	public static boolean isModelingPerspective() {
		return gui == null ? false : gui.isModelingPerspective();
	}

	/**
	 * 
	 */
	public static void openModelingPerspective() {
		if ( gui != null ) {
			gui.openModelingPerspective();
		}
	}

	/**
	 * @return
	 */
	public static boolean isSimulationPerspective() {
		return gui == null ? false : gui.isSimulationPerspective();
	}

	/**
	 * 
	 */
	public static void togglePerspective() {
		if ( gui != null ) {
			gui.togglePerspective();
		}
	}

	/**
	 * 
	 */
	public static void openSimulationPerspective() {
		if ( gui != null ) {
			gui.openSimulationPerspective();
		}
	}

	/**
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static IGraphics newGraphics(final int width, final int height) {
		return gui != null ? gui.newGraphics(width, height) : null;
	}

	/**
	 * @param layerDisplayOutput
	 * @param w
	 * @param h
	 * @return
	 */
	public static IDisplaySurface getDisplaySurfaceFor(final String keyword,
		final IDisplayOutput layerDisplayOutput, final double w, final double h) {
		return gui != null ? gui.getDisplaySurfaceFor(keyword, layerDisplayOutput, w, h) : null;
	}

	/**
	 * 
	 */
	public static void clearErrors() {
		if ( gui != null ) {
			gui.clearErrors();
		}
	}

	public static Map<String, Object> openUserInputDialog(final String title,
		final Map<String, Object> initialValues) {
		if ( gui == null ) { return initialValues; }
		return gui.openUserInputDialog(title, initialValues);
	}

	public static void openUserControlPanel(final IScope scope, final UserPanelStatement panel) {
		if ( gui == null ) { return; }
		gui.openUserControlPanel(scope, panel);
	}

	public static void closeDialogs() {
		if ( gui == null ) { return; }
		gui.closeDialogs();
	}

	public static IAgent getHighlightedAgent() {
		if ( gui == null ) { return null; }
		return gui.getHighlightedAgent();
	}

	public static void setHighlightedAgent(final IAgent a) {
		if ( gui == null ) { return; }
		gui.setHighlightedAgent(a);
		GAMA.getExperiment().getOutputManager().forceUpdateOutputs();
	}

}
