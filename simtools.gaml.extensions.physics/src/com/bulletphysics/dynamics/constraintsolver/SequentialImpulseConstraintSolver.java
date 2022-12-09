/*******************************************************************************************************
 *
 * SequentialImpulseConstraintSolver.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

import static com.bulletphysics.Pools.BODIES;
import static com.bulletphysics.Pools.CONSTRAINTS;
import static com.bulletphysics.Pools.JACOBIANS;
import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MiscUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.TransformUtil;
import com.bulletphysics.util.IntArrayList;

/**
 * SequentialImpulseConstraintSolver uses a Propagation Method and Sequentially applies impulses. The approach is the 3D
 * version of Erin Catto's GDC 2006 tutorial. See http://www.gphysics.com
 * <p>
 *
 * Although Sequential Impulse is more intuitive, it is mathematically equivalent to Projected Successive Overrelaxation
 * (iterative LCP).
 * <p>
 *
 * Applies impulses for combined restitution and penetration recovery and to simulate friction.
 *
 * @author jezek2
 */
public class SequentialImpulseConstraintSolver implements ConstraintSolver {

	// Prevents parallel operations !
	/** The temp vec. */
	// Done for optimization purposes
	Vector3f tempVec = new Vector3f();
	
	/** The temp trans. */
	Transform tempTrans = new Transform();
	
	/** The temp mat. */
	Matrix3f tempMat = new Matrix3f();

	/** The Constant MAX_CONTACT_SOLVER_TYPES. */
	private static final int MAX_CONTACT_SOLVER_TYPES = ContactConstraintEnum.MAX_CONTACT_SOLVER_TYPES.ordinal();

	/** The Constant SEQUENTIAL_IMPULSE_MAX_SOLVER_POINTS. */
	private static final int SEQUENTIAL_IMPULSE_MAX_SOLVER_POINTS = 16384;
	
	/** The g order. */
	private final OrderIndex[] gOrder = new OrderIndex[SEQUENTIAL_IMPULSE_MAX_SOLVER_POINTS];

	/** The total cpd. */
	private int totalCpd = 0;

	{
		for (int i = 0; i < gOrder.length; i++) {
			gOrder[i] = new OrderIndex();
		}
	}

	////////////////////////////////////////////////////////////////////////////

	// private final ObjectPool<SolverBody> bodiesPool = ObjectPool.get(SolverBody.class);
	// private final ObjectPool<SolverConstraint> constraintsPool = ObjectPool.get(SolverConstraint.class);
	// private final ObjectPool<JacobianEntry> jacobiansPool = ObjectPool.get(JacobianEntry.class);

	/** The tmp solver body pool. */
	private final ArrayList<SolverBody> tmpSolverBodyPool = new ArrayList<>();
	
	/** The tmp solver constraint pool. */
	private final ArrayList<SolverConstraint> tmpSolverConstraintPool = new ArrayList<>();
	
	/** The tmp solver friction constraint pool. */
	private final ArrayList<SolverConstraint> tmpSolverFrictionConstraintPool = new ArrayList<>();
	
	/** The order tmp constraint pool. */
	private final IntArrayList orderTmpConstraintPool = new IntArrayList();
	
	/** The order friction constraint pool. */
	private final IntArrayList orderFrictionConstraintPool = new IntArrayList();

	/** The contact dispatch. */
	protected final ContactSolverFunc[][] contactDispatch =
			new ContactSolverFunc[MAX_CONTACT_SOLVER_TYPES][MAX_CONTACT_SOLVER_TYPES];
	
	/** The friction dispatch. */
	protected final ContactSolverFunc[][] frictionDispatch =
			new ContactSolverFunc[MAX_CONTACT_SOLVER_TYPES][MAX_CONTACT_SOLVER_TYPES];

	/** The bt seed 2. */
	// btSeed2 is used for re-arranging the constraint rows. improves convergence/quality of friction
	protected long btSeed2 = 0L;

	/**
	 * Instantiates a new sequential impulse constraint solver.
	 */
	public SequentialImpulseConstraintSolver() {

		// initialize default friction/contact funcs
		int i, j;
		for (i = 0; i < MAX_CONTACT_SOLVER_TYPES; i++) {
			for (j = 0; j < MAX_CONTACT_SOLVER_TYPES; j++) {
				contactDispatch[i][j] = ContactConstraint.resolveSingleCollision;
				frictionDispatch[i][j] = ContactConstraint.resolveSingleFriction;
			}
		}
	}

	/**
	 * Rand 2.
	 *
	 * @return the long
	 */
	public long rand2() {
		btSeed2 = 1664525L * btSeed2 + 1013904223L & 0xffffffff;
		return btSeed2;
	}

	/**
	 * Rand int 2.
	 *
	 * @param n the n
	 * @return the int
	 */
	// See ODE: adam's all-int straightforward(?) dRandInt (0..n-1)
	public int randInt2(final int n) {
		// seems good; xor-fold and modulus
		long un = n;
		long r = rand2();

		// note: probably more aggressive than it needs to be -- might be
		// able to get away without one or two of the innermost branches.
		if (un <= 0x00010000L) {
			r ^= r >>> 16;
			if (un <= 0x00000100L) {
				r ^= r >>> 8;
				if (un <= 0x00000010L) {
					r ^= r >>> 4;
					if (un <= 0x00000004L) {
						r ^= r >>> 2;
						if (un <= 0x00000002L) { r ^= r >>> 1; }
					}
				}
			}
		}

		// TODO: check modulo C vs Java mismatch
		return (int) Math.abs(r % un);
	}

	/**
	 * Inits the solver body.
	 *
	 * @param solverBody the solver body
	 * @param collisionObject the collision object
	 */
	private void initSolverBody(final SolverBody solverBody, final CollisionObject collisionObject) {
		RigidBody rb = RigidBody.upcast(collisionObject);
		// Transform tmp = TRANSFORMS.get();
		if (rb != null) {
			rb.getAngularVelocity(solverBody.angularVelocity);

			solverBody.centerOfMassPosition.set(collisionObject.getWorldTransform(tempTrans).origin);
			solverBody.friction = collisionObject.getFriction();
			solverBody.invMass = rb.getInvMass();
			rb.getLinearVelocity(solverBody.linearVelocity);
			solverBody.originalBody = rb;
			solverBody.angularFactor = rb.getAngularFactor();
		} else {
			solverBody.angularVelocity.set(0f, 0f, 0f);
			solverBody.centerOfMassPosition.set(collisionObject.getWorldTransform(tempTrans).origin);
			solverBody.friction = collisionObject.getFriction();
			solverBody.invMass = 0f;
			solverBody.linearVelocity.set(0f, 0f, 0f);
			solverBody.originalBody = null;
			solverBody.angularFactor = 1f;
		}

		solverBody.pushVelocity.set(0f, 0f, 0f);
		solverBody.turnVelocity.set(0f, 0f, 0f);
		// TRANSFORMS.release(tmp);
	}

