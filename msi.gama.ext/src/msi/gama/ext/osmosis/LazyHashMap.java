// This software is released into the Public Domain. See copying.txt for details.
package msi.gama.ext.osmosis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This map uses a HashMap internally, but only instantiates it after values are added. This is intended for cases where
 * most instances of the map will remain empty and avoids the instantiation overhead of creating the full map.
 *
 * @author Brett Henderson
 *
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 */
public class LazyHashMap<K, V> implements Map<K, V> {

	private Map<K, V> internalMap;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		if (internalMap != null) {
			internalMap.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsKey(final Object key) {
		if (internalMap != null) {
			return internalMap.containsKey(key);
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsValue(final Object value) {
		if (internalMap != null) {
			return internalMap.containsValue(value);
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		if (internalMap != null) {
			return internalMap.entrySet();
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V get(final Object key) {
		if (internalMap != null) {
			return internalMap.get(key);
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		if (internalMap != null) {
			return internalMap.isEmpty();
		} else {
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<K> keySet() {
		if (internalMap != null) {
			return internalMap.keySet();
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V put(final K key, final V value) {
		if (internalMap == null) {
			internalMap = new HashMap<>();
		}

		return internalMap.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		if (internalMap == null) {
			internalMap = new HashMap<>();
		}

		internalMap.putAll(m);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V remove(final Object key) {
		if (internalMap != null) {
			return internalMap.remove(key);
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		if (internalMap != null) {
			return internalMap.size();
		} else {
			return 0;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<V> values() {
		if (internalMap != null) {
			return internalMap.values();
		} else {
			return Collections.<K, V> emptyMap().values();
		}
	}
}
