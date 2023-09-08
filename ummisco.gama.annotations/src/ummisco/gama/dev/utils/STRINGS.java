/*******************************************************************************************************
 *
 * STRINGS.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.dev.utils;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * The Class STRINGS.
 */
public class STRINGS {

	/** The Constant TO_STRING. */
	static final ConcurrentHashMap<Class<?>, Function<Object, String>> TO_STRING = new ConcurrentHashMap<>();

	static {
		TO_STRING.put(int.class, o -> Arrays.toString((int[]) o));
		TO_STRING.put(double.class, o -> Arrays.toString((double[]) o));
		TO_STRING.put(float.class, o -> Arrays.toString((float[]) o));
		TO_STRING.put(byte.class, o -> Arrays.toString((byte[]) o));
		TO_STRING.put(boolean.class, o -> Arrays.toString((boolean[]) o));
		TO_STRING.put(long.class, o -> Arrays.toString((long[]) o));
		TO_STRING.put(short.class, o -> Arrays.toString((short[]) o));
		TO_STRING.put(char.class, o -> Arrays.toString((char[]) o));
	}

	/**
	 * Tries to obtain a correct string representation of the object, including when it is an array (or an array of
	 * arrays). Made public to be used outside the debug sessions
	 *
	 * @param object
	 *            any object
	 * @return its string representation
	 */
	public static String TO_STRING(final Object object) {
		if (object == null) return "null";
		if (object.getClass().isArray()) {
			final Class<?> clazz = object.getClass().getComponentType();
			if (clazz.isPrimitive()) return TO_STRING.get(clazz).apply(object);
			return Arrays.deepToString((Object[]) object);
		}
		return object.toString();

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
		if (string.length() >= minLength) return string;
		final StringBuilder sb = new StringBuilder(minLength);
		sb.append(string);
		for (int i = string.length(); i < minLength; i++) { sb.append(c); }
		return sb.toString();
	}

}
