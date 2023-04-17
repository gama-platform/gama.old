/*******************************************************************************************************
 *
 * AbstractNewModelWizard.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.wizards;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;

import com.google.common.net.UrlEscapers;

import msi.gama.runtime.GAMA;
import ummisco.gama.ui.navigator.contents.ResourceManager;

/**
 * The role of this wizard is to create a new file resource in the provided container. If the container resource (a
 * folder or a project) is selected in the workspace when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension "gaml" and open the registered editor.
 */

public abstract class AbstractNewModelWizard extends Wizard implements INewWizard {

	/** The Constant _AUTHOR. */
	static final String _AUTHOR = "modelAuthor";

	/** The Constant _DESCRIPTION. */
	static final String _DESCRIPTION = "modelDescription";

	/** The Constant _TITLE. */
	static final String _TITLE = "modelTitle";

	/** The Constant _FILENAME. */
	static final String _FILENAME = "modelFilename";

	/** The Constant _DOC. */
	static final String _DOC = "documentation";

	/** The Constant TEMPLATES. */
	static final Map<String, String> TEMPLATES = new HashMap<>() {
		{
			put(EXPERIMENT, "/templates/experiment.template.resource");
			put(TEST_EXP, "/templates/test.experiment.template.resource");
		}
	};
	static {
		final Bundle bundle = Platform.getBundle("ummisco.gama.ui.navigator");
		final Enumeration<URL> urls = bundle.findEntries("templates", "*.model.template.resource", false);
		while (urls.hasMoreElements()) {
			try {
				final URI uri = urls.nextElement().toURI();
				final String name = uri.getPath().replaceAll(".model.template.resource", "").replace("/templates/", "");
				TEMPLATES.put(name, uri.getPath());

			} catch (final URISyntaxException e) {
				
				e.printStackTrace();
			}

		}
	}

	/** The Constant GUI. */
	public static final String GUI = "GUI";

	/** The Constant HEADLESS. */
	public static final String HEADLESS = "Headless";

	/** The Constant EXPERIMENT. */
	public static final String EXPERIMENT = "experiment";

	/** The Constant TEST_EXP. */
	public static final String TEST_EXP = "test_experiment";

	/** The selection. */
	protected AbstractNewModelWizardPage page;
	
	/** The selection. */
	protected ISelection selection;
	// protected String fileHeader;

