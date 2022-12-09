/*******************************************************************************************************
 *
 * CollisionWorld.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.QUATS;
import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.BroadphaseProxy;
import com.bulletphysics.collision.broadphase.CollisionFilterGroups;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.broadphase.OverlappingPairCache;
import com.bulletphysics.collision.narrowphase.ConvexCast;
import com.bulletphysics.collision.narrowphase.ConvexCast.CastResult;
import com.bulletphysics.collision.narrowphase.GjkConvexCast;
import com.bulletphysics.collision.narrowphase.SubsimplexConvexCast;
import com.bulletphysics.collision.narrowphase.TriangleConvexcastCallback;
import com.bulletphysics.collision.narrowphase.TriangleRaycastCallback;
import com.bulletphysics.collision.narrowphase.VoronoiSimplexSolver;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.ConcaveShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.AabbUtil2;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.TransformUtil;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * CollisionWorld is interface and container for the collision detection.
 *
 * @author jezek2
 */
public class CollisionWorld {

	// protected final BulletStack stack = BulletStack.get();

	/** The collision objects. */
	protected List<CollisionObject> collisionObjects = new ArrayList<>();
	
	/** The dispatcher 1. */
	protected Dispatcher dispatcher1;
	
	/** The dispatch info. */
	protected DispatcherInfo dispatchInfo = new DispatcherInfo();
	
	/** The broadphase pair cache. */
	protected BroadphaseInterface broadphasePairCache;

	/**
	 * This constructor doesn't own the dispatcher and paircache/broadphase.
	 */
	public CollisionWorld(final Dispatcher dispatcher, final BroadphaseInterface broadphasePairCache,
			final CollisionConfiguration collisionConfiguration) {
		this.dispatcher1 = dispatcher;
		this.broadphasePairCache = broadphasePairCache;
	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		// clean up remaining objects
		for (CollisionObject collisionObject : collisionObjects) {
			BroadphaseProxy bp = collisionObject.getBroadphaseHandle();
			if (bp != null) {
				//
				// only clear the cached algorithms
				//
				getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(bp, dispatcher1);
				getBroadphase().destroyProxy(bp, dispatcher1);
			}
		}
	}

	/**
	 * Adds the collision object.
	 *
	 * @param collisionObject the collision object
	 */
	public void addCollisionObject(final CollisionObject collisionObject) {
		addCollisionObject(collisionObject, CollisionFilterGroups.DEFAULT_FILTER, CollisionFilterGroups.ALL_FILTER);
	}

	/**
	 * Adds the collision object.
	 *
	 * @param collisionObject the collision object
	 * @param collisionFilterGroup the collision filter group
	 * @param collisionFilterMask the collision filter mask
	 */
	public void addCollisionObject(final CollisionObject collisionObject, final short collisionFilterGroup,
			final short collisionFilterMask) {
		// check that the object isn't already added
		assert !collisionObjects.contains(collisionObject);

		collisionObjects.add(collisionObject);

		// calculate new AABB
		// TODO: check if it's overwritten or not
		Transform trans = collisionObject.getWorldTransform(TRANSFORMS.get());

		Vector3f minAabb = VECTORS.get();
		Vector3f maxAabb = VECTORS.get();
		collisionObject.getCollisionShape().getAabb(trans, minAabb, maxAabb);

		BroadphaseNativeType type = collisionObject.getCollisionShape().getShapeType();
		collisionObject.setBroadphaseHandle(getBroadphase().createProxy(minAabb, maxAabb, type, collisionObject,
				collisionFilterGroup, collisionFilterMask, dispatcher1, null));
		VECTORS.release(minAabb, maxAabb);
		TRANSFORMS.release(trans);
	}

	/**
	 * Perform discrete collision detection.
	 */
	public void performDiscreteCollisionDetection() {
		// BulletStats.pushProfile("performDiscreteCollisionDetection");
		// try {
		// DispatcherInfo dispatchInfo = getDispatchInfo();

		updateAabbs();

		// BulletStats.pushProfile("calculateOverlappingPairs");
		// try {
		broadphasePairCache.calculateOverlappingPairs(dispatcher1);
		// } finally {
		// BulletStats.popProfile();
		// }

		Dispatcher dispatcher = getDispatcher();
		{
			// BulletStats.pushProfile("dispatchAllCollisionPairs");
			// try {
			if (dispatcher != null) {
				dispatcher.dispatchAllCollisionPairs(broadphasePairCache.getOverlappingPairCache(), dispatchInfo,
						dispatcher1);
			}
			// } finally {
			// BulletStats.popProfile();
			// }
		}
		// } finally {
		// BulletStats.popProfile();
		// }
	}

	/**
	 * Removes the collision object.
	 *
	 * @param collisionObject the collision object
	 */
	public void removeCollisionObject(final CollisionObject collisionObject) {
		// bool removeFromBroadphase = false;

		{
			BroadphaseProxy bp = collisionObject.getBroadphaseHandle();
			if (bp != null) {
				//
				// only clear the cached algorithms
				//
				getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(bp, dispatcher1);
				getBroadphase().destroyProxy(bp, dispatcher1);
				collisionObject.setBroadphaseHandle(null);
			}
		}

		// swapremove
		collisionObjects.remove(collisionObject);
	}

