/*******************************************************************************************************
 *
 * FileOpener.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

	/** The Constant PAGE. */
	static final IWorkbenchPage PAGE = WorkbenchHelper.getPage();

	/**
	 * Open file.
	 *
	 * @param uri the uri
	 * @return the i editor part
	 */
	public static IEditorPart openFile(final URI uri) {
		if (uri == null) {
			MessageDialog.openWarning(null, "No file found", "Trying to open a null file");
			return null;
		}
		try {
			if (uri.isPlatformResource()) return FileOpener.openFileInWorkspace(uri);
			if (uri.isFile()) return FileOpener.openFileInFileSystem(uri);
		} catch (final PartInitException e) {
			MessageDialog.openWarning(null, "No file found",
					"The file'" + uri.toString() + "' does not exist on disk.");
		}
		MessageDialog.openWarning(null, "No file found", "The file'" + uri.toString() + "' cannot be found.");
		return null;
	}

	/**
	 * Open file in workspace.
	 *
	 * @param uri the uri
	 * @return the i editor part
	 * @throws PartInitException the part init exception
	 */
	public static IEditorPart openFileInWorkspace(final URI uri) throws PartInitException {
		final IFile file = FileUtils.getWorkspaceFile(uri);
		if (file == null) {
			MessageDialog.openWarning(null, "No file found", "The file'" + uri.toString() + "' cannot be found.");
			return null;
		}
		if (file.isLinked() && !NavigatorRoot.getInstance().getManager().validateLocation(file)) {
			MessageDialog.openWarning(null, "No file found",
					"The file'" + file.getRawLocation() + "' referenced by '" + file.getName() + "' cannot be found.");
			return null;
		}
		return IDE.openEditor(PAGE, file);
	}

	/**
	 * Open file in file system.
	 *
	 * @param uri the uri
	 * @return the i editor part
	 * @throws PartInitException the part init exception
	 */
	public static IEditorPart openFileInFileSystem(final URI uri) throws PartInitException {
		if (uri == null) return null;
		IFileStore fileStore;
		try {
			fileStore = EFS.getLocalFileSystem().getStore(Path.fromOSString(uri.toFileString()));
		} catch (final Exception e1) {
			MessageDialog.openWarning(null, "No file found", "The file'" + uri.toString() + "' cannot be found.");
			return null;
		}
		IFileInfo info;
		try {
			info = fileStore.fetchInfo();
		} catch (final Exception e) {
			MessageDialog.openWarning(null, "No file found", "The file'" + uri.toString() + "' cannot be found.");
			return null;
		}
		if (!info.exists()) {
			MessageDialog.openWarning(null, "No file found", "The file'" + uri.toString() + "' cannot be found.");
		}
		return IDE.openInternalEditorOnFileStore(PAGE, fileStore);
	}

}
