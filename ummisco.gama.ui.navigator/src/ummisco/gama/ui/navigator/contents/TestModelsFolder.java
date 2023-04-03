/*******************************************************************************************************
 *
 * TestModelsFolder.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import msi.gaml.statements.test.CompoundSummary;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class TestModelsFolder.
 */
public class TestModelsFolder extends TopLevelFolder {

	/**
	 * Instantiates a new test models folder.
	 *
	 * @param root
	 *            the root
	 * @param name
	 *            the name
	 */
	public TestModelsFolder(final NavigatorRoot root, final String name) {
		super(root, name, FOLDER_TEST, "Built-in tests", NEUTRAL, WorkbenchHelper.TEST_NATURE, Location.Tests);
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		final CompoundSummary<?, ?> summary = getManager().getTestsSummary();
		if (summary != null) {
			sb.append(summary.getStringSummary());
		} else {
			super.getSuffix(sb);
			sb.append(", not yet run");
		}

	}

}