	/**
	 * Sets the broadphase.
	 *
	 * @param pairCache the new broadphase
	 */
	public void setBroadphase(final BroadphaseInterface pairCache) {
		broadphasePairCache = pairCache;
	}

	/**
	 * Gets the broadphase.
	 *
	 * @return the broadphase
	 */
	public BroadphaseInterface getBroadphase() {
		return broadphasePairCache;
	}

	/**
	 * Gets the pair cache.
	 *
	 * @return the pair cache
	 */
	public OverlappingPairCache getPairCache() {
		return broadphasePairCache.getOverlappingPairCache();
	}

	/**
	 * Gets the dispatcher.
	 *
	 * @return the dispatcher
	 */
	public Dispatcher getDispatcher() {
		return dispatcher1;
	}

	/**
	 * Gets the dispatch info.
	 *
	 * @return the dispatch info
	 */
	public DispatcherInfo getDispatchInfo() {
		return dispatchInfo;
	}

	/** The update aabbs report me. */
	private static boolean updateAabbs_reportMe = true;

	/**
	 * Update single aabb.
	 *
	 * @param colObj the col obj
	 */
	// JAVA NOTE: ported from 2.74, missing contact threshold stuff
	public void updateSingleAabb(final CollisionObject colObj) {
		Vector3f minAabb = VECTORS.get(), maxAabb = VECTORS.get();
		Vector3f tmp = VECTORS.get();
		Transform tmpTrans = TRANSFORMS.get();

		colObj.getCollisionShape().getAabb(colObj.getWorldTransform(tmpTrans), minAabb, maxAabb);
		// need to increase the aabb for contact thresholds
		Vector3f contactThreshold = VECTORS.get();
		contactThreshold.set(BulletGlobals.getContactBreakingThreshold(), BulletGlobals.getContactBreakingThreshold(),
				BulletGlobals.getContactBreakingThreshold());
		minAabb.sub(contactThreshold);
		maxAabb.add(contactThreshold);

		BroadphaseInterface bp = broadphasePairCache;

		// moving objects should be moderately sized, probably something wrong if not
		tmp.sub(maxAabb, minAabb); // TODO: optimize
		if (colObj.isStaticObject() || tmp.lengthSquared() < 1e12f) {
			bp.setAabb(colObj.getBroadphaseHandle(), minAabb, maxAabb, dispatcher1);
		} else {
			// something went wrong, investigate
			// this assert is unwanted in 3D modelers (danger of loosing work)
			colObj.setActivationState(CollisionObject.DISABLE_SIMULATION);

		}
		VECTORS.release(contactThreshold, minAabb, maxAabb, tmp);
		TRANSFORMS.release(tmpTrans);
	}

	/**
	 * Update aabbs.
	 */
	public void updateAabbs() {
		// BulletStats.pushProfile("updateAabbs");
		// try {
		for (CollisionObject colObj : collisionObjects) {
			// only update aabb of active objects
			if (colObj.isActive()) { updateSingleAabb(colObj); }
		}
		// } finally {
		// BulletStats.popProfile();
		// }
	}

	/**
	 * Gets the num collision objects.
	 *
	 * @return the num collision objects
	 */
	public int getNumCollisionObjects() {
		return collisionObjects.size();
	}

