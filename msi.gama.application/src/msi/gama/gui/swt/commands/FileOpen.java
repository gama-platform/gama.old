/*********************************************************************************************
 *
 *
 * 'FileOpen.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.commands;

import org.eclipse.core.commands.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import msi.gama.application.projects.WorkspaceModelsManager;

/**
 * Opens a file
 */
public class FileOpen extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.gaml", "*.*" });
		dialog.setFilterNames(new String[] { "GAML model files", "All Files" });
		String fileSelected = dialog.open();

		if ( fileSelected != null && fileSelected.endsWith(".gaml") ) {
			// Perform Action, like open the file.
			WorkspaceModelsManager.instance.openModelPassedAsArgument(fileSelected);
		}
		return null;
	}
}