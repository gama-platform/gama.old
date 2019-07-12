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
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;

import msi.gama.common.interfaces.IAttributed.BiConsumerWithPruning;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.GamaMapType;
import msi.gaml.types.GamaType;
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
	public boolean equals(final Object other) {
		if (!(other instanceof GamaMap)) { return false; }
		final GamaMap that = (GamaMap) other;
		if (this == that) { return true; }
		return super.equals(other);
	}

	/**
	 * Returns the list of values by default (NOT the list of pairs) Method listValue()
	 *
	 * @see msi.gama.util.IContainer#listValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IList<V> listValue(final IScope scope, final IType contentsType, final boolean copy) {
		if (!GamaType.requiresCasting(contentsType, type.getContentType())) {
			if (!copy) { return GamaListFactory.wrap(contentsType, values()); }
			return GamaListFactory.createWithoutCasting(contentsType, values());
		} else {
			return GamaListFactory.create(scope, contentsType, values());
		}
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		// No attempt to coerce the contentsType, as both keys and values should
		// be in the same matrix
		final GamaObjectMatrix matrix = new GamaObjectMatrix(2, size(), contentsType);
		int i = 0;
		for (final Map.Entry entry : entrySet()) {
			matrix.set(scope, 0, i, GamaType.toType(scope, entry.getKey(), contentsType, false));
			matrix.set(scope, 1, i, GamaType.toType(scope, entry.getValue(), contentsType, false));
			i++;
		}
		return matrix;
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) throws GamaRuntimeException {
		return matrixValue(scope, contentsType, copy);
	}

	@Override
	public String stringValue(final IScope scope) {
		return serialize(false);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "map(" + getPairs().serialize(includingBuiltIn) + ")";
	}

	@Override
	public IMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy) {
		final boolean coerceKey = GamaType.requiresCasting(keyType, type.getKeyType());
		final boolean coerceValue = GamaType.requiresCasting(contentsType, type.getContentType());
		if (coerceKey || coerceValue) {
			final IMap result = GamaMapFactory.create(keyType, contentsType, size());
			for (final Map.Entry<K, V> entry : super.entrySet()) {
				result.put(coerceKey ? result.buildIndex(scope, entry.getKey()) : entry.getKey(),
						coerceValue ? result.buildValue(scope, entry.getValue()) : entry.getValue());
			}
			return result;
		} else {
			if (copy) {
				final IMap result = copy(scope);
				return result;
			} else {
				return this;
			}
		}

	}

	/**
	 * Method add()
	 *
	 * @see msi.gama.util.IContainer#add(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void addValue(final IScope scope, final V v) {
		if (v instanceof GamaPair) {
			setValueAtIndex(scope, (K) ((GamaPair) v).key, (V) ((GamaPair) v).value);
		} else {
			setValueAtIndex(scope, v, v);
		}
	}

	/**
	 * Method add()
	 *
	 * @see msi.gama.util.IContainer#add(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void addValueAtIndex(final IScope scope, final Object index, final V value) {
		// Cf. discussion on mailing-list about making "add" a synonym of "put"
		// for maps
		// if ( !containsKey(index) ) {
		setValueAtIndex(scope, index, value);
		// }
	}

	/**
	 * Method put()
	 *
	 * @see msi.gama.util.IContainer#put(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setValueAtIndex(final IScope scope, final Object index, final V value) {
		final K key = buildIndex(scope, index);
		final V val = buildValue(scope, value);
		super.put(key, val);
	}

	/**
	 * Method addAll()
	 *
	 * @see msi.gama.util.IContainer#addAll(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void addValues(final IScope scope, final IContainer/* <?, GamaPair<K, V>> */ values) {
		for (final Object o : values.iterable(scope)) {
			addValue(scope, (V) o);
		}
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
	 * Method removeAt()
	 *
	 * @see msi.gama.util.IContainer#removeAt(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void removeIndex(final IScope scope, final Object index) {
		remove(index);
	}

	/**
	 * Method removeAll()
	 *
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeValues(final IScope scope, final IContainer<?, ?> values) {
		// we suppose we have pairs
		for (final Object o : values.iterable(scope)) {
			removeValue(scope, o);
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
	public boolean contains(final IScope scope, final Object o) {
		// AD: see Issue 918 and #2772
		return /* containsKey(o) || */containsValue(o);
	}

	@Override
	public GamaMap reverse(final IScope scope) {
		final GamaMap map = new GamaMap(size(), getGamlType().getContentType(), getGamlType().getKeyType());
		forEach((k, v) -> map.put(v, k));
		return map;
	}

	@Override
	@getter ("keys")
	public IList<K> getKeys() {
		return GamaListFactory.<K> wrap(getGamlType().getKeyType(), keySet());
	}

	@Override
	@getter ("values")
	public IList<V> getValues() {
		return GamaListFactory.<V> wrap(getGamlType().getContentType(), values());
	}

	@Override
	@getter ("pairs")
	public IPairList getPairs() {
		// FIXME: in the future, this method will be directly operating upon the
		// entry set (so as to
		// avoir duplications). See GamaPair
		final GamaPairList pairs = new GamaPairList(this);
		forEachEntry((key, value) -> pairs.add(new GamaPair(key, value, type.getKeyType(), type.getContentType())));
		return pairs;
	}

	@Override
	public IMap copy(final IScope scope) {
		return GamaMapFactory.createWithoutCasting(getGamlType().getKeyType(), getGamlType().getContentType(), this);
	}

	@Override
	public V get(final IScope scope, final K index) throws GamaRuntimeException {
		return get(index);
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return _size == 0;
	}

	@Override
	public java.lang.Iterable<V> iterable(final IScope scope) {
		return values();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return super.entrySet();
	}

	@Override
	public V getFromIndicesList(final IScope scope, final IList<K> indices) throws GamaRuntimeException {
		if (indices == null || indices.isEmpty()) { return null; }
		return get(scope, indices.get(0));
		// We do not consider the case where multiple indices are used. Maybe
		// could be used in the
		// future to return a list of values ?
	}

	/**
	 * Method checkBounds()
	 *
	 * @see msi.gama.util.IContainer#checkBounds(java.lang.Object, boolean)
	 */
	@Override
	public boolean checkBounds(final IScope scope, final Object index, final boolean forAdding) {
		return true;
	}

	/**
	 * Method removeIndexes()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#removeIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, ?> index) {
		for (final Object key : index.iterable(scope)) {
			remove(key);
		}
	}

	/**
	 * Method buildValue()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public V buildValue(final IScope scope, final Object object) {
		// If we pass a pair to this method, but the content type is not a pair,
		// then it is is interpreted as a key + a value by addValue()
		if (object instanceof GamaPair) {
			if (!type.getContentType().isTranslatableInto(Types.PAIR)) { return (V) object; }
		}
		return (V) type.getContentType().cast(scope, object, null, false);

		// GamaPairType.staticCast(scope, object, type.getKeyType(),
		// type.getContentType(), false);

	}

	/**
	 * Method buildValues()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public IContainer<?, GamaPair<K, V>> buildValues(final IScope scope, final IContainer objects) {
		return GamaMapType.staticCast(scope, objects, type.getKeyType(), type.getContentType(), false);
	}

	/**
	 * Method buildIndex()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public K buildIndex(final IScope scope, final Object object) {
		return (K) type.getKeyType().cast(scope, object, null, false);
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
	public void forEachPair(final BiConsumerWithPruning<K, V> procedure) {
		final Object[] keys = _set;
		final V[] values = _values;
		final int[] inserts = _indicesByInsertOrder;
		for (int i = 0; i <= _lastInsertOrderIndex; i++) {
			final int index = inserts[i];
			if (index == EMPTY) {
				continue;
			}
			if (keys[index] != FREE && keys[index] != REMOVED) {
				if (!procedure.process((K) keys[index], values[index])) { return; }
			}
		}

	}

}
