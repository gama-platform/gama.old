/*******************************************************************************************************
 *
 * StoppingCriterionMaxIt.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.Map;

/**
 * The Class StoppingCriterionMaxIt.
 */
public class StoppingCriterionMaxIt implements StoppingCriterion {

	/** The max it. */
	private final int maxIt;

	/**
	 * Instantiates a new stopping criterion max it.
	 *
	 * @param maxIt the max it
	 */
	public StoppingCriterionMaxIt(final int maxIt) {
		super();
		this.maxIt = maxIt;
	}

	@Override
	@SuppressWarnings("boxing")
	public boolean stopSearchProcess(final Map<String, Object> parameters) {
		return (Integer) parameters.get("Iteration") > maxIt;
	}

}
