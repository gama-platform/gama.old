/*******************************************************************************************************
 *
 * Initialization.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.kernel.batch.optimization.genetic;

import java.util.List;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Interface Initialization.
 */
public interface Initialization {

	/**
	 * Initialize pop.
	 *
	 * @param scope the scope
	 * @param variables the variables
	 * @param algo the algo
	 * @return the list
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	List<Chromosome> initializePop(IScope scope, List<IParameter.Batch> variables, GeneticAlgorithm algo)
			throws GamaRuntimeException;
}
