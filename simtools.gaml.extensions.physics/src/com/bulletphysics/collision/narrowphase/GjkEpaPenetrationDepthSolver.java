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

package com.bulletphysics.collision.narrowphase;

import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;

/**
 * GjkEpaPenetrationDepthSolver uses the Expanding Polytope Algorithm to calculate
 * the penetration depth between two convex shapes.
 * 
 * @author jezek2
 */
public class GjkEpaPenetrationDepthSolver extends ConvexPenetrationDepthSolver {

	private GjkEpaSolver gjkEpaSolver = new GjkEpaSolver();

	public boolean calcPenDepth(SimplexSolverInterface simplexSolver,
												  ConvexShape pConvexA, ConvexShape pConvexB,
												  Transform transformA, Transform transformB,
												  Vector3f v, Vector3f wWitnessOnA, Vector3f wWitnessOnB,
												  IDebugDraw debugDraw/*, btStackAlloc* stackAlloc*/)
	{
		float radialmargin = 0f;

		// JAVA NOTE: 2.70b1: update when GjkEpaSolver2 is ported
		
		GjkEpaSolver.Results results = new GjkEpaSolver.Results();
		if (gjkEpaSolver.collide(pConvexA, transformA,
				pConvexB, transformB,
				radialmargin/*,stackAlloc*/, results)) {
			//debugDraw->drawLine(results.witnesses[1],results.witnesses[1]+results.normal,btVector3(255,0,0));
			//resultOut->addContactPoint(results.normal,results.witnesses[1],-results.depth);
			wWitnessOnA.set(results.witnesses[0]);
			wWitnessOnB.set(results.witnesses[1]);
			return true;
		}

		return false;
	}

}
