/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.matrix.GamaObjectMatrix;

/**
 * The Class GamaPair.
 */
@vars({ @var(name = GamaPair.KEY, type = IType.NONE_STR),
	@var(name = GamaPair.VALUE, type = IType.NONE_STR) })
public class GamaPair<K, V> implements IGamaContainer<K, V> {

	public static final String KEY = "key";

	public static final String VALUE = "value";

	public Object key;

	public V value;

	private final PairIterator iterator = new PairIterator(this);

	public GamaPair(final Object k, final Object v) {
		// super(sim);
		key = k;
		value = (V) v;
	}

	@Override
	public void add(final V v, final Object param) {
		value = v;
	}

	@Override
	public void add(final K index, final V v, final Object param) {
		key = index;
		value = v;
	}

	@Override
	public boolean contains(final Object o) {
		return key == null ? o == null : key.equals(o);
	}

	public boolean equals(final GamaPair p) {
		return key.equals(p.key) && value.equals(p.value);
	}

	@Override
	public boolean equals(final Object a) {
		if ( a == null ) { return false; }
		if ( a instanceof GamaPair ) { return equals((GamaPair) a); }
		return false;
	}

	@Override
	public Object get(final Object index) {
		if ( contains(index) ) { return value; }
		return null;
	}

	@Override
	@getter(var = "key")
	public V first() {
		return (V) key;
	}

	@Override
	@getter(var = "value")
	public V last() {
		return value;
	}

	@Override
	public int length() {
		return 2;
	}

	@Override
	public V max() {
		if ( key instanceof Comparable ) { return ((Comparable) key).compareTo(value) > 0 ? (V) key
			: value; }
		return null;
	}

	@Override
	public V min() {
		if ( key instanceof Comparable ) { return ((Comparable) key).compareTo(value) < 0 ? (V) key
			: value; }
		return null;
	}

	@Override
	public Object product() {
		if ( key instanceof Number ) {
			double k = ((Number) key).doubleValue();
			if ( value instanceof Number ) {
				double v = ((Number) value).doubleValue();
				return k * v;
			}
			return 0d;
		}
		return 0d;
	}

	@Override
	public Object sum() {
		if ( key instanceof Number ) {
			double k = ((Number) key).doubleValue();
			if ( value instanceof Number ) {
				double v = ((Number) value).doubleValue();
				return k + v;
			}
			return 0d;
		}
		return 0d;
	}

	@Override
	public boolean isEmpty() {
		return key == null && value == null;
	}

	@Override
	public GamaList listValue(final IScope scope) {
		return GamaList.with(key, value);
	}

	@Override
	public GamaMap mapValue(final IScope scope) {
		final GamaMap result = new GamaMap();
		result.put(key, value);
		return result;
	}

	@Override
	public IMatrix matrixValue(final IScope scope) {
		return new GamaObjectMatrix(listValue(scope), true, null);
	}

	@Override
	// priority = IPriority.CAST)
	public IMatrix matrixValue(final IScope scope, final GamaPoint preferredSize) {
		return new GamaObjectMatrix(listValue(scope), true, preferredSize);
	}

	@Override
	public boolean removeAll(final IGamaContainer list) {
		for ( Object v : list ) {
			removeFirst(v);
		}
		return true;
	}

	@Override
	public boolean removeFirst(final Object v) {
		if ( v != null && v.equals(value) ) {
			value = null;
			return true;
		}
		return false;
	}

	@Override
	public Object removeAt(final Object index) {
		if ( index.equals(key) ) {
			Object v = value;
			value = null;
			return v;
		}
		return null;
	}

	@Override
	public IGamaContainer reverse() {
		return new GamaPair(value, key);
	}

	@Override
	public void put(final K index, final V value, final Object param) {
		add(index, value, null);
	}

	@Override
	// @operator(value = "string", can_be_const = true)
	public String stringValue() throws GamaRuntimeException {
		return Cast.asString(key) + "::" + Cast.asString(value);
	}

	@Override
	public String toGaml() {
		return "(" + Cast.toGaml(key) + ")" + "::" + "(" + Cast.toGaml(value) + ")";
	}

	@Override
	public String toJava() {
		return GamaPairType.class.getCanonicalName() + "opPair(" + Cast.toJava(key) + "," +
			Cast.toJava(value) + ")";
	}

	@Override
	public String toString() {
		return (key == null ? "nil" : key.toString()) + "::" +
			(value == null ? "nil" : value.toString());
	}

	@Override
	public IType type() {
		return Types.get(IType.PAIR);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.IGamaContainer#fillWith(java.lang.Object)
	 */
	@Override
	public void putAll(final Object v, final Object param) {
		// key = v;
		value = (V) v;
	}

	@Override
	public void clear() {
		key = null;
		value = null;

	}

	@Override
	public GamaPair copy() {
		return new GamaPair(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkIndex(java.lang.Object)
	 */
	@Override
	public boolean checkIndex(final Object index) {
		return index != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public boolean checkBounds(final K index, final boolean forAdding) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return true;
	}

	private static class PairIterator implements Iterator {

		int i = 0;
		GamaPair p;

		PairIterator(final GamaPair pair) {
			p = pair;
		}

		void reset() {
			i = 0;
		}

		@Override
		public boolean hasNext() {
			return i < 2;
		}

		@Override
		public Object next() {
			switch (i) {
				case 0:
					i++;
					return p.key;
				case 1:
					i++;
					return p.value;
				default:
					throw new NoSuchElementException("No more elements in pair");
			}
		}

		@Override
		public void remove() {}

	}

	@Override
	public Iterator iterator() {
		iterator.reset();
		return iterator;

		// return listValue(null).iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */
	@Override
	public void addAll(final IGamaContainer value, final Object param) throws GamaRuntimeException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */
	@Override
	public void addAll(final K index, final IGamaContainer value, final Object param)
		throws GamaRuntimeException {

	}

	/**
	 * @see msi.gama.interfaces.IGamaContainer#any()
	 */
	@Override
	public V any() {
		int i = GAMA.getRandom().between(0, 1);
		return i == 0 ? (V) key : value;
	}

}
