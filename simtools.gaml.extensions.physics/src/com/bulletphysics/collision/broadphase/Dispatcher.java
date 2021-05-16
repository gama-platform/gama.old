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

package com.bulletphysics.collision.broadphase;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.util.ObjectArrayList;

/**
 * Dispatcher abstract class can be used in combination with broadphase to dispatch
 * calculations for overlapping pairs. For example for pairwise collision detection,
 * calculating contact points stored in {@link PersistentManifold} or user callbacks
 * (game logic).
 * 
 * @author jezek2
 */
public abstract class Dispatcher {

	public final CollisionAlgorithm findAlgorithm(CollisionObject body0, CollisionObject body1) {
		return findAlgorithm(body0, body1, null);
	}

	public abstract CollisionAlgorithm findAlgorithm(CollisionObject body0, CollisionObject body1, PersistentManifold sharedManifold);

	public abstract PersistentManifold getNewManifold(Object body0, Object body1);

	public abstract void releaseManifold(PersistentManifold manifold);

	public abstract void clearManifold(PersistentManifold manifold);

	public abstract boolean needsCollision(CollisionObject body0, CollisionObject body1);

	public abstract boolean needsResponse(CollisionObject body0, CollisionObject body1);

	public abstract void dispatchAllCollisionPairs(OverlappingPairCache pairCache, DispatcherInfo dispatchInfo, Dispatcher dispatcher);

	public abstract int getNumManifolds();

	public abstract PersistentManifold getManifoldByIndexInternal(int index);

	public abstract ObjectArrayList<PersistentManifold> getInternalManifoldPointer();

	//public abstract Object allocateCollisionAlgorithm(int size);

	public abstract void freeCollisionAlgorithm(CollisionAlgorithm algo);
	
}
