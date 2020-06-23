/*********************************************************************************************
 *
 * 'PluginsModelsFolder.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import msi.gaml.statements.test.CompoundSummary;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class TestModelsFolder extends TopLevelFolder {

	public TestModelsFolder(final NavigatorRoot root, final String name) {
		super(root, name, FOLDER_TEST, "navigator/folder.status.test", "Built-in tests", NEUTRAL,
				WorkbenchHelper.TEST_NATURE, Location.Tests);
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		final CompoundSummary<?, ?> summary = getManager().getTestsSummary();
		if (summary != null)
			sb.append(summary.getStringSummary());
		else {
			super.getSuffix(sb);
			sb.append(", not yet run");
		}

	}

}
