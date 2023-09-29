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
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.implementations.SerialisedAgentSaver;
import ummisco.gama.serializer.implementations.SerialisedObjectReader;
import ummisco.gama.serializer.implementations.SerialisedObjectSaver;

/**
 * The Class ReverseOperators.
 */
public class ReverseOperators {

	static {
		DEBUG.OFF();
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
			value = "Serializes any object/agent/simulation into a string, using the default 'binary' format. The result is not compressed."
					+ "The result of this operator can be then used in the `from:` facet of `restore` or `create` statements in case of agents, or using `deserialize` for other items",
			see = "")
	@no_test ()
	public static String serialize(final IScope scope, final Object agent) {
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
			value = "Serializes any object/agent/simulation into a string, using the format passed in parameter (either 'binary', 'xml' or 'json'). The result is not compressed."
					+ "The result of this operator can be then used in the `from:` facet of `restore` or `create` statements in case of agents, or using `deserialize` for other items",
			see = "")
	@no_test ()
	public static String serialize(final IScope scope, final Object agent, final String format) {
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
			value = "Serializes any object/agent/simulation into a string, using the format passed in parameter (either 'binary', 'xml' or 'json'). The result is compressed if the last parameter is true."
					+ "The result of this operator can be then used in the `from:` facet of `restore` or `create` statements in case of agents, or using `deserialize` for other items",
			see = "")
	@no_test ()
	public static String serialize(final IScope scope, final Object obj, final String format, final boolean compress) {
		if (obj instanceof IAgent agent) {
			SerialisedAgentSaver sas = SerialisedAgentSaver.getInstance(format);
			sas.compress(compress);
			return sas.saveToString(agent);
		}
		SerialisedObjectSaver sas = SerialisedObjectSaver.getInstance(format);
		sas.compress(compress);
		return sas.saveToString(scope, obj);
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
			value = "Deserializes an object precedently serialized using `serialize`. "
					+ "Should not be used to deserialize agents nor simulations (use the 'restore' or 'create' statements instead)")
	@no_test
	public static Object unserialize(final IScope scope, final String s) {
		return unserialize(scope, s, Types.NO_TYPE);
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
			value = "deserialize",
			type = ITypeProvider.DENOTED_TYPE_AT_INDEX + 2)
	@doc (
			value = "Deserializes an object precedently serialized using `serialize`. The second argument represents the type expected."
					+ "Should not be used to deserialize agents nor simulations (use the 'restore' or 'create' statements instead)")
	@no_test
	public static Object unserialize(final IScope scope, final String s, final IType t) {
		if (s == null || s.isBlank()) return null;
		if (t.isAgentType())
			throw GamaRuntimeException.error("Use `restore` or `create` to deserialize agents and simulations", scope);
		SerialisedObjectReader reader = SerialisedObjectReader.getInstance();
		return t.cast(scope, reader.restoreFromString(scope, s), t.getDefault(), false);
	}
}