	/**
	 * Restitution curve.
	 *
	 * @param rel_vel the rel vel
	 * @param restitution the restitution
	 * @return the float
	 */
	private float restitutionCurve(final float rel_vel, final float restitution) {
		float rest = restitution * -rel_vel;
		return rest;
	}

	/**
	 * Resolve split penetration impulse cache friendly.
	 *
	 * @param body1 the body 1
	 * @param body2 the body 2
	 * @param contactConstraint the contact constraint
	 * @param solverInfo the solver info
	 */
	private void resolveSplitPenetrationImpulseCacheFriendly(final SolverBody body1, final SolverBody body2,
			final SolverConstraint contactConstraint, final ContactSolverInfo solverInfo) {

		if (contactConstraint.penetration < solverInfo.splitImpulsePenetrationThreshold) {
			// BulletStats.gNumSplitImpulseRecoveries++;
			float normalImpulse;

			// Optimized version of projected relative velocity, use precomputed cross products with normal
			// body1.getVelocityInLocalPoint(contactConstraint.m_rel_posA,vel1);
			// body2.getVelocityInLocalPoint(contactConstraint.m_rel_posB,vel2);
			// btVector3 vel = vel1 - vel2;
			// btScalar rel_vel = contactConstraint.m_contactNormal.dot(vel);

			float rel_vel;
			float vel1Dotn = contactConstraint.contactNormal.dot(body1.pushVelocity)
					+ contactConstraint.relpos1CrossNormal.dot(body1.turnVelocity);
			float vel2Dotn = contactConstraint.contactNormal.dot(body2.pushVelocity)
					+ contactConstraint.relpos2CrossNormal.dot(body2.turnVelocity);

			rel_vel = vel1Dotn - vel2Dotn;

			float positionalError = -contactConstraint.penetration * solverInfo.erp2 / solverInfo.timeStep;
			// btScalar positionalError = contactConstraint.m_penetration;

			float velocityError = contactConstraint.restitution - rel_vel;// * damping;

			float penetrationImpulse = positionalError * contactConstraint.jacDiagABInv;
			float velocityImpulse = velocityError * contactConstraint.jacDiagABInv;
			normalImpulse = penetrationImpulse + velocityImpulse;

			// See Erin Catto's GDC 2006 paper: Clamp the accumulated impulse
			float oldNormalImpulse = contactConstraint.appliedPushImpulse;
			float sum = oldNormalImpulse + normalImpulse;
			contactConstraint.appliedPushImpulse = 0f > sum ? 0f : sum;

			normalImpulse = contactConstraint.appliedPushImpulse - oldNormalImpulse;

			// Vector3f tmp = VECTORS.get();

			tempVec.scale(body1.invMass, contactConstraint.contactNormal);
			body1.internalApplyPushImpulse(tempVec, contactConstraint.angularComponentA, normalImpulse);

			tempVec.scale(body2.invMass, contactConstraint.contactNormal);
			body2.internalApplyPushImpulse(tempVec, contactConstraint.angularComponentB, -normalImpulse);
			// VECTORS.release(tmp);
		}
	}

	/**
	 * velocity + friction response between two dynamic objects with friction
	 */
	private float resolveSingleCollisionCombinedCacheFriendly(final SolverBody body1, final SolverBody body2,
			final SolverConstraint contactConstraint, final ContactSolverInfo solverInfo) {

		float normalImpulse;

		{

			float rel_vel;
			float vel1Dotn = contactConstraint.contactNormal.dot(body1.linearVelocity)
					+ contactConstraint.relpos1CrossNormal.dot(body1.angularVelocity);
			float vel2Dotn = contactConstraint.contactNormal.dot(body2.linearVelocity)
					+ contactConstraint.relpos2CrossNormal.dot(body2.angularVelocity);

			rel_vel = vel1Dotn - vel2Dotn;

			float positionalError = 0.f;
			if (!solverInfo.splitImpulse
					|| contactConstraint.penetration > solverInfo.splitImpulsePenetrationThreshold) {
				positionalError = -contactConstraint.penetration * solverInfo.erp / solverInfo.timeStep;
			}

			float velocityError = contactConstraint.restitution - rel_vel;// * damping;

			float penetrationImpulse = positionalError * contactConstraint.jacDiagABInv;
			float velocityImpulse = velocityError * contactConstraint.jacDiagABInv;
			normalImpulse = penetrationImpulse + velocityImpulse;

			// See Erin Catto's GDC 2006 paper: Clamp the accumulated impulse
			float oldNormalImpulse = contactConstraint.appliedImpulse;
			float sum = oldNormalImpulse + normalImpulse;
			contactConstraint.appliedImpulse = 0f > sum ? 0f : sum;

			normalImpulse = contactConstraint.appliedImpulse - oldNormalImpulse;

			// Vector3f tmp = VECTORS.get();

			tempVec.scale(body1.invMass, contactConstraint.contactNormal);
			body1.internalApplyImpulse(tempVec, contactConstraint.angularComponentA, normalImpulse);

			tempVec.scale(body2.invMass, contactConstraint.contactNormal);
			body2.internalApplyImpulse(tempVec, contactConstraint.angularComponentB, -normalImpulse);
			// VECTORS.release(tmp);
		}

		return normalImpulse;
	}

	/**
	 * Resolve single friction cache friendly.
	 *
	 * @param body1 the body 1
	 * @param body2 the body 2
	 * @param contactConstraint the contact constraint
	 * @param solverInfo the solver info
	 * @param appliedNormalImpulse the applied normal impulse
	 * @return the float
	 */
	private float resolveSingleFrictionCacheFriendly(final SolverBody body1, final SolverBody body2,
			final SolverConstraint contactConstraint, final ContactSolverInfo solverInfo,
			final float appliedNormalImpulse) {
		float combinedFriction = contactConstraint.friction;

		float limit = appliedNormalImpulse * combinedFriction;

		if (appliedNormalImpulse > 0f) // friction
		{

			float j1;
			{

				float rel_vel;
				float vel1Dotn = contactConstraint.contactNormal.dot(body1.linearVelocity)
						+ contactConstraint.relpos1CrossNormal.dot(body1.angularVelocity);
				float vel2Dotn = contactConstraint.contactNormal.dot(body2.linearVelocity)
						+ contactConstraint.relpos2CrossNormal.dot(body2.angularVelocity);
				rel_vel = vel1Dotn - vel2Dotn;

				// calculate j that moves us to zero relative velocity
				j1 = -rel_vel * contactConstraint.jacDiagABInv;
				float oldTangentImpulse = contactConstraint.appliedImpulse;
				contactConstraint.appliedImpulse = oldTangentImpulse + j1;

				if (limit < contactConstraint.appliedImpulse) {
					contactConstraint.appliedImpulse = limit;
				} else {
					if (contactConstraint.appliedImpulse < -limit) { contactConstraint.appliedImpulse = -limit; }
				}
				j1 = contactConstraint.appliedImpulse - oldTangentImpulse;
			}

			// Vector3f tmp = VECTORS.get();

			tempVec.scale(body1.invMass, contactConstraint.contactNormal);
			body1.internalApplyImpulse(tempVec, contactConstraint.angularComponentA, j1);

			tempVec.scale(body2.invMass, contactConstraint.contactNormal);
			body2.internalApplyImpulse(tempVec, contactConstraint.angularComponentB, -j1);
			// VECTORS.release(tmp);
		}
		return 0f;
	}

