/*******************************************************************************************************
 *
 * Point2PointConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * Point to point constraint between two rigid bodies each with a pivot point that descibes the "ballsocket" location in
 * local space.
 *
 * @author jezek2
 */
public class Point2PointConstraint extends TypedConstraint {

	/** The jac. */
	private final JacobianEntry[] jac =
			new JacobianEntry[]/* [3] */ { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() }; // 3
																											// orthogonal
																											// linear
																											// constraints

	/** The pivot in A. */
																											private final Vector3f pivotInA = new Vector3f();
	
	/** The pivot in B. */
	private final Vector3f pivotInB = new Vector3f();

	/** The setting. */
	public ConstraintSetting setting = new ConstraintSetting();

	/**
	 * Instantiates a new point 2 point constraint.
	 */
	public Point2PointConstraint() {
		super(TypedConstraintType.POINT2POINT_CONSTRAINT_TYPE);
	}

	/**
	 * Instantiates a new point 2 point constraint.
	 *
	 * @param rbA the rb A
	 * @param rbB the rb B
	 * @param pivotInA the pivot in A
	 * @param pivotInB the pivot in B
	 */
	public Point2PointConstraint(final RigidBody rbA, final RigidBody rbB, final Vector3f pivotInA,
			final Vector3f pivotInB) {
		super(TypedConstraintType.POINT2POINT_CONSTRAINT_TYPE, rbA, rbB);
		this.pivotInA.set(pivotInA);
		this.pivotInB.set(pivotInB);
	}

	/**
	 * Instantiates a new point 2 point constraint.
	 *
	 * @param rbA the rb A
	 * @param pivotInA the pivot in A
	 */
	public Point2PointConstraint(final RigidBody rbA, final Vector3f pivotInA) {
		super(TypedConstraintType.POINT2POINT_CONSTRAINT_TYPE, rbA);
		this.pivotInA.set(pivotInA);
		this.pivotInB.set(pivotInA);
		rbA.getCenterOfMassTransform(TRANSFORMS.get()).transform(this.pivotInB);
	}

	@Override
	public void buildJacobian() {
		appliedImpulse = 0f;

		Vector3f normal = VECTORS.get();
		normal.set(0f, 0f, 0f);

		Matrix3f tmpMat1 = MATRICES.get();
		Matrix3f tmpMat2 = MATRICES.get();
		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();
		Vector3f tmpVec = VECTORS.get();
		Vector3f vmp0 = VECTORS.get(), vmp1 = VECTORS.get();

		Transform centerOfMassA = rbA.getCenterOfMassTransform(TRANSFORMS.get());
		Transform centerOfMassB = rbB.getCenterOfMassTransform(TRANSFORMS.get());

		for (int i = 0; i < 3; i++) {
			VectorUtil.setCoord(normal, i, 1f);

			tmpMat1.transpose(centerOfMassA.basis);
			tmpMat2.transpose(centerOfMassB.basis);

			tmp1.set(pivotInA);
			centerOfMassA.transform(tmp1);
			tmp1.sub(rbA.getCenterOfMassPosition(tmpVec));

			tmp2.set(pivotInB);
			centerOfMassB.transform(tmp2);
			tmp2.sub(rbB.getCenterOfMassPosition(tmpVec));

			jac[i].init(tmpMat1, tmpMat2, tmp1, tmp2, normal, rbA.getInvInertiaDiagLocal(vmp0), rbA.getInvMass(),
					rbB.getInvInertiaDiagLocal(vmp1), rbB.getInvMass());
			VectorUtil.setCoord(normal, i, 0f);
		}
		VECTORS.release(tmp1, tmp2, tmpVec, normal, vmp0, vmp1);
		MATRICES.release(tmpMat1, tmpMat2);
		TRANSFORMS.release(centerOfMassA, centerOfMassB);
	}

	@Override
	public void solveConstraint(final float timeStep) {
		Vector3f tmp = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();
		Vector3f tmpVec = VECTORS.get();

		Transform centerOfMassA = rbA.getCenterOfMassTransform(TRANSFORMS.get());
		Transform centerOfMassB = rbB.getCenterOfMassTransform(TRANSFORMS.get());

		Vector3f pivotAInW = VECTORS.get(pivotInA);
		centerOfMassA.transform(pivotAInW);

		Vector3f pivotBInW = VECTORS.get(pivotInB);
		centerOfMassB.transform(pivotBInW);

		Vector3f normal = VECTORS.get();
		normal.set(0f, 0f, 0f);

		// btVector3 angvelA = m_rbA.getCenterOfMassTransform().getBasis().transpose() * m_rbA.getAngularVelocity();
		// btVector3 angvelB = m_rbB.getCenterOfMassTransform().getBasis().transpose() * m_rbB.getAngularVelocity();

		for (int i = 0; i < 3; i++) {
			VectorUtil.setCoord(normal, i, 1f);
			float jacDiagABInv = 1f / jac[i].getDiagonal();

			Vector3f rel_pos1 = VECTORS.get();
			rel_pos1.sub(pivotAInW, rbA.getCenterOfMassPosition(tmpVec));
			Vector3f rel_pos2 = VECTORS.get();
			rel_pos2.sub(pivotBInW, rbB.getCenterOfMassPosition(tmpVec));
			// this jacobian entry could be re-used for all iterations

			Vector3f vel1 = rbA.getVelocityInLocalPoint(rel_pos1, VECTORS.get());
			Vector3f vel2 = rbB.getVelocityInLocalPoint(rel_pos2, VECTORS.get());
			Vector3f vel = VECTORS.get();
			vel.sub(vel1, vel2);

			float rel_vel;
			rel_vel = normal.dot(vel);

			/*
			 * //velocity error (first order error) btScalar rel_vel =
			 * m_jac[i].getRelativeVelocity(m_rbA.getLinearVelocity(),angvelA, m_rbB.getLinearVelocity(),angvelB);
			 */

			// positional error (zeroth order error)
			tmp.sub(pivotAInW, pivotBInW);
			float depth = -tmp.dot(normal); // this is the error projected on the normal

			float impulse = depth * setting.tau / timeStep * jacDiagABInv - setting.damping * rel_vel * jacDiagABInv;

			float impulseClamp = setting.impulseClamp;
			if (impulseClamp > 0f) {
				if (impulse < -impulseClamp) { impulse = -impulseClamp; }
				if (impulse > impulseClamp) { impulse = impulseClamp; }
			}

			appliedImpulse += impulse;
			Vector3f impulse_vector = VECTORS.get();
			impulse_vector.scale(impulse, normal);
			tmp.sub(pivotAInW, rbA.getCenterOfMassPosition(tmpVec));
			rbA.applyImpulse(impulse_vector, tmp);
			tmp.negate(impulse_vector);
			tmp2.sub(pivotBInW, rbB.getCenterOfMassPosition(tmpVec));
			rbB.applyImpulse(tmp, tmp2);

			VectorUtil.setCoord(normal, i, 0f);
			VECTORS.release(rel_pos1, rel_pos2, vel1, vel2, vel, impulse_vector);
		}
		VECTORS.release(normal, pivotBInW, pivotAInW, tmp, tmp2, tmpVec);
		TRANSFORMS.release(centerOfMassA, centerOfMassB);
	}

	/**
	 * Update RHS.
	 *
	 * @param timeStep the time step
	 */
	public void updateRHS(final float timeStep) {}

	/**
	 * Sets the pivot A.
	 *
	 * @param pivotA the new pivot A
	 */
	public void setPivotA(final Vector3f pivotA) {
		pivotInA.set(pivotA);
	}

	/**
	 * Sets the pivot B.
	 *
	 * @param pivotB the new pivot B
	 */
	public void setPivotB(final Vector3f pivotB) {
		pivotInB.set(pivotB);
	}

	/**
	 * Gets the pivot in A.
	 *
	 * @param out the out
	 * @return the pivot in A
	 */
	public Vector3f getPivotInA(final Vector3f out) {
		out.set(pivotInA);
		return out;
	}

	/**
	 * Gets the pivot in B.
	 *
	 * @param out the out
	 * @return the pivot in B
	 */
	public Vector3f getPivotInB(final Vector3f out) {
		out.set(pivotInB);
		return out;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class ConstraintSetting.
	 */
	public static class ConstraintSetting {
		
		/** The tau. */
		public float tau = 0.3f;
		
		/** The damping. */
		public float damping = 1f;
		
		/** The impulse clamp. */
		public float impulseClamp = 0f;
	}

}
