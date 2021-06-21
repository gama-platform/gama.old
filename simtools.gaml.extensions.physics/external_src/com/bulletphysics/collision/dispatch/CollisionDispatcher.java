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

package com.bulletphysics.collision.dispatch;

import static com.bulletphysics.Pools.MANIFOLDS;

import java.util.ArrayList;
import java.util.Collections;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.broadphase.OverlapCallback;
import com.bulletphysics.collision.broadphase.OverlappingPairCache;
import com.bulletphysics.collision.narrowphase.PersistentManifold;

/**
 * CollisionDispatcher supports algorithms that handle ConvexConvex and ConvexConcave collision pairs. Time of Impact,
 * Closest Points and Penetration Depth.
 *
 * @author jezek2
 */
public class CollisionDispatcher implements Dispatcher {

	// protected final ObjectPool<PersistentManifold> manifoldsPool = ObjectPool.get(s.class);

	private static final int MAX_BROADPHASE_COLLISION_TYPES =
			BroadphaseNativeType.MAX_BROADPHASE_COLLISION_TYPES.ordinal();
	// private int count = 0;
	private final ArrayList<PersistentManifold> manifoldsPtr = new ArrayList<>();
	// private boolean useIslands = true;
	private boolean staticWarningReported = false;
	// private ManifoldResult defaultManifoldResult;
	private NearCallback nearCallback;
	// private PoolAllocator* m_collisionAlgorithmPoolAllocator;
	// private PoolAllocator* m_persistentManifoldPoolAllocator;
	private final CollisionAlgorithmCreateFunc[][] doubleDispatch =
			new CollisionAlgorithmCreateFunc[MAX_BROADPHASE_COLLISION_TYPES][MAX_BROADPHASE_COLLISION_TYPES];
	private CollisionConfiguration collisionConfiguration;
	// private static int gNumManifold = 0;

	private final CollisionAlgorithmConstructionInfo tmpCI = new CollisionAlgorithmConstructionInfo();

	public CollisionDispatcher(final CollisionConfiguration collisionConfiguration) {
		this.collisionConfiguration = collisionConfiguration;

		setNearCallback(new DefaultNearCallback());

		// m_collisionAlgorithmPoolAllocator = collisionConfiguration->getCollisionAlgorithmPool();
		// m_persistentManifoldPoolAllocator = collisionConfiguration->getPersistentManifoldPool();

		for (int i = 0; i < MAX_BROADPHASE_COLLISION_TYPES; i++) {
			for (int j = 0; j < MAX_BROADPHASE_COLLISION_TYPES; j++) {
				doubleDispatch[i][j] = collisionConfiguration.getCollisionAlgorithmCreateFunc(
						BroadphaseNativeType.forValue(i), BroadphaseNativeType.forValue(j));
				assert doubleDispatch[i][j] != null;
			}
		}
	}

	public void registerCollisionCreateFunc(final int proxyType0, final int proxyType1,
			final CollisionAlgorithmCreateFunc createFunc) {
		doubleDispatch[proxyType0][proxyType1] = createFunc;
	}

	public NearCallback getNearCallback() {
		return nearCallback;
	}

	public void setNearCallback(final NearCallback nearCallback) {
		this.nearCallback = nearCallback;
	}

	public CollisionConfiguration getCollisionConfiguration() {
		return collisionConfiguration;
	}

	public void setCollisionConfiguration(final CollisionConfiguration collisionConfiguration) {
		this.collisionConfiguration = collisionConfiguration;
	}

	@Override
	public CollisionAlgorithm findAlgorithm(final CollisionObject body0, final CollisionObject body1,
			final PersistentManifold sharedManifold) {
		CollisionAlgorithmConstructionInfo ci = tmpCI;
		ci.dispatcher1 = this;
		ci.manifold = sharedManifold;
		CollisionAlgorithmCreateFunc createFunc =
				doubleDispatch[body0.getCollisionShape().getShapeType().ordinal()][body1.getCollisionShape()
						.getShapeType().ordinal()];
		CollisionAlgorithm algo = createFunc.createCollisionAlgorithm(ci, body0, body1);
		algo.internalSetCreateFunc(createFunc);

		return algo;
	}

	@Override
	public void freeCollisionAlgorithm(final CollisionAlgorithm algo) {
		CollisionAlgorithmCreateFunc createFunc = algo.internalGetCreateFunc();
		algo.internalSetCreateFunc(null);
		if (createFunc != null) { createFunc.releaseCollisionAlgorithm(algo); }
		algo.destroy();
	}

	@Override
	public PersistentManifold getNewManifold(final Object b0, final Object b1) {

		CollisionObject body0 = (CollisionObject) b0;
		CollisionObject body1 = (CollisionObject) b1;

		PersistentManifold manifold = MANIFOLDS.get();
		manifold.init(body0, body1, 0);

		manifold.index1a = manifoldsPtr.size();
		manifoldsPtr.add(manifold);

		return manifold;
	}

	@Override
	public void releaseManifold(final PersistentManifold manifold) {
		// clearManifold(manifold);
		int findIndex = manifold.index1a;
		assert findIndex < manifoldsPtr.size();
		Collections.swap(manifoldsPtr, findIndex, manifoldsPtr.size() - 1);
		manifoldsPtr.get(findIndex).index1a = findIndex;
		manifoldsPtr.remove(manifoldsPtr.size() - 1);

		MANIFOLDS.release(manifold);
	}

	// @Override
	// public void clearManifold( final PersistentManifold manifold) {
	// manifold.clearManifold();
	// }

	@Override
	public boolean needsCollision(final CollisionObject body0, final CollisionObject body1) {
		assert body0 != null;
		assert body1 != null;

		boolean needsCollision = true;

		// #ifdef BT_DEBUG
		if (!staticWarningReported) {
			// broadphase filtering already deals with this
			if ((body0.isStaticObject() || body0.isKinematicObject())
					&& (body1.isStaticObject() || body1.isKinematicObject())) {
				staticWarningReported = true;
				System.err.println("warning CollisionDispatcher.needsCollision: static-static collision!");
			}
		}
		// #endif //BT_DEBUG

		if (!body0.isActive() && !body1.isActive()) {
			needsCollision = false;
		} else if (!body0.checkCollideWith(body1)) { needsCollision = false; }

		return needsCollision;
	}

	@Override
	public boolean needsResponse(final CollisionObject body0, final CollisionObject body1) {
		// here you can do filtering
		boolean hasResponse = body0.hasContactResponse() && body1.hasContactResponse();
		// no response between two static/kinematic bodies:
		hasResponse = hasResponse && (!body0.isStaticOrKinematicObject() || !body1.isStaticOrKinematicObject());
		return hasResponse;
	}

	private static class CollisionPairCallback implements OverlapCallback {
		private DispatcherInfo dispatchInfo;
		private CollisionDispatcher dispatcher;

		public void init(final DispatcherInfo dispatchInfo, final CollisionDispatcher dispatcher) {
			this.dispatchInfo = dispatchInfo;
			this.dispatcher = dispatcher;
		}

		@Override
		public boolean processOverlap(final BroadphasePair pair) {
			dispatcher.getNearCallback().handleCollision(pair, dispatcher, dispatchInfo);
			return false;
		}
	}

	private final CollisionPairCallback collisionPairCallback = new CollisionPairCallback();

	@Override
	public void dispatchAllCollisionPairs(final OverlappingPairCache pairCache, final DispatcherInfo dispatchInfo,
			final Dispatcher dispatcher) {
		// m_blockedForChanges = true;
		collisionPairCallback.init(dispatchInfo, this);
		pairCache.processAllOverlappingPairs(collisionPairCallback, dispatcher);
		// m_blockedForChanges = false;
	}

	@Override
	public int getNumManifolds() {
		return manifoldsPtr.size();
	}

	@Override
	public PersistentManifold getManifoldByIndexInternal(final int index) {
		return manifoldsPtr.get(index);
	}

	@Override
	public ArrayList<PersistentManifold> getInternalManifoldPointer() {
		return manifoldsPtr;
	}

}
