/*********************************************************************************************
 *
 *
 * 'Cast.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.operators;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 15 d�c. 2010
 *
 * @todo Description
 *
 */
public class Cast {

	public static <T> T as(final Object value, final Class<T> clazz, final boolean copy) {
		return GAMA.run(new InScope<T>() {

			@Override
			public T run(final IScope scope) {
				final IType<T> t = Types.get(clazz);
				return t.cast(scope, value, null, copy);
			}

		});
	}

	public static <T> T as(final IExpression value, final Class<T> clazz, final boolean copy) {
		return GAMA.run(new InScope<T>() {

			@Override
			public T run(final IScope scope) {
				final IType<T> t = Types.get(clazz);
				return t.cast(scope, value.value(scope), null, copy);
			}

		});
	}

	// @operator(value = { "containing" }, type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.SECOND_TYPE)
	// @doc(value = "Internal use only. Allows to cast the contents type of containers")
	// public static Object containing(final IScope scope, final Object a, final IExpression b) {
	// return a;
	// }

	@operator(value = { IKeyword.IS }, category = { IOperatorCategory.CASTING }, concept = {IConcept.CASTING_OPERATOR, IConcept.TYPE} )
	@doc(value = "returns true if the left operand is of the right operand type, false otherwise",
		examples = { @example(value = "0 is int", equals = "true"),
			@example(value = "an_agent is node", equals = "true", isExecutable = false),
			@example(value = "1 is float", equals = "false") })
	public static Boolean isA(final IScope scope, final Object a, final IExpression b) throws GamaRuntimeException {
		final IType type = asType(scope, b);
		if ( type.isAgentType() ) {
			final ISpecies s = scope.getSimulationScope().getModel().getSpecies(type.getSpeciesName());
			if ( a instanceof IAgent ) { return ((IAgent) a).isInstanceOf(s, false); }
			return false;
		}
		return type.isAssignableFrom(GamaType.of(a));
	}

	@operator(value = IKeyword.IS_SKILL, category = { IOperatorCategory.CASTING }, concept = {IConcept.CASTING_OPERATOR, IConcept.SKILL})
	@doc(value = "returns true if the left operand is an agent whose species implementes the right-hand skill name",
		examples = { @example(value = "agentA is_skill 'moving'", equals = "true", isExecutable = false) })
	public static Boolean isSkill(final IScope scope, final Object a, final String skill) {
		if ( !(a instanceof IAgent) ) { return false; }
		final ISpecies s = ((IAgent) a).getSpecies();
		return s.implementsSkill(skill);
	}

	public static IType asType(final IScope scope, final IExpression expr) throws GamaRuntimeException {
		final Object value = expr.value(scope);
		if ( value instanceof String ) {
			final IModel m = scope.getSimulationScope().getModel();
			return m.getDescription().getTypeNamed((String) value);
		} else if ( value instanceof ISpecies ) {
			return ((ISpecies) value).getDescription().getType();
		} else {
			return expr.getType();
		}
	}

	public static IGraph asGraph(final IScope scope, final Object val) {
		return GamaGraphType.staticCast(scope, val, null, false);
	}

	@operator(value = IKeyword.TOPOLOGY, content_type = IType.GEOMETRY, category = { IOperatorCategory.CASTING }, concept = {IConcept.CASTING_OPERATOR})
	@doc(value = "casting of the operand to a topology.",
		usages = { @usage("if the operand is a topology, returns the topology itself;"),
			@usage("if the operand is a spatial graph, returns the graph topology associated;"),
			@usage("if the operand is a population, returns the topology of the population;"),
			@usage("if the operand is a shape or a geometry, returns the continuous topology bounded by the geometry;"),
			@usage("if the operand is a matrix, returns the grid topology associated"),
			@usage("if the operand is another kind of container, returns the multiple topology associated to the container"),
			@usage("otherwise, casts the operand to a geometry and build a topology from it.") },
		examples = { @example(value = "topology(0)", equals = "nil", isExecutable = true),
			@example(
				value = "topology(a_graph)	--: Multiple topology in POLYGON ((24.712119771887785 7.867357373616512, 24.712119771887785 61.283226839310565, 82.4013676510046  7.867357373616512)) " +
					"at location[53.556743711446195;34.57529210646354]",
				isExecutable = false) },
		see = { "geometry" })
	public static ITopology asTopology(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaTopologyType.staticCast(scope, val, false);
	}

	// @operator(value = IKeyword.AGENT)
	// @doc(value =
	// "casting of the operand to an agent (if a species name is used, casting to an instance of species name).",
	// special_cases = {
	// "if the operand is a point, returns the closest agent (resp. closest instance of species name) to that point (computed in the topology of the calling agent);",
	// "if the operand is an agent, returns the agent (resp. tries to cast this agent to species name and returns nil if the agent is instance of another species);",
	// "if the operand is an int, returns the agent (resp. instance of species name) with this unique index;" },
	// examples = {
	// "species node {}", "node(0) --: node0", "node(3.78) --: null", "node(true) --: null",
	// "node({23, 4.0} --: node2", "node(5::34) --: null", "node(green) --: null", "node([1,5,9,3]) --: null",
	// "node(node1) --: node1", "node('4') --: null" }, see = { "of_species", "species" })
	public static IAgent asAgent(final IScope scope, final Object val) throws GamaRuntimeException {
		return (IAgent) Types.AGENT.cast(scope, val, null, false);
	}

	@operator(value = IKeyword.AS,
		type = ITypeProvider.SECOND_TYPE,
		content_type = ITypeProvider.SECOND_CONTENT_TYPE,
		index_type = ITypeProvider.SECOND_KEY_TYPE,
		can_be_const = true,
		category = { IOperatorCategory.CASTING },
		concept = {IConcept.CASTING_OPERATOR})
	@doc(value = "casting of the first argument into a given type",
		comment = "It is equivalent to the application of the type operator on the left operand.",
		examples = @example(value = "3.5 as int", returnType = "int", equals = "int(3.5)"))
	public static Object as(final IScope scope, final Object val, final IExpression expr) {
		// WARNING copy is set explicity to false
		return expr.getType().cast(scope, val, null, false);
	}

	// @operator(value = IKeyword.AS, type = ITypeProvider.SECOND_CONTENT_TYPE, content_type =
	// ITypeProvider.SECOND_CONTENT_TYPE)
	// @doc(value = "casting of the left-hand operand to a species.", special_cases = {
	// "if the right-hand operand is nil, transforms the left-hand operand into an agent",
	// "if the left-hand operand is nil, returns nil",
	// "if the left-operand is a integer, returns an agent of the species with the right-operans as id",
	// "if the left-operand is a point, returns the agent of the species closest to the right-hand operand",
	// "otherwise, returns nil" }, see = { "agent" })
	// public static IAgent asAgent(final IScope scope, final Object val, final ISpecies species)
	// throws GamaRuntimeException {
	// if ( species == null ) { return asAgent(scope, val); }
	// return (IAgent) species.getDescription().getType().cast(scope, val, species, Types.NO_TYPE, Types.NO_TYPE);
	// }

	// @operator(value = IKeyword.BOOL, can_be_const = true)
	// @doc(deprecated = "generated automatically now", value = "casting of the operand to a boolean value.",
	// special_cases = {
	// "if the operand is null, returns false;", "if the operand is an agent, returns true if the agent is not dead;",
	// "if the operand is an int or a float, returns true if it is not equal to 0 (or 0.0);",
	// "if the operand is a file, bool is formally equivalent to exists;",
	// "if the operand is a container, bool is formally equivalent to not empty (a la Lisp);",
	// "if the operand is a string, returns true is the operand is true;", "Otherwise, returns false." }, examples = {
	// "bool(3.78) --: true", "bool(true) --: true", "bool({23, 4.0} --: false", "bool(5::34) --: false",
	// "bool(green) --: false", "bool([1,5,9,3]) --: true", "bool(node1) --: true", "bool('4') --: false",
	// "bool('4.7') --: false " })
	public static Boolean asBool(final IScope scope, final Object val, final boolean copy) {
		// copy not passed
		return GamaBoolType.staticCast(scope, val, null, false);
	}

	public static Boolean asBool(final IScope scope, final Object val) {
		// copy not passed
		return asBool(scope, val, false);
	}

