/*******************************************************************************************************
 *
 * msi.gama.util.GamaMapFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.util.Map;
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

	private static final int DEFAULT_SIZE = 10;

	public static class GamaMapSupplier implements Supplier<IMap> {
		IType k;
		IType c;

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
		return new GamaMapWrapper(wrapped, key, contents);
	}

	public static IMap create() {
		return create(Types.NO_TYPE, Types.NO_TYPE);
	}

	public static IMap create(final IType key, final IType contents) {
		return create(key, contents, DEFAULT_SIZE);
	}

	public static IMap create(final IType key, final IType contents, final int size) {
		return new GamaMap(size, key, contents);
	}

	/**
	 * @warning ***WARNING*** This operation can end up putting values of the wrong type into the map
	 * @return
	 */
	public static <K, V> IMap<K, V> createWithoutCasting(final IType<K> key, final IType<V> contents,
			final Map<K, V> map) {
		final IMap<K, V> result = create(key, contents, map.size());
		result.putAll(map);
		return result;
	}

	public static <K, V> IMap<K, V> create(final IScope scope, final IType<K> key, final IType<V> contents,
			final Map<K, V> map) {
		if (map == null || map.isEmpty()) { return create(key, contents); }
		final IMap<K, V> result = create(key, contents, map.size());
		map.forEach((k, v) -> result.setValueAtIndex(scope, k, v));
		return result;
	}

	public static <K, V> IMap<K, V> create(final IScope scope, final IType<K> key, final IType<V> contents,
			final IList<K> keys, final IList<V> values) {
		final IMap<K, V> result = create(key, contents, keys.length(scope));
		for (int i = 0; i < Math.min(keys.length(scope), values.length(scope)); i++) {
			result.put(keys.get(i), values.get(i));
		}
		return result;
	}
}
