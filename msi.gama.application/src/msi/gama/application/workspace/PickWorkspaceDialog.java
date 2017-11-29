/*********************************************************************************************
 *
 * 'PickWorkspaceDialog.java, in plugin msi.gama.application, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.application.workspace;

// import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog that lets/forces a user to enter/select a workspace that will be used
 * when saving all configuration files and settings. This dialog is shown at
 * startup of the GUI just after the splash screen has shown. Inspired by
 * http://hexapixel.com/2009/01/12/rcp-workspaces
 */
public class PickWorkspaceDialog extends TitleAreaDialog {

	/*
	 * The name of the file that tells us that the workspace directory belongs
	 * to our application
	 */

	private static final String keyWorkspaceRootDir = "wsRootDir";
	private static final String keyRememberWorkspace = "wsRemember";
	private static final String keyLastUsedWorkspaces = "wsLastUsedWorkspaces";

	/*
	 * This are our preferences we will be using as the IPreferenceStore is not
	 * available yet
	 */
	// FIX: Removed the static reference in case it was causing trouble. Issue
	// 240.

	// static Preferences preferences = Preferences.userRoot().node("gama");
	/* Various dialog messages */
	private static final String strMsg =
		"Your workspace is where settings and files of your Gama models will be stored.";
	private static final String strInfo = "Please select a directory that will be the workspace root";
	private static final String strError = "You must set a directory";

	/* Our controls */
	protected Combo workspacePathCombo;
	protected List<String> lastUsedWorkspaces;
	protected Button rememberWorkspaceButton;

	/* Used as separator when we save the last used workspace locations */
	private static final String splitChar = "#";
	/* Max number of entries in the history box */
	private static final int maxHistory = 20;

	/* Whatever the user picks ends up on this variable */
	private boolean cloning = false;

	/**
	 * Creates a new workspace dialog with a specific image as title-area image.
	 * 
	 * @param switchWorkspace
	 *            true if we're using this dialog as a switch workspace dialog
	 * @param wizardImage
	 *            Image to show
	 */
	public PickWorkspaceDialog() {
		super(Display.getDefault().getActiveShell());
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("GAMA Models Workspace");
	}

