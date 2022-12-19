/*******************************************************************************************************
 *
 * Pair.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

/**
 * Overlapping pair.
 * 
 * @author jezek2
 */
class Pair {

	/** The index 1. */
	public int index1;
	
	/** The index 2. */
	public int index2;

	/**
	 * Instantiates a new pair.
	 */
	public Pair() {
	}

	/**
	 * Instantiates a new pair.
	 *
	 * @param index1 the index 1
	 * @param index2 the index 2
	 */
	public Pair(int index1, int index2) {
		this.index1 = index1;
		this.index2 = index2;
	}

	/**
	 * Instantiates a new pair.
	 *
	 * @param p the p
	 */
	public Pair(Pair p) {
		index1 = p.index1;
		index2 = p.index2;
	}

}
