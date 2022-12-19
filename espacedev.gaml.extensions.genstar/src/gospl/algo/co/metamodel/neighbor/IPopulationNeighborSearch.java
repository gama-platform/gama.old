/*******************************************************************************************************
 *
 * IPopulationNeighborSearch.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.metamodel.neighbor;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;

/**
 * Express how to define neighborhood in the context of synthetic population: what are the possible neighbors of a given
 * synthetic population
 *
 * @author kevinchapuis
 *
 * @param <U>
 */
public interface IPopulationNeighborSearch<Population extends IPopulation<ADemoEntity, Attribute<? extends IValue>>, Predicate> {

	/**
	 * Find a neighbor population given any predicate to based neighborhood on and a degree (something close as moor or
	 * von neuman neighborhood notion). More precisely the predicate will define the properties to shift the population,
	 * e.g. an attribute (then the population next to a population will be a switch on that attribute); while the degree
	 * could be the number of changes on that properties (e.g. 1 individual or several)
	 *
	 * @param population
	 * @param predicate
	 * @param degree
	 * @return
	 */
	default Population getNeighbor(final Population population, final Predicate predicate, final int degree,
			final boolean keepChildNumberConstant) {
		return this.getNeighbor(population,
				this.getPairwisedEntities(population, predicate, degree, keepChildNumberConstant));
	}

	/**
	 * @see #getNeighbor(IPopulation, Object, int)
	 *
	 * @param population
	 * @param theSwitches
	 * @return
	 */
	default Population getNeighbor(final Population population, final Map<ADemoEntity, ADemoEntity> theSwitches) {
		@SuppressWarnings ("unchecked") final Population neighbor = (Population) population.clone();

		if (neighbor.isEmpty()) throw new IllegalStateException("Cannot get neighbor of an empty population");

		for (Entry<ADemoEntity, ADemoEntity> theSwitch : theSwitches.entrySet()) {
			IPopulationNeighborSearch.deepSwitch(neighbor, theSwitch.getKey(), theSwitch.getValue());
		}

		if (population.equals(neighbor))
			throw new IllegalStateException("Current population and neighbor should be different somehow");

		return neighbor;
	}

	/**
	 * Given a predicate property find mapped entities to swap; Entities from the current population are keys, while
	 * entities from the sample are associated values
	 *
	 * @param population
	 * @param predicate
	 * @param size
	 * @return
	 */
	Map<ADemoEntity, ADemoEntity> getPairwisedEntities(Population population, Predicate predicate, int size);

	/**
	 * Find mapped entities according to a given Predicate and should or should not have the same child size
	 *
	 * @see {@link #getPairwisedEntities(IPopulation, Object, int)}
	 *
	 * @param population
	 * @param predicate
	 * @param size
	 * @return
	 */
	Map<ADemoEntity, ADemoEntity> getPairwisedEntities(Population population, Predicate predicate, int size,
			boolean childSizeConsistant);

	/**
	 * Find mapped entities (they are close in they attributes) and should or should not have the same child size
	 *
	 * @see {@link #getPairwisedEntities(IPopulation, Object, int)}
	 *
	 * @param population
	 * @param predicate
	 * @param size
	 * @return
	 */
	Map<ADemoEntity, ADemoEntity> getPairwisedEntities(Population population, int size, boolean childSizeConsistant);

	/**
	 * The predicates to be used
	 *
	 * @return
	 */
	Collection<Predicate> getPredicates();

	/**
	 * Set the collection of predicate that could be used
	 */
	void setPredicates(Collection<Predicate> predicates);

	/**
	 * Update the state of predicate based on the current population
	 *
	 * @param predicate
	 */
	void updatePredicates(Population population);

	/**
	 * The sample of entities which is the reservoir to swap entities from given population to its neighbors
	 *
	 * @param sample
	 */
	void setSample(Population sample);

	// ----------------- UTILITY ----------------- //

	/**
	 * Ensure that two entities have been deeply switch in the population
	 * <p>
	 * WARNING: newEntity should be a clone of an existing one, if one already exist in the population
	 *
	 * @param population
	 * @param oldEntity
	 * @param newEntity
	 * @return
	 */
	static <Pop extends IPopulation<ADemoEntity, Attribute<? extends IValue>>> Pop
			deepSwitch(final Pop population, final ADemoEntity oldEntity, final ADemoEntity newEntity) {
		if (oldEntity.equals(newEntity)) throw new IllegalArgumentException("Equal entities should not be removed");
		if (!population.remove(oldEntity))
			throw new IllegalArgumentException("Cannot remove " + oldEntity + " from population " + population);
		if (!population.add(newEntity)) throw new IllegalArgumentException(
				"Have not been able to add entity " + newEntity + " to population " + population);
		return population;
	}

}