	// @operator(value = IKeyword.RGB, can_be_const = true)
	// @doc(value = "casting of the operand to a rgb color.", special_cases = {
	// "if the operand is nil, returns white;",
	// "if the operand is a string, the allowed color names are the constants defined in the java.awt.Color class, "
	// + "i.e.: black, blue, cyan, darkGray, lightGray, gray, green, magenta, orange, pink, red, white, yellow. "
	// + "Otherwise tries to cast the string to an int and returns this color",
	// "if the operand is a list, the integer value associated to the three first elements of the list are used to define the three red "
	// +
	// "(element 0 of the list), green (element 1 of the list) and blue (element 2 of the list) components of the color;",
	// "if the operand is a map, the red, green, blue components take the value associated to the keys \"r\", \"g\", \"b\" in the map;",
	// "if the operand is a matrix, return the color of the matrix casted as a list;",
	// "if the operand is a boolean, returns black for true and white for false;",
	// "if the operand is an integer value, the decimal integer is translated into a hexadecimal value: OxRRGGBB. "
	// + "The red (resp. green, blue) component of the color take the value RR (resp. GG, BB) translated in decimal." },
	// examples = {
	// "rgb(3.78) --: rgb([0,0,3])", "rgb(true) --: rgb([0,0,0]) //black ",
	// "rgb({23, 4.0} --: rgb([0,0,0]) //black ", "rgb(5::34) --: rgb([0,0,0]) //black ",
	// "rgb(green) --: rgb([0,255,0]) //green ", "rgb([1,5,9,3]) --: rgb([1,5,9])",
	// "rgb(node1) --: rgb([0,0,1])", "rgb('4') --: rgb([0,0,4])", "rgb('4.7') --: // Exception " })
	public static GamaColor asColor(final IScope scope, final Object val, final boolean copy)
		throws GamaRuntimeException {
		// copy not passed
		return GamaColorType.staticCast(scope, val, null, false);
	}

	public static GamaColor asColor(final IScope scope, final Object val) {
		return asColor(scope, val, false);
	}

	// @operator(value = IKeyword.FLOAT, can_be_const = true)
	// @doc(value = "casting of the operand to a floating point value.", special_cases = {
	// "if the operand is numerical value, returns its value as a floating point value;",
	// "if the operand is a string, tries to convert its content to a floating point value;",
	// "if the operand is a boolean, returns 1.0 for true and 0.0 for false;", "otherwise, returns 0.0" }, examples = {
	// "float(7) --: 7.0", "float(true) --: 1.0", "float({23, 4.0} --: 0.0", "float(5::34) --: 0.0",
	// "float(green) --: 0.0", "float([1,5,9,3]) --: 0.0", "float(node1) --: 0.0", "int('4') --: 4.0",
	// "int('4.7') --: 4.7 " }, see = { "int" })
	public static Double asFloat(final IScope scope, final Object val) {
		return GamaFloatType.staticCast(scope, val, null, false);
	}

	// @operator(IKeyword.GEOMETRY)
	// @doc(value = "casts the operand into a geometry", special_cases = {
	// "if the operand is a point, returns a corresponding geometry point",
	// "if the operand is a agent, returns its geometry",
	// "if the operand is a population, returns the union of each agent geometry",
	// "if the operand is a pair of two agents or geometries, returns the link between the geometry of each element of the operand",
	// "if the operans is a graph, returns the corresponding multi-points geometry",
	// "if the operand is a container of points, if first and the last points are the same, returns the polygon built from these points",
	// "if the operand is a container, returns the union of the geometry of each element", "otherwise, returns nil" },
	// examples = {
	// "geometry({23, 4.0}) --: Point", "geometry(a_graph) --: MultiPoint",
	// "geometry(node1) --: Point", "geometry([{0,0},{1,4},{4,8},{0,0}]) --: Polygon " })
	public static IShape asGeometry(final IScope scope, final Object s, final boolean copy)
		throws GamaRuntimeException {
		return GamaGeometryType.staticCast(scope, s, null, copy);
	}

	public static IShape asGeometry(final IScope scope, final Object s) throws GamaRuntimeException {
		return asGeometry(scope, s, false);
	}

	// @operator(value = IKeyword.INT, can_be_const = true)
	// @doc(value = "casting of the operand to an integer value.", special_cases = {
	// "if the operand is a float, returns its value truncated (but not rounded);",
	// "if the operand is an agent, returns its unique index;",
	// "if the operand is a string, tries to convert its content to an integer value;",
	// "if the operand is a boolean, returns 1 for true and 0 for false;",
	// "if the operand is a color, returns its RGB value as an integer;", "otherwise, returns 0" }, examples = {
	// "int(3.78) --: 3", "int(true) --: 1", "int({23, 4.0} --: 0", "int(5::34) --: 0",
	// "int(green) --: -16711936", "int([1,5,9,3]) --: 0", "int(node1) --: 1", "int('4') --: 4",
	// "int('4.7') --: // Exception " }, see = { "round", "float" })
	public static Integer asInt(final IScope scope, final Object val) {
		return GamaIntegerType.staticCast(scope, val, null, false);
	}

