/*******************************************************************************************************
 *
 * msi.gama.util.PostponedWarningList.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.util.HashMap;
import java.util.Map;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Receives warnings as Strings (hopefully, many times the same). Stores them. Then, when asked for, reports all these
 * warnings to GAMA by grouping them. Result is like "errorA (10 times)" instead of displaying the same thing 10 times.
 *
 * Remember to use quite generic messages. Else they will never be the same, and will not be grouped together. The
 * comparison is based on the default equals of the String class.
 *
 * @author Samuel Thiriot
 *
 */
public class PostponedWarningList {

	static {
		DEBUG.OFF();
	}

	private final Map<String, Integer> warning2count = new HashMap<>();

	public static boolean writeSystemOut = false;

	public void clear() {
		warning2count.clear();
	}

	public void addWarning(final String msg) {
		warning2count.compute(msg, (k, v) -> v == null ? 1 : v + 1);
	}

	/**
	 * Raise GAma exceptions and transmists them with GAMA.reportError. If several warnings were detected, the "header"
	 * will be displayed first.
	 *
	 * @param header
	 */
	public void publishAsGAMAWarning(final IScope scope, final String header) {

		if (warning2count.isEmpty()) {
			return; // quick exit
		}

		// raise errors
		if (header != null && !header.isEmpty() && warning2count.size() > 1) {
			GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.error(header, scope), true);
		}
		warning2count.forEach((msg, times) -> {
			final StringBuffer sb = new StringBuffer();
			sb.append(msg).append(" (").append(times);
			if (times == 1) {
				sb.append(" time)");
			} else {
				sb.append(" times)");
			}
			if (DEBUG.IS_ON()) {
				DEBUG.ERR(sb.toString());
			}
			GAMA.reportError(GAMA.getRuntimeScope(), GamaRuntimeException.error(sb.toString(), scope), true);
		});

	}
}
