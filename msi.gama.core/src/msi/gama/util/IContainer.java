/*******************************************************************************************************
 *
 * msi.gama.util.IContainer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.util.Collection;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.StreamEx;

/**
 * Written by drogoul Modified on 3 juin 2010
 *
 * @todo Description
 *
 */
public interface IContainer<KeyType, ValueType> extends IValue {

	@Override
	IContainerType<?> getGamlType();

	IList<ValueType> listValue(IScope scope, IType<?> contentType, boolean copy);

	IMatrix<?> matrixValue(IScope scope, IType<?> contentType, boolean copy);

	IMatrix<?> matrixValue(IScope scope, IType<?> contentType, ILocation size, boolean copy);

	<D, C> IMap<C, D> mapValue(IScope scope, IType<C> keyType, IType<D> contentType, boolean copy);

	java.lang.Iterable<? extends ValueType> iterable(final IScope scope);

	/**
	 * Internal method to get a correct stream in order to reuse the algorithms of the Java Collections Framework. The
	 * intent is to reduce to the minimum the amount of computation needed in that case. The default is to return the
	 * stream of the underlying collection when the container is already a collection. Must be redefined in subclasses.
	 *
	 * @param scope
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	default StreamEx<ValueType> stream(final IScope scope) {
		if (this instanceof Collection) { return StreamEx.of(((Collection<ValueType>) this).stream()); }
		return StreamEx.of(listValue(scope, Types.NO_TYPE, false));
	}

	default StreamEx<ValueType> parallelStream(final IScope scope) {
		return stream(scope).parallel(GamaExecutorService.AGENT_PARALLEL_EXECUTOR);
	}

	public interface Addressable<KeyType, ValueType> {

		ValueType get(IScope scope, KeyType index) throws GamaRuntimeException;

		/**
		 * Method sent from GAML with a list containing the index or indices. It is the responsibility of the container
		 * to extract the index and return the value associated (if any)
		 *
		 * @param scope
		 * @param indices
		 * @return
		 * @throws GamaRuntimeException
		 */

