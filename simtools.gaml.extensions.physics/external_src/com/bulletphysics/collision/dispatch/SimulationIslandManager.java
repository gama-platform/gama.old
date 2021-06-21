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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bulletphysics.collision.broadphase.BroadphasePair;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.narrowphase.PersistentManifold;

/**
 * SimulationIslandManager creates and handles simulation islands, using {@link UnionFind}.
 *
 * @author jezek2
 */
public class SimulationIslandManager {

	private final UnionFind unionFind = new UnionFind();

	private final ArrayList<PersistentManifold> islandmanifold = new ArrayList<>();
	private final ArrayList<CollisionObject> islandBodies = new ArrayList<>();

	public void initUnionFind(final int n) {
		unionFind.reset(n);
	}

	public UnionFind getUnionFind() {
		return unionFind;
	}

	public void findUnions(final Dispatcher dispatcher, final CollisionWorld colWorld) {
		List<BroadphasePair> pairPtr = colWorld.getPairCache().getOverlappingPairArray();
		for (BroadphasePair collisionPair : pairPtr) {
			CollisionObject colObj0 = (CollisionObject) collisionPair.pProxy0.clientObject;
			CollisionObject colObj1 = (CollisionObject) collisionPair.pProxy1.clientObject;

			if (colObj0 != null && colObj0.mergesSimulationIslands() && colObj1 != null
					&& colObj1.mergesSimulationIslands()) {
				unionFind.unite(colObj0.getIslandTag(), colObj1.getIslandTag());
			}
		}
	}

	public void updateActivationState(final CollisionWorld colWorld, final Dispatcher dispatcher) {
		initUnionFind(colWorld.getCollisionObjectArray().size());

		// put the index into m_controllers into m_tag
		{
			int index = 0;
			int i;
			for (i = 0; i < colWorld.getCollisionObjectArray().size(); i++) {
				CollisionObject collisionObject = colWorld.getCollisionObjectArray().get(i);
				collisionObject.setIslandTag(index);
				collisionObject.setCompanionId(-1);
				collisionObject.setHitFraction(1f);
				index++;
			}
		}
		// do the union find

		findUnions(dispatcher, colWorld);
	}

	public void storeIslandActivationState(final CollisionWorld colWorld) {
		// put the islandId ('find' value) into m_tag
		{
			int index = 0;
			int i;
			for (i = 0; i < colWorld.getCollisionObjectArray().size(); i++) {
				CollisionObject collisionObject = colWorld.getCollisionObjectArray().get(i);
				if (!collisionObject.isStaticOrKinematicObject()) {
					collisionObject.setIslandTag(unionFind.find(index));
					collisionObject.setCompanionId(-1);
				} else {
					collisionObject.setIslandTag(-1);
					collisionObject.setCompanionId(-2);
				}
				index++;
			}
		}
	}

	public void buildIslands(final Dispatcher dispatcher, final List<CollisionObject> collisionObjects) {
		islandmanifold.clear();

		// we are going to sort the unionfind array, and store the element id in
		// the size
		// afterwards, we clean unionfind, to make sure no-one uses it anymore

		getUnionFind().sortIslands();
		int numElem = getUnionFind().getNumElements();

		int endIslandIndex = 1;
		int startIslandIndex;

		// update the sleeping state for bodies, if all are sleeping
		for (startIslandIndex = 0; startIslandIndex < numElem; startIslandIndex = endIslandIndex) {
			int islandId = getUnionFind().getElement(startIslandIndex).id;
			for (endIslandIndex = startIslandIndex + 1; endIslandIndex < numElem
					&& getUnionFind().getElement(endIslandIndex).id == islandId; endIslandIndex++) {}

			// int numSleeping = 0;

			boolean allSleeping = true;

			int idx;
			for (idx = startIslandIndex; idx < endIslandIndex; idx++) {
				int i = getUnionFind().getElement(idx).sz;

				CollisionObject colObj0 = collisionObjects.get(i);
				if (colObj0.getIslandTag() != islandId && colObj0.getIslandTag() != -1) {
					// System.err.println("error in island management\n");
				}

				assert colObj0.getIslandTag() == islandId || colObj0.getIslandTag() == -1;
				if (colObj0.getIslandTag() == islandId) {
					if (colObj0.getActivationState() == CollisionObject.ACTIVE_TAG) { allSleeping = false; }
					if (colObj0.getActivationState() == CollisionObject.DISABLE_DEACTIVATION) { allSleeping = false; }
				}
			}

			if (allSleeping) {
				// int idx;
				for (idx = startIslandIndex; idx < endIslandIndex; idx++) {
					int i = getUnionFind().getElement(idx).sz;
					CollisionObject colObj0 = collisionObjects.get(i);
					if (colObj0.getIslandTag() != islandId && colObj0.getIslandTag() != -1) {
						// System.err.println("error in island management\n");
					}

					assert colObj0.getIslandTag() == islandId || colObj0.getIslandTag() == -1;

					if (colObj0.getIslandTag() == islandId) {
						colObj0.setActivationState(CollisionObject.ISLAND_SLEEPING);
					}
				}
			} else {

				// int idx;
				for (idx = startIslandIndex; idx < endIslandIndex; idx++) {
					int i = getUnionFind().getElement(idx).sz;

					CollisionObject colObj0 = collisionObjects.get(i);
					if (colObj0.getIslandTag() != islandId && colObj0.getIslandTag() != -1) {
						// System.err.println("error in island management\n");
					}

					assert colObj0.getIslandTag() == islandId || colObj0.getIslandTag() == -1;

					if (colObj0.getIslandTag() == islandId) {
						if (colObj0.getActivationState() == CollisionObject.ISLAND_SLEEPING) {
							colObj0.setActivationState(CollisionObject.WANTS_DEACTIVATION);
						}
					}
				}
			}
		}

		int i;
		int maxNumManifolds = dispatcher.getNumManifolds();
		for (i = 0; i < maxNumManifolds; i++) {
			PersistentManifold manifold = dispatcher.getManifoldByIndexInternal(i);
			if (manifold == null) { continue; }
			CollisionObject colObj0 = (CollisionObject) manifold.getBody0();
			CollisionObject colObj1 = (CollisionObject) manifold.getBody1();
			// todo: check sleeping conditions!
			if (colObj0 != null && colObj0.getActivationState() != CollisionObject.ISLAND_SLEEPING
					|| colObj1 != null && colObj1.getActivationState() != CollisionObject.ISLAND_SLEEPING) {

				// kinematic objects don't merge islands, but wake up all
				// connected objects
				if (colObj0.isKinematicObject() && colObj0.getActivationState() != CollisionObject.ISLAND_SLEEPING) {
					colObj1.activate();
				}
				if (colObj1.isKinematicObject() && colObj1.getActivationState() != CollisionObject.ISLAND_SLEEPING) {
					colObj0.activate();
				}
				// #ifdef SPLIT_ISLANDS
				// filtering for response
				if (dispatcher.needsResponse(colObj0, colObj1)) { islandmanifold.add(manifold); }
				// #endif //SPLIT_ISLANDS
			}
		}
	}

