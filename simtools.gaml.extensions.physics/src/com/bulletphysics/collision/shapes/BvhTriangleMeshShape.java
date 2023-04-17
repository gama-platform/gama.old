/*******************************************************************************************************
 *
 * BvhTriangleMeshShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.VECTORS;
import static msi.gama.common.util.PoolUtils.create;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.linearmath.VectorUtil;

import msi.gama.common.util.PoolUtils.ObjectPool;

/**
 * BvhTriangleMeshShape is a static-triangle mesh shape with several optimizations, such as bounding volume hierarchy.
 * It is recommended to enable useQuantizedAabbCompression for better memory usage.
 * <p>
 *
 * It takes a triangle mesh as input, for example a {@link TriangleMesh} or {@link TriangleIndexVertexArray}. The
 * BvhTriangleMeshShape class allows for triangle mesh deformations by a refit or partialRefit method.
 * <p>
 *
 * Instead of building the bounding volume hierarchy acceleration structure, it is also possible to serialize (save) and
 * deserialize (load) the structure from disk. See ConcaveDemo for an example.
 *
 * @author jezek2
 */
public class BvhTriangleMeshShape extends TriangleMeshShape {

	/** The Constant NODE_OVERLAPS. */
	public static final ObjectPool<MyNodeOverlapCallback> NODE_OVERLAPS =
			create("MyNodeOverlapCallbacks", true, () -> new MyNodeOverlapCallback(), null, null);

	/** The bvh. */
	private OptimizedBvh bvh;
	
	/** The use quantized aabb compression. */
	private boolean useQuantizedAabbCompression;
	
	/** The owns bvh. */
	private boolean ownsBvh;

	/**
	 * Instantiates a new bvh triangle mesh shape.
	 */
	public BvhTriangleMeshShape() {
		super(null);
		this.bvh = null;
		this.ownsBvh = false;
	}

	/**
	 * Instantiates a new bvh triangle mesh shape.
	 *
	 * @param meshInterface the mesh interface
	 * @param useQuantizedAabbCompression the use quantized aabb compression
	 */
	public BvhTriangleMeshShape( final StridingMeshInterface meshInterface,
			final boolean useQuantizedAabbCompression) {
		this( meshInterface, useQuantizedAabbCompression, true);
	}

	/**
	 * Instantiates a new bvh triangle mesh shape.
	 *
	 * @param meshInterface the mesh interface
	 * @param useQuantizedAabbCompression the use quantized aabb compression
	 * @param buildBvh the build bvh
	 */
	public BvhTriangleMeshShape( final StridingMeshInterface meshInterface,
			final boolean useQuantizedAabbCompression, final boolean buildBvh) {
		super(meshInterface);
		this.bvh = null;
		this.useQuantizedAabbCompression = useQuantizedAabbCompression;
		this.ownsBvh = false;

		// construct bvh from meshInterface
		// #ifndef DISABLE_BVH

		Vector3f bvhAabbMin = new Vector3f(), bvhAabbMax = new Vector3f();
		meshInterface.calculateAabbBruteForce( bvhAabbMin, bvhAabbMax);

		if (buildBvh) {
			bvh = new OptimizedBvh();
			bvh.build( meshInterface, useQuantizedAabbCompression, bvhAabbMin, bvhAabbMax);
			ownsBvh = true;

			// JAVA NOTE: moved from TriangleMeshShape
			recalcLocalAabb();
		}

		// #endif //DISABLE_BVH
	}

	/**
	 * Optionally pass in a larger bvh aabb, used for quantization. This allows for deformations within this aabb.
	 */
	public BvhTriangleMeshShape( final StridingMeshInterface meshInterface,
			final boolean useQuantizedAabbCompression, final Vector3f bvhAabbMin, final Vector3f bvhAabbMax) {
		this( meshInterface, useQuantizedAabbCompression, bvhAabbMin, bvhAabbMax, true);
	}

