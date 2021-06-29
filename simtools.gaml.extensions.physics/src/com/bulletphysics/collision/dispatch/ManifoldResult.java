/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

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

	private PersistentManifold manifoldPtr;

	// we need this for compounds
	private final Transform rootTransA = new Transform();
	private final Transform rootTransB = new Transform();
	private CollisionObject body0;
	private CollisionObject body1;
	private int partId0;
	private int partId1;
	private int index0;
	private int index1;

	public ManifoldResult() {}

	public ManifoldResult(final CollisionObject body0, final CollisionObject body1) {
		init(body0, body1);
	}

	public void init(final CollisionObject body0, final CollisionObject body1) {
		this.body0 = body0;
		this.body1 = body1;
		body0.getWorldTransform(this.rootTransA);
		body1.getWorldTransform(this.rootTransB);
	}

	public PersistentManifold getPersistentManifold() {
		return manifoldPtr;
	}

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
	/// body0->m_collisionFlags |= btCollisionObject::customMaterialCallback;
	private static float calculateCombinedFriction(final CollisionObject body0, final CollisionObject body1) {
		float friction = body0.getFriction() * body1.getFriction();

		float MAX_FRICTION = 10f;
		if (friction < -MAX_FRICTION) { friction = -MAX_FRICTION; }
		if (friction > MAX_FRICTION) { friction = MAX_FRICTION; }
		return friction;
	}

	private static float calculateCombinedRestitution(final CollisionObject body0, final CollisionObject body1) {
		return body0.getRestitution() * body1.getRestitution();
	}

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
