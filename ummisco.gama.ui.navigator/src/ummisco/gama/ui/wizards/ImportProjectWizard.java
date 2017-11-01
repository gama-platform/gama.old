package ummisco.gama.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizard;

public class ImportProjectWizard extends ExternalProjectImportWizard implements IImportWizard {

	public ImportProjectWizard() {}

	public ImportProjectWizard(final String initialPath) {
		super(initialPath);
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection currentSelection) {
		// TODO Auto-generated method stub
		super.init(workbench, currentSelection);
	}

}
