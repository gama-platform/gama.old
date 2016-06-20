package ummisco.gama.ui;

import org.eclipse.ui.IStartup;

import ummisco.gama.ui.utils.CleanupHelper;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		CleanupHelper.run();
	}

}