		ValueType getFromIndicesList(IScope scope, IList<KeyType> indices) throws GamaRuntimeException;

	}

	public interface Modifiable<KeyType, ValueType> {

		boolean checkBounds(IScope scope, Object index, boolean forAdding);

		// The simple method, that simply contains the object to add
		void addValue(IScope scope, final ValueType value);

		// The same but with an index
		void addValueAtIndex(IScope scope, final Object index, final ValueType value);

		// Set, that takes a mandatory index
		void setValueAtIndex(IScope scope, final Object index, final ValueType value);

		// Then, methods for "all" operations
		// Adds the values if possible, without replacing existing ones
		// AD July 2020: Addition of the index (see #2985)
		void addValues(IScope scope, Object index, IContainer<?, ?> values);

		default void addValues(final IScope scope, final IContainer<?, ?> values) {
			addValues(scope, null, values);
		}

		// Adds this value to all slots (if this operation is available),
		// otherwise replaces the values with this one
		void setAllValues(IScope scope, ValueType value);

		void removeValue(IScope scope, Object value);

		void removeIndex(IScope scope, Object index);

		void removeIndexes(IScope scope, IContainer<?, ?> index);

		void removeValues(IScope scope, IContainer<?, ?> values);

		void removeAllOccurrencesOfValue(IScope scope, Object value);

	}

	// Operators available in GAML

	@operator (
			value = { "contains", "contains_value" },
			can_be_const = true,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "true, if the container contains the right operand, false otherwise. 'contains' can also be written 'contains_value'. On graphs, it is equivalent to calling 'contains_edge'",
			masterDoc = true,
			comment = "the contains operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list or a matrix, contains returns true if the list or matrix contains the right operand",
					examples = { @example (
							value = "[1, 2, 3] contains 2",
							equals = "true"),
							@example (
									value = "[{1,2}, {3,4}, {5,6}] contains {3,4}",
									equals = "true") }),
					@usage ("if it is a map, contains, which can also be written 'contains_value', returns true if the operand is a value of the map"),
					@usage ("if it is a pair, contains_key returns true if the operand is equal to the value of the pair"),
					@usage ("if it is a file, contains returns true it the operand is contained in the file content"),
					@usage ("if it is a population, contains returns true if the operand is an agent of the population, false otherwise"),
					@usage ("if it is a graph, contains can be written 'contains_edge' and  returns true if the operand is an edge of the graph, false otherwise (use 'contains_node' for testing the presence of a node)") },
			see = { "contains_all", "contains_any", "contains_key" })
	@test ("['aa'::'bb', 13::14] contains 'bb'")
	boolean contains(IScope scope, Object o) throws GamaRuntimeException;

	@operator (
			value = { "contains_key", "contains_node" },
			can_be_const = true,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })

	@doc (
			value = "true, if the left-hand operand -- the container -- contains a key -- or an index -- equal to the right-hand operand, false otherwise. On graphs, 'contains_key' is equivalent to calling 'contains_vertex' ",
			masterDoc = true,
			comment = "the behavior of contains_key depends on the nature of the container",
			usages = { @usage (
					value = "if it is a list, contains_key returns true if the right-hand operand is an integer and if it is a valid index (i.e. >= 0 and < length)",
					examples = { @example (
							isExecutable = true,
							value = "[1, 2, 3] contains_key 3",
							equals = "false"),
							@example (
									isExecutable = true,
									value = "[{1,2}, {3,4}, {5,6}] contains_key 0",
									equals = "true") }),
					@usage ("if it is a map, contains_key returns true if the operand is a key of the map"),
					@usage ("if it is a pair, contains_key returns true if the operand is equal to the key of the pair"),
					@usage ("if it is a matrix, contains_key returns true if the point operand is a valid index of the matrix (i.e. >= {0,0} and < {rows, col})"),
					@usage ("if it is a file, contains_key is applied to the file contents -- a container"),
					@usage ("if it is a graph, contains_key returns true if the graph contains the corresponding vertex") },
			see = { "contains_all", "contains", "contains_any" })
	@test ("['aa'::'bb', 13::14] contains_key 'aa'")
	boolean containsKey(IScope scope, Object o) throws GamaRuntimeException;

	@operator (
			value = "first",
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "the first value of the operand",
			masterDoc = true,
			comment = "the first operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list, first returns the first element of the list, or nil if the list is empty",
					examples = { @example (
							value = "first ([1, 2, 3])",
							returnType = IKeyword.INT,
							equals = "1") }),
					@usage (
							value = "if it is a map, first returns the first value of the first pair (in insertion order)"),
					@usage (
							value = "if it is a file, first returns the first element of the content of the file (that is also a container)"),
					@usage (
							value = "if it is a population, first returns the first agent of the population"),
					@usage (
							value = "if it is a graph, first returns the first edge (in creation order)"),
					@usage (
							value = "if it is a matrix, first returns the element at {0,0} in the matrix"),
					@usage (
							value = "for a matrix of int or float, it will return 0 if the matrix is empty"),
					@usage (
							value = "for a matrix of object or geometry, it will return nil if the matrix is empty") },
			see = { "last" })
	ValueType firstValue(IScope scope) throws GamaRuntimeException;

	@operator (
			value = "last",
			can_be_const = true,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "the last element of the operand",
			masterDoc = true,
			comment = "the last operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list, last returns the last element of the list, or nil if the list is empty",
					examples = { @example (
							value = "last ([1, 2, 3])",
							returnType = IKeyword.INT,
							equals = "3") }),
					@usage (
							value = "if it is a map, last returns the value of the last pair (in insertion order)"),
					@usage (
							value = "if it is a file, last returns the last element of the content of the file (that is also a container)"),
					@usage (
							value = "if it is a population, last returns the last agent of the population"),
					@usage (
							value = "if it is a graph, last returns a list containing the last edge created"),
					@usage (
							value = "if it is a matrix, last returns the element at {length-1,length-1} in the matrix"),
					@usage (
							value = "for a matrix of int or float, it will return 0 if the matrix is empty"),
					@usage (
							value = "for a matrix of object or geometry, it will return nil if the matrix is empty") },
			see = { "first" })
	ValueType lastValue(IScope scope) throws GamaRuntimeException;

	@operator (
			value = "length",
			can_be_const = true,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "the number of elements contained in the operand",
			masterDoc = true,
			comment = "the length operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list or a map, length returns the number of elements in the list or map",
					examples = { @example (
							value = "length([12,13])",
							equals = "2"),
							@example (
									value = "length([])",
									equals = "0") }),
					@usage ("if it is a population, length returns number of agents of the population"),
					@usage ("if it is a graph, length returns the number of vertexes or of edges (depending on the way it was created)"),
					@usage (
							value = "if it is a matrix, length returns the number of cells",
							examples = { @example (
									value = "length(matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]))",
									equals = "6") }) })
	int length(IScope scope);

	@operator (
			value = "empty",
			can_be_const = true,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "true if the operand is empty, false otherwise.",
			masterDoc = true,
			comment = "the empty operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list, empty returns true if there is no element in the list, and false otherwise",
					examples = { @example (
							value = "empty([])",
							equals = "true") }),
					@usage (
							value = "if it is a map, empty returns true if the map contains no key-value mappings, and false otherwise"),
					@usage (
							value = "if it is a file, empty returns true if the content of the file (that is also a container) is empty, and false otherwise"),
					@usage (
							value = "if it is a population, empty returns true if there is no agent in the population, and false otherwise"),
					@usage (
							value = "if it is a graph, empty returns true if it contains no vertex and no edge, and false otherwise"),
					@usage (
							value = "if it is a matrix of int, float or object, it will return true if all elements are respectively 0, 0.0 or null, and false otherwise"),
					@usage (
							value = "if it is a matrix of geometry, it will return true if the matrix contains no cell, and false otherwise") })
	boolean isEmpty(IScope scope);

	@operator (
			value = "reverse",
			can_be_const = true,
			type = ITypeProvider.TYPE_AT_INDEX + 1,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "the operand elements in the reversed order in a copy of the operand.",
			masterDoc = true,
			comment = "the reverse operator behavior depends on the nature of the operand",
			usages = { @usage (
					value = "if it is a list, reverse returns a copy of the operand list with elements in the reversed order",
					examples = { @example (
							value = "reverse ([10,12,14])",
							equals = "[14, 12, 10]",
							returnType = "list<int>") }),
					@usage (
							value = "if it is a map, reverse returns a copy of the operand map with each pair in the reversed order (i.e. all keys become values and values become keys)",
							examples = { @example (
									value = "reverse (['k1'::44, 'k2'::32, 'k3'::12])",
									equals = "[44::'k1', 32::'k2', 12::'k3']",
									returnType = "map<int,string>") }),
					@usage (
							value = "if it is a file, reverse returns a copy of the file with a reversed content"),
					@usage (
							value = "if it is a population, reverse returns a copy of the population with elements in the reversed order"),
					@usage (
							value = "if it is a graph, reverse returns a copy of the graph (with all edges and vertexes), with all of the edges reversed"),
					@usage (
							value = "if it is a matrix, reverse returns a new matrix containing the transpose of the operand.",
							examples = { @example (
									returnType = "matrix<string>",
									value = "reverse(matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]))",
									equals = "matrix([[\"c11\",\"c21\"],[\"c12\",\"c22\"],[\"c13\",\"c23\"]])") }) })
	IContainer<?, ?> reverse(IScope scope) throws GamaRuntimeException;

	/**
	 * @return one of the values stored in this container using GAMA.getRandom()
	 */
	@operator (
			value = { "one_of", "any" },
			can_be_const = false,
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.CONTAINER })
	@doc (
			value = "one of the values stored in this container  at a random key",
			masterDoc = true,
			comment = "the one_of operator behavior depends on the nature of the operand",
			usages = { @usage (value = "if the operand is empty, one_of returns nil"),
					@usage (
							value = "if it is a list or a matrix, one_of returns one of the values of the list or of the matrix",
							examples = { @example (
									value = "any ([1,2,3])",
									var = "i",
									equals = "1, 2 or 3",
									returnType = IKeyword.INT,
									test = false),
									@example ("string sMat <- one_of(matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]])); 	// sMat equals \"c11\",\"c12\",\"c13\", \"c21\",\"c22\" or \"c23\""),
							}),
					@usage (
							value = "if it is a map, one_of returns one the value of a random pair of the map",
							examples = { @example ("int im <- one_of ([2::3, 4::5, 6::7]);	// im equals 3, 5 or 7"),
									@example (
											value = "[2::3, 4::5, 6::7].values contains im",
											returnType = IKeyword.BOOL,
											equals = "true") }),
					@usage (
							value = "if it is a graph, one_of returns one of the lists of edges"),
					@usage (
							value = "if it is a file, one_of returns one of the elements of the content of the file (that is also a container)"),
					@usage (
							value = "if it is a population, one_of returns one of the agents of the population",
							examples = {
									@example (
											value = "bug b <- one_of(bug);  	// Given a previously defined species bug, b is one of the created bugs, e.g. bug3",
											isExecutable = false) }) },
			see = { "contains" })
	@test("one_of([]) = nil")
	@test("int i  <- any([1,2,3]); [1,2,3] contains i")
	@test("string sMat <- one_of(matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]])); matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]) contains sMat")
	@test("agent b <- one_of(agents);")
	ValueType anyValue(IScope scope);

}