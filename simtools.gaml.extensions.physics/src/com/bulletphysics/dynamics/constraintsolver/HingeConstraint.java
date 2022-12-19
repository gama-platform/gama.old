/*******************************************************************************************************
 *
 * HingeConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

/* Hinge Constraint by Dirk Gregorius. Limits added by Marcus Hennix at Starbreeze Studios */

package com.bulletphysics.dynamics.constraintsolver;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.QUATS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.ScalarUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.TransformUtil;

/**
 * Hinge constraint between two rigid bodies each with a pivot point that descibes the axis location in local space.
 * Axis defines the orientation of the hinge axis.
 *
 * @author jezek2
 */
public class HingeConstraint extends TypedConstraint {

	/** The jac. */
	private final JacobianEntry[] jac/* [3] */ =
			new JacobianEntry[] { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() }; // 3 orthogonal
																									// linear
																									/** The jac ang. */
																									// constraints
	private final JacobianEntry[] jacAng/* [3] */ =
			new JacobianEntry[] { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() }; // 2 orthogonal
																									// angular
																									// constraints+ 1
																									// for limit/motor

	/** The rb A frame. */
																									private final Transform rbAFrame = new Transform(); // constraint axii. Assumes z is hinge axis.
	
	/** The rb B frame. */
	private final Transform rbBFrame = new Transform();

	/** The motor target velocity. */
	private float motorTargetVelocity;
	
	/** The max motor impulse. */
	private float maxMotorImpulse;

	/** The limit softness. */
	private float limitSoftness;
	
	/** The bias factor. */
	private float biasFactor;
	
	/** The relaxation factor. */
	private float relaxationFactor;

	/** The lower limit. */
	private float lowerLimit;
	
	/** The upper limit. */
	private float upperLimit;

	/** The k hinge. */
	private float kHinge;

	/** The limit sign. */
	private float limitSign;
	
	/** The correction. */
	private float correction;

	/** The acc limit impulse. */
	private float accLimitImpulse;

	/** The angular only. */
	private boolean angularOnly;
	
	/** The enable angular motor. */
	private boolean enableAngularMotor;
	
	/** The solve limit. */
	private boolean solveLimit;

	/**
	 * Instantiates a new hinge constraint.
	 */
	public HingeConstraint() {
		super(TypedConstraintType.HINGE_CONSTRAINT_TYPE);
		enableAngularMotor = false;
	}

	/**
	 * Instantiates a new hinge constraint.
	 *
	 * @param rbA the rb A
	 * @param rbB the rb B
	 * @param pivotInA the pivot in A
	 * @param pivotInB the pivot in B
	 * @param axisInA the axis in A
	 * @param axisInB the axis in B
	 */
	public HingeConstraint(final RigidBody rbA, final RigidBody rbB, final Vector3f pivotInA, final Vector3f pivotInB,
			final Vector3f axisInA, final Vector3f axisInB) {
		super(TypedConstraintType.HINGE_CONSTRAINT_TYPE, rbA, rbB);
		angularOnly = false;
		enableAngularMotor = false;

		rbAFrame.origin.set(pivotInA);

		// since no frame is given, assume this to be zero angle and just pick rb transform axis
		Vector3f rbAxisA1 = VECTORS.get();
		Vector3f rbAxisA2 = VECTORS.get();

		Transform centerOfMassA = rbA.getCenterOfMassTransform(TRANSFORMS.get());
		centerOfMassA.basis.getColumn(0, rbAxisA1);
		float projection = axisInA.dot(rbAxisA1);

		if (projection >= 1.0f - BulletGlobals.SIMD_EPSILON) {
			centerOfMassA.basis.getColumn(2, rbAxisA1);
			rbAxisA1.negate();
			centerOfMassA.basis.getColumn(1, rbAxisA2);
		} else if (projection <= -1.0f + BulletGlobals.SIMD_EPSILON) {
			centerOfMassA.basis.getColumn(2, rbAxisA1);
			centerOfMassA.basis.getColumn(1, rbAxisA2);
		} else {
			rbAxisA2.cross(axisInA, rbAxisA1);
			rbAxisA1.cross(rbAxisA2, axisInA);
		}

		rbAFrame.basis.setRow(0, rbAxisA1.x, rbAxisA2.x, axisInA.x);
		rbAFrame.basis.setRow(1, rbAxisA1.y, rbAxisA2.y, axisInA.y);
		rbAFrame.basis.setRow(2, rbAxisA1.z, rbAxisA2.z, axisInA.z);

		Quat4f rotationArc = QuaternionUtil.shortestArcQuat(axisInA, axisInB, QUATS.get());
		Vector3f rbAxisB1 = QuaternionUtil.quatRotate(rotationArc, rbAxisA1, VECTORS.get());
		Vector3f rbAxisB2 = VECTORS.get();
		rbAxisB2.cross(axisInB, rbAxisB1);

		rbBFrame.origin.set(pivotInB);
		rbBFrame.basis.setRow(0, rbAxisB1.x, rbAxisB2.x, -axisInB.x);
		rbBFrame.basis.setRow(1, rbAxisB1.y, rbAxisB2.y, -axisInB.y);
		rbBFrame.basis.setRow(2, rbAxisB1.z, rbAxisB2.z, -axisInB.z);

		VECTORS.release(rbAxisB2, rbAxisB1);
		QUATS.release(rotationArc);

		// start with free
		lowerLimit = 1e30f;
		upperLimit = -1e30f;
		biasFactor = 0.3f;
		relaxationFactor = 1.0f;
		limitSoftness = 0.9f;
		solveLimit = false;
	}

