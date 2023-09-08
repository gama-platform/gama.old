/*******************************************************************************************************
 *
 * COUNTER.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.dev.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Class COUNTER. A simple way to get unique indexes for various objects
 */
public class COUNTER {

	/** The Constant COUNTERS. */
	private static final ConcurrentHashMap<String, Integer> COUNTERS = new ConcurrentHashMap<>();

	/** The count. */
	private static AtomicInteger COUNT = new AtomicInteger();

	/**
	 * Returns a unique integer
	 *
	 * @return the int
	 */
	public static int GET_UNIQUE() {
		return COUNT.incrementAndGet();
	}

	/**
	 * Returns an automatically incremented integer count proper to the class of the calling object. Useful for counting
	 * a number of invocations, etc. without having to define a static number on the class
	 *
	 * @return 0 if it is the first call, otherwise an incremented integer
	 */
	public static Integer COUNT() {
		final String s = DEBUG.findCallingClassName();
		Integer result = -1;
		if (COUNTERS.containsKey(s)) {
			result = COUNTERS.get(s) + 1;
		} else {
			result = 0;
		}
		COUNTERS.put(s, result);
		return result;
	}

}
