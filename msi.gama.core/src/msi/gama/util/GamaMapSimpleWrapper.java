package msi.gama.util;

import java.util.Map;

import com.google.common.collect.ForwardingMap;

import msi.gama.runtime.IScope;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@SuppressWarnings ("unchecked")
public abstract class GamaMapSimpleWrapper<K, V> extends ForwardingMap<K, V> implements IMap<K, V> {

	@Override
	public boolean equals(final Object o) {
		if (o == this) { return true; }
		if (!(o instanceof IMap)) { return false; }
		return GamaMapFactory.equals(this, (IMap) o);
	}

	@Override
	public IContainerType<?> getGamlType() {
		return Types.MAP;
	}

	/**
	 * Method buildValue()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public V buildValue(final IScope scope, final Object object) {
		return (V) object;
	}

	/**
	 * Method buildValues()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public IContainer<?, GamaPair<K, V>> buildValues(final IScope scope, final IContainer objects) {
		return objects;
	}

	/**
	 * Method buildIndex()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public K buildIndex(final IScope scope, final Object object) {
		return (K) object;
	}

	@Override
	public IList<K> getKeys() {
		return GamaListFactory.<K> wrap(Types.NO_TYPE, keySet());
	}

	@Override
	public IList<V> getValues() {
		return GamaListFactory.<V> wrap(Types.NO_TYPE, values());
	}

	@Override
	public IPairList getPairs() {
		// FIXME: in the future, this method will be directly operating upon the
		// entry set (so as to
		// avoir duplications). See GamaPair
		final GamaPairList<K, V> pairs = new GamaPairList<>(this);
		forEach((key, value) -> pairs.add(new GamaPair<>(key, value, Types.NO_TYPE, Types.NO_TYPE)));
		return pairs;
	}

	@Override
	public IList<V> listValue(final IScope scope, final IType contentsType, final boolean copy) {
		if (!copy) { return GamaListFactory.wrap(contentsType, values()); }
		return GamaListFactory.create(scope, contentsType, values());
	}

	@Override
	public IMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy) {

		final IMap result = GamaMapFactory.create(keyType, contentsType, size());
		for (final Map.Entry<K, V> entry : entrySet()) {
			result.put(result.buildIndex(scope, entry.getKey()), result.buildValue(scope, entry.getValue()));
		}
		return result;

	}

	@Override
	public IMap reverse(final IScope scope) {
		final IMap map = isOrdered() ? GamaMapFactory.createOrdered() : GamaMapFactory.createUnordered();
		for (final Map.Entry<K, V> entry : entrySet()) {
			map.put(entry.getValue(), entry.getKey());
		}
		return map;
	}

}
