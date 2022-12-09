/*******************************************************************************************************
 *
 * DiscreteDynamicsWorld.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics;

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;
import java.util.Comparator;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.broadphase.CollisionFilterGroups;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.broadphase.OverlappingPairCache;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestConvexResultCallback;
import com.bulletphysics.collision.dispatch.CollisionWorld.LocalConvexResult;
import com.bulletphysics.collision.dispatch.SimulationIslandManager;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.ContactSolverInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.dynamics.vehicle.RaycastVehicle;
import com.bulletphysics.linearmath.ScalarUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.TransformUtil;

/**
 * DiscreteDynamicsWorld provides discrete rigid body simulation.
 *
 * @author jezek2
 */
public class DiscreteDynamicsWorld extends DynamicsWorld {

	/** The constraint solver. */
	protected ConstraintSolver constraintSolver;
	
	/** The island manager. */
	protected SimulationIslandManager islandManager;
	
	/** The constraints. */
	protected final ArrayList<TypedConstraint> constraints = new ArrayList<>();
	
	/** The gravity. */
	protected final Vector3f gravity = new Vector3f(0f, -10f, 0f);

	/** The local time. */
	// for variable timesteps
	protected float localTime = 1f / 60f;
	// for variable timesteps

	/** The owns island manager. */
	protected boolean ownsIslandManager;
	
	/** The owns constraint solver. */
	protected boolean ownsConstraintSolver;

	/** The vehicles. */
	protected ArrayList<RaycastVehicle> vehicles = new ArrayList<>();

	/** The actions. */
	protected ArrayList<ActionInterface> actions = new ArrayList<>();

	/** The profile timings. */
	protected int profileTimings = 0;

	/** The pre tick callback. */
	protected InternalTickCallback preTickCallback;

	/**
	 * Instantiates a new discrete dynamics world.
	 *
	 * @param dispatcher the dispatcher
	 * @param pairCache the pair cache
	 * @param constraintSolver the constraint solver
	 * @param collisionConfiguration the collision configuration
	 */
	public DiscreteDynamicsWorld(final Dispatcher dispatcher, final BroadphaseInterface pairCache,
			final ConstraintSolver constraintSolver, final CollisionConfiguration collisionConfiguration) {
		super(dispatcher, pairCache, collisionConfiguration);
		this.constraintSolver = constraintSolver;

		if (this.constraintSolver == null) {
			this.constraintSolver = new SequentialImpulseConstraintSolver();
			ownsConstraintSolver = true;
		} else {
			ownsConstraintSolver = false;
		}

		{
			islandManager = new SimulationIslandManager();
		}

		ownsIslandManager = true;
	}

