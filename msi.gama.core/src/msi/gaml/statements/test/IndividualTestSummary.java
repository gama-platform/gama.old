/*******************************************************************************************************
 *
 * IndividualTestSummary.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.test;

import msi.gaml.operators.Strings;

/**
 * A summary of a test statement
 * 
 * @author drogoul
 *
 */
public class IndividualTestSummary extends CompoundSummary<AssertionSummary, TestStatement> {

	/**
	 * Instantiates a new individual test summary.
	 *
	 * @param test the test
	 */
	IndividualTestSummary(final TestStatement test) {
		super(test);
	}

	@Override
	public int countTestsWith(final TestState state) {
		return getState() == state ? 1 : 0;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	protected void printFooter(final StringBuilder sb) {
		sb.append(Strings.LN);
	}

	@Override
	protected void printHeader(final StringBuilder sb) {
		sb.append(Strings.LN);
	}

}