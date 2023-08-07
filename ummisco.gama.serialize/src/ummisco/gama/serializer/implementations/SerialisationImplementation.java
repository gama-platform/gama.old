/*******************************************************************************************************
 *
 * SerialisationImplementation.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SerialisationImplementation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 7 août 2023
 */
public abstract class SerialisationImplementation<SerialisedForm> {

	static {
		DEBUG.ON();
	}

	/** The scope. */
	IScope scope;

	/** The Constant zip. */
	boolean zip = true;

	/** The Constant UNKNOWN. */
	final static Long UNKNOWN = -1L;

	/** The Constant NULL. */
	static final byte[] NULL = {};

	/** The size. */
	protected long timeToWrite, timeToRead, timeToEncode, timeToRestore, size, compressedSize;

	/** The binary history tree. */
	protected final GamaTree<byte[]> history = new GamaTree<>();

	/** The binary history node. */
	protected GamaNode<byte[]> current;

	/** The save to file. */
	boolean saveToFile;

	/**
	 * Instantiates a new serialisation implementation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param zip
	 *            the Constant zip.
	 * @param save
	 *            the save
	 * @date 7 août 2023
	 */
	public SerialisationImplementation(final boolean zip, final boolean save) {
		this.zip = zip;
		this.saveToFile = save;
	}

	/**
	 * Save.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 6 août 2023
	 */
	public final void save(final SimulationAgent sim) {
		try {
			scope = sim.getScope();
			long startTime = System.nanoTime();
			timeToWrite = UNKNOWN;
			timeToEncode = UNKNOWN;
			size = 0;
			byte[] state = NULL;
			SerialisedForm objectToSerialise = encodeToSerialisedForm(sim);
			timeToEncode = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
			startTime = System.nanoTime();
			state = write(objectToSerialise);
			compressedSize = size = state.length;
			if (zip) {
				state = zip(state);
				compressedSize = state.length;
			}
			timeToWrite = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
			if (current == null) {
				current = history.setRoot(state);
			} else {
				current = current.addChild(state);
			}
			DEBUG.OUT("Serialise in " + (timeToWrite + timeToEncode) + "ms [Encode in " + timeToEncode
					+ "ms and write in " + timeToWrite + "ms]; Size: " + size / 1000000d + "Mb "
					+ (zip ? "[Compressed: " + compressedSize / 1000000d + "Mb]" : ""));
			if (saveToFile) {
				File file = new File(sim.getName() + "_" + sim.getCycle(sim.getScope()) + ".gsim");
				try (FileOutputStream fos = new FileOutputStream(file); FileChannel channel = fos.getChannel();) {
					ByteBuffer bb = ByteBuffer.wrap(state);
					channel.write(bb);
				} catch (IOException e) {
					throw GamaRuntimeException.create(e, sim.getScope());
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			scope = null;
		}
	}

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 6 août 2023
	 */
	public final void restore(final SimulationAgent sim) {
		scope = sim.getScope();
		current = current.getParent();
		try {
			if (current != null) {
				long startTime = System.nanoTime();
				timeToRead = UNKNOWN;
				timeToRestore = UNKNOWN;
				byte[] input = current.getData();
				if (zip) { input = unzip(input); }
				SerialisedForm previousSim = read(input);
				timeToRead = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
				startTime = System.nanoTime();
				restoreFromSerialisedForm(sim, previousSim);
				timeToRestore = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
				DEBUG.OUT("Deserialise in: " + (timeToRead + timeToRestore) + "ms [Read in " + timeToRead
						+ "ms and restore in " + timeToRestore + "ms]");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			scope = null;
		}

	}

	/**
	 * Convert to proxy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @return the proxy
	 * @date 7 août 2023
	 */
	protected abstract SerialisedForm encodeToSerialisedForm(SimulationAgent object);

	/**
	 * Convert from proxy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @param proxy
	 *            the proxy
	 * @date 7 août 2023
	 */
	protected abstract void restoreFromSerialisedForm(SimulationAgent object, SerialisedForm proxy);

	/**
	 * Write.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param objectToSerialise
	 *            the object to serialise
	 * @return the byte[]
	 * @date 7 août 2023
	 */
	protected abstract byte[] write(SerialisedForm objectToSerialise);

	/**
	 * Read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param input
	 *            the input
	 * @return the proxy
	 * @date 7 août 2023
	 */
	protected abstract SerialisedForm read(byte[] input);

	/**
	 * Zip bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param filename
	 *            the filename
	 * @param input
	 *            the input
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 7 août 2023
	 */
	private byte[] zip(final byte[] input) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPOutputStream zos = new GZIPOutputStream(baos);) {
			zos.write(input);
			zos.finish();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return NULL;
		}
	}

	/**
	 * Unzip.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param input
	 *            the input
	 * @return the byte[]
	 * @date 7 août 2023
	 */
	private byte[] unzip(final byte[] input) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(input);
				GZIPInputStream zos = new GZIPInputStream(bais);) {
			return zos.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return NULL;
		}
	}

}