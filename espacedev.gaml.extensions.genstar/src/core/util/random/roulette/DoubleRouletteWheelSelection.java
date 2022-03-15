package core.util.random.roulette;

import java.util.List;
import java.util.stream.Collectors;

import core.util.random.GenstarRandom;

public class DoubleRouletteWheelSelection<K> extends ARouletteWheelSelection<Double, K> {

	public DoubleRouletteWheelSelection(List<Double> distribution) {
		super(distribution);
	}

	@Override
	protected Double computeDistributionSum(List<Double> distribution) {
		return distribution.stream().collect(Collectors.summingDouble(n -> n));
	}

	@Override
	public int drawIndex() throws IllegalStateException {
		
		if (distribution == null)
			throw new IllegalStateException("please define the distributoin first using setDistribution()");
		
		final double random = GenstarRandom.getInstance().nextDouble()*total;
		
		double currentSum = 0.;

		for (int index=0; index<distribution.size();index++) {
			currentSum += distribution.get(index);	
		
			if (random < currentSum) 
				return index;
		}
		
		// in case something is lost during the sum (unlikely except for huge distributions with very low values) 
		return distribution.size()-1;
	}


}
