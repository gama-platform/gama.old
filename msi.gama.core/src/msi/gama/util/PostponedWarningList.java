/*********************************************************************************************
 * 
 * 
 * 'PostponedWarningList.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util;

import gnu.trove.map.hash.TObjectIntHashMap;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Receives warnings as Strings (hopefully, many times the same). Stores them.
 * Then, when asked for, reports all these warnings to GAMA by grouping them.
 * Result is like "errorA (10 times)" instead of displaying the same thing 10
 * times.
 * 
 * Remember to use quite generic messages. Else they will never be the same, and
 * will not be grouped together. The comparison is based on the default equals
 * of the String class.
 * 
 * @author Samuel Thiriot
 * 
 */
public class PostponedWarningList {

	private final TObjectIntHashMap<String> warning2count = new TObjectIntHashMap<>();

	public static boolean writeSystemOut = false;

	public void clear() {
		warning2count.clear();
	}

	public void addWarning(final String msg) {
		warning2count.adjustOrPutValue(msg, 1, 1);
	}

	/**
	 * Raise GAma exceptions and transmists them with GAMA.reportError. If
	 * several warnings were detected, the "header" will be displayed first.
	 * 
	 * @param header
	 */
	public void publishAsGAMAWarning(final String header) {

		if (warning2count.isEmpty()) {
			return; // quick exit
		}

		// raise errors
		if (header != null && !header.isEmpty() && warning2count.size() > 1) {
			GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.error(header), true);
		}
		warning2count.forEachEntry((msg, times) -> {
			final StringBuffer sb = new StringBuffer();
			sb.append(msg).append(" (").append(times);
			if (times == 1) {
				sb.append(" time)");
			} else {
				sb.append(" times)");
			}
			if (writeSystemOut) {
				System.err.println(sb.toString());
			}
			GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.error(sb.toString()), true);
			return true;
		});

	}
}
