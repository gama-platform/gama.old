/*******************************************************************************************************
 *
 * SimpleBroadphase.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The handles. */
	private final ArrayList<SimpleBroadphaseProxy> handles = new ArrayList<>();
	
	/** The max handles. */
	private int maxHandles; // max number of handles
	
	/** The pair cache. */
	private OverlappingPairCache pairCache;
	
	/** The owns pair cache. */
	private boolean ownsPairCache;

	/**
	 * Instantiates a new simple broadphase.
	 */
	public SimpleBroadphase() {
		this(16384, null);
	}

	/**
	 * Instantiates a new simple broadphase.
	 *
	 * @param maxProxies the max proxies
	 */
	public SimpleBroadphase(final int maxProxies) {
		this(maxProxies, null);
	}

	/**
	 * Instantiates a new simple broadphase.
	 *
	 * @param maxProxies the max proxies
	 * @param overlappingPairCache the overlapping pair cache
	 */
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

	/**
	 * Aabb overlap.
	 *
	 * @param proxy0 the proxy 0
	 * @param proxy1 the proxy 1
	 * @return true, if successful
	 */
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
