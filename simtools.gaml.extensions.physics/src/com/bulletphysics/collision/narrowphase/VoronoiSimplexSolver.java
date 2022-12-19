/*******************************************************************************************************
 *
 * VoronoiSimplexSolver.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import static com.bulletphysics.Pools.SUB_SIMPLEX;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.VectorUtil;

/**
 * VoronoiSimplexSolver is an implementation of the closest point distance algorithm from a 1-4 points simplex to the
 * origin. Can be used with GJK, as an alternative to Johnson distance algorithm.
 *
 * @author jezek2
 */
public class VoronoiSimplexSolver extends SimplexSolverInterface {

	/** The Constant VORONOI_SIMPLEX_MAX_VERTS. */
	private static final int VORONOI_SIMPLEX_MAX_VERTS = 5;

	/** The Constant VERTA. */
	private static final int VERTA = 0;
	
	/** The Constant VERTB. */
	private static final int VERTB = 1;
	
	/** The Constant VERTC. */
	private static final int VERTC = 2;
	
	/** The Constant VERTD. */
	private static final int VERTD = 3;

	/** The num vertices. */
	public int numVertices;

	/** The simplex vector W. */
	public final Vector3f[] simplexVectorW = new Vector3f[VORONOI_SIMPLEX_MAX_VERTS];
	
	/** The simplex points P. */
	public final Vector3f[] simplexPointsP = new Vector3f[VORONOI_SIMPLEX_MAX_VERTS];
	
	/** The simplex points Q. */
	public final Vector3f[] simplexPointsQ = new Vector3f[VORONOI_SIMPLEX_MAX_VERTS];

	/** The cached P 1. */
	public final Vector3f cachedP1 = new Vector3f();
	
	/** The cached P 2. */
	public final Vector3f cachedP2 = new Vector3f();
	
	/** The cached V. */
	public final Vector3f cachedV = new Vector3f();
	
	/** The last W. */
	public final Vector3f lastW = new Vector3f();
	
	/** The cached valid closest. */
	public boolean cachedValidClosest;

	/** The cached BC. */
	public final SubSimplexClosestResult cachedBC = new SubSimplexClosestResult();

	/** The needs update. */
	public boolean needsUpdate;

	{
		for (int i = 0; i < VORONOI_SIMPLEX_MAX_VERTS; i++) {
			simplexVectorW[i] = new Vector3f();
			simplexPointsP[i] = new Vector3f();
			simplexPointsQ[i] = new Vector3f();
		}
	}

	/**
	 * Removes the vertex.
	 *
	 * @param index the index
	 */
	public void removeVertex(final int index) {
		assert numVertices > 0;
		numVertices--;
		simplexVectorW[index].set(simplexVectorW[numVertices]);
		simplexPointsP[index].set(simplexPointsP[numVertices]);
		simplexPointsQ[index].set(simplexPointsQ[numVertices]);
	}

	/**
	 * Reduce vertices.
	 *
	 * @param usedVerts the used verts
	 */
	public void reduceVertices(final UsageBitfield usedVerts) {
		if (numVertices() >= 4 && !usedVerts.usedVertexD) { removeVertex(3); }

		if (numVertices() >= 3 && !usedVerts.usedVertexC) { removeVertex(2); }

		if (numVertices() >= 2 && !usedVerts.usedVertexB) { removeVertex(1); }

		if (numVertices() >= 1 && !usedVerts.usedVertexA) { removeVertex(0); }
	}

