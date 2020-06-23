/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartHistory.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ummisco.gama.dev.utils.DEBUG;

/**
 * A simple history for charts, that tries to maintain a low memory footprint by encoding and zipping the contents
 * whenever it reaches a certain limit.
 * 
 * @author drogoul
 *
 */
public class ChartHistory {

	final static int MAX = 5000000;

	public StringBuilder current = new StringBuilder(MAX);
	List<byte[]> older = new ArrayList<>();
	final char[] buffer = new char[4096];

	public void append(final String string) {
		current.append(string);
		verifyOverflow();
	}

	private void verifyOverflow() {
		if (current.length() > MAX) {
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			try (GZIPOutputStream zos = new GZIPOutputStream(stream)) {
				zos.write(current.toString().getBytes(StandardCharsets.US_ASCII));
			} catch (final IOException e) {}
			older.add(stream.toByteArray());
			DEBUG.ERR("Chart history limit reached (compressing " + current.toString().getBytes().length
					+ " bytes into " + older.get(older.size() - 1).length + " bytes)");
			current.setLength(0);
		}
	}

	public void writeTo(final BufferedWriter bw) throws IOException {
		for (final byte[] array : older) {
			try (final InputStreamReader reader =
					new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(array)))) {
				int n = 0;
				while (-1 != (n = reader.read(buffer))) {
					bw.write(buffer, 0, n);
				}
			}
		}
		bw.append(current);

	}

}
