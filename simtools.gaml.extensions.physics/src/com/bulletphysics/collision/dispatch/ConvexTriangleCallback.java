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

import static com.bulletphysics.Pools.TRANSFORMS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.CollisionAlgorithm;
import com.bulletphysics.collision.broadphase.CollisionAlgorithmConstructionInfo;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleCallback;
import com.bulletphysics.collision.shapes.TriangleShape;
import com.bulletphysics.linearmath.Transform;

/**
 * For each triangle in the concave mesh that overlaps with the AABB of a convex (see {@link #convexBody} field),
 * processTriangle is called.
 *
 * @author jezek2
 */
class ConvexTriangleCallback implements TriangleCallback {

	// protected final BulletStack stack = BulletStack.get();

	private final CollisionObject convexBody;
	private final CollisionObject triBody;

	private final Vector3f aabbMin = new Vector3f();
	private final Vector3f aabbMax = new Vector3f();

	private ManifoldResult resultOut;

	private final Dispatcher dispatcher;
	// private DispatcherInfo info;
	private float collisionMarginTriangle;

	public int triangleCount;
	public PersistentManifold manifoldPtr;

	public ConvexTriangleCallback(final Dispatcher dispatcher, final CollisionObject body0, final CollisionObject body1,
			final boolean isSwapped) {
		this.dispatcher = dispatcher;
		// this.info = null;

		convexBody = isSwapped ? body1 : body0;
		triBody = isSwapped ? body0 : body1;

		//
		// create the manifold from the dispatcher 'manifold pool'
		//
		manifoldPtr = dispatcher.getNewManifold(convexBody, triBody);

		// clearCache();
	}

	public void destroy() {
		// clearCache();
		dispatcher.releaseManifold(manifoldPtr);
	}

	public void setTimeStepAndCounters(final float collisionMarginTriangle, final ManifoldResult resultOut) {
		// this.info = dispatchInfo;
		this.collisionMarginTriangle = collisionMarginTriangle;
		this.resultOut = resultOut;

		// recalc aabbs
		Transform convexInTriangleSpace = TRANSFORMS.get();

		triBody.getWorldTransform(convexInTriangleSpace);
		convexInTriangleSpace.inverse();
		Transform tmpTrans = convexBody.getWorldTransform(TRANSFORMS.get());
		convexInTriangleSpace.mul(tmpTrans);

		CollisionShape convexShape = convexBody.getCollisionShape();
		// CollisionShape* triangleShape = static_cast<btCollisionShape*>(triBody->m_collisionShape);
		convexShape.getAabb(convexInTriangleSpace, aabbMin, aabbMax);
		float extraMargin = collisionMarginTriangle;
		Vector3f extra = VECTORS.get();
		extra.set(extraMargin, extraMargin, extraMargin);

		aabbMax.add(extra);
		aabbMin.sub(extra);
		VECTORS.release(extra);
		TRANSFORMS.release(tmpTrans, convexInTriangleSpace);

	}

	private final CollisionAlgorithmConstructionInfo ci = new CollisionAlgorithmConstructionInfo();
	private final TriangleShape tm = new TriangleShape();

	@Override
	public void processTriangle(final Vector3f[] triangle, final int partId, final int triangleIndex) {
		// just for debugging purposes
		// printf("triangle %d",m_triangleCount++);

		// aabb filter is already applied!

		ci.dispatcher1 = dispatcher;

		CollisionObject ob = triBody;

		// btCollisionObject* colObj = static_cast<btCollisionObject*>(m_convexProxy->m_clientObject);

		if (convexBody.getCollisionShape().isConvex()) {
			tm.init(triangle[0], triangle[1], triangle[2]);
			tm.setMargin(collisionMarginTriangle);

			CollisionShape tmpShape = ob.getCollisionShape();
			ob.internalSetTemporaryCollisionShape(tm);

			CollisionAlgorithm colAlgo = ci.dispatcher1.findAlgorithm(convexBody, triBody, manifoldPtr);
			// this should use the btDispatcher, so the actual registered algorithm is used
			// btConvexConvexAlgorithm cvxcvxalgo(m_manifoldPtr,ci,m_convexBody,m_triBody);

			resultOut.setShapeIdentifiers(-1, -1, partId, triangleIndex);
			// cvxcvxalgo.setShapeIdentifiers(-1,-1,partId,triangleIndex);
			// cvxcvxalgo.processCollision(m_convexBody,m_triBody,*m_dispatchInfoPtr,m_resultOut);
			colAlgo.processCollision(convexBody, triBody, resultOut);
			// colAlgo.destroy();
			ci.dispatcher1.freeCollisionAlgorithm(colAlgo);
			ob.internalSetTemporaryCollisionShape(tmpShape);
		}
	}

	// public void clearCache() {
	// dispatcher.clearManifold(manifoldPtr);
	// }

	public Vector3f getAabbMin(final Vector3f out) {
		out.set(aabbMin);
		return out;
	}

	public Vector3f getAabbMax(final Vector3f out) {
		out.set(aabbMax);
		return out;
	}

}
