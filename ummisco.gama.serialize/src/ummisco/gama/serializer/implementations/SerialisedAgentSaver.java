/*******************************************************************************************************
 *
 * SerialisedAgentSaver.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
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
import java.util.Map;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SerialisedSimulationSaver. The 3 first bytes are reserved for (1) GAMA_IDENTIFIER ; (2) Format Identifier ;
 * (3) Compressed or not
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SerialisedAgentSaver extends SerialisedAgentManipulator {
	/** The instance. */

	static Map<String, SerialisedAgentSaver> INSTANCES = Map.of(JSON_FORMAT,
			new SerialisedAgentSaver(JSON_FORMAT, false), XML_FORMAT, new SerialisedAgentSaver(XML_FORMAT, false),
			BINARY_FORMAT, new SerialisedAgentSaver(BINARY_FORMAT, true));

	/**
	 * Gets the single instance of SerialisedAgentReader.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return single instance of SerialisedAgentReader
	 * @date 21 août 2023
	 */
	public static SerialisedAgentSaver getInstance(final String format) {
		return INSTANCES.get(format);
	}

	static {
		DEBUG.ON();
	}

	/**
	 * Compress.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the serialised agent saver
	 * @date 24 août 2023
	 */
	public SerialisedAgentSaver compress(final boolean doIt) {
		zip = doIt;
		return this;
	}

	/**
	 * Instantiates a new serialised simulation saver.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param format
	 *            the format
	 * @param zip
	 *            the zip
	 * @date 8 août 2023
	 */
	private SerialisedAgentSaver(final String format, final boolean zip) {
		super(format, zip);
		if (!zip) { processor.prettyPrint(); }
	}

	/**
	 * Save to file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 8 août 2023
	 */
	public final void saveToFile(final IAgent sim, final String path) {
		try (FileOutputStream fos = new FileOutputStream(path, true)) {
			fos.write(saveToBytes(sim));
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, sim.getScope());
		}
	}

	/**
	 * Save to file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 8 août 2023
	 */
	public final void saveToFile(final SimulationAgent sim, final String path, final boolean withHistory) {
		try (FileOutputStream fos = new FileOutputStream(path, true)) {
			sim.serializeHistory(withHistory);
			fos.write(saveToBytes(sim));
			sim.serializeHistory(false);
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, sim.getScope());
		}
	}

	/**
	 * Save to string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return
	 * @date 8 août 2023
	 */
	public final String saveToString(final IAgent sim) {
		return new String(saveToBytes(sim), STRING_BYTE_ARRAY_CHARSET);
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
	public final byte[] saveToBytes(final IAgent agent) {
		byte[] toSave = processor.saveAgentToBytes(agent);
		if (zip) { toSave = zip(toSave); }
		try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
			fos.write(GAMA_IDENTIFIER);
			fos.write(processor.getFormatIdentifier());
			fos.write(zip ? COMPRESSED : UNCOMPRESSED);
			fos.write(toSave);
			return fos.toByteArray();
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, agent.getScope());
		}
	}

}
