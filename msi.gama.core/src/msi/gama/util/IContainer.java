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

import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 3 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IContainer<KeyType, ValueType> extends IValue, Iterable<ValueType> {

	// Operators available in GAML

	@operator(value = { IKeyword.AT, "@" }, can_be_const = true, type = ITypeProvider.LEFT_CONTENT_TYPE)
	public ValueType get(KeyType index) throws GamaRuntimeException;

	@operator(value = "contains", can_be_const = true)
	public boolean contains(Object o) throws GamaRuntimeException;

	@operator(value = "first", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc(
		value = "the first element of the operand",
		comment = "the first operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color, first returns the red component",			
			"if it is a list, first returns the first element of the list, or nil if the list is empty",
			"if it is a map, first returns nil (the map do not keep track of the order of elements)",
			"if it is a pair, first returns the key of the pair key::value",
			"if it is a point, first returns the x-coordinate of a point",
			"if it is a file, first returns the first element of the content of the file (that is also a container)",
			"if it is a graph, first returns the first element in the list of vertexes",
			"if it is a matrix, first returns the element at {0,0} in the matrix",
			"for a matrix of int or float, it will return 0 if the matrix is empty",
		"for a matrix of object or geometry, it will return null if the matrix is empty"},
		examples = {"first ([1, 2, 3]) 		--:   1",
					"first ({10,12})     	--:   10."},
		see = {"last"})		
	public ValueType first() throws GamaRuntimeException;

	
	@operator(value = "last", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc(
		value = "the last element of the operand",
		comment = "the last operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color, last returns the blue component",
			"if it is a list, last returns the last element of the list, or nil if the list is empty",
			"if it is a map, last returns nil (the map do not keep track of the order of elements)",
			"if it is a pair, last returns the value of the pair key::value",
			"if it is a point, last returns the y-coordinate of a point",
			"if it is a file, last returns the last element of the content of the file (that is also a container)",
			"if it is a graph, last returns the last element in the list of vertexes",
			"if it is a matrix, last returns the element at {length-1,length-1} in the matrix",
			"for a matrix of int or float, it will return 0 if the matrix is empty",
			"for a matrix of object or geometry, it will return null if the matrix is empty"},
		see = {"first"},
		examples = {"last ({10,12}) 	--:   12",
					"last ([1, 2, 3]) 	--:   3."})
	public ValueType last() throws GamaRuntimeException;

	
	@operator(value = "length", can_be_const = true)
	@doc(
		value = "the number of elements contained in the operand",
		comment = "the length operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color, length always returns 3",
			"if it is a list or a map, length returns the number of elements in the list",
			"if it is a point or a pair, length always return 2",
			"if it is a graph, last returns the number of vertexes or of edges (depending on the way it was created)",
			"if it is a matrix, length returns the number of cells"},
		examples = {"length ([12,13])	--: 	2"})
	public int length();

	
	@operator(value = "max", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc(
		value = "the maximum element found in the operand",
		comment = "the max operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color, max returns the maximum of the three components",
			"if it is a list of int of float, max returns the maximum of all the elements",
			"if it is a list of points: max returns the maximum of all points as a point (i.e. the point with the greatest coordinate on the x-axis, in case of equality the point with the greatest coordinate on the y-axis is chosen. If all the points are equal, the first one is returned. )",
			"if it is a list of other type: max transforms all elements into float and returns the maximum of them",
			"if it is a map, max returns the maximum among the list of all elements value",
			"if it is a pair, max returns the maximum of the two elements of the pair if the first is comparable",
			"if it is a point, max returns the maximum of the two coordinates",
			"if it is a file, max returns the maximum of the content of the file (that is also a container)",
			"if it is a graph, max returns the maximum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)",
			"if it is a matrix of int, float or object, max returns the maximum of all the numerical elements (thus all elements for integer and float matrices)",
			"if it is a matrix of geometry, max returns the maximum of the list of the geometries"},
		see = {"min"},
		examples = {"max ([100, 23.2, 34.5]) 	--: 	100.0"})
	public ValueType max(IScope scope) throws GamaRuntimeException;

	
	@operator(value = "min", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc(
		value = "the minimum element found in the operand.",
		comment = "the min operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color, min returns the minimum of the three components",
			"if it is a list of int or float: min returns the minimum of all the elements",
			"if it is a list of points: min returns the minimum of all points as a point (i.e. the point with the smallest coordinate on the x-axis, in case of equality the point with the smallest coordinate on the y-axis is chosen. If all the points are equal, the first one is returned. )",
			"if it is a list of other types: min transforms all elements into float and returns the minimum of them",
			"if it is a map, min returns the minimum among the list of all elements value",
			"if it is a pair, min returns the minimum of the two elements of the pair if the first is comparable",
			"if it is a point, min returns the minimum of the two coordinates",
			"if it is a file, min returns the minimum of the content of the file (that is also a container)",
			"if it is a graph, min returns the minimum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)",
			"if it is a matrix of int, float or object, min returns the minimum of all the numerical elements (thus all elements for integer and float matrices)",
			"if it is a matrix of geometry, min returns the minimum of the list of the geometries"},
		see = {"max"},
		examples = {"min ([100, 23.2, 34.5]) 	--: 	23.2"})
	public ValueType min(IScope scope) throws GamaRuntimeException;

	
	@operator(value = { "mul", "product" }, can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc(
		value = "the product of all the elements of the operand",
		comment = "the mul operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color, mul returns the product of the three components",
			"if it is a list of int or float: mul returns the product of all the elements",
			"if it is a list of points: mul returns the product of all points as a point (each coordinate is the product of the corresponding coordinate of each element)",
			"if it is a list of other types: mul transforms all elements into float and multiplies them",
			"if it is a map, mul returns the product of the value of all elements",
			"if it is a pair, mul returns the product of the two elements of the pair if they are integer or float, 0.0 otherwise",
			"if it is a point, mul returns the product of the two coordinates",
			"if it is a file, mul returns the product of the content of the file (that is also a container)",
			"if it is a graph, mul returns the product of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)",
			"if it is a matrix of int, float or object, mul returns the product of all the numerical elements (thus all elements for integer and float matrices)",
			"if it is a matrix of geometry, mul returns the product of the list of the geometries"},
		see = {"sum"},
		examples = {"mul ([100, 23.2, 34.5]) 	--:		80040.0"})
	public Object product(IScope scope) throws GamaRuntimeException;

	
	@operator(value = "sum", can_be_const = true, type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc(
		value = "the sum of all the elements of the operand",
		comment = "the sum operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color, sum returns the sum of the three components",
			"if it is a list of int or float: sum returns the sum of all the elements",
			"if it is a list of points: sum returns the sum of all points as a point (each coordinate is the sum of the corresponding coordinate of each element)",
			"if it is a list of other types: sum transforms all elements into float and sums them",
			"if it is a map, sum returns the sum of the value of all elements",
			"if it is a pair, sum returns the sum of the two elements of the pair if they are integer or float, 0.0 otherwise",
			"if it is a point, sum returns the sum of the two coordinates",
			"if it is a file, sum returns the sum of the content of the file (that is also a container)",
			"if it is a graph, sum returns the sum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)",
			"if it is a matrix of int, float or object, sum returns the sum of all the numerical elements (i.e. all elements for integer and float matrices)",
			"if it is a matrix of geometry, sum returns the sum of the list of the geometries"},
		see = {"mul"},
		examples = "sum ([12,10, 3]) 	--: 	25.0")
	public Object sum(IScope scope) throws GamaRuntimeException;

	
	@operator(value = "empty", can_be_const = true)
	@doc(
		value ="true if the operand is empty, false otherwise.",
		comment = "the empty operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color or a point, empty returns the false",
			"if it is a list, empty returns true if there is no element in the list, and false otherwise",
			"if it is a map, empty returns true if the map contains no key-value mappings, and false otherwise",
			"if it is a pair, empty returns true if both the key and the value are null, and false otherwise",
			"if it is a file, empty returns true if the content of the file (that is also a container) is empty, and false otherwise",
			"if it is a graph, empty returns true if it contains no vertex and no edge, and false otherwise",
			"if it is a matrix of int, float or object, it will return true if all elements are respectively 0, 0.0 or null, and false otherwise",
			"if it is a matrix of geometry, it will return true if the matrix contains no cell, and false otherwise"},
		examples =  {"empty ([]) 	--: 	true;"})
	public boolean isEmpty();

	
	@operator(value = "reverse", can_be_const = true, type = ITypeProvider.TYPE, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc(
		value = "the operand elements in the reversed order in a copy of the operand.",
		comment = "the reverse operator behavior depends on the nature of the operand",
		specialCases = {
			"if it is a color (say RRGGBB), reverse returns a new color with the colors in the reversed order (BBGGRR)",
			"if it is a list, reverse return a copy of the operand list with elements in the reversed order",
			"if it is a map, reverse returns a copy of the operand map with each pair in the reversed order (i.e. all keys become values and values become keys)",
			"if it is a pair, reverse returns a new pair in the reversed order (the old key is the new value and the old value is the new key)",
			"if it is a point, reverse returns a new point with reversed coordonates",
			"if it is a file, reverse returns a copy of the file with a reversed content",
			"if it is a graph, reverse returns a copy of the graph (with all edges and vertexes), with all of the edges reversed",
			"if it is a matrix, reverse returns a new matrix containing the transpose of the operand."},
		examples = {
			"reverse ({10,13}) 					--: 	{13, 10}",
			"reverse ([10,12,14]) 				--: 	[14, 12, 10]",
			"reverse ([k1::44, k2::32, k3::12]) --: 	[12::k3,  32::k2, 44::k1]"})
	public IContainer<KeyType, ValueType> reverse() throws GamaRuntimeException;

	/**
	 * @return one of the values stored in this container using GAMA.getRandom()
	 */
	// TODO : to finish
	@operator(value = { "one_of", "any" }, can_be_const = false, type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc( 
		value = "a random element from the container",
		comment = "the one_of operator behavior depends on the nature of the operand",
		specialCases = {
			"if the operand is empty, one_of returns nil",
			"if it is a list, one_of returns one of the element of the list",			
			"if it is a map, one_of returns one of the values of the map",
			"In the case of a species, the operand is casted to a list before the expression is evaluated. " +
			"Therefore, if foo is the name of a species, any (foo) will return a random agent from this species "},
		examples = {
			"any ([1,2,3]) 		--: 	1, 2, or 3",
			"one_of ([1,2,3]) 	--:	 	1, 2, or 3",
			"",
			"// The species bug has previously been defined",
			"one_of (bug) 		--:		 bug3",
			"",
			"let mat3 type:matrix value: matrix([[\"c11\",\"c12\",\"c13\"],[\"c21\",\"c22\",\"c23\"]])",
			"one_of(mat3) 		--: 	 \"c11\",\"c12\",\"c13\", \"c21\",\"c22\" or \"c23\"" })
	public ValueType any();

	// Interfaces used by GAML commands add, remove, put

	/**
	 * Checks if the container length is fixed (i.e. if add or remove operations are accepted)
	 * 
	 * @return true, if the length of the container is fixed.
	 */
	public boolean isFixedLength();

	/**
	 * Purpose of this method is to return whether or not the given index is valid.
	 * 
	 * @param index the index (can be null)
	 * @return true, if the index is valid
	 */
	public boolean checkIndex(Object index);

	/**
	 * Purpose of this method is to return whether or not the given value is valid to be stored in
	 * this container.
	 * 
	 * @param value the value (can be null)
	 * @return true, if the value is valid
	 */
	public boolean checkValue(Object value);

	/**
	 * 
	 * Purpose of this method is to return whether or not the given index is within the
	 * bounds allowed by the container.
	 * 
	 * 
	 * @param index the index (can be null)
	 * @param forAdding indicates if the test is made before adding or putting/removing a value
	 * @return true, if the index is within the bounds
	 */
	public boolean checkBounds(KeyType index, boolean forAdding);

	public void addAll(final IContainer value, final Object param) throws GamaRuntimeException;

	public void addAll(final KeyType index, final IContainer value, final Object param)
		throws GamaRuntimeException;

	public void add(final ValueType value, final Object param) throws GamaRuntimeException;

	public void add(final KeyType index, final ValueType value, Object param)
		throws GamaRuntimeException;

	public boolean removeFirst(final ValueType value) throws GamaRuntimeException;

	public boolean removeAll(final IContainer<?, ValueType> value) throws GamaRuntimeException;

	public Object removeAt(final KeyType index) throws GamaRuntimeException;

	public void putAll(final ValueType value, Object param) throws GamaRuntimeException;

	public void put(final KeyType index, final ValueType value, Object param)
		throws GamaRuntimeException;

	public void clear() throws GamaRuntimeException;

	// Casting operations to/from common types of containers

	@operator(value = IType.LIST_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public abstract IList listValue(IScope scope) throws GamaRuntimeException;

	@operator(value = IType.MATRIX_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public abstract IMatrix matrixValue(IScope scope) throws GamaRuntimeException;

	@operator(value = "as_matrix", content_type = ITypeProvider.LEFT_CONTENT_TYPE, can_be_const = true)
	public abstract IMatrix matrixValue(IScope scope, ILocation preferredSize)
		throws GamaRuntimeException;

	@operator(value = IType.MAP_STR, can_be_const = true)
	public abstract Map mapValue(IScope scope) throws GamaRuntimeException;

}
