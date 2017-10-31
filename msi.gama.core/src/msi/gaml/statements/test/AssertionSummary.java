package msi.gaml.statements.test;

import msi.gaml.operators.Strings;

public class AssertionSummary extends AbstractSummary<AssertStatement> {
	private TestState state = TestState.NOT_RUN;

	public AssertionSummary(final AssertStatement a) {
		super(a);
	}

	@Override
	public void setState(final TestState s) {
		state = s;
	}

	@Override
	public void reset() {
		super.reset();
		state = TestState.NOT_RUN;
	}

	@Override
	public TestState getState() {
		return state;
	}

	@Override
	protected void printFooter(final StringBuilder sb) {
		sb.append(Strings.LN);
	}

}