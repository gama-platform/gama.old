/*********************************************************************************************
 *
 * 'NewProjectWizard.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import msi.gama.application.workspace.WorkspaceModelsManager;

public class NewProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	/**
	 * Use the WizardNewProjectCreationPage, which is provided by the Eclipse framework.
	 */
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

		if (project != null) { return true; }

		final IProject projectHandle = wizardPage.getProjectHandle();
		final URI projectURI = !wizardPage.useDefaults() ? wizardPage.getLocationURI() : null;
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription desc = workspace.newProjectDescription(projectHandle.getName());
		desc.setLocationURI(projectURI);

		/**
		 * An operation object that modifies workspaces in order to create new projects.
		 */
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

			@Override
			protected void execute(final IProgressMonitor monitor) throws CoreException {
				createProject(desc, projectHandle, monitor);
			}
		};

		try {
			getContainer().run(true, true, op);
		} catch (final InterruptedException e) {
			return false;
		} catch (final InvocationTargetException e) {
			final Throwable realException = e.getTargetException();
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

			if (monitor.isCanceled()) { throw new OperationCanceledException(); }
			proj.open(new SubProgressMonitor(monitor, 1000));

			WorkspaceModelsManager.setValuesProjectDescription(proj, false, false, false, null);

			/*
			 * We now have the project and we can do more things with it before updating the perspective.
			 */
			final IContainer container = proj;
			//
			// /* Add the doc folder */
			// final IFolder libFolder = container.getFolder(new Path("doc"));
			// libFolder.create(true, true, monitor);
			//
			// /* Add the snapshots folder in the doc folder */
			// final IFolder snapshotsFolder = libFolder.getFolder(new Path("snapshots"));
			// snapshotsFolder.create(true, true, monitor);

			/* Add the models folder */
			final IFolder modelFolder = container.getFolder(new Path("models"));
			modelFolder.create(true, true, monitor);

			/* Add the includes folder */
			final IFolder incFolder = container.getFolder(new Path("includes"));
			incFolder.create(true, true, monitor);

			/* Add the images folder */
			final IFolder imFolder = container.getFolder(new Path("tests"));
			imFolder.create(true, true, monitor);

		} catch (final CoreException ioe) {
			final IStatus status =
					new Status(IStatus.ERROR, "ProjectWizard", IStatus.OK, ioe.getLocalizedMessage(), null);
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