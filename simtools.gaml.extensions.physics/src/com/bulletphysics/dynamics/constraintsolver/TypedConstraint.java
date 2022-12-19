/*******************************************************************************************************
 *
 * TypedConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

import com.bulletphysics.dynamics.RigidBody;
import javax.vecmath.Vector3f;

/**
 * TypedConstraint is the base class for Bullet constraints and vehicles.
 * 
 * @author jezek2
 */
public abstract class TypedConstraint {
	
	//protected final BulletStack stack = BulletStack.get();
	
	/** The s fixed. */
	// TODO: stack allocation
	private static /*final*/ RigidBody s_fixed;// = new RigidBody(0, null, null);
	
	/**
	 * Gets the fixed.
	 *
	 * @return the fixed
	 */
	private static synchronized RigidBody getFixed() {
		if (s_fixed == null) {
			s_fixed = new RigidBody(0, null, null);
		}
		return s_fixed;
	}

	/** The user constraint type. */
	private int userConstraintType = -1;
	
	/** The user constraint id. */
	private int userConstraintId = -1;

	/** The constraint type. */
	private TypedConstraintType constraintType;
	
	/** The rb A. */
	protected RigidBody rbA;
	
	/** The rb B. */
	protected RigidBody rbB;
	
	/** The applied impulse. */
	protected float appliedImpulse = 0f;

	/**
	 * Instantiates a new typed constraint.
	 *
	 * @param type the type
	 */
	public TypedConstraint(TypedConstraintType type) {
		this(type, getFixed(), getFixed());
	}
	
	/**
	 * Instantiates a new typed constraint.
	 *
	 * @param type the type
	 * @param rbA the rb A
	 */
	public TypedConstraint(TypedConstraintType type, RigidBody rbA) {
		this(type, rbA, getFixed());
	}
	
	/**
	 * Instantiates a new typed constraint.
	 *
	 * @param type the type
	 * @param rbA the rb A
	 * @param rbB the rb B
	 */
	public TypedConstraint(TypedConstraintType type, RigidBody rbA, RigidBody rbB) {
		this.constraintType = type;
		this.rbA = rbA;
		this.rbB = rbB;
		getFixed().setMassProps(0f, new Vector3f(0f, 0f, 0f));
	}
	
	/**
	 * Builds the jacobian.
	 */
	public abstract void buildJacobian();

	/**
	 * Solve constraint.
	 *
	 * @param timeStep the time step
	 */
	public abstract void solveConstraint(float timeStep);
	
	/**
	 * Gets the rigid body A.
	 *
	 * @return the rigid body A
	 */
	public RigidBody getRigidBodyA() {
		return rbA;
	}

	/**
	 * Gets the rigid body B.
	 *
	 * @return the rigid body B
	 */
	public RigidBody getRigidBodyB() {
		return rbB;
	}

	/**
	 * Gets the user constraint type.
	 *
	 * @return the user constraint type
	 */
	public int getUserConstraintType() {
		return userConstraintType;
	}
	
	/**
	 * Sets the user constraint type.
	 *
	 * @param userConstraintType the new user constraint type
	 */
	public void setUserConstraintType(int userConstraintType) {
		this.userConstraintType = userConstraintType;
	}

	/**
	 * Gets the user constraint id.
	 *
	 * @return the user constraint id
	 */
	public int getUserConstraintId() {
		return userConstraintId;
	}

	/**
	 * Gets the uid.
	 *
	 * @return the uid
	 */
	public int getUid() {
		return userConstraintId;
	}

	/**
	 * Sets the user constraint id.
	 *
	 * @param userConstraintId the new user constraint id
	 */
	public void setUserConstraintId(int userConstraintId) {
		this.userConstraintId = userConstraintId;
	}

	/**
	 * Gets the applied impulse.
	 *
	 * @return the applied impulse
	 */
	public float getAppliedImpulse() {
		return appliedImpulse;
	}

	/**
	 * Gets the constraint type.
	 *
	 * @return the constraint type
	 */
	public TypedConstraintType getConstraintType() {
		return constraintType;
	}
	
}