	/**
	 * Ray test single.
	 *
	 * @param rayFromTrans the ray from trans
	 * @param rayToTrans the ray to trans
	 * @param collisionObject the collision object
	 * @param collisionShape the collision shape
	 * @param colObjWorldTransform the col obj world transform
	 * @param resultCallback the result callback
	 */
	// TODO
	public static void rayTestSingle(final Transform rayFromTrans, final Transform rayToTrans,
			final CollisionObject collisionObject, final CollisionShape collisionShape,
			final Transform colObjWorldTransform, final RayResultCallback resultCallback) {
		SphereShape pointShape = new SphereShape(0f);
		pointShape.setMargin(0f);
		ConvexShape castShape = pointShape;

		if (collisionShape.isConvex()) {
			CastResult castResult = new CastResult();
			castResult.fraction = resultCallback.closestHitFraction;

			ConvexShape convexShape = (ConvexShape) collisionShape;
			VoronoiSimplexSolver simplexSolver = new VoronoiSimplexSolver();

			// #define USE_SUBSIMPLEX_CONVEX_CAST 1
			// #ifdef USE_SUBSIMPLEX_CONVEX_CAST
			SubsimplexConvexCast convexCaster = new SubsimplexConvexCast(castShape, convexShape, simplexSolver);
			// #else
			// btGjkConvexCast convexCaster(castShape,convexShape,&simplexSolver);
			// btContinuousConvexCollision convexCaster(castShape,convexShape,&simplexSolver,0);
			// #endif //#USE_SUBSIMPLEX_CONVEX_CAST

			if (convexCaster.calcTimeOfImpact(rayFromTrans, rayToTrans, colObjWorldTransform, colObjWorldTransform,
					castResult)) {
				// add hit
				if (castResult.normal.lengthSquared() > 0.0001f) {
					if (castResult.fraction < resultCallback.closestHitFraction) {
						// #ifdef USE_SUBSIMPLEX_CONVEX_CAST
						// rotate normal into worldspace
						rayFromTrans.basis.transform(castResult.normal);
						// #endif //USE_SUBSIMPLEX_CONVEX_CAST

						castResult.normal.normalize();
						LocalRayResult localRayResult =
								new LocalRayResult(collisionObject, null, castResult.normal, castResult.fraction);

						boolean normalInWorldSpace = true;
						resultCallback.addSingleResult(localRayResult, normalInWorldSpace);
					}
				}
			}
		} else {
			if (collisionShape.isConcave()) {
				if (collisionShape.getShapeType() == BroadphaseNativeType.TRIANGLE_MESH_SHAPE_PROXYTYPE) {
					// optimized version for BvhTriangleMeshShape
					BvhTriangleMeshShape triangleMesh = (BvhTriangleMeshShape) collisionShape;
					Transform worldTocollisionObject = TRANSFORMS.get();
					worldTocollisionObject.inverse(colObjWorldTransform);
					Vector3f rayFromLocal = VECTORS.get(rayFromTrans.origin);
					worldTocollisionObject.transform(rayFromLocal);
					Vector3f rayToLocal = VECTORS.get(rayToTrans.origin);
					worldTocollisionObject.transform(rayToLocal);

					BridgeTriangleRaycastCallback rcb = new BridgeTriangleRaycastCallback(rayFromLocal, rayToLocal,
							resultCallback, collisionObject, triangleMesh);
					rcb.hitFraction = resultCallback.closestHitFraction;
					triangleMesh.performRaycast(rcb, rayFromLocal, rayToLocal);
					VECTORS.release(rayFromLocal, rayToLocal);
					TRANSFORMS.release(worldTocollisionObject);
				} else {
					ConcaveShape triangleMesh = (ConcaveShape) collisionShape;

					Transform worldTocollisionObject = TRANSFORMS.get();
					worldTocollisionObject.inverse(colObjWorldTransform);

					Vector3f rayFromLocal = VECTORS.get(rayFromTrans.origin);
					worldTocollisionObject.transform(rayFromLocal);
					Vector3f rayToLocal = VECTORS.get(rayToTrans.origin);
					worldTocollisionObject.transform(rayToLocal);

					BridgeTriangleRaycastCallback rcb = new BridgeTriangleRaycastCallback(rayFromLocal, rayToLocal,
							resultCallback, collisionObject, triangleMesh);
					rcb.hitFraction = resultCallback.closestHitFraction;

					Vector3f rayAabbMinLocal = VECTORS.get(rayFromLocal);
					VectorUtil.setMin(rayAabbMinLocal, rayToLocal);
					Vector3f rayAabbMaxLocal = VECTORS.get(rayFromLocal);
					VectorUtil.setMax(rayAabbMaxLocal, rayToLocal);

					triangleMesh.processAllTriangles(rcb, rayAabbMinLocal, rayAabbMaxLocal);
					VECTORS.release(rayFromLocal, rayToLocal, rayAabbMinLocal, rayAabbMaxLocal);
					TRANSFORMS.release(worldTocollisionObject);
				}
			} else {
				// todo: use AABB tree or other BVH acceleration structure!
				if (collisionShape.isCompound()) {
					CompoundShape compoundShape = (CompoundShape) collisionShape;
					int i = 0;
					Transform childTrans = TRANSFORMS.get();
					for (i = 0; i < compoundShape.getNumChildShapes(); i++) {
						compoundShape.getChildTransform(i, childTrans);
						CollisionShape childCollisionShape = compoundShape.getChildShape(i);
						Transform childWorldTrans = TRANSFORMS.get(colObjWorldTransform);
						childWorldTrans.mul(childTrans);
						// replace collision shape so that callback can determine the triangle
						CollisionShape saveCollisionShape = collisionObject.getCollisionShape();
						collisionObject.internalSetTemporaryCollisionShape(childCollisionShape);
						rayTestSingle(rayFromTrans, rayToTrans, collisionObject, childCollisionShape, childWorldTrans,
								resultCallback);
						// restore
						collisionObject.internalSetTemporaryCollisionShape(saveCollisionShape);
						TRANSFORMS.release(childWorldTrans);

					}
					TRANSFORMS.release(childTrans);
				}
			}
		}
	}

	/**
	 * The Class BridgeTriangleConvexcastCallback.
	 */
	private static class BridgeTriangleConvexcastCallback extends TriangleConvexcastCallback {
		
		/** The result callback. */
		public ConvexResultCallback resultCallback;
		
		/** The collision object. */
		public CollisionObject collisionObject;
		
		/** The normal in world space. */
		// public ConcaveShape triangleMesh;
		public boolean normalInWorldSpace;

