package msi.gama.util;

import static msi.gama.util.GamaMapFactory.createWithoutCasting;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.common.interfaces.ConsumerWithPruning;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.GamaMapType;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars ({ @variable (
		name = GamaMap.KEYS,
		type = IType.LIST,
		of = ITypeProvider.KEY_TYPE_AT_INDEX + 1,
		doc = { @doc ("Returns the list of keys of this map (in their order of insertion)") }),
		@variable (
				name = GamaMap.VALUES,
				type = IType.LIST,
				of = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				doc = { @doc ("Returns the list of values of this map (in their order of insertion)") }),
		@variable (
				name = GamaMap.PAIRS,
				type = IType.LIST,
				of = IType.PAIR,
				doc = { @doc ("Returns the list of pairs (key, value) that compose this map") }) })
@SuppressWarnings ("unchecked")

public interface IMap<K, V> extends Map<K, V>, IModifiableContainer<K, V, K, V>, IAddressableContainer<K, V, K, V> {

	interface IPairList<K, V> extends Set<Map.Entry<K, V>>, IList<Map.Entry<K, V>> {

		@Override
		default Spliterator<Entry<K, V>> spliterator() {
			return IList.super.spliterator();
		}

	}

	String KEYS = "keys";
	String VALUES = "values";
	String PAIRS = "pairs";

	@Override
	default String stringValue(final IScope scope) {
		return serialize(false);
	}

