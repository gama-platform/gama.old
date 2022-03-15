package gospl.algo.co.metamodel;

import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;
import gospl.distribution.matrix.INDimensionalMatrix;

/**
 * Main interfaces for combinatorial optimization algorithm
 * 
 * @author kevinchapuis
 *
 */
public interface IOptimizationAlgorithm<
	Population extends IPopulation<ADemoEntity, Attribute<? extends IValue>>,
	Solution extends ISyntheticPopulationSolution<Population>> {

	/**
	 * Execute the algorithm to perform targeted optimization.
	 * @param {@code initialSolution} the start point of the algorithm
	 * @return the best solution found in the given conditions
	 */
	public Solution run(Solution initialSolution);
	
	/**
	 * Retrieve the set of objectives this optimization algorithm is
	 * calibrated with
	 * @return
	 */
	public Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> getObjectives();
	
	/**
	 * Add objectives to assess solution goodness-of-fit
	 * @param objectives
	 */
	public void addObjectives(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives);
	
	/**
	 * Returns the sample used to drive combinatorial optimization algorithme
	 * @return
	 */
	public IPopulation<ADemoEntity, Attribute<? extends IValue>> getSample();
	
	/**
	 * Set the sample of entity to be used to drive combinatorial optimization algorithm
	 * @param sample
	 */
	public void setSample(Population sample);
	
	/**
	 * Return the algorithm to be used when asking for neighbor populations
	 * @return
	 */
	public IPopulationNeighborSearch<Population, ?> getNeighborSearchAlgorithm();
	
	/**
	 * Set the algorithm to be used when searching for neighbor populations
	 * @param neighborSearch
	 */
	public void setNeighborSearch(IPopulationNeighborSearch<Population, ?> neighborSearch);
	
	public double getFitnessThreshold();
	
	public void setFitnessThreshold(double fitnessThreshold);
	
	/**
	 * The k neighbor buffer to size default neighbor search. Put it simply,
	 * it is the ratio of entity to be switch to neighbor population
	 * @see IPopulationNeighborSearch
	 * @return
	 */
	public double getK_neighborRatio();

	/**
	 * Set k neighbor buffer ratio
	 * @param k_neighborRatio
	 */
	public void setK_neighborRatio(double k_neighborRatio);
	
}
