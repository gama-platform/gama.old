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

package com.bulletphysics.collision.narrowphase;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;

/**
 * GjkPairDetector uses GJK to implement the {@link DiscreteCollisionDetectorInterface}.
 *
 * @author jezek2
 */
public class GjkPairDetector implements DiscreteCollisionDetectorInterface {

	// protected final BulletStack stack = BulletStack.get();

	// must be above the machine epsilon
	private static final float REL_ERROR2 = 1.0e-6f;

	private final Vector3f cachedSeparatingAxis = new Vector3f();
	private ConvexPenetrationDepthSolver penetrationDepthSolver;
	private SimplexSolverInterface simplexSolver;
	private ConvexShape minkowskiA;
	private ConvexShape minkowskiB;
	private boolean ignoreMargin;

	// some debugging to fix degeneracy problems
	public int lastUsedMethod;
	public int curIter;
	public int degenerateSimplex;
	public int catchDegeneracies;

	public void init(final ConvexShape objectA, final ConvexShape objectB, final SimplexSolverInterface simplexSolver,
			final ConvexPenetrationDepthSolver penetrationDepthSolver) {
		this.cachedSeparatingAxis.set(0f, 0f, 1f);
		this.ignoreMargin = false;
		this.lastUsedMethod = -1;
		this.catchDegeneracies = 1;

		this.penetrationDepthSolver = penetrationDepthSolver;
		this.simplexSolver = simplexSolver;
		this.minkowskiA = objectA;
		this.minkowskiB = objectB;
	}

