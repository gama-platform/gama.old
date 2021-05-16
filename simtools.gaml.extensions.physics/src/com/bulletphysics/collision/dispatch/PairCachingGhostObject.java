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

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.broadphase.HashedOverlappingPairCache;

/**
 *
 * @author tomrbryn
 */
public class PairCachingGhostObject extends GhostObject {
	
	HashedOverlappingPairCache hashPairCache = new HashedOverlappingPairCache();

	/**
	 * This method is mainly for expert/internal use only.
	 */
	@Override
	public void addOverlappingObjectInternal(BroadphaseProxy otherProxy, BroadphaseProxy thisProxy) {
		BroadphaseProxy actualThisProxy = thisProxy != null? thisProxy : getBroadphaseHandle();
		assert(actualThisProxy != null);

		CollisionObject otherObject = (CollisionObject) otherProxy.clientObject;
		assert (otherObject != null);

		// if this linearSearch becomes too slow (too many overlapping objects) we should add a more appropriate data structure
		int index = overlappingObjects.indexOf(otherObject);
		if (index == -1) {
			overlappingObjects.add(otherObject);
			hashPairCache.addOverlappingPair(actualThisProxy, otherProxy);
		}
	}

	@Override
	public void removeOverlappingObjectInternal(BroadphaseProxy otherProxy, Dispatcher dispatcher, BroadphaseProxy thisProxy1) {
		CollisionObject otherObject = (CollisionObject)otherProxy.clientObject;
		BroadphaseProxy actualThisProxy = thisProxy1 != null? thisProxy1 : getBroadphaseHandle();
		assert(actualThisProxy != null);

		assert (otherObject != null);
		int index = overlappingObjects.indexOf(otherObject);
		if (index != -1) {
			overlappingObjects.setQuick(index, overlappingObjects.getQuick(overlappingObjects.size()-1));
			overlappingObjects.removeQuick(overlappingObjects.size()-1);
			hashPairCache.removeOverlappingPair(actualThisProxy, otherProxy, dispatcher);
		}
	}

	public HashedOverlappingPairCache getOverlappingPairCache() {
		return hashPairCache;
	}
	
}
