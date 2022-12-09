/*******************************************************************************************************
 *
 * GImpactTriangleCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.TriangleCallback;

/**
 *
 * @author jezek2
 */
class GImpactTriangleCallback implements TriangleCallback {

	/** The algorithm. */
	public GImpactCollisionAlgorithm algorithm;
	
	/** The body 0. */
	public CollisionObject body0;
	
	/** The body 1. */
	public CollisionObject body1;
	
	/** The gimpactshape 0. */
	public GImpactShapeInterface gimpactshape0;
	
	/** The swapped. */
	public boolean swapped;
	
	/** The margin. */
	public float margin;

	@Override
	public void processTriangle( final Vector3f[] triangle, final int partId,
			final int triangleIndex) {
		TriangleShapeEx tri1 = new TriangleShapeEx(triangle[0], triangle[1], triangle[2]);
		tri1.setMargin(margin);
		if (swapped) {
			algorithm.setPart0(partId);
			algorithm.setFace0(triangleIndex);
		} else {
			algorithm.setPart1(partId);
			algorithm.setFace1(triangleIndex);
		}
		algorithm.gimpact_vs_shape( body0, body1, gimpactshape0, tri1, swapped);
	}

}
