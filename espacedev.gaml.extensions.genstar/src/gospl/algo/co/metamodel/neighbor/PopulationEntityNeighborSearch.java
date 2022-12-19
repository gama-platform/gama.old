/*******************************************************************************************************
 *
 * PopulationEntityNeighborSearch.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.metamodel.neighbor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.comparator.HammingEntityComparator;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandomUtils;
import gospl.GosplPopulation;

/**
 * Will search for neighbor based on entity as predicate: meaning that basic search will swap a given entity with
 * another one from a sample.
 *
 * @author kevinchapuis
 *
 */
public class PopulationEntityNeighborSearch implements IPopulationNeighborSearch<GosplPopulation, ADemoEntity> {

	/** The sample. */
	private IPopulation<ADemoEntity, Attribute<? extends IValue>> sample;

	/** The predicates. */
	private Collection<ADemoEntity> predicates;

	/**
	 * Instantiates a new population entity neighbor search.
	 */
	public PopulationEntityNeighborSearch() {
		this.predicates = new HashSet<>();
	}

	// ---------------------------------------------- //

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population,
			final ADemoEntity predicate, final int size) {
		return this.getPairwisedEntities(population, predicate, size, false);
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population, final int size,
			final boolean childSizeConsistant) {
		return this.getPairwisedEntities(population, GenstarRandomUtils.oneOf(this.getPredicates()), size, false);
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population,
			final ADemoEntity predicate, final int size, final boolean childSizeConsistant) {
		Map<ADemoEntity, ADemoEntity> pair = new HashMap<>();

		Set<ADemoEntity> preds = new HashSet<>(Arrays.asList(predicate));
		if (size > 1) {
			preds = population.stream().sorted(new HammingEntityComparator(predicate)).limit(size)
					.collect(Collectors.toSet());
		}

		if (childSizeConsistant && predicate.hasChildren()) {
			int sizeConstraint = predicate.getChildren().size();
			preds = preds.stream()
					.filter(candidate -> candidate.hasChildren() && candidate.getChildren().size() == sizeConstraint)
					.collect(Collectors.toSet());
		}

		for (ADemoEntity oldEntity : preds) {
			ADemoEntity candidateEntity = GenstarRandomUtils.oneOf(sample);
			while (candidateEntity.equals(oldEntity)) { candidateEntity = GenstarRandomUtils.oneOf(sample); }
			pair.put(oldEntity, candidateEntity);
		}

		return pair;
	}

	@Override
	public Collection<ADemoEntity> getPredicates() { return Collections.unmodifiableCollection(predicates); }

	@Override
	public void setPredicates(final Collection<ADemoEntity> predicates) { this.predicates = predicates; }

	@Override
	public void updatePredicates(final GosplPopulation population) {
		this.setPredicates(population);
	}

	@Override
	public void setSample(final GosplPopulation sample) { this.sample = sample; }

}
