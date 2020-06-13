/*******************************************************************************************************
 *
 * msi.gaml.statements.test.TestExperimentSummary.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
