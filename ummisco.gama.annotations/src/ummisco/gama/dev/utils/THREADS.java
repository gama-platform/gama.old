/*******************************************************************************************************
 *
 * THREADS.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.dev.utils;

/**
 * The Class THREADS.
 */
public class THREADS {

	/**
	 * Wait. Make the current thread wait for the specified amount of msec, and optionnaly outputs a text while it is
	 * waiting and a text in case it is interrupted
	 *
	 * @param title
	 *            the title
	 * @param msec
	 *            the msec
	 * @return true, if successful
	 */
	public static boolean WAIT(final long msec, final String... texts) {
		if (msec == 0) return true;
		String title = null, error = null;
		if (texts.length > 0) {
			title = texts[0];
			if (texts.length > 1) { error = texts[1]; }
		}
		try {
			if (title != null) { DEBUG.OUT(title + ". Waiting " + msec + "ms."); }
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			if (error != null) { DEBUG.ERR(error); }
			// e.printStackTrace();
			return false;
		}
		return true;
	}

}
