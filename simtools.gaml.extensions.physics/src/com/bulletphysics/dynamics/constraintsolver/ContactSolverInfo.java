/*******************************************************************************************************
 *
 * ContactSolverInfo.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

/**
 * Current state of contact solver.
 *
 * @author jezek2
 */
public class ContactSolverInfo {

	/** The tau. */
	public float tau = 0.6f;
	
	/** The damping. */
	public float damping = 1f;
	
	/** The friction. */
	public float friction = 0.3f;
	
	/** The time step. */
	public float timeStep;
	
	/** The restitution. */
	public float restitution = 0f;
	
	/** The num iterations. */
	public int numIterations = 10;
	
	/** The max error reduction. */
	public float maxErrorReduction = 20f;
	
	/** The sor. */
	public float sor = 1.3f;
	
	/** The erp. */
	public float erp = 0.2f; // used as Baumgarte factor
	
	/** The erp 2. */
	public float erp2 = 0.1f; // used in Split Impulse
	
	/** The split impulse. */
	public boolean splitImpulse = false;
	
	/** The split impulse penetration threshold. */
	public float splitImpulsePenetrationThreshold = -0.02f;
	
	/** The linear slop. */
	public float linearSlop = 0f;
	
	/** The warmstarting factor. */
	public float warmstartingFactor = 0.85f;

	/** The solver mode. */
	public int solverMode = /* SolverMode.SOLVER_RANDMIZE_ORDER | */SolverMode.SOLVER_CACHE_FRIENDLY
	/* | SolverMode.SOLVER_USE_WARMSTARTING */;

	/**
	 * Instantiates a new contact solver info.
	 */
	public ContactSolverInfo() {}

	/**
	 * Instantiates a new contact solver info.
	 *
	 * @param g the g
	 */
	public ContactSolverInfo(final ContactSolverInfo g) {
		tau = g.tau;
		damping = g.damping;
		friction = g.friction;
		timeStep = g.timeStep;
		restitution = g.restitution;
		numIterations = g.numIterations;
		maxErrorReduction = g.maxErrorReduction;
		sor = g.sor;
		erp = g.erp;
	}

}
