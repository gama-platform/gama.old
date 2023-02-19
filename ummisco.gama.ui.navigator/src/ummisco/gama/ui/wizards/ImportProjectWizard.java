/*******************************************************************************************************
 *
 * ImportProjectWizard.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import ummisco.gama.ui.resources.GamaIcons;

/**
 * The Class ImportProjectWizard.
 */
public class ImportProjectWizard extends Wizard implements IImportWizard {

	/** The Constant EXTERNAL_PROJECT_SECTION. */
	private static final String EXTERNAL_PROJECT_SECTION = "ExternalProjectImportWizard";//$NON-NLS-1$

	/** The main page. */
	private ImportProjectWizardPage mainPage;

	/** The current selection. */
	private final IStructuredSelection currentSelection = null;

	/** The initial path. */
	private String initialPath = null;

	/**
	 * Instantiates a new import project wizard.
	 */
	public ImportProjectWizard() {
		this(null);
	}

	/**
	 * Instantiates a new import project wizard.
	 *
	 * @param initialPath
	 *            the initial path
	 */
	public ImportProjectWizard(final String initialPath) {
		this.initialPath = initialPath;
		setNeedsProgressMonitor(true);
		final IDialogSettings workbenchSettings = IDEWorkbenchPlugin.getDefault().getDialogSettings();

		IDialogSettings wizardSettings = workbenchSettings.getSection(EXTERNAL_PROJECT_SECTION);
		if (wizardSettings == null) { wizardSettings = workbenchSettings.addNewSection(EXTERNAL_PROJECT_SECTION); }
		setDialogSettings(wizardSettings);
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	@Override
	public boolean performCancel() {
		mainPage.performCancel();
		return true;
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	@Override
	public boolean performFinish() {
		return mainPage.createProjects();
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection currentSelection) {
		setDefaultPageImageDescriptor(GamaIcons.create("navigator/import.project").descriptor());
	}

	@Override
	public void addPages() {
		mainPage = new ImportProjectWizardPage("wizardExternalProjectsPage", initialPath, currentSelection); //$NON-NLS-1$
		addPage(mainPage);
	}

}
