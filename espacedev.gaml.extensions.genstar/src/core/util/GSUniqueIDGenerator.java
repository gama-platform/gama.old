/*******************************************************************************************************
 *
 * GSUniqueIDGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package core.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Key id value generator encapsulating {@link AtomicInteger}
 *
 * @author kevinchapuis
 *
 */
public class GSUniqueIDGenerator {

	/** The idgen. */
	private static GSUniqueIDGenerator idgen;

	/** The ai. */
	private AtomicInteger ai;

	/**
	 * Instantiates a new GS unique ID generator.
	 */
	private GSUniqueIDGenerator() {
		this.ai = new AtomicInteger();
	}

	/**
	 * Access to singleton instance
	 *
	 * @return
	 */
	public static GSUniqueIDGenerator getInstance() {
		if (idgen == null) { idgen = new GSUniqueIDGenerator(); }
		return idgen;
	}

	/**
	 * Get next unique ID
	 *
	 * @return
	 */
	public int getNextID() { return ai.getAndIncrement(); }

	/**
	 * Reset the id generator to default initial value
	 */
	public void reset() {
		this.ai = new AtomicInteger();
	}

	/**
	 * Reset the id generator to {@code initValue}
	 *
	 * @param initValue
	 */
	public void reset(final int initValue) {
		this.ai = new AtomicInteger(initValue);
	}

}
