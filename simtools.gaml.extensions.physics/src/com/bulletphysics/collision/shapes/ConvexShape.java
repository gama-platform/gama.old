/*******************************************************************************************************
 *
 * ConvexShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

/**
 * ConvexShape is an abstract shape class. It describes general convex shapes using the {@link #localGetSupportingVertex
 * localGetSupportingVertex} interface used in combination with GJK or ConvexCast.
 *
 * @author jezek2
 */
public interface ConvexShape extends CollisionShape {

	/** The max preferred penetration directions. */
	int MAX_PREFERRED_PENETRATION_DIRECTIONS = 10;

	/**
	 * Local get supporting vertex.
	 *
	 * @param vec the vec
	 * @param out the out
	 * @return the vector 3 f
	 */
	Vector3f localGetSupportingVertex(Vector3f vec, Vector3f out);

	/**
	 * Local get supporting vertex without margin.
	 *
	 * @param vec the vec
	 * @param out the out
	 * @return the vector 3 f
	 */
	Vector3f localGetSupportingVertexWithoutMargin(Vector3f vec, Vector3f out);

	/**
	 * Batched unit vector get supporting vertex without margin.
	 *
	 * @param vectors the vectors
	 * @param supportVerticesOut the support vertices out
	 * @param numVectors the num vectors
	 */
	void batchedUnitVectorGetSupportingVertexWithoutMargin(Vector3f[] vectors, Vector3f[] supportVerticesOut,
			int numVectors);

	/**
	 * Gets the aabb slow.
	 *
	 * @param t the t
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @return the aabb slow
	 */
	void getAabbSlow(Transform t, Vector3f aabbMin, Vector3f aabbMax);

	/**
	 * Gets the num preferred penetration directions.
	 *
	 * @return the num preferred penetration directions
	 */
	int getNumPreferredPenetrationDirections();

	/**
	 * Gets the preferred penetration direction.
	 *
	 * @param index the index
	 * @param penetrationVector the penetration vector
	 * @return the preferred penetration direction
	 */
	void getPreferredPenetrationDirection(int index, Vector3f penetrationVector);

}
