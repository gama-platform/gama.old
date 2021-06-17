/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.linearmath;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.QUATS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;

/**
 * Utility functions for transforms.
 *
 * @author jezek2
 */
public class TransformUtil {

	public static final float SIMDSQRT12 = 0.7071067811865475244008443621048490f;
	public static final float ANGULAR_MOTION_THRESHOLD = 0.5f * BulletGlobals.SIMD_HALF_PI;

	public static float recipSqrt(final float x) {
		return 1f / (float) Math.sqrt(x); /* reciprocal square root */
	}

	public static void planeSpace1(final Vector3f n, final Vector3f p, final Vector3f q) {
		if (Math.abs(n.z) > SIMDSQRT12) {
			// choose p in y-z plane
			float a = n.y * n.y + n.z * n.z;
			float k = recipSqrt(a);
			p.set(0, -n.z * k, n.y * k);
			// set q = n x p
			q.set(a * k, -n.x * p.z, n.x * p.y);
		} else {
			// choose p in x-y plane
			float a = n.x * n.x + n.y * n.y;
			float k = recipSqrt(a);
			p.set(-n.y * k, n.x * k, 0);
			// set q = n x p
			q.set(-n.z * p.y, n.z * p.x, a * k);
		}
	}

	public static void integrateTransform(final Transform curTrans, final Vector3f linvel, final Vector3f angvel,
			final float timeStep, final Transform predictedTransform) {
		predictedTransform.origin.scaleAdd(timeStep, linvel, curTrans.origin);

		Vector3f axis = VECTORS.get();
		float fAngle = angvel.length();

		// limit the angular motion
		if (fAngle * timeStep > ANGULAR_MOTION_THRESHOLD) { fAngle = ANGULAR_MOTION_THRESHOLD / timeStep; }

		if (fAngle < 0.001f) {
			// use Taylor's expansions of sync function
			axis.scale(0.5f * timeStep - timeStep * timeStep * timeStep * 0.020833333333f * fAngle * fAngle, angvel);
		} else {
			// sync(fAngle) = sin(c*fAngle)/t
			axis.scale((float) Math.sin(0.5f * fAngle * timeStep) / fAngle, angvel);
		}
		Quat4f dorn = QUATS.get();
		dorn.set(axis.x, axis.y, axis.z, (float) Math.cos(fAngle * timeStep * 0.5f));
		Quat4f orn0 = curTrans.getRotation(QUATS.get());

		Quat4f predictedOrn = QUATS.get();
		predictedOrn.mul(dorn, orn0);
		predictedOrn.normalize();
		// #endif
		predictedTransform.setRotation(predictedOrn);
		QUATS.release(dorn, orn0, predictedOrn);
		VECTORS.release(axis);
	}

	public static void calculateVelocity(final Transform transform0, final Transform transform1, final float timeStep,
			final Vector3f linVel, final Vector3f angVel) {
		linVel.sub(transform1.origin, transform0.origin);
		linVel.scale(1f / timeStep);

		Vector3f axis = VECTORS.get();
		float[] angle = new float[1];
		calculateDiffAxisAngle(transform0, transform1, axis, angle);
		angVel.scale(angle[0] / timeStep, axis);
	}

	public static void calculateDiffAxisAngle(final Transform transform0, final Transform transform1,
			final Vector3f axis, final float[] angle) {
		Matrix3f tmp = MATRICES.get();
		tmp.set(transform0.basis);
		MatrixUtil.invert(tmp);

		Matrix3f dmat = MATRICES.get();
		dmat.mul(transform1.basis, tmp);

		Quat4f dorn = QUATS.get();
		MatrixUtil.getRotation(dmat, dorn);

		// floating point inaccuracy can lead to w component > 1..., which breaks

		dorn.normalize();

		angle[0] = QuaternionUtil.getAngle(dorn);
		axis.set(dorn.x, dorn.y, dorn.z);
		// TODO: probably not needed
		// axis[3] = btScalar(0.);

		// check for axis length
		float len = axis.lengthSquared();
		if (len < BulletGlobals.FLT_EPSILON * BulletGlobals.FLT_EPSILON) {
			axis.set(1f, 0f, 0f);
		} else {
			axis.scale(1f / (float) Math.sqrt(len));
		}
		QUATS.release(dorn);
		MATRICES.release(tmp, dmat);
	}

}
