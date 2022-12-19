/*******************************************************************************************************
 *
 * ContactConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

import static com.bulletphysics.Pools.JACOBIANS;
import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.RigidBody;

/**
 * Functions for resolving contacts.
 *
 * @author jezek2
 */
public class ContactConstraint {

	/** The Constant resolveSingleCollision. */
	public static final ContactSolverFunc resolveSingleCollision = new ContactSolverFunc() {
		@Override
		public float resolveContact(final RigidBody body1, final RigidBody body2, final ManifoldPoint contactPoint,
				final ContactSolverInfo info) {
			return resolveSingleCollision(body1, body2, contactPoint, info);
		}
	};

	/** The Constant resolveSingleFriction. */
	public static final ContactSolverFunc resolveSingleFriction = new ContactSolverFunc() {
		@Override
		public float resolveContact(final RigidBody body1, final RigidBody body2, final ManifoldPoint contactPoint,
				final ContactSolverInfo info) {
			return resolveSingleFriction(body1, body2, contactPoint, info);
		}
	};

	/** The Constant resolveSingleCollisionCombined. */
	public static final ContactSolverFunc resolveSingleCollisionCombined = new ContactSolverFunc() {
		@Override
		public float resolveContact(final RigidBody body1, final RigidBody body2, final ManifoldPoint contactPoint,
				final ContactSolverInfo info) {
			return resolveSingleCollisionCombined(body1, body2, contactPoint, info);
		}
	};

	/**
	 * Bilateral constraint between two dynamic objects.
	 */
	public static void resolveSingleBilateral(final RigidBody body1, final Vector3f pos1, final RigidBody body2,
			final Vector3f pos2, final float distance, final Vector3f normal, final float[] impulse,
			final float timeStep) {
		float normalLenSqr = normal.lengthSquared();
		assert Math.abs(normalLenSqr) < 1.1f;
		if (normalLenSqr > 1.1f) {
			impulse[0] = 0f;
			return;
		}

		// ObjectPool<JacobianEntry> jacobiansPool = ObjectPool.get(JacobianEntry.class);
		Vector3f tmp = VECTORS.get();
		Vector3f rel_pos1 = VECTORS.get();
		rel_pos1.sub(pos1, body1.getCenterOfMassPosition(tmp));
		Vector3f rel_pos2 = VECTORS.get();
		rel_pos2.sub(pos2, body2.getCenterOfMassPosition(tmp));

		// this jacobian entry could be re-used for all iterations

		Vector3f vel1 = VECTORS.get();
		body1.getVelocityInLocalPoint(rel_pos1, vel1);
		Vector3f vel2 = VECTORS.get();
		body2.getVelocityInLocalPoint(rel_pos2, vel2);
		Vector3f vel = VECTORS.get();
		vel.sub(vel1, vel2);
		Matrix3f mat1 = body1.getCenterOfMassTransform(TRANSFORMS.get()).basis;
		mat1.transpose();
		Matrix3f mat2 = body2.getCenterOfMassTransform(TRANSFORMS.get()).basis;
		mat2.transpose();

		JacobianEntry jac = JACOBIANS.get();
		jac.init(mat1, mat2, rel_pos1, rel_pos2, normal, body1.getInvInertiaDiagLocal(VECTORS.get()),
				body1.getInvMass(), body2.getInvInertiaDiagLocal(VECTORS.get()), body2.getInvMass());

		float jacDiagAB = jac.getDiagonal();
		float jacDiagABInv = 1f / jacDiagAB;

		Vector3f tmp1 = body1.getAngularVelocity(VECTORS.get());
		mat1.transform(tmp1);

		Vector3f tmp2 = body2.getAngularVelocity(VECTORS.get());
		mat2.transform(tmp2);

		Vector3f tmp3 = body1.getLinearVelocity(VECTORS.get());
		Vector3f tmp4 = body2.getLinearVelocity(VECTORS.get());
		float rel_vel = jac.getRelativeVelocity(tmp3, tmp1, tmp4, tmp2);

		JACOBIANS.release(jac);

		// float a;
		// a = jacDiagABInv;

		rel_vel = normal.dot(vel);

		// todo: move this into proper structure
		float contactDamping = 0.2f;

		// #ifdef ONLY_USE_LINEAR_MASS
		// btScalar massTerm = btScalar(1.) / (body1.getInvMass() + body2.getInvMass());
		// impulse = - contactDamping * rel_vel * massTerm;
		// #else
		float velocityImpulse = -contactDamping * rel_vel * jacDiagABInv;
		impulse[0] = velocityImpulse;
		// #endif
		VECTORS.release(tmp1, tmp2, tmp3, tmp4, vel, vel1, vel2, tmp, rel_pos1, rel_pos2);
		MATRICES.release(mat1, mat2);
	}

