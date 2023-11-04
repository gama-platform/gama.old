/*******************************************************************************************************
 *
 * JsonObject.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import msi.gama.runtime.IScope;
import msi.gama.util.IMap;

/**
 * Represents a JSON object, a set of name/value pairs, where the names are strings and the values are JSON values.
 * <p>
 * Members can be added using the <code>add(String, ...)</code> methods which accept instances of {@link JsonValue},
 * strings, primitive numbers, and boolean values. To modify certain values of an object, use the
 * <code>set(String, ...)</code> methods. Please note that the <code>add</code> methods are faster than <code>set</code>
 * as they do not search for existing members. On the other hand, the <code>add</code> methods do not prevent adding
 * multiple members with the same name. Duplicate names are discouraged but not prohibited by JSON.
 * </p>
 * <p>
 * Members can be accessed by their name using {@link #get(String)}. A list of all names can be obtained from the method
 * {@link #names()}. This class also supports iterating over the members in document order using an {@link #iterator()}
 * or an enhanced for loop:
 * </p>
 *
 * <pre>
 * for (Member member : jsonObject) {
 *   String name = member.getName();
 *   JsonValue value = member.getValue();
 *   ...
 * }
 * </pre>
 * <p>
 * Even though JSON objects are unordered by definition, instances of this class preserve the order of members to allow
 * processing in document order and to guarantee a predictable output.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads access a <code>JsonObject</code>
 * instance concurrently, while at least one of these threads modifies the contents of this object, access to the
 * instance must be synchronized externally. Failure to do so may lead to an inconsistent state.
 * </p>
 *
 */
@SuppressWarnings ("serial") // use default serial UID
public class JsonObject extends JsonAbstractObject {

	/**
	 * Creates a new empty JsonObject.
	 */
	JsonObject(final Json json) {
		super(json);
	}

	@Override
	public IMap<String, Object> toGamlValue(final IScope scope) {
		return toMap(scope);
	}

	@Override
	public JsonObject asObject() {
		return this;
	}

	@Override
	public boolean isObject() { return true; }

}
