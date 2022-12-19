/*******************************************************************************************************
 *
 * RigidBodyConstructionInfo.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The mass. */
	public float mass;

	/**
	 * When a motionState is provided, the rigid body will initialize its world transform
	 * from the motion state. In this case, startWorldTransform is ignored.
	 */
	public MotionState motionState;
	
	/** The start world transform. */
	public final Transform startWorldTransform = new Transform();

	/** The collision shape. */
	public CollisionShape collisionShape;
	
	/** The local inertia. */
	public final Vector3f localInertia = new Vector3f();
	
	/** The linear damping. */
	public float linearDamping = 0f;
	
	/** The angular damping. */
	public float angularDamping = 0f;

	/** Best simulation results when friction is non-zero. */
	public float friction = 0.5f;
	/** Best simulation results using zero restitution. */
	public float restitution = 0f;

	/** The linear sleeping threshold. */
	public float linearSleepingThreshold = 0.8f;
	
	/** The angular sleeping threshold. */
	public float angularSleepingThreshold = 1.0f;

	/**
	 * Additional damping can help avoiding lowpass jitter motion, help stability for ragdolls etc.
	 * Such damping is undesirable, so once the overall simulation quality of the rigid body dynamics
	 * system has improved, this should become obsolete.
	 */
	public boolean additionalDamping = false;
	
	/** The additional damping factor. */
	public float additionalDampingFactor = 0.005f;
	
	/** The additional linear damping threshold sqr. */
	public float additionalLinearDampingThresholdSqr = 0.01f;
	
	/** The additional angular damping threshold sqr. */
	public float additionalAngularDampingThresholdSqr = 0.01f;
	
	/** The additional angular damping factor. */
	public float additionalAngularDampingFactor = 0.01f;

	/**
	 * Instantiates a new rigid body construction info.
	 *
	 * @param mass the mass
	 * @param motionState when a motionState is provided, the rigid body will initialize its world transform
	 * from the motion state. In this case, startWorldTransform is ignored.
	 * @param collisionShape the collision shape
	 */
	public RigidBodyConstructionInfo(float mass, MotionState motionState, CollisionShape collisionShape) {
		this(mass, motionState, collisionShape, new Vector3f(0f, 0f, 0f));
	}
	
	/**
	 * Instantiates a new rigid body construction info.
	 *
	 * @param mass the mass
	 * @param motionState when a motionState is provided, the rigid body will initialize its world transform
	 * from the motion state. In this case, startWorldTransform is ignored.
	 * @param collisionShape the collision shape
	 * @param localInertia the local inertia
	 */
	public RigidBodyConstructionInfo(float mass, MotionState motionState, CollisionShape collisionShape, Vector3f localInertia) {
		this.mass = mass;
		this.motionState = motionState;
		this.collisionShape = collisionShape;
		this.localInertia.set(localInertia);
		
		startWorldTransform.setIdentity();
	}
	
}
