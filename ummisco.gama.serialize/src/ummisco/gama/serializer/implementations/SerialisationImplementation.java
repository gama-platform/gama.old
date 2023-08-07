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
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;

/**
 * The Class SerialisationImplementation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 7 août 2023
 */
public abstract class SerialisationImplementation {

	/** The binary history tree. */
	protected final GamaTree<byte[]> history = new GamaTree<>();

	/** The binary history node. */
	protected GamaNode<byte[]> current;

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 6 août 2023
	 */
	public abstract void restore(final SimulationAgent sim);

	/**
	 * Save.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 6 août 2023
	 */
	public abstract void save(final SimulationAgent sim);

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
	protected static byte[] zip(final byte[] input) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPOutputStream zos = new GZIPOutputStream(baos);) {
			zos.write(input);
			zos.finish();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
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
	protected static byte[] unzip(final byte[] input) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(input);
				GZIPInputStream zos = new GZIPInputStream(bais);) {
			return zos.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

}