	/**
	 * Update closest vector and points.
	 *
	 * @return true, if successful
	 */
	public boolean updateClosestVectorAndPoints() {
		if (needsUpdate) {
			cachedBC.reset();

			needsUpdate = false;

			switch (numVertices()) {
				case 0:
					cachedValidClosest = false;
					break;
				case 1: {
					cachedP1.set(simplexPointsP[0]);
					cachedP2.set(simplexPointsQ[0]);
					cachedV.sub(cachedP1, cachedP2); // == m_simplexVectorW[0]
					cachedBC.reset();
					cachedBC.setBarycentricCoordinates(1f, 0f, 0f, 0f);
					cachedValidClosest = cachedBC.isValid();
					break;
				}
				case 2: {
					Vector3f tmp = VECTORS.get();
					// closest point origin from line segment
					Vector3f from = simplexVectorW[0];
					Vector3f to = simplexVectorW[1];
					Vector3f nearest = VECTORS.get();
					Vector3f p = VECTORS.get();
					p.set(0f, 0f, 0f);
					Vector3f diff = VECTORS.get();
					diff.sub(p, from);
					Vector3f v = VECTORS.get();
					v.sub(to, from);
					float t = v.dot(diff);
					if (t > 0) {
						float dotVV = v.dot(v);
						if (t < dotVV) {
							t /= dotVV;
							tmp.scale(t, v);
							diff.sub(tmp);
							cachedBC.usedVertices.usedVertexA = true;
							cachedBC.usedVertices.usedVertexB = true;
						} else {
							t = 1;
							diff.sub(v);
							// reduce to 1 point
							cachedBC.usedVertices.usedVertexB = true;
						}
					} else {
						t = 0;
						// reduce to 1 point
						cachedBC.usedVertices.usedVertexA = true;
					}
					cachedBC.setBarycentricCoordinates(1f - t, t, 0f, 0f);

					tmp.scale(t, v);
					nearest.add(from, tmp);

					tmp.sub(simplexPointsP[1], simplexPointsP[0]);
					tmp.scale(t);
					cachedP1.add(simplexPointsP[0], tmp);

					tmp.sub(simplexPointsQ[1], simplexPointsQ[0]);
					tmp.scale(t);
					cachedP2.add(simplexPointsQ[0], tmp);

					cachedV.sub(cachedP1, cachedP2);

					reduceVertices(cachedBC.usedVertices);

					cachedValidClosest = cachedBC.isValid();
					VECTORS.release(tmp, nearest, p, diff, v);
					break;
				}
				case 3: {
					Vector3f tmp1 = VECTORS.get();
					Vector3f tmp2 = VECTORS.get();
					Vector3f tmp3 = VECTORS.get();
					// closest point origin from triangle
					Vector3f p = VECTORS.get();
					p.set(0f, 0f, 0f);

					Vector3f a = simplexVectorW[0];
					Vector3f b = simplexVectorW[1];
					Vector3f c = simplexVectorW[2];

					closestPtPointTriangle(p, a, b, c, cachedBC);

					tmp1.scale(cachedBC.barycentricCoords[0], simplexPointsP[0]);
					tmp2.scale(cachedBC.barycentricCoords[1], simplexPointsP[1]);
					tmp3.scale(cachedBC.barycentricCoords[2], simplexPointsP[2]);
					VectorUtil.add(cachedP1, tmp1, tmp2, tmp3);

					tmp1.scale(cachedBC.barycentricCoords[0], simplexPointsQ[0]);
					tmp2.scale(cachedBC.barycentricCoords[1], simplexPointsQ[1]);
					tmp3.scale(cachedBC.barycentricCoords[2], simplexPointsQ[2]);
					VectorUtil.add(cachedP2, tmp1, tmp2, tmp3);

					cachedV.sub(cachedP1, cachedP2);

					reduceVertices(cachedBC.usedVertices);
					cachedValidClosest = cachedBC.isValid();
					VECTORS.release(tmp1, tmp2, tmp3, p);
					break;
				}
				case 4: {
					Vector3f tmp1 = VECTORS.get();
					Vector3f tmp2 = VECTORS.get();
					Vector3f tmp3 = VECTORS.get();
					Vector3f tmp4 = VECTORS.get();
					Vector3f p = VECTORS.get();
					p.set(0f, 0f, 0f);

					try {
						Vector3f a = simplexVectorW[0];
						Vector3f b = simplexVectorW[1];
						Vector3f c = simplexVectorW[2];
						Vector3f d = simplexVectorW[3];
						boolean hasSeperation = closestPtPointTetrahedron(p, a, b, c, d, cachedBC);
						if (hasSeperation) {
							tmp1.scale(cachedBC.barycentricCoords[0], simplexPointsP[0]);
							tmp2.scale(cachedBC.barycentricCoords[1], simplexPointsP[1]);
							tmp3.scale(cachedBC.barycentricCoords[2], simplexPointsP[2]);
							tmp4.scale(cachedBC.barycentricCoords[3], simplexPointsP[3]);
							VectorUtil.add(cachedP1, tmp1, tmp2, tmp3, tmp4);

							tmp1.scale(cachedBC.barycentricCoords[0], simplexPointsQ[0]);
							tmp2.scale(cachedBC.barycentricCoords[1], simplexPointsQ[1]);
							tmp3.scale(cachedBC.barycentricCoords[2], simplexPointsQ[2]);
							tmp4.scale(cachedBC.barycentricCoords[3], simplexPointsQ[3]);
							VectorUtil.add(cachedP2, tmp1, tmp2, tmp3, tmp4);

							cachedV.sub(cachedP1, cachedP2);
							reduceVertices(cachedBC.usedVertices);
						} else {
							// printf("sub distance got penetration\n");

							if (cachedBC.degenerate) {
								cachedValidClosest = false;
							} else {
								cachedValidClosest = true;
								// degenerate case == false, penetration = true + zero
								cachedV.set(0f, 0f, 0f);
							}
							break;
						}
						cachedValidClosest = cachedBC.isValid();
						// closest point origin from tetrahedron
						break;
					} finally {
						VECTORS.release(tmp1, tmp2, tmp3, tmp4, p);
					}
				}
				default: {
					cachedValidClosest = false;
				}
			}
		}

		return cachedValidClosest;
	}

