/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.SelectionRoulette.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.*;
import msi.gama.runtime.IScope;

public class SelectionRoulette implements Selection {

	public SelectionRoulette() {}

	@Override
	public List<Chromosome> select(final IScope scope, final List<Chromosome> population, final int populationDim,
		final boolean maximize) {

		final List<Chromosome> nextGen = new ArrayList<Chromosome>();
		double fitnessTotal = 0;
		for ( final Chromosome chromosome : population ) {
			if ( maximize ) {
				fitnessTotal += chromosome.getFitness();
			} else {
				fitnessTotal += 1.0 / chromosome.getFitness();
			}
		}
		// TODO Infinite Loop problem here ???
		while (nextGen.size() < populationDim) {
			final double rand = scope.getRandom().next();
			double fitnessSum = 0;
			for ( final Chromosome chromosome : population ) {
				if ( maximize ) {
					fitnessSum += chromosome.getFitness() / fitnessTotal;
				} else {
					fitnessSum += 1.0 / chromosome.getFitness() / fitnessTotal;
				}
				if ( fitnessSum >= rand ) {
					nextGen.add(chromosome);
					break;
				}
			}
		}

		return nextGen;
	}

}
