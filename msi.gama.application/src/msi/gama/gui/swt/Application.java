/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt;

import java.io.*;
import java.net.*;
import java.util.*;
import msi.gama.gui.swt.dialogs.PickWorkspaceDialog;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.app.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.*;
import org.eclipse.ui.internal.ide.application.IDEApplication;

/** This class controls all aspects of the application's execution */
public class Application extends IDEApplication {

	private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$
	private static final Integer EXIT_RELAUNCH = new Integer(24);
	private static final Integer EXIT_WORKSPACE_LOCKED = new Integer(15);
	private static final String VERSION_FILENAME = "version.ini"; //$NON-NLS-1$

	private static final String WORKSPACE_VERSION_KEY = "org.eclipse.core.runtime"; //$NON-NLS-1$

	private static final String WORKSPACE_VERSION_VALUE = "1"; //$NON-NLS-1$

	public static ApplicationWorkbenchAdvisor gamaAdvisor;

	@Override
	public Object start(final IApplicationContext context) {

		Display display = PlatformUI.createDisplay();
		/* Fetch the Location that we will be modifying */
		Location instanceLoc = Platform.getInstanceLocation();
		/* Get what the user last said about remembering the workspace location */
		boolean remember = PickWorkspaceDialog.isRememberWorkspace();
		/* Get the last used workspace location */
		String lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory();
		/* If we have a "remember" but no last used workspace, it's not much to remember */
		if ( remember && (lastUsedWs == null || lastUsedWs.length() == 0) ) {
			remember = false;
		}
		/* Check to ensure the workspace location is still OK */
		if ( remember ) {
			/*
			 * If there's any problem whatsoever with the workspace, force a dialog which in its
			 * turn will tell them what's bad
			 */
			String ret =
				PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(),
					lastUsedWs, false, false);
			if ( ret != null ) {
				remember = false;
			}
		}
		ImageDescriptor imgDesc = SwtGui.getImageDescriptor("icons/launcher_icons/splash-icon.png");
		Image myImage = imgDesc.createImage();

