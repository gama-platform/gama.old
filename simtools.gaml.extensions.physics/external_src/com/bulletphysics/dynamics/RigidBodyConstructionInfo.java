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

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;

/**
 * RigidBodyConstructionInfo provides information to create a rigid body.<p>
 * 
 * Setting mass to zero creates a fixed (non-dynamic) rigid body. For dynamic objects,
 * you can use the collision shape to approximate the local inertia tensor, otherwise
 * use the zero vector (default argument).<p>
 * 
 * You can use {@link MotionState} to synchronize the world transform
 * between physics and graphics objects. And if the motion state is provided, the rigid
 * body will initialize its initial world transform from the motion state,
 * {@link #startWorldTransform startWorldTransform} is only used when you don't provide
 * a motion state.
 * 
 * @author jezek2
 */
public class RigidBodyConstructionInfo {

	public float mass;

	/**
	 * When a motionState is provided, the rigid body will initialize its world transform
	 * from the motion state. In this case, startWorldTransform is ignored.
	 */
	public MotionState motionState;
	public final Transform startWorldTransform = new Transform();

	public CollisionShape collisionShape;
	public final Vector3f localInertia = new Vector3f();
	public float linearDamping = 0f;
	public float angularDamping = 0f;

	/** Best simulation results when friction is non-zero. */
	public float friction = 0.5f;
	/** Best simulation results using zero restitution. */
	public float restitution = 0f;

	public float linearSleepingThreshold = 0.8f;
	public float angularSleepingThreshold = 1.0f;

	/**
	 * Additional damping can help avoiding lowpass jitter motion, help stability for ragdolls etc.
	 * Such damping is undesirable, so once the overall simulation quality of the rigid body dynamics
	 * system has improved, this should become obsolete.
	 */
	public boolean additionalDamping = false;
	public float additionalDampingFactor = 0.005f;
	public float additionalLinearDampingThresholdSqr = 0.01f;
	public float additionalAngularDampingThresholdSqr = 0.01f;
	public float additionalAngularDampingFactor = 0.01f;

	public RigidBodyConstructionInfo(float mass, MotionState motionState, CollisionShape collisionShape) {
		this(mass, motionState, collisionShape, new Vector3f(0f, 0f, 0f));
	}
	
	public RigidBodyConstructionInfo(float mass, MotionState motionState, CollisionShape collisionShape, Vector3f localInertia) {
		this.mass = mass;
		this.motionState = motionState;
		this.collisionShape = collisionShape;
		this.localInertia.set(localInertia);
		
		startWorldTransform.setIdentity();
	}
	
}
