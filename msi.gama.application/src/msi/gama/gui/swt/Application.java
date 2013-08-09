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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt;

import java.net.URL;
import msi.gama.gui.swt.dialogs.PickWorkspaceDialog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;

/** This class controls all aspects of the application's execution */
public class Application implements IApplication {

	@Override
	public Object start(final IApplicationContext context) throws Exception {

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
			 * If there's any problem with the workspace, force a dialog
			 */
			String ret =
				PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(), lastUsedWs, false,
					false);
			if ( ret != null ) {
				remember = false;
			}
		}
		/* If we don't remember the workspace, show the dialog */
		if ( !remember ) {
			PickWorkspaceDialog pwd =
				new PickWorkspaceDialog(SwtGui.getImageDescriptor("icons/launcher_icons/icon205.png").createImage());
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
			/* Set the last used location and continue */
			instanceLoc.set(new URL("file", null, lastUsedWs), false);
		}

		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if ( returnCode == PlatformUI.RETURN_RESTART ) { return IApplication.EXIT_RESTART; }
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
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
