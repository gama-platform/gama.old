/*********************************************************************************************
 *
 * 'Cast.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.operators;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.GamaColorType;
import msi.gaml.types.GamaFloatType;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.GamaGraphType;
import msi.gaml.types.GamaIntegerType;
import msi.gaml.types.GamaListType;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.GamaPairType;
import msi.gaml.types.GamaPointType;
import msi.gaml.types.GamaStringType;
import msi.gaml.types.GamaTopologyType;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 15 d�c. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "rawtypes" })
public class Cast {

	@operator (
			value = { IKeyword.IS },
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST, IConcept.TYPE })
	@doc (
			value = "returns true if the left operand is of the right operand type, false otherwise",
			examples = { @example (
					value = "0 is int",
					equals = "true"),
					@example (
							value = "an_agent is node",
							equals = "true",
							isExecutable = false),
					@example (
							value = "1 is float",
							equals = "false") })
	public static Boolean isA(final IScope scope, final Object a, final IExpression b) throws GamaRuntimeException {
		final IType<?> type = asType(scope, b);
		if (type.isAgentType()) {
			final ISpecies s = scope.getSimulation().getModel().getSpecies(type.getSpeciesName());
			if (a instanceof IAgent) { return ((IAgent) a).isInstanceOf(s, false); }
			return false;
		}
		return type.isAssignableFrom(GamaType.of(a));
	}

	@operator (
			value = IKeyword.IS_SKILL,
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST, IConcept.SKILL })
	@doc (
			value = "returns true if the left operand is an agent whose species implements the right-hand skill name",
			examples = { @example (
					value = "agentA is_skill 'moving'",
					equals = "true",
					isExecutable = false) })
	public static Boolean isSkill(final IScope scope, final Object a, final String skill) {
		if (!(a instanceof IAgent)) { return false; }
		final ISpecies s = ((IAgent) a).getSpecies();
		return s.implementsSkill(skill);
	}

	public static IType asType(final IScope scope, final IExpression expr) throws GamaRuntimeException {
		final Object value = expr.value(scope);
		if (value instanceof String) {
			final IModel m = scope.getSimulation().getModel();
			return m.getDescription().getTypeNamed((String) value);
		} else if (value instanceof ISpecies) {
			return ((ISpecies) value).getDescription().getType();
		} else {
			return expr.getType();
		}
	}

	public static IGraph asGraph(final IScope scope, final Object val) {
		return GamaGraphType.staticCast(scope, val, null, false);
	}

	@operator (
			value = IKeyword.TOPOLOGY,
			content_type = IType.GEOMETRY,
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST, IConcept.TOPOLOGY })
	@doc (
			value = "casting of the operand to a topology.",
			usages = { @usage ("if the operand is a topology, returns the topology itself;"),
					@usage ("if the operand is a spatial graph, returns the graph topology associated;"),
					@usage ("if the operand is a population, returns the topology of the population;"),
					@usage ("if the operand is a shape or a geometry, returns the continuous topology bounded by the geometry;"),
					@usage ("if the operand is a matrix, returns the grid topology associated"),
					@usage ("if the operand is another kind of container, returns the multiple topology associated to the container"),
					@usage ("otherwise, casts the operand to a geometry and build a topology from it.") },
			examples = { @example (
					value = "topology(0)",
					equals = "nil",
					isExecutable = true),
					@example (
							value = "topology(a_graph)	--: Multiple topology in POLYGON ((24.712119771887785 7.867357373616512, 24.712119771887785 61.283226839310565, 82.4013676510046  7.867357373616512)) "
									+ "at location[53.556743711446195;34.57529210646354]",
							isExecutable = false) },
			see = { "geometry" })
	public static ITopology asTopology(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaTopologyType.staticCast(scope, val, false);
	}

	public static IAgent asAgent(final IScope scope, final Object val) throws GamaRuntimeException {
		return (IAgent) Types.AGENT.cast(scope, val, null, false);
	}

	@operator (
			value = IKeyword.AS,
			type = ITypeProvider.SECOND_DENOTED_TYPE,
			// content_type = ITypeProvider.SECOND_CONTENT_TYPE,
			// index_type = ITypeProvider.SECOND_KEY_TYPE,
			can_be_const = true,
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST })
	@doc (
			value = "casting of the first argument into a given type",
			comment = "It is equivalent to the application of the type operator on the left operand.",
			examples = @example (
					value = "3.5 as int",
					returnType = "int",
					equals = "int(3.5)"))
	public static Object as(final IScope scope, final Object val, final IType type) {
		// WARNING copy is set explicity to false
		return type.cast(scope, val, null, false);
	}

	public static Boolean asBool(final IScope scope, final Object val, final boolean copy) {
		// copy not passed
		return GamaBoolType.staticCast(scope, val, null, false);
	}

	public static Boolean asBool(final IScope scope, final Object val) {
		// copy not passed
		return asBool(scope, val, false);
	}

	public static GamaColor asColor(final IScope scope, final Object val, final boolean copy)
			throws GamaRuntimeException {
		// copy not passed
		return GamaColorType.staticCast(scope, val, null, false);
	}

	public static GamaColor asColor(final IScope scope, final Object val) {
		return asColor(scope, val, false);
	}

	public static Double asFloat(final IScope scope, final Object val) {
		return GamaFloatType.staticCast(scope, val, null, false);
	}

	public static IShape asGeometry(final IScope scope, final Object s, final boolean copy)
			throws GamaRuntimeException {
		return GamaGeometryType.staticCast(scope, s, null, copy);
	}

	public static IShape asGeometry(final IScope scope, final Object s) throws GamaRuntimeException {
		return asGeometry(scope, s, false);
	}

	public static Integer asInt(final IScope scope, final Object val) {
		return GamaIntegerType.staticCast(scope, val, null, false);
	}

	public static GamaPair asPair(final IScope scope, final Object val, final boolean copy)
			throws GamaRuntimeException {
		return GamaPairType.staticCast(scope, val, Types.NO_TYPE, Types.NO_TYPE, copy);
	}

	public static String asString(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaStringType.staticCast(scope, val, false);
	}

	public static ILocation asPoint(final IScope scope, final Object val, final boolean copy) {
		return GamaPointType.staticCast(scope, val, copy);
	}

	public static ILocation asPoint(final IScope scope, final Object val) {
		return asPoint(scope, val, false);
	}

	public static GamaMap asMap(final IScope scope, final Object val, final boolean copy) throws GamaRuntimeException {
		return (GamaMap) Types.MAP.cast(scope, val, null, copy);
	}

	@operator (
			value = "as_int",
			can_be_const = true,
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST })
	@doc (
			value = "parses the string argument as a signed integer in the radix specified by the second argument.",
			usages = { @usage ("if the left operand is nil or empty, as_int returns 0"),
					@usage ("if the left operand does not represent an integer in the specified radix, as_int throws an exception ") },
			examples = { @example (
					value = "'20' as_int 10",
					equals = "20"),
					@example (
							value = "'20' as_int 8",
							equals = "16"),
					@example (
							value = "'20' as_int 16",
							equals = "32"),
					@example (
							value = "'1F' as_int 16",
							equals = "31"),
					@example (
							value = "'hello' as_int 32",
							equals = "18306744") },
			see = { "int" })
	public static Integer asInt(final IScope scope, final String string, final Integer radix)
			throws GamaRuntimeException {
		if (string == null || string.isEmpty()) { return 0; }
		return GamaIntegerType.staticCast(scope, string, radix, false);
	}

	public static IList asList(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaListType.staticCast(scope, val, null, false);
	}

	@operator (
			value = "list_with",
			content_type = ITypeProvider.SECOND_TYPE,
			can_be_const = false,
			concept = { IConcept.CAST, IConcept.CONTAINER })
	@doc (
			value = "creates a list with a size provided by the first operand, and filled with the second operand",
			comment = "Note that the right operand  should be positive, and that the second one is evaluated for each position  in the list.",
			see = { "list" })
	public static IList list_with(final IScope scope, final Integer size, final IExpression init) {
		return GamaListFactory.create(scope, init, size);
	}

	// @operator(value = IKeyword.MATRIX, can_be_const = true, content_type =
	// ITypeProvider.FIRST_CONTENT_TYPE)
	// @doc(value = "casts the operand into a matrix", special_cases = {
	// "if the operand is a file, returns its content casted as a matrix",
	// "if the operand is a map, returns a 2-columns matrix with keyx in the
	// first one and value in the second one;",
	// "if the operand is a list, returns a matrix where all sub-lists represent
	// columns. Notice that each element of the list should be a single element
	// or lists with the same length;",
	// "if the operand is a graph, returns nil;",
	// "otherwise, returns a 1x1 matrix with the operand at the (0,0) position."
	// }, see = "as_matrix")
	public static IMatrix asMatrix(final IScope scope, final Object val) throws GamaRuntimeException {
		return asMatrix(scope, val, null);
	}

	@operator (
			value = "matrix_with",
			content_type = ITypeProvider.SECOND_TYPE,
			can_be_const = true,
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST, IConcept.CONTAINER })
	@doc (
			value = "creates a matrix with a size provided by the first operand, and filled with the second operand",
			comment = "Note that both components of the right operand point should be positive, otherwise an exception is raised.",
			see = { IKeyword.MATRIX, "as_matrix" })
	public static IMatrix matrix_with(final IScope scope, final ILocation size, final IExpression init) {
		if (size == null) { throw GamaRuntimeException.error("A nil size is not allowed for matrices", scope); }
		// final IType type = init.getType();
		// if ( init.isConst() ) {
		// Object val = init.value(scope);
		// return GamaMatrixType.with(scope, val, (GamaPoint) size, type);

		// } else {
		return GamaMatrixType.with(scope, init, (GamaPoint) size);
		// }
	}

	// @operator(value = IKeyword.MATRIX, can_be_const = true, content_type =
	// ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE)
	// @doc(value = "casts the list (left operand) into a one-row matrix",
	// examples = {
	// " as_matrix [1, 2, 3] --: [ [1, 2, 3] ] " })
	// public static IMatrix asMatrix(final IScope scope, final IList val)
	// throws GamaRuntimeException {
	// return GamaMatrixType.from(scope, val, val.getType().getContentType(),
	// null);
	//
	// }

	@operator (
			value = "as_matrix",
			content_type = ITypeProvider.FIRST_CONTENT_TYPE_OR_TYPE,
			can_be_const = true, // AD:
									// was
									// previously
									// true
									// --
									// see
									// Issue
									// 1127
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST, IConcept.CONTAINER })
	@doc (
			value = "casts the left operand into a matrix with right operand as preferred size",
			comment = "This operator is very useful to cast a file containing raster data into a matrix."
					+ "Note that both components of the right operand point should be positive, otherwise an exception is raised."
					+ "The operator as_matrix creates a matrix of preferred size. It fills in it with elements of the left operand until the matrix is full "
					+ "If the size is to short, some elements will be omitted. Matrix remaining elements will be filled in by nil.",
			usages = { @usage ("if the right operand is nil, as_matrix is equivalent to the matrix operator") },
			see = { IKeyword.MATRIX })
	public static IMatrix asMatrix(final IScope scope, final Object val, final ILocation size)
			throws GamaRuntimeException {
		return GamaMatrixType.staticCast(scope, val, size, Types.NO_TYPE, false);
	}

	@operator (
			value = { IKeyword.SPECIES, "species_of" },
			content_type = ITypeProvider.FIRST_TYPE,
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST, IConcept.SPECIES })
	@doc (
			value = "casting of the operand to a species.",
			usages = { @usage ("if the operand is nil, returns nil;"),
					@usage ("if the operand is an agent, returns its species;"),
					@usage ("if the operand is a string, returns the species with this name (nil if not found);"),
					@usage ("otherwise, returns nil") },
			examples = { @example (
					value = "species(self)",
					equals = "the species of the current agent",
					isExecutable = false),
					@example (
							value = "species('node')",
							equals = "node",
							isExecutable = false),
					@example (
							value = "species([1,5,9,3])",
							equals = "nil",
							isExecutable = false),
					@example (
							value = "species(node1)",
							equals = "node",
							isExecutable = false) })
	public static ISpecies asSpecies(final IScope scope, final Object val) throws GamaRuntimeException {
		return (ISpecies) Types.SPECIES.cast(scope, val, null, false);
	}

	@operator (
			value = "to_gaml",
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.CAST })
	@doc (
			value = "returns the literal description of an expression or description -- action, behavior, species, aspect, even model -- in gaml",
			examples = { @example (
					value = "to_gaml(0)",
					equals = "'0'"),
					@example (
							value = "to_gaml(3.78)",
							equals = "'3.78'"),
					@example (
							value = "to_gaml(true)",
							equals = "'true'"),
					@example (
							value = "to_gaml({23, 4.0})",
							equals = "'{23.0,4.0,0.0}'"),
					@example (
							value = "to_gaml(5::34)",
							equals = "'5::34'"),
					@example (
							value = "to_gaml(rgb(255,0,125))",
							equals = "'rgb (255, 0, 125,255)'"),
					@example (
							value = "to_gaml('hello')",
							equals = "\"'hello'\""),
					@example (
							value = "to_gaml([1,5,9,3])",
							equals = "'[1,5,9,3]'"),
					@example (
							value = "to_gaml(['a'::345, 'b'::13, 'c'::12])",
							equals = "\"([\'a\'::345,\'b\'::13,\'c\'::12] as map )\""),
					@example (
							value = "to_gaml([[3,5,7,9],[2,4,6,8]])",
							equals = "'[[3,5,7,9],[2,4,6,8]]'"),
					@example (
							value = "to_gaml(a_graph)",
							equals = "([((1 as node)::(3 as node))::(5 as edge),((0 as node)::(3 as node))::(3 as edge),((1 as node)::(2 as node))::(1 as edge),((0 as node)::(2 as node))::(2 as edge),((0 as node)::(1 as node))::(0 as edge),((2 as node)::(3 as node))::(4 as edge)] as map ) as graph",
							isExecutable = false),
					@example (
							value = "to_gaml(node1)",
							equals = " 1 as node",
							isExecutable = false) },
			see = {})
	public static String toGaml(final Object val) {
		return StringUtils.toGaml(val, false);
	}

}
