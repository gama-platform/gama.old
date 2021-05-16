/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.dynamics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.ContactSolverInfo;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.dynamics.vehicle.RaycastVehicle;
import javax.vecmath.Vector3f;

/**
 * DynamicsWorld is the interface class for several dynamics implementation,
 * basic, discrete, parallel, and continuous etc.
 * 
 * @author jezek2
 */
public abstract class DynamicsWorld extends CollisionWorld {

	protected InternalTickCallback internalTickCallback;
	protected Object worldUserInfo;
	
	protected final ContactSolverInfo solverInfo = new ContactSolverInfo();
	
	public DynamicsWorld(Dispatcher dispatcher, BroadphaseInterface broadphasePairCache, CollisionConfiguration collisionConfiguration) {
		super(dispatcher, broadphasePairCache, collisionConfiguration);
	}

	public final int stepSimulation(float timeStep) {
		return stepSimulation(timeStep, 1, 1f / 60f);
	}

	public final int stepSimulation(float timeStep, int maxSubSteps) {
		return stepSimulation(timeStep, maxSubSteps, 1f / 60f);
	}

	/**
	 * Proceeds the simulation over 'timeStep', units in preferably in seconds.<p>
	 *
	 * By default, Bullet will subdivide the timestep in constant substeps of each
	 * 'fixedTimeStep'.<p>
	 *
	 * In order to keep the simulation real-time, the maximum number of substeps can
	 * be clamped to 'maxSubSteps'.<p>
	 * 
	 * You can disable subdividing the timestep/substepping by passing maxSubSteps=0
	 * as second argument to stepSimulation, but in that case you have to keep the
	 * timeStep constant.
	 */
	public abstract int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep);

	public abstract void debugDrawWorld();

	public final void addConstraint(TypedConstraint constraint) {
		addConstraint(constraint, false);
	}
	
	public void addConstraint(TypedConstraint constraint, boolean disableCollisionsBetweenLinkedBodies) {
	}

	public void removeConstraint(TypedConstraint constraint) {
	}

	public void addAction(ActionInterface action) {
	}

	public void removeAction(ActionInterface action) {
	}

	public void addVehicle(RaycastVehicle vehicle) {
	}

	public void removeVehicle(RaycastVehicle vehicle) {
	}

	/**
	 * Once a rigidbody is added to the dynamics world, it will get this gravity assigned.
	 * Existing rigidbodies in the world get gravity assigned too, during this method.
	 */
	public abstract void setGravity(Vector3f gravity);
	
	public abstract Vector3f getGravity(Vector3f out);

	public abstract void addRigidBody(RigidBody body);

	public abstract void removeRigidBody(RigidBody body);

	public abstract void setConstraintSolver(ConstraintSolver solver);

	public abstract ConstraintSolver getConstraintSolver();

	public int getNumConstraints() {
		return 0;
	}

	public TypedConstraint getConstraint(int index) {
		return null;
	}

	// JAVA NOTE: not part of the original api
	public int getNumActions() {
		return 0;
	}

	// JAVA NOTE: not part of the original api
	public ActionInterface getAction(int index) {
		return null;
	}

	public abstract DynamicsWorldType getWorldType();

	public abstract void clearForces();
	
	/**
	 * Set the callback for when an internal tick (simulation substep) happens, optional user info.
	 */
	public void setInternalTickCallback(InternalTickCallback cb, Object worldUserInfo) {
		this.internalTickCallback = cb;
		this.worldUserInfo = worldUserInfo;
	}

	public void setWorldUserInfo(Object worldUserInfo) {
		this.worldUserInfo = worldUserInfo;
	}

	public Object getWorldUserInfo() {
		return worldUserInfo;
	}

	public ContactSolverInfo getSolverInfo() {
		return solverInfo;
	}
	
}
