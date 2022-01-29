/*******************************************************************************************************
 *
 * RayCastInput.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.collision;

import org.jbox2d.common.Vec2;

// updated to rev 100
/**
 * Ray-cast input data. The ray extends from p1 to p1 + maxFraction * (p2 - p1).
 */
public class RayCastInput{
	
	/** The p 2. */
	public final Vec2 p1, p2;
	
	/** The max fraction. */
	public float maxFraction;

	/**
	 * Instantiates a new ray cast input.
	 */
	public RayCastInput(){
		p1 = new Vec2();
		p2 = new Vec2();
		maxFraction = 0;
	}

	/**
	 * Sets the.
	 *
	 * @param rci the rci
	 */
	public void set(final RayCastInput rci){
		p1.set(rci.p1);
		p2.set(rci.p2);
		maxFraction = rci.maxFraction;
	}
}
