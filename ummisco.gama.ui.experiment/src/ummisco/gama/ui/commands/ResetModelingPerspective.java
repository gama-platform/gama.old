package ummisco.gama.ui.commands;

import static org.eclipse.jface.dialogs.MessageDialog.QUESTION;
import static org.eclipse.jface.dialogs.MessageDialog.open;
import static org.eclipse.swt.SWT.SHEET;
import static ummisco.gama.ui.utils.WorkbenchHelper.getShell;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import msi.gama.application.Application;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class ResetModelingPerspective extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final String message =
				"Resetting the modeling perspective will lose memory of the current editors, navigator state and restart GAMA in a pristine state. Do you want to proceed ?";
		final boolean result = open(QUESTION, getShell(), "Reset modeling perspective", message, SHEET);
		if (result) {
			Application.ClearWorkspace(true);
			// removeWorkbenchXMI();
			WorkbenchHelper.getWorkbench().restart();
		}
		return null;

	}
	//
	// public static void removeWorkbenchXMI() {
	// final File workspace = new File(Platform.getInstanceLocation().getURL().getFile());
	// DEBUG.OUT("[GAMA] Removing the definition of workbench.xmi from the workspace");
	// File[] files = workspace.listFiles((FileFilter) file -> file.getName().equals(".metadata"));
	// if (files.length == 0) { return; }
	// files = files[0].listFiles((FileFilter) file -> file.getName().equals(".plugins"));
	// if (files.length == 0) { return; }
	// files = files[0].listFiles((FileFilter) file -> file.getName().equals("org.eclipse.e4.workbench"));
	// if (files.length == 0) { return; }
	// files = files[0].listFiles((FileFilter) file -> file.getName().equals("workbench.xmi"));
	// if (files.length == 0) { return; }
	// final File toRemove = files[0];
	// if (toRemove.exists()) {
	// final File renamed = new File(toRemove.getAbsolutePath().replace("workbench.xmi", "corrupted.xmi"));
	// toRemove.renameTo(renamed);
	// }
	// DEBUG.OUT("[GAMA] workbench.xmi removed. Restarting now");
	// return;
	//
	// }

}
