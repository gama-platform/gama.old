/*******************************************************************************************************
 *
 * GjkConvexCast.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import static com.bulletphysics.Pools.CLOSEST_POINTS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.narrowphase.DiscreteCollisionDetectorInterface.ClosestPointInput;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * GjkConvexCast performs a raycast on a convex object using support mapping.
 *
 * @author jezek2
 */
public class GjkConvexCast implements ConvexCast {

	// #ifdef BT_USE_DOUBLE_PRECISION
	// private static final int MAX_ITERATIONS = 64;
	/** The Constant MAX_ITERATIONS. */
	// #else
	private static final int MAX_ITERATIONS = 32;
	// #endif

	/** The simplex solver. */
	private final SimplexSolverInterface simplexSolver;
	
	/** The convex A. */
	private final ConvexShape convexA;
	
	/** The convex B. */
	private final ConvexShape convexB;

	/** The gjk. */
	private final GjkPairDetector gjk = new GjkPairDetector();

	/**
	 * Instantiates a new gjk convex cast.
	 *
	 * @param convexA the convex A
	 * @param convexB the convex B
	 * @param simplexSolver the simplex solver
	 */
	public GjkConvexCast(final ConvexShape convexA, final ConvexShape convexB,
			final SimplexSolverInterface simplexSolver) {
		this.simplexSolver = simplexSolver;
		this.convexA = convexA;
		this.convexB = convexB;
	}

	@Override
	public boolean calcTimeOfImpact( final Transform fromA, final Transform toA,
			final Transform fromB, final Transform toB, final CastResult result) {
		simplexSolver.reset();

		// compute linear velocity for this interval, to interpolate
		// assume no rotation/angular velocity, assert here?
		Vector3f linVelA = VECTORS.get();
		Vector3f linVelB = VECTORS.get();

		linVelA.sub(toA.origin, fromA.origin);
		linVelB.sub(toB.origin, fromB.origin);

		float radius = 0.001f;
		float lambda = 0f;
		Vector3f v = VECTORS.get();
		v.set(1f, 0f, 0f);

		int maxIter = MAX_ITERATIONS;

		Vector3f n = VECTORS.get();
		n.set(0f, 0f, 0f);
		boolean hasResult = false;
		Vector3f c = VECTORS.get();
		Vector3f r = VECTORS.get();
		r.sub(linVelA, linVelB);

		float lastLambda = lambda;
		// btScalar epsilon = btScalar(0.001);

		int numIter = 0;
		// first solution, using GJK

		Transform identityTrans = TRANSFORMS.get();
		identityTrans.setIdentity();

		// result.drawCoordSystem(sphereTr);

		PointCollector pointCollector = new PointCollector();

		gjk.init(convexA, convexB, simplexSolver, null); // penetrationDepthSolver);
		ClosestPointInput input = CLOSEST_POINTS.get();
		input.init();
		try {
			// we don't use margins during CCD
			// gjk.setIgnoreMargin(true);

			input.transformA.set(fromA);
			input.transformB.set(fromB);
			gjk.getClosestPoints( input, pointCollector);

			hasResult = pointCollector.hasResult;
			c.set(pointCollector.pointInWorld);

			if (hasResult) {
				float dist;
				dist = pointCollector.distance;
				n.set(pointCollector.normalOnBInWorld);

				// not close enough
				while (dist > radius) {
					numIter++;
					if (numIter > maxIter) return false; // todo: report a failure
					float dLambda = 0f;

					float projectedLinearVelocity = r.dot(n);

					dLambda = dist / projectedLinearVelocity;

					lambda = lambda - dLambda;

					if (lambda > 1f) return false;
					if (lambda < 0f) return false; // todo: next check with relative epsilon

					if (lambda <= lastLambda) return false;
					// n.setValue(0,0,0);
					// break;
					lastLambda = lambda;

					// interpolate to next lambda
					VectorUtil.setInterpolate3(input.transformA.origin, fromA.origin, toA.origin, lambda);
					VectorUtil.setInterpolate3(input.transformB.origin, fromB.origin, toB.origin, lambda);

					gjk.getClosestPoints( input, pointCollector);
					if (pointCollector.hasResult) {
						if (pointCollector.distance < 0f) {
							result.fraction = lastLambda;
							n.set(pointCollector.normalOnBInWorld);
							result.normal.set(n);
							result.hitPoint.set(pointCollector.pointInWorld);
							return true;
						}
						c.set(pointCollector.pointInWorld);
						n.set(pointCollector.normalOnBInWorld);
						dist = pointCollector.distance;
					} else
						// ??
						return false;

				}

				// is n normalized?
				// don't report time of impact for motion away from the contact normal (or causes minor penetration)
				if (n.dot(r) >= -result.allowedPenetration) return false;
				result.fraction = lambda;
				result.normal.set(n);
				result.hitPoint.set(c);
				return true;
			}

			return false;
		} finally {
			VECTORS.release(c, n, r, v, linVelA, linVelB);
			TRANSFORMS.release(identityTrans);
			CLOSEST_POINTS.release(input);
		}
	}

}
