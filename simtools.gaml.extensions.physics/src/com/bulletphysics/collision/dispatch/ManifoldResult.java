/*******************************************************************************************************
 *
 * ManifoldResult.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import static com.bulletphysics.Pools.MANIFOLD_POINTS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.narrowphase.DiscreteCollisionDetectorInterface;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.linearmath.Transform;

/**
 * ManifoldResult is helper class to manage contact results.
 *
 * @author jezek2
 */
public class ManifoldResult implements DiscreteCollisionDetectorInterface.Result {

	/** The manifold ptr. */
	private PersistentManifold manifoldPtr;

	/** The root trans A. */
	// we need this for compounds
	private final Transform rootTransA = new Transform();
	
	/** The root trans B. */
	private final Transform rootTransB = new Transform();
	
	/** The body 0. */
	private CollisionObject body0;
	
	/** The body 1. */
	private CollisionObject body1;
	
	/** The part id 0. */
	private int partId0;
	
	/** The part id 1. */
	private int partId1;
	
	/** The index 0. */
	private int index0;
	
	/** The index 1. */
	private int index1;

	/**
	 * Instantiates a new manifold result.
	 */
	public ManifoldResult() {}

	/**
	 * Instantiates a new manifold result.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 */
	public ManifoldResult(final CollisionObject body0, final CollisionObject body1) {
		init(body0, body1);
	}

	/**
	 * Inits the.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 */
	public void init(final CollisionObject body0, final CollisionObject body1) {
		this.body0 = body0;
		this.body1 = body1;
		body0.getWorldTransform(this.rootTransA);
		body1.getWorldTransform(this.rootTransB);
	}

	/**
	 * Gets the persistent manifold.
	 *
	 * @return the persistent manifold
	 */
	public PersistentManifold getPersistentManifold() {
		return manifoldPtr;
	}

	/**
	 * Sets the persistent manifold.
	 *
	 * @param manifoldPtr the new persistent manifold
	 */
	public void setPersistentManifold(final PersistentManifold manifoldPtr) {
		this.manifoldPtr = manifoldPtr;
	}

	@Override
	public void setShapeIdentifiers(final int partId0, final int index0, final int partId1, final int index1) {
		this.partId0 = partId0;
		this.partId1 = partId1;
		this.index0 = index0;
		this.index1 = index1;
	}

	@Override
	public void addContactPoint(final Vector3f normalOnBInWorld, final Vector3f pointInWorld, final float depth) {
		assert manifoldPtr != null;
		// order in manifold needs to match

		if (depth > manifoldPtr.getContactBreakingThreshold()) return;

		boolean isSwapped = manifoldPtr.getBody0() != body0;

		Vector3f pointA = VECTORS.get();
		pointA.scaleAdd(depth, normalOnBInWorld, pointInWorld);

		Vector3f localA = VECTORS.get();
		Vector3f localB = VECTORS.get();

		if (isSwapped) {
			rootTransB.invXform(pointA, localA);
			rootTransA.invXform(pointInWorld, localB);
		} else {
			rootTransA.invXform(pointA, localA);
			rootTransB.invXform(pointInWorld, localB);
		}

		ManifoldPoint newPt = MANIFOLD_POINTS.get();
		newPt.init(localA, localB, normalOnBInWorld, depth);

		newPt.positionWorldOnA.set(pointA);
		newPt.positionWorldOnB.set(pointInWorld);

		int insertIndex = manifoldPtr.getCacheEntry(newPt);

		newPt.combinedFriction = calculateCombinedFriction(body0, body1);
		newPt.combinedRestitution = calculateCombinedRestitution(body0, body1);

		// BP mod, store contact triangles.
		newPt.partId0 = partId0;
		newPt.partId1 = partId1;
		newPt.index0 = index0;
		newPt.index1 = index1;

		/// todo, check this for any side effects
		if (insertIndex >= 0) {
			// const btManifoldPoint& oldPoint = m_manifoldPtr->getContactPoint(insertIndex);
			manifoldPtr.replaceContactPoint(newPt, insertIndex);
		} else {
			insertIndex = manifoldPtr.addManifoldPoint(newPt);
		}

		// User can override friction and/or restitution
		// if (info.world.getContactAddedCallback() != null &&
		// // and if either of the two bodies requires custom material
		// ((body0.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0
		// || (body1.getCollisionFlags() & CollisionFlags.CUSTOM_MATERIAL_CALLBACK) != 0)) {
		// // experimental feature info, for per-triangle material etc.
		// CollisionObject obj0 = isSwapped ? body1 : body0;
		// CollisionObject obj1 = isSwapped ? body0 : body1;
		// info.world.getContactAddedCallback().contactAdded(manifoldPtr.getContactPoint(insertIndex), obj0, partId0,
		// index0, obj1, partId1, index1);
		// }

		MANIFOLD_POINTS.release(newPt);
	}

	/// User can override this material combiner by implementing gContactAddedCallback and setting
	/**
	 * Calculate combined friction.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @return the float
	 */
	/// body0->m_collisionFlags |= btCollisionObject::customMaterialCallback;
	private static float calculateCombinedFriction(final CollisionObject body0, final CollisionObject body1) {
		float friction = body0.getFriction() * body1.getFriction();

		float MAX_FRICTION = 10f;
		if (friction < -MAX_FRICTION) { friction = -MAX_FRICTION; }
		if (friction > MAX_FRICTION) { friction = MAX_FRICTION; }
		return friction;
	}

	/**
	 * Calculate combined restitution.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @return the float
	 */
	private static float calculateCombinedRestitution(final CollisionObject body0, final CollisionObject body1) {
		return body0.getRestitution() * body1.getRestitution();
	}

	/**
	 * Refresh contact points.
	 */
	public void refreshContactPoints() {
		assert manifoldPtr != null;
		if (manifoldPtr.getNumContacts() == 0) return;

		boolean isSwapped = manifoldPtr.getBody0() != body0;

		if (isSwapped) {
			manifoldPtr.refreshContactPoints(rootTransB, rootTransA);
		} else {
			manifoldPtr.refreshContactPoints(rootTransA, rootTransB);
		}
	}
}
