/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.collision.broadphase;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

/**
 * SimpleBroadphase is just a unit-test for {@link AxisSweep3}, {@link AxisSweep3_32}, or {@link DbvtBroadphase}, so use
 * those classes instead. It is a brute force AABB culling broadphase based on O(n^2) AABB checks.
 *
 * @author jezek2
 */
public class SimpleBroadphase implements BroadphaseInterface {

	private final ArrayList<SimpleBroadphaseProxy> handles = new ArrayList<>();
	private int maxHandles; // max number of handles
	private OverlappingPairCache pairCache;
	private boolean ownsPairCache;

	public SimpleBroadphase() {
		this(16384, null);
	}

	public SimpleBroadphase(final int maxProxies) {
		this(maxProxies, null);
	}

	public SimpleBroadphase(final int maxProxies, final OverlappingPairCache overlappingPairCache) {
		this.pairCache = overlappingPairCache;

		if (overlappingPairCache == null) {
			pairCache = new HashedOverlappingPairCache();
			ownsPairCache = true;
		}
	}

	@Override
	public BroadphaseProxy createProxy( final Vector3f aabbMin, final Vector3f aabbMax,
			final BroadphaseNativeType shapeType, final Object userPtr, final short collisionFilterGroup,
			final short collisionFilterMask, final Dispatcher dispatcher, final Object multiSapProxy) {
		assert aabbMin.x <= aabbMax.x && aabbMin.y <= aabbMax.y && aabbMin.z <= aabbMax.z;

		SimpleBroadphaseProxy proxy = new SimpleBroadphaseProxy(aabbMin, aabbMax, shapeType, userPtr,
				collisionFilterGroup, collisionFilterMask, multiSapProxy);
		proxy.uniqueId = handles.size();
		handles.add(proxy);
		return proxy;
	}

	@Override
	public void destroyProxy( final BroadphaseProxy proxyOrg, final Dispatcher dispatcher) {
		handles.remove(proxyOrg);

		pairCache.removeOverlappingPairsContainingProxy( proxyOrg, dispatcher);
	}

	@Override
	public void setAabb( final BroadphaseProxy proxy, final Vector3f aabbMin,
			final Vector3f aabbMax, final Dispatcher dispatcher) {
		SimpleBroadphaseProxy sbp = (SimpleBroadphaseProxy) proxy;
		sbp.min.set(aabbMin);
		sbp.max.set(aabbMax);
	}

	private static boolean aabbOverlap(final SimpleBroadphaseProxy proxy0, final SimpleBroadphaseProxy proxy1) {
		return proxy0.min.x <= proxy1.max.x && proxy1.min.x <= proxy0.max.x && proxy0.min.y <= proxy1.max.y
				&& proxy1.min.y <= proxy0.max.y && proxy0.min.z <= proxy1.max.z && proxy1.min.z <= proxy0.max.z;
	}

	@Override
	public void calculateOverlappingPairs( final Dispatcher dispatcher) {
		for (SimpleBroadphaseProxy proxy0 : handles) {
			for (SimpleBroadphaseProxy proxy1 : handles) {
				if (proxy0 == proxy1) { continue; }

				if (aabbOverlap(proxy0, proxy1)) {
					if (pairCache.findPair(proxy0, proxy1) == null) { pairCache.addOverlappingPair(proxy0, proxy1); }
				} else {
					// JAVA NOTE: pairCache.hasDeferredRemoval() = true is not implemented

					if (!pairCache.hasDeferredRemoval()) {
						if (pairCache.findPair(proxy0, proxy1) != null) {
							pairCache.removeOverlappingPair( proxy0, proxy1, dispatcher);
						}
					}
				}
			}
		}
	}

	@Override
	public OverlappingPairCache getOverlappingPairCache() {
		return pairCache;
	}

	@Override
	public void getBroadphaseAabb(final Vector3f aabbMin, final Vector3f aabbMax) {
		aabbMin.set(-1e30f, -1e30f, -1e30f);
		aabbMax.set(1e30f, 1e30f, 1e30f);
	}

	@Override
	public void printStats() {
		// System.out.printf("btSimpleBroadphase.h\n");
		// System.out.printf("numHandles = %d, maxHandles = %d\n", /*numHandles*/ handles.size(), maxHandles);
	}

}
