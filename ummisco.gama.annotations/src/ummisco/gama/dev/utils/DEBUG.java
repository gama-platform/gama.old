package ummisco.gama.dev.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple and generic debugging/logging class that can be turned on / off on a class basis.
 * 
 * @author A. Drogoul
 * @since August 2018
 */
public class DEBUG {

	// AD 08/18: Changes to ConcurrentHashMap for multi-threaded DEBUG operations
	static Map<String, String> REGISTERED = new ConcurrentHashMap<>();
	static Map<String, Integer> COUNTERS = new ConcurrentHashMap<>();
	static boolean GLOBAL_OFF = false;
	static boolean GLOBAL_ON = false;
	static Map<Class<?>, Function<Object, String>> ARRAY_TO_STRING = new ConcurrentHashMap<>();

	static {
		ARRAY_TO_STRING.put(int.class, (o) -> Arrays.toString((int[]) o));
		ARRAY_TO_STRING.put(double.class, (o) -> Arrays.toString((double[]) o));
		ARRAY_TO_STRING.put(float.class, (o) -> Arrays.toString((float[]) o));
		ARRAY_TO_STRING.put(byte.class, (o) -> Arrays.toString((byte[]) o));
		ARRAY_TO_STRING.put(boolean.class, (o) -> Arrays.toString((boolean[]) o));
		ARRAY_TO_STRING.put(long.class, (o) -> Arrays.toString((long[]) o));
		ARRAY_TO_STRING.put(short.class, (o) -> Arrays.toString((short[]) o));
		ARRAY_TO_STRING.put(char.class, (o) -> Arrays.toString((char[]) o));
	}

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
	 * Returns an automatically incremented integer count if the calling class is registered. -1 otherwise. Useful for
	 * counting a number of invocations, etc. without having to define a static number on the class
	 * 
	 * @return -1 if the class is not registered, 0 if it is the first call, otherwise an incremented integer
	 */
	public static Integer COUNT() {
		final String s = findCallingClassName();
		Integer result = -1;
		if (REGISTERED.containsKey(s)) {
			if (COUNTERS.containsKey(s)) {
				result = COUNTERS.get(s) + 1;
				COUNTERS.put(s, result);
			} else {
				result = 0;
				COUNTERS.put(s, result);
			}
		}
		return result;
	}

	/**
	 * Resets the number previously used by COUNT() so that the next call to COUNT() returns 0;
	 * 
	 */
	public static void RESET() {
		final String s = findCallingClassName();
		if (REGISTERED.containsKey(s)) {
			if (COUNTERS.containsKey(s)) {
				COUNTERS.put(s, -1);
			}
		}
	}

	/**
	 * Simple timing utility to measure and output the number of ms taken by a runnable. If the class is registered,
	 * outputs the title provided and the time taken once the runnable is finished, otherwise simply runs the runnable
	 * (the overhead is minimal compared to simply executing the contents of the runnable).
	 * 
	 * Usage: DEBUG.TIMER("Important task", ()-> importantTask(...)); Output: Important Taks: 100ms
	 * 
	 * @param title
	 *            a string that will prefix the number of ms in the output
	 * @param supplier
	 *            an object that encapsulates the computation to measure
	 */

	public static void TIMER(final String title, final Runnable runnable) {
		final String s = findCallingClassName();
		if (!REGISTERED.containsKey(s)) {
			runnable.run();
		}
		final long start = System.currentTimeMillis();
		runnable.run();
		LOG(title + ": " + (System.currentTimeMillis() - start) + "ms");
	}

	/**
	 * Simple timing utility to measure and output the number of ms taken by the execution of a Supplier. Contrary to
	 * the timer accepting a runnable, this one returns a result. If the class is registered, outputs the title provided
	 * and the time taken once the supplier is finished and returns its result, otherwise simply returns the result of
	 * the supplier (the overhead is minimal compared to simply executing the contents of the provider)
	 * 
	 * Usage: Integer i = DEBUG.TIMER("My important integer computation", ()->myIntegerComputation()); // provided
	 * myIntegerComputation() returns an Integer.
	 * 
	 * Output: My important integer computation: 100ms
	 * 
	 * @param title
	 *            a string that will prefix the number of ms
	 * @param supplier
	 *            an object that encapsulates the computation to measure
	 * 
	 * @return The result of the supplier passed in argument
	 */

	public static <T> T TIMER(final String title, final Supplier<T> supplier) {
		final String s = findCallingClassName();
		if (!REGISTERED.containsKey(s)) { return supplier.get(); }
		final long start = System.currentTimeMillis();
		final T result = supplier.get();
		LOG(title + ": " + (System.currentTimeMillis() - start) + "ms");
		return result;
	}

	/**
	 * Turns DEBUG on for the calling class
	 */
	public static final void ON() {
		if (GLOBAL_OFF) { return; }
		final String calling = findCallingClassName();
		REGISTERED.put(calling, calling);
	}

	/**
	 * Turns DEBUG off for the calling class. This call can be avoided in a static context (not calling ON() will
	 * prevent the calling class from debugging anyway), but it can be used to disable logging based on some user
	 * actions, for instance.
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
	 * Unconditional output to System.err except if GLOBAL_OFF is true
	 * 
	 * @param string
	 */
	public static final void ERR(final Object s) {
		if (!GLOBAL_OFF) {
			System.err.println(s);
		}
	}

	/**
	 * Unconditional output to System.out except if GLOBAL_OFF is true.
	 * 
	 * @param string
	 */
	public static void LOG(final Object string) {
		if (!GLOBAL_OFF) {
			LOG(string, true);
		}
	}

	/**
	 * Will always output to System.out (using print if 'ln' is false) except if GLOBAL_OFF is true. Takes care of
	 * arrays so as to output their contents (and not their identity)
	 * 
	 * @param object
	 *            the message to output
	 * @param newLine
	 *            whether to pass a new line after or not
	 */
	public static void LOG(final Object object, final boolean newLine) {
		if (!GLOBAL_OFF) {
			if (newLine) {
				System.out.println(TO_STRING(object));
			} else {
				System.out.print(TO_STRING(object));
			}
		}
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
		if (object == null) { return "null"; }
		if (object.getClass().isArray()) {
			final Class<?> clazz = object.getClass().getComponentType();
			if (clazz.isPrimitive()) {
				return ARRAY_TO_STRING.get(clazz).apply(object);
			} else {
				return Arrays.deepToString((Object[]) object);
			}
		}
		return object.toString();

	}

	private static boolean IS_ON(final String className) {
		// Necessary to loop on the names as the call can emanate from an inner class or an anonymous class of the
		// "allowed" class
		for (final String name : REGISTERED.keySet()) {
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
		if (title == null) { return; }
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
		if (s == null) { return; }
		LINE();
		LOG(PAD("---------- " + s.toUpperCase() + " ", 80, '-'));
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
