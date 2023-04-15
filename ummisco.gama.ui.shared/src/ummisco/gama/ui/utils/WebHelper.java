/*******************************************************************************************************
 *
 * WebHelper.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static org.eclipse.core.runtime.FileLocator.toFileURL;
import static org.eclipse.core.runtime.Platform.getBundle;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.part.NullEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import msi.gama.application.workbench.IWebHelper;
import msi.gama.common.interfaces.IGamaView.Html;
import msi.gama.common.preferences.GamaPreferences;

/**
 * The Class WebHelper.
 */
public class WebHelper implements IWebHelper {

	/** The instance. */
	private static WebHelper instance = new WebHelper();

	/**
	 * Gets the single instance of WebHelper.
	 *
	 * @return single instance of WebHelper
	 */
	public static WebHelper getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new web helper.
	 */
	private WebHelper() {}

	/** The home url. */
	private static URL HOME_URL;

	/**
	 * Gets the welcome page URL.
	 *
	 * @return the welcome page URL
	 */
	public static URL getWelcomePageURL() {
		if (HOME_URL == null)
			try {
				final var welcomePage = "/welcome/" + (isDark() ? "dark" : "light") + "/welcome.html";
				HOME_URL = toFileURL(getBundle("ummisco.gama.ui.shared").getEntry(welcomePage));
			} catch (final IOException e) {
				e.printStackTrace();
			}
		return HOME_URL;
	}

	/**
	 * Open welcome page.
	 *
	 * @param ifEmpty the if empty
	 */
	public static void openWelcomePage(final boolean ifEmpty) {
		if (ifEmpty && WorkbenchHelper.getPage().getActiveEditor() != null) { return; }
		if (ifEmpty && !GamaPreferences.Interface.CORE_SHOW_PAGE.getValue()) { return; }
		// get the workspace
		final var workspace = ResourcesPlugin.getWorkspace();

		// create the path to the file
		final IPath location = new Path(getWelcomePageURL().getPath());

		// try to get the IFile (returns null if it could not be found in the
		// workspace)
		final var file = workspace.getRoot().getFileForLocation(location);
		IEditorInput input;
		if (file == null) {
			// not found in the workspace, get the IFileStore (external files)
			final var fileStore = EFS.getLocalFileSystem().getStore(location);
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

	/**
	 * Show web 2 editor.
	 *
	 * @param url the url
	 */
	public static void showWeb2Editor(final URL url) {

		// get the workspace
		final var workspace = ResourcesPlugin.getWorkspace();

		// create the path to the file
		final IPath location = new Path(url.getPath());

		// try to get the IFile (returns null if it could not be found in the
		// workspace)
		final var file = workspace.getRoot().getFileForLocation(location);
		IEditorInput input;
		if (file == null) {
			// not found in the workspace, get the IFileStore (external files)
			final var fileStore = EFS.getLocalFileSystem().getStore(location);
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

	/**
	 * Open page.
	 *
	 * @param string the string
	 */
	public static void openPage(final String string) {
		try {
			final var view =
					(Html) WorkbenchHelper.getPage().openEditor(new NullEditorInput(), "msi.gama.application.browser");
			view.setUrl(string);
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showWelcome() {
		openWelcomePage(false);

	}

	@Override
	public void showPage(final String url) {
		openPage(url);
	}

	@Override
	public void showURL(final URL url) {
		showWeb2Editor(url);

	}

}
