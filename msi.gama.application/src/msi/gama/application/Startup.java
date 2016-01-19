package msi.gama.application;

import org.eclipse.ui.IStartup;
import msi.gama.gui.swt.SwtGui;
import msi.gama.runtime.GAMA;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		GAMA.setRegularGui(new SwtGui());
	}

}
