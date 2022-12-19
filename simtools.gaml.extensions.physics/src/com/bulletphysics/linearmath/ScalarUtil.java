/*******************************************************************************************************
 *
 * ScalarUtil.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

import com.bulletphysics.BulletGlobals;

/**
 * Utility functions for scalars (floats).
 * 
 * @author jezek2
 */
public class ScalarUtil {

	/**
	 * Fsel.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @return the float
	 */
	public static float fsel(float a, float b, float c) {
		return a >= 0 ? b : c;
	}
	
	/**
	 * Fuzzy zero.
	 *
	 * @param x the x
	 * @return true, if successful
	 */
	public static boolean fuzzyZero(float x) {
		return Math.abs(x) < BulletGlobals.FLT_EPSILON;
	}

	/**
	 * Atan 2 fast.
	 *
	 * @param y the y
	 * @param x the x
	 * @return the float
	 */
	public static float atan2Fast(float y, float x) {
		float coeff_1 = BulletGlobals.SIMD_PI / 4.0f;
		float coeff_2 = 3.0f * coeff_1;
		float abs_y = Math.abs(y);
		float angle;
		if (x >= 0.0f) {
			float r = (x - abs_y) / (x + abs_y);
			angle = coeff_1 - coeff_1 * r;
		}
		else {
			float r = (x + abs_y) / (abs_y - x);
			angle = coeff_2 - coeff_1 * r;
		}
		return (y < 0.0f) ? -angle : angle;
	}

}
