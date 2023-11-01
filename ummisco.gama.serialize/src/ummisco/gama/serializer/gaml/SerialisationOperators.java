/*******************************************************************************************************
 *
 * SerialisationOperators.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gaml;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.WriterConfig;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.save.GeoJSonSaver;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.implementations.BinarySerialisation;

/**
 * The Class ReverseOperators.
 */
public class SerialisationOperators {

	static {
		DEBUG.OFF();
	}

	/**
	 * As json string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param container
	 *            the container
	 * @return the string
	 * @date 20 août 2023
	 */
	@operator (
			value = { "as_json_string" },
			can_be_const = true,
			category = { IOperatorCategory.CONTAINER },
			concept = { IConcept.SERIALIZE })
	@doc (
			deprecated = "Use to_json() instead",
			value = "Tries to convert the container into a json-formatted string",
			usages = { @usage (
					value = "With a map:",
					examples = { @example (
							value = "as_json_string(map('int_value'::1, 'string_value'::'some words', 'tab'::[1, 2, 3]))",
							returnType = IKeyword.STRING,
							equals = "{\"int_value\":1,\"string_value\":\"some words\",\"tab\":[1,2,3]}") }),
					@usage (
							value = "With an array:",
							examples = { @example (
									value = "as_json_string([1, 2, 3, 'some words'])",
									returnType = IKeyword.STRING,
									equals = "[1,2,3,\"some words\"]") }) })
	@Deprecated
	public static String asJsonString(final IScope scope, final IContainer container) {
		return Json.getNew().valueOf(container).toString();
	}

