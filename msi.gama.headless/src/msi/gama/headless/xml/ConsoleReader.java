package msi.gama.headless.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import utils.DEBUG;

public abstract class ConsoleReader {

	static {
		DEBUG.ON();
	}
	public static String END_OF_FILE = "</Experiment_plan>";

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (!entry.contains(END_OF_FILE));

		return new ByteArrayInputStream(entry.getBytes());

	}

}
