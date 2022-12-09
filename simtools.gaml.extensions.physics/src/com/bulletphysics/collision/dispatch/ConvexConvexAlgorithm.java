/*******************************************************************************************************
 *
 * ConvexConvexAlgorithm.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import static com.bulletphysics.Pools.CLOSEST_POINTS;
import static com.bulletphysics.Pools.CONVEX_CONVEX_COLLISIONS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.narrowphase.ConvexCast;
import com.bulletphysics.collision.narrowphase.ConvexPenetrationDepthSolver;
import com.bulletphysics.collision.narrowphase.DiscreteCollisionDetectorInterface.ClosestPointInput;
import com.bulletphysics.collision.narrowphase.GjkConvexCast;
import com.bulletphysics.collision.narrowphase.GjkPairDetector;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.narrowphase.SimplexSolverInterface;
import com.bulletphysics.collision.narrowphase.VoronoiSimplexSolver;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

/**
 * ConvexConvexAlgorithm collision algorithm implements time of impact, convex closest points and penetration depth
 * calculations.
 *
 * @author jezek2
 */
public class ConvexConvexAlgorithm extends CollisionAlgorithm {

	/** The gjk pair detector. */
	private final GjkPairDetector gjkPairDetector = new GjkPairDetector();

	/** The own manifold. */
	public boolean ownManifold;
	
	/** The manifold ptr. */
	public PersistentManifold manifoldPtr;
	
	/** The low level of detail. */
	public boolean lowLevelOfDetail;

	/**
	 * Inits the.
	 *
	 * @param mf the mf
	 * @param ci the ci
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param simplexSolver the simplex solver
	 * @param pdSolver the pd solver
	 */
	public void init(final PersistentManifold mf, final CollisionAlgorithmConstructionInfo ci,
			final CollisionObject body0, final CollisionObject body1, final SimplexSolverInterface simplexSolver,
			final ConvexPenetrationDepthSolver pdSolver) {
		super.init(ci);
		gjkPairDetector.init(null, null, simplexSolver, pdSolver);
		this.manifoldPtr = mf;
		this.ownManifold = false;
		this.lowLevelOfDetail = false;
	}

	@Override
	public void destroy() {
		if (ownManifold) {
			if (manifoldPtr != null) { dispatcher.releaseManifold( manifoldPtr); }
			manifoldPtr = null;
		}
	}

	/**
	 * Sets the low level of detail.
	 *
	 * @param useLowLevel the new low level of detail
	 */
	public void setLowLevelOfDetail(final boolean useLowLevel) {
		this.lowLevelOfDetail = useLowLevel;
	}

	/**
	 * Convex-Convex collision algorithm.
	 */
	@Override
	public void processCollision( final CollisionObject body0, final CollisionObject body1,
			final ManifoldResult resultOut) {
		if (manifoldPtr == null) {
			// swapped?
			manifoldPtr = dispatcher.getNewManifold(body0, body1);
			ownManifold = true;
		}
		resultOut.setPersistentManifold(manifoldPtr);

		ConvexShape min0 = (ConvexShape) body0.getCollisionShape();
		ConvexShape min1 = (ConvexShape) body1.getCollisionShape();

		ClosestPointInput input = CLOSEST_POINTS.get();
		input.init();

		gjkPairDetector.setMinkowskiA(min0);
		gjkPairDetector.setMinkowskiB(min1);
		input.maximumDistanceSquared = min0.getMargin() + min1.getMargin() + manifoldPtr.getContactBreakingThreshold();
		input.maximumDistanceSquared *= input.maximumDistanceSquared;

		body0.getWorldTransform(input.transformA);
		body1.getWorldTransform(input.transformB);

		gjkPairDetector.getClosestPoints( input, resultOut);

		CLOSEST_POINTS.release(input);

		if (ownManifold) { resultOut.refreshContactPoints(); }
	}

	@Override
	public float calculateTimeOfImpact( final CollisionObject col0,
			final CollisionObject col1, final ManifoldResult resultOut) {
		Vector3f tmp = VECTORS.get();

		Transform tmpTrans1 = TRANSFORMS.get();
		Transform tmpTrans2 = TRANSFORMS.get();

		try {

			// Rather then checking ALL pairs, only calculate TOI when motion exceeds threshold

			// Linear motion for one of objects needs to exceed m_ccdSquareMotionThreshold
			// col0->m_worldTransform,
			float resultFraction = 1f;

			tmp.sub(col0.getInterpolationWorldTransform(tmpTrans1).origin, col0.getWorldTransform(tmpTrans2).origin);
			float squareMot0 = tmp.lengthSquared();

			tmp.sub(col1.getInterpolationWorldTransform(tmpTrans1).origin, col1.getWorldTransform(tmpTrans2).origin);
			float squareMot1 = tmp.lengthSquared();

			if (squareMot0 < col0.getCcdSquareMotionThreshold() && squareMot1 < col1.getCcdSquareMotionThreshold())
				return resultFraction;

			Transform tmpTrans3 = TRANSFORMS.get();
			Transform tmpTrans4 = TRANSFORMS.get();

			// An adhoc way of testing the Continuous Collision Detection algorithms
			// One object is approximated as a sphere, to simplify things
			// Starting in penetration should report no time of impact
			// For proper CCD, better accuracy and handling of 'allowed' penetration should be added
			// also the mainloop of the physics should have a kind of toi queue (something like Brian Mirtich's
			// application
			// of Timewarp for Rigidbodies)

			// Convex0 against sphere for Convex1
			{
				ConvexShape convex0 = (ConvexShape) col0.getCollisionShape();

				SphereShape sphere1 = new SphereShape(col1.getCcdSweptSphereRadius()); // todo: allow non-zero sphere
																						// sizes,
																						// for better approximation
				ConvexCast.CastResult result = new ConvexCast.CastResult();
				VoronoiSimplexSolver voronoiSimplex = new VoronoiSimplexSolver();
				// SubsimplexConvexCast ccd0(&sphere,min0,&voronoiSimplex);
				/// Simplification, one object is simplified as a sphere
				GjkConvexCast ccd1 = new GjkConvexCast(convex0, sphere1, voronoiSimplex);
				// ContinuousConvexCollision ccd(min0,min1,&voronoiSimplex,0);
				if (ccd1.calcTimeOfImpact( col0.getWorldTransform(tmpTrans1),
						col0.getInterpolationWorldTransform(tmpTrans2), col1.getWorldTransform(tmpTrans3),
						col1.getInterpolationWorldTransform(tmpTrans4), result)) {
					// store result.m_fraction in both bodies

					if (col0.getHitFraction() > result.fraction) { col0.setHitFraction(result.fraction); }

					if (col1.getHitFraction() > result.fraction) { col1.setHitFraction(result.fraction); }

					if (resultFraction > result.fraction) { resultFraction = result.fraction; }
				}
			}

			// Sphere (for convex0) against Convex1
			{
				ConvexShape convex1 = (ConvexShape) col1.getCollisionShape();

				SphereShape sphere0 = new SphereShape(col0.getCcdSweptSphereRadius()); // todo: allow non-zero sphere
																						// sizes,
																						// for better approximation
				ConvexCast.CastResult result = new ConvexCast.CastResult();
				VoronoiSimplexSolver voronoiSimplex = new VoronoiSimplexSolver();
				// SubsimplexConvexCast ccd0(&sphere,min0,&voronoiSimplex);
				/// Simplification, one object is simplified as a sphere
				GjkConvexCast ccd1 = new GjkConvexCast(sphere0, convex1, voronoiSimplex);
				// ContinuousConvexCollision ccd(min0,min1,&voronoiSimplex,0);
				if (ccd1.calcTimeOfImpact( col0.getWorldTransform(tmpTrans1),
						col0.getInterpolationWorldTransform(tmpTrans2), col1.getWorldTransform(tmpTrans3),
						col1.getInterpolationWorldTransform(tmpTrans4), result)) {
					// store result.m_fraction in both bodies

					if (col0.getHitFraction() > result.fraction) { col0.setHitFraction(result.fraction); }

					if (col1.getHitFraction() > result.fraction) { col1.setHitFraction(result.fraction); }

					if (resultFraction > result.fraction) { resultFraction = result.fraction; }

				}
			}
			TRANSFORMS.release(tmpTrans3, tmpTrans4);
			return resultFraction;
		} finally {
			VECTORS.release(tmp);
			TRANSFORMS.release(tmpTrans1, tmpTrans2);
		}
	}

	@Override
	public void getAllContactManifolds(final ArrayList<PersistentManifold> manifoldArray) {
		// should we use ownManifold to avoid adding duplicates?
		if (manifoldPtr != null && ownManifold) { manifoldArray.add(manifoldPtr); }
	}

	/**
	 * Gets the manifold.
	 *
	 * @return the manifold
	 */
	public PersistentManifold getManifold() {
		return manifoldPtr;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class CreateFunc.
	 */
	public static class CreateFunc implements CollisionAlgorithmCreateFunc {

		/** The pd solver. */
		public ConvexPenetrationDepthSolver pdSolver;
		
		/** The simplex solver. */
		public SimplexSolverInterface simplexSolver;

		/**
		 * Instantiates a new creates the func.
		 *
		 * @param simplexSolver the simplex solver
		 * @param pdSolver the pd solver
		 */
		public CreateFunc(final SimplexSolverInterface simplexSolver, final ConvexPenetrationDepthSolver pdSolver) {
			this.simplexSolver = simplexSolver;
			this.pdSolver = pdSolver;
		}

		@Override
		public CollisionAlgorithm createCollisionAlgorithm(final CollisionAlgorithmConstructionInfo ci,
				final CollisionObject body0, final CollisionObject body1) {
			ConvexConvexAlgorithm algo = CONVEX_CONVEX_COLLISIONS.get();
			algo.init(ci.manifold, ci, body0, body1, simplexSolver, pdSolver);
			return algo;
		}

		@Override
		public void releaseCollisionAlgorithm(final CollisionAlgorithm algo) {
			CONVEX_CONVEX_COLLISIONS.release((ConvexConvexAlgorithm) algo);
		}
	}

}
