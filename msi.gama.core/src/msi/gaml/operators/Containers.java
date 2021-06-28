/*******************************************************************************************************
 *
 * msi.gaml.operators.Containers.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import static com.google.common.collect.Iterables.toArray;
import static msi.gaml.compilation.GAML.notNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import msi.gama.common.interfaces.IGamlIssue;
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
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaListFactory.GamaListSupplier;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaMapFactory.GamaMapSupplier;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.IGraph;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.IOperatorValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.BinaryOperator;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

/**
 * Written by drogoul Modified on 31 juil. 2010
 *
 * GAML operators dedicated to containers (list, matrix, graph, etc.)
 *
 * @see also IMatrix, IContainer for other operators
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class Containers {

	private static class InterleavingIterator extends AbstractIterator {

		private final Queue<Iterator> queue = new ArrayDeque<>();

		public InterleavingIterator(final IScope scope, final Object... objects) {
			for (final Object object : objects) {
				if (object instanceof IContainer) {
					queue.add(((IContainer) object).iterable(scope).iterator());
				} else if (object instanceof Iterator) {
					queue.add((Iterator) object);
				} else if (object instanceof Iterable) {
					queue.add(((Iterable) object).iterator());
				} else {
					queue.add(Iterators.singletonIterator(object));
				}
			}
		}

		@Override
		protected Object computeNext() {
			while (!queue.isEmpty()) {
				final Iterator topIter = queue.poll();
				if (topIter.hasNext()) {
					final Object result = topIter.next();
					queue.offer(topIter);
					return result;
				}
			}
			return endOfData();
		}
	}

	public static <T> Function<Object, T> with(final IScope scope, final IExpression filter) {
		return t -> {
			scope.setEach(t);
			return (T) filter.value(scope);
		};
	}

	public static <T> Predicate<T> by(final IScope scope, final IExpression filter) {
		return (final T t) -> {
			scope.setEach(t);
			return (Boolean) filter.value(scope);
		};
	}

	public static <T> Predicate<T> inContainer(final IScope scope, final IContainer l) {
		final IContainer c = GAML.notNull(scope, l);
		return t -> c.contains(scope, t);
	}

	private static Function<Object, IList<?>> toLists =
			a -> a instanceof IList ? (IList) a : GamaListFactory.wrap(Types.NO_TYPE, a);

	private static StreamEx stream(final IScope scope, final IContainer c) {
		return notNull(scope, c).stream(scope);
	}

	public static GamaListSupplier listOf(final IType t) {
		return new GamaListSupplier(t);
	}

	public static Supplier<IList> listLike(final IContainer c) {
		return new GamaListSupplier(c == null ? Types.NO_TYPE : c.getGamlType().getContentType());
	}

	public static Supplier<IList> listLike(final IContainer c, final IContainer c1) {
		return listOf(c.getGamlType().getContentType().findCommonSupertypeWith(c1.getGamlType().getContentType()));
	}

	public static GamaMapSupplier asMapOf(final IType k, final IType v) {
		return new GamaMapSupplier(k, v);
	}

	public static abstract class Range {

		@operator (
				value = "range",
				content_type = IType.INT,
				category = { IOperatorCategory.CONTAINER },
				can_be_const = true)
		@doc (
				value = "builds a list of int representing all contiguous values from zero to the argument. The range can be increasing or decreasing.",
				masterDoc = true,
				special_cases = "Passing 0 will return a singleton list with 0.")
		@test ("range(2) = [0,1,2]")
		public static IList range(final IScope scope, final Integer end) {
			if (end == 0) return GamaListFactory.wrap(Types.INT, Integer.valueOf(0));
			return range(scope, 0, end);
		}

		@operator (
				value = { "range", "to" },
				content_type = IType.INT,
				category = { IOperatorCategory.CONTAINER },
				can_be_const = true)
		@doc (
				value = "the list of int representing all contiguous values from the first to the second argument.",
				usages = { @usage (
						value = "When used with 2 operands, it returns the list of int representing all contiguous values from the first to the second argument. "
								+ "Passing the same value for both will return a singleton list with this value",
						examples = { @example (
								value = "range(0,2)",
								equals = "[0,1,2]") }) })
		@test ("range(0,2) = [0,1,2]")
		public static IList range(final IScope scope, final Integer start, final Integer end) {
			final Integer step = start > end ? -1 : 1;
			return range(scope, start, end, step);
		}

		@operator (
				value = "range",
				content_type = IType.INT,
				category = { IOperatorCategory.CONTAINER },
				can_be_const = true)
		@doc (
				value = "a list of int representing all contiguous values from the first to the second argument, using the step represented by the third argument.",
				usages = { @usage (
						value = "When used with 3 operands, it returns a list of int representing all contiguous values from the first to the second argument, using the step represented by the third argument. The range can be increasing or decreasing. Passing the same value for both will return a singleton list with this value. Passing a step of 0 will result in an exception. Attempting to build infinite ranges (e.g. end > start with a negative step) will similarly not be accepted and yield an exception",
						examples = { @example (
								value = "range(0,6,2)",
								equals = "[0,2,4,6]") }) })
		public static IList range(final IScope scope, final Integer start, final Integer end, final Integer step) {
			if (step == 0) throw GamaRuntimeException.error("The step of a range should not be equal to 0", scope);
			if (start.equals(end)) return GamaListFactory.wrap(Types.INT, start);
			if (end > start) {
				if (step < 0)
					throw GamaRuntimeException.error("Negative step would result in an infinite range", scope);
			} else {
				if (step > 0)
					throw GamaRuntimeException.error("Positive step would result in an infinite range", scope);
			}
			return IntStreamEx.rangeClosed(start, end, step).boxed().toCollection(listOf(Types.INT));

		}

		@operator (
				value = "every",
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.CONTAINER },
				can_be_const = true)
		@doc (
				value = "Retrieves elements from the first argument every `step` (second argument) elements. Raises an error if the step is negative or equal to zero")
		@test ("[1,2,3,4,5] every 2 = [1,3,5]")
		public static IList every(final IScope scope, final IList source, final Integer step) {
			if (step <= 0)
				throw GamaRuntimeException.error("The step value in `every` should be strictly positive", scope);
			return IntStreamEx.range(0, notNull(scope, source).size(), step).mapToObj(source::get)
					.toCollection(listLike(source));
		}

		@operator (
				value = { "copy_between", "between" /* , "copy" */ },
				can_be_const = true,
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.LIST },
				concept = { IConcept.CONTAINER, IConcept.LIST })
		@doc (
				value = "Returns a copy of the first operand between the indexes determined by the second (inclusive) and third operands (exclusive)",
				examples = { @example (
						value = " copy_between ([4, 1, 6, 9 ,7], 1, 3)",
						equals = "[1, 6]") },
				usages = { @usage ("If the first operand is empty, returns an empty object of the same type"),
						@usage ("If the second operand is greater than or equal to the third operand, return an empty object of the same type"),
						@usage ("If the first operand is nil, raises an error") })
		@test ("copy_between ([4, 1, 6, 9 ,7], 1, 3) = [1,6]")
		public static IList copy_between(final IScope scope, final IList l1, final Integer begin, final Integer end) {
			final int beginIndex = begin < 0 ? 0 : begin;
			final int size = notNull(scope, l1).size();
			final int endIndex = end > size ? size : end;
			final IList result = listLike(l1).get();
			if (beginIndex < endIndex) { result.addAll(l1.subList(beginIndex, endIndex)); }
			return result;
		}

		@operator (
				internal = true,
				value = { "internal_between" },
				can_be_const = true,
				content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				category = { IOperatorCategory.LIST },
				concept = { IConcept.CONTAINER, IConcept.LIST })
		@doc (
				value = "For internal use only. Corresponds to the implementation, for containers, of the access with [begin::end]",
				masterDoc = true)
		public static IList copy_between(final IScope scope, final IList l1, final GamaPair p) {
			return copy_between(scope, l1, Cast.asInt(scope, p.key), Cast.asInt(scope, p.value));
		}

	}

	@operator (
			internal = true,
			value = { "internal_list" },
			content_type = IType.INT,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER, IConcept.MATRIX })
	@doc (
			value = "For internal use only.Corresponds to the 2 elements list created when accessed matrices with int cols and rows",
			masterDoc = true)
	@no_test
	public static IList internal_list(final IScope scope, final Integer i, final Integer j) {
		return GamaListFactory.create(scope, Types.INT, i, j);
	}

	@operator (
			internal = true,
			value = { "internal_at" },
			content_type = IType.NONE,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER, IConcept.GEOMETRY })
	@doc (
			value = "For internal use only. Corresponds to the implementation, for geometries, of the access to containers with [index]",
			masterDoc = true)
	@no_test
	public static Object internal_at(final IScope scope, final IShape shape, final IList indices)
			throws GamaRuntimeException {
		// TODO How to test if the index is correct ?
		if (shape == null) return null;
		final String key = Cast.asString(scope, indices.get(0));
		return shape.getAttribute(key);
		// final IMap map = (IMap) shape.getAttributes();
		// if (map == null) { return null; }
		// return map.getFromIndicesList(scope, indices);
	}

	@operator (
			internal = true,
			value = { "internal_at" },
			content_type = IType.NONE,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.SPECIES })
	@doc ("For internal use only. Corresponds to the implementation of the access to agents with [index]")
	@no_test
	public static Object internal_at(final IScope scope, final IAgent agent, final IList indices)
			throws GamaRuntimeException {
		if (agent == null) return null;
		return agent.getFromIndicesList(scope, indices);
	}

	@operator (
			internal = true,
			value = { "internal_at" },
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "For internal use only. Corresponds to the implementation of the access to containers with [index]",
			see = { IKeyword.AT })
	@no_test
	@validator (InternalAtValidator.class)
	public static Object internal_at(final IScope scope, final IContainer container, final IList indices)
			throws GamaRuntimeException {
		if (container instanceof IContainer.Addressable)
			return ((IContainer.Addressable) container).getFromIndicesList(scope, indices);
		throw GamaRuntimeException.error("" + container + " cannot be accessed using " + indices, scope);
	}

	@operator (
			value = { IKeyword.AT, "@" },
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "the element at the right operand index of the container",
			masterDoc = true,
			comment = "The first element of the container is located at the index 0. "
					+ "In addition, if the user tries to get the element at an index higher or equals than the length of the container, he will get an IndexOutOfBoundException."
					+ "The at operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list or a matrix, at returns the element at the index specified by the right operand",
					examples = { @example (
							value = "[1, 2, 3] at 2",
							returnType = IKeyword.INT,
							equals = "3"),
							@example (
									value = "[{1,2}, {3,4}, {5,6}] at 0",
									returnType = IKeyword.POINT,
									equals = "{1.0,2.0}") }),
					@usage ("if it is a file, at returns the element of the file content at the index specified by the right operand"),
					@usage ("if it is a population, at returns the agent at the index specified by the right operand"),
					@usage ("if it is a graph and if the right operand is a node, at returns the in and out edges corresponding to that node"),
					@usage ("if it is a graph and if the right operand is an edge, at returns the pair node_out::node_in of the edge"),
					@usage ("if it is a graph and if the right operand is a pair node1::node2, at returns the edge from node1 to node2 in the graph") },
			see = { "contains_all", "contains_any" })
	@validator (AtValidator.class)
	public static Object at(final IScope scope, final IContainer container, final Object key) {
		if (container instanceof IContainer.Addressable) return ((IContainer.Addressable) container).get(scope, key);
		throw GamaRuntimeException.error("" + container + " cannot be accessed using " + key, scope);
	}

	public static class AtValidator implements IOperatorValidator {

		@Override
		public boolean validate(final IDescription context, final EObject emfContext, final IExpression... arguments) {
			IType type = arguments[0].getGamlType();
			final IType indexType = arguments[1].getGamlType();
			if (Types.FILE.isAssignableFrom(type)) { type = type.getWrappedType(); }
			final IType keyType = type.getKeyType();
			final boolean wrongKey = keyType != Types.NO_TYPE && !indexType.isTranslatableInto(keyType);
			if (wrongKey) {
				context.error("The contents of this " + type.getGamlType().getName() + " can only be accessed with "
						+ type.getKeyType() + " keys", IGamlIssue.WRONG_TYPE, emfContext);
				return false;
			}
			return true;
		}

	}

	public static class InternalAtValidator implements IOperatorValidator {

		@Override
		public boolean validate(final IDescription context, final EObject emfContext, final IExpression... arguments) {
			// Used in remove, for instance
			if (Types.isEmpty(arguments[1])) return true;
			IType type = arguments[0].getGamlType();
			// It is normally a list with 1 or 2 indices
			final IType indexType = arguments[1].getGamlType().getContentType();
			if (Types.FILE.isAssignableFrom(type)) { type = type.getWrappedType(); }
			final IType keyType = type.getKeyType();
			final boolean wrongKey = keyType != Types.NO_TYPE && !indexType.isTranslatableInto(keyType)
					&& !(Types.MATRIX.isAssignableFrom(type) && indexType == Types.INT);
			if (wrongKey) {
				context.error("The contents of this " + type.getGamlType().getName() + " can only be accessed with "
						+ type.getKeyType() + " keys", IGamlIssue.WRONG_TYPE, emfContext);
				return false;
			}
			return true;
		}

	}

	@operator (
			value = { IKeyword.AT, "@" },
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc ("the element at the right operand index of the container")
	@no_test
	public static Object at(final IScope scope, final IList container, final Integer key) {
		return container.get(scope, key);
	}

	@operator (
			value = { IKeyword.AT, "@" },
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc ("the element at the right (point) operand index of the matrix")
	@no_test
	public static Object at(final IScope scope, final IMatrix container, final GamaPoint key) {
		return container.get(scope, key);
	}

	@operator (
			value = { IKeyword.AT, "@" },
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc ("the agent at the right operand index of the given species")
	@no_test
	public static IAgent at(final IScope scope, final ISpecies species, final Integer key) {
		return species.get(scope, key);
	}

	@operator (
			value = { "grid_at" },
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.POINT, IOperatorCategory.GRID },
			concept = { IConcept.GRID, IConcept.POINT })
	@doc (
			value = "returns the cell of the grid (right-hand operand) at the position given by the right-hand operand",
			comment = "If the left-hand operand is a point of floats, it is used as a point of ints.",
			usages = { @usage ("if the left-hand operand is not a grid cell species, returns nil") },
			examples = { @example (
					value = "grid_cell grid_at {1,2}",
					equals = "the agent grid_cell with grid_x=1 and grid_y = 2",
					isExecutable = false) })
	@no_test
	public static IAgent grid_at(final IScope scope, final ISpecies s, final GamaPoint val)
			throws GamaRuntimeException {
		final ITopology t = scope.getAgent().getPopulationFor(s).getTopology();
		final IContainer<?, IShape> m = t.getPlaces();
		if (m instanceof IGrid) {
			final IShape shp = ((IGrid) m).get(scope, val);
			if (shp != null) return shp.getAgent();
		}
		return null;
	}

	@operator (
			value = { "remove_duplicates", "distinct" },
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			index_type = ITypeProvider.KEY_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "produces a set from the elements of the operand (i.e. a list without duplicated elements)",
			usages = { @usage (
					value = "if the operand is empty, remove_duplicates returns an empty list",
					examples = { @example (
							value = "remove_duplicates([])",
							equals = "[]") }),
					@usage (
							value = "if the operand is a graph, remove_duplicates returns the set of nodes"),
					@usage (
							value = "if the operand is a map, remove_duplicates returns the set of values without duplicate",
							examples = { @example (
									value = "remove_duplicates([1::3,2::4,3::3,5::7])",
									equals = "[3,4,7]") }),
					@usage (
							value = "if the operand is a matrix, remove_duplicates returns a list containing all the elments with duplicated.",
							examples = { @example (
									value = "remove_duplicates([[\"c11\",\"c12\",\"c13\",\"c13\"],[\"c21\",\"c22\",\"c23\",\"c23\"]])",
									equals = "[[\"c11\",\"c12\",\"c13\",\"c21\",\"c22\",\"c23\"]]",
									test = false) }) },
			examples = { @example (
					value = "remove_duplicates([3,2,5,1,2,3,5,5,5])",
					equals = "[3,2,5,1]") })
	@test ("remove_duplicates([3,2,5,1,2,3,5,5,5]) = [3,2,5,1]")
	public static IList remove_duplicates(final IScope scope, final IContainer c) {
		return (IList) stream(scope, c).distinct().toCollection(listLike(c));
	}

	@operator (
			value = "contains_all",
			can_be_const = true,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "true if the left operand contains all the elements of the right operand, false otherwise",
			comment = "the definition of contains depends on the container",
			usages = { @usage ("if the right operand is nil or empty, contains_all returns true") },
			examples = { @example (
					value = "[1,2,3,4,5,6] contains_all [2,4]",
					equals = "true "),
					@example (
							value = "[1,2,3,4,5,6] contains_all [2,8]",
							equals = "false"),
					@example (
							value = "[1::2, 3::4, 5::6] contains_all [1,3]",
							equals = "false "),
					@example (
							value = "[1::2, 3::4, 5::6] contains_all [2,4]",
							equals = "true") },
			see = { "contains", "contains_any" })
	@test ("[1,2,3,4,5,6] contains_all [2,8] = false")
	@test ("[1::2, 3::4, 5::6] contains_all [1,3] = false")
	@test ("[1::2, 3::4, 5::6] contains_all [2,4] = true")
	public static Boolean contains_all(final IScope scope, final IContainer c, final IContainer c2) {
		return stream(scope, c2).allMatch(inContainer(scope, c));
	}

	@operator (
			value = "contains_any",
			can_be_const = true,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "true if the left operand contains one of the elements of the right operand, false otherwise",
			comment = "the definition of contains depends on the container",
			special_cases = { "if the right operand is nil or empty, contains_any returns false" },
			examples = { @example (
					value = "[1,2,3,4,5,6] contains_any [2,4]",
					equals = "true "),
					@example (
							value = "[1,2,3,4,5,6] contains_any [2,8]",
							equals = "true"),
					@example (
							value = "[1::2, 3::4, 5::6] contains_any [1,3]",
							equals = "false"),
					@example (
							value = "[1::2, 3::4, 5::6] contains_any [2,4]",
							equals = "true") },
			see = { "contains", "contains_all" })
	@test ("[1,2,3,4,5,6] contains_any [2,4] = true")
	@test ("[1,2,3,4,5,6] contains_any [2,8] = true")
	@test ("[1::2, 3::4, 5::6] contains_any [2,4] = true")
	public static Boolean contains_any(final IScope scope, final IContainer c, final IContainer c1) {
		return stream(scope, c1).anyMatch(inContainer(scope, c));
	}

	@operator (
			value = { "first", "first_of" },
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "Returns the nth first elements of the container. If n is greater than the list size, a translation of the container to a list is returned. If it is equal or less than zero, returns an empty list")
	@test ("first(3, [1,2,3,4,5,6]) = [1,2,3]")
	@test ("first(0,[1,2,3,4,5,6]) = []")
	@test ("first_of(3, [1,2,3,4,5,6]) = [1,2,3]")
	@test ("first_of(0,[1,2,3,4,5,6]) = []")
	public static IList first(final IScope scope, final Integer number, final IContainer c) {
		return (IList) stream(scope, c).limit(number < 0 ? 0 : number).toCollection(listLike(c));
	}

	@operator (
			value = { "last", "last_of" },
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "Returns the nth last elements of the container. If n is greater than the list size,  returns the container cast as a list. If it is equal or less than zero, returns an empty list")
	@test ("last(3, [1,2,3,4,5,6]) = [4,5,6]")
	@test ("last(0,[1,2,3,4,5,6]) = []")
	@test ("last(10,[1::2, 3::4]) is list")
	public static IList last(final IScope scope, final Integer number, final IContainer c) {
		final IList result = GamaListFactory.create(scope, c.getGamlType().getContentType(), Iterables.limit(
				Lists.reverse(notNull(scope, c).listValue(scope, Types.NO_TYPE, false)), number < 0 ? 0 : number));
		Collections.reverse(result);
		return result;
	}

	@operator (
			value = "in",
			can_be_const = true,
			category = { IOperatorCategory.CONTAINER })
	@doc (
			value = "true if the right operand contains the left operand, false otherwise",
			comment = "the definition of in depends on the container",
			usages = { @usage ("if the right operand is nil or empty, in returns false") },
			examples = { @example (
					value = "2 in [1,2,3,4,5,6]",
					equals = "true"),
					@example (
							value = "7 in [1,2,3,4,5,6]",
							equals = "false"),
					@example (
							value = "3 in [1::2, 3::4, 5::6]",
							equals = "false"),
					@example (
							value = "6 in [1::2, 3::4, 5::6]",
							equals = "true") },
			see = { "contains" })
	@test ("2 in [1,2,3,4,5,6] = true")
	@test ("3 in [1::2, 3::4, 5::6] = false")

	public static Boolean in(final IScope scope, final Object o, final IContainer c) throws GamaRuntimeException {
		return notNull(scope, c).contains(scope, o);
	}

	@operator (
			value = "index_of",
			can_be_const = true,
			category = { IOperatorCategory.SPECIES },
			concept = { IConcept.CONTAINER, IConcept.SPECIES })
	@doc (
			usages = @usage ("if the left operator is a species, returns the index of an agent in a species. "
					+ "If the argument is not an agent of this species, returns -1. Use int(agent) instead"),
			masterDoc = true)
	@no_test
	public static Integer index_of(final IScope scope, final ISpecies s, final Object o) {
		if (!(o instanceof IAgent)) return -1;
		if (!((IAgent) o).isInstanceOf(notNull(scope, s), true)) return -1;
		return ((IAgent) o).getIndex();
	}

	@operator (
			value = "index_of",
			can_be_const = true,
			category = { IOperatorCategory.LIST },
			concept = { IConcept.LIST })
	@doc (
			value = "the index of the first occurence of the right operand in the left operand container",
			masterDoc = true,
			comment = "The definition of index_of and the type of the index depend on the container",
			usages = @usage (
					value = "if the left operand is a list, index_of returns the index as an integer",
					examples = { @example (
							value = "[1,2,3,4,5,6] index_of 4",
							equals = "3"),
							@example (
									value = "[4,2,3,4,5,4] index_of 4",
									equals = "0") }),
			see = { "at", "last_index_of" })
	@test ("[1,2,3,1,2,1,4,5] index_of 4 = 6")
	public static Integer index_of(final IScope scope, final IList c, final Object o) {
		return notNull(scope, c).indexOf(o);
	}

	@operator (
			value = "index_of",
			can_be_const = true,
			category = { IOperatorCategory.MAP },
			concept = { IConcept.MAP })
	@doc (
			usages = @usage ("if the left operand is a map, index_of returns the index of a value or nil if the value is not mapped"),
			examples = { @example (
					value = "[1::2, 3::4, 5::6] index_of 4",
					equals = "3") })
	@test ("[1::2, 3::4, 5::6] index_of 4 = 3")
	public static Object index_of(final IScope scope, final IMap<?, ?> c, final Object o) {
		for (final Map.Entry<?, ?> k : notNull(scope, c).entrySet()) {
			if (k.getValue().equals(o)) return k.getKey();
		}
		return null;
	}

	@operator (
			value = "index_of",
			can_be_const = true,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.CONTAINER, IConcept.MATRIX })
	@doc (
			usages = @usage (
					value = "if the left operand is a matrix, index_of returns the index as a point",
					examples = { @example (
							value = "matrix([[1,2,3],[4,5,6]]) index_of 4",
							equals = "{1.0,0.0}") }))
	@test ("matrix([[1,2,3],[4,5,6]]) index_of 4 = {1.0,0.0}")
	public static ILocation index_of(final IScope scope, final IMatrix c, final Object o) {
		for (int i = 0; i < notNull(scope, c).getCols(scope); i++) {
			for (int j = 0; j < c.getRows(scope); j++) {
				if (c.get(scope, i, j).equals(o)) return new GamaPoint(i, j);
			}
		}
		return null;
	}

	@operator (
			value = "all_indexes_of",
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.LIST },
			concept = { IConcept.LIST })
	@doc (
			value = "all the index of all the occurences of the right operand in the left operand container",
			masterDoc = true,
			comment = "The definition of all_indexes_of and the type of the index depend on the container",
			usages = @usage (
					value = "if the left operand is a list, all_indexes_of returns a list of all the indexes as integers",
					examples = { @example (
							value = "[1,2,3,1,2,3] all_indexes_of 1",
							equals = "[0,3]"),
							@example (
									value = "[1,2,3,1,2,3] all_indexes_of 4",
									equals = "[]") }),
			see = { "index_of", "last_index_of" })
	public static IList all_indexes_of2(final IScope scope, final IList c, final Object o) {
		final IList results = GamaListFactory.create(Types.INT);
		for (int i = 0; i < notNull(scope, c).size(); i++) {
			if (o == c.get(scope, i)) { results.add(i); }
		}
		return results;

		// Note: I also tested the following version with streams, but it was around 2 times slower...
		// return (IList) IntStream.range(0, notNull(scope,c).size()).filter(i -> c.get(scope,i) ==
		// o).boxed().collect(Collectors.toList());
	}

	@operator (
			value = "last_index_of",
			can_be_const = true,
			category = { IOperatorCategory.SPECIES },
			concept = { IConcept.CONTAINER, IConcept.SPECIES })
	@doc (
			value = "the index of the last occurence of the right operand in the left operand container",
			usages = @usage ("if the left operand is a species, the last index of an agent is the same as its index"),
			see = { "at", "index_of" })
	@test ("last_index_of([1,2,2,2,5], 2) = 3")
	public static Integer last_index_of(final IScope scope, final ISpecies c, final Object o) {
		return index_of(scope, notNull(scope, c), o);
	}

	@operator (
			value = "last_index_of",
			can_be_const = true,
			category = { IOperatorCategory.LIST },
			concept = { IConcept.LIST })
	@doc (
			value = "the index of the last occurence of the right operand in the left operand container",
			masterDoc = true,
			comment = "The definition of last_index_of and the type of the index depend on the container",
			usages = { @usage (
					value = "if the left operand is a list, last_index_of returns the index as an integer",
					examples = { @example (
							value = "[1,2,3,4,5,6] last_index_of 4",
							equals = "3"),
							@example (
									value = "[4,2,3,4,5,4] last_index_of 4",
									equals = "5") }) },
			see = { "at", "last_index_of" })
	@test ("[4,2,3,4,5,4] last_index_of 4 = 5")
	public static Integer last_index_of(final IScope scope, final IList c, final Object o) {
		return notNull(scope, c).lastIndexOf(o);
	}

	@operator (
			value = "last_index_of",
			can_be_const = true,
			category = { IOperatorCategory.MATRIX },
			concept = { IConcept.CONTAINER, IConcept.MATRIX })
	@doc (
			value = "the index of the last occurence of the right operand in the left operand container",
			usages = @usage (
					value = "if the left operand is a matrix, last_index_of returns the index as a point",
					examples = { @example (
							value = "matrix([[1,2,3],[4,5,4]]) last_index_of 4",
							equals = "{1.0,2.0}") }))
	@test ("matrix([[1,2,3],[4,5,4]]) last_index_of 4 = {1.0,2.0}")
	public static ILocation last_index_of(final IScope scope, final IMatrix c, final Object o) {
		for (int i = notNull(scope, c).getCols(scope) - 1; i > -1; i--) {
			for (int j = c.getRows(scope) - 1; j > -1; j--) {
				if (c.get(scope, i, j).equals(o)) return new GamaPoint(i, j);
			}
		}
		return null;
	}

	@operator (
			value = "last_index_of",
			can_be_const = true,
			type = ITypeProvider.KEY_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.MAP },
			concept = { IConcept.MAP })
	@doc (
			value = "the index of the last occurence of the right operand in the left operand container",
			usages = @usage (
					value = "if the left operand is a map, last_index_of returns the index as an int (the key of the pair)",
					examples = { @example (
							value = "[1::2, 3::4, 5::4] last_index_of 4",
							equals = "5") }))
	@test ("[1::2, 3::4, 5::4] last_index_of 4 = 5")
	public static Object last_index_of(final IScope scope, final IMap<?, ?> c, final Object o) {
		for (final Map.Entry<?, ?> k : Lists.reverse(new ArrayList<>(notNull(scope, c).entrySet()))) {
			if (k.getValue().equals(o)) return k.getKey();
		}
		return null;
	}

	@operator (
			value = "inter",
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "the intersection of the two operands",
			comment = "both containers are transformed into sets (so without duplicated element, cf. remove_deplicates operator) before the set intersection is computed.",
			usages = { @usage (
					value = "if an operand is a graph, it will be transformed into the set of its nodes"),
					@usage (
							value = "if an operand is a map, it will be transformed into the set of its values",
							examples = { @example (
									value = "[1::2, 3::4, 5::6] inter [2,4]",
									equals = "[2,4]"),
									@example (
											value = "[1::2, 3::4, 5::6] inter [1,3]",
											equals = "[]") }),
					@usage (
							value = "if an operand is a matrix, it will be transformed into the set of the lines",
							examples = @example (
									value = "matrix([[3,2,1],[4,5,4]]) inter [3,4]",
									equals = "[3,4]")) },
			examples = { @example (
					value = "[1,2,3,4,5,6] inter [2,4]",
					equals = "[2,4]"),
					@example (
							value = "[1,2,3,4,5,6] inter [0,8]",
							equals = "[]") },
			see = { "remove_duplicates" })
	@test ("[1,2,3,4,5,6] inter [0,8] = []")
	public static IList inter(final IScope scope, final IContainer c, final IContainer c1) {
		return (IList) stream(scope, c).filter(inContainer(scope, c1)).distinct().toCollection(listLike(c, c1));
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "returns a new list in which all the elements of the right operand have been removed from the left one",
			comment = "The behavior of the operator depends on the type of the operands.",
			usages = { @usage (
					value = "if both operands are containers and the right operand is empty, " + IKeyword.MINUS
							+ " returns the left operand"),
					@usage (
							value = "if both operands are containers, returns a new list in which all the elements of the right operand have been removed from the left one",
							examples = { @example (
									value = "[1,2,3,4,5,6] - [2,4,9]",
									returnType = "list<int>",
									equals = "[1,3,5,6]"),
									@example (
											value = "[1,2,3,4,5,6] - [0,8]",
											returnType = "list<int>",
											equals = "[1,2,3,4,5,6]") }) },
			see = { "" + IKeyword.PLUS })
	@test ("[1,2,3,4,5,6] - [0,8] = [1,2,3,4,5,6]")
	public static IList minus(final IScope scope, final IContainer source, final IContainer l) {
		final IList result =
				notNull(scope, source).listValue(scope, source.getGamlType().getContentType(), false).copy(scope);
		result.removeAll(notNull(scope, l).listValue(scope, Types.NO_TYPE, false));
		return result;
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			usages = { @usage (
					value = "if the left operand is a list and the right operand is an object of any type (except list), "
							+ IKeyword.MINUS
							+ " returns a list containing the elements of the left operand minus the first occurence of this object",
					examples = { @example (
							value = "[1,2,3,4,5,6,2] - 2",
							returnType = "list<int>",
							equals = "[1,3,4,5,6,2]"),
							@example (
									value = "[1,2,3,4,5,6] - 0",
									returnType = "list<int>",
									equals = "[1,2,3,4,5,6]") }) })
	@test ("[1,2,3,4,5,6] - 0 = [1,2,3,4,5,6]")
	public static IList minus(final IScope scope, final IList l1, final Object object) {
		final IList result = notNull(scope, l1).copy(scope);
		result.remove(object);
		return result;
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = IOperatorCategory.CONTAINER,
			concept = {})
	@doc (
			usages = { @usage (
					value = "if the left operand is a species and the right operand is an agent of the species, "
							+ IKeyword.MINUS
							+ " returns a list containing all the agents of the species minus this agent") })
	@test ("([1,2,2,3,5] - 3) = [1,2,2,5] ")
	public static IList minus(final IScope scope, final ISpecies l1, final IAgent object) {
		return minus(scope, l1.listValue(scope, scope.getType(l1.getName()), false), object);
	}

	@operator (
			value = "of_generic_species",
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			category = IOperatorCategory.SPECIES,
			concept = { IConcept.SPECIES })
	@doc (
			value = "a list, containing the agents of the left-hand operand whose species is that denoted by the right-hand operand "
					+ "and whose species extends the right-hand operand species ",
			examples = { @example (
					value = "// species speciesA {}"),
					@example (
							value = "// species sub_speciesA parent: speciesA {}"),
					@example (
							value = "[sub_speciesA(0),sub_speciesA(1),speciesA(2),speciesA(3)] of_generic_species speciesA",
							equals = "[sub_speciesA0,sub_speciesA1,speciesA0,speciesA1]",
							isExecutable = false),
					@example (
							value = "[sub_speciesA(0),sub_speciesA(1),speciesA(2),speciesA(3)] of_generic_species sous_test",
							equals = "[sub_speciesA0,sub_speciesA1]",
							isExecutable = false),
					@example (
							value = "[sub_speciesA(0),sub_speciesA(1),speciesA(2),speciesA(3)] of_species speciesA",
							equals = "[speciesA0,speciesA1]",
							isExecutable = false),
					@example (
							value = "[sub_speciesA(0),sub_speciesA(1),speciesA(2),speciesA(3)] of_species sous_test",
							equals = "[sub_speciesA0,sub_speciesA1]",
							isExecutable = false) },
			see = { "of_species" })
	public static IList of_generic_species(final IScope scope, final IContainer agents, final ISpecies s) {
		return of_species(scope, notNull(scope, agents), notNull(scope, s), true);
	}

	@operator (
			value = "of_species",
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			category = IOperatorCategory.SPECIES,
			concept = { IConcept.SPECIES })
	@doc (
			value = "a list, containing the agents of the left-hand operand whose species is the one denoted by the right-hand operand."
					+ "The expression agents of_species (species self) is equivalent to agents where (species each = species self); "
					+ "however, the advantage of using the first syntax is that the resulting list is correctly typed with the right species, "
					+ "whereas, in the second syntax, the parser cannot determine the species of the agents within the list "
					+ "(resulting in the need to cast it explicitly if it is to be used in an ask statement, for instance).",
			usages = @usage ("if the right operand is nil, of_species returns the right operand"),
			examples = { @example (
					value = "(self neighbors_at 10) of_species (species (self))",
					equals = "all the neighboring agents of the same species.",
					isExecutable = false),
					@example (
							value = "[test(0),test(1),node(1),node(2)] of_species test",
							equals = "[test0,test1]",
							isExecutable = false) },
			see = { "of_generic_species" })
	@no_test
	public static IList of_species(final IScope scope, final IContainer agents, final ISpecies s) {
		return of_species(scope, notNull(scope, agents), notNull(scope, s), false);
	}

	private static IList of_species(final IScope scope, final IContainer agents, final ISpecies s,
			final boolean generic) {
		return (IList) agents.stream(scope)
				.filter((each) -> each instanceof IAgent && ((IAgent) each).isInstanceOf(s, !generic))
				.toCollection(listOf(scope.getType(s.getName())));
	}

	@operator (
			value = { "::" },
			can_be_const = true,
			type = IType.PAIR,
			index_type = ITypeProvider.TYPE_AT_INDEX + 1,
			content_type = ITypeProvider.TYPE_AT_INDEX + 2,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "produces a new pair combining the left and the right operands",
			special_cases = "nil is not acceptable as a key (although it is as a value). If such a case happens, :: will throw an appropriate error")
	@test ("string(1::2) = '1::2'")
	public static GamaPair pair(final IScope scope, final IExpression a, final IExpression b) {
		final Object v1 = a.value(scope);
		final Object v2 = b.value(scope);
		return new GamaPair(notNull(scope, v1), v2, a.getGamlType(), b.getGamlType());
	}

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			type = ITypeProvider.BOTH,
			content_type = ITypeProvider.BOTH,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "returns a new list containing all the elements of both operands",
			usages = { @usage (
					value = "if one of the operands is nil, " + IKeyword.PLUS + " throws an error"),
					@usage (
							value = "if both operands are species, returns a special type of list called meta-population"),
					@usage (
							value = "if both operands are list, " + IKeyword.PLUS
									+ "returns the concatenation of both lists.",
							examples = { @example (
									value = "[1,2,3,4,5,6] + [2,4,9]",
									returnType = "list<int>",
									equals = "[1,2,3,4,5,6,2,4,9]"),
									@example (
											value = "[1,2,3,4,5,6] + [0,8]",
											returnType = "list<int>",
											equals = "[1,2,3,4,5,6,0,8]") }) },
			see = { "" + IKeyword.MINUS })
	@test ("[1,2,3,4,5,6] + [2,4,9] = [1,2,3,4,5,6,2,4,9]")
	public static IContainer plus(final IScope scope, final IContainer c1, final IContainer c2) {
		// special case for the addition of two populations or meta-populations
		if (c1 instanceof IPopulationSet && c2 instanceof IPopulationSet) {
			final MetaPopulation mp = new MetaPopulation();
			mp.addPopulationSet((IPopulationSet) c1);
			mp.addPopulationSet((IPopulationSet) c2);
		}
		return (IContainer) stream(scope, c1).append(stream(scope, c2)).toCollection(listLike(c1, c2));
	}

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = IOperatorCategory.CONTAINER,
			concept = {})
	@doc (
			usages = @usage (
					value = "if the right operand is an object of any type (except a container), " + IKeyword.PLUS
							+ " returns a list of the elements of the left operand, to which this object has been added",
					examples = { @example (
							value = "[1,2,3,4,5,6] + 2",
							returnType = "list<int>",
							equals = "[1,2,3,4,5,6,2]"),
							@example (
									value = "[1,2,3,4,5,6] + 0",
									returnType = "list<int>",
									equals = "[1,2,3,4,5,6,0]") }))
	@test ("[1,2,3,4,5,6] + 2 = [1,2,3,4,5,6,2]")
	public static IList plus(final IScope scope, final IContainer l1, final Object l) {
		final IList result = notNull(scope, l1).listValue(scope, Types.NO_TYPE, false).copy(scope);
		result.add(l);
		return result;
	}

	@operator (
			value = "union",
			can_be_const = true,
			content_type = ITypeProvider.BOTH,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "returns a new list containing all the elements of both containers without duplicated elements.",
			comment = "",
			usages = { @usage ("if the left or right operand is nil, union throws an error") },
			examples = { @example (
					value = "[1,2,3,4,5,6] union [2,4,9]",
					equals = "[1,2,3,4,5,6,9]"),
					@example (
							value = "[1,2,3,4,5,6] union [0,8]",
							equals = "[1,2,3,4,5,6,0,8]"),
					@example (
							value = "[1,3,2,4,5,6,8,5,6] union [0,8]",
							equals = "[1,3,2,4,5,6,8,0]") },
			see = { "inter", IKeyword.PLUS })
	@test ("[1,2,3,4,5,6] union [2,4,9] = [1,2,3,4,5,6,9]")
	public static IList union(final IScope scope, final IContainer c, final IContainer c1) {
		return (IList) stream(scope, c).append(stream(scope, c1)).distinct().toCollection(listLike(c, c1));
	}

	// ITERATORS

	@operator (
			value = { "group_by" },
			iterator = true,
			index_type = ITypeProvider.TYPE_AT_INDEX + 2,
			content_type = IType.LIST,
			content_type_content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			concept = { IConcept.CONTAINER, IConcept.MAP })
	@doc (
			value = "Returns a map, where the keys take the possible values of the right-hand operand and the map values are the list of elements "
					+ "of the left-hand operand associated to the key value",
			masterDoc = true,
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage ("if the left-hand operand is nil, group_by throws an error") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] group_by (each > 3)",
					equals = "[false::[1, 2, 3], true::[4, 5, 6, 7, 8]]"),
					@example (
							value = "g2 group_by (length(g2 out_edges_of each) )",
							equals = "[ 0::[node9, node7, node10, node8, node11], 1::[node6], 2::[node5], 3::[node4]]",
							isExecutable = false),
					@example (
							value = "(list(node) group_by (round(node(each).location.x))",
							equals = "[32::[node5], 21::[node1], 4::[node0], 66::[node2], 96::[node3]]",
							isExecutable = false),
					@example (
							value = "[1::2, 3::4, 5::6] group_by (each > 4)",
							equals = "[false::[2, 4], true::[6]]",
							returnType = "map<bool,list>") },
			see = { "first_with", "last_with", "where" })
	@test ("[1,2,3,4,5,6,7,8] group_by (each > 3) = [false::[1, 2, 3], true::[4, 5, 6, 7, 8]]")
	@test ("[1::2, 3::4, 5::6] group_by (each > 4) = [false::[2, 4], true::[6]]")
	public static IMap group_by(final IScope scope, final IContainer c, final IExpression e) {
		final IType ct = notNull(scope, c).getGamlType().getContentType();
		return (IMap) stream(scope, c).groupingTo(with(scope, e), asMapOf(e.getGamlType(), Types.LIST.of(ct)),
				listOf(ct));
	}

	@operator (
			value = { "last_with" },
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			iterator = true,
			expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the last element of the left-hand operand that makes the right-hand operand evaluate to true.",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage ("if the left-hand operand is nil, last_with throws an error."),
					@usage ("If there is no element that satisfies the condition, it returns nil"), @usage (
							value = "if the left-operand is a map, the keyword each will contain each value",
							examples = { @example (
									value = "[1::2, 3::4, 5::6] last_with (each >= 4)",
									equals = "6"),
									@example (
											value = "[1::2, 3::4, 5::6].pairs last_with (each.value >= 4)",
											equals = "(5::6)") }) },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] last_with (each > 3)",
					equals = "8",
					returnType = IKeyword.INT),
					@example (
							value = "graph g2 <- graph([]);",
							isTestOnly = true),
					@example (
							value = "g2 last_with (length(g2 out_edges_of each) = 0 )",
							equals = "a node",
							isExecutable = false),
					@example (
							value = "(list(node) last_with (round(node(each).location.x) > 32)",
							equals = "node3",
							isExecutable = false) },
			see = { "group_by", "first_with", "where" })
	@test ("[1,2,3,4,5,6,7,8] last_with (each > 3) = 8")
	public static Object last_with(final IScope scope, final IContainer c, final IExpression filter) {
		return stream(scope, c).filter(by(scope, filter)).reduce((a, b) -> b).orElse(null);
	}

	@operator (
			value = { "first_with" },
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			iterator = true,
			expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the first element of the left-hand operand that makes the right-hand operand evaluate to true.",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = {
					@usage ("if the left-hand operand is nil, first_with throws an error. If there is no element that satisfies the condition, it returns nil"),
					@usage (
							value = "if the left-operand is a map, the keyword each will contain each value",
							examples = { @example (
									value = "[1::2, 3::4, 5::6] first_with (each >= 4)",
									equals = "4",
									returnType = IKeyword.INT),
									@example (
											value = "[1::2, 3::4, 5::6].pairs first_with (each.value >= 4)",
											equals = "(3::4)",
											returnType = IKeyword.PAIR) }) },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] first_with (each > 3)",
					equals = "4"),
					@example (
							value = "graph g2 <- graph([]);",
							isTestOnly = true),
					@example (
							value = "g2 first_with (length(g2 out_edges_of each) = 0)",
							equals = "node9",
							test = false),
					@example (
							value = "(list(node) first_with (round(node(each).location.x) > 32)",
							equals = "node2",
							isExecutable = false) },
			see = { "group_by", "last_with", "where" })
	@test ("[1,2,3,4,5,6,7,8] first_with (each > 3) = 4")
	public static Object first_with(final IScope scope, final IContainer c, final IExpression filter) {
		return stream(scope, c).findFirst(by(scope, filter)).orElse(null);
	}

	@operator (
			value = { "max_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the maximum value of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage ("As of GAMA 1.6, if the left-hand operand is nil or empty, max_of throws an error"),
					@usage (
							value = "if the left-operand is a map, the keyword each will contain each value",
							examples = { @example (
									value = "[1::2, 3::4, 5::6] max_of (each + 3)",
									equals = "9") }) },
			examples = {
					// @example ( value = "graph([]) max_of([])", raises = "error", isTestOnly = true),
					@example (
							value = "[1,2,4,3,5,7,6,8] max_of (each * 100 )",
							equals = "800"),
					@example (
							value = "graph g2 <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);"),
					@example (
							value = "g2.vertices max_of (g2 degree_of( each ))",
							equals = "2"),
					@example (
							value = "(list(node) max_of (round(node(each).location.x))",
							equals = "96",
							isExecutable = false) },
			see = { "min_of" })
	@test ("[1,2,4,3,5,7,6,8] max_of (each * 100 ) = 800")
	@validator (ComparableValidator.class)
	public static Object max_of(final IScope scope, final IContainer c, final IExpression filter) {
		return stream(scope, c).map(with(scope, filter)).maxBy(Function.identity()).orElse(null);
	}

	@operator (
			value = "sum",
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			expected_content_type = { IType.INT, IType.FLOAT, IType.POINT, IType.COLOR, IType.STRING },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER, IOperatorCategory.COLOR },
			concept = { IConcept.STATISTIC, IConcept.COLOR })
	@doc (
			value = "the sum of all the elements of the operand",
			masterDoc = true,
			comment = "the behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list of int or float: sum returns the sum of all the elements",
					examples = { @example (
							value = "sum ([12,10,3])",
							returnType = IKeyword.INT,
							equals = "25") }),
					@usage (
							value = "if it is a list of points: sum returns the sum of all points as a point (each coordinate is the sum of the corresponding coordinate of each element)",
							examples = { @example (
									value = "sum([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}])",
									equals = "{20.0,17.0}") }),
					@usage (
							value = "if it is a population or a list of other types: sum transforms all elements into float and sums them"),
					@usage (
							value = "if it is a map, sum returns the sum of the value of all elements"),
					@usage (
							value = "if it is a file, sum returns the sum of the content of the file (that is also a container)"),
					@usage (
							value = "if it is a graph, sum returns the total weight of the graph"),
					@usage (
							value = "if it is a matrix of int, float or object, sum returns the sum of all the numerical elements (i.e. all elements for integer and float matrices)"),
					@usage (
							value = "if it is a matrix of other types: sum transforms all elements into float and sums them"),
					@usage (
							value = "if it is a list of colors: sum will sum them and return the blended resulting color") },
			see = { "mul" })
	@test ("sum([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}]) = {20.0,17.0}")
	@test ("sum ([12,10,3]) = 25")
	public static Object sum(final IScope scope, final IContainer l) {
		return sum_of(scope, l, null);
	}

	@operator (
			value = "sum",
			can_be_const = true,
			doc = @doc ("Returns the sum of the weights of the graph nodes"),
			category = { IOperatorCategory.GRAPH },
			concept = { IConcept.GRAPH })
	@test ("sum(as_edge_graph(line([{10,10},{30,10}]))) = 20.0")
	public static double sum(final IScope scope, final IGraph g) {
		if (g == null) return 0.0;
		return g.computeTotalWeight();
	}

	@operator (
			value = { "sum_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			expected_content_type = { IType.FLOAT, IType.POINT, IType.COLOR, IType.INT, IType.STRING },
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the sum of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage (
					value = "if the left-operand is a map, the keyword each will contain each value",
					examples = { @example (
							value = "[1::2, 3::4, 5::6] sum_of (each + 3)",
							equals = "21") }) },
			examples = { @example (
					value = "[1,2] sum_of (each * 100 )",
					equals = "300") },
			see = { "min_of", "max_of", "product_of", "mean_of" })
	@test ("[1,2] sum_of (each * 100 ) = 300")
	public static Object sum_of(final IScope scope, final IContainer container, final IExpression filter) {
		Stream s = stream(scope, container);
		IType t;
		if (filter != null) {
			s = s.map(with(scope, filter));
			t = filter.getGamlType();
		} else {
			t = container.getGamlType().getContentType();
		}
		s = s.map(each -> t.cast(scope, each, null, false));
		switch (t.id()) {
			case IType.INT:
				return ((Stream<Integer>) s).reduce(0, Integer::sum);
			case IType.FLOAT:
				return ((Stream<Double>) s).reduce(0d, Double::sum);
			case IType.POINT:
				return ((Stream<GamaPoint>) s).reduce(new GamaPoint(), GamaPoint::plus);
			case IType.COLOR:
				return ((Stream<GamaColor>) s).reduce(new GamaColor(0, 0, 0, 0), GamaColor::merge);
			case IType.STRING:
				return ((Stream<String>) s).reduce("", String::concat);
			default:
				return GamaRuntimeException.error("No sum can be computed for " + container.serialize(true), scope);
		}
	}

	@operator (
			value = "mean",
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1 + ITypeProvider.FLOAT_IN_CASE_OF_INT,
			expected_content_type = { IType.INT, IType.FLOAT, IType.POINT, IType.COLOR },
			category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER, IOperatorCategory.COLOR },
			concept = { IConcept.STATISTIC, IConcept.COLOR })
	@doc (
			value = "the mean of all the elements of the operand",
			comment = "the elements of the operand are summed (see sum for more details about the sum of container elements ) and then the sum value is divided by the number of elements.",
			special_cases = {
					"if the container contains points, the result will be a point. If the container contains rgb values, the result will be a rgb color" },
			examples = { @example (
					value = "mean ([4.5, 3.5, 5.5, 7.0])",
					equals = "5.125 ") },
			see = { "sum" })
	@test ("mean ([4.5, 3.5, 5.5, 7.0]) with_precision 3 = 5.125")
	public static Object mean(final IScope scope, final IContainer l) throws GamaRuntimeException {

		final Object s = Containers.sum(scope, l);
		int size = l.length(scope);
		if (size == 0) { size = 1; }
		if (s instanceof Number) return ((Number) s).doubleValue() / size;
		if (s instanceof ILocation) return Points.divide(scope, (GamaPoint) s, size);
		if (s instanceof GamaColor) return Colors.divide((GamaColor) s, size);
		return Cast.asFloat(scope, s) / size;
	}

	@operator (
			value = { "mean_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2 + ITypeProvider.FLOAT_IN_CASE_OF_INT,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the mean of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage (
					value = "if the left-operand is a map, the keyword each will contain each value",
					examples = { @example (
							value = "[1::2, 3::4, 5::6] mean_of (each)",
							equals = "4") }) },
			examples = { @example (
					value = "[1,2] mean_of (each * 10 )",
					equals = "15") },
			see = { "min_of", "max_of", "sum_of", "product_of" })
	@test ("[1,2] mean_of (each * 10 ) = 15")
	@test ("[1,2] mean_of (each * 10 ) = 15")
	@test ("[1,2] mean_of (each * 10 ) = 15")
	public static Object mean_of(final IScope scope, final IContainer container, final IExpression filter) {
		return mean(scope, collect(scope, container, filter));
	}

	@operator (
			value = { "min_of" },
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "the minimum value of the right-hand expression evaluated on each of the elements of the left-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage ("if the left-hand operand is nil or empty, min_of throws an error"), @usage (
					value = "if the left-operand is a map, the keyword each will contain each value",
					examples = { @example (
							value = "[1::2, 3::4, 5::6] min_of (each + 3)",
							equals = "5") }) },
			examples = {
					// @example (value = "graph([]) min_of([])", raises = "error", isTestOnly = true),
					@example (
							value = "[1,2,4,3,5,7,6,8] min_of (each * 100 )",
							equals = "100"),
					@example (
							value = "graph g2 <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);"),
					@example (
							value = "g2 min_of (length(g2 out_edges_of each) )",
							equals = "0"),
					@example (
							value = "(list(node) min_of (round(node(each).location.x))",
							equals = "4",
							isExecutable = false) },
			see = { "max_of" })
	@test ("[1,2,4,3,5,7,6,8] min_of (each * 100 ) = 100")
	@validator (ComparableValidator.class)
	public static Object min_of(final IScope scope, final IContainer c, final IExpression filter) {
		return stream(scope, c).map(with(scope, filter)).minBy(Function.identity()).orElse(null);
	}

	@operator (
			value = "among",
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "Returns a list of length the value of the left-hand operand, containing random elements from the right-hand operand. As of GAMA 1.6, the order in which the elements are returned can be different than the order in which they appear in the right-hand container",
			special_cases = {
					"if the right-hand operand is empty, among returns a new empty list. If it is nil, it throws an error.",
					"if the left-hand operand is greater than the length of the right-hand operand, among returns the right-hand operand (converted as a list). If it is smaller or equal to zero, it returns an empty list" },
			examples = { @example (
					value = "3 among [1,2,4,3,5,7,6,8]",
					returnType = "list<int>",
					equals = "[1,2,8] (for example)",
					test = false),
					@example (
							value = "3 among g2",
							equals = "[node6,node11,node7]",
							isExecutable = false),
					@example (
							value = "3 among list(node)",
							equals = "[node1,node11,node4]",
							isExecutable = false),
					@example (
							value = "1 among [1::2,3::4]",
							returnType = "list<int>",
							equals = "2 or 4",
							test = false) })
	@no_test
	public static IList among(final IScope scope, final Integer number, final IContainer c)
			throws GamaRuntimeException {
		if (number <= 0) {
			if (number < 0) {
				GAMA.reportAndThrowIfNeeded(scope,
						GamaRuntimeException.warning("'among' expects a positive number (not " + number + ")", scope),
						false);
			}
			return listLike(c).get();
		}
		final IList l = notNull(scope, c).listValue(scope, c.getGamlType().getContentType(), false);
		final int size = l.size();
		if (number >= size) return l;
		final int[] indexes = new int[size];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = i;
		}
		scope.getRandom().shuffleInPlace(indexes);
		final IList result = listLike(c).get();
		for (int i = 0; i < number; i++) {
			result.add(l.get(indexes[i]));
		}
		return result;
	}

	public static class ComparableValidator implements IOperatorValidator {

		@Override
		public boolean validate(final IDescription context, final EObject emfContext, final IExpression... arguments) {
			final IExpression filter = arguments[1];
			if (!filter.getGamlType().isComparable()) {
				context.error(
						"The comparison function should return values that are comparable with each other (e.g. int, float, string, point, color, etc.)",
						IGamlIssue.UNMATCHED_TYPES, emfContext);
				return false;
			}
			return true;
		}

	}

	@operator (
			value = { "sort_by", "sort" },
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "Returns a list, containing the elements of the left-hand operand sorted in ascending order by the value of the right-hand operand when it is evaluated on them. ",
			comment = "the left-hand operand is casted to a list before applying the operator. In the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.",
			special_cases = {
					"if the left-hand operand is nil, sort_by throws an error. If the sorting function returns values that cannot be compared, an error will be thrown as well" },
			examples = { @example (
					value = "[1,2,4,3,5,7,6,8] sort_by (each)",
					equals = "[1,2,3,4,5,6,7,8]"),
					@example (
							value = "graph g2 <- graph([]);",
							isTestOnly = true),
					@example (
							value = "g2 sort_by (length(g2 out_edges_of each) )",
							equals = "[node9, node7, node10, node8, node11, node6, node5, node4]",
							test = false),
					@example (
							value = "(list(node) sort_by (round(node(each).location.x))",
							equals = "[node5, node1, node0, node2, node3]",
							isExecutable = false),
					@example (
							value = "[1::2, 5::6, 3::4] sort_by (each)",
							equals = "[2, 4, 6]") },
			see = { "group_by" })
	@test ("[1,2,4,3,5,7,6,8] sort_by (each) = [1,2,3,4,5,6,7,8]")
	@validator (ComparableValidator.class)
	public static IList sort(final IScope scope, final IContainer c, final IExpression filter) {
		return (IList) stream(scope, c).sortedBy(with(scope, filter)).toCollection(listLike(c));
	}

	@operator (
			value = { "where", "select" },
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			iterator = true,
			// expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			masterDoc = true,
			value = "a list containing all the elements of the left-hand operand that make the right-hand operand evaluate to true. ",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage (
					value = "if the left-hand operand is nil, where throws an error"),
					@usage (
							value = "if the left-operand is a map, the keyword each will contain each value",
							examples = { @example (
									value = "[1::2, 3::4, 5::6] where (each >= 4)",
									equals = "[4, 6]") }) },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] where (each > 3)",
					equals = "[4, 5, 6, 7, 8] "),
					@example (
							value = "graph g2 <- graph([]);",
							isTestOnly = true),
					@example (
							value = "g2 where (length(g2 out_edges_of each) = 0 )",
							equals = "[node9, node7, node10, node8, node11]",
							test = false),
					@example (
							value = "(list(node) where (round(node(each).location.x) > 32)",
							equals = "[node2, node3]",
							isExecutable = false) },
			see = { "first_with", "last_with" })
	@test ("[1,2,3,4,5,6,7,8] where (each > 3) = [4, 5, 6, 7, 8] ")
	public static IList where(final IScope scope, final IContainer c, final IExpression filter) {
		return (IList) stream(scope, c).filter(by(scope, filter)).toCollection(listLike(c));
	}

	@operator (
			value = { "where", "select" },
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			iterator = true,
			// expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "Returns a list contaning only the elements that make the predicate return true")
	@test ("[1,2,3,4,5,6,7,8] where (each != 2) = [1, 3, 4, 5, 6, 7, 8] ")
	/**
	 * Optimization for a very common case (Ilist)
	 *
	 * @param scope
	 * @param c
	 * @param filter
	 * @return
	 */
	public static IList where(final IScope scope, final IList c, final IExpression filter) {
		return where(scope, c.iterable(scope), c.getGamlType().getContentType(), filter);
	}

	private static IList where(final IScope scope, final Iterable c, final IType contentType,
			final IExpression filter) {
		final IList result = GamaListFactory.create(contentType);
		for (final Object o : c) {
			scope.setEach(o);
			if ((Boolean) filter.value(scope)) { result.add(o); }
		}
		scope.setEach(null);
		return result;
	}

	@operator (
			value = { "where", "select" },
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			iterator = true,
			// expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "Returns a list containing only the agents of this species that make the predicate return true")
	@no_test
	/**
	 * Optimization for a very common case (ISpecies)
	 *
	 * @param scope
	 * @param c
	 * @param filter
	 * @return
	 */
	public static IList where(final IScope scope, final ISpecies c, final IExpression filter) {
		return where(scope, c.iterable(scope), c.getGamlType().getContentType(), filter);
	}

	@operator (
			value = { "with_max_of" },
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "one of elements of the left-hand operand that maximizes the value of the right-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage (
					value = "if the left-hand operand is nil, with_max_of returns the default value of the right-hand operand") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] with_max_of (each )",
					equals = "8"),
					@example (
							value = "graph g2 <- graph([]);",
							isTestOnly = true),
					@example (
							value = "g2 with_max_of (length(g2 out_edges_of each)  ) ",
							equals = "node4",
							test = false),
					@example (
							value = "(list(node) with_max_of (round(node(each).location.x))",
							equals = "node3",
							isExecutable = false),
					@example (
							value = "[1::2, 3::4, 5::6] with_max_of (each)",
							equals = "6") },
			see = { "where", "with_min_of" })
	@test ("[1,2,3,4,5,6,7,8] with_max_of (each ) = 8")
	@validator (ComparableValidator.class)
	public static Object with_max_of(final IScope scope, final IContainer c, final IExpression filter) {
		return stream(scope, c).maxBy(with(scope, filter)).orElse(null);
	}

	@operator (
			value = { "with_min_of" },
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.FILTER })
	@doc (
			value = "one of elements of the left-hand operand that minimizes the value of the right-hand operand",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the right-hand operand elements. ",
			usages = { @usage (
					value = "if the left-hand operand is nil, with_max_of returns the default value of the right-hand operand") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] with_min_of (each )",
					equals = "1"),
					@example (
							value = "graph g2 <- graph([]);",
							isTestOnly = true),
					@example (
							value = "g2 with_min_of (length(g2 out_edges_of each)  )",
							equals = "node11",
							test = false),
					@example (
							value = "(list(node) with_min_of (round(node(each).location.x))",
							equals = "node0",
							isExecutable = false),
					@example (
							value = "[1::2, 3::4, 5::6] with_min_of (each)",
							equals = "2") },
			see = { "where", "with_max_of" })
	@test ("[1,2,3,4,5,6,7,8] with_min_of (each )  = 1")
	@validator (ComparableValidator.class)
	public static Object with_min_of(final IScope scope, final IContainer c, final IExpression filter) {
		return stream(scope, c).minBy(with(scope, filter)).orElse(null);
	}

	@operator (
			value = { "accumulate" },
			content_type = ITypeProvider.SECOND_CONTENT_TYPE_OR_TYPE,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "returns a new flat list, in which each element is the evaluation of the right-hand operand. If this evaluation returns a list, the elements of this result are added directly to the list returned",
			comment = "accumulate is dedicated to the application of a same computation on each element of a container (and returns a list). "
					+ "In the right-hand operand, the keyword each can be used to represent, in turn, each of the left-hand operand elements. ",
			examples = { @example (
					value = "[a1,a2,a3] accumulate (each neighbors_at 10)",
					equals = "a flat list of all the neighbors of these three agents",
					isExecutable = false),
					@example (
							value = "[1,2,4] accumulate ([2,4])",
							returnType = "list<int>",
							equals = "[2,4,2,4,2,4]"),
					@example (
							value = "[1,2,4] accumulate (each * 2)",
							returnType = "list<int>",
							equals = "[2,4,8]") },
			see = { "collect" })
	@test ("[1,2,4] accumulate ([2,4]) = [2,4,2,4,2,4]")
	public static IList accumulate(final IScope scope, final IContainer c, final IExpression filter) {
		// WARNING TODO The resulting type is not computed
		final IType type = filter.getGamlType();
		IType resultingContentsType = type;
		if (resultingContentsType.isContainer()) { resultingContentsType = resultingContentsType.getContentType(); }
		return (IList) stream(scope, c).flatCollection(with(scope, filter).andThen(toLists))
				.toCollection(listOf(resultingContentsType));

	}

	@operator (
			value = { "collect" },
			content_type = ITypeProvider.TYPE_AT_INDEX + 2,
			iterator = true,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "returns a new list, in which each element is the evaluation of the right-hand operand.",
			comment = "collect is similar to accumulate except that accumulate always produces flat lists if the right-hand operand returns a list."
					+ "In addition, collect can be applied to any container.",
			usages = { @usage ("if the left-hand operand is nil, collect throws an error") },
			examples = { @example (
					value = "[1,2,4] collect (each *2)",
					equals = "[2,4,8]"),
					@example (
							value = "[1,2,4] collect ([2,4])",
							equals = "[[2,4],[2,4],[2,4]]"),
					@example (
							value = "[1::2, 3::4, 5::6] collect (each + 2)",
							equals = "[4,6,8]"),
					@example (
							value = "(list(node) collect (node(each).location.x * 2)",
							equals = "the list of nodes with their x multiplied by 2",
							isExecutable = false) },
			see = { "accumulate" })
	@test ("[1,2,4] collect (each *2) = [2,4,8]")
	@test ("[1,2,4] collect ([2,4]) = [[2,4],[2,4],[2,4]]")
	public static IList collect(final IScope scope, final IContainer c, final IExpression filter) {
		return (IList) stream(scope, c).map(with(scope, filter)).toCollection(listOf(filter.getGamlType()));
	}

	@operator (
			value = { "interleave" },
			content_type = ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "Returns a new list containing the interleaved elements of the containers contained in the operand",
			comment = "the operand should be a list of lists of elements. The result is a list of elements. ",
			examples = { @example (
					value = "interleave([1,2,4,3,5,7,6,8])",
					equals = "[1,2,4,3,5,7,6,8]"),
					@example (
							value = "interleave([['e11','e12','e13'],['e21','e22','e23'],['e31','e32','e33']])",
							equals = "['e11','e21','e31','e12','e22','e32','e13','e23','e33']") })
	public static IList interleave(final IScope scope, final IContainer cc) {
		final Iterable iterable = notNull(scope, cc).iterable(scope);
		IType type = cc.getGamlType().getContentType();
		if (type.isContainer()) { type = type.getContentType(); }
		final Iterator it = new InterleavingIterator(scope, toArray(iterable, Object.class));
		return GamaListFactory.create(scope, type, it);
	}

	@operator (
			value = { "count" },
			iterator = true,
			expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "returns an int, equal to the number of elements of the left-hand operand that make the right-hand operand evaluate to true.",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.",
			usages = { @usage ("if the left-hand operand is nil, count throws an error") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] count (each > 3)",
					equals = "5"),
					@example (
							value = "// Number of nodes of graph g2 without any out edge"),
					@example (
							value = "graph g2 <- graph([]);"),
					@example (
							value = "g2 count (length(g2 out_edges_of each) = 0  ) ",
							equals = "the total number of out edges",
							test = false),
					@example (
							value = "// Number of agents node with x > 32"),
					@example (
							value = "int n <- (list(node) count (round(node(each).location.x) > 32);",
							isExecutable = false),
					@example (
							value = "[1::2, 3::4, 5::6] count (each > 4)",
							equals = "1") },
			see = { "group_by" })
	public static Integer count(final IScope scope, final IContainer original, final IExpression filter) {
		return (int) notNull(scope, original).stream(scope).filter(by(scope, filter)).count();
	}

	@operator (
			value = { "one_matches", "one_verifies" },
			iterator = true,
			expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "Returns true if at least one of the elements of the left-hand operand make the right-hand operand evaluate to true.  Returns false if the left-hand operand is empty. 'c one_matches each.property' is strictly equivalent to '(c count each.property) > 0' but faster in most cases (as it is a shortcircuited operator) ",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.",
			usages = { @usage ("if the left-hand operand is nil, one_matches throws an error") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] one_matches (each > 3)",
					equals = "true"),
					@example (
							value = "[1::2, 3::4, 5::6] one_matches (each > 4)",
							equals = "true") },
			see = { "none_matches", "all_match", "count" })
	public static Boolean one_matches(final IScope scope, final IContainer original, final IExpression filter) {
		return notNull(scope, original).stream(scope).anyMatch(by(scope, filter));
	}

	@operator (
			value = { "none_matches", "none_verifies" },
			iterator = true,
			expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "Returns true if none of the elements of the left-hand operand make the right-hand operand evaluate to true. 'c none_matches each.property' is strictly equivalent to '(c count each.property) = 0'",
			comment = "In the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.",
			usages = { @usage ("If the left-hand operand is nil, none_matches throws an error."),
					@usage ("If the left-hand operand is empty, none_matches returns true.") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] none_matches (each > 3)",
					equals = "false"),
					@example (
							value = "[1::2, 3::4, 5::6] none_matches (each > 4)",
							equals = "false") },
			see = { "one_matches", "all_match", "count" })
	public static Boolean none_matches(final IScope scope, final IContainer original, final IExpression filter) {
		return notNull(scope, original).stream(scope).noneMatch(by(scope, filter));
	}

	@operator (
			value = { "all_match", "all_verify" },
			iterator = true,
			expected_content_type = IType.BOOL,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "Returns true if all the elements of the left-hand operand make the right-hand operand evaluate to true. Returns true if the left-hand operand is empty. 'c all_match each.property' is strictly equivalent to '(c count each.property)  = length(c)' but faster in most cases (as it is a shortcircuited operator)",
			comment = "in the right-hand operand, the keyword each can be used to represent, in turn, each of the elements.",
			usages = { @usage ("if the left-hand operand is nil, all_match throws an error") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] all_match (each > 3)",
					equals = "false"),
					@example (
							value = "[1::2, 3::4, 5::6] all_match (each > 4)",
							equals = "false") },
			see = { "none_matches", "one_matches", "count" })
	public static Boolean all_match(final IScope scope, final IContainer original, final IExpression filter) {
		return notNull(scope, original).stream(scope).allMatch(by(scope, filter));
	}

	@operator (
			value = { "index_by" },
			iterator = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			index_type = ITypeProvider.TYPE_AT_INDEX + 2,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "produces a new map from the evaluation of the right-hand operand for each element of the left-hand operand",
			usages = {
					@usage ("if the left-hand operand is nil, index_by throws an error. If the operation results in duplicate keys, only the first value corresponding to the key is kept") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] index_by (each - 1)",
					equals = "[0::1, 1::2, 2::3, 3::4, 4::5, 5::6, 6::7, 7::8]") },
			see = {})
	public static IMap index_by(final IScope scope, final IContainer original, final IExpression keyProvider) {

		final StreamEx s = original.stream(scope);
		final IType contentsType = original.getGamlType().getContentType();
		return (IMap) s.collect(Collectors.toMap(with(scope, keyProvider), (a) -> a, (a, b) -> a,
				asMapOf(keyProvider.getGamlType(), contentsType)));
	}

	@operator (
			value = { "as_map" },
			iterator = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = ITypeProvider.KEY_TYPE_AT_INDEX + 2,
			expected_content_type = IType.PAIR,
			category = IOperatorCategory.MAP,
			concept = { IConcept.CONTAINER, IConcept.MAP })
	@doc (
			value = "produces a new map from the evaluation of the right-hand operand for each element of the left-hand operand",
			comment = "the right-hand operand should be a pair",
			usages = { @usage ("if the left-hand operand is nil, as_map throws an error.") },
			examples = { @example (
					value = "[1,2,3,4,5,6,7,8] as_map (each::(each * 2))",
					returnType = "map<int,int>",
					equals = "[1::2, 2::4, 3::6, 4::8, 5::10, 6::12, 7::14, 8::16]"),
					@example (
							value = "[1::2,3::4,5::6] as_map (each::(each * 2))",
							returnType = "map<int,int>",
							equals = "[2::4, 4::8, 6::12] ") },
			see = {})
	public static IMap as_map(final IScope scope, final IContainer original, final IExpression filter) {
		if (!(filter instanceof BinaryOperator))
			throw GamaRuntimeException.error("'as_map' expects a pair as second argument", scope);
		final BinaryOperator pair = (BinaryOperator) filter;
		if (!pair.getName().equals("::"))
			throw GamaRuntimeException.error("'as_map' expects a pair as second argument", scope);
		final IExpression key = pair.arg(0);
		final IExpression value = pair.arg(1);
		return (IMap) stream(scope, original).collect(Collectors.toMap(with(scope, key), with(scope, value),
				(a, b) -> a, asMapOf(key.getGamlType(), value.getGamlType())));
	}

	@operator (
			value = { "create_map" },
			iterator = true,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = IOperatorCategory.MAP,
			expected_content_type = ITypeProvider.BOTH,
			concept = { IConcept.CONTAINER, IConcept.MAP })
	@doc (
			value = "returns a new map using the left operand as keys for the right operand",
			usages = { @usage ("if the left operand contains duplicates, create_map throws an error."),
					@usage ("if both operands have different lengths, choose the minimum length between the two operands"
							+ "for the size of the map") },
			examples = { @example (
					value = "create_map([0,1,2],['a','b','c'])",
					returnType = "map<int,string>",
					equals = "[0::'a',1::'b',2::'c']"),
					@example (
							value = "create_map([0,1],[0.1,0.2,0.3])",
							returnType = "map<int,float>",
							equals = "[0::0.1,1::0.2]"),
					@example (
							value = "create_map(['a','b','c','d'],[1.0,2.0,3.0])",
							returnType = "map<string,float>",
							equals = "['a'::1.0,'b'::2.0,'c'::3.0]") },
			see = {})
	public static IMap create_map(final IScope scope, final IList keys, final IList values) {
		if (keys.length(scope) != values.length(scope)) {
			GamaRuntimeException.warning("'create_map' expects two lists of the same length", scope);
		}
		final HashSet newSet = new HashSet(keys);
		if (newSet.size() < keys.length(scope))
			throw GamaRuntimeException.error("'create_map' expects unique values in the keys list", scope);
		return GamaMapFactory.create(scope, keys.getGamlType(), values.getGamlType(), keys, values);
	}

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			type = ITypeProvider.BOTH,
			content_type = ITypeProvider.BOTH,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "returns a new map containing all the elements of both operands",
			examples = { @example (
					value = "['a'::1,'b'::2] + ['c'::3]",
					equals = "['a'::1,'b'::2,'c'::3]"),
					@example (
							value = "['a'::1,'b'::2] + [5::3.0]",
							equals = "['a'::1,'b'::2,5::3.0]") },
			see = { "" + IKeyword.MINUS })
	public static IMap plus(final IScope scope, final IMap m1, final IMap m2) {
		final IType type = GamaType.findCommonType(notNull(scope, m1).getGamlType(), notNull(scope, m2).getGamlType());
		final IMap res = GamaMapFactory.createWithoutCasting(type.getKeyType(), type.getContentType(), m1);
		res.putAll(m2);
		return res;
	}

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			type = ITypeProvider.TYPE_AT_INDEX + 1,
			content_type = ITypeProvider.BOTH,
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER })
	@doc (
			value = "returns a new map containing all the elements of both operands",
			examples = { @example (
					value = "['a'::1,'b'::2] + ('c'::3)",
					equals = "['a'::1,'b'::2,'c'::3]"),
					@example (
							value = "['a'::1,'b'::2] + ('c'::3)",
							equals = "['a'::1,'b'::2,'c'::3]") },
			see = { "" + IKeyword.MINUS })
	public static IMap plus(final IScope scope, final IMap m1, final GamaPair m2) {
		final IType type = GamaType.findCommonType(notNull(scope, m1).getGamlType(), notNull(scope, m2).getGamlType());
		final IMap res = GamaMapFactory.createWithoutCasting(type.getKeyType(), type.getContentType(), m1);
		res.put(m2.key, m2.value);
		return res;
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			type = ITypeProvider.BOTH,
			content_type = ITypeProvider.BOTH,
			category = IOperatorCategory.CONTAINER,
			concept = {})
	@doc (
			value = "returns a new map containing all the elements of the first operand not present in the second operand",
			examples = { @example (
					value = "['a'::1,'b'::2] - ['b'::2]",
					equals = "['a'::1]"),
					@example (
							value = "['a'::1,'b'::2] - ['b'::2,'c'::3]",
							equals = "['a'::1]") },
			see = { "" + IKeyword.MINUS })
	public static IMap minus(final IScope scope, final IMap m1, final IMap m2) {
		final IMap res = notNull(scope, m1).copy(scope);
		res.removeValues(scope, m2);
		return res;
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			type = ITypeProvider.TYPE_AT_INDEX + 1,
			content_type = ITypeProvider.BOTH,
			category = IOperatorCategory.CONTAINER,
			concept = {})
	@doc (
			value = "returns a new map containing all the elements of the first operand without the one of the second operand",
			examples = { @example (
					value = "['a'::1,'b'::2] - ('b'::2)",
					equals = "['a'::1]"),
					@example (
							value = "['a'::1,'b'::2] - ('c'::3)",
							equals = "['a'::1,'b'::2]") },
			see = { "" + IKeyword.MINUS })
	public static IMap minus(final IScope scope, final IMap m1, final GamaPair m2) {
		final IMap res = notNull(scope, m1).copy(scope);
		res.remove(m2.getKey());
		return res;
	}

}
