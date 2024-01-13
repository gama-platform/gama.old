/*******************************************************************************************************
 *
 * DEBUG.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.dev.utils;

import static java.lang.System.currentTimeMillis;
import static ummisco.gama.dev.utils.FLAGS.ENABLE_DEBUG;
import static ummisco.gama.dev.utils.FLAGS.ENABLE_LOGGING;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.StackWalker.StackFrame;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A simple and generic debugging/logging class that can be turned on / off on a class basis.
 *
 * @author A. Drogoul
 * @since August 2018
 */
public class DEBUG {

	/** The force on. */
	public static boolean FORCE_ON;

	/**
	 * A custom security manager that exposes the getClassContext() information
	 */
	static class MySecurityManager extends SecurityManager {

		/**
		 * Gets the caller class name.
		 *
		 * @param callStackDepth
		 *            the call stack depth
		 * @return the caller class name
		 */
		public String getCallerClassName(final int callStackDepth) {
			return getClassContext()[callStackDepth].getName();
		}

	}

	/** The Constant SECURITY_MANAGER. */
	private final static MySecurityManager SECURITY_MANAGER = new MySecurityManager();

	/** The Constant REGISTERED. */
	// AD 08/18: Changes to ConcurrentHashMap for multi-threaded DEBUG operations
	private static final ConcurrentHashMap<String, String> REGISTERED = new ConcurrentHashMap<>();

	/** The Constant COUNTERS. */
	private static final ConcurrentHashMap<String, Integer> COUNTERS = new ConcurrentHashMap<>();

	/** The Constant LOG_WRITERS. */
	private static final ThreadLocal<PrintStream> LOG_WRITERS = ThreadLocal.withInitial(() -> System.out);

	/** The Constant stackWalker. */
	static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	/**
	 * Uses a custom security manager to get the caller class name. Use of reflection would be faster, but more prone to
	 * Oracle evolutions. StackWalker in Java 9 will be interesting to use for that
	 *
	 * @return the name of the class that has called the method that has called this method
	 */
	static String findCallingClassName() {
		Optional<String> caller = STACK_WALKER.walk(frames -> frames.map(StackFrame::getClassName)
				.filter(s -> !s.contains("ummisco.gama.dev.utils")).findFirst());
		if (caller.isEmpty()) return SECURITY_MANAGER.getCallerClassName(3);
		return caller.get();
	}

	/**
	 * Resets the number previously used by COUNT() so that the next call to COUNT() returns 0;
	 *
	 */
	public static void RESET() {
		final String s = findCallingClassName();
		if (REGISTERED.containsKey(s) && COUNTERS.containsKey(s)) { COUNTERS.put(s, -1); }
	}

	/**
	 * The Interface RunnableWithException.
	 *
	 * @param <T>
	 *            the generic type
	 */
	public interface RunnableWithException<T extends Throwable> {

		/**
		 * Run.
		 *
		 * @throws T
		 *             the t
		 */
		void run() throws T;
	}

	/**
	 * Simple timing utility to measure and output the number of ms taken by a runnable. If the class is registered,
	 * outputs the title provided and the time taken once the runnable is finished, otherwise simply runs the runnable
	 * (the overhead is minimal compared to simply executing the contents of the runnable).
	 *
	 * Usage: DEBUG.TIMER("Important task", "done in", ()-> importantTask(...)); Output: Important Taks done in 100ms
	 *
	 * @param title
	 *            a string that will prefix the number of ms in the output
	 * @param supplier
	 *            an object that encapsulates the computation to measure
	 * @throws Exception
	 */

	@SafeVarargs
	public static void TIMER(final String category, final String begin, final String end, final Runnable runnable,
			final Consumer<Long>... followUpWithResult) {
		if (!ENABLE_LOGGING) {
			runnable.run();
			return;
		}
		final long start = currentTimeMillis();
		runnable.run();
		long duration = currentTimeMillis() - start;
		BANNER(category, begin, end, duration + "ms");
		if (followUpWithResult != null && followUpWithResult.length > 0) { followUpWithResult[0].accept(duration); }
	}