	@Override
	public void getClosestPoints(final ClosestPointInput input, final Result output, final boolean swapResults) {
		Vector3f tmp = VECTORS.get();

		float distance = 0f;
		Vector3f normalInB = VECTORS.get();
		normalInB.set(0f, 0f, 0f);
		Vector3f pointOnA = VECTORS.get(), pointOnB = VECTORS.get();
		Transform localTransA = TRANSFORMS.get(input.transformA);
		Transform localTransB = TRANSFORMS.get(input.transformB);
		Vector3f positionOffset = VECTORS.get();
		positionOffset.add(localTransA.origin, localTransB.origin);
		positionOffset.scale(0.5f);
		localTransA.origin.sub(positionOffset);
		localTransB.origin.sub(positionOffset);

		float marginA = minkowskiA.getMargin();
		float marginB = minkowskiB.getMargin();

		// BulletStats.gNumGjkChecks++;

		// for CCD we don't use margins
		if (ignoreMargin) {
			marginA = 0f;
			marginB = 0f;
		}

		curIter = 0;
		int gGjkMaxIter = 1000; // this is to catch invalid input, perhaps check for #NaN?
		cachedSeparatingAxis.set(0f, 1f, 0f);

		boolean isValid = false;
		boolean checkSimplex = false;
		boolean checkPenetration = true;
		degenerateSimplex = 0;

		lastUsedMethod = -1;
		Vector3f seperatingAxisInA = VECTORS.get();
		Vector3f seperatingAxisInB = VECTORS.get();

		Vector3f pInA = VECTORS.get();
		Vector3f qInB = VECTORS.get();

		Vector3f pWorld = VECTORS.get();
		Vector3f qWorld = VECTORS.get();
		Vector3f w = VECTORS.get();

		Vector3f tmpPointOnA = VECTORS.get(), tmpPointOnB = VECTORS.get();
		Vector3f tmpNormalInB = VECTORS.get();

		try {
			float squaredDistance = BulletGlobals.SIMD_INFINITY;
			float delta = 0f;

			float margin = marginA + marginB;

			simplexSolver.reset();

			for (;;) // while (true)
			{
				seperatingAxisInA.negate(cachedSeparatingAxis);
				MatrixUtil.transposeTransform(seperatingAxisInA, seperatingAxisInA, input.transformA.basis);

				seperatingAxisInB.set(cachedSeparatingAxis);
				MatrixUtil.transposeTransform(seperatingAxisInB, seperatingAxisInB, input.transformB.basis);

				minkowskiA.localGetSupportingVertexWithoutMargin(seperatingAxisInA, pInA);
				minkowskiB.localGetSupportingVertexWithoutMargin(seperatingAxisInB, qInB);

				pWorld.set(pInA);
				localTransA.transform(pWorld);

				qWorld.set(qInB);
				localTransB.transform(qWorld);

				w.sub(pWorld, qWorld);

				delta = cachedSeparatingAxis.dot(w);

				// potential exit, they don't overlap
				if (delta > 0f && delta * delta > squaredDistance * input.maximumDistanceSquared) {
					checkPenetration = false;
					break;
				}

				// exit 0: the new point is already in the simplex, or we didn't come any closer
				if (simplexSolver.inSimplex(w)) {
					degenerateSimplex = 1;
					checkSimplex = true;
					break;
				}
				// are we getting any closer ?
				float f0 = squaredDistance - delta;
				float f1 = squaredDistance * REL_ERROR2;

				if (f0 <= f1) {
					if (f0 <= 0f) { degenerateSimplex = 2; }
					checkSimplex = true;
					break;
				}
				// add current vertex to simplex
				simplexSolver.addVertex(w, pWorld, qWorld);

				// calculate the closest point to the origin (update vector v)
				if (!simplexSolver.closest(cachedSeparatingAxis)) {
					degenerateSimplex = 3;
					checkSimplex = true;
					break;
				}

				if (cachedSeparatingAxis.lengthSquared() < REL_ERROR2) {
					degenerateSimplex = 6;
					checkSimplex = true;
					break;
				}

				float previousSquaredDistance = squaredDistance;
				squaredDistance = cachedSeparatingAxis.lengthSquared();

				// redundant m_simplexSolver->compute_points(pointOnA, pointOnB);

				// are we getting any closer ?
				if (previousSquaredDistance - squaredDistance <= BulletGlobals.FLT_EPSILON * previousSquaredDistance) {
					simplexSolver.backup_closest(cachedSeparatingAxis);
					checkSimplex = true;
					break;
				}

				// degeneracy, this is typically due to invalid/uninitialized worldtransforms for a CollisionObject
				if (curIter++ > gGjkMaxIter) {
					// #if defined(DEBUG) || defined (_DEBUG)
					if (BulletGlobals.DEBUG) {
						System.err.printf("btGjkPairDetector maxIter exceeded:%i\n", curIter);
						System.err.printf("sepAxis=(%f,%f,%f), squaredDistance = %f, shapeTypeA=%i,shapeTypeB=%i\n",
								cachedSeparatingAxis.x, cachedSeparatingAxis.y, cachedSeparatingAxis.z, squaredDistance,
								minkowskiA.getShapeType(), minkowskiB.getShapeType());
					}
					// #endif
					break;

				}

				boolean check = !simplexSolver.fullSimplex();
				// bool check = (!m_simplexSolver->fullSimplex() && squaredDistance > SIMD_EPSILON *
				// m_simplexSolver->maxVertex());

				if (!check) {
					// do we need this backup_closest here ?
					simplexSolver.backup_closest(cachedSeparatingAxis);
					break;
				}
			}

			if (checkSimplex) {
				simplexSolver.compute_points(pointOnA, pointOnB);
				normalInB.sub(pointOnA, pointOnB);
				float lenSqr = cachedSeparatingAxis.lengthSquared();
				// valid normal
				if (lenSqr < 0.0001f) { degenerateSimplex = 5; }
				if (lenSqr > BulletGlobals.FLT_EPSILON * BulletGlobals.FLT_EPSILON) {
					float rlen = 1f / (float) Math.sqrt(lenSqr);
					normalInB.scale(rlen); // normalize
					float s = (float) Math.sqrt(squaredDistance);

					assert s > 0f;

					tmp.scale(marginA / s, cachedSeparatingAxis);
					pointOnA.sub(tmp);

					tmp.scale(marginB / s, cachedSeparatingAxis);
					pointOnB.add(tmp);

					distance = 1f / rlen - margin;
					isValid = true;

					lastUsedMethod = 1;
				} else {
					lastUsedMethod = 2;
				}
			}

			boolean catchDegeneratePenetrationCase = catchDegeneracies != 0 && penetrationDepthSolver != null
					&& degenerateSimplex != 0 && distance + margin < 0.01f;

			// if (checkPenetration && !isValid)
			if (checkPenetration && (!isValid || catchDegeneratePenetrationCase)) {
				// penetration case

				// if there is no way to handle penetrations, bail out
				if (penetrationDepthSolver != null) {
					// Penetration depth case.
					// BulletStats.gNumDeepPenetrationChecks++;

					boolean isValid2 = penetrationDepthSolver.calcPenDepth(simplexSolver, minkowskiA, minkowskiB,
							localTransA, localTransB, cachedSeparatingAxis, tmpPointOnA, tmpPointOnB);

					if (isValid2) {
						tmpNormalInB.sub(tmpPointOnB, tmpPointOnA);

						float lenSqr = tmpNormalInB.lengthSquared();
						if (lenSqr > BulletGlobals.FLT_EPSILON * BulletGlobals.FLT_EPSILON) {
							tmpNormalInB.scale(1f / (float) Math.sqrt(lenSqr));
							tmp.sub(tmpPointOnA, tmpPointOnB);
							float distance2 = -tmp.length();
							// only replace valid penetrations when the result is deeper (check)
							if (!isValid || distance2 < distance) {
								distance = distance2;
								pointOnA.set(tmpPointOnA);
								pointOnB.set(tmpPointOnB);
								normalInB.set(tmpNormalInB);
								isValid = true;
								lastUsedMethod = 3;
							} else {

							}
						} else {
							// isValid = false;
							lastUsedMethod = 4;
						}
					} else {
						lastUsedMethod = 5;
					}

				}
			}
		} finally {
			VECTORS.release(seperatingAxisInA, seperatingAxisInB, pInA, qInB, pWorld, qWorld, w, tmpPointOnA,
					tmpPointOnB, tmpNormalInB, tmp, normalInB, pointOnA, pointOnB, positionOffset);
			TRANSFORMS.release(localTransA, localTransB);
		}

		if (isValid) {
			tmp.add(pointOnB, positionOffset);
			output.addContactPoint(normalInB, tmp, distance);
		}
	}

	public void setMinkowskiA(final ConvexShape minkA) {
		minkowskiA = minkA;
	}

	public void setMinkowskiB(final ConvexShape minkB) {
		minkowskiB = minkB;
	}

	public void setCachedSeperatingAxis(final Vector3f seperatingAxis) {
		cachedSeparatingAxis.set(seperatingAxis);
	}

	public void setPenetrationDepthSolver(final ConvexPenetrationDepthSolver penetrationDepthSolver) {
		this.penetrationDepthSolver = penetrationDepthSolver;
	}

	/**
	 * Don't use setIgnoreMargin, it's for Bullet's internal use.
	 */
	public void setIgnoreMargin(final boolean ignoreMargin) {
		this.ignoreMargin = ignoreMargin;
	}

}
