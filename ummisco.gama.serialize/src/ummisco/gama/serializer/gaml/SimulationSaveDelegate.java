/*******************************************************************************************************
 *
 * SimulationSaveDelegate.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gaml;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;

import msi.gama.common.interfaces.ISaveDelegate;
import msi.gama.common.interfaces.ISerialisationConstants;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.serializer.implementations.BinarySerialisation;

/**
 * The Class SimulationSaveDelegate.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SimulationSaveDelegate implements ISaveDelegate, ISerialisationConstants {

	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String type, final Object attributesToSave) throws IOException {
		Object toSave = item.value(scope);
		if (toSave instanceof IAgent sa) {
			BinarySerialisation.saveToFile(scope, sa, file.getPath(), type, true, true);
		}
	}

	@Override
	public Set<String> getFileTypes() { return Sets.union(FILE_FORMATS, FILE_TYPES); }

	@Override
	public IType getDataType() { return Types.AGENT; }

	@Override
	public boolean handlesDataType(final IType request) {
		return request.isAgentType();
	}

}
