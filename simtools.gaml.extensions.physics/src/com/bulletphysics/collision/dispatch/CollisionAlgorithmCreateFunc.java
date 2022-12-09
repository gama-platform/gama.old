/*******************************************************************************************************
 *
 * CollisionAlgorithmCreateFunc.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;

/**
 * Used by the CollisionDispatcher to register and create instances for CollisionAlgorithm.
 *
 * @author jezek2
 */
public interface CollisionAlgorithmCreateFunc {

	/**
	 * Creates the collision algorithm.
	 *
	 * @param ci the ci
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @return the collision algorithm
	 */
	CollisionAlgorithm createCollisionAlgorithm(CollisionAlgorithmConstructionInfo ci, CollisionObject body0,
			CollisionObject body1);

	/**
	 * Release collision algorithm.
	 *
	 * @param algo the algo
	 */
	// JAVA NOTE: added
	void releaseCollisionAlgorithm(CollisionAlgorithm algo);

}
