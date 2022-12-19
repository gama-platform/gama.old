/*******************************************************************************************************
 *
 * ConstraintPersistentData.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

import javax.vecmath.Vector3f;

/**
 * Stores some extra information to each contact point. It is not in the contact
 * point, because that want to keep the collision detection independent from the
 * constraint solver.
 * 
 * @author jezek2
 */
public class ConstraintPersistentData {
	
	/** total applied impulse during most recent frame */
	public float appliedImpulse = 0f;
	
	/** The prev applied impulse. */
	public float prevAppliedImpulse = 0f;
	
	/** The accumulated tangent impulse 0. */
	public float accumulatedTangentImpulse0 = 0f;
	
	/** The accumulated tangent impulse 1. */
	public float accumulatedTangentImpulse1 = 0f;

	/** The jac diag AB inv. */
	public float jacDiagABInv = 0f;
	
	/** The jac diag AB inv tangent 0. */
	public float jacDiagABInvTangent0;
	
	/** The jac diag AB inv tangent 1. */
	public float jacDiagABInvTangent1;
	
	/** The persistent life time. */
	public int persistentLifeTime = 0;
	
	/** The restitution. */
	public float restitution = 0f;
	
	/** The friction. */
	public float friction = 0f;
	
	/** The penetration. */
	public float penetration = 0f;
	
	/** The friction world tangential 0. */
	public final Vector3f frictionWorldTangential0 = new Vector3f();
	
	/** The friction world tangential 1. */
	public final Vector3f frictionWorldTangential1 = new Vector3f();

	/** The friction angular component 0 A. */
	public final Vector3f frictionAngularComponent0A = new Vector3f();
	
	/** The friction angular component 0 B. */
	public final Vector3f frictionAngularComponent0B = new Vector3f();
	
	/** The friction angular component 1 A. */
	public final Vector3f frictionAngularComponent1A = new Vector3f();
	
	/** The friction angular component 1 B. */
	public final Vector3f frictionAngularComponent1B = new Vector3f();

	/** The angular component A. */
	//some data doesn't need to be persistent over frames: todo: clean/reuse this
	public final Vector3f angularComponentA = new Vector3f();
	
	/** The angular component B. */
	public final Vector3f angularComponentB = new Vector3f();

	/** The contact solver func. */
	public ContactSolverFunc contactSolverFunc = null;
	
	/** The friction solver func. */
	public ContactSolverFunc frictionSolverFunc = null;
	
	/**
	 * Reset.
	 */
	public void reset() {
		appliedImpulse = 0f;
		prevAppliedImpulse = 0f;
		accumulatedTangentImpulse0 = 0f;
		accumulatedTangentImpulse1 = 0f;

		jacDiagABInv = 0f;
		jacDiagABInvTangent0 = 0f;
		jacDiagABInvTangent1 = 0f;
		persistentLifeTime = 0;
		restitution = 0f;
		friction = 0f;
		penetration = 0f;
		frictionWorldTangential0.set(0f, 0f, 0f);
		frictionWorldTangential1.set(0f, 0f, 0f);

		frictionAngularComponent0A.set(0f, 0f, 0f);
		frictionAngularComponent0B.set(0f, 0f, 0f);
		frictionAngularComponent1A.set(0f, 0f, 0f);
		frictionAngularComponent1B.set(0f, 0f, 0f);

		angularComponentA.set(0f, 0f, 0f);
		angularComponentB.set(0f, 0f, 0f);

		contactSolverFunc = null;
		frictionSolverFunc = null;
	}
	
}