	/**
	 * Optionally pass in a larger bvh aabb, used for quantization. This allows for deformations within this aabb.
	 */
	public BvhTriangleMeshShape( final StridingMeshInterface meshInterface,
			final boolean useQuantizedAabbCompression, final Vector3f bvhAabbMin, final Vector3f bvhAabbMax,
			final boolean buildBvh) {
		super(meshInterface);

		this.bvh = null;
		this.useQuantizedAabbCompression = useQuantizedAabbCompression;
		this.ownsBvh = false;

		// construct bvh from meshInterface
		// #ifndef DISABLE_BVH

		if (buildBvh) {
			bvh = new OptimizedBvh();

			bvh.build( meshInterface, useQuantizedAabbCompression, bvhAabbMin, bvhAabbMax);
			ownsBvh = true;
		}

		// JAVA NOTE: moved from TriangleMeshShape
		recalcLocalAabb();
		// #endif //DISABLE_BVH
	}

	/**
	 * Gets the owns bvh.
	 *
	 * @return the owns bvh
	 */
	public boolean getOwnsBvh() {
		return ownsBvh;
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.TRIANGLE_MESH_SHAPE_PROXYTYPE;
	}

	/**
	 * Perform raycast.
	 *
	 * @param callback the callback
	 * @param raySource the ray source
	 * @param rayTarget the ray target
	 */
	public void performRaycast( final TriangleCallback callback, final Vector3f raySource,
			final Vector3f rayTarget) {
		MyNodeOverlapCallback myNodeCallback = NODE_OVERLAPS.get();
		myNodeCallback.init(callback, meshInterface);

		bvh.reportRayOverlappingNodex( myNodeCallback, raySource, rayTarget);

		NODE_OVERLAPS.release(myNodeCallback);
	}

	/**
	 * Perform convexcast.
	 *
	 * @param callback the callback
	 * @param raySource the ray source
	 * @param rayTarget the ray target
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 */
	public void performConvexcast( final TriangleCallback callback, final Vector3f raySource,
			final Vector3f rayTarget, final Vector3f aabbMin, final Vector3f aabbMax) {
		MyNodeOverlapCallback myNodeCallback = NODE_OVERLAPS.get();
		myNodeCallback.init(callback, meshInterface);

		bvh.reportBoxCastOverlappingNodex( myNodeCallback, raySource, rayTarget, aabbMin, aabbMax);

		NODE_OVERLAPS.release(myNodeCallback);
	}

	/**
	 * Perform bvh tree traversal and report overlapping triangles to 'callback'.
	 */
	@Override
	public void processAllTriangles( final TriangleCallback callback, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		// #ifdef DISABLE_BVH
		// // brute force traverse all triangles
		// btTriangleMeshShape::processAllTriangles(callback,aabbMin,aabbMax);
		// #else

		// first get all the nodes
		MyNodeOverlapCallback myNodeCallback = NODE_OVERLAPS.get();
		myNodeCallback.init(callback, meshInterface);

		bvh.reportAabbOverlappingNodex( myNodeCallback, aabbMin, aabbMax);

		NODE_OVERLAPS.release(myNodeCallback);
		// #endif//DISABLE_BVH
	}

	/**
	 * Refit tree.
	 *
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 */
	public void refitTree( final Vector3f aabbMin, final Vector3f aabbMax) {
		// JAVA NOTE: update it for 2.70b1
		// bvh.refit(meshInterface, aabbMin, aabbMax);
		bvh.refit( meshInterface);

		recalcLocalAabb();
	}

	/**
	 * For a fast incremental refit of parts of the tree. Note: the entire AABB of the tree will become more
	 * conservative, it never shrinks.
	 */
	public void partialRefitTree(final Vector3f aabbMin, final Vector3f aabbMax) {
		bvh.refitPartial(meshInterface, aabbMin, aabbMax);

		VectorUtil.setMin(localAabbMin, aabbMin);
		VectorUtil.setMax(localAabbMax, aabbMax);
	}

	@Override
	public String getName() {
		return "BVHTRIANGLEMESH";
	}