	// @operator(value = IKeyword.PAIR, can_be_const = true)
	// @doc(deprecated = "generated automatically now", value = "casting of the operand to a pair value.", special_cases
	// = {
	// "if the operand is null, returns null;",
	// "if the operand is a point, returns the pair x-coordinate::y-coordinate;",
	// "if the operand is a particular kind of geometry, a link between geometry, returns the pair formed with these two geoemtries;",
	// "if the operand is a map, returns the pair where the first element is the list of all the keys of the map and the second element is the list of all the values of the map;",
	// "if the operand is a list, returns a pair with the two first element of the list used to built the pair",
	// "Otherwise, returns the pair operand::operand." }, examples = { "pair(true) --: true::true",
	// "pair({23, 4.0} --: 23.0::4.0", "pair([1,5,9,3]) --: 1::5", "pair([[3,7],[2,6,9],0]) --: [3,7]::[2,6,9]",
	// "pair(['a'::345, 'b'::13, 'c'::12]) --: [b,c,a]::[13,12,345]" })
	// @Deprecated
	public static GamaPair asPair(final IScope scope, final Object val, final boolean copy)
		throws GamaRuntimeException {
		return GamaPairType.staticCast(scope, val, Types.NO_TYPE, Types.NO_TYPE, copy);
	}

	// @operator(value = IKeyword.STRING, can_be_const = true)
	// @doc(deprecated = "generated automatically now", value = "casting of the operand to a string.", special_cases = {
	// "if the operand is nil, returns 'nil';",
	// "if the operand is an agent, returns its name;",
	// "if the operand is a string, returns the operand;",
	// "if the operand is an int or a float, returns their string representation (as in Java);",
	// "if the operand is a boolean, returns 'true' or 'false';",
	// "if the operand is a species, returns its name;",
	// "if the operand is a color, returns its litteral value if it has been created with one (i.e. 'black', 'green', etc.) or the string representation of its hexadecimal value.",
	// "if the operand is a container, returns its string representation." }, examples = { "string(0) --: 0",
	// "string({23, 4.0} --: {23.0;4.0}", "string(5::34) --: 5::34",
	// "string(['a'::345, 'b'::13, 'c'::12]) --: b,13; c,12; a,345;" })
	// @Deprecated
	public static String asString(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaStringType.staticCast(scope, val, false);
	}

	// @operator(value = IKeyword.POINT, can_be_const = true)
	// @doc(deprecated = "generated automatically now", value = "casting of the operand to a point value.",
	// special_cases = {
	// "if the operand is null, returns null;",
	// "if the operand is an agent, returns its location",
	// "if the operand is a geometry, returns its centroid",
	// "if the operand is a list with at least two elements, returns a point with the two first elements of the list (casted to float)",
	// "if the operand is a map, returns the point with values associated respectively with keys \"x\" and \"y\"",
	// "if the operand is a pair, returns a point with the two elements of the pair (casted to float)",
	// "otherwise, returns a point {val,val} where val is the float value of the operand" }, examples = {
	// "point(0) --: {0.0;0.0}", "point(true) --: {1.0;1.0}", "point(5::34) --: {5.0;34.0}",
	// "point([1,5,9,3]) --: {1.0;5.0}", "point([[3,7],[2,6,9],0]) --:{0.0;0.0}",
	// "point(['a'::345, 'y'::13, 'c'::12]) --: {0.0;13.0}",
	// "point(node1) --: {64.06165572529225;18.401233796267537} // centroid of node1 shape" })
	// @Deprecated
	public static ILocation asPoint(final IScope scope, final Object val, final boolean copy) {
		return GamaPointType.staticCast(scope, val, copy);
	}

	public static ILocation asPoint(final IScope scope, final Object val) {
		return asPoint(scope, val, false);
	}

	// @operator(value = IKeyword.MAP, can_be_const = true)
	// @doc(deprecated = "generated automatically now", value = "casting of the operand to a map.", special_cases = {
	// "if the operand is pair, returns a map with this only element;",
	// "if the operand is a agent or a shape, returns a map containing all the attributes as a pair attribute_name::attribute_value;",
	// "if the operand is a list, returns a map containing either elements of the list if it is a list of pairs, or pairs list[i]::list[i];",
	// "if the operand is a file, returns the content casted to map;",
	// "if the operand is a graph, returns the a map with pairs node_source::node_target;",
	// "otherwise returns a map containing only the pair operand::operand." }, examples = {})
	// @Deprecated
	public static GamaMap asMap(final IScope scope, final Object val, final boolean copy) throws GamaRuntimeException {
		return (GamaMap) Types.MAP.cast(scope, val, null, copy);
	}

