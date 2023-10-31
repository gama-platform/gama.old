/*******************************************************************************************************
 *
 * ReverseOperators.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gaml;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.WriterConfig;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.implementations.BinarySerialisation;

/**
 * The Class ReverseOperators.
 */
public class ReverseOperators {

	static {
		DEBUG.OFF();
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
			value = { "to_json" })
	@doc (
			value = "Serializes any object/agent/simulation into a string, using the 'json' format. A flag can be passed to enable/disable pretty printing (true by default)",
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
			value = { "to_json" })
	@doc (
			value = "Serializes any object/agent/simulation into a string, using the 'json' format",
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
			value = { "serialize", "to_binary" })
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
			type = ITypeProvider.DENOTED_TYPE_AT_INDEX + 2)
	@doc (
			value = "Deserializes an object precedently serialized using `serialize`. The second argument represents the type expected."
					+ "It is safer to deserialize agents or simulations with the 'restore' or 'create' statements rather than with this operator.",
			see = { "from_gaml", "from_json" })
	@no_test
	public static Object unserialize(final IScope scope, final String s) {
		return BinarySerialisation.createFromString(scope, s);
	}
}