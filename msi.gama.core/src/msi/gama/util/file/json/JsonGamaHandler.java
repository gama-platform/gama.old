/*******************************************************************************************************
 *
 * JsonGamaHandler.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import msi.gama.common.interfaces.IKeyword;
import msi.gaml.types.Types;

/**
 * The Class DefaultHandler.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 29 oct. 2023
 */
class JsonGamaHandler extends JsonHandler<JsonArray, JsonObject> implements IJsonConstants {

	/**
	 * Instantiates a new default handler.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param json
	 *            the json
	 * @date 1 nov. 2023
	 */
	JsonGamaHandler(final Json json) {
		this.json = json;
	}

	/** The json. */
	protected Json json;

	/** The value. */
	protected JsonValue value;

	@Override
	public JsonArray startArray() {
		return new JsonArray(json);
	}

	@Override
	public JsonObject startObject() {
		return new JsonObject(json);
	}

	@Override
	public void endNull() {
		value = IJsonConstants.NULL;
	}

	@Override
	public void endBoolean(final boolean bool) {
		value = bool ? IJsonConstants.TRUE : IJsonConstants.FALSE;
	}

	@Override
	public void endString(final String string) {
		value = new JsonString(string);
	}

	@Override
	public void endNumber(final String string, final boolean isFloat) {
		value = isFloat ? new JsonFloat(string) : new JsonInt(string);
	}

	@Override
	public void endArray(final JsonArray array) {
		value = array;
	}

	@Override
	public void endObject(final JsonObject object) {
		if (object.contains(GAML_SPECIES_LABEL)) {
			value = new JsonGamlAgent(object.remove(GAML_SPECIES_LABEL).asString(),
					object.remove(IKeyword.INDEX).asInt(), object, json);
		} else if (object.contains(AGENT_REFERENCE_LABEL)) {
			value = new JsonReferenceObject(object.get(AGENT_REFERENCE_LABEL).asString(), json);
		} else if (object.contains(CONTENTS_WITH_REFERENCES_LABEL)) {
			value = new JsonGamaContentsObject(object.get(CONTENTS_WITH_REFERENCES_LABEL),
					object.get(REFERENCE_TABLE_LABEL).asObject(), json);
		} else {
			value = object;
		}
	}

	/**
	 * End gaml object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @date 4 nov. 2023
	 */
	@Override
	public void endGamlObject(final String type, final JsonObject object) {
		if (Types.GEOMETRY.getName().equals(type)) {
			value = new JsonGeometryObject(object, json);
		} else {
			value = new JsonGamlObject(type, object, json);
		}
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