	/**
	 * Method add()
	 *
	 * @see msi.gama.util.IContainer#add(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	default void addValue(final IScope scope, final V v) {
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
	default void addValueAtIndex(final IScope scope, final Object index, final V value) {
		// Cf. discussion on mailing-list about making "add" a synonym of "put"
		// for maps
		// if ( !containsKey(index) ) {
		setValueAtIndex(scope, index, value);
		// }
	}

	/**
	 * Method addAll()
	 *
	 * @see msi.gama.util.IContainer#addAll(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	// AD July 2020: Addition of the index (see #2985)
	@Override
	default void addValues(final IScope scope, final Object index, final IContainer/* <?, GamaPair<K, V>> */ values) {
		// If an index is specified, we add only the last object
		if (index != null) {
			final Iterable list = values.iterable(scope);
			setValueAtIndex(scope, index, (V) Iterables.getLast(list));
		} else {
			for (final Object o : values.iterable(scope)) {
				addValue(scope, (V) o);
			}
		}
	}

	/**
	 * Method removeAt()
	 *
	 * @see msi.gama.util.IContainer#removeAt(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	default void removeIndex(final IScope scope, final Object index) {
		remove(index);
	}

	@Override
	default boolean containsKey(final IScope scope, final Object o) throws GamaRuntimeException {
		return containsKey(o);
	}

	@Override
	default int length(final IScope scope) {
		return size();
	}

	@Override
	default boolean isEmpty(final IScope scope) {
		return length(scope) == 0;
	}

	@Override
	default V anyValue(final IScope scope) {
		final int size = length(scope);
		if (size == 0) { return null; }
		final K key = scope.getRandom().oneOf(keySet());
		return get(key);
	}

	@Override
	default V firstValue(final IScope scope) throws GamaRuntimeException {
		if (length(scope) == 0) { return null; }
		return Iterators.get(values().iterator(), 0);
	}

	@Override
	default V lastValue(final IScope scope) throws GamaRuntimeException {
		if (length(scope) == 0) { return null; }
		return Iterators.getLast(values().iterator());
	}

	default V valueAt(final int index) {
		if (size() == 0) { return null; }
		return Iterators.get(values().iterator(), index);
	}

	/**
	 * Method removeAll()
	 *
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	default void removeValues(final IScope scope, final IContainer<?, ?> values) {
		// we suppose we have pairs
		for (final Object o : values.iterable(scope)) {
			removeValue(scope, o);
		}
	}

	/**
	 * Method checkBounds()
	 *
	 * @see msi.gama.util.IContainer#checkBounds(java.lang.Object, boolean)
	 */
	@Override
	default boolean checkBounds(final IScope scope, final Object index, final boolean forAdding) {
		return true;
	}

	/**
	 * Method removeIndexes()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#removeIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	default void removeIndexes(final IScope scope, final IContainer<?, ?> index) {
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
	default V buildValue(final IScope scope, final Object object) {
		// If we pass a pair to this method, but the content type is not a pair,
		// then it is is interpreted as a key + a value by addValue()
		if (object instanceof GamaPair) {
			if (!getGamlType().getContentType().isTranslatableInto(Types.PAIR)) { return (V) object; }
		}
		return (V) getGamlType().getContentType().cast(scope, object, null, false);
	}

	/**
	 * Method buildValues()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer,
	 *      msi.gaml.types.IContainerType)
	 */
	default IContainer<?, GamaPair<K, V>> buildValues(final IScope scope, final IContainer objects) {
		return GamaMapType.staticCast(scope, objects, getGamlType().getKeyType(), getGamlType().getContentType(),
				false);
	}

	/**
	 * Method buildIndex()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	default K buildIndex(final IScope scope, final Object object) {
		return (K) getGamlType().getKeyType().cast(scope, object, null, false);
	}

	@Override
	default java.lang.Iterable<V> iterable(final IScope scope) {
		return values();
	}

	@Override
	default V getFromIndicesList(final IScope scope, final IList<K> indices) throws GamaRuntimeException {
		if (indices == null || indices.isEmpty()) { return null; }
		return get(scope, indices.get(0));
		// We do not consider the case where multiple indices are used. Maybe
		// could be used in the
		// future to return a list of values ?
	}

	@Override
	default boolean contains(final IScope scope, final Object o) {
		// AD: see Issue 918 and #2772
		return /* containsKey(o) || */containsValue(o);
	}

	/**
	 * Returns the list of values by default (NOT the list of pairs) Method listValue()
	 *
	 * @see msi.gama.util.IContainer#listValue(msi.gama.runtime.IScope)
	 */
	@Override
	default IList<V> listValue(final IScope scope, final IType contentsType, final boolean copy) {
		if (!GamaType.requiresCasting(contentsType, getGamlType().getContentType())) {
			if (!copy) { return GamaListFactory.wrap(contentsType, values()); }
			return GamaListFactory.createWithoutCasting(contentsType, values());
		} else {
			return GamaListFactory.create(scope, contentsType, values());
		}
	}

	@Override
	default IMatrix matrixValue(final IScope scope, final IType contentsType, final boolean copy)
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
	default IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) throws GamaRuntimeException {
		return matrixValue(scope, contentsType, copy);
	}

	@Override
	default String serialize(final boolean includingBuiltIn) {
		return "map(" + getPairs().serialize(includingBuiltIn) + ")";
	}

	@Override
	default IMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy) {
		final boolean coerceKey = GamaType.requiresCasting(keyType, getGamlType().getKeyType());
		final boolean coerceValue = GamaType.requiresCasting(contentsType, getGamlType().getContentType());
		if (coerceKey || coerceValue) {
			final IMap result = GamaMapFactory.create(keyType, contentsType, size());
			for (final Map.Entry<K, V> entry : entrySet()) {
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

	@Override
	default IMap<K, V> copy(final IScope scope) {
		return createWithoutCasting((IType<K>) getGamlType().getKeyType(), (IType<V>) getGamlType().getContentType(),
				this, isOrdered());
	}

	/**
	 * Method put()
	 *
	 * @see msi.gama.util.IContainer#put(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object)
	 */
	@Override
	default void setValueAtIndex(final IScope scope, final Object index, final V value) {
		final K key = buildIndex(scope, index);
		final V val = buildValue(scope, value);
		put(key, val);
	}

	@operator (
			value = "reverse",
			can_be_const = true,
			type = IType.MAP,
			content_type = ITypeProvider.KEY_TYPE_AT_INDEX + 1,
			index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "Specialization of the reverse operator for maps. Reverses keys and values",
			comment = "",
			examples = { @example ("map<int,int> m <- [1::111,2::222, 3::333, 4::444];"), @example (
					value = "reverse(m)",
					equals = "map([111::1,222::2,333::3,444::4])") })

	@test ("map<int,int> m2 <- [1::111,2::222, 3::333, 4::444]; reverse(m2) = map([111::1,222::2,333::3,444::4])")

	@Override
	default IMap reverse(final IScope scope) {
		final IMap map = GamaMapFactory.create(getGamlType().getContentType(), getGamlType().getKeyType());
		for (final Map.Entry<K, V> entry : entrySet()) {
			map.put(entry.getValue(), entry.getKey());
		}
		return map;
	}

	/**
	 * Method removeAll()
	 *
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	default void removeAllOccurrencesOfValue(final IScope scope, final Object value) {
		values().removeIf((v) -> Objects.equal(value, v));
	}

	/**
	 * Method remove()
	 *
	 * @see msi.gama.util.IContainer#remove(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	default void removeValue(final IScope scope, final Object value) {
		// Dont know what to do... Removing the first pair with value = value ?
		final Collection<V> values = values();
		final Iterator<V> it = values.iterator();
		while (it.hasNext()) {
			final V v = it.next();
			if (Objects.equal(v, value)) {
				values.remove(v);
				return;
			}
		}
	}

	@getter ("keys")
	default IList<K> getKeys() {
		// See issue #2792. key can be used to modify the map...
		return GamaListFactory.<K> createWithoutCasting(getGamlType().getKeyType(), keySet());
	}

	@getter ("values")
	default IList<V> getValues() {
		return GamaListFactory.<V> wrap(getGamlType().getContentType(), values());
	}

	@Override
	default V get(final IScope scope, final K index) throws GamaRuntimeException {
		return get(index);
	}

	/**
	 * Method setAll()
	 *
	 * @see msi.gama.util.IContainer#setAll(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	default void setAllValues(final IScope scope, final V value) {
		replaceAll((k, v) -> value);
	}

	@getter ("pairs")
	default IPairList getPairs() {
		// FIXME: in the future, this method will be directly operating upon the
		// entry set (so as to
		// avoir duplications). See GamaPair
		final GamaPairList<K, V> pairs = new GamaPairList<>(this);
		forEach((key, value) -> pairs
				.add(new GamaPair<>(key, value, getGamlType().getKeyType(), getGamlType().getContentType())));
		return pairs;
	}

	default boolean forEachPair(final BiConsumerWithPruning<K, V> visitor) {
		final Iterator<Map.Entry<K, V>> it = entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<K, V> entry = it.next();
			if (!visitor.process(entry.getKey(), entry.getValue())) { return false; }
		}
		return true;
	}

	boolean isOrdered();

	default boolean forEachValue(final ConsumerWithPruning<? super V> visitor) {
		final Iterator<Map.Entry<K, V>> it = entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<K, ? extends V> entry = it.next();
			if (!visitor.process(entry.getValue())) { return false; }
		}
		return true;
	}

	default boolean forEachKey(final ConsumerWithPruning<K> visitor) {
		final Iterator<Map.Entry<K, V>> it = entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<K, V> entry = it.next();
			if (!visitor.process(entry.getKey())) { return false; }
		}
		return true;
	}

}