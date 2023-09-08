/*******************************************************************************************************
 *
 * GamaSavedSimulationFile.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gaml;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import ummisco.gama.serializer.implementations.SerialisationConstants;

/**
 * The Class GamaSavedSimulationFile.
 */
@file (
		name = IKeyword.SIMULATION,
		extensions = { "gsim", IKeyword.SIMULATION },
		buffer_type = IType.LIST,
		buffer_content = IType.STRING,
		buffer_index = IType.INT,
		concept = { IConcept.FILE, IConcept.SAVE_FILE },
		doc = @doc ("Represents a saved simulation file. The internal contents is a string at index 0 that contains the textual (json, xml) or binary (bytes) representation of the simulation"))
// TODO : this type needs to be improved ....
@SuppressWarnings ({ "unchecked" })
public class GamaSavedSimulationFile extends GamaSavedAgentFile implements SerialisationConstants {

	/**
	 * Instantiates a new gama saved simulation file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc ("File containing 	a saved simulation. Three internal formats are supported: json, xml and java binary serialisation protocol")
	public GamaSavedSimulationFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

}
