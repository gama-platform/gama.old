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
import msi.gama.application.projects.PluginNature;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;

public class PluginsModelsFolder extends TopLevelFolder {

	public PluginsModelsFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public Image getImage() {
		return IGamaIcons.FOLDER_PLUGIN.image();
	}

	@Override
	public Image getImageForStatus() {
		return GamaIcons.create("navigator/folder.status.plugin").image();
	}

	@Override
	public String getMessageForStatus() {
		return "Models present in GAMA plugins";
	}

	@Override
	public GamaUIColor getColorForStatus() {
		return IGamaColors.WARNING;
	}

	/**
	 * Method accepts()
	 * @see msi.gama.gui.navigator.TopLevelFolder#accepts(org.eclipse.core.resources.IProjectDescription)
	 */
	@Override
	protected boolean accepts(final IProjectDescription desc) {
		return desc.hasNature(PluginNature.NATURE_ID);
	}

	/**
	 * Method getModelsLocation()
	 * @see msi.gama.gui.navigator.TopLevelFolder#getModelsLocation()
	 */
	@Override
	protected Location getModelsLocation() {
		return Location.Plugins;
	}

}
