package msi.gama.gui.swt.commands;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.WorkspaceModelsManager;
import org.eclipse.core.commands.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;

/**
 * Opens a file
 */
public class FileOpen extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		GuiUtils.error("Open file");
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