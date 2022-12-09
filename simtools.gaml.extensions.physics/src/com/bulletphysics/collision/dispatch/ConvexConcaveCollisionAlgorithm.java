/*******************************************************************************************************
 *
 * ConvexConcaveCollisionAlgorithm.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import static com.bulletphysics.Pools.CONVEX_CONCAVE_COLLISIONS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;
import com.bulletphysics.collision.narrowphase.ConvexCast.CastResult;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.narrowphase.SubsimplexConvexCast;
import com.bulletphysics.collision.narrowphase.VoronoiSimplexSolver;
import com.bulletphysics.collision.shapes.ConcaveShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.TriangleCallback;
import com.bulletphysics.collision.shapes.TriangleShape;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * ConvexConcaveCollisionAlgorithm supports collision between convex shapes and (concave) trianges meshes.
 *
 * @author jezek2
 */
public class ConvexConcaveCollisionAlgorithm extends CollisionAlgorithm {

	/** The is swapped. */
	private boolean isSwapped;
	
	/** The bt convex triangle callback. */
	private ConvexTriangleCallback btConvexTriangleCallback;

	/**
	 * Inits the.
	 *
	 * @param ci the ci
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param isSwapped the is swapped
	 */
	public void init(final CollisionAlgorithmConstructionInfo ci, final CollisionObject body0,
			final CollisionObject body1, final boolean isSwapped) {
		super.init(ci);
		this.isSwapped = isSwapped;
		this.btConvexTriangleCallback = new ConvexTriangleCallback(dispatcher, body0, body1, isSwapped);
	}

	@Override
	public void destroy() {
		btConvexTriangleCallback.destroy();
	}

	@Override
	public void processCollision(final CollisionObject body0, final CollisionObject body1,
			final ManifoldResult resultOut) {
		CollisionObject convexBody = isSwapped ? body1 : body0;
		CollisionObject triBody = isSwapped ? body0 : body1;

		if (triBody.getCollisionShape().isConcave()) {
			CollisionObject triOb = triBody;
			ConcaveShape concaveShape = (ConcaveShape) triOb.getCollisionShape();

			if (convexBody.getCollisionShape().isConvex()) {
				float collisionMarginTriangle = concaveShape.getMargin();

				resultOut.setPersistentManifold(btConvexTriangleCallback.manifoldPtr);
				btConvexTriangleCallback.setTimeStepAndCounters(collisionMarginTriangle, resultOut);

				// Disable persistency. previously, some older algorithm calculated all contacts in one go, so you can
				// clear it here.
				// m_dispatcher->clearManifold(m_btConvexTriangleCallback.m_manifoldPtr);

				btConvexTriangleCallback.manifoldPtr.setBodies(convexBody, triBody);
				Vector3f min = btConvexTriangleCallback.getAabbMin(VECTORS.get());
				Vector3f max = btConvexTriangleCallback.getAabbMax(VECTORS.get());
				concaveShape.processAllTriangles(btConvexTriangleCallback, min, max);
				VECTORS.release(min, max);
				resultOut.refreshContactPoints();
			}
		}
	}

	@Override
	public float calculateTimeOfImpact(final CollisionObject body0, final CollisionObject body1,
			final ManifoldResult resultOut) {
		Vector3f tmp = VECTORS.get();

		CollisionObject convexbody = isSwapped ? body1 : body0;
		CollisionObject triBody = isSwapped ? body0 : body1;

		// quick approximation using raycast, todo: hook up to the continuous collision detection (one of the
		// btConvexCast)

		// only perform CCD above a certain threshold, this prevents blocking on the long run
		// because object in a blocked ccd state (hitfraction<1) get their linear velocity halved each frame...

		Transform tmp1 = convexbody.getInterpolationWorldTransform(TRANSFORMS.get());
		Transform tmp2 = convexbody.getWorldTransform(TRANSFORMS.get());

		tmp.sub(tmp1.origin, tmp2.origin);
		float squareMot0 = tmp.lengthSquared();
		if (squareMot0 < convexbody.getCcdSquareMotionThreshold()) return 1f;

		Transform tmpTrans = TRANSFORMS.get();

		// const btVector3& from = convexbody->m_worldTransform.getOrigin();
		// btVector3 to = convexbody->m_interpolationWorldTransform.getOrigin();
		// todo: only do if the motion exceeds the 'radius'

		Transform triInv = triBody.getWorldTransform(TRANSFORMS.get());
		triInv.inverse();

		Transform convexFromLocal = TRANSFORMS.get();
		convexFromLocal.mul(triInv, convexbody.getWorldTransform(tmpTrans));

		Transform convexToLocal = TRANSFORMS.get();
		convexToLocal.mul(triInv, convexbody.getInterpolationWorldTransform(tmpTrans));

		if (triBody.getCollisionShape().isConcave()) {
			Vector3f rayAabbMin = VECTORS.get(convexFromLocal.origin);
			VectorUtil.setMin(rayAabbMin, convexToLocal.origin);

			Vector3f rayAabbMax = VECTORS.get(convexFromLocal.origin);
			VectorUtil.setMax(rayAabbMax, convexToLocal.origin);

			float ccdRadius0 = convexbody.getCcdSweptSphereRadius();

			tmp.set(ccdRadius0, ccdRadius0, ccdRadius0);
			rayAabbMin.sub(tmp);
			rayAabbMax.add(tmp);

			float curHitFraction = 1f; // is this available?
			LocalTriangleSphereCastCallback raycastCallback = new LocalTriangleSphereCastCallback(convexFromLocal,
					convexToLocal, convexbody.getCcdSweptSphereRadius(), curHitFraction);

			raycastCallback.hitFraction = convexbody.getHitFraction();

			CollisionObject concavebody = triBody;

			ConcaveShape triangleMesh = (ConcaveShape) concavebody.getCollisionShape();

			if (triangleMesh != null) { triangleMesh.processAllTriangles(raycastCallback, rayAabbMin, rayAabbMax); }
			VECTORS.release(rayAabbMin, rayAabbMax);
			if (raycastCallback.hitFraction < convexbody.getHitFraction()) {
				convexbody.setHitFraction(raycastCallback.hitFraction);
				return raycastCallback.hitFraction;
			}
		}
		VECTORS.release(tmp);
		TRANSFORMS.release(convexToLocal, convexFromLocal, tmp1, tmp2, tmpTrans, triInv);
		return 1f;
	}

