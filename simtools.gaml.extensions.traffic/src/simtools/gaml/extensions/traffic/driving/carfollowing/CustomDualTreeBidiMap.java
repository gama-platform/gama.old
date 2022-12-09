/*******************************************************************************************************
 *
 * CustomDualTreeBidiMap.java, in simtools.gaml.extensions.traffic, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.driving.carfollowing;

import java.util.Comparator;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualTreeBidiMap;

/**
 * The Class CustomDualTreeBidiMap.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class CustomDualTreeBidiMap<K, V> extends DualTreeBidiMap<K, V> {
	
	/**
	 * Instantiates a new custom dual tree bidi map.
	 *
	 * @param normalMap the normal map
	 * @param reverseMap the reverse map
	 * @param inverseBidiMap the inverse bidi map
	 */
	protected CustomDualTreeBidiMap(final Map<K, V> normalMap, final Map<V, K> reverseMap,
			final BidiMap<V, K> inverseBidiMap) {
		super(normalMap, reverseMap, inverseBidiMap);
	}

	/**
	 * Instantiates a new custom dual tree bidi map.
	 *
	 * @param keyComparator the key comparator
	 * @param valueComparator the value comparator
	 */
	public CustomDualTreeBidiMap(final Comparator<? super K> keyComparator,
			final Comparator<? super V> valueComparator) {
		super(keyComparator, valueComparator);
	}

	@Override
	protected CustomDualTreeBidiMap<V, K> createBidiMap(final Map<V, K> normalMap, final Map<K, V> reverseMap,
			final BidiMap<K, V> inverseMap) {
		return new CustomDualTreeBidiMap<>(normalMap, reverseMap, inverseMap);
	}

	/**
	 * The original method in DualTreeBidiMap does not work correctly when the key is not present in the map.
	 */
	@Override
	public K nextKey(final K key) {
		if (containsKey(key) || size() == 0) return super.nextKey(key);
		K last = lastKey();
		if (comparator().compare(key, last) > 0)
			return null;
		else {
			K next = super.nextKey(key);
			return next == null ? last : previousKey(next);
		}
	}
}
