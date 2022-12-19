/*******************************************************************************************************
 *
 * DynamicsWorld.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.ContactSolverInfo;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.dynamics.vehicle.RaycastVehicle;

/**
 * DynamicsWorld is the interface class for several dynamics implementation, basic, discrete, parallel, and continuous
 * etc.
 *
 * @author jezek2
 */
public abstract class DynamicsWorld extends CollisionWorld {

	/** The internal tick callback. */
	protected InternalTickCallback internalTickCallback;
	
	/** The world user info. */
	protected Object worldUserInfo;

	/** The solver info. */
	protected final ContactSolverInfo solverInfo = new ContactSolverInfo();

	/**
	 * Instantiates a new dynamics world.
	 *
	 * @param dispatcher the dispatcher
	 * @param broadphasePairCache the broadphase pair cache
	 * @param collisionConfiguration the collision configuration
	 */
	public DynamicsWorld(final Dispatcher dispatcher, final BroadphaseInterface broadphasePairCache,
			final CollisionConfiguration collisionConfiguration) {
		super(dispatcher, broadphasePairCache, collisionConfiguration);
	}

	/**
	 * Step simulation.
	 *
	 * @param timeStep the time step
	 * @return the int
	 */
	public final int stepSimulation(final float timeStep) {
		return stepSimulation(timeStep, 1, 1f / 60f);
	}

	/**
	 * Step simulation.
	 *
	 * @param timeStep the time step
	 * @param maxSubSteps the max sub steps
	 * @return the int
	 */
	public final int stepSimulation(final float timeStep, final int maxSubSteps) {
		return stepSimulation(timeStep, maxSubSteps, 1f / 60f);
	}

	/**
	 * Proceeds the simulation over 'timeStep', units in preferably in seconds.
	 * <p>
	 *
	 * By default, Bullet will subdivide the timestep in constant substeps of each 'fixedTimeStep'.
	 * <p>
	 *
	 * In order to keep the simulation real-time, the maximum number of substeps can be clamped to 'maxSubSteps'.
	 * <p>
	 *
	 * You can disable subdividing the timestep/substepping by passing maxSubSteps=0 as second argument to
	 * stepSimulation, but in that case you have to keep the timeStep constant.
	 */
	public abstract int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep);

	/**
	 * Adds the constraint.
	 *
	 * @param constraint the constraint
	 */
	public final void addConstraint(final TypedConstraint constraint) {
		addConstraint(constraint, false);
	}

	/**
	 * Adds the constraint.
	 *
	 * @param constraint the constraint
	 * @param disableCollisionsBetweenLinkedBodies the disable collisions between linked bodies
	 */
	public void addConstraint(final TypedConstraint constraint, final boolean disableCollisionsBetweenLinkedBodies) {}

	/**
	 * Removes the constraint.
	 *
	 * @param constraint the constraint
	 */
	public void removeConstraint(final TypedConstraint constraint) {}

	/**
	 * Adds the action.
	 *
	 * @param action the action
	 */
	public void addAction(final ActionInterface action) {}

	/**
	 * Removes the action.
	 *
	 * @param action the action
	 */
	public void removeAction(final ActionInterface action) {}

	/**
	 * Adds the vehicle.
	 *
	 * @param vehicle the vehicle
	 */
	public void addVehicle(final RaycastVehicle vehicle) {}

	/**
	 * Removes the vehicle.
	 *
	 * @param vehicle the vehicle
	 */
	public void removeVehicle(final RaycastVehicle vehicle) {}

	/**
	 * Once a rigidbody is added to the dynamics world, it will get this gravity assigned. Existing rigidbodies in the
	 * world get gravity assigned too, during this method.
	 */
	public abstract void setGravity(Vector3f gravity);

	/**
	 * Gets the gravity.
	 *
	 * @param out the out
	 * @return the gravity
	 */
	public abstract Vector3f getGravity(Vector3f out);

	/**
	 * Adds the rigid body.
	 *
	 * @param body the body
	 */
	public abstract void addRigidBody(RigidBody body);

	/**
	 * Removes the rigid body.
	 *
	 * @param body the body
	 */
	public abstract void removeRigidBody(RigidBody body);

	/**
	 * Sets the constraint solver.
	 *
	 * @param solver the new constraint solver
	 */
	public abstract void setConstraintSolver(ConstraintSolver solver);

	/**
	 * Gets the constraint solver.
	 *
	 * @return the constraint solver
	 */
	public abstract ConstraintSolver getConstraintSolver();

	/**
	 * Gets the num constraints.
	 *
	 * @return the num constraints
	 */
	public int getNumConstraints() {
		return 0;
	}

	/**
	 * Gets the constraint.
	 *
	 * @param index the index
	 * @return the constraint
	 */
	public TypedConstraint getConstraint(final int index) {
		return null;
	}

	/**
	 * Gets the num actions.
	 *
	 * @return the num actions
	 */
	// JAVA NOTE: not part of the original api
	public int getNumActions() {
		return 0;
	}

	/**
	 * Gets the action.
	 *
	 * @param index the index
	 * @return the action
	 */
	// JAVA NOTE: not part of the original api
	public ActionInterface getAction(final int index) {
		return null;
	}

	/**
	 * Gets the world type.
	 *
	 * @return the world type
	 */
	public abstract DynamicsWorldType getWorldType();

	/**
	 * Clear forces.
	 */
	public abstract void clearForces();

	/**
	 * Set the callback for when an internal tick (simulation substep) happens, optional user info.
	 */
	public void setInternalTickCallback(final InternalTickCallback cb, final Object worldUserInfo) {
		this.internalTickCallback = cb;
		this.worldUserInfo = worldUserInfo;
	}

	/**
	 * Sets the world user info.
	 *
	 * @param worldUserInfo the new world user info
	 */
	public void setWorldUserInfo(final Object worldUserInfo) {
		this.worldUserInfo = worldUserInfo;
	}

	/**
	 * Gets the world user info.
	 *
	 * @return the world user info
	 */
	public Object getWorldUserInfo() {
		return worldUserInfo;
	}

	/**
	 * Gets the solver info.
	 *
	 * @return the solver info
	 */
	public ContactSolverInfo getSolverInfo() {
		return solverInfo;
	}

}
