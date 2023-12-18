/*******************************************************************************************************
 *
 * JsonGamlObject.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;

import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

/**
 * The Class JsonGamlObject.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 3 nov. 2023
 */
public class JsonGamlObject extends JsonAbstractObject {

	/** The type. */
	final String type;

	/**
	 * Instantiates a new json gaml object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param json
	 *            the json
	 * @date 3 nov. 2023
	 */
	public JsonGamlObject(final String type, final Json json) {
		super(json);
		this.type = type;
	}

	/**
	 * Instantiates a new json gaml object from an existing JsonObject
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param json
	 *            the json
	 * @date 3 nov. 2023
	 */
	public JsonGamlObject(final String type, final JsonAbstractObject object, final Json json) {
		this(type, json);
		for (JsonObjectMember m : object) { add(m.getName(), m.getValue()); }
	}

	@Override
	public Object toGamlValue(final IScope scope) {
		IType<?> gamlType = scope.getTypes().decodeType(type);
		return gamlType.deserializeFromJson(scope, toMap(scope));

	}

	@Override
	protected void writeMembers(final JsonWriter writer) throws IOException {
		writer.writeMemberName(IJsonConstants.GAML_TYPE_LABEL);
		writer.writeMemberSeparator();
		writer.writeString(type);
		writer.writeObjectSeparator();
		super.writeMembers(writer);
	}

	@Override
	public boolean isGamlObject() { return true; }

	@Override
	public JsonGamlObject asGamlObject() {
		return this;
	}

}
