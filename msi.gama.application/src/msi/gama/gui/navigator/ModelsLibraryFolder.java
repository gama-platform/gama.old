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
package msi.gama.gui.navigator;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.swt.graphics.Image;
import msi.gama.application.projects.BuiltinNature;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.swing.Platform;

public class ModelsLibraryFolder extends TopLevelFolder {

	public ModelsLibraryFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public Image getImage() {
		if (Platform.isGtk()) return IGamaIcons.FOLDER_BUILTIN_16.image();
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
	 * @see msi.gama.gui.navigator.TopLevelFolder#accepts(org.eclipse.core.resources.IProjectDescription)
	 */
	@Override
	protected boolean accepts(final IProjectDescription desc) {
		return desc.hasNature(BuiltinNature.NATURE_ID);
	}

	/**
	 * Method getModelsLocation()
	 * @see msi.gama.gui.navigator.TopLevelFolder#getModelsLocation()
	 */
	@Override
	protected Location getModelsLocation() {
		return Location.CoreModels;
	}

}
