/*******************************************************************************************************
 *
 * DispatchFunc.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

/**
 * 
 * @author jezek2
 */
public enum DispatchFunc {

	/** The dispatch discrete. */
	DISPATCH_DISCRETE(1),
	
	/** The dispatch continuous. */
	DISPATCH_CONTINUOUS(2);
	
	/** The value. */
	private int value;
	
	/**
	 * Instantiates a new dispatch func.
	 *
	 * @param value the value
	 */
	private DispatchFunc(int value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	
}