	/**
	 * To gaml.
	 *
	 * @param val
	 *            the val
	 * @return the string
	 */
	@operator (
			value = "to_gaml",
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.SERIALIZE })
	@doc (
			value = "Returns the literal description of an expression in gaml, in a format suitable to be reinterpreted and return a similar object",
			examples = { @example (
					value = "to_gaml(0)",
					equals = "'0'"),
					@example (
							value = "to_gaml(3.78)",
							equals = "'3.78'"),
					@example (
							value = "to_gaml({23, 4.0})",
							equals = "'{23.0,4.0,0.0}'"),
					@example (
							value = "to_gaml(rgb(255,0,125))",
							equals = "'rgb (255, 0, 125,255)'"),
					@example (
							value = "to_gaml('hello')",
							equals = "\"'hello'\""),
					@example (
							value = "to_gaml(a_graph)",
							equals = "([((1 as node)::(3 as node))::(5 as edge),((0 as node)::(3 as node))::(3 as edge),((1 as node)::(2 as node))::(1 as edge),((0 as node)::(2 as node))::(2 as edge),((0 as node)::(1 as node))::(0 as edge),((2 as node)::(3 as node))::(4 as edge)] as map ) as graph",
							isExecutable = false),
					@example (
							value = "to_gaml(node1)",
							equals = " 1 as node",
							isExecutable = false) },
			see = {})
	@test ("to_gaml(true) = 'true'")
	@test ("to_gaml(5::34) = '5::34'")
	@test ("to_gaml([1,5,9,3]) = '[1,5,9,3]'")
	@test ("to_gaml(['a'::345, 'b'::13, 'c'::12]) = \"map([\'a\'::345,\'b\'::13,\'c\'::12])\"")
	@test ("to_gaml([[3,5,7,9],[2,4,6,8]]) = '[[3,5,7,9],[2,4,6,8]]'")
	public static String toGaml(final Object val) {
		return StringUtils.toGaml(val, false);
	}

	/**
	 * To geojson.
	 *
	 * @param val
	 *            the val
	 * @return the string
	 */
	@operator (
			value = "to_geojson",
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.SERIALIZE })
	@doc (
			value = "Returns a geojson representation of a population, a list of agents/geometries or an agent/geometry, provided with a CRS and a list of attributes to save",
			examples = { @example (
					value = "to_geojson(boat,\"EPSG:4326\",[\"color\"])",
					equals = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[100.51155642068785,3.514781609095577E-4,0.0]},\"properties\":{},\"id\":\"0\"}]}") },
			see = {})
	@no_test
	public static String toGeoJSon(final IScope scope, final IExpression spec, final String epsgCode,
			final IExpression attributesFacet) {

		final GeoJSonSaver gjsoner = new GeoJSonSaver();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gjsoner.save(scope, spec, baos, epsgCode, attributesFacet);
			return baos.toString(StandardCharsets.UTF_8);

		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * To json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the string
	 * @date 31 oct. 2023
	 */
	@operator (
			value = { "to_json" },
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.SERIALIZE })
	@doc (
			value = "Serializes any object/agent/simulation into a string, using the json format. A flag can be passed to enable/disable pretty printing (false by default)."
					+ "The format used by GAMA follows simple rules. int, float, bool, string values are outputted as they are. nil is outputted as 'null'. A list is outputted as a json array. Any other object or agent is outputted as a json object. If this object possesses the \"gaml_type\" attribute, "
					+ "it is an instance of the corresponding type, and the members that follow contain the attributes and the values necessary to reconstruct it. If it has the \"agent_reference\" attribute, its value represent the reference to an agent. If any reference to an agent is found, the "
					+ "json string returned will be an object with two attributes: \"gama_object\", the object containing the references, and \"reference_table\" a dictionary mapping the references to the json description of the agents (their species, name, index, and list of attributes). "
					+ "This choice allows to manage cross references between agents",
			see = { "serialize", "to_gaml" })
	public static String toJson(final IScope scope, final Object obj, final boolean pretty) {
		return Json.getNew().valueOf(obj).toString(pretty ? WriterConfig.PRETTY_PRINT : WriterConfig.MINIMAL);
	}

	/**
	 * To json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the string
	 * @date 31 oct. 2023
	 */
	@operator (
			value = { "to_json" },
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.SERIALIZE })
	@doc (
			value = "Serializes any object/agent/simulation into a string, using the json format and no pretty printing."
					+ "The format used by GAMA follows simple rules. int, float, bool, string values are outputted as they are. nil is outputted as 'null'. A list is outputted as a json array. Any other object or agent is outputted as a json object. If this object possesses the \"gaml_type\" attribute, "
					+ "it is an instance of the corresponding type, and the members that follow contain the attributes and the values necessary to reconstruct it. If it has the \"agent_reference\" attribute, its value represent the reference to an agent. If any reference to an agent is found, the "
					+ "json string returned will be an object with two attributes: \"gama_object\", the object containing the references, and \"reference_table\" a dictionary mapping the references to the json description of the agents (their species, name, index, and list of attributes). "
					+ "This choice allows to manage cross references between agents",
			see = { "serialize", "to_gaml" })
	public static String toJson(final IScope scope, final Object obj) {
		return toJson(scope, obj, false);
	}

	/**
	 * Serialize.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the string
	 * @date 28 oct. 2023
	 */
	@operator (
			value = { "serialize", "to_binary" },
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.SERIALIZE })
	@doc (
			value = "Serializes any object/agent/simulation into a string, using the 'binary' format"
					+ "The result of this operator can be then used in the `from:` facet of `restore` or `create` statements in case of agents, or using `deserialize` for other items",
			see = { "to_json", "to_gaml" })
	@no_test ()
	public static String serialize(final IScope scope, final Object obj) {
		return BinarySerialisation.saveToString(scope, obj, "binary", true);
	}

	/**
	 * Unserialize.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return the object
	 * @date 29 sept. 2023
	 */
	@operator (
			value = { "deserialize", "from_binary" },
			type = ITypeProvider.DENOTED_TYPE_AT_INDEX + 2,
			category = { IOperatorCategory.CASTING },
			concept = { IConcept.SERIALIZE })
	@doc (
			value = "Deserializes an object precedently serialized using `serialize`. The second argument represents the type expected."
					+ "It is safer to deserialize agents or simulations with the 'restore' or 'create' statements rather than with this operator.",
			see = { "from_gaml", "from_json" })
	@no_test
	public static Object unserialize(final IScope scope, final String s) {
		return BinarySerialisation.createFromString(scope, s);
	}
}