/*******************************************************************************************************
 *
 * SerialisedAgentSaver.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SerialisedSimulationSaver. The 3 first bytes are reserved for (1) GAMA_IDENTIFIER ; (2) Format Identifier ;
 * (3) Compressed or not
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SerialisedAgentSaver implements ISerialisationConstants {
	/** The instance. */

	/**
	 * Gets the single instance of SerialisedAgentSaver.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param format
	 *            the format
	 * @return single instance of SerialisedAgentSaver
	 * @date 30 sept. 2023
	 */
	static SerialisedAgentSaver INSTANCE = new SerialisedAgentSaver();

	/**
	 * Gets the single instance of SerialisedAgentReader.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return single instance of SerialisedAgentReader
	 * @date 21 août 2023
	 */
	public static SerialisedAgentSaver getInstance() { return INSTANCE; }

	static {
		DEBUG.ON();
	}

	/**
	 * Save to file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 8 août 2023
	 */
	public final void saveToFile(final IScope scope, final IAgent sim, final String path, final String format,
			final boolean zip) {
		try (FileOutputStream fos = new FileOutputStream(path, true)) {
			fos.write(saveToBytes(scope, sim, format, zip));
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Save to file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 8 août 2023
	 */
	public final void saveToFile(final IScope scope, final SimulationAgent sim, final String path,
			final boolean withHistory, final String format, final boolean zip) {
		try (FileOutputStream fos = new FileOutputStream(path, true)) {
			sim.serializeHistory(withHistory);
			fos.write(saveToBytes(scope, sim, format, zip));
			sim.serializeHistory(false);
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Save to string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return
	 * @date 8 août 2023
	 */
	public final String saveToString(final IScope scope, final IAgent sim, final String format, final boolean zip) {
		return new String(saveToBytes(scope, sim, format, zip), STRING_BYTE_ARRAY_CHARSET);
	}

	/**
	 * Save to bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the sim
	 * @return the string
	 * @date 21 août 2023
	 */
	public final byte[] saveToBytes(final IScope scope, final IAgent agent, final String format, final boolean zip) {
		if (agent == null) return NULL;
		ISerialisationProcessor processor = SerialisationProcessorFactory.create(format);
		if (processor == null) throw GamaRuntimeException.error("No agent serializer called " + format
				+ " found. Available serializers are " + SerialisationProcessorFactory.getAvailableProcessors(),
				agent.getScope());
		byte[] toSave = processor.saveAgentToBytes(scope, agent);
		if (zip) { toSave = ByteArrayZipper.zip(toSave); }
		try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
			fos.write(GAMA_AGENT_IDENTIFIER);
			fos.write(processor.getFormatIdentifier());
			fos.write(zip ? COMPRESSED : UNCOMPRESSED);
			fos.write(toSave);
			return fos.toByteArray();
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

}
