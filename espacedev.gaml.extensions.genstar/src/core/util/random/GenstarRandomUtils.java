/*******************************************************************************************************
 *
 * GenstarRandomUtils.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util.random;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import core.util.data.GSDataParser;
import core.util.data.GSEnumDataType;
import core.util.exception.GenstarException;

/**
 * The Class GenstarRandomUtils.
 */
public class GenstarRandomUtils {

	/**
	 * Instantiates a new genstar random utils.
	 */
	private GenstarRandomUtils() {}

	/**
	 * Returns one element uniformly picked from the list
	 *
	 * @param l
	 * @return
	 */
	public static <T> T oneOf(final List<T> l) {

		// check param
		if (l.isEmpty()) throw new IllegalArgumentException("cannot take one value out of an empty list");

		// quick exit
		if (l.size() == 1) return l.get(0);

		return l.get(GenstarRandom.getInstance().nextInt(l.size()));
	}

	/**
	 * returns one element for a set. Warning: should in theory not be used on a Set which is not Sorted or ordered, as
	 * this underlying random order breaks the reproductibility of the generation.
	 *
	 * @param s
	 * @return
	 */
	public static <T> T oneOf(final Set<T> s) {

		// check param
		if (s.isEmpty()) throw new IllegalArgumentException("cannot take one value out of an empty set");

		// quick exit
		if (s.size() == 1) return s.iterator().next();

		return s.stream().skip(GenstarRandom.getInstance().nextInt(s.size())).findFirst()
				.orElseThrow(AssertionError::new);

	}

	/**
	 *
	 * @param <T>
	 * @param c
	 * @return
	 */
	public static <T> T oneOf(final Collection<T> c) {
		// check param
		if (c.isEmpty()) throw new IllegalArgumentException("cannot take one value out of an empty list");

		// quick exit
		if (c.size() == 1) return c.iterator().next();

		return c.stream().skip(GenstarRandom.getInstance().nextInt(c.size())).findFirst()
				.orElseThrow(AssertionError::new);
	}

	/**
	 * make a coin flip with given probability d to be true
	 *
	 * @param d
	 * @return
	 */
	public static boolean flip(final double d) {
		return GenstarRandom.getInstance().nextDouble() < d;
	}

	/**
	 * Get a random number between n1 and n2 either using double -- {@link Random#nextDouble()} -- or integer --
	 * {@link Random#nextInt(int)} .
	 *
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static Number rnd(final Number n1, final Number n2) {
		Random rand = GenstarRandom.getInstance();
		GSDataParser gsdp = new GSDataParser();
		GSEnumDataType type = gsdp.getValueType(n1.toString());
		return switch (type) {
			case Continue -> rand.nextDouble() * (n2.doubleValue() - n1.doubleValue()) + n1.doubleValue();
			case Integer -> rand.nextInt(n2.intValue() - n1.intValue()) + n1.intValue();
			default -> throw new GenstarException();
		};
	}

}
