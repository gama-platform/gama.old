/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.expressions.*;

/**
 * Written by drogoul Modified on 31 juil. 2010
 * 
 * GAML operators dedicated to containers (list, matrix, graph, etc.)
 * 
 * @see also IMatrix, IGamaContainer for other operators
 * 
 */
public class Containers {

	// === OPERATORS THAT ALLOW SPECIES TO OPERATE AS CONTAINERS OF AGENTS
	// === i.e. allow to write 'first species' instead_of 'first list species'
	// TODO TO BE ENTIRELY CHANGED BY THE FACT THAT POPULATIONS ARE NOW THE WAY TO
	// ADDRESS GROUPS OF AGENTS WITH THE SAME SPECIES. POPULATIONS WILL BE RETURNED NOW WHEN WE USE
	// THE NAME OF THE SPECIES. AND POPULATIONS WILL BE CONTAINERS.

	@operator(value = "first", type = ITypeProvider.CHILD_CONTENT_TYPE, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static IAgent getFirst(final IScope scope, final ISpecies s) {
		if ( s == null ) { return null; }
		return scope.getAgentScope().getPopulationFor(s).getAgentsList().first();
	}

	@operator(value = "last", type = ITypeProvider.CHILD_CONTENT_TYPE, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static IAgent getLast(final IScope scope, final ISpecies s) {
		if ( s == null ) { return null; }
		return scope.getAgentScope().getPopulationFor(s).getAgentsList().last();
	}

	@operator(value = "length")
	public static Integer getLength(final IScope scope, final ISpecies s) {
		if ( s == null ) { return 0; }
		return scope.getAgentScope().getPopulationFor(s).size();
	}

	@operator(value = { "at", "@" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static IAgent getAgent(final IScope scope, final ISpecies s, final GamaPoint val) {
		return scope.getAgentScope().getPopulationFor(s).getAgent(val);
		// TODO Add maybe a method than can help localizing the agent of the species closest to a
		// point
	}

	@operator(value = { "at", "@" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static IAgent getAgent(final IScope scope, final ISpecies s, final Integer val) {
		return scope.getAgentScope().getPopulationFor(s).getAgent(val);
	}

	// =====
	// =====

	/** The results of sort. */
	final static Map<Object, Comparable> results = new HashMap<Object, Comparable>();

	final static GamaRuntimeException[] ex = new GamaRuntimeException[] { null };
	/** The comp. of sort */
	final static Comparator<Object> comp = new Comparator<Object>() {

		@Override
		public int compare(final Object a, final Object b) {
			final Comparable ca = results.get(a);
			final Comparable cb = results.get(b);
			if ( ca instanceof String ) { return ((String) ca).compareTo((String) cb); }
			Double aa, bb;
			aa = Cast.asFloat(null, ca);
			bb = Cast.asFloat(null, cb);
			return aa.compareTo(bb);

		}
	};

	@operator(value = "remove_duplicates", can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static GamaList asSet(final List l) {
		if ( l == null ) { return null; }
		final HashSet list = new HashSet(l);
		return new GamaList(list);
	}

	@operator(value = "contains_all", can_be_const = true)
	public static Boolean opContainsAll(final IContainer m, final IContainer l)
		throws GamaRuntimeException {
		if ( l == null || l.isEmpty() ) { return true; }
		for ( Object o : l ) {
			if ( !m.contains(o) ) { return false; }
		}
		return true;
	}

	@operator(value = "contains_any", can_be_const = true)
	public static Boolean opContainsAny(final IContainer m, final IContainer l)
		throws GamaRuntimeException {
		if ( l == null || l.isEmpty() ) { return false; }
		IContainer c = m;
		for ( Object o : l ) {
			if ( c.contains(o) ) { return true; }
		}
		return false;
	}

	@operator(value = { "copy_between", "copy" }, can_be_const = true, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static GamaList opCopy(final List l1, final GamaPoint p) {
		if ( p == null ) { return new GamaList(l1); }
		final int beginIndex = p.x < 0 ? 0 : (int) p.x;
		final int endIndex = p.y > l1.size() ? l1.size() : (int) p.y;
		if ( beginIndex > endIndex ) { return new GamaList(); }
		return new GamaList(l1.subList(beginIndex, endIndex));
	}

	@operator(value = "in", can_be_const = true)
	public static Boolean opIn(final Object o, final IContainer source)
		throws GamaRuntimeException {
		if ( source == null ) { return false; }
		return source.contains(o);
	}

	@operator(value = "index_of", can_be_const = true)
	public static Object opIndexOf(final GamaMap m, final Object o) {
		for ( Map.Entry<Object, Object> k : m.entrySet() ) {
			if ( k.getValue().equals(o) ) { return k; }
		}
		return null;
	}

	@operator(value = "index_of", can_be_const = true)
	public static GamaPoint opIndexOf(final IMatrix m, final Object o) {
		for ( int i = 0; i < m.getRows(); i++ ) {
			for ( int j = 0; j < m.getCols(); j++ ) {
				if ( m.get(i, j).equals(o) ) { return new GamaPoint(i, j); }
			}
		}
		return null;
	}

	@operator(value = "last_index_of", can_be_const = true)
	public static GamaPoint opLastIndexOf(final IMatrix m, final Object o) {
		for ( int i = m.getRows() - 1; i > -1; i-- ) {
			for ( int j = m.getCols() - 1; j > -1; j-- ) {
				if ( m.get(i, j).equals(o) ) { return new GamaPoint(i, j); }
			}
		}
		return null;
	}

	@operator(value = "index_of", can_be_const = true)
	public static Integer opIndexOf(final List l1, final Object o) {
		return l1.indexOf(o);
	}

	@operator(value = "inter", priority = IPriority.ADDITION, can_be_const = true, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static GamaList opInter(final List l1, final List l) {
		if ( l == null ) { return new GamaList(l1); }
		Set s = new HashSet(l1.size());
		s.addAll(l1);
		s.retainAll(l);
		return new GamaList(s);
	}

	@operator(value = "last_index_of", can_be_const = true)
	public static Object opLastIndexOf(final GamaMap m, final Object o) {
		return opIndexOf(m, o);
	}

	@operator(value = "last_index_of", can_be_const = true)
	public static Integer opLastIndexOf(final List l1, final Object o) {
		return l1.lastIndexOf(o);
	}

	@operator(value = Maths.MINUS, priority = IPriority.ADDITION, can_be_const = true, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static GamaList opMinus(final GamaList l1, final GamaList l) {
		if ( l == null || l.isEmpty() ) { return l1; }
		HashSet set = new HashSet(l1);
		if ( set.removeAll(l) ) { return new GamaList(set); }
		return l1;
	}

	@operator(value = Maths.MINUS, priority = IPriority.ADDITION, can_be_const = true, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static GamaList opMinus(final List l1, final Object l) {
		if ( l == null ) { return new GamaList(l1); }
		GamaList result = new GamaList(l1);
		result.remove(l);
		return result;
	}

	@operator(value = "of_generic_species", content_type = ITypeProvider.RIGHT_CONTENT_TYPE, priority = IPriority.CAST)
	public static List opOfGenericSpecies(final List agents, final ISpecies s) {
		return opOfSpecies(agents, s, true);
	}

	@operator(value = "of_species", content_type = ITypeProvider.RIGHT_CONTENT_TYPE, priority = IPriority.CAST)
	public static List opOfSpecies(final List agents, final ISpecies s) {
		return opOfSpecies(agents, s, false);
	}

	private static List opOfSpecies(final List agents, final ISpecies s, final boolean generic) {
		if ( s == null ) { return agents; }
		int n = agents.size();
		final GamaList result = new GamaList(n);
		for ( int i = 0; i < n; i++ ) {
			final Object be = agents.get(i);
			if ( be instanceof IAgent && ((IAgent) be).isInstanceOf(s, !generic) ) {
				result.add(be);
			}
		}
		return result;
	}

	@operator(value = { "::" }, priority = IPriority.TERNARY, can_be_const = true, type = IType.PAIR, content_type = ITypeProvider.RIGHT_TYPE)
	public static GamaPair opPair(final Object a, final Object b) {
		return new GamaPair(a, b);
	}

	@operator(value = Maths.PLUS, priority = IPriority.ADDITION, can_be_const = true, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static GamaList opPlus(final List l1, final List l) {
		if ( l == null ) { return new GamaList(l1); }
		GamaList result = new GamaList(l1.size() + l.size());
		result.addAll(l1);
		result.addAll(l);
		return result;
	}

	@operator(value = Maths.PLUS, priority = IPriority.ADDITION, can_be_const = true, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static GamaList opPlus(final List l1, final Object l) {
		if ( l == null ) { return new GamaList(l1); }
		GamaList result = new GamaList(l1);
		result.add(l);
		return result;
	}

	@operator(value = "union", priority = IPriority.ADDITION, can_be_const = true, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static GamaList opUnion(final List l1, final List l) {
		if ( l == null ) { return new GamaList(l1); }
		Set s = new HashSet(l.size() + l1.size());
		s.addAll(l1);
		s.addAll(l);
		return new GamaList(s);
	}

	// ITERATORS

	@operator(value = { "group_by" }, priority = IPriority.ITERATOR)
	public static GamaMap groupBy(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return new GamaMap(); }
		final GamaMap result = new GamaMap();
		for ( Object each : original ) {
			scope.setEach(each);
			Object key = filter.value(scope);
			if ( !result.containsKey(key) ) {
				result.put(key, new GamaList());
			}
			((GamaList) result.get(key)).add(each);
		}
		return result;
	}

	@operator(value = { "last_with" }, type = ITypeProvider.LEFT_CONTENT_TYPE, priority = IPriority.ITERATOR, iterator = true)
	public static Object last_with(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return null; }
		for ( Object each : original.reverse() ) {
			scope.setEach(each);
			if ( Cast.asBool(scope, filter.value(scope)) ) { return each; }
		}
		return null;
	}

	@operator(value = { "max_of" }, priority = IPriority.ITERATOR, type = ITypeProvider.RIGHT_TYPE, iterator = true)
	public static Object maxOf(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return filter.type().getDefault(); }
		if ( filter.type().id() == IType.INT ) {
			int max = Integer.MIN_VALUE;
			for ( Object each : original ) {
				scope.setEach(each);
				final int rv = Cast.asInt(scope, filter.value(scope));
				if ( rv > max ) {
					max = rv;
				}
			}
			return max;
		}
		double max = Double.MIN_VALUE;
		for ( Object each : original ) {
			scope.setEach(each);
			final double rv = Cast.asFloat(scope, filter.value(scope));
			if ( rv > max ) {
				max = rv;
			}
		}
		return max;
	}

	@operator(value = { "min_of" }, priority = IPriority.ITERATOR, type = ITypeProvider.RIGHT_TYPE, iterator = true)
	public static Object minOf(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return filter.type().getDefault(); }
		if ( filter.type().id() == IType.INT ) {
			int min = Integer.MAX_VALUE;
			for ( Object each : original ) {
				scope.setEach(each);
				final int rv = Cast.asInt(scope, filter.value(scope));
				if ( rv < min ) {
					min = rv;
				}
			}
			return min;
		}

		double min = Double.MAX_VALUE;
		for ( Object each : original ) {
			scope.setEach(each);
			final double rv = Cast.asFloat(scope, filter.value(scope));
			if ( rv < min ) {
				min = rv;
			}
		}
		return min;
	}

	@operator(value = "among", content_type = ITypeProvider.RIGHT_CONTENT_TYPE)
	public static GamaMap opAmong(final IScope scope, final Integer number, final GamaMap l)
		throws GamaRuntimeException {
		final GamaMap result = new GamaMap();
		if ( l == null ) { return result; }
		int size = l.size();
		if ( number == 0 ) { return result; }
		if ( number >= size ) { return l; }
		final List indexes = opAmong(scope, number, new GamaList(l.keySet()));
		for ( int i = 0; i < number; i++ ) {
			Object o = indexes.get(i);
			result.put(o, l.get(o));
		}
		return result;
	}

	@operator(value = "among", content_type = ITypeProvider.RIGHT_CONTENT_TYPE)
	public static List opAmong(final IScope scope, final Integer number, final IContainer c)
		throws GamaRuntimeException {
		if ( c == null ) { return new GamaList(); }
		final GamaList result = new GamaList();
		final GamaList l = c.listValue(scope);
		int size = l.size();
		if ( number == 0 ) { return result; }
		if ( number >= size ) { return l; }
		final List<Integer> indexes = new ArrayList(number);
		for ( int i = 0; i < number; i++ ) {
			int place = -1;
			do {
				place = GAMA.getRandom().between(0, size - 1);
			} while (indexes.contains(place));
			indexes.add(place);
		}
		for ( int i = 0; i < number; i++ ) {
			result.add(l.get(indexes.get(i)));
		}
		return result;
	}

	@operator(value = { "sort", "sort_by" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE, priority = IPriority.ITERATOR, iterator = true)
	public static GamaList sort(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return null; }
		final GamaList lv = original.listValue(scope).copy();
		// copy in order to prevent any side effect on the left member
		if ( lv.isEmpty() ) { return lv; }
		short fType = filter.type().id();
		boolean isComparable = fType == IType.STRING || fType == IType.INT || fType == IType.FLOAT;
		for ( int i = 0, n = lv.size(); i < n; i++ ) {
			Object each = lv.get(i);
			scope.setEach(each);
			final Object rv = filter.value(scope);
			if ( isComparable || rv instanceof Comparable ) {
				isComparable = true;
				results.put(each, (Comparable) rv);
			} else {
				results.put(each, Cast.asFloat(null, rv));
			}
		}
		Collections.sort(lv, comp);
		GamaRuntimeException e = ex[0];
		if ( e != null ) {
			ex[0] = null;
			throw e;
		}
		results.clear();
		return lv;
	}

	@operator(value = { "where", "select" }, priority = IPriority.ITERATOR, iterator = true)
	public static GamaMap where(final IScope scope, final GamaMap original, final IExpression filter)
		throws GamaRuntimeException {
		if ( original == null ) { return new GamaMap(); }
		final GamaMap result = new GamaMap();
		for ( Object p : original.listValue(scope) ) {
			scope.setEach(p);
			if ( Cast.asBool(scope, filter.value(scope)) ) {
				result.add((GamaPair) p);
			}
		}
		return result;
	}

	@operator(value = { "where", "select" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE, priority = IPriority.ITERATOR, iterator = true)
	public static List where(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return GamaList.EMPTY_LIST; }
		final GamaList result = new GamaList(original.length());
		for ( Object each : original ) {
			scope.setEach(each);
			if ( Cast.asBool(scope, filter.value(scope)) ) {
				result.add(each);
			}
		}
		return result;
	}

	@operator(value = { "with_max_of" }, type = ITypeProvider.LEFT_CONTENT_TYPE, priority = IPriority.ITERATOR, iterator = true)
	public static Object withMaxOf(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return filter.type().getDefault(); }
		double max = Double.MIN_VALUE;
		Object result = null;
		for ( Object each : original ) {
			scope.setEach(each);
			final double rv = Cast.asFloat(scope, filter.value(scope));
			if ( rv > max ) {
				max = rv;
				result = each;
			}
		}
		return result;
	}

	@operator(value = { "with_min_of" }, type = ITypeProvider.LEFT_CONTENT_TYPE, priority = IPriority.ITERATOR, iterator = true)
	public static Object withMinOf(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return null; }
		double min = Double.MAX_VALUE;
		Object result = null;
		for ( Object each : original ) {
			scope.setEach(each);
			final double rv = Cast.asFloat(scope, filter.value(scope));
			if ( rv < min ) {
				min = rv;
				result = each;
			}
		}
		return result;
	}

	@operator(value = { "accumulate" }, content_type = ITypeProvider.RIGHT_CONTENT_TYPE, priority = IPriority.ITERATOR, iterator = true)
	public static GamaList accumulate(final IScope scope, final List original,
		final IExpression filter) throws GamaRuntimeException {
		final GamaList result = new GamaList();
		if ( original == null ) { return result; }
		for ( int i = 0, n = original.size(); i < n; i++ ) {
			scope.setEach(original.get(i));
			final Object values = filter.value(scope);
			if ( values instanceof GamaList ) {
				result.addAll((GamaList) values);
			} else {
				result.add(values);
			}
		}
		return result;
	}

	@operator(value = "collate", content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static GamaList interleave(final IScope scope, final List original) {

		final GamaList result = new GamaList();
		if ( original == null ) { return result; }
		int n = original.size();
		final int[] sizeArray = new int[n];
		final boolean[] isListArray = new boolean[n];
		int maxSize = 0;
		for ( int i = 0; i < n; i++ ) {
			final Object values = original.get(i);
			isListArray[i] = values instanceof GamaList;
			int size = isListArray[i] ? ((GamaList) values).size() : 0;
			if ( size > maxSize ) {
				maxSize = size;
			}
			sizeArray[i] = size;
		}

		for ( int index = 0; index < maxSize; index++ ) {
			for ( int i = 0; i < n; i++ ) {
				final Object values = original.get(i);
				if ( isListArray[i] ) {
					int size = sizeArray[i];
					if ( index < size ) {
						result.add(((GamaList) values).get(index));
					}
				} else {
					if ( index == 0 ) {
						result.add(values);
					}
				}
			}
		}
		return result;
	}

	@operator(value = { "count" }, priority = IPriority.ITERATOR, iterator = true)
	public static Integer count(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return 0; }
		Integer result = 0;
		for ( Object each : original ) {
			scope.setEach(each);
			if ( Cast.asBool(scope, filter.value(scope)) ) {
				result++;
			}
		}
		return result;
	}

	@operator(value = { "first_with" }, type = ITypeProvider.LEFT_CONTENT_TYPE, priority = IPriority.ITERATOR, iterator = true)
	public static Object first_with(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return null; }
		for ( Object each : original ) {
			scope.setEach(each);
			if ( Cast.asBool(scope, filter.value(scope)) ) { return each; }
		}
		return null;
	}

	@operator(value = { "as_map" }, priority = IPriority.ITERATOR, iterator = true)
	public static GamaMap asMap(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		final GamaMap result = new GamaMap();
		if ( original == null || original.isEmpty() ) { return result; }
		GamaPair<IExpression, IExpression> p;
		if ( filter instanceof MapExpression ) {
			MapExpression exp = (MapExpression) filter;
			p = new GamaPair(exp.keysArray()[0], exp.valuesArray()[0]);
		} else if ( filter instanceof BinaryOperator &&
			((BinaryOperator) filter).getName().equals("::") ) {
			p = new GamaPair(((BinaryOperator) filter).left(), ((BinaryOperator) filter).right());
		} else {
			throw new GamaRuntimeException(
				"The as_map operator expects either a pair or a map for its second argument");
		}
		for ( Object each : original ) {
			scope.setEach(each);
			result.put(((IExpression) p.key).value(scope), p.value.value(scope));
		}
		return result;
	}

	@operator(value = { "collect" }, content_type = ITypeProvider.RIGHT_TYPE, priority = IPriority.ITERATOR, iterator = true)
	public static List collect(final IScope scope, final List original, final IExpression filter)
		throws GamaRuntimeException {
		if ( original == null ) { return GamaList.EMPTY_LIST; }
		int size = original.size();
		final Object[] result = new Object[size];
		for ( int i = 0; i < size; i++ ) {
			scope.setEach(original.get(i));
			result[i] = filter.value(scope);
		}
		return new GamaList(result);
	}

}
