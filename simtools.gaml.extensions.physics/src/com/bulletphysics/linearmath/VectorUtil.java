/*******************************************************************************************************
 *
 * VectorUtil.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Utility functions for vectors.
 * 
 * @author jezek2
 */
public class VectorUtil {

	/**
	 * Max axis.
	 *
	 * @param v the v
	 * @return the int
	 */
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
	
	/**
	 * Max axis 4.
	 *
	 * @param v the v
	 * @return the int
	 */
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

	/**
	 * Closest axis 4.
	 *
	 * @param vec the vec
	 * @return the int
	 */
	public static int closestAxis4(Vector4f vec) {
		Vector4f tmp = new Vector4f(vec);
		tmp.absolute();
		return maxAxis4(tmp);
	}
	
	/**
	 * Gets the coord.
	 *
	 * @param vec the vec
	 * @param num the num
	 * @return the coord
	 */
	public static float getCoord(Vector3f vec, int num) {
		switch (num) {
			case 0: return vec.x;
			case 1: return vec.y;
			case 2: return vec.z;
			default: throw new InternalError();
		}
	}
	
	/**
	 * Sets the coord.
	 *
	 * @param vec the vec
	 * @param num the num
	 * @param value the value
	 */
	public static void setCoord(Vector3f vec, int num, float value) {
		switch (num) {
			case 0: vec.x = value; break;
			case 1: vec.y = value; break;
			case 2: vec.z = value; break;
			default: throw new InternalError();
		}
	}

	/**
	 * Mul coord.
	 *
	 * @param vec the vec
	 * @param num the num
	 * @param value the value
	 */
	public static void mulCoord(Vector3f vec, int num, float value) {
		switch (num) {
			case 0: vec.x *= value; break;
			case 1: vec.y *= value; break;
			case 2: vec.z *= value; break;
			default: throw new InternalError();
		}
	}

	/**
	 * Sets the interpolate 3.
	 *
	 * @param dest the dest
	 * @param v0 the v 0
	 * @param v1 the v 1
	 * @param rt the rt
	 */
	public static void setInterpolate3(Vector3f dest, Vector3f v0, Vector3f v1, float rt) {
		float s = 1f - rt;
		dest.x = s * v0.x + rt * v1.x;
		dest.y = s * v0.y + rt * v1.y;
		dest.z = s * v0.z + rt * v1.z;
		// don't do the unused w component
		//		m_co[3] = s * v0[3] + rt * v1[3];
	}

	/**
	 * Adds the.
	 *
	 * @param dest the dest
	 * @param v1 the v 1
	 * @param v2 the v 2
	 */
	public static void add(Vector3f dest, Vector3f v1, Vector3f v2) {
		dest.x = v1.x + v2.x;
		dest.y = v1.y + v2.y;
		dest.z = v1.z + v2.z;
	}
	
	/**
	 * Adds the.
	 *
	 * @param dest the dest
	 * @param v1 the v 1
	 * @param v2 the v 2
	 * @param v3 the v 3
	 */
	public static void add(Vector3f dest, Vector3f v1, Vector3f v2, Vector3f v3) {
		dest.x = v1.x + v2.x + v3.x;
		dest.y = v1.y + v2.y + v3.y;
		dest.z = v1.z + v2.z + v3.z;
	}
	
	/**
	 * Adds the.
	 *
	 * @param dest the dest
	 * @param v1 the v 1
	 * @param v2 the v 2
	 * @param v3 the v 3
	 * @param v4 the v 4
	 */
	public static void add(Vector3f dest, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
		dest.x = v1.x + v2.x + v3.x + v4.x;
		dest.y = v1.y + v2.y + v3.y + v4.y;
		dest.z = v1.z + v2.z + v3.z + v4.z;
	}
	
	/**
	 * Mul.
	 *
	 * @param dest the dest
	 * @param v1 the v 1
	 * @param v2 the v 2
	 */
	public static void mul(Vector3f dest, Vector3f v1, Vector3f v2) {
		dest.x = v1.x * v2.x;
		dest.y = v1.y * v2.y;
		dest.z = v1.z * v2.z;
	}
	
	/**
	 * Div.
	 *
	 * @param dest the dest
	 * @param v1 the v 1
	 * @param v2 the v 2
	 */
	public static void div(Vector3f dest, Vector3f v1, Vector3f v2) {
		dest.x = v1.x / v2.x;
		dest.y = v1.y / v2.y;
		dest.z = v1.z / v2.z;
	}
	
	/**
	 * Sets the min.
	 *
	 * @param a the a
	 * @param b the b
	 */
	public static void setMin(Vector3f a, Vector3f b) {
		a.x = Math.min(a.x, b.x);
		a.y = Math.min(a.y, b.y);
		a.z = Math.min(a.z, b.z);
	}
	
	/**
	 * Sets the max.
	 *
	 * @param a the a
	 * @param b the b
	 */
	public static void setMax(Vector3f a, Vector3f b) {
		a.x = Math.max(a.x, b.x);
		a.y = Math.max(a.y, b.y);
		a.z = Math.max(a.z, b.z);
	}
	
	/**
	 * Dot 3.
	 *
	 * @param v0 the v 0
	 * @param v1 the v 1
	 * @return the float
	 */
	public static float dot3(Vector4f v0, Vector3f v1) {
		return (v0.x*v1.x + v0.y*v1.y + v0.z*v1.z);
	}

	/**
	 * Dot 3.
	 *
	 * @param v0 the v 0
	 * @param v1 the v 1
	 * @return the float
	 */
	public static float dot3(Vector4f v0, Vector4f v1) {
		return (v0.x*v1.x + v0.y*v1.y + v0.z*v1.z);
	}

	/**
	 * Dot 3.
	 *
	 * @param v0 the v 0
	 * @param v1 the v 1
	 * @return the float
	 */
	public static float dot3(Vector3f v0, Vector4f v1) {
		return (v0.x*v1.x + v0.y*v1.y + v0.z*v1.z);
	}

	/**
	 * Length squared 3.
	 *
	 * @param v the v
	 * @return the float
	 */
	public static float lengthSquared3(Vector4f v) {
		return (v.x*v.x + v.y*v.y + v.z*v.z);
	}

	/**
	 * Normalize 3.
	 *
	 * @param v the v
	 */
	public static void normalize3(Vector4f v) {
		float norm = (float)(1.0/Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z));
		v.x *= norm;
		v.y *= norm;
		v.z *= norm;
	}

	/**
	 * Cross 3.
	 *
	 * @param dest the dest
	 * @param v1 the v 1
	 * @param v2 the v 2
	 */
	public static void cross3(Vector3f dest, Vector4f v1, Vector4f v2) {
        float x,y;
        x = v1.y*v2.z - v1.z*v2.y;
        y = v2.x*v1.z - v2.z*v1.x;
        dest.z = v1.x*v2.y - v1.y*v2.x;
        dest.x = x;
        dest.y = y;
	}
	
}
