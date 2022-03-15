package spll.localizer.distribution;

import java.util.Collections;
import java.util.List;

import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandom;

/**
 * Uniform Spatial Distribution: each candidate has the same probability to be chosen 
 * 
 * @author patricktaillandier
 *
 * @param <N>
 */
public class UniformSpatialDistribution<N extends Number, E extends ADemoEntity> implements ISpatialDistribution<E> {
	
	private List<? extends AGeoEntity<? extends IValue>> candidates;

	@Override
	public AGeoEntity<? extends IValue> getCandidate(E entity, List<? extends AGeoEntity<? extends IValue>> candidates) {
		return candidates.get(GenstarRandom.getInstance().nextInt(candidates.size()));
	}

	@Override
	public AGeoEntity<? extends IValue> getCandidate(E entity) {
		if(this.candidates == null || this.candidates.isEmpty())
			throw new NullPointerException("No candidates have been setp - use ISpatialDistribution.setCandidates(List) first");
		return this.getCandidate(entity, candidates);
	}

	@Override
	public void setCandidate(List<? extends AGeoEntity<? extends IValue>> candidates) {
		this.candidates = candidates;
	}

	@Override
	public List<? extends AGeoEntity<? extends IValue>> getCandidates() {
		return Collections.unmodifiableList(candidates);
	}
	
}