	/**
	 * Adds the friction constraint.
	 *
	 * @param normalAxis the normal axis
	 * @param solverBodyIdA the solver body id A
	 * @param solverBodyIdB the solver body id B
	 * @param frictionIndex the friction index
	 * @param cp the cp
	 * @param rel_pos1 the rel pos 1
	 * @param rel_pos2 the rel pos 2
	 * @param colObj0 the col obj 0
	 * @param colObj1 the col obj 1
	 * @param relaxation the relaxation
	 */
	protected void addFrictionConstraint(final Vector3f normalAxis, final int solverBodyIdA, final int solverBodyIdB,
			final int frictionIndex, final ManifoldPoint cp, final Vector3f rel_pos1, final Vector3f rel_pos2,
			final CollisionObject colObj0, final CollisionObject colObj1, final float relaxation) {
		RigidBody body0 = RigidBody.upcast(colObj0);
		RigidBody body1 = RigidBody.upcast(colObj1);

		SolverConstraint solverConstraint = CONSTRAINTS.get();
		tmpSolverFrictionConstraintPool.add(solverConstraint);

		solverConstraint.contactNormal.set(normalAxis);

		solverConstraint.solverBodyIdA = solverBodyIdA;
		solverConstraint.solverBodyIdB = solverBodyIdB;
		solverConstraint.constraintType = SolverConstraintType.SOLVER_FRICTION_1D;
		solverConstraint.frictionIndex = frictionIndex;

		solverConstraint.friction = cp.combinedFriction;
		solverConstraint.originalContactPoint = null;

		solverConstraint.appliedImpulse = 0f;
		solverConstraint.appliedPushImpulse = 0f;
		solverConstraint.penetration = 0f;

		Vector3f ftorqueAxis1 = VECTORS.get();
		// Vector3f tmp = VECTORS.get();

		{
			ftorqueAxis1.cross(rel_pos1, solverConstraint.contactNormal);
			solverConstraint.relpos1CrossNormal.set(ftorqueAxis1);
			if (body0 != null) {
				solverConstraint.angularComponentA.set(ftorqueAxis1);
				body0.getInvInertiaTensorWorld(tempMat).transform(solverConstraint.angularComponentA);
			} else {
				solverConstraint.angularComponentA.set(0f, 0f, 0f);
			}
		}
		{
			ftorqueAxis1.cross(rel_pos2, solverConstraint.contactNormal);
			solverConstraint.relpos2CrossNormal.set(ftorqueAxis1);
			if (body1 != null) {
				solverConstraint.angularComponentB.set(ftorqueAxis1);
				body1.getInvInertiaTensorWorld(tempMat).transform(solverConstraint.angularComponentB);
			} else {
				solverConstraint.angularComponentB.set(0f, 0f, 0f);
			}
		}

		float denom0 = 0f;
		float denom1 = 0f;
		if (body0 != null) {
			tempVec.cross(solverConstraint.angularComponentA, rel_pos1);
			denom0 = body0.getInvMass() + normalAxis.dot(tempVec);
		}
		if (body1 != null) {
			tempVec.cross(solverConstraint.angularComponentB, rel_pos2);
			denom1 = body1.getInvMass() + normalAxis.dot(tempVec);
		}
		// #endif //COMPUTE_IMPULSE_DENOM

		float denom = relaxation / (denom0 + denom1);
		solverConstraint.jacDiagABInv = denom;
		VECTORS.release(/* tmp, */ ftorqueAxis1);
		// MATRICES.release(tmpMat);
	}

