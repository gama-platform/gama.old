/*******************************************************************************************************
 *
 * SerialisedObjectReader.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SerialiasedSimulationReader. The 3 first bytes are reserved for (1) GAMA_IDENTIFIER ; (2) Format Identifier
 * ; (3) Compressed or not
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SerialisedObjectReader implements ISerialisationConstants {

	/** The instance. */
	static SerialisedObjectReader INSTANCE = new SerialisedObjectReader();

	/**
	 * Gets the single instance of SerialisedAgentReader.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return single instance of SerialisedAgentReader
	 * @date 21 août 2023
	 */
	public static SerialisedObjectReader getInstance() { return INSTANCE; }

	static {
		DEBUG.ON();
	}

	/**
	 * Instantiates a new serialised agent reader.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 21 août 2023
	 */
	private SerialisedObjectReader() {}

	/**
	 * Restore from file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 8 août 2023
	 */
	public final Object restoreFromFile(final IScope scope, final String path) {
		try {
			byte[] all = Files.readAllBytes(Path.of(path));
			return restoreFromBytes(scope, all);
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
		}
	}

	/**
	 * Restore from string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the agent
	 * @param string
	 *            the string
	 * @date 8 août 2023
	 */
	public Object restoreFromString(final IScope scope, final String string) {
		try {
			byte[] all = string.getBytes(ISerialisationConstants.STRING_BYTE_ARRAY_CHARSET);
			return restoreFromBytes(scope, all);
		} catch (Throwable e) {
			e.printStackTrace();
			// The string is maybe a path ?
			try {
				return restoreFromFile(scope, string);
			} catch (Throwable ex) {
				throw GamaRuntimeException.create(ex, scope);
			}
		}
	}

	/**
	 * Restore from bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @param bytes
	 *            the bytes
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 8 août 2023
	 */
	private Object restoreFromBytes(final IScope scope, final byte[] bytes) {
		if (bytes[0] != GAMA_OBJECT_IDENTIFIER)
			throw GamaRuntimeException.error("Not an object serialisation record", scope);
		ISerialisationProcessor processor = SerialisationProcessorFactory.create(bytes[1]);
		boolean zip = bytes[2] == COMPRESSED;
		byte[] some = Arrays.copyOfRange(bytes, 3, bytes.length);
		if (zip) { some = ByteArrayZipper.unzip(some); }
		return processor.restoreObjectFromBytes(scope, some);
	}

}
