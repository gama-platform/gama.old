/*******************************************************************************************************
 *
 * TestExperimentSummary.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

	/**
	 * Instantiates a new test experiment summary.
	 *
	 * @param testAgent the test agent
	 */
	public TestExperimentSummary(final TestAgent testAgent) {
		super(testAgent);
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
