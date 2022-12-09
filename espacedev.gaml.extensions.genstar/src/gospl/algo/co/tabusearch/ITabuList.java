/*******************************************************************************************************
 *
 * ITabuList.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.tabusearch;

import gospl.GosplPopulation;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;

/**
 * Tabu list interface
 *
 * @author Alex Ferreira
 *
 */
public interface ITabuList extends Iterable<ISyntheticPopulationSolution<GosplPopulation>> {

	/**
	 * Add some solution to the tabu
	 *
	 * @param solution
	 *            the solution to be added
	 */
	void add(ISyntheticPopulationSolution<GosplPopulation> solution);

	/**
	 * Check if a given solution is inside of this tabu list
	 *
	 * @param solution
	 *            the solution to check
	 * @return true if the given solution is contained by this tabu, false otherwise
	 */
	boolean contains(ISyntheticPopulationSolution<GosplPopulation> solution);

	/**
	 * Update the size of the tabu list dinamically<br>
	 * This method should be implemented only by dynamic sized tabu lists, and may be called after each iteration of the
	 * algorithm
	 *
	 * @param currentIteration
	 *            the current iteration of the algorithm
	 * @param bestSolutionFound
	 *            the best solution found so far
	 */
	default void updateSize(final Integer currentIteration,
			final ISyntheticPopulationSolution<GosplPopulation> bestSolutionFound) {}

	/**
	 * Max size.
	 *
	 * @return the int
	 */
	int maxSize();

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	int getSize();

}