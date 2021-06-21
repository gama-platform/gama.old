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

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.Transform;

/**
 * CollisionShape class provides an interface for collision shapes that can be shared among {@link CollisionObject}s.
 *
 * @author jezek2
 */
public interface CollisionShape {

	/// getAabb returns the axis aligned bounding box in the coordinate frame of the given transform t.
	void getAabb(Transform t, Vector3f aabbMin, Vector3f aabbMax);

	default void getBoundingSphere(final Vector3f center, final float[] radius) {
		Vector3f tmp = VECTORS.get();

		Transform tr = TRANSFORMS.get();
		tr.setIdentity();
		Vector3f aabbMin = VECTORS.get(), aabbMax = VECTORS.get();

		getAabb(tr, aabbMin, aabbMax);

		tmp.sub(aabbMax, aabbMin);
		radius[0] = tmp.length() * 0.5f;

		tmp.add(aabbMin, aabbMax);
		center.scale(0.5f, tmp);
		VECTORS.release(tmp, aabbMin, aabbMax);
		TRANSFORMS.release(tr);
	}

	/// getAngularMotionDisc returns the maximus radius needed for Conservative Advancement to handle time-of-impact
	/// with rotations.
	default float getAngularMotionDisc() {
		Vector3f center = VECTORS.get();
		float[] disc = new float[1]; // TODO: stack
		getBoundingSphere(center, disc);
		disc[0] += center.length();
		VECTORS.release(center);
		return disc[0];
	}

	/// calculateTemporalAabb calculates the enclosing aabb for the moving object over interval [0..timeStep)
	/// result is conservative
	default void calculateTemporalAabb(final Transform curTrans, final Vector3f linvel, final Vector3f angvel,
			final float timeStep, final Vector3f temporalAabbMin, final Vector3f temporalAabbMax) {
		// start with static aabb
		getAabb(curTrans, temporalAabbMin, temporalAabbMax);

		float temporalAabbMaxx = temporalAabbMax.x;
		float temporalAabbMaxy = temporalAabbMax.y;
		float temporalAabbMaxz = temporalAabbMax.z;
		float temporalAabbMinx = temporalAabbMin.x;
		float temporalAabbMiny = temporalAabbMin.y;
		float temporalAabbMinz = temporalAabbMin.z;

		// add linear motion
		Vector3f linMotion = VECTORS.get(linvel);
		linMotion.scale(timeStep);

		// todo: simd would have a vector max/min operation, instead of per-element access
		if (linMotion.x > 0f) {
			temporalAabbMaxx += linMotion.x;
		} else {
			temporalAabbMinx += linMotion.x;
		}
		if (linMotion.y > 0f) {
			temporalAabbMaxy += linMotion.y;
		} else {
			temporalAabbMiny += linMotion.y;
		}
		if (linMotion.z > 0f) {
			temporalAabbMaxz += linMotion.z;
		} else {
			temporalAabbMinz += linMotion.z;
		}

		// add conservative angular motion
		float angularMotion = angvel.length() * getAngularMotionDisc() * timeStep;
		Vector3f angularMotion3d = VECTORS.get();
		angularMotion3d.set(angularMotion, angularMotion, angularMotion);
		temporalAabbMin.set(temporalAabbMinx, temporalAabbMiny, temporalAabbMinz);
		temporalAabbMax.set(temporalAabbMaxx, temporalAabbMaxy, temporalAabbMaxz);

		temporalAabbMin.sub(angularMotion3d);
		temporalAabbMax.add(angularMotion3d);
		VECTORS.release(angularMotion3d, linMotion);
	}

	// #ifndef __SPU__
	default boolean isPolyhedral() {
		return getShapeType().isPolyhedral();
	}

	default boolean isConvex() {
		return getShapeType().isConvex();
	}

	default boolean isConcave() {
		return getShapeType().isConcave();
	}

	default boolean isCompound() {
		return getShapeType().isCompound();
	}

	/// isInfinite is used to catch simulation error (aabb check)
	default boolean isInfinite() {
		return getShapeType().isInfinite();
	}

	BroadphaseNativeType getShapeType();

	void setLocalScaling( Vector3f scaling);

	// TODO: returns const
	Vector3f getLocalScaling(Vector3f out);

	void calculateLocalInertia(float mass, Vector3f inertia);

	// debugging support
	String getName();

	// #endif //__SPU__
	void setMargin(float margin);

	float getMargin();

}
