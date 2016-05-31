package msi.gama.gui.swt.perspectives;

public class SavePerspectiveHandler {

	// @Execute
	// public static void execute() {
	//
	// final WorkbenchPage wp = (WorkbenchPage) SwtGui.getPage();
	// final MWindow window = wp.getWindowModel();
	// final IEclipseContext context = window.getContext();
	// final EModelService modelService = context.get(EModelService.class);
	//
	// // store model of the active perspective
	// final MPerspective activePerspective = modelService.getActivePerspective(window);
	//
	// // create a resource, which is able to store e4 model elements
	// final E4XMIResourceFactory e4xmiResourceFactory = new E4XMIResourceFactory();
	// final Resource resource = e4xmiResourceFactory.createResource(null);
	//
	// // You must clone the perspective as snippet, otherwise the running
	// // application would break, because the saving process of the resource
	// // removes the element from the running application model
	// final MUIElement clonedPerspective = modelService.cloneElement(activePerspective, window);
	//
	// // add the cloned model element to the resource so that it may be stored
	// resource.getContents().add((EObject) clonedPerspective);
	//
	// FileOutputStream outputStream = null;
	// try {
	//
	// // Use a stream to save the model element
	// final IModel m = GAMA.getModel();
	// final IExperimentPlan e = GAMA.getExperiment();
	// final String path = m.getWorkingPath();
	// final String exp = e.getName();
	// outputStream = new FileOutputStream(path + File.pathSeparator + exp + ".xmi");
	//
	// resource.save(outputStream, null);
	// } catch (final IOException ex) {
	// ex.printStackTrace();
	// } finally {
	// if ( outputStream != null ) {
	// try {
	// outputStream.close();
	// } catch (final IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
}
