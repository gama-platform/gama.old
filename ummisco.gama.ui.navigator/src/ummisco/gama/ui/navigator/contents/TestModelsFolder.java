/*********************************************************************************************
 *
 * 'PluginsModelsFolder.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import ummisco.gama.ui.utils.WorkbenchHelper;

public class TestModelsFolder extends TopLevelFolder {

	public TestModelsFolder(final Object root, final String name) {
		super(root, name, FOLDER_TEST, "navigator/folder.status.test", "Built-in tests", NEUTRAL,
				WorkbenchHelper.TEST_NATURE, Location.Tests);
	}

}
