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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INewWizard;

public class NewExperimentWizard extends AbstractNewModelWizard implements INewWizard {

	@Override
	protected String getHeader(final IContainer folder, final String str, final String title) {
		final IResource model =
				ResourcesPlugin.getWorkspace().getRoot().findMember(getPage().getExperimentedModelPath());
		final IPath pathToModel = model.getFullPath().makeRelativeTo(folder.getFullPath());
		return super.getHeader(folder, str, title).replaceAll("\\$MODEL\\$", "'" + pathToModel + "'");
	}

	@Override
	public AbstractNewModelWizardPage createPage(final ISelection selection) {
		return new NewExperimentWizardPage(selection);
	}

	@Override
	public NewExperimentWizardPage getPage() {
		return (NewExperimentWizardPage) page;
	}

	@Override
	protected String getDefaultFolderForModels() {
		return "models";
	}

}