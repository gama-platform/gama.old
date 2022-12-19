/*******************************************************************************************************
 *
 * CapsuleShapeX.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

/**
 * CapsuleShapeX represents a capsule around the X axis.<p>
 * 
 * The total height is <code>height+2*radius</code>, so the height is just the
 * height between the center of each "sphere" of the capsule caps.
 * 
 * @author jezek2
 */
public class CapsuleShapeX extends CapsuleShape {

	/**
	 * Instantiates a new capsule shape X.
	 *
	 * @param radius the radius
	 * @param height the height
	 */
	public CapsuleShapeX(float radius, float height) {
		upAxis = 0;
		implicitShapeDimensions.set(0.5f * height, radius, radius);
	}
	
	@Override
	public String getName() {
		return "CapsuleX";
	}

}
