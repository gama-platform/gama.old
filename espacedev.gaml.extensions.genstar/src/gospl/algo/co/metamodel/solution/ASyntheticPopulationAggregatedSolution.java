package gospl.algo.co.metamodel.solution;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.validation.GosplIndicatorFactory;

/**
 * Solution of optimization process which encapsulate a IPopulation. Fitness computation is based on
 * aggregated marginals, that is marginals are not recomputed each time but 
 * 
 * @author kevinchapuis
 *
 */
public abstract class ASyntheticPopulationAggregatedSolution<Population extends IPopulation<ADemoEntity, Attribute<? extends IValue>>> 
	implements ISyntheticPopulationSolution<Population> {

	private Population population;
	
	private AFullNDimensionalMatrix<Integer> marginals;
	
	private double fitness = -1;
	
	public ASyntheticPopulationAggregatedSolution(Population population,
			AFullNDimensionalMatrix<Integer> marginals){
		this.population = population;
		this.marginals = marginals;
	}
	
	public ASyntheticPopulationAggregatedSolution(Population population) {
		this(population, new GosplNDimensionalMatrixFactory().createContingency(population));
	}
	
	// ----------------------- //
	
	@Override
	public <U> Collection<ISyntheticPopulationSolution<Population>> getNeighbors(IPopulationNeighborSearch<Population, U> neighborSearch) {
		 return getNeighbors(neighborSearch, 1);
	}
	
	/*
	@Override
	public <U> Collection<ISyntheticPopulationSolution<Population>> getNeighbors(IPopulationNeighborSearch<Population, U> neighborSearch,
			int k_neighbors) {
		
		Collection<ISyntheticPopulationSolution<Population>> neighbors = new ArrayList<>();
		
		for(U predicate : neighborSearch.getPredicates()) {
			Map<ADemoEntity, ADemoEntity> theSwitch = neighborSearch.getPairwisedEntities(this.population, predicate, k_neighbors); 

			neighbors.add(new ASyntheticPopulationAggregatedSolution<>(
					neighborSearch.getNeighbor(this.population, theSwitch), 
					this.makeSwitch(new GosplNDimensionalMatrixFactory()
							.cloneContingency(this.marginals), theSwitch)));
		}
		
		return neighbors;
	}*/
	
	@Override
	public <U> ISyntheticPopulationSolution<Population> getRandomNeighbor(IPopulationNeighborSearch<Population, U> neighborSearch) {
		return getRandomNeighbor(neighborSearch, 1);
	}

	/*
	@Override
	public <U> ISyntheticPopulationSolution<Population> getRandomNeighbor(IPopulationNeighborSearch<Population, U> neighborSearch, 
			int k_neighbors) {
		Map<ADemoEntity, ADemoEntity> theSwitch = neighborSearch.getPairwisedEntities(this.population, 
				GenstarRandomUtils.oneOf(neighborSearch.getPredicates()), k_neighbors);
		
		return new ASyntheticPopulationAggregatedSolution<>(neighborSearch.getNeighbor(this.population, theSwitch), 
				this.makeSwitch(new GosplNDimensionalMatrixFactory().cloneContingency(this.marginals), theSwitch));
	}*/
	
	// ----------------------- //
	
	public abstract ASyntheticPopulationAggregatedSolution<Population> spSolutionProvider();
	
	// ----------------------- //

	@Override
	public Double getFitness(Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		// Only compute once
		if(fitness == -1){
			fitness = objectives.stream().mapToDouble(obj -> GosplIndicatorFactory.getFactory()
					.getIntegerTAE(obj, marginals)).sum();
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
	public Population getSolution() {
		return population;
	}
	
	// ------------------------ //
	
	protected AFullNDimensionalMatrix<Integer> makeSwitch(AFullNDimensionalMatrix<Integer> matrix, 
			Map<ADemoEntity, ADemoEntity> theSwitch){
		
		for(ADemoEntity oldEntity : theSwitch.keySet()) {
			matrix.getVal(matrix.getCoordinate(new HashSet<>(oldEntity.getValues()))).add(-1);
			matrix.getVal(matrix.getCoordinate(new HashSet<>(theSwitch.get(oldEntity).getValues()))).add(1);
		}
		
		return matrix;
	}

}
