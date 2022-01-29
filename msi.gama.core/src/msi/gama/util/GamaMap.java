/*******************************************************************************************************
 *
 * GamaMap.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util;

import java.util.LinkedHashMap;

import msi.gama.runtime.IScope;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaMap. Use GamaMapFactory to create GamaMaps
 */

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaMap<K, V> extends LinkedHashMap<K, V> implements IMap<K, V> {

	/** The Constant KEYS. */
	public static final String KEYS = "keys";
	
	/** The Constant VALUES. */
	public static final String VALUES = "values";
	
	/** The Constant PAIRS. */
	public static final String PAIRS = "pairs";

	/** The type. */
	IContainerType type;

	/**
	 * Instantiates a new gama map.
	 *
	 * @param capacity the capacity
	 * @param key the key
	 * @param content the content
	 */
	protected GamaMap(final int capacity, final IType key, final IType content) {
		super(capacity);
		type = Types.MAP.of(key, content);
	}

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

	@Override
	public GamaMap reverse(final IScope scope) {
		final GamaMap map = new GamaMap(size(), getGamlType().getContentType(), getGamlType().getKeyType());
		forEach((k, v) -> map.put(v, k));
		return map;
	}

	/**
	 * Builds the indexes.
	 *
	 * @param scope the scope
	 * @param value the value
	 * @return the i container
	 */
	protected IContainer<?, K> buildIndexes(final IScope scope, final IContainer value) {
		final IList<K> result = GamaListFactory.create(getGamlType().getContentType());
		for (final Object o : value.iterable(scope)) {
			result.add(buildIndex(scope, o));
		}
		return result;
	}

	@Override
	public boolean isOrdered() {
		return true;
	}

}
