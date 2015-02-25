package msi.gama.lang.gaml.ui;

import msi.gama.common.util.GuiUtils;
import msi.gama.lang.gaml.GamlRuntimeModule;
import org.eclipse.ui.IStartup;

public class AutoStartup implements IStartup {

	@Override
	public void earlyStartup() {
		System.err.println("Early startup of the XText UI plugin so as to ensure all services are setup");
		GuiUtils.setSwtGui(new XtextGui());
		GamlRuntimeModule.staticInitialize();
		// System.out.println("Gaml Editor loaded: " + XtextEditor.ID);
		// System.out.println("Gaml Decorator loaded: " + GamlDecorator.decoratorId);
		// System.out.println("Cleaning old state");

		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// try {
		// workspace.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		// } catch (CoreException ex) {
		// ex.printStackTrace();
		// }

	}

}
