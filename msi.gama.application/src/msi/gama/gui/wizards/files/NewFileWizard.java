/*********************************************************************************************
 *
 *
 * 'NewFileWizard.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.wizards.files;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import msi.gaml.operators.Strings;

/**
 * The role of this wizard is to create a new file resource in the provided container. If
 * the container resource (a folder or a project) is selected in the workspace when the
 * wizard is opened, it will accept it as the target container. The wizard creates one
 * file with the extension "gaml" and open the registered editor.
 */

public class NewFileWizard extends Wizard implements INewWizard {

	private NewFileWizardPage page;
	private ISelection selection;
	private String fileHeader;

	public NewFileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/** Adding pages to the wizard. */
	@Override
	public void addPages() {
		page = new NewFileWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We will create
	 * an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String typeOfModel = page.getTypeOfModel();
		final String fileName = page.getFileName();
		final String author = page.getAuthor();
		final String title = page.getModelName();
		final String desc = page.getDescription();
		final boolean htmlTemplate = page.getValueHtmlTemplate();
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(final IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, typeOfModel, fileName, author, title, desc, htmlTemplate, monitor);
				} catch (CoreException e) {
					e.printStackTrace();
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
					// RefreshHandler.run();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing or just
	 * replace its contents, and open the editor on the newly created file.
	 */
	private void doFinish(final String containerName, final String typeOfModel, final String fileName,
		final String author, final String title, final String desc, final boolean htmlTemplate,
		final IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));

		if ( !resource.exists() || !(resource instanceof IContainer) ) {
			// MessageDialog.openError(getShell(), "Container \"" + containerName +
			// "\" does not exist. Creating it automatically", "");
			// IFolder folder = root.getFolder(new Path(containerName));
			// folder.create(true, true, monitor);
			// resource = folder;
		}

		IContainer container = resource.getProject();

		/* Add the models folder */
		final IFolder modelFolder = container.getFolder(new Path("models"));
		if ( !modelFolder.exists() ) {
			modelFolder.create(true, true, monitor);
		}

		/* Add the doc folder */
		final IFolder libFolder = container.getFolder(new Path("doc"));
		if ( !libFolder.exists() ) {
			libFolder.create(true, true, monitor);
		}

		final IFile file = container.getFile(new Path("models/" + fileName));

		fileHeader = "/**\n" + "* Name: " + title + "\n" + "* Author: " + author + "\n" + "* Description: " + desc +
			"\n" + "* Tags: Tag1, Tag2, TagN\n*/";
		// "/**\n * " + title + "\n" + " * Author: " + author + "\n" + " * Description: " + desc + "\n" + " */\n\n";

		InputStream streamModel = null;

		try {
			if ( typeOfModel.equals("empty") ) {
				/* Initialize a skeleton file contents */
				streamModel = getClass().getResourceAsStream("/templates/empty-file-template.resource");
			} else if ( typeOfModel.equals("skeleton") ) {
				streamModel = getClass().getResourceAsStream("/templates/skeleton-file-template.resource");
			} else if ( typeOfModel.equals("example") ) {
				streamModel = getClass().getResourceAsStream("/templates/example-file-template.resource");
			}
			if ( streamModel != null ) {
				streamModel = addFileHeader(streamModel, title, desc);

				try {
					file.create(streamModel, true, monitor);
					if ( htmlTemplate ) {
						final IFile htmlFile = container.getFile(new Path("doc/" + title + ".html"));
						InputStream resourceStream = openContentStreamHtmlFile(title, desc, author);
						htmlFile.create(resourceStream, true, monitor);
						resourceStream.close();
					}
				} finally {
					streamModel.close();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});
		monitor.worked(1);
	}

	/** Method for adding to the stream the header of the file just created */
	private InputStream addFileHeader(final InputStream streamModel, final String title, final String desc)
		throws CoreException {
		String line = "";
		StringWriter writer = new StringWriter();
		try {
			InputStreamReader streamReader = new InputStreamReader(streamModel);
			/* The buffer for the readline */
			BufferedReader buffer = new BufferedReader(streamReader);
			try {
				while ((line = buffer.readLine()) != null) {
					writer.write(line + "\n");
				}
			} finally {
				buffer.close();
				streamReader.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			IStatus status = new Status(IStatus.ERROR, "ExampleWizard", IStatus.OK, ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		}
		/* Final output in the String */
		String str = writer.toString();
		String output = fileHeader + Strings.LN + Strings.LN + str.replaceAll("\\$TITLE\\$", title);

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

	/** Initialize the file contents to contents of the given resource. */
	private InputStream openContentStreamHtmlFile(final String title, final String desc, final String author)
		throws CoreException {
		final String newline = "\n";
		String line;
		StringBuffer sb = new StringBuffer();
		try {
			InputStream input = this.getClass().getResourceAsStream("/templates/description-html-template.resource");
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			try {
				while ((line = reader.readLine()) != null) {
					line = line.replaceAll("authorModel", "By " + author);
					line = line.replaceAll("titleModel", "Description of the model " + title);
					line = line.replaceAll("descModel", desc);
					sb.append(line);
					sb.append(newline);
				}

			} finally {
				reader.close();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			IStatus status = new Status(IStatus.ERROR, "ExampleWizard", IStatus.OK, ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		}

		return new ByteArrayInputStream(sb.toString().getBytes());
	}

	/** We will accept the selection in the workbench to see if we can initialize from it. */
	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.selection = selection;
	}
}