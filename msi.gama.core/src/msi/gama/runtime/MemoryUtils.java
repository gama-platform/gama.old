/*******************************************************************************************************
 *
 * MemoryUtils.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime;

import static org.eclipse.core.runtime.Platform.getConfigurationLocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.dev.utils.DEBUG;

/**
 * All-purpose static-method container class.
 *
 * @author Sebastiano Vigna
 * @since 0.1
 */

public final class MemoryUtils {

	static {
		DEBUG.OFF();
	}

	/**
	 * Instantiates a new memory utils.
	 */
	private MemoryUtils() {}

	/** A static reference to {@link Runtime#getRuntime()}. */
	public final static Runtime RUNTIME = Runtime.getRuntime();

	/**
	 * Returns true if less then a percentage of the available memory is free.
	 *
	 */
	public static boolean memoryIsLow() {
		return availableMemory() * 10e8 < RUNTIME.totalMemory()
				* GamaPreferences.Runtime.CORE_MEMORY_PERCENTAGE.getValue();
	}

	/**
	 * Returns the amount of available memory (free memory plus never allocated memory).
	 *
	 * @return the amount of available memory, in megabytes.
	 */
	public static int availableMemory() {
		long bytes = RUNTIME.freeMemory() + RUNTIME.maxMemory() - RUNTIME.totalMemory();
		double result = bytes / 10e6;
		return (int) result;
	}

	/**
	 * Max memory.
	 *
	 * @return the int
	 */
	public static int maxMemory() {
		if (DEBUG.IS_ON()) {
			DEBUG.OUT("Max memory via runtime: " + maxMemoryThroughRuntime());
			DEBUG.OUT("Max memory via runtime bean: " + maxMemoryThroughRuntimeBean());
			DEBUG.OUT("Max memory via memory bean: " + maxMemoryThroughMemoryBean());
		}
		// This one seems to be the most reliable (if -Xmx is defined, otherwise 0 is returned
		return maxMemoryThroughRuntimeBean();
	}

	/**
	 * Returns the amount of available memory (free memory plus never allocated memory).
	 *
	 * @return the amount of available memory, in megabytes.
	 */
	public static int maxMemoryThroughRuntime() {
		long bytes = RUNTIME.maxMemory();
		double result = bytes / 10e6;
		return (int) result;
	}

	/**
	 * Max memory through MX bean.
	 *
	 * @return the int
	 */
	public static int maxMemoryThroughMemoryBean() {
		MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
		long bytes = bean.getHeapMemoryUsage().getMax();
		double result = bytes / 10e6;
		return (int) result;
	}

	/**
	 * Max memory through runtime bean.
	 *
	 * @return the int
	 */
	public static int maxMemoryThroughRuntimeBean() {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		List<String> aList = bean.getInputArguments();
		for (String s : aList) {
			DEBUG.OUT(s);
			if (s.startsWith("-Xmx")) {
				final var last = s.charAt(s.length() - 1);
				var divider = 1000000D;
				var unit = false;
				switch (last) {
					case 'k':
					case 'K':
						unit = true;
						divider = 1000;
						break;
					case 'm':
					case 'M':
						unit = true;
						divider = 1;
						break;
					case 'g':
					case 'G':
						unit = true;
						divider = 0.001;
						break;
				}
				var trim = s;
				trim = trim.replace("-Xmx", "");
				if (unit) { trim = trim.substring(0, trim.length() - 1); }
				final var result = Integer.parseInt(trim);
				return (int) (result / divider);

			}
		}
		return 0;
	}

	/**
	 * Read max memory in megabytes.
	 *
	 * @param ini
	 *            the ini
	 * @return the int
	 */
	public static int readMaxMemoryInMegabytes(final File ini) {
		try {
			if (ini != null) {
				try (final var stream = new FileInputStream(ini);
						final var reader = new BufferedReader(new InputStreamReader(stream));) {
					var s = reader.readLine();
					while (s != null) {
						if (s.startsWith("-Xmx")) {
							final var last = s.charAt(s.length() - 1);
							var divider = 1000000D;
							var unit = false;
							switch (last) {
								case 'k':
								case 'K':
									unit = true;
									divider = 1000;
									break;
								case 'm':
								case 'M':
									unit = true;
									divider = 1;
									break;
								case 'g':
								case 'G':
									unit = true;
									divider = 0.001;
									break;
							}
							var trim = s;
							trim = trim.replace("-Xmx", "");
							if (unit) { trim = trim.substring(0, trim.length() - 1); }
							final var result = Integer.parseInt(trim);
							return (int) (result / divider);

						}
						s = reader.readLine();
					}
				}
			}
		} catch (final IOException e) {}
		return 0;

	}

	/**
	 * Change max memory.
	 *
	 * @param ini
	 *            the ini
	 * @param memory
	 *            the memory
	 */
	public static void changeMaxMemory(final File ini, final int memory) {
		final var mem = memory < 128 ? 128 : memory;
		try {
			final List<String> contents = new ArrayList<>();
			if (ini != null) {
				try (final var stream = new FileInputStream(ini);
						final var reader = new BufferedReader(new InputStreamReader(stream));) {
					var s = reader.readLine();
					while (s != null) {
						if (s.startsWith("-Xmx")) { s = "-Xmx" + mem + "m"; }
						contents.add(s);
						s = reader.readLine();
					}
				}
				try (final var os = new FileOutputStream(ini);
						final var writer = new BufferedWriter(new OutputStreamWriter(os));) {
					for (final String line : contents) {
						writer.write(line);
						writer.newLine();
					}
					writer.flush();
				}
			}
		} catch (final IOException e) {}

	}

	/**
	 * Find ini file.
	 *
	 * @return the file
	 */
	public static File findIniFile() {
		return findIt(new File(getConfigurationLocation().getURL().getPath()));
	}

	/**
	 * Find it.
	 *
	 * @param rootDir
	 *            the root dir
	 * @return the file
	 */
	public static File findIt(final File rootDir) {
		File[] files = rootDir.listFiles();
		if (files != null) {
			List<File> directories = new ArrayList<>(files.length);
			for (File file : files) {
				if ("Gama.ini".equals(file.getName())) return file;
				if (file.isDirectory()) { directories.add(file); }
			}
			for (File directory : directories) {
				File file = findIt(directory);
				if (file != null) return file;
			}
		}
		return null;
	}

}
