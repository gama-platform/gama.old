/*******************************************************************************************************
 *
 * ConeTwistConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

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
 * ConeTwistConstraint can be used to simulate ragdoll joints (upper arm, leg etc).
 *
 * @author jezek2
 */
public class ConeTwistConstraint extends TypedConstraint {

	/** The jac. */
	private final JacobianEntry[] jac/* [3] */ =
			new JacobianEntry[] { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() }; // 3 orthogonal
																									// linear
																									// constraints

	/** The rb A frame. */
																									private final Transform rbAFrame = new Transform();
	
	/** The rb B frame. */
	private final Transform rbBFrame = new Transform();

	/** The limit softness. */
	private float limitSoftness;
	
	/** The bias factor. */
	private float biasFactor;
	
	/** The relaxation factor. */
	private float relaxationFactor;

	/** The swing span 1. */
	private float swingSpan1;
	
	/** The swing span 2. */
	private float swingSpan2;
	
	/** The twist span. */
	private float twistSpan;

	/** The swing axis. */
	private final Vector3f swingAxis = new Vector3f();
	
	/** The twist axis. */
	private final Vector3f twistAxis = new Vector3f();

	/** The k swing. */
	private float kSwing;
	
	/** The k twist. */
	private float kTwist;

	/** The twist limit sign. */
	private float twistLimitSign;
	
	/** The swing correction. */
	private float swingCorrection;
	
	/** The twist correction. */
	private float twistCorrection;

	/** The acc swing limit impulse. */
	private float accSwingLimitImpulse;
	
	/** The acc twist limit impulse. */
	private float accTwistLimitImpulse;

	/** The angular only. */
	private boolean angularOnly = false;
	
	/** The solve twist limit. */
	private boolean solveTwistLimit;
	
	/** The solve swing limit. */
	private boolean solveSwingLimit;

	/**
	 * Instantiates a new cone twist constraint.
	 */
	public ConeTwistConstraint() {
		super(TypedConstraintType.CONETWIST_CONSTRAINT_TYPE);
	}

	/**
	 * Instantiates a new cone twist constraint.
	 *
	 * @param rbA the rb A
	 * @param rbB the rb B
	 * @param rbAFrame the rb A frame
	 * @param rbBFrame the rb B frame
	 */
	public ConeTwistConstraint(final RigidBody rbA, final RigidBody rbB, final Transform rbAFrame,
			final Transform rbBFrame) {
		super(TypedConstraintType.CONETWIST_CONSTRAINT_TYPE, rbA, rbB);
		this.rbAFrame.set(rbAFrame);
		this.rbBFrame.set(rbBFrame);

		swingSpan1 = 1e30f;
		swingSpan2 = 1e30f;
		twistSpan = 1e30f;
		biasFactor = 0.3f;
		relaxationFactor = 1.0f;

		solveTwistLimit = false;
		solveSwingLimit = false;
	}

	/**
	 * Instantiates a new cone twist constraint.
	 *
	 * @param rbA the rb A
	 * @param rbAFrame the rb A frame
	 */
	public ConeTwistConstraint(final RigidBody rbA, final Transform rbAFrame) {
		super(TypedConstraintType.CONETWIST_CONSTRAINT_TYPE, rbA);
		this.rbAFrame.set(rbAFrame);
		this.rbBFrame.set(this.rbAFrame);

		swingSpan1 = 1e30f;
		swingSpan2 = 1e30f;
		twistSpan = 1e30f;
		biasFactor = 0.3f;
		relaxationFactor = 1.0f;

		solveTwistLimit = false;
		solveSwingLimit = false;
	}

	@Override
	public void buildJacobian() {
		Vector3f tmp = VECTORS.get();
		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();

		Transform tmpTrans = TRANSFORMS.get();

		appliedImpulse = 0f;

		// set bias, sign, clear accumulator
		swingCorrection = 0f;
		twistLimitSign = 0f;
		solveTwistLimit = false;
		solveSwingLimit = false;
		accTwistLimitImpulse = 0f;
		accSwingLimitImpulse = 0f;

		if (!angularOnly) {
			Vector3f pivotAInW = VECTORS.get(rbAFrame.origin);
			rbA.getCenterOfMassTransform(tmpTrans).transform(pivotAInW);

			Vector3f pivotBInW = VECTORS.get(rbBFrame.origin);
			rbB.getCenterOfMassTransform(tmpTrans).transform(pivotBInW);

			Vector3f relPos = VECTORS.get();
			relPos.sub(pivotBInW, pivotAInW);

			// TODO: stack
			Vector3f[] normal = new Vector3f[] { VECTORS.get(), VECTORS.get(), VECTORS.get() };
			if (relPos.lengthSquared() > BulletGlobals.FLT_EPSILON) {
				normal[0].normalize(relPos);
			} else {
				normal[0].set(1f, 0f, 0f);
			}
			VECTORS.release(relPos);

			TransformUtil.planeSpace1(normal[0], normal[1], normal[2]);

			for (int i = 0; i < 3; i++) {
				Matrix3f mat1 = rbA.getCenterOfMassTransform(TRANSFORMS.get()).basis;
				mat1.transpose();

				Matrix3f mat2 = rbB.getCenterOfMassTransform(TRANSFORMS.get()).basis;
				mat2.transpose();

				tmp1.sub(pivotAInW, rbA.getCenterOfMassPosition(tmp));
				tmp2.sub(pivotBInW, rbB.getCenterOfMassPosition(tmp));
				Vector3f vmp1 = VECTORS.get();
				Vector3f vmp2 = VECTORS.get();
				jac[i].init(mat1, mat2, tmp1, tmp2, normal[i], rbA.getInvInertiaDiagLocal(vmp1), rbA.getInvMass(),
						rbB.getInvInertiaDiagLocal(vmp2), rbB.getInvMass());
				VECTORS.release(vmp1, vmp2);
			}
			VECTORS.release(pivotAInW, pivotBInW, normal[0], normal[1], normal[2]);
		}

		Vector3f b1Axis1 = VECTORS.get(), b1Axis2 = VECTORS.get(), b1Axis3 = VECTORS.get();
		Vector3f b2Axis1 = VECTORS.get(), b2Axis2 = VECTORS.get();

		rbAFrame.basis.getColumn(0, b1Axis1);
		getRigidBodyA().getCenterOfMassTransform(tmpTrans).basis.transform(b1Axis1);

		rbBFrame.basis.getColumn(0, b2Axis1);
		getRigidBodyB().getCenterOfMassTransform(tmpTrans).basis.transform(b2Axis1);

		float swing1 = 0f, swing2 = 0f;

		float swx = 0f, swy = 0f;
		float thresh = 10f;
		float fact;

		// Get Frame into world space
		if (swingSpan1 >= 0.05f) {
			rbAFrame.basis.getColumn(1, b1Axis2);
			getRigidBodyA().getCenterOfMassTransform(tmpTrans).basis.transform(b1Axis2);
			// swing1 = ScalarUtil.atan2Fast(b2Axis1.dot(b1Axis2), b2Axis1.dot(b1Axis1));
			swx = b2Axis1.dot(b1Axis1);
			swy = b2Axis1.dot(b1Axis2);
			swing1 = ScalarUtil.atan2Fast(swy, swx);
			fact = (swy * swy + swx * swx) * thresh * thresh;
			fact = fact / (fact + 1f);
			swing1 *= fact;
		}

		if (swingSpan2 >= 0.05f) {
			rbAFrame.basis.getColumn(2, b1Axis3);
			getRigidBodyA().getCenterOfMassTransform(tmpTrans).basis.transform(b1Axis3);
			// swing2 = ScalarUtil.atan2Fast(b2Axis1.dot(b1Axis3), b2Axis1.dot(b1Axis1));
			swx = b2Axis1.dot(b1Axis1);
			swy = b2Axis1.dot(b1Axis3);
			swing2 = ScalarUtil.atan2Fast(swy, swx);
			fact = (swy * swy + swx * swx) * thresh * thresh;
			fact = fact / (fact + 1f);
			swing2 *= fact;
		}

		float RMaxAngle1Sq = 1.0f / (swingSpan1 * swingSpan1);
		float RMaxAngle2Sq = 1.0f / (swingSpan2 * swingSpan2);
		float EllipseAngle = Math.abs(swing1 * swing1) * RMaxAngle1Sq + Math.abs(swing2 * swing2) * RMaxAngle2Sq;

		if (EllipseAngle > 1.0f) {
			swingCorrection = EllipseAngle - 1.0f;
			solveSwingLimit = true;

			// Calculate necessary axis & factors
			tmp1.scale(b2Axis1.dot(b1Axis2), b1Axis2);
			tmp2.scale(b2Axis1.dot(b1Axis3), b1Axis3);
			tmp.add(tmp1, tmp2);
			swingAxis.cross(b2Axis1, tmp);
			swingAxis.normalize();

			float swingAxisSign = b2Axis1.dot(b1Axis1) >= 0.0f ? 1.0f : -1.0f;
			swingAxis.scale(swingAxisSign);

			kSwing = 1f / (getRigidBodyA().computeAngularImpulseDenominator(swingAxis)
					+ getRigidBodyB().computeAngularImpulseDenominator(swingAxis));

		}

		// Twist limits
		if (twistSpan >= 0f) {
			// Vector3f b2Axis2 = VECTORS.get();;
			rbBFrame.basis.getColumn(1, b2Axis2);
			getRigidBodyB().getCenterOfMassTransform(tmpTrans).basis.transform(b2Axis2);

			Quat4f rotationArc = QuaternionUtil.shortestArcQuat(b2Axis1, b1Axis1, QUATS.get());
			Vector3f TwistRef = QuaternionUtil.quatRotate(rotationArc, b2Axis2, VECTORS.get());
			float twist = ScalarUtil.atan2Fast(TwistRef.dot(b1Axis3), TwistRef.dot(b1Axis2));
			QUATS.release(rotationArc);
			VECTORS.release(TwistRef);

			float lockedFreeFactor = twistSpan > 0.05f ? limitSoftness : 0f;
			if (twist <= -twistSpan * lockedFreeFactor) {
				twistCorrection = -(twist + twistSpan);
				solveTwistLimit = true;

				twistAxis.add(b2Axis1, b1Axis1);
				twistAxis.scale(0.5f);
				twistAxis.normalize();
				twistAxis.scale(-1.0f);

				kTwist = 1f / (getRigidBodyA().computeAngularImpulseDenominator(twistAxis)
						+ getRigidBodyB().computeAngularImpulseDenominator(twistAxis));

			} else if (twist > twistSpan * lockedFreeFactor) {
				twistCorrection = twist - twistSpan;
				solveTwistLimit = true;

				twistAxis.add(b2Axis1, b1Axis1);
				twistAxis.scale(0.5f);
				twistAxis.normalize();

				kTwist = 1f / (getRigidBodyA().computeAngularImpulseDenominator(twistAxis)
						+ getRigidBodyB().computeAngularImpulseDenominator(twistAxis));
			}
		}
		VECTORS.release(b1Axis1, b1Axis2, b1Axis3, b2Axis1, b2Axis2, tmp, tmp1, tmp2);
		TRANSFORMS.release(tmpTrans);
	}

	@Override
	public void solveConstraint(final float timeStep) {
		Vector3f tmp = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();

		Vector3f tmpVec = VECTORS.get();
		Transform tmpTrans = TRANSFORMS.get();

		Vector3f pivotAInW = VECTORS.get(rbAFrame.origin);
		rbA.getCenterOfMassTransform(tmpTrans).transform(pivotAInW);

		Vector3f pivotBInW = VECTORS.get(rbBFrame.origin);
		rbB.getCenterOfMassTransform(tmpTrans).transform(pivotBInW);

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
			VECTORS.release(vel, vel1, vel2, rel_pos2, rel_pos1);
		}

		{
			// solve angular part
			Vector3f angVelA = getRigidBodyA().getAngularVelocity(VECTORS.get());
			Vector3f angVelB = getRigidBodyB().getAngularVelocity(VECTORS.get());

			// solve swing limit
			if (solveSwingLimit) {
				tmp.sub(angVelB, angVelA);
				float amplitude = tmp.dot(swingAxis) * relaxationFactor * relaxationFactor
						+ swingCorrection * (1f / timeStep) * biasFactor;
				float impulseMag = amplitude * kSwing;

				// Clamp the accumulated impulse
				float temp = accSwingLimitImpulse;
				accSwingLimitImpulse = Math.max(accSwingLimitImpulse + impulseMag, 0.0f);
				impulseMag = accSwingLimitImpulse - temp;

				Vector3f impulse = VECTORS.get();
				impulse.scale(impulseMag, swingAxis);

				rbA.applyTorqueImpulse(impulse);

				tmp.negate(impulse);
				rbB.applyTorqueImpulse(tmp);
				VECTORS.release(impulse);
			}

			// solve twist limit
			if (solveTwistLimit) {
				tmp.sub(angVelB, angVelA);
				float amplitude = tmp.dot(twistAxis) * relaxationFactor * relaxationFactor
						+ twistCorrection * (1f / timeStep) * biasFactor;
				float impulseMag = amplitude * kTwist;

				// Clamp the accumulated impulse
				float temp = accTwistLimitImpulse;
				accTwistLimitImpulse = Math.max(accTwistLimitImpulse + impulseMag, 0.0f);
				impulseMag = accTwistLimitImpulse - temp;

				Vector3f impulse = VECTORS.get();
				impulse.scale(impulseMag, twistAxis);

				rbA.applyTorqueImpulse(impulse);

				tmp.negate(impulse);
				rbB.applyTorqueImpulse(tmp);
			}
			VECTORS.release(angVelA, angVelB);
		}
		VECTORS.release(pivotAInW, pivotBInW, tmpVec, tmp, tmp2);
		TRANSFORMS.release(tmpTrans);
	}

	/**
	 * Update RHS.
	 *
	 * @param timeStep the time step
	 */
	public void updateRHS(final float timeStep) {}

	/**
	 * Sets the angular only.
	 *
	 * @param angularOnly the new angular only
	 */
	public void setAngularOnly(final boolean angularOnly) {
		this.angularOnly = angularOnly;
	}

	/**
	 * Sets the limit.
	 *
	 * @param _swingSpan1 the swing span 1
	 * @param _swingSpan2 the swing span 2
	 * @param _twistSpan the twist span
	 */
	public void setLimit(final float _swingSpan1, final float _swingSpan2, final float _twistSpan) {
		setLimit(_swingSpan1, _swingSpan2, _twistSpan, 0.8f, 0.3f, 1.0f);
	}

	/**
	 * Sets the limit.
	 *
	 * @param _swingSpan1 the swing span 1
	 * @param _swingSpan2 the swing span 2
	 * @param _twistSpan the twist span
	 * @param _softness the softness
	 * @param _biasFactor the bias factor
	 * @param _relaxationFactor the relaxation factor
	 */
	public void setLimit(final float _swingSpan1, final float _swingSpan2, final float _twistSpan,
			final float _softness, final float _biasFactor, final float _relaxationFactor) {
		swingSpan1 = _swingSpan1;
		swingSpan2 = _swingSpan2;
		twistSpan = _twistSpan;

		limitSoftness = _softness;
		biasFactor = _biasFactor;
		relaxationFactor = _relaxationFactor;
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
	 * Gets the solve twist limit.
	 *
	 * @return the solve twist limit
	 */
	public boolean getSolveTwistLimit() {
		return solveTwistLimit;
	}

	/**
	 * Gets the solve swing limit.
	 *
	 * @return the solve swing limit
	 */
	public boolean getSolveSwingLimit() {
		return solveTwistLimit;
	}

	/**
	 * Gets the twist limit sign.
	 *
	 * @return the twist limit sign
	 */
	public float getTwistLimitSign() {
		return twistLimitSign;
	}

}
