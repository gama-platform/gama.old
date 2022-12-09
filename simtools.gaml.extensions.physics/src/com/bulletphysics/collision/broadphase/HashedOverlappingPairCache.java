/*******************************************************************************************************
 *
 * HashedOverlappingPairCache.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

import com.bulletphysics.linearmath.MiscUtil;
import com.bulletphysics.util.IntArrayList;
import com.bulletphysics.util.ObjectArrayList;

/**
 * Hash-space based {@link OverlappingPairCache}.
 *
 * @author jezek2
 */
public class HashedOverlappingPairCache implements OverlappingPairCache {

	// private final ObjectPool<BroadphasePair> pairsPool = ObjectPool.get(BroadphasePair.class);

	/** The Constant NULL_PAIR. */
	private static final int NULL_PAIR = 0xffffffff;

	/** The overlapping pair array. */
	private final ObjectArrayList<BroadphasePair> overlappingPairArray = new ObjectArrayList<>();
	
	/** The overlap filter callback. */
	private OverlapFilterCallback overlapFilterCallback;
	// private final boolean blockedForChanges = false;

	/** The hash table. */
	private final IntArrayList hashTable = new IntArrayList();
	
	/** The next. */
	private final IntArrayList next = new IntArrayList();
	
	/** The ghost pair callback. */
	protected OverlappingPairCallback ghostPairCallback;

	/**
	 * Instantiates a new hashed overlapping pair cache.
	 */
	public HashedOverlappingPairCache() {
		// int initialAllocatedSize = 2;
		// JAVA TODO: overlappingPairArray.ensureCapacity(initialAllocatedSize);
		growTables();
	}

	/**
	 * Add a pair and return the new pair. If the pair already exists, no new pair is created and the old one is
	 * returned.
	 */
	@Override
	public BroadphasePair addOverlappingPair(final BroadphaseProxy proxy0, final BroadphaseProxy proxy1) {
		// BulletStats.gAddedPairs++;

		if (!needsBroadphaseCollision(proxy0, proxy1)) return null;

		return internalAddPair(proxy0, proxy1);
	}

	@Override
	public Object removeOverlappingPair(BroadphaseProxy proxy0, BroadphaseProxy proxy1, final Dispatcher dispatcher) {
		// BulletStats.gRemovePairs++;
		if (proxy0.getUid() > proxy1.getUid()) {
			BroadphaseProxy tmp = proxy0;
			proxy0 = proxy1;
			proxy1 = tmp;
		}
		int proxyId1 = proxy0.getUid();
		int proxyId2 = proxy1.getUid();

		/*
		 * if (proxyId1 > proxyId2) btSwap(proxyId1, proxyId2);
		 */

		int hash = getHash(proxyId1, proxyId2) & overlappingPairArray.capacity() - 1;

		BroadphasePair pair = internalFindPair(proxy0, proxy1, hash);
		if (pair == null) return null;

		cleanOverlappingPair(pair, dispatcher);

		Object userData = pair.userInfo;

		assert pair.pProxy0.getUid() == proxyId1;
		assert pair.pProxy1.getUid() == proxyId2;

		// JAVA TODO: optimize
		// int pairIndex = int(pair - &m_overlappingPairArray[0]);
		int pairIndex = overlappingPairArray.indexOf(pair);
		assert pairIndex != -1;

		assert pairIndex < overlappingPairArray.size();

		// Remove the pair from the hash table.
		int index = hashTable.get(hash);
		assert index != NULL_PAIR;

		int previous = NULL_PAIR;
		while (index != pairIndex) {
			previous = index;
			index = next.get(index);
		}

		if (previous != NULL_PAIR) {
			assert next.get(previous) == pairIndex;
			next.set(previous, next.get(pairIndex));
		} else {
			hashTable.set(hash, next.get(pairIndex));
		}

		// We now move the last pair into spot of the
		// pair being removed. We need to fix the hash
		// table indices to support the move.

		int lastPairIndex = overlappingPairArray.size() - 1;

		if (ghostPairCallback != null) { ghostPairCallback.removeOverlappingPair(proxy0, proxy1, dispatcher); }

		// If the removed pair is the last pair, we are done.
		if (lastPairIndex == pairIndex) {
			overlappingPairArray.removeQuick(overlappingPairArray.size() - 1);
			return userData;
		}

		// Remove the last pair from the hash table.
		BroadphasePair last = overlappingPairArray.getQuick(lastPairIndex);
		/* missing swap here too, Nat. */
		int lastHash = getHash(last.pProxy0.getUid(), last.pProxy1.getUid()) & overlappingPairArray.capacity() - 1;

		index = hashTable.get(lastHash);
		assert index != NULL_PAIR;

		previous = NULL_PAIR;
		while (index != lastPairIndex) {
			previous = index;
			index = next.get(index);
		}

		if (previous != NULL_PAIR) {
			assert next.get(previous) == lastPairIndex;
			next.set(previous, next.get(lastPairIndex));
		} else {
			hashTable.set(lastHash, next.get(lastPairIndex));
		}

		// Copy the last pair into the remove pair's spot.
		overlappingPairArray.getQuick(pairIndex).set(overlappingPairArray.getQuick(lastPairIndex));

		// Insert the last pair into the hash table
		next.set(pairIndex, hashTable.get(lastHash));
		hashTable.set(lastHash, pairIndex);

		overlappingPairArray.removeQuick(overlappingPairArray.size() - 1);

		return userData;
	}

	/**
	 * Needs broadphase collision.
	 *
	 * @param proxy0 the proxy 0
	 * @param proxy1 the proxy 1
	 * @return true, if successful
	 */
	public boolean needsBroadphaseCollision(final BroadphaseProxy proxy0, final BroadphaseProxy proxy1) {
		if (overlapFilterCallback != null) return overlapFilterCallback.needBroadphaseCollision(proxy0, proxy1);

		boolean collides = (proxy0.collisionFilterGroup & proxy1.collisionFilterMask) != 0;
		return collides && (proxy1.collisionFilterGroup & proxy0.collisionFilterMask) != 0;
	}

	@Override
	public void processAllOverlappingPairs(final OverlapCallback callback, final Dispatcher dispatcher) {
		// printf("m_overlappingPairArray.size()=%d\n",m_overlappingPairArray.size());
		for (int i = 0; i < overlappingPairArray.size();) {

			BroadphasePair pair = overlappingPairArray.getQuick(i);
			if (callback.processOverlap(pair)) {
				removeOverlappingPair(pair.pProxy0, pair.pProxy1, dispatcher);

				// BulletStats.gOverlappingPairs--;
			} else {
				i++;
			}
		}
	}

	@Override
	public void removeOverlappingPairsContainingProxy(final BroadphaseProxy proxy, final Dispatcher dispatcher) {
		processAllOverlappingPairs(new RemovePairCallback(proxy), dispatcher);
	}

	@Override
	public void cleanProxyFromPairs(final BroadphaseProxy proxy, final Dispatcher dispatcher) {
		processAllOverlappingPairs(new CleanPairCallback(proxy, this, dispatcher), dispatcher);
	}

	@Override
	public ObjectArrayList<BroadphasePair> getOverlappingPairArray() { return overlappingPairArray; }

	@Override
	public void cleanOverlappingPair(final BroadphasePair pair, final Dispatcher dispatcher) {
		if (pair.algorithm != null) {
			// pair.algorithm.destroy();
			dispatcher.freeCollisionAlgorithm(pair.algorithm);
			pair.algorithm = null;
		}
	}

	@Override
	public BroadphasePair findPair(BroadphaseProxy proxy0, BroadphaseProxy proxy1) {
		// BulletStats.gFindPairs++;
		if (proxy0.getUid() > proxy1.getUid()) {
			BroadphaseProxy tmp = proxy0;
			proxy0 = proxy1;
			proxy1 = tmp;
		}
		int proxyId1 = proxy0.getUid();
		int proxyId2 = proxy1.getUid();

		/*
		 * if (proxyId1 > proxyId2) btSwap(proxyId1, proxyId2);
		 */

		int hash = getHash(proxyId1, proxyId2) & overlappingPairArray.capacity() - 1;

		if (hash >= hashTable.size()) return null;

		int index = hashTable.get(hash);
		while (index != NULL_PAIR && !equalsPair(overlappingPairArray.getQuick(index), proxyId1, proxyId2)) {
			index = next.get(index);
		}

		if (index == NULL_PAIR) return null;

		assert index < overlappingPairArray.size();

		return overlappingPairArray.getQuick(index);
	}

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public int getCount() { return overlappingPairArray.size(); }

	/**
	 * Gets the overlap filter callback.
	 *
	 * @return the overlap filter callback
	 */
	// btBroadphasePair* GetPairs() { return m_pairs; }
	public OverlapFilterCallback getOverlapFilterCallback() { return overlapFilterCallback; }

	@Override
	public void setOverlapFilterCallback(final OverlapFilterCallback overlapFilterCallback) {
		this.overlapFilterCallback = overlapFilterCallback;
	}

	@Override
	public int getNumOverlappingPairs() { return overlappingPairArray.size(); }

	@Override
	public boolean hasDeferredRemoval() {
		return false;
	}

	/**
	 * Internal add pair.
	 *
	 * @param proxy0 the proxy 0
	 * @param proxy1 the proxy 1
	 * @return the broadphase pair
	 */
	private BroadphasePair internalAddPair(BroadphaseProxy proxy0, BroadphaseProxy proxy1) {
		if (proxy0.getUid() > proxy1.getUid()) {
			BroadphaseProxy tmp = proxy0;
			proxy0 = proxy1;
			proxy1 = tmp;
		}
		int proxyId1 = proxy0.getUid();
		int proxyId2 = proxy1.getUid();

		/*
		 * if (proxyId1 > proxyId2) btSwap(proxyId1, proxyId2);
		 */

		int hash = getHash(proxyId1, proxyId2) & overlappingPairArray.capacity() - 1; // New hash value with new mask

		BroadphasePair pair = internalFindPair(proxy0, proxy1, hash);
		if (pair != null) return pair;
		/*
		 * for(int i=0;i<m_overlappingPairArray.size();++i) { if( (m_overlappingPairArray[i].m_pProxy0==proxy0)&&
		 * (m_overlappingPairArray[i].m_pProxy1==proxy1)) { printf("Adding duplicated %u<>%u\r\n",proxyId1,proxyId2);
		 * internalFindPair(proxy0, proxy1, hash); } }
		 */
		int count = overlappingPairArray.size();
		int oldCapacity = overlappingPairArray.capacity();
		overlappingPairArray.add(null);

		// this is where we add an actual pair, so also call the 'ghost'
		if (ghostPairCallback != null) { ghostPairCallback.addOverlappingPair(proxy0, proxy1); }

		int newCapacity = overlappingPairArray.capacity();

		if (oldCapacity < newCapacity) {
			growTables();
			// hash with new capacity
			hash = getHash(proxyId1, proxyId2) & overlappingPairArray.capacity() - 1;
		}

		pair = new BroadphasePair(proxy0, proxy1);
		// pair->m_pProxy0 = proxy0;
		// pair->m_pProxy1 = proxy1;
		pair.algorithm = null;
		pair.userInfo = null;

		overlappingPairArray.setQuick(overlappingPairArray.size() - 1, pair);

		next.set(count, hashTable.get(hash));
		hashTable.set(hash, count);

		return pair;
	}

	/**
	 * Grow tables.
	 */
	private void growTables() {
		int newCapacity = overlappingPairArray.capacity();

		if (hashTable.size() < newCapacity) {
			// grow hashtable and next table
			int curHashtableSize = hashTable.size();

			MiscUtil.resize(hashTable, newCapacity, 0);
			MiscUtil.resize(next, newCapacity, 0);

			for (int i = 0; i < newCapacity; ++i) { hashTable.set(i, NULL_PAIR); }
			for (int i = 0; i < newCapacity; ++i) { next.set(i, NULL_PAIR); }

			for (int i = 0; i < curHashtableSize; i++) {

				BroadphasePair pair = overlappingPairArray.getQuick(i);
				int proxyId1 = pair.pProxy0.getUid();
				int proxyId2 = pair.pProxy1.getUid();
				/*
				 * if (proxyId1 > proxyId2) btSwap(proxyId1, proxyId2);
				 */
				int hashValue = getHash(proxyId1, proxyId2) & overlappingPairArray.capacity() - 1; // New hash value
																									// with new mask
				next.set(i, hashTable.get(hashValue));
				hashTable.set(hashValue, i);
			}
		}
	}

	/**
	 * Equals pair.
	 *
	 * @param pair the pair
	 * @param proxyId1 the proxy id 1
	 * @param proxyId2 the proxy id 2
	 * @return true, if successful
	 */
	private boolean equalsPair(final BroadphasePair pair, final int proxyId1, final int proxyId2) {
		return pair.pProxy0.getUid() == proxyId1 && pair.pProxy1.getUid() == proxyId2;
	}

	/**
	 * Gets the hash.
	 *
	 * @param proxyId1 the proxy id 1
	 * @param proxyId2 the proxy id 2
	 * @return the hash
	 */
	private int getHash(final int proxyId1, final int proxyId2) {
		int key = proxyId1 | proxyId2 << 16;
		// Thomas Wang's hash

		key += ~(key << 15);
		key ^= key >>> 10;
		key += key << 3;
		key ^= key >>> 6;
		key += ~(key << 11);
		key ^= key >>> 16;
		return key;
	}

	/**
	 * Internal find pair.
	 *
	 * @param proxy0 the proxy 0
	 * @param proxy1 the proxy 1
	 * @param hash the hash
	 * @return the broadphase pair
	 */
	private BroadphasePair internalFindPair(final BroadphaseProxy proxy0, final BroadphaseProxy proxy1,
			final int hash) {
		int proxyId1 = proxy0.getUid();
		int proxyId2 = proxy1.getUid();
		// #if 0 // wrong, 'equalsPair' use unsorted uids, copy-past devil striked again. Nat.
		// if (proxyId1 > proxyId2)
		// btSwap(proxyId1, proxyId2);
		// #endif

		int index = hashTable.get(hash);

		while (index != NULL_PAIR && !equalsPair(overlappingPairArray.getQuick(index), proxyId1, proxyId2)) {
			index = next.get(index);
		}

		if (index == NULL_PAIR) return null;

		assert index < overlappingPairArray.size();

		return overlappingPairArray.getQuick(index);
	}

	@Override
	public void setInternalGhostPairCallback(final OverlappingPairCallback ghostPairCallback) {
		this.ghostPairCallback = ghostPairCallback;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class RemovePairCallback.
	 */
	private static class RemovePairCallback implements OverlapCallback {
		
		/** The obsolete proxy. */
		private final BroadphaseProxy obsoleteProxy;

		/**
		 * Instantiates a new removes the pair callback.
		 *
		 * @param obsoleteProxy the obsolete proxy
		 */
		public RemovePairCallback(final BroadphaseProxy obsoleteProxy) {
			this.obsoleteProxy = obsoleteProxy;
		}

		@Override
		public boolean processOverlap(final BroadphasePair pair) {
			return pair.pProxy0 == obsoleteProxy || pair.pProxy1 == obsoleteProxy;
		}
	}

	/**
	 * The Class CleanPairCallback.
	 */
	private static class CleanPairCallback implements OverlapCallback {
		
		/** The clean proxy. */
		private final BroadphaseProxy cleanProxy;
		
		/** The pair cache. */
		private final OverlappingPairCache pairCache;
		
		/** The dispatcher. */
		private final Dispatcher dispatcher;

		/**
		 * Instantiates a new clean pair callback.
		 *
		 * @param cleanProxy the clean proxy
		 * @param pairCache the pair cache
		 * @param dispatcher the dispatcher
		 */
		public CleanPairCallback(final BroadphaseProxy cleanProxy, final OverlappingPairCache pairCache,
				final Dispatcher dispatcher) {
			this.cleanProxy = cleanProxy;
			this.pairCache = pairCache;
			this.dispatcher = dispatcher;
		}

		@Override
		public boolean processOverlap(final BroadphasePair pair) {
			if (pair.pProxy0 == cleanProxy || pair.pProxy1 == cleanProxy) {
				pairCache.cleanOverlappingPair(pair, dispatcher);
			}
			return false;
		}
	}

}
