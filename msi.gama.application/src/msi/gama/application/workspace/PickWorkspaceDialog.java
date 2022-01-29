/*******************************************************************************************************
 *
 * PickWorkspaceDialog.java, in msi.gama.application, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.application.workspace;

// import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * Dialog that lets/forces a user to enter/select a workspace that will be used when saving all configuration files and
 * settings. This dialog is shown at startup of the GUI just after the splash screen has shown. Inspired by
 * http://hexapixel.com/2009/01/12/rcp-workspaces
 */
public class PickWorkspaceDialog extends TitleAreaDialog {

	/*
	 * This are our preferences we will be using as the IPreferenceStore is not available yet
	 */
	// FIX: Removed the static reference in case it was causing trouble. Issue
	// 240.

	// static Preferences preferences = Preferences.userRoot().node("gama");
	/** The Constant strMsg. */
	/* Various dialog messages */
	private static final String strMsg =
			"Your workspace is where settings and files of your Gama models will be stored.";

	/** The Constant strInfo. */
	private static final String strInfo = "Please select a directory that will be the workspace root";

	/** The Constant strError. */
	private static final String strError = "You must set a directory";

	/** The workspace path combo. */
	/* Our controls */
	protected Combo workspacePathCombo;

	/** The last used workspaces. */
	protected List<String> lastUsedWorkspaces;

	/** The remember workspace button. */
	protected Button rememberWorkspaceButton;

	/** The Constant splitChar. */
	/* Used as separator when we save the last used workspace locations */
	private static final String splitChar = "#";

	/** The Constant maxHistory. */
	/* Max number of entries in the history box */
	private static final int maxHistory = 20;

	/** The cloning. */
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
			final String wsRoot = WorkspacePreferences.getLastSetWorkspaceDirectory();
			workspacePathCombo.setText(wsRoot);

			/* Checkbox below */
			rememberWorkspaceButton = new Button(inner, SWT.CHECK);
			rememberWorkspaceButton.setText("Remember");
			rememberWorkspaceButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			rememberWorkspaceButton.setSelection(WorkspacePreferences.isRememberWorkspace());

			final String lastUsed = WorkspacePreferences.getLastUsedWorkspaces();
			lastUsedWorkspaces = new ArrayList<>();
			if (lastUsed != null) {
				final String[] all = lastUsed.split(splitChar);
				Collections.addAll(lastUsedWorkspaces, all);
			}
			for (final String last : lastUsedWorkspaces) { workspacePathCombo.add(last); }

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
				if (pick == null) {
					if (workspacePathCombo.getText().length() == 0) { setMessage(strError, IMessageProvider.ERROR); }
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
	public String getSelectedWorkspaceLocation() { return WorkspacePreferences.getSelectedWorkspaceRootLocation(); }

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {

		/* Clone workspace needs a lot of checks */
		final Button clone = createButton(parent, IDialogConstants.IGNORE_ID, "Clone existing workspace", false);
		clone.addListener(SWT.Selection, arg0 -> cloneCurrentWorkspace());
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Copy one absolute path over another
	 *
	 * @param source
	 *            the source
	 * @param t
	 *            the t
	 * @param override
	 *            the override
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	static void copy(final File src, final File dest) throws IOException {
		/* Check to ensure that the source is valid... */
		Path source = src.toPath();
		Path target = dest.toPath();
		if (Files.notExists(source)) throw new IOException("Can not find: " + source);
		if (!Files.isReadable(source)) throw new IOException("Cannot read: " + source);
		if (source.startsWith(target) || target.startsWith(source)) throw new IOException(
				"Source (" + source + ") and destination (" + target + ") must be separate directories");
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
					throws IOException {
				if (dir.toString().contains("org.eclipse") && !"org.eclipse.core.resources".equals(dir.toString()))
					return FileVisitResult.SKIP_SUBTREE;
				Files.createDirectories(target.resolve(source.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Files.copy(file, target.resolve(source.relativize(file)), LinkOption.NOFOLLOW_LINKS,
						StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}

		});
	}

	/**
	 * Clone current workspace.
	 */
	protected void cloneCurrentWorkspace() {
		final String currentLocation = WorkspacePreferences.getLastSetWorkspaceDirectory();
		if (currentLocation == null || currentLocation.isEmpty()) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"No current workspace exists. Can only clone from an existing workspace");
			return;
		}
		final String newLocation = workspacePathCombo.getText();
		// Fixes Issue #2848
		if (newLocation.startsWith(currentLocation)) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"The path entered is either that of the current wokspace or of a subdirectory of it. Neither can be used as a destination.");
			return;
		}
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
		if (str.length() == 0) {
			setMessage(strError, IMessageProvider.ERROR);
			return;
		}