	/**
	 * Instantiates a new hinge constraint.
	 *
	 * @param rbA the rb A
	 * @param pivotInA the pivot in A
	 * @param axisInA the axis in A
	 */
	public HingeConstraint(final RigidBody rbA, final Vector3f pivotInA, final Vector3f axisInA) {
		super(TypedConstraintType.HINGE_CONSTRAINT_TYPE, rbA);
		angularOnly = false;
		enableAngularMotor = false;

		// since no frame is given, assume this to be zero angle and just pick rb transform axis
		// fixed axis in worldspace
		Vector3f rbAxisA1 = VECTORS.get();
		Transform centerOfMassA = rbA.getCenterOfMassTransform(TRANSFORMS.get());
		centerOfMassA.basis.getColumn(0, rbAxisA1);

		float projection = rbAxisA1.dot(axisInA);
		if (projection > BulletGlobals.FLT_EPSILON) {
			rbAxisA1.scale(projection);
			rbAxisA1.sub(axisInA);
		} else {
			centerOfMassA.basis.getColumn(1, rbAxisA1);
		}

		Vector3f rbAxisA2 = VECTORS.get();
		rbAxisA2.cross(axisInA, rbAxisA1);

		rbAFrame.origin.set(pivotInA);
		rbAFrame.basis.setRow(0, rbAxisA1.x, rbAxisA2.x, axisInA.x);
		rbAFrame.basis.setRow(1, rbAxisA1.y, rbAxisA2.y, axisInA.y);
		rbAFrame.basis.setRow(2, rbAxisA1.z, rbAxisA2.z, axisInA.z);

		Vector3f axisInB = VECTORS.get();
		axisInB.negate(axisInA);
		centerOfMassA.basis.transform(axisInB);

		Quat4f rotationArc = QuaternionUtil.shortestArcQuat(axisInA, axisInB, QUATS.get());
		Vector3f rbAxisB1 = QuaternionUtil.quatRotate(rotationArc, rbAxisA1, VECTORS.get());
		Vector3f rbAxisB2 = VECTORS.get();
		rbAxisB2.cross(axisInB, rbAxisB1);

		rbBFrame.origin.set(pivotInA);
		centerOfMassA.transform(rbBFrame.origin);
		rbBFrame.basis.setRow(0, rbAxisB1.x, rbAxisB2.x, axisInB.x);
		rbBFrame.basis.setRow(1, rbAxisB1.y, rbAxisB2.y, axisInB.y);
		rbBFrame.basis.setRow(2, rbAxisB1.z, rbAxisB2.z, axisInB.z);

		VECTORS.release(rbAxisA1, rbAxisA2, axisInB, rbAxisB2, rbAxisB1);
		QUATS.release(rotationArc);
		TRANSFORMS.release(centerOfMassA);

		// start with free
		lowerLimit = 1e30f;
		upperLimit = -1e30f;
		biasFactor = 0.3f;
		relaxationFactor = 1.0f;
		limitSoftness = 0.9f;
		solveLimit = false;
	}

	/**
	 * Instantiates a new hinge constraint.
	 *
	 * @param rbA the rb A
	 * @param rbB the rb B
	 * @param rbAFrame the rb A frame
	 * @param rbBFrame the rb B frame
	 */
	public HingeConstraint(final RigidBody rbA, final RigidBody rbB, final Transform rbAFrame,
			final Transform rbBFrame) {
		super(TypedConstraintType.HINGE_CONSTRAINT_TYPE, rbA, rbB);
		this.rbAFrame.set(rbAFrame);
		this.rbBFrame.set(rbBFrame);
		angularOnly = false;
		enableAngularMotor = false;

		// flip axis
		this.rbBFrame.basis.m02 *= -1f;
		this.rbBFrame.basis.m12 *= -1f;
		this.rbBFrame.basis.m22 *= -1f;

		// start with free
		lowerLimit = 1e30f;
		upperLimit = -1e30f;
		biasFactor = 0.3f;
		relaxationFactor = 1.0f;
		limitSoftness = 0.9f;
		solveLimit = false;
	}

	/**
	 * Instantiates a new hinge constraint.
	 *
	 * @param rbA the rb A
	 * @param rbAFrame the rb A frame
	 */
	public HingeConstraint(final RigidBody rbA, final Transform rbAFrame) {
		super(TypedConstraintType.HINGE_CONSTRAINT_TYPE, rbA);
		this.rbAFrame.set(rbAFrame);
		this.rbBFrame.set(rbAFrame);
		angularOnly = false;
		enableAngularMotor = false;

		// not providing rigidbody B means implicitly using worldspace for body B

		// flip axis
		this.rbBFrame.basis.m02 *= -1f;
		this.rbBFrame.basis.m12 *= -1f;
		this.rbBFrame.basis.m22 *= -1f;

		this.rbBFrame.origin.set(this.rbAFrame.origin);
		rbA.getCenterOfMassTransform(TRANSFORMS.get()).transform(this.rbBFrame.origin);

		// start with free
		lowerLimit = 1e30f;
		upperLimit = -1e30f;
		biasFactor = 0.3f;
		relaxationFactor = 1.0f;
		limitSoftness = 0.9f;
		solveLimit = false;
	}

	@Override
	public void buildJacobian() {
		Vector3f tmp = VECTORS.get();
		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();
		Vector3f tmpVec = VECTORS.get();
		Matrix3f mat1 = MATRICES.get();
		Matrix3f mat2 = MATRICES.get();
		Vector3f vmp0 = VECTORS.get();
		Vector3f vmp1 = VECTORS.get();

		Transform centerOfMassA = rbA.getCenterOfMassTransform(TRANSFORMS.get());
		Transform centerOfMassB = rbB.getCenterOfMassTransform(TRANSFORMS.get());

		appliedImpulse = 0f;

		if (!angularOnly) {
			Vector3f pivotAInW = VECTORS.get(rbAFrame.origin);
			centerOfMassA.transform(pivotAInW);

			Vector3f pivotBInW = VECTORS.get(rbBFrame.origin);
			centerOfMassB.transform(pivotBInW);

			Vector3f relPos = VECTORS.get();
			relPos.sub(pivotBInW, pivotAInW);

			Vector3f[] normal/* [3] */ = new Vector3f[] { VECTORS.get(), VECTORS.get(), VECTORS.get() };
			if (relPos.lengthSquared() > BulletGlobals.FLT_EPSILON) {
				normal[0].set(relPos);
				normal[0].normalize();
			} else {
				normal[0].set(1f, 0f, 0f);
			}

			TransformUtil.planeSpace1(normal[0], normal[1], normal[2]);

			for (int i = 0; i < 3; i++) {
				mat1.transpose(centerOfMassA.basis);
				mat2.transpose(centerOfMassB.basis);

				tmp1.sub(pivotAInW, rbA.getCenterOfMassPosition(tmpVec));
				tmp2.sub(pivotBInW, rbB.getCenterOfMassPosition(tmpVec));

				jac[i].init(mat1, mat2, tmp1, tmp2, normal[i], rbA.getInvInertiaDiagLocal(VECTORS.get()),
						rbA.getInvMass(), rbB.getInvInertiaDiagLocal(VECTORS.get()), rbB.getInvMass());
			}
			VECTORS.release(normal[0], normal[1], normal[2], relPos, pivotBInW, pivotAInW);
		}

		// calculate two perpendicular jointAxis, orthogonal to hingeAxis
		// these two jointAxis require equal angular velocities for both bodies

		// this is unused for now, it's a todo
		Vector3f jointAxis0local = VECTORS.get();
		Vector3f jointAxis1local = VECTORS.get();

		rbAFrame.basis.getColumn(2, tmp);
		TransformUtil.planeSpace1(tmp, jointAxis0local, jointAxis1local);

		// TODO: check this
		// getRigidBodyA().getCenterOfMassTransform().getBasis() * m_rbAFrame.getBasis().getColumn(2);

		Vector3f jointAxis0 = VECTORS.get(jointAxis0local);
		centerOfMassA.basis.transform(jointAxis0);

		Vector3f jointAxis1 = VECTORS.get(jointAxis1local);
		centerOfMassA.basis.transform(jointAxis1);

		Vector3f hingeAxisWorld = VECTORS.get();
		rbAFrame.basis.getColumn(2, hingeAxisWorld);
		centerOfMassA.basis.transform(hingeAxisWorld);

		mat1.transpose(centerOfMassA.basis);
		mat2.transpose(centerOfMassB.basis);

		jacAng[0].init(jointAxis0, mat1, mat2, rbA.getInvInertiaDiagLocal(vmp0), rbB.getInvInertiaDiagLocal(vmp1));

		// JAVA NOTE: reused mat1 and mat2, as recomputation is not needed
		jacAng[1].init(jointAxis1, mat1, mat2, rbA.getInvInertiaDiagLocal(vmp0), rbB.getInvInertiaDiagLocal(vmp1));

		// JAVA NOTE: reused mat1 and mat2, as recomputation is not needed
		jacAng[2].init(hingeAxisWorld, mat1, mat2, rbA.getInvInertiaDiagLocal(vmp0), rbB.getInvInertiaDiagLocal(vmp1));

		// Compute limit information
		float hingeAngle = getHingeAngle();

		// set bias, sign, clear accumulator
		correction = 0f;
		limitSign = 0f;
		solveLimit = false;
		accLimitImpulse = 0f;

		if (lowerLimit < upperLimit) {
			if (hingeAngle <= lowerLimit * limitSoftness) {
				correction = lowerLimit - hingeAngle;
				limitSign = 1.0f;
				solveLimit = true;
			} else if (hingeAngle >= upperLimit * limitSoftness) {
				correction = upperLimit - hingeAngle;
				limitSign = -1.0f;
				solveLimit = true;
			}
		}

		// Compute K = J*W*J' for hinge axis
		Vector3f axisA = VECTORS.get();
		rbAFrame.basis.getColumn(2, axisA);
		centerOfMassA.basis.transform(axisA);

		kHinge = 1.0f / (getRigidBodyA().computeAngularImpulseDenominator(axisA)
				+ getRigidBodyB().computeAngularImpulseDenominator(axisA));
		VECTORS.release(axisA, hingeAxisWorld, jointAxis1, jointAxis0, jointAxis0local, jointAxis1local, tmpVec, tmp,
				tmp1, tmp2, vmp0, vmp1);
	}

	@Override
	public void solveConstraint(final float timeStep) {
		Vector3f tmp = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();
		Vector3f tmpVec = VECTORS.get();

		Transform centerOfMassA = rbA.getCenterOfMassTransform(TRANSFORMS.get());
		Transform centerOfMassB = rbB.getCenterOfMassTransform(TRANSFORMS.get());

		Vector3f pivotAInW = VECTORS.get(rbAFrame.origin);
		centerOfMassA.transform(pivotAInW);

		Vector3f pivotBInW = VECTORS.get(rbBFrame.origin);
		centerOfMassB.transform(pivotBInW);

		float tau = 0.3f;

		// linear part
		if (!angularOnly) {
			Vector3f rel_pos1 = VECTORS.get();
			rel_pos1.sub(pivotAInW, rbA.getCenterOfMassPosition(tmpVec));

			Vector3f rel_pos2 = VECTORS.get();
			rel_pos2.sub(pivotBInW, rbB.getCenterOfMassPosition(tmpVec));

			Vector3f vel1 = rbA.getVelocityInLocalPoint(rel_pos1, VECTORS.get());
			Vector3f vel2 = rbB.getVelocityInLocalPoint(rel_pos2, VECTORS.get());
			Vector3f vel = VECTORS.get();
			vel.sub(vel1, vel2);

			for (int i = 0; i < 3; i++) {
				Vector3f normal = jac[i].linearJointAxis;
				float jacDiagABInv = 1f / jac[i].getDiagonal();

				float rel_vel;
				rel_vel = normal.dot(vel);
				// positional error (zeroth order error)
				tmp.sub(pivotAInW, pivotBInW);
				float depth = -tmp.dot(normal); // this is the error projected on the normal
				float impulse = depth * tau / timeStep * jacDiagABInv - rel_vel * jacDiagABInv;
				appliedImpulse += impulse;
				Vector3f impulse_vector = VECTORS.get();
				impulse_vector.scale(impulse, normal);

				tmp.sub(pivotAInW, rbA.getCenterOfMassPosition(tmpVec));
				rbA.applyImpulse(impulse_vector, tmp);

				tmp.negate(impulse_vector);
				tmp2.sub(pivotBInW, rbB.getCenterOfMassPosition(tmpVec));
				rbB.applyImpulse(tmp, tmp2);
				VECTORS.release(impulse_vector);
			}
			VECTORS.release(rel_pos1, rel_pos2, vel1, vel2, vel);
		}

		{
			// solve angular part

			// get axes in world space
			Vector3f axisA = VECTORS.get();
			rbAFrame.basis.getColumn(2, axisA);
			centerOfMassA.basis.transform(axisA);

			Vector3f axisB = VECTORS.get();
			rbBFrame.basis.getColumn(2, axisB);
			centerOfMassB.basis.transform(axisB);

			Vector3f angVelA = getRigidBodyA().getAngularVelocity(VECTORS.get());
			Vector3f angVelB = getRigidBodyB().getAngularVelocity(VECTORS.get());

			Vector3f angVelAroundHingeAxisA = VECTORS.get();
			angVelAroundHingeAxisA.scale(axisA.dot(angVelA), axisA);

			Vector3f angVelAroundHingeAxisB = VECTORS.get();
			angVelAroundHingeAxisB.scale(axisB.dot(angVelB), axisB);

			Vector3f angAorthog = VECTORS.get();
			angAorthog.sub(angVelA, angVelAroundHingeAxisA);

			Vector3f angBorthog = VECTORS.get();
			angBorthog.sub(angVelB, angVelAroundHingeAxisB);

			Vector3f velrelOrthog = VECTORS.get();
			velrelOrthog.sub(angAorthog, angBorthog);

			{
				// solve orthogonal angular velocity correction
				float relaxation = 1f;
				float len = velrelOrthog.length();
				if (len > 0.00001f) {
					Vector3f normal = VECTORS.get();
					normal.normalize(velrelOrthog);

					float denom = getRigidBodyA().computeAngularImpulseDenominator(normal)
							+ getRigidBodyB().computeAngularImpulseDenominator(normal);
					// scale for mass and relaxation
					// todo: expose this 0.9 factor to developer
					velrelOrthog.scale(1f / denom * relaxationFactor);
					VECTORS.release(normal);
				}

				// solve angular positional correction
				// TODO: check
				// Vector3f angularError = -axisA.cross(axisB) *(btScalar(1.)/timeStep);
				Vector3f angularError = VECTORS.get();
				angularError.cross(axisA, axisB);
				angularError.negate();
				angularError.scale(1f / timeStep);
				float len2 = angularError.length();
				if (len2 > 0.00001f) {
					Vector3f normal2 = VECTORS.get();
					normal2.normalize(angularError);

					float denom2 = getRigidBodyA().computeAngularImpulseDenominator(normal2)
							+ getRigidBodyB().computeAngularImpulseDenominator(normal2);
					angularError.scale(1f / denom2 * relaxation);
					VECTORS.release(normal2);
				}

				tmp.negate(velrelOrthog);
				tmp.add(angularError);
				rbA.applyTorqueImpulse(tmp);

				tmp.sub(velrelOrthog, angularError);
				rbB.applyTorqueImpulse(tmp);

				// solve limit
				if (solveLimit) {
					tmp.sub(angVelB, angVelA);
					float amplitude =
							(tmp.dot(axisA) * relaxationFactor + correction * (1f / timeStep) * biasFactor) * limitSign;

					float impulseMag = amplitude * kHinge;

					// Clamp the accumulated impulse
					float temp = accLimitImpulse;
					accLimitImpulse = Math.max(accLimitImpulse + impulseMag, 0f);
					impulseMag = accLimitImpulse - temp;

					Vector3f impulse = VECTORS.get();
					impulse.scale(impulseMag * limitSign, axisA);

					rbA.applyTorqueImpulse(impulse);

					tmp.negate(impulse);
					rbB.applyTorqueImpulse(tmp);
					VECTORS.release(impulse);
				}
				VECTORS.release(angularError);
			}

			// apply motor
			if (enableAngularMotor) {
				// todo: add limits too
				Vector3f angularLimit = VECTORS.get();
				angularLimit.set(0f, 0f, 0f);

				Vector3f velrel = VECTORS.get();
				velrel.sub(angVelAroundHingeAxisA, angVelAroundHingeAxisB);
				float projRelVel = velrel.dot(axisA);

				float desiredMotorVel = motorTargetVelocity;
				float motor_relvel = desiredMotorVel - projRelVel;

				float unclippedMotorImpulse = kHinge * motor_relvel;
				// todo: should clip against accumulated impulse
				float clippedMotorImpulse =
						unclippedMotorImpulse > maxMotorImpulse ? maxMotorImpulse : unclippedMotorImpulse;
				clippedMotorImpulse = clippedMotorImpulse < -maxMotorImpulse ? -maxMotorImpulse : clippedMotorImpulse;
				Vector3f motorImp = VECTORS.get();
				motorImp.scale(clippedMotorImpulse, axisA);

				tmp.add(motorImp, angularLimit);
				rbA.applyTorqueImpulse(tmp);

				tmp.negate(motorImp);
				tmp.sub(angularLimit);
				rbB.applyTorqueImpulse(tmp);
				VECTORS.release(angularLimit, velrel, motorImp);
			}
			VECTORS.release(axisA, axisB, angVelA, angVelB, angVelAroundHingeAxisA, angVelAroundHingeAxisB, angAorthog,
					angBorthog, velrelOrthog);
		}
		VECTORS.release(tmp, tmp2, tmpVec, pivotAInW, pivotBInW);
		TRANSFORMS.release(centerOfMassA, centerOfMassB);
	}

	/**
	 * Update RHS.
	 *
	 * @param timeStep the time step
	 */
	public void updateRHS(final float timeStep) {}

	/**
	 * Gets the hinge angle.
	 *
	 * @return the hinge angle
	 */
	public float getHingeAngle() {
		Transform centerOfMassA = rbA.getCenterOfMassTransform(TRANSFORMS.get());
		Transform centerOfMassB = rbB.getCenterOfMassTransform(TRANSFORMS.get());

		Vector3f refAxis0 = VECTORS.get();
		rbAFrame.basis.getColumn(0, refAxis0);
		centerOfMassA.basis.transform(refAxis0);

		Vector3f refAxis1 = VECTORS.get();
		rbAFrame.basis.getColumn(1, refAxis1);
		centerOfMassA.basis.transform(refAxis1);

		Vector3f swingAxis = VECTORS.get();
		rbBFrame.basis.getColumn(1, swingAxis);
		centerOfMassB.basis.transform(swingAxis);

		VECTORS.release(refAxis0, refAxis1, swingAxis);
		TRANSFORMS.release(centerOfMassA, centerOfMassB);
		return ScalarUtil.atan2Fast(swingAxis.dot(refAxis0), swingAxis.dot(refAxis1));
	}

	/**
	 * Sets the angular only.
	 *
	 * @param angularOnly the new angular only
	 */
	public void setAngularOnly(final boolean angularOnly) {
		this.angularOnly = angularOnly;
	}

	/**
	 * Enable angular motor.
	 *
	 * @param enableMotor the enable motor
	 * @param targetVelocity the target velocity
	 * @param maxMotorImpulse the max motor impulse
	 */
	public void enableAngularMotor(final boolean enableMotor, final float targetVelocity, final float maxMotorImpulse) {
		this.enableAngularMotor = enableMotor;
		this.motorTargetVelocity = targetVelocity;
		this.maxMotorImpulse = maxMotorImpulse;
	}

	/**
	 * Sets the limit.
	 *
	 * @param low the low
	 * @param high the high
	 */
	public void setLimit(final float low, final float high) {
		setLimit(low, high, 0.9f, 0.3f, 1.0f);
	}

	/**
	 * Sets the limit.
	 *
	 * @param low the low
	 * @param high the high
	 * @param _softness the softness
	 * @param _biasFactor the bias factor
	 * @param _relaxationFactor the relaxation factor
	 */
	public void setLimit(final float low, final float high, final float _softness, final float _biasFactor,
			final float _relaxationFactor) {
		lowerLimit = low;
		upperLimit = high;

		limitSoftness = _softness;
		biasFactor = _biasFactor;
		relaxationFactor = _relaxationFactor;
	}

	/**
	 * Gets the lower limit.
	 *
	 * @return the lower limit
	 */
	public float getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * Gets the upper limit.
	 *
	 * @return the upper limit
	 */
	public float getUpperLimit() {
		return upperLimit;
	}

	/**
	 * Gets the a frame.
	 *
	 * @param out the out
	 * @return the a frame
	 */
	public Transform getAFrame(final Transform out) {
		out.set(rbAFrame);
		return out;
	}

	/**
	 * Gets the b frame.
	 *
	 * @param out the out
	 * @return the b frame
	 */
	public Transform getBFrame(final Transform out) {
		out.set(rbBFrame);
		return out;
	}

	/**
	 * Gets the solve limit.
	 *
	 * @return the solve limit
	 */
	public boolean getSolveLimit() {
		return solveLimit;
	}

	/**
	 * Gets the limit sign.
	 *
	 * @return the limit sign
	 */
	public float getLimitSign() {
		return limitSign;
	}

	/**
	 * Gets the angular only.
	 *
	 * @return the angular only
	 */
	public boolean getAngularOnly() {
		return angularOnly;
	}

	/**
	 * Gets the enable angular motor.
	 *
	 * @return the enable angular motor
	 */
	public boolean getEnableAngularMotor() {
		return enableAngularMotor;
	}

	/**
	 * Gets the motor target velosity.
	 *
	 * @return the motor target velosity
	 */
	public float getMotorTargetVelosity() {
		return motorTargetVelocity;
	}

	/**
	 * Gets the max motor impulse.
	 *
	 * @return the max motor impulse
	 */
	public float getMaxMotorImpulse() {
		return maxMotorImpulse;
	}

}
