package msi.gaml.statements.test;

import msi.gama.kernel.experiment.TestAgent;
import msi.gaml.operators.Strings;

/**
 * A summary of a whole test experiment
 * 
 * @author drogoul
 *
 */
public class TestExperimentSummary extends CompoundSummary<IndividualTestSummary, TestAgent> {

	public TestExperimentSummary(final TestAgent testAgent) {
		super(testAgent);
	}

	@Override
	public int countTestsWith(final TestState state) {
		final int[] result = { 0 };
		summaries.values().forEach(s -> result[0] += s.countTestsWith(state));
		return result[0];
	}

	@Override
	protected void printFooter(final StringBuilder sb) {
		sb.append(Strings.LN);
		sb.append("----------------------------------------------------------------");
	}

	@Override
	protected void printHeader(final StringBuilder sb) {
		sb.append("----------------------------------------------------------------");
		sb.append(Strings.LN);
	}
}
