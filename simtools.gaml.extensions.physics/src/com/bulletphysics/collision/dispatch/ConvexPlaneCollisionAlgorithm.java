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

import static com.bulletphysics.Pools.CONVEX_PLANE_COLLISIONS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.linearmath.Transform;

/**
 * ConvexPlaneCollisionAlgorithm provides convex/plane collision detection.
 *
 * @author jezek2
 */
public class ConvexPlaneCollisionAlgorithm extends CollisionAlgorithm {

	private boolean ownManifold;
	private PersistentManifold manifoldPtr;
	private boolean isSwapped;

	public void init(final PersistentManifold mf, final CollisionAlgorithmConstructionInfo ci,
			final CollisionObject col0, final CollisionObject col1, final boolean isSwapped) {
		super.init(ci);
		this.ownManifold = false;
		this.manifoldPtr = mf;
		this.isSwapped = isSwapped;

		CollisionObject convexObj = isSwapped ? col1 : col0;
		CollisionObject planeObj = isSwapped ? col0 : col1;

		if (manifoldPtr == null && dispatcher.needsCollision(convexObj, planeObj)) {
			manifoldPtr = dispatcher.getNewManifold(convexObj, planeObj);
			ownManifold = true;
		}
	}

	@Override
	public void destroy() {
		if (ownManifold) {
			if (manifoldPtr != null) { dispatcher.releaseManifold( manifoldPtr); }
			manifoldPtr = null;
		}
	}

	@Override
	public void processCollision( final CollisionObject body0, final CollisionObject body1,
			final ManifoldResult resultOut) {
		if (manifoldPtr == null) return;

		Transform tmpTrans = TRANSFORMS.get();

		CollisionObject convexObj = isSwapped ? body1 : body0;
		CollisionObject planeObj = isSwapped ? body0 : body1;

		ConvexShape convexShape = (ConvexShape) convexObj.getCollisionShape();
		StaticPlaneShape planeShape = (StaticPlaneShape) planeObj.getCollisionShape();

		boolean hasCollision = false;
		Vector3f planeNormal = planeShape.getPlaneNormal(VECTORS.get());
		float planeConstant = planeShape.getPlaneConstant();

		Transform planeInConvex = TRANSFORMS.get();
		convexObj.getWorldTransform(planeInConvex);
		planeInConvex.inverse();
		planeInConvex.mul(planeObj.getWorldTransform(tmpTrans));

		Transform convexInPlaneTrans = TRANSFORMS.get();
		convexInPlaneTrans.inverse(planeObj.getWorldTransform(tmpTrans));
		convexInPlaneTrans.mul(convexObj.getWorldTransform(tmpTrans));

		Vector3f tmp = VECTORS.get();
		tmp.negate(planeNormal);
		planeInConvex.basis.transform(tmp);

		Vector3f vtx = convexShape.localGetSupportingVertex(tmp, VECTORS.get());
		Vector3f vtxInPlane = VECTORS.get(vtx);
		convexInPlaneTrans.transform(vtxInPlane);

		float distance = planeNormal.dot(vtxInPlane) - planeConstant;

		Vector3f vtxInPlaneProjected = VECTORS.get();
		tmp.scale(distance, planeNormal);
		vtxInPlaneProjected.sub(vtxInPlane, tmp);

		Vector3f vtxInPlaneWorld = VECTORS.get(vtxInPlaneProjected);
		planeObj.getWorldTransform(tmpTrans).transform(vtxInPlaneWorld);

		hasCollision = distance < manifoldPtr.getContactBreakingThreshold();
		resultOut.setPersistentManifold(manifoldPtr);
		if (hasCollision) {
			// report a contact. internally this will be kept persistent, and contact reduction is done
			Vector3f normalOnSurfaceB = VECTORS.get(planeNormal);
			planeObj.getWorldTransform(tmpTrans).basis.transform(normalOnSurfaceB);

			Vector3f pOnB = VECTORS.get(vtxInPlaneWorld);
			resultOut.addContactPoint( normalOnSurfaceB, pOnB, distance);
			VECTORS.release(pOnB, normalOnSurfaceB);
		}
		VECTORS.release(tmp, vtx, vtxInPlane, vtxInPlaneProjected, vtxInPlaneWorld, planeNormal);
		TRANSFORMS.release(tmpTrans, planeInConvex, convexInPlaneTrans);
		if (ownManifold) { if (manifoldPtr.getNumContacts() != 0) { resultOut.refreshContactPoints(); } }
	}

	@Override
	public float calculateTimeOfImpact( final CollisionObject body0,
			final CollisionObject body1, final ManifoldResult resultOut) {
		// not yet
		return 1f;
	}

	@Override
	public void getAllContactManifolds(final ArrayList<PersistentManifold> manifoldArray) {
		if (manifoldPtr != null && ownManifold) { manifoldArray.add(manifoldPtr); }
	}

	////////////////////////////////////////////////////////////////////////////

	public static class CreateFunc implements CollisionAlgorithmCreateFunc {

		boolean swapped;

		@Override
		public CollisionAlgorithm createCollisionAlgorithm(final CollisionAlgorithmConstructionInfo ci,
				final CollisionObject body0, final CollisionObject body1) {
			ConvexPlaneCollisionAlgorithm algo = CONVEX_PLANE_COLLISIONS.get();
			if (!swapped) {
				algo.init(null, ci, body0, body1, false);
			} else {
				algo.init(null, ci, body0, body1, true);
			}
			return algo;
		}

		@Override
		public void releaseCollisionAlgorithm(final CollisionAlgorithm algo) {
			CONVEX_PLANE_COLLISIONS.release((ConvexPlaneCollisionAlgorithm) algo);
		}
	}

}
