/**
 * Created by drogoul, 1 févr. 2015
 * 
 */
package msi.gama.util;

import java.util.Map;

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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaMapFactory {

	private static final int DEFAULT_SIZE = 10;

	public static GamaMap create() {
		return create(Types.NO_TYPE, Types.NO_TYPE);
	}

	public static GamaMap create(final IType key, final IType contents) {
		return create(key, contents, DEFAULT_SIZE);
	}

	public static GamaMap create(final IType key, final IType contents, final int size) {
		return new GamaMap(size, key, contents);
	}

	/**
	 * @warning ***WARNING*** This operation can end up putting values of the
	 *          wrong type into the map
	 * @return
	 */
	public static <K, V> GamaMap<K, V> createWithoutCasting(final IType key, final IType contents,
			final Map<K, V> map) {
		final GamaMap<K, V> result = create(key, contents, map.size());
		result.putAll(map);
		return result;
	}

	public static <K, V> GamaMap<K, V> create(final IScope scope, final IType key, final IType contents,
			final Map<K, V> map) {
		final GamaMap<K, V> result = create(key, contents, map.size());
		for (final Map.Entry<K, V> entry : map.entrySet()) {
			result.setValueAtIndex(scope, entry.getKey(), entry.getValue());
		}
		return result;
	}
}
