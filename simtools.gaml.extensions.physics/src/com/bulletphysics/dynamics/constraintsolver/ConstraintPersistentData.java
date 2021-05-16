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
	public float prevAppliedImpulse = 0f;
	public float accumulatedTangentImpulse0 = 0f;
	public float accumulatedTangentImpulse1 = 0f;

	public float jacDiagABInv = 0f;
	public float jacDiagABInvTangent0;
	public float jacDiagABInvTangent1;
	public int persistentLifeTime = 0;
	public float restitution = 0f;
	public float friction = 0f;
	public float penetration = 0f;
	public final Vector3f frictionWorldTangential0 = new Vector3f();
	public final Vector3f frictionWorldTangential1 = new Vector3f();

	public final Vector3f frictionAngularComponent0A = new Vector3f();
	public final Vector3f frictionAngularComponent0B = new Vector3f();
	public final Vector3f frictionAngularComponent1A = new Vector3f();
	public final Vector3f frictionAngularComponent1B = new Vector3f();

	//some data doesn't need to be persistent over frames: todo: clean/reuse this
	public final Vector3f angularComponentA = new Vector3f();
	public final Vector3f angularComponentB = new Vector3f();

	public ContactSolverFunc contactSolverFunc = null;
	public ContactSolverFunc frictionSolverFunc = null;
	
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