		/**
		 * Instantiates a new bridge triangle convexcast callback.
		 *
		 * @param castShape the cast shape
		 * @param from the from
		 * @param to the to
		 * @param resultCallback the result callback
		 * @param collisionObject the collision object
		 * @param triangleMesh the triangle mesh
		 * @param triangleToWorld the triangle to world
		 */
		public BridgeTriangleConvexcastCallback(final ConvexShape castShape, final Transform from, final Transform to,
				final ConvexResultCallback resultCallback, final CollisionObject collisionObject,
				final ConcaveShape triangleMesh, final Transform triangleToWorld) {
			super(castShape, from, to, triangleToWorld, triangleMesh.getMargin());
			this.resultCallback = resultCallback;
			this.collisionObject = collisionObject;
			// this.triangleMesh = triangleMesh;
		}

		@Override
		public float reportHit(final Vector3f hitNormalLocal, final Vector3f hitPointLocal, final float hitFraction,
				final int partId, final int triangleIndex) {
			LocalShapeInfo shapeInfo = new LocalShapeInfo();
			shapeInfo.shapePart = partId;
			shapeInfo.triangleIndex = triangleIndex;
			if (hitFraction <= resultCallback.closestHitFraction) {
				LocalConvexResult convexResult =
						new LocalConvexResult(collisionObject, shapeInfo, hitNormalLocal, hitPointLocal, hitFraction);
				return resultCallback.addSingleResult(convexResult, normalInWorldSpace);
			}
			return hitFraction;
		}
	}

	/**
	 * objectQuerySingle performs a collision detection query and calls the resultCallback. It is used internally by
	 * rayTest.
	 */
	public static void objectQuerySingle(final ConvexShape castShape, final Transform convexFromTrans,
			final Transform convexToTrans, final CollisionObject collisionObject, final CollisionShape collisionShape,
			final Transform colObjWorldTransform, final ConvexResultCallback resultCallback,
			final float allowedPenetration) {
		if (collisionShape.isConvex()) {
			CastResult castResult = new CastResult();
			castResult.allowedPenetration = allowedPenetration;
			castResult.fraction = 1f; // ??

			ConvexShape convexShape = (ConvexShape) collisionShape;
			VoronoiSimplexSolver simplexSolver = new VoronoiSimplexSolver();
			// GjkEpaPenetrationDepthSolver gjkEpaPenetrationSolver = new GjkEpaPenetrationDepthSolver();

			// JAVA TODO: should be convexCaster1
			// ContinuousConvexCollision convexCaster1(castShape,convexShape,&simplexSolver,&gjkEpaPenetrationSolver);
			GjkConvexCast convexCaster2 = new GjkConvexCast(castShape, convexShape, simplexSolver);
			// btSubsimplexConvexCast convexCaster3(castShape,convexShape,&simplexSolver);

			ConvexCast castPtr = convexCaster2;

			if (castPtr.calcTimeOfImpact(convexFromTrans, convexToTrans, colObjWorldTransform, colObjWorldTransform,
					castResult)) {
				// add hit
				if (castResult.normal.lengthSquared() > 0.0001f) {
					if (castResult.fraction < resultCallback.closestHitFraction) {
						castResult.normal.normalize();
						LocalConvexResult localConvexResult = new LocalConvexResult(collisionObject, null,
								castResult.normal, castResult.hitPoint, castResult.fraction);

						boolean normalInWorldSpace = true;
						resultCallback.addSingleResult(localConvexResult, normalInWorldSpace);
					}
				}
			}
		} else {
			if (collisionShape.isConcave()) {
				if (collisionShape.getShapeType() == BroadphaseNativeType.TRIANGLE_MESH_SHAPE_PROXYTYPE) {
					BvhTriangleMeshShape triangleMesh = (BvhTriangleMeshShape) collisionShape;
					Transform worldTocollisionObject = TRANSFORMS.get();
					worldTocollisionObject.inverse(colObjWorldTransform);

					Vector3f convexFromLocal = VECTORS.get(convexFromTrans.origin);
					worldTocollisionObject.transform(convexFromLocal);

					Vector3f convexToLocal = VECTORS.get(convexToTrans.origin);
					worldTocollisionObject.transform(convexToLocal);

					// rotation of box in local mesh space = MeshRotation^-1 * ConvexToRotation
					Transform rotationXform = TRANSFORMS.get();
					Matrix3f tmpMat = MATRICES.get();
					tmpMat.mul(worldTocollisionObject.basis, convexToTrans.basis);
					rotationXform.set(tmpMat);

					BridgeTriangleConvexcastCallback tccb =
							new BridgeTriangleConvexcastCallback(castShape, convexFromTrans, convexToTrans,
									resultCallback, collisionObject, triangleMesh, colObjWorldTransform);
					tccb.hitFraction = resultCallback.closestHitFraction;
					tccb.normalInWorldSpace = true;

					Vector3f boxMinLocal = VECTORS.get();
					Vector3f boxMaxLocal = VECTORS.get();
					castShape.getAabb(rotationXform, boxMinLocal, boxMaxLocal);
					triangleMesh.performConvexcast(tccb, convexFromLocal, convexToLocal, boxMinLocal, boxMaxLocal);
					VECTORS.release(convexFromLocal, convexToLocal, boxMinLocal, boxMaxLocal);
					TRANSFORMS.release(rotationXform, worldTocollisionObject);
					MATRICES.release(tmpMat);
				} else {
					ConcaveShape triangleMesh = (ConcaveShape) collisionShape;
					Transform worldTocollisionObject = TRANSFORMS.get();
					worldTocollisionObject.inverse(colObjWorldTransform);

					Vector3f convexFromLocal = VECTORS.get(convexFromTrans.origin);
					worldTocollisionObject.transform(convexFromLocal);

					Vector3f convexToLocal = VECTORS.get(convexToTrans.origin);
					worldTocollisionObject.transform(convexToLocal);

					// rotation of box in local mesh space = MeshRotation^-1 * ConvexToRotation
					Transform rotationXform = TRANSFORMS.get();
					Matrix3f tmpMat = MATRICES.get();
					tmpMat.mul(worldTocollisionObject.basis, convexToTrans.basis);
					rotationXform.set(tmpMat);

					BridgeTriangleConvexcastCallback tccb =
							new BridgeTriangleConvexcastCallback(castShape, convexFromTrans, convexToTrans,
									resultCallback, collisionObject, triangleMesh, colObjWorldTransform);
					tccb.hitFraction = resultCallback.closestHitFraction;
					tccb.normalInWorldSpace = false;
					Vector3f boxMinLocal = VECTORS.get();
					Vector3f boxMaxLocal = VECTORS.get();
					castShape.getAabb(rotationXform, boxMinLocal, boxMaxLocal);

					Vector3f rayAabbMinLocal = VECTORS.get(convexFromLocal);
					VectorUtil.setMin(rayAabbMinLocal, convexToLocal);
					Vector3f rayAabbMaxLocal = VECTORS.get(convexFromLocal);
					VectorUtil.setMax(rayAabbMaxLocal, convexToLocal);
					rayAabbMinLocal.add(boxMinLocal);
					rayAabbMaxLocal.add(boxMaxLocal);
					triangleMesh.processAllTriangles(tccb, rayAabbMinLocal, rayAabbMaxLocal);
					VECTORS.release(convexFromLocal, convexToLocal, rayAabbMaxLocal, rayAabbMinLocal, boxMinLocal,
							boxMaxLocal);
					TRANSFORMS.release(rotationXform, worldTocollisionObject);
					MATRICES.release(tmpMat);
				}
			} else {
				// todo: use AABB tree or other BVH acceleration structure!
				if (collisionShape.isCompound()) {
					CompoundShape compoundShape = (CompoundShape) collisionShape;
					for (int i = 0; i < compoundShape.getNumChildShapes(); i++) {
						Transform childTrans = compoundShape.getChildTransform(i, TRANSFORMS.get());
						CollisionShape childCollisionShape = compoundShape.getChildShape(i);
						Transform childWorldTrans = TRANSFORMS.get();
						childWorldTrans.mul(colObjWorldTransform, childTrans);
						// replace collision shape so that callback can determine the triangle
						CollisionShape saveCollisionShape = collisionObject.getCollisionShape();
						collisionObject.internalSetTemporaryCollisionShape(childCollisionShape);
						objectQuerySingle(castShape, convexFromTrans, convexToTrans, collisionObject,
								childCollisionShape, childWorldTrans, resultCallback, allowedPenetration);
						// restore
						collisionObject.internalSetTemporaryCollisionShape(saveCollisionShape);
						TRANSFORMS.release(childTrans, childWorldTrans);
					}
				}
			}
		}
	}

