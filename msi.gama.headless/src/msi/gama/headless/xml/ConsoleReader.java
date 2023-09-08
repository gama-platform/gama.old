/*******************************************************************************************************
 *
 * ConsoleReader.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ConsoleReader.
 */
public abstract class ConsoleReader {

	static {
		DEBUG.ON();
	}
	
	/** The end of file. */
	public static String END_OF_FILE = "</Experiment_plan>";

	/**
	 * Read on console.
	 *
	 * @return the input stream
	 */
	public static InputStream readOnConsole() {
		String entry = "";
		final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		final String pp = new File(".").getAbsolutePath();
		DEBUG.OUT("************************** CURRENT PATH **********************************\n"
				+ pp.substring(0, pp.length() - 1)
				+ "\n************************************************************\n");

		do {
			try {
				entry = entry + br.readLine();
			} catch (final IOException e) {
				
				e.printStackTrace();
			}
		} while (!entry.contains(END_OF_FILE));

		return new ByteArrayInputStream(entry.getBytes());

	}

}
