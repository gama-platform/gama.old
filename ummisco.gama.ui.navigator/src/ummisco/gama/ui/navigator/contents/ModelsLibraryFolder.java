/*********************************************************************************************
 *
 * 'ModelsLibraryFolder.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import ummisco.gama.ui.utils.WorkbenchHelper;

public class ModelsLibraryFolder extends TopLevelFolder {

	public ModelsLibraryFolder(final NavigatorRoot root, final String name) {
		super(root, name, FOLDER_BUILTIN, "navigator/folder.status.library", "Models shipped with GAMA", BLUE,
				WorkbenchHelper.BUILTIN_NATURE, Location.CoreModels);
	}

}
