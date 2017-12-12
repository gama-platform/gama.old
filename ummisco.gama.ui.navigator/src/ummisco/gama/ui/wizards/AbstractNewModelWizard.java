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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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

import msi.gama.runtime.GAMA;
import msi.gaml.operators.Strings;
import ummisco.gama.ui.navigator.contents.ResourceManager;

/**
 * The role of this wizard is to create a new file resource in the provided container. If the container resource (a
 * folder or a project) is selected in the workspace when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension "gaml" and open the registered editor.
 */

public abstract class AbstractNewModelWizard extends Wizard implements INewWizard {

	public static final String EMPTY = "empty";
	public static final String SKELETON = "skeleton";
	public static final String TEST = "test";
	public static final String EXPERIMENT = "experiment";
	public static final String TEST_EXP = "test_experiment";
	private static final Map<String, String> TEMPLATES = new HashMap<String, String>() {
		{
			put(EMPTY, "/templates/empty-file-template.resource");
			put(SKELETON, "/templates/skeleton-file-template.resource");
			put(TEST, "/templates/test-file-template.resource");
			put(EXPERIMENT, "/templates/experiment.template.resource");
			put(TEST_EXP, "/templates/test.experiment.template.resource");
		}
	};

	protected AbstractNewModelWizardPage page;
	protected ISelection selection;
	protected String fileHeader;

	public AbstractNewModelWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.selection = selection;
	}

	/** Adding pages to the wizard. */
	@Override
	public final void addPages() {
		page = createPage(selection);
		addPage(getPage());
	}

	public abstract AbstractNewModelWizardPage createPage(ISelection selection);

	public AbstractNewModelWizardPage getPage() {
		return page;
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using
	 * wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final IRunnableWithProgress op = monitor -> {
			try {
				doFinish(monitor);
			} catch (final CoreException e) {
				e.printStackTrace();
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		};
		try {
			getContainer().run(false, false, op);
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
	private void doFinish(final IProgressMonitor monitor) throws CoreException {
		final String containerName = getPage().getContainerName();
		final String fileName = getPage().getFileName();
		final String author = getPage().getAuthor();
		final String title = getPage().getModelName();
		final String desc = getPage().getDescription();
		final boolean createDoc = getPage().createDoc();

		monitor.beginTask("Creating " + fileName, 2);
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IResource container = findContainer(monitor, containerName, root);
		if (container == null)
			return;
		IContainer folder = (IContainer) container;
		final IProject project = folder.getProject();

		/* Add the models folder */
		if (project == container) {
			final IFolder modelFolder = folder.getFolder(new Path(getDefaultFolderForModels()));
			if (!modelFolder.exists()) {
				modelFolder.create(true, true, monitor);
			}
			folder = modelFolder;
		}

		/* Add the doc folder */
		if (createDoc) {
			final IFolder libFolder = project.getFolder(new Path("doc"));
			if (!libFolder.exists()) {
				libFolder.create(true, true, monitor);
			}
		}

		final IFile file = folder.getFile(new Path(fileName));

		fileHeader = "/**\n" + "* Name: " + title + "\n" + "* Author: " + author + "\n" + "* Description: " + desc
				+ "\n" + "* Tags: Tag1, Tag2, TagN\n*/" + Strings.LN + Strings.LN;

		try {
			InputStream streamModel = getClass().getResourceAsStream(TEMPLATES.get(getPage().getTemplateType()));
			streamModel = addFileHeader(folder, streamModel, title, desc);

			try {
				file.create(streamModel, true, monitor);
				if (createDoc) {
					final IFile htmlFile = project.getFile(new Path("doc/" + title + ".html"));
					final InputStream resourceStream = openContentStreamHtmlFile(title, desc, author);
					htmlFile.create(resourceStream, true, monitor);
					resourceStream.close();
				}
			} finally {
				streamModel.close();
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		ResourceManager.getInstance().reveal(file);
		GAMA.getGui().editModel(null, file);
		monitor.worked(1);
	}

	public IResource findContainer(final IProgressMonitor monitor, final String containerName,
			final IWorkspaceRoot root) throws CoreException {
		IResource container = root.findMember(new Path(containerName));
		if (container == null || !container.exists()) {
			final boolean create = MessageDialog.openConfirm(getShell(), "Folder does not exist",
					"Folder \"" + containerName + "\" does not exist. Create it automatically ?");
			if (create) {
				final IFolder folder = root.getFolder(new Path(containerName));
				folder.create(true, true, monitor);
				container = folder;
			} else
				return null;
		} else if (!(container instanceof IContainer)) {
			MessageDialog.openError(getShell(), "Not a folder", containerName + " is not a folder. Cannot proceed");
			return null;
		}
		return container;
	}

	protected abstract String getDefaultFolderForModels();

	/**
	 * Method for adding to the stream the header of the file just created
	 * 
	 * @param folder
	 */
	protected InputStream addFileHeader(final IContainer folder, final InputStream streamModel, final String title,
			final String desc) throws CoreException {

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
		final String output = getHeader(folder, str, title);
		return new ByteArrayInputStream(output.getBytes());
	}

	protected String getHeader(final IContainer folder, final String str, final String title) {
		return fileHeader + str.replaceAll("\\$TITLE\\$", title);
	}

	/** Initialize the file contents to contents of the given resource. */
	private InputStream openContentStreamHtmlFile(final String title, final String desc, final String author)
			throws CoreException {
		final String newline = "\n";
		String line;
		final StringBuffer sb = new StringBuffer();
		try {
			final InputStream input =
					this.getClass().getResourceAsStream("/templates/description-html-template.resource");
			final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
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

		} catch (final IOException ioe) {
			ioe.printStackTrace();
			final IStatus status =
					new Status(IStatus.ERROR, "ExampleWizard", IStatus.OK, ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		}

		return new ByteArrayInputStream(sb.toString().getBytes());
	}

}