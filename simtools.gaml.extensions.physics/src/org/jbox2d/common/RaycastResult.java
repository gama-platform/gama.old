/*******************************************************************************************************
 *
 * RaycastResult.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.common;

// updated to rev 100

/**
 * The Class RaycastResult.
 */
public class RaycastResult {
	
	/** The lambda. */
	public float lambda = 0.0f;
	
	/** The normal. */
	public final Vec2 normal = new Vec2();
	
	/**
	 * Sets the.
	 *
	 * @param argOther the arg other
	 * @return the raycast result
	 */
	public RaycastResult set(RaycastResult argOther){
		lambda = argOther.lambda;
		normal.set( argOther.normal);
		return this;
	}
}
