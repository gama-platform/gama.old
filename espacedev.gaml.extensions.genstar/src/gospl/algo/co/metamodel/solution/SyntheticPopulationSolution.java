package gospl.algo.co.metamodel.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandomUtils;
import gospl.GosplPopulation;
import gospl.algo.co.metamodel.IOptimizationAlgorithm;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.validation.GosplIndicatorFactory;

/**
 * Abstract Combinatorial Optimization solution to be used in {@link IOptimizationAlgorithm}. 
 * Provide essential fitness calculation and the solution to combinatorial optimization problem
 * as a {@link IPopulation}
 * 
 * @author kevinchapuis
 *
 */
public class SyntheticPopulationSolution implements ISyntheticPopulationSolution<GosplPopulation> {

	protected GosplPopulation population;
		
	private double fitness = -1;
	
	public SyntheticPopulationSolution(GosplPopulation population){
		this.population = population;
	}
	
	public SyntheticPopulationSolution(Collection<ADemoEntity> population){
		this(new GosplPopulation(population));
	}
	
	// ----------------------- NEIGHBOR ----------------------- //
	
	@Override
	public <U> Collection<ISyntheticPopulationSolution<GosplPopulation>> getNeighbors(IPopulationNeighborSearch<GosplPopulation, U> neighborSearch) {
		 return getNeighbors(neighborSearch, 1);
	}
	
	@Override
	public <U> Collection<ISyntheticPopulationSolution<GosplPopulation>> getNeighbors(IPopulationNeighborSearch<GosplPopulation, U> neighborSearch,
			int k_neighbors) {
		return neighborSearch.getPredicates().stream()
				.map(u -> new SyntheticPopulationSolution(
						neighborSearch.getNeighbor(this.population, u, k_neighbors,false)))
				.collect(Collectors.toCollection(ArrayList::new)); 
	}
	
	@Override
	public <U> SyntheticPopulationSolution getRandomNeighbor(IPopulationNeighborSearch<GosplPopulation, U> neighborSearch) {
		return getRandomNeighbor(neighborSearch, 1);
	}

	@Override
	public <U> SyntheticPopulationSolution getRandomNeighbor(IPopulationNeighborSearch<GosplPopulation, U> neighborSearch, 
			int k_neighbors) {
		return new SyntheticPopulationSolution(
				neighborSearch.getNeighbor(this.population, 
						GenstarRandomUtils.oneOf(neighborSearch.getPredicates()), k_neighbors, false));
	}
	
	// ----------------------- FITNESS & SOLUTION ----------------------- //
	
	@Override
	public Double getFitness(Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		// Only compute once
		if(fitness == -1){
			AFullNDimensionalMatrix<Integer> popMatrix = GosplNDimensionalMatrixFactory
					.getFactory().createContingency(population);
			fitness = objectives.stream().mapToDouble(obj -> GosplIndicatorFactory.getFactory()
					.getIntegerTAE(obj, popMatrix)).sum();
		}
		return fitness;
	}
	
	@Override
	public INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> getAbsoluteErrors(
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> errorMatrix,
			Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		throw new UnsupportedOperationException("Ask dev to code it ;)");
	}
	
	@Override
	public GosplPopulation getSolution() {
		return population;
	}
	
	// ----------------------- UTILITY ----------------------- // 
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((population == null) ? 0 : population.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SyntheticPopulationSolution other = (SyntheticPopulationSolution) obj;
		if (population == null) {
			if (other.population != null)
				return false;
		} else if (!population.equals(other.population))
			return false;
		return true;
	}
	
}
