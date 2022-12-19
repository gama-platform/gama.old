/*******************************************************************************************************
 *
 * COUNTER.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.dev.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Class COUNTER. A simple way to get unique indexes for various objects
 */
public class COUNTER {

	/** The count. */
	private static AtomicInteger COUNT = new AtomicInteger();

	/**
	 * Gets the.
	 *
	 * @return the int
	 */
	public static int GET() {
		return COUNT.incrementAndGet();
	}

}
