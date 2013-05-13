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
package msi.gaml.operators;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

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

	// @operator(value = "first", type = ITypeProvider.FIRST_CONTENT_TYPE, content_type =
	// ITypeProvider.FIRST_CONTENT_TYPE)
	// @doc(deprecated =
	// "The use of first on a species is deprecated, please use it one a population instead (list(species_name) instead of species_name)")
	// public static IAgent getFirst(final IScope scope, final ISpecies s) throws
	// GamaRuntimeException {
	// if ( s == null ) { return null; }
	// return s.first(scope);
	// }
	//
	// @operator(value = "last", type = ITypeProvider.FIRST_CONTENT_TYPE, content_type =
	// ITypeProvider.FIRST_CONTENT_TYPE)
	// @doc(deprecated =
	// "The use of last on a species is deprecated, please use it one a population instead (list(species_name) instead of species_name)")
	// public static IAgent getLast(final IScope scope, final ISpecies s) throws
	// GamaRuntimeException {
	// if ( s == null ) { return null; }
	// return s.last(scope);
	// }
	//
	// @operator(value = "length")
	// @doc(deprecated =
	// "The use of length on a species is deprecated, please use it one a population instead (list(species_name) instead of species_name)")
	// public static Integer getLength(final IScope scope, final ISpecies s)
	// throws GamaRuntimeException {
	// if ( s == null ) { return 0; }
	// return scope.getAgentScope().getPopulationFor(s).size();
	// }
	//

	@operator(value = { "internal_at" }, content_type = IType.NONE)
	@doc("For internal use only. Corresponds to the implementation of the access to containers with [index]")
	public static Object internal_at(IScope scope, IShape shape, IList indices)
		throws GamaRuntimeException {
		// TODO How to test if the index is correct ?
		if ( shape == null ) { return null; }
		GamaMap map = shape.getAttributes();
		if ( map == null ) { return null; }
		return map.getFromIndicesList(scope, indices);
	}

	@operator(value = { IKeyword.AT, "@" }, type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(deprecated = "The use of at on a species is deprecated, please use it one a population instead (list(species_name) instead of species_name)")
	public static IAgent at(final IScope scope, final ISpecies s, final GamaPoint val)
		throws GamaRuntimeException {
		return scope.getAgentScope().getPopulationFor(s).getAgent(val);
	}

	@operator(value = { "grid_at" }, type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "returns the cell of the grid (right-hand operand) at the position given by the right-hand operand", comment = "If the left-hand operand is a point of floats, it is used as a point of ints.", special_cases = { "if the left-hand operand is not a grid cell species, returns nil" }, examples = { "grid_cell grid_at {1,2} 	--: 	returns the agent grid_cell with grid_x=1 and grid_y = 2" })
	public static IAgent grid_at(final IScope scope, final ISpecies s, final GamaPoint val)
		throws GamaRuntimeException {
		ITopology t = scope.getAgentScope().getPopulationFor(s).getTopology();
		IContainer<?, IShape> m = t.getPlaces();
		if ( m instanceof GamaSpatialMatrix ) {
			IShape shp = ((GamaSpatialMatrix) m).get(scope, val);
			if ( shp != null ) { return shp.getAgent(); }
		}
		return null;
	}

	//
	// @operator(value = { "at" }, content_type = ITypeProvider.LEFT_CONTENT_TYPE)
	// @doc(deprecated =
	// "The use of at on a species is deprecated, please use it one a population instead (list(species_name) instead of species_name)")
	// public static IAgent getAgent(final IScope scope, final ISpecies s, final Integer val)
	// throws GamaRuntimeException {
	// return scope.getAgentScope().getPopulationFor(s).getAgent(val);
	// }

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

	@operator(value = "remove_duplicates", can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.FIRST_KEY_TYPE)
	@doc(value = "produces a set from the elements of the operand (i.e. a list without duplicated elements)", special_cases = {
		"if the operand is nil, remove_duplicates returns nil",
		"if the operand is a graph, remove_duplicates returns the set of nodes",
		"if the operand is a map, remove_duplicates returns the set of values without duplicate",
		"if the operand is a matrix, remove_duplicates returns a matrix withtout duplicated row" }, examples = {
		"remove_duplicates([3,2,5,1,2,3,5,5,5]) --: [3,2,5,1]",
		"remove_duplicates([1::3,2::4,3::3,5::7]) --: [3,4,7]" })
	// TODO finish doc for other kinds of Container
	public static IList remove_duplicates(final IScope scope, final IContainer l) {
		// ATTENTION NE GARDE PAS L'ORDRE DU CONTAINER SI ON UTILISE UN HASHSET. LinkedHashSet
		// utilis� � la place � v�rifier

		if ( l == null ) { return null; }
		final LinkedHashSet list = new LinkedHashSet(l.length(scope));
		for ( Object o : l.iterable(scope) ) {
			list.add(o);
		}
		return new GamaList(list);
	}

	@operator(value = "contains_all", can_be_const = true)
	@doc(value = "true if the left operand contains all the elements of the right operand, false otherwise", comment = "the definition of contains depends on the container", special_cases = { "if the right operand is nil or empty, contains_all returns true" }, examples = {
		"[1,2,3,4,5,6] contains_all [2,4] 		--: 	true ",
		"[1,2,3,4,5,6] contains_all [2,8] 		--: 	false",
		"[1::2, 3::4, 5::6] contains_all [1,3] 	--: 	true ",
		"[1::2, 3::4, 5::6] contains_all [2,4] 	--: 	false" }, see = { "contains", "contains_any" })
	public static Boolean contains_all(final IScope scope, final IContainer m, final IContainer l)
		throws GamaRuntimeException {
		if ( l == null || l.isEmpty(scope) ) { return true; }
		for ( Object o : l.iterable(scope) ) {
			if ( !m.contains(scope, o) ) { return false; }
		}
		return true;
	}

	@operator(value = "contains_any", can_be_const = true)
	@doc(value = "true if the left operand contains one of the elements of the right operand, false otherwise", comment = "the definition of contains depends on the container", special_cases = { "if the right operand is nil or empty, contains_any returns false" }, examples = {
		"[1,2,3,4,5,6] contains_any [2,4] 		--: 	true ",
		"[1,2,3,4,5,6] contains_any [2,8] 		--: 	true",
		"[1::2, 3::4, 5::6] contains_any [1,3] 	--: 	true ",
		"[1::2, 3::4, 5::6] contains_any [2,4] 	--: 	false" }, see = { "contains", "contains_all" })
	public static Boolean contains_any(final IScope scope, final IContainer c, final IContainer l)
		throws GamaRuntimeException {
		if ( l == null || l.isEmpty(scope) ) { return false; }
		for ( Object o : l.iterable(scope) ) {
			if ( c.contains(scope, o) ) { return true; }
		}
		return false;
	}

	@operator(value = { "copy_between" /* , "copy" */}, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "returns a copy of a sublist of the left operand between a begin index (x of the right operand point) and a end index (y of the right operand point", special_cases = {
		"if the right operand is nil or empty, copy_between returns a copy of the left operand",
		"if the begin index is higher than the end index, copy_between returns a new empty list" }, examples = { "[1,2,3,4,5,6,7] copy_between {0,3} 	--:		 [1,2,3]" })
	public static IList copy_between(final IList l1, final GamaPoint p) {
		if ( p == null ) { return new GamaList(l1); }
		final int beginIndex = p.x < 0 ? 0 : (int) p.x;
		final int endIndex = p.y > l1.size() ? l1.size() : (int) p.y;
		if ( beginIndex > endIndex ) { return new GamaList(); }
		return new GamaList(l1.subList(beginIndex, endIndex));
	}

	@operator(value = "in", can_be_const = true)
	@doc(value = "true if the right operand contains the left operand, false otherwise", comment = "the definition of in depends on the container", special_cases = { "if the right operand is nil or empty, in returns false" }, examples = {
		"2 in [1,2,3,4,5,6] : true", "7 in [1,2,3,4,5,6] : false",
		"3 in [1::2, 3::4, 5::6] : true", "6 in [1::2, 3::4, 5::6] : false" }, see = { "contains" })
	public static Boolean in(final IScope scope, final Object o, final IContainer source)
		throws GamaRuntimeException {
		if ( source == null ) { return false; }
		return source.contains(scope, o);
	}

	@operator(value = "index_of", can_be_const = true)
	@doc(value = "the index of an agent in a species. If the argument is not an agent of this species, returns -1", comment = "Use int(agent) instead")
	public static Integer index_of(final ISpecies s, final Object o) {
		if ( !(o instanceof IAgent) ) { return -1; }
		if ( !((IAgent) o).isInstanceOf(s, true) ) { return -1; }
		return ((IAgent) o).getIndex();
	}

	@operator(value = "index_of", can_be_const = true)
	@doc(value = "the index of the first occurence of the right operand in the left operand container", comment = "The definition of index_of and the type of the index depend on the container", special_cases = { "if the left operand is a list, index_of returns the index as an integer" }, examples = {
		"[1,2,3,4,5,6] index_of 4 	--: 	3", "[4,2,3,4,5,4] index_of 4  	--: 	0" }, see = { "at",
		"last_index_of" })
	public static Integer index_of(final IList l1, final Object o) {
		return l1.indexOf(o);
	}

	@operator(value = "index_of", can_be_const = true)
	@doc(special_cases = { "if the left operand is a map, index_of returns the index of a value or nil if the value is not mapped" }, examples = { "[1::2, 3::4, 5::6] index_of 4 		--: 	3" })
	public static Object index_of(final GamaMap<?, ?> m, final Object o) {
		for ( Map.Entry<?, ?> k : m.entrySet() ) {
			if ( k.getValue().equals(o) ) { return k.getKey(); }
		}
		return null;
	}

	@operator(value = "index_of", can_be_const = true)
	@doc(special_cases = { "if the left operand is a matrix, index_of returns the index as a point" }, examples = { "matrix([[1,2,3],[4,5,6]]) index_of 4  	--: 	{1.0;0.0}" })
	public static ILocation index_of(IScope scope, final IMatrix m, final Object o) {
		for ( int i = 0; i < m.getCols(scope); i++ ) {
			for ( int j = 0; j < m.getRows(scope); j++ ) {
				if ( m.get(scope, i, j).equals(o) ) { return new GamaPoint(i, j); }
			}
		}
		return null;
	}

	@operator(value = "last_index_of", can_be_const = true)
	@doc(value = "for species, the last index of an agent is the same as its index", comment = "", see = {
		"at", "index_of" })
	public static Integer last_index_of(final ISpecies l1, final Object o) {
		return index_of(l1, o);
	}

	@operator(value = "last_index_of", can_be_const = true)
	@doc(value = "the index of the last occurence of the right operand in the left operand container", comment = "The definition of last_index_of and the type of the index depend on the container", special_cases = { "if the left operand is a list, last_index_of returns the index as an integer" }, examples = {
		"[1,2,3,4,5,6] last_index_of 4  	--: 	3", "[4,2,3,4,5,4] last_index_of 4  	--: 	5" }, see = {
		"at", "last_index_of" })
	public static Integer last_index_of(final List l1, final Object o) {
		return l1.lastIndexOf(o);
	}

	@operator(value = "last_index_of", can_be_const = true)
	@doc(special_cases = { "if the left operand is a matrix, last_index_of returns the index as a point" }, examples = { "matrix([[1,2,3],[4,5,4]]) last_index_of 4  	--: 	{1.0;2.0}" })
	public static ILocation last_index_of(IScope scope, final IMatrix m, final Object o) {
		for ( int i = m.getCols(scope) - 1; i > -1; i-- ) {
			for ( int j = m.getRows(scope) - 1; j > -1; j-- ) {
				if ( m.get(scope, i, j).equals(o) ) { return new GamaPoint(i, j); }
			}
		}
		return null;
	}

	@operator(value = "last_index_of", can_be_const = true, type = ITypeProvider.FIRST_KEY_TYPE)
	@doc(special_cases = { "if the left operand is a map, last_index_of returns the index as a pair" }, examples = { "[1::2, 3::4, 5::4] last_index_of 4  	--:  	5::4" })
	public static Object last_index_of(final GamaMap m, final Object o) {
		return index_of(m, o);
	}

	@operator(value = "inter", can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "the intersection of the two operands", comment = "both containers are transformed into sets (so without duplicated element, cf. remove_deplicates operator) before the set intersection is computed.", special_cases = {
		"if an operand is a graph, it will be transformed into the set of its nodes",
		"if an operand is a map, it will be transformed into the set of its values",
		"if an operand is a matrix, it will be transformed into the set of the lines" }, examples = {
		"[1,2,3,4,5,6] inter [2,4] 				--: 	[2,4]", "[1,2,3,4,5,6] inter [0,8] 				--: 	[]",
		"[1::2, 3::4, 5::6] inter [2,4] 		--: 	[2,4]", "[1::2, 3::4, 5::6] inter [1,3] 		--: 	[]",
		"matrix([[1,2,3],[4,5,4]]) inter [3,4] 	--: 	[4,3]" }, see = { "remove_duplicates" })
	public static IList inter(final IScope scope, final IContainer l1, final IContainer l) {
		IList list = remove_duplicates(scope, l1);
		if ( l == null ) { return list; }
		list.retainAll(remove_duplicates(scope, l));
		return list;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "returns a new list in which all the elements of the right operand have been removed from the left one", comment = "The behavior of the operator depends on the type of the operands.", special_cases = { "if the right operand is empty or nil, " +
		IKeyword.MINUS + " returns the left operand" }, examples = {
		"[1,2,3,4,5,6] - [2,4,9] 	--: 	[1,3,5,6]", "[1,2,3,4,5,6] - [0,8] 		--:	 	[1,2,3,4,5,6]" }, see = { "" +
		IKeyword.PLUS })
	public static IList minus(final IScope scope, final IContainer source, final IContainer l) {
		if ( source == null || source.isEmpty(scope) ) { return new GamaList(); }
		if ( l == null || l.isEmpty(scope) ) { return source.listValue(scope); }
		IList l1 = source.listValue(scope);
		HashSet set = new HashSet(l1);
		if ( set.removeAll(l.listValue(scope)) ) { return new GamaList(set); }
		return l1;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(special_cases = { "if the right operand is an object of any type (except list), " +
		IKeyword.MINUS + " returns a copie of the left operand without this object" }, examples = {
		"[1,2,3,4,5,6] - 2 		--: 	[1,3,4,5,6]", "[1,2,3,4,5,6] - 0 		--:	 	[1,2,3,4,5,6]" })
	public static IList minus(final IList l1, final Object l) {
		if ( l == null ) { return new GamaList(l1); }
		GamaList result = new GamaList(l1);
		result.remove(l);
		return result;
	}

	// PRENDRE EN COMPTE:
	//
	// - index_type
	// - nouvelles valeurs de ITypeProvider

	@operator(value = "of_generic_species", content_type = ITypeProvider.SECOND_CONTENT_TYPE)
	@doc(value = "a list, containing the agents of the left-hand operand whose species is that denoted by the right-hand operand "
		+ "and whose species extends the right-hand operand species ", examples = {
		"// species test {}",
		"// species sous_test parent: test {}",
		"[sous_test(0),sous_test(1),test(2),test(3)] of_generic_species test 		--: [sous_test0,sous_test1,test2,test3]",
		"[sous_test(0),sous_test(1),test(2),test(3)] of_generic_species sous_test 	--: [sous_test0,sous_test1]",
		"[sous_test(0),sous_test(1),test(2),test(3)] of_species test 				--: [test2,test3]",
		"[sous_test(0),sous_test(1),test(2),test(3)] of_species sous_test 			--: [sous_test0,sous_test1]" }, see = { "of_species" })
	public static IList of_generic_species(final IScope scope, final IContainer agents,
		final ISpecies s) {
		return of_species(scope, agents, s, true);
	}

	@operator(value = "of_species", content_type = ITypeProvider.SECOND_CONTENT_TYPE)
	@doc(value = "a list, containing the agents of the left-hand operand whose species is that denoted by the right-hand operand."
		+ "The expression agents of_species (species self) is equivalent to agents where (species each = species self); "
		+ "however, the advantage of using the first syntax is that the resulting list is correctly typed with the right species, "
		+ "whereas, in the second syntax, the parser cannot determine the species of the agents within the list "
		+ "(resulting in the need to cast it explicitely if it is to be used in an ask statement, for instance).", special_cases = "if the right operand is nil, of_species returns the right operand", examples = {
		"(self neighbours_at 10) of_species (species (self)) 	--:  all the neighbouring agents of the same species.",
		"[test(0),test(1),node(1),node(2)] of_species test 		--:  [test0,test1]",
		"[1,2,3,4,5,6] of_species test							--:	 []" }, see = { "of_generic_species" })
	public static IList of_species(final IScope scope, final IContainer agents, final ISpecies s) {
		return of_species(scope, agents, s, false);
	}

	private static IList of_species(final IScope scope, final IContainer agents, final ISpecies s,
		final boolean generic) {
		if ( s == null ) { return GamaList.EMPTY_LIST; }
		// int n = agents.size();
		final GamaList result = new GamaList(agents.length(scope));
		for ( Object be : agents.iterable(scope) ) {
			if ( be instanceof IAgent && ((IAgent) be).isInstanceOf(s, !generic) ) {
				result.add(be);
			}
		}
		return result;
	}

	@operator(value = { "::" }, can_be_const = true, type = IType.PAIR, index_type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.SECOND_TYPE)
	@doc(value = "produces a new pair combining the left and the right operands")
	public static GamaPair pair(final Object a, final Object b) {
		return new GamaPair(a, b);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "returns a new list containing all the elements of both operands", special_cases = { "if one of the operands is nil, " +
		IKeyword.PLUS + " returns the casting of the other one into a list" }, examples = {
		"[1,2,3,4,5,6] + [2,4,9] 	--: 	[1,2,3,4,5,6,2,4,9]",
		"[1,2,3,4,5,6] + [0,8] 		--: 	[1,2,3,4,5,6,0,8]" }, see = { "" + IKeyword.MINUS })
	public static IList plus(final IScope scope, final IContainer source, final IContainer l) {
		// GuiUtils.debug("Adding: " + source + " and " + l);
		if ( source == null || source.isEmpty(scope) ) {
			if ( l == null ) { return new GamaList(); }
			return l.listValue(scope);
		}
		IList l1 = source.listValue(scope);
		if ( l == null || l.isEmpty(scope) ) { return l1; }
		GamaList result = new GamaList(l1.length(scope) + l.length(scope));
		result.addAll(l1);
		result.addAll(l.listValue(scope));
		return result;
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(special_cases = { "if the right operand is an object of any type (except a container), " +
		IKeyword.PLUS + " returns a copie of the left operand with this object" }, examples = {
		"[1,2,3,4,5,6] + 2 		--: 	[1,2,3,4,5,6,2]", "[1,2,3,4,5,6] + 0 		--:	 	[1,2,3,4,5,6,0]" })
	public static IList plus(final IScope scope, final IContainer l1, final Object l) {
		// GuiUtils.debug("Adding [IContainer, Object]: " + l1 + " and " + l);
		IList result = l1.listValue(scope);
		if ( l == null ) { return result; }

		result.add(l);
		return result;
	}

	@operator(value = "union", can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "returns a new list containing all the elements of both operands without duplicated elements. Elements of this new list are sorted.", comment = "union is only defined with a list as left operand", special_cases = { "if the right operand is nil, union returns a copy of the left operand" }, examples = {
		"[1,2,3,4,5,6] union [2,4,9] 		--: 	[1,2,3,4,5,6,9]",
		"[1,2,3,4,5,6] union [0,8] 			--: 	[0,1,2,3,4,5,6,8]",
		"[1,3,2,4,5,6,8,5,6] union [0,8] 	--: 	[0,1,2,3,4,5,6,8]" }, see = { "inter", IKeyword.PLUS })
	public static IList union(final IScope scope, final IContainer source, final IContainer l) {
		if ( source == null || source.isEmpty(scope) ) {
			if ( l == null ) { return new GamaList(); }
			return l.listValue(scope);
		}
		IList l1 = source.listValue(scope);
		if ( l == null ) { return source.listValue(scope); }
		Set s = new LinkedHashSet(l.length(scope) + l1.length(scope));
		s.addAll(l1);
		s.addAll(l.listValue(scope));
		return new GamaList(s);
	}

	// ITERATORS

	@operator(value = { "group_by" }, iterator = true, index_type = ITypeProvider.SECOND_CONTENT_TYPE, content_type = IType.LIST)
	@doc(value = "a map, where the keys take the possible values of the right-hand operand and the map values are the list of elements "
		+ "of the left-hand operand associated to the key value", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is nil, group_by returns a new empty map" }, examples = {
		"[1,2,3,4,5,6,7,8] group_by (each > 3) 	--: 	[false::[1, 2, 3], true::[4, 5, 6, 7, 8]] ",
		"g2 group_by (length(g2 out_edges_of each) ) 	--: 	[ 0::[node9, node7, node10, node8, node11], 1::[node6], 2::[node5], 3::[node4]]",
		"(list(node) group_by (round(node(each).location.x))    --:  [32::[node5], 21::[node1], 4::[node0], 66::[node2], 96::[node3]]",
		"[1::2, 3::4, 5::6] group_by (each > 4) 	--: 	[false::[2, 4], true::[6]]" }, see = {
		"first_with", "last_with", "where" })
	public static GamaMap group_by(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return new GamaMap(); }
		final GamaMap result = new GamaMap();
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			Object key = filter.value(scope);
			if ( !result.containsKey(key) ) {
				result.put(key, new GamaList());
			}
			((GamaList) result.get(key)).add(each);
		}
		return result;
	}

	@operator(value = { "group_by" }, iterator = true, content_type = IType.MAP)
	public static GamaMap group_by(final IScope scope, final GamaMap original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return new GamaMap(); }
		final GamaMap result = new GamaMap();
		for ( Object each : original ) {
			scope.setEach(each);
			Object key = filter.value(scope);
			if ( !result.containsKey(key) ) {
				result.put(key, new GamaMap());
			}
			((GamaMap) result.get(key)).add((GamaPair) each);
		}
		return result;
	}

	@operator(value = { "last_with" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	@doc(value = "the last element of the left-hand operand that makes the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is nil, last_with returns nil" }, examples = {
		"[1,2,3,4,5,6,7,8] last_with (each > 3) 					--: 	8",
		"g2 last_with (length(g2 out_edges_of each) = 0 ) 			--: 	node11",
		"(list(node) last_with (round(node(each).location.x) > 32)  --: 	node3",
		"[1::2, 3::4, 5::6] last_with (each.key > 4) 				--: 	5::6" }, see = { "group_by",
		"first_with", "where" })
	public static Object last_with(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return null; }
		for ( Object each : original.reverse(scope) ) {
			scope.setEach(each);
			if ( Cast.asBool(scope, filter.value(scope)) ) { return each; }
		}
		return null;
	}

	@operator(value = { "first_with" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	@doc(value = "the first element of the left-hand operand that makes the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is nil, first_with returns nil" }, examples = {
		"[1,2,3,4,5,6,7,8] first_with (each > 3) 						--: 	4",
		"g2 first_with (length(g2 out_edges_of each) = 0) 				--: 	node9",
		"(list(node) first_with (round(node(each).location.x) > 32)   	--: 	node2",
		"[1::2, 3::4, 5::6] first_with (each.key > 4) 					--: 	5::6" }, see = { "group_by",
		"last_with", "where" })
	public static Object first_with(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return null; }
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			if ( Cast.asBool(scope, filter.value(scope)) ) { return each; }
		}
		return null;
	}

	@operator(value = { "max_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true)
	@doc(value = "the maximum value of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is nil, max_of returns the right-hand operand default value" }, examples = {
		"[1,2,4,3,5,7,6,8] max_of (each * 100 ) 			--: 	800",
		"g2 max_of (length(g2 out_edges_of each) ) 			--: 	3",
		"(list(node) max_of (round(node(each).location.x))  --: 	96",
		"[1::2, 3::4, 5::6] max_of (each.value + 3) 		--: 	9" }, see = { "min_of" })
	public static Object max_of(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return filter.getType().getDefault(); }
		if ( filter.getType().id() == IType.INT ) {
			int max = Integer.MIN_VALUE;
			for ( Object each : original.iterable(scope) ) {
				scope.setEach(each);
				final int rv = Cast.asInt(scope, filter.value(scope));
				if ( rv > max ) {
					max = rv;
				}
			}
			return max;
		}
		double max = -Double.MAX_VALUE;
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			final double rv = Cast.asFloat(scope, filter.value(scope));
			if ( rv > max ) {
				max = rv;
			}
		}
		return max;
	}

	@operator(value = { "min_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true)
	@doc(value = "the minimum value of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is nil, first_with returns nil" }, examples = {
		"[1,2,4,3,5,7,6,8] min_of (each * 100 ) 			--: 	100",
		"g2 min_of (length(g2 out_edges_of each) ) 			--: 	0",
		"(list(node) min_of (round(node(each).location.x))  --: 	4",
		"[1::2, 3::4, 5::6] min_of (each.value + 3) 				--: 	5" }, see = { "max_of" })
	public static Object min_of(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return filter.getType().getDefault(); }
		if ( filter.getType().id() == IType.INT ) {
			int min = Integer.MAX_VALUE;
			for ( Object each : original.iterable(scope) ) {
				scope.setEach(each);
				final int rv = Cast.asInt(scope, filter.value(scope));
				if ( rv < min ) {
					min = rv;
				}
			}
			return min;
		}

		double min = Double.MAX_VALUE;
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			final double rv = Cast.asFloat(scope, filter.value(scope));
			if ( rv < min ) {
				min = rv;
			}
		}
		return min;
	}

	// @operator(value = "among", content_type = ITypeProvider.RIGHT_CONTENT_TYPE)
	// @doc(special_cases = {
	// "if the right-hand operand is a map, among returns a map of right-hand operand element instead of a list"
	// }, examples = { "2 among [1::2, 3::4, 5::6] 	--: 	[1::2, 3::4]" })
	// public static GamaMap among(final IScope scope, final Integer number, final GamaMap l)
	// throws GamaRuntimeException {
	// final GamaMap result = new GamaMap();
	// if ( l == null ) { return result; }
	// int size = l.size();
	// if ( number == 0 ) { return result; }
	// if ( number >= size ) { return l; }
	// final IList indexes = among(scope, number, new GamaList(l.keySet()));
	// for ( int i = 0; i < number; i++ ) {
	// Object o = indexes.get(i);
	// result.put(o, l.get(o));
	// }
	// return result;
	// }

	@operator(value = "among", content_type = ITypeProvider.SECOND_CONTENT_TYPE)
	@doc(value = "a list of length the value of the left-hand operand, containing random elements from the right-hand operand", special_cases = {
		"if the right-hand operand is empty or nil, among returns a new empty list",
		"if the left-hand operand is greater than the length of the right-hand operand, among returns the right-hand operand." }, examples = {
		"3 among [1,2,4,3,5,7,6,8] 		--: 	[1,2,8]", "3 among g2 					--: 	[node6,node11,node7]",
		"3 among list(node)    			--:  	[node1,node11,node4]" })
	public static IList among(final IScope scope, final Integer number, final IContainer c)
		throws GamaRuntimeException {
		if ( c == null ) { return new GamaList(); }
		final GamaList result = new GamaList();
		final IList l = c.listValue(scope);
		int size = l.size();
		if ( number == 0 ) { return result; }
		if ( number >= size ) { return l; }
		final IList<Integer> indexes = new GamaList(number);
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

	@operator(value = { "sort_by", "sort" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	@doc(value = "a list, containing the elements of the left-hand operand sorted in ascending order by the value of the right-hand operand when it is evaluated on them. ", comment = "the left-hand operand is casted to a list before applying the operator. In the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.", special_cases = { "if the left-hand operand is nil, sort_by returns  nil" }, examples = {
		"[1,2,4,3,5,7,6,8] sort_by (each) 					--: 	[1,2,3,4,5,6,7,8]",
		"g2 sort_by (length(g2 out_edges_of each) ) 		--: 	[node9, node7, node10, node8, node11, node6, node5, node4]",
		"(list(node) sort_by (round(node(each).location.x)) --:  	[node5, node1, node0, node2, node3]",
		"[1::2, 3::4, 5::6] sort_by (each) 				--: 	" }, see = { "group_by" })
	public static IList sort(final IScope scope, final IContainer original, final IExpression filter)
		throws GamaRuntimeException {
		if ( original == null ) { return null; }
		final GamaList lv = new GamaList(original.listValue(scope));
		// copy in order to prevent any side effect on the left member
		if ( lv.isEmpty() ) { return lv; }
		int fType = filter.getType().id();
		boolean isComparable = fType == IType.STRING || fType == IType.INT || fType == IType.FLOAT;
		for ( int i = 0, n = lv.size(); i < n; i++ ) {
			Object each = lv.get(i);
			scope.setEach(each);
			final Object rv = filter.value(scope);
			if ( isComparable || rv instanceof Comparable ) {
				isComparable = true;
				results.put(each, (Comparable) rv);
			} else {
				results.put(each, Cast.asFloat(scope, rv));
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

	/**
	 * for maps, we sort the keys and reinsert them in this order in the new map
	 * @param scope
	 * @param original
	 * @param filter
	 * @return
	 * @throws GamaRuntimeException
	 */
	// FIXME Completely false
	@operator(value = { "sort_by", "sort" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	public static GamaMap sort(final IScope scope, final GamaMap original, final IExpression filter)
		throws GamaRuntimeException {
		if ( original == null ) { return null; }
		final GamaMap resultMap = new GamaMap(original);
		// copy in order to prevent any side effect on the left member
		if ( resultMap.isEmpty() ) { return resultMap; }
		final IList<GamaPair> sortedPairs = sort(scope, resultMap.getPairs(), filter);
		for ( GamaPair pair : sortedPairs ) {
			resultMap.add(pair);
		}
		return resultMap;
	}

	// @operator(value = { "where", "select" }, priority = IPriority.ITERATOR, iterator = true)
	// public static GamaMap where(final IScope scope, final GamaMap original, final IExpression
	// filter)
	// throws GamaRuntimeException {
	// if ( original == null ) { return new GamaMap(); }
	// final GamaMap result = new GamaMap();
	// for ( GamaPair p : original.iterable(scope) ) {
	// scope.setEach(p);
	// if ( Cast.asBool(scope, filter.value(scope)) ) {
	// result.add(p);
	// }
	// }
	// return result;
	// }

	@operator(value = { "where", "select" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	@doc(value = "a list containing all the elements of the left-hand operand that make the right-hand operand evaluate to true. ", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is a list nil, where returns a new empty list" }, examples = {
		"[1,2,3,4,5,6,7,8] where (each > 3) 						--: 	[4, 5, 6, 7, 8] ",
		"g2 where (length(g2 out_edges_of each) = 0 ) 				--: 	[node9, node7, node10, node8, node11]",
		"(list(node) where (round(node(each).location.x) > 32)   	--:  	[node2, node3]",
		"[1::2, 3::4, 5::6] where (each.value > 4) 						--: 	" }, see = { "first_with",
		"last_with", "where" })
	public static IList where(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return GamaList.EMPTY_LIST; }
		if ( filter == null ) { return Cast.asList(scope, original); }
		final GamaList result = new GamaList(original.length(scope));
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			if ( Cast.asBool(scope, filter.value(scope)) ) {
				result.add(each);
			}
		}
		return result;
	}

	@operator(value = { "with_max_of" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	@doc(value = "one of elements of the left-hand operand that maximizes the value of the right-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is nil, with_max_of returns the default value of the right-hand operand" }, examples = {
		"[1,2,3,4,5,6,7,8] with_max_of (each ) 						--: 	8",
		"g2 with_max_of (length(g2 out_edges_of each)  ) 			--: 	node4",
		"(list(node) with_max_of (round(node(each).location.x))    	--:  	node3",
		"[1::2, 3::4, 5::6] with_max_of (each) 						--: 	6" }, see = { "where", "with_min_of" })
	public static Object with_max_of(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return filter.getType().getDefault(); }
		// double max = Double.MIN_VALUE;
		double max = -Double.MAX_VALUE;
		Object result = null;
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			final double rv = Cast.asFloat(scope, filter.value(scope));
			if ( rv > max ) {
				max = rv;
				result = each;
			}
		}
		return result;
	}

	@operator(value = { "with_min_of" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	@doc(value = "one of elements of the left-hand operand that minimizes the value of the right-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is nil, with_max_of returns the default value of the right-hand operand" }, examples = {
		"[1,2,3,4,5,6,7,8] with_min_of (each ) 						--: 	1",
		"g2 with_min_of (length(g2 out_edges_of each)  ) 			--: 	node11",
		"(list(node) with_min_of (round(node(each).location.x))    	--:  	node0",
		"[1::2, 3::4, 5::6] with_min_of (each) 						--: 	2" }, see = { "where", "with_max_of" })
	public static Object with_min_of(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return null; }
		double min = Double.MAX_VALUE;
		Object result = null;
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			final double rv = Cast.asFloat(scope, filter.value(scope));
			if ( rv < min ) {
				min = rv;
				result = each;
			}
		}
		return result;
	}

	// TODO check the doc (since it is difficult to distinguish it from collect)
	@operator(value = { "accumulate" }, content_type = ITypeProvider.SECOND_CONTENT_TYPE, iterator = true)
	@doc(value = "returns a new flat list, in which each element is the evaluation of the right-hand operand. If this evaluation returns a list, the elements of this result are added directly to the list returned", comment = "accumulate is dedicated to the application of a same computation on each element of a container (and returns a list) "
		+ "In the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", special_cases = { "if the left-hand operand is nil, accumulate returns an empty list" }, examples = {
		"[a1,a2,a3] accumulate (each neighbours_at 10)  		--: 	a flat list of all the neighbours of these three agents",
		"[1,2,4] accumulate ([2,4])  		--: 	[2,4,2,4,2,4]" }, see = { "collect" })
	public static IList accumulate(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		final GamaList result = new GamaList();
		if ( original == null ) { return result; }
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			final Object values = filter.value(scope);
			if ( values instanceof IContainer ) {
				result.addAll(((IContainer) values).listValue(scope));
			} else {
				result.add(values);
			}
		}
		return result;
	}

	// TODO Check why collate([...]) does not return [...]

	@operator(value = "collate", content_type = ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE)
	@doc(value = "a new list containing interleaved elements of the operand", comment = "the operand should be a list of lists of elements. The result is a list of elements. ", special_cases = { "if the operand is nil or a list of (non-list) elements, accumulate returns an empty list" }, examples = {
		"collate([1,2,4,3,5,7,6,8]) 	--: 	[]",
		"collate([['e11','e12','e13'],['e21','e22','e23'],['e31','e32','e33']])  --:  [e11,e21,e31,e12,e22,e32,e13,e23,e33]" })
	public static IList collate(final IScope scope, final IContainer cc) {

		final GamaList result = new GamaList();
		if ( cc == null ) { return result; }
		IList original = cc.listValue(scope);
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

	@operator(value = { "count" }, iterator = true)
	@doc(value = "returns an int, equal to the number of elements of the left-hand operand that make the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.", special_cases = { "if the left-hand operand is nil, count returns 0" }, examples = {
		"[1,2,3,4,5,6,7,8] count (each > 3) 					--: 	5",
		"g2 count (length(g2 out_edges_of each) = 0  ) 			--: 	5	// Number of nodes of graph g2 without any out edge",
		"(list(node) count (round(node(each).location.x) > 32)  --:  	2 	// Number of agents node with x > 32",
		"[1::2, 3::4, 5::6] count (each > 4) 					--: 	1" }, see = { "group_by" })
	public static Integer count(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return 0; }
		Integer result = 0;
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			if ( Cast.asBool(scope, filter.value(scope)) ) {
				result++;
			}
		}
		return result;
	}

	@operator(value = { "as_map" }, iterator = true, content_type = ITypeProvider.SECOND_CONTENT_TYPE, index_type = ITypeProvider.SECOND_KEY_TYPE)
	@doc(value = "produces a new map from the evaluation of the right-hand operand for each element of the left-hand operand", comment = "the right-hand operand should be pair or a map.", special_cases = { "if the left-hand operand is nil or empty, as_map returns a new empty map." }, examples = {
		"[1,2,3,4,5,6,7,8] as_map (each::(each * 2) 	--: 	[1::2, 2::4, 3::6, 4::8, 5::10, 6::12, 7::14, 8::16]",
		"[1::2,3::4,5::6] as_map (each::(each * 2))		--: 	[2::4, 4::8, 6::12] " }, see = {})
	public static GamaMap as_map(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		final GamaMap result = new GamaMap();
		if ( original == null || original.isEmpty(scope) ) { return result; }
		IExpression[] p = new IExpression[2];
		if ( filter instanceof MapExpression ) {
			MapExpression exp = (MapExpression) filter;
			p[0] = exp.keysArray()[0];
			p[1] = exp.valuesArray()[0];
		} else if ( filter instanceof BinaryOperator &&
			((BinaryOperator) filter).getName().equals("::") ) {
			p[0] = ((BinaryOperator) filter).left();
			p[1] = ((BinaryOperator) filter).right();
		} else if ( filter instanceof ListExpression ) {
			return as_map(scope, original, ((ListExpression) filter).getElements()[0]);
		} else {
			throw GamaRuntimeException.error("The as_map operator expects either a pair or a map for its second argument");
		}
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			result.put(p[0].value(scope), p[1].value(scope));
		}
		return result;
	}

	@operator(value = { "collect" }, content_type = ITypeProvider.SECOND_TYPE, iterator = true)
	@doc(value = "returns a new list, in which each element is the evaluation of the right-hand operand.", comment = "collect is very similar to accumulate except. Nevertheless if the evaluation of the right-hand operand produces a list,"
		+ "the returned list is a list of list of elements. In contrarily, the list produces by accumulate is only a list of elements "
		+ "(all the lists) produced are concaneted. In addition, collect can be applied to any container.", special_cases = { "if the left-hand operand is nil, accumulate returns an empty list" }, examples = {
		"[1,2,4] collect (each *2)  	--: 	[2,4,8]",
		"[1,2,4] collect ([2,4])  		--: 	[[2,4],[2,4],[2,4]]",
		"[1::2, 3::4, 5::6] collect (each + 2) --: [8,4,6]",
		"(list(node) collect (node(each).location.x * 2)    --:  [25.65, 158.99, 140.80, 80.11, 125.47, 37.830, 4.62,...]" }, see = { "accumulate" })
	public static IList collect(final IScope scope, final IContainer original,
		final IExpression filter) throws GamaRuntimeException {
		if ( original == null ) { return GamaList.EMPTY_LIST; }
		int size = original.length(scope);
		final Object[] result = new Object[size];
		int i = 0;
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			result[i++] = filter.value(scope);
		}
		return new GamaList(result);
	}
}
