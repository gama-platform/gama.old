/*******************************************************************************************************
 *
 * CollisionDispatcher.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The Constant MAX_BROADPHASE_COLLISION_TYPES. */
	private static final int MAX_BROADPHASE_COLLISION_TYPES =
			BroadphaseNativeType.MAX_BROADPHASE_COLLISION_TYPES.ordinal();
	
	/** The manifolds ptr. */
	// private int count = 0;
	private final ArrayList<PersistentManifold> manifoldsPtr = new ArrayList<>();
	
	/** The static warning reported. */
	// private boolean useIslands = true;
	private boolean staticWarningReported = false;
	
	/** The near callback. */
	// private ManifoldResult defaultManifoldResult;
	private NearCallback nearCallback;
	// private PoolAllocator* m_collisionAlgorithmPoolAllocator;
	/** The double dispatch. */
	// private PoolAllocator* m_persistentManifoldPoolAllocator;
	private final CollisionAlgorithmCreateFunc[][] doubleDispatch =
			new CollisionAlgorithmCreateFunc[MAX_BROADPHASE_COLLISION_TYPES][MAX_BROADPHASE_COLLISION_TYPES];
	
	/** The collision configuration. */
	private CollisionConfiguration collisionConfiguration;
	// private static int gNumManifold = 0;

	/** The tmp CI. */
	private final CollisionAlgorithmConstructionInfo tmpCI = new CollisionAlgorithmConstructionInfo();

	/**
	 * Instantiates a new collision dispatcher.
	 *
	 * @param collisionConfiguration the collision configuration
	 */
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

	/**
	 * Register collision create func.
	 *
	 * @param proxyType0 the proxy type 0
	 * @param proxyType1 the proxy type 1
	 * @param createFunc the create func
	 */
	public void registerCollisionCreateFunc(final int proxyType0, final int proxyType1,
			final CollisionAlgorithmCreateFunc createFunc) {
		doubleDispatch[proxyType0][proxyType1] = createFunc;
	}

	/**
	 * Gets the near callback.
	 *
	 * @return the near callback
	 */
	public NearCallback getNearCallback() {
		return nearCallback;
	}

	/**
	 * Sets the near callback.
	 *
	 * @param nearCallback the new near callback
	 */
	public void setNearCallback(final NearCallback nearCallback) {
		this.nearCallback = nearCallback;
	}

	/**
	 * Gets the collision configuration.
	 *
	 * @return the collision configuration
	 */
	public CollisionConfiguration getCollisionConfiguration() {
		return collisionConfiguration;
	}

	/**
	 * Sets the collision configuration.
	 *
	 * @param collisionConfiguration the new collision configuration
	 */
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

	/**
	 * The Class CollisionPairCallback.
	 */
	private static class CollisionPairCallback implements OverlapCallback {
		
		/** The dispatch info. */
		private DispatcherInfo dispatchInfo;
		
		/** The dispatcher. */
		private CollisionDispatcher dispatcher;

		/**
		 * Inits the.
		 *
		 * @param dispatchInfo the dispatch info
		 * @param dispatcher the dispatcher
		 */
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

	/** The collision pair callback. */
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