	@operator(value = "as_int", can_be_const = true, category = { IOperatorCategory.CASTING }, concept = {IConcept.CASTING_OPERATOR})
	@doc(value = "parses the string argument as a signed integer in the radix specified by the second argument.",
		usages = { @usage("if the left operand is nil or empty, as_int returns 0"),
			@usage("if the left operand does not represent an integer in the specified radix, as_int throws an exception ") },
		examples = { @example(value = "'20' as_int 10", equals = "20"),
			@example(value = "'20' as_int 8", equals = "16"), @example(value = "'20' as_int 16", equals = "32"),
			@example(value = "'1F' as_int 16", equals = "31"),
			@example(value = "'hello' as_int 32", equals = "18306744") },
		see = { "int" })
	public static Integer asInt(final IScope scope, final String string, final Integer radix)
		throws GamaRuntimeException {
		if ( string == null || string.isEmpty() ) { return 0; }
		return GamaIntegerType.staticCast(scope, string, radix, false);
	}

	// @operator(value = IKeyword.LIST, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE_OR_TYPE)
	// @doc(value = "transforms the operand into a list", comment =
	// "list always tries to cast the operand except if it is an int, a bool or a float; "
	// +
	// "to create a list, instead, containing the operand (including another list), use the + operator on an empty list (like [] + 'abc').",
	// special_cases = {
	// "if the operand is a point or a pair, returns a list containing its components (two coordinates or the key and the value);",
	// "if the operand is a rgb color, returns a list containing its three integer components;",
	// "if the operand is a file, returns its contents as a list;",
	// "if the operand is a matrix, returns a list containing its elements;",
	// "if the operand is a graph, returns the list of vetices or edges (depending on the graph)",
	// "if the operand is a species, return a list of its agents;",
	// "if the operand is a string, returns a list of strings, each containing one character;",
	// "otherwise returns a list containing the operand." }, examples = { "" })
	public static IList asList(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaListType.staticCast(scope, val, null, false);
	}

	@operator(value = "list_with", content_type = ITypeProvider.SECOND_TYPE, can_be_const = false, concept = {IConcept.CASTING_OPERATOR, IConcept.CONTAINER})
	@doc(value = "creates a list with a size provided by the first operand, and filled with the second operand",
		comment = "Note that the right operand  should be positive, and that the second one is evaluated for each position  in the list.",
		see = { "list" })
	public static IList list_with(final IScope scope, final Integer size, final IExpression init) {
		return GamaListFactory.create(scope, init, size);
	}

	// @operator(value = IKeyword.MATRIX, can_be_const = true, content_type = ITypeProvider.FIRST_CONTENT_TYPE)
	// @doc(value = "casts the operand into a matrix", special_cases = {
	// "if the operand is a file, returns its content casted as a matrix",
	// "if the operand is a map, returns a 2-columns matrix with keyx in the first one and value in the second one;",
	// "if the operand is a list, returns a matrix where all sub-lists represent columns. Notice that each element of the list should be a single element or lists with the same length;",
	// "if the operand is a graph, returns nil;",
	// "otherwise, returns a 1x1 matrix with the operand at the (0,0) position." }, see = "as_matrix")
	public static IMatrix asMatrix(final IScope scope, final Object val) throws GamaRuntimeException {
		return asMatrix(scope, val, null);
	}

	@operator(value = "matrix_with",
		content_type = ITypeProvider.SECOND_TYPE,
		can_be_const = true, // AD: was true -- see Issue 1127
		category = { IOperatorCategory.CASTING },
		concept = {IConcept.CASTING_OPERATOR, IConcept.CONTAINER})
	@doc(value = "creates a matrix with a size provided by the first operand, and filled with the second operand",
		comment = "Note that both components of the right operand point should be positive, otherwise an exception is raised.",
		see = { IKeyword.MATRIX, "as_matrix" })
	public static IMatrix matrix_with(final IScope scope, final ILocation size, final IExpression init) {
		if ( size == null ) { throw GamaRuntimeException.error("A nil size is not allowed for matrices", scope); }
		IType type = init.getType();
		if ( init.isConst() ) {
			Object val = init.value(scope);
			return GamaMatrixType.with(scope, val, (GamaPoint) size, type);

		} else {
			return GamaMatrixType.with(scope, init, (GamaPoint) size);
		}
	}

