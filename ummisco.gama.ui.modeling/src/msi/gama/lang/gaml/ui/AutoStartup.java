package msi.gama.lang.gaml.ui;

import org.eclipse.ui.IStartup;

import msi.gama.lang.gaml.GamlRuntimeModule;
import msi.gama.runtime.GAMA;

public class AutoStartup implements IStartup {

	@Override
	public void earlyStartup() {
		System.err.println(Thread.currentThread().getName()
				+ "Early startup of the XText UI plugin so as to ensure all services are setup");
		GAMA.setRegularGui(new XtextGui());
		GamlRuntimeModule.staticInitialize();
		// System.out.println("Gaml Editor loaded: " + XtextEditor.ID);
		// System.out.println("Gaml Decorator loaded: " +
		// GamlDecorator.decoratorId);
		// System.out.println("Cleaning old state");

		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// try {
		// workspace.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		// } catch (CoreException ex) {
		// ex.printStackTrace();
		// }

	}

}
