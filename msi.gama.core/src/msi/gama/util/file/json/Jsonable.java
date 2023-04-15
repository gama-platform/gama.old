/*******************************************************************************************************
 *
 * Jsonable.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file.json;

/**
 * Jsonables can be serialized in java script object notation (JSON). Deserializing a String produced by a Jsonable
 * should represent the Jsonable in JSON form.
 *
 * @author A. Drogoul. Simplification and adaptation from json-simple
 *
 */
public interface Jsonable {
	/**
	 * Serialize to a JSON formatted string.
	 *
	 * @return a string, formatted in JSON, that represents the Jsonable.
	 */
	String toJson();

}
