/*********************************************************************************************
 *
 *
 * 'HeadlessListener.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.runtime;

import java.util.Map;
import java.util.logging.*;
import org.eclipse.core.runtime.CoreException;
import msi.gama.common.interfaces.*;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.common.util.AbstractGui;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.*;
import msi.gama.outputs.display.NullDisplaySurface;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.types.IType;

public class HeadlessListener extends AbstractGui {

	static Logger LOGGER = LogManager.getLogManager().getLogger("");

	static {

		if ( GAMA.isInHeadLessMode() ) {

			for ( Handler h : LOGGER.getHandlers() ) {
				h.setLevel(Level.ALL);
			}
			LOGGER.setLevel(Level.ALL);
			// Handler h = new ConsoleHandler();
			// h.setLevel(Level.ALL);
			// LOGGER.addHandler(h);
			// System.out.println("Configuring Headless Mode");
			// System.out.println("Configuring Headless Mode");

		}
		GAMA.setHeadlessGui(new HeadlessListener());
	}

	@Override
	public Map<String, Object> openUserInputDialog(final String title, final Map<String, Object> initialValues,
		final Map<String, IType> types) {
		return null;
	}

	@Override
	public void openUserControlPanel(final IScope scope, final UserPanelStatement panel) {}

	@Override
	public void closeDialogs() {}

	@Override
	public IAgent getHighlightedAgent() {
		return null;
	}

	@Override
	public void setHighlightedAgent(final IAgent a) {}

	@Override
	public void setStatus(final String error, final int code) {}

	@Override
	public void run(final Runnable block) {
		block.run();
	}

	@Override
	public void asyncRun(final Runnable block) {
		block.run();
	}

	@Override
	public void raise(final Throwable ex) {
		System.out.println("Error: " + ex.getMessage());
		// System.out.println("Error: " + ex.getMessage());
	}

	@Override
	public IGamaView showView(final String viewId, final String name, final int code) {
		return null;
	}

	@Override
	public void tell(final String message) {
		System.out.println("Message: " + message);
		// System.out.println("Message: " + message);
	}

	@Override
	public void error(final String error) {
		// System.out.println("Error: " + error);
		System.out.println("Error: " + error);

	}

	@Override
	public void showParameterView(final IExperimentPlan exp) {}

	@Override
	public void debugConsole(final int cycle, final String s) {
		System.out.println("Debug (step " + cycle + "): " + s);
		// System.out.println("Debug (step " + cycle + "): " + s);
	}

	@Override
	public void informConsole(final String s) {
		System.out.println("Information: " + s);
		// System.out.println("Information: " + s);
	}

	// @Override
	// public void updateViewOf(final IDisplayOutput output) {}

	@Override
	public void debug(final String string) {
		System.out.println("Debug: " + string);
	}

	@Override
	public void warn(final String string) {
		System.out.println("Warning: " + string);
		// System.out.println("Warning: " + string);
	}

	@Override
	public void runtimeError(final GamaRuntimeException g) {
		System.out.println("Runtime error: " + g.getMessage());
		// System.out.println("Runtime error: " + g.getMessage());
	}

	@Override
	public IEditorFactory getEditorFactory() {
		return null;
	}

	@Override
	public boolean confirmClose(final IExperimentPlan experiment) {
		return true;
	}

	@Override
	public void prepareForExperiment(final IExperimentPlan exp) {}

	@Override
	public void showConsoleView() {}

	@Override
	public void setWorkbenchWindowTitle(final String string) {}

	//
	// @Override
	// public void closeViewOf(final IDisplayOutput out) {}

	@Override
	public IGamaView hideView(final String viewId) {
		return null;
	}

	@Override
	public boolean isModelingPerspective() {
		return true;
	}

	@Override
	public boolean openModelingPerspective(final boolean immediately) {
		return false;
	}

	@Override
	public boolean isSimulationPerspective() {
		return true;
	}

	@Override
	public void togglePerspective(final boolean immediately) {}

	@Override
	public boolean openSimulationPerspective(final boolean immediately) {
		return true;
	}

	static Map<String, Class> displayClasses = null;

	@Override
	public IDisplaySurface getDisplaySurfaceFor(final LayeredDisplayOutput output) {

		IDisplaySurface surface = null;
		final IDisplayCreator creator = DISPLAYS.get("image");
		if ( creator != null ) {
			surface = creator.create(output);
			surface.outputReloaded();
		} else {
			return new NullDisplaySurface();
			// throw GamaRuntimeException.error("Display " + keyword + " is not defined anywhere.", scope);
		}
		return surface;
	}

	@Override
	public void editModel(final Object eObject) {}

	@Override
	public void updateParameterView(final IExperimentPlan exp) {}

	//
	// @Override
	// public void cycleDisplayViews(final Set<String> names) {}

	@Override
	public void setSelectedAgent(final IAgent a) {}

	@Override
	public void cleanAfterExperiment(final IExperimentPlan exp) {}

	@Override
	public void prepareForSimulation(final SimulationAgent agent) {}

	@Override
	public void cleanAfterSimulation() {}

	@Override
	public void waitForViewsToBeInitialized() {}

	@Override
	public void debug(final Exception e) {
		e.printStackTrace();
	}

	@Override
	public void runModel(final Object object, final String exp) throws CoreException {}

	/**
	 * Method updateSpeedDisplay()
	 * @see msi.gama.common.interfaces.IGui#updateSpeedDisplay(java.lang.Double)
	 */
	@Override
	public void updateSpeedDisplay(final Double d, final boolean notify) {}

	/**
	 * Method showWebEditor()
	 * @see msi.gama.common.interfaces.IGui#showWebEditor(java.lang.String, java.lang.String)
	 */
	@Override
	public Object showWebEditor(final String url, final String html) {
		return null;
	}

	/**
	 * Method beginSubStatus()
	 * @see msi.gama.common.interfaces.IGui#beginSubStatus(java.lang.String)
	 */
	@Override
	public void beginSubStatus(final String name) {}

	/**
	 * Method endSubStatus()
	 * @see msi.gama.common.interfaces.IGui#endSubStatus(java.lang.String)
	 */
	@Override
	public void endSubStatus(final String name) {}

	/**
	 * Method setSubStatusCompletion()
	 * @see msi.gama.common.interfaces.IGui#setSubStatusCompletion(double)
	 */
	@Override
	public void setSubStatusCompletion(final double status) {}

	/**
	 * Method getName()
	 * @see msi.gama.common.interfaces.IGui#getName()
	 */
	@Override
	public String getName() {
		return "Headless";
	}

	/**
	 * Method setStatus()
	 * @see msi.gama.common.interfaces.IGui#setStatus(java.lang.String, msi.gama.util.GamaColor)
	 */
	@Override
	public void setStatusInternal(final String msg, final GamaColor color) {
		System.out.println(msg);
	}

	/**
	 * Method resumeStatus()
	 * @see msi.gama.common.interfaces.IGui#resumeStatus()
	 */
	@Override
	public void resumeStatus() {}

	/**
	 * Method findView()
	 * @see msi.gama.common.interfaces.IGui#findView(msi.gama.outputs.IDisplayOutput)
	 */
	@Override
	public IGamaView findView(final IDisplayOutput output) {
		return null;
	}

	/**
	 * Method getMetaDataProvider()
	 * @see msi.gama.common.interfaces.IGui#getMetaDataProvider()
	 */
	@Override
	public IFileMetaDataProvider getMetaDataProvider() {
		return null;
	}

	/**
	 * Method wipeExperiments()
	 * @see msi.gama.common.interfaces.IGui#wipeExperiments()
	 */
	@Override
	public void wipeExperiments() {}

	/**
	 * Method closeSimulationViews()
	 * @see msi.gama.common.interfaces.IGui#closeSimulationViews(boolean)
	 */
	@Override
	public void closeSimulationViews(final boolean andOpenModelingPerspective) {}

	/**
	 * Method getDisplayDescriptionFor()
	 * @see msi.gama.common.interfaces.IGui#getDisplayDescriptionFor(java.lang.String)
	 */
	@Override
	public DisplayDescription getDisplayDescriptionFor(final String name) {
		return null;
	}
}
