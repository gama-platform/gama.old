/*******************************************************************************************************
 *
 * DefaultVehicleRaycaster.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.vehicle;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import javax.vecmath.Vector3f;

/**
 * Default implementation of {@link VehicleRaycaster}.
 * 
 * @author jezek2
 */
public class DefaultVehicleRaycaster extends VehicleRaycaster {

	/** The dynamics world. */
	protected DynamicsWorld dynamicsWorld;

	/**
	 * Instantiates a new default vehicle raycaster.
	 *
	 * @param world the world
	 */
	public DefaultVehicleRaycaster(DynamicsWorld world) {
		this.dynamicsWorld = world;
	}

	public Object castRay(Vector3f from, Vector3f to, VehicleRaycasterResult result) {
		//RayResultCallback& resultCallback;

		ClosestRayResultCallback rayCallback = new ClosestRayResultCallback(from, to);

		dynamicsWorld.rayTest(from, to, rayCallback);

		if (rayCallback.hasHit()) {
			RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
			if (body != null && body.hasContactResponse()) {
				result.hitPointInWorld.set(rayCallback.hitPointWorld);
				result.hitNormalInWorld.set(rayCallback.hitNormalWorld);
				result.hitNormalInWorld.normalize();
				result.distFraction = rayCallback.closestHitFraction;
				return body;
			}
		}
		return null;
	}

}
