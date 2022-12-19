/*******************************************************************************************************
 *
 * InternalTriangleIndexCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;

/**
 * Callback for internal processing of triangles.
 *
 * @see StridingMeshInterface#internalProcessAllTriangles
 * @author jezek2
 */
@FunctionalInterface
public interface InternalTriangleIndexCallback {

	/**
	 * Internal process triangle index.
	 *
	 * @param triangle the triangle
	 * @param partId the part id
	 * @param triangleIndex the triangle index
	 */
	void internalProcessTriangleIndex( Vector3f[] triangle, int partId, int triangleIndex);

}
