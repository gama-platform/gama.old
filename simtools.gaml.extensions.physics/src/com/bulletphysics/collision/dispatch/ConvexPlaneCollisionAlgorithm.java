/*******************************************************************************************************
 *
 * ConvexPlaneCollisionAlgorithm.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The own manifold. */
	private boolean ownManifold;
	
	/** The manifold ptr. */
	private PersistentManifold manifoldPtr;
	
	/** The is swapped. */
	private boolean isSwapped;

	/**
	 * Inits the.
	 *
	 * @param mf the mf
	 * @param ci the ci
	 * @param col0 the col 0
	 * @param col1 the col 1
	 * @param isSwapped the is swapped
	 */
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

	/**
	 * The Class CreateFunc.
	 */
	public static class CreateFunc implements CollisionAlgorithmCreateFunc {

		/** The swapped. */
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