	/**
	 * rayTest performs a raycast on all objects in the CollisionWorld, and calls the resultCallback. This allows for
	 * several queries: first hit, all hits, any hit, dependent on the value returned by the callback.
	 */
	public void rayTest(final Vector3f rayFromWorld, final Vector3f rayToWorld,
			final RayResultCallback resultCallback) {
		Transform rayFromTrans = TRANSFORMS.get(), rayToTrans = TRANSFORMS.get();
		rayFromTrans.setIdentity();
		rayFromTrans.origin.set(rayFromWorld);
		rayToTrans.setIdentity();

		rayToTrans.origin.set(rayToWorld);

		// go over all objects, and if the ray intersects their aabb, do a ray-shape query using convexCaster (CCD)
		Vector3f collisionObjectAabbMin = VECTORS.get(), collisionObjectAabbMax = VECTORS.get();
		float[] hitLambda = new float[1];

		Transform tmpTrans = TRANSFORMS.get();

		for (CollisionObject collisionObject : collisionObjects) {
			// terminate further ray tests, once the closestHitFraction reached zero
			if (resultCallback.closestHitFraction == 0f) { break; }

			// only perform raycast if filterMask matches
			if (resultCallback.needsCollision(collisionObject.getBroadphaseHandle())) {
				// RigidcollisionObject* collisionObject = ctrl->GetRigidcollisionObject();
				collisionObject.getCollisionShape().getAabb(collisionObject.getWorldTransform(tmpTrans),
						collisionObjectAabbMin, collisionObjectAabbMax);

				hitLambda[0] = resultCallback.closestHitFraction;
				Vector3f hitNormal = VECTORS.get();
				if (AabbUtil2.rayAabb(rayFromWorld, rayToWorld, collisionObjectAabbMin, collisionObjectAabbMax,
						hitLambda, hitNormal)) {
					rayTestSingle(rayFromTrans, rayToTrans, collisionObject, collisionObject.getCollisionShape(),
							collisionObject.getWorldTransform(tmpTrans), resultCallback);
				}
				VECTORS.release(hitNormal);
			}

		}
		VECTORS.release(collisionObjectAabbMin, collisionObjectAabbMax);
		TRANSFORMS.release(rayFromTrans, rayToTrans, tmpTrans);
	}

