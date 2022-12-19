/*******************************************************************************************************
 *
 * GjkEpaPenetrationDepthSolver.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * GjkEpaPenetrationDepthSolver uses the Expanding Polytope Algorithm to calculate the penetration depth between two
 * convex shapes.
 *
 * @author jezek2
 */
public class GjkEpaPenetrationDepthSolver implements ConvexPenetrationDepthSolver {

	/** The gjk epa solver. */
	private final GjkEpaSolver gjkEpaSolver = new GjkEpaSolver();

	@Override
	public boolean calcPenDepth(final SimplexSolverInterface simplexSolver, final ConvexShape pConvexA,
			final ConvexShape pConvexB, final Transform transformA, final Transform transformB, final Vector3f v,
			final Vector3f wWitnessOnA, final Vector3f wWitnessOnB) {
		float radialmargin = 0f;

		// JAVA NOTE: 2.70b1: update when GjkEpaSolver2 is ported

		GjkEpaSolver.Results results = new GjkEpaSolver.Results();
		if (gjkEpaSolver.collide(pConvexA, transformA, pConvexB, transformB, radialmargin/* ,stackAlloc */, results)) {
			// debugDraw->drawLine(results.witnesses[1],results.witnesses[1]+results.normal,btVector3(255,0,0));
			// resultOut->addContactPoint(results.normal,results.witnesses[1],-results.depth);
			wWitnessOnA.set(results.witnesses[0]);
			wWitnessOnB.set(results.witnesses[1]);
			return true;
		}

		return false;
	}

}
