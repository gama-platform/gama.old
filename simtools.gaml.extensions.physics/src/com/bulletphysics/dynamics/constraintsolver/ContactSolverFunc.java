/*******************************************************************************************************
 *
 * ContactSolverFunc.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.RigidBody;

/**
 * Contact solving function.
 *
 * @author jezek2
 */
public interface ContactSolverFunc {

	/**
	 * Resolve contact.
	 *
	 * @param body1 the body 1
	 * @param body2 the body 2
	 * @param contactPoint the contact point
	 * @param info the info
	 * @return the float
	 */
	float resolveContact(RigidBody body1, RigidBody body2, ManifoldPoint contactPoint, ContactSolverInfo info);

}
