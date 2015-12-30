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

import java.net.URL;
import java.util.Arrays;
import msi.gama.application.projects.WorkspaceModelsManager;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.dialogs.PickWorkspaceDialog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.*;
import org.eclipse.equinox.internal.app.CommandLineArgs;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

/** This class controls all aspects of the application's execution */
public class Application implements IApplication {

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		System.out.println(Arrays.toString(CommandLineArgs.getAllArgs()));
		// System.out.println(Platform.getProduct() == null ? "No product" : Platform.getProduct().getId() + " version " +
		// Platform.getProduct().getDefiningBundle().getVersion());
		System.err
			.println("If you are running the developer version of GAMA, be sure to perform a clean build of your projects before launching it. Unexpected compilation errors can occur if the annotations are somehow out of sync with the code.");

		Display display = PlatformUI.createDisplay();
		WorkspaceModelsManager.createProcessor(display);
		// OpenDocumentEventProcessor openDocProcessor = new OpenDocumentEventProcessor(display);
		// display.addListener(SWT.OpenDocument, openDocProcessor);
		// DelayedEventsProcessor delayedProcessor = new DelayedEventsProcessor(display);
		/* Fetch the Location that we will be modifying */
		Location instanceLoc = Platform.getInstanceLocation();
		if ( instanceLoc == null ) {
			// -data @none was specified but GAMA requires a workspace
			MessageDialog.openError(display.getActiveShell(),
				IDEWorkbenchMessages.IDEApplication_workspaceMandatoryTitle,
				IDEWorkbenchMessages.IDEApplication_workspaceMandatoryMessage);
			return EXIT_OK;
		}
		boolean remember = false;
		String lastUsedWs = null;
		if ( instanceLoc.isSet() ) {
			lastUsedWs = instanceLoc.getURL().getFile();
			String ret =
				PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(), lastUsedWs, false,
					false, false);
			if ( ret != null ) {
				GuiUtils.debug(ret);
				// remember = false;
				/* If we dont or cant remember and the location is set, we cant do anything as we need a workspace */
				MessageDialog.openError(display.getActiveShell(), "Error",
					"The workspace provided as argument cannot be used. Please change or remove it");
				PlatformUI.getWorkbench().close();
				System.exit(0);
				return IApplication.EXIT_OK;
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
				String ret =
					PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(), lastUsedWs,
						false, false, false);
				if ( ret != null ) {
					if ( ret.equals("models") ) {
						remember =
							!MessageDialog
								.openConfirm(Display.getDefault().getActiveShell(),
									"Outdated version of the models library",
									"The workspace contains an old version of the models library. Do you want to create a new workspace ?");

					} else {
						GuiUtils.debug(ret);
						remember = false;
					}
				}
			}
		}

		/* If we don't remember the workspace, show the dialog */
		if ( !remember ) {
			PickWorkspaceDialog pwd = new PickWorkspaceDialog();
			int pick = pwd.open();
			/* If the user cancelled, we can't do anything as we need a workspace */
			if ( pick == Window.CANCEL && pwd.getSelectedWorkspaceLocation() == null ) {
				MessageDialog.openError(display.getActiveShell(), "Error",
					"The application can not start without a workspace and will now exit.");
				PlatformUI.getWorkbench().close();
				System.exit(0);
				return IApplication.EXIT_OK;
			}
			/* Tell Eclipse what the selected location was and continue */
			instanceLoc.set(new URL("file", null, pwd.getSelectedWorkspaceLocation()), false);
		} else {
			if ( !instanceLoc.isSet() ) {
				/* Set the last used location and continue */
				instanceLoc.set(new URL("file", null, lastUsedWs), false);
			}
		}

		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if ( returnCode == PlatformUI.RETURN_RESTART ) { return IApplication.EXIT_RESTART; }
			return IApplication.EXIT_OK;
		} finally {
			if ( display != null ) {
				display.dispose();
			}
			instanceLoc = Platform.getInstanceLocation();
			if ( instanceLoc != null ) {
				instanceLoc.release();
			}
		}
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