	/**
	 * Closest pt point triangle.
	 *
	 * @param p the p
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @param result the result
	 * @return true, if successful
	 */
	public boolean closestPtPointTriangle(final Vector3f p, final Vector3f a, final Vector3f b, final Vector3f c,
			final SubSimplexClosestResult result) {
		result.usedVertices.reset();

		// Check if P in vertex region outside A
		Vector3f ab = VECTORS.get();
		ab.sub(b, a);

		Vector3f ac = VECTORS.get();
		ac.sub(c, a);

		try {
			Vector3f ap = VECTORS.get();
			ap.sub(p, a);
			float d1 = ab.dot(ap);
			float d2 = ac.dot(ap);
			VECTORS.release(ap);
			if (d1 <= 0f && d2 <= 0f) {
				result.closestPointOnSimplex.set(a);
				result.usedVertices.usedVertexA = true;
				result.setBarycentricCoordinates(1f, 0f, 0f, 0f);
				return true; // a; // barycentric coordinates (1,0,0)
			}
			// Check if P in vertex region outside B
			Vector3f bp = VECTORS.get();
			bp.sub(p, b);
			float d3 = ab.dot(bp);
			float d4 = ac.dot(bp);
			VECTORS.release(bp);
			if (d3 >= 0f && d4 <= d3) {
				result.closestPointOnSimplex.set(b);
				result.usedVertices.usedVertexB = true;
				result.setBarycentricCoordinates(0, 1f, 0f, 0f);

				return true; // b; // barycentric coordinates (0,1,0)
			}
			// Check if P in edge region of AB, if so return projection of P onto AB
			float vc = d1 * d4 - d3 * d2;
			if (vc <= 0f && d1 >= 0f && d3 <= 0f) {
				float v = d1 / (d1 - d3);
				result.closestPointOnSimplex.scaleAdd(v, ab, a);
				result.usedVertices.usedVertexA = true;
				result.usedVertices.usedVertexB = true;
				result.setBarycentricCoordinates(1f - v, v, 0f, 0f);
				return true;
				// return a + v * ab; // barycentric coordinates (1-v,v,0)
			}
			// Check if P in vertex region outside C
			Vector3f cp = VECTORS.get();
			cp.sub(p, c);
			float d5 = ab.dot(cp);
			float d6 = ac.dot(cp);
			VECTORS.release(cp);
			if (d6 >= 0f && d5 <= d6) {
				result.closestPointOnSimplex.set(c);
				result.usedVertices.usedVertexC = true;
				result.setBarycentricCoordinates(0f, 0f, 1f, 0f);
				return true;// c; // barycentric coordinates (0,0,1)
			}
			// Check if P in edge region of AC, if so return projection of P onto AC
			float vb = d5 * d2 - d1 * d6;
			if (vb <= 0f && d2 >= 0f && d6 <= 0f) {
				float w = d2 / (d2 - d6);
				result.closestPointOnSimplex.scaleAdd(w, ac, a);
				result.usedVertices.usedVertexA = true;
				result.usedVertices.usedVertexC = true;
				result.setBarycentricCoordinates(1f - w, 0f, w, 0f);
				return true;
				// return a + w * ac; // barycentric coordinates (1-w,0,w)
			}
			// Check if P in edge region of BC, if so return projection of P onto BC
			float va = d3 * d6 - d5 * d4;
			if (va <= 0f && d4 - d3 >= 0f && d5 - d6 >= 0f) {
				float w = (d4 - d3) / (d4 - d3 + (d5 - d6));

				Vector3f tmp = VECTORS.get();
				tmp.sub(c, b);
				result.closestPointOnSimplex.scaleAdd(w, tmp, b);

				result.usedVertices.usedVertexB = true;
				result.usedVertices.usedVertexC = true;
				result.setBarycentricCoordinates(0, 1f - w, w, 0f);
				VECTORS.release(tmp);
				return true;
				// return b + w * (c - b); // barycentric coordinates (0,1-w,w)
			}
			// P inside face region. Compute Q through its barycentric coordinates (u,v,w)
			float denom = 1f / (va + vb + vc);
			float v = vb * denom;
			float w = vc * denom;
			Vector3f tmp1 = VECTORS.get();
			Vector3f tmp2 = VECTORS.get();
			tmp1.scale(v, ab);
			tmp2.scale(w, ac);
			VectorUtil.add(result.closestPointOnSimplex, a, tmp1, tmp2);
			result.usedVertices.usedVertexA = true;
			result.usedVertices.usedVertexB = true;
			result.usedVertices.usedVertexC = true;
			result.setBarycentricCoordinates(1f - v - w, v, w, 0f);
			VECTORS.release(tmp1, tmp2);
			return true;
			// return a + ab * v + ac * w; // = u*a + v*b + w*c, u = va * denom = btScalar(1.0) - v - w
		} finally {
			VECTORS.release(ab, ac);
		}
	}