	// @operator(value = IKeyword.MATRIX, can_be_const = true, content_type = ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE)
	// @doc(value = "casts the list (left operand) into a one-row matrix", examples = {
	// " as_matrix [1, 2, 3] --: [ [1, 2, 3] ] " })
	// public static IMatrix asMatrix(final IScope scope, final IList val) throws GamaRuntimeException {
	// return GamaMatrixType.from(scope, val, val.getType().getContentType(), null);
	//
	// }

	@operator(value = "as_matrix",
		content_type = ITypeProvider.FIRST_CONTENT_TYPE_OR_TYPE,
		can_be_const = true, // AD: was previously true -- see Issue 1127
		category = { IOperatorCategory.CASTING },
		concept = {IConcept.CASTING_OPERATOR, IConcept.CONTAINER})
	@doc(value = "casts the left operand into a matrix with right operand as preferrenced size",
		comment = "This operator is very useful to cast a file containing raster data into a matrix." +
			"Note that both components of the right operand point should be positive, otherwise an exception is raised." +
			"The operator as_matrix creates a matrix of preferred size. It fills in it with elements of the left operand until the matrix is full " +
			"If the size is to short, some elements will be omitted. Matrix remaining elements will be filled in by nil.",
		usages = { @usage("if the right operand is nil, as_matrix is equivalent to the matrix operator") },
		see = { IKeyword.MATRIX })
	public static IMatrix asMatrix(final IScope scope, final Object val, final ILocation size)
		throws GamaRuntimeException {
		return GamaMatrixType.staticCast(scope, val, size, Types.NO_TYPE, false);
	}

	@operator(value = { IKeyword.SPECIES, "species_of" },
		content_type = ITypeProvider.FIRST_TYPE,
		category = { IOperatorCategory.CASTING },
		concept = {IConcept.CASTING_OPERATOR})
	@doc(value = "casting of the operand to a species.",
		usages = { @usage("if the operand is nil, returns nil;"),
			@usage("if the operand is an agent, returns its species;"),
			@usage("if the operand is a string, returns the species with this name (nil if not found);"),
			@usage("otherwise, returns nil") },
		examples = {
			@example(value = "species(self)", equals = "the species of the current agent", isExecutable = false),
			@example(value = "species('node')", equals = "node", isExecutable = false),
			@example(value = "species([1,5,9,3])", equals = "nil", isExecutable = false),
			@example(value = "species(node1)", equals = "node", isExecutable = false) })
	public static ISpecies asSpecies(final IScope scope, final Object val) throws GamaRuntimeException {
		return (ISpecies) Types.SPECIES.cast(scope, val, null, false);
	}

	@operator(value = "to_gaml", category = { IOperatorCategory.CASTING })
	@doc(
		value = "returns the litteral description of an expression or description -- action, behavior, species, aspect, even model -- in gaml",
		examples = { @example(value = "to_gaml(0)", equals = "'0'"),
			@example(value = "to_gaml(3.78)", equals = "'3.78'"), @example(value = "to_gaml(true)", equals = "'true'"),
			@example(value = "to_gaml({23, 4.0})", equals = "'{23.0,4.0,0.0}'"),
			@example(value = "to_gaml(5::34)", equals = "'5::34'"),
			@example(value = "to_gaml(rgb(255,0,125))", equals = "'rgb (255, 0, 125,255)'"),
			@example(value = "to_gaml('hello')", equals = "\"'hello'\""),
			@example(value = "to_gaml([1,5,9,3])", equals = "'[1,5,9,3]'"),
			@example(value = "to_gaml(['a'::345, 'b'::13, 'c'::12])",
				equals = "\"([\'a\'::345,\'b\'::13,\'c\'::12] as map )\""),
			@example(value = "to_gaml([[3,5,7,9],[2,4,6,8]])", equals = "'[[3,5,7,9],[2,4,6,8]]'"),
			@example(value = "to_gaml(a_graph)",
				equals = "([((1 as node)::(3 as node))::(5 as edge),((0 as node)::(3 as node))::(3 as edge),((1 as node)::(2 as node))::(1 as edge),((0 as node)::(2 as node))::(2 as edge),((0 as node)::(1 as node))::(0 as edge),((2 as node)::(3 as node))::(4 as edge)] as map ) as graph",
				isExecutable = false),
			@example(value = "to_gaml(node1)", equals = " 1 as node", isExecutable = false) },
		see = { "to_java" })
	public static String toGaml(final Object val) {
		return StringUtils.toGaml(val, false);
	}

}
