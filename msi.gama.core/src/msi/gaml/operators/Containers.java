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

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Iterables.*;
import static msi.gama.util.GAML.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.GAML.InterleavingIterator;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import com.google.common.base.*;
import com.google.common.collect.*;

/**
 * Written by drogoul Modified on 31 juil. 2010
 * 
 * GAML operators dedicated to containers (list, matrix, graph, etc.)
 * 
 * @see also IMatrix, IContainer for other operators
 * 
 */
public class Containers {

	@operator(value = { "internal_at" }, content_type = IType.NONE, category={IOperatorCategory.CONTAINER})
	@doc("For internal use only. Corresponds to the implementation of the access to containers with [index]")
	public static Object internal_at(final IScope scope, final IShape shape, final IList indices)
		throws GamaRuntimeException {
		// TODO How to test if the index is correct ?
		if ( shape == null ) { return null; }
		final GamaMap map = shape.getAttributes();
		if ( map == null ) { return null; }
		return map.getFromIndicesList(scope, indices);
	}



	@operator(value = { "grid_at" }, type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.POINT,IOperatorCategory.GRID})
	@doc(value = "returns the cell of the grid (right-hand operand) at the position given by the right-hand operand", comment = "If the left-hand operand is a point of floats, it is used as a point of ints.", usages = { @usage("if the left-hand operand is not a grid cell species, returns nil") }, examples = { @example(value="grid_cell grid_at {1,2} 	--: 	returns the agent grid_cell with grid_x=1 and grid_y = 2", isExecutable=false) })
	public static IAgent grid_at(final IScope scope, final ISpecies s, final GamaPoint val) throws GamaRuntimeException {
		final ITopology t = scope.getAgentScope().getPopulationFor(s).getTopology();
		final IContainer<?, IShape> m = t.getPlaces();
		if ( m instanceof IGrid ) {
			final IShape shp = ((IGrid) m).get(scope, val);
			if ( shp != null ) { return shp.getAgent(); }
		}
		return null;
	}

	@operator(value = "remove_duplicates", can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.FIRST_KEY_TYPE, category={IOperatorCategory.CONTAINER})
	@doc(value = "produces a set from the elements of the operand (i.e. a list without duplicated elements)", usages = {
		@usage(value="if the operand is nil, remove_duplicates returns nil"),
		@usage(value="if the operand is a graph, remove_duplicates returns the set of nodes"),
		@usage(value="if the operand is a map, remove_duplicates returns the set of values without duplicate", examples={@example(value="remove_duplicates(map([1::3,2::4,3::3,5::7]))",equals="[3,4,7]")}),
		@usage(value="if the operand is a matrix, remove_duplicates returns a matrix withtout duplicated row") }, examples = {@example(value="remove_duplicates([3,2,5,1,2,3,5,5,5])",equals="[3,2,5,1]") })
	// TODO finish doc for other kinds of Container
	public static IList remove_duplicates(final IScope scope, final IContainer l) {
		return new GamaList(Sets.newLinkedHashSet(nullCheck(l).iterable(scope)));
	}

	@operator(value = "contains_all", can_be_const = true, category={IOperatorCategory.CONTAINER})
	@doc(value = "true if the left operand contains all the elements of the right operand, false otherwise", comment = "the definition of contains depends on the container", usages = { @usage("if the right operand is nil or empty, contains_all returns true") }, examples = {
		@example(value="[1,2,3,4,5,6] contains_all [2,4]", equals="true "), @example(value="[1,2,3,4,5,6] contains_all [2,8]", equals="false"),
		@example(value="[1::2, 3::4, 5::6] contains_all [1,3]", equals="true "), @example(value="[1::2, 3::4, 5::6] contains_all [2,4]", equals="false") }, see = {
		"contains", "contains_any" })
	public static Boolean contains_all(final IScope scope, final IContainer m, final IContainer l) {
		return Iterables.all(nullCheck(l).iterable(scope), inContainer(scope, m));
	}

	@operator(value = "contains_any", can_be_const = true, category={IOperatorCategory.CONTAINER})
	@doc(value = "true if the left operand contains one of the elements of the right operand, false otherwise", comment = "the definition of contains depends on the container", special_cases = { "if the right operand is nil or empty, contains_any returns false" }, examples = {
		@example(value="[1,2,3,4,5,6] contains_any [2,4]",equals="true "), @example(value="[1,2,3,4,5,6] contains_any [2,8]",equals="true"),
		@example(value="[1::2, 3::4, 5::6] contains_any [1,3]",equals="true "), @example(value="[1::2, 3::4, 5::6] contains_any [2,4]",equals="false") }, see = {
		"contains", "contains_all" })
	public static Boolean contains_any(final IScope scope, final IContainer c, final IContainer l) {
		return Iterables.any(nullCheck(c).iterable(scope), inContainer(scope, l));
	}

	@operator(value = { "copy_between" /* , "copy" */}, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.LIST})
	@doc(deprecated = "Deprecated. Use copy_between(list, int, int) instead")
	public static IList copy_between(final IList l1, final GamaPoint p) {
		return copy_between(l1, (int) nullCheck(p).x, (int) p.y);
	}

	@operator(value = { "copy_between" /* , "copy" */}, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.LIST})
	@doc(value = "Returns a copy of the first operand between the indexes determined by the second (inclusive) and third operands (exclusive)", examples = { @example(value=" copy_between ([4, 1, 6, 9 ,7], 1, 3)", equals="[1, 6]") }, usages = {
		@usage("If the first operand is empty, returns an empty object of the same type"),
		@usage("If the second operand is greater than or equal to the third operand, return an empty object of the same type"),
		@usage("If the first operand is nil, raises an error") })
	public static IList copy_between(final IList l1, final Integer begin, final Integer end) {
		final int beginIndex = begin < 0 ? 0 : begin;
		final int size = nullCheck(l1).size();
		final int endIndex = end > size ? size : end;
		if ( beginIndex >= endIndex ) { return new GamaList(); }
		return new GamaList(l1.subList(beginIndex, endIndex));
	}

	@operator(value = { "first" }, can_be_const = true, content_type = ITypeProvider.SECOND_CONTENT_TYPE, category={IOperatorCategory.CONTAINER})
	@doc(value = "Returns the nth first elements of the container. If n is greater than the list size, a translation of the container to a list is returned. If it is equal or less than zero, returns an empty list")
	public static IList first(final IScope scope, final Integer number, final IContainer l1) {
		return new GamaList(Iterables.limit(l1.iterable(scope), number < 0 ? 0 : number));
	}

	@operator(value = { "last" }, can_be_const = true, content_type = ITypeProvider.SECOND_CONTENT_TYPE, category={IOperatorCategory.CONTAINER})
	@doc(value = "Returns the nth last elements of the container. If n is greater than the list size, a translation of the container to a list is returned. If it is equal or less than zero, returns an empty list")
	public static IList last(final IScope scope, final Integer number, final IContainer l1) {
		IList result =
			new GamaList(Iterables.limit(Lists.reverse(nullCheck(l1).listValue(scope, Types.NO_TYPE)), number < 0 ? 0
				: number));
		return result;
	}

	@operator(value = "in", can_be_const = true, category={IOperatorCategory.CONTAINER})
	@doc(value = "true if the right operand contains the left operand, false otherwise", comment = "the definition of in depends on the container", usages = { @usage("if the right operand is nil or empty, in returns false") }, examples = {
		@example(value="2 in [1,2,3,4,5,6]", equals="true"), @example(value="7 in [1,2,3,4,5,6]", equals="false"), @example(value="3 in [1::2, 3::4, 5::6]", equals="true"),
		@example(value="6 in [1::2, 3::4, 5::6]", equals="false") }, see = { "contains" })
	public static Boolean in(final IScope scope, final Object o, final IContainer source) throws GamaRuntimeException {
		return source.contains(scope, o);
		// return contains(nullCheck(source).iterable(scope), o);
	}

	@operator(value = "index_of", can_be_const = true, category={IOperatorCategory.SPECIES})
	@doc(value = "the index of the first occurence of the right operand in the left operand container", 
		usages = @usage("if the left operator is a species, returns the index of an agent in a species. If the argument is not an agent of this species, returns -1. Use int(agent) instead"), masterDoc=true)
	public static Integer index_of(final ISpecies s, final Object o) {
		if ( !(o instanceof IAgent) ) { return -1; }
		if ( !((IAgent) o).isInstanceOf(nullCheck(s), true) ) { return -1; }
		return ((IAgent) o).getIndex();
	}

	@operator(value = "index_of", can_be_const = true, category={IOperatorCategory.LIST})
	@doc(value = "the index of the first occurence of the right operand in the left operand container", masterDoc=true, comment = "The definition of index_of and the type of the index depend on the container", usages = @usage(value="if the left operand is a list, index_of returns the index as an integer" , examples = {
		@example(value="[1,2,3,4,5,6] index_of 4", equals="3"), @example(value="[4,2,3,4,5,4] index_of 4", equals="0")}), see = { "at", "last_index_of" })
	public static Integer index_of(final IList l1, final Object o) {
		return nullCheck(l1).indexOf(o);
	}

	@operator(value = "index_of", can_be_const = true, category={IOperatorCategory.MAP})
	@doc(value = "the index of the first occurence of the right operand in the left operand container",
		usages = @usage("if the left operand is a map, index_of returns the index of a value or nil if the value is not mapped"), examples = { @example(value="[1::2, 3::4, 5::6] index_of 4", equals="3")})
	public static Object index_of(final GamaMap<?, ?> m, final Object o) {
		for ( final Map.Entry<?, ?> k : nullCheck(m).entrySet() ) {
			if ( k.getValue().equals(o) ) { return k.getKey(); }
		}
		return null;
	}

	@operator(value = "index_of", can_be_const = true, category={IOperatorCategory.MATRIX})
	@doc(value = "the index of the first occurence of the right operand in the left operand container", 
		usages = @usage(value="if the left operand is a matrix, index_of returns the index as a point", examples = { @example(value="matrix([[1,2,3],[4,5,6]]) index_of 4", equals="{1.0,0.0}")}))
	public static ILocation index_of(final IScope scope, final IMatrix m, final Object o) {
		for ( int i = 0; i < nullCheck(m).getCols(scope); i++ ) {
			for ( int j = 0; j < m.getRows(scope); j++ ) {
				if ( m.get(scope, i, j).equals(o) ) { return new GamaPoint(i, j); }
			}
		}
		return null;
	}

	@operator(value = "last_index_of", can_be_const = true, category={IOperatorCategory.SPECIES})
	@doc(value = "the index of the last occurence of the right operand in the left operand container",
		usages = @usage("if the left operand is a species, the last index of an agent is the same as its index"), see = { "at", "index_of" })
	public static Integer last_index_of(final ISpecies l1, final Object o) {
		return index_of(nullCheck(l1), o);
	}

	@operator(value = "last_index_of", can_be_const = true, category={IOperatorCategory.LIST})
	@doc(value = "the index of the last occurence of the right operand in the left operand container", masterDoc=true, comment = "The definition of last_index_of and the type of the index depend on the container", 
		usages = { @usage(value="if the left operand is a list, last_index_of returns the index as an integer", examples = {
		@example(value="[1,2,3,4,5,6] last_index_of 4", equals="3"), @example(value="[4,2,3,4,5,4] last_index_of 4", equals="5") })}, see = { "at", "last_index_of" })
	public static Integer last_index_of(final IList l1, final Object o) {
		return l1.lastIndexOf(nullCheck(o));
	}

	@operator(value = "last_index_of", can_be_const = true, category={IOperatorCategory.MATRIX})
	@doc(value = "the index of the last occurence of the right operand in the left operand container",
		usages = @usage(value="if the left operand is a matrix, last_index_of returns the index as a point", examples = { @example(value= "matrix([[1,2,3],[4,5,4]]) last_index_of 4", equals="{1.0;2.0}") }))
	public static ILocation last_index_of(final IScope scope, final IMatrix m, final Object o) {
		for ( int i = nullCheck(m).getCols(scope) - 1; i > -1; i-- ) {
			for ( int j = m.getRows(scope) - 1; j > -1; j-- ) {
				if ( m.get(scope, i, j).equals(o) ) { return new GamaPoint(i, j); }
			}
		}
		return null;
	}

	@operator(value = "last_index_of", can_be_const = true, type = ITypeProvider.FIRST_KEY_TYPE, category={IOperatorCategory.MAP})
	@doc(value = "the index of the last occurence of the right operand in the left operand container",
		usages = @usage(value="if the left operand is a map, last_index_of returns the index as a pair", examples = { @example(value="[1::2, 3::4, 5::4] last_index_of 4", equals="5::4")}))
	public static Object last_index_of(final GamaMap m, final Object o) {
		return index_of(m, o);
	}

	@operator(value = "inter", can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category=IOperatorCategory.CONTAINER)
	@doc(value = "the intersection of the two operands", comment = "both containers are transformed into sets (so without duplicated element, cf. remove_deplicates operator) before the set intersection is computed.", usages = {
		@usage(value="if an operand is a graph, it will be transformed into the set of its nodes"),
		@usage(value="if an operand is a map, it will be transformed into the set of its values", examples = {@example(value="[1::2, 3::4, 5::6] inter [2,4]", equals="[2,4]"), @example(value="[1::2, 3::4, 5::6] inter [1,3]", equals="[]")}),
		@usage(value="if an operand is a matrix, it will be transformed into the set of the lines", examples = @example(value="matrix([[1,2,3],[4,5,4]]) inter [3,4]", equals="[4,3]"))  }, examples = {
		@example(value="[1,2,3,4,5,6] inter [2,4]", equals="[2,4]"), @example(value="[1,2,3,4,5,6] inter [0,8]", equals="[]")}, see = { "remove_duplicates" })
	public static IList inter(final IScope scope, final IContainer l1, final IContainer l) {
		return new GamaList(Sets.intersection(Sets.newHashSet(nullCheck(l1).iterable(scope)),
			Sets.newHashSet(nullCheck(l).iterable(scope))));
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category=IOperatorCategory.CONTAINER)
	@doc(value = "returns a new list in which all the elements of the right operand have been removed from the left one", comment = "The behavior of the operator depends on the type of the operands.", 
		usages = { @usage(value="if the right operand is empty, " + IKeyword.MINUS + " returns the left operand"), 
			@usage(value="if both operands are containers, returns a new list in which all the elements of the right operand have been removed from the left one", examples = { @example(value="[1,2,3,4,5,6] - [2,4,9]", equals="[1,3,5,6]"),
		@example(value="[1,2,3,4,5,6] - [0,8]", equals="[1,2,3,4,5,6]") })}, see = { "" + IKeyword.PLUS })
	public static IList minus(final IScope scope, final IContainer source, final IContainer l) {
		final IList result = (IList) nullCheck(source).listValue(scope, Types.NO_TYPE).copy(scope);
		result.removeAll(nullCheck(l).listValue(scope, Types.NO_TYPE));
		return result;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category=IOperatorCategory.CONTAINER)
	@doc(usages = { @usage(value="if the right operand is an object of any type (except list), " + IKeyword.MINUS +
		" returns a list containining the elements of the left operand minus all the occurences of this object", examples = {
		@example(value="[1,2,3,4,5,6] - 2", equals="[1,3,4,5,6]"), @example(value="[1,2,3,4,5,6] - 0", equals="[1,2,3,4,5,6]") })})
	public static IList minus(final IScope scope, final IList l1, final Object object) {
		final IList result = (IList) nullCheck(l1).copy(scope);
		Iterables.removeIf(result, equalTo(object));
		return result;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category=IOperatorCategory.CONTAINER)
	@doc(usages = { @usage(value="if the right operand is an agent of the species, " + IKeyword.MINUS +
		" returns a list containining all the agents of the species minus this agent") })
	public static IList minus(final IScope scope, final ISpecies l1, final IAgent object) {
		return minus(scope, l1.listValue(scope, Types.NO_TYPE), object);
	}

	// PRENDRE EN COMPTE:
	//
	// - index_type
	// - nouvelles valeurs de ITypeProvider

	@operator(value = "of_generic_species", content_type = ITypeProvider.SECOND_CONTENT_TYPE, category=IOperatorCategory.SPECIES)
	@doc(value = "a list, containing the agents of the left-hand operand whose species is that denoted by the right-hand operand "
		+ "and whose species extends the right-hand operand species ", examples = {
		@example(value="// species test {}"),
		@example(value="// species sous_test parent: test {}"),
		@example(value="[sous_test(0),sous_test(1),test(2),test(3)] of_generic_species test 		--: [sous_test0,sous_test1,test2,test3]"),
		@example(value="[sous_test(0),sous_test(1),test(2),test(3)] of_generic_species sous_test 	--: [sous_test0,sous_test1]"),
		@example(value="[sous_test(0),sous_test(1),test(2),test(3)] of_species test 				--: [test2,test3]"),
		@example(value="[sous_test(0),sous_test(1),test(2),test(3)] of_species sous_test 			--: [sous_test0,sous_test1]") }, see = { "of_species" })
	public static IList of_generic_species(final IScope scope, final IContainer agents, final ISpecies s) {
		return of_species(scope, nullCheck(agents), nullCheck(s), true);
	}

	@operator(value = "of_species", content_type = ITypeProvider.SECOND_CONTENT_TYPE, category=IOperatorCategory.SPECIES)
	@doc(value = "a list, containing the agents of the left-hand operand whose species is the one denoted by the right-hand operand."
		+ "The expression agents of_species (species self) is equivalent to agents where (species each = species self); "
		+ "however, the advantage of using the first syntax is that the resulting list is correctly typed with the right species, "
		+ "whereas, in the second syntax, the parser cannot determine the species of the agents within the list "
		+ "(resulting in the need to cast it explicitely if it is to be used in an ask statement, for instance).", usages = @usage("if the right operand is nil, of_species returns the right operand"), examples = {
		@example(value="(self neighbours_at 10) of_species (species (self)) 	--:  all the neighbouring agents of the same species."),
		@example(value="[test(0),test(1),node(1),node(2)] of_species test 		--:  [test0,test1]"),
		@example(value="[1,2,3,4,5,6] of_species test							--:	 []") }, see = { "of_generic_species" })
	public static IList of_species(final IScope scope, final IContainer agents, final ISpecies s) {
		return of_species(scope, nullCheck(agents), nullCheck(s), false);
	}

	private static IList of_species(final IScope scope, final IContainer agents, final ISpecies s, final boolean generic) {

		return new GamaList(Iterables.filter(agents.iterable(scope),
			and(instanceOf(IAgent.class), new Predicate<IAgent>() {

				@Override
				public boolean apply(final IAgent be) {
					return be.isInstanceOf(s, !generic);
				}
			})));
	}

	@operator(value = { "::" }, can_be_const = true, type = IType.PAIR, index_type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.SECOND_TYPE)
	@doc(value = "produces a new pair combining the left and the right operands", special_cases = "nil is not acceptable as a key (although it is as a value). If such a case happens, :: will throw an appropriate error")
	public static GamaPair pair(final Object a, final Object b) {
		return new GamaPair(nullCheck(a), b);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, type = ITypeProvider.BOTH, content_type = ITypeProvider.BOTH, category=IOperatorCategory.CONTAINER)
	@doc(value = "returns a new list containing all the elements of both operands", usages = {
		@usage(value="if one of the operands is nil, " + IKeyword.PLUS + " throws an error"),
		@usage(value="If both operands are species, returns a special type of list called meta-population") }, examples = {
		@example(value="[1,2,3,4,5,6] + [2,4,9]", equals="[1,2,3,4,5,6,2,4,9]"), @example(value="[1,2,3,4,5,6] + [0,8]", equals="[1,2,3,4,5,6,0,8]") }, see = { "" +
		IKeyword.MINUS })
	public static IContainer plus(final IScope scope, final IContainer c1, final IContainer c2) {
		// special case for the addition of two populations or meta-populations
		if ( c1 instanceof IPopulationSet && c2 instanceof IPopulationSet ) { return new MetaPopulation(
			(IPopulationSet) c1, (IPopulationSet) c2); }
		return new GamaList(Iterables.concat(nullCheck(c1).iterable(scope), nullCheck(c2).iterable(scope)));
	}

	// TODO plus / union / inter / minus on maps and graphs and maybe on lists

	@operator(value = IKeyword.PLUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category=IOperatorCategory.CONTAINER)
	@doc(usages = @usage(value="if the right operand is an object of any type (except a container), " + IKeyword.PLUS +
		" returns a list of the elemets of the left operand, to which this object has been added", examples = {
		@example(value="[1,2,3,4,5,6] + 2", equals="[1,2,3,4,5,6,2]"), @example(value="[1,2,3,4,5,6] + 0", equals="[1,2,3,4,5,6,0]") }))
	public static IList plus(final IScope scope, final IContainer l1, final Object l) {
		final IList result = (IList) nullCheck(l1).listValue(scope, Types.NO_TYPE).copy(scope);
		result.add(l);
		return result;
	}

	@operator(value = "union", can_be_const = true, content_type = ITypeProvider.BOTH, category=IOperatorCategory.CONTAINER)
	@doc(value = "returns a new list containing all the elements of both containers without duplicated elements.", comment = "", usages = { @usage("if the left or right operand is nil, union throws an error") }, examples = {
		@example(value="[1,2,3,4,5,6] union [2,4,9]",equals="[1,2,3,4,5,6,9]"), @example(value="[1,2,3,4,5,6] union [0,8]", equals="[0,1,2,3,4,5,6,8]"),
		@example(value="[1,3,2,4,5,6,8,5,6] union [0,8]", equals="[0,1,2,3,4,5,6,8]") }, see = { "inter", IKeyword.PLUS })
	public static IList union(final IScope scope, final IContainer source, final IContainer l) {
		return new GamaList(Sets.union(Sets.newHashSet(nullCheck(source).iterable(scope)),
			Sets.newHashSet(nullCheck(l).iterable(scope))));
	}

	// ITERATORS

	@operator(value = { "group_by" }, iterator = true, index_type = ITypeProvider.SECOND_CONTENT_TYPE, content_type = IType.LIST)
	@doc(value = "Returns a map, where the keys take the possible values of the right-hand operand and the map values are the list of elements "
		+ "of the left-hand operand associated to the key value", masterDoc=true, comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = { @usage("if the left-hand operand is nil, group_by throws an error") }, examples = {
		@example(value="[1,2,3,4,5,6,7,8] group_by (each > 3)", equals="[false::[1, 2, 3], true::[4, 5, 6, 7, 8]] "),
		@example(value="g2 group_by (length(g2 out_edges_of each) )", equals="[ 0::[node9, node7, node10, node8, node11], 1::[node6], 2::[node5], 3::[node4]]"),
		@example(value="(list(node) group_by (round(node(each).location.x))", equals="[32::[node5], 21::[node1], 4::[node0], 66::[node2], 96::[node3]]"),
		@example(value="[1::2, 3::4, 5::6] group_by (each > 4)", equals="[false::[2, 4], true::[6]]") }, see = { "first_with", "last_with","where" })
	public static GamaMap group_by(final IScope scope, final IContainer original, final IExpression filter) {
		// AD: 16/9/13 Bugfix where the lists created could not be used in further computations
		ImmutableListMultimap m = Multimaps.index(nullCheck(original).iterable(scope), function(scope, filter));
		// FluentIterable.from(nullCheck(original).iterable(scope)).index(function(scope, filter)).asMap();
		GamaMap result = new GamaMap();
		for ( Map.Entry<Object, List> entry : (Collection<Map.Entry<Object, List>>) m.asMap().entrySet() ) {
			result.put(entry.getKey(), new GamaList(entry.getValue()));
		}
		return result;
	}

	// FIXME: I DO NOT UNDERSTAND THIS METHOD ! WHAT IS IT SUPPOSED TO DO ?
	@operator(value = { "group_by" }, iterator = true, content_type = IType.MAP)
	@doc(value = "Returns a map, where the keys take the possible values of the right-hand operand and the map values are the list of elements "
		+ "of the left-hand operand associated to the key value")
	public static GamaMap group_by(final IScope scope, final GamaMap original, final IExpression filter)
		throws GamaRuntimeException {
		if ( original == null ) { return new GamaMap(); }

		final GamaMap result = new GamaMap();
		for ( final Object each : original.iterable(scope) ) {
			scope.setEach(each);
			final Object key = filter.value(scope);
			if ( !result.containsKey(key) ) {
				result.put(key, new GamaMap());
			}
			((GamaMap) result.get(key)).add((GamaPair) each);
		}
		return result;
	}

	@operator(value = { "last_with" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, expected_content_type = IType.BOOL, category=IOperatorCategory.CONTAINER)
	@doc(value = "the last element of the left-hand operand that makes the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = { @usage("if the left-hand operand is nil, last_with throws an error."), @usage("If there is no element that satisfies the condition, it returns nil") }, examples = {
		@example(value="[1,2,3,4,5,6,7,8] last_with (each > 3)", equals="8"),
		@example(value="g2 last_with (length(g2 out_edges_of each) = 0 )", equals="node11", isExecutable=false),
		@example(value="(list(node) last_with (round(node(each).location.x) > 32)", equals="node3", isExecutable=false),
		@example(value="[1::2, 3::4, 5::6] last_with (each.key > 4)", equals="5::6") }, see = { "group_by", "first_with", "where" })
	public static Object last_with(final IScope scope, final IContainer original, final IExpression filter) {
		final Iterable it = filter(nullCheck(original).iterable(scope), withPredicate(scope, filter));
		return size(it) == 0 ? null : getLast(it);
	}

	@operator(value = { "first_with" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, expected_content_type = IType.BOOL, category=IOperatorCategory.CONTAINER)
	@doc(value = "the first element of the left-hand operand that makes the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = { @usage("if the left-hand operand is nil, first_with throws an error. If there is no element that satisfies the condition, it returns nil") }, examples = {
		@example(value="[1,2,3,4,5,6,7,8] first_with (each > 3)", equals="4"),
		@example(value="g2 first_with (length(g2 out_edges_of each) = 0)", equals="node9"),
		@example(value="(list(node) first_with (round(node(each).location.x) > 32)", equals="node2"),
		@example(value="[1::2, 3::4, 5::6] first_with (each.key > 4)", equals="5::6") }, see = { "group_by", "last_with", "where" })
	public static Object first_with(final IScope scope, final IContainer original, final IExpression filter) {
		return find(nullCheck(original).iterable(scope), withPredicate(scope, filter), null);
	}

	@operator(value = { "max_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true, category=IOperatorCategory.CONTAINER)
	@doc(value = "the maximum value of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = { @usage("As of GAMA 1.6, if the left-hand operand is nil or empty, max_of throws an error") }, examples = {
		@example(value="[1,2,4,3,5,7,6,8] max_of (each * 100 )", equals="800"), @example(value="g2 max_of (length(g2 out_edges_of each) )", equals="3", isExecutable=false),
		@example(value="(list(node) max_of (round(node(each).location.x))", equals="96"),
		@example(value="[1::2, 3::4, 5::6] max_of (each.value + 3)", equals="9") }, see = { "min_of" })
	public static Object max_of(final IScope scope, final IContainer container, final IExpression filter) {
		final Function f = GAML.<Comparable> function(scope, filter);
		final Object result = f.apply(orderOn(f).max(emptyCheck(scope, container).iterable(scope)));
		return result;
	}

	@operator(value = { "min_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true, category=IOperatorCategory.CONTAINER)
	@doc(value = "the minimum value of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = { @usage("if the left-hand operand is nil or empty, min_of throws an error") }, examples = {
		@example(value="[1,2,4,3,5,7,6,8] min_of (each * 100 )", equals="100"), @example(value="g2 min_of (length(g2 out_edges_of each) )", equals="0", isExecutable=false),
		@example(value="(list(node) min_of (round(node(each).location.x))", equals="4", isExecutable=false),
		@example(value="[1::2, 3::4, 5::6] min_of (each.value + 3)", equals="5") }, see = { "max_of" })
	public static Object min_of(final IScope scope, final IContainer container, final IExpression filter) {
		final Function f = GAML.<Comparable> function(scope, filter);
		final Object result = f.apply(orderOn(f).min(emptyCheck(scope, container).iterable(scope)));
		return result;
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

	@operator(value = "among", content_type = ITypeProvider.SECOND_CONTENT_TYPE, category=IOperatorCategory.CONTAINER)
	@doc(value = "Returns a list of length the value of the left-hand operand, containing random elements from the right-hand operand. As of GAMA 1.6, the order in which the elements are returned can be different than the order in which they appear in the right-hand container", special_cases = {
		"if the right-hand operand is empty, among returns a new empty list. If it is nil, it throws an error.",
		"if the left-hand operand is greater than the length of the right-hand operand, among returns the right-hand operand. If it is smaller or equal to zero, it returns an empty list" }, examples = {
		@example(value="3 among [1,2,4,3,5,7,6,8]", equals="[1,2,8]"), @example(value="3 among g2", equals="[node6,node11,node7]",isExecutable=false),
		@example(value="3 among list(node)", equals="[node1,node11,node4]", isExecutable=false) })
	public static IList among(final IScope scope, final Integer number, final IContainer c) throws GamaRuntimeException {
		final List l = new GamaList(nullCheck(c).listValue(scope, Types.NO_TYPE));
		return new GamaList(Iterables.limit(GAMA.getRandom().shuffle(l), number < 0 ? 0 : number));
		// TODO: reorder with .toSortedList(Ordering.explicit(l)));
	}

	@operator(value = { "sort_by", "sort" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, category=IOperatorCategory.CONTAINER)
	@doc(value = "Returns a list, containing the elements of the left-hand operand sorted in ascending order by the value of the right-hand operand when it is evaluated on them. ", comment = "the left-hand operand is casted to a list before applying the operator. In the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.", special_cases = { "if the left-hand operand is nil, sort_by throws an error" }, examples = {
		@example(value="[1,2,4,3,5,7,6,8] sort_by (each)", equals="[1,2,3,4,5,6,7,8]"),
		@example(value="g2 sort_by (length(g2 out_edges_of each) )", equals="[node9, node7, node10, node8, node11, node6, node5, node4]", isExecutable=false),
		@example(value="(list(node) sort_by (round(node(each).location.x))", equals="[node5, node1, node0, node2, node3]", isExecutable=false),
		@example(value="[1::2, 3::4, 5::6] sort_by (each)", equals="") }, see = { "group_by" })
	public static IList sort(final IScope scope, final IContainer original, final IExpression filter) {
		final Iterable it = nullCheck(original).iterable(scope);
		final int size = size(it);
		if ( size == 0 ) { return GamaList.EMPTY_LIST; }
		if ( size == 1 ) { return GamaList.with(getFirst(it, null)); }
		return new GamaList(orderOn(function(scope, filter)).sortedCopy(it));
	}

	/**
	 * for maps, we sort the keys and reinsert them in this order in the new map
	 * @param scope
	 * @param original
	 * @param filter
	 * @return
	 * @throws GamaRuntimeException
	 */
	// // FIXME Completely false: rewrite this method
	// @operator(value = { "sort_by", "sort" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	// public static GamaMap sort(final IScope scope, final GamaMap original, final IExpression filter)
	// throws GamaRuntimeException {
	// final GamaMap resultMap = new GamaMap(nullCheck(original));
	// // copy in order to prevent any side effect on the left member
	// if ( resultMap.isEmpty() ) { return resultMap; }
	// final IList<GamaPair> sortedPairs = sort(scope, resultMap.getPairs(), filter);
	// for ( final GamaPair pair : sortedPairs ) {
	// resultMap.add(pair);
	// }
	// return resultMap;
	// }

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

	@operator(value = { "where", "select" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, expected_content_type = IType.BOOL, category=IOperatorCategory.CONTAINER)
	@doc(value = "a list containing all the elements of the left-hand operand that make the right-hand operand evaluate to true. ", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = { @usage(value="if the left-hand operand is a list nil, where returns a new empty list") }, examples = {
		@example(value="[1,2,3,4,5,6,7,8] where (each > 3)", equals="[4, 5, 6, 7, 8] "),
		@example(value="g2 where (length(g2 out_edges_of each) = 0 )", equals="[node9, node7, node10, node8, node11]", isExecutable=false),
		@example(value="(list(node) where (round(node(each).location.x) > 32)", equals="[node2, node3]", isExecutable=false),
		@example(value="[1::2, 3::4, 5::6] where (each.value > 4)", equals="") }, see = { "first_with", "last_with", "where" })
	public static IList where(final IScope scope, final IContainer original, final IExpression filter) {
		return new GamaList(filter(nullCheck(original).iterable(scope), withPredicate(scope, filter)));
	}

	@operator(value = { "with_max_of" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, category=IOperatorCategory.CONTAINER)
	@doc(value = "one of elements of the left-hand operand that maximizes the value of the right-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = { @usage(value="if the left-hand operand is nil, with_max_of returns the default value of the right-hand operand") }, examples = {
		@example(value="[1,2,3,4,5,6,7,8] with_max_of (each )", equals="8"),
		@example(value="g2 with_max_of (length(g2 out_edges_of each)  ) ", equals="node4",isExecutable=false),
		@example(value="(list(node) with_max_of (round(node(each).location.x))", equals="node3", isExecutable=false),
		@example(value="[1::2, 3::4, 5::6] with_max_of (each)", equals="6") }, see = { "where", "with_min_of" })
	public static Object with_max_of(final IScope scope, final IContainer container, final IExpression filter) {
		if ( nullCheck(container).isEmpty(scope) ) { return null; }
		return orderOn(function(scope, filter)).max(container.iterable(scope));
	}

	@operator(value = { "with_min_of" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, category=IOperatorCategory.CONTAINER)
	@doc(value = "one of elements of the left-hand operand that minimizes the value of the right-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = { @usage(value="if the left-hand operand is nil, with_max_of returns the default value of the right-hand operand") }, examples = {
			@example(value="[1,2,3,4,5,6,7,8] with_min_of (each )", equals="1"),
		@example(value="g2 with_min_of (length(g2 out_edges_of each)  )", equals="node11", isExecutable=false),
		@example(value="(list(node) with_min_of (round(node(each).location.x))", equals="node0", isExecutable=false),
		@example(value="[1::2, 3::4, 5::6] with_min_of (each)", equals="2") }, see = { "where", "with_max_of" })
	public static Object with_min_of(final IScope scope, final IContainer container, final IExpression filter) {
		if ( nullCheck(container).isEmpty(scope) ) { return null; }
		return orderOn(function(scope, filter)).min(container.iterable(scope));
	}

	@operator(value = { "accumulate" }, content_type = ITypeProvider.SECOND_CONTENT_TYPE_OR_TYPE, iterator = true, category=IOperatorCategory.CONTAINER)
	@doc(value = "returns a new flat list, in which each element is the evaluation of the right-hand operand. If this evaluation returns a list, the elements of this result are added directly to the list returned", comment = "accumulate is dedicated to the application of a same computation on each element of a container (and returns a list) "
		+ "In the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", examples = {
		@example(value="[a1,a2,a3] accumulate (each neighbours_at 10)", equals="a flat list of all the neighbours of these three agents", isExecutable=false),
		@example(value="[1,2,4] accumulate ([2,4])", equals="[2,4,2,4,2,4]") }, see = { "collect" })
	public static IList accumulate(final IScope scope, final IContainer original, final IExpression filter) {
		return new GamaList(Iterables.concat(Iterables.transform(nullCheck(original).iterable(scope),
			iterableFunction(scope, filter))));
	}

	@operator(value = { "interleave" }, content_type = ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE, category=IOperatorCategory.CONTAINER)
	@doc(value = "a new list containing the interleaved elements of the containers contained in the operand", comment = "the operand should be a list of lists of elements. The result is a list of elements. ", examples = {
		@example(value="interleave([1,2,4,3,5,7,6,8])", equals="[1,2,3,4,5,7,6,8]"),
		@example(value="interleave([['e11','e12','e13'],['e21','e22','e23'],['e31','e32','e33']])", equals="['e11','e21','e31','e12','e22','e32','e13','e23','e33']") })
	public static IList interleave(final IScope scope, final IContainer cc) {
		final Iterator it = new InterleavingIterator(toArray(nullCheck(cc).iterable(scope), Object.class));
		return new GamaList(Iterators.toArray(it, Object.class));
	}

	@operator(value = { "count" }, iterator = true, expected_content_type = IType.BOOL, category=IOperatorCategory.CONTAINER)
	@doc(value = "returns an int, equal to the number of elements of the left-hand operand that make the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.", usages = { @usage("if the left-hand operand is nil, count throws an error") }, examples = {
		@example(value="[1,2,3,4,5,6,7,8] count (each > 3)", equals="5"),
		@example(value="// Number of nodes of graph g2 without any out edge"), @example(value="g2 count (length(g2 out_edges_of each) = 0  ) ", equals="5"),
		@example(value="// Number of agents node with x > 32"), @example(value="(list(node) count (round(node(each).location.x) > 32)", equals="2"),
		@example(value="[1::2, 3::4, 5::6] count (each > 4)", equals="1") }, see = { "group_by" })
	public static Integer count(final IScope scope, final IContainer original, final IExpression filter) {
		return size(filter(nullCheck(original).iterable(scope), withPredicate(scope, filter)));
	}

	@operator(value = { "index_by" }, iterator = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.SECOND_TYPE, category=IOperatorCategory.CONTAINER)
	@doc(value = "produces a new map from the evaluation of the right-hand operand for each element of the left-hand operand", usages = { @usage("if the left-hand operand is nil, index_by throws an error.") }, examples = { @example(value="[1,2,3,4,5,6,7,8] index_by (each - 1)", equals="[0::1, 1::2, 2::3, 3::4, 4::5, 5::6, 6::7, 7::8]") }, see = {})
	public static GamaMap index_by(final IScope scope, final IContainer original, final IExpression keyProvider) {
		try {
			return new GamaMap(Maps.uniqueIndex(nullCheck(original).iterable(scope), function(scope, keyProvider)));
		} catch (IllegalArgumentException e) {
			GAMA.reportError(GamaRuntimeException.warning("The key computed by " + Cast.toGaml(keyProvider) +
				" is not unique.", scope), false);
			return group_by(scope, original, keyProvider);
		}
	}

	@operator(value = { "as_map" }, iterator = true, content_type = ITypeProvider.SECOND_CONTENT_TYPE, index_type = ITypeProvider.SECOND_KEY_TYPE, expected_content_type = IType.PAIR, category=IOperatorCategory.MAP)
	@doc(value = "produces a new map from the evaluation of the right-hand operand for each element of the left-hand operand", comment = "the right-hand operand should be a pair", usages = { @usage("if the left-hand operand is nil, as_map throws an error.") }, examples = {
		@example(value="[1,2,3,4,5,6,7,8] as_map (each::(each * 2))", equals="[1::2, 2::4, 3::6, 4::8, 5::10, 6::12, 7::14, 8::16]"),
		@example(value="[1::2,3::4,5::6] as_map (each::(each * 2))", equals="[2::4, 4::8, 6::12] ") }, see = {})
	public static GamaMap as_map(final IScope scope, final IContainer original, final IExpression filter) {
		if ( !(filter instanceof BinaryOperator) ) { throw GamaRuntimeException
			.error("'as_map' expects a pair as second argument"); }
		final BinaryOperator pair = (BinaryOperator) filter;
		if ( !pair.getName().equals("::") ) { throw GamaRuntimeException
			.error("'as_map' expects a pair as second argument"); }
		final Function keyFunction = function(scope, pair.left());
		final Function valueFunction = function(scope, pair.right());
		return new GamaMap(Maps.transformValues(Maps.uniqueIndex(nullCheck(original).iterable(scope), keyFunction),
			valueFunction));
	}

	@operator(value = { "collect" }, content_type = ITypeProvider.SECOND_TYPE, iterator = true, category=IOperatorCategory.CONTAINER)
	@doc(value = "returns a new list, in which each element is the evaluation of the right-hand operand.", comment = "collect is very similar to accumulate except. Nevertheless if the evaluation of the right-hand operand produces a list,"
		+ "the returned list is a list of list of elements. In contrarily, the list produces by accumulate is only a list of elements "
		+ "(all the lists) produced are concaneted. In addition, collect can be applied to any container.", usages = { @usage("if the left-hand operand is nil, collect throws an error") }, examples = {
		@example(value="[1,2,4] collect (each *2)", equals="[2,4,8]"), @example(value="[1,2,4] collect ([2,4])", equals="[[2,4],[2,4],[2,4]]"),
		@example(value="[1::2, 3::4, 5::6] collect (each + 2)", equals="[8,4,6]"),
		@example(value="(list(node) collect (node(each).location.x * 2)", equals="[25.65, 158.99, 140.80, 80.11, 125.47, 37.830, 4.62,...]", isExecutable=true) }, see = { "accumulate" })
	public static IList collect(final IScope scope, final IContainer original, final IExpression filter) {
		// GuiUtils.debug("Containers.collect begin for " + scope.getAgentScope());
		IList list = new GamaList(Iterables.transform(nullCheck(original).iterable(scope), function(scope, filter)));
		// GuiUtils.debug("Containers.collect end");
		return list;
	}
}
