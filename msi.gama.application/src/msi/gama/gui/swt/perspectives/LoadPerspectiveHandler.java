package msi.gama.gui.swt.perspectives;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.internal.workbench.E4XMIResourceFactory;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.internal.WorkbenchPage;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.model.IModel;

public class LoadPerspectiveHandler {

	@Execute
	public static boolean execute(final IModel model, final String experiment) {
		final IEclipseContext context = SwtGui.getWindow().getService(IEclipseContext.class);
		final WorkbenchPage wp = (WorkbenchPage) SwtGui.getPage();
		final MWindow window = wp.getWindowModel();
		final EModelService modelService = context.get(EModelService.class);
		final EPartService ps = context.get(EPartService.class);

		// create a resource, which is able to store e4 model elements
		final E4XMIResourceFactory e4xmiResourceFactory = new E4XMIResourceFactory();
		final Resource resource = e4xmiResourceFactory.createResource(null);

		FileInputStream inputStream = null;
		try {

			final String path = model.getWorkingPath();
			final String exp = experiment;
			final File f = new File(path + File.pathSeparator + exp + ".xmi");
			if ( !f.exists() )
				return false;
			inputStream = new FileInputStream(f.getAbsolutePath());

			// load the stored model element
			resource.load(inputStream, null);

			if ( !resource.getContents().isEmpty() ) {

				// after the model element is loaded it can be obtained from the
				// contents of the resource
				final MPerspective loadedPerspective = (MPerspective) resource.getContents().get(0);

				// get the parent perspective stack, so that the loaded
				// perspective can be added to it.
				final MPerspective activePerspective = modelService.getActivePerspective(window);
				final MElementContainer<MUIElement> perspectiveParent = activePerspective.getParent();

				// remove the current perspective, which should be replaced by
				// the loaded one
				// final List<MPerspective> alreadyPresentPerspective =
				// modelService.findElements(window, loadedPerspective.getElementId(), MPerspective.class, null);
				// for ( final MPerspective perspective : alreadyPresentPerspective ) {
				// modelService.removePerspectiveModel(perspective, window);
				// }

				// add the loaded perspective and switch to it
				perspectiveParent.getChildren().add(loadedPerspective);

				ps.switchPerspective(loadedPerspective);
			}
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} finally {
			if ( inputStream != null ) {
				try {
					inputStream.close();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}