	/**
	 * Solve group cache friendly setup.
	 *
	 * @param bodies the bodies
	 * @param numBodies the num bodies
	 * @param manifoldPtr the manifold ptr
	 * @param manifold_offset the manifold offset
	 * @param numManifolds the num manifolds
	 * @param constraints the constraints
	 * @param constraints_offset the constraints offset
	 * @param numConstraints the num constraints
	 * @param infoGlobal the info global
	 * @return the float
	 */
	public float solveGroupCacheFriendlySetup(final ArrayList<CollisionObject> bodies, final int numBodies,
			final ArrayList<PersistentManifold> manifoldPtr, final int manifold_offset, final int numManifolds,
			final ArrayList<TypedConstraint> constraints, final int constraints_offset, final int numConstraints,
			final ContactSolverInfo infoGlobal) {
		// BulletStats.pushProfile("solveGroupCacheFriendlySetup");

		try {

			if (numConstraints + numManifolds == 0) // printf("empty\n");
				return 0f;
			PersistentManifold manifold = null;
			CollisionObject colObj0 = null, colObj1 = null;

			{
				{
					int i;

					Vector3f rel_pos1 = VECTORS.get();
					Vector3f rel_pos2 = VECTORS.get();
					Vector3f pos1 = VECTORS.get();
					Vector3f pos2 = VECTORS.get();
					Vector3f vel = VECTORS.get();
					Vector3f torqueAxis0 = VECTORS.get();
					Vector3f torqueAxis1 = VECTORS.get();
					Vector3f vel1 = VECTORS.get();
					Vector3f vel2 = VECTORS.get();
					Vector3f vec = VECTORS.get();
					Vector3f tmp = VECTORS.get();

					Matrix3f tmpMat = MATRICES.get();

					for (i = 0; i < numManifolds; i++) {
						manifold = manifoldPtr.get(manifold_offset + i);
						colObj0 = (CollisionObject) manifold.getBody0();
						colObj1 = (CollisionObject) manifold.getBody1();

						int solverBodyIdA = -1;
						int solverBodyIdB = -1;

						if (manifold.getNumContacts() != 0) {
							if (colObj0.getIslandTag() >= 0) {
								if (colObj0.getCompanionId() >= 0) {
									// body has already been converted
									solverBodyIdA = colObj0.getCompanionId();
								} else {
									solverBodyIdA = tmpSolverBodyPool.size();
									SolverBody solverBody = BODIES.get();
									tmpSolverBodyPool.add(solverBody);
									initSolverBody(solverBody, colObj0);
									colObj0.setCompanionId(solverBodyIdA);
								}
							} else {
								// create a static body
								solverBodyIdA = tmpSolverBodyPool.size();
								SolverBody solverBody = BODIES.get();
								tmpSolverBodyPool.add(solverBody);
								initSolverBody(solverBody, colObj0);
							}

							if (colObj1.getIslandTag() >= 0) {
								if (colObj1.getCompanionId() >= 0) {
									solverBodyIdB = colObj1.getCompanionId();
								} else {
									solverBodyIdB = tmpSolverBodyPool.size();
									SolverBody solverBody = BODIES.get();
									tmpSolverBodyPool.add(solverBody);
									initSolverBody(solverBody, colObj1);
									colObj1.setCompanionId(solverBodyIdB);
								}
							} else {
								// create a static body
								solverBodyIdB = tmpSolverBodyPool.size();
								SolverBody solverBody = BODIES.get();
								tmpSolverBodyPool.add(solverBody);
								initSolverBody(solverBody, colObj1);
							}
						}

						float relaxation;

						for (int j = 0; j < manifold.getNumContacts(); j++) {

							ManifoldPoint cp = manifold.getContactPoint(j);

							if (cp.getDistance() <= 0f) {
								cp.getPositionWorldOnA(pos1);
								cp.getPositionWorldOnB(pos2);

								rel_pos1.sub(pos1, colObj0.getWorldTransform(tempTrans).origin);
								rel_pos2.sub(pos2, colObj1.getWorldTransform(tempTrans).origin);

								relaxation = 1f;
								float rel_vel;

								int frictionIndex = tmpSolverConstraintPool.size();

								{
									SolverConstraint solverConstraint = CONSTRAINTS.get();
									tmpSolverConstraintPool.add(solverConstraint);
									RigidBody rb0 = RigidBody.upcast(colObj0);
									RigidBody rb1 = RigidBody.upcast(colObj1);

									solverConstraint.solverBodyIdA = solverBodyIdA;
									solverConstraint.solverBodyIdB = solverBodyIdB;
									solverConstraint.constraintType = SolverConstraintType.SOLVER_CONTACT_1D;

									solverConstraint.originalContactPoint = cp;

									torqueAxis0.cross(rel_pos1, cp.normalWorldOnB);

									if (rb0 != null) {
										solverConstraint.angularComponentA.set(torqueAxis0);
										rb0.getInvInertiaTensorWorld(tmpMat)
												.transform(solverConstraint.angularComponentA);
									} else {
										solverConstraint.angularComponentA.set(0f, 0f, 0f);
									}

									torqueAxis1.cross(rel_pos2, cp.normalWorldOnB);

									if (rb1 != null) {
										solverConstraint.angularComponentB.set(torqueAxis1);
										rb1.getInvInertiaTensorWorld(tmpMat)
												.transform(solverConstraint.angularComponentB);
									} else {
										solverConstraint.angularComponentB.set(0f, 0f, 0f);
									}

									{
										// #ifdef COMPUTE_IMPULSE_DENOM
										// btScalar denom0 = rb0->computeImpulseDenominator(pos1,cp.m_normalWorldOnB);
										// btScalar denom1 = rb1->computeImpulseDenominator(pos2,cp.m_normalWorldOnB);
										// #else
										float denom0 = 0f;
										float denom1 = 0f;
										if (rb0 != null) {
											vec.cross(solverConstraint.angularComponentA, rel_pos1);
											denom0 = rb0.getInvMass() + cp.normalWorldOnB.dot(vec);
										}
										if (rb1 != null) {
											vec.cross(solverConstraint.angularComponentB, rel_pos2);
											denom1 = rb1.getInvMass() + cp.normalWorldOnB.dot(vec);
										}
										// #endif //COMPUTE_IMPULSE_DENOM

										float denom = relaxation / (denom0 + denom1);
										solverConstraint.jacDiagABInv = denom;
									}

									solverConstraint.contactNormal.set(cp.normalWorldOnB);
									solverConstraint.relpos1CrossNormal.cross(rel_pos1, cp.normalWorldOnB);
									solverConstraint.relpos2CrossNormal.cross(rel_pos2, cp.normalWorldOnB);

									if (rb0 != null) {
										rb0.getVelocityInLocalPoint(rel_pos1, vel1);
									} else {
										vel1.set(0f, 0f, 0f);
									}

									if (rb1 != null) {
										rb1.getVelocityInLocalPoint(rel_pos2, vel2);
									} else {
										vel2.set(0f, 0f, 0f);
									}

									vel.sub(vel1, vel2);

									rel_vel = cp.normalWorldOnB.dot(vel);

									solverConstraint.penetration =
											Math.min(cp.getDistance() + infoGlobal.linearSlop, 0f);
									// solverConstraint.m_penetration = cp.getDistance();

									solverConstraint.friction = cp.combinedFriction;
									solverConstraint.restitution = restitutionCurve(rel_vel, cp.combinedRestitution);
									if (solverConstraint.restitution <= 0f) { solverConstraint.restitution = 0f; }

									float penVel = -solverConstraint.penetration / infoGlobal.timeStep;

									if (solverConstraint.restitution > penVel) { solverConstraint.penetration = 0f; }

									// warm starting (or zero if disabled)
									if ((infoGlobal.solverMode & SolverMode.SOLVER_USE_WARMSTARTING) != 0) {
										solverConstraint.appliedImpulse =
												cp.appliedImpulse * infoGlobal.warmstartingFactor;
										if (rb0 != null) {
											tmp.scale(rb0.getInvMass(), solverConstraint.contactNormal);
											tmpSolverBodyPool.get(solverConstraint.solverBodyIdA).internalApplyImpulse(
													tmp, solverConstraint.angularComponentA,
													solverConstraint.appliedImpulse);
										}
										if (rb1 != null) {
											tmp.scale(rb1.getInvMass(), solverConstraint.contactNormal);
											tmpSolverBodyPool.get(solverConstraint.solverBodyIdB).internalApplyImpulse(
													tmp, solverConstraint.angularComponentB,
													-solverConstraint.appliedImpulse);
										}
									} else {
										solverConstraint.appliedImpulse = 0f;
									}

									solverConstraint.appliedPushImpulse = 0f;

									solverConstraint.frictionIndex = tmpSolverFrictionConstraintPool.size();
									if (!cp.lateralFrictionInitialized) {
										cp.lateralFrictionDir1.scale(rel_vel, cp.normalWorldOnB);
										cp.lateralFrictionDir1.sub(vel, cp.lateralFrictionDir1);

										float lat_rel_vel = cp.lateralFrictionDir1.lengthSquared();
										if (lat_rel_vel > BulletGlobals.FLT_EPSILON)// 0.0f)
										{
											cp.lateralFrictionDir1.scale(1f / (float) Math.sqrt(lat_rel_vel));
											addFrictionConstraint(cp.lateralFrictionDir1, solverBodyIdA, solverBodyIdB,
													frictionIndex, cp, rel_pos1, rel_pos2, colObj0, colObj1,
													relaxation);
											cp.lateralFrictionDir2.cross(cp.lateralFrictionDir1, cp.normalWorldOnB);
											cp.lateralFrictionDir2.normalize(); // ??
											addFrictionConstraint(cp.lateralFrictionDir2, solverBodyIdA, solverBodyIdB,
													frictionIndex, cp, rel_pos1, rel_pos2, colObj0, colObj1,
													relaxation);
										} else {
											// re-calculate friction direction every frame, todo: check if this is
											// really needed

											TransformUtil.planeSpace1(cp.normalWorldOnB, cp.lateralFrictionDir1,
													cp.lateralFrictionDir2);
											addFrictionConstraint(cp.lateralFrictionDir1, solverBodyIdA, solverBodyIdB,
													frictionIndex, cp, rel_pos1, rel_pos2, colObj0, colObj1,
													relaxation);
											addFrictionConstraint(cp.lateralFrictionDir2, solverBodyIdA, solverBodyIdB,
													frictionIndex, cp, rel_pos1, rel_pos2, colObj0, colObj1,
													relaxation);
										}
										cp.lateralFrictionInitialized = true;

									} else {
										addFrictionConstraint(cp.lateralFrictionDir1, solverBodyIdA, solverBodyIdB,
												frictionIndex, cp, rel_pos1, rel_pos2, colObj0, colObj1, relaxation);
										addFrictionConstraint(cp.lateralFrictionDir2, solverBodyIdA, solverBodyIdB,
												frictionIndex, cp, rel_pos1, rel_pos2, colObj0, colObj1, relaxation);
									}

									{
										SolverConstraint frictionConstraint1 =
												tmpSolverFrictionConstraintPool.get(solverConstraint.frictionIndex);
										if ((infoGlobal.solverMode & SolverMode.SOLVER_USE_WARMSTARTING) != 0) {
											frictionConstraint1.appliedImpulse =
													cp.appliedImpulseLateral1 * infoGlobal.warmstartingFactor;
											if (rb0 != null) {
												tmp.scale(rb0.getInvMass(), frictionConstraint1.contactNormal);
												tmpSolverBodyPool.get(solverConstraint.solverBodyIdA)
														.internalApplyImpulse(tmp,
																frictionConstraint1.angularComponentA,
																frictionConstraint1.appliedImpulse);
											}
											if (rb1 != null) {
												tmp.scale(rb1.getInvMass(), frictionConstraint1.contactNormal);
												tmpSolverBodyPool.get(solverConstraint.solverBodyIdB)
														.internalApplyImpulse(tmp,
																frictionConstraint1.angularComponentB,
																-frictionConstraint1.appliedImpulse);
											}
										} else {
											frictionConstraint1.appliedImpulse = 0f;
										}
									}
									{
										SolverConstraint frictionConstraint2 =
												tmpSolverFrictionConstraintPool.get(solverConstraint.frictionIndex + 1);
										if ((infoGlobal.solverMode & SolverMode.SOLVER_USE_WARMSTARTING) != 0) {
											frictionConstraint2.appliedImpulse =
													cp.appliedImpulseLateral2 * infoGlobal.warmstartingFactor;
											if (rb0 != null) {
												tmp.scale(rb0.getInvMass(), frictionConstraint2.contactNormal);
												tmpSolverBodyPool.get(solverConstraint.solverBodyIdA)
														.internalApplyImpulse(tmp,
																frictionConstraint2.angularComponentA,
																frictionConstraint2.appliedImpulse);
											}
											if (rb1 != null) {
												tmp.scale(rb1.getInvMass(), frictionConstraint2.contactNormal);
												tmpSolverBodyPool.get(solverConstraint.solverBodyIdB)
														.internalApplyImpulse(tmp,
																frictionConstraint2.angularComponentB,
																-frictionConstraint2.appliedImpulse);
											}
										} else {
											frictionConstraint2.appliedImpulse = 0f;
										}
									}
								}
							}
						}
					}
					VECTORS.release(rel_pos1, rel_pos2, pos1, pos2, vel, torqueAxis0, torqueAxis1, vel1, vel2, vec,
							tmp);
					MATRICES.release(tmpMat);
				}
			}

			// TODO: btContactSolverInfo info = infoGlobal;

			{
				int j;
				for (j = 0; j < numConstraints; j++) {
					TypedConstraint constraint = constraints.get(constraints_offset + j);
					constraint.buildJacobian();
				}
			}

			int numConstraintPool = tmpSolverConstraintPool.size();
			int numFrictionPool = tmpSolverFrictionConstraintPool.size();

			// todo: use stack allocator for such temporarily memory, same for solver bodies/constraints
			MiscUtil.resize(orderTmpConstraintPool, numConstraintPool, 0);
			MiscUtil.resize(orderFrictionConstraintPool, numFrictionPool, 0);
			{
				int i;
				for (i = 0; i < numConstraintPool; i++) {
					orderTmpConstraintPool.set(i, i);
				}
				for (i = 0; i < numFrictionPool; i++) {
					orderFrictionConstraintPool.set(i, i);
				}
			}

			return 0f;
		} finally {
			// BulletStats.popProfile();
			// TRANSFORMS.release(tmpTrans);
		}
	}

	/**
	 * Solve group cache friendly iterations.
	 *
	 * @param bodies the bodies
	 * @param numBodies the num bodies
	 * @param manifoldPtr the manifold ptr
	 * @param manifold_offset the manifold offset
	 * @param numManifolds the num manifolds
	 * @param constraints the constraints
	 * @param constraints_offset the constraints offset
	 * @param numConstraints the num constraints
	 * @param infoGlobal the info global
	 * @return the float
	 */
	public float solveGroupCacheFriendlyIterations(final ArrayList<CollisionObject> bodies, final int numBodies,
			final ArrayList<PersistentManifold> manifoldPtr, final int manifold_offset, final int numManifolds,
			final ArrayList<TypedConstraint> constraints, final int constraints_offset, final int numConstraints,
			final ContactSolverInfo infoGlobal) {
		int numConstraintPool = tmpSolverConstraintPool.size();
		int numFrictionPool = tmpSolverFrictionConstraintPool.size();

		// should traverse the contacts random order...
		int iteration;
		{
			for (iteration = 0; iteration < infoGlobal.numIterations; iteration++) {

				int j;
				if ((infoGlobal.solverMode & SolverMode.SOLVER_RANDMIZE_ORDER) != 0) {
					if ((iteration & 7) == 0) {
						for (j = 0; j < numConstraintPool; ++j) {
							int tmp = orderTmpConstraintPool.get(j);
							int swapi = randInt2(j + 1);
							orderTmpConstraintPool.set(j, orderTmpConstraintPool.get(swapi));
							orderTmpConstraintPool.set(swapi, tmp);
						}

						for (j = 0; j < numFrictionPool; ++j) {
							int tmp = orderFrictionConstraintPool.get(j);
							int swapi = randInt2(j + 1);
							orderFrictionConstraintPool.set(j, orderFrictionConstraintPool.get(swapi));
							orderFrictionConstraintPool.set(swapi, tmp);
						}
					}
				}

				for (j = 0; j < numConstraints; j++) {
					TypedConstraint constraint = constraints.get(constraints_offset + j);
					// todo: use solver bodies, so we don't need to copy from/to btRigidBody

					if (constraint.getRigidBodyA().getIslandTag() >= 0
							&& constraint.getRigidBodyA().getCompanionId() >= 0) {
						tmpSolverBodyPool.get(constraint.getRigidBodyA().getCompanionId()).writebackVelocity();
					}
					if (constraint.getRigidBodyB().getIslandTag() >= 0
							&& constraint.getRigidBodyB().getCompanionId() >= 0) {
						tmpSolverBodyPool.get(constraint.getRigidBodyB().getCompanionId()).writebackVelocity();
					}

					constraint.solveConstraint(infoGlobal.timeStep);

					if (constraint.getRigidBodyA().getIslandTag() >= 0
							&& constraint.getRigidBodyA().getCompanionId() >= 0) {
						tmpSolverBodyPool.get(constraint.getRigidBodyA().getCompanionId()).readVelocity();
					}
					if (constraint.getRigidBodyB().getIslandTag() >= 0
							&& constraint.getRigidBodyB().getCompanionId() >= 0) {
						tmpSolverBodyPool.get(constraint.getRigidBodyB().getCompanionId()).readVelocity();
					}
				}

				{
					int numPoolConstraints = tmpSolverConstraintPool.size();
					for (j = 0; j < numPoolConstraints; j++) {
						SolverConstraint solveManifold = tmpSolverConstraintPool.get(orderTmpConstraintPool.get(j));
						resolveSingleCollisionCombinedCacheFriendly(tmpSolverBodyPool.get(solveManifold.solverBodyIdA),
								tmpSolverBodyPool.get(solveManifold.solverBodyIdB), solveManifold, infoGlobal);
					}
				}

				{
					int numFrictionPoolConstraints = tmpSolverFrictionConstraintPool.size();

					for (j = 0; j < numFrictionPoolConstraints; j++) {
						SolverConstraint solveManifold =
								tmpSolverFrictionConstraintPool.get(orderFrictionConstraintPool.get(j));

						float totalImpulse = tmpSolverConstraintPool.get(solveManifold.frictionIndex).appliedImpulse
								+ tmpSolverConstraintPool.get(solveManifold.frictionIndex).appliedPushImpulse;

						resolveSingleFrictionCacheFriendly(tmpSolverBodyPool.get(solveManifold.solverBodyIdA),
								tmpSolverBodyPool.get(solveManifold.solverBodyIdB), solveManifold, infoGlobal,
								totalImpulse);
					}
				}
			}

			if (infoGlobal.splitImpulse) {
				for (iteration = 0; iteration < infoGlobal.numIterations; iteration++) {
					{
						int numPoolConstraints = tmpSolverConstraintPool.size();
						int j;
						for (j = 0; j < numPoolConstraints; j++) {
							SolverConstraint solveManifold = tmpSolverConstraintPool.get(orderTmpConstraintPool.get(j));

							resolveSplitPenetrationImpulseCacheFriendly(
									tmpSolverBodyPool.get(solveManifold.solverBodyIdA),
									tmpSolverBodyPool.get(solveManifold.solverBodyIdB), solveManifold, infoGlobal);
						}
					}
				}
			}
		}

		return 0f;
		// }
		// finally {
		// BulletStats.popProfile();
		// }
	}

