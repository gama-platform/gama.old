/*******************************************************************************************************
 *
 * ContactProcessedCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;

/**
 * Called when existing contact between two collision objects has been processed.
 *
 * @see BulletGlobals#setContactProcessedCallback
 * @author jezek2
 */
public interface ContactProcessedCallback {

	/**
	 * Contact processed.
	 *
	 * @param cp the cp
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @return true, if successful
	 */
	boolean contactProcessed(ManifoldPoint cp, CollisionObject body0, CollisionObject body1);

}
