package msi.gama.runtime;

import msi.gama.common.preferences.GamaPreferences;

/**
 * All-purpose static-method container class.
 *
 * @author Sebastiano Vigna
 * @since 0.1
 */

public final class MemoryUtils {
	private MemoryUtils() {}

	/** A static reference to {@link Runtime#getRuntime()}. */
	public final static Runtime RUNTIME = Runtime.getRuntime();

	/**
	 * Returns true if less then a percentage of the available memory is free.
	 *
	 */
	public static boolean memoryIsLow() {
		return availableMemory() * 100 < RUNTIME.totalMemory()
				* GamaPreferences.Runtime.CORE_MEMORY_PERCENTAGE.getValue();
	}

	/**
	 * Returns the amount of available memory (free memory plus never allocated memory).
	 *
	 * @return the amount of available memory, in bytes.
	 */
	public static long availableMemory() {
		return RUNTIME.freeMemory() + RUNTIME.maxMemory() - RUNTIME.totalMemory();
	}

	/**
	 * Returns the percentage of available memory (free memory plus never allocated memory).
	 *
	 * @return the percentage of available memory.
	 */
	public static int percAvailableMemory() {
		return (int) (MemoryUtils.availableMemory() * 100 / Runtime.getRuntime().maxMemory());
	}

	/**
	 * Tries to compact memory as much as possible by forcing garbage collection.
	 */
	public static void compactMemory() {
		try {
			final byte[][] unused = new byte[128][];
			for (int i = unused.length; i-- != 0;) {
				unused[i] = new byte[2000000000];
			}
		} catch (final OutOfMemoryError itsWhatWeWanted) {}
		System.gc();
	}

}
