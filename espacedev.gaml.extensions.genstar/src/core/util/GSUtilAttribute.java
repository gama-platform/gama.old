/*******************************************************************************************************
 *
 * GSUtilAttribute.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.IValue;

/**
 * The Class GSUtilAttribute.
 */
public class GSUtilAttribute {

	/**
	 * Instantiates a new GS util attribute.
	 */
	private GSUtilAttribute() {}

	/**
	 * Gets the values combination.
	 *
	 * @param <A>
	 *            the generic type
	 * @param attributes
	 *            the attributes
	 * @return the values combination
	 */
	// Optimize
	public static <A extends IAttribute<? extends IValue>> Collection<Map<A, IValue>>
			getValuesCombination(final Collection<A> attributes) {
		if (attributes.isEmpty())
			throw new IllegalArgumentException("You need at least one attribute to define value combination");
		List<A> tmpAttribtues = new ArrayList<>(attributes);
		Set<Map<A, IValue>> combination = new HashSet<>();
		A att = tmpAttribtues.remove(0);
		for (IValue val : att.getValueSpace().getValues()) {
			Map<A, IValue> map = new HashMap<>();
			map.put(att, val);
			combination.add(map);
		}
		for (A attribute : tmpAttribtues) {
			Set<Map<A, IValue>> newCombination = new HashSet<>();
			for (IValue value : attribute.getValueSpace().getValues()) {
				for (Map<A, IValue> comb : combination) {
					Map<A, IValue> newComb = new HashMap<>(comb);
					newComb.put(attribute, value);
					newCombination.add(newComb);
				}
			}
			combination = newCombination;
		}
		return combination;
	}

	/**
	 * Gets the values combination.
	 *
	 * @param <A>
	 *            the generic type
	 * @param attributes
	 *            the attributes
	 * @return the values combination
	 */
	public static <A extends IAttribute<? extends IValue>> Collection<Map<A, IValue>>
			getValuesCombination(@SuppressWarnings ("unchecked") final A... attributes) {
		return getValuesCombination(Arrays.asList(attributes));
	}

	/**
	 * Gets the i values.
	 *
	 * @param attribute
	 *            the attribute
	 * @param values
	 *            the values
	 * @return the i values
	 */
	public static Collection<IValue> getIValues(final IAttribute<? extends IValue> attribute, final String... values) {
		return Stream.of(values).map(val -> attribute.getValueSpace().getValue(val)).collect(Collectors.toSet());
	}

}
