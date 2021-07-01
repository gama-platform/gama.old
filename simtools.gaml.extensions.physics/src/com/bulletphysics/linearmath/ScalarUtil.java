/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.linearmath;

import com.bulletphysics.BulletGlobals;

/**
 * Utility functions for scalars (floats).
 * 
 * @author jezek2
 */
public class ScalarUtil {

	public static float fsel(float a, float b, float c) {
		return a >= 0 ? b : c;
	}
	
	public static boolean fuzzyZero(float x) {
		return Math.abs(x) < BulletGlobals.FLT_EPSILON;
	}

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
