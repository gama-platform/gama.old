/*********************************************************************************************
 *
 * 'UpdateBuiltInModelsHandler.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import msi.gama.application.workspace.WorkspaceModelsManager;

public class UpdateBuiltInModelsHandler extends AbstractHandler {

	IWorkbenchPage page;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		WorkspaceModelsManager.linkSampleModelsToWorkspace();

		// job.addJobChangeListener(new JobChangeAdapter() {
		//
		// @Override
		// public void done(final IJobChangeEvent event) {
		// RefreshHandler.run();
		// }
		//
		// });

		return null;
	}

}