	public static Preferences getNode() {
		try {
			if ( Preferences.userRoot().nodeExists("gama") ) { return Preferences.userRoot().node("gama"); }
		} catch (final BackingStoreException e1) {
			e1.printStackTrace();
		}
		final Preferences p = Preferences.userRoot().node("gama");
		try {
			p.flush();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * Returns whether the user selected "remember workspace" in the preferences
	 */
	public static boolean isRememberWorkspace() {
		return getNode().getBoolean(keyRememberWorkspace, false);
	}

	/**
	 * Returns the last set workspace directory from the preferences
	 * 
	 * @return null if none
	 */
	public static String getLastSetWorkspaceDirectory() {
		return getNode().get(keyWorkspaceRootDir, null);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		setTitle("Choose a Workspace to store your models, settings, etc.");
		setMessage(strMsg);

		try {
			final Composite inner = new Composite(parent, SWT.NONE);
			final GridLayout l = new GridLayout(4, false);
			// double[][] layout =
			// new double[][] {
			// { 5, LatticeConstants.PREFERRED, 5, 250, 5,
			// LatticeConstants.PREFERRED, 5 },
			// { 5, LatticeConstants.PREFERRED, 5, LatticeConstants.PREFERRED,
			// 40 } };
			inner.setLayout(l);
			inner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));

			/* Label on the left */
			final CLabel label = new CLabel(inner, SWT.NONE);
			label.setText("GAMA Workspace");
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

			/* Combo in the middle */
			workspacePathCombo = new Combo(inner, SWT.BORDER);
			final GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			data.widthHint = 200;
			workspacePathCombo.setLayoutData(data);
			String wsRoot = getNode().get(keyWorkspaceRootDir, "");
			if ( wsRoot == null || wsRoot.length() == 0 ) {
				wsRoot = getWorkspacePathSuggestion();
			}
			workspacePathCombo.setText(wsRoot);

			/* Checkbox below */
			rememberWorkspaceButton = new Button(inner, SWT.CHECK);
			rememberWorkspaceButton.setText("Remember");
			rememberWorkspaceButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			rememberWorkspaceButton.setSelection(getNode().getBoolean(keyRememberWorkspace, false));

			final String lastUsed = getNode().get(keyLastUsedWorkspaces, "");
			lastUsedWorkspaces = new ArrayList<String>();
			if ( lastUsed != null ) {
				final String[] all = lastUsed.split(splitChar);
				for ( final String str : all ) {
					lastUsedWorkspaces.add(str);
				}
			}
			for ( final String last : lastUsedWorkspaces ) {
				workspacePathCombo.add(last);
			}

			/* Browse button on the right */
			final Button browse = new Button(inner, SWT.PUSH);
			browse.setText("Browse...");
			browse.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
			browse.addListener(SWT.Selection, event -> {
				final DirectoryDialog dd = new DirectoryDialog(getParentShell());
				dd.setText("Select Workspace Root");
				dd.setMessage(strInfo);
				dd.setFilterPath(workspacePathCombo.getText());
				final String pick = dd.open();
				if ( pick == null ) {
					if ( workspacePathCombo.getText().length() == 0 ) {
						setMessage(strError, IMessageProvider.ERROR);
					}
				} else {
					setMessage(strMsg);
					workspacePathCombo.setText(pick);
				}
			});
			return inner;
		} catch (final RuntimeException err) {
			err.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns whatever path the user selected in the dialog.
	 * 
	 * @return Path
	 */
	public String getSelectedWorkspaceLocation() {
		return WorkspacePreferences.getSelectedWorkspaceRootLocation();
	}

	/* Suggests a default path based on the user.home/GAMA directory location */
	private String getWorkspacePathSuggestion() {
		final StringBuffer buf = new StringBuffer();

		String uHome = System.getProperty("user.home");
		if ( uHome == null ) {
			uHome = "c:";
		}

		buf.append(uHome).append(File.separator).append("gama_workspace");

		return buf.toString();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {

		/* Clone workspace needs a lot of checks */
		final Button clone = createButton(parent, IDialogConstants.IGNORE_ID, "Clone existing workspace", false);
		clone.addListener(SWT.Selection, arg0 -> cloneCurrentWorkspace());
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/* Checks whether a target directory is a subdirectory of ourselves */
	private boolean isTargetSubdirOfDir(final File source, final File target) {
		final List<File> subdirs = new ArrayList<File>();
		getAllSubdirectoriesOf(source, subdirs);
		return subdirs.contains(target);
	}

	/* Helper for above */
	private void getAllSubdirectoriesOf(final File target, final List<File> buffer) {
		final File[] files = target.listFiles();
		if ( files == null || files.length == 0 ) { return; }

		for ( final File f : files ) {
			if ( f.isDirectory() ) {
				buffer.add(f);
				getAllSubdirectoriesOf(f, buffer);
			}
		}
	}

	/**
	 * This function will copy files or directories from one location to
	 * another. note that the source and the destination must be mutually
	 * exclusive. This function can not be used to copy a directory to a sub
	 * directory of itself. The function will also have problems if the
	 * destination files already exist.
	 * 
	 * @param src
	 *            -- A File object that represents the source for the copy
	 * @param dest
	 *            -- A File object that represents the destination for the copy.
	 * @throws IOException
	 *             if unable to copy.
	 */
	public static void copyFiles(final File src, final File dest) throws IOException {
		/* Check to ensure that the source is valid... */
		if ( !src.exists() ) {
			throw new IOException("Can not find source: " + src.getAbsolutePath());
		} else if ( !src.canRead() ) { // check to ensure we have rights to the
										// source...
			throw new IOException("Cannot read: " + src.getAbsolutePath() + ". Check file permissions.");
		}
		/* Is this a directory copy? */
		final List<String> noCopy = Arrays.asList("org.eclipse.core.runtime", "org.eclipse.e4.workbench",
			"org.eclipse.emf.common.ui", "org.eclipse.ui.workbench", "org.eclipse.xtext.builder");
		if ( src.isDirectory() ) {
			if ( noCopy.contains(src.getName()) )
				return;
			/* Does the destination already exist? */
			if ( !dest.exists() ) {
				/* If not we need to make it exist if possible */
				if ( !dest
					.mkdirs() ) { throw new IOException("Could not create direcotry: " + dest.getAbsolutePath()); }
			}
			/* Get a listing of files... */
			final String list[] = src.list();
			/* Copy all the files in the list. */
			if ( list != null )
				for ( int i = 0; i < list.length; i++ ) {
					final File dest1 = new File(dest, list[i]);
					final File src1 = new File(src, list[i]);
					copyFiles(src1, dest1);
				}
		} else {
			/* This was not a directory, so lets just copy the file */
			final byte[] buffer = new byte[4096];
			int bytesRead;
			try (FileInputStream fin = new FileInputStream(src); FileOutputStream fout = new FileOutputStream(dest);) {
				/* Open the files for input and output */

				/* While bytesRead indicates a successful read, lets write... */
				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer, 0, bytesRead);
				}
			} catch (final IOException e) {
				final IOException wrapper =
					new IOException("Unable to copy file: " + src.getAbsolutePath() + "to" + dest.getAbsolutePath());
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
				/* Ensure that the files are closed (if they were open). */
			}
		}
	}

	protected void cloneCurrentWorkspace() {
		final String currentLocation = getNode().get(keyWorkspaceRootDir, "");
		if ( currentLocation.isEmpty() ) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
				"No current workspace exists. Can only clone from an existing workspace");
			return;
		}
		cloneWorkspace(currentLocation);
	}

