package core.util.random.roulette;

import java.util.List;
import java.util.stream.Collectors;

import core.util.random.GenstarRandom;

public class IntegerRouletteWheelSelection<K> extends ARouletteWheelSelection<Integer, K> {

	public IntegerRouletteWheelSelection(List<Integer> distribution) {
		super(distribution);
	}

	@Override
	protected Integer computeDistributionSum(List<Integer> distribution) {
		return distribution.stream().collect(Collectors.summingInt(n->n));
	}

	@Override
	public int drawIndex() throws IllegalStateException {
		
		if (distribution == null)
			throw new IllegalStateException("please define the distributoin first using setDistribution()");
		
		final int random = GenstarRandom.getInstance().nextInt(total);
		
		int currentSum = 0;

		for (int index=0; index<distribution.size();index++) {
			currentSum += distribution.get(index);	
		
			if (random < currentSum) 
				return index;
		}
		
		// in case something is lost during the sum (unlikely except for huge distributions with very low values) 
		return distribution.size()-1;
	}


}
