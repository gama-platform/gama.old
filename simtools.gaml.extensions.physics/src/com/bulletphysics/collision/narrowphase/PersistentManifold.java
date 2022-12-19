/*******************************************************************************************************
 *
 * PersistentManifold.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import static com.bulletphysics.Pools.VECTORS;
import static com.bulletphysics.Pools.VECTORS4;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * PersistentManifold is a contact point cache, it stays persistent as long as objects are overlapping in the
 * broadphase. Those contact points are created by the collision narrow phase.
 * <p>
 *
 * The cache can be empty, or hold 1, 2, 3 or 4 points. Some collision algorithms (GJK) might only add one point at a
 * time, updates/refreshes old contact points, and throw them away if necessary (distance becomes too large).
 * <p>
 *
 * Reduces the cache to 4 points, when more then 4 points are added, using following rules: the contact point with
 * deepest penetration is always kept, and it tries to maximize the area covered by the points.
 * <p>
 *
 * Note that some pairs of objects might have more then one contact manifold.
 *
 * @author jezek2
 */
public class PersistentManifold implements Comparable<PersistentManifold> {

	// protected final BulletStack stack = BulletStack.get();

	/** The Constant MANIFOLD_CACHE_SIZE. */
	public static final int MANIFOLD_CACHE_SIZE = 4;

	/** The point cache. */
	private final ManifoldPoint[] pointCache = new ManifoldPoint[MANIFOLD_CACHE_SIZE];
	/// this two body pointers can point to the physics rigidbody class.
	/** The body 0. */
	/// void* will allow any rigidbody class
	private CollisionObject body0;
	
	/** The body 1. */
	private CollisionObject body1;
	
	/** The cached points. */
	private int cachedPoints;
	// int islandId = -1;

	/** The index 1 a. */
	public int index1a;

	{
		for (int i = 0; i < pointCache.length; i++) {
			pointCache[i] = new ManifoldPoint();
		}
	}

	/**
	 * Instantiates a new persistent manifold.
	 */
	public PersistentManifold() {}

	/**
	 * Instantiates a new persistent manifold.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param bla the bla
	 */
	public PersistentManifold(final CollisionObject body0, final CollisionObject body1, final int bla) {
		init(body0, body1, bla);
	}

	/**
	 * Gets the island id.
	 *
	 * @return the island id
	 */
	public int getIslandId() {
		// if (islandId == -1) {
		CollisionObject rcolObj0 = (CollisionObject) getBody0();
		CollisionObject rcolObj1 = (CollisionObject) getBody1();
		int islandId = rcolObj0.getIslandTag() >= 0 ? rcolObj0.getIslandTag() : rcolObj1.getIslandTag();
		// }
		return islandId;

	}

	@Override
	public int compareTo(final PersistentManifold o) {
		return Integer.compare(getIslandId(), o.getIslandId());
	}

	/**
	 * Inits the.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param bla the bla
	 */
	public void init(final CollisionObject body0, final CollisionObject body1, final int bla) {
		this.body0 = body0;
		this.body1 = body1;
		cachedPoints = 0;
		index1a = 0;
	}

	/**
	 * Sort cached points.
	 *
	 * @param pt the pt
	 * @return the int
	 */
	/// sort cached points so most isolated points come first
	private int sortCachedPoints(final ManifoldPoint pt) {
		// calculate 4 possible cases areas, and take biggest area
		// also need to keep 'deepest'

		int maxPenetrationIndex = -1;
		float maxPenetration = pt.getDistance();
		for (int i = 0; i < 4; i++) {
			if (pointCache[i].getDistance() < maxPenetration) {
				maxPenetrationIndex = i;
				maxPenetration = pointCache[i].getDistance();
			}
		}
		// #endif //KEEP_DEEPEST_POINT

		float res0 = 0f, res1 = 0f, res2 = 0f, res3 = 0f;
		if (maxPenetrationIndex != 0) {
			Vector3f a0 = VECTORS.get(pt.localPointA);
			a0.sub(pointCache[1].localPointA);

			Vector3f b0 = VECTORS.get(pointCache[3].localPointA);
			b0.sub(pointCache[2].localPointA);

			Vector3f cross = VECTORS.get();
			cross.cross(a0, b0);

			res0 = cross.lengthSquared();
			VECTORS.release(a0, b0, cross);
		}

		if (maxPenetrationIndex != 1) {
			Vector3f a1 = VECTORS.get(pt.localPointA);
			a1.sub(pointCache[0].localPointA);

			Vector3f b1 = VECTORS.get(pointCache[3].localPointA);
			b1.sub(pointCache[2].localPointA);

			Vector3f cross = VECTORS.get();
			cross.cross(a1, b1);
			res1 = cross.lengthSquared();
			VECTORS.release(a1, b1, cross);
		}

		if (maxPenetrationIndex != 2) {
			Vector3f a2 = VECTORS.get(pt.localPointA);
			a2.sub(pointCache[0].localPointA);

			Vector3f b2 = VECTORS.get(pointCache[3].localPointA);
			b2.sub(pointCache[1].localPointA);

			Vector3f cross = VECTORS.get();
			cross.cross(a2, b2);

			res2 = cross.lengthSquared();
			VECTORS.release(a2, b2, cross);
		}

		if (maxPenetrationIndex != 3) {
			Vector3f a3 = VECTORS.get(pt.localPointA);
			a3.sub(pointCache[0].localPointA);

			Vector3f b3 = VECTORS.get(pointCache[2].localPointA);
			b3.sub(pointCache[1].localPointA);

			Vector3f cross = VECTORS.get();
			cross.cross(a3, b3);
			res3 = cross.lengthSquared();
			VECTORS.release(a3, b3, cross);
		}

		Vector4f maxvec = VECTORS4.get();
		maxvec.set(res0, res1, res2, res3);
		int biggestarea = VectorUtil.closestAxis4(maxvec);
		VECTORS4.release(maxvec);
		return biggestarea;
	}

