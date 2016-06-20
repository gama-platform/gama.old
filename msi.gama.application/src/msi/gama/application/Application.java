/*********************************************************************************************
 *
 *
 * 'Application.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.application;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import msi.gama.application.workbench.ApplicationWorkbenchAdvisor;
import msi.gama.application.workspace.PickWorkspaceDialog;
import msi.gama.application.workspace.WorkspaceModelsManager;
import msi.gama.application.workspace.WorkspacePreferences;

/** This class controls all aspects of the application's execution */
public class Application implements IApplication {

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		Display.setAppName("Gama Platform");
		Display.setAppVersion("1.7.0");
		WorkspaceModelsManager.createProcessor();
		if ( checkWorkspace() == EXIT_OK )
			return EXIT_OK;
		Display display = null;
		try {
			display = Display.getDefault();
			final int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if ( returnCode == PlatformUI.RETURN_RESTART ) { return IApplication.EXIT_RESTART; }
			return IApplication.EXIT_OK;
		} finally {
			if ( display != null )
				display.dispose();
			final Location instanceLoc = Platform.getInstanceLocation();
			if ( instanceLoc != null ) {
				instanceLoc.release();
			}
		}

	}

	public static Object checkWorkspace() throws IOException, MalformedURLException {
		final Location instanceLoc = Platform.getInstanceLocation();
		if ( instanceLoc == null ) {
			// -data @none was specified but GAMA requires a workspace
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
				"A workspace is required to run GAMA");
			return EXIT_OK;
		}
		boolean remember = false;
		String lastUsedWs = null;
		if ( instanceLoc.isSet() ) {
			lastUsedWs = instanceLoc.getURL().getFile();
			final String ret = WorkspacePreferences.checkWorkspaceDirectory(lastUsedWs, false, false, false);
			if ( ret != null ) {
				/* If we dont or cant remember and the location is set, we cant do anything as we need a workspace */
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"The workspace provided cannot be used. Please change it");
				PlatformUI.getWorkbench().close();
				System.exit(0);
				return EXIT_OK;
			}
		} else {

			/* Get what the user last said about remembering the workspace location */
			remember = PickWorkspaceDialog.isRememberWorkspace();
			/* Get the last used workspace location */
			lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory();
			/* If we have a "remember" but no last used workspace, it's not much to remember */
			if ( remember && (lastUsedWs == null || lastUsedWs.length() == 0) ) {
				remember = false;
			}
			if ( remember ) {
				/*
				 * If there's any problem with the workspace, force a dialog
				 */
				final String ret = WorkspacePreferences.checkWorkspaceDirectory(lastUsedWs, false, false, false);
				if ( ret != null ) {
					if ( ret.equals("models") ) {
						remember = !MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
							"Outdated version of the models library",
							"The workspace contains an old version of the models library. Do you want to create a new workspace ?");

					} else {
						remember = false;
					}
				}
			}
		}

		/* If we don't remember the workspace, show the dialog */
		if ( !remember ) {
			final int pick = new PickWorkspaceDialog().open();
			/* If the user cancelled, we can't do anything as we need a workspace */
			if ( pick == 1 /* Window.CANCEL */ && WorkspacePreferences.getSelectedWorkspaceRootLocation() == null ) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"The application can not start without a workspace and will now exit.");
				System.exit(0);
				return IApplication.EXIT_OK;
			}
			/* Tell Eclipse what the selected location was and continue */
			instanceLoc.set(new URL("file", null, WorkspacePreferences.getSelectedWorkspaceRootLocation()), false);
			if ( WorkspacePreferences.applyPrefs() ) {
				WorkspacePreferences.applyEclipsePreferences(WorkspacePreferences.getSelectedWorkspaceRootLocation());
			}
		} else {
			if ( !instanceLoc.isSet() ) {
				/* Set the last used location and continue */
				instanceLoc.set(new URL("file", null, lastUsedWs), false);
			}

		}

		return null;
	}

	@Override
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if ( workbench == null ) { return; }
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				if ( !display.isDisposed() ) {
					workbench.close();
				}
			}
		});
	}

}
