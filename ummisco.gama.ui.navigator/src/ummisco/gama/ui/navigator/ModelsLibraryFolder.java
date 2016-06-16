/*********************************************************************************************
 *
 *
 * 'ModelsLibraryFolder.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.swt.graphics.Image;
import msi.gama.application.projects.BuiltinNature;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.Platform;

public class ModelsLibraryFolder extends TopLevelFolder {

	public ModelsLibraryFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public Image getImage() {
		if ( Platform.isGtk() )
			return IGamaIcons.FOLDER_BUILTIN_16.image();
		return IGamaIcons.FOLDER_BUILTIN.image(); // FOLDER_BUILTIN
	}

	@Override
	public Image getImageForStatus() {
		return GamaIcons.create("navigator/folder.status.library").image();
	}

	@Override
	public String getMessageForStatus() {
		return "Models shipped with GAMA";
	}

	@Override
	public GamaUIColor getColorForStatus() {
		return IGamaColors.BLUE;
	}

	/**
	 * Method accepts()
	 * @see ummisco.gama.ui.navigator.TopLevelFolder#accepts(org.eclipse.core.resources.IProjectDescription)
	 */
	@Override
	protected boolean accepts(final IProjectDescription desc) {
		return desc.hasNature(BuiltinNature.NATURE_ID);
	}

	/**
	 * Method getModelsLocation()
	 * @see ummisco.gama.ui.navigator.TopLevelFolder#getModelsLocation()
	 */
	@Override
	protected Location getModelsLocation() {
		return Location.CoreModels;
	}

}
