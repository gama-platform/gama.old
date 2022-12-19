/*******************************************************************************************************
 *
 * ConvexPenetrationDepthSolver.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.linearmath.Transform;

/**
 * ConvexPenetrationDepthSolver provides an interface for penetration depth calculation.
 *
 * @author jezek2
 */
@FunctionalInterface
public interface ConvexPenetrationDepthSolver {

	/**
	 * Calc pen depth.
	 *
	 * @param simplexSolver the simplex solver
	 * @param convexA the convex A
	 * @param convexB the convex B
	 * @param transA the trans A
	 * @param transB the trans B
	 * @param v the v
	 * @param pa the pa
	 * @param pb the pb
	 * @return true, if successful
	 */
	boolean calcPenDepth(SimplexSolverInterface simplexSolver, ConvexShape convexA, ConvexShape convexB,
			Transform transA, Transform transB, Vector3f v, Vector3f pa, Vector3f pb);

}
