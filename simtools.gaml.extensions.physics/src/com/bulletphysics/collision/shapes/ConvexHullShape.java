/*******************************************************************************************************
 *
 * ConvexHullShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * ConvexHullShape implements an implicit convex hull of an array of vertices. Bullet provides a general and fast
 * collision detector for convex shapes based on GJK and EPA using localGetSupportingVertex.
 *
 * @author jezek2
 */
public class ConvexHullShape extends PolyhedralConvexShape {

	/** The points. */
	private final ArrayList<Vector3f> points = new ArrayList<>();

	/**
	 * Instantiates a new convex hull shape.
	 */
	public ConvexHullShape() {

	}

	/**
	 * TODO: This constructor optionally takes in a pointer to points. Each point is assumed to be 3 consecutive float
	 * (x,y,z), the striding defines the number of bytes between each point, in memory. It is easier to not pass any
	 * points in the constructor, and just add one point at a time, using addPoint. ConvexHullShape make an internal
	 * copy of the points.
	 */
	// TODO: make better constuctors (ByteBuffer, etc.)
	public ConvexHullShape(final ArrayList<Vector3f> points) {
		// JAVA NOTE: rewritten

		for (int i = 0; i < points.size(); i++) {
			this.points.add(new Vector3f(points.get(i)));
		}

		recalcLocalAabb();
	}

	@Override
	public void setLocalScaling(final Vector3f scaling) {
		localScaling.set(scaling);
		recalcLocalAabb();
	}

	/**
	 * Adds the point.
	 *
	 * @param point the point
	 */
	public void addPoint(final Vector3f point) {
		points.add(new Vector3f(point));
		recalcLocalAabb();
	}

	/**
	 * Gets the points.
	 *
	 * @return the points
	 */
	public ArrayList<Vector3f> getPoints() {
		return points;
	}

	/**
	 * Gets the num points.
	 *
	 * @return the num points
	 */
	public int getNumPoints() {
		return points.size();
	}

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec0, final Vector3f out) {
		Vector3f supVec = out;
		supVec.set(0f, 0f, 0f);
		float newDot, maxDot = -1e30f;

		Vector3f vec = VECTORS.get(vec0);
		float lenSqr = vec.lengthSquared();
		if (lenSqr < 0.0001f) {
			vec.set(1f, 0f, 0f);
		} else {
			float rlen = 1f / (float) Math.sqrt(lenSqr);
			vec.scale(rlen);
		}

		Vector3f vtx = VECTORS.get();
		for (Vector3f point : points) {
			VectorUtil.mul(vtx, point, localScaling);

			newDot = vec.dot(vtx);
			if (newDot > maxDot) {
				maxDot = newDot;
				supVec.set(vtx);
			}
		}
		return out;
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		float newDot;

		// JAVA NOTE: rewritten as code used W coord for temporary usage in Vector3
		// TODO: optimize it
		float[] wcoords = new float[numVectors];

		// use 'w' component of supportVerticesOut?
		{
			for (int i = 0; i < numVectors; i++) {
				// supportVerticesOut[i][3] = btScalar(-1e30);
				wcoords[i] = -1e30f;
			}
		}
		Vector3f vtx = VECTORS.get();
		for (Vector3f point : points) {
			VectorUtil.mul(vtx, point, localScaling);

			for (int j = 0; j < numVectors; j++) {
				Vector3f vec = vectors[j];

				newDot = vec.dot(vtx);
				// if (newDot > supportVerticesOut[j][3])
				if (newDot > wcoords[j]) {
					// WARNING: don't swap next lines, the w component would get overwritten!
					supportVerticesOut[j].set(vtx);
					// supportVerticesOut[j][3] = newDot;
					wcoords[j] = newDot;
				}
			}
		}
	}

	@Override
	public Vector3f localGetSupportingVertex(final Vector3f vec, final Vector3f out) {
		Vector3f supVertex = localGetSupportingVertexWithoutMargin(vec, out);

		if (getMargin() != 0f) {
			Vector3f vecnorm = VECTORS.get(vec);
			if (vecnorm.lengthSquared() < BulletGlobals.FLT_EPSILON * BulletGlobals.FLT_EPSILON) {
				vecnorm.set(-1f, -1f, -1f);
			}
			vecnorm.normalize();
			supVertex.scaleAdd(getMargin(), vecnorm, supVertex);
		}
		return out;
	}

	/**
	 * Currently just for debugging (drawing), perhaps future support for algebraic continuous collision detection.
	 * Please note that you can debug-draw ConvexHullShape with the Raytracer Demo.
	 */
	@Override
	public int getNumVertices() {
		return points.size();
	}

	@Override
	public int getNumEdges() {
		return points.size();
	}

	@Override
	public void getEdge(final int i, final Vector3f pa, final Vector3f pb) {
		int index0 = i % points.size();
		int index1 = (i + 1) % points.size();
		VectorUtil.mul(pa, points.get(index0), localScaling);
		VectorUtil.mul(pb, points.get(index1), localScaling);
	}

	@Override
	public void getVertex(final int i, final Vector3f vtx) {
		VectorUtil.mul(vtx, points.get(i), localScaling);
	}

	@Override
	public int getNumPlanes() {
		return 0;
	}

	@Override
	public void getPlane(final Vector3f planeNormal, final Vector3f planeSupport, final int i) {
		assert false;
	}

	@Override
	public boolean isInside(final Vector3f pt, final float tolerance) {
		assert false;
		return false;
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.CONVEX_HULL_SHAPE_PROXYTYPE;
	}

	@Override
	public String getName() {
		return "Convex";
	}

}
