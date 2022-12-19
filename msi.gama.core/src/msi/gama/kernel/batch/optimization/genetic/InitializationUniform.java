/*******************************************************************************************************
 *
 * InitializationUniform.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.kernel.batch.optimization.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class InitializationUniform.
 */
public class InitializationUniform implements Initialization {

	/**
	 * Instantiates a new initialization uniform.
	 */
	public InitializationUniform() {}

	@Override
	public List<Chromosome> initializePop(final IScope scope, final List<IParameter.Batch> variables,
			final GeneticAlgorithm algo) throws GamaRuntimeException {
		final List<Chromosome> populationInit = new ArrayList<>();
		final int nbPrelimGenerations = algo.getNbPrelimGenerations();
		final int populationDim = algo.getPopulationDim();
		for (int i = 0; i < nbPrelimGenerations; i++) {
			for (int j = 0; j < populationDim; j++) {
				populationInit.add(new Chromosome(scope, variables, true));
			}
		}
		/*for (final Chromosome chromosome : populationInit) {
			algo.computeChroFitness(scope, chromosome);
		}*/
		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue())
			algo.computePopFitnessAll(scope, populationInit); 
		else
			algo.computePopFitness(scope, populationInit);
		final List<Chromosome> populationInitOrd = new ArrayList<>(populationInit);
		Collections.sort(populationInitOrd);
		if (algo.isMaximize()) { Collections.reverse(populationInitOrd); }
		return populationInitOrd.subList(0, populationDim - 1);
	}

}
