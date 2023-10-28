/*******************************************************************************************************
 *
 * IJsonable.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.interfaces;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.json.DeserializationException;
import msi.gama.util.file.json.Jsoner;

/**
 * The interface IJSonable. Represents objects that can represent themselves in terms of JSON descriptions
 * (serialization).
 *
 * @author A. Drogoul
 *
 *
 */
public interface IJsonable {

	/**
	 * Returns the serialization in JSON of this object
	 *
	 * @return a string that can be reinterpreted to reproduce the object
	 */
	default String serializeToJson() {
		return Jsoner.serialize(this);
	}

	/**
	 * Deserialize. Returns the Object represented by this string in JSON. A scope is necessary to know how to interpret
	 * it.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param gaml
	 *            the gaml
	 * @return the object
	 * @date 27 oct. 2023
	 */
	default Object deserializeFromJson(final IScope scope, final String json) {
		try {
			return Jsoner.deserialize(json);
		} catch (DeserializationException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}
}
