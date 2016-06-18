package ummisco.gama.ui.startup;

import org.eclipse.ui.IStartup;

import ummisco.gama.ui.utils.SwtGui;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.factories.AgentMenuFactory;
import ummisco.gama.ui.factories.UserDialogFactory;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		GamaKeyBindings.install();
		SwtGui.setAgentMenuFactory(new AgentMenuFactory());
		SwtGui.setUserDialogMenuFactory(new UserDialogFactory());

	}

}
