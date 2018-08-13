package ummisco.gama.dev.utils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A simple and generic debugging/logging class that can be turned on / off on a class basis.
 * 
 * @author A. Drogoul
 * @since August 2018
 */
public class DEBUG {

	static Set<String> REGISTERED = new HashSet<>();
	static boolean GLOBAL_OFF = false;
	static boolean GLOBAL_ON = false;

	/**
	 * Uses the stack trace to find the calling class. Use of reflection would be faster, but more prone to Oracle
	 * evolutions. StackWalker in Java 9 will be interesting to use for that
	 * 
	 * @return the name of the class that has called the method that has called this method
	 */
	private static String findCallingClassName() {
		return Thread.currentThread().getStackTrace()[3].getClassName();
	}

	/**
	 * Turns DEBUG on for the calling class
	 */
	public static final void ON() {
		if (GLOBAL_OFF) { return; }
		REGISTERED.add(findCallingClassName());
	}

	/**
	 * Turns DEBUG off for the calling class. This call can be avoided in a static context (no calls to ON() will
	 * prevent the calling class from debugging), but it can be used to disable logging based on some user actions, for
	 * instance.
	 */
	public static final void OFF() {
		if (GLOBAL_OFF) { return; }
		final String name = findCallingClassName();
		REGISTERED.remove(name);
	}

	/**
	 * Whether DEBUG is active for the calling class. Returns false if GLOBAL_OFF is true, and true if GLOBAL_ON is
	 * true.
	 * 
	 * @return whether DEBUG is active for this class
	 */
	public static boolean IS_ON() {
		if (GLOBAL_OFF) { return false; }
		return GLOBAL_ON || IS_ON(findCallingClassName());
	}

	/**
	 * Will always output to System.err except if GLOBAL_OFF is true
	 * 
	 * @param string
	 */
	public static final void ERR(final Object s) {
		if (!GLOBAL_OFF) {
			System.err.println(s);
		}
	}

	/**
	 * Will always output to System.out except if GLOBAL_OFF is true
	 * 
	 * @param string
	 */
	public static void LOG(final Object string) {
		if (!GLOBAL_OFF) {
			LOG(string, true);
		}
	}

	/**
	 * Will always output to System.out (using print if 'ln' is false) except if GLOBAL_OFF is true
	 * 
	 * @param string
	 *            the message to output
	 * @param newLine
	 *            whether to pass a new line after or not
	 */
	public static void LOG(final Object string, final boolean newLine) {
		if (!GLOBAL_OFF) {
			if (newLine) {
				System.out.println(string);
			} else {
				System.out.print(string);
			}
		}
	}

	private static boolean IS_ON(final String className) {
		// Necessary to loop on the names as the call can emanate from an inner class or an anonymous class of the
		// "allowed" class
		for (final String name : REGISTERED) {
			if (className.startsWith(name)) { return true; }
		}
		return false;
	}

	private DEBUG() {}

	/**
	 * Outputs a debug message to System.out if DEBUG is turned on for this class
	 * 
	 * @param s
	 *            the message to output
	 */
	public static final void OUT(final Object s) {
		if (GLOBAL_OFF) { return; }
		if (GLOBAL_ON || IS_ON(findCallingClassName())) {
			LOG(s, true);
		}
	}

	/**
	 * Outputs a debug message to System.out if DEBUG is turned on for this class, followed or not by a new line
	 * 
	 * @param s
	 *            the message to output
	 * @param newLine
	 *            whether or not to output a new line after the message
	 */
	public static final void OUT(final Object s, final boolean newLine) {
		if (GLOBAL_OFF) { return; }
		if (GLOBAL_ON || IS_ON(findCallingClassName())) {
			LOG(s, newLine);
		}
	}

	/**
	 * Outputs a debug message to System.out if DEBUG is turned on for this class
	 * 
	 * @param title
	 *            the first string to output
	 * @param pad
	 *            the minimum length of the first string (padded with spaces if shorter)
	 * @param other
	 *            another object on which toString() is applied
	 */
	public static final void OUT(final String title, final int pad, final Object other) {
		if (GLOBAL_OFF) { return; }
		if (GLOBAL_ON || IS_ON(findCallingClassName())) {
			LOG(PAD(title, pad) + Objects.toString(other));
		}
	}

	/**
	 * A utility method to output a line of 80 dashes.
	 */
	public static final void LINE() {
		LOG(PAD("", 80, '-'));
	}

	/**
	 * A utility method to output a "section" (i.e. a title padded with dashes between two lines of 80 chars
	 * 
	 */
	public static final void SECTION(final String s) {
		LINE();
		LOG(PAD("---------- " + s + " ", 80, '-'));
		LINE();
	}

	/**
	 * A utility method for padding a string with spaces in order to obtain a length of "minLength"
	 * 
	 * @param string
	 *            the string to pad
	 * @param minLength
	 *            the minimum length to reach (if the string is longer, it will be return as is)
	 * @return a string of minimum length minLength
	 */
	public static String PAD(final String string, final int minLength) {
		return PAD(string, minLength, ' ');
	}

	/**
	 * A utility method for padding a string with any character in order to obtain a length of "minLength"
	 * 
	 * @param string
	 *            the string to pad
	 * @param minLength
	 *            the minimum length to reach (if the string is longer, it will be return as is)
	 * @return a string of minimum length minLength
	 */

	public static String PAD(final String string, final int minLength, final char c) {
		if (string.length() >= minLength) { return string; }
		final StringBuilder sb = new StringBuilder(minLength);
		sb.append(string);
		for (int i = string.length(); i < minLength; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

}
