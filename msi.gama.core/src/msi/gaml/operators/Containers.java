/*********************************************************************************************
 *
 *
 * 'Containers.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.operators;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.toArray;
import static msi.gama.util.GAML.emptyCheck;
import static msi.gama.util.GAML.nullCheck;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulationSet;
import msi.gama.metamodel.population.MetaPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.Guava;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.BinaryOperator;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 31 juil. 2010
 *
 * GAML operators dedicated to containers (list, matrix, graph, etc.)
 *
 * @see also IMatrix, IContainer for other operators
 *
 */
public class Containers {

	public static abstract class Range {

		@operator(value = "range", content_type = IType.INT, category = {
				IOperatorCategory.CONTAINER }, can_be_const = true)
		@doc(value = "Allows to build a list of int representing all contiguous values from zero to the argument. The range can be increasing or decreasing. Passing 0 will return a singleton list with 0")
		public static IList range(final IScope scope, final Integer end) {
			if (end == 0)
				return GamaListFactory.create(scope, Types.INT, Integer.valueOf(0));
			return range(scope, 0, end);
		}

		@operator(value = { "range", "to" }, content_type = IType.INT, category = {
				IOperatorCategory.CONTAINER }, can_be_const = true)
		@doc(value = "Allows to build a list of int representing all contiguous values from the first to the second argument. The range can be increasing or decreasing. Passing the same value for both will return a singleton list with this value")
		public static IList range(final IScope scope, final Integer start, final Integer end) {
			final Integer step = start > end ? -1 : 1;
			return range(scope, start, end, step);
		}

		@operator(value = "range", content_type = IType.INT, category = {
				IOperatorCategory.CONTAINER }, can_be_const = true)
		@doc(value = "Allows to build a list of int representing all contiguous values from the first to the second argument, using the step represented by the third argument. The range can be increasing or decreasing. Passing the same value for both will return a singleton list with this value. Passing a step of 0 will result in an exception. Attempting to build infinite ranges (e.g. end > start with a negative step) will similarly not be accepted and yield an exception")
		public static IList range(final IScope scope, final Integer start, final Integer end, final Integer step) {
			if (step == 0)
				throw GamaRuntimeException.error("The step of a range should not be equal to 0", scope);
			if (start == end)
				return GamaListFactory.create(scope, Types.INT, start);
			if (end > start) {
				if (step < 0) {
					throw GamaRuntimeException.error("Negative step would result in an infinite range", scope);
				}
			} else {
				if (step > 0) {
					throw GamaRuntimeException.error("Positive step would result in an infinite range", scope);
				}
			}
			final IList list = GamaListFactory.create(Types.INT);
			for (int i = start; step > 0 ? i <= end : i >= end; i += step) {
				list.add(i);
			}
			return list;
		}

		@operator(value = "every", content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
				IOperatorCategory.CONTAINER }, can_be_const = true)
		@doc(value = "Retrieves elements from the first argument every `step` (second argument) elements. Raises an error if the step is negative or equal to zero")
		public static IList every(final IScope scope, final IList source, final Integer step) {
			if (step <= 0)
				throw GamaRuntimeException.error("The step value in `every` should be strictly positive", scope);
			final IList list = GamaListFactory.create(nullCheck(scope, source).getType().getContentType());
			for (int i = 0; i < source.length(scope); i += step) {
				list.add(source.get(scope, i));
			}
			return list;
		}

