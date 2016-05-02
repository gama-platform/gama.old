package msi.gama.gui.swt.perspectives;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4XMIResourceFactory;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.internal.WorkbenchPage;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.GAMA;

public class SavePerspectiveHandler {

	// @Execute
	public static void execute() {

		final WorkbenchPage wp = (WorkbenchPage) SwtGui.getPage();
		final MWindow window = wp.getWindowModel();
		final IEclipseContext context = window.getContext();
		final EModelService modelService = context.get(EModelService.class);

		// store model of the active perspective
		final MPerspective activePerspective = modelService.getActivePerspective(window);

		// create a resource, which is able to store e4 model elements
		final E4XMIResourceFactory e4xmiResourceFactory = new E4XMIResourceFactory();
		final Resource resource = e4xmiResourceFactory.createResource(null);

		// You must clone the perspective as snippet, otherwise the running
		// application would break, because the saving process of the resource
		// removes the element from the running application model
		final MUIElement clonedPerspective = modelService.cloneElement(activePerspective, window);

		// add the cloned model element to the resource so that it may be stored
		resource.getContents().add((EObject) clonedPerspective);

		FileOutputStream outputStream = null;
		try {

			// Use a stream to save the model element
			final IModel m = GAMA.getModel();
			final IExperimentPlan e = GAMA.getExperiment();
			final String path = m.getWorkingPath();
			final String exp = e.getName();
			outputStream = new FileOutputStream(path + File.pathSeparator + exp + ".xmi");

			resource.save(outputStream, null);
		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			if ( outputStream != null ) {
				try {
					outputStream.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
