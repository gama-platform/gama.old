/*******************************************************************************************************
 *
 * WorkspacePreferences.java, in msi.gama.application, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application.workspace;

import static msi.gama.common.preferences.GamaPreferenceStore.getStore;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import msi.gama.application.Application;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class WorkspacePreferences.
 */
public class WorkspacePreferences {

	/** The Constant KEY_WORSPACE_PATH. */
	private static final String KEY_WORSPACE_PATH = "pref_workspace_path";

	/** The Constant KEY_WORKSPACE_REMEMBER. */
	private static final String KEY_WORKSPACE_REMEMBER = "pref_workspace_remember";

	/** The Constant KEY_WORKSPACE_LIST. */
	private static final String KEY_WORKSPACE_LIST = "pref_workspace_list";

	/** The Constant KEY_ASK_REBUILD. */
	private static final String KEY_ASK_REBUILD = "pref_ask_rebuild";

	/** The Constant KEY_ASK_OUTDATED. */
	private static final String KEY_ASK_OUTDATED = "pref_ask_outdated";

	/** The Constant WORKSPACE_IDENTIFIER. */
	public static final String WORKSPACE_IDENTIFIER = ".gama_application_workspace";

	/** The model identifier. */
	private static String MODEL_IDENTIFIER = null;

	/** The selected workspace root location. */
	static String selectedWorkspaceRootLocation;

	/**
	 * Returns whether the user selected "remember workspace" in the preferences
	 */
	public static boolean isRememberWorkspace() { return getStore().getBoolean(KEY_WORKSPACE_REMEMBER, false); }

	/**
	 * Checks if is remember workspace.
	 *
	 * @param remember
	 *            the remember
	 */
	public static void isRememberWorkspace(final boolean remember) {
		getStore().putBoolean(KEY_WORKSPACE_REMEMBER, remember);
	}

	/**
	 * Gets the last used workspaces.
	 *
	 * @return the last used workspaces
	 */
	public static String getLastUsedWorkspaces() { return getStore().get(KEY_WORKSPACE_LIST, ""); }

	/**
	 * Sets the last used workspaces.
	 *
	 * @param used
	 *            the new last used workspaces
	 */
	public static void setLastUsedWorkspaces(final String used) {
		getStore().put(KEY_WORKSPACE_LIST, used);
	}

	/**
	 * Returns the last set workspace directory from the preferences
	 *
	 * @return null if none
	 */
	public static String getLastSetWorkspaceDirectory() {
		return getStore().get(KEY_WORSPACE_PATH, System.getProperty("user.home") + File.separator + "Gama_Workspace");
	}

	/**
	 * Sets the last set workspace directory.
	 *
	 * @param last
	 *            the new last set workspace directory
	 */
	public static void setLastSetWorkspaceDirectory(final String last) {
		getStore().put(KEY_WORSPACE_PATH, last);
	}

	/**
	 * Ask before rebuilding workspace.
	 *
	 * @return true, if successful
	 */
	public static boolean askBeforeRebuildingWorkspace() {
		// true by default
		return getStore().getBoolean(KEY_ASK_REBUILD, true);
	}

	/**
	 * Ask before rebuilding workspace.
	 *
	 * @param ask
	 *            the ask
	 */
	public static void askBeforeRebuildingWorkspace(final boolean ask) {
		// true by default
		getStore().putBoolean(KEY_ASK_REBUILD, ask);
	}

	/**
	 * Ask before using outdated workspace.
	 *
	 * @return true, if successful
	 */
	public static boolean askBeforeUsingOutdatedWorkspace() {
		// true by default
		return getStore().getBoolean(KEY_ASK_OUTDATED, true);
	}

	/**
	 * Ask before using outdated workspace.
	 *
	 * @param ask
	 *            the ask
	 */
	public static void askBeforeUsingOutdatedWorkspace(final boolean ask) {
		// true by default
		getStore().putBoolean(KEY_ASK_OUTDATED, ask);
	}

	/**
	 * Gets the selected workspace root location.
	 *
	 * @return the selected workspace root location
	 */
	public static String getSelectedWorkspaceRootLocation() { return selectedWorkspaceRootLocation; }

	/**
	 * Sets the selected workspace root location.
	 *
	 * @param s
	 *            the new selected workspace root location
	 */
	public static void setSelectedWorkspaceRootLocation(final String s) { selectedWorkspaceRootLocation = s; }

	/**
	 * Gets the current gama stamp string.
	 *
	 * @return the current gama stamp string
	 */
	public static String getCurrentGamaStampString() {
		String gamaStamp = null;
		try {
			final URL tmpURL = new URL("platform:/plugin/msi.gama.models/models/");
			final URL resolvedFileURL = FileLocator.toFileURL(tmpURL);
			// We need to use the 3-arg constructor of URI in order to properly escape file system chars
			final URI resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null).normalize();
			final File modelsRep = new File(resolvedURI);

			// loading file from URL Path is not a good idea. There are some bugs
			// File modelsRep = new File(urlRep.getPath());

			final long time = modelsRep.lastModified();
			gamaStamp = ".built_in_models_" + time;
			DEBUG.OUT(">GAMA version " + Platform.getProduct().getDefiningBundle().getVersion().toString()
					+ " loading...");
			DEBUG.OUT(">GAMA models library version: " + gamaStamp);
		} catch (final IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return gamaStamp;
	}

	/**
	 * Ensures a workspace directory is OK in regards of reading/writing, etc. This method will get called externally as
	 * well.
	 *
	 * @param parentShell
	 *            Shell parent shell
	 * @param workspaceLocation
	 *            Directory the user wants to use
	 * @param askCreate
	 *            Whether to ask if to create the workspace or not in this location if it does not exist already
	 * @param fromDialog
	 *            Whether this method was called from our dialog or from somewhere else just to check a location
	 * @return null if everything is ok, or an error message if not
	 */
	public static String checkWorkspaceDirectory(final String workspaceLocation, final boolean askCreate,
			final boolean fromDialog, final boolean cloning) {
		final File f = new File(workspaceLocation);
		if (!f.exists() && askCreate) {
			final boolean create = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "New Directory",
					workspaceLocation + " does not exist. Would you like to create a new workspace here"
							+ (cloning ? ", copy the projects of your current workspace into it," : "")
							+ " and proceeed ?");
			if (create) {
				try {
					f.mkdirs();
					final File wsDot = new File(workspaceLocation + File.separator + WORKSPACE_IDENTIFIER);
					wsDot.createNewFile();
					final File dotFile = new File(workspaceLocation + File.separator + getModelIdentifier());
					dotFile.createNewFile();
				} catch (final RuntimeException | IOException er) {
					er.printStackTrace();
					return "Error creating directories, please check folder permissions";
				}
			}

			if (!f.exists()) return "The selected directory does not exist";
			return null;
		}

		if (!f.canRead()) return "The selected directory is not readable";

		if (!f.isDirectory()) return "The selected path is not a directory";

		testWorkspaceSanity(f);

		final File wsTest = new File(workspaceLocation + File.separator + WORKSPACE_IDENTIFIER);
		if (fromDialog) {
			if (!wsTest.exists()) {
				final boolean create = MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
						"New Workspace", "The directory '" + wsTest.getAbsolutePath()
								+ "' exists but is not identified as a GAMA workspace. \n\nWould you like to use it anyway ?");
				if (!create) return "Please select a directory for your workspace";
				try {
					f.mkdirs();
					final File wsDot = new File(workspaceLocation + File.separator + WORKSPACE_IDENTIFIER);
					wsDot.createNewFile();
				} catch (final Exception err) {
					return "Error creating directories, please check folder permissions";
				}

				if (!wsTest.exists()) return "The selected directory does not exist";
				return null;
			}
		} else if (!wsTest.exists()) return "The selected directory is not a workspace directory";
		final File dotFile = new File(workspaceLocation + File.separator + getModelIdentifier());
		if (!dotFile.exists()) {
			if (fromDialog) {
				boolean create = true;
				if (askBeforeUsingOutdatedWorkspace()) {
					create = MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
							"Different version of the models library",
							"The workspace contains a different version of the models library. Do you want to proceed anyway ?");
				}
				if (create) {
					try {
						dotFile.createNewFile();
					} catch (final IOException e) {
						return "Error updating the models library";
					}
					return null;
				}
			}

			return "models";
		}
		if (cloning) {
			final boolean b = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Existing workspace",
					"The path entered is a path to an existing workspace. All its contents will be erased and replaced by the current workspace contents. Proceed anyway ?");
			if (!b) return "";
		}
		return null;
	}

	/**
	 * Test workspace sanity.
	 *
	 * @param workspace
	 *            the workspace
	 * @return true, if successful
	 */
	public static boolean testWorkspaceSanity(final File workspace) {
		DEBUG.OUT("[GAMA] Checking for workspace sanity");
		File[] files = workspace.listFiles((FileFilter) file -> ".metadata".equals(file.getName()));
		if (files == null || files.length == 0) return true;
		final File[] logs = files[0].listFiles((FileFilter) file -> file.getName().contains(".log"));
		if (logs != null) { for (final File log : logs) { log.delete(); } }
		files = files[0].listFiles((FileFilter) file -> ".plugins".equals(file.getName()));
		if (files == null) return false;
		if (files.length == 0) return true;
		files = files[0].listFiles((FileFilter) file -> "org.eclipse.core.resources".equals(file.getName()));
		if (files == null) return false;
		if (files.length == 0) return true;
		files = files[0].listFiles((FileFilter) file -> file.getName().contains("snap"));
		if (files == null) return false;
		DEBUG.OUT("[GAMA] Workspace appears to be " + (files.length == 0 ? "clean" : "corrupted"));
		if (files.length == 0) return true;
		boolean rebuild = true;
		if (askBeforeRebuildingWorkspace()) {
			rebuild = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Corrupted workspace",
					"The workspace appears to be corrupted (due to a previous crash) or it is currently used by another instance of the platform. Would you like GAMA to clean it ?");
		}
		if (rebuild) {
			for (final File file : files) { if (file.exists()) { file.delete(); } }
			Application.ClearWorkspace(true);
			return false;
		}
		return true;
	}

	/**
	 * Gets the model identifier.
	 *
	 * @return the model identifier
	 */
	public static String getModelIdentifier() {
		if (MODEL_IDENTIFIER == null) { MODEL_IDENTIFIER = getCurrentGamaStampString(); }
		return MODEL_IDENTIFIER;
	}

}
