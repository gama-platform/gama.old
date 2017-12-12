/*********************************************************************************************
 *
 * 'NewFileWizard.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.wizards;

import org.eclipse.jface.viewers.ISelection;

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