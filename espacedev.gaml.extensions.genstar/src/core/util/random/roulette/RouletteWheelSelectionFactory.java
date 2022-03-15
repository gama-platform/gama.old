package core.util.random.roulette;

import java.util.List;

/**
 * Creates a RouletteWheelSelectionFactory based on the type of the distribution passed as parameter.
 * Used to sample indices from a list that contains a distribution.
 * 
 * @author Samuel Thiriot
 *
 */
public final class RouletteWheelSelectionFactory {


	
	@SuppressWarnings("unchecked")
	public static <X extends Number, K> ARouletteWheelSelection<X, K> getRouletteWheel(List<X> distribution) {
		
		if (distribution.isEmpty())
			throw new IllegalArgumentException("the distribution cannot be empty for roulette wheel selection");
		
		// pick up one value from the distribution 
		Object val = distribution.get(0);
		// and find the right selection based on its type
		if (val instanceof Double) {
			return (ARouletteWheelSelection<X, K>) new DoubleRouletteWheelSelection<K>((List<Double>) distribution);
		}
		if (val instanceof Integer) {
			return (ARouletteWheelSelection<X, K>) new IntegerRouletteWheelSelection<K>((List<Integer>) distribution);
		} 
		
		throw new IllegalArgumentException("roulette wheel selection is only implemented for Double or Integer; "+val.getClass().getSimpleName()+" found instead.");
		
	}
	
	public static <X extends Number, K> ARouletteWheelSelection<X, K> getRouletteWheel(List<X> distribution, List<K> keys) {
		ARouletteWheelSelection<X, K> res = getRouletteWheel(distribution);
		res.setKeys(keys);
		return res;
	}
	
	private RouletteWheelSelectionFactory() {}
	

}
