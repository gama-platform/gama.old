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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.*;
import msi.gaml.types.*;

/**
 * The Class GamaMap.
 */
@vars({ @var(name = GamaMap.KEYS, type = IType.LIST_STR),
	@var(name = GamaMap.VALUES, type = IType.LIST_STR),
	@var(name = GamaMap.PAIRS, type = IType.LIST_STR, of = IType.PAIR_STR) })
public class GamaMap extends LinkedHashMap implements IContainer {

	public static final String KEYS = "keys";

	public static final String VALUES = "values";

	public static final String PAIRS = "pairs";

	private static final GamaMap.ToStringProcedure toStringProcedure = new ToStringProcedure();
	private static final GamaMap.ToMatrixProcedure toMatrixProcedure = new ToMatrixProcedure();
	private static final GamaMap.ToReverseProcedure toReverseProcedure = new ToReverseProcedure();

	public static GamaMap with(final IList keys, final IList values) {
		GamaMap result = new GamaMap(keys.size());
		for ( int i = 0, n = keys.size(); i < n; i++ ) {
			result.put(keys.get(i), values.get(i));
		}
		return result;
	}

	public GamaMap() {
		// instances++;
		// OutputManager.debug("Maps created " + instances);
	}

	public GamaMap(final int capacity) {
		super(capacity);
		// instances++;
		// OutputManager.debug("Maps created " + instances);
	}

	public GamaMap(final Map arg0) {
		this(arg0.size());
		putAll(arg0);
	}

