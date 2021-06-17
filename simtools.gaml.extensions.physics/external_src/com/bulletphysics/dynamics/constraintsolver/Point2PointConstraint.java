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

	private final JacobianEntry[] jac =
			new JacobianEntry[]/* [3] */ { new JacobianEntry(), new JacobianEntry(), new JacobianEntry() }; // 3
																											// orthogonal
																											// linear
																											// constraints

	private final Vector3f pivotInA = new Vector3f();
	private final Vector3f pivotInB = new Vector3f();

	public ConstraintSetting setting = new ConstraintSetting();

	public Point2PointConstraint() {
		super(TypedConstraintType.POINT2POINT_CONSTRAINT_TYPE);
	}

	public Point2PointConstraint(final RigidBody rbA, final RigidBody rbB, final Vector3f pivotInA,
			final Vector3f pivotInB) {
		super(TypedConstraintType.POINT2POINT_CONSTRAINT_TYPE, rbA, rbB);
		this.pivotInA.set(pivotInA);
		this.pivotInB.set(pivotInB);
	}

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

	public void updateRHS(final float timeStep) {}

	public void setPivotA(final Vector3f pivotA) {
		pivotInA.set(pivotA);
	}

	public void setPivotB(final Vector3f pivotB) {
		pivotInB.set(pivotB);
	}

	public Vector3f getPivotInA(final Vector3f out) {
		out.set(pivotInA);
		return out;
	}

	public Vector3f getPivotInB(final Vector3f out) {
		out.set(pivotInB);
		return out;
	}

	////////////////////////////////////////////////////////////////////////////

	public static class ConstraintSetting {
		public float tau = 0.3f;
		public float damping = 1f;
		public float impulseClamp = 0f;
	}

}
