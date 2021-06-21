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

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Utility functions for vectors.
 * 
 * @author jezek2
 */
public class VectorUtil {

	public static int maxAxis(Vector3f v) {
		int maxIndex = -1;
		float maxVal = -1e30f;
		if (v.x > maxVal) {
			maxIndex = 0;
			maxVal = v.x;
		}
		if (v.y > maxVal) {
			maxIndex = 1;
			maxVal = v.y;
		}
		if (v.z > maxVal) {
			maxIndex = 2;
			maxVal = v.z;
		}

		return maxIndex;
	}
	
	public static int maxAxis4(Vector4f v) {
		int maxIndex = -1;
		float maxVal = -1e30f;
		if (v.x > maxVal) {
			maxIndex = 0;
			maxVal = v.x;
		}
		if (v.y > maxVal) {
			maxIndex = 1;
			maxVal = v.y;
		}
		if (v.z > maxVal) {
			maxIndex = 2;
			maxVal = v.z;
		}
		if (v.w > maxVal) {
			maxIndex = 3;
			maxVal = v.w;
		}

		return maxIndex;
	}

	public static int closestAxis4(Vector4f vec) {
		Vector4f tmp = new Vector4f(vec);
		tmp.absolute();
		return maxAxis4(tmp);
	}
	
	public static float getCoord(Vector3f vec, int num) {
		switch (num) {
			case 0: return vec.x;
			case 1: return vec.y;
			case 2: return vec.z;
			default: throw new InternalError();
		}
	}
	
	public static void setCoord(Vector3f vec, int num, float value) {
		switch (num) {
			case 0: vec.x = value; break;
			case 1: vec.y = value; break;
			case 2: vec.z = value; break;
			default: throw new InternalError();
		}
	}

	public static void mulCoord(Vector3f vec, int num, float value) {
		switch (num) {
			case 0: vec.x *= value; break;
			case 1: vec.y *= value; break;
			case 2: vec.z *= value; break;
			default: throw new InternalError();
		}
	}

	public static void setInterpolate3(Vector3f dest, Vector3f v0, Vector3f v1, float rt) {
		float s = 1f - rt;
		dest.x = s * v0.x + rt * v1.x;
		dest.y = s * v0.y + rt * v1.y;
		dest.z = s * v0.z + rt * v1.z;
		// don't do the unused w component
		//		m_co[3] = s * v0[3] + rt * v1[3];
	}

	public static void add(Vector3f dest, Vector3f v1, Vector3f v2) {
		dest.x = v1.x + v2.x;
		dest.y = v1.y + v2.y;
		dest.z = v1.z + v2.z;
	}
	
	public static void add(Vector3f dest, Vector3f v1, Vector3f v2, Vector3f v3) {
		dest.x = v1.x + v2.x + v3.x;
		dest.y = v1.y + v2.y + v3.y;
		dest.z = v1.z + v2.z + v3.z;
	}
	
	public static void add(Vector3f dest, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
		dest.x = v1.x + v2.x + v3.x + v4.x;
		dest.y = v1.y + v2.y + v3.y + v4.y;
		dest.z = v1.z + v2.z + v3.z + v4.z;
	}
	
	public static void mul(Vector3f dest, Vector3f v1, Vector3f v2) {
		dest.x = v1.x * v2.x;
		dest.y = v1.y * v2.y;
		dest.z = v1.z * v2.z;
	}
	
	public static void div(Vector3f dest, Vector3f v1, Vector3f v2) {
		dest.x = v1.x / v2.x;
		dest.y = v1.y / v2.y;
		dest.z = v1.z / v2.z;
	}
	
	public static void setMin(Vector3f a, Vector3f b) {
		a.x = Math.min(a.x, b.x);
		a.y = Math.min(a.y, b.y);
		a.z = Math.min(a.z, b.z);
	}
	
	public static void setMax(Vector3f a, Vector3f b) {
		a.x = Math.max(a.x, b.x);
		a.y = Math.max(a.y, b.y);
		a.z = Math.max(a.z, b.z);
	}
	
	public static float dot3(Vector4f v0, Vector3f v1) {
		return (v0.x*v1.x + v0.y*v1.y + v0.z*v1.z);
	}

	public static float dot3(Vector4f v0, Vector4f v1) {
		return (v0.x*v1.x + v0.y*v1.y + v0.z*v1.z);
	}

	public static float dot3(Vector3f v0, Vector4f v1) {
		return (v0.x*v1.x + v0.y*v1.y + v0.z*v1.z);
	}

	public static float lengthSquared3(Vector4f v) {
		return (v.x*v.x + v.y*v.y + v.z*v.z);
	}

	public static void normalize3(Vector4f v) {
		float norm = (float)(1.0/Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z));
		v.x *= norm;
		v.y *= norm;
		v.z *= norm;
	}

	public static void cross3(Vector3f dest, Vector4f v1, Vector4f v2) {
        float x,y;
        x = v1.y*v2.z - v1.z*v2.y;
        y = v2.x*v1.z - v2.z*v1.x;
        dest.z = v1.x*v2.y - v1.y*v2.x;
        dest.x = x;
        dest.y = y;
	}
	
}
