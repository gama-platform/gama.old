/*******************************************************************************************************
 *
 * InternalTickCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics;

/**
 * Callback called for each internal tick.
 *
 * @see DynamicsWorld#setInternalTickCallback
 * @author jezek2
 */
@FunctionalInterface
public interface InternalTickCallback {

	/**
	 * Internal tick.
	 *
	 * @param world the world
	 * @param timeStep the time step
	 */
	void internalTick(DynamicsWorld world, float timeStep);

}
