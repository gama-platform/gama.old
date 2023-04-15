/*******************************************************************************************************
 *
 * Mutation.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.kernel.batch.optimization.genetic;

import java.util.List;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;

/**
 * The Interface Mutation.
 */
public interface Mutation {

	/**
	 * Mutate.
	 *
	 * @param scope the scope
	 * @param chromosome the chromosome
	 * @param variables the variables
	 * @return the chromosome
	 */
	public Chromosome mutate(IScope scope, Chromosome chromosome, List<IParameter.Batch> variables);

}