	protected void cloneWorkspace(final String locationToClone) {
		// Some checks first
		final String newLocation = workspacePathCombo.getText();

		final File workspaceDirectory = new File(locationToClone);
		final File targetDirectory = new File(newLocation);
		if ( workspaceDirectory.exists() &&
			targetDirectory.getAbsolutePath().equals(workspaceDirectory.getAbsolutePath()) ) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
				"Please enter a different location for the new workspace. A workspace cannot be cloned into itself.");
			return;
		}
		// recursive check, if new directory is a subdirectory of
		// our workspace, that's a big no-no or we'll
		// create directories forever
		if ( isTargetSubdirOfDir(workspaceDirectory, targetDirectory) ) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
				"The path entered is a subdirectory of the current workspace. A workspace cannot be cloned in one of its sub-directories");
			return;
		}
		// If the checks are ok, we set "cloning" to true and do as if ok was
		// pressed.
		cloning = true;
		try {
			okPressed();
		} finally {
			cloning = false;
		}
	}

	@Override
	protected void okPressed() {
		final String str = workspacePathCombo.getText();
		// scope.getGui().debug("Directory to create " + str);
		if ( str.length() == 0 ) {
			setMessage(strError, IMessageProvider.ERROR);
			return;
		}

		final String ret = WorkspacePreferences.checkWorkspaceDirectory(str, true, true, cloning);
		if ( ret != null ) {
			setMessage(ret, IMessageProvider.ERROR);
			return;
		}
		// scope.getGui().debug("Directory to create (after check " + str);
		/* Save it so we can show it in combo later */
		lastUsedWorkspaces.remove(str);

		if ( !lastUsedWorkspaces.contains(str) ) {
			lastUsedWorkspaces.add(0, str);
		}

		/* Deal with the max history */
		if ( lastUsedWorkspaces.size() > maxHistory ) {
			final List<String> remove = new ArrayList<String>();
			for ( int i = maxHistory; i < lastUsedWorkspaces.size(); i++ ) {
				remove.add(lastUsedWorkspaces.get(i));
			}

			lastUsedWorkspaces.removeAll(remove);
		}

		/* Create a string concatenation of all our last used workspaces */
		final StringBuffer buf = new StringBuffer();
		for ( int i = 0; i < lastUsedWorkspaces.size(); i++ ) {
			buf.append(lastUsedWorkspaces.get(i));
			if ( i != lastUsedWorkspaces.size() - 1 ) {
				buf.append(splitChar);
			}
		}

		/* Save them onto our preferences */
		getNode().putBoolean(keyRememberWorkspace, rememberWorkspaceButton.getSelection());
		getNode().put(keyLastUsedWorkspaces, buf.toString());
		try {
			getNode().flush();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}

		/* Now create it */
		final boolean ok = checkAndCreateWorkspaceRoot(str);
		if ( !ok ) {
			// scope.getGui().debug("Problem creating " + str);
			setMessage("No workspace could be created at location " + str + ", please check the error log");
			return;
		}

		/* Here we set the location so that we can later fetch it again */
		WorkspacePreferences.setSelectedWorkspaceRootLocation(str);

		/* And on our preferences as well */
		// scope.getGui().debug("Writing " + str + " in the preferences");
		if ( cloning ) {
			final String previousLocation = getNode().get(keyWorkspaceRootDir, "");
			File workspaceDirectory = new File(previousLocation);
			if ( !workspaceDirectory.exists() || previousLocation.equals(str) ) {
				final DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
				dialog.setText("Choose an existing workspace");
				final String result = dialog.open();
				if ( result != null ) {
					workspaceDirectory = new File(result);
				} else {
					workspaceDirectory = null;
				}
			}
			if ( workspaceDirectory != null ) {
				final File targetDirectory = new File(str);
				try {
					copyFiles(workspaceDirectory, targetDirectory);
					WorkspacePreferences.setApplyPrefs(true);
				} catch (final Exception err) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
						"There was an error cloning the workspace: " + err.getMessage());
					return;
				}

			}
		}
		getNode().put(keyWorkspaceRootDir, str);
		try {
			getNode().flush();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}

		super.okPressed();
	}

	/**
	 * Checks to see if a workspace exists at a given directory string, and if
	 * not, creates it. Also puts our identifying file inside that workspace.
	 * 
	 * @param wsRoot
	 *            Workspace root directory as string
	 * @return true if all checks and creations succeeded, false if there was a
	 *         problem
	 */
	public static boolean checkAndCreateWorkspaceRoot(final String wsRoot) {
		try {
			final File fRoot = new File(wsRoot);
			if ( !fRoot.exists() ) {
				// scope.getGui().debug("Folder " + wsRoot + " does not exist");
				return false;
			}

			File dotFile = new File(wsRoot + File.separator + WorkspacePreferences.WS_IDENTIFIER);
			if ( !dotFile.exists() ) {
				final boolean created = dotFile.createNewFile();
				if ( !created ) { return false; }
				dotFile = new File(wsRoot + File.separator + WorkspacePreferences.MODEL_IDENTIFIER);
				dotFile.createNewFile();
			}

			return true;
		} catch (final Exception err) {
			err.printStackTrace();
			return false;
		}
	}

}
