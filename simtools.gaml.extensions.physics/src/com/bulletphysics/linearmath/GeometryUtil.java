/*******************************************************************************************************
 *
 * GeometryUtil.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

import static com.bulletphysics.Pools.VECTORS;
import static com.bulletphysics.Pools.VECTORS4;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.util.ObjectArrayList;

/**
 * GeometryUtil helper class provides a few methods to convert between plane equations and vertices.
 *
 * @author jezek2
 */
public class GeometryUtil {

	/**
	 * Checks if is point inside planes.
	 *
	 * @param planeEquations the plane equations
	 * @param point the point
	 * @param margin the margin
	 * @return true, if is point inside planes
	 */
	public static boolean isPointInsidePlanes(final ObjectArrayList<Vector4f> planeEquations, final Vector3f point,
			final float margin) {
		int numbrushes = planeEquations.size();
		for (int i = 0; i < numbrushes; i++) {
			Vector4f N1 = planeEquations.getQuick(i);
			float dist = VectorUtil.dot3(N1, point) + N1.w - margin;
			if (dist > 0f) return false;
		}
		return true;
	}

	/**
	 * Are vertices behind plane.
	 *
	 * @param planeNormal the plane normal
	 * @param vertices the vertices
	 * @param margin the margin
	 * @return true, if successful
	 */
	public static boolean areVerticesBehindPlane(final Vector4f planeNormal, final ObjectArrayList<Vector3f> vertices,
			final float margin) {
		int numvertices = vertices.size();
		for (int i = 0; i < numvertices; i++) {
			Vector3f N1 = vertices.getQuick(i);
			float dist = VectorUtil.dot3(planeNormal, N1) + planeNormal.w - margin;
			if (dist > 0f) return false;
		}
		return true;
	}

	/**
	 * Not exist.
	 *
	 * @param planeEquation the plane equation
	 * @param planeEquations the plane equations
	 * @return true, if successful
	 */
	private static boolean notExist(final Vector4f planeEquation, final ObjectArrayList<Vector4f> planeEquations) {
		int numbrushes = planeEquations.size();
		for (int i = 0; i < numbrushes; i++) {
			Vector4f N1 = planeEquations.getQuick(i);
			if (VectorUtil.dot3(planeEquation, N1) > 0.999f) return false;
		}
		return true;
	}

	/**
	 * Gets the plane equations from vertices.
	 *
	 * @param vertices the vertices
	 * @param planeEquationsOut the plane equations out
	 * @return the plane equations from vertices
	 */
	public static void getPlaneEquationsFromVertices(final ObjectArrayList<Vector3f> vertices,
			final ObjectArrayList<Vector4f> planeEquationsOut) {
		Vector4f planeEquation = VECTORS4.get();
		Vector3f edge0 = VECTORS.get(), edge1 = VECTORS.get();
		Vector3f tmp = VECTORS.get();

		int numvertices = vertices.size();
		// brute force:
		for (int i = 0; i < numvertices; i++) {
			Vector3f N1 = vertices.getQuick(i);

			for (int j = i + 1; j < numvertices; j++) {
				Vector3f N2 = vertices.getQuick(j);

				for (int k = j + 1; k < numvertices; k++) {
					Vector3f N3 = vertices.getQuick(k);

					edge0.sub(N2, N1);
					edge1.sub(N3, N1);
					float normalSign = 1f;
					for (int ww = 0; ww < 2; ww++) {
						tmp.cross(edge0, edge1);
						planeEquation.x = normalSign * tmp.x;
						planeEquation.y = normalSign * tmp.y;
						planeEquation.z = normalSign * tmp.z;

						if (VectorUtil.lengthSquared3(planeEquation) > 0.0001f) {
							VectorUtil.normalize3(planeEquation);
							if (notExist(planeEquation, planeEquationsOut)) {
								planeEquation.w = -VectorUtil.dot3(planeEquation, N1);

								// check if inside, and replace supportingVertexOut if needed
								if (areVerticesBehindPlane(planeEquation, vertices, 0.01f)) {
									planeEquationsOut.add(new Vector4f(planeEquation));
								}
							}
						}
						normalSign = -1f;
					}
				}
			}
		}
		VECTORS4.release(planeEquation);
		VECTORS.release(tmp, edge0, edge1);
	}

	/**
	 * Gets the vertices from plane equations.
	 *
	 * @param planeEquations the plane equations
	 * @param verticesOut the vertices out
	 * @return the vertices from plane equations
	 */
	public static void getVerticesFromPlaneEquations(final ObjectArrayList<Vector4f> planeEquations,
			final ObjectArrayList<Vector3f> verticesOut) {
		Vector3f n2n3 = VECTORS.get();
		Vector3f n3n1 = VECTORS.get();
		Vector3f n1n2 = VECTORS.get();
		Vector3f potentialVertex = VECTORS.get();

		int numbrushes = planeEquations.size();
		// brute force:
		for (int i = 0; i < numbrushes; i++) {
			Vector4f N1 = planeEquations.getQuick(i);

			for (int j = i + 1; j < numbrushes; j++) {
				Vector4f N2 = planeEquations.getQuick(j);

				for (int k = j + 1; k < numbrushes; k++) {
					Vector4f N3 = planeEquations.getQuick(k);

					VectorUtil.cross3(n2n3, N2, N3);
					VectorUtil.cross3(n3n1, N3, N1);
					VectorUtil.cross3(n1n2, N1, N2);

					if (n2n3.lengthSquared() > 0.0001f && n3n1.lengthSquared() > 0.0001f
							&& n1n2.lengthSquared() > 0.0001f) {
						// point P out of 3 plane equations:

						// d1 ( N2 * N3 ) + d2 ( N3 * N1 ) + d3 ( N1 * N2 )
						// P = -------------------------------------------------------------------------
						// N1 . ( N2 * N3 )

						float quotient = VectorUtil.dot3(N1, n2n3);
						if (Math.abs(quotient) > 0.000001f) {
							quotient = -1f / quotient;
							n2n3.scale(N1.w);
							n3n1.scale(N2.w);
							n1n2.scale(N3.w);
							potentialVertex.set(n2n3);
							potentialVertex.add(n3n1);
							potentialVertex.add(n1n2);
							potentialVertex.scale(quotient);

							// check if inside, and replace supportingVertexOut if needed
							if (isPointInsidePlanes(planeEquations, potentialVertex, 0.01f)) {
								verticesOut.add(new Vector3f(potentialVertex));
							}
						}
					}
				}
			}
		}
		VECTORS.release(n2n3, n3n1, n1n2, potentialVertex);
	}

}
