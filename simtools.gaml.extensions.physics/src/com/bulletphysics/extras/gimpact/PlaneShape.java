/*******************************************************************************************************
 *
 * PlaneShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
class PlaneShape {

	/**
	 * Gets the plane equation.
	 *
	 * @param shape the shape
	 * @param equation the equation
	 * @return the plane equation
	 */
	public static void get_plane_equation(final StaticPlaneShape shape, final Vector4f equation) {
		Vector3f tmp = VECTORS.get();
		equation.set(shape.getPlaneNormal(tmp));
		equation.w = shape.getPlaneConstant();
		VECTORS.release(tmp);

	}

	/**
	 * Gets the plane equation transformed.
	 *
	 * @param shape the shape
	 * @param trans the trans
	 * @param equation the equation
	 * @return the plane equation transformed
	 */
	public static void get_plane_equation_transformed(final StaticPlaneShape shape, final Transform trans,
			final Vector4f equation) {
		get_plane_equation(shape, equation);

		Vector3f tmp = VECTORS.get();

		trans.basis.getRow(0, tmp);
		float x = VectorUtil.dot3(tmp, equation);
		trans.basis.getRow(1, tmp);
		float y = VectorUtil.dot3(tmp, equation);
		trans.basis.getRow(2, tmp);
		float z = VectorUtil.dot3(tmp, equation);

		float w = VectorUtil.dot3(trans.origin, equation) + equation.w;

		equation.set(x, y, z, w);
		VECTORS.release(tmp);
	}

}
