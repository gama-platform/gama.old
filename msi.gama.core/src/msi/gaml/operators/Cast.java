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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
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

	@operator(value = { IKeyword.IS }, priority = IPriority.COMPARATOR)
	public static Boolean isA(final IScope scope, final IExpression a, final IExpression b)
		throws GamaRuntimeException {
		// TODO Verify this method. And see if the treatment of species and types cannot be
		// unified�
		IType type = asType(scope, b);
		if ( a == null ) { return type == Types.NO_TYPE; }
		if ( type.isSpeciesType() ) {
			ISpecies s = scope.getSimulationScope().getModel().getSpecies(type.getSpeciesName());
			Object v = a.value(scope);
			if ( v instanceof IAgent ) { return ((IAgent) v).isInstanceOf(s, false); }
			return false;
		}
		return type.isAssignableFrom(a.getType());
	}

	//
	// public static String toJava(final Object val) {
	// if ( val == null ) { return "null"; }
	// if ( val instanceof IValue ) { return ((IValue) val).toJava(); }
	// if ( val instanceof String ) { return StringUtils.toJavaString((String) val); }
	// return val.toString();
	// }

	public static IType asType(final IScope scope, final IExpression expr)
		throws GamaRuntimeException {
		Object value = expr.value(scope);
		if ( value instanceof String ) {
			IModel m = scope.getSimulationScope().getModel();
			return m.getDescription().getTypeNamed((String) value);
		} else if ( value instanceof ISpecies ) {
			return ((ISpecies) value).getAgentType();
		} else {
			return expr.getType();
		}
	}

	@operator(value = IType.CONTAINER_STR, content_type = ITypeProvider.CHILD_CONTENT_TYPE, priority = IPriority.CAST)
	@doc(value = "casting of the operand to a container", special_cases = {
		"if the operand is a container, returns itself",
		"otherwise, returns the operand casted to a list" }, see = "list")
	public static IContainer asContainer(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaContainerType.staticCast(scope, val, null);
	}

	@operator(value = IType.GRAPH_STR, content_type = ITypeProvider.CHILD_CONTENT_TYPE, priority = IPriority.CAST)
	@doc(value = "casting of the operand to a graph.", special_cases = {
		"if the operand is a graph, returns the graph itself",
		"if the operand is a list, returns a new graph with the elements of the left-hand operand as vertices and no edge. "
			+ "The graph will be spatial is the right-hand operand is true;",
		"if the operand is a map, ", "otherwise, returns nil" }, examples = {
		"graph([1,5,9,3]) 	--: ([1: in[] + out[], 3: in[] + out[], 5: in[] + out[], 9: in[] + out[]], [])",
		"graph(['a'::345, 'b'::13])  --:  ([b: in[] + out[b::13], a: in[] + out[a::345], 13: in[b::13] + out[], 345: in[a::345] + out[]], [a::345=(a,345), b::13=(b,13)])",
		"graph(a_graph)	--: a_graph", "graph(node1)	--: null" })
	public static IGraph asGraph(final IScope scope, final Object val) {
		return GamaGraphType.staticCast(scope, val, null);
	}

	@operator(value = IType.TOPOLOGY_STR, content_type = IType.GEOMETRY, priority = IPriority.CAST)
	@doc(value = "casting of the operand to a topology.", special_cases = {
		"if the operand is a topology, returns the topology itself;",
		"if the operand is a spatial graph, returns the graph topology associated;",
		"if the operand is a population, returns the topology of the population;",
		"if the operand is a shape or a geometry, returns the continuous topology bounded by the geometry;",
		"if the operand is a matrix, returns the grid topology associated",
		"if the operand is another kind of container, returns the multiple topology associated to the container",
		"otherwise, casts the operand to a geometry and build a topology from it." }, examples = {
		"topology(0) 		--: null",
		"topology(a_graph)	--: Multiple topology in POLYGON ((24.712119771887785 7.867357373616512, 24.712119771887785 61.283226839310565, 82.4013676510046  7.867357373616512)) "
			+ "at location[53.556743711446195;34.57529210646354]" }, see = { "geometry" })
	public static ITopology asTopology(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaTopologyType.staticCast(scope, val, null);
	}

	@operator(value = IType.AGENT_STR)
	@doc(value = "casting of the operand to an agent (if a species name is used, casting to an instance of species name).", special_cases = {
		"if the operand is a point, returns the closest agent (resp. closest instance of species name) to that point (computed in the topology of the calling agent);",
		"if the operand is an agent, returns the agent (resp. tries to cast this agent to species name and returns nil if the agent is instance of another species);",
		"if the operand is an int, returns the agent (resp. instance of species name) with this unique index;" }, examples = {
		"species node {}", "node(0) 			--: node0", "node(3.78) 		--: null",
		"node(true) 		--: null", "node({23, 4.0} 	--: node2", "node(5::34) 		--: null",
		"node(green) 		--: null", "node([1,5,9,3]) 	--: null", "node(node1)		--: node1",
		"node('4')			--: null" }, see = { "of_species", "species" })
	public static IAgent asAgent(final IScope scope, final Object val) throws GamaRuntimeException {
		return (IAgent) Types.get(IType.AGENT).cast(scope, val, null);
	}

	@operator(value = "as", type = ITypeProvider.RIGHT_CONTENT_TYPE, content_type = ITypeProvider.RIGHT_CONTENT_TYPE, priority = IPriority.CAST)
	@doc(value = "", special_cases = {
		"if the right-hand operand is nil, transforms the left-hand operand into an agentE", "" }, see = { "agent" })
	public static IAgent asAgent(final IScope scope, final Object val, final ISpecies species)
		throws GamaRuntimeException {
		if ( val == null ) { return null; }
		if ( species == null ) { return asAgent(scope, val); }
		if ( val instanceof IAgent ) {
			// if ( ((IAgent) val).dead() ) { return null; }
			return ((IAgent) val).isInstanceOf(species, false) ? (IAgent) val : null;
		}
		// if ( val instanceof String ) { return species.getAgent((String) val); }
		if ( val instanceof Integer ) { return scope.getAgentScope().getPopulationFor(species)
			.getAgent((Integer) val); }
		if ( val instanceof ILocation ) { return scope.getAgentScope().getPopulationFor(species)
			.getAgent((GamaPoint) val); }
		return null;
	}

	@operator(value = IType.BOOL_STR, can_be_const = true)
	@doc(value = "casting of the operand to a boolean value.", special_cases = {
		"if the operand is null, returns false;",
		"if the operand is an agent, returns true if the agent is not dead;",
		"if the operand is an int or a float, returns true if it is not equal to 0 (or 0.0);",
		"if the operand is a file, bool is formally equivalent to exists;",
		"if the operand is a container, bool is formally equivalent to not empty (a la Lisp);",
		"if the operand is a string, returns true is the operand is true;",
		"Otherwise, returns false." }, examples = { "bool(3.78) 		--: true",
		"bool(true) 		--: true", "bool({23, 4.0} 	--: false", "bool(5::34) 		--: false",
		"bool(green) 		--: false", "bool([1,5,9,3]) 	--: true", "bool(node1)		--: true",
		"bool('4')			--: false", "bool('4.7')		--: false " })
	public static Boolean asBool(final IScope scope, final Object val) {
		return GamaBoolType.staticCast(scope, val, null);
	}

	@operator(value = IType.COLOR_STR, can_be_const = true)
	@doc(value = "casting of the operand to a rgb color.", special_cases = {
		"if the operand is nil, returns white;",
		"if the operand is a string, the allowed color names are the constants defined in the java.awt.Color class, "
			+ "i.e.: black, blue, cyan, darkGray, lightGray, gray, green, magenta, orange, pink, red, white, yellow. "
			+ "Otherwise tries to cast the string to an int and returns this color",
		"if the operand is a list, the integer value associated to the three first elements of the list are used to define the three red "
			+ "(element 0 of the list), green (element 1 of the list) and blue (element 2 of the list) components of the color;",
		"if the operand is a map, the red, green, blue components take the value associated to the keys \"r\", \"g\", \"b\" in the map;",
		"if the operand is a matrix, return the color of the matrix casted as a list;",
		"if the operand is a boolean, returns black for true and white for false;",
		"if the operand is an integer value, the decimal integer is translated into a hexadecimal value: OxRRGGBB. "
			+ "The red (resp. green, blue) component of the color take the value RR (resp. GG, BB) translated in decimal." }, examples = {
		"rgb(3.78) 			--: rgb([0,0,3])", "rgb(true) 			--: rgb([0,0,0]) //black ",
		"rgb({23, 4.0} 		--: rgb([0,0,0]) //black ", "rgb(5::34) 		--: rgb([0,0,0]) //black ",
		"rgb(green) 		--: rgb([0,255,0]) //green ", "rgb([1,5,9,3]) 	--: rgb([1,5,9])",
		"rgb(node1)			--: rgb([0,0,1])", "rgb('4')			--: rgb([0,0,4])",
		"rgb('4.7')			--:  // Exception " })
	public static GamaColor asColor(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaColorType.staticCast(scope, val, null);
	}

	@operator(value = IType.FLOAT_STR, can_be_const = true)
	@doc(value = "casting of the operand to a floating point value.", special_cases = {
		"if the operand is numerical value, returns its value as a floating point value;",
		"if the operand is a string, tries to convert its content to a floating point value;",
		"if the operand is a boolean, returns 1.0 for true and 0.0 for false;",
		"otherwise, returns 0.0" }, examples = { "float(7) 				--: 7.0", "float(true) 			--: 1.0",
		"float({23, 4.0} 		--: 0.0", "float(5::34) 			--: 0.0", "float(green) 			--: 0.0",
		"float([1,5,9,3]) 		--: 0.0", "float(node1)			--: 0.0", "int('4')				--: 4.0",
		"int('4.7')				--: 4.7 " }, see = { "int" })
	public static Double asFloat(final IScope scope, final Object val) {
		return GamaFloatType.staticCast(scope, val, null);
	}

	@operator(IType.GEOM_STR)
	public static IShape asGeometry(final IScope scope, final Object s) throws GamaRuntimeException {
		return GamaGeometryType.staticCast(scope, s, null);
	}

	@operator(value = IType.INT_STR, can_be_const = true)
	@doc(value = "casting of the operand to an integer value.", special_cases = {
		"if the operand is a float, returns its value truncated (but not rounded);",
		"if the operand is an agent, returns its unique index;",
		"if the operand is a string, tries to convert its content to an integer value;",
		"if the operand is a boolean, returns 1 for true and 0 for false;",
		"if the operand is a color, returns its RGB value as an integer;", "otherwise, returns 0" }, examples = {
		"int(3.78) 			--: 3", "int(true) 			--: 1", "int({23, 4.0} 		--: 0", "int(5::34) 		--: 0",
		"int(green) 		--: -16711936", "int([1,5,9,3]) 	--: 0", "int(node1)			--: 1",
		"int('4')			--: 4", "int('4.7')			--:  // Exception " }, see = { "round", "float" })
	public static Integer asInt(final IScope scope, final Object val) {
		return GamaIntegerType.staticCast(scope, val, null);
	}

	@operator(value = "as_int", can_be_const = true)
	public static Integer asInt(final IScope scope, final String string, final Integer radix)
		throws GamaRuntimeException {
		if ( string == null || string.isEmpty() ) { return 0; }
		return GamaIntegerType.staticCast(scope, string, radix);
	}

	@operator(value = IType.LIST_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	@doc(value = "transforms the operand into a list", comment = "list always tries to cast the operand except if it is an int, a bool or a float; "
		+ "to create a list, instead, containing the operand (including another list), use the + operator on an empty list (like [] + 'abc').", special_cases = {
		"if the operand is a point or a pair, returns a list containing its components (two coordinates or the key and the value);",
		"if the operand is a rgb color, returns a list containing its three integer components;",
		"if the operand is a file, returns its contents as a list;",
		"if the operand is a matrix, returns a list containing its elements;",
		"if the operand is a graph, returns the list of vetices or edges (depending on the graph)",
		"if the operand is a species, return a list of its agents;",
		"if the operand is a string, returns a list of strings, each containing one character;",
		"otherwise returns a list containing the operand." }, examples = { "" })
	public static IList asList(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaListType.staticCast(scope, val, null);
	}

	@operator(value = IType.MAP_STR, can_be_const = true)
	public static GamaMap asMap(final IScope scope, final Object val) throws GamaRuntimeException {
		return (GamaMap) Types.get(IType.MAP).cast(scope, val, null);
	}

	@operator(value = IType.MATRIX_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static IMatrix asMatrix(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return asMatrix(scope, val, null);
	}

	@operator(value = IType.MATRIX_STR, can_be_const = true, content_type = ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE)
	public static IMatrix asMatrix(final IScope scope, final IList val) throws GamaRuntimeException {
		return asMatrix(scope, val, null);
	}

	@operator(value = "as_matrix", content_type = ITypeProvider.LEFT_CONTENT_TYPE, can_be_const = true)
	public static IMatrix asMatrix(final IScope scope, final Object val, final ILocation size)
		throws GamaRuntimeException {
		return (IMatrix) Types.get(IType.MATRIX).cast(scope, val, size);
	}

	@operator(value = IType.NONE_STR, can_be_const = true)
	public static Object asObject(final Object obj) {
		return obj;
	}

	@operator(value = IType.PAIR_STR, can_be_const = true)
	public static GamaPair asPair(final IScope scope, final Object val) throws GamaRuntimeException {
		return (GamaPair) Types.get(IType.PAIR).cast(scope, val, null);
	}

	@operator(value = IType.POINT_STR, can_be_const = true)
	public static ILocation asPoint(final IScope scope, final Object val) {
		return GamaPointType.staticCast(scope, val, null);
	}

	@operator(value = { IType.SPECIES_STR, "species_of" }, content_type = ITypeProvider.CHILD_TYPE)
	public static ISpecies asSpecies(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return (ISpecies) Types.get(IType.SPECIES).cast(scope, val, null);
	}

	@operator(value = IType.STRING_STR, can_be_const = true)
	public static String asString(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaStringType.staticCast(val, null);
	}

	@operator(value = "to_gaml")
	public static String toGaml(final Object val) {
		return StringUtils.toGaml(val);
	}

	@operator(value = "to_java")
	public static String toJava(final Object val) throws GamaRuntimeException {
		throw new GamaRuntimeException("to_java is not yet implemented")/* Cast.toJava(val) */;
	}

}
