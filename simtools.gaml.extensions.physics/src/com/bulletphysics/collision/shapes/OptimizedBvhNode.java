/*******************************************************************************************************
 *
 * OptimizedBvhNode.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import java.io.Serializable;
import javax.vecmath.Vector3f;

/**
 * OptimizedBvhNode contains both internal and leaf node information.
 * 
 * @author jezek2
 */
public class OptimizedBvhNode implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The aabb min org. */
	public final Vector3f aabbMinOrg = new Vector3f();
	
	/** The aabb max org. */
	public final Vector3f aabbMaxOrg = new Vector3f();

	/** The escape index. */
	public int escapeIndex;

	/** The sub part. */
	// for child nodes
	public int subPart;
	
	/** The triangle index. */
	public int triangleIndex;
	
	/**
	 * Sets the.
	 *
	 * @param n the n
	 */
	public void set(OptimizedBvhNode n) {
		aabbMinOrg.set(n.aabbMinOrg);
		aabbMaxOrg.set(n.aabbMaxOrg);
		escapeIndex = n.escapeIndex;
		subPart = n.subPart;
		triangleIndex = n.triangleIndex;
	}

}
