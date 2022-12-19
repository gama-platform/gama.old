/*******************************************************************************************************
 *
 * CollisionShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/**
	 * Gets the aabb.
	 *
	 * @param t the t
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @return the aabb
	 */
	/// getAabb returns the axis aligned bounding box in the coordinate frame of the given transform t.
	void getAabb(Transform t, Vector3f aabbMin, Vector3f aabbMax);

	/**
	 * Gets the bounding sphere.
	 *
	 * @param center the center
	 * @param radius the radius
	 * @return the bounding sphere
	 */
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
	/**
	 * Gets the angular motion disc.
	 *
	 * @return the angular motion disc
	 */
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
	/**
	 * Calculate temporal aabb.
	 *
	 * @param curTrans the cur trans
	 * @param linvel the linvel
	 * @param angvel the angvel
	 * @param timeStep the time step
	 * @param temporalAabbMin the temporal aabb min
	 * @param temporalAabbMax the temporal aabb max
	 */
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

	/**
	 * Checks if is polyhedral.
	 *
	 * @return true, if is polyhedral
	 */
	// #ifndef __SPU__
	default boolean isPolyhedral() {
		return getShapeType().isPolyhedral();
	}

	/**
	 * Checks if is convex.
	 *
	 * @return true, if is convex
	 */
	default boolean isConvex() {
		return getShapeType().isConvex();
	}

	/**
	 * Checks if is concave.
	 *
	 * @return true, if is concave
	 */
	default boolean isConcave() {
		return getShapeType().isConcave();
	}

	/**
	 * Checks if is compound.
	 *
	 * @return true, if is compound
	 */
	default boolean isCompound() {
		return getShapeType().isCompound();
	}

	/**
	 * Checks if is infinite.
	 *
	 * @return true, if is infinite
	 */
	/// isInfinite is used to catch simulation error (aabb check)
	default boolean isInfinite() {
		return getShapeType().isInfinite();
	}

	/**
	 * Gets the shape type.
	 *
	 * @return the shape type
	 */
	BroadphaseNativeType getShapeType();

	/**
	 * Sets the local scaling.
	 *
	 * @param scaling the new local scaling
	 */
	void setLocalScaling( Vector3f scaling);

	/**
	 * Gets the local scaling.
	 *
	 * @param out the out
	 * @return the local scaling
	 */
	// TODO: returns const
	Vector3f getLocalScaling(Vector3f out);

	/**
	 * Calculate local inertia.
	 *
	 * @param mass the mass
	 * @param inertia the inertia
	 */
	void calculateLocalInertia(float mass, Vector3f inertia);

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	// debugging support
	String getName();

	/**
	 * Sets the margin.
	 *
	 * @param margin the new margin
	 */
	// #endif //__SPU__
	void setMargin(float margin);

	/**
	 * Gets the margin.
	 *
	 * @return the margin
	 */
	float getMargin();

}
