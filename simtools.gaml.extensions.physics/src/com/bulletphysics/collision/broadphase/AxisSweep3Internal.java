/*******************************************************************************************************
 *
 * AxisSweep3Internal.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

import static com.bulletphysics.Pools.VECTORS;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.MiscUtil;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * AxisSweep3Internal is an internal base class that implements sweep and prune. Use concrete implementation
 * {@link AxisSweep3} or {@link AxisSweep3_32}.
 *
 * @author jezek2
 */
public abstract class AxisSweep3Internal implements BroadphaseInterface {

	/** The bp handle mask. */
	protected int bpHandleMask;
	
	/** The handle sentinel. */
	protected int handleSentinel;

	/** The world aabb min. */
	protected final Vector3f worldAabbMin = new Vector3f(); // overall system bounds
	
	/** The world aabb max. */
	protected final Vector3f worldAabbMax = new Vector3f(); // overall system bounds

	/** The quantize. */
	protected final Vector3f quantize = new Vector3f(); // scaling factor for quantization

	/** The num handles. */
	protected int numHandles; // number of active handles
	
	/** The max handles. */
	protected int maxHandles; // max number of handles
	
	/** The p handles. */
	protected Handle[] pHandles; // handles pool
	
	/** The first free handle. */
	protected int firstFreeHandle; // free handles list

	/** The p edges. */
	protected EdgeArray[] pEdges = new EdgeArray[3]; // edge arrays for the 3 axes (each array has m_maxHandles * 2 + 2
														// sentinel entries)

	/** The pair cache. */
														protected OverlappingPairCache pairCache;

	// OverlappingPairCallback is an additional optional user callback for adding/removing overlapping pairs, similar
	/** The user pair callback. */
	// interface to OverlappingPairCache.
	protected OverlappingPairCallback userPairCallback = null;

	/** The owns pair cache. */
	protected boolean ownsPairCache = false;

	/** The invalid pair. */
	protected int invalidPair = 0;

	/** The mask. */
	// JAVA NOTE: added
	protected int mask;

	/**
	 * Instantiates a new axis sweep 3 internal.
	 *
	 * @param worldAabbMin the world aabb min
	 * @param worldAabbMax the world aabb max
	 * @param handleMask the handle mask
	 * @param handleSentinel the handle sentinel
	 * @param userMaxHandles the user max handles
	 * @param pairCache the pair cache
	 */
	AxisSweep3Internal(final Vector3f worldAabbMin, final Vector3f worldAabbMax, final int handleMask,
			final int handleSentinel, final int userMaxHandles/* = 16384 */,
			final OverlappingPairCache pairCache/* =0 */) {
		this.bpHandleMask = handleMask;
		this.handleSentinel = handleSentinel;
		this.pairCache = pairCache;

		int maxHandles = userMaxHandles + 1; // need to add one sentinel handle

		if (this.pairCache == null) {
			this.pairCache = new HashedOverlappingPairCache();
			ownsPairCache = true;
		}

		// assert(bounds.HasVolume());

		// init bounds
		this.worldAabbMin.set(worldAabbMin);
		this.worldAabbMax.set(worldAabbMax);

		Vector3f aabbSize = VECTORS.get();
		aabbSize.sub(this.worldAabbMax, this.worldAabbMin);

		int maxInt = this.handleSentinel;

		quantize.set(maxInt / aabbSize.x, maxInt / aabbSize.y, maxInt / aabbSize.z);
		VECTORS.release(aabbSize);

		// allocate handles buffer and put all handles on free list
		pHandles = new Handle[maxHandles];
		for (int i = 0; i < maxHandles; i++) {
			pHandles[i] = createHandle();
		}
		this.maxHandles = maxHandles;
		this.numHandles = 0;

		// handle 0 is reserved as the null index, and is also used as the sentinel
		firstFreeHandle = 1;
		{
			for (int i = firstFreeHandle; i < maxHandles; i++) {
				pHandles[i].setNextFree(i + 1);
			}
			pHandles[maxHandles - 1].setNextFree(0);
		}

		{
			// allocate edge buffers
			for (int i = 0; i < 3; i++) {
				pEdges[i] = createEdgeArray(maxHandles * 2);
			}
		}
		// removed overlap management

		// make boundary sentinels

		pHandles[0].clientObject = null;

		for (int axis = 0; axis < 3; axis++) {
			pHandles[0].setMinEdges(axis, 0);
			pHandles[0].setMaxEdges(axis, 1);

			pEdges[axis].setPos(0, 0);
			pEdges[axis].setHandle(0, 0);
			pEdges[axis].setPos(1, handleSentinel);
			pEdges[axis].setHandle(1, 0);
			// #ifdef DEBUG_BROADPHASE
			// debugPrintAxis(axis);
			// #endif //DEBUG_BROADPHASE
		}

		// JAVA NOTE: added
		mask = getMask();
	}

