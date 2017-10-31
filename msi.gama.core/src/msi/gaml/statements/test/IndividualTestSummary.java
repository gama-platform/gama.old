package msi.gaml.statements.test;

import msi.gaml.operators.Strings;

/**
 * A summary of a test statement
 * 
 * @author drogoul
 *
 */
public class IndividualTestSummary extends CompoundSummary<AssertionSummary, TestStatement> {

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