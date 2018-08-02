/*********************************************************************************************
 *
 * 'WorkspacePreferences.java, in plugin msi.gama.application, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.application.workspace;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.PreferenceFilterEntry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class WorkspacePreferences {

	static String lastWs;
	static String selectedWorkspaceRootLocation;
	static boolean applyPrefs;
	public static final String WS_IDENTIFIER = ".gama_application_workspace";
	private static String MODEL_IDENTIFIER = null;

	public static String getSelectedWorkspaceRootLocation() {
		return selectedWorkspaceRootLocation;
	}

	public static void setSelectedWorkspaceRootLocation(final String s) {
		selectedWorkspaceRootLocation = s;
	}

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
			System.out.println(
				">GAMA version " + Platform.getProduct().getDefiningBundle().getVersion().toString() + " loading...");
			System.out.println(">GAMA models library version: " + gamaStamp);
		} catch (final IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return gamaStamp;
	}

	public static IPreferenceFilter[] getPreferenceFilters() {
		final IPreferenceFilter[] transfers = new IPreferenceFilter[1];

		// For export all create a preference filter that can export
		// all nodes of the Instance and Configuration scopes
		transfers[0] = new IPreferenceFilter() {

			@Override
			public String[] getScopes() {
				return new String[] { InstanceScope.SCOPE };
			}

			@Override
			public Map<String, PreferenceFilterEntry[]> getMapping(final String scope) {
				return null;
			}
		};

		return transfers;
	}

	public static boolean applyPrefs() {
		return applyPrefs;
	}

	public static void setApplyPrefs(final boolean b) {
		applyPrefs = b;
	}

	public static void applyEclipsePreferences(final String targetDirectory) {
		final IPreferencesService service = Platform.getPreferencesService();
		IExportedPreferences prefs;

		try (FileInputStream input = new FileInputStream(new File(targetDirectory + "/.gama.epf"))) {
			prefs = service.readPreferences(input);
			service.applyPreferences(prefs, WorkspacePreferences.getPreferenceFilters());
		} catch (final IOException e) {} catch (final CoreException e) {}
		WorkspacePreferences.setApplyPrefs(false);

	}

	/**
	 * Ensures a workspace directory is OK in regards of reading/writing, etc.
	 * This method will get called externally as well.
	 * 
	 * @param parentShell
	 *            Shell parent shell
	 * @param workspaceLocation
	 *            Directory the user wants to use
	 * @param askCreate
	 *            Whether to ask if to create the workspace or not in this
	 *            location if it does not exist already
	 * @param fromDialog
	 *            Whether this method was called from our dialog or from
	 *            somewhere else just to check a location
	 * @return null if everything is ok, or an error message if not
	 */
	public static String checkWorkspaceDirectory(final String workspaceLocation, final boolean askCreate,
		final boolean fromDialog, final boolean cloning) {
		final File f = new File(workspaceLocation);
		if ( !f.exists() ) {
			if ( askCreate ) {
				final boolean create =
					MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "New Directory",
						workspaceLocation + " does not exist. Would you like to create a new workspace here" +
							(cloning ? ", copy the projects and preferences of your current workspace into it, " : "") +
							" and proceeed ?");
				if ( create ) {
					try {
						f.mkdirs();
						final File wsDot = new File(workspaceLocation + File.separator + WS_IDENTIFIER);
						wsDot.createNewFile();
						final File dotFile = new File(workspaceLocation + File.separator + getModelIdentifier());
						dotFile.createNewFile();
					} catch (final RuntimeException err) {
						err.printStackTrace();
						return "Error creating directories, please check folder permissions";
					} catch (final IOException er) {
						er.printStackTrace();
						return "Error creating directories, please check folder permissions";
					}
				}

				if ( !f.exists() ) {
					return "The selected directory does not exist";
				} else {
					return null;
				}
			}
		}

		if ( !f.canRead() ) {
			// scope.getGui().debug("The selected directory is not readable");
			return "The selected directory is not readable";
		}

		if ( !f.isDirectory() ) {
			// scope.getGui().debug("The selected path is not a directory");
			return "The selected path is not a directory";
		}

		testWorkspaceSanity(f);

		final File wsTest = new File(workspaceLocation + File.separator + WS_IDENTIFIER);
		if ( fromDialog ) {
			if ( !wsTest.exists() ) {
				final boolean create = MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
					"New Workspace", "The directory '" + wsTest.getAbsolutePath() +
						"' exists but is not identified as a GAMA workspace. \n\nWould you like to use it anyway ?");
				if ( create ) {
					try {
						f.mkdirs();
						final File wsDot = new File(workspaceLocation + File.separator + WS_IDENTIFIER);
						wsDot.createNewFile();
					} catch (final Exception err) {
						return "Error creating directories, please check folder permissions";
					}
				} else {
					return "Please select a directory for your workspace";
				}

				if ( !wsTest.exists() ) { return "The selected directory does not exist"; }

				return null;
			}
		} else {
			if ( !wsTest.exists() ) { return "The selected directory is not a workspace directory"; }
		}
		final File dotFile = new File(workspaceLocation + File.separator + getModelIdentifier());
		if ( !dotFile.exists() ) {
			if ( fromDialog ) {
				final boolean create = MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
					"Different version of the models library",
					"The workspace contains a different version of the models library. Do you want to proceed anyway ?");
				if ( create ) {
					try {
						dotFile.createNewFile();
					} catch (final IOException e) {
						return "Error updating the models library";
					}
					return null;
				}
			}

			return "models";
		} else if ( cloning ) {
			final boolean b = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Existing workspace",
				"The path entered is a path to an existing workspace. All its contents will be erased and replaced by the current workspace contents. Proceed anyway ?");
			if ( !b ) { return ""; }
		}
		return null;
	}

	public static void testWorkspaceSanity(final File workspace) {
		System.out.println("[GAMA] Checking for workspace sanity");
		File[] files = workspace.listFiles((FileFilter) file -> file.getName().equals(".metadata"));
		if ( files.length == 0 ) { return; }
		final File[] logs = files[0].listFiles((FileFilter) file -> file.getName().contains(".log"));
		for ( final File log : logs ) {
			log.delete();
		}
		files = files[0].listFiles((FileFilter) file -> file.getName().equals(".plugins"));
		if ( files.length == 0 ) { return; }
		files = files[0].listFiles((FileFilter) file -> file.getName().equals("org.eclipse.core.resources"));
		if ( files.length == 0 ) { return; }
		files = files[0].listFiles((FileFilter) file -> file.getName().contains("snap"));
		if ( files.length == 0 ) { return; }
		if ( MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Corrupted workspace",
			"The workspace appears to be corrupted (due to a previous crash) or it is currently used by another instance of the platform. Would you like GAMA to clean it ? Once it is done, you should exit the platform and restart it again to complete the cleaning process.") ) {
			for ( final File file : files ) {
				if ( file.exists() ) {
					file.delete();
				}
			}
			return;
		}
		System.out.println("[GAMA] Workspace appears to be " + (files.length == 0 ? "clean" : "corrupted"));
	}

	public static String getModelIdentifier() {
		if ( MODEL_IDENTIFIER == null ) {
			MODEL_IDENTIFIER = getCurrentGamaStampString();
		}
		return MODEL_IDENTIFIER;
	}

}