	/**
	 * Alloc handle.
	 *
	 * @return the int
	 */
	// allocation/deallocation
	protected int allocHandle() {
		assert firstFreeHandle != 0;

		int handle = firstFreeHandle;
		firstFreeHandle = getHandle(handle).getNextFree();
		numHandles++;

		return handle;
	}

	/**
	 * Free handle.
	 *
	 * @param handle the handle
	 */
	protected void freeHandle(final int handle) {
		assert handle > 0 && handle < maxHandles;

		getHandle(handle).setNextFree(firstFreeHandle);
		firstFreeHandle = handle;

		numHandles--;
	}

	/**
	 * Test overlap.
	 *
	 * @param ignoreAxis the ignore axis
	 * @param pHandleA the handle A
	 * @param pHandleB the handle B
	 * @return true, if successful
	 */
	protected boolean testOverlap(final int ignoreAxis, final Handle pHandleA, final Handle pHandleB) {
		// optimization 1: check the array index (memory address), instead of the m_pos

		for (int axis = 0; axis < 3; axis++) {
			if (axis != ignoreAxis) {
				if (pHandleA.getMaxEdges(axis) < pHandleB.getMinEdges(axis)
						|| pHandleB.getMaxEdges(axis) < pHandleA.getMinEdges(axis))
					return false;
			}
		}

		// optimization 2: only 2 axis need to be tested (conflicts with 'delayed removal' optimization)

		/*
		 * for (int axis = 0; axis < 3; axis++) { if (m_pEdges[axis][pHandleA->m_maxEdges[axis]].m_pos <
		 * m_pEdges[axis][pHandleB->m_minEdges[axis]].m_pos || m_pEdges[axis][pHandleB->m_maxEdges[axis]].m_pos <
		 * m_pEdges[axis][pHandleA->m_minEdges[axis]].m_pos) { return false; } }
		 */

		return true;
	}

	// #ifdef DEBUG_BROADPHASE
	// void debugPrintAxis(int axis,bool checkCardinality=true);
	// #endif //DEBUG_BROADPHASE

	/**
	 * Quantize.
	 *
	 * @param out the out
	 * @param point the point
	 * @param isMax the is max
	 */
	protected void quantize(final int[] out, final Vector3f point, final int isMax) {
		Vector3f clampedPoint = VECTORS.get();
		clampedPoint.set(point);

		VectorUtil.setMax(clampedPoint, worldAabbMin);
		VectorUtil.setMin(clampedPoint, worldAabbMax);

		Vector3f v = VECTORS.get();
		v.sub(clampedPoint, worldAabbMin);
		VectorUtil.mul(v, v, quantize);

		out[0] = ((int) v.x & bpHandleMask | isMax) & mask;
		out[1] = ((int) v.y & bpHandleMask | isMax) & mask;
		out[2] = ((int) v.z & bpHandleMask | isMax) & mask;
		VECTORS.release(v, clampedPoint);
	}

