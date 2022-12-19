/*******************************************************************************************************
 *
 * TranslationalLimitMotor.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

/*
 * 2007-09-09 btGeneric6DofConstraint Refactored by Francisco Le�n email: projectileman@yahoo.com http://gimpact.sf.net
 */

package com.bulletphysics.dynamics.constraintsolver;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
public class TranslationalLimitMotor {

	// protected final BulletStack stack = BulletStack.get();

	/** The lower limit. */
	public final Vector3f lowerLimit = new Vector3f(); // !< the constraint lower limits
	
	/** The upper limit. */
	public final Vector3f upperLimit = new Vector3f(); // !< the constraint upper limits
	
	/** The accumulated impulse. */
	public final Vector3f accumulatedImpulse = new Vector3f();

	/** The limit softness. */
	public float limitSoftness; // !< Softness for linear limit
	
	/** The damping. */
	public float damping; // !< Damping for linear limit
	
	/** The restitution. */
	public float restitution; // ! Bounce parameter for linear limit

	/**
	 * Instantiates a new translational limit motor.
	 */
	public TranslationalLimitMotor() {
		lowerLimit.set(0f, 0f, 0f);
		upperLimit.set(0f, 0f, 0f);
		accumulatedImpulse.set(0f, 0f, 0f);

		limitSoftness = 0.7f;
		damping = 1.0f;
		restitution = 0.5f;
	}

	/**
	 * Instantiates a new translational limit motor.
	 *
	 * @param other the other
	 */
	public TranslationalLimitMotor(final TranslationalLimitMotor other) {
		lowerLimit.set(other.lowerLimit);
		upperLimit.set(other.upperLimit);
		accumulatedImpulse.set(other.accumulatedImpulse);

		limitSoftness = other.limitSoftness;
		damping = other.damping;
		restitution = other.restitution;
	}

	/**
	 * Test limit.
	 * <p>
	 * - free means upper &lt; lower,<br>
	 * - locked means upper == lower<br>
	 * - limited means upper &gt; lower<br>
	 * - limitIndex: first 3 are linear, next 3 are angular
	 */
	public boolean isLimited(final int limitIndex) {
		return VectorUtil.getCoord(upperLimit, limitIndex) >= VectorUtil.getCoord(lowerLimit, limitIndex);
	}

	/**
	 * Solve linear axis.
	 *
	 * @param timeStep the time step
	 * @param jacDiagABInv the jac diag AB inv
	 * @param body1 the body 1
	 * @param pointInA the point in A
	 * @param body2 the body 2
	 * @param pointInB the point in B
	 * @param limit_index the limit index
	 * @param axis_normal_on_a the axis normal on a
	 * @param anchorPos the anchor pos
	 * @return the float
	 */
	public float solveLinearAxis(final float timeStep, final float jacDiagABInv, final RigidBody body1,
			final Vector3f pointInA, final RigidBody body2, final Vector3f pointInB, final int limit_index,
			final Vector3f axis_normal_on_a, final Vector3f anchorPos) {
		Vector3f tmp = VECTORS.get();
		Vector3f tmpVec = VECTORS.get();
		Vector3f rel_pos1 = VECTORS.get();
		rel_pos1.sub(anchorPos, body1.getCenterOfMassPosition(tmpVec));
		Vector3f rel_pos2 = VECTORS.get();
		rel_pos2.sub(anchorPos, body2.getCenterOfMassPosition(tmpVec));
		Vector3f vel1 = body1.getVelocityInLocalPoint(rel_pos1, VECTORS.get());
		Vector3f vel2 = body2.getVelocityInLocalPoint(rel_pos2, VECTORS.get());
		Vector3f vel = VECTORS.get();

		vel.sub(vel1, vel2);
		float rel_vel = axis_normal_on_a.dot(vel);
		tmp.sub(pointInA, pointInB);
		float depth = -tmp.dot(axis_normal_on_a);
		float lo = -1e30f;
		float hi = 1e30f;
		float minLimit = VectorUtil.getCoord(lowerLimit, limit_index);
		float maxLimit = VectorUtil.getCoord(upperLimit, limit_index);
		try {
			// handle the limits
			if (minLimit < maxLimit) {
				{
					if (depth > maxLimit) {
						depth -= maxLimit;
						lo = 0f;

					} else {
						if (depth < minLimit) {
							depth -= minLimit;
							hi = 0f;
						} else
							return 0.0f;
					}
				}
			}

			float normalImpulse = limitSoftness * (restitution * depth / timeStep - damping * rel_vel) * jacDiagABInv;

			float oldNormalImpulse = VectorUtil.getCoord(accumulatedImpulse, limit_index);
			float sum = oldNormalImpulse + normalImpulse;
			VectorUtil.setCoord(accumulatedImpulse, limit_index, sum > hi ? 0f : sum < lo ? 0f : sum);
			normalImpulse = VectorUtil.getCoord(accumulatedImpulse, limit_index) - oldNormalImpulse;

			Vector3f impulse_vector = VECTORS.get();
			impulse_vector.scale(normalImpulse, axis_normal_on_a);
			body1.applyImpulse(impulse_vector, rel_pos1);

			tmp.negate(impulse_vector);
			body2.applyImpulse(tmp, rel_pos2);
			VECTORS.release(impulse_vector);
			return normalImpulse;
		} finally {
			VECTORS.release(tmp, tmpVec, rel_pos1, rel_pos2, vel1, vel2, vel);
		}
	}

}
