/*******************************************************************************************************
 *
 * SolverConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * 1D constraint along a normal axis between bodyA and bodyB. It can be combined
 * to solve contact and friction constraints.
 * 
 * @author jezek2
 */
public class SolverConstraint {

	/** The relpos 1 cross normal. */
	public final Vector3f relpos1CrossNormal = new Vector3f();
	
	/** The contact normal. */
	public final Vector3f contactNormal = new Vector3f();

	/** The relpos 2 cross normal. */
	public final Vector3f relpos2CrossNormal = new Vector3f();
	
	/** The angular component A. */
	public final Vector3f angularComponentA = new Vector3f();

	/** The angular component B. */
	public final Vector3f angularComponentB = new Vector3f();
	
	/** The applied push impulse. */
	public float appliedPushImpulse;
	
	/** The applied impulse. */
	public float appliedImpulse;
	
	/** The solver body id A. */
	public int solverBodyIdA;
	
	/** The solver body id B. */
	public int solverBodyIdB;
	
	/** The friction. */
	public float friction;
	
	/** The restitution. */
	public float restitution;
	
	/** The jac diag AB inv. */
	public float jacDiagABInv;
	
	/** The penetration. */
	public float penetration;
	
	/** The constraint type. */
	public SolverConstraintType constraintType;
	
	/** The friction index. */
	public int frictionIndex;
	
	/** The original contact point. */
	public Object originalContactPoint;
	
}
