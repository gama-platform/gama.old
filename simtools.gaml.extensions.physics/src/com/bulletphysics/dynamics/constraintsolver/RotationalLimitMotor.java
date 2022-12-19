/*******************************************************************************************************
 *
 * RotationalLimitMotor.java, in simtools.gaml.extensions.physics, is part of the source code of the
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

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.dynamics.RigidBody;

/**
 * Rotation limit structure for generic joints.
 *
 * @author jezek2
 */
public class RotationalLimitMotor {

	// protected final BulletStack stack = BulletStack.get();

	/** The lo limit. */
	public float loLimit; // !< joint limit
	
	/** The hi limit. */
	public float hiLimit; // !< joint limit
	
	/** The target velocity. */
	public float targetVelocity; // !< target motor velocity
	
	/** The max motor force. */
	public float maxMotorForce; // !< max force on motor
	
	/** The max limit force. */
	public float maxLimitForce; // !< max force on limit
	
	/** The damping. */
	public float damping; // !< Damping.
	
	/** The limit softness. */
	public float limitSoftness; // ! Relaxation factor
	
	/** The erp. */
	public float ERP; // !< Error tolerance factor when joint is at limit
	
	/** The bounce. */
	public float bounce; // !< restitution factor
	
	/** The enable motor. */
	public boolean enableMotor;

	/** The current limit error. */
	public float currentLimitError;// ! How much is violated this limit
	
	/** The current limit. */
	public int currentLimit;// !< 0=free, 1=at lo limit, 2=at hi limit
	
	/** The accumulated impulse. */
	public float accumulatedImpulse;

	/**
	 * Instantiates a new rotational limit motor.
	 */
	public RotationalLimitMotor() {
		accumulatedImpulse = 0.f;
		targetVelocity = 0;
		maxMotorForce = 0.1f;
		maxLimitForce = 300.0f;
		loLimit = -BulletGlobals.SIMD_INFINITY;
		hiLimit = BulletGlobals.SIMD_INFINITY;
		ERP = 0.5f;
		bounce = 0.0f;
		damping = 1.0f;
		limitSoftness = 0.5f;
		currentLimit = 0;
		currentLimitError = 0;
		enableMotor = false;
	}

	/**
	 * Instantiates a new rotational limit motor.
	 *
	 * @param limot the limot
	 */
	public RotationalLimitMotor(final RotationalLimitMotor limot) {
		targetVelocity = limot.targetVelocity;
		maxMotorForce = limot.maxMotorForce;
		limitSoftness = limot.limitSoftness;
		loLimit = limot.loLimit;
		hiLimit = limot.hiLimit;
		ERP = limot.ERP;
		bounce = limot.bounce;
		currentLimit = limot.currentLimit;
		currentLimitError = limot.currentLimitError;
		enableMotor = limot.enableMotor;
	}

	/**
	 * Is limited?
	 */
	public boolean isLimited() {
		if (loLimit >= hiLimit) return false;
		return true;
	}

	/**
	 * Need apply correction?
	 */
	public boolean needApplyTorques() {
		if (currentLimit == 0 && enableMotor == false) return false;
		return true;
	}

	/**
	 * Calculates error. Calculates currentLimit and currentLimitError.
	 */
	public int testLimitValue(final float test_value) {
		if (loLimit > hiLimit) {
			currentLimit = 0; // Free from violation
			return 0;
		}

		if (test_value < loLimit) {
			currentLimit = 1; // low limit violation
			currentLimitError = test_value - loLimit;
			return 1;
		} else if (test_value > hiLimit) {
			currentLimit = 2; // High limit violation
			currentLimitError = test_value - hiLimit;
			return 2;
		}

		currentLimit = 0; // Free from violation
		return 0;
	}

	/**
	 * Apply the correction impulses for two bodies.
	 */

	public float solveAngularLimits(final float timeStep, final Vector3f axis, final float jacDiagABInv,
			final RigidBody body0, final RigidBody body1) {
		if (needApplyTorques() == false) return 0.0f;

		float target_velocity = this.targetVelocity;
		float maxMotorForce = this.maxMotorForce;

		// current error correction
		if (currentLimit != 0) {
			target_velocity = -ERP * currentLimitError / timeStep;
			maxMotorForce = maxLimitForce;
		}

		maxMotorForce *= timeStep;

		// current velocity difference
		Vector3f vel_diff = body0.getAngularVelocity(VECTORS.get());
		if (body1 != null) { vel_diff.sub(body1.getAngularVelocity(VECTORS.get())); }

		float rel_vel = axis.dot(vel_diff);

		// correction velocity
		float motor_relvel = limitSoftness * (target_velocity - damping * rel_vel);

		if (motor_relvel < BulletGlobals.FLT_EPSILON && motor_relvel > -BulletGlobals.FLT_EPSILON) return 0.0f; // no
																												// need
																												// for
																												// applying
																												// force

		// correction impulse
		float unclippedMotorImpulse = (1 + bounce) * motor_relvel * jacDiagABInv;

		// clip correction impulse
		float clippedMotorImpulse;

		// todo: should clip against accumulated impulse
		if (unclippedMotorImpulse > 0.0f) {
			clippedMotorImpulse = unclippedMotorImpulse > maxMotorForce ? maxMotorForce : unclippedMotorImpulse;
		} else {
			clippedMotorImpulse = unclippedMotorImpulse < -maxMotorForce ? -maxMotorForce : unclippedMotorImpulse;
		}

		// sort with accumulated impulses
		float lo = -1e30f;
		float hi = 1e30f;

		float oldaccumImpulse = accumulatedImpulse;
		float sum = oldaccumImpulse + clippedMotorImpulse;
		accumulatedImpulse = sum > hi ? 0f : sum < lo ? 0f : sum;

		clippedMotorImpulse = accumulatedImpulse - oldaccumImpulse;

		Vector3f motorImp = VECTORS.get();
		motorImp.scale(clippedMotorImpulse, axis);

		body0.applyTorqueImpulse(motorImp);
		if (body1 != null) {
			motorImp.negate();
			body1.applyTorqueImpulse(motorImp);
		}
		VECTORS.release(motorImp, vel_diff);
		return clippedMotorImpulse;
	}

}