	@Override
	public GamaList<GamaPair> listValue(final IScope scope) {
		GamaList list = new GamaList();
		for ( Object key : keySet() ) {
			list.add(new GamaPair(key, this.get(key)));
		}
		return list;
	}

	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		toMatrixProcedure.init(size());
		for ( Map.Entry entry : entrySet() ) {
			toMatrixProcedure.execute(entry.getKey(), entry.getValue());
		}
		return toMatrixProcedure.matrix;
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize)
		throws GamaRuntimeException {
		return matrixValue(scope);
	}

	@Override
	public String stringValue() {
		toStringProcedure.string = "";
		for ( Map.Entry<Object, Object> entry : entrySet() ) {
			toStringProcedure.execute(entry.getKey(), entry.getValue());
		}
		return toStringProcedure.string;
	}

	@Override
	public String toGaml() {
		return "(" + listValue(null).toGaml() + " as map )";
	}

	//
	// @Override
	// public String toJava() {
	// final StringBuilder sb = new StringBuilder(size() * 10);
	// sb.append(GamaMapType.class.getCanonicalName()).append("from(new ")
	// .append(GamaList.class.getCanonicalName()).append('(');
	// int i = 0;
	// for ( Object o : keySet() ) {
	// if ( i != 0 ) {
	// sb.append(',');
	// }
	// sb.append(Cast.toJava(o)).append(Cast.toJava(get(o)));
	// i++;
	// }
	// sb.append("))");
	// return sb.toString();
	// }

	@Override
	public IType type() {
		return Types.get(IType.MAP);
	}

	@Override
	public GamaMap mapValue(final IScope scope) {
		return this;
	}

	@Override
	@operator(value = { IKeyword.AT }, can_be_const = true, type = IType.NONE)
	@doc(special_cases = "if it is a map, at returns the value corresponding the right operand as key. If the right operand is not a key of the map, at returns nil")
	public Object get(final Object index) {
		return super.get(index);
	}

	public void add(final GamaPair v) {
		put(v.first(), v.last());
	}

	@Override
	public Object any() {
		if ( isEmpty() ) { return null; }
		Object[] array = values().toArray();
		int i = GAMA.getRandom().between(0, array.length - 1);
		return array[i];
	}

	@Override
	public void add(final Object value, final Object param) {
		// Exception if value not GamaPair !
		add(null, value, param);
	}

	@Override
	public void add(final Object index, final Object value, final Object param) {
		if ( index == null && value instanceof GamaPair ) {
			add((GamaPair) value);
		} else {
			put(index, value);
		}
	}

	@Override
	public boolean removeFirst(final Object value) throws GamaRuntimeException {
		return remove(value) != null;
	}

	@Override
	public boolean removeAll(final IContainer list) throws GamaRuntimeException {
		for ( Object value : list ) {
			removeFirst(value);
		}
		return true;
	}

	@Override
	public Object removeAt(final Object index) {
		return remove(index);
	}

	@Override
	public void put(final Object index, final Object value, final Object param) {
		put(index, value);
	}

	@Override
	public Object first() {
		Iterator<Map.Entry<Object, Object>> it = entrySet().iterator();
		Map.Entry entry = it.hasNext() ? it.next() : null;
		return entry == null ? new GamaPair(null, null) : new GamaPair(entry.getKey(),
			entry.getValue());
	}

	@Override
	public Object last() {
		List<Map.Entry<Object, Object>> list = new GamaList(entrySet());
		Collections.reverse(list);
		Iterator<Map.Entry<Object, Object>> it = list.iterator();
		Map.Entry entry = it.hasNext() ? it.next() : null;
		return entry == null ? new GamaPair(null, null) : new GamaPair(entry.getKey(),
			entry.getValue());
	}

	@Override
	public void putAll(final Object value, final Object param) {
		for ( Map.Entry e : entrySet() ) {
			e.setValue(value);
		}
	}

	@Override
	public Object sum(final IScope scope) throws GamaRuntimeException {
		GamaList l = new GamaList(values());
		return l.sum(scope);
	}

	@Override
	public Object product(final IScope scope) throws GamaRuntimeException {
		GamaList l = new GamaList(values());
		return l.product(scope);
	}

	@Override
	public int length() {
		return size();
	}

	@Override
	public Object max(final IScope scope) throws GamaRuntimeException {
		GamaList l = new GamaList(values());
		return l.max(scope);
	}

	@Override
	public Object min(final IScope scope) throws GamaRuntimeException {
		GamaList l = new GamaList(values());
		return l.min(scope);
	}

	@Override
	public boolean contains(final Object o) {
		return containsKey(o);
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

		public boolean execute(final Object a, final Object b) throws GamaRuntimeException {
			matrix.set(0, i, a);
			matrix.set(1, i, b);
			i++;
			return true;
		}

		public void init(final int size) {
			matrix = new GamaObjectMatrix(2, size);
			i = 0;
		}

	}

	public static class ToStringProcedure {

		public String string;

		public boolean execute(final Object a, final Object b) {

			StringBuilder res = new StringBuilder(50);
			res.append(string);
			res.append(a);
			res.append(',');
			res.append(b);
			res.append("; ");
			string = res.toString();
			return true;
		}

	}

	@Override
	public IContainer reverse() {
		toReverseProcedure.init(size());
		for ( Map.Entry<Object, Object> entry : entrySet() ) {
			toReverseProcedure.execute(entry.getKey(), entry.getValue());
		}
		return toReverseProcedure.map;
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		return super.entrySet();
	}

	@getter("keys")
	public GamaList getKeys() {
		return new GamaList(keySet());
	}

	@getter("values")
	public GamaList getValues() {
		return new GamaList(values());
	}

	@getter("pairs")
	public GamaList<GamaPair> getPairs() {
		return listValue(null);
	}

	@Override
	public GamaMap copy() {
		GamaMap result = new GamaMap(this);
		return result;
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
	public boolean checkBounds(final Object index, final boolean forAdding) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return false;
	}

	/**
	 * Returns an iterator that iterates on the list of GamaPair
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator iterator() {
		return listValue(null).iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */
	@Override
	public void addAll(final IContainer value, final Object param) throws GamaRuntimeException {
		if ( value instanceof GamaMap ) {
			putAll((GamaMap) value);
		} else {
			for ( Object o : value ) {
				put(o, o);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */
	@Override
	public void addAll(final Object index, final IContainer value, final Object param)
		throws GamaRuntimeException {
		addAll(value, null);
	}

}
