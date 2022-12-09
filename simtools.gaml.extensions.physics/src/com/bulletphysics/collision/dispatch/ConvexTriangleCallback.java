/*******************************************************************************************************
 *
 * ConvexTriangleCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

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

	/** The convex body. */
	private final CollisionObject convexBody;
	
	/** The tri body. */
	private final CollisionObject triBody;

	/** The aabb min. */
	private final Vector3f aabbMin = new Vector3f();
	
	/** The aabb max. */
	private final Vector3f aabbMax = new Vector3f();

	/** The result out. */
	private ManifoldResult resultOut;

	/** The dispatcher. */
	private final Dispatcher dispatcher;
	
	/** The collision margin triangle. */
	// private DispatcherInfo info;
	private float collisionMarginTriangle;

	/** The triangle count. */
	public int triangleCount;
	
	/** The manifold ptr. */
	public PersistentManifold manifoldPtr;

	/**
	 * Instantiates a new convex triangle callback.
	 *
	 * @param dispatcher the dispatcher
	 * @param body0 the body 0
	 * @param body1 the body 1
	 * @param isSwapped the is swapped
	 */
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

	/**
	 * Destroy.
	 */
	public void destroy() {
		// clearCache();
		dispatcher.releaseManifold(manifoldPtr);
	}

	/**
	 * Sets the time step and counters.
	 *
	 * @param collisionMarginTriangle the collision margin triangle
	 * @param resultOut the result out
	 */
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

	/** The ci. */
	private final CollisionAlgorithmConstructionInfo ci = new CollisionAlgorithmConstructionInfo();
	
	/** The tm. */
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

	/**
	 * Gets the aabb min.
	 *
	 * @param out the out
	 * @return the aabb min
	 */
	public Vector3f getAabbMin(final Vector3f out) {
		out.set(aabbMin);
		return out;
	}

	/**
	 * Gets the aabb max.
	 *
	 * @param out the out
	 * @return the aabb max
	 */
	public Vector3f getAabbMax(final Vector3f out) {
		out.set(aabbMax);
		return out;
	}

}
