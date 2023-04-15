/*******************************************************************************************************
 *
 * NewTestExperimentWizardPage.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.wizards;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * The Class NewTestExperimentWizardPage.
 */
public class NewTestExperimentWizardPage extends AbstractNewModelWizardPage {

	/**
	 * Instantiates a new new test experiment wizard page.
	 *
	 * @param selection the selection
	 */
	public NewTestExperimentWizardPage(final ISelection selection) {
		super(selection);
		setTitle("Test Experiment");
		setDescription("This wizard creates a new test experiment");
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		createContainerSection(container);
		createFileNameSection(container);
		createAuthorSection(container);
		createNameSection(container);
		/* Need to add empty label so the next two controls are pushed to the next line in the grid. */
		/* Finished adding the custom control */
		initialize();
		dialogChanged();
		setControl(container);
	}

	@Override
	public String getTemplateType() {
		return AbstractNewModelWizard.TEST_EXP;
	}

	@Override
	public String getTemplatePath() {
		return AbstractNewModelWizard.TEMPLATES.get(getTemplateType());
	}

	@Override
	public String getExtension() {
		return ".experiment";
	}

	@Override
	protected String getInnerDefaultFolder() {
		return "tests";
	}

	@Override
	public String gamlType() {
		return "Test Experiment";
	}

	@Override
	public boolean createDoc() {
		return false;
	}

}