	/**
	 * Sort min down.
	 *
	 * @param axis the axis
	 * @param edge the edge
	 * @param dispatcher the dispatcher
	 * @param updateOverlaps the update overlaps
	 */
	// sorting a min edge downwards can only ever *add* overlaps
	protected void sortMinDown( final int axis, final int edge, final Dispatcher dispatcher,
			final boolean updateOverlaps) {
		EdgeArray edgeArray = pEdges[axis];
		int pEdge_idx = edge;
		int pPrev_idx = pEdge_idx - 1;

		Handle pHandleEdge = getHandle(edgeArray.getHandle(pEdge_idx));

		while (edgeArray.getPos(pEdge_idx) < edgeArray.getPos(pPrev_idx)) {
			Handle pHandlePrev = getHandle(edgeArray.getHandle(pPrev_idx));

			if (edgeArray.isMax(pPrev_idx) != 0) {
				// if previous edge is a maximum check the bounds and add an overlap if necessary
				if (updateOverlaps && testOverlap(axis, pHandleEdge, pHandlePrev)) {
					pairCache.addOverlappingPair(pHandleEdge, pHandlePrev);
					if (userPairCallback != null) {
						userPairCallback.addOverlappingPair(pHandleEdge, pHandlePrev);
						// AddOverlap(pEdge->m_handle, pPrev->m_handle);
					}
				}

				// update edge reference in other handle
				pHandlePrev.incMaxEdges(axis);
			} else {
				pHandlePrev.incMinEdges(axis);
			}
			pHandleEdge.decMinEdges(axis);

			// swap the edges
			edgeArray.swap(pEdge_idx, pPrev_idx);

			// decrement
			pEdge_idx--;
			pPrev_idx--;
		}

		// #ifdef DEBUG_BROADPHASE
		// debugPrintAxis(axis);
		// #endif //DEBUG_BROADPHASE
	}

	/**
	 * Sort min up.
	 *
	 * @param axis the axis
	 * @param edge the edge
	 * @param dispatcher the dispatcher
	 * @param updateOverlaps the update overlaps
	 */
	// sorting a min edge upwards can only ever *remove* overlaps
	protected void sortMinUp( final int axis, final int edge, final Dispatcher dispatcher,
			final boolean updateOverlaps) {
		EdgeArray edgeArray = pEdges[axis];
		int pEdge_idx = edge;
		int pNext_idx = pEdge_idx + 1;
		Handle pHandleEdge = getHandle(edgeArray.getHandle(pEdge_idx));

		while (edgeArray.getHandle(pNext_idx) != 0 && edgeArray.getPos(pEdge_idx) >= edgeArray.getPos(pNext_idx)) {
			Handle pHandleNext = getHandle(edgeArray.getHandle(pNext_idx));

			if (edgeArray.isMax(pNext_idx) != 0) {
				// if next edge is maximum remove any overlap between the two handles
				if (updateOverlaps) {
					Handle handle0 = getHandle(edgeArray.getHandle(pEdge_idx));
					Handle handle1 = getHandle(edgeArray.getHandle(pNext_idx));

					pairCache.removeOverlappingPair( handle0, handle1, dispatcher);
					if (userPairCallback != null) {
						userPairCallback.removeOverlappingPair( handle0, handle1, dispatcher);
					}
				}

				// update edge reference in other handle
				pHandleNext.decMaxEdges(axis);
			} else {
				pHandleNext.decMinEdges(axis);
			}
			pHandleEdge.incMinEdges(axis);

			// swap the edges
			edgeArray.swap(pEdge_idx, pNext_idx);

			// increment
			pEdge_idx++;
			pNext_idx++;
		}
	}

	/**
	 * Sort max down.
	 *
	 * @param axis the axis
	 * @param edge the edge
	 * @param dispatcher the dispatcher
	 * @param updateOverlaps the update overlaps
	 */
	// sorting a max edge downwards can only ever *remove* overlaps
	protected void sortMaxDown( final int axis, final int edge, final Dispatcher dispatcher,
			final boolean updateOverlaps) {
		EdgeArray edgeArray = pEdges[axis];
		int pEdge_idx = edge;
		int pPrev_idx = pEdge_idx - 1;
		Handle pHandleEdge = getHandle(edgeArray.getHandle(pEdge_idx));

		while (edgeArray.getPos(pEdge_idx) < edgeArray.getPos(pPrev_idx)) {
			Handle pHandlePrev = getHandle(edgeArray.getHandle(pPrev_idx));

			if (edgeArray.isMax(pPrev_idx) == 0) {
				// if previous edge was a minimum remove any overlap between the two handles
				if (updateOverlaps) {
					// this is done during the overlappingpairarray iteration/narrowphase collision
					Handle handle0 = getHandle(edgeArray.getHandle(pEdge_idx));
					Handle handle1 = getHandle(edgeArray.getHandle(pPrev_idx));
					pairCache.removeOverlappingPair( handle0, handle1, dispatcher);
					if (userPairCallback != null) {
						userPairCallback.removeOverlappingPair( handle0, handle1, dispatcher);
					}
				}

				// update edge reference in other handle
				pHandlePrev.incMinEdges(axis);
			} else {
				pHandlePrev.incMaxEdges(axis);
			}
			pHandleEdge.decMaxEdges(axis);

			// swap the edges
			edgeArray.swap(pEdge_idx, pPrev_idx);

			// decrement
			pEdge_idx--;
			pPrev_idx--;
		}

		// #ifdef DEBUG_BROADPHASE
		// debugPrintAxis(axis);
		// #endif //DEBUG_BROADPHASE
	}

	/**
	 * Sort max up.
	 *
	 * @param axis the axis
	 * @param edge the edge
	 * @param dispatcher the dispatcher
	 * @param updateOverlaps the update overlaps
	 */
	// sorting a max edge upwards can only ever *add* overlaps
	protected void sortMaxUp(final int axis, final int edge, final Dispatcher dispatcher,
			final boolean updateOverlaps) {
		EdgeArray edgeArray = pEdges[axis];
		int pEdge_idx = edge;
		int pNext_idx = pEdge_idx + 1;
		Handle pHandleEdge = getHandle(edgeArray.getHandle(pEdge_idx));

		while (edgeArray.getHandle(pNext_idx) != 0 && edgeArray.getPos(pEdge_idx) >= edgeArray.getPos(pNext_idx)) {
			Handle pHandleNext = getHandle(edgeArray.getHandle(pNext_idx));

			if (edgeArray.isMax(pNext_idx) == 0) {
				// if next edge is a minimum check the bounds and add an overlap if necessary
				if (updateOverlaps && testOverlap(axis, pHandleEdge, pHandleNext)) {
					Handle handle0 = getHandle(edgeArray.getHandle(pEdge_idx));
					Handle handle1 = getHandle(edgeArray.getHandle(pNext_idx));
					pairCache.addOverlappingPair(handle0, handle1);
					if (userPairCallback != null) { userPairCallback.addOverlappingPair(handle0, handle1); }
				}

				// update edge reference in other handle
				pHandleNext.decMinEdges(axis);
			} else {
				pHandleNext.decMaxEdges(axis);
			}
			pHandleEdge.incMaxEdges(axis);

			// swap the edges
			edgeArray.swap(pEdge_idx, pNext_idx);

			// increment
			pEdge_idx++;
			pNext_idx++;
		}
	}

	/**
	 * Gets the num handles.
	 *
	 * @return the num handles
	 */
	public int getNumHandles() {
		return numHandles;
	}

	@Override
	public void calculateOverlappingPairs( final Dispatcher dispatcher) {
		if (pairCache.hasDeferredRemoval()) {
			List<BroadphasePair> overlappingPairArray = pairCache.getOverlappingPairArray();

			// perform a sort, to find duplicates and to sort 'invalid' pairs to the end
			Collections.sort(overlappingPairArray);
			// overlappingPairArray.sort(broadphasePairSortPredicate);

			MiscUtil.resize(overlappingPairArray, overlappingPairArray.size() - invalidPair, BroadphasePair.class);
			invalidPair = 0;

			int i;

			BroadphasePair previousPair = new BroadphasePair();
			previousPair.pProxy0 = null;
			previousPair.pProxy1 = null;
			previousPair.algorithm = null;

			for (i = 0; i < overlappingPairArray.size(); i++) {
				BroadphasePair pair = overlappingPairArray.get(i);

				boolean isDuplicate = pair.equals(previousPair);

				previousPair.set(pair);

				boolean needsRemoval = false;

				if (!isDuplicate) {
					boolean hasOverlap = testAabbOverlap(pair.pProxy0, pair.pProxy1);

					if (hasOverlap) {
						needsRemoval = false;// callback->processOverlap(pair);
					} else {
						needsRemoval = true;
					}
				} else {
					// remove duplicate
					needsRemoval = true;
					// should have no algorithm
					assert pair.algorithm == null;
				}

				if (needsRemoval) {
					pairCache.cleanOverlappingPair( pair, dispatcher);

					// m_overlappingPairArray.swap(i,m_overlappingPairArray.size()-1);
					// m_overlappingPairArray.pop_back();
					pair.pProxy0 = null;
					pair.pProxy1 = null;
					invalidPair++;
					// BulletStats.gOverlappingPairs--;
				}

			}

			// if you don't like to skip the invalid pairs in the array, execute following code:
			// #define CLEAN_INVALID_PAIRS 1
			// #ifdef CLEAN_INVALID_PAIRS

			// perform a sort, to sort 'invalid' pairs to the end
			Collections.sort(overlappingPairArray);
			// overlappingPairArray.sort(broadphasePairSortPredicate);

			MiscUtil.resize(overlappingPairArray, overlappingPairArray.size() - invalidPair, BroadphasePair.class);
			invalidPair = 0;
			// #endif//CLEAN_INVALID_PAIRS

			// printf("overlappingPairArray.size()=%d\n",overlappingPairArray.size());
		}
	}

