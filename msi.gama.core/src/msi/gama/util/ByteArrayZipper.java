/*******************************************************************************************************
 *
 * ByteArrayZipper.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The Class SerialisedSimulationManipulator.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public abstract class ByteArrayZipper {

	/** The null. */
	static byte[] NULL = {};

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
	public static byte[] zip(final byte[] input) {
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
	public static byte[] unzip(final byte[] input) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(input);
				GZIPInputStream zos = new GZIPInputStream(bais);) {
			return zos.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return NULL;
		}
	}

}
