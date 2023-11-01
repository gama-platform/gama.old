/*******************************************************************************************************
 *
 * SerialisationProcessorFactory.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A factory for creating SerialisationImplementation objects.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SerialisationProcessorFactory implements ISerialisationConstants {

	/** The processors. */
	static Map<Byte, ISerialisationProcessor> PROCESSORS_BY_BYTE = new HashMap<>();

	/** The processors by format. */
	static Map<String, ISerialisationProcessor> PROCESSORS_BY_FORMAT = new HashMap<>();

	static {
		register(new FSTBinaryProcessor());
	}

	/**
	 * Register.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param processor
	 *            the processor
	 * @date 8 août 2023
	 */
	public static void register(final ISerialisationProcessor processor) {
		PROCESSORS_BY_BYTE.put(processor.getFormatIdentifier(), processor);
		PROCESSORS_BY_FORMAT.put(processor.getFormat(), processor);
	}

	/**
	 * Creates from a byte identifier
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param b
	 *            the b
	 * @return the abstract serialisation implementation
	 * @date 8 août 2023
	 */
	public static ISerialisationProcessor create(final byte b) {
		ISerialisationProcessor result = PROCESSORS_BY_BYTE.get(b);
		if (result == null) { result = PROCESSORS_BY_BYTE.get(0); }
		return result;
	}

	/**
	 * Creates from a byte identifier
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param b
	 *            the b
	 * @return the abstract serialisation implementation
	 * @date 8 août 2023
	 */
	public static ISerialisationProcessor create(final String b) {
		ISerialisationProcessor result = PROCESSORS_BY_FORMAT.get(b);
		if (result == null) { result = PROCESSORS_BY_FORMAT.get(BINARY_FORMAT); }
		return result;
	}

	/**
	 * Gets the available processors.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the available processors
	 * @date 30 sept. 2023
	 */
	public static Set<String> getAvailableProcessors() { return PROCESSORS_BY_FORMAT.keySet(); }

}
