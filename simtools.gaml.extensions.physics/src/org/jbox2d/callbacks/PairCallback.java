/*******************************************************************************************************
 *
 * PairCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.callbacks;

/**
 * The Interface PairCallback.
 */
// updated to rev 100
public interface PairCallback {
	
	/**
	 * Adds the pair.
	 *
	 * @param userDataA the user data A
	 * @param userDataB the user data B
	 */
	public void addPair(Object userDataA, Object userDataB);
}
