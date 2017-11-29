/*********************************************************************************************
 *
 * 'NewFileWizard.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.wizards;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import ummisco.gama.ui.commands.RefreshHandler;

/**
 * The role of this wizard is to create a new file resource in the provided container. If the container resource (a
 * folder or a project) is selected in the workspace when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension "gaml" and open the registered editor.
 */

public class NewTestExperimentWizard extends Wizard implements INewWizard {

	private NewTestExperimentWizardPage page;
	private ISelection selection;
	private String fileHeader;

	public NewTestExperimentWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/** Adding pages to the wizard. */
	@Override
	public void addPages() {
		page = new NewTestExperimentWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using
	 * wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		final String author = page.getAuthor();
		final String title = page.getTitleName();
		final String desc = page.getDescription();

		final IRunnableWithProgress op = monitor -> {
			try {
				doFinish(containerName, fileName, author, title, desc, monitor);
			} catch (final CoreException e) {
				e.printStackTrace();
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
				// RefreshHandler.run();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (final InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
			final Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */
	private void doFinish(final String containerName, final String fileName, final String author, final String title,
			final String desc, final IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Creating " + fileName, 2);
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource container = root.findMember(new Path(containerName));

		if (!container.exists()) {
			final boolean create = MessageDialog.openConfirm(getShell(), "Folder does not exist",
					"Folder \"" + containerName + "\" does not exist. Create it automatically ?");
			if (create) {
				final IFolder folder = root.getFolder(new Path(containerName));
				folder.create(true, true, monitor);
				container = folder;
			} else
				return;
		} else if (!(container instanceof IContainer)) {
			MessageDialog.openError(getShell(), "Not a folder", containerName + " is not a folder. Cannot proceed");
			return;
		}
		IContainer folder = (IContainer) container;
		final IContainer project = folder.getProject();
		final IPath pathToExperimentFolder = folder.getFullPath();
		/* Add the tests folder */
		if (project == container) {
			final IFolder modelFolder = folder.getFolder(new Path("tests"));
			if (!modelFolder.exists()) {
				modelFolder.create(true, true, monitor);
			}
			folder = modelFolder;
		}

		/* Add the doc folder */
		// final IFolder libFolder = project.getFolder(new Path("doc"));
		// if (!libFolder.exists()) {
		// libFolder.create(true, true, monitor);
		// }

		final IFile file = folder.getFile(new Path(fileName));

		fileHeader = "/**\n" + "* Name: " + title + "\n" + "* Author: " + author + "\n" + "* Description: " + desc
				+ "\n" + "* Tags: Tag1, Tag2, TagN\n*/";

		try {
			InputStream streamModel = getClass().getResourceAsStream("/templates/test.experiment.template.resource");
			streamModel = addFileHeader(streamModel, title, desc);
			try {
				file.create(streamModel, true, monitor);
			} finally {
				streamModel.close();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		RefreshHandler.run(file);
		getShell().getDisplay().asyncExec(() -> {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file, true);
			} catch (final PartInitException e) {
				e.printStackTrace();
			}
		});
		monitor.worked(1);
	}

	/** Method for adding to the stream the header of the file just created */
	private InputStream addFileHeader(final InputStream streamModel, final String title, final String desc)
			throws CoreException {
		String line = "";
		final StringWriter writer = new StringWriter();
		try (final InputStreamReader streamReader = new InputStreamReader(streamModel);
				final BufferedReader buffer = new BufferedReader(streamReader);) {
			while ((line = buffer.readLine()) != null) {
				writer.write(line + "\n");
			}
		} catch (final IOException ioe) {
			ioe.printStackTrace();
			final IStatus status =
					new Status(IStatus.ERROR, "ExampleWizard", IStatus.OK, ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		}
		/* Final output in the String */
		final String str = writer.toString();
		final String output = fileHeader + java.lang.System.getProperty("line.separator")
				+ java.lang.System.getProperty("line.separator") + str.replaceAll("\\$TITLE\\$", title);

		return new ByteArrayInputStream(output.getBytes());
	}

	// /** Initialize an empty file contents */
	// public InputStream openContentStreamEmptyModelFile() throws CoreException {
	// String contents = fileHeader + "model {\n\t/** Insert your model definition here */\n}\n";
	// return new ByteArrayInputStream(contents.getBytes());
	// }

	// private void throwCoreException(final String message) throws CoreException {
	// IStatus status =
	// new Status(IStatus.ERROR, "msi.gama.gui.wizards", IStatus.OK, message, null);
	// throw new CoreException(status);
	// }

	/** We will accept the selection in the workbench to see if we can initialize from it. */
	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.selection = selection;
	}
}