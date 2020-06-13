/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.SelectionBest.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import static java.lang.Double.compare;
import static one.util.streamex.StreamEx.of;

import java.util.Collections;
import java.util.List;

import msi.gama.runtime.IScope;

public class SelectionBest implements Selection {

	public SelectionBest() {}

	@Override
	public List<Chromosome> select(final IScope scope, final List<Chromosome> population, final int populationDim,
			final boolean maximize) {

		final List<Chromosome> nextGen =
				of(population).sorted((e1, e2) -> compare(e1.getFitness(), e2.getFitness())).toList();
		if (maximize) {
			Collections.reverse(nextGen);
		}
		return nextGen.subList(0, populationDim);
	}
}
