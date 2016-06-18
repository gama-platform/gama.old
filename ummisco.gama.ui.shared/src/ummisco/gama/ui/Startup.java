package ummisco.gama.ui;

import org.eclipse.ui.IStartup;

import msi.gama.runtime.GAMA;
import ummisco.gama.ui.utils.CleanupHelper;
import ummisco.gama.ui.utils.SwtGui;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		CleanupHelper.run();
		// System.err.println(Thread.currentThread().getName() +
		// "Early startup of the SWT Regular UI plugin so as to ensure all
		// services are setup");
		// We make sure we do not replace the XText GUI
		if (GAMA.getRegularGui() == null) {
			GAMA.setRegularGui(new SwtGui());
		}
	}

}
