/**
 * Created by drogoul, 26 janv. 2016
 *
 */
package msi.gama.gui.swt.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;

/**
 * Class SavePerspectiveHandler.
 *
 * @author drogoul
 * @since 26 janv. 2016
 *
 */
public class SavePerspectiveHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(final ExecutionEvent event) {

		// msi.gama.gui.swt.perspectives.SavePerspectiveHandler.execute();

		// final IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		// if ( activeWorkbenchWindow != null ) {
		// final WorkbenchPage page = (WorkbenchPage) activeWorkbenchWindow.getActivePage();
		// if ( page != null ) {
		// final PerspectiveDescriptor descriptor = (PerspectiveDescriptor) page.getPerspective();
		// if ( descriptor != null ) {
		// saveNonSingleton(page, descriptor);
		// }
		// }
		// }
		return null;
	}

	/**
	 * Save a singleton over the user selection.
	 */
	private void saveNonSingleton(final IWorkbenchPage page, final PerspectiveDescriptor oldDesc) {
		// Get reg.
		final PerspectiveRegistry reg = (PerspectiveRegistry) WorkbenchPlugin.getDefault().getPerspectiveRegistry();
		// org.eclipse.ui.internal.e4.migration.PerspectiveBuilder builder;
		// // Get persp name.
		// final SavePerspectiveDialog dlg = new SavePerspectiveDialog(page.getWorkbenchWindow().getShell(), reg);
		final IPerspectiveDescriptor description = reg.findPerspectiveWithId(oldDesc.getId());
		// dlg.setInitialSelection(description);
		// if ( dlg.open() != IDialogConstants.OK_ID ) { return; }

		// Create descriptor.
		// PerspectiveDescriptor newDesc = null;
		// if ( newDesc == null ) {
		// final String name = SwtGui.getNewPerspectiveName(GAMA.getModel(), GAMA.getExperiment().getName());
		// newDesc = reg.createPerspective(name, oldDesc);

		// }
		System.out.println("Saving " + description.getId());
		// Save state.
		page.savePerspectiveAs(description);
	}
}
