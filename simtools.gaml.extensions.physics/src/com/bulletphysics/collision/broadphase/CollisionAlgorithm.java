/*******************************************************************************************************
 *
 * CollisionAlgorithm.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

import java.util.ArrayList;

import com.bulletphysics.collision.dispatch.CollisionAlgorithmCreateFunc;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.ManifoldResult;
import com.bulletphysics.collision.narrowphase.PersistentManifold;

/**
 * Collision algorithm for handling narrowphase or midphase collision detection between two collision object types.
 *
 * @author jezek2
 */
public abstract class CollisionAlgorithm {

	// protected final BulletStack stack = BulletStack.get();

	/** The create func. */
	// JAVA NOTE: added
	private CollisionAlgorithmCreateFunc createFunc;

	/** The dispatcher. */
	protected Dispatcher dispatcher;

	/**
	 * Inits the.
	 */
	public void init() {}

	/**
	 * Inits the.
	 *
	 * @param ci the ci
	 */
	public void init(final CollisionAlgorithmConstructionInfo ci) {
		dispatcher = ci.dispatcher1;
	}

	/**
	 * Destroy.
	 */
	public abstract void destroy();

	/**
	 * Process collision.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param resultOut the result out
	 */
	public abstract void processCollision(CollisionObject body0, CollisionObject body1, ManifoldResult resultOut);

	/**
	 * Calculate time of impact.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param resultOut the result out
	 * @return the float
	 */
	public abstract float calculateTimeOfImpact(CollisionObject body0, CollisionObject body1, ManifoldResult resultOut);

	/**
	 * Gets the all contact manifolds.
	 *
	 * @param manifoldArray the manifold array
	 * @return the all contact manifolds
	 */
	public abstract void getAllContactManifolds(ArrayList<PersistentManifold> manifoldArray);

	/**
	 * Internal set create func.
	 *
	 * @param func the func
	 */
	public final void internalSetCreateFunc(final CollisionAlgorithmCreateFunc func) {
		createFunc = func;
	}

	/**
	 * Internal get create func.
	 *
	 * @return the collision algorithm create func
	 */
	public final CollisionAlgorithmCreateFunc internalGetCreateFunc() {
		return createFunc;
	}

}
