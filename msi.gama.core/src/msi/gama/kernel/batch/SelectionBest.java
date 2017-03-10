package msi.gama.kernel.batch;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import msi.gama.runtime.IScope;

public class SelectionBest implements Selection {

	public SelectionBest() {}

	@Override
	public List<Chromosome> select(final IScope scope, final List<Chromosome> population, final int populationDim,
		final boolean maximize) {

		List<Chromosome> nextGen = population.stream()
         .sorted((e1, e2) -> new Double(e1.getFitness()).compareTo(new Double(e2.getFitness()))).collect(Collectors.toList());
		if (maximize) 
			Collections.reverse(nextGen);
		return nextGen.subList(0, populationDim);
	}
}
