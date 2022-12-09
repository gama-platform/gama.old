/*******************************************************************************************************
 *
 * Quantization.java, in simtools.gaml.extensions.physics, is part of the source code of the
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

import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
class Quantization {

	/**
	 * Bt calc quantization parameters.
	 *
	 * @param outMinBound the out min bound
	 * @param outMaxBound the out max bound
	 * @param bvhQuantization the bvh quantization
	 * @param srcMinBound the src min bound
	 * @param srcMaxBound the src max bound
	 * @param quantizationMargin the quantization margin
	 */
	public static void bt_calc_quantization_parameters(final Vector3f outMinBound, final Vector3f outMaxBound,
			final Vector3f bvhQuantization, final Vector3f srcMinBound, final Vector3f srcMaxBound,
			final float quantizationMargin) {
		// enlarge the AABB to avoid division by zero when initializing the quantization values
		Vector3f clampValue = VECTORS.get();
		clampValue.set(quantizationMargin, quantizationMargin, quantizationMargin);
		outMinBound.sub(srcMinBound, clampValue);
		outMaxBound.add(srcMaxBound, clampValue);
		Vector3f aabbSize = VECTORS.get();
		aabbSize.sub(outMaxBound, outMinBound);
		bvhQuantization.set(65535.0f, 65535.0f, 65535.0f);
		VectorUtil.div(bvhQuantization, bvhQuantization, aabbSize);
		VECTORS.release(aabbSize, clampValue);

	}

	/**
	 * Bt quantize clamp.
	 *
	 * @param out the out
	 * @param point the point
	 * @param min_bound the min bound
	 * @param max_bound the max bound
	 * @param bvhQuantization the bvh quantization
	 */
	public static void bt_quantize_clamp(final short[] out, final Vector3f point, final Vector3f min_bound,
			final Vector3f max_bound, final Vector3f bvhQuantization) {
		Vector3f clampedPoint = VECTORS.get(point);
		VectorUtil.setMax(clampedPoint, min_bound);
		VectorUtil.setMin(clampedPoint, max_bound);

		Vector3f v = VECTORS.get();
		v.sub(clampedPoint, min_bound);
		VectorUtil.mul(v, v, bvhQuantization);

		out[0] = (short) (v.x + 0.5f);
		out[1] = (short) (v.y + 0.5f);
		out[2] = (short) (v.z + 0.5f);
		VECTORS.release(v, clampedPoint);
	}

	/**
	 * Bt unquantize.
	 *
	 * @param vecIn the vec in
	 * @param offset the offset
	 * @param bvhQuantization the bvh quantization
	 * @param out the out
	 * @return the vector 3 f
	 */
	public static Vector3f bt_unquantize(final short[] vecIn, final Vector3f offset, final Vector3f bvhQuantization,
			final Vector3f out) {
		out.set((vecIn[0] & 0xFFFF) / bvhQuantization.x, (vecIn[1] & 0xFFFF) / bvhQuantization.y,
				(vecIn[2] & 0xFFFF) / bvhQuantization.z);
		out.add(offset);
		return out;
	}

}
