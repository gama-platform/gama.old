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
package msi.gama.gui.navigator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import msi.gama.application.projects.*;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;

public class UserProjectsFolder extends TopLevelFolder implements IAdaptable {

	public UserProjectsFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public Image getImage() {
		return IGamaIcons.FOLDER_USER.image();
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
	 * @see msi.gama.gui.navigator.TopLevelFolder#accepts(org.eclipse.core.resources.IProjectDescription)
	 */
	@Override
	protected boolean accepts(final IProjectDescription desc) {
		// Addition of a test regarding the "old" versions thay may still be labeled as 'built-in'. The simplest way is to verify that no other natures have been added to the project (i.e. it only has
		// Xtext and GAMA). If the number of versions is greater than 2 we return false.
		if ( !desc.hasNature(GamaNature.NATURE_ID) || desc.getNatureIds().length > 2 ) { return false; }
		return !(desc.hasNature(BuiltinNature.NATURE_ID) || desc.hasNature(PluginNature.NATURE_ID));
	}

	/**
	 * Method getModelsLocation()
	 * @see msi.gama.gui.navigator.TopLevelFolder#getModelsLocation()
	 */
	@Override
	protected Location getModelsLocation() {
		return Location.Other;
	}

	/**
	 * Method getAdapter()
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(final Class adapter) {
		if ( adapter == getClass() ) { return this; }
		if ( IContainer.class.isAssignableFrom(adapter) ) { return ResourcesPlugin.getWorkspace(); }
		return null;
	}

}
