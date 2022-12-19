/*******************************************************************************************************
 *
 * SolverMode.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

/**
 * Solver flags.
 * 
 * @author jezek2
 */
public class SolverMode {
	
	/** The Constant SOLVER_RANDMIZE_ORDER. */
	public static final int SOLVER_RANDMIZE_ORDER    = 1;
	
	/** The Constant SOLVER_FRICTION_SEPARATE. */
	public static final int SOLVER_FRICTION_SEPARATE = 2;
	
	/** The Constant SOLVER_USE_WARMSTARTING. */
	public static final int SOLVER_USE_WARMSTARTING  = 4;
	
	/** The Constant SOLVER_CACHE_FRIENDLY. */
	public static final int SOLVER_CACHE_FRIENDLY    = 8;

}
