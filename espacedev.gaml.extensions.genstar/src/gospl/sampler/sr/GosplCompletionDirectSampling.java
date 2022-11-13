package gospl.sampler.sr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import core.util.random.roulette.RouletteWheelSelectionFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.ICompletionSampler;
import gospl.sampler.IDistributionSampler;

/**
 * Sampler that is able to return a coordinate associated to another (partial) coordinate. In fact,
 * the sampler will take into account a small number of dimension/value pairs given as a coordinate to sample
 * missing dimension/value pairs according to the given distribution
 * 
 * @author kevinchapuis
 *
 */
public class GosplCompletionDirectSampling implements ICompletionSampler<ACoordinate<Attribute<? extends IValue>, IValue>>, IDistributionSampler {

	private AFullNDimensionalMatrix<Double> distribution;
	private GosplBasicSampler innerSampler;
	
	public GosplCompletionDirectSampling() {
		this.innerSampler = new GosplBasicSampler();
	}
	
	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> complete(
			ACoordinate<Attribute<? extends IValue>, IValue> originalEntity) {
		
		Map<ACoordinate<Attribute<? extends IValue>, IValue>, Double> subDistribution = 
				distribution.getMatrix().entrySet()
				.stream().filter(e -> e.getKey().containsAll(originalEntity.values()))
			.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getValue()));
		
		List<ACoordinate<Attribute<? extends IValue>, IValue>> keys = new ArrayList<>(subDistribution.keySet());
		
		return RouletteWheelSelectionFactory.getRouletteWheel(
				keys.stream().map(k -> subDistribution.get(k)).toList(), 
				keys).drawObject();
	}

	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> draw() {
		return this.innerSampler.draw();
	}

	@Override
	public Collection<ACoordinate<Attribute<? extends IValue>, IValue>> draw(int numberOfDraw) {
		return this.innerSampler.draw(numberOfDraw);
	}

	@Override
	public void setDistribution(AFullNDimensionalMatrix<Double> distribution) {
		this.distribution = distribution;
		this.innerSampler.setDistribution(distribution);
	}
	
	@Override
	public String toCsv(String csvSeparator) {
		
		return null;
	}

}
