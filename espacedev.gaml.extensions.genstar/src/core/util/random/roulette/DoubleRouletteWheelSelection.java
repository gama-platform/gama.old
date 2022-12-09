/*******************************************************************************************************
 *
 * DoubleRouletteWheelSelection.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.util.random.roulette;

import java.util.List;
import java.util.stream.Collectors;

import core.util.random.GenstarRandom;

/**
 * The Class DoubleRouletteWheelSelection.
 *
 * @param <K> the key type
 */
public class DoubleRouletteWheelSelection<K> extends ARouletteWheelSelection<Double, K> {

	/**
	 * Instantiates a new double roulette wheel selection.
	 *
	 * @param distribution the distribution
	 */
	public DoubleRouletteWheelSelection(final List<Double> distribution) {
		super(distribution);
	}

	@Override
	protected Double computeDistributionSum(final List<Double> dist) {
		return dist.stream().collect(Collectors.summingDouble(n -> n));
	}

	@Override
	public int drawIndex() throws IllegalStateException {

		if (distribution == null)
			throw new IllegalStateException("please define the distributoin first using setDistribution()");

		final double random = GenstarRandom.getInstance().nextDouble() * total;

		double currentSum = 0.;

		for (int index = 0; index < distribution.size(); index++) {
			currentSum += distribution.get(index);

			if (random < currentSum) return index;
		}

		// in case something is lost during the sum (unlikely except for huge distributions with very low values)
		return distribution.size() - 1;
	}

}