	/**
	 * Adds the handle.
	 *
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @param pOwner the owner
	 * @param collisionFilterGroup the collision filter group
	 * @param collisionFilterMask the collision filter mask
	 * @param dispatcher the dispatcher
	 * @param multiSapProxy the multi sap proxy
	 * @return the int
	 */
	public int addHandle( final Vector3f aabbMin, final Vector3f aabbMax, final Object pOwner,
			final short collisionFilterGroup, final short collisionFilterMask, final Dispatcher dispatcher,
			final Object multiSapProxy) {
		// quantize the bounds
		int[] min = new int[3], max = new int[3];
		quantize(min, aabbMin, 0);
		quantize(max, aabbMax, 1);

		// allocate a handle
		int handle = allocHandle();

		Handle pHandle = getHandle(handle);

		pHandle.uniqueId = handle;
		// pHandle->m_pOverlaps = 0;
		pHandle.clientObject = pOwner;
		pHandle.collisionFilterGroup = collisionFilterGroup;
		pHandle.collisionFilterMask = collisionFilterMask;
		pHandle.multiSapParentProxy = multiSapProxy;

		// compute current limit of edge arrays
		int limit = numHandles * 2;

		// insert new edges just inside the max boundary edge
		for (int axis = 0; axis < 3; axis++) {
			pHandles[0].setMaxEdges(axis, pHandles[0].getMaxEdges(axis) + 2);

			pEdges[axis].set(limit + 1, limit - 1);

			pEdges[axis].setPos(limit - 1, min[axis]);
			pEdges[axis].setHandle(limit - 1, handle);

			pEdges[axis].setPos(limit, max[axis]);
			pEdges[axis].setHandle(limit, handle);

			pHandle.setMinEdges(axis, limit - 1);
			pHandle.setMaxEdges(axis, limit);
		}

		// now sort the new edges to their correct position
		sortMinDown( 0, pHandle.getMinEdges(0), dispatcher, false);
		sortMaxDown( 0, pHandle.getMaxEdges(0), dispatcher, false);
		sortMinDown( 1, pHandle.getMinEdges(1), dispatcher, false);
		sortMaxDown( 1, pHandle.getMaxEdges(1), dispatcher, false);
		sortMinDown( 2, pHandle.getMinEdges(2), dispatcher, true);
		sortMaxDown( 2, pHandle.getMaxEdges(2), dispatcher, true);

		return handle;
	}

	/**
	 * Removes the handle.
	 *
	 * @param handle the handle
	 * @param dispatcher the dispatcher
	 */
	public void removeHandle( final int handle, final Dispatcher dispatcher) {
		Handle pHandle = getHandle(handle);

		// explicitly remove the pairs containing the proxy
		// we could do it also in the sortMinUp (passing true)
		// todo: compare performance
		if (!pairCache.hasDeferredRemoval()) {
			pairCache.removeOverlappingPairsContainingProxy( pHandle, dispatcher);
		}

		// compute current limit of edge arrays
		int limit = numHandles * 2;

		int axis;

		for (axis = 0; axis < 3; axis++) {
			pHandles[0].setMaxEdges(axis, pHandles[0].getMaxEdges(axis) - 2);
		}

		// remove the edges by sorting them up to the end of the list
		for (axis = 0; axis < 3; axis++) {
			EdgeArray pEdges = this.pEdges[axis];
			int max = pHandle.getMaxEdges(axis);
			pEdges.setPos(max, handleSentinel);

			sortMaxUp(axis, max, dispatcher, false);

			int i = pHandle.getMinEdges(axis);
			pEdges.setPos(i, handleSentinel);

			sortMinUp( axis, i, dispatcher, false);

			pEdges.setHandle(limit - 1, 0);
			pEdges.setPos(limit - 1, handleSentinel);

			// #ifdef DEBUG_BROADPHASE
			// debugPrintAxis(axis,false);
			// #endif //DEBUG_BROADPHASE
		}

		// free the handle
		freeHandle(handle);
	}

