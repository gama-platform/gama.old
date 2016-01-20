package msi.gama.application;

import org.eclipse.ui.IStartup;
import msi.gama.gui.swt.SwtGui;
import msi.gama.runtime.GAMA;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		System.err.println("Early startup of the SWT Regular UI plugin so as to ensure all services are setup");
		// We make sure we do not replace the XText GUI
		if ( GAMA.getRegularGui() == null ) {
			GAMA.setRegularGui(new SwtGui());
		}
	}

}
