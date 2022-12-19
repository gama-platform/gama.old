/*******************************************************************************************************
 *
 * CapsuleShapeZ.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

/**
 * CapsuleShapeZ represents a capsule around the Z axis.<p>
 * 
 * The total height is <code>height+2*radius</code>, so the height is just the
 * height between the center of each "sphere" of the capsule caps.
 * 
 * @author jezek2
 */
public class CapsuleShapeZ extends CapsuleShape {

	/**
	 * Instantiates a new capsule shape Z.
	 *
	 * @param radius the radius
	 * @param height the height
	 */
	public CapsuleShapeZ(float radius, float height) {
		upAxis = 2;
		implicitShapeDimensions.set(radius, radius, 0.5f * height);
	}
	
	@Override
	public String getName() {
		return "CapsuleZ";
	}

}
