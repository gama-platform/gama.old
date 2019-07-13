/*******************************************************************************************************
 *
 * msi.gama.util.GamaMap.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.util.Arrays;

import com.google.common.base.Objects;

import msi.gama.common.interfaces.IAttributed.BiConsumerWithPruning;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaMap. Use GamaMapFactory to create GamaMaps
 */

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaMap<K, V> extends TOrderedHashMap<K, V> implements IMap<K, V> {

	public static final String KEYS = "keys";
	public static final String VALUES = "values";
	public static final String PAIRS = "pairs";

	IContainerType type;

	protected GamaMap(final int capacity, final IType key, final IType content) {
		super(capacity);
		type = Types.MAP.of(key, content);
	}

	//
	@Override
	public IContainerType getGamlType() {
		return type;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) { return true; }
		if (!(o instanceof IMap)) { return false; }
		return GamaMapFactory.equals(this, (IMap) o);
	}

	/**
	 * Method setAll()
	 *
	 * @see msi.gama.util.IContainer#setAll(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void setAllValues(final IScope scope, final V value) {
		// value is supposed to be correctly casted to V
		final V val = buildValue(scope, value);
		Arrays.fill(_values, val);
	}

	/**
	 * Method remove()
	 *
	 * @see msi.gama.util.IContainer#remove(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void removeValue(final IScope scope, final Object value) {
		// Dont know what to do... Removing the first pair with value = value ?
		final V[] values = _values;
		final int[] inserts = _indicesByInsertOrder;
		for (int i = 0; i <= _lastInsertOrderIndex; i++) {
			final int index = inserts[i];
			if (index == EMPTY) {
				continue;
			}
			if (Objects.equal(value, values[index])) {
				removeAt(index);
				return;
			}
		}

	}

	/**
	 * Method removeAll()
	 *
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void removeAllOccurrencesOfValue(final IScope scope, final Object value) {
		for (int i = 0; i < _size; i++) {
			if (Objects.equal(_values[i], value)) {
				removeAt(i);
			}
		}

	}

	@Override
	public V firstValue(final IScope scope) {
		return valueAt(0);
	}

	@Override
	public V lastValue(final IScope scope) {
		return valueAt(_size - 1);
	}

	@Override
	public int length(final IScope scope) {
		return _size;
	}

	@Override
	public GamaMap reverse(final IScope scope) {
		final GamaMap map = new GamaMap(size(), getGamlType().getContentType(), getGamlType().getKeyType());
		forEach((k, v) -> map.put(v, k));
		return map;
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return _size == 0;
	}

	protected IContainer<?, K> buildIndexes(final IScope scope, final IContainer value) {
		final IList<K> result = GamaListFactory.create(getGamlType().getContentType());
		for (final Object o : value.iterable(scope)) {
			result.add(buildIndex(scope, o));
		}
		return result;
	}

	@Override
	public boolean containsKey(final IScope scope, final Object o) throws GamaRuntimeException {
		return index(o) >= 0;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public boolean forEachPair(final BiConsumerWithPruning<K, V> procedure) {
		final Object[] keys = _set;
		final V[] values = _values;
		final int[] inserts = _indicesByInsertOrder;
		for (int i = 0; i <= _lastInsertOrderIndex; i++) {
			final int index = inserts[i];
			if (index == EMPTY) {
				continue;
			}
			if (keys[index] != FREE && keys[index] != REMOVED) {
				if (!procedure.process((K) keys[index], values[index])) { return false; }
			}
		}
		return true;

	}

}
