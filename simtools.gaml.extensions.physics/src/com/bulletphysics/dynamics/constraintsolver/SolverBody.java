/*******************************************************************************************************
 *
 * SolverBody.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.TransformUtil;

/**
 * SolverBody is an internal data structure for the constraint solver. Only necessary data is packed to increase cache
 * coherence/performance.
 *
 * @author jezek2
 */
public class SolverBody {

	// protected final BulletStack stack = BulletStack.get();

	/** The angular velocity. */
	public final Vector3f angularVelocity = new Vector3f();
	
	/** The angular factor. */
	public float angularFactor;
	
	/** The inv mass. */
	public float invMass;
	
	/** The friction. */
	public float friction;
	
	/** The original body. */
	public RigidBody originalBody;
	
	/** The linear velocity. */
	public final Vector3f linearVelocity = new Vector3f();
	
	/** The center of mass position. */
	public final Vector3f centerOfMassPosition = new Vector3f();

	/** The push velocity. */
	public final Vector3f pushVelocity = new Vector3f();
	
	/** The turn velocity. */
	public final Vector3f turnVelocity = new Vector3f();

	/**
	 * Gets the velocity in local point.
	 *
	 * @param rel_pos the rel pos
	 * @param velocity the velocity
	 * @return the velocity in local point
	 */
	public void getVelocityInLocalPoint(final Vector3f rel_pos, final Vector3f velocity) {
		Vector3f tmp = VECTORS.get();
		tmp.cross(angularVelocity, rel_pos);
		velocity.add(linearVelocity, tmp);
		VECTORS.release(tmp);
	}

	/**
	 * Optimization for the iterative solver: avoid calculating constant terms involving inertia, normal, relative
	 * position.
	 */
	public void internalApplyImpulse(final Vector3f linearComponent, final Vector3f angularComponent,
			final float impulseMagnitude) {
		if (invMass != 0f) {
			linearVelocity.scaleAdd(impulseMagnitude, linearComponent, linearVelocity);
			angularVelocity.scaleAdd(impulseMagnitude * angularFactor, angularComponent, angularVelocity);
		}
	}

	/**
	 * Internal apply push impulse.
	 *
	 * @param linearComponent the linear component
	 * @param angularComponent the angular component
	 * @param impulseMagnitude the impulse magnitude
	 */
	public void internalApplyPushImpulse(final Vector3f linearComponent, final Vector3f angularComponent,
			final float impulseMagnitude) {
		if (invMass != 0f) {
			pushVelocity.scaleAdd(impulseMagnitude, linearComponent, pushVelocity);
			turnVelocity.scaleAdd(impulseMagnitude * angularFactor, angularComponent, turnVelocity);
		}
	}

	/**
	 * Writeback velocity.
	 */
	public void writebackVelocity() {
		if (invMass != 0f) {
			originalBody.setLinearVelocity(linearVelocity);
			originalBody.setAngularVelocity(angularVelocity);
			// m_originalBody->setCompanionId(-1);
		}
	}

	/**
	 * Writeback velocity.
	 *
	 * @param timeStep the time step
	 */
	public void writebackVelocity(final float timeStep) {
		if (invMass != 0f) {
			originalBody.setLinearVelocity(linearVelocity);
			originalBody.setAngularVelocity(angularVelocity);

			// correct the position/orientation based on push/turn recovery
			Transform newTransform = TRANSFORMS.get();
			Transform curTrans = originalBody.getWorldTransform(TRANSFORMS.get());
			TransformUtil.integrateTransform(curTrans, pushVelocity, turnVelocity, timeStep, newTransform);
			originalBody.setWorldTransform(newTransform);
			TRANSFORMS.release(curTrans, newTransform);
			// m_originalBody->setCompanionId(-1);
		}
	}

	/**
	 * Read velocity.
	 */
	public void readVelocity() {
		if (invMass != 0f) {
			originalBody.getLinearVelocity(linearVelocity);
			originalBody.getAngularVelocity(angularVelocity);
		}
	}

}
