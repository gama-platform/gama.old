/*******************************************************************************************************
 *
 * PopulationRandomNeighborSearch.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co.metamodel.neighbor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import core.metamodel.entity.ADemoEntity;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import gospl.GosplPopulation;
import gospl.sampler.co.MicroDataSampler;

/**
 * The Class PopulationRandomNeighborSearch.
 *
 * @param <Predicate> the generic type
 */
public class PopulationRandomNeighborSearch<Predicate>
		implements IPopulationNeighborSearch<GosplPopulation, Predicate> {

	/** The to sample. */
	private MicroDataSampler toSample;
	
	/** The use weights. */
	private boolean useWeights;

	/**
	 * Instantiates a new population random neighbor search.
	 */
	public PopulationRandomNeighborSearch() {
		this(false);
	}

	/**
	 * Instantiates a new population random neighbor search.
	 *
	 * @param useWeights the use weights
	 */
	public PopulationRandomNeighborSearch(final boolean useWeights) {
		this.useWeights = useWeights;
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population,
			final Predicate predicate, final int size) {
		return this.getPairwisedEntities(population, size, false);
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population,
			final Predicate predicate, final int size, final boolean childSizeConsistant) {
		return this.getPairwisedEntities(population, size, childSizeConsistant);
	}

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(final GosplPopulation population, final int size,
			final boolean childSizeConsistant) {
		// Output
		Map<ADemoEntity, ADemoEntity> output = new HashMap<>();
		// Utility sampler for current population pick entities to remove
		MicroDataSampler toRemove = new MicroDataSampler(false);
		toRemove.setSample(population, useWeights);

		GSPerformanceUtil gspu = new GSPerformanceUtil("Random neighbor search", Level.TRACE);
		gspu.sysoStempMessage("Sample from " + toSample.draw().getEntityType() + " entity type to switch with "
				+ population.stream().findFirst().get().getEntityType() + " base population entity");

		if (toRemove.isEmpty() || toSample.isEmpty())
			throw new IllegalStateException("Cannot have inner samplers empty");

		int tries = 0;
		while (output.size() < size && tries++ < Math.pow(10, 6)) {
			ADemoEntity er = toRemove.draw();
			ADemoEntity ea = toSample.draw(10).stream()
					.filter(candidate -> !candidate.getValues().containsAll(er.getValues())
							&& (childSizeConsistant ? candidate.getChildren().size() != er.getChildren().size() : true))
					.findFirst().orElse(null);
			if (ea == null) { continue; }
			output.put(er, ea);
		}

		if (output.containsKey(null))
			throw new IllegalArgumentException("This pairwised collection of entity contains null val");

		gspu.sysoStempMessage("The output is key::" + output.keySet().stream().findFirst().get().getEntityType() + " ("
				+ output.size() + ")" + " | value::" + output.values().stream().findFirst().get().getEntityType() + " ("
				+ output.size() + ")");

		return output;
	}

	@Override
	public Collection<Predicate> getPredicates() { return null; }

	@Override
	public void setPredicates(final Collection<Predicate> predicates) {
		// Do nothing
	}

	@Override
	public void updatePredicates(final GosplPopulation population) {
		// Do nothing
	}

	@Override
	public void setSample(final GosplPopulation sample) {
		if (this.toSample == null) { this.toSample = new MicroDataSampler(); }
		this.toSample.setSample(sample, useWeights);
	}

}
