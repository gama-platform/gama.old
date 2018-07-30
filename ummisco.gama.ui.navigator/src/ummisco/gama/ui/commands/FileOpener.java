/*********************************************************************************************
 *
 * 'FileOpener.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import msi.gama.common.util.FileUtils;
import ummisco.gama.ui.navigator.contents.NavigatorRoot;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Utility methods related to open file from different type of locations.
 * 
 * @author alruiz@google.com (Alex Ruiz), Alexis Drogoul (2018)
 */
public class FileOpener {

	static final IWorkbenchPage PAGE = WorkbenchHelper.getPage();

	public static IEditorPart openFile(final String path) {
		return openFile(path, null);
	}

	public static IEditorPart openFile(final String path, final URI root) {
		return openFile(FileUtils.getURI(path, root));
	}

	public static IEditorPart openFile(final URI uri) {
		if (uri == null) {
			MessageDialog.openWarning(WorkbenchHelper.getShell(), "No file found", "Trying to open a null file");
			return null;
		}
		try {
			if (uri.isPlatformResource()) { return FileOpener.openFileInWorkspace(uri); }
			if (uri.isFile()) { return FileOpener.openFileInFileSystem(uri); }
		} catch (final PartInitException e) {
			MessageDialog.openWarning(WorkbenchHelper.getShell(), "No file found",
					"The file'" + uri.toString() + "' does not exist on disk.");
		}
		MessageDialog.openWarning(WorkbenchHelper.getShell(), "No file found",
				"The file'" + uri.toString() + "' cannot be found.");
		return null;
	}

	public static IEditorPart openFileInWorkspace(final URI uri) throws PartInitException {
		final IFile file = FileUtils.getWorkspaceFile(uri);
		if (file == null) {
			MessageDialog.openWarning(WorkbenchHelper.getShell(), "No file found",
					"The file'" + uri.toString() + "' cannot be found.");
			return null;
		}
		if (file.isLinked()) {
			if (!NavigatorRoot.getInstance().getManager().validateLocation(file)) {
				MessageDialog.openWarning(WorkbenchHelper.getShell(), "No file found", "The file'"
						+ file.getRawLocation() + "' referenced by '" + file.getName() + "' cannot be found.");
				return null;
			}
		}
		return IDE.openEditor(PAGE, file);
	}

	public static IEditorPart openFileInFileSystem(final URI uri) throws PartInitException {
		if (uri == null) { return null; }
		IFileStore fileStore;
		try {
			fileStore = EFS.getLocalFileSystem().getStore(Path.fromOSString(uri.toFileString()));
		} catch (final Exception e1) {
			MessageDialog.openWarning(WorkbenchHelper.getShell(), "No file found",
					"The file'" + uri.toString() + "' cannot be found.");
			return null;
		}
		IFileInfo info;
		try {
			info = fileStore.fetchInfo();
		} catch (final Exception e) {
			MessageDialog.openWarning(WorkbenchHelper.getShell(), "No file found",
					"The file'" + uri.toString() + "' cannot be found.");
			return null;
		}
		if (!info.exists()) {
			MessageDialog.openWarning(WorkbenchHelper.getShell(), "No file found",
					"The file'" + uri.toString() + "' cannot be found.");
		}
		return IDE.openInternalEditorOnFileStore(PAGE, fileStore);
	}

}