	/**
	 * Instantiates a new abstract new model wizard.
	 */
	public AbstractNewModelWizard() {
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

	/**
	 * Creates the page.
	 *
	 * @param selection
	 *            the selection
	 * @return the abstract new model wizard page
	 */
	public abstract AbstractNewModelWizardPage createPage(ISelection selection);

	/**
	 * Gets the page.
	 *
	 * @return the page
	 */
	public AbstractNewModelWizardPage getPage() { return page; }

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
		IContainer folder = findContainer(monitor, containerName, root);
		if (folder == null) return;
		final IProject project = folder.getProject();

		/* Add the models folder */
		final IFolder modelFolder = project.getFolder(new Path(getDefaultFolderForModels()));
		if (project == folder) {
			if (!modelFolder.exists()) { modelFolder.create(true, true, monitor); }
			folder = modelFolder;
		}

		final IFile file = folder.getFile(new Path(fileName));

		final String template = getPage().getTemplatePath();

		try (InputStream streamModel = getInputStream(folder, template, title, author, desc)) {
			ResourceManager.getInstance().reveal(file);
			file.create(streamModel, true, monitor);
			if (createDoc) {
				final IFolder libFolder = project.getFolder(new Path(_DOC));
				if (!libFolder.exists()) { libFolder.create(true, true, monitor); }

				IPath p;
				if (modelFolder.getFullPath().isPrefixOf(file.getFullPath())) {
					p = file.getFullPath().removeLastSegments(1).makeRelativeTo(modelFolder.getFullPath());
				} else {
					p = new Path("");
				}

				String name = file.getFullPath().removeFileExtension().lastSegment();
				try (InputStream resourceStream = openContentStreamMDFile(title, desc, author, name);) {
					IFolder docFolder = (IFolder) createRecursively(root, libFolder.getFullPath().append(p));
					final IFile htmlFile = docFolder.getFile(name + ".md");
					htmlFile.create(resourceStream, true, monitor);
				}
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		GAMA.getGui().editModel(null, file);
		monitor.worked(1);
	}

	/**
	 * Gets the input stream.
	 *
	 * @param folder
	 *            the folder
	 * @param template
	 *            the template
	 * @param title
	 *            the title
	 * @param author
	 *            the author
	 * @param desc
	 *            the desc
	 * @return the input stream
	 */
	@SuppressWarnings ("resource")
	private InputStream getInputStream(final IContainer folder, final String template, final String title,
			final String author, final String desc) {
		InputStream result;
		final IFile file = folder.getProject().getFile(new Path(template));
		if (file.exists()) {
			try {
				result = file.getContents();
			} catch (final CoreException e) {
				return null;
			}
		} else {
			result = getClass().getResourceAsStream(template);
		}
		return replacePlaceHolders(folder, result, title, author, desc);
	}

	/**
	 * Find container.
	 *
	 * @param monitor
	 *            the monitor
	 * @param containerName
	 *            the container name
	 * @param root
	 *            the root
	 * @return the i container
	 * @throws CoreException
	 *             the core exception
	 */
	public IContainer findContainer(final IProgressMonitor monitor, final String containerName,
			final IWorkspaceRoot root) throws CoreException {
		IResource resource = root.findMember(new Path(containerName));
		if (resource == null || !resource.exists()) {
			final boolean create = MessageDialog.openConfirm(getShell(), "Folder does not exist",
					"Folder \"" + containerName + "\" does not exist. Create it automatically ?");
			if (!create) return null;
			final IContainer folder = createRecursively(root, new Path(containerName));
			resource = folder;
		} else if (!(resource instanceof IContainer)) {
			MessageDialog.openError(getShell(), "Not a folder", containerName + " is not a folder. Cannot proceed");
			return null;
		}
		return (IContainer) resource;
	}

	/**
	 * Creates the recursively.
	 *
	 * @param root
	 *            the root
	 * @param fullFolderPath
	 *            the full folder path
	 * @return the i container
	 * @throws CoreException
	 *             the core exception
	 */
	IContainer createRecursively(final IWorkspaceRoot root, final IPath fullFolderPath) throws CoreException {
		IContainer folder = root.getProject(fullFolderPath.segment(0));
		if (folder == null) return root;
		for (int i = 1; i < fullFolderPath.segmentCount(); i++) {
			String current = fullFolderPath.segment(i);
			folder = folder.getFolder(new Path(current));
			if (!folder.exists()) { ((IFolder) folder).create(true, true, null); }
		}
		return folder;
	}

	/**
	 * Gets the default folder for models.
	 *
	 * @return the default folder for models
	 */
	protected abstract String getDefaultFolderForModels();

	/**
	 * Method for adding to the stream the header of the file just created
	 *
	 * @param folder
	 */
	protected InputStream replacePlaceHolders(final IContainer folder, final InputStream streamModel,
			final String title, final String author, final String desc) {

		String line = "";
		final StringWriter writer = new StringWriter();
		try (final InputStreamReader streamReader = new InputStreamReader(streamModel);
				final BufferedReader buffer = new BufferedReader(streamReader);) {
			while ((line = buffer.readLine()) != null) { writer.write(line + "\n"); }
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		}
		/* Final output in the String */
		final String str = writer.toString();
		final String output = getHeader(folder, str, title, author, desc);
		return new ByteArrayInputStream(output.getBytes());
	}

	/**
	 * Gets the header.
	 *
	 * @param folder
	 *            the folder
	 * @param str
	 *            the str
	 * @param title
	 *            the title
	 * @param author
	 *            the author
	 * @param desc
	 *            the desc
	 * @return the header
	 */
	protected String getHeader(final IContainer folder, final String str, final String title, final String author,
			final String desc) {
		return /* fileHeader + */str.replaceAll("\\$TITLE\\$", title).replaceAll("\\$AUTHOR\\$", author)
				.replaceAll("\\$DESC\\$", desc);
	}

	/** Initialize the file contents to contents of the given resource. */
	private InputStream openContentStreamMDFile(final String title, final String desc, final String author,
			final String fileName) throws CoreException {
		final String newline = "\n";
		String line;
		final StringBuilder sb = new StringBuilder();
		try (final InputStream input =
				this.getClass().getResourceAsStream("/templates/description-md-template.resource");
				final BufferedReader reader = new BufferedReader(new InputStreamReader(input));) {
			while ((line = reader.readLine()) != null) {
				line = line.replaceAll(_AUTHOR, author).replaceAll(_TITLE, "Documentation of " + title)
						.replaceAll(_DESCRIPTION, desc)
						.replaceAll(_FILENAME, UrlEscapers.urlFragmentEscaper().escape(fileName));

				sb.append(line);
				sb.append(newline);
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