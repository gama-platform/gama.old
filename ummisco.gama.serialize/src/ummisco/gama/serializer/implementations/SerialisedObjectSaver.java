/*******************************************************************************************************
 *
 * SerialisedObjectSaver.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
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

import msi.gama.runtime.GAMA;
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
public class SerialisedObjectSaver implements ISerialisationConstants {
	/** The instance. */

	static SerialisedObjectSaver INSTANCE = new SerialisedObjectSaver();

	/**
	 * Gets the single instance of SerialisedAgentReader.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return single instance of SerialisedAgentReader
	 * @date 21 août 2023
	 */
	public static SerialisedObjectSaver getInstance() { return INSTANCE; }

	static {
		DEBUG.ON();
	}

	/**
	 * Save to file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 8 août 2023
	 */
	public final void saveToFile(final IScope scope, final Object sim, final String path, final String format,
			final boolean zip) {
		try (FileOutputStream fos = new FileOutputStream(path, true)) {
			fos.write(saveToBytes(scope, sim, format, zip));
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
		}
	}

	/**
	 * Save to string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return
	 * @date 8 août 2023
	 */
	public final String saveToString(final IScope scope, final Object sim, final String format, final boolean zip) {
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
	private final byte[] saveToBytes(final IScope scope, final Object agent, final String format, final boolean zip) {
		ISerialisationProcessor processor = SerialisationProcessorFactory.create(format);
		if (processor == null) throw GamaRuntimeException.error("No object serializer called " + format
				+ " found. Available serializers are " + SerialisationProcessorFactory.getAvailableProcessors(), scope);
		byte[] toSave = processor.saveObjectToBytes(scope, agent);
		if (zip) { toSave = ByteArrayZipper.zip(toSave); }
		try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
			fos.write(GAMA_OBJECT_IDENTIFIER);
			fos.write(processor.getFormatIdentifier());
			fos.write(zip ? COMPRESSED : UNCOMPRESSED);
			fos.write(toSave);
			return fos.toByteArray();
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
		}
	}

}
