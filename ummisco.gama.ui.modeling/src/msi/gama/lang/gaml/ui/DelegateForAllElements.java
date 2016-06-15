/*********************************************************************************************
 *
 *
 * 'DelegateForAllElements.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui;

import org.eclipse.core.commands.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.*;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.services.IServiceLocator;

public class DelegateForAllElements implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(final IAction action) {

		// Obtain IServiceLocator implementer, e.g. from PlatformUI.getWorkbench():
		IServiceLocator serviceLocator = PlatformUI.getWorkbench();
		// or a site from within a editor or view:
		// IServiceLocator serviceLocator = getSite();

		ICommandService commandService = serviceLocator.getService(ICommandService.class);

		try {
			// Lookup commmand with its ID
			Command command = commandService.getCommand("org.eclipse.xtext.ui.shared.OpenXtextElementCommand");

			// Optionally pass a ExecutionEvent instance, default no-param arg creates blank event
			command.executeWithChecks(new ExecutionEvent());

		} catch (Exception e) {

			// Replace with real-world exception handling
			e.printStackTrace();
		}

	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {}

	@Override
	public void dispose() {}

	@Override
	public void init(final IWorkbenchWindow window) {

	}

}
