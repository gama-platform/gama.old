/*********************************************************************************************
 *
 *
 * 'NewProjectWizard.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import msi.gama.application.projects.WorkspaceModelsManager;

public class NewProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	/** Use the WizardNewProjectCreationPage, which is provided by the Eclipse framework. */
	public static final String NATURE_ID = "msi.gama.application.nature.gamaNature";
	private WizardNewProjectCreationPage wizardPage;
	// private IConfigurationElement config;
	private IProject project;

	public NewProjectWizard() {
		super();
	}

	@Override
	public void addPages() {
		wizardPage = new WizardNewProjectCreationPage("NewGAMAProject");
		wizardPage.setDescription("Create a new GAMA Project.");
		wizardPage.setTitle("New GAMA Project");
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {

		if ( project != null ) { return true; }

		final IProject projectHandle = wizardPage.getProjectHandle();
		URI projectURI = !wizardPage.useDefaults() ? wizardPage.getLocationURI() : null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription desc = workspace.newProjectDescription(projectHandle.getName());
		desc.setLocationURI(projectURI);

		/** An operation object that modifies workspaces in order to create new projects. */
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

			@Override
			protected void execute(final IProgressMonitor monitor) throws CoreException {
				createProject(desc, projectHandle, monitor);
			}
		};

		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}

		project = projectHandle;

		// if ( project == null ) { return false; }

		// BasicNewProjectResourceWizard.updatePerspective(config);

		return true;

	}

	/**
	 * This creates the project in the workspace.
	 *
	 * @param description
	 * @param projectHandle
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	void createProject(final IProjectDescription description, final IProject proj, final IProgressMonitor monitor)
		throws CoreException, OperationCanceledException {
		try {

			monitor.beginTask("", 2000);
			proj.create(description, new SubProgressMonitor(monitor, 1000));

			if ( monitor.isCanceled() ) { throw new OperationCanceledException(); }
			proj.open(new SubProgressMonitor(monitor, 1000));
			// proj.open(IResource., new SubProgressMonitor(monitor, 1000));

			WorkspaceModelsManager.setValuesProjectDescription(proj, false, false, null);

			/*
			 * We now have the project and we can do more things with it before updating
			 * the perspective.
			 */
			IContainer container = proj;

			/* Add the doc folder */
			final IFolder libFolder = container.getFolder(new Path("doc"));
			libFolder.create(true, true, monitor);

			/* Add the snapshots folder in the doc folder */
			final IFolder snapshotsFolder = libFolder.getFolder(new Path("snapshots"));
			snapshotsFolder.create(true, true, monitor);

			/* Add the models folder */
			final IFolder modelFolder = container.getFolder(new Path("models"));
			modelFolder.create(true, true, monitor);

			/* Add the includes folder */
			final IFolder incFolder = container.getFolder(new Path("includes"));
			incFolder.create(true, true, monitor);

			/* Add the images folder */
			final IFolder imFolder = container.getFolder(new Path("images"));
			imFolder.create(true, true, monitor);

		} catch (CoreException ioe) {
			IStatus status = new Status(IStatus.ERROR, "ProjectWizard", IStatus.OK, ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		} finally {
			monitor.done();
			// RefreshHandler.run();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		// snipped...
	}

	/** Sets the initialization data for the wizard. */
	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data)
		throws CoreException {
		// snipped...
	}
}