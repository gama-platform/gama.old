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

import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.DispatchFunc;
import com.bulletphysics.collision.broadphase.DispatcherInfo;

/**
 * Default implementation of {@link NearCallback}.
 * 
 * @author jezek2
 */
public class DefaultNearCallback extends NearCallback {

	private final ManifoldResult contactPointResult = new ManifoldResult();

	public void handleCollision(BroadphasePair collisionPair, CollisionDispatcher dispatcher, DispatcherInfo dispatchInfo) {
		CollisionObject colObj0 = (CollisionObject) collisionPair.pProxy0.clientObject;
		CollisionObject colObj1 = (CollisionObject) collisionPair.pProxy1.clientObject;

		if (dispatcher.needsCollision(colObj0, colObj1)) {
			// dispatcher will keep algorithms persistent in the collision pair
			if (collisionPair.algorithm == null) {
				collisionPair.algorithm = dispatcher.findAlgorithm(colObj0, colObj1);
			}

			if (collisionPair.algorithm != null) {
				//ManifoldResult contactPointResult = new ManifoldResult(colObj0, colObj1);
				contactPointResult.init(colObj0, colObj1);

				if (dispatchInfo.dispatchFunc == DispatchFunc.DISPATCH_DISCRETE) {
					// discrete collision detection query
					collisionPair.algorithm.processCollision(colObj0, colObj1, dispatchInfo, contactPointResult);
				}
				else {
					// continuous collision detection query, time of impact (toi)
					float toi = collisionPair.algorithm.calculateTimeOfImpact(colObj0, colObj1, dispatchInfo, contactPointResult);
					if (dispatchInfo.timeOfImpact > toi) {
						dispatchInfo.timeOfImpact = toi;
					}
				}
			}
		}
	}
	
}
