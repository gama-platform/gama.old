package msi.gama.headless.runtime;

import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserPanel;

public class HeadlessListener implements IGui {

	@Override
	public void setStatus(final String error, final int code) {
		// TODO Auto-generated method stub
		// IKeyword.GUI_
	}

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
		System.out.println("Erreur: " + ex.getMessage());
	}

	@Override
	public IGamaView showView(final String viewId, final String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopIfCancelled() throws InterruptedException {
		// TODO Auto-generated method stub
		// System.out.println("Stop Simulation");

	}

	@Override
	public void tell(final String message) {
		System.out.println("tell : " + message);
	}

	@Override
	public void error(final String error) {
		System.out.println("error : " + error);
	}

	@Override
	public void showParameterView(final IExperiment exp) {
		// System.out.println(exp.get)
	}

	@Override
	public void debugConsole(final int cycle, final String s) {
		System.out.println("debug console step " + cycle + ": " + s);
	}

	@Override
	public void informConsole(final String s) {
		System.out.println("inform console :" + s);
	}

	@Override
	public void updateViewOf(final IDisplayOutput output) {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug(final String string) {
		System.out.println("debug :" + string);
	}

	@Override
	public void warn(final String string) {
		System.out.println("warning : " + string);
	}

	@Override
	public void runtimeError(final GamaRuntimeException g) {
		System.out.println("runtime error : " + g.getMessage());
	}

	@Override
	public IEditorFactory getEditorFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean confirmClose(final IExperiment experiment) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void prepareFor(final boolean isGui) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showConsoleView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideMonitorView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWorkbenchWindowTitle(final String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeViewOf(final IDisplayOutput out) {
		// TODO Auto-generated method stub

	}

	@Override
	public IGamaView hideView(final String viewId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isModelingPerspective() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean openModelingPerspective() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSimulationPerspective() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void togglePerspective() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean openSimulationPerspective() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public IGraphics newGraphics(final int width, final int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearErrors() {
		// TODO Auto-generated method stub

	}

	@Override
	public IDisplaySurface getDisplaySurfaceFor(final String keyword,
		final IDisplayOutput layerDisplayOutput, final double w, final double h) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> openUserInputDialog(final String title,
		final Map<String, Object> initialValues) {
		return initialValues;
	}

	@Override
	public void openUserControlPanel(final IScope scope, final UserPanel panel) {}

	@Override
	public void closeDialogs() {}

	@Override
	public IAgent getHighlightedAgent() {
		return null;
	}

	@Override
	public void setHighlightedAgent(final IAgent a) {}

}
