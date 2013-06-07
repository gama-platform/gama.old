package msi.gama.outputs;

import msi.gama.common.util.GuiUtils;

public class HeadlessOutputManager extends GuiOutputManager {

	public HeadlessOutputManager(final SimulationOutputManager m) {
		GuiUtils.prepareFor(false);
	}

}