		final String ret = WorkspacePreferences.checkWorkspaceDirectory(str, true, true, cloning);
		if (ret != null) {
			setMessage(ret, IMessageProvider.ERROR);
			return;
		}
		// scope.getGui().debug("Directory to create (after check " + str);
		/* Save it so we can show it in combo later */
		lastUsedWorkspaces.remove(str);

		if (!lastUsedWorkspaces.contains(str)) { lastUsedWorkspaces.add(0, str); }

		/* Deal with the max history */
		if (lastUsedWorkspaces.size() > maxHistory) {
			final List<String> remove = new ArrayList<>();
			for (int i = maxHistory; i < lastUsedWorkspaces.size(); i++) { remove.add(lastUsedWorkspaces.get(i)); }

			lastUsedWorkspaces.removeAll(remove);
		}

		/* Create a string concatenation of all our last used workspaces */
		final StringBuilder buf = new StringBuilder();
		for (int i = 0; i < lastUsedWorkspaces.size(); i++) {
			buf.append(lastUsedWorkspaces.get(i));
			if (i != lastUsedWorkspaces.size() - 1) { buf.append(splitChar); }
		}

		/* Save them onto our preferences */
		WorkspacePreferences.isRememberWorkspace(rememberWorkspaceButton.getSelection());
		WorkspacePreferences.setLastUsedWorkspaces(buf.toString());

		/* Now create it */
		final boolean ok = checkAndCreateWorkspaceRoot(str);
		if (!ok) {
			// scope.getGui().debug("Problem creating " + str);
			setMessage("No workspace could be created at location " + str + ", please check the error log");
			return;
		}

		/* Here we set the location so that we can later fetch it again */
		WorkspacePreferences.setSelectedWorkspaceRootLocation(str);

		/* And on our preferences as well */
		// scope.getGui().debug("Writing " + str + " in the preferences");
		if (cloning) {
			final String previousLocation = WorkspacePreferences.getLastSetWorkspaceDirectory();
			File workspaceDirectory = new File(previousLocation);
			if (!workspaceDirectory.exists() || previousLocation.equals(str)) {
				final DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
				dialog.setText("Choose an existing workspace");
				final String result = dialog.open();
				if (result != null) {
					workspaceDirectory = new File(result);
				} else {
					workspaceDirectory = null;
				}
			}
			if (workspaceDirectory != null) {
				final File targetDirectory = new File(str);
				try {
					copy(workspaceDirectory, targetDirectory);
					// WorkspacePreferences.setApplyPrefs(true);
				} catch (final Exception err) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
							"There was an error cloning the workspace: " + err.getMessage());
					return;
				}

			}
		}
		WorkspacePreferences.setLastSetWorkspaceDirectory(str);

		super.okPressed();
	}

	/**
	 * Checks to see if a workspace exists at a given directory string, and if not, creates it. Also puts our
	 * identifying file inside that workspace.
	 *
	 * @param wsRoot
	 *            Workspace root directory as string
	 * @return true if all checks and creations succeeded, false if there was a problem
	 */
	public static boolean checkAndCreateWorkspaceRoot(final String wsRoot) {
		try {
			final File fRoot = new File(wsRoot);
			if (!fRoot.exists()) // scope.getGui().debug("Folder " + wsRoot + " does not exist");
				return false;

			File dotFile = new File(wsRoot + File.separator + WorkspacePreferences.WORKSPACE_IDENTIFIER);
			if (!dotFile.exists()) {
				final boolean created = dotFile.createNewFile();
				if (!created) return false;
				dotFile = new File(wsRoot + File.separator + WorkspacePreferences.getModelIdentifier());
				dotFile.createNewFile();
			}

			return true;
		} catch (final Exception err) {
			err.printStackTrace();
			return false;
		}
	}

}
