/*******************************************************************************************************
 *
 * BinarySerialisation.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import msi.gama.common.interfaces.ISerialisationConstants;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SerialisedAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class BinarySerialisationReader.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 31 oct. 2023
 */
public class BinarySerialisation2 implements ISerialisationConstants {

	/**
	 * Creates an object or an agent from a file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param path
	 *            the path
	 * @return the object
	 * @date 31 oct. 2023
	 */
	public static Object createFromFile(final IScope scope, final String path) {
		try {
			byte[] all = Files.readAllBytes(Path.of(FileUtils.constructAbsoluteFilePath(scope, path, true)));
			return createFromBytes(scope, all);
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, GAMA.getRuntimeScope());
		}
	}

	/**
	 * Creates the from string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param string
	 *            the string
	 * @return the object
	 * @date 31 oct. 2023
	 */
	public static Object createFromString(final IScope scope, final String string) {
		if (string == null || string.isBlank()) return null;
		try {
			byte[] all = string.getBytes(ISerialisationConstants.STRING_BYTE_ARRAY_CHARSET);
			return createFromBytes(scope, all);
		} catch (Throwable e) {
			e.printStackTrace();
			try {
				return createFromFile(scope, string);
			} catch (Throwable ex) {
				throw GamaRuntimeException.create(ex, scope);
			}
		}
	}

	/**
	 * Creates the from bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param all
	 *            the all
	 * @return the object
	 * @date 31 oct. 2023
	 */
	public static Object createFromBytes(final IScope scope, final byte[] bytes) {
		// byte type = bytes[0];
		// if (type != GAMA_OBJECT_IDENTIFIER && type != GAMA_AGENT_IDENTIFIER)
		// throw GamaRuntimeException.error("Not a GAMA serialisation record", scope);
		// ISerialisationProcessor processor = FSTBinaryProcessor.INSTANCE;
		// boolean zip = bytes[2] == COMPRESSED;
		byte[] some = bytes; // ByteArrayZipper.unzip(bytes);
		// Arrays.copyOfRange(bytes, 3, bytes.length);
		// if (zip) { some = ByteArrayZipper.unzip(some); }
		// return type == GAMA_OBJECT_IDENTIFIER ?

		return new FSTBinaryProcessor().createObjectFromBytes(scope, some);
		// : processor.createAgentFromBytes(scope, some);
	}

	/**
	 * Restore from file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 8 août 2023
	 */
	public static void restoreFromFile(final IAgent agent, final String path) {
		try {
			byte[] all = Files.readAllBytes(Path.of(path));
			restoreFromBytes(agent, all);
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, agent.getScope());
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
	public static void restoreFromString(final IAgent agent, final String string) {
		try {
			byte[] all = string.getBytes(ISerialisationConstants.STRING_BYTE_ARRAY_CHARSET);
			restoreFromBytes(agent, all);
		} catch (Throwable e) {
			e.printStackTrace();
			// The string is maybe a path ?
			try {
				restoreFromFile(agent, string);
			} catch (Throwable ex) {
				throw GamaRuntimeException.create(ex, agent.getScope());
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
	public static void restoreFromBytes(final IAgent sim, final byte[] bytes) throws IOException {
		// if (bytes[0] != GAMA_AGENT_IDENTIFIER) throw new IOException("Not an agent serialisation record");
		// ISerialisationProcessor processor = FSTBinaryProcessor.INSTANCE;
		// boolean zip = bytes[2] == COMPRESSED;
		byte[] some = bytes; // ByteArrayZipper.unzip(bytes);
		// Arrays.copyOfRange(bytes, 3, bytes.length);
		// if (zip) { some = ByteArrayZipper.unzip(some); }
		new FSTBinaryProcessor().restoreAgentFromBytes(sim, some);
	}

	/**
	 * Save to file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope of the current simulation
	 * @param o
	 *            the object to serialise
	 * @param path
	 *            the path of the file to which to save the serialisation
	 * @param format
	 *            the format of the serialisation ("json" or "binary")
	 * @param zip
	 *            whether to zip the result or not
	 * @param includingHistory
	 *            whether to include the "history" of the agent in the serialisation. Only applicables to simulations
	 * @date 31 oct. 2023
	 */
	public static final void saveToFile(final IScope scope, final Object o, final String path, final String format,
			final boolean zip, final boolean includingHistory) {
		try (FileOutputStream fos = new FileOutputStream(path, true)) {
			if (o instanceof SimulationAgent sim) {
				sim.setAttribute(SerialisedAgent.SERIALISE_HISTORY, includingHistory);
			}
			fos.write(saveToBytes(scope, o, zip));
			if (o instanceof SimulationAgent sim) { sim.setAttribute(SerialisedAgent.SERIALISE_HISTORY, false); }
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
	public static final String saveToString(final IScope scope, final Object sim, final String format,
			final boolean zip) {
		return new String(saveToBytes(scope, sim, zip), STRING_BYTE_ARRAY_CHARSET);
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
	public static final byte[] saveToBytes(final IScope scope, final Object agent, final boolean zip) {
		FSTBinaryProcessor processor = new FSTBinaryProcessor();
		// if (processor == null) throw GamaRuntimeException.error("No object serializer called " + format
		// + " found. Available serializers are " + SerialisationProcessorFactory.getAvailableProcessors(), scope);
		byte[] toSave = processor.saveObjectToBytes(scope, agent);
		// if (zip) {
		// toSave = ByteArrayZipper.zip(toSave);

		// }
		try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
			// fos.write(agent instanceof IAgent ? GAMA_AGENT_IDENTIFIER : GAMA_OBJECT_IDENTIFIER);
			// fos.write(processor.getFormatIdentifier());
			// fos.write(zip ? COMPRESSED : UNCOMPRESSED);
			fos.write(toSave);
			return fos.toByteArray();
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	// /**
	// * Save to bytes.
	// *
	// * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	// * @param scope
	// * the scope
	// * @param object
	// * the agent
	// * @param zip
	// * the zip
	// * @return the byte[]
	// * @date 29 déc. 2023
	// */
	// public static final byte[] saveToBytes(final IScope scope, final Object object, final boolean zip) {
	// return saveToBytes(scope, object, zip);
	// }

	/**
	 * Save to bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @param zip
	 *            the zip
	 * @return the byte[]
	 * @date 29 déc. 2023
	 */
	public static final byte[] saveToBytes(final Object object, final boolean zip) {
		return saveToBytes(null, object, zip);
	}

}
