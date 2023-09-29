/*******************************************************************************************************
 *
 * FSTInputStream.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.serialization.util;

import java.io.IOException;
import java.io.InputStream;

import ummisco.gama.dev.utils.DEBUG;

/**
 * Created with IntelliJ IDEA. User: ruedi Date: 27.11.12 Time: 00:35 To change this template use File | Settings | File
 * Templates.
 */
public final class FSTInputStream extends InputStream {

	/** The report read fails. */
	public static boolean REPORT_READ_FAILS = false;

	/** The chunk size. */
	public int chunk_size = 8000;

	/** The cached buffer. */
	public static ThreadLocal<byte[]> cachedBuffer = new ThreadLocal<>();

	/** The buf. */
	public byte buf[];

	/** The pos. */
	public int pos;

	/** The count. */
	public int count; // avaiable valid read bytes

	/** The in. */
	InputStream in;

	/** The fully read. */
	boolean fullyRead = false; // true if input source has been read til end

	/** The byte backed. */
	public boolean byteBacked = false;

	/**
	 * Instantiates a new FST input stream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param in
	 *            the in
	 * @date 29 sept. 2023
	 */
	public FSTInputStream(final InputStream in) {
		initFromStream(in);
	}

	/**
	 * Reset for reuse.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param b
	 *            the b
	 * @param length
	 *            the length
	 * @date 29 sept. 2023
	 */
	public void resetForReuse(final byte b[], final int length) {
		reset();
		count = length;
		buf = b;
		pos = 0;
		byteBacked = true;
		fullyRead = true;
	}

	/**
	 * Inits the from stream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param in
	 *            the in
	 * @date 29 sept. 2023
	 */
	public void initFromStream(final InputStream in) {
		fullyRead = false;
		byteBacked = false;
		pos = 0;
		this.in = in;
		if (buf == null) {
			buf = cachedBuffer.get();
			if (buf == null) {
				buf = new byte[chunk_size];
				cachedBuffer.set(buf);
			}
		}
		readNextChunk(in);
	}

	/**
	 * Checks if is fully read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is fully read
	 * @date 29 sept. 2023
	 */
	public boolean isFullyRead() { return fullyRead && pos >= count; }

	/**
	 * Read next chunk.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param in
	 *            the in
	 * @date 29 sept. 2023
	 */
	public void readNextChunk(final InputStream in) {
		int read;
		try {
			if (buf.length < count + chunk_size) {
				ensureCapacity(Math.max(Math.min(Integer.MAX_VALUE - 1, buf.length * 2), count + chunk_size)); // at
																												// least
																												// grab
																												// 5kb
			}
			read = in.read(buf, count, chunk_size);
			if (read > 0) {
				count += read;
			} else {
				fullyRead = true;
			}
		} catch (IOException e) {
			if (REPORT_READ_FAILS) { // flag bound to avoid breaking things ..
				DEBUG.ERR("Failed to read next chunk from InputStream", e);
				throw new RuntimeException("Failed to read next chunk from InputStream", e);
			}
			fullyRead = true;
		}
	}

	/**
	 * Ensure capacity.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param siz
	 *            the siz
	 * @date 29 sept. 2023
	 */
	public void ensureCapacity(final int siz) {
		if (buf.length < siz && !fullyRead) {
			byte newBuf[] = new byte[siz];
			System.arraycopy(buf, 0, newBuf, 0, buf.length);
			buf = newBuf;
			if (siz < 10 * 1024 * 1024) { // issue 19, don't go overboard with buffer caching
				cachedBuffer.set(buf);
			}
		}
	}

	/**
	 * Instantiates a new FST input stream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param buf
	 *            the buf
	 * @date 29 sept. 2023
	 */
	public FSTInputStream(final byte buf[]) {
		this.buf = buf;
		this.pos = 0;
		this.count = buf.length;
	}

	/**
	 * Instantiates a new FST input stream.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param buf
	 *            the buf
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @date 29 sept. 2023
	 */
	public FSTInputStream(final byte buf[], final int offset, final int length) {
		this.buf = buf;
		this.pos = offset;
		this.count = Math.min(offset + length, buf.length);
	}

	@Override
	public int read() {
		if (pos < count) return buf[pos++] & 0xff;
		readNextChunk(in);
		if (fullyRead) {}
		return -1;
	}

	@Override
	public int read(final byte b[], final int off, int len) {
		if (isFullyRead()) return -1;
		while (!fullyRead && pos + len >= count) { readNextChunk(in); }
		int avail = count - pos;
		if (len > avail) { len = avail; }
		if (len <= 0) return 0;
		System.arraycopy(buf, pos, b, off, len);
		pos += len;
		return len;
	}

	@Override
	public long skip(final long n) {
		long k = count - pos;
		if (n < k) { k = n < 0 ? 0 : n; }
		pos += k;
		return k;
	}

	@Override
	public int available() {
		return count - pos;
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public void mark(final int readAheadLimit) {}

	@Override
	public void reset() {
		count = 0;
		pos = 0;
		fullyRead = false;
		byteBacked = false;
	}

	@Override
	public void close() throws IOException {
		if (in != null) { in.close(); }
	}

	/**
	 * Ensure read ahead.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param bytes
	 *            the bytes
	 * @date 29 sept. 2023
	 */
	public void ensureReadAhead(final int bytes) {
		if (byteBacked) return;
		int targetCount = pos + bytes;
		while (!fullyRead && count < targetCount) { readNextChunk(in); }
	}
}