	/**
	 * convexTest performs a swept convex cast on all objects in the {@link CollisionWorld}, and calls the
	 * resultCallback This allows for several queries: first hit, all hits, any hit, dependent on the value return by
	 * the callback.
	 */
	public void convexSweepTest(final ConvexShape castShape, final Transform convexFromWorld,
			final Transform convexToWorld, final ConvexResultCallback resultCallback) {
		Transform convexFromTrans = TRANSFORMS.get();
		Transform convexToTrans = TRANSFORMS.get();

		convexFromTrans.set(convexFromWorld);
		convexToTrans.set(convexToWorld);

		Vector3f castShapeAabbMin = VECTORS.get();
		Vector3f castShapeAabbMax = VECTORS.get();

		// Compute AABB that encompasses angular movement
		{
			Vector3f linVel = VECTORS.get();
			Vector3f angVel = VECTORS.get();
			TransformUtil.calculateVelocity(convexFromTrans, convexToTrans, 1f, linVel, angVel);
			Transform R = TRANSFORMS.get();
			R.setIdentity();
			Quat4f qmp = convexFromTrans.getRotation(QUATS.get());
			R.setRotation(qmp);
			castShape.calculateTemporalAabb(R, linVel, angVel, 1f, castShapeAabbMin, castShapeAabbMax);
			VECTORS.release(linVel, angVel);
			QUATS.release(qmp);
			TRANSFORMS.release(R);
		}

		Transform tmpTrans = TRANSFORMS.get();
		Vector3f collisionObjectAabbMin = VECTORS.get();
		Vector3f collisionObjectAabbMax = VECTORS.get();
		float[] hitLambda = new float[1];

		// go over all objects, and if the ray intersects their aabb + cast shape aabb,
		// do a ray-shape query using convexCaster (CCD)
		for (CollisionObject collisionObject : collisionObjects) {
			// only perform raycast if filterMask matches
			if (resultCallback.needsCollision(collisionObject.getBroadphaseHandle())) {
				// RigidcollisionObject* collisionObject = ctrl->GetRigidcollisionObject();
				collisionObject.getWorldTransform(tmpTrans);
				collisionObject.getCollisionShape().getAabb(tmpTrans, collisionObjectAabbMin, collisionObjectAabbMax);
				AabbUtil2.aabbExpand(collisionObjectAabbMin, collisionObjectAabbMax, castShapeAabbMin,
						castShapeAabbMax);
				hitLambda[0] = 1f; // could use resultCallback.closestHitFraction, but needs testing
				Vector3f hitNormal = VECTORS.get();
				if (AabbUtil2.rayAabb(convexFromWorld.origin, convexToWorld.origin, collisionObjectAabbMin,
						collisionObjectAabbMax, hitLambda, hitNormal)) {
					objectQuerySingle(castShape, convexFromTrans, convexToTrans, collisionObject,
							collisionObject.getCollisionShape(), tmpTrans, resultCallback,
							getDispatchInfo().allowedCcdPenetration);
				}
				VECTORS.release(hitNormal);
			}
		}
		VECTORS.release(castShapeAabbMin, castShapeAabbMax, collisionObjectAabbMin, collisionObjectAabbMax);
		TRANSFORMS.release(convexFromTrans, convexToTrans, tmpTrans);
	}

