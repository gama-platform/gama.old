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

import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 3 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IContainer<KeyType, ValueType> extends IValue {

	public abstract IList listValue(IScope scope, IType contentType);

	public abstract IMatrix matrixValue(IScope scope, IType contentType);

	public abstract IMatrix matrixValue(IScope scope, IType contentType, ILocation size);

	public abstract GamaMap mapValue(IScope scope, IType keyType, IType contentType);

	public java.lang.Iterable<? extends ValueType> iterable(final IScope scope);

	public static interface Addressable<KeyType, ValueType> {

		@operator(value = { IKeyword.AT, "@" }, can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.CONTAINER})
		@doc(value = "the element at the right operand index of the container", masterDoc=true, comment = "The first element of the container is located at the index 0. "
			+ "In addition, if the user tries to get the element at an index higher or equals than the length of the container, he will get an IndexOutOfBoundException."
			+ "The at operator behavior depends on the nature of the operand", 
			usages = {
				@usage(value="if it is a list or a matrix, at returns the element at the index specified by the right operand",
					examples={@example(value="[1, 2, 3] at 2", equals="3"), @example(value="[{1,2}, {3,4}, {5,6}] at 0", equals="{1.0,2.0}")}),
				@usage("if it is a file, at returns the element of the file content at the index specified by the right operand"),
				@usage("if it is a population, at returns the agent at the index specified by the right operand"),
				@usage("if it is a graph and if the right operand is a node, at returns the in and out edges corresponding to that node"),
				@usage("if it is a graph and if the right operand is an edge, at returns the pair node_out::node_in of the edge"),
				@usage("if it is a graph and if the right operand is a pair node1::node2, at returns the edge from node1 to node2 in the graph")},
			see = { "contains_all, contains_any" })
		public ValueType get(IScope scope, KeyType index) throws GamaRuntimeException;

		// FIXME No way to test if the index is correct or not

		/**
		 * Method sent from GAML with a list containing the index or indices. It is the responsibility
		 * of the container to extract the index and return the value associated (if any)
		 * @param scope
		 * @param indices
		 * @return
		 * @throws GamaRuntimeException
		 */
		@operator(value = { "internal_at" }, type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.CONTAINER})
		@doc(value="For internal use only. Corresponds to the implementation of the access to containers with [index]", see={IKeyword.AT})
		public ValueType getFromIndicesList(IScope scope, IList<KeyType> indices) throws GamaRuntimeException;

	}

	public static interface Modifiable<KeyType, ValueType> {

		public boolean checkBounds(IScope scope, Object index, boolean forAdding);

		// The simple method, that simply contains the object to add
		public void addValue(IScope scope, final ValueType value);

		// The same but with an index
		public void addValueAtIndex(IScope scope, final KeyType index, final ValueType value);

		// Put, that takes a mandatory index
		public void setValueAtIndex(IScope scope, final KeyType index, final ValueType value);

		// Then, methods for "all" operations
		// Adds the values if possible, without replacing existing ones
		public void addVallues(IScope scope, IContainer<?, ValueType> values);

		// Adds this value to all slots (if this operation is available), otherwise replaces the values with this one
		public void setAllValues(IScope scope, ValueType value);

		public void removeValue(IScope scope, Object value);

		public void removeIndex(IScope scope, Object index);

		public void removeIndexes(IScope scope, IContainer<?, Object> index);

		public void removeValues(IScope scope, IContainer<?, ?> values);

		public void removeAllOccurencesOfValue(IScope scope, Object value);

		public ValueType buildValue(IScope scope, Object object, IContainerType containerType);

		public IContainer<?, ValueType> buildValues(IScope scope, IContainer objects, IContainerType containerType);

		public KeyType buildIndex(IScope scope, Object object, IContainerType containerType);

		public IContainer<?, KeyType> buildIndexes(IScope scope, IContainer value, IContainerType containerType);

	}

	// Operators available in GAML

	@operator(value = "contains", can_be_const = true, category={IOperatorCategory.CONTAINER})
	@doc(value = "true, if the container contains the right operand, false otherwise", masterDoc=true, comment = "the contains operator behavior depends on the nature of the operand", usages = {
		@usage(value="if it is a list or a matrix, contains returns true if the list or matrix contains the right operand", examples = {
			@example(value="[1, 2, 3] contains 2", equals="true"),
			@example(value="[{1,2}, {3,4}, {5,6}] contains {3,4}", equals="true")}),
		@usage("if it is a map, contains returns true if the operand is a key of the map"),
		@usage("if it is a file, contains returns true it the operand is contained in the file content"),
		@usage("if it is a population, contains returns true if the operand is an agent of the population, false otherwise"),
		@usage("if it is a graph, contains returns true if the operand is a node or an edge of the graph, false otherwise")},
		see = { "contains_all, contains_any" })
	public boolean contains(IScope scope, Object o) throws GamaRuntimeException;

	@operator(value = "first", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.CONTAINER})
	@doc(value = "the first value of the operand", masterDoc=true, comment = "the first operator behavior depends on the nature of the operand", usages = {
		@usage(value="if it is a list, first returns the first element of the list, or nil if the list is empty", examples = {
			@example(value="first ([1, 2, 3])", equals="1")}),
		@usage("if it is a map, first returns the first value of the first pair (in insertion order)"),
		@usage("if it is a file, first returns the first element of the content of the file (that is also a container)"),
		@usage("if it is a population, first returns the first agent of the population"),
		@usage("if it is a graph, first returns the first edge (in creation order)"),
		@usage("if it is a matrix, first returns the element at {0,0} in the matrix"),
		@usage("for a matrix of int or float, it will return 0 if the matrix is empty"),
		@usage("for a matrix of object or geometry, it will return nil if the matrix is empty")}, see = { "last" })
	public ValueType firstValue(IScope scope) throws GamaRuntimeException;

	@operator(value = "last", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.CONTAINER})
	@doc(value = "the last element of the operand", masterDoc=true, comment = "the last operator behavior depends on the nature of the operand", usages = {
		@usage(value="if it is a list, last returns the last element of the list, or nil if the list is empty",examples = {
			@example(value="last ([1, 2, 3])",equals="3")}),
		@usage("if it is a map, last returns the value of the last pair (in insertion order)"),
		@usage("if it is a file, last returns the last element of the content of the file (that is also a container)"),
		@usage("if it is a population, last returns the last agent of the population"),
		@usage("if it is a graph, last returns a list containing the last edge created"),
		@usage("if it is a matrix, last returns the element at {length-1,length-1} in the matrix"),
		@usage("for a matrix of int or float, it will return 0 if the matrix is empty"),
		@usage("for a matrix of object or geometry, it will return nil if the matrix is empty")}, see = { "first" })
	public ValueType lastValue(IScope scope) throws GamaRuntimeException;

	@operator(value = "length", can_be_const = true, category={IOperatorCategory.CONTAINER})
	@doc(value = "the number of elements contained in the operand", masterDoc=true, comment = "the length operator behavior depends on the nature of the operand", usages = {
		@usage(value="if it is a list or a map, length returns the number of elements in the list or map", examples = {
			@example(value="length([12,13])", equals="2"),@example(value="length([])",equals="0")}),
		@usage("if it is a population, length returns number of agents of the population"),
		@usage("if it is a graph, length returns the number of vertexes or of edges (depending on the way it was created)"),
		@usage(value="if it is a matrix, length returns the number of cells", examples = {
			@example(value="length(matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]))", equals="6")})})
	public int length(IScope scope);
	
	@operator(value = "empty", can_be_const = true, category={IOperatorCategory.CONTAINER})
	@doc(value = "true if the operand is empty, false otherwise.", masterDoc=true, comment = "the empty operator behavior depends on the nature of the operand", usages={
		@usage(value="if it is a list, empty returns true if there is no element in the list, and false otherwise", examples={
			@example(value="empty([])", equals="true")}),
		@usage("if it is a map, empty returns true if the map contains no key-value mappings, and false otherwise"),
		@usage("if it is a file, empty returns true if the content of the file (that is also a container) is empty, and false otherwise"),
		@usage("if it is a population, empty returns true if there is no agent in the population, and false otherwise"),
		@usage("if it is a graph, empty returns true if it contains no vertex and no edge, and false otherwise"),
		@usage("if it is a matrix of int, float or object, it will return true if all elements are respectively 0, 0.0 or null, and false otherwise"),
		@usage("if it is a matrix of geometry, it will return true if the matrix contains no cell, and false otherwise")})
	public boolean isEmpty(IScope scope);

	@operator(value = "reverse", can_be_const = true, type = ITypeProvider.TYPE, content_type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.CONTAINER})
	@doc(value = "the operand elements in the reversed order in a copy of the operand.", masterDoc=true, comment = "the reverse operator behavior depends on the nature of the operand", usages={
		@usage(value="if it is a list, reverse returns a copy of the operand list with elements in the reversed order", examples={
			@example(value="reverse ([10,12,14])", equals="[14, 12, 10]")}),
		@usage(value="if it is a map, reverse returns a copy of the operand map with each pair in the reversed order (i.e. all keys become values and values become keys)", examples={
			@example(value="reverse (['k1'::44, 'k2'::32, 'k3'::12])", equals="[12::'k3',  32::'k2', 44::'k1']")}),
		@usage("if it is a file, reverse returns a copy of the file with a reversed content"),
		@usage("if it is a population, reverse returns a copy of the population with elements in the reversed order"),
		@usage("if it is a graph, reverse returns a copy of the graph (with all edges and vertexes), with all of the edges reversed"),
		@usage(value="if it is a matrix, reverse returns a new matrix containing the transpose of the operand.", examples={
			@example(value="reverse(matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]))", equals="matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]])")})})
	public IContainer<KeyType, ValueType> reverse(IScope scope) throws GamaRuntimeException;

	/**
	 * @return one of the values stored in this container using GAMA.getRandom()
	 */
	@operator(value = { "one_of", "any" }, can_be_const = false, type = ITypeProvider.FIRST_CONTENT_TYPE, category={IOperatorCategory.CONTAINER})
	@doc(value = "one of the values stored in this container  at a random key", masterDoc=true, comment = "the one_of operator behavior depends on the nature of the operand", 
		usages = {
			@usage(value="if the operand is empty, one_of returns nil", examples={
				@example(value="one_of([])",equals="nil")}),
			@usage(value="if it is a list or a matrix, one_of returns one of the values of the list or of the matrix", examples ={
					@example("int i <- any ([1,2,3]);	// i equals 1, 2 or 3"),@example(value="[1,2,3] contains i",equals="true", isTestOnly=true),
					@example("string sMat <- one_of(matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]])); 	// sMat equals \"c11\",\"c12\",\"c13\", \"c21\",\"c22\" or \"c23\""),
					@example(value="matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]]) contains sMat", equals="true",isTestOnly=true)}),
			@usage(value="if it is a map, one_of returns one the value of a random pair of the map", examples ={
					@example("int im <- one_of ([2::3, 4::5, 6::7]);	// im equals 3, 5 or 7"),@example(value="[2::3, 4::5, 6::7].values contains im",equals="true")}),
			@usage(value="if it is a graph, one_of returns one of the lists of edges"),
			@usage(value="if it is a file, one_of returns one of the elements of the content of the file (that is also a container)"),
			@usage(value="if it is a population, one_of returns one of the agents of the population", examples={
				@example(value="agent b <- one_of(agents);", isTestOnly=true),@example(value="bug b <- one_of(bug);  	// Given a previously defined species bug, b is one of the created bugs, e.g. bug3", isExecutable=false)})},
		see = {"contains"})
	public ValueType anyValue(IScope scope);

}
