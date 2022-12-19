/*******************************************************************************************************
 *
 * SubsimplexConvexCast.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * SubsimplexConvexCast implements Gino van den Bergens' paper "Ray Casting against bteral Convex Objects with
 * Application to Continuous Collision Detection" GJK based Ray Cast, optimized version Objects should not start in
 * overlap, otherwise results are not defined.
 *
 * @author jezek2
 */
public class SubsimplexConvexCast implements ConvexCast {

	// protected final BulletStack stack = BulletStack.get();

	// Typically the conservative advancement reaches solution in a few iterations, clip it to 32 for degenerate cases.
	// See discussion about this here http://www.bulletphysics.com/phpBB2/viewtopic.php?t=565
	// #ifdef BT_USE_DOUBLE_PRECISION
	// #define MAX_ITERATIONS 64
	// #else
	// #define MAX_ITERATIONS 32
	// #endif

	/** The Constant MAX_ITERATIONS. */
	private static final int MAX_ITERATIONS = 32;

	/** The simplex solver. */
	private final SimplexSolverInterface simplexSolver;
	
	/** The convex A. */
	private final ConvexShape convexA;
	
	/** The convex B. */
	private final ConvexShape convexB;

	/**
	 * Instantiates a new subsimplex convex cast.
	 *
	 * @param shapeA the shape A
	 * @param shapeB the shape B
	 * @param simplexSolver the simplex solver
	 */
	public SubsimplexConvexCast(final ConvexShape shapeA, final ConvexShape shapeB,
			final SimplexSolverInterface simplexSolver) {
		this.convexA = shapeA;
		this.convexB = shapeB;
		this.simplexSolver = simplexSolver;
	}

	@Override
	public boolean calcTimeOfImpact( final Transform fromA, final Transform toA,
			final Transform fromB, final Transform toB, final CastResult result) {

		simplexSolver.reset();
		Vector3f tmp = VECTORS.get();
		Vector3f linVelA = VECTORS.get();
		Vector3f linVelB = VECTORS.get();
		Vector3f n = VECTORS.get();
		// Vector3f c = VECTORS.get();
		Vector3f r = VECTORS.get();
		Vector3f v = VECTORS.get();
		Vector3f w = VECTORS.get(), p = VECTORS.get();
		Transform interpolatedTransA = TRANSFORMS.get(fromA);
		Transform interpolatedTransB = TRANSFORMS.get(fromB);

		try {
			// take relative motion
			linVelA.sub(toA.origin, fromA.origin);
			linVelB.sub(toB.origin, fromB.origin);
			r.sub(linVelA, linVelB);

			tmp.negate(r);
			MatrixUtil.transposeTransform(tmp, tmp, fromA.basis);
			Vector3f supVertexA = convexA.localGetSupportingVertex(tmp, VECTORS.get());
			fromA.transform(supVertexA);

			MatrixUtil.transposeTransform(tmp, r, fromB.basis);
			Vector3f supVertexB = convexB.localGetSupportingVertex(tmp, VECTORS.get());
			fromB.transform(supVertexB);

			v.sub(supVertexA, supVertexB);

			int maxIter = MAX_ITERATIONS;

			n.set(0f, 0f, 0f);
			// boolean hasResult = false;

			// float lastLambda = lambda;
			float lambda = 0f;
			float dist2 = v.lengthSquared();
			// #ifdef BT_USE_DOUBLE_PRECISION
			// btScalar epsilon = btScalar(0.0001);
			// #else
			float epsilon = 0.0001f;
			// #endif

			float VdotR;

			while (dist2 > epsilon && maxIter-- != 0) {
				tmp.negate(v);
				MatrixUtil.transposeTransform(tmp, tmp, interpolatedTransA.basis);
				convexA.localGetSupportingVertex(tmp, supVertexA);
				interpolatedTransA.transform(supVertexA);

				MatrixUtil.transposeTransform(tmp, v, interpolatedTransB.basis);
				convexB.localGetSupportingVertex(tmp, supVertexB);
				interpolatedTransB.transform(supVertexB);

				w.sub(supVertexA, supVertexB);

				float VdotW = v.dot(w);

				if (lambda > 1f) return false;

				if (VdotW > 0f) {
					VdotR = v.dot(r);

					if (VdotR >= -(BulletGlobals.FLT_EPSILON * BulletGlobals.FLT_EPSILON))
						return false;
					else {
						lambda = lambda - VdotW / VdotR;

						// interpolate to next lambda
						// x = s + lambda * r;
						VectorUtil.setInterpolate3(interpolatedTransA.origin, fromA.origin, toA.origin, lambda);
						VectorUtil.setInterpolate3(interpolatedTransB.origin, fromB.origin, toB.origin, lambda);
						// m_simplexSolver->reset();
						// check next line
						w.sub(supVertexA, supVertexB);
						// lastLambda = lambda;
						n.set(v);
						// hasResult = true;
					}
				}
				simplexSolver.addVertex(w, supVertexA, supVertexB);
				if (simplexSolver.closest(v)) {
					dist2 = v.lengthSquared();
					// hasResult = true;
					// todo: check this normal for validity
					// n.set(v);
					// printf("V=%f , %f, %f\n",v[0],v[1],v[2]);
					// printf("DIST2=%f\n",dist2);
					// printf("numverts = %i\n",m_simplexSolver->numVertices());
				} else {
					dist2 = 0f;
				}
			}

			// int numiter = MAX_ITERATIONS - maxIter;
			// printf("number of iterations: %d", numiter);

			// don't report a time of impact when moving 'away' from the hitnormal

			result.fraction = lambda;
			if (n.lengthSquared() >= BulletGlobals.SIMD_EPSILON * BulletGlobals.SIMD_EPSILON) {
				result.normal.normalize(n);
			} else {
				result.normal.set(0f, 0f, 0f);
			}

			// don't report time of impact for motion away from the contact normal (or causes minor penetration)
			if (result.normal.dot(r) >= -result.allowedPenetration) return false;

			Vector3f hitA = VECTORS.get();
			Vector3f hitB = VECTORS.get();
			simplexSolver.compute_points(hitA, hitB);
			result.hitPoint.set(hitB);
			VECTORS.release(hitA, hitB, supVertexA, supVertexB);
			return true;
		} finally {
			VECTORS.release(tmp, linVelA, linVelB, n, r, v, w, p);
			TRANSFORMS.release(interpolatedTransA, interpolatedTransB);
		}
	}

}
