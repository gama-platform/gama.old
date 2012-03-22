package msi.gama.headless.runtime;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IEditorFactory;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IGui;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class HeadlessListener implements IGui {

	@Override
	public void setStatus(String error, int code) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(Runnable block) {
		block.run();
	}

	@Override
	public void asyncRun(Runnable block) {
		block.run();
	}

	@Override
	public void raise(Throwable ex) {
		System.out.println("Erreur: "+ ex.getMessage());
	}

	@Override
	public IGamaView showView(String viewId, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopIfCancelled() throws InterruptedException {
		// TODO Auto-generated method stub
		//System.out.println("Stop Simulation");

	}

	@Override
	public void tell(String message) {
		System.out.println("tell : "+ message );
	}

	@Override
	public void error(String error) {
		System.out.println("error : "+ error );
	}

	@Override
	public void showParameterView(IExperiment exp) {
	//	System.out.println(exp.get)
	}

	@Override
	public void debugConsole(int cycle, String s) {
		System.out.println("debug console step "+ cycle + ": "+ s );
	}

	@Override
	public void informConsole(String s) {
		System.out.println("inform console :"+ s );
	}

	@Override
	public void updateViewOf(IDisplayOutput output) {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug(String string) {
		System.out.println("debug :"+ string );
	}

	@Override
	public void warn(String string) {
		System.out.println("warning : "+ string );
	}

	@Override
	public void runtimeError(GamaRuntimeException g) {
		System.out.println("runtime error : "+ g.getMessage() );
	}

	@Override
	public IEditorFactory getEditorFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean confirmClose(IExperiment experiment) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void prepareFor(boolean isGui) {
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
	public void setWorkbenchWindowTitle(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeViewOf(IDisplayOutput out) {
		// TODO Auto-generated method stub

	}

	@Override
	public IGamaView hideView(String viewId) {
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
	public IGraphics newGraphics(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearErrors() {
		// TODO Auto-generated method stub

	}

	@Override
	public IDisplaySurface getDisplaySurfaceFor(String keyword,
			IDisplayOutput layerDisplayOutput, double w, double h) {
		// TODO Auto-generated method stub
		return null;
	}

}
