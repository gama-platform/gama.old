/*******************************************************************************************************
 *
 * OverlappingPairCache.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

import java.util.List;

/**
 * OverlappingPairCache provides an interface for overlapping pair management (add, remove, storage), used by the
 * {@link BroadphaseInterface} broadphases.
 *
 * @author jezek2
 */

public interface OverlappingPairCache extends OverlappingPairCallback {

	/**
	 * Gets the overlapping pair array.
	 *
	 * @return the overlapping pair array
	 */
	List<BroadphasePair> getOverlappingPairArray();

	/**
	 * Clean overlapping pair.
	 *
	 * @param pair the pair
	 * @param dispatcher the dispatcher
	 */
	void cleanOverlappingPair( BroadphasePair pair, Dispatcher dispatcher);

	/**
	 * Gets the num overlapping pairs.
	 *
	 * @return the num overlapping pairs
	 */
	int getNumOverlappingPairs();

	/**
	 * Clean proxy from pairs.
	 *
	 * @param proxy the proxy
	 * @param dispatcher the dispatcher
	 */
	void cleanProxyFromPairs( BroadphaseProxy proxy, Dispatcher dispatcher);

	/**
	 * Sets the overlap filter callback.
	 *
	 * @param overlapFilterCallback the new overlap filter callback
	 */
	void setOverlapFilterCallback(OverlapFilterCallback overlapFilterCallback);

	/**
	 * Process all overlapping pairs.
	 *
	 * @param callback the callback
	 * @param dispatcher the dispatcher
	 */
	void processAllOverlappingPairs( OverlapCallback callback, Dispatcher dispatcher);

	/**
	 * Find pair.
	 *
	 * @param proxy0 the proxy 0
	 * @param proxy1 the proxy 1
	 * @return the broadphase pair
	 */
	BroadphasePair findPair(BroadphaseProxy proxy0, BroadphaseProxy proxy1);

	/**
	 * Checks for deferred removal.
	 *
	 * @return true, if successful
	 */
	boolean hasDeferredRemoval();

	/**
	 * Sets the internal ghost pair callback.
	 *
	 * @param ghostPairCallback the new internal ghost pair callback
	 */
	void setInternalGhostPairCallback(OverlappingPairCallback ghostPairCallback);

}
