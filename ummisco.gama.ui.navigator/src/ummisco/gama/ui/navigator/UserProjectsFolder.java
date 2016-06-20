/*********************************************************************************************
 *
 *
 * 'UserProjectsFolder.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class UserProjectsFolder extends TopLevelFolder implements IAdaptable {

	public UserProjectsFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public Image getImage() {
		if (PlatformHelper.isGtk())
			return IGamaIcons.FOLDER_USER_16.image();
		return IGamaIcons.FOLDER_USER.image(); // FOLDER_USER
	}

	@Override
	public Image getImageForStatus() {
		return GamaIcons.create("navigator/folder.status.user").image();
	}

	@Override
	public String getMessageForStatus() {
		return "User-defined models";
	}

	@Override
	public GamaUIColor getColorForStatus() {
		return IGamaColors.OK;
	}

	/**
	 * Method accepts()
	 * 
	 * @see ummisco.gama.ui.navigator.TopLevelFolder#accepts(org.eclipse.core.resources.IProjectDescription)
	 */
	@Override
	protected boolean accepts(final IProjectDescription desc) {
		// Addition of a test regarding the "old" versions thay may still be
		// labeled as 'built-in'. The simplest way is to verify that no other
		// natures have been added to the project (i.e. it only has
		// Xtext and GAMA). If the number of versions is greater than 2 we
		// return false.
		if (!desc.hasNature(WorkbenchHelper.GAMA_NATURE) || desc.getNatureIds().length > 2) {
			return false;
		}
		return !(desc.hasNature(WorkbenchHelper.BUILTIN_NATURE) || desc.hasNature(WorkbenchHelper.PLUGIN_NATURE));
	}

	/**
	 * Method getModelsLocation()
	 * 
	 * @see ummisco.gama.ui.navigator.TopLevelFolder#getModelsLocation()
	 */
	@Override
	protected Location getModelsLocation() {
		return Location.Other;
	}

	/**
	 * Method getAdapter()
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == getClass()) {
			return this;
		}
		if (IContainer.class.isAssignableFrom(adapter)) {
			return ResourcesPlugin.getWorkspace();
		}
		return null;
	}

}
