/*******************************************************************************************************
 *
 * PopulationAttributeNeighborSearch.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co.metamodel.neighbor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.comparator.HammingEntityComparator;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandomUtils;
import gospl.GosplPopulation;

/**
 * Will search for neighbor based on attribute as predicate: meaning that basic search will swap a given entity with
 * another one from a sample, based on the fact that they have same value attribute (or the highest number of common
 * value attribute) except the one given as predicate.
 *
 * @author kevinchapuis
 *
 */
public class PopulationAttributeNeighborSearch
		implements IPopulationNeighborSearch<GosplPopulation, Attribute<? extends IValue>> {

	/** The predicates. */
	private Collection<Attribute<? extends IValue>> predicates;
	
	/** The sample. */
	private IPopulation<ADemoEntity, Attribute<? extends IValue>> sample;

	/**
	 * Instantiates a new population attribute neighbor search.
	 */
	public PopulationAttributeNeighborSearch() {
		this.predicates = new HashSet<>();
	}

	// ------------------------ NEIGHBORING ------------------------ //

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: Can be very time consuming, because it has to check for an entity in the sample that has the following
	 * property: have the exact same set of value except for predicate attribute. If none have been found, rely on
	 * hamming distance with predicate attribute regex.
	 * <p>
	 *
	 * @see HammingEntityComparator
	 */
	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population,
			final Attribute<? extends IValue> predicate, final int size) {
		return this.getPairwisedEntities(population, predicate, size, false);
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population, final int size,
			final boolean childSizeConsistant) {
		return this.getPairwisedEntities(population, GenstarRandomUtils.oneOf(this.getPredicates()), size,
				childSizeConsistant);
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population,
			final Attribute<? extends IValue> predicate, final int size, final boolean childSizeConsistant) {
		if (!population.hasPopulationAttributeNamed(predicate.getAttributeName()))
			throw new IllegalArgumentException("Trying to search for neighbor population on attribute "
					+ predicate.getAttributeName() + " that is not present");

		Map<ADemoEntity, ADemoEntity> pair = new HashMap<>();

		Map<ADemoEntity, Collection<IValue>> keys = new HashMap<>();
		while (keys.size() < size) {
			ADemoEntity oldEntity = GenstarRandomUtils.oneOf(population);
			if (keys.containsKey(oldEntity)) { continue; }
			Collection<IValue> matches = new ArrayList<>(oldEntity.getValues());
			matches.remove(oldEntity.getValueForAttribute(predicate));
			keys.put(oldEntity, matches);
		}

		for (ADemoEntity oldEntity : keys.keySet()) {
			Optional<ADemoEntity> candidateEntity = sample.stream()
					.filter(e -> !e.getValueForAttribute(predicate).equals(oldEntity.getValueForAttribute(predicate)) // Not
																														// the
																														// same
																														// value
																														// on
																														// predicate
																														// attribute
							&& e.getValues().containsAll(keys.get(oldEntity)) // Same value for the rest
							&& childSizeConsistant
									? e.hasChildren() && oldEntity.getChildren().size() == e.getChildren().size()
									: true) // And same number of children
					.findFirst();
			if (candidateEntity.isPresent()) {
				pair.put(oldEntity, candidateEntity.get());
			} else {
				pair.put(oldEntity, this.sample.stream().filter(
						e -> !e.getValueForAttribute(predicate).equals(oldEntity.getValueForAttribute(predicate))
								&& childSizeConsistant
										? e.hasChildren() && oldEntity.getChildren().size() == e.getChildren().size()
										: true)
						.sorted(new HammingEntityComparator(oldEntity)).findFirst().get());
			}
		}

		return pair;
	}

	// ---------------------------------------------------- //

	@Override
	public Collection<Attribute<? extends IValue>> getPredicates() {
		return Collections.unmodifiableCollection(this.predicates);
	}

	@Override
	public void setPredicates(final Collection<Attribute<? extends IValue>> predicates) {
		this.predicates = predicates;
	}

	@Override
	public void updatePredicates(final GosplPopulation population) {
		this.setPredicates(population.getPopulationAttributes());
	}

	@Override
	public void setSample(final GosplPopulation sample) { this.sample = sample; }

}
