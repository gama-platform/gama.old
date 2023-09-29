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

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.implementations.SerialisationConstants;
import ummisco.gama.serializer.implementations.SerialisedAgentSaver;

/**
 * The Class ReverseOperators.
 */
public class ReverseOperators {

	static {
		DEBUG.OFF();
	}

	/**
	 * Serialize.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 */
	@operator (
			value = "serialize_to_xml")
	@doc (
			value = "Serializes any item into a string, using the 'xml' format",
			see = "deserialize",
			deprecated = "this type of serialization should be avoided unless xml is really required")
	@no_test ()
	public static String serializeToXml(final IScope scope, final Object o) {
		return StreamConverter.convertObjectToStream(scope, o);
	}

	/**
	 * Serialize.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 */
	@operator (
			value = "serialize")
	@doc (
			value = "Serializes any item into a string, using the default 'binary' format",
			see = "deserialize")
	@no_test ()
	public static String serialize(final IScope scope, final Object o) {
		return StreamConverter.convertObjectToStream(scope, o);
	}

	/**
	 * Serialize agents and simulations.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 * @date 9 août 2023
	 */
	@operator (
			value = "serialize")
	@doc (
			value = "Serializes any agent/simulation into a string, using the default 'binary' format. The result is not compressed."
					+ "The result of this operator can be then used in the `from:` facet of `restore` or `create` statements",
			see = "")
	@no_test ()
	public static String serialize(final IScope scope, final IAgent agent) {
		return serialize(scope, agent, "binary");
	}

	/**
	 * Serialize agents and simulation with a format
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 * @date 9 août 2023
	 */
	@operator (
			value = "serialize")
	@doc (
			value = "Serializes any agent/simulation into a string, using the format passed in parameter (either 'binary', 'xml' or 'json'). The result is not compressed."
					+ "The result of this operator can be then used in the `from:` facet of `restore` or `create` statements",
			see = "")
	@no_test ()
	public static String serialize(final IScope scope, final IAgent agent, final String format) {
		return serialize(scope, agent, format, false);
	}

	/**
	 * Serialize agents and simulation with a format, compressed or not
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 * @date 9 août 2023
	 */
	@operator (
			value = "serialize")
	@doc (
			value = "Serializes any agent/simulation into a string, using the format passed in parameter (either 'binary', 'xml' or 'json'). The result is compressed if the last parameter is true."
					+ "The result of this operator can be then used in the `from:` facet of `restore` or `create` statements",
			see = "")
	@no_test ()
	public static String serialize(final IScope scope, final IAgent agent, final String format,
			final boolean compress) {
		SerialisedAgentSaver sas = SerialisedAgentSaver.getInstance(format);
		sas.compress(compress);
		return sas.saveToString(agent);
	}

	/**
	 * Unserialize.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @return the object
	 */
	@operator (
			value = "deserialize_from_xml")
	@doc (
			value = "Deserializes items precedently serialized into the 'xml' format. Should not be used to deserialize agents nor simulations (use the 'restore' or 'create' statements instead)",
			deprecated = "Still in alpha version. Should not be used unless xml is required")
	public static Object unserializeFromXml(final IScope scope, final String s) {
		if (s == null || s.isBlank()) return null;
		byte[] b = s.getBytes();
		if (b[0] == SerialisationConstants.GAMA_IDENTIFIER)
			throw GamaRuntimeException.error("Use `restore` or `create` to deserialize agents and simulations", scope);
		return StreamConverter.convertStreamToObject(scope, s);
	}

	/**
	 * Unserialize.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @return the object
	 */
	@operator (
			value = "deserialize")
	@doc (
			value = "Deserializes items precedently serialized into the 'binary' format. Should not be used to deserialize agents nor simulations (use the 'restore' or 'create' statements instead)",
			deprecated = "Still in alpha version.")
	public static Object unserialize(final IScope scope, final String s) {
		if (s == null || s.isBlank()) return null;
		byte[] b = s.getBytes();
		if (b[0] == SerialisationConstants.GAMA_IDENTIFIER)
			throw GamaRuntimeException.error("Use `restore` or `create` to deserialize agents and simulations", scope);
		return StreamConverter.convertStreamToObject(scope, s);
	}
}