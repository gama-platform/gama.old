package ummisco.gama.ui.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.resources.GamaIcons;

public class ExportProjectWizard extends Wizard implements IExportWizard {

	private static final String EXTERNAL_PROJECT_SECTION = "ExternalProjectExportWizard";//$NON-NLS-1$
	private ExportProjectWizardPage mainPage;
	private IStructuredSelection currentSelection = null;
	private String initialPath = null;

	public ExportProjectWizard() {
		this(null);
	}

	public ExportProjectWizard(final String initialPath) {
		this.initialPath = initialPath;
		setNeedsProgressMonitor(true);
		final IDialogSettings workbenchSettings = IDEWorkbenchPlugin.getDefault().getDialogSettings();

		IDialogSettings wizardSettings = workbenchSettings.getSection(EXTERNAL_PROJECT_SECTION);
		if (wizardSettings == null) {
			wizardSettings = workbenchSettings.addNewSection(EXTERNAL_PROJECT_SECTION);
		}
		setDialogSettings(wizardSettings);
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	@Override
	public boolean performCancel() {
		// mainPage.performCancel();
		return true;
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	@Override
	public boolean performFinish() {
		return mainPage.finish();// createProjects();
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		setDefaultPageImageDescriptor(GamaIcons.create("navigator/navigator.import.project2").descriptor());
		final Object[] all = selection.toArray();
		for (int i = 0; i < all.length; i++) {
			all[i] = ResourceManager.getResource(all[i]);
		}
		this.currentSelection = new StructuredSelection(all);

	}

	@Override
	public void addPages() {
		// mainPage = new ExportProjectWizardPage("wizardExternalProjectsPage", initialPath, currentSelection);
		// //$NON-NLS-1$
		mainPage = new ExportProjectWizardPage(currentSelection);
		addPage(mainPage);
	}

}