	/**
	 * Solve group cache friendly.
	 *
	 * @param bodies the bodies
	 * @param numBodies the num bodies
	 * @param manifoldPtr the manifold ptr
	 * @param manifold_offset the manifold offset
	 * @param numManifolds the num manifolds
	 * @param constraints the constraints
	 * @param constraints_offset the constraints offset
	 * @param numConstraints the num constraints
	 * @param infoGlobal the info global
	 * @return the float
	 */
	public float solveGroupCacheFriendly(final ArrayList<CollisionObject> bodies, final int numBodies,
			final ArrayList<PersistentManifold> manifoldPtr, final int manifold_offset, final int numManifolds,
			final ArrayList<TypedConstraint> constraints, final int constraints_offset, final int numConstraints,
			final ContactSolverInfo infoGlobal) {
		solveGroupCacheFriendlySetup(bodies, numBodies, manifoldPtr, manifold_offset, numManifolds, constraints,
				constraints_offset, numConstraints, infoGlobal);
		solveGroupCacheFriendlyIterations(bodies, numBodies, manifoldPtr, manifold_offset, numManifolds, constraints,
				constraints_offset, numConstraints, infoGlobal);

		int numPoolConstraints = tmpSolverConstraintPool.size();
		for (int j = 0; j < numPoolConstraints; j++) {

			SolverConstraint solveManifold = tmpSolverConstraintPool.get(j);
			ManifoldPoint pt = (ManifoldPoint) solveManifold.originalContactPoint;
			assert pt != null;
			pt.appliedImpulse = solveManifold.appliedImpulse;
			pt.appliedImpulseLateral1 = tmpSolverFrictionConstraintPool.get(solveManifold.frictionIndex).appliedImpulse;
			pt.appliedImpulseLateral1 =
					tmpSolverFrictionConstraintPool.get(solveManifold.frictionIndex + 1).appliedImpulse;

			// do a callback here?
		}

		if (infoGlobal.splitImpulse) {
			for (SolverBody element : tmpSolverBodyPool) {
				element.writebackVelocity(infoGlobal.timeStep);
				BODIES.release(element);
			}
		} else {
			for (SolverBody element : tmpSolverBodyPool) {
				element.writebackVelocity();
				BODIES.release(element);
			}
		}

		tmpSolverBodyPool.clear();

		for (SolverConstraint element : tmpSolverConstraintPool) {
			CONSTRAINTS.release(element);
		}
		tmpSolverConstraintPool.clear();

		for (SolverConstraint element : tmpSolverFrictionConstraintPool) {
			CONSTRAINTS.release(element);
		}
		tmpSolverFrictionConstraintPool.clear();

		return 0f;
	}

