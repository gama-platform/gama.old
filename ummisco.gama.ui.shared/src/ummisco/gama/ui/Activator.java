package ummisco.gama.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import msi.gama.runtime.GAMA;
import ummisco.gama.ui.utils.SwtGui;

public class Activator extends AbstractUIPlugin {

	public Activator() {
		if (GAMA.getRegularGui() == null) {
			GAMA.setRegularGui(new SwtGui());
		}
	}

}