	/**
	 * Timer with exceptions.
	 *
	 * @param <T>
	 *            the generic type
	 * @param title
	 *            the title
	 * @param runnable
	 *            the runnable
	 * @throws T
	 *             the t
	 */
	public static <T extends Throwable> void TIMER_WITH_EXCEPTIONS(final String category, final String begin,
			final String end, final RunnableWithException<T> runnable) throws T {
		if (!ENABLE_LOGGING) {
			runnable.run();
			return;
		}
		final long start = currentTimeMillis();
		runnable.run();
		BANNER(category, begin, end, currentTimeMillis() - start + "ms");
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

	public static <T> T TIMER(final String category, final String title, final String end, final Supplier<T> supplier) {
		if (!ENABLE_LOGGING) return supplier.get();
		final long start = System.currentTimeMillis();
		final T result = supplier.get();
		BANNER(category, title, end, currentTimeMillis() - start + "ms");
		return result;
	}

	/**
	 * Turns DEBUG on for the calling class
	 */
	public static final void ON() {
		if (!ENABLE_DEBUG || !ENABLE_LOGGING) return;
		final String calling = findCallingClassName();
		REGISTERED.put(calling, calling);
	}

	/**
	 * Turns DEBUG off for the calling class. This call can be avoided in a static context (not calling ON() will
	 * prevent the calling class from debugging anyway), but it can be used to disable logging based on some user
	 * actions, for instance.
	 */
	public static final void OFF() {
		if (!ENABLE_DEBUG || !ENABLE_LOGGING) return;
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
		if (!ENABLE_DEBUG || !ENABLE_LOGGING) return false;
		return IS_ON(findCallingClassName());
	}

	/**
	 * Unconditional output to System.err except if GLOBAL_OFF is true
	 *
	 * @param string
	 */
	public static final void ERR(final Object s) {
		if (!ENABLE_DEBUG || !ENABLE_LOGGING) return;
		System.err.println(STRINGS.TO_STRING(s));
	}

	/**
	 * Unconditional output to System.err except if GLOBAL_OFF is true. The stack trace is included
	 *
	 * @param string
	 */
	public static final void ERR(final Object s, final Throwable t) {
		if (!ENABLE_DEBUG || !ENABLE_LOGGING) return;
		System.err.println(STRINGS.TO_STRING(s));
		t.printStackTrace();
	}

	/**
	 * Unconditional output to System.out except if GLOBAL_OFF is true.
	 *
	 * @param string
	 */
	public static void LOG(final Object string) {
		if (ENABLE_LOGGING) { LOG(string, true); }
	}

	/**
	 * Banner.
	 *
	 * @param title
	 *            the title
	 * @param state
	 *            the state
	 * @param result
	 *            the result
	 */
	public static void BANNER(final String title, final String state, final String result) {
		BANNER("GAMA", title, state, result);
	}

	/**
	 * Banner.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param category
	 *            the category
	 * @param title
	 *            the title
	 * @param state
	 *            the state
	 * @param result
	 *            the result
	 * @date 1 janv. 2024
	 */
	public static void BANNER(final String category, final String title, final String state, final String result) {
		String cat = STRINGS.PAD("> " + category, 8, ' ') + ": ";
		LOG(STRINGS.PAD(cat + title + " ", 55, ' ') + STRINGS.PAD(" " + state, 15, '_') + " " + result);
	}

	/**
	 * Will always output to System.out or the registered logger for this thread (using print if 'newLine' is false)
	 * except if GLOBAL_OFF is true. Takes care of arrays so as to output their contents (and not their identity)
	 *
	 * @param object
	 *            the message to output
	 * @param newLine
	 *            whether to pass a new line after or not
	 */
	public static void LOG(final Object object, final boolean newLine) {
		if (ENABLE_LOGGING) {
			if (newLine) {
				LOG_WRITERS.get().println(STRINGS.TO_STRING(object));
			} else {
				LOG_WRITERS.get().print(STRINGS.TO_STRING(object));
			}
		}
	}

	/**
	 * Register log writer.
	 *
	 * @param writer
	 *            the writer
	 */
	public static void REGISTER_LOG_WRITER(final OutputStream writer) {
		LOG_WRITERS.set(new PrintStream(writer, true));
	}

	/**
	 * Unregister log writer.
	 */
	public static void UNREGISTER_LOG_WRITER() {
		LOG_WRITERS.remove();
	}

	/**
	 * Checks if is on.
	 *
	 * @param className
	 *            the class name
	 * @return true, if successful
	 */
	static boolean IS_ON(final String className) {
		// Necessary to loop on the names as the call can emanate from an inner class or an anonymous class of the
		// "allowed" class
		if (FORCE_ON) return true;
		for (final String name : REGISTERED.keySet()) { if (className.startsWith(name)) return true; }
		return false;
	}

	/**
	 * Instantiates a new debug.
	 */
	private DEBUG() {}

	/**
	 * Outputs a debug message to System.out if DEBUG is turned on for this class
	 *
	 * @param s
	 *            the message to output
	 */
	public static final void OUT(final Object s) {
		if (!ENABLE_DEBUG || !ENABLE_LOGGING) return;
		if (IS_ON(findCallingClassName())) { LOG(s, true); }
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
		if (!ENABLE_DEBUG || !ENABLE_LOGGING) return;
		if (IS_ON(findCallingClassName())) { LOG(s, newLine); }
	}

	/**
	 * Outputs a debug message to System.out if DEBUG is turned on for this class
	 *
	 * @param title
	 *            the first string to output
	 * @param pad
	 *            the minimum length of the first string (padded with spaces if shorter)
	 * @param other
	 *            another object on which TO_STRING() is applied
	 */
	public static final void OUT(final String title, final int pad, final Object other) {
		if (!ENABLE_DEBUG || !ENABLE_LOGGING || title == null) return;
		if (IS_ON(findCallingClassName())) { LOG(STRINGS.PAD(title, pad) + STRINGS.TO_STRING(other)); }
	}

	/**
	 * A utility method to output a line of 80 dashes.
	 */
	public static final void LINE() {
		LOG(STRINGS.PAD("", 80, '-'));
	}

	/**
	 * A utility method to output a "section" (i.e. a title padded with dashes between two lines of 80 chars).
	 * Equivalent to LINE();TITLE(s);LINE()
	 *
	 */
	public static final void SECTION(final String s) {
		if (s == null) return;
		LINE();
		TITLE(s);
		LINE();
	}

	/**
	 * A utility method to output a "title" (i.e. a title centered and padded with dashes to form a line of 80 chars)
	 *
	 */
	public static final void TITLE(final String s) {
		if (s == null) return;
		LOG(STRINGS.PAD("---------- " + s.toUpperCase() + " ", 80, '-'));
	}

	/**
	 *
	 * Stack.
	 */
	public static void STACK() {
		if (!ENABLE_LOGGING || !DEBUG.IS_ON(DEBUG.findCallingClassName())) return;
		DEBUG.LOG(STRINGS.PAD("--- Stack trace ", 80, '-'));
		DEBUG.STACK_WALKER.walk(stream1 -> {
			stream1.skip(2).forEach(s -> DEBUG.LOG("> " + s));
			return null;
		});
		DEBUG.LINE();
	}

	/**
	 * Force on.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 15 oct. 2023
	 */
	public static void FORCE_ON() {
		FORCE_ON = true;
	}

}