	/// Test if point p and d lie on opposite sides of plane through abc

	/**
	 * Point outside of plane.
	 *
	 * @param p the p
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @param d the d
	 * @return the int
	 */
	public static int pointOutsideOfPlane(final Vector3f p, final Vector3f a, final Vector3f b, final Vector3f c,
			final Vector3f d) {
		Vector3f tmp = VECTORS.get();
		Vector3f normal = VECTORS.get();
		normal.sub(b, a);
		tmp.sub(c, a);
		normal.cross(normal, tmp);

		tmp.sub(p, a);
		float signp = tmp.dot(normal); // [AP AB AC]

		tmp.sub(d, a);
		float signd = tmp.dot(normal); // [AD AB AC]
		VECTORS.release(tmp, normal);
		if (signd * signd < 1e-4f * 1e-4f) return -1;

		// Points on opposite sides if expression signs are opposite
		return signp * signd < 0f ? 1 : 0;
	}

	/**
	 * Closest pt point tetrahedron.
	 *
	 * @param p the p
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @param d the d
	 * @param finalResult the final result
	 * @return true, if successful
	 */
	public boolean closestPtPointTetrahedron(final Vector3f p, final Vector3f a, final Vector3f b, final Vector3f c,
			final Vector3f d, final SubSimplexClosestResult finalResult) {
		SubSimplexClosestResult tempResult = SUB_SIMPLEX.get();
		tempResult.reset();
		Vector3f tmp = VECTORS.get();
		Vector3f q = VECTORS.get();
		try {
			// Start out assuming point inside all halfspaces, so closest to itself
			finalResult.closestPointOnSimplex.set(p);
			finalResult.usedVertices.reset();
			finalResult.usedVertices.usedVertexA = true;
			finalResult.usedVertices.usedVertexB = true;
			finalResult.usedVertices.usedVertexC = true;
			finalResult.usedVertices.usedVertexD = true;

			int pointOutsideABC = pointOutsideOfPlane(p, a, b, c, d);
			int pointOutsideACD = pointOutsideOfPlane(p, a, c, d, b);
			int pointOutsideADB = pointOutsideOfPlane(p, a, d, b, c);
			int pointOutsideBDC = pointOutsideOfPlane(p, b, d, c, a);

			if (pointOutsideABC < 0 || pointOutsideACD < 0 || pointOutsideADB < 0 || pointOutsideBDC < 0) {
				finalResult.degenerate = true;
				return false;
			}

			if (pointOutsideABC == 0 && pointOutsideACD == 0 && pointOutsideADB == 0 && pointOutsideBDC == 0)
				return false;

			float bestSqDist = Float.MAX_VALUE;
			// If point outside face abc then compute closest point on abc
			if (pointOutsideABC != 0) {
				closestPtPointTriangle(p, a, b, c, tempResult);
				q.set(tempResult.closestPointOnSimplex);

				tmp.sub(q, p);
				float sqDist = tmp.dot(tmp);
				// Update best closest point if (squared) distance is less than current best
				if (sqDist < bestSqDist) {
					bestSqDist = sqDist;
					finalResult.closestPointOnSimplex.set(q);
					// convert result bitmask!
					finalResult.usedVertices.reset();
					finalResult.usedVertices.usedVertexA = tempResult.usedVertices.usedVertexA;
					finalResult.usedVertices.usedVertexB = tempResult.usedVertices.usedVertexB;
					finalResult.usedVertices.usedVertexC = tempResult.usedVertices.usedVertexC;
					finalResult.setBarycentricCoordinates(tempResult.barycentricCoords[VERTA],
							tempResult.barycentricCoords[VERTB], tempResult.barycentricCoords[VERTC], 0);

				}
			}

			// Repeat test for face acd
			if (pointOutsideACD != 0) {
				closestPtPointTriangle(p, a, c, d, tempResult);
				q.set(tempResult.closestPointOnSimplex);
				// convert result bitmask!

				tmp.sub(q, p);
				float sqDist = tmp.dot(tmp);
				if (sqDist < bestSqDist) {
					bestSqDist = sqDist;
					finalResult.closestPointOnSimplex.set(q);
					finalResult.usedVertices.reset();
					finalResult.usedVertices.usedVertexA = tempResult.usedVertices.usedVertexA;

					finalResult.usedVertices.usedVertexC = tempResult.usedVertices.usedVertexB;
					finalResult.usedVertices.usedVertexD = tempResult.usedVertices.usedVertexC;
					finalResult.setBarycentricCoordinates(tempResult.barycentricCoords[VERTA], 0,
							tempResult.barycentricCoords[VERTB], tempResult.barycentricCoords[VERTC]);

				}
			}
			// Repeat test for face adb

			if (pointOutsideADB != 0) {
				closestPtPointTriangle(p, a, d, b, tempResult);
				q.set(tempResult.closestPointOnSimplex);
				// convert result bitmask!

				tmp.sub(q, p);
				float sqDist = tmp.dot(tmp);
				if (sqDist < bestSqDist) {
					bestSqDist = sqDist;
					finalResult.closestPointOnSimplex.set(q);
					finalResult.usedVertices.reset();
					finalResult.usedVertices.usedVertexA = tempResult.usedVertices.usedVertexA;
					finalResult.usedVertices.usedVertexB = tempResult.usedVertices.usedVertexC;

					finalResult.usedVertices.usedVertexD = tempResult.usedVertices.usedVertexB;
					finalResult.setBarycentricCoordinates(tempResult.barycentricCoords[VERTA],
							tempResult.barycentricCoords[VERTC], 0, tempResult.barycentricCoords[VERTB]);

				}
			}
			// Repeat test for face bdc

			if (pointOutsideBDC != 0) {
				closestPtPointTriangle(p, b, d, c, tempResult);
				q.set(tempResult.closestPointOnSimplex);
				// convert result bitmask!
				tmp.sub(q, p);
				float sqDist = tmp.dot(tmp);
				if (sqDist < bestSqDist) {
					bestSqDist = sqDist;
					finalResult.closestPointOnSimplex.set(q);
					finalResult.usedVertices.reset();
					//
					finalResult.usedVertices.usedVertexB = tempResult.usedVertices.usedVertexA;
					finalResult.usedVertices.usedVertexC = tempResult.usedVertices.usedVertexC;
					finalResult.usedVertices.usedVertexD = tempResult.usedVertices.usedVertexB;

					finalResult.setBarycentricCoordinates(0, tempResult.barycentricCoords[VERTA],
							tempResult.barycentricCoords[VERTC], tempResult.barycentricCoords[VERTB]);

				}
			}

			// help! we ended up full !

			if (finalResult.usedVertices.usedVertexA && finalResult.usedVertices.usedVertexB
					&& finalResult.usedVertices.usedVertexC && finalResult.usedVertices.usedVertexD)
				return true;

			return true;
		} finally {
			VECTORS.release(tmp, q);
			SUB_SIMPLEX.release(tempResult);
		}
	}

