/*******************************************************************************************************
 *
 * ConvexCast.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.linearmath.Transform;

/**
 * ConvexCast is an interface for casting.
 *
 * @author jezek2
 */
@FunctionalInterface
public interface ConvexCast {

	/**
	 * Cast a convex against another convex object.
	 */
	boolean calcTimeOfImpact( Transform fromA, Transform toA, Transform fromB, Transform toB,
			CastResult result);

	////////////////////////////////////////////////////////////////////////////

	/**
	 * RayResult stores the closest result. Alternatively, add a callback method to decide about closest/all results.
	 */
	public static class CastResult {
		
		/** The hit transform A. */
		public final Transform hitTransformA = new Transform();
		
		/** The hit transform B. */
		public final Transform hitTransformB = new Transform();

		/** The normal. */
		public final Vector3f normal = new Vector3f();
		
		/** The hit point. */
		public final Vector3f hitPoint = new Vector3f();
		
		/** The fraction. */
		public float fraction = 1e30f; // input and output
		
		/** The allowed penetration. */
		public float allowedPenetration = 0f;

		/**
		 * Draw coord system.
		 *
		 * @param trans the trans
		 */
		public void drawCoordSystem(final Transform trans) {}
	}

}
