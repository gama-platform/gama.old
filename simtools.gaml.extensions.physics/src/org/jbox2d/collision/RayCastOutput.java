/*******************************************************************************************************
 *
 * RayCastOutput.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * Ray-cast output data. The ray hits at p1 + fraction * (p2 - p1), where p1 and p2
 * come from b2RayCastInput.
 */
public class RayCastOutput{
	
	/** The normal. */
	public final Vec2 normal;
	
	/** The fraction. */
	public float fraction;

	/**
	 * Instantiates a new ray cast output.
	 */
	public RayCastOutput(){
		normal = new Vec2();
		fraction = 0;
	}

	/**
	 * Sets the.
	 *
	 * @param rco the rco
	 */
	public void set(final RayCastOutput rco){
		normal.set(rco.normal);
		fraction = rco.fraction;
	}
};