	public void buildAndProcessIslands(final Dispatcher dispatcher, final List<CollisionObject> collisionObjects,
			final IslandCallback callback) {
		buildIslands(dispatcher, collisionObjects);

		int endIslandIndex = 1;
		int startIslandIndex;
		int numElem = getUnionFind().getNumElements();

		int numManifolds = islandmanifold.size();
		Collections.sort(islandmanifold);
		// now process all active islands (sets of manifolds for now)
		int startManifoldIndex = 0;
		int endManifoldIndex = 1;
		// traverse the simulation islands, and call the solver, unless all
		// objects are sleeping/deactivated
		for (startIslandIndex = 0; startIslandIndex < numElem; startIslandIndex = endIslandIndex) {
			int islandId = getUnionFind().getElement(startIslandIndex).id;
			boolean islandSleeping = false;

			for (endIslandIndex = startIslandIndex; endIslandIndex < numElem
					&& getUnionFind().getElement(endIslandIndex).id == islandId; endIslandIndex++) {
				int i = getUnionFind().getElement(endIslandIndex).sz;
				CollisionObject colObj0 = collisionObjects.get(i);
				islandBodies.add(colObj0);
				if (!colObj0.isActive()) { islandSleeping = true; }
			}

			// find the accompanying contact manifold for this islandId
			int numIslandManifolds = 0;
			// ArrayList<PersistentManifold> startManifold = null;
			int startManifold_idx = -1;

			if (startManifoldIndex < numManifolds) {
				int curIslandId = islandmanifold.get(startManifoldIndex).getIslandId();
				if (curIslandId == islandId) {
					// startManifold = &m_islandmanifold[startManifoldIndex];
					// startManifold =
					// islandmanifold.subList(startManifoldIndex,
					// islandmanifold.size());
					startManifold_idx = startManifoldIndex;

					for (endManifoldIndex = startManifoldIndex + 1; endManifoldIndex < numManifolds
							&& islandId == islandmanifold.get(endManifoldIndex).getIslandId(); endManifoldIndex++) {

					}
					// Process the actual simulation, only if not
					// sleeping/deactivated
					numIslandManifolds = endManifoldIndex - startManifoldIndex;
				}

			}

			if (!islandSleeping) {
				callback.processIsland(islandBodies, islandBodies.size(), islandmanifold, startManifold_idx,
						numIslandManifolds, islandId);
				// printf("Island callback of size:%d bodies, %d
				// manifolds\n",islandBodies.size(),numIslandManifolds);
			}

			if (numIslandManifolds != 0) { startManifoldIndex = endManifoldIndex; }

			islandBodies.clear();
		}
	}

	public static abstract class IslandCallback {
		public abstract void processIsland(ArrayList<CollisionObject> bodies, int numBodies,
				ArrayList<PersistentManifold> manifolds, int manifolds_offset, int numManifolds, int islandId);
	}

}