	@Override
	public void setLocalScaling( final Vector3f scaling) {
		Vector3f tmp = VECTORS.get();
		Vector3f tmp2 = getLocalScaling(VECTORS.get());
		tmp.sub(tmp2, scaling);

		if (tmp.lengthSquared() > BulletGlobals.SIMD_EPSILON) {
			super.setLocalScaling( scaling);
			/*
			 * if (ownsBvh) { m_bvh->~btOptimizedBvh(); btAlignedFree(m_bvh); }
			 */
			/// m_localAabbMin/m_localAabbMax is already re-calculated in btTriangleMeshShape. We could just scale aabb,
			/// but this needs some more work
			bvh = new OptimizedBvh();
			// rebuild the bvh...
			bvh.build( meshInterface, useQuantizedAabbCompression, localAabbMin, localAabbMax);
			ownsBvh = true;
		}
		VECTORS.release(tmp, tmp2);
	}

	/**
	 * Gets the optimized bvh.
	 *
	 * @return the optimized bvh
	 */
	public OptimizedBvh getOptimizedBvh() {
		return bvh;
	}

	/**
	 * Sets the optimized bvh.
	 *
	 * @param bvh the new optimized bvh
	 */
	public void setOptimizedBvh( final OptimizedBvh bvh) {
		Vector3f scaling = VECTORS.get();
		scaling.set(1f, 1f, 1f);
		setOptimizedBvh( bvh, scaling);
		VECTORS.release(scaling);
	}

	/**
	 * Sets the optimized bvh.
	 *
	 * @param bvh the bvh
	 * @param scaling the scaling
	 */
	public void setOptimizedBvh( final OptimizedBvh bvh, final Vector3f scaling) {
		assert this.bvh == null;
		assert !ownsBvh;

		this.bvh = bvh;
		ownsBvh = false;

		// update the scaling without rebuilding the bvh
		Vector3f tmp = VECTORS.get();
		Vector3f tmp2 = getLocalScaling(VECTORS.get());
		tmp.sub(tmp2, scaling);

		if (tmp.lengthSquared() > BulletGlobals.SIMD_EPSILON) { super.setLocalScaling( scaling); }
		VECTORS.release(tmp, tmp2);
	}

	/**
	 * Uses quantized aabb compression.
	 *
	 * @return true, if successful
	 */
	public boolean usesQuantizedAabbCompression() {
		return useQuantizedAabbCompression;
	}

	////////////////////////////////////////////////////////////////////////////

	/**
	 * The Class MyNodeOverlapCallback.
	 */
	protected static class MyNodeOverlapCallback implements NodeOverlapCallback {
		
		/** The mesh interface. */
		public StridingMeshInterface meshInterface;
		
		/** The callback. */
		public TriangleCallback callback;

		/** The triangle. */
		private final Vector3f[] triangle/* [3] */ = new Vector3f[] { new Vector3f(), new Vector3f(), new Vector3f() };

		/**
		 * Instantiates a new my node overlap callback.
		 */
		public MyNodeOverlapCallback() {}

		/**
		 * Inits the.
		 *
		 * @param callback the callback
		 * @param meshInterface the mesh interface
		 */
		public void init(final TriangleCallback callback, final StridingMeshInterface meshInterface) {
			this.meshInterface = meshInterface;
			this.callback = callback;
		}

		@Override
		public void processNode( final int nodeSubPart, final int nodeTriangleIndex) {
			VertexData data = meshInterface.getLockedReadOnlyVertexIndexBase(nodeSubPart);

			Vector3f meshScaling = meshInterface.getScaling(VECTORS.get());

			data.getTriangle(nodeTriangleIndex * 3, meshScaling, triangle);

			/* Perform ray vs. triangle collision here */
			callback.processTriangle( triangle, nodeSubPart, nodeTriangleIndex);

			meshInterface.unLockReadOnlyVertexBase(nodeSubPart);
			VECTORS.release(meshScaling);
		}
	}

}
