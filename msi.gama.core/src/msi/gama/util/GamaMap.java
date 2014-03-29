/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.util.*;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.*;
import msi.gaml.types.*;

/**
 * The Class GamaMap.
 */
@vars({ @var(name = GamaMap.KEYS, type = IType.LIST, of = ITypeProvider.FIRST_KEY_TYPE),
	@var(name = GamaMap.VALUES, type = IType.LIST, of = ITypeProvider.FIRST_CONTENT_TYPE),
	@var(name = GamaMap.PAIRS, type = IType.LIST, of = IType.PAIR) })
public class GamaMap<K, V> extends LinkedHashMap<K, V> implements IModifiableContainer<K, V, K, GamaPair<K, V>>,
	IAddressableContainer<K, V, K, V> {

	// @Override
	// public Set<Map.Entry<K, V>> entrySet() {
	// return super.entrySet();
	// }

	public static final String KEYS = "keys";
	public static final String VALUES = "values";
	public static final String PAIRS = "pairs";

	// private static final GamaMap.ToStringProcedure toStringProcedure = new ToStringProcedure();
	private static final GamaMap.ToMatrixProcedure toMatrixProcedure = new ToMatrixProcedure();
	private static final GamaMap.ToReverseProcedure toReverseProcedure = new ToReverseProcedure();
	public static final GamaMap EMPTY_MAP = new GamaMap();

	public static GamaMap with(final IList keys, final IList values) {
		final GamaMap result = new GamaMap(keys.size());
		for ( int i = 0, n = keys.size(); i < n; i++ ) {
			result.put(keys.get(i), values.get(i));
		}
		return result;
	}

	public GamaMap() {}

	public GamaMap(final int capacity) {
		super(capacity);
	}

	public GamaMap(final Map arg0) {
		this(arg0.size());
		putAll(arg0);
	}

	public GamaMap(final GamaPair<K, V> pair) {
		this(1);
		put(pair.key, pair.value);
	}

	/**
	 * Returns the list of values by default (NOT the list of pairs)
	 * Method listValue()
	 * @see msi.gama.util.IContainer#listValue(msi.gama.runtime.IScope)
	 */
	@Override
	public GamaList<V> listValue(final IScope scope, final IType contentsType) {
		// WARNING Double copy of the list !
		final GamaList<V> list = new GamaList(values()).listValue(scope, contentsType);
		return list;
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType) throws GamaRuntimeException {
		toMatrixProcedure.init(scope, size());
		for ( final Map.Entry entry : entrySet() ) {
			toMatrixProcedure.execute(scope, entry.getKey(), entry.getValue(), contentsType);
		}
		return toMatrixProcedure.matrix;
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize)
		throws GamaRuntimeException {
		return matrixValue(scope, contentsType);
	}

	@Override
	public String stringValue(final IScope scope) {
		return toGaml();
	}

	@Override
	public String toGaml() {
		return "(" + getPairs().toGaml() + " as map )";
	}

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType) {
		GamaMap result = new GamaMap();
		for ( Map.Entry<K, V> entry : super.entrySet() ) {
			result.put(GamaType.toType(scope, entry.getKey(), keyType),
				GamaType.toType(scope, entry.getValue(), contentsType));
		}

		return result;
	}

	public void add(final GamaPair<K, V> v) {
		put(v.getKey(), v.getValue());
	}

	@Override
	public V anyValue(final IScope scope) {
		if ( isEmpty() ) { return null; }
		final V[] array = (V[]) values().toArray();
		final int i = GAMA.getRandom().between(0, array.length - 1);
		return array[i];
	}

	/**
	 * Method add()
	 * @see msi.gama.util.IContainer#add(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void addValue(final IScope scope, final GamaPair<K, V> value) {
		// value is supposed to be a pair
		// if ( value instanceof GamaPair ) {
		this.add(value);
		// }
		// else ?
		// We dont convert anymore, as it would lose the information about the key type / content type
		// The ContainerStatements should handle this.
	}

	/**
	 * Method add()
	 * @see msi.gama.util.IContainer#add(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void addValueAtIndex(final IScope scope, final K index, final GamaPair<K, V> value) {
		if ( !containsKey(index) ) {
			put(index, value.value);
		}
	}

	/**
	 * Method put()
	 * @see msi.gama.util.IContainer#put(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setValueAtIndex(final IScope scope, final K index, final GamaPair<K, V> value) {
		put(index, value.value);
	}

	/**
	 * Method addAll()
	 * @see msi.gama.util.IContainer#addAll(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void addVallues(final IScope scope, final IContainer<?, GamaPair<K, V>> values) {
		if ( values instanceof GamaMap ) {
			putAll((GamaMap) values);
		} else {
			// values are supposed to be pairs
			for ( GamaPair<K, V> o : values.iterable(scope) ) {
				addValue(scope, o);
			}
		}
	}

	/**
	 * Method setAll()
	 * @see msi.gama.util.IContainer#setAll(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void setAllValues(final IScope scope, final GamaPair<K, V> value) {
		// value is supposed to be correctly casted to V
		for ( Map.Entry<K, V> entry : entrySet() ) {
			entry.setValue(value.value);
		}
	}

	/**
	 * Method remove()
	 * @see msi.gama.util.IContainer#remove(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void removeValue(final IScope scope, final Object value) {
		// Dont know what to do... Removing the first pair with value = value ?
		Iterator<Map.Entry<K, V>> it = entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<K, V> entry = it.next();
			boolean toRemove = value == null ? entry.getValue() == null : value.equals(entry.getValue());
			if ( toRemove ) {
				it.remove();
				return;
			}
		}
	}

	/**
	 * Method removeAt()
	 * @see msi.gama.util.IContainer#removeAt(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void removeIndex(final IScope scope, final Object index) {
		remove(index);
	}

	/**
	 * Method removeAll()
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeValues(final IScope scope, final IContainer<?, ?> values) {
		// we suppose we have pairs
		for ( Object o : values.iterable(scope) ) {
			removeValue(scope, o);
		}
	}

	/**
	 * Method removeAll()
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public void removeAllOccurencesOfValue(final IScope scope, final Object value) {
		Iterator<Map.Entry<K, V>> it = super.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<K, V> entry = it.next();
			if ( value == null ? entry.getValue() == null : value.equals(entry.getValue()) ) {
				it.remove();
			}
		}
	}

	//
	// @Override
	// public void add(final IScope scope, final K index, final Object value, final Object param, final boolean all,
	// final boolean add) {
	// if ( index == null ) {
	// if ( all ) {
	// if ( value instanceof GamaMap ) {
	// putAll((GamaMap) value);
	// } else if ( value instanceof IContainer ) {
	// for ( final Object o : ((IContainer) value).iterable(scope) ) {
	// add(scope, null, o, null, false, false);
	// }
	// } else {
	// for ( final Map.Entry e : entrySet() ) {
	// e.setValue(value);
	// }
	// }
	// } else {
	// // 08/01/14: Removal of this useless test (handled by Cast.asPair())
	// // if ( value instanceof GamaPair ) {
	// // final GamaPair<K, V> p = (GamaPair) value;
	// // // TODO Check type with class cast exception ?
	// // put(p.getKey(), p.getValue());
	// // } else
	// if ( value instanceof GamaMap ) {
	// putAll((GamaMap) value);
	// } else {
	// final GamaPair<K, V> p = Cast.asPair(scope, value);
	// put(p.getKey(), p.getValue());
	// }
	// }
	// } else {
	// // TODO Check type with class cast exception ?
	// put(index, (V) value);
	// }
	// }
	//
	// @Override
	// public void remove(final IScope scope, final Object index, final Object value, final boolean all) {
	// if ( index == null ) {
	// if ( all ) {
	// if ( value instanceof IContainer ) {
	// for ( final Object obj : ((IContainer) value).iterable(scope) ) {
	// remove(scope, null, obj, true);
	// }
	// } else if ( value != null ) {
	// remove(value);
	// } else {
	// clear();
	// }
	// } else {
	// remove(value);
	// }
	// } else {
	// remove(index);
	// }
	// }

	@Override
	public V firstValue(final IScope scope) {
		final Iterator<Map.Entry<K, V>> it = entrySet().iterator();
		final Map.Entry<K, V> entry = it.hasNext() ? it.next() : null;
		return entry == null ? null : entry.getValue();
	}

	public GamaPair getAtIndex(final Integer index) {
		if ( index >= size() ) { return null; }
		final List<Map.Entry<Object, Object>> list = new GamaList(entrySet());
		final Map.Entry entry = list.get(index);
		return entry == null ? null : new GamaPair(entry.getKey(), entry.getValue());

	}

	@Override
	public V lastValue(final IScope scope) {
		if ( size() == 0 ) { return null; }
		final List<Map.Entry<K, V>> list = new GamaList(entrySet());
		final Map.Entry<K, V> entry = list.get(list.size() - 1);
		return entry == null ? null : entry.getValue();
	}

	@Override
	public int length(final IScope scope) {
		return size();
	}

	@Override
	public boolean contains(final IScope scope, final Object o) {
		// AD: see Issue 918
		return /* containsKey(o) || */containsValue(o);
	}

	private static class ToReverseProcedure {

		private GamaMap map;

		public boolean execute(final Object a, final Object b) {
			map.put(b, a);
			return true;
		}

		private void init(final int size) {
			map = new GamaMap(size);
		}

	}

	public static class ToMatrixProcedure {

		public IMatrix matrix;
		private int i;

		public boolean execute(final IScope scope, final Object a, final Object b, final IType contentsType)
			throws GamaRuntimeException {
			matrix.set(scope, 0, i, GamaType.toType(scope, a, contentsType));
			matrix.set(scope, 1, i, GamaType.toType(scope, b, contentsType));
			i++;
			return true;
		}

		public void init(final IScope scope, final int size) {
			matrix = new GamaObjectMatrix(2, size);
			i = 0;
		}

	}

	// public static class ToStringProcedure {
	//
	// public String string;
	//
	// public boolean execute(final Object a, final Object b) {
	//
	// final StringBuilder res = new StringBuilder(50);
	// res.append(string);
	// res.append(a);
	// res.append(',');
	// res.append(b);
	// res.append("; ");
	// string = res.toString();
	// return true;
	// }
	//
	// }

	@Override
	public IContainer reverse(final IScope scope) {
		toReverseProcedure.init(size());
		for ( final Map.Entry<K, V> entry : entrySet() ) {
			toReverseProcedure.execute(entry.getKey(), entry.getValue());
		}
		return toReverseProcedure.map;
	}

	@getter("keys")
	public GamaList<K> getKeys() {
		return new GamaList(keySet());
	}

	@getter("values")
	public GamaList<V> getValues() {
		return new GamaList(values());
	}

	@getter("pairs")
	public IList getPairs() {
		// FIXME: in the future, this method will be directly operating upon the entry set (so as to
		// avoir duplications). See GamaPair
		final GamaPairList pairs = new GamaPairList();
		for ( final Map.Entry<K, V> entry : entrySet() ) {
			pairs.add(new GamaPair(entry));
		}
		return pairs;
	}

	@Override
	public GamaMap copy(final IScope scope) {
		final GamaMap result = new GamaMap(this);
		return result;
	}

	/**
	 * Returns an iterator that iterates on the list of VALUES (not GamaPairs anymore)
	 * @see java.lang.Iterable#iterator()
	 */
	// @Override
	// public Iterator<V> iterator() {
	// return values().iterator();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */

	@Override
	public V get(final IScope scope, final K index) throws GamaRuntimeException {
		return get(index);
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return isEmpty();
	}

	@Override
	public java.lang.Iterable<V> iterable(final IScope scope) {
		return values();
	}

	@Override
	public V getFromIndicesList(final IScope scope, final IList<K> indices) throws GamaRuntimeException {
		if ( indices == null || indices.isEmpty() ) { return null; }
		return get(scope, indices.get(0));
		// We do not consider the case where multiple indices are used. Maybe could be used in the
		// future to return a list of values ?
	}

	class GamaPairList extends GamaList<Map.Entry<K, V>> implements Set<Map.Entry<K, V>> {

	}

	/**
	 * Method checkBounds()
	 * @see msi.gama.util.IContainer#checkBounds(java.lang.Object, boolean)
	 */
	@Override
	public boolean checkBounds(final IScope scope, final Object index, final boolean forAdding) {
		return true;
	}

	/**
	 * Method removeIndexes()
	 * @see msi.gama.util.IContainer.Modifiable#removeIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, Object> index) {
		for ( Object key : index.iterable(scope) ) {
			remove(key);
		}
	}

	/**
	 * Method buildValue()
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public GamaPair<K, V> buildValue(final IScope scope, final Object object, final IContainerType containerType) {
		return GamaPairType.staticCast(scope, object, containerType.getKeyType(), containerType.getContentType());

	}

	/**
	 * Method buildValues()
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public IContainer<?, GamaPair<K, V>> buildValues(final IScope scope, final IContainer objects,
		final IContainerType containerType) {
		return GamaMapType.staticCast(scope, objects, containerType.getKeyType(), containerType.getContentType());
	}

	/**
	 * Method buildIndex()
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public K buildIndex(final IScope scope, final Object object, final IContainerType containerType) {
		return (K) containerType.getKeyType().cast(scope, object, null);
	}

	@Override
	public IContainer<?, K> buildIndexes(final IScope scope, final IContainer value, final IContainerType containerType) {
		IList<K> result = new GamaList();
		for ( Object o : value.iterable(scope) ) {
			result.add(buildIndex(scope, o, containerType));
		}
		return result;
	}

}