	// private int findContactPoint(ManifoldPoint unUsed, int numUnused, ManifoldPoint pt);

	/**
	 * Gets the body 0.
	 *
	 * @return the body 0
	 */
	public Object getBody0() {
		return body0;
	}

	/**
	 * Gets the body 1.
	 *
	 * @return the body 1
	 */
	public Object getBody1() {
		return body1;
	}

	/**
	 * Sets the bodies.
	 *
	 * @param body0 the body 0
	 * @param body1 the body 1
	 */
	public void setBodies(final CollisionObject body0, final CollisionObject body1) {
		this.body0 = body0;
		this.body1 = body1;
	}

	// public void clearUserCache( final ManifoldPoint pt) {
	// if (pt.userPersistentData != null && info.world.getContactDestroyedCallback() != null) {
	// info.world.getContactDestroyedCallback().contactDestroyed(pt.userPersistentData);
	// pt.userPersistentData = null;
	// }
	// }

	/**
	 * Gets the num contacts.
	 *
	 * @return the num contacts
	 */
	public int getNumContacts() {
		return cachedPoints;
	}

	/**
	 * Gets the contact point.
	 *
	 * @param index the index
	 * @return the contact point
	 */
	public ManifoldPoint getContactPoint(final int index) {
		return pointCache[index];
	}

	/**
	 * Gets the contact breaking threshold.
	 *
	 * @return the contact breaking threshold
	 */
	// todo: get this margin from the current physics / collision environment
	public float getContactBreakingThreshold() {
		return BulletGlobals.getContactBreakingThreshold();
	}

	/**
	 * Gets the cache entry.
	 *
	 * @param newPoint the new point
	 * @return the cache entry
	 */
	public int getCacheEntry(final ManifoldPoint newPoint) {
		float shortestDist = getContactBreakingThreshold() * getContactBreakingThreshold();
		int size = getNumContacts();
		int nearestPoint = -1;
		Vector3f diffA = VECTORS.get();
		for (int i = 0; i < size; i++) {
			ManifoldPoint mp = pointCache[i];

			diffA.sub(mp.localPointA, newPoint.localPointA);

			float distToManiPoint = diffA.dot(diffA);
			if (distToManiPoint < shortestDist) {
				shortestDist = distToManiPoint;
				nearestPoint = i;
			}
		}
		VECTORS.release(diffA);
		return nearestPoint;
	}

	/**
	 * Adds the manifold point.
	 *
	 * @param newPoint the new point
	 * @return the int
	 */
	public int addManifoldPoint(final ManifoldPoint newPoint) {
		assert validContactDistance(newPoint);

		int insertIndex = getNumContacts();
		if (insertIndex == MANIFOLD_CACHE_SIZE) {
			// #if MANIFOLD_CACHE_SIZE >= 4
			if (MANIFOLD_CACHE_SIZE >= 4) {
				// sort cache so best points come first, based on area
				insertIndex = sortCachedPoints(newPoint);
			} else {
				// #else
				insertIndex = 0;
			}
			// #endif

			// clearUserCache( pointCache[insertIndex]);
		} else {
			cachedPoints++;
		}
		assert pointCache[insertIndex].userPersistentData == null;
		pointCache[insertIndex].set(newPoint);
		return insertIndex;
	}

