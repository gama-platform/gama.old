/*******************************************************************************************************
 *
 * OverlappingPairCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

/**
 * OverlappingPairCallback class is an additional optional broadphase user callback for adding/removing overlapping
 * pairs, similar interface to {@link OverlappingPairCache}.
 *
 * @author jezek2
 */

public interface OverlappingPairCallback {

	/**
	 * Adds the overlapping pair.
	 *
	 * @param proxy0 the proxy 0
	 * @param proxy1 the proxy 1
	 * @return the broadphase pair
	 */
	BroadphasePair addOverlappingPair(BroadphaseProxy proxy0, BroadphaseProxy proxy1);

	/**
	 * Removes the overlapping pair.
	 *
	 * @param proxy0 the proxy 0
	 * @param proxy1 the proxy 1
	 * @param dispatcher the dispatcher
	 * @return the object
	 */
	Object removeOverlappingPair( BroadphaseProxy proxy0, BroadphaseProxy proxy1,
			Dispatcher dispatcher);

	/**
	 * Removes the overlapping pairs containing proxy.
	 *
	 * @param proxy0 the proxy 0
	 * @param dispatcher the dispatcher
	 */
	void removeOverlappingPairsContainingProxy( BroadphaseProxy proxy0, Dispatcher dispatcher);

}
