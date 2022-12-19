/*******************************************************************************************************
 *
 * PluginsModelsFolder.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class PluginsModelsFolder.
 */
public class PluginsModelsFolder extends TopLevelFolder {

	/**
	 * Instantiates a new plugins models folder.
	 *
	 * @param root the root
	 * @param name the name
	 */
	public PluginsModelsFolder(final NavigatorRoot root, final String name) {
		super(root, name, FOLDER_PLUGIN, "navigator/folder.status.plugin", "Models present in GAMA plugins", WARNING,
				WorkbenchHelper.PLUGIN_NATURE, Location.Plugins);
	}

}