		try {
			/* If we don't remember the workspace, show the dialog */
			if ( !remember ) {
				PickWorkspaceDialog pwd = new PickWorkspaceDialog(myImage);
				int pick = pwd.open();
				/*
				 * If the user cancelled, we can't do anything as we need a workspace, so in this
				 * case, we tell them and exit
				 */
				if ( pick == Window.CANCEL ) {
					if ( pwd.getSelectedWorkspaceLocation() == null ) {
						MessageDialog.openError(display.getActiveShell(), "Error",
							"The application can not start without a workspace and will now exit.");
						try {
							PlatformUI.getWorkbench().close();
						} catch (Exception err) {

						}
						System.exit(0);
						return IApplication.EXIT_OK;
					}
				} else {
					/* Tell Eclipse what the selected location was and continue */
					instanceLoc.set(new URL("file", null, pwd.getSelectedWorkspaceLocation()),
						false);
				}

			} else {
				/* Set the last used location and continue */
				instanceLoc.set(new URL("file", null, lastUsedWs), false);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			int returnCode =
				PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if ( returnCode == PlatformUI.RETURN_RESTART ) { return IApplication.EXIT_RESTART; }
			try {
				PlatformUI.getWorkbench().getDecoratorManager()
					.setEnabled("org.eclipse.pde.ui.binaryProjectDecorator", false);
				PlatformUI.getWorkbench().getDecoratorManager()
					.setEnabled("org.eclipse.jdt.ui.decorator", false);
				PlatformUI.getWorkbench().getDecoratorManager()
					.setEnabled("org.eclipse.jdt.ui.interface.decorator", false);
				PlatformUI.getWorkbench().getDecoratorManager()
					.setEnabled("org.eclipse.jdt.ui.buildpath.decorator", false);
				PlatformUI.getWorkbench().getDecoratorManager()
					.setEnabled("org.eclipse.jdt.ui.override.decorator", false);
				// PlatformUI.getWorkbench().getDecoratorManager()
				// .setEnabled("org.eclipse.team.svn.ui.decorator.SVNLightweightDecorator", false);
				PlatformUI.getWorkbench().getDecoratorManager()
					.setEnabled("org.eclipse.ui.LinkedResourceDecorator", false);
				PlatformUI.getWorkbench().getDecoratorManager()
					.setEnabled("org.eclipse.ui.VirtualResourceDecorator", false);
				PlatformUI.getWorkbench().getDecoratorManager()
					.setEnabled("org.eclipse.xtext.builder.nature.overlay", false);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	// @Override
	// public void stop() {
	// if ( !PlatformUI.isWorkbenchRunning() ) { return; }
	// final IWorkbench workbench = PlatformUI.getWorkbench();
	// final Display display = workbench.getDisplay();
	// display.syncExec(new Runnable() {
	//
	// @Override
	// public void run() {
	// if ( !display.isDisposed() ) {
	// workbench.close();
	// }
	// }
	// });
	// }

	private Object checkInstanceLocation(Shell shell, Map applicationArguments) {
		// -data @none was specified but an ide requires workspace
		Location instanceLoc = Platform.getInstanceLocation();
		if ( instanceLoc == null ) {
			MessageDialog.openError(shell,
				IDEWorkbenchMessages.IDEApplication_workspaceMandatoryTitle,
				IDEWorkbenchMessages.IDEApplication_workspaceMandatoryMessage);
			return EXIT_OK;
		}

		// -data "/valid/path", workspace already set
		if ( instanceLoc.isSet() ) {
			// make sure the meta data version is compatible (or the user has
			// chosen to overwrite it).
			if ( !checkValidWorkspace(shell, instanceLoc.getURL()) ) { return EXIT_OK; }

			// at this point its valid, so try to lock it and update the
			// metadata version information if successful
			try {
				if ( instanceLoc.lock() ) {
					writeWorkspaceVersion();
					return null;
				}

				// we failed to create the directory.
				// Two possibilities:
				// 1. directory is already in use
				// 2. directory could not be created
				File workspaceDirectory = new File(instanceLoc.getURL().getFile());
				if ( workspaceDirectory.exists() ) {
					if ( isDevLaunchMode(applicationArguments) ) { return EXIT_WORKSPACE_LOCKED; }
					MessageDialog.openError(shell,
						IDEWorkbenchMessages.IDEApplication_workspaceCannotLockTitle,
						IDEWorkbenchMessages.IDEApplication_workspaceCannotLockMessage);
				} else {
					MessageDialog.openError(shell,
						IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetTitle,
						IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetMessage);
				}
			} catch (IOException e) {
				IDEWorkbenchPlugin.log("Could not obtain lock for workspace location", //$NON-NLS-1$
					e);
				MessageDialog.openError(shell, IDEWorkbenchMessages.InternalError, e.getMessage());
			}
			return EXIT_OK;
		}

		// -data @noDefault or -data not specified, prompt and set
		ChooseWorkspaceData launchData = new ChooseWorkspaceData(instanceLoc.getDefault());

		boolean force = false;
		while (true) {
			URL workspaceUrl = promptForWorkspace(shell, launchData, force);
			if ( workspaceUrl == null ) { return EXIT_OK; }

			// if there is an error with the first selection, then force the
			// dialog to open to give the user a chance to correct
			force = true;

			try {
				// the operation will fail if the url is not a valid
				// instance data area, so other checking is unneeded
				if ( instanceLoc.setURL(workspaceUrl, true) ) {
					launchData.writePersistedData();
					writeWorkspaceVersion();
					return null;
				}
			} catch (IllegalStateException e) {
				MessageDialog.openError(shell,
					IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetTitle,
					IDEWorkbenchMessages.IDEApplication_workspaceCannotBeSetMessage);
				return EXIT_OK;
			}

			// by this point it has been determined that the workspace is
			// already in use -- force the user to choose again
			MessageDialog.openError(shell, IDEWorkbenchMessages.IDEApplication_workspaceInUseTitle,
				IDEWorkbenchMessages.IDEApplication_workspaceInUseMessage);
		}
	}

	private static boolean isDevLaunchMode(Map args) {
		// see org.eclipse.pde.internal.core.PluginPathFinder.isDevLaunchMode()
		if ( Boolean.getBoolean("eclipse.pde.launch") ) { return true; }
		return args.containsKey("-pdelaunch"); //$NON-NLS-1$
	}

	/**
	 * Open a workspace selection dialog on the argument shell, populating the
	 * argument data with the user's selection. Perform first level validation
	 * on the selection by comparing the version information. This method does
	 * not examine the runtime state (e.g., is the workspace already locked?).
	 * 
	 * @param shell
	 * @param launchData
	 * @param force
	 *            setting to true makes the dialog open regardless of the
	 *            showDialog value
	 * @return An URL storing the selected workspace or null if the user has
	 *         canceled the launch operation.
	 */
	private URL promptForWorkspace(Shell shell, ChooseWorkspaceData launchData, boolean force) {
		URL url = null;
		do {
			// okay to use the shell now - this is the splash shell
			new ChooseWorkspaceDialog(shell, launchData, false, true).prompt(force);
			String instancePath = launchData.getSelection();
			if ( instancePath == null ) { return null; }

			// the dialog is not forced on the first iteration, but is on every
			// subsequent one -- if there was an error then the user needs to be
			// allowed to fix it
			force = true;

			// 70576: don't accept empty input
			if ( instancePath.length() <= 0 ) {
				MessageDialog.openError(shell,
					IDEWorkbenchMessages.IDEApplication_workspaceEmptyTitle,
					IDEWorkbenchMessages.IDEApplication_workspaceEmptyMessage);
				continue;
			}

			// create the workspace if it does not already exist
			File workspace = new File(instancePath);
			if ( !workspace.exists() ) {
				workspace.mkdir();
			}

			try {
				// Don't use File.toURL() since it adds a leading slash that Platform does not
				// handle properly. See bug 54081 for more details.
				String path = workspace.getAbsolutePath().replace(File.separatorChar, '/');
				url = new URL("file", null, path); //$NON-NLS-1$
			} catch (MalformedURLException e) {
				MessageDialog.openError(shell,
					IDEWorkbenchMessages.IDEApplication_workspaceInvalidTitle,
					IDEWorkbenchMessages.IDEApplication_workspaceInvalidMessage);
				continue;
			}
		} while (!checkValidWorkspace(shell, url));

		return url;
	}

	/**
	 * Return true if the argument directory is ok to use as a workspace and
	 * false otherwise. A version check will be performed, and a confirmation
	 * box may be displayed on the argument shell if an older version is
	 * detected.
	 * 
	 * @return true if the argument URL is ok to use as a workspace and false
	 *         otherwise.
	 */
	private boolean checkValidWorkspace(Shell shell, URL url) {
		// a null url is not a valid workspace
		if ( url == null ) { return false; }

		String version = readWorkspaceVersion(url);

		// if the version could not be read, then there is not any existing
		// workspace data to trample, e.g., perhaps its a new directory that
		// is just starting to be used as a workspace
		if ( version == null ) { return true; }

		final int ide_version = Integer.parseInt(WORKSPACE_VERSION_VALUE);
		int workspace_version = Integer.parseInt(version);

		// equality test is required since any version difference (newer
		// or older) may result in data being trampled
		if ( workspace_version == ide_version ) { return true; }

		// At this point workspace has been detected to be from a version
		// other than the current ide version -- find out if the user wants
		// to use it anyhow.
		String title = IDEWorkbenchMessages.IDEApplication_versionTitle;
		String message =
			NLS.bind(IDEWorkbenchMessages.IDEApplication_versionMessage, url.getFile());

		MessageBox mbox =
			new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_WARNING | SWT.APPLICATION_MODAL);
		mbox.setText(title);
		mbox.setMessage(message);
		return mbox.open() == SWT.OK;
	}

	/**
	 * Look at the argument URL for the workspace's version information. Return
	 * that version if found and null otherwise.
	 */
	private static String readWorkspaceVersion(URL workspace) {
		File versionFile = getVersionFile(workspace, false);
		if ( versionFile == null || !versionFile.exists() ) { return null; }

		try {
			// Although the version file is not spec'ed to be a Java properties
			// file, it happens to follow the same format currently, so using
			// Properties to read it is convenient.
			Properties props = new Properties();
			FileInputStream is = new FileInputStream(versionFile);
			try {
				props.load(is);
			} finally {
				is.close();
			}

			return props.getProperty(WORKSPACE_VERSION_KEY);
		} catch (IOException e) {
			IDEWorkbenchPlugin.log("Could not read version file", new Status( //$NON-NLS-1$
				IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH, IStatus.ERROR,
				e.getMessage() == null ? "" : e.getMessage(), //$NON-NLS-1$, 
				e));
			return null;
		}
	}

	/**
	 * Write the version of the metadata into a known file overwriting any
	 * existing file contents. Writing the version file isn't really crucial,
	 * so the function is silent about failure
	 */
	private static void writeWorkspaceVersion() {
		Location instanceLoc = Platform.getInstanceLocation();
		if ( instanceLoc == null || instanceLoc.isReadOnly() ) { return; }

		File versionFile = getVersionFile(instanceLoc.getURL(), true);
		if ( versionFile == null ) { return; }

		OutputStream output = null;
		try {
			String versionLine = WORKSPACE_VERSION_KEY + '=' + WORKSPACE_VERSION_VALUE;

			output = new FileOutputStream(versionFile);
			output.write(versionLine.getBytes("UTF-8")); //$NON-NLS-1$
		} catch (IOException e) {
			IDEWorkbenchPlugin.log("Could not write version file", //$NON-NLS-1$
				StatusUtil.newStatus(IStatus.ERROR, e.getMessage(), e));
		} finally {
			try {
				if ( output != null ) {
					output.close();
				}
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	/**
	 * The version file is stored in the metadata area of the workspace. This
	 * method returns an URL to the file or null if the directory or file does
	 * not exist (and the create parameter is false).
	 * 
	 * @param create
	 *            If the directory and file does not exist this parameter
	 *            controls whether it will be created.
	 * @return An url to the file or null if the version file does not exist or
	 *         could not be created.
	 */
	private static File getVersionFile(URL workspaceUrl, boolean create) {
		if ( workspaceUrl == null ) { return null; }

		try {
			// make sure the directory exists
			File metaDir = new File(workspaceUrl.getPath(), METADATA_FOLDER);
			if ( !metaDir.exists() && (!create || !metaDir.mkdir()) ) { return null; }

			// make sure the file exists
			File versionFile = new File(metaDir, VERSION_FILENAME);
			if ( !versionFile.exists() && (!create || !versionFile.createNewFile()) ) { return null; }

			return versionFile;
		} catch (IOException e) {
			// cannot log because instance area has not been set
			return null;
		}
	}
}
