/**
 * Created by drogoul, 26 janv. 2016
 *
 */
package msi.gama.gui.swt.commands;

import org.eclipse.core.commands.*;
import org.eclipse.ui.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.*;
import org.eclipse.ui.internal.registry.*;
import msi.gama.runtime.GAMA;

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

		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		if ( activeWorkbenchWindow != null ) {
			WorkbenchPage page = (WorkbenchPage) activeWorkbenchWindow.getActivePage();
			if ( page != null ) {
				PerspectiveDescriptor descriptor = (PerspectiveDescriptor) page.getPerspective();
				if ( descriptor != null ) {
					saveNonSingleton(page, descriptor);
				}
			}
		}
		return null;
	}

	/**
	 * Save a singleton over the user selection.
	 */
	private void saveNonSingleton(final IWorkbenchPage page, final PerspectiveDescriptor oldDesc) {
		// Get reg.
		PerspectiveRegistry reg = (PerspectiveRegistry) WorkbenchPlugin.getDefault().getPerspectiveRegistry();
		// org.eclipse.ui.internal.e4.migration.PerspectiveBuilder builder;
		// // Get persp name.
		// SavePerspectiveDialog dlg = new SavePerspectiveDialog(page.getWorkbenchWindow().getShell(), reg);
		// Look up the descriptor by id again to ensure it is still valid.
		// IPerspectiveDescriptor description = reg.findPerspectiveWithId(oldDesc.getId());
		// dlg.setInitialSelection(description);
		// if ( dlg.open() != IDialogConstants.OK_ID ) { return; }

		// Create descriptor.
		PerspectiveDescriptor newDesc = null;
		if ( newDesc == null ) {
			String name = oldDesc.getId() + GAMA.getModel().getName() + GAMA.getExperiment().getName();
			newDesc = reg.createPerspective(name, oldDesc);
		}
		System.out.println(newDesc);
		// Save state.
		page.savePerspectiveAs(newDesc);
	}
}