	/**
	 * Save kinematic state.
	 *
	 * @param timeStep the time step
	 */
	protected void saveKinematicState(final float timeStep) {
		for (CollisionObject colObj : collisionObjects) {
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null) {
				// Transform predictedTrans = new Transform();
				if (body.getActivationState() != CollisionObject.ISLAND_SLEEPING) {
					if (body.isKinematicObject()) {
						// to calculate velocities next frame
						body.saveKinematicState(timeStep);
					}
				}
			}
		}
	}

	@Override
	public void clearForces() {
		// todo: iterate over awake simulation islands!
		for (CollisionObject colObj : collisionObjects) {
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null) { body.clearForces(); }
		}
	}

	/**
	 * Apply gravity, call this once per timestep.
	 */
	public void applyGravity() {
		// todo: iterate over awake simulation islands!
		for (CollisionObject colObj : collisionObjects) {
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null && body.isActive()) { body.applyGravity(); }
		}
	}

	/**
	 * Synchronize motion states.
	 */
	protected void synchronizeMotionStates() {
		Transform interpolatedTransform = TRANSFORMS.get();

		Transform tmpTrans = TRANSFORMS.get();
		Vector3f tmpLinVel = VECTORS.get();
		Vector3f tmpAngVel = VECTORS.get();

		// todo: iterate over awake simulation islands!
		for (CollisionObject colObj : collisionObjects) {
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null && body.getMotionState() != null && !body.isStaticOrKinematicObject()) {
				{
					TransformUtil.integrateTransform(body.getInterpolationWorldTransform(tmpTrans),
							body.getInterpolationLinearVelocity(tmpLinVel),
							body.getInterpolationAngularVelocity(tmpAngVel), localTime * body.getHitFraction(),
							interpolatedTransform);
					body.getMotionState().setWorldTransform(interpolatedTransform);
				}
			}
		}
		TRANSFORMS.release(interpolatedTransform, tmpTrans);
		VECTORS.release(tmpLinVel, tmpAngVel);
	}

	@Override
	public int stepSimulation(final float timeStep, int maxSubSteps, float fixedTimeStep) {
		int numSimulationSubSteps = 0;

		if (maxSubSteps != 0) {
			// fixed timestep with interpolation
			localTime += timeStep;
			if (localTime >= fixedTimeStep) {
				numSimulationSubSteps = (int) (localTime / fixedTimeStep);
				localTime -= numSimulationSubSteps * fixedTimeStep;
			}
		} else {
			// variable timestep
			fixedTimeStep = timeStep;
			localTime = timeStep;
			if (ScalarUtil.fuzzyZero(timeStep)) {
				numSimulationSubSteps = 0;
				maxSubSteps = 0;
			} else {
				numSimulationSubSteps = 1;
				maxSubSteps = 1;
			}
		}

		if (numSimulationSubSteps != 0) {
			saveKinematicState(fixedTimeStep);

			applyGravity();

			// clamp the number of substeps, to prevent simulation grinding spiralling down to a halt
			int clampedSimulationSteps = numSimulationSubSteps > maxSubSteps ? maxSubSteps : numSimulationSubSteps;

			for (int i = 0; i < clampedSimulationSteps; i++) {
				internalSingleStepSimulation(fixedTimeStep);
				synchronizeMotionStates();
			}
		}

		synchronizeMotionStates();

		clearForces();
		return numSimulationSubSteps;
	}

	/**
	 * Internal single step simulation.
	 *
	 * @param timeStep the time step
	 */
	protected void internalSingleStepSimulation(final float timeStep) {
		// BulletStats.pushProfile("internalSingleStepSimulation");
		try {
			if (preTickCallback != null) { preTickCallback.internalTick(this, timeStep); }

			// apply gravity, predict motion
			predictUnconstraintMotion(timeStep);

			DispatcherInfo dispatchInfo = getDispatchInfo();

			dispatchInfo.timeStep = timeStep;
			dispatchInfo.stepCount = 0;

			// perform collision detection
			performDiscreteCollisionDetection();

			calculateSimulationIslands();

			getSolverInfo().timeStep = timeStep;

			// solve contact and other joint constraints
			solveConstraints(getSolverInfo());

			// CallbackTriggers();

			// integrate transforms
			integrateTransforms(timeStep);

			// update vehicle simulation
			updateActions(timeStep);

			// update vehicle simulation
			updateVehicles(timeStep);

			updateActivationState(timeStep);

			if (internalTickCallback != null) { internalTickCallback.internalTick(this, timeStep); }
		} finally {
			// BulletStats.popProfile();
		}
	}

	@Override
	public void setGravity(final Vector3f gravity) {
		this.gravity.set(gravity);
		for (CollisionObject colObj : collisionObjects) {
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null) { body.setGravity(gravity); }
		}
	}

	@Override
	public Vector3f getGravity(final Vector3f out) {
		out.set(gravity);
		return out;
	}

	@Override
	public void removeRigidBody(final RigidBody body) {
		removeCollisionObject(body);
	}

	@Override
	public void addRigidBody(final RigidBody body) {
		if (!body.isStaticOrKinematicObject()) { body.setGravity(gravity); }

		if (body.getCollisionShape() != null) {
			boolean isDynamic = !(body.isStaticObject() || body.isKinematicObject());
			short collisionFilterGroup = isDynamic ? (short) CollisionFilterGroups.DEFAULT_FILTER
					: (short) CollisionFilterGroups.STATIC_FILTER;
			short collisionFilterMask = isDynamic ? (short) CollisionFilterGroups.ALL_FILTER
					: (short) (CollisionFilterGroups.ALL_FILTER ^ CollisionFilterGroups.STATIC_FILTER);

			addCollisionObject(body, collisionFilterGroup, collisionFilterMask);
		}
	}

	/**
	 * Adds the rigid body.
	 *
	 * @param body the body
	 * @param group the group
	 * @param mask the mask
	 */
	public void addRigidBody(final RigidBody body, final short group, final short mask) {
		if (!body.isStaticOrKinematicObject()) { body.setGravity(gravity); }

		if (body.getCollisionShape() != null) { addCollisionObject(body, group, mask); }
	}

	/**
	 * Update actions.
	 *
	 * @param timeStep the time step
	 */
	public void updateActions(final float timeStep) {
		for (ActionInterface action : actions) {
			action.updateAction(this, timeStep);
		}
	}

	/**
	 * Update vehicles.
	 *
	 * @param timeStep the time step
	 */
	protected void updateVehicles(final float timeStep) {
		for (RaycastVehicle vehicle : vehicles) {
			vehicle.updateVehicle(timeStep);
		}
	}

	/**
	 * Update activation state.
	 *
	 * @param timeStep the time step
	 */
	protected void updateActivationState(final float timeStep) {
		Vector3f tmp = VECTORS.get();
		try {

			for (CollisionObject colObj : collisionObjects) {
				RigidBody body = RigidBody.upcast(colObj);
				if (body != null) {
					body.updateDeactivation(timeStep);

					if (body.wantsSleeping()) {
						if (body.isStaticOrKinematicObject()) {
							body.setActivationState(CollisionObject.ISLAND_SLEEPING);
						} else {
							if (body.getActivationState() == CollisionObject.ACTIVE_TAG) {
								body.setActivationState(CollisionObject.WANTS_DEACTIVATION);
							}
							if (body.getActivationState() == CollisionObject.ISLAND_SLEEPING) {
								tmp.set(0f, 0f, 0f);
								body.setAngularVelocity(tmp);
								body.setLinearVelocity(tmp);
							}
						}
					} else {
						if (body.getActivationState() != CollisionObject.DISABLE_DEACTIVATION) {
							body.setActivationState(CollisionObject.ACTIVE_TAG);
						}
					}
				}
			}
		} finally {
			// BulletStats.popProfile();
			VECTORS.release(tmp);
		}
	}

	@Override
	public void addConstraint(final TypedConstraint constraint, final boolean disableCollisionsBetweenLinkedBodies) {
		constraints.add(constraint);
		if (disableCollisionsBetweenLinkedBodies) {
			constraint.getRigidBodyA().addConstraintRef(constraint);
			constraint.getRigidBodyB().addConstraintRef(constraint);
		}
	}

	@Override
	public void removeConstraint(final TypedConstraint constraint) {
		constraints.remove(constraint);
		constraint.getRigidBodyA().removeConstraintRef(constraint);
		constraint.getRigidBodyB().removeConstraintRef(constraint);
	}

	@Override
	public void addAction(final ActionInterface action) {
		actions.add(action);
	}

	@Override
	public void removeAction(final ActionInterface action) {
		actions.remove(action);
	}

	@Override
	public void addVehicle(final RaycastVehicle vehicle) {
		vehicles.add(vehicle);
	}

	@Override
	public void removeVehicle(final RaycastVehicle vehicle) {
		vehicles.remove(vehicle);
	}

	/**
	 * Gets the constraint island id.
	 *
	 * @param lhs the lhs
	 * @return the constraint island id
	 */
	private static int getConstraintIslandId(final TypedConstraint lhs) {
		int islandId;

		CollisionObject rcolObj0 = lhs.getRigidBodyA();
		CollisionObject rcolObj1 = lhs.getRigidBodyB();
		islandId = rcolObj0.getIslandTag() >= 0 ? rcolObj0.getIslandTag() : rcolObj1.getIslandTag();
		return islandId;
	}

	/**
	 * The Class InplaceSolverIslandCallback.
	 */
	private static class InplaceSolverIslandCallback extends SimulationIslandManager.IslandCallback {
		
		/** The solver info. */
		public ContactSolverInfo solverInfo;
		
		/** The solver. */
		public ConstraintSolver solver;
		
		/** The sorted constraints. */
		public ArrayList<TypedConstraint> sortedConstraints;
		
		/** The num constraints. */
		public int numConstraints;
		
		/** The dispatcher. */
		// public StackAlloc* m_stackAlloc;
		public Dispatcher dispatcher;

		/**
		 * Inits the.
		 *
		 * @param solverInfo the solver info
		 * @param solver the solver
		 * @param sortedConstraints the sorted constraints
		 * @param numConstraints the num constraints
		 * @param dispatcher the dispatcher
		 */
		public void init(final ContactSolverInfo solverInfo, final ConstraintSolver solver,
				final ArrayList<TypedConstraint> sortedConstraints, final int numConstraints,
				final Dispatcher dispatcher) {
			this.solverInfo = solverInfo;
			this.solver = solver;
			this.sortedConstraints = sortedConstraints;
			this.numConstraints = numConstraints;
			this.dispatcher = dispatcher;
		}

		@Override
		public void processIsland(final ArrayList<CollisionObject> bodies, final int numBodies,
				final ArrayList<PersistentManifold> manifolds, final int manifolds_offset, final int numManifolds,
				final int islandId) {
			if (islandId < 0) {
				// we don't split islands, so all constraints/contact manifolds/bodies are passed into the solver
				// regardless the island id
				solver.solveGroup(bodies, numBodies, manifolds, manifolds_offset, numManifolds, sortedConstraints, 0,
						numConstraints, solverInfo, /* ,m_stackAlloc */ dispatcher);
			} else {
				// also add all non-contact constraints/joints for this island
				// ArrayList<TypedConstraint> startConstraint = null;
				int startConstraint_idx = -1;
				int numCurConstraints = 0;
				int i;

				// find the first constraint for this island
				for (i = 0; i < numConstraints; i++) {
					if (getConstraintIslandId(sortedConstraints.get(i)) == islandId) {
						// startConstraint = &m_sortedConstraints[i];
						// startConstraint = sortedConstraints.subList(i, sortedConstraints.size());
						startConstraint_idx = i;
						break;
					}
				}
				// count the number of constraints in this island
				for (; i < numConstraints; i++) {
					if (getConstraintIslandId(sortedConstraints.get(i)) == islandId) { numCurConstraints++; }
				}

				// only call solveGroup if there is some work: avoid virtual function call, its overhead can be
				// excessive
				if (numManifolds + numCurConstraints > 0) {
					solver.solveGroup(bodies, numBodies, manifolds, manifolds_offset, numManifolds, sortedConstraints,
							startConstraint_idx, numCurConstraints, solverInfo, dispatcher);
				}
			}
		}
	}

	/** The sorted constraints. */
	private final ArrayList<TypedConstraint> sortedConstraints = new ArrayList<>();
	
	/** The solver callback. */
	private final InplaceSolverIslandCallback solverCallback = new InplaceSolverIslandCallback();

	/**
	 * Solve constraints.
	 *
	 * @param solverInfo the solver info
	 */
	protected void solveConstraints(final ContactSolverInfo solverInfo) {
		// sorted version of all btTypedConstraint, based on islandId
		sortedConstraints.clear();
		for (int i = 0; i < constraints.size(); i++) {
			sortedConstraints.add(constraints.get(i));
		}
		sortedConstraints.sort(sortConstraintOnIslandPredicate);

		ArrayList<TypedConstraint> constraintsPtr = getNumConstraints() != 0 ? sortedConstraints : null;

		solverCallback.init(solverInfo, constraintSolver, constraintsPtr, sortedConstraints.size(), dispatcher1);

		constraintSolver.prepareSolve(getCollisionWorld().getNumCollisionObjects(),
				getCollisionWorld().getDispatcher().getNumManifolds());

		// solve all the constraints for this island
		islandManager.buildAndProcessIslands(getCollisionWorld().getDispatcher(),
				getCollisionWorld().getCollisionObjectArray(), solverCallback);

		constraintSolver.allSolved(solverInfo);
	}

	/**
	 * Calculate simulation islands.
	 */
	protected void calculateSimulationIslands() {
		getSimulationIslandManager().updateActivationState(getCollisionWorld(), getCollisionWorld().getDispatcher());

		{
			int i;
			int numConstraints = constraints.size();
			for (i = 0; i < numConstraints; i++) {
				TypedConstraint constraint = constraints.get(i);

				RigidBody colObj0 = constraint.getRigidBodyA();
				RigidBody colObj1 = constraint.getRigidBodyB();

				if (colObj0 != null && !colObj0.isStaticOrKinematicObject() && colObj1 != null
						&& !colObj1.isStaticOrKinematicObject()) {
					if (colObj0.isActive() || colObj1.isActive()) {
						getSimulationIslandManager().getUnionFind().unite(colObj0.getIslandTag(),
								colObj1.getIslandTag());
					}
				}
			}
		}

		// Store the island id in each body
		getSimulationIslandManager().storeIslandActivationState(getCollisionWorld());
	}

	/**
	 * Integrate transforms.
	 *
	 * @param timeStep the time step
	 */
	protected void integrateTransforms(final float timeStep) {
		// BulletStats.pushProfile("integrateTransforms");
		Vector3f tmp = VECTORS.get();
		Transform tmpTrans = TRANSFORMS.get();

		Transform predictedTrans = TRANSFORMS.get();
		try {

			for (CollisionObject colObj : collisionObjects) {
				RigidBody body = RigidBody.upcast(colObj);
				if (body != null) {
					body.setHitFraction(1f);

					if (body.isActive() && !body.isStaticOrKinematicObject()) {
						body.predictIntegratedTransform(timeStep, predictedTrans);

						tmp.sub(predictedTrans.origin, body.getWorldTransform(tmpTrans).origin);
						float squareMotion = tmp.lengthSquared();

						if (body.getCcdSquareMotionThreshold() != 0f
								&& body.getCcdSquareMotionThreshold() < squareMotion) {
							if (body.getCollisionShape().isConvex()) {

								ClosestNotMeConvexResultCallback sweepResults = new ClosestNotMeConvexResultCallback(
										body, body.getWorldTransform(tmpTrans).origin, predictedTrans.origin,
										getBroadphase().getOverlappingPairCache(), getDispatcher());
								SphereShape tmpSphere = new SphereShape(body.getCcdSweptSphereRadius());

								sweepResults.collisionFilterGroup = body.getBroadphaseProxy().collisionFilterGroup;
								sweepResults.collisionFilterMask = body.getBroadphaseProxy().collisionFilterMask;

								convexSweepTest(tmpSphere, body.getWorldTransform(tmpTrans), predictedTrans,
										sweepResults);
								if (sweepResults.hasHit() && sweepResults.closestHitFraction > 0.0001f) {
									body.setHitFraction(sweepResults.closestHitFraction);
									body.predictIntegratedTransform(timeStep * body.getHitFraction(), predictedTrans);
									body.setHitFraction(0f);
								}
							}

						}

						body.proceedToTransform(predictedTrans);
					}
				}
			}
		} finally {
			VECTORS.release(tmp);
			TRANSFORMS.release(tmpTrans, predictedTrans);
		}
	}

	/**
	 * Predict unconstraint motion.
	 *
	 * @param timeStep the time step
	 */
	protected void predictUnconstraintMotion(final float timeStep) {
		Transform tmpTrans = TRANSFORMS.get();
		try {

			for (CollisionObject colObj : collisionObjects) {
				RigidBody body = RigidBody.upcast(colObj);
				if (body != null) {
					if (!body.isStaticOrKinematicObject()) {
						if (body.isActive()) {
							body.integrateVelocities(timeStep);
							// damping
							body.applyDamping(timeStep);

							body.predictIntegratedTransform(timeStep, body.getInterpolationWorldTransform(tmpTrans));
						}
					}
				}
			}

		} finally {
			TRANSFORMS.release(tmpTrans);
		}
	}

	@Override
	public void setConstraintSolver(final ConstraintSolver solver) {
		ownsConstraintSolver = false;
		constraintSolver = solver;
	}

	@Override
	public ConstraintSolver getConstraintSolver() {
		return constraintSolver;
	}

	@Override
	public int getNumConstraints() {
		return constraints.size();
	}

	@Override
	public TypedConstraint getConstraint(final int index) {
		return constraints.get(index);
	}

	// JAVA NOTE: not part of the original api
	@Override
	public int getNumActions() {
		return actions.size();
	}

	// JAVA NOTE: not part of the original api
	@Override
	public ActionInterface getAction(final int index) {
		return actions.get(index);
	}

	/**
	 * Gets the simulation island manager.
	 *
	 * @return the simulation island manager
	 */
	public SimulationIslandManager getSimulationIslandManager() {
		return islandManager;
	}

	/**
	 * Gets the collision world.
	 *
	 * @return the collision world
	 */
	public CollisionWorld getCollisionWorld() {
		return this;
	}

	@Override
	public DynamicsWorldType getWorldType() {
		return DynamicsWorldType.DISCRETE_DYNAMICS_WORLD;
	}

	/**
	 * Sets the num tasks.
	 *
	 * @param numTasks the new num tasks
	 */
	public void setNumTasks(final int numTasks) {}

	/**
	 * Sets the pre tick callback.
	 *
	 * @param callback the new pre tick callback
	 */
	public void setPreTickCallback(final InternalTickCallback callback) {
		preTickCallback = callback;
	}

	////////////////////////////////////////////////////////////////////////////

	/** The Constant sortConstraintOnIslandPredicate. */
	private static final Comparator<TypedConstraint> sortConstraintOnIslandPredicate = (lhs, rhs) -> {
		int rIslandId0, lIslandId0;
		rIslandId0 = getConstraintIslandId(rhs);
		lIslandId0 = getConstraintIslandId(lhs);
		return lIslandId0 < rIslandId0 ? -1 : +1;
	};

	/**
	 * The Class ClosestNotMeConvexResultCallback.
	 */
	private static class ClosestNotMeConvexResultCallback extends ClosestConvexResultCallback {
		
		/** The me. */
		private final CollisionObject me;
		
		/** The allowed penetration. */
		private final float allowedPenetration = 0f;
		
		/** The pair cache. */
		private final OverlappingPairCache pairCache;
		
		/** The dispatcher. */
		private final Dispatcher dispatcher;

		/**
		 * Instantiates a new closest not me convex result callback.
		 *
		 * @param me the me
		 * @param fromA the from A
		 * @param toA the to A
		 * @param pairCache the pair cache
		 * @param dispatcher the dispatcher
		 */
		public ClosestNotMeConvexResultCallback(final CollisionObject me, final Vector3f fromA, final Vector3f toA,
				final OverlappingPairCache pairCache, final Dispatcher dispatcher) {
			super(fromA, toA);
			this.me = me;
			this.pairCache = pairCache;
			this.dispatcher = dispatcher;
		}

		@Override
		public float addSingleResult(final LocalConvexResult convexResult, final boolean normalInWorldSpace) {
			if (convexResult.hitCollisionObject == me) return 1f;

			Vector3f linVelA = VECTORS.get(), linVelB = VECTORS.get();
			linVelA.sub(convexToWorld, convexFromWorld);
			linVelB.set(0f, 0f, 0f);// toB.getOrigin()-fromB.getOrigin();

			Vector3f relativeVelocity = VECTORS.get();
			relativeVelocity.sub(linVelA, linVelB);
			// don't report time of impact for motion away from the contact normal (or causes minor penetration)
			if (convexResult.hitNormalLocal.dot(relativeVelocity) >= -allowedPenetration) return 1f;

			VECTORS.release(linVelA, linVelB, relativeVelocity);
			return super.addSingleResult(convexResult, normalInWorldSpace);
		}

		@Override
		public boolean needsCollision(final BroadphaseProxy proxy0) {
			// don't collide with itself
			if (proxy0.clientObject == me) return false;

			// don't do CCD when the collision filters are not matching
			if (!super.needsCollision(proxy0)) return false;

			CollisionObject otherObj = (CollisionObject) proxy0.clientObject;

			// call needsResponse, see http://code.google.com/p/bullet/issues/detail?id=179
			if (dispatcher.needsResponse(me, otherObj)) {
				// don't do CCD when there are already contact points (touching contact/penetration)
				ArrayList<PersistentManifold> manifoldArray = new ArrayList<>();
				BroadphasePair collisionPair = pairCache.findPair(me.getBroadphaseHandle(), proxy0);
				if (collisionPair != null) {
					if (collisionPair.algorithm != null) {
						// manifoldArray.resize(0);
						collisionPair.algorithm.getAllContactManifolds(manifoldArray);
						for (PersistentManifold manifold : manifoldArray) {
							if (manifold.getNumContacts() > 0) return false;
						}
					}
				}
			}
			return true;
		}
	}

}
