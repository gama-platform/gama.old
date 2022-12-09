/*******************************************************************************************************
 *
 * IntegerRouletteWheelSelection.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
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
 * The Class IntegerRouletteWheelSelection.
 *
 * @param <K> the key type
 */
public class IntegerRouletteWheelSelection<K> extends ARouletteWheelSelection<Integer, K> {

	/**
	 * Instantiates a new integer roulette wheel selection.
	 *
	 * @param distribution the distribution
	 */
	public IntegerRouletteWheelSelection(final List<Integer> distribution) {
		super(distribution);
	}

	@Override
	protected Integer computeDistributionSum(final List<Integer> dist) {
		return dist.stream().collect(Collectors.summingInt(n -> n));
	}

	@Override
	public int drawIndex() throws IllegalStateException {

		if (distribution == null)
			throw new IllegalStateException("please define the distributoin first using setDistribution()");

		final int random = GenstarRandom.getInstance().nextInt(total);

		int currentSum = 0;

		for (int index = 0; index < distribution.size(); index++) {
			currentSum += distribution.get(index);

			if (random < currentSum) return index;
		}

		// in case something is lost during the sum (unlikely except for huge distributions with very low values)
		return distribution.size() - 1;
	}

}
