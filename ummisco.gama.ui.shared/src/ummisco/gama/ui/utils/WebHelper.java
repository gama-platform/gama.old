package ummisco.gama.ui.utils;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import msi.gama.common.GamaPreferences;

public class WebHelper {

	private static URL HOME_URL;

	public static URL getWelcomePageURL() {
		if (HOME_URL == null)
			try {
				HOME_URL = FileLocator
						.toFileURL(Platform.getBundle("ummisco.gama.ui.shared").getEntry("/welcome/welcome.html"));
			} catch (final IOException e) {
				e.printStackTrace();
			}
		return HOME_URL;
	}

	public static void openWelcomePage(final boolean ifEmpty) {
		if (ifEmpty && WorkbenchHelper.getPage().getActiveEditor() != null) {
			return;
		}
		if (ifEmpty && !GamaPreferences.CORE_SHOW_PAGE.getValue()) {
			return;
		}
		// get the workspace
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		// create the path to the file
		final IPath location = new Path(getWelcomePageURL().getPath());
		
		// try to get the IFile (returns null if it could not be found in the
		// workspace)
		final IFile file = workspace.getRoot().getFileForLocation(location);
		IEditorInput input;
		if (file == null) {
			// not found in the workspace, get the IFileStore (external files)
			final IFileStore fileStore = EFS.getLocalFileSystem().getStore(location);
			input = new FileStoreEditorInput(fileStore);
		
		} else {
			input = new FileEditorInput(file);
		}
		
		try {
			WorkbenchHelper.getPage().openEditor(input, "msi.gama.application.browser");
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	public static void showWeb2Editor(final URL url) {

		// get the workspace
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// create the path to the file
		final IPath location = new Path(url.getPath());

		// try to get the IFile (returns null if it could not be found in the
		// workspace)
		final IFile file = workspace.getRoot().getFileForLocation(location);
		IEditorInput input;
		if (file == null) {
			// not found in the workspace, get the IFileStore (external files)
			final IFileStore fileStore = EFS.getLocalFileSystem().getStore(location);
			input = new FileStoreEditorInput(fileStore);

		} else {
			input = new FileEditorInput(file);
		}

		try {
			WorkbenchHelper.getPage().openEditor(input, "msi.gama.application.browser");
		} catch (final PartInitException e) {
			e.printStackTrace();
		}

	}

}