	/**
	 * Gets the collision object array.
	 *
	 * @return the collision object array
	 */
	public List<CollisionObject> getCollisionObjectArray() {
		return collisionObjects;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * LocalShapeInfo gives extra information for complex shapes. Currently, only btTriangleMeshShape is available, so
	 * it just contains triangleIndex and subpart.
	 */
	public static class LocalShapeInfo {
		
		/** The shape part. */
		public int shapePart;
		
		/** The triangle index. */
		public int triangleIndex;
		// const btCollisionShape* m_shapeTemp;
		// const btTransform* m_shapeLocalTransform;
	}

	/**
	 * The Class LocalRayResult.
	 */
	public static class LocalRayResult {
		
		/** The collision object. */
		public CollisionObject collisionObject;
		
		/** The local shape info. */
		public LocalShapeInfo localShapeInfo;
		
		/** The hit normal local. */
		public final Vector3f hitNormalLocal = new Vector3f();
		
		/** The hit fraction. */
		public float hitFraction;

		/**
		 * Instantiates a new local ray result.
		 *
		 * @param collisionObject the collision object
		 * @param localShapeInfo the local shape info
		 * @param hitNormalLocal the hit normal local
		 * @param hitFraction the hit fraction
		 */
		public LocalRayResult(final CollisionObject collisionObject, final LocalShapeInfo localShapeInfo,
				final Vector3f hitNormalLocal, final float hitFraction) {
			this.collisionObject = collisionObject;
			this.localShapeInfo = localShapeInfo;
			this.hitNormalLocal.set(hitNormalLocal);
			this.hitFraction = hitFraction;
		}
	}

	/**
	 * RayResultCallback is used to report new raycast results.
	 */
	public static abstract class RayResultCallback {
		
		/** The closest hit fraction. */
		public float closestHitFraction = 1f;
		
		/** The collision object. */
		public CollisionObject collisionObject;
		
		/** The collision filter group. */
		public short collisionFilterGroup = CollisionFilterGroups.DEFAULT_FILTER;
		
		/** The collision filter mask. */
		public short collisionFilterMask = CollisionFilterGroups.ALL_FILTER;

		/**
		 * Checks for hit.
		 *
		 * @return true, if successful
		 */
		public boolean hasHit() {
			return collisionObject != null;
		}

		/**
		 * Needs collision.
		 *
		 * @param proxy0 the proxy 0
		 * @return true, if successful
		 */
		public boolean needsCollision(final BroadphaseProxy proxy0) {
			boolean collides = (proxy0.collisionFilterGroup & collisionFilterMask & 0xFFFF) != 0;
			collides = collides && (collisionFilterGroup & proxy0.collisionFilterMask & 0xFFFF) != 0;
			return collides;
		}

		/**
		 * Adds the single result.
		 *
		 * @param rayResult the ray result
		 * @param normalInWorldSpace the normal in world space
		 * @return the float
		 */
		public abstract float addSingleResult(LocalRayResult rayResult, boolean normalInWorldSpace);
	}

	/**
	 * The Class ClosestRayResultCallback.
	 */
	public static class ClosestRayResultCallback extends RayResultCallback {
		
		/** The ray from world. */
		public final Vector3f rayFromWorld = new Vector3f(); // used to calculate hitPointWorld from hitFraction
		
		/** The ray to world. */
		public final Vector3f rayToWorld = new Vector3f();

		/** The hit normal world. */
		public final Vector3f hitNormalWorld = new Vector3f();
		
		/** The hit point world. */
		public final Vector3f hitPointWorld = new Vector3f();

		/**
		 * Instantiates a new closest ray result callback.
		 *
		 * @param rayFromWorld the ray from world
		 * @param rayToWorld the ray to world
		 */
		public ClosestRayResultCallback(final Vector3f rayFromWorld, final Vector3f rayToWorld) {
			this.rayFromWorld.set(rayFromWorld);
			this.rayToWorld.set(rayToWorld);
		}

		@Override
		public float addSingleResult(final LocalRayResult rayResult, final boolean normalInWorldSpace) {
			// caller already does the filter on the closestHitFraction
			assert rayResult.hitFraction <= closestHitFraction;

			closestHitFraction = rayResult.hitFraction;
			collisionObject = rayResult.collisionObject;
			if (normalInWorldSpace) {
				hitNormalWorld.set(rayResult.hitNormalLocal);
			} else {
				// need to transform normal into worldspace
				hitNormalWorld.set(rayResult.hitNormalLocal);
				Transform tmp = collisionObject.getWorldTransform(TRANSFORMS.get());
				tmp.basis.transform(hitNormalWorld);
				TRANSFORMS.release(tmp);
			}

			VectorUtil.setInterpolate3(hitPointWorld, rayFromWorld, rayToWorld, rayResult.hitFraction);
			return rayResult.hitFraction;
		}
	}

	/**
	 * The Class LocalConvexResult.
	 */
	public static class LocalConvexResult {
		
		/** The hit collision object. */
		public CollisionObject hitCollisionObject;
		
		/** The local shape info. */
		public LocalShapeInfo localShapeInfo;
		
		/** The hit normal local. */
		public final Vector3f hitNormalLocal = new Vector3f();
		
		/** The hit point local. */
		public final Vector3f hitPointLocal = new Vector3f();
		
		/** The hit fraction. */
		public float hitFraction;

		/**
		 * Instantiates a new local convex result.
		 *
		 * @param hitCollisionObject the hit collision object
		 * @param localShapeInfo the local shape info
		 * @param hitNormalLocal the hit normal local
		 * @param hitPointLocal the hit point local
		 * @param hitFraction the hit fraction
		 */
		public LocalConvexResult(final CollisionObject hitCollisionObject, final LocalShapeInfo localShapeInfo,
				final Vector3f hitNormalLocal, final Vector3f hitPointLocal, final float hitFraction) {
			this.hitCollisionObject = hitCollisionObject;
			this.localShapeInfo = localShapeInfo;
			this.hitNormalLocal.set(hitNormalLocal);
			this.hitPointLocal.set(hitPointLocal);
			this.hitFraction = hitFraction;
		}
	}

	/**
	 * The Class ConvexResultCallback.
	 */
	public static abstract class ConvexResultCallback {
		
		/** The closest hit fraction. */
		public float closestHitFraction = 1f;
		
		/** The collision filter group. */
		public short collisionFilterGroup = CollisionFilterGroups.DEFAULT_FILTER;
		
		/** The collision filter mask. */
		public short collisionFilterMask = CollisionFilterGroups.ALL_FILTER;

		/**
		 * Checks for hit.
		 *
		 * @return true, if successful
		 */
		public boolean hasHit() {
			return closestHitFraction < 1f;
		}

		/**
		 * Needs collision.
		 *
		 * @param proxy0 the proxy 0
		 * @return true, if successful
		 */
		public boolean needsCollision(final BroadphaseProxy proxy0) {
			boolean collides = (proxy0.collisionFilterGroup & collisionFilterMask & 0xFFFF) != 0;
			collides = collides && (collisionFilterGroup & proxy0.collisionFilterMask & 0xFFFF) != 0;
			return collides;
		}

		/**
		 * Adds the single result.
		 *
		 * @param convexResult the convex result
		 * @param normalInWorldSpace the normal in world space
		 * @return the float
		 */
		public abstract float addSingleResult(LocalConvexResult convexResult, boolean normalInWorldSpace);
	}

	/**
	 * The Class ClosestConvexResultCallback.
	 */
	public static class ClosestConvexResultCallback extends ConvexResultCallback {
		
		/** The convex from world. */
		public final Vector3f convexFromWorld = new Vector3f(); // used to calculate hitPointWorld from hitFraction
		
		/** The convex to world. */
		public final Vector3f convexToWorld = new Vector3f();
		
		/** The hit normal world. */
		public final Vector3f hitNormalWorld = new Vector3f();
		
		/** The hit point world. */
		public final Vector3f hitPointWorld = new Vector3f();
		
		/** The hit collision object. */
		public CollisionObject hitCollisionObject;

		/**
		 * Instantiates a new closest convex result callback.
		 *
		 * @param convexFromWorld the convex from world
		 * @param convexToWorld the convex to world
		 */
		public ClosestConvexResultCallback(final Vector3f convexFromWorld, final Vector3f convexToWorld) {
			this.convexFromWorld.set(convexFromWorld);
			this.convexToWorld.set(convexToWorld);
			this.hitCollisionObject = null;
		}

		@Override
		public float addSingleResult(final LocalConvexResult convexResult, final boolean normalInWorldSpace) {
			// caller already does the filter on the m_closestHitFraction
			assert convexResult.hitFraction <= closestHitFraction;

			closestHitFraction = convexResult.hitFraction;
			hitCollisionObject = convexResult.hitCollisionObject;
			if (normalInWorldSpace) {
				hitNormalWorld.set(convexResult.hitNormalLocal);
				if (hitNormalWorld.length() > 2) {
					System.out.println("CollisionWorld.addSingleResult world " + hitNormalWorld);
				}
			} else {
				// need to transform normal into worldspace
				hitNormalWorld.set(convexResult.hitNormalLocal);
				Transform tmp = hitCollisionObject.getWorldTransform(TRANSFORMS.get());
				tmp.basis.transform(hitNormalWorld);
				TRANSFORMS.release(tmp);
				if (hitNormalWorld.length() > 2) {
					System.out.println("CollisionWorld.addSingleResult world " + hitNormalWorld);
				}
			}

			hitPointWorld.set(convexResult.hitPointLocal);
			return convexResult.hitFraction;
		}
	}

	/**
	 * The Class BridgeTriangleRaycastCallback.
	 */
	private static class BridgeTriangleRaycastCallback extends TriangleRaycastCallback {
		
		/** The result callback. */
		public RayResultCallback resultCallback;
		
		/** The collision object. */
		public CollisionObject collisionObject;
		// public ConcaveShape triangleMesh;

		/**
		 * Instantiates a new bridge triangle raycast callback.
		 *
		 * @param from the from
		 * @param to the to
		 * @param resultCallback the result callback
		 * @param collisionObject the collision object
		 * @param triangleMesh the triangle mesh
		 */
		public BridgeTriangleRaycastCallback(final Vector3f from, final Vector3f to,
				final RayResultCallback resultCallback, final CollisionObject collisionObject,
				final ConcaveShape triangleMesh) {
			super(from, to);
			this.resultCallback = resultCallback;
			this.collisionObject = collisionObject;
			// this.triangleMesh = triangleMesh;
		}

		@Override
		public float reportHit(final Vector3f hitNormalLocal, final float hitFraction, final int partId,
				final int triangleIndex) {
			LocalShapeInfo shapeInfo = new LocalShapeInfo();
			shapeInfo.shapePart = partId;
			shapeInfo.triangleIndex = triangleIndex;

			LocalRayResult rayResult = new LocalRayResult(collisionObject, shapeInfo, hitNormalLocal, hitFraction);

			boolean normalInWorldSpace = false;
			return resultCallback.addSingleResult(rayResult, normalInWorldSpace);
		}
	}

}