	/**
	 * Update handle.
	 *
	 * @param handle the handle
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @param dispatcher the dispatcher
	 */
	public void updateHandle( final int handle, final Vector3f aabbMin,
			final Vector3f aabbMax, final Dispatcher dispatcher) {
		Handle pHandle = getHandle(handle);

		// quantize the new bounds
		int[] min = new int[3], max = new int[3];
		quantize(min, aabbMin, 0);
		quantize(max, aabbMax, 1);

		// update changed edges
		for (int axis = 0; axis < 3; axis++) {
			int emin = pHandle.getMinEdges(axis);
			int emax = pHandle.getMaxEdges(axis);

			int dmin = min[axis] - pEdges[axis].getPos(emin);
			int dmax = max[axis] - pEdges[axis].getPos(emax);

			pEdges[axis].setPos(emin, min[axis]);
			pEdges[axis].setPos(emax, max[axis]);

			// expand (only adds overlaps)
			if (dmin < 0) { sortMinDown( axis, emin, dispatcher, true); }
			if (dmax > 0) {
				sortMaxUp(axis, emax, dispatcher, true); // shrink (only removes overlaps)
			}
			if (dmin > 0) { sortMinUp( axis, emin, dispatcher, true); }
			if (dmax < 0) { sortMaxDown( axis, emax, dispatcher, true); }

			// #ifdef DEBUG_BROADPHASE
			// debugPrintAxis(axis);
			// #endif //DEBUG_BROADPHASE
		}
	}

	/**
	 * Gets the handle.
	 *
	 * @param index the index
	 * @return the handle
	 */
	public Handle getHandle(final int index) {
		return pHandles[index];
	}

	// public void processAllOverlappingPairs(OverlapCallback callback) {
	// }

	@Override
	public BroadphaseProxy createProxy( final Vector3f aabbMin, final Vector3f aabbMax,
			final BroadphaseNativeType shapeType, final Object userPtr, final short collisionFilterGroup,
			final short collisionFilterMask, final Dispatcher dispatcher, final Object multiSapProxy) {
		int handleId = addHandle( aabbMin, aabbMax, userPtr, collisionFilterGroup, collisionFilterMask, dispatcher,
				multiSapProxy);

		Handle handle = getHandle(handleId);

		return handle;
	}

	@Override
	public void destroyProxy( final BroadphaseProxy proxy, final Dispatcher dispatcher) {
		Handle handle = (Handle) proxy;
		removeHandle( handle.uniqueId, dispatcher);
	}

	@Override
	public void setAabb( final BroadphaseProxy proxy, final Vector3f aabbMin,
			final Vector3f aabbMax, final Dispatcher dispatcher) {
		Handle handle = (Handle) proxy;
		updateHandle( handle.uniqueId, aabbMin, aabbMax, dispatcher);
	}

	/**
	 * Test aabb overlap.
	 *
	 * @param proxy0 the proxy 0
	 * @param proxy1 the proxy 1
	 * @return true, if successful
	 */
	public boolean testAabbOverlap(final BroadphaseProxy proxy0, final BroadphaseProxy proxy1) {
		Handle pHandleA = (Handle) proxy0;
		Handle pHandleB = (Handle) proxy1;

		// optimization 1: check the array index (memory address), instead of the m_pos

		for (int axis = 0; axis < 3; axis++) {
			if (pHandleA.getMaxEdges(axis) < pHandleB.getMinEdges(axis)
					|| pHandleB.getMaxEdges(axis) < pHandleA.getMinEdges(axis))
				return false;
		}
		return true;
	}

	@Override
	public OverlappingPairCache getOverlappingPairCache() {
		return pairCache;
	}

	/**
	 * Sets the overlapping pair user callback.
	 *
	 * @param pairCallback the new overlapping pair user callback
	 */
	public void setOverlappingPairUserCallback(final OverlappingPairCallback pairCallback) {
		userPairCallback = pairCallback;
	}

	/**
	 * Gets the overlapping pair user callback.
	 *
	 * @return the overlapping pair user callback
	 */
	public OverlappingPairCallback getOverlappingPairUserCallback() {
		return userPairCallback;
	}

	// getAabb returns the axis aligned bounding box in the 'global' coordinate frame
	// will add some transform later
	@Override
	public void getBroadphaseAabb(final Vector3f aabbMin, final Vector3f aabbMax) {
		aabbMin.set(worldAabbMin);
		aabbMax.set(worldAabbMax);
	}

