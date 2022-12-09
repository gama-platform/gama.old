/*******************************************************************************************************
 *
 * PairCachingGhostObject.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.broadphase.HashedOverlappingPairCache;

/**
 *
 * @author tomrbryn
 */
public class PairCachingGhostObject extends GhostObject {

	/** The hash pair cache. */
	HashedOverlappingPairCache hashPairCache = new HashedOverlappingPairCache();

	/**
	 * This method is mainly for expert/internal use only.
	 */
	@Override
	public void addOverlappingObjectInternal(final BroadphaseProxy otherProxy, final BroadphaseProxy thisProxy) {
		BroadphaseProxy actualThisProxy = thisProxy != null ? thisProxy : getBroadphaseHandle();
		assert actualThisProxy != null;

		CollisionObject otherObject = (CollisionObject) otherProxy.clientObject;
		assert otherObject != null;

		// if this linearSearch becomes too slow (too many overlapping objects) we should add a more appropriate data
		// structure
		int index = overlappingObjects.indexOf(otherObject);
		if (index == -1) {
			overlappingObjects.add(otherObject);
			hashPairCache.addOverlappingPair(actualThisProxy, otherProxy);
		}
	}

	@Override
	public void removeOverlappingObjectInternal( final BroadphaseProxy otherProxy,
			final Dispatcher dispatcher, final BroadphaseProxy thisProxy1) {
		CollisionObject otherObject = (CollisionObject) otherProxy.clientObject;
		BroadphaseProxy actualThisProxy = thisProxy1 != null ? thisProxy1 : getBroadphaseHandle();
		assert actualThisProxy != null;

		assert otherObject != null;
		int index = overlappingObjects.indexOf(otherObject);
		if (index != -1) {
			overlappingObjects.set(index, overlappingObjects.get(overlappingObjects.size() - 1));
			overlappingObjects.remove(overlappingObjects.size() - 1);
			hashPairCache.removeOverlappingPair( actualThisProxy, otherProxy, dispatcher);
		}
	}

	/**
	 * Gets the overlapping pair cache.
	 *
	 * @return the overlapping pair cache
	 */
	public HashedOverlappingPairCache getOverlappingPairCache() {
		return hashPairCache;
	}

}
