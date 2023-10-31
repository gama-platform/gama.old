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

import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.JsonValue;

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
	 * Returns the serialization in JSON of this object. The context of serialization is passed through "json".
	 *
	 * @return a string that can be reinterpreted to reproduce the object
	 */
	JsonValue serializeToJson(Json json);

}
