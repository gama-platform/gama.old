/*********************************************************************************************
 *
 * 'NewFileWizardPage.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.wizards;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class NewTestExperimentWizardPage extends NewModelWizardPage {

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
		createLabel(container, null);
		/* Finished adding the custom control */
		initialize();
		dialogChanged();
		setControl(container);
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

}