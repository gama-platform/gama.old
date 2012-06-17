package msi.gama.outputs;

import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperiment;

public class HeadlessOutputManager extends GuiOutputManager {

	public HeadlessOutputManager(final OutputManager m) {
		GuiUtils.prepareFor(false);
	}

	@Override
	public void buildOutputs(final IExperiment exp) {
		System.out.println("build output headless");
		GuiUtils.hideMonitorView();
		GuiUtils.setWorkbenchWindowTitle(exp.getName() + " - " + exp.getModel().getFilePath());
		GuiUtils.showParameterView(exp);
		GuiUtils.informStatus(" Simulation of experiment " + exp.getName() + " of model " +
			exp.getModel().getName() + " ready.");
	}

}