	@Override
	public void printStats() {
		/*
		 * printf("btAxisSweep3.h\n"); printf("numHandles = %d, maxHandles = %d\n",m_numHandles,m_maxHandles);
		 * printf("aabbMin=%f,%f,%f,aabbMax=%f,%f,%f\n",m_worldAabbMin.getX(),m_worldAabbMin.getY(),m_worldAabbMin.getZ(
		 * ), m_worldAabbMax.getX(),m_worldAabbMax.getY(),m_worldAabbMax.getZ());
		 */
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates the edge array.
	 *
	 * @param size the size
	 * @return the edge array
	 */
	protected abstract EdgeArray createEdgeArray(int size);

	/**
	 * Creates the handle.
	 *
	 * @return the handle
	 */
	protected abstract Handle createHandle();

	/**
	 * Gets the mask.
	 *
	 * @return the mask
	 */
	protected abstract int getMask();

	/**
	 * The Class EdgeArray.
	 */
	protected static abstract class EdgeArray {
		
		/**
		 * Swap.
		 *
		 * @param idx1 the idx 1
		 * @param idx2 the idx 2
		 */
		public abstract void swap(int idx1, int idx2);

		/**
		 * Sets the.
		 *
		 * @param dest the dest
		 * @param src the src
		 */
		public abstract void set(int dest, int src);

		/**
		 * Gets the pos.
		 *
		 * @param index the index
		 * @return the pos
		 */
		public abstract int getPos(int index);

		/**
		 * Sets the pos.
		 *
		 * @param index the index
		 * @param value the value
		 */
		public abstract void setPos(int index, int value);

		/**
		 * Gets the handle.
		 *
		 * @param index the index
		 * @return the handle
		 */
		public abstract int getHandle(int index);

		/**
		 * Sets the handle.
		 *
		 * @param index the index
		 * @param value the value
		 */
		public abstract void setHandle(int index, int value);

		/**
		 * Checks if is max.
		 *
		 * @param offset the offset
		 * @return the int
		 */
		public int isMax(final int offset) {
			return getPos(offset) & 1;
		}
	}

	/**
	 * The Class Handle.
	 */
	protected static abstract class Handle extends BroadphaseProxy {
		
		/**
		 * Gets the min edges.
		 *
		 * @param edgeIndex the edge index
		 * @return the min edges
		 */
		public abstract int getMinEdges(int edgeIndex);

		/**
		 * Sets the min edges.
		 *
		 * @param edgeIndex the edge index
		 * @param value the value
		 */
		public abstract void setMinEdges(int edgeIndex, int value);

		/**
		 * Gets the max edges.
		 *
		 * @param edgeIndex the edge index
		 * @return the max edges
		 */
		public abstract int getMaxEdges(int edgeIndex);

		/**
		 * Sets the max edges.
		 *
		 * @param edgeIndex the edge index
		 * @param value the value
		 */
		public abstract void setMaxEdges(int edgeIndex, int value);

		/**
		 * Inc min edges.
		 *
		 * @param edgeIndex the edge index
		 */
		public void incMinEdges(final int edgeIndex) {
			setMinEdges(edgeIndex, getMinEdges(edgeIndex) + 1);
		}

		/**
		 * Inc max edges.
		 *
		 * @param edgeIndex the edge index
		 */
		public void incMaxEdges(final int edgeIndex) {
			setMaxEdges(edgeIndex, getMaxEdges(edgeIndex) + 1);
		}

		/**
		 * Dec min edges.
		 *
		 * @param edgeIndex the edge index
		 */
		public void decMinEdges(final int edgeIndex) {
			setMinEdges(edgeIndex, getMinEdges(edgeIndex) - 1);
		}

		/**
		 * Dec max edges.
		 *
		 * @param edgeIndex the edge index
		 */
		public void decMaxEdges(final int edgeIndex) {
			setMaxEdges(edgeIndex, getMaxEdges(edgeIndex) - 1);
		}

		/**
		 * Sets the next free.
		 *
		 * @param next the new next free
		 */
		public void setNextFree(final int next) {
			setMinEdges(0, next);
		}

		/**
		 * Gets the next free.
		 *
		 * @return the next free
		 */
		public int getNextFree() {
			return getMinEdges(0);
		}
	}

}