	/**
	 * Sequentially applies impulses.
	 */
	@Override
	public float solveGroup(final ArrayList<CollisionObject> bodies, final int numBodies,
			final ArrayList<PersistentManifold> manifoldPtr, final int manifold_offset, final int numManifolds,
			final ArrayList<TypedConstraint> constraints, final int constraints_offset, final int numConstraints,
			final ContactSolverInfo infoGlobal, final Dispatcher dispatcher) {
		// BulletStats.pushProfile("solveGroup");
		// try {
		// TODO: solver cache friendly
		if ((infoGlobal.solverMode & SolverMode.SOLVER_CACHE_FRIENDLY) != 0) {
			// you need to provide at least some bodies
			// SimpleDynamicsWorld needs to switch off SOLVER_CACHE_FRIENDLY
			assert bodies != null;
			assert numBodies != 0;
			float value = solveGroupCacheFriendly(bodies, numBodies, manifoldPtr, manifold_offset, numManifolds,
					constraints, constraints_offset, numConstraints, infoGlobal);
			return value;
		}

		ContactSolverInfo info = new ContactSolverInfo(infoGlobal);

		int numiter = infoGlobal.numIterations;

		int totalPoints = 0;
		{
			short j;
			for (j = 0; j < numManifolds; j++) {
				PersistentManifold manifold = manifoldPtr.get(manifold_offset + j);
				prepareConstraints(manifold, info);

				for (short p = 0; p < manifoldPtr.get(manifold_offset + j).getNumContacts(); p++) {
					gOrder[totalPoints].manifoldIndex = j;
					gOrder[totalPoints].pointIndex = p;
					totalPoints++;
				}
			}
		}

		{
			int j;
			for (j = 0; j < numConstraints; j++) {
				TypedConstraint constraint = constraints.get(constraints_offset + j);
				constraint.buildJacobian();
			}
		}

		// should traverse the contacts random order...
		int iteration;
		{
			for (iteration = 0; iteration < numiter; iteration++) {
				int j;
				if ((infoGlobal.solverMode & SolverMode.SOLVER_RANDMIZE_ORDER) != 0) {
					if ((iteration & 7) == 0) {
						for (j = 0; j < totalPoints; ++j) {
							OrderIndex tmp = gOrder[j];
							int swapi = randInt2(j + 1);
							gOrder[j] = gOrder[swapi];
							gOrder[swapi] = tmp;
						}
					}
				}

				for (j = 0; j < numConstraints; j++) {
					TypedConstraint constraint = constraints.get(constraints_offset + j);
					constraint.solveConstraint(info.timeStep);
				}

				for (j = 0; j < totalPoints; j++) {
					PersistentManifold manifold = manifoldPtr.get(manifold_offset + gOrder[j].manifoldIndex);
					solve((RigidBody) manifold.getBody0(), (RigidBody) manifold.getBody1(),
							manifold.getContactPoint(gOrder[j].pointIndex), info, iteration);
				}

				for (j = 0; j < totalPoints; j++) {
					PersistentManifold manifold = manifoldPtr.get(manifold_offset + gOrder[j].manifoldIndex);
					solveFriction((RigidBody) manifold.getBody0(), (RigidBody) manifold.getBody1(),
							manifold.getContactPoint(gOrder[j].pointIndex), info, iteration);
				}

			}
		}

		return 0f;
		// } finally {
		// BulletStats.popProfile();
		// }
	}

	/**
	 * Prepare constraints.
	 *
	 * @param manifoldPtr the manifold ptr
	 * @param info the info
	 */
	protected void prepareConstraints(final PersistentManifold manifoldPtr, final ContactSolverInfo info) {
		RigidBody body0 = (RigidBody) manifoldPtr.getBody0();
		RigidBody body1 = (RigidBody) manifoldPtr.getBody1();

		// only necessary to refresh the manifold once (first iteration). The integration is done outside the loop

		int numpoints = manifoldPtr.getNumContacts();

		// BulletStats.gTotalContactPoints += numpoints;

		// Vector3f tmpVec = VECTORS.get();
		// Matrix3f tmpMat3 = MATRICES.get();

		Vector3f pos1 = VECTORS.get();
		Vector3f pos2 = VECTORS.get();
		Vector3f rel_pos1 = VECTORS.get();
		Vector3f rel_pos2 = VECTORS.get();
		Vector3f vel1 = VECTORS.get();
		Vector3f vel2 = VECTORS.get();
		Vector3f vel = VECTORS.get();
		Vector3f totalImpulse = VECTORS.get();
		Vector3f torqueAxis0 = VECTORS.get();
		Vector3f torqueAxis1 = VECTORS.get();
		Vector3f ftorqueAxis0 = VECTORS.get();
		Vector3f ftorqueAxis1 = VECTORS.get();

		for (int i = 0; i < numpoints; i++) {
			ManifoldPoint cp = manifoldPtr.getContactPoint(i);
			if (cp.getDistance() <= 0f) {
				cp.getPositionWorldOnA(pos1);
				cp.getPositionWorldOnB(pos2);

				rel_pos1.sub(pos1, body0.getCenterOfMassPosition(tempVec));
				rel_pos2.sub(pos2, body1.getCenterOfMassPosition(tempVec));

				// Transform tmp = TRANSFORMS.get();
				// this jacobian entry is re-used for all iterations
				Matrix3f mat1 = body0.getCenterOfMassTransform(tempTrans).basis;
				mat1.transpose();

				Matrix3f mat2 = body1.getCenterOfMassTransform(tempTrans).basis;
				mat2.transpose();

				// TRANSFORMS.release(tmp);

				JacobianEntry jac = JACOBIANS.get();
				jac.init(mat1, mat2, rel_pos1, rel_pos2, cp.normalWorldOnB, body0.getInvInertiaDiagLocal(VECTORS.get()),
						body0.getInvMass(), body1.getInvInertiaDiagLocal(VECTORS.get()), body1.getInvMass());
				float jacDiagAB = jac.getDiagonal();
				JACOBIANS.release(jac);

				ConstraintPersistentData cpd = (ConstraintPersistentData) cp.userPersistentData;
				if (cpd != null) {
					// might be invalid
					cpd.persistentLifeTime++;
					if (cpd.persistentLifeTime != cp.getLifeTime()) {
						cpd.reset();
						cpd.persistentLifeTime = cp.getLifeTime();

					}
				} else {
					cpd = new ConstraintPersistentData();

					totalCpd++;
					cp.userPersistentData = cpd;
					cpd.persistentLifeTime = cp.getLifeTime();
				}
				assert cpd != null;

				cpd.jacDiagABInv = 1f / jacDiagAB;

				// Dependent on Rigidbody A and B types, fetch the contact/friction response func
				// perhaps do a similar thing for friction/restutution combiner funcs...

				cpd.frictionSolverFunc = frictionDispatch[body0.frictionSolverType][body1.frictionSolverType];
				cpd.contactSolverFunc = contactDispatch[body0.contactSolverType][body1.contactSolverType];

				body0.getVelocityInLocalPoint(rel_pos1, vel1);
				body1.getVelocityInLocalPoint(rel_pos2, vel2);
				vel.sub(vel1, vel2);

				float rel_vel;
				rel_vel = cp.normalWorldOnB.dot(vel);

				float combinedRestitution = cp.combinedRestitution;

				cpd.penetration = cp.getDistance(); /// btScalar(info.m_numIterations);
				cpd.friction = cp.combinedFriction;
				cpd.restitution = restitutionCurve(rel_vel, combinedRestitution);
				if (cpd.restitution <= 0f) { cpd.restitution = 0f; }

				// restitution and penetration work in same direction so
				// rel_vel

				float penVel = -cpd.penetration / info.timeStep;

				if (cpd.restitution > penVel) { cpd.penetration = 0f; }

				float relaxation = info.damping;
				if ((info.solverMode & SolverMode.SOLVER_USE_WARMSTARTING) != 0) {
					cpd.appliedImpulse *= relaxation;
				} else {
					cpd.appliedImpulse = 0f;
				}

				// for friction
				cpd.prevAppliedImpulse = cpd.appliedImpulse;

				// re-calculate friction direction every frame, todo: check if this is really needed
				TransformUtil.planeSpace1(cp.normalWorldOnB, cpd.frictionWorldTangential0,
						cpd.frictionWorldTangential1);

				cpd.accumulatedTangentImpulse0 = 0f;
				cpd.accumulatedTangentImpulse1 = 0f;
				float denom0 = body0.computeImpulseDenominator(pos1, cpd.frictionWorldTangential0);
				float denom1 = body1.computeImpulseDenominator(pos2, cpd.frictionWorldTangential0);
				float denom = relaxation / (denom0 + denom1);
				cpd.jacDiagABInvTangent0 = denom;

				denom0 = body0.computeImpulseDenominator(pos1, cpd.frictionWorldTangential1);
				denom1 = body1.computeImpulseDenominator(pos2, cpd.frictionWorldTangential1);
				denom = relaxation / (denom0 + denom1);
				cpd.jacDiagABInvTangent1 = denom;

				totalImpulse.scale(cpd.appliedImpulse, cp.normalWorldOnB);

				torqueAxis0.cross(rel_pos1, cp.normalWorldOnB);

				cpd.angularComponentA.set(torqueAxis0);
				body0.getInvInertiaTensorWorld(tempMat).transform(cpd.angularComponentA);

				torqueAxis1.cross(rel_pos2, cp.normalWorldOnB);

				cpd.angularComponentB.set(torqueAxis1);
				body1.getInvInertiaTensorWorld(tempMat).transform(cpd.angularComponentB);
				ftorqueAxis0.cross(rel_pos1, cpd.frictionWorldTangential0);

				cpd.frictionAngularComponent0A.set(ftorqueAxis0);
				body0.getInvInertiaTensorWorld(tempMat).transform(cpd.frictionAngularComponent0A);
				ftorqueAxis1.cross(rel_pos1, cpd.frictionWorldTangential1);

				cpd.frictionAngularComponent1A.set(ftorqueAxis1);
				body0.getInvInertiaTensorWorld(tempMat).transform(cpd.frictionAngularComponent1A);
				ftorqueAxis0.cross(rel_pos2, cpd.frictionWorldTangential0);

				cpd.frictionAngularComponent0B.set(ftorqueAxis0);
				body1.getInvInertiaTensorWorld(tempMat).transform(cpd.frictionAngularComponent0B);
				ftorqueAxis1.cross(rel_pos2, cpd.frictionWorldTangential1);

				cpd.frictionAngularComponent1B.set(ftorqueAxis1);
				body1.getInvInertiaTensorWorld(tempMat).transform(cpd.frictionAngularComponent1B);

				// apply previous frames impulse on both bodies
				body0.applyImpulse(totalImpulse, rel_pos1);

				tempVec.negate(totalImpulse);
				body1.applyImpulse(tempVec, rel_pos2);
			}

		}
		VECTORS.release(/* tmpVec, */ pos1, pos2, rel_pos1, rel_pos2, vel1, vel2, vel, totalImpulse, torqueAxis0,
				torqueAxis1, ftorqueAxis0, ftorqueAxis1);
		// MATRICES.release(tmpMat3);

	}