	/**
	 * Removes the contact point.
	 *
	 * @param index the index
	 */
	public void removeContactPoint(final int index) {
		// clearUserCache( pointCache[index]);

		int lastUsedIndex = getNumContacts() - 1;
		// m_pointCache[index] = m_pointCache[lastUsedIndex];
		if (index != lastUsedIndex) {
			// TODO: possible bug
			pointCache[index].set(pointCache[lastUsedIndex]);
			// get rid of duplicated userPersistentData pointer
			pointCache[lastUsedIndex].userPersistentData = null;
			pointCache[lastUsedIndex].appliedImpulse = 0f;
			pointCache[lastUsedIndex].lateralFrictionInitialized = false;
			pointCache[lastUsedIndex].appliedImpulseLateral1 = 0f;
			pointCache[lastUsedIndex].appliedImpulseLateral2 = 0f;
			pointCache[lastUsedIndex].lifeTime = 0;
		}

		assert pointCache[lastUsedIndex].userPersistentData == null;
		cachedPoints--;
	}

	/**
	 * Replace contact point.
	 *
	 * @param newPoint the new point
	 * @param insertIndex the insert index
	 */
	public void replaceContactPoint(final ManifoldPoint newPoint, final int insertIndex) {
		assert validContactDistance(newPoint);

		int lifeTime = pointCache[insertIndex].getLifeTime();
		float appliedImpulse = pointCache[insertIndex].appliedImpulse;
		float appliedLateralImpulse1 = pointCache[insertIndex].appliedImpulseLateral1;
		float appliedLateralImpulse2 = pointCache[insertIndex].appliedImpulseLateral2;

		assert lifeTime >= 0;
		Object cache = pointCache[insertIndex].userPersistentData;

		pointCache[insertIndex].set(newPoint);

		pointCache[insertIndex].userPersistentData = cache;
		pointCache[insertIndex].appliedImpulse = appliedImpulse;
		pointCache[insertIndex].appliedImpulseLateral1 = appliedLateralImpulse1;
		pointCache[insertIndex].appliedImpulseLateral2 = appliedLateralImpulse2;

		pointCache[insertIndex].lifeTime = lifeTime;
	}

	/**
	 * Valid contact distance.
	 *
	 * @param pt the pt
	 * @return true, if successful
	 */
	private boolean validContactDistance(final ManifoldPoint pt) {
		return pt.distance1 <= getContactBreakingThreshold();
	}

	/**
	 * Refresh contact points.
	 *
	 * @param trA the tr A
	 * @param trB the tr B
	 */
	/// calculated new worldspace coordinates and depth, and reject points that exceed the collision margin
	public void refreshContactPoints(final Transform trA, final Transform trB) {
		Vector3f tmp = VECTORS.get();
		Vector3f projectedDifference = VECTORS.get(), projectedPoint = VECTORS.get();
		int i;
		// first refresh worldspace positions and distance
		for (i = getNumContacts() - 1; i >= 0; i--) {
			ManifoldPoint manifoldPoint = pointCache[i];
			manifoldPoint.positionWorldOnA.set(manifoldPoint.localPointA);
			trA.transform(manifoldPoint.positionWorldOnA);
			manifoldPoint.positionWorldOnB.set(manifoldPoint.localPointB);
			trB.transform(manifoldPoint.positionWorldOnB);
			tmp.set(manifoldPoint.positionWorldOnA);
			tmp.sub(manifoldPoint.positionWorldOnB);
			manifoldPoint.distance1 = tmp.dot(manifoldPoint.normalWorldOnB);
			manifoldPoint.lifeTime++;
		}

		// then
		float distance2d;

		for (i = getNumContacts() - 1; i >= 0; i--) {

			ManifoldPoint manifoldPoint = pointCache[i];
			// contact becomes invalid when signed distance exceeds margin (projected on contactnormal direction)
			if (!validContactDistance(manifoldPoint)) {
				removeContactPoint(i);
			} else {
				// contact also becomes invalid when relative movement orthogonal to normal exceeds margin
				tmp.scale(manifoldPoint.distance1, manifoldPoint.normalWorldOnB);
				projectedPoint.sub(manifoldPoint.positionWorldOnA, tmp);
				projectedDifference.sub(manifoldPoint.positionWorldOnB, projectedPoint);
				distance2d = projectedDifference.dot(projectedDifference);
				if (distance2d > getContactBreakingThreshold() * getContactBreakingThreshold()) {
					removeContactPoint(i);
				} else {
					// contact point processed callback
					// if (info.world.getContactProcessedCallback() != null) {
					// info.world.getContactProcessedCallback().contactProcessed(manifoldPoint, body0, body1);
					// }
				}
			}
		}
		VECTORS.release(tmp, projectedDifference, projectedPoint);
	}

	// public void clearManifold() {
	// int i;
	// for (i = 0; i < cachedPoints; i++) {
	// clearUserCache( pointCache[i]);
	// }
	// cachedPoints = 0;
	// }

}
