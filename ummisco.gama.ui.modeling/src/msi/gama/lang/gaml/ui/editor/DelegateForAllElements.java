/*********************************************************************************************
 *
 * 'DelegateForAllElements.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import ummisco.gama.ui.utils.WorkbenchHelper;

public class DelegateForAllElements implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(final IAction action) {
		try {
			WorkbenchHelper.runCommand("org.eclipse.xtext.ui.shared.OpenXtextElementCommand");
		} catch (final ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {}

	@Override
	public void dispose() {}

	@Override
	public void init(final IWorkbenchWindow window) {}

}