	/**
	 * Solve combined contact friction.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param cp the cp
	 * @param info the info
	 * @param iter the iter
	 * @return the float
	 */
	public float solveCombinedContactFriction(final RigidBody body0, final RigidBody body1, final ManifoldPoint cp,
			final ContactSolverInfo info, final int iter) {
		float maxImpulse = 0f;

		{
			if (cp.getDistance() <= 0f) {
				{
					// btConstraintPersistentData* cpd = (btConstraintPersistentData*) cp.m_userPersistentData;
					float impulse = ContactConstraint.resolveSingleCollisionCombined(body0, body1, cp, info);

					if (maxImpulse < impulse) { maxImpulse = impulse; }
				}
			}
		}
		return maxImpulse;
	}

	/**
	 * Solve.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param cp the cp
	 * @param info the info
	 * @param iter the iter
	 * @return the float
	 */
	protected float solve(final RigidBody body0, final RigidBody body1, final ManifoldPoint cp,
			final ContactSolverInfo info, final int iter) {
		float maxImpulse = 0f;

		{
			if (cp.getDistance() <= 0f) {
				{
					ConstraintPersistentData cpd = (ConstraintPersistentData) cp.userPersistentData;
					float impulse = cpd.contactSolverFunc.resolveContact(body0, body1, cp, info);

					if (maxImpulse < impulse) { maxImpulse = impulse; }
				}
			}
		}

		return maxImpulse;
	}

	/**
	 * Solve friction.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param cp the cp
	 * @param info the info
	 * @param iter the iter
	 * @return the float
	 */
	protected float solveFriction(final RigidBody body0, final RigidBody body1, final ManifoldPoint cp,
			final ContactSolverInfo info, final int iter) {
		{
			if (cp.getDistance() <= 0f) {
				ConstraintPersistentData cpd = (ConstraintPersistentData) cp.userPersistentData;
				cpd.frictionSolverFunc.resolveContact(body0, body1, cp, info);
			}
		}
		return 0f;
	}

	@Override
	public void reset() {
		btSeed2 = 0;
	}

	/**
	 * Advanced: Override the default contact solving function for contacts, for certain types of rigidbody<br>
	 * See RigidBody.contactSolverType and RigidBody.frictionSolverType
	 */
	public void setContactSolverFunc(final ContactSolverFunc func, final int type0, final int type1) {
		contactDispatch[type0][type1] = func;
	}

	/**
	 * Advanced: Override the default friction solving function for contacts, for certain types of rigidbody<br>
	 * See RigidBody.contactSolverType and RigidBody.frictionSolverType
	 */
	public void setFrictionSolverFunc(final ContactSolverFunc func, final int type0, final int type1) {
		frictionDispatch[type0][type1] = func;
	}

	/**
	 * Sets the rand seed.
	 *
	 * @param seed the new rand seed
	 */
	public void setRandSeed(final long seed) {
		btSeed2 = seed;
	}

	/**
	 * Gets the rand seed.
	 *
	 * @return the rand seed
	 */
	public long getRandSeed() {
		return btSeed2;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class OrderIndex.
	 */
	private static class OrderIndex {
		
		/** The manifold index. */
		public int manifoldIndex;
		
		/** The point index. */
		public int pointIndex;
	}

}
