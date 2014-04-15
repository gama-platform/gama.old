/*********************************************************************************************
 * 
 * 
 * 'InitializationUniform.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.batch;

import gnu.trove.set.hash.THashSet;
import java.util.*;
import msi.gama.kernel.experiment.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class InitializationUniform implements Initialization {

	public InitializationUniform() {}

	@Override
	public List<Chromosome> initializePop(final List<IParameter.Batch> variables, final BatchAgent exp,
		final int populationDim, final int nbPrelimGenerations, final boolean isMaximize) throws GamaRuntimeException {
		final Set<Chromosome> populationInit = new THashSet<Chromosome>();
		for ( int i = 0; i < nbPrelimGenerations; i++ ) {
			for ( int j = 0; j < populationDim; j++ ) {
				populationInit.add(new Chromosome(exp.getScope(), variables, true));
			}
		}
		for ( final Chromosome chromosome : populationInit ) {
			final ParametersSet sol = chromosome.convertToSolution(variables);
			chromosome.setFitness(exp.launchSimulationsWithSolution(sol));
		}
		for ( final Chromosome chromosome1 : populationInit ) {
			final ParametersSet sol = chromosome1.convertToSolution(variables);
			chromosome1.setFitness(exp.launchSimulationsWithSolution(sol));
		}
		final List<Chromosome> populationInitOrd = new ArrayList<Chromosome>(populationInit);
		Collections.sort(populationInitOrd);
		final List<Chromosome> populationInitFinal = new ArrayList<Chromosome>();
		if ( !isMaximize ) {
			for ( int i = 0; i < populationDim; i++ ) {
				populationInitFinal.add(populationInitOrd.get(i));
			}
		} else {
			for ( int i = populationInitOrd.size() - 1; i > populationInitOrd.size() - populationDim - 1; i-- ) {
				populationInitFinal.add(populationInitOrd.get(i));
			}
		}
		return populationInitFinal;
	}

}
