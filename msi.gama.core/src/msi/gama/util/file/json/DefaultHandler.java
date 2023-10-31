/*******************************************************************************************************
 *
 * DefaultHandler.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

/**
 * The Class DefaultHandler.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 29 oct. 2023
 */
class DefaultHandler extends JsonHandler<JsonArray, JsonObject> {

	/** The value. */
	protected JsonValue value;

	@Override
	public JsonArray startArray() {
		return new JsonArray(Json.getInstance());
	}

	@Override
	public JsonObject startObject() {
		return new JsonObject(Json.getInstance());
	}

	@Override
	public void endNull() {
		value = Json.NULL;
	}

	@Override
	public void endBoolean(final boolean bool) {
		value = bool ? Json.TRUE : Json.FALSE;
	}

	@Override
	public void endString(final String string) {
		value = new JsonString(string);
	}

	@Override
	public void endNumber(final String string) {
		value = new JsonNumber(string);
	}

	@Override
	public void endArray(final JsonArray array) {
		value = array;
	}

	@Override
	public void endObject(final JsonObject object) {
		value = object;
	}

	@Override
	public void endArrayValue(final JsonArray array) {
		array.add(value);
	}

	@Override
	public void endObjectValue(final JsonObject object, final String name) {
		object.add(name, value);
	}

	/**
	 * Gets the value.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the value
	 * @date 29 oct. 2023
	 */
	JsonValue getValue() { return value; }

}