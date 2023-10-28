/*******************************************************************************************************
 *
 * ConverterJSON.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.inject;

import msi.gama.runtime.IScope;
import msi.gama.util.serialize.ILastResortJSonConverter;
import ummisco.gama.serializer.implementations.ISerialisationConstants;
import ummisco.gama.serializer.implementations.SerialisedObjectSaver;

/**
 * The Class ConverterJSON.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 28 oct. 2023
 */
public class ConverterJSON implements ILastResortJSonConverter {

	@Override
	public String toJSon(final IScope scope, final Object o) {

		return SerialisedObjectSaver.getInstance().saveToString(scope, o, ISerialisationConstants.JSON_FORMAT, false);
		// return StreamConverter.convertObjectToJSONStream(scope,o);
	}
}
