/*******************************************************************************************************
 *
 * FSTOutputStream.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package org.nustaq.serialization.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import ummisco.gama.dev.utils.DEBUG;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 19.11.12 Time: 10:00 To change this template use File | Settings | File
 * Templates.
 */
public final class FSTOutputStream extends OutputStream {

	/**
	 * The buffer where data is stored.
	 */
	public byte buf[];
	/**
	 * The number of valid bytes in the buffer.
	 */
	public int pos;

	/** The outstream. */
	OutputStream outstream;

	/** The off. */
	private int off;

	/**
	 * Instantiates a new FST output stream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param out
	 *            the out
	 * @date 29 sept. 2023
	 */
	public FSTOutputStream(final OutputStream out) {
		this(4000, out);
	}

	/**
	 * Instantiates a new FST output stream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param size
	 *            the size
	 * @param out
	 *            the out
	 * @date 29 sept. 2023
	 */
	public FSTOutputStream(final int size, final OutputStream out) {
		buf = new byte[size];
		outstream = out;
	}

	/**
	 * Gets the outstream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the outstream
	 * @date 29 sept. 2023
	 */
	public OutputStream getOutstream() { return outstream; }

	/**
	 * Sets the outstream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param outstream
	 *            the new outstream
	 * @date 29 sept. 2023
	 */
	public void setOutstream(final OutputStream outstream) { this.outstream = outstream; }

	/**
	 * Gets the buffer where data is stored.
	 *
	 * @return the buffer where data is stored
	 */
	public byte[] getBuf() { return buf; }

	/**
	 * Sets the buffer where data is stored.
	 *
	 * @param buf
	 *            the new buffer where data is stored
	 */
	public void setBuf(final byte[] buf) { this.buf = buf; }

	/**
	 * Ensure free.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param free
	 *            the free
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void ensureFree(final int free) throws IOException {
		// inline ..
		if (pos + free - buf.length > 0) { grow(pos + free); }
	}

	/**
	 * Ensure capacity.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param minCapacity
	 *            the min capacity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 29 sept. 2023
	 */
	public void ensureCapacity(final int minCapacity) throws IOException {
		if (minCapacity - buf.length > 0) { grow(minCapacity); }
	}

	/**
	 * Grow.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param minCapacity
	 *            the min capacity
	 * @date 29 sept. 2023
	 */
	private void grow(final int minCapacity) {
		// overflow-conscious code
		int oldCapacity = buf.length;
		int newCapacity = oldCapacity * 2;
		if (oldCapacity > 50 * 1024 * 1024) { // for large object graphs, grow more carefully
			newCapacity = minCapacity + 1024 * 1024 * 20;
		} else if (oldCapacity < 1001) {
			newCapacity = 4000; // large step initially
		}
		if (newCapacity - minCapacity < 0) { newCapacity = minCapacity; }

		try {
			buf = Arrays.copyOf(buf, newCapacity);
		} catch (OutOfMemoryError ome) {
			DEBUG.ERR("OME resize from " + buf.length + " to " + newCapacity + " clearing caches ..", ome);
			throw new RuntimeException(ome);
		}
	}

	@Override
	public void write(final int b) throws IOException {
		ensureCapacity(pos + 1);
		buf[pos] = (byte) b;
		pos += 1;
	}

	@Override
	public void write(final byte b[], final int off, final int len) throws IOException {
		ensureCapacity(pos + len);
		System.arraycopy(b, off, buf, pos, len);
		pos += len;
	}

	/**
	 * only works if no flush has been triggered (aka only write one object per stream instance)
	 *
	 * @param out
	 * @throws IOException
	 */
	public void copyTo(final OutputStream out) throws IOException {
		out.write(buf, 0, pos);
	}

	/**
	 * Reset.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 sept. 2023
	 */
	public void reset() {
		pos = 0;
		off = 0;
	}

	/**
	 * To byte array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the byte[]
	 * @date 29 sept. 2023
	 */
	public byte toByteArray()[] {
		return Arrays.copyOf(buf, pos);
	}

	/**
	 * Size.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the int
	 * @date 29 sept. 2023
	 */
	public int size() {
		return pos;
	}

	@Override
	public void close() throws IOException {
		flush();
		if (outstream != this) { outstream.close(); }
	}

	@Override
	public void flush() throws IOException {
		if (pos > 0 && outstream != null && outstream != this) {
			copyTo(outstream);
			off = pos;
			reset();
		}
		if (outstream != this && outstream != null) { outstream.flush(); }
	}

	/**
	 * Reset.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param out
	 *            the out
	 * @date 29 sept. 2023
	 */
	public void reset(final byte[] out) {
		reset();
		buf = out;
	}

	/**
	 * Gets the off.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the off
	 * @date 29 sept. 2023
	 */
	// return offset of pos to stream position
	public int getOff() { return off; }
}
