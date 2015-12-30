/*********************************************************************************************
 * 
 * 
 * 'UpdateBuiltInModelsHandler.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator.commands;

import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.*;
import msi.gama.application.projects.WorkspaceModelsManager;

public class UpdateBuiltInModelsHandler extends AbstractHandler {

	IWorkbenchPage page;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		Job job = new Job("Updating the Built-in Models Library") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				// Nothing to do really. Maybe a later version will remove this command. See Issue 669
				WorkspaceModelsManager.linkSampleModelsToWorkspace();
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();

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
