/*******************************************************************************************************
 *
 * NewFileWizard.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.wizards;

import org.eclipse.jface.viewers.ISelection;

/**
 * The Class NewFileWizard.
 */
public class NewFileWizard extends AbstractNewModelWizard {

	@Override
	public AbstractNewModelWizardPage createPage(final ISelection selection) {
		return new NewFileWizardPage(selection);
	}

	@Override
	protected String getDefaultFolderForModels() {
		return "models";
	}

}