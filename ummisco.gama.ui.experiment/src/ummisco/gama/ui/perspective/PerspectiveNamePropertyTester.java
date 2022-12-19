/*******************************************************************************************************
 *
 * PerspectiveNamePropertyTester.java, in ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.perspective;

import org.eclipse.core.expressions.PropertyTester;

/**
 * The Class PerspectiveNamePropertyTester.
 */
public class PerspectiveNamePropertyTester extends PropertyTester {

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		// DEBUG.LOG("Perspective name :" + receiver + " contains " + expectedValue + " ?");
		final String s = receiver instanceof String ? (String) receiver : "";
		final String in = expectedValue instanceof String ? (String) expectedValue : "";
		return s.contains(in);
	}

}
