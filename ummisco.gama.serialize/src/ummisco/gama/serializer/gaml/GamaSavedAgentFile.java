/*******************************************************************************************************
 *
 * GamaSavedAgentFile.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaFile;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.serializer.implementations.SerialisationConstants;

/**
 * The Class GamaSavedSimulationFile.
 */
@file (
		name = IKeyword.AGENT,
		extensions = { IKeyword.AGENT },
		buffer_type = IType.LIST,
		buffer_content = IType.STRING,
		buffer_index = IType.INT,
		concept = { IConcept.FILE, IConcept.SAVE_FILE },
		doc = @doc ("Represents a saved agent file. The internal contents is a string at index 0 that contains the textual (json, xml) or binary (bytes) representation of the agent"))
// TODO : this type needs to be improved ....
@SuppressWarnings ({ "unchecked" })
public class GamaSavedAgentFile extends GamaFile<IList<String>, String> implements SerialisationConstants {

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
	@doc ("File containing a saved agent. Three internal formats are supported: json, xml and java binary serialisation protocol")
	public GamaSavedAgentFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, true);
	}

	@Override
	public IContainerType<?> getGamlType() { return Types.FILE.of(Types.INT, Types.STRING); }

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(Path.of(getPath(scope)));
			setBuffer(GamaListFactory.create(scope, Types.STRING, new String(bytes, STRING_BYTE_ARRAY_CHARSET)));
		} catch (IOException e) {
			setBuffer(GamaListFactory.create());
			throw GamaRuntimeException.create(e, scope);
		}

	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

}
