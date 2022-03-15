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
 * Will search for neighbor based on entity as predicate: meaning that basic search will swap a given
 * entity with another one from a sample.
 * 
 * @author kevinchapuis
 *
 */
public class PopulationEntityNeighborSearch implements IPopulationNeighborSearch<GosplPopulation, ADemoEntity> {

	private IPopulation<ADemoEntity, Attribute<? extends IValue>> sample;
	private Collection<ADemoEntity> predicates;
	
	public PopulationEntityNeighborSearch() {
		this.predicates = new HashSet<>();
	}
	
	// ---------------------------------------------- //

	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(GosplPopulation population, ADemoEntity predicate, int size) {
		return this.getPairwisedEntities(population, predicate, size, false);
	}
	
	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(GosplPopulation population, int size, boolean childSizeConsistant) {
		return this.getPairwisedEntities(population, GenstarRandomUtils.oneOf(this.getPredicates()), size, false);
	}
	
	@Override
	public Map<ADemoEntity, ADemoEntity> getPairwisedEntities(GosplPopulation population, ADemoEntity predicate, int size, boolean childSizeConsistant) {
		Map<ADemoEntity, ADemoEntity> pair = new HashMap<>();
		
		Set<ADemoEntity> predicates = new HashSet<>(Arrays.asList(predicate));
		if(size > 1)
			predicates = population.stream().sorted(new HammingEntityComparator(predicate))
				.limit(size).collect(Collectors.toSet());
		
		if (childSizeConsistant && predicate.hasChildren()) {
			int sizeConstraint = predicate.getChildren().size();
			predicates.stream().filter(candidate  -> 
				candidate.hasChildren() && candidate.getChildren().size() == sizeConstraint 
					).collect(Collectors.toSet());
		}
		
		for(ADemoEntity oldEntity : predicates) {
			ADemoEntity candidateEntity = GenstarRandomUtils.oneOf(sample);
			while(candidateEntity.equals(oldEntity))
				candidateEntity = GenstarRandomUtils.oneOf(sample);
			pair.put(oldEntity, candidateEntity);
		}
		
		return pair;
	}

	@Override
	public Collection<ADemoEntity> getPredicates() {
		return Collections.unmodifiableCollection(predicates);
	}
	
	@Override
	public void setPredicates(Collection<ADemoEntity> predicates) {
		this.predicates = predicates;
	}

	@Override
	public void updatePredicates(GosplPopulation population) {
		this.setPredicates(population);
	}

	@Override
	public void setSample(GosplPopulation sample) {
		this.sample = sample;
	}

}
