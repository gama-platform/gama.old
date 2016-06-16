/*********************************************************************************************
 *
 *
 * 'CopyProjectToWorkspaceHandler.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.*;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

public class CopyProjectToWorkspaceHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand("org.eclipse.ui.edit.copy", null);
			handlerService.executeCommand("org.eclipse.ui.edit.paste", null);
			// handlerService.executeCommand("org.eclipse.team.svn.ui.command.DisconnectCommand", null);
			// deleteMetaSvn(path);
		} catch (NotDefinedException e) {
			e.printStackTrace();
		} catch (NotEnabledException e) {
			e.printStackTrace();
		} catch (NotHandledException e) {
			e.printStackTrace();
		}
		// RefreshHandler.run();
		return null;
	}
	//
	// private void deleteMetaSvn(File path) {
	// File[] files = path.listFiles();
	// for(int i=0; i<files.length; i++) {
	// String name = files[i].getName();
	// if(files[i].isDirectory() && !name.equals(".svn")) {
	// deleteMetaSvn(files[i]);
	// }
	// else {
	// if(name.equals(".svn"))
	// files[i].delete();
	// }
	// }
	// }

}
