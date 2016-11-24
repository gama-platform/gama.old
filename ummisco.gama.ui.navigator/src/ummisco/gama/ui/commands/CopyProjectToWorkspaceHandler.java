/*********************************************************************************************
 *
 * 'CopyProjectToWorkspaceHandler.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

public class CopyProjectToWorkspaceHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand("org.eclipse.ui.edit.copy", null);
			handlerService.executeCommand("org.eclipse.ui.edit.paste", null);
			// handlerService.executeCommand("org.eclipse.team.svn.ui.command.DisconnectCommand",
			// null);
			// deleteMetaSvn(path);
		} catch (final NotDefinedException e) {
			e.printStackTrace();
		} catch (final NotEnabledException e) {
			e.printStackTrace();
		} catch (final NotHandledException e) {
			e.printStackTrace();
		}
		return null;
	}

}
