/*******************************************************************************************************
 *
 * OSUtils.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler.doc.utils;

/**
 * The Class OSUtils.
 */
public class OSUtils {

	/** The os. */
	private static String OS = null;

	/**
	 * Gets the os name.
	 *
	 * @return the os name
	 */
	public static String getOsName() {
		if (OS == null) {
			OS = System.getProperty("os.name");
		}
		return OS;
	}

	/**
	 * Checks if is windows.
	 *
	 * @return true, if is windows
	 */
	public static boolean isWindows() {
		return getOsName().startsWith("Windows");
	}

	/**
	 * Checks if is mac OS.
	 *
	 * @return true, if is mac OS
	 */
	public static boolean isMacOS() {
		return getOsName().startsWith("Mac");
	}

	/**
	 * Checks if is other.
	 *
	 * @return true, if is other
	 */
	public static boolean isOther() {
		return !isWindows() && !isMacOS();
	}

}
