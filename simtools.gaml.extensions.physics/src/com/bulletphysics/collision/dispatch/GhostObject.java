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

import static com.bulletphysics.Pools.QUATS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.linearmath.AabbUtil2;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.TransformUtil;

/**
 * GhostObject can keep track of all objects that are overlapping. By default, this overlap is based on the AABB. This
 * is useful for creating a character controller, collision sensors/triggers, explosions etc.
 *
 * @author tomrbryn
 */
public class GhostObject extends CollisionObject {

	protected ArrayList<CollisionObject> overlappingObjects = new ArrayList<>();

	public GhostObject() {
		this.internalType = CollisionObjectType.GHOST_OBJECT;
	}

	/**
	 * This method is mainly for expert/internal use only.
	 */
	public void addOverlappingObjectInternal(final BroadphaseProxy otherProxy, final BroadphaseProxy thisProxy) {
		CollisionObject otherObject = (CollisionObject) otherProxy.clientObject;
		assert otherObject != null;

		// if this linearSearch becomes too slow (too many overlapping objects) we should add a more appropriate data
		// structure
		int index = overlappingObjects.indexOf(otherObject);
		if (index == -1) {
			// not found
			overlappingObjects.add(otherObject);
		}
	}

	/**
	 * This method is mainly for expert/internal use only.
	 */
	public void removeOverlappingObjectInternal( final BroadphaseProxy otherProxy,
			final Dispatcher dispatcher, final BroadphaseProxy thisProxy) {
		CollisionObject otherObject = (CollisionObject) otherProxy.clientObject;
		assert otherObject != null;

		int index = overlappingObjects.indexOf(otherObject);
		if (index != -1) {
			overlappingObjects.set(index, overlappingObjects.get(overlappingObjects.size() - 1));
			overlappingObjects.remove(overlappingObjects.size() - 1);
		}
	}

	public void convexSweepTest( final ConvexShape castShape, final Transform convexFromWorld,
			final Transform convexToWorld, final CollisionWorld.ConvexResultCallback resultCallback,
			final float allowedCcdPenetration) {
		Transform convexFromTrans = TRANSFORMS.get();
		Transform convexToTrans = TRANSFORMS.get();

		convexFromTrans.set(convexFromWorld);
		convexToTrans.set(convexToWorld);

		Vector3f castShapeAabbMin = VECTORS.get();
		Vector3f castShapeAabbMax = VECTORS.get();

		// compute AABB that encompasses angular movement
		{
			Vector3f linVel = VECTORS.get();
			Vector3f angVel = VECTORS.get();
			TransformUtil.calculateVelocity(convexFromTrans, convexToTrans, 1f, linVel, angVel);
			Transform R = TRANSFORMS.get();
			R.setIdentity();
			Quat4f qmp = convexFromTrans.getRotation(QUATS.get());
			R.setRotation(qmp);
			castShape.calculateTemporalAabb(R, linVel, angVel, 1f, castShapeAabbMin, castShapeAabbMax);
			QUATS.release(qmp);
			TRANSFORMS.release(R);
			VECTORS.release(linVel, angVel);
		}

		Transform tmpTrans = TRANSFORMS.get();

		// go over all objects, and if the ray intersects their aabb + cast shape aabb,
		// do a ray-shape query using convexCaster (CCD)
		for (CollisionObject collisionObject : overlappingObjects) {
			// only perform raycast if filterMask matches
			if (resultCallback.needsCollision(collisionObject.getBroadphaseHandle())) {
				// RigidcollisionObject* collisionObject = ctrl->GetRigidcollisionObject();
				Vector3f collisionObjectAabbMin = VECTORS.get();
				Vector3f collisionObjectAabbMax = VECTORS.get();
				collisionObject.getCollisionShape().getAabb(collisionObject.getWorldTransform(tmpTrans),
						collisionObjectAabbMin, collisionObjectAabbMax);
				AabbUtil2.aabbExpand(collisionObjectAabbMin, collisionObjectAabbMax, castShapeAabbMin,
						castShapeAabbMax);
				float[] hitLambda = new float[] { 1f }; // could use resultCallback.closestHitFraction, but needs
														// testing
				Vector3f hitNormal = VECTORS.get();
				if (AabbUtil2.rayAabb(convexFromWorld.origin, convexToWorld.origin, collisionObjectAabbMin,
						collisionObjectAabbMax, hitLambda, hitNormal)) {
					CollisionWorld.objectQuerySingle( castShape, convexFromTrans, convexToTrans, collisionObject,
							collisionObject.getCollisionShape(), collisionObject.getWorldTransform(tmpTrans),
							resultCallback, allowedCcdPenetration);
				}
				VECTORS.release(collisionObjectAabbMin, collisionObjectAabbMax, hitNormal);
			}
		}
		TRANSFORMS.release(tmpTrans, convexFromTrans, convexToTrans);
		VECTORS.release(castShapeAabbMin, castShapeAabbMax);

	}

	public void rayTest( final Vector3f rayFromWorld, final Vector3f rayToWorld,
			final CollisionWorld.RayResultCallback resultCallback) {
		Transform rayFromTrans = TRANSFORMS.get();
		rayFromTrans.setIdentity();
		rayFromTrans.origin.set(rayFromWorld);
		Transform rayToTrans = TRANSFORMS.get();
		rayToTrans.setIdentity();
		rayToTrans.origin.set(rayToWorld);

		Transform tmpTrans = TRANSFORMS.get();

		for (CollisionObject collisionObject : overlappingObjects) {
			// only perform raycast if filterMask matches
			if (resultCallback.needsCollision(collisionObject.getBroadphaseHandle())) {
				CollisionWorld.rayTestSingle( rayFromTrans, rayToTrans, collisionObject,
						collisionObject.getCollisionShape(), collisionObject.getWorldTransform(tmpTrans),
						resultCallback);
			}
		}
		TRANSFORMS.release(tmpTrans, rayFromTrans, rayToTrans);
	}

	public int getNumOverlappingObjects() {
		return overlappingObjects.size();
	}

	public CollisionObject getOverlappingObject(final int index) {
		return overlappingObjects.get(index);
	}

	public ArrayList<CollisionObject> getOverlappingPairs() {
		return overlappingObjects;
	}

	//
	// internal cast
	//

	public static GhostObject upcast(final CollisionObject colObj) {
		if (colObj.getInternalType() == CollisionObjectType.GHOST_OBJECT) return (GhostObject) colObj;

		return null;
	}

}
