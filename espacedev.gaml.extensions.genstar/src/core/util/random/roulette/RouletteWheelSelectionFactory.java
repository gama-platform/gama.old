/*******************************************************************************************************
 *
 * RouletteWheelSelectionFactory.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util.random.roulette;

import java.util.List;

/**
 * Creates a RouletteWheelSelectionFactory based on the type of the distribution passed as parameter. Used to sample
 * indices from a list that contains a distribution.
 *
 * @author Samuel Thiriot
 *
 */
public final class RouletteWheelSelectionFactory {

	/**
	 * Gets the roulette wheel.
	 *
	 * @param <X>
	 *            the generic type
	 * @param <K>
	 *            the key type
	 * @param distribution
	 *            the distribution
	 * @return the roulette wheel
	 */
	@SuppressWarnings ("unchecked")
	public static <X extends Number, K> ARouletteWheelSelection<X, K> getRouletteWheel(final List<X> distribution) {

		if (distribution.isEmpty())
			throw new IllegalArgumentException("the distribution cannot be empty for roulette wheel selection");

		// pick up one value from the distribution
		Object val = distribution.get(0);
		// and find the right selection based on its type
		if (val instanceof Double)
			return (ARouletteWheelSelection<X, K>) new DoubleRouletteWheelSelection<>((List<Double>) distribution);
		if (val instanceof Integer)
			return (ARouletteWheelSelection<X, K>) new IntegerRouletteWheelSelection<>((List<Integer>) distribution);

		throw new IllegalArgumentException("roulette wheel selection is only implemented for Double or Integer; "
				+ val.getClass().getSimpleName() + " found instead.");

	}

	/**
	 * Gets the roulette wheel.
	 *
	 * @param <X>
	 *            the generic type
	 * @param <K>
	 *            the key type
	 * @param distribution
	 *            the distribution
	 * @param keys
	 *            the keys
	 * @return the roulette wheel
	 */
	public static <X extends Number, K> ARouletteWheelSelection<X, K> getRouletteWheel(final List<X> distribution,
			final List<K> keys) {
		ARouletteWheelSelection<X, K> res = getRouletteWheel(distribution);
		res.setKeys(keys);
		return res;
	}

	/**
	 * Instantiates a new roulette wheel selection factory.
	 */
	private RouletteWheelSelectionFactory() {}

}
