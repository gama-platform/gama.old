/*********************************************************************************************
 *
 * 'FileOpen.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import msi.gama.application.workspace.WorkspaceModelsManager;

/**
 * Opens a file
 */
public class FileOpen extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.gaml", "*.*" });
		dialog.setFilterNames(new String[] { "GAML model files", "All Files" });
		final String fileSelected = dialog.open();

		if (fileSelected != null && fileSelected.endsWith(".gaml")) {
			// Perform Action, like open the file.
			WorkspaceModelsManager.instance.openModelPassedAsArgument(fileSelected);
		}
		return null;
	}
}