	/**
	 * Response between two dynamic objects with friction.
	 */
	public static float resolveSingleCollision(final RigidBody body1, final RigidBody body2,
			final ManifoldPoint contactPoint, final ContactSolverInfo solverInfo) {

		Vector3f tmpVec = VECTORS.get();
		Vector3f pos1_ = contactPoint.getPositionWorldOnA(VECTORS.get());
		Vector3f pos2_ = contactPoint.getPositionWorldOnB(VECTORS.get());
		Vector3f normal = contactPoint.normalWorldOnB;
		// constant over all iterations
		Vector3f rel_pos1 = VECTORS.get();
		rel_pos1.sub(pos1_, body1.getCenterOfMassPosition(tmpVec));
		Vector3f rel_pos2 = VECTORS.get();
		rel_pos2.sub(pos2_, body2.getCenterOfMassPosition(tmpVec));
		Vector3f vel1 = body1.getVelocityInLocalPoint(rel_pos1, VECTORS.get());
		Vector3f vel2 = body2.getVelocityInLocalPoint(rel_pos2, VECTORS.get());
		Vector3f vel = VECTORS.get();
		vel.sub(vel1, vel2);
		float rel_vel;
		rel_vel = normal.dot(vel);
		float Kfps = 1f / solverInfo.timeStep;
		// btScalar damping = solverInfo.m_damping ;
		float Kerp = solverInfo.erp;
		float Kcor = Kerp * Kfps;
		ConstraintPersistentData cpd = (ConstraintPersistentData) contactPoint.userPersistentData;
		assert cpd != null;
		float distance = cpd.penetration;
		float positionalError = Kcor * -distance;
		float velocityError = cpd.restitution - rel_vel; // * damping;
		float penetrationImpulse = positionalError * cpd.jacDiagABInv;
		float velocityImpulse = velocityError * cpd.jacDiagABInv;
		float normalImpulse = penetrationImpulse + velocityImpulse;
		// See Erin Catto's GDC 2006 paper: Clamp the accumulated impulse
		float oldNormalImpulse = cpd.appliedImpulse;
		float sum = oldNormalImpulse + normalImpulse;
		cpd.appliedImpulse = 0f > sum ? 0f : sum;
		normalImpulse = cpd.appliedImpulse - oldNormalImpulse;
		// #ifdef USE_INTERNAL_APPLY_IMPULSE
		Vector3f tmp = VECTORS.get();
		if (body1.getInvMass() != 0f) {
			tmp.scale(body1.getInvMass(), contactPoint.normalWorldOnB);
			body1.internalApplyImpulse(tmp, cpd.angularComponentA, normalImpulse);
		}
		if (body2.getInvMass() != 0f) {
			tmp.scale(body2.getInvMass(), contactPoint.normalWorldOnB);
			body2.internalApplyImpulse(tmp, cpd.angularComponentB, -normalImpulse);
		}
		VECTORS.release(tmpVec, pos1_, pos2_, rel_pos1, rel_pos2, vel1, vel2, vel, tmp);
		return normalImpulse;
	}

	/**
	 * Resolve single friction.
	 *
	 * @param body1 the body 1
	 * @param body2 the body 2
	 * @param contactPoint the contact point
	 * @param solverInfo the solver info
	 * @return the float
	 */
	public static float resolveSingleFriction(final RigidBody body1, final RigidBody body2,
			final ManifoldPoint contactPoint, final ContactSolverInfo solverInfo) {
		Vector3f tmpVec = VECTORS.get();
		Vector3f pos1 = contactPoint.getPositionWorldOnA(VECTORS.get());
		Vector3f pos2 = contactPoint.getPositionWorldOnB(VECTORS.get());
		Vector3f rel_pos1 = VECTORS.get();
		rel_pos1.sub(pos1, body1.getCenterOfMassPosition(tmpVec));
		Vector3f rel_pos2 = VECTORS.get();
		rel_pos2.sub(pos2, body2.getCenterOfMassPosition(tmpVec));
		ConstraintPersistentData cpd = (ConstraintPersistentData) contactPoint.userPersistentData;
		assert cpd != null;
		float combinedFriction = cpd.friction;
		float limit = cpd.appliedImpulse * combinedFriction;
		if (cpd.appliedImpulse > 0f) {
			// apply friction in the 2 tangential directions
			// 1st tangent
			Vector3f vel1 = VECTORS.get();
			body1.getVelocityInLocalPoint(rel_pos1, vel1);
			Vector3f vel2 = VECTORS.get();
			body2.getVelocityInLocalPoint(rel_pos2, vel2);
			Vector3f vel = VECTORS.get();
			vel.sub(vel1, vel2);
			float j1, j2;
			{
				float vrel = cpd.frictionWorldTangential0.dot(vel);
				// calculate j that moves us to zero relative velocity
				j1 = -vrel * cpd.jacDiagABInvTangent0;
				float oldTangentImpulse = cpd.accumulatedTangentImpulse0;
				cpd.accumulatedTangentImpulse0 = oldTangentImpulse + j1;

				cpd.accumulatedTangentImpulse0 = Math.min(cpd.accumulatedTangentImpulse0, limit);
				cpd.accumulatedTangentImpulse0 = Math.max(cpd.accumulatedTangentImpulse0, -limit);
				j1 = cpd.accumulatedTangentImpulse0 - oldTangentImpulse;
			}
			{
				// 2nd tangent
				float vrel = cpd.frictionWorldTangential1.dot(vel);
				// calculate j that moves us to zero relative velocity
				j2 = -vrel * cpd.jacDiagABInvTangent1;
				float oldTangentImpulse = cpd.accumulatedTangentImpulse1;
				cpd.accumulatedTangentImpulse1 = oldTangentImpulse + j2;

				cpd.accumulatedTangentImpulse1 = Math.min(cpd.accumulatedTangentImpulse1, limit);
				cpd.accumulatedTangentImpulse1 = Math.max(cpd.accumulatedTangentImpulse1, -limit);
				j2 = cpd.accumulatedTangentImpulse1 - oldTangentImpulse;
			}

			// #ifdef USE_INTERNAL_APPLY_IMPULSE
			Vector3f tmp = VECTORS.get();
			if (body1.getInvMass() != 0f) {
				tmp.scale(body1.getInvMass(), cpd.frictionWorldTangential0);
				body1.internalApplyImpulse(tmp, cpd.frictionAngularComponent0A, j1);

				tmp.scale(body1.getInvMass(), cpd.frictionWorldTangential1);
				body1.internalApplyImpulse(tmp, cpd.frictionAngularComponent1A, j2);
			}
			if (body2.getInvMass() != 0f) {
				tmp.scale(body2.getInvMass(), cpd.frictionWorldTangential0);
				body2.internalApplyImpulse(tmp, cpd.frictionAngularComponent0B, -j1);

				tmp.scale(body2.getInvMass(), cpd.frictionWorldTangential1);
				body2.internalApplyImpulse(tmp, cpd.frictionAngularComponent1B, -j2);
			}
			VECTORS.release(vel1, vel2, vel, tmp);
		}
		VECTORS.release(tmpVec, pos1, pos2, rel_pos1, rel_pos2);

		return cpd.appliedImpulse;
	}

	/**
	 * velocity + friction<br>
	 * response between two dynamic objects with friction
	 */
	public static float resolveSingleCollisionCombined(final RigidBody body1, final RigidBody body2,
			final ManifoldPoint contactPoint, final ContactSolverInfo solverInfo) {

		Vector3f tmpVec = VECTORS.get();

		Vector3f pos1 = contactPoint.getPositionWorldOnA(VECTORS.get());
		Vector3f pos2 = contactPoint.getPositionWorldOnB(VECTORS.get());
		Vector3f normal = contactPoint.normalWorldOnB;

		Vector3f rel_pos1 = VECTORS.get();
		rel_pos1.sub(pos1, body1.getCenterOfMassPosition(tmpVec));

		Vector3f rel_pos2 = VECTORS.get();
		rel_pos2.sub(pos2, body2.getCenterOfMassPosition(tmpVec));

		Vector3f vel1 = body1.getVelocityInLocalPoint(rel_pos1, VECTORS.get());
		Vector3f vel2 = body2.getVelocityInLocalPoint(rel_pos2, VECTORS.get());
		Vector3f vel = VECTORS.get();
		vel.sub(vel1, vel2);

		float rel_vel;
		rel_vel = normal.dot(vel);

		float Kfps = 1f / solverInfo.timeStep;

		// btScalar damping = solverInfo.m_damping ;
		float Kerp = solverInfo.erp;
		float Kcor = Kerp * Kfps;

		ConstraintPersistentData cpd = (ConstraintPersistentData) contactPoint.userPersistentData;
		assert cpd != null;
		float distance = cpd.penetration;
		float positionalError = Kcor * -distance;
		float velocityError = cpd.restitution - rel_vel;// * damping;

		float penetrationImpulse = positionalError * cpd.jacDiagABInv;

		float velocityImpulse = velocityError * cpd.jacDiagABInv;

		float normalImpulse = penetrationImpulse + velocityImpulse;

		// See Erin Catto's GDC 2006 paper: Clamp the accumulated impulse
		float oldNormalImpulse = cpd.appliedImpulse;
		float sum = oldNormalImpulse + normalImpulse;
		cpd.appliedImpulse = 0f > sum ? 0f : sum;

		normalImpulse = cpd.appliedImpulse - oldNormalImpulse;

		// #ifdef USE_INTERNAL_APPLY_IMPULSE
		Vector3f tmp = VECTORS.get();
		if (body1.getInvMass() != 0f) {
			tmp.scale(body1.getInvMass(), contactPoint.normalWorldOnB);
			body1.internalApplyImpulse(tmp, cpd.angularComponentA, normalImpulse);
		}
		if (body2.getInvMass() != 0f) {
			tmp.scale(body2.getInvMass(), contactPoint.normalWorldOnB);
			body2.internalApplyImpulse(tmp, cpd.angularComponentB, -normalImpulse);
		}
		// #else //USE_INTERNAL_APPLY_IMPULSE
		// body1.applyImpulse(normal*(normalImpulse), rel_pos1);
		// body2.applyImpulse(-normal*(normalImpulse), rel_pos2);
		// #endif //USE_INTERNAL_APPLY_IMPULSE

		{
			// friction
			body1.getVelocityInLocalPoint(rel_pos1, vel1);
			body2.getVelocityInLocalPoint(rel_pos2, vel2);
			vel.sub(vel1, vel2);

			rel_vel = normal.dot(vel);

			tmp.scale(rel_vel, normal);
			Vector3f lat_vel = VECTORS.get();
			lat_vel.sub(vel, tmp);
			float lat_rel_vel = lat_vel.length();

			float combinedFriction = cpd.friction;

			if (cpd.appliedImpulse > 0) {
				if (lat_rel_vel > BulletGlobals.FLT_EPSILON) {
					lat_vel.scale(1f / lat_rel_vel);

					Vector3f temp1 = VECTORS.get();
					temp1.cross(rel_pos1, lat_vel);
					body1.getInvInertiaTensorWorld(MATRICES.get()).transform(temp1);

					Vector3f temp2 = VECTORS.get();
					temp2.cross(rel_pos2, lat_vel);
					body2.getInvInertiaTensorWorld(MATRICES.get()).transform(temp2);

					Vector3f java_tmp1 = VECTORS.get();
					java_tmp1.cross(temp1, rel_pos1);

					Vector3f java_tmp2 = VECTORS.get();
					java_tmp2.cross(temp2, rel_pos2);

					tmp.add(java_tmp1, java_tmp2);

					float friction_impulse = lat_rel_vel / (body1.getInvMass() + body2.getInvMass() + lat_vel.dot(tmp));
					float normal_impulse = cpd.appliedImpulse * combinedFriction;

					friction_impulse = Math.min(friction_impulse, normal_impulse);
					friction_impulse = Math.max(friction_impulse, -normal_impulse);

					tmp.scale(-friction_impulse, lat_vel);
					body1.applyImpulse(tmp, rel_pos1);

					tmp.scale(friction_impulse, lat_vel);
					body2.applyImpulse(tmp, rel_pos2);
					VECTORS.release(temp1, temp2, java_tmp1, java_tmp2);
				}
			}
			VECTORS.release(lat_vel);
		}
		VECTORS.release(tmp, tmpVec, vel, vel1, vel2, pos1, pos2, rel_pos1, rel_pos2);
		return normalImpulse;
	}

	/**
	 * Resolve single friction empty.
	 *
	 * @param body1 the body 1
	 * @param body2 the body 2
	 * @param contactPoint the contact point
	 * @param solverInfo the solver info
	 * @return the float
	 */
	public static float resolveSingleFrictionEmpty(final RigidBody body1, final RigidBody body2,
			final ManifoldPoint contactPoint, final ContactSolverInfo solverInfo) {
		return 0f;
	}

}
