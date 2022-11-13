package spll.localizer.distribution;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import core.util.random.roulette.ARouletteWheelSelection;
import core.util.random.roulette.RouletteWheelSelectionFactory;
import spll.SpllEntity;
import spll.localizer.distribution.function.ISpatialComplexFunction;

/**
 * Spatial distribution that relies on both attribute of spatial and population entity. 
 * For example, probability attached to the distance between the entity to bind and the spatial entity to be bound with.
 * 
 * @author kevinchapuis
 *
 * @param <N>
 */
public class ComplexSpatialDistribution<N extends Number> implements ISpatialDistribution<SpllEntity> {

	private ISpatialComplexFunction<N> function;
	
	private List<? extends AGeoEntity<? extends IValue>> candidates;
	private Map<SpllEntity, ARouletteWheelSelection<N, ? extends AGeoEntity<? extends IValue>>> roulettes;
	
	public ComplexSpatialDistribution(ISpatialComplexFunction<N> function) {
		this.function = function;
	}
	
	@Override
	public AGeoEntity<? extends IValue> getCandidate(SpllEntity entity, 
			List<? extends AGeoEntity<? extends IValue>> candidates) {
		return RouletteWheelSelectionFactory.getRouletteWheel(candidates.stream()
				.map(candidate -> function.apply(candidate, entity)).toList(), candidates)
			.drawObject();
	}

	@Override
	public AGeoEntity<? extends IValue> getCandidate(SpllEntity entity) {
		if(this.candidates == null || this.candidates.isEmpty())
			throw new NullPointerException("No candidates have been setup, must use "
					+ "ISpatialDistribution.setCandidates(List) first");
		if(this.roulettes == null)
			this.roulettes = new HashMap<>();
		if(this.roulettes.isEmpty()
				|| !this.roulettes.containsKey(entity))
			this.roulettes.put(entity, RouletteWheelSelectionFactory.getRouletteWheel(candidates.stream()
				.map(candidate -> function.apply(candidate, entity)).toList(), candidates));
		return roulettes.get(entity).drawObject();
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
