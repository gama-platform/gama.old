package spll.localizer.distribution;

import java.util.List;
import java.util.stream.Collectors;

import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import core.util.random.roulette.ARouletteWheelSelection;
import core.util.random.roulette.RouletteWheelSelectionFactory;
import spll.localizer.distribution.function.ISpatialEntityFunction;

/**
 * Spatial Distribution that relies on spatial entity attribute to asses probability. For exemple,
 * probability could be computed based on the area of spatial entity.
 * 
 * @author kevinchapuis
 *
 * @param <N>
 */
public class BasicSpatialDistribution<N extends Number, E extends ADemoEntity> implements ISpatialDistribution<E> {
	
	private ISpatialEntityFunction<N> function;
	private ARouletteWheelSelection<N, ? extends AGeoEntity<? extends IValue>> roulette;

	public BasicSpatialDistribution(ISpatialEntityFunction<N> function) {
		this.function = function;
	}
	
	@Override
	public AGeoEntity<? extends IValue> getCandidate(E entity, List<? extends AGeoEntity<? extends IValue>> candidates) {
		return RouletteWheelSelectionFactory.getRouletteWheel(candidates.stream()
				.map(a -> function.apply(a)).toList(), candidates)
			.drawObject();
	}

	@Override
	public AGeoEntity<? extends IValue> getCandidate(E entity) {
		if(this.roulette == null || this.roulette.getKeys().isEmpty())
			throw new NullPointerException("No candidate geographic entity to draw from");
		return roulette.drawObject();
	}

	@Override
	public void setCandidate(List<? extends AGeoEntity<? extends IValue>> candidates) {
		this.roulette = RouletteWheelSelectionFactory.getRouletteWheel(candidates.stream()
				.map(a -> function.apply(a)).toList(), candidates);
	}

	@Override
	public List<? extends AGeoEntity<? extends IValue>> getCandidates() {
		return roulette.getKeys();
	}
	
}