	@Override
	public void getAllContactManifolds(final ArrayList<PersistentManifold> manifoldArray) {
		if (btConvexTriangleCallback.manifoldPtr != null) { manifoldArray.add(btConvexTriangleCallback.manifoldPtr); }
	}

	// public void clearCache() {
	// btConvexTriangleCallback.clearCache();
	// }

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class LocalTriangleSphereCastCallback.
	 */
	private static class LocalTriangleSphereCastCallback implements TriangleCallback {
		
		/** The ccd sphere from trans. */
		public final Transform ccdSphereFromTrans = new Transform();
		
		/** The ccd sphere to trans. */
		public final Transform ccdSphereToTrans = new Transform();
		// public final Transform meshTransform = new Transform();

		/** The ccd sphere radius. */
		public float ccdSphereRadius;
		
		/** The hit fraction. */
		public float hitFraction;

		/** The ident. */
		private final Transform ident = new Transform();

		/**
		 * Instantiates a new local triangle sphere cast callback.
		 *
		 * @param from the from
		 * @param to the to
		 * @param ccdSphereRadius the ccd sphere radius
		 * @param hitFraction the hit fraction
		 */
		public LocalTriangleSphereCastCallback(final Transform from, final Transform to, final float ccdSphereRadius,
				final float hitFraction) {
			this.ccdSphereFromTrans.set(from);
			this.ccdSphereToTrans.set(to);
			this.ccdSphereRadius = ccdSphereRadius;
			this.hitFraction = hitFraction;

			// JAVA NOTE: moved here from processTriangle
			ident.setIdentity();
		}

		@Override
		public void processTriangle(final Vector3f[] triangle, final int partId, final int triangleIndex) {
			// do a swept sphere for now

			// btTransform ident;
			// ident.setIdentity();

			CastResult castResult = new CastResult();
			castResult.fraction = hitFraction;
			SphereShape pointShape = new SphereShape(ccdSphereRadius);
			TriangleShape triShape = new TriangleShape(triangle[0], triangle[1], triangle[2]);
			VoronoiSimplexSolver simplexSolver = new VoronoiSimplexSolver();
			SubsimplexConvexCast convexCaster = new SubsimplexConvexCast(pointShape, triShape, simplexSolver);
			// GjkConvexCast convexCaster(&pointShape,convexShape,&simplexSolver);
			// ContinuousConvexCollision convexCaster(&pointShape,convexShape,&simplexSolver,0);
			// local space?

			if (convexCaster.calcTimeOfImpact(ccdSphereFromTrans, ccdSphereToTrans, ident, ident, castResult)) {
				if (hitFraction > castResult.fraction) { hitFraction = castResult.fraction; }
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class CreateFunc.
	 */
	public static class CreateFunc implements CollisionAlgorithmCreateFunc {

		@Override
		public CollisionAlgorithm createCollisionAlgorithm(final CollisionAlgorithmConstructionInfo ci,
				final CollisionObject body0, final CollisionObject body1) {
			ConvexConcaveCollisionAlgorithm algo = CONVEX_CONCAVE_COLLISIONS.get();
			algo.init(ci, body0, body1, false);
			return algo;
		}

		@Override
		public void releaseCollisionAlgorithm(final CollisionAlgorithm algo) {
			CONVEX_CONCAVE_COLLISIONS.release((ConvexConcaveCollisionAlgorithm) algo);
		}
	}

	/**
	 * The Class SwappedCreateFunc.
	 */
	public static class SwappedCreateFunc implements CollisionAlgorithmCreateFunc {

		@Override
		public CollisionAlgorithm createCollisionAlgorithm(final CollisionAlgorithmConstructionInfo ci,
				final CollisionObject body0, final CollisionObject body1) {
			ConvexConcaveCollisionAlgorithm algo = CONVEX_CONCAVE_COLLISIONS.get();
			algo.init(ci, body0, body1, true);
			return algo;
		}

		@Override
		public void releaseCollisionAlgorithm(final CollisionAlgorithm algo) {
			CONVEX_CONCAVE_COLLISIONS.release((ConvexConcaveCollisionAlgorithm) algo);
		}
	}

}
