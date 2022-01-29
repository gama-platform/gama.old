/*******************************************************************************************************
 *
 * ModelsLibraryFolder.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class ModelsLibraryFolder.
 */
public class ModelsLibraryFolder extends TopLevelFolder {

	/**
	 * Instantiates a new models library folder.
	 *
	 * @param root the root
	 * @param name the name
	 */
	public ModelsLibraryFolder(final NavigatorRoot root, final String name) {
		super(root, name, FOLDER_BUILTIN, "navigator/folder.status.library", "Models shipped with GAMA", BLUE,
				WorkbenchHelper.BUILTIN_NATURE, Location.CoreModels);
	}

}