	/**
	 * Clear the simplex, remove all the vertices.
	 */
	@Override
	public void reset() {
		cachedValidClosest = false;
		numVertices = 0;
		needsUpdate = true;
		lastW.set(1e30f, 1e30f, 1e30f);
		cachedBC.reset();
	}

	@Override
	public void addVertex(final Vector3f w, final Vector3f p, final Vector3f q) {
		lastW.set(w);
		needsUpdate = true;

		simplexVectorW[numVertices].set(w);
		simplexPointsP[numVertices].set(p);
		simplexPointsQ[numVertices].set(q);

		numVertices++;
	}

	/**
	 * Return/calculate the closest vertex.
	 */
	@Override
	public boolean closest(final Vector3f v) {
		boolean succes = updateClosestVectorAndPoints();
		v.set(cachedV);
		return succes;
	}

	@Override
	public float maxVertex() {
		int i, numverts = numVertices();
		float maxV = 0f;
		for (i = 0; i < numverts; i++) {
			float curLen2 = simplexVectorW[i].lengthSquared();
			if (maxV < curLen2) { maxV = curLen2; }
		}
		return maxV;
	}

	@Override
	public boolean fullSimplex() {
		return numVertices == 4;
	}

	@Override
	public int getSimplex(final Vector3f[] pBuf, final Vector3f[] qBuf, final Vector3f[] yBuf) {
		for (int i = 0; i < numVertices(); i++) {
			yBuf[i].set(simplexVectorW[i]);
			pBuf[i].set(simplexPointsP[i]);
			qBuf[i].set(simplexPointsQ[i]);
		}
		return numVertices();
	}

