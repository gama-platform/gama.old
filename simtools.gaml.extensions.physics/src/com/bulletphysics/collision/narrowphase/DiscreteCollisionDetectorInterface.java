/*******************************************************************************************************
 *
 * DiscreteCollisionDetectorInterface.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

/**
 * This interface is made to be used by an iterative approach to do TimeOfImpact calculations.
 * <p>
 *
 * This interface allows to query for closest points and penetration depth between two (convex) objects the closest
 * point is on the second object (B), and the normal points from the surface on B towards A. distance is between closest
 * points on B and closest point on A. So you can calculate closest point on A by taking
 * <code>closestPointInA = closestPointInB + distance * normalOnSurfaceB</code>.
 *
 * @author jezek2
 */
@FunctionalInterface
public interface DiscreteCollisionDetectorInterface {

	/**
	 * The Interface Result.
	 */
	public interface Result {
		
		/**
		 * Sets the shape identifiers.
		 *
		 * @param partId0 the part id 0
		 * @param index0 the index 0
		 * @param partId1 the part id 1
		 * @param index1 the index 1
		 */
		/// setShapeIdentifiers provides experimental support for per-triangle material / custom material combiner
		void setShapeIdentifiers(int partId0, int index0, int partId1, int index1);

		/**
		 * Adds the contact point.
		 *
		 * @param normalOnBInWorld the normal on B in world
		 * @param pointInWorld the point in world
		 * @param depth the depth
		 */
		void addContactPoint(Vector3f normalOnBInWorld, Vector3f pointInWorld, float depth);
	}

	/**
	 * The Class ClosestPointInput.
	 */
	public static class ClosestPointInput {
		
		/** The transform A. */
		public final Transform transformA = new Transform();
		
		/** The transform B. */
		public final Transform transformB = new Transform();
		
		/** The maximum distance squared. */
		public float maximumDistanceSquared;

		/**
		 * Instantiates a new closest point input.
		 */
		public ClosestPointInput() {
			init();
		}

		/**
		 * Inits the.
		 */
		public void init() {
			maximumDistanceSquared = Float.MAX_VALUE;
		}
	}

	/**
	 * Give either closest points (distance > 0) or penetration (distance) the normal always points from B towards A.
	 */
	default void getClosestPoints(final ClosestPointInput input, final Result output) {
		getClosestPoints(input, output, false);
	}

	/**
	 * Give either closest points (distance > 0) or penetration (distance) the normal always points from B towards A.
	 */
	void getClosestPoints(ClosestPointInput input, Result output, boolean swapResults);

}
