/*******************************************************************************************************
 *
 * VertexData.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.VectorUtil;

/**
 * Allows accessing vertex data.
 *
 * @author jezek2
 */
public interface VertexData {

	/**
	 * Gets the vertex count.
	 *
	 * @return the vertex count
	 */
	int getVertexCount();

	/**
	 * Gets the index count.
	 *
	 * @return the index count
	 */
	int getIndexCount();

	/**
	 * Gets the vertex.
	 *
	 * @param <T> the generic type
	 * @param idx the idx
	 * @param out the out
	 * @return the vertex
	 */
	<T extends Tuple3f> T getVertex(int idx, T out);

	/**
	 * Sets the vertex.
	 *
	 * @param idx the idx
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	void setVertex(int idx, float x, float y, float z);

	/**
	 * Sets the vertex.
	 *
	 * @param idx the idx
	 * @param t the t
	 */
	default void setVertex(final int idx, final Tuple3f t) {
		setVertex(idx, t.x, t.y, t.z);
	}

	/**
	 * Gets the index.
	 *
	 * @param idx the idx
	 * @return the index
	 */
	int getIndex(int idx);

	/**
	 * Gets the triangle.
	 *
	 * @param firstIndex the first index
	 * @param scale the scale
	 * @param triangle the triangle
	 * @return the triangle
	 */
	default void getTriangle(final int firstIndex, final Vector3f scale, final Vector3f[] triangle) {
		for (int i = 0; i < 3; i++) {
			getVertex(getIndex(firstIndex + i), triangle[i]);
			VectorUtil.mul(triangle[i], triangle[i], scale);
		}
	}

}
