/*********************************************************************************************
 *
 * 'InitializationUniform.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import gnu.trove.set.hash.THashSet;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class InitializationUniform implements Initialization {

	public InitializationUniform() {
	}

	@Override
	public List<Chromosome> initializePop(final IScope scope, final List<IParameter.Batch> variables,
			GeneticAlgorithm algo)
			throws GamaRuntimeException {
		final Set<Chromosome> populationInit = new THashSet<Chromosome>();
		int nbPrelimGenerations = algo.getNbPrelimGenerations();
		int populationDim = algo.getPopulationDim();
		for (int i = 0; i < nbPrelimGenerations; i++) {
			for (int j = 0; j < populationDim; j++) {
				populationInit.add(new Chromosome(scope, variables, true));
			}
		}
		for (final Chromosome chromosome : populationInit) {
			algo.computeChroFitness(scope, chromosome);
		}
		final List<Chromosome> populationInitOrd = new ArrayList<Chromosome>(populationInit);
		Collections.sort(populationInitOrd);
		final List<Chromosome> populationInitFinal = new ArrayList<Chromosome>();
		if (!algo.isMaximize) {
			for (int i = 0; i < populationDim; i++) {
				populationInitFinal.add(populationInitOrd.get(i));
			}
		} else {
			for (int i = populationInitOrd.size() - 1; i > populationInitOrd.size() - populationDim - 1; i--) {
				populationInitFinal.add(populationInitOrd.get(i));
			}
		}
		return populationInitFinal;
	}

}
