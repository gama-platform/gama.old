/*******************************************************************************************************
 *
 * ExportProjectWizard.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.wizards;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The Class ExportProjectWizard.
 */
public class ExportProjectWizard extends Wizard implements IExportWizard {

	/** The Constant EXTERNAL_PROJECT_SECTION. */
	private static final String EXTERNAL_PROJECT_SECTION = "ExternalProjectExportWizard";//$NON-NLS-1$

	/** The main page. */
	private ExportProjectWizardPage mainPage;

	/** The current selection. */
	private IStructuredSelection currentSelection = null;
	// private String initialPath = null;

	/**
	 * Instantiates a new export project wizard.
	 */
	public ExportProjectWizard() {
		this(null);
	}

	/**
	 * Instantiates a new export project wizard.
	 *
	 * @param initialPath
	 *            the initial path
	 */
	public ExportProjectWizard(final String initialPath) {
		// this.initialPath = initialPath;
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
		setDefaultPageImageDescriptor(GamaIcon.named(IGamaIcons.IMPORT_PROJECT).descriptor());
		final Object[] all = selection.toArray();
		for (int i = 0; i < all.length; i++) { all[i] = ResourceManager.getResource(all[i]); }
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