		@operator(value = {
				"copy_between" /* , "copy" */ }, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = {
						IOperatorCategory.LIST }, concept = { IConcept.CONTAINER, IConcept.LIST })
		@doc(value = "Returns a copy of the first operand between the indexes determined by the second (inclusive) and third operands (exclusive)", examples = {
				@example(value = " copy_between ([4, 1, 6, 9 ,7], 1, 3)", equals = "[1, 6]") }, usages = {
						@usage("If the first operand is empty, returns an empty object of the same type"),
						@usage("If the second operand is greater than or equal to the third operand, return an empty object of the same type"),
						@usage("If the first operand is nil, raises an error") })
		public static IList copy_between(final IScope scope, final IList l1, final Integer begin, final Integer end) {
			final int beginIndex = begin < 0 ? 0 : begin;
			final int size = nullCheck(scope, l1).size();
			final int endIndex = end > size ? size : end;
			if (beginIndex >= endIndex) {
				return GamaListFactory.create(l1.getType().getContentType());
			}
			return GamaListFactory.createWithoutCasting(l1.getType().getContentType(),
					l1.subList(beginIndex, endIndex));
		}

	}

	@operator(value = { "internal_at" }, content_type = IType.NONE, category = {
			IOperatorCategory.CONTAINER }, concept = { IConcept.CONTAINER, IConcept.GEOMETRY })
	@doc("For internal use only. Corresponds to the implementation, for geometries, of the access to containers with [index]")
	public static Object internal_at(final IScope scope, final IShape shape, final IList indices)
			throws GamaRuntimeException {
		// TODO How to test if the index is correct ?
		if (shape == null) {
			return null;
		}
		final GamaMap map = shape.getAttributes();
		if (map == null) {
			return null;
		}
		return map.getFromIndicesList(scope, indices);
	}

	@operator(value = { "internal_at" }, content_type = IType.NONE, category = {
			IOperatorCategory.CONTAINER }, concept = { IConcept.SPECIES })
	@doc("For internal use only. Corresponds to the implementation, for agents, of the access to containers with [index]")
	public static Object internal_at(final IScope scope, final IAgent agent, final IList indices)
			throws GamaRuntimeException {
		if (agent == null) {
			return null;
		}
		return agent.getFromIndicesList(scope, indices);
	}

	@operator(value = { "grid_at" }, type = ITypeProvider.FIRST_CONTENT_TYPE, category = { IOperatorCategory.POINT,
			IOperatorCategory.GRID }, concept = { IConcept.GRID, IConcept.POINT })
	@doc(value = "returns the cell of the grid (right-hand operand) at the position given by the right-hand operand", comment = "If the left-hand operand is a point of floats, it is used as a point of ints.", usages = {
			@usage("if the left-hand operand is not a grid cell species, returns nil") }, examples = {
					@example(value = "grid_cell grid_at {1,2}", equals = "the agent grid_cell with grid_x=1 and grid_y = 2", isExecutable = false) })
	public static IAgent grid_at(final IScope scope, final ISpecies s, final GamaPoint val)
			throws GamaRuntimeException {
		final ITopology t = scope.getAgentScope().getPopulationFor(s).getTopology();
		final IContainer<?, IShape> m = t.getPlaces();
		if (m instanceof IGrid) {
			final IShape shp = ((IGrid) m).get(scope, val);
			if (shp != null) {
				return shp.getAgent();
			}
		}
		return null;
	}

	@operator(value = "remove_duplicates", can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.FIRST_KEY_TYPE, category = {
			IOperatorCategory.CONTAINER }, concept = { IConcept.CONTAINER })
	@doc(value = "produces a set from the elements of the operand (i.e. a list without duplicated elements)", usages = {
			@usage(value = "if the operand is nil, remove_duplicates returns nil"),
			@usage(value = "if the operand is a graph, remove_duplicates returns the set of nodes"),
			@usage(value = "if the operand is a map, remove_duplicates returns the set of values without duplicate", examples = {
					@example(value = "remove_duplicates([1::3,2::4,3::3,5::7])", equals = "[3,4,7]") }),
			@usage(value = "if the operand is a matrix, remove_duplicates returns a matrix without duplicated row") }, examples = {
					@example(value = "remove_duplicates([3,2,5,1,2,3,5,5,5])", equals = "[3,2,5,1]") })
	// TODO finish doc for other kinds of Container
	public static IList remove_duplicates(final IScope scope, final IContainer l) {
		return GamaListFactory.createWithoutCasting(l.getType().getContentType(),
				Sets.newLinkedHashSet(nullCheck(scope, l).iterable(scope)));
	}

	@operator(value = "contains_all", can_be_const = true, category = { IOperatorCategory.CONTAINER }, concept = {
			IConcept.CONTAINER })
	@doc(value = "true if the left operand contains all the elements of the right operand, false otherwise", comment = "the definition of contains depends on the container", usages = {
			@usage("if the right operand is nil or empty, contains_all returns true") }, examples = {
					@example(value = "[1,2,3,4,5,6] contains_all [2,4]", equals = "true "),
					@example(value = "[1,2,3,4,5,6] contains_all [2,8]", equals = "false"),
					@example(value = "[1::2, 3::4, 5::6] contains_all [1,3]", equals = "false "),
					@example(value = "[1::2, 3::4, 5::6] contains_all [2,4]", equals = "true") }, see = { "contains",
							"contains_any" })
	public static Boolean contains_all(final IScope scope, final IContainer m, final IContainer l) {
		return Iterables.all(nullCheck(scope, l).iterable(scope), Guava.inContainer(scope, m));
	}

	@operator(value = "contains_any", can_be_const = true, category = { IOperatorCategory.CONTAINER }, concept = {
			IConcept.CONTAINER })
	@doc(value = "true if the left operand contains one of the elements of the right operand, false otherwise", comment = "the definition of contains depends on the container", special_cases = {
			"if the right operand is nil or empty, contains_any returns false" }, examples = {
					@example(value = "[1,2,3,4,5,6] contains_any [2,4]", equals = "true "),
					@example(value = "[1,2,3,4,5,6] contains_any [2,8]", equals = "true"),
					@example(value = "[1::2, 3::4, 5::6] contains_any [1,3]", equals = "false"),
					@example(value = "[1::2, 3::4, 5::6] contains_any [2,4]", equals = "true") }, see = { "contains",
							"contains_all" })
	public static Boolean contains_any(final IScope scope, final IContainer c, final IContainer l) {
		return Iterables.any(nullCheck(scope, c).iterable(scope), Guava.inContainer(scope, l));
	}

	@operator(value = { "first" }, can_be_const = true, content_type = ITypeProvider.SECOND_CONTENT_TYPE, category = {
			IOperatorCategory.CONTAINER }, concept = { IConcept.CONTAINER })
	@doc(value = "Returns the nth first elements of the container. If n is greater than the list size, a translation of the container to a list is returned. If it is equal or less than zero, returns an empty list")
	public static IList first(final IScope scope, final Integer number, final IContainer l1) {
		return GamaListFactory.create(scope, l1.getType().getContentType(),
				Iterables.limit(l1.iterable(scope), number < 0 ? 0 : number));
	}

	@operator(value = { "last" }, can_be_const = true, content_type = ITypeProvider.SECOND_CONTENT_TYPE, category = {
			IOperatorCategory.CONTAINER }, concept = { IConcept.CONTAINER })
	@doc(value = "Returns the nth last elements of the container. If n is greater than the list size, a translation of the container to a list is returned. If it is equal or less than zero, returns an empty list")
	public static IList last(final IScope scope, final Integer number, final IContainer l1) {
		final IList result = GamaListFactory.create(scope, l1.getType().getContentType(), Iterables.limit(
				Lists.reverse(nullCheck(scope, l1).listValue(scope, Types.NO_TYPE, false)), number < 0 ? 0 : number));
		Collections.reverse(result);
		return result;
	}

	@operator(value = "in", can_be_const = true, category = { IOperatorCategory.CONTAINER })
	@doc(value = "true if the right operand contains the left operand, false otherwise", comment = "the definition of in depends on the container", usages = {
			@usage("if the right operand is nil or empty, in returns false") }, examples = {
					@example(value = "2 in [1,2,3,4,5,6]", equals = "true"),
					@example(value = "7 in [1,2,3,4,5,6]", equals = "false"),
					@example(value = "3 in [1::2, 3::4, 5::6]", equals = "false"),
					@example(value = "6 in [1::2, 3::4, 5::6]", equals = "true") }, see = { "contains" })
	public static Boolean in(final IScope scope, final Object o, final IContainer source) throws GamaRuntimeException {
		return source.contains(scope, o);
		// return contains(nullCheck(scope, source).iterable(scope), o);
	}

	@operator(value = "index_of", can_be_const = true, category = { IOperatorCategory.SPECIES }, concept = {
			IConcept.CONTAINER, IConcept.SPECIES })
	@doc(value = "the index of the first occurence of the right operand in the left operand container", usages = @usage("if the left operator is a species, returns the index of an agent in a species. If the argument is not an agent of this species, returns -1. Use int(agent) instead"), masterDoc = true)
	public static Integer index_of(final IScope scope, final ISpecies s, final Object o) {
		if (!(o instanceof IAgent)) {
			return -1;
		}
		if (!((IAgent) o).isInstanceOf(nullCheck(scope, s), true)) {
			return -1;
		}
		return ((IAgent) o).getIndex();
	}

	@operator(value = "index_of", can_be_const = true, category = { IOperatorCategory.LIST }, concept = {
			IConcept.LIST })
	@doc(value = "the index of the first occurence of the right operand in the left operand container", masterDoc = true, comment = "The definition of index_of and the type of the index depend on the container", usages = @usage(value = "if the left operand is a list, index_of returns the index as an integer", examples = {
			@example(value = "[1,2,3,4,5,6] index_of 4", equals = "3"),
			@example(value = "[4,2,3,4,5,4] index_of 4", equals = "0") }), see = { "at", "last_index_of" })
	public static Integer index_of(final IScope scope, final IList l1, final Object o) {
		return nullCheck(scope, l1).indexOf(o);
	}

	@operator(value = "index_of", can_be_const = true, category = { IOperatorCategory.MAP }, concept = { IConcept.MAP })
	@doc(value = "the index of the first occurence of the right operand in the left operand container", usages = @usage("if the left operand is a map, index_of returns the index of a value or nil if the value is not mapped"), examples = {
			@example(value = "[1::2, 3::4, 5::6] index_of 4", equals = "3") })
	public static Object index_of(final IScope scope, final GamaMap<?, ?> m, final Object o) {
		for (final Map.Entry<?, ?> k : nullCheck(scope, m).entrySet()) {
			if (k.getValue().equals(o)) {
				return k.getKey();
			}
		}
		return null;
	}

	@operator(value = "index_of", can_be_const = true, category = { IOperatorCategory.MATRIX }, concept = {
			IConcept.CONTAINER, IConcept.MATRIX })
	@doc(value = "the index of the first occurence of the right operand in the left operand container", usages = @usage(value = "if the left operand is a matrix, index_of returns the index as a point", examples = {
			@example(value = "matrix([[1,2,3],[4,5,6]]) index_of 4", equals = "{1.0,0.0}") }))
	public static ILocation index_of(final IScope scope, final IMatrix m, final Object o) {
		for (int i = 0; i < nullCheck(scope, m).getCols(scope); i++) {
			for (int j = 0; j < m.getRows(scope); j++) {
				if (m.get(scope, i, j).equals(o)) {
					return new GamaPoint(i, j);
				}
			}
		}
		return null;
	}

	@operator(value = "last_index_of", can_be_const = true, category = { IOperatorCategory.SPECIES }, concept = {
			IConcept.CONTAINER, IConcept.SPECIES })
	@doc(value = "the index of the last occurence of the right operand in the left operand container", usages = @usage("if the left operand is a species, the last index of an agent is the same as its index"), see = {
			"at", "index_of" })
	public static Integer last_index_of(final IScope scope, final ISpecies l1, final Object o) {
		return index_of(scope, nullCheck(scope, l1), o);
	}

	@operator(value = "last_index_of", can_be_const = true, category = { IOperatorCategory.LIST }, concept = {
			IConcept.LIST })
	@doc(value = "the index of the last occurence of the right operand in the left operand container", masterDoc = true, comment = "The definition of last_index_of and the type of the index depend on the container", usages = {
			@usage(value = "if the left operand is a list, last_index_of returns the index as an integer", examples = {
					@example(value = "[1,2,3,4,5,6] last_index_of 4", equals = "3"),
					@example(value = "[4,2,3,4,5,4] last_index_of 4", equals = "5") }) }, see = { "at",
							"last_index_of" })
	public static Integer last_index_of(final IScope scope, final IList l1, final Object o) {
		return l1.lastIndexOf(nullCheck(scope, o));
	}

	@operator(value = "last_index_of", can_be_const = true, category = { IOperatorCategory.MATRIX }, concept = {
			IConcept.CONTAINER, IConcept.MATRIX })
	@doc(value = "the index of the last occurence of the right operand in the left operand container", usages = @usage(value = "if the left operand is a matrix, last_index_of returns the index as a point", examples = {
			@example(value = "matrix([[1,2,3],[4,5,4]]) last_index_of 4", equals = "{1.0,2.0}") }))
	public static ILocation last_index_of(final IScope scope, final IMatrix m, final Object o) {
		for (int i = nullCheck(scope, m).getCols(scope) - 1; i > -1; i--) {
			for (int j = m.getRows(scope) - 1; j > -1; j--) {
				if (m.get(scope, i, j).equals(o)) {
					return new GamaPoint(i, j);
				}
			}
		}
		return null;
	}

	@operator(value = "last_index_of", can_be_const = true, type = ITypeProvider.FIRST_KEY_TYPE, category = {
			IOperatorCategory.MAP }, concept = { IConcept.MAP })
	@doc(value = "the index of the last occurence of the right operand in the left operand container", usages = @usage(value = "if the left operand is a map, last_index_of returns the index as an int (the key of the pair)", examples = {
			@example(value = "[1::2, 3::4, 5::4] last_index_of 4", equals = "5") }))
	public static Object last_index_of(final IScope scope, final GamaMap<?, ?> m, final Object o) {
		for (final Map.Entry<?, ?> k : Lists.reverse(ImmutableList.copyOf(nullCheck(scope, m).entrySet()))) {
			if (k.getValue().equals(o)) {
				return k.getKey();
			}
		}
		return null;
		// return index_of(m, o);
	}

	@operator(value = "inter", can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = IOperatorCategory.CONTAINER, concept = {
			IConcept.CONTAINER })
	@doc(value = "the intersection of the two operands", comment = "both containers are transformed into sets (so without duplicated element, cf. remove_deplicates operator) before the set intersection is computed.", usages = {
			@usage(value = "if an operand is a graph, it will be transformed into the set of its nodes"),
			@usage(value = "if an operand is a map, it will be transformed into the set of its values", examples = {
					@example(value = "[1::2, 3::4, 5::6] inter [2,4]", equals = "[2,4]"),
					@example(value = "[1::2, 3::4, 5::6] inter [1,3]", equals = "[]") }),
			@usage(value = "if an operand is a matrix, it will be transformed into the set of the lines", examples = @example(value = "matrix([[1,2,3],[4,5,4]]) inter [3,4]", equals = "[3,4]")) }, examples = {
					@example(value = "[1,2,3,4,5,6] inter [2,4]", equals = "[2,4]"),
					@example(value = "[1,2,3,4,5,6] inter [0,8]", equals = "[]") }, see = { "remove_duplicates" })
	public static IList inter(final IScope scope, final IContainer l1, final IContainer l) {
		return GamaListFactory.create(scope,
				l1.getType().getContentType().findCommonSupertypeWith(l.getType().getContentType()),
				Sets.intersection(Sets.newHashSet(nullCheck(scope, l1).iterable(scope)),
						Sets.newHashSet(nullCheck(scope, l).iterable(scope))));
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = IOperatorCategory.CONTAINER, concept = {
			IConcept.CONTAINER })
	@doc(value = "returns a new list in which all the elements of the right operand have been removed from the left one", comment = "The behavior of the operator depends on the type of the operands.", usages = {
			@usage(value = "if both operands are containers and the right operand is empty, " + IKeyword.MINUS
					+ " returns the left operand"),
			@usage(value = "if both operands are containers, returns a new list in which all the elements of the right operand have been removed from the left one", examples = {
					@example(value = "[1,2,3,4,5,6] - [2,4,9]", returnType = "list<int>", equals = "[1,3,5,6]"),
					@example(value = "[1,2,3,4,5,6] - [0,8]", returnType = "list<int>", equals = "[1,2,3,4,5,6]") }) }, see = {
							"" + IKeyword.PLUS })
	public static IList minus(final IScope scope, final IContainer source, final IContainer l) {
		final IList result = (IList) nullCheck(scope, source).listValue(scope, Types.NO_TYPE, false).copy(scope);
		result.removeAll(nullCheck(scope, l).listValue(scope, Types.NO_TYPE, false));
		return result;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = IOperatorCategory.CONTAINER, concept = {
			IConcept.CONTAINER })
	@doc(usages = {
			@usage(value = "if the left operand is a list and the right operand is an object of any type (except list), "
					+ IKeyword.MINUS
					+ " returns a list containing the elements of the left operand minus all the occurrences of this object", examples = {
							@example(value = "[1,2,3,4,5,6] - 2", returnType = "list<int>", equals = "[1,3,4,5,6]"),
							@example(value = "[1,2,3,4,5,6] - 0", returnType = "list<int>", equals = "[1,2,3,4,5,6]") }) })
	public static IList minus(final IScope scope, final IList l1, final Object object) {
		final IList result = (IList) nullCheck(scope, l1).copy(scope);
		Iterables.removeIf(result, equalTo(object));
		return result;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = IOperatorCategory.CONTAINER, concept = {})
	@doc(usages = { @usage(value = "if the left operand is a species and the right operand is an agent of the species, "
			+ IKeyword.MINUS + " returns a list containing all the agents of the species minus this agent") })
	public static IList minus(final IScope scope, final ISpecies l1, final IAgent object) {
		return minus(scope, l1.listValue(scope, Types.NO_TYPE, false), object);
	}

	// PRENDRE EN COMPTE:
	//
	// - index_type
	// - nouvelles valeurs de ITypeProvider

	@operator(value = "of_generic_species", content_type = ITypeProvider.SECOND_CONTENT_TYPE, category = IOperatorCategory.SPECIES, concept = {
			IConcept.SPECIES })
	@doc(value = "a list, containing the agents of the left-hand operand whose species is that denoted by the right-hand operand "
			+ "and whose species extends the right-hand operand species ", examples = {
					@example(value = "// species test {}"), @example(value = "// species sous_test parent: test {}"),
					@example(value = "[sous_test(0),sous_test(1),test(2),test(3)] of_generic_species test", equals = "[sous_test0,sous_test1,test2,test3]", isExecutable = false),
					@example(value = "[sous_test(0),sous_test(1),test(2),test(3)] of_generic_species sous_test", equals = "[sous_test0,sous_test1]", isExecutable = false),
					@example(value = "[sous_test(0),sous_test(1),test(2),test(3)] of_species test", equals = "[test2,test3]", isExecutable = false),
					@example(value = "[sous_test(0),sous_test(1),test(2),test(3)] of_species sous_test", equals = "[sous_test0,sous_test1]", isExecutable = false) }, see = {
							"of_species" })
	public static IList of_generic_species(final IScope scope, final IContainer agents, final ISpecies s) {
		return of_species(scope, nullCheck(scope, agents), nullCheck(scope, s), true);
	}

	@operator(value = "of_species", content_type = ITypeProvider.SECOND_CONTENT_TYPE, category = IOperatorCategory.SPECIES, concept = {
			IConcept.SPECIES })
	@doc(value = "a list, containing the agents of the left-hand operand whose species is the one denoted by the right-hand operand."
			+ "The expression agents of_species (species self) is equivalent to agents where (species each = species self); "
			+ "however, the advantage of using the first syntax is that the resulting list is correctly typed with the right species, "
			+ "whereas, in the second syntax, the parser cannot determine the species of the agents within the list "
			+ "(resulting in the need to cast it explicitly if it is to be used in an ask statement, for instance).", usages = @usage("if the right operand is nil, of_species returns the right operand"), examples = {
					@example(value = "(self neighbors_at 10) of_species (species (self))", equals = "all the neighboring agents of the same species.", isExecutable = false),
					@example(value = "[test(0),test(1),node(1),node(2)] of_species test", equals = "[test0,test1]", isExecutable = false) }, see = {
							"of_generic_species" })
	public static IList of_species(final IScope scope, final IContainer agents, final ISpecies s) {
		return of_species(scope, nullCheck(scope, agents), nullCheck(scope, s), false);
	}

	private static IList of_species(final IScope scope, final IContainer agents, final ISpecies s,
			final boolean generic) {

		return GamaListFactory.create(scope, scope.getModelContext().getTypeNamed(s.getName()),
				Iterables.filter(agents.iterable(scope), and(instanceOf(IAgent.class), new Predicate<IAgent>() {

					@Override
					public boolean apply(final IAgent be) {
						return be.isInstanceOf(s, !generic);
					}
				})));
	}

	@operator(value = {
			"::" }, can_be_const = true, type = IType.PAIR, index_type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.SECOND_TYPE, concept = {
					IConcept.CONTAINER })
	@doc(value = "produces a new pair combining the left and the right operands", special_cases = "nil is not acceptable as a key (although it is as a value). If such a case happens, :: will throw an appropriate error")
	public static GamaPair pair(final IScope scope, final IExpression a, final IExpression b) {
		final Object v1 = a.value(scope);
		final Object v2 = b.value(scope);
		return new GamaPair(nullCheck(scope, v1), v2, a.getType(), b.getType());
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, type = ITypeProvider.BOTH, content_type = ITypeProvider.BOTH, category = IOperatorCategory.CONTAINER, concept = {
			IConcept.CONTAINER })
	@doc(value = "returns a new list containing all the elements of both operands", usages = {
			@usage(value = "if one of the operands is nil, " + IKeyword.PLUS + " throws an error"),
			@usage(value = "if both operands are species, returns a special type of list called meta-population"),
			@usage(value = "if both operands are list, " + IKeyword.PLUS
					+ "returns the concatenation of both lists.", examples = {
							@example(value = "[1,2,3,4,5,6] + [2,4,9]", returnType = "list<int>", equals = "[1,2,3,4,5,6,2,4,9]"),
							@example(value = "[1,2,3,4,5,6] + [0,8]", returnType = "list<int>", equals = "[1,2,3,4,5,6,0,8]") }) }, see = {
									"" + IKeyword.MINUS })
	public static IContainer plus(final IScope scope, final IContainer c1, final IContainer c2) {
		// special case for the addition of two populations or meta-populations
		if (c1 instanceof IPopulationSet && c2 instanceof IPopulationSet) {
			return new MetaPopulation((IPopulationSet) c1, (IPopulationSet) c2);
		}
		return GamaListFactory.create(scope,
				c1.getType().getContentType().findCommonSupertypeWith(c2.getType().getContentType()),
				Iterables.concat(nullCheck(scope, c1).iterable(scope), nullCheck(scope, c2).iterable(scope)));
	}

	// TODO plus / union / inter / minus on maps and graphs and maybe on lists

	@operator(value = IKeyword.PLUS, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category = IOperatorCategory.CONTAINER, concept = {})
	@doc(usages = @usage(value = "if the right operand is an object of any type (except a container), " + IKeyword.PLUS
			+ " returns a list of the elements of the left operand, to which this object has been added", examples = {
					@example(value = "[1,2,3,4,5,6] + 2", returnType = "list<int>", equals = "[1,2,3,4,5,6,2]"),
					@example(value = "[1,2,3,4,5,6] + 0", returnType = "list<int>", equals = "[1,2,3,4,5,6,0]") }))
	public static IList plus(final IScope scope, final IContainer l1, final Object l) {
		final IList result = (IList) nullCheck(scope, l1).listValue(scope, Types.NO_TYPE, false).copy(scope);
		result.add(l);
		return result;
	}

	@operator(value = "union", can_be_const = true, content_type = ITypeProvider.BOTH, category = IOperatorCategory.CONTAINER, concept = {
			IConcept.CONTAINER })
	@doc(value = "returns a new list containing all the elements of both containers without duplicated elements.", comment = "", usages = {
			@usage("if the left or right operand is nil, union throws an error") }, examples = {
					@example(value = "[1,2,3,4,5,6] union [2,4,9]", equals = "[1,2,3,4,5,6,9]"),
					@example(value = "[1,2,3,4,5,6] union [0,8]", equals = "[1,2,3,4,5,6,0,8]"),
					@example(value = "[1,3,2,4,5,6,8,5,6] union [0,8]", equals = "[1,3,2,4,5,6,8,0]") }, see = {
							"inter", IKeyword.PLUS })
	public static IList union(final IScope scope, final IContainer source, final IContainer l) {
		/*
		 * return new GamaList(Sets.union(Sets.newHashSet(nullCheck(scope,
		 * source).iterable(scope)), Sets.newHashSet(nullCheck(scope,
		 * l).iterable(scope))));
		 */
		// New solution less optimized but that keep the order of the first list
		final IList r = GamaListFactory
				.create(source.getType().getContentType().findCommonSupertypeWith(l.getType().getContentType()));
		final LinkedHashSet s = new LinkedHashSet((Collection) plus(scope, source, l));
		r.addAll(s);
		return r;
	}

	// ITERATORS

	@operator(value = {
			"group_by" }, iterator = true, index_type = ITypeProvider.SECOND_TYPE, content_type = ITypeProvider.FIRST_TYPE, concept = {
					IConcept.CONTAINER })
	@doc(value = "Returns a map, where the keys take the possible values of the right-hand operand and the map values are the list of elements "
			+ "of the left-hand operand associated to the key value", masterDoc = true, comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
					@usage("if the left-hand operand is nil, group_by throws an error") }, examples = {
							@example(value = "[1,2,3,4,5,6,7,8] group_by (each > 3)", equals = "[false::[1, 2, 3], true::[4, 5, 6, 7, 8]]"),
							@example(value = "g2 group_by (length(g2 out_edges_of each) )", equals = "[ 0::[node9, node7, node10, node8, node11], 1::[node6], 2::[node5], 3::[node4]]", isExecutable = false),
							@example(value = "(list(node) group_by (round(node(each).location.x))", equals = "[32::[node5], 21::[node1], 4::[node0], 66::[node2], 96::[node3]]", isExecutable = false),
							@example(value = "[1::2, 3::4, 5::6] group_by (each > 4)", equals = "[false::[2, 4], true::[6]]") }, see = {
									"first_with", "last_with", "where" })
	public static GamaMap group_by(final IScope scope, final IContainer original, final IExpression filter) {
		// AD: 16/9/13 Bugfix where the lists created could not be used in
		// further computations
		final ImmutableListMultimap m = Multimaps.index(nullCheck(scope, original).iterable(scope),
				Guava.function(scope, filter));
		final GamaMap result = GamaMapFactory.create(filter.getType(),
				GamaType.from(Types.LIST, Types.INT, original.getType().getContentType()));
		for (final Map.Entry<Object, List> entry : (Collection<Map.Entry<Object, List>>) m.asMap().entrySet()) {
			result.put(entry.getKey(),
					GamaListFactory.create(scope, original.getType().getContentType(), entry.getValue()));
		}
		return result;
	}

	// FIXME: I DO NOT UNDERSTAND THIS METHOD ! WHAT IS IT SUPPOSED TO DO ?
	// @operator(value = { "group_by" }, iterator = true, content_type =
	// IType.MAP)
	// @doc(value =
	// "Returns a map, where the keys take the possible values of the right-hand
	// operand and the map values are the list of elements "
	// + "of the left-hand operand associated to the key value")
	// public static GamaMap group_by(final IScope scope, final GamaMap
	// original, final IExpression filter)
	// throws GamaRuntimeException {
	// if ( original == null ) { return GamaMapFactory.create(); }
	//
	// final GamaMap result = GamaMapFactory.create();
	// for ( final Object each : original.iterable(scope) ) {
	// scope.setEach(each);
	// final Object key = filter.value(scope);
	// if ( !result.containsKey(key) ) {
	// result.put(key, GamaMapFactory.create());
	// }
	// ((GamaMap) result.get(key)).add(Cast.asPair(scope, each));
	// }
	// return result;
	// }

	@operator(value = {
			"last_with" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, expected_content_type = IType.BOOL, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "the last element of the left-hand operand that makes the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage("if the left-hand operand is nil, last_with throws an error."),
			@usage("If there is no element that satisfies the condition, it returns nil"),
			@usage(value = "if the left-operand is a map, the keyword each will contain each value", examples = {
					@example(value = "[1::2, 3::4, 5::6] last_with (each >= 4)", equals = "6"),
					@example(value = "[1::2, 3::4, 5::6].pairs last_with (each.value >= 4)", equals = "5::6") }) }, examples = {
							@example(value = "[1,2,3,4,5,6,7,8] last_with (each > 3)", equals = "8"),
							@example(value = "graph g2 <- graph([]);", isTestOnly = true),
							@example(value = "g2 last_with (length(g2 out_edges_of each) = 0 )", equals = "node11", isExecutable = false),
							@example(value = "(list(node) last_with (round(node(each).location.x) > 32)", equals = "node3", isExecutable = false) }, see = {
									"group_by", "first_with", "where" })
	public static Object last_with(final IScope scope, final IContainer original, final IExpression filter) {
		final Iterable it = filter(nullCheck(scope, original).iterable(scope), Guava.withPredicate(scope, filter));
		return size(it) == 0 ? null : getLast(it);
	}

	@operator(value = {
			"first_with" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, expected_content_type = IType.BOOL, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "the first element of the left-hand operand that makes the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage("if the left-hand operand is nil, first_with throws an error. If there is no element that satisfies the condition, it returns nil"),
			@usage(value = "if the left-operand is a map, the keyword each will contain each value", examples = {
					@example(value = "[1::2, 3::4, 5::6] first_with (each >= 4)", equals = "4"),
					@example(value = "[1::2, 3::4, 5::6].pairs first_with (each.value >= 4)", equals = "3::4") }) }, examples = {
							@example(value = "[1,2,3,4,5,6,7,8] first_with (each > 3)", equals = "4"),
							@example(value = "graph g2 <- graph([]);", isTestOnly = true),
							@example(value = "g2 first_with (length(g2 out_edges_of each) = 0)", equals = "node9", test = false),
							@example(value = "(list(node) first_with (round(node(each).location.x) > 32)", equals = "node2", isExecutable = false) }, see = {
									"group_by", "last_with", "where" })
	public static Object first_with(final IScope scope, final IContainer original, final IExpression filter) {
		return find(nullCheck(scope, original).iterable(scope), Guava.withPredicate(scope, filter), null);
	}

	@operator(value = {
			"max_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "the maximum value of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage("As of GAMA 1.6, if the left-hand operand is nil or empty, max_of throws an error"),
			@usage(value = "if the left-operand is a map, the keyword each will contain each value", examples = {
					@example(value = "[1::2, 3::4, 5::6] max_of (each + 3)", equals = "6") }) }, examples = {
							@example(value = "graph([]) max_of([])", raises = "error", isTestOnly = true),
							@example(value = "[1,2,4,3,5,7,6,8] max_of (each * 100 )", equals = "800"),
							@example(value = "graph g2 <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);"),
							@example(value = "g2.vertices max_of (g2 degree_of( each ))", equals = "2"),
							@example(value = "(list(node) max_of (round(node(each).location.x))", equals = "96", isExecutable = false) }, see = {
									"min_of" })
	public static Object max_of(final IScope scope, final IContainer container, final IExpression filter) {
		final Function f = Guava.<Comparable> function(scope, filter);
		final Object result = f.apply(Guava.orderOn(f).max(emptyCheck(scope, container).iterable(scope)));
		return result;
	}

	@operator(value = { "sum_of" }, type = ITypeProvider.SECOND_TYPE, expected_content_type = { IType.FLOAT,
			IType.POINT, IType.COLOR, IType.INT,
			IType.STRING }, iterator = true, category = IOperatorCategory.CONTAINER, concept = { IConcept.CONTAINER,
					IConcept.FILTER })
	@doc(value = "the sum of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage(value = "if the left-operand is a map, the keyword each will contain each value", examples = {
					@example(value = "[1::2, 3::4, 5::6] sum_of (each + 3)", equals = "21") }) }, examples = {
							@example(value = "graph([]) sum_of([])", equals = "0.0", isTestOnly = true),
							@example(value = "[1,2] sum_of (each * 100 )", equals = "300") }, see = { "min_of",
									"max_of", "product_of", "mean_of" })
	public static Object sum_of(final IScope scope, final IContainer container, final IExpression filter) {
		return Stats.sum(scope, collect(scope, container, filter));
	}

	@operator(value = {
			"product_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "the product of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage(value = "if the left-operand is a map, the keyword each will contain each value", examples = {
					@example(value = "[1::2, 3::4, 5::6] product_of (each)", equals = "48") }) }, examples = {
							@example(value = "graph([]) product_of([])", equals = "0.0", isTestOnly = true),
							@example(value = "[1,2] product_of (each * 10 )", equals = "200") }, see = { "min_of",
									"max_of", "sum_of", "mean_of" })
	public static Object product_of(final IScope scope, final IContainer container, final IExpression filter) {
		return Stats.product(scope, collect(scope, container, filter));
	}

	@operator(value = {
			"mean_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "the mean of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage(value = "if the left-operand is a map, the keyword each will contain each value", examples = {
					@example(value = "[1::2, 3::4, 5::6] mean_of (each)", equals = "4") }) }, examples = {
							@example(value = "graph([]) mean_of([])", equals = "0.0", isTestOnly = true),
							@example(value = "[1,2] mean_of (each * 10 )", equals = "15") }, see = { "min_of", "max_of",
									"sum_of", "product_of" })
	public static Object mean_of(final IScope scope, final IContainer container, final IExpression filter) {
		return Stats.getMean(scope, collect(scope, container, filter));
	}

	@operator(value = {
			"variance_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "the variance of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", see = {
			"min_of", "max_of", "sum_of", "product_of" })
	public static Object variance_of(final IScope scope, final IContainer container, final IExpression filter) {
		return Stats.opVariance(scope, collect(scope, container, filter));
	}

	@operator(value = {
			"min_of" }, type = ITypeProvider.SECOND_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "the minimum value of the right-hand expression evaluated on each of the elements of the left-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage("if the left-hand operand is nil or empty, min_of throws an error"),
			@usage(value = "if the left-operand is a map, the keyword each will contain each value", examples = {
					@example(value = "[1::2, 3::4, 5::6] min_of (each + 3)", equals = "5") }) }, examples = {
							@example(value = "graph([]) min_of([])", raises = "error", isTestOnly = true),
							@example(value = "[1,2,4,3,5,7,6,8] min_of (each * 100 )", equals = "100"),
							@example(value = "graph g2 <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);"),
							@example(value = "g2 min_of (length(g2 out_edges_of each) )", equals = "0"),
							@example(value = "(list(node) min_of (round(node(each).location.x))", equals = "4", isExecutable = false) }, see = {
									"max_of" })
	public static Object min_of(final IScope scope, final IContainer container, final IExpression filter) {
		final Function f = Guava.<Comparable> function(scope, filter);
		final Object result = f.apply(Guava.orderOn(f).min(emptyCheck(scope, container).iterable(scope)));
		return result;
	}

	// @operator(value = "among", content_type =
	// ITypeProvider.RIGHT_CONTENT_TYPE)
	// @doc(special_cases = {
	// "if the right-hand operand is a map, among returns a map of right-hand
	// operand element instead of a list"
	// }, examples = { "2 among [1::2, 3::4, 5::6] --: [1::2, 3::4]" })
	// public static GamaMap among(final IScope scope, final Integer number,
	// final GamaMap l)
	// throws GamaRuntimeException {
	// final GamaMap result = GamaMapFactory.create();
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

	@operator(value = "among", content_type = ITypeProvider.SECOND_CONTENT_TYPE, category = IOperatorCategory.CONTAINER, concept = {
			IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "Returns a list of length the value of the left-hand operand, containing random elements from the right-hand operand. As of GAMA 1.6, the order in which the elements are returned can be different than the order in which they appear in the right-hand container", special_cases = {
			"if the right-hand operand is empty, among returns a new empty list. If it is nil, it throws an error.",
			"if the left-hand operand is greater than the length of the right-hand operand, among returns the right-hand operand (converted as a list). If it is smaller or equal to zero, it returns an empty list" }, examples = {
					@example(value = "3 among [1,2,4,3,5,7,6,8]", returnType = "list<int>", equals = "[1,2,8] (for example)", test = false),
					@example(value = "3 among g2", equals = "[node6,node11,node7]", isExecutable = false),
					@example(value = "3 among list(node)", equals = "[node1,node11,node4]", isExecutable = false),
					@example(value = "1 among [1::2,3::4]", returnType = "list<int>", equals = "2 or 4", test = false) })
	public static IList among(final IScope scope, final Integer number, final IContainer c)
			throws GamaRuntimeException {
		final IList result = GamaListFactory.create(scope, c.getType().getContentType());
		if (number <= 0) {
			return result;
		}
		final IList l = nullCheck(scope, c).listValue(scope, c.getType().getContentType(), false);
		final int size = l.size();
		if (number >= size) {
			return l;
		}
		final int[] indexes = new int[size];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = i;
		}
		scope.getRandom().shuffle(indexes);
		for (int i = 0; i < number; i++) {
			result.add(l.get(indexes[i]));
		}
		return result;
	}

	@operator(value = { "sort_by",
			"sort" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER })
	@doc(value = "Returns a list, containing the elements of the left-hand operand sorted in ascending order by the value of the right-hand operand when it is evaluated on them. ", comment = "the left-hand operand is casted to a list before applying the operator. In the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.", special_cases = {
			"if the left-hand operand is nil, sort_by throws an error" }, examples = {
					@example(value = "[1,2,4,3,5,7,6,8] sort_by (each)", equals = "[1,2,3,4,5,6,7,8]"),
					@example(value = "graph g2 <- graph([]);", isTestOnly = true),
					@example(value = "g2 sort_by (length(g2 out_edges_of each) )", equals = "[node9, node7, node10, node8, node11, node6, node5, node4]", test = false),
					@example(value = "(list(node) sort_by (round(node(each).location.x))", equals = "[node5, node1, node0, node2, node3]", isExecutable = false),
					@example(value = "[1::2, 5::6, 3::4] sort_by (each)", equals = "[2, 4, 6]") }, see = { "group_by" })
	public static IList sort(final IScope scope, final IContainer original, final IExpression filter) {
		final Iterable it = nullCheck(scope, original).iterable(scope);
		final int size = size(it);
		if (size == 0) {
			return GamaListFactory.EMPTY_LIST;
		}
		// if ( size == 1 ) { return
		// GamaListFactory.createWithoutCasting(original.getType().getContentType(),
		// getFirst(it, null)); }
		return GamaListFactory.createWithoutCasting(original.getType().getContentType(),
				Guava.orderOn(Guava.function(scope, filter)).sortedCopy(it));
	}

	/**
	 * for maps, we sort the keys and reinsert them in this order in the new map
	 * 
	 * @param scope
	 * @param original
	 * @param filter
	 * @return
	 * @throws GamaRuntimeException
	 */
	// // FIXME Completely false: rewrite this method
	// @operator(value = { "sort_by", "sort" }, content_type =
	// ITypeProvider.FIRST_CONTENT_TYPE, iterator = true)
	// public static GamaMap sort(final IScope scope, final GamaMap original,
	// final IExpression filter)
	// throws GamaRuntimeException {
	// final GamaMap resultMap = GamaMapFactory.create(nullCheck(scope,
	// original));
	// // copy in order to prevent any side effect on the left member
	// if ( resultMap.isEmpty() ) { return resultMap; }
	// final IList<GamaPair> sortedPairs = sort(scope, resultMap.getPairs(),
	// filter);
	// for ( final GamaPair pair : sortedPairs ) {
	// resultMap.add(pair);
	// }
	// return resultMap;
	// }

	// @operator(value = { "where", "select" }, priority = IPriority.ITERATOR,
	// iterator = true)
	// public static GamaMap where(final IScope scope, final GamaMap original,
	// final IExpression
	// filter)
	// throws GamaRuntimeException {
	// if ( original == null ) { return GamaMapFactory.create(); }
	// final GamaMap result = GamaMapFactory.create();
	// for ( GamaPair p : original.iterable(scope) ) {
	// scope.setEach(p);
	// if ( Cast.asBool(scope, filter.value(scope)) ) {
	// result.add(p);
	// }
	// }
	// return result;
	// }

	@operator(value = { "where",
			"select" }, content_type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, expected_content_type = IType.BOOL, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "a list containing all the elements of the left-hand operand that make the right-hand operand evaluate to true. ", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage(value = "if the left-hand operand is a list nil, where returns a new empty list"),
			@usage(value = "if the left-operand is a map, the keyword each will contain each value", examples = {
					@example(value = "[1::2, 3::4, 5::6] where (each >= 4)", equals = "[4, 6]") }) }, examples = {
							@example(value = "[1,2,3,4,5,6,7,8] where (each > 3)", equals = "[4, 5, 6, 7, 8] "),
							@example(value = "graph g2 <- graph([]);", isTestOnly = true),
							@example(value = "g2 where (length(g2 out_edges_of each) = 0 )", equals = "[node9, node7, node10, node8, node11]", test = false),
							@example(value = "(list(node) where (round(node(each).location.x) > 32)", equals = "[node2, node3]", isExecutable = false) }, see = {
									"first_with", "last_with", "where" })
	public static IList where(final IScope scope, final IContainer original, final IExpression filter) {
		return GamaListFactory.create(scope, original.getType().getContentType(),
				filter(nullCheck(scope, original).iterable(scope), Guava.withPredicate(scope, filter)));
	}

	@operator(value = {
			"with_max_of" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "one of elements of the left-hand operand that maximizes the value of the right-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage(value = "if the left-hand operand is nil, with_max_of returns the default value of the right-hand operand") }, examples = {
					@example(value = "[1,2,3,4,5,6,7,8] with_max_of (each )", equals = "8"),
					@example(value = "graph g2 <- graph([]);", isTestOnly = true),
					@example(value = "g2 with_max_of (length(g2 out_edges_of each)  ) ", equals = "node4", test = false),
					@example(value = "(list(node) with_max_of (round(node(each).location.x))", equals = "node3", isExecutable = false),
					@example(value = "[1::2, 3::4, 5::6] with_max_of (each)", equals = "6") }, see = { "where",
							"with_min_of" })
	public static Object with_max_of(final IScope scope, final IContainer container, final IExpression filter) {
		if (nullCheck(scope, container).isEmpty(scope)) {
			return null;
		}
		return Guava.orderOn(Guava.function(scope, filter)).max(container.iterable(scope));
	}

	@operator(value = {
			"with_min_of" }, type = ITypeProvider.FIRST_CONTENT_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER, IConcept.FILTER })
	@doc(value = "one of elements of the left-hand operand that minimizes the value of the right-hand operand", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ", usages = {
			@usage(value = "if the left-hand operand is nil, with_max_of returns the default value of the right-hand operand") }, examples = {
					@example(value = "[1,2,3,4,5,6,7,8] with_min_of (each )", equals = "1"),
					@example(value = "graph g2 <- graph([]);", isTestOnly = true),
					@example(value = "g2 with_min_of (length(g2 out_edges_of each)  )", equals = "node11", test = false),
					@example(value = "(list(node) with_min_of (round(node(each).location.x))", equals = "node0", isExecutable = false),
					@example(value = "[1::2, 3::4, 5::6] with_min_of (each)", equals = "2") }, see = { "where",
							"with_max_of" })
	public static Object with_min_of(final IScope scope, final IContainer container, final IExpression filter) {
		if (nullCheck(scope, container).isEmpty(scope)) {
			return null;
		}
		return Guava.orderOn(Guava.function(scope, filter)).min(container.iterable(scope));
	}

	@operator(value = {
			"accumulate" }, content_type = ITypeProvider.SECOND_CONTENT_TYPE_OR_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER })
	@doc(value = "returns a new flat list, in which each element is the evaluation of the right-hand operand. If this evaluation returns a list, the elements of this result are added directly to the list returned", comment = "accumulate is dedicated to the application of a same computation on each element of a container (and returns a list). "
			+ "In the right-hand operand, the keyword each can be used to represent, in turn, each of the left-hand operand elements. ", examples = {
					@example(value = "[a1,a2,a3] accumulate (each neighbors_at 10)", equals = "a flat list of all the neighbors of these three agents", isExecutable = false),
					@example(value = "[1,2,4] accumulate ([2,4])", returnType = "list<int>", equals = "[2,4,2,4,2,4]"),
					@example(value = "[1,2,4] accumulate (each * 2)", returnType = "list<int>", equals = "[2,4,8]") }, see = {
							"collect" })
	public static IList accumulate(final IScope scope, final IContainer original, final IExpression filter) {
		// WARNING TODO The resulting type is not computed
		IType type = filter.getType();
		if (type.isContainer()) {
			type = type.getContentType();
		}

		final IList collection = collect(scope, original, filter);
		final Iterable<? extends Iterable<? extends Object>> inputs = Iterables.transform(collection,
				Guava.transformToIterables);

		// Iterable source = nullCheck(scope, original).iterable(scope);
		// Function<Object, Iterable> function = Guava.function(scope, filter);
		// Iterable<? extends Iterable<? extends Object>> inputs =
		// Iterables.transform(source, function);
		final Iterable result = Iterables.concat(inputs);
		return GamaListFactory.create(scope, type, result);
	}

	@operator(value = {
			"collect" }, content_type = ITypeProvider.SECOND_TYPE, iterator = true, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER })
	@doc(value = "returns a new list, in which each element is the evaluation of the right-hand operand.", comment = "collect is similar to accumulate except that accumulate always produces flat lists if the right-hand operand returns a list."
			+ "In addition, collect can be applied to any container.", usages = {
					@usage("if the left-hand operand is nil, collect throws an error") }, examples = {
							@example(value = "[1,2,4] collect (each *2)", equals = "[2,4,8]"),
							@example(value = "[1,2,4] collect ([2,4])", equals = "[[2,4],[2,4],[2,4]]"),
							@example(value = "[1::2, 3::4, 5::6] collect (each + 2)", equals = "[4,6,8]"),
							@example(value = "(list(node) collect (node(each).location.x * 2)", equals = "the list of nodes with their x multiplied by 2", isExecutable = false) }, see = {
									"accumulate" })
	public static IList collect(final IScope scope, final IContainer original, final IExpression filter) {
		final IList list = GamaListFactory.create(scope, filter.getType(),
				Iterables.transform(nullCheck(scope, original).iterable(scope), Guava.function(scope, filter)));
		return list;
	}

	@operator(value = {
			"interleave" }, content_type = ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER })
	@doc(value = "a new list containing the interleaved elements of the containers contained in the operand", comment = "the operand should be a list of lists of elements. The result is a list of elements. ", examples = {
			@example(value = "interleave([1,2,4,3,5,7,6,8])", equals = "[1,2,4,3,5,7,6,8]"),
			@example(value = "interleave([['e11','e12','e13'],['e21','e22','e23'],['e31','e32','e33']])", equals = "['e11','e21','e31','e12','e22','e32','e13','e23','e33']") })
	public static IList interleave(final IScope scope, final IContainer cc) {
		final Iterable iterable = nullCheck(scope, cc).iterable(scope);
		IType type = cc.getType().getContentType();
		if (type.isContainer()) {
			type = type.getContentType();
		}
		final Iterator it = new Guava.InterleavingIterator(toArray(iterable, Object.class));
		return GamaListFactory.create(scope, type, it);
	}

	@operator(value = {
			"count" }, iterator = true, expected_content_type = IType.BOOL, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER })
	@doc(value = "returns an int, equal to the number of elements of the left-hand operand that make the right-hand operand evaluate to true.", comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.", usages = {
			@usage("if the left-hand operand is nil, count throws an error") }, examples = {
					@example(value = "[1,2,3,4,5,6,7,8] count (each > 3)", equals = "5"),
					@example(value = "// Number of nodes of graph g2 without any out edge"),
					@example(value = "graph g2 <- graph([]);"),
					@example(value = "g2 count (length(g2 out_edges_of each) = 0  ) ", equals = "the total number of out edges", test = false),
					@example(value = "// Number of agents node with x > 32"),
					@example(value = "int n <- (list(node) count (round(node(each).location.x) > 32);", isExecutable = false),
					@example(value = "[1::2, 3::4, 5::6] count (each > 4)", equals = "1") }, see = { "group_by" })
	public static Integer count(final IScope scope, final IContainer original, final IExpression filter) {
		return size(filter(nullCheck(scope, original).iterable(scope), Guava.withPredicate(scope, filter)));
	}

	@operator(value = {
			"index_by" }, iterator = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.SECOND_TYPE, category = IOperatorCategory.CONTAINER, concept = {
					IConcept.CONTAINER })
	@doc(value = "produces a new map from the evaluation of the right-hand operand for each element of the left-hand operand", usages = {
			@usage("if the left-hand operand is nil, index_by throws an error.") }, examples = {
					@example(value = "[1,2,3,4,5,6,7,8] index_by (each - 1)", equals = "[0::1, 1::2, 2::3, 3::4, 4::5, 5::6, 6::7, 7::8]") }, see = {})
	public static GamaMap index_by(final IScope scope, final IContainer original, final IExpression keyProvider) {
		try {
			final Map result = Maps.uniqueIndex(nullCheck(scope, original).iterable(scope),
					Guava.function(scope, keyProvider));
			return GamaMapFactory.createWithoutCasting(keyProvider.getType(), original.getType().getContentType(),
					result);
		} catch (final IllegalArgumentException e) {
			GAMA.reportError(scope, GamaRuntimeException
					.warning("The key computed by " + Cast.toGaml(keyProvider) + " is not unique.", scope), false);
			return group_by(scope, original, keyProvider);
		}
	}

	@operator(value = {
			"as_map" }, iterator = true, content_type = ITypeProvider.SECOND_CONTENT_TYPE, index_type = ITypeProvider.SECOND_KEY_TYPE, expected_content_type = IType.PAIR, category = IOperatorCategory.MAP, concept = {
					IConcept.CONTAINER, IConcept.MAP })
	@doc(value = "produces a new map from the evaluation of the right-hand operand for each element of the left-hand operand", comment = "the right-hand operand should be a pair", usages = {
			@usage("if the left-hand operand is nil, as_map throws an error.") }, examples = {
					@example(value = "[1,2,3,4,5,6,7,8] as_map (each::(each * 2))", returnType = "map<int,int>", equals = "[1::2, 2::4, 3::6, 4::8, 5::10, 6::12, 7::14, 8::16]"),
					@example(value = "[1::2,3::4,5::6] as_map (each::(each * 2))", returnType = "map<int,int>", equals = "[2::4, 4::8, 6::12] ") }, see = {})
	public static GamaMap as_map(final IScope scope, final IContainer original, final IExpression filter) {
		if (!(filter instanceof BinaryOperator)) {
			throw GamaRuntimeException.error("'as_map' expects a pair as second argument", scope);
		}
		final BinaryOperator pair = (BinaryOperator) filter;
		if (!pair.getName().equals("::")) {
			throw GamaRuntimeException.error("'as_map' expects a pair as second argument", scope);
		}
		final Function keyFunction = Guava.function(scope, pair.arg(0));
		final Function valueFunction = Guava.function(scope, pair.arg(1));
		final Map result = Maps.transformValues(
				Maps.uniqueIndex(nullCheck(scope, original).iterable(scope), keyFunction), valueFunction);
		return GamaMapFactory.createWithoutCasting(pair.arg(0).getType(), pair.arg(1).getType(), result);
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, type = ITypeProvider.BOTH, content_type = ITypeProvider.BOTH, category = IOperatorCategory.CONTAINER, concept = {
			IConcept.CONTAINER })
	@doc(value = "returns a new map containing all the elements of both operands", examples = {
			@example(value = "['a'::1,'b'::2] + ['c'::3]", equals = "['a'::1,'b'::2,'c'::3]"),
			@example(value = "['a'::1,'b'::2] + [5::3.0]", equals = "['a'::1.0,'b'::2.0,5::3.0]") }, see = {
					"" + IKeyword.MINUS })
	public static GamaMap plus(final IScope scope, final GamaMap m1, final GamaMap m2) {
		final IType type = GamaType.findCommonType(m1.getType(), m2.getType());
		final GamaMap res = GamaMapFactory.createWithoutCasting(type.getKeyType(), type.getContentType(), m1);
		res.putAll(m2);
		return res;
	}

	@operator(value = IKeyword.PLUS, can_be_const = true, type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.BOTH, category = IOperatorCategory.CONTAINER, concept = {
			IConcept.CONTAINER })
	@doc(value = "returns a new map containing all the elements of both operands", examples = {
			@example(value = "['a'::1,'b'::2] + ('c'::3)", equals = "['a'::1,'b'::2,'c'::3]"),
			@example(value = "['a'::1,'b'::2] + ('c'::3)", equals = "['a'::1,'b'::2,'c'::3]") }, see = {
					"" + IKeyword.MINUS })
	public static GamaMap plus(final IScope scope, final GamaMap m1, final GamaPair m2) {
		final IType type = GamaType.findCommonType(m1.getType(), m2.getType());
		final GamaMap res = GamaMapFactory.createWithoutCasting(type.getKeyType(), type.getContentType(), m1);
		res.put(m2.key, m2.value);
		return res;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, type = ITypeProvider.BOTH, content_type = ITypeProvider.BOTH, category = IOperatorCategory.CONTAINER, concept = {})
	@doc(value = "returns a new map containing all the elements of the first operand not present in the second operand", examples = {
			@example(value = "['a'::1,'b'::2] - ['b'::2]", equals = "['a'::1]"),
			@example(value = "['a'::1,'b'::2] - ['b'::2,'c'::3]", equals = "['a'::1]") }, see = { "" + IKeyword.MINUS })
	public static GamaMap minus(final IScope scope, final GamaMap m1, final GamaMap m2) {
		// special case for the addition of two populations or meta-populations
		// final GamaMap res=(GamaMap) nullCheck(m1).mapValue(scope,
		// Types.NO_TYPE).copy(scope);
		final GamaMap res = nullCheck(scope, m1).copy(scope);
		res.removeValues(scope, m2);
		return res;
	}

	@operator(value = IKeyword.MINUS, can_be_const = true, type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.BOTH, category = IOperatorCategory.CONTAINER, concept = {})
	@doc(value = "returns a new map containing all the elements of the first operand without the one of the second operand", examples = {
			@example(value = "['a'::1,'b'::2] - ('b'::2)", equals = "['a'::1]"),
			@example(value = "['a'::1,'b'::2] - ('c'::3)", equals = "['a'::1,'b'::2]") }, see = { "" + IKeyword.MINUS })
	public static GamaMap minus(final IScope scope, final GamaMap m1, final GamaPair m2) {
		// special case for the addition of two populations or meta-populations
		// final GamaMap res=(GamaMap) nullCheck(m1).mapValue(scope,
		// Types.NO_TYPE).copy(scope);
		final GamaMap res = nullCheck(scope, m1).copy(scope);
		res.remove(m2.getKey());
		return res;
	}

}
