/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application;

import java.io.IOException;
import java.net.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.exceptions.GamlException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;

/** This class controls all aspects of the application's execution */
public class Application implements IApplication {

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

		ImageDescriptor imgDesc =
			Activator.getImageDescriptor("icons/launcher_icons/splash-icon.png");
		Image myImage = imgDesc.createImage();
		/* Early optimizations */
		try {
			GamlCompiler.preBuild();
		} catch (GamlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
//			if (instanceLoc.getURL() != null) {
//				System.out.println("DEBUG: "+instanceLoc.getURL());
//			} else
			/* If we don't remember the workspace, show the dialog */
			if ( !remember ) {
				PickWorkspaceDialog pwd = new PickWorkspaceDialog(false, myImage);
				int pick = pwd.open();

				/*
				 * If the user cancelled, we can't do anything as we need a workspace, so in this
				 * case, we tell them and exit
				 */
				if ( pick == Window.CANCEL ) {
					if ( pwd.getSelectedWorkspaceLocation() == null ) {
						MessageDialog
							.openError(display.getActiveShell(), "Error",
								"The application can not start without a workspace root and will now exit.");
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
			gamaAdvisor = new ApplicationWorkbenchAdvisor();
			int returnCode = PlatformUI.createAndRunWorkbench(display, gamaAdvisor);
			if ( returnCode == PlatformUI.RETURN_RESTART ) { return IApplication.EXIT_RESTART; }
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	@Override
	public void stop() {
		if ( !PlatformUI.isWorkbenchRunning() ) { return; }
		// TODO OutputManager.run() ?
		final IWorkbench workbench = PlatformUI.getWorkbench();
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