	@Override
	public boolean inSimplex(final Vector3f w) {
		boolean found = false;
		int i, numverts = numVertices();
		// btScalar maxV = btScalar(0.);

		// w is in the current (reduced) simplex
		for (i = 0; i < numverts; i++) {
			if (simplexVectorW[i].equals(w)) { found = true; }
		}

		// check in case lastW is already removed
		if (w.equals(lastW)) return true;

		return found;
	}

	@Override
	public void backup_closest(final Vector3f v) {
		v.set(cachedV);
	}

	@Override
	public boolean emptySimplex() {
		return numVertices() == 0;
	}

	@Override
	public void compute_points(final Vector3f p1, final Vector3f p2) {
		updateClosestVectorAndPoints();
		p1.set(cachedP1);
		p2.set(cachedP2);
	}

	@Override
	public int numVertices() {
		return numVertices;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class UsageBitfield.
	 */
	public static class UsageBitfield {
		
		/** The used vertex A. */
		public boolean usedVertexA;
		
		/** The used vertex B. */
		public boolean usedVertexB;
		
		/** The used vertex C. */
		public boolean usedVertexC;
		
		/** The used vertex D. */
		public boolean usedVertexD;

		/**
		 * Reset.
		 */
		public void reset() {
			usedVertexA = false;
			usedVertexB = false;
			usedVertexC = false;
			usedVertexD = false;
		}
	}

	/**
	 * The Class SubSimplexClosestResult.
	 */
	public static class SubSimplexClosestResult {
		
		/** The closest point on simplex. */
		public final Vector3f closestPointOnSimplex = new Vector3f();
		// MASK for m_usedVertices
		// stores the simplex vertex-usage, using the MASK,
		/** The used vertices. */
		// if m_usedVertices & MASK then the related vertex is used
		public final UsageBitfield usedVertices = new UsageBitfield();
		
		/** The barycentric coords. */
		public final float[] barycentricCoords = new float[4];
		
		/** The degenerate. */
		public boolean degenerate;

		/**
		 * Reset.
		 */
		public void reset() {
			degenerate = false;
			setBarycentricCoordinates(0f, 0f, 0f, 0f);
			usedVertices.reset();
		}

		/**
		 * Checks if is valid.
		 *
		 * @return true, if is valid
		 */
		public boolean isValid() {
			boolean valid = barycentricCoords[0] >= 0f && barycentricCoords[1] >= 0f && barycentricCoords[2] >= 0f
					&& barycentricCoords[3] >= 0f;
			return valid;
		}

		/**
		 * Sets the barycentric coordinates.
		 *
		 * @param a the a
		 * @param b the b
		 * @param c the c
		 * @param d the d
		 */
		public void setBarycentricCoordinates(final float a, final float b, final float c, final float d) {
			barycentricCoords[0] = a;
			barycentricCoords[1] = b;
			barycentricCoords[2] = c;
			barycentricCoords[3] = d;
		}
	}

}
