/*******************************************************************************************************
 *
 * GamaMapFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class GamaMapFactory.
 *
 * @author drogoul
 * @since 1 févr. 2015
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaMapFactory {

	/** The Constant DEFAULT_SIZE. */
	private static final int DEFAULT_SIZE = 10;

	/**
	 * The Class GamaMapSupplier.
	 */
	public static class GamaMapSupplier implements Supplier<IMap> {

		/** The k. */
		IType k;

		/** The c. */
		IType c;

		/**
		 * Instantiates a new gama map supplier.
		 *
		 * @param key
		 *            the key
		 * @param contents
		 *            the contents
		 */
		public GamaMapSupplier(final IType key, final IType contents) {
			k = key;
			c = contents;
		}

		@Override
		public IMap get() {
			return create(k, c);
		}
	}

	/**
	 * Create a forwarding map that offers an IMap interface to a regular map
	 *
	 * @param <K>
	 * @param <V>
	 * @param key
	 * @param contents
	 * @param wrapped
	 * @return
	 */
	public static <K, V> IMap<K, V> wrap(final IType key, final IType contents, final Map<K, V> wrapped) {
		return new GamaMapWrapper(wrapped, key, contents, true);
	}

	/**
	 * Wrap.
	 *
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @param isOrdered
	 *            the is ordered
	 * @param wrapped
	 *            the wrapped
	 * @return the i map
	 */
	public static <K, V> IMap<K, V> wrap(final IType key, final IType contents, final boolean isOrdered,
			final Map<K, V> wrapped) {
		return new GamaMapWrapper(wrapped, key, contents, isOrdered);
	}

	/**
	 * Synchronized map.
	 *
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param target
	 *            the target
	 * @return the i map
	 */
	public static <K, V> IMap<K, V> synchronizedMap(final IMap<K, V> target) {
		final IType key = target.getGamlType().getKeyType();
		final IType contents = target.getGamlType().getContentType();
		final boolean isOrdered = target.isOrdered();
		return wrap(key, contents, isOrdered, Collections.synchronizedMap(target));
	}

	/**
	 * Synchronized map. Only operation that does not return a IMap
	 *
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param target
	 *            the target
	 * @return the i map
	 */
	public static <K, V> Map<K, V> synchronizedOrderedMap() {
		return Collections.synchronizedMap(create());
	}

	/**
	 * Concurrent map.
	 *
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @return the i map
	 */
	public static <K, V> IMap<K, V> concurrentMap() {
		return wrap(Types.NO_TYPE, Types.NO_TYPE, false, new ConcurrentHashMap<>());
	}

	/**
	 * Creates the.
	 *
	 * @return the i map
	 */
	public static IMap create() {
		return createOrdered();
	}

	/**
	 * Creates a new GamaMap object.
	 *
	 * @return the i map
	 */
	public static IMap createOrdered() {
		return new GamaMap(DEFAULT_SIZE, Types.NO_TYPE, Types.NO_TYPE);
		// return new GamaMapSimpleWrapper() {
		//
		// @Override
		// public boolean isOrdered() {
		// return true;
		// }
		//
		// @Override
		// protected Map delegate() {
		// return map;
		// }
		// };
	}

	/**
	 * Creates a new GamaMap object.
	 *
	 * @return the i map
	 */
	public static IMap createUnordered() {
		final Map map = new HashMap();
		return new GamaMapSimpleWrapper() {

			@Override
			public boolean isOrdered() { return false; }

			@Override
			protected Map delegate() {
				return map;
			}
		};
	}

	/**
	 * Creates a new GamaMap object.
	 *
	 * @return the i map
	 */
	public static IMap createSynchronizedUnordered() {
		final Map map = Collections.synchronizedMap(new HashMap());
		return new GamaMapSimpleWrapper() {

			@Override
			public boolean isOrdered() { return false; }

			@Override
			protected Map delegate() {
				return map;
			}
		};
	}

	/**
	 * Creates the.
	 *
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @return the i map
	 */
	public static IMap create(final IType key, final IType contents) {
		return create(key, contents, DEFAULT_SIZE);
	}

	/**
	 * Creates the.
	 *
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @param ordered
	 *            the ordered
	 * @return the i map
	 */
	public static IMap create(final IType key, final IType contents, final boolean ordered) {
		return create(key, contents, DEFAULT_SIZE, ordered);
	}

	/**
	 * Creates the.
	 *
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @param size
	 *            the size
	 * @return the i map
	 */
	public static IMap create(final IType key, final IType contents, final int size) {
		return create(key, contents, size, true);
	}

	/**
	 * Creates the.
	 *
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @param size
	 *            the size
	 * @param ordered
	 *            the ordered
	 * @return the i map
	 */
	public static IMap create(final IType key, final IType contents, final int size, final boolean ordered) {
		if (ordered) return new GamaMap<>(size, key, contents);
		return new GamaMapWrapper<>(new HashMap(size), key, contents, false);
	}

	/**
	 * @warning ***WARNING*** This operation can end up putting values of the wrong type into the map
	 * @return
	 */
	public static <K, V> IMap<K, V> createWithoutCasting(final IType<K> key, final IType<V> contents,
			final Map<K, V> map) {
		return createWithoutCasting(key, contents, map, true);
	}

	/**
	 * Creates a new GamaMap object.
	 *
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @param map
	 *            the map
	 * @param ordered
	 *            the ordered
	 * @return the i map< k, v>
	 */
	public static <K, V> IMap<K, V> createWithoutCasting(final IType<K> key, final IType<V> contents,
			final Map<K, V> map, final boolean ordered) {
		final IMap<K, V> result = create(key, contents, map.size(), ordered);
		result.putAll(map);
		return result;
	}

	/**
	 * Creates the.
	 *
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param scope
	 *            the scope
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @param map
	 *            the map
	 * @return the i map
	 */
	public static <K, V> IMap<K, V> create(final IScope scope, final IType<K> key, final IType<V> contents,
			final Map<K, V> map) {
		if (map == null || map.isEmpty()) return create(key, contents);
		final IMap<K, V> result = create(key, contents, map.size());
		map.forEach((k, v) -> result.setValueAtIndex(scope, k, v));
		return result;
	}

	/**
	 * Creates the.
	 *
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param scope
	 *            the scope
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @param map
	 *            the map
	 * @param ordered
	 *            the ordered
	 * @return the i map
	 */
	public static <K, V> IMap<K, V> create(final IScope scope, final IType<K> key, final IType<V> contents,
			final Map<K, V> map, final boolean ordered) {
		if (map == null || map.isEmpty()) return create(key, contents, ordered);
		final IMap<K, V> result = create(key, contents, map.size(), ordered);
		map.forEach((k, v) -> result.setValueAtIndex(scope, k, v));
		return result;
	}

	/**
	 * Creates the.
	 *
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param scope
	 *            the scope
	 * @param key
	 *            the key
	 * @param contents
	 *            the contents
	 * @param keys
	 *            the keys
	 * @param values
	 *            the values
	 * @return the i map
	 */
	public static <K, V> IMap<K, V> create(final IScope scope, final IType<K> key, final IType<V> contents,
			final IList<K> keys, final IList<V> values) {
		final IMap<K, V> result = create(key, contents, keys.length(scope));
		for (int i = 0; i < Math.min(keys.length(scope), values.length(scope)); i++) {
			result.put(keys.get(i), values.get(i));
		}
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param one
	 *            the one
	 * @param two
	 *            the two
	 * @return true, if successful
	 */
	public static boolean equals(final IMap one, final IMap two) {
		if (one.size() != two.size()) return false;
		return one.forEachPair((k1, v1) -> {
			if (!Objects.equals(v1, two.get(k1))) return false;
			return true;
		});
	}
}
