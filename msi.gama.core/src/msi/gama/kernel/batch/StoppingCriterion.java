/*******************************************************************************************************
 *
 * StoppingCriterion.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.Map;

/**
 * The Interface StoppingCriterion.
 */
public interface StoppingCriterion {

	/**
	 * Stop search process.
	 *
	 * @param parameters the parameters
	 * @return true, if successful
	 */
	public boolean stopSearchProcess(Map<String, Object> parameters);
}
