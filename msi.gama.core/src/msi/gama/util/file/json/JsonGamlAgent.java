/*******************************************************************************************************
 *
 * JsonGamlAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;

/**
 * The Class JsonGamlObject.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 3 nov. 2023
 */
public class JsonGamlAgent extends JsonAbstractObject<Object> {

	/** The type. */
	final String species;

	/** The index. */
	int index;

	/**
	 * Instantiates a new json gaml object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param json
	 *            the json
	 * @date 3 nov. 2023
	 */
	public JsonGamlAgent(final String species, final int index, final Json json) {
		super(json);
		this.species = species;
		this.index = index;
	}

	/**
	 * Instantiates a new json gaml object from an existing JsonObject
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param json
	 *            the json
	 * @date 3 nov. 2023
	 */
	public JsonGamlAgent(final String species, final int index, final JsonAbstractObject<?> object, final Json json) {
		this(species, index, json);
		for (JsonObjectMember m : object) { add(m.getName(), m.getValue()); }
	}

	@Override
	public Object toGamlValue(final IScope scope) {
		IPopulation pop = scope.getAgent().getPopulationFor(species);
		return toMap(scope);
		// return pop.deserializeFromJson(toMap(scope));
	}

	@Override
	protected void writeMembers(final JsonWriter writer) throws IOException {
		writer.writeMemberName(Json.GAML_SPECIES_LABEL);
		writer.writeMemberSeparator();
		writer.writeString(species);
		writer.writeObjectSeparator();
		writer.writeMemberName(IKeyword.INDEX);
		writer.writeMemberSeparator();
		writer.writeNumber(String.valueOf(index));
		writer.writeObjectSeparator();
		super.writeMembers(writer);
	}

	@Override
	public boolean isGamlAgent() { return true; }

	@Override
	public JsonGamlAgent asGamlAgent() {
		return this;
	}

}
