/*******************************************************************************************************
 *
 * QuaternionUtil.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

import static com.bulletphysics.Pools.QUATS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;

/**
 * Utility functions for quaternions.
 *
 * @author jezek2
 */
public class QuaternionUtil {

	/**
	 * Gets the angle.
	 *
	 * @param q the q
	 * @return the angle
	 */
	public static float getAngle(final Quat4f q) {
		float s = 2f * (float) Math.acos(q.w);
		return s;
	}

	/**
	 * Sets the rotation.
	 *
	 * @param q the q
	 * @param axis the axis
	 * @param angle the angle
	 */
	public static void setRotation(final Quat4f q, final Vector3f axis, final float angle) {
		float d = axis.length();
		assert d != 0f;
		float s = (float) Math.sin(angle * 0.5f) / d;
		q.set(axis.x * s, axis.y * s, axis.z * s, (float) Math.cos(angle * 0.5f));
	}

	/**
	 * Shortest arc quat.
	 *
	 * @param v0 the v 0
	 * @param v1 the v 1
	 * @param out the out
	 * @return the quat 4 f
	 */
	// Game Programming Gems 2.10. make sure v0,v1 are normalized
	public static Quat4f shortestArcQuat(final Vector3f v0, final Vector3f v1, final Quat4f out) {
		Vector3f c = VECTORS.get();
		c.cross(v0, v1);
		float d = v0.dot(v1);

		if (d < -1.0 + BulletGlobals.FLT_EPSILON) {
			// just pick any vector
			out.set(0.0f, 1.0f, 0.0f, 0.0f);
			return out;
		}

		float s = (float) Math.sqrt((1.0f + d) * 2.0f);
		float rs = 1.0f / s;

		out.set(c.x * rs, c.y * rs, c.z * rs, s * 0.5f);
		VECTORS.release(c);
		return out;
	}

	/**
	 * Mul.
	 *
	 * @param q the q
	 * @param w the w
	 */
	public static void mul(final Quat4f q, final Vector3f w) {
		float rx = q.w * w.x + q.y * w.z - q.z * w.y;
		float ry = q.w * w.y + q.z * w.x - q.x * w.z;
		float rz = q.w * w.z + q.x * w.y - q.y * w.x;
		float rw = -q.x * w.x - q.y * w.y - q.z * w.z;
		q.set(rx, ry, rz, rw);
	}

	/**
	 * Quat rotate.
	 *
	 * @param rotation the rotation
	 * @param v the v
	 * @param out the out
	 * @return the vector 3 f
	 */
	public static Vector3f quatRotate(final Quat4f rotation, final Vector3f v, final Vector3f out) {
		Quat4f q = QUATS.get(rotation);
		QuaternionUtil.mul(q, v);

		Quat4f tmp = QUATS.get();
		inverse(tmp, rotation);
		q.mul(tmp);

		out.set(q.x, q.y, q.z);
		QUATS.release(tmp, q);
		return out;
	}

	/**
	 * Inverse.
	 *
	 * @param q the q
	 */
	public static void inverse(final Quat4f q) {
		q.x = -q.x;
		q.y = -q.y;
		q.z = -q.z;
	}

	/**
	 * Inverse.
	 *
	 * @param q the q
	 * @param src the src
	 */
	public static void inverse(final Quat4f q, final Quat4f src) {
		q.x = -src.x;
		q.y = -src.y;
		q.z = -src.z;
		q.w = src.w;
	}

	/**
	 * Sets the euler.
	 *
	 * @param q the q
	 * @param yaw the yaw
	 * @param pitch the pitch
	 * @param roll the roll
	 */
	public static void setEuler(final Quat4f q, final float yaw, final float pitch, final float roll) {
		float halfYaw = yaw * 0.5f;
		float halfPitch = pitch * 0.5f;
		float halfRoll = roll * 0.5f;
		float cosYaw = (float) Math.cos(halfYaw);
		float sinYaw = (float) Math.sin(halfYaw);
		float cosPitch = (float) Math.cos(halfPitch);
		float sinPitch = (float) Math.sin(halfPitch);
		float cosRoll = (float) Math.cos(halfRoll);
		float sinRoll = (float) Math.sin(halfRoll);
		q.x = cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw;
		q.y = cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw;
		q.z = sinRoll * cosPitch * cosYaw - cosRoll * sinPitch * sinYaw;
		q.w = cosRoll * cosPitch * cosYaw + sinRoll * sinPitch * sinYaw;
	}

}
