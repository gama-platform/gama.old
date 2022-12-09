/*******************************************************************************************************
 *
 * OptimizedBvh.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.VECTORS;

import java.io.Serializable;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.AabbUtil2;
import com.bulletphysics.linearmath.MiscUtil;
import com.bulletphysics.linearmath.VectorUtil;

// JAVA NOTE: OptimizedBvh still from 2.66, update it for 2.70b1

/**
 * OptimizedBvh store an AABB tree that can be quickly traversed on CPU (and SPU, GPU in future).
 *
 * @author jezek2
 */
public class OptimizedBvh implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	// protected final BulletStack stack = BulletStack.get();

	/** The Constant DEBUG_TREE_BUILDING. */
	private static final boolean DEBUG_TREE_BUILDING = false;

	/** The g stack depth. */
	private static int gStackDepth = 0;

	/** The g max stack depth. */
	private static int gMaxStackDepth = 0;

	/** The max iterations. */
	private static int maxIterations = 0;

	/** The Constant MAX_SUBTREE_SIZE_IN_BYTES. */
	// Note: currently we have 16 bytes per quantized node
	public static final int MAX_SUBTREE_SIZE_IN_BYTES = 2048;

	// 10 gives the potential for 1024 parts, with at most 2^21 (2097152) (minus one
	/** The Constant MAX_NUM_PARTS_IN_BITS. */
	// actually) triangles each (since the sign bit is reserved
	public static final int MAX_NUM_PARTS_IN_BITS = 10;

	////////////////////////////////////////////////////////////////////////////

	/** The leaf nodes. */
	private final ArrayList<OptimizedBvhNode> leafNodes = new ArrayList<>();

	/** The contiguous nodes. */
	private final ArrayList<OptimizedBvhNode> contiguousNodes = new ArrayList<>();

	/** The quantized leaf nodes. */
	private final QuantizedBvhNodes quantizedLeafNodes = new QuantizedBvhNodes();

	/** The quantized contiguous nodes. */
	private final QuantizedBvhNodes quantizedContiguousNodes = new QuantizedBvhNodes();

	/** The cur node index. */
	private int curNodeIndex;

	/** The use quantization. */
	// quantization data
	private boolean useQuantization;

	/** The bvh aabb min. */
	private final Vector3f bvhAabbMin = new Vector3f();

	/** The bvh aabb max. */
	private final Vector3f bvhAabbMax = new Vector3f();

	/** The bvh quantization. */
	private final Vector3f bvhQuantization = new Vector3f();

	/** The traversal mode. */
	protected TraversalMode traversalMode = TraversalMode.STACKLESS;

	/** The Subtree headers. */
	protected final ArrayList<BvhSubtreeInfo> SubtreeHeaders = new ArrayList<>();

	/** The subtree header count. */
	// This is only used for serialization so we don't have to add serialization directly to btAlignedObjectArray
	protected int subtreeHeaderCount;

	// two versions, one for quantized and normal nodes. This allows code-reuse while maintaining readability (no
	// template/macro!)
	/**
	 * Sets the internal node aabb min.
	 *
	 * @param nodeIndex
	 *            the node index
	 * @param aabbMin
	 *            the aabb min
	 */
	// this might be refactored into a virtual, it is usually not calculated at run-time
	public void setInternalNodeAabbMin(final int nodeIndex, final Vector3f aabbMin) {
		if (useQuantization) {
			quantizedContiguousNodes.setQuantizedAabbMin(nodeIndex, quantizeWithClamp(aabbMin));
		} else {
			contiguousNodes.get(nodeIndex).aabbMinOrg.set(aabbMin);
		}
	}

	/**
	 * Sets the internal node aabb max.
	 *
	 * @param nodeIndex
	 *            the node index
	 * @param aabbMax
	 *            the aabb max
	 */
	public void setInternalNodeAabbMax(final int nodeIndex, final Vector3f aabbMax) {
		if (useQuantization) {
			quantizedContiguousNodes.setQuantizedAabbMax(nodeIndex, quantizeWithClamp(aabbMax));
		} else {
			contiguousNodes.get(nodeIndex).aabbMaxOrg.set(aabbMax);
		}
	}

	/**
	 * Gets the aabb min.
	 *
	 * @param nodeIndex
	 *            the node index
	 * @return the aabb min
	 */
	public Vector3f getAabbMin(final int nodeIndex) {
		if (useQuantization) {
			Vector3f tmp = new Vector3f();
			unQuantize(tmp, quantizedLeafNodes.getQuantizedAabbMin(nodeIndex));
			return tmp;
		}

		// non-quantized
		return leafNodes.get(nodeIndex).aabbMinOrg;
	}

	/**
	 * Gets the aabb max.
	 *
	 * @param nodeIndex
	 *            the node index
	 * @return the aabb max
	 */
	public Vector3f getAabbMax(final int nodeIndex) {
		if (useQuantization) {
			Vector3f tmp = new Vector3f();
			unQuantize(tmp, quantizedLeafNodes.getQuantizedAabbMax(nodeIndex));
			return tmp;
		}

		// non-quantized
		return leafNodes.get(nodeIndex).aabbMaxOrg;
	}

	/**
	 * Sets the quantization values.
	 *
	 * @param aabbMin
	 *            the aabb min
	 * @param aabbMax
	 *            the aabb max
	 */
	public void setQuantizationValues(final Vector3f aabbMin, final Vector3f aabbMax) {
		setQuantizationValues(aabbMin, aabbMax, 1f);
	}

	/**
	 * Sets the quantization values.
	 *
	 * @param aabbMin
	 *            the aabb min
	 * @param aabbMax
	 *            the aabb max
	 * @param quantizationMargin
	 *            the quantization margin
	 */
	public void setQuantizationValues(final Vector3f aabbMin, final Vector3f aabbMax, final float quantizationMargin) {
		// enlarge the AABB to avoid division by zero when initializing the quantization values
		Vector3f clampValue = VECTORS.get();
		clampValue.set(quantizationMargin, quantizationMargin, quantizationMargin);
		bvhAabbMin.sub(aabbMin, clampValue);
		bvhAabbMax.add(aabbMax, clampValue);
		Vector3f aabbSize = VECTORS.get();
		aabbSize.sub(bvhAabbMax, bvhAabbMin);
		bvhQuantization.set(65535f, 65535f, 65535f);
		VectorUtil.div(bvhQuantization, bvhQuantization, aabbSize);
	}

	/**
	 * Sets the internal node escape index.
	 *
	 * @param nodeIndex
	 *            the node index
	 * @param escapeIndex
	 *            the escape index
	 */
	public void setInternalNodeEscapeIndex(final int nodeIndex, final int escapeIndex) {
		if (useQuantization) {
			quantizedContiguousNodes.setEscapeIndexOrTriangleIndex(nodeIndex, -escapeIndex);
		} else {
			contiguousNodes.get(nodeIndex).escapeIndex = escapeIndex;
		}
	}

	/**
	 * Merge internal node aabb.
	 *
	 * @param nodeIndex
	 *            the node index
	 * @param newAabbMin
	 *            the new aabb min
	 * @param newAabbMax
	 *            the new aabb max
	 */
	public void mergeInternalNodeAabb(final int nodeIndex, final Vector3f newAabbMin, final Vector3f newAabbMax) {
		if (useQuantization) {
			long quantizedAabbMin;
			long quantizedAabbMax;

			quantizedAabbMin = quantizeWithClamp(newAabbMin);
			quantizedAabbMax = quantizeWithClamp(newAabbMax);
			for (int i = 0; i < 3; i++) {
				if (quantizedContiguousNodes.getQuantizedAabbMin(nodeIndex, i) > QuantizedBvhNodes
						.getCoord(quantizedAabbMin, i)) {
					quantizedContiguousNodes.setQuantizedAabbMin(nodeIndex, i,
							QuantizedBvhNodes.getCoord(quantizedAabbMin, i));
				}

				if (quantizedContiguousNodes.getQuantizedAabbMax(nodeIndex, i) < QuantizedBvhNodes
						.getCoord(quantizedAabbMax, i)) {
					quantizedContiguousNodes.setQuantizedAabbMax(nodeIndex, i,
							QuantizedBvhNodes.getCoord(quantizedAabbMax, i));
				}
			}
		} else {
			// non-quantized
			VectorUtil.setMin(contiguousNodes.get(nodeIndex).aabbMinOrg, newAabbMin);
			VectorUtil.setMax(contiguousNodes.get(nodeIndex).aabbMaxOrg, newAabbMax);
		}
	}

	/**
	 * Swap leaf nodes.
	 *
	 * @param i
	 *            the i
	 * @param splitIndex
	 *            the split index
	 */
	public void swapLeafNodes(final int i, final int splitIndex) {
		if (useQuantization) {
			quantizedLeafNodes.swap(i, splitIndex);
		} else {
			// JAVA NOTE: changing reference instead of copy
			OptimizedBvhNode tmp = leafNodes.get(i);
			leafNodes.set(i, leafNodes.get(splitIndex));
			leafNodes.set(splitIndex, tmp);
		}
	}

	/**
	 * Assign internal node from leaf node.
	 *
	 * @param internalNode
	 *            the internal node
	 * @param leafNodeIndex
	 *            the leaf node index
	 */
	public void assignInternalNodeFromLeafNode(final int internalNode, final int leafNodeIndex) {
		if (useQuantization) {
			quantizedContiguousNodes.set(internalNode, quantizedLeafNodes, leafNodeIndex);
		} else {
			contiguousNodes.get(internalNode).set(leafNodes.get(leafNodeIndex));
		}
	}

	/**
	 * The Class NodeTriangleCallback.
	 */
	private static class NodeTriangleCallback implements InternalTriangleIndexCallback {

		/** The triangle nodes. */
		public ArrayList<OptimizedBvhNode> triangleNodes;

		/**
		 * Instantiates a new node triangle callback.
		 *
		 * @param triangleNodes
		 *            the triangle nodes
		 */
		public NodeTriangleCallback(final ArrayList<OptimizedBvhNode> triangleNodes) {
			this.triangleNodes = triangleNodes;
		}

		/** The aabb max. */
		private final Vector3f aabbMin = new Vector3f(), aabbMax = new Vector3f();

		@Override
		public void internalProcessTriangleIndex(final Vector3f[] triangle, final int partId, final int triangleIndex) {
			OptimizedBvhNode node = new OptimizedBvhNode();
			aabbMin.set(1e30f, 1e30f, 1e30f);
			aabbMax.set(-1e30f, -1e30f, -1e30f);
			VectorUtil.setMin(aabbMin, triangle[0]);
			VectorUtil.setMax(aabbMax, triangle[0]);
			VectorUtil.setMin(aabbMin, triangle[1]);
			VectorUtil.setMax(aabbMax, triangle[1]);
			VectorUtil.setMin(aabbMin, triangle[2]);
			VectorUtil.setMax(aabbMax, triangle[2]);

			// with quantization?
			node.aabbMinOrg.set(aabbMin);
			node.aabbMaxOrg.set(aabbMax);

			node.escapeIndex = -1;

			// for child nodes
			node.subPart = partId;
			node.triangleIndex = triangleIndex;
			triangleNodes.add(node);
		}
	}

	/**
	 * The Class QuantizedNodeTriangleCallback.
	 */
	private static class QuantizedNodeTriangleCallback implements InternalTriangleIndexCallback {
		// protected final BulletStack stack = BulletStack.get();

		/** The triangle nodes. */
		public QuantizedBvhNodes triangleNodes;

		/** The optimized tree. */
		public OptimizedBvh optimizedTree; // for quantization

		/**
		 * Instantiates a new quantized node triangle callback.
		 *
		 * @param triangleNodes
		 *            the triangle nodes
		 * @param tree
		 *            the tree
		 */
		public QuantizedNodeTriangleCallback(final QuantizedBvhNodes triangleNodes, final OptimizedBvh tree) {
			this.triangleNodes = triangleNodes;
			this.optimizedTree = tree;
		}

		@Override
		public void internalProcessTriangleIndex(final Vector3f[] triangle, final int partId, final int triangleIndex) {
			// The partId and triangle index must fit in the same (positive) integer
			assert partId < 1 << MAX_NUM_PARTS_IN_BITS;
			assert triangleIndex < 1 << 31 - MAX_NUM_PARTS_IN_BITS;
			// negative indices are reserved for escapeIndex
			assert triangleIndex >= 0;

			int nodeId = triangleNodes.add();
			Vector3f aabbMin = VECTORS.get(), aabbMax = VECTORS.get();
			aabbMin.set(1e30f, 1e30f, 1e30f);
			aabbMax.set(-1e30f, -1e30f, -1e30f);
			VectorUtil.setMin(aabbMin, triangle[0]);
			VectorUtil.setMax(aabbMax, triangle[0]);
			VectorUtil.setMin(aabbMin, triangle[1]);
			VectorUtil.setMax(aabbMax, triangle[1]);
			VectorUtil.setMin(aabbMin, triangle[2]);
			VectorUtil.setMax(aabbMax, triangle[2]);

			// PCK: add these checks for zero dimensions of aabb
			final float MIN_AABB_DIMENSION = 0.002f;
			final float MIN_AABB_HALF_DIMENSION = 0.001f;
			if (aabbMax.x - aabbMin.x < MIN_AABB_DIMENSION) {
				aabbMax.x = aabbMax.x + MIN_AABB_HALF_DIMENSION;
				aabbMin.x = aabbMin.x - MIN_AABB_HALF_DIMENSION;
			}
			if (aabbMax.y - aabbMin.y < MIN_AABB_DIMENSION) {
				aabbMax.y = aabbMax.y + MIN_AABB_HALF_DIMENSION;
				aabbMin.y = aabbMin.y - MIN_AABB_HALF_DIMENSION;
			}
			if (aabbMax.z - aabbMin.z < MIN_AABB_DIMENSION) {
				aabbMax.z = aabbMax.z + MIN_AABB_HALF_DIMENSION;
				aabbMin.z = aabbMin.z - MIN_AABB_HALF_DIMENSION;
			}

			triangleNodes.setQuantizedAabbMin(nodeId, optimizedTree.quantizeWithClamp(aabbMin));
			triangleNodes.setQuantizedAabbMax(nodeId, optimizedTree.quantizeWithClamp(aabbMax));

			triangleNodes.setEscapeIndexOrTriangleIndex(nodeId, partId << 31 - MAX_NUM_PARTS_IN_BITS | triangleIndex);
		}
	}

	/**
	 * Builds the.
	 *
	 * @param triangles
	 *            the triangles
	 * @param useQuantizedAabbCompression
	 *            the use quantized aabb compression
	 * @param _aabbMin
	 *            the aabb min
	 * @param _aabbMax
	 *            the aabb max
	 */
	public void build(final StridingMeshInterface triangles, final boolean useQuantizedAabbCompression,
			final Vector3f _aabbMin, final Vector3f _aabbMax) {
		this.useQuantization = useQuantizedAabbCompression;

		// NodeArray triangleNodes;

		int numLeafNodes = 0;

		if (useQuantization) {
			// initialize quantization values
			setQuantizationValues(_aabbMin, _aabbMax);

			QuantizedNodeTriangleCallback callback = new QuantizedNodeTriangleCallback(quantizedLeafNodes, this);

			triangles.internalProcessAllTriangles(callback, bvhAabbMin, bvhAabbMax);

			// now we have an array of leafnodes in m_leafNodes
			numLeafNodes = quantizedLeafNodes.size();

			quantizedContiguousNodes.resize(2 * numLeafNodes);
		} else {
			NodeTriangleCallback callback = new NodeTriangleCallback(leafNodes);

			Vector3f aabbMin = VECTORS.get();
			aabbMin.set(-1e30f, -1e30f, -1e30f);
			Vector3f aabbMax = VECTORS.get();
			aabbMax.set(1e30f, 1e30f, 1e30f);

			triangles.internalProcessAllTriangles(callback, aabbMin, aabbMax);

			// now we have an array of leafnodes in m_leafNodes
			numLeafNodes = leafNodes.size();

			// TODO: check
			// contiguousNodes.resize(2*numLeafNodes);
			MiscUtil.resize(contiguousNodes, 2 * numLeafNodes, OptimizedBvhNode.class);
		}

		curNodeIndex = 0;

		buildTree(0, numLeafNodes);

		// if the entire tree is small then subtree size, we need to create a header info for the tree
		if (useQuantization && SubtreeHeaders.size() == 0) {
			BvhSubtreeInfo subtree = new BvhSubtreeInfo();
			SubtreeHeaders.add(subtree);

			subtree.setAabbFromQuantizeNode(quantizedContiguousNodes, 0);
			subtree.rootNodeIndex = 0;
			subtree.subtreeSize =
					quantizedContiguousNodes.isLeafNode(0) ? 1 : quantizedContiguousNodes.getEscapeIndex(0);
		}

		// PCK: update the copy of the size
		subtreeHeaderCount = SubtreeHeaders.size();

		// PCK: clear m_quantizedLeafNodes and m_leafNodes, they are temporary
		quantizedLeafNodes.clear();
		leafNodes.clear();
	}

	/**
	 * Refit.
	 *
	 * @param meshInterface
	 *            the mesh interface
	 */
	public void refit(final StridingMeshInterface meshInterface) {
		if (useQuantization) {
			// calculate new aabb
			Vector3f aabbMin = VECTORS.get(), aabbMax = VECTORS.get();
			meshInterface.calculateAabbBruteForce(aabbMin, aabbMax);

			setQuantizationValues(aabbMin, aabbMax);

			updateBvhNodes(meshInterface, 0, curNodeIndex, 0);

			// now update all subtree headers

			int i;
			for (i = 0; i < SubtreeHeaders.size(); i++) {
				BvhSubtreeInfo subtree = SubtreeHeaders.get(i);
				subtree.setAabbFromQuantizeNode(quantizedContiguousNodes, subtree.rootNodeIndex);
			}

		} else {
			// JAVA NOTE: added for testing, it's too slow for practical use
			build(meshInterface, false, null, null);
		}
	}

	/**
	 * Refit partial.
	 *
	 * @param meshInterface
	 *            the mesh interface
	 * @param aabbMin
	 *            the aabb min
	 * @param aabbMax
	 *            the aabb max
	 */
	public void refitPartial(final StridingMeshInterface meshInterface, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		throw new UnsupportedOperationException();
		// // incrementally initialize quantization values
		// assert (useQuantization);
		//
		// btAssert(aabbMin.getX() > m_bvhAabbMin.getX());
		// btAssert(aabbMin.getY() > m_bvhAabbMin.getY());
		// btAssert(aabbMin.getZ() > m_bvhAabbMin.getZ());
		//
		// btAssert(aabbMax.getX() < m_bvhAabbMax.getX());
		// btAssert(aabbMax.getY() < m_bvhAabbMax.getY());
		// btAssert(aabbMax.getZ() < m_bvhAabbMax.getZ());
		//
		// ///we should update all quantization values, using updateBvhNodes(meshInterface);
		// ///but we only update chunks that overlap the given aabb
		//
		// unsigned short quantizedQueryAabbMin[3];
		// unsigned short quantizedQueryAabbMax[3];
		//
		// quantizeWithClamp(&quantizedQueryAabbMin[0],aabbMin);
		// quantizeWithClamp(&quantizedQueryAabbMax[0],aabbMax);
		//
		// int i;
		// for (i=0;i<this->m_SubtreeHeaders.size();i++)
		// {
		// btBvhSubtreeInfo& subtree = m_SubtreeHeaders[i];
		//
		// //PCK: unsigned instead of bool
		// unsigned overlap =
		// testQuantizedAabbAgainstQuantizedAabb(quantizedQueryAabbMin,quantizedQueryAabbMax,subtree.m_quantizedAabbMin,subtree.m_quantizedAabbMax);
		// if (overlap != 0)
		// {
		// updateBvhNodes(meshInterface,subtree.m_rootNodeIndex,subtree.m_rootNodeIndex+subtree.m_subtreeSize,i);
		//
		// subtree.setAabbFromQuantizeNode(m_quantizedContiguousNodes[subtree.m_rootNodeIndex]);
		// }
		// }
	}

	/**
	 * Update bvh nodes.
	 *
	 * @param meshInterface
	 *            the mesh interface
	 * @param firstNode
	 *            the first node
	 * @param endNode
	 *            the end node
	 * @param index
	 *            the index
	 */
	public void updateBvhNodes(final StridingMeshInterface meshInterface, final int firstNode, final int endNode,
			final int index) {
		assert useQuantization;

		int curNodeSubPart = -1;

		Vector3f[] triangleVerts/* [3] */ = { VECTORS.get(), VECTORS.get(), VECTORS.get() };
		Vector3f aabbMin = VECTORS.get(), aabbMax = VECTORS.get();
		Vector3f meshScaling = meshInterface.getScaling(VECTORS.get());

		VertexData data = null;

		for (int i = endNode - 1; i >= firstNode; i--) {
			QuantizedBvhNodes curNodes = quantizedContiguousNodes;
			int curNodeId = i;

			if (curNodes.isLeafNode(curNodeId)) {
				// recalc aabb from triangle data
				int nodeSubPart = curNodes.getPartId(curNodeId);
				int nodeTriangleIndex = curNodes.getTriangleIndex(curNodeId);
				if (nodeSubPart != curNodeSubPart) {
					if (curNodeSubPart >= 0) { meshInterface.unLockReadOnlyVertexBase(curNodeSubPart); }
					data = meshInterface.getLockedReadOnlyVertexIndexBase(nodeSubPart);
					data.getTriangle(nodeTriangleIndex * 3, meshScaling, triangleVerts);
				}
				// triangles->getLockedReadOnlyVertexIndexBase(vertexBase,numVerts,

				aabbMin.set(1e30f, 1e30f, 1e30f);
				aabbMax.set(-1e30f, -1e30f, -1e30f);
				VectorUtil.setMin(aabbMin, triangleVerts[0]);
				VectorUtil.setMax(aabbMax, triangleVerts[0]);
				VectorUtil.setMin(aabbMin, triangleVerts[1]);
				VectorUtil.setMax(aabbMax, triangleVerts[1]);
				VectorUtil.setMin(aabbMin, triangleVerts[2]);
				VectorUtil.setMax(aabbMax, triangleVerts[2]);

				curNodes.setQuantizedAabbMin(curNodeId, quantizeWithClamp(aabbMin));
				curNodes.setQuantizedAabbMax(curNodeId, quantizeWithClamp(aabbMax));
			} else {
				// combine aabb from both children

				// quantizedContiguousNodes
				int leftChildNodeId = i + 1;

				int rightChildNodeId = quantizedContiguousNodes.isLeafNode(leftChildNodeId) ? i + 2
						: i + 1 + quantizedContiguousNodes.getEscapeIndex(leftChildNodeId);

				for (int i2 = 0; i2 < 3; i2++) {
					curNodes.setQuantizedAabbMin(curNodeId, i2,
							quantizedContiguousNodes.getQuantizedAabbMin(leftChildNodeId, i2));
					if (curNodes.getQuantizedAabbMin(curNodeId, i2) > quantizedContiguousNodes
							.getQuantizedAabbMin(rightChildNodeId, i2)) {
						curNodes.setQuantizedAabbMin(curNodeId, i2,
								quantizedContiguousNodes.getQuantizedAabbMin(rightChildNodeId, i2));
					}

					curNodes.setQuantizedAabbMax(curNodeId, i2,
							quantizedContiguousNodes.getQuantizedAabbMax(leftChildNodeId, i2));
					if (curNodes.getQuantizedAabbMax(curNodeId, i2) < quantizedContiguousNodes
							.getQuantizedAabbMax(rightChildNodeId, i2)) {
						curNodes.setQuantizedAabbMax(curNodeId, i2,
								quantizedContiguousNodes.getQuantizedAabbMax(rightChildNodeId, i2));
					}
				}
			}
		}

		if (curNodeSubPart >= 0) { meshInterface.unLockReadOnlyVertexBase(curNodeSubPart); }

		VECTORS.release(triangleVerts[0], triangleVerts[1], triangleVerts[2], aabbMin, aabbMax, meshScaling);
	}

	/**
	 * Builds the tree.
	 *
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 */
	protected void buildTree(final int startIndex, final int endIndex) {
		// #ifdef DEBUG_TREE_BUILDING
		if (DEBUG_TREE_BUILDING) {
			gStackDepth++;
			if (gStackDepth > gMaxStackDepth) { gMaxStackDepth = gStackDepth; }
		}
		// #endif //DEBUG_TREE_BUILDING

		int splitAxis, splitIndex, i;
		int numIndices = endIndex - startIndex;
		int curIndex = curNodeIndex;

		assert numIndices > 0;

		if (numIndices == 1) {
			// #ifdef DEBUG_TREE_BUILDING
			if (DEBUG_TREE_BUILDING) { gStackDepth--; }
			// #endif //DEBUG_TREE_BUILDING

			assignInternalNodeFromLeafNode(curNodeIndex, startIndex);

			curNodeIndex++;
			return;
		}
		// calculate Best Splitting Axis and where to split it. Sort the incoming 'leafNodes' array within range
		// 'startIndex/endIndex'.

		splitAxis = calcSplittingAxis(startIndex, endIndex);

		splitIndex = sortAndCalcSplittingIndex(startIndex, endIndex, splitAxis);

		int internalNodeIndex = curNodeIndex;

		Vector3f tmp1 = VECTORS.get();
		tmp1.set(-1e30f, -1e30f, -1e30f);
		setInternalNodeAabbMax(curNodeIndex, tmp1);
		Vector3f tmp2 = VECTORS.get();
		tmp2.set(1e30f, 1e30f, 1e30f);
		setInternalNodeAabbMin(curNodeIndex, tmp2);

		for (i = startIndex; i < endIndex; i++) { mergeInternalNodeAabb(curNodeIndex, getAabbMin(i), getAabbMax(i)); }

		curNodeIndex++;

		// internalNode->m_escapeIndex;

		int leftChildNodexIndex = curNodeIndex;

		// build left child tree
		buildTree(startIndex, splitIndex);

		int rightChildNodexIndex = curNodeIndex;
		// build right child tree
		buildTree(splitIndex, endIndex);

		// #ifdef DEBUG_TREE_BUILDING
		if (DEBUG_TREE_BUILDING) { gStackDepth--; }
		// #endif //DEBUG_TREE_BUILDING

		int escapeIndex = curNodeIndex - curIndex;

		if (useQuantization) {
			// escapeIndex is the number of nodes of this subtree
			int sizeQuantizedNode = QuantizedBvhNodes.getNodeSize();
			int treeSizeInBytes = escapeIndex * sizeQuantizedNode;
			if (treeSizeInBytes > MAX_SUBTREE_SIZE_IN_BYTES) {
				updateSubtreeHeaders(leftChildNodexIndex, rightChildNodexIndex);
			}
		}

		setInternalNodeEscapeIndex(internalNodeIndex, escapeIndex);
		VECTORS.release(tmp1, tmp2);
	}

	/**
	 * Test quantized aabb against quantized aabb.
	 *
	 * @param aabbMin1
	 *            the aabb min 1
	 * @param aabbMax1
	 *            the aabb max 1
	 * @param aabbMin2
	 *            the aabb min 2
	 * @param aabbMax2
	 *            the aabb max 2
	 * @return true, if successful
	 */
	protected boolean testQuantizedAabbAgainstQuantizedAabb(final long aabbMin1, final long aabbMax1,
			final long aabbMin2, final long aabbMax2) {
		int aabbMin1_0 = QuantizedBvhNodes.getCoord(aabbMin1, 0);
		int aabbMin1_1 = QuantizedBvhNodes.getCoord(aabbMin1, 1);
		int aabbMin1_2 = QuantizedBvhNodes.getCoord(aabbMin1, 2);

		int aabbMax1_0 = QuantizedBvhNodes.getCoord(aabbMax1, 0);
		int aabbMax1_1 = QuantizedBvhNodes.getCoord(aabbMax1, 1);
		int aabbMax1_2 = QuantizedBvhNodes.getCoord(aabbMax1, 2);

		int aabbMin2_0 = QuantizedBvhNodes.getCoord(aabbMin2, 0);
		int aabbMin2_1 = QuantizedBvhNodes.getCoord(aabbMin2, 1);
		int aabbMin2_2 = QuantizedBvhNodes.getCoord(aabbMin2, 2);

		int aabbMax2_0 = QuantizedBvhNodes.getCoord(aabbMax2, 0);
		int aabbMax2_1 = QuantizedBvhNodes.getCoord(aabbMax2, 1);
		int aabbMax2_2 = QuantizedBvhNodes.getCoord(aabbMax2, 2);

		boolean overlap = true;
		overlap = aabbMin1_0 > aabbMax2_0 || aabbMax1_0 < aabbMin2_0 ? false : overlap;
		overlap = aabbMin1_2 > aabbMax2_2 || aabbMax1_2 < aabbMin2_2 ? false : overlap;
		return aabbMin1_1 > aabbMax2_1 || aabbMax1_1 < aabbMin2_1 ? false : overlap;
	}

	/**
	 * Update subtree headers.
	 *
	 * @param leftChildNodexIndex
	 *            the left child nodex index
	 * @param rightChildNodexIndex
	 *            the right child nodex index
	 */
	protected void updateSubtreeHeaders(final int leftChildNodexIndex, final int rightChildNodexIndex) {
		assert useQuantization;

		// btQuantizedBvhNode& leftChildNode = m_quantizedContiguousNodes[leftChildNodexIndex];
		int leftSubTreeSize = quantizedContiguousNodes.isLeafNode(leftChildNodexIndex) ? 1
				: quantizedContiguousNodes.getEscapeIndex(leftChildNodexIndex);
		int leftSubTreeSizeInBytes = leftSubTreeSize * QuantizedBvhNodes.getNodeSize();

		// btQuantizedBvhNode& rightChildNode = m_quantizedContiguousNodes[rightChildNodexIndex];
		int rightSubTreeSize = quantizedContiguousNodes.isLeafNode(rightChildNodexIndex) ? 1
				: quantizedContiguousNodes.getEscapeIndex(rightChildNodexIndex);
		int rightSubTreeSizeInBytes = rightSubTreeSize * QuantizedBvhNodes.getNodeSize();

		if (leftSubTreeSizeInBytes <= MAX_SUBTREE_SIZE_IN_BYTES) {
			BvhSubtreeInfo subtree = new BvhSubtreeInfo();
			SubtreeHeaders.add(subtree);

			subtree.setAabbFromQuantizeNode(quantizedContiguousNodes, leftChildNodexIndex);
			subtree.rootNodeIndex = leftChildNodexIndex;
			subtree.subtreeSize = leftSubTreeSize;
		}

		if (rightSubTreeSizeInBytes <= MAX_SUBTREE_SIZE_IN_BYTES) {
			BvhSubtreeInfo subtree = new BvhSubtreeInfo();
			SubtreeHeaders.add(subtree);

			subtree.setAabbFromQuantizeNode(quantizedContiguousNodes, rightChildNodexIndex);
			subtree.rootNodeIndex = rightChildNodexIndex;
			subtree.subtreeSize = rightSubTreeSize;
		}

		// PCK: update the copy of the size
		subtreeHeaderCount = SubtreeHeaders.size();
	}

	/**
	 * Sort and calc splitting index.
	 *
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @param splitAxis
	 *            the split axis
	 * @return the int
	 */
	protected int sortAndCalcSplittingIndex(final int startIndex, final int endIndex, final int splitAxis) {
		int i;
		int splitIndex = startIndex;
		int numIndices = endIndex - startIndex;
		float splitValue;

		Vector3f means = VECTORS.get();
		means.set(0f, 0f, 0f);
		Vector3f center = VECTORS.get();
		for (i = startIndex; i < endIndex; i++) {
			center.add(getAabbMax(i), getAabbMin(i));
			center.scale(0.5f);
			means.add(center);
		}
		means.scale(1f / numIndices);

		splitValue = VectorUtil.getCoord(means, splitAxis);

		// sort leafNodes so all values larger then splitValue comes first, and smaller values start from 'splitIndex'.
		for (i = startIndex; i < endIndex; i++) {
			// Vector3f center = new Vector3f();
			center.add(getAabbMax(i), getAabbMin(i));
			center.scale(0.5f);

			if (VectorUtil.getCoord(center, splitAxis) > splitValue) {
				// swap
				swapLeafNodes(i, splitIndex);
				splitIndex++;
			}
		}

		// if the splitIndex causes unbalanced trees, fix this by using the center in between startIndex and endIndex
		// otherwise the tree-building might fail due to stack-overflows in certain cases.
		// unbalanced1 is unsafe: it can cause stack overflows
		// bool unbalanced1 = ((splitIndex==startIndex) || (splitIndex == (endIndex-1)));

		// unbalanced2 should work too: always use center (perfect balanced trees)
		// bool unbalanced2 = true;

		// this should be safe too:
		int rangeBalancedIndices = numIndices / 3;
		boolean unbalanced =
				splitIndex <= startIndex + rangeBalancedIndices || splitIndex >= endIndex - 1 - rangeBalancedIndices;

		if (unbalanced) { splitIndex = startIndex + (numIndices >> 1); }

		boolean unbal = splitIndex == startIndex || splitIndex == endIndex;
		assert !unbal;

		VECTORS.release(means, center);

		return splitIndex;
	}

	/**
	 * Calc splitting axis.
	 *
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the int
	 */
	protected int calcSplittingAxis(final int startIndex, final int endIndex) {
		int i;

		Vector3f means = VECTORS.get();
		means.set(0f, 0f, 0f);
		Vector3f variance = VECTORS.get();
		variance.set(0f, 0f, 0f);
		int numIndices = endIndex - startIndex;

		Vector3f center = VECTORS.get();
		for (i = startIndex; i < endIndex; i++) {
			center.add(getAabbMax(i), getAabbMin(i));
			center.scale(0.5f);
			means.add(center);
		}
		means.scale(1f / numIndices);

		Vector3f diff2 = VECTORS.get();
		for (i = startIndex; i < endIndex; i++) {
			center.add(getAabbMax(i), getAabbMin(i));
			center.scale(0.5f);
			diff2.sub(center, means);
			// diff2 = diff2 * diff2;
			VectorUtil.mul(diff2, diff2, diff2);
			variance.add(diff2);
		}
		variance.scale(1f / ((float) numIndices - 1));
		VECTORS.release(means, center, diff2, variance);
		return VectorUtil.maxAxis(variance);
	}

	/**
	 * Report aabb overlapping nodex.
	 *
	 * @param nodeCallback
	 *            the node callback
	 * @param aabbMin
	 *            the aabb min
	 * @param aabbMax
	 *            the aabb max
	 */
	public void reportAabbOverlappingNodex(final NodeOverlapCallback nodeCallback, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		// either choose recursive traversal (walkTree) or stackless (walkStacklessTree)

		if (useQuantization) {
			// quantize query AABB
			long quantizedQueryAabbMin;
			long quantizedQueryAabbMax;
			quantizedQueryAabbMin = quantizeWithClamp(aabbMin);
			quantizedQueryAabbMax = quantizeWithClamp(aabbMax);

			// JAVA TODO:
			switch (traversalMode) {
				case STACKLESS:
					walkStacklessQuantizedTree(nodeCallback, quantizedQueryAabbMin, quantizedQueryAabbMax, 0,
							curNodeIndex);
					break;

				// case STACKLESS_CACHE_FRIENDLY:
				// walkStacklessQuantizedTreeCacheFriendly(nodeCallback, quantizedQueryAabbMin, quantizedQueryAabbMax);
				// break;

				case RECURSIVE:
					walkRecursiveQuantizedTreeAgainstQueryAabb(quantizedContiguousNodes, 0, nodeCallback,
							quantizedQueryAabbMin, quantizedQueryAabbMax);
					break;

				default:
					assert false; // unsupported
			}
		} else {
			walkStacklessTree(nodeCallback, aabbMin, aabbMax);
		}
	}

	/**
	 * Walk stackless tree.
	 *
	 * @param nodeCallback
	 *            the node callback
	 * @param aabbMin
	 *            the aabb min
	 * @param aabbMax
	 *            the aabb max
	 */
	protected void walkStacklessTree(final NodeOverlapCallback nodeCallback, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		assert !useQuantization;

		// JAVA NOTE: rewritten
		OptimizedBvhNode rootNode = null;// contiguousNodes.get(0);
		int rootNode_index = 0;

		int escapeIndex, curIndex = 0;
		int walkIterations = 0;
		boolean isLeafNode;
		// PCK: unsigned instead of bool
		// unsigned aabbOverlap;
		boolean aabbOverlap;

		while (curIndex < curNodeIndex) {
			// catch bugs in tree data
			assert walkIterations < curNodeIndex;

			walkIterations++;

			rootNode = contiguousNodes.get(rootNode_index);

			aabbOverlap = AabbUtil2.testAabbAgainstAabb2(aabbMin, aabbMax, rootNode.aabbMinOrg, rootNode.aabbMaxOrg);
			isLeafNode = rootNode.escapeIndex == -1;

			// PCK: unsigned instead of bool
			if (isLeafNode && aabbOverlap) { nodeCallback.processNode(rootNode.subPart, rootNode.triangleIndex); }

			rootNode = null;

			// PCK: unsigned instead of bool
			if (aabbOverlap || isLeafNode) {
				rootNode_index++;
				curIndex++;
			} else {
				escapeIndex = /* rootNode */ contiguousNodes.get(rootNode_index).escapeIndex;
				rootNode_index += escapeIndex;
				curIndex += escapeIndex;
			}
		}
		if (maxIterations < walkIterations) { maxIterations = walkIterations; }
	}

	/**
	 * Walk recursive quantized tree against query aabb.
	 *
	 * @param currentNodes
	 *            the current nodes
	 * @param currentNodeId
	 *            the current node id
	 * @param nodeCallback
	 *            the node callback
	 * @param quantizedQueryAabbMin
	 *            the quantized query aabb min
	 * @param quantizedQueryAabbMax
	 *            the quantized query aabb max
	 */
	protected void walkRecursiveQuantizedTreeAgainstQueryAabb(final QuantizedBvhNodes currentNodes,
			final int currentNodeId, final NodeOverlapCallback nodeCallback, final long quantizedQueryAabbMin,
			final long quantizedQueryAabbMax) {
		assert useQuantization;

		boolean isLeafNode;
		boolean aabbOverlap;

		aabbOverlap = testQuantizedAabbAgainstQuantizedAabb(quantizedQueryAabbMin, quantizedQueryAabbMax,
				currentNodes.getQuantizedAabbMin(currentNodeId), currentNodes.getQuantizedAabbMax(currentNodeId));
		isLeafNode = currentNodes.isLeafNode(currentNodeId);

		if (aabbOverlap) {
			if (isLeafNode) {
				nodeCallback.processNode(currentNodes.getPartId(currentNodeId),
						currentNodes.getTriangleIndex(currentNodeId));
			} else {
				// process left and right children
				int leftChildNodeId = currentNodeId + 1;
				walkRecursiveQuantizedTreeAgainstQueryAabb(currentNodes, leftChildNodeId, nodeCallback,
						quantizedQueryAabbMin, quantizedQueryAabbMax);

				int rightChildNodeId = currentNodes.isLeafNode(leftChildNodeId) ? leftChildNodeId + 1
						: leftChildNodeId + currentNodes.getEscapeIndex(leftChildNodeId);
				walkRecursiveQuantizedTreeAgainstQueryAabb(currentNodes, rightChildNodeId, nodeCallback,
						quantizedQueryAabbMin, quantizedQueryAabbMax);
			}
		}
	}

	/**
	 * Walk stackless quantized tree against ray.
	 *
	 * @param nodeCallback
	 *            the node callback
	 * @param raySource
	 *            the ray source
	 * @param rayTarget
	 *            the ray target
	 * @param aabbMin
	 *            the aabb min
	 * @param aabbMax
	 *            the aabb max
	 * @param startNodeIndex
	 *            the start node index
	 * @param endNodeIndex
	 *            the end node index
	 */
	protected void walkStacklessQuantizedTreeAgainstRay(final NodeOverlapCallback nodeCallback,
			final Vector3f raySource, final Vector3f rayTarget, final Vector3f aabbMin, final Vector3f aabbMax,
			final int startNodeIndex, final int endNodeIndex) {
		assert useQuantization;

		Vector3f tmp = VECTORS.get();

		int curIndex = startNodeIndex;
		int walkIterations = 0;
		int subTreeSize = endNodeIndex - startNodeIndex;

		QuantizedBvhNodes rootNode = quantizedContiguousNodes;
		int rootNode_idx = startNodeIndex;
		int escapeIndex;

		boolean isLeafNode;
		boolean boxBoxOverlap = false;
		boolean rayBoxOverlap = false;

		// float lambda_max = 1f;
		// #define RAYAABB2
		// #ifdef RAYAABB2
		// Vector3f rayFrom = VECTORS.get(raySource);
		Vector3f rayDirection = VECTORS.get();
		tmp.sub(rayTarget, raySource);
		rayDirection.normalize(tmp);
		// lambda_max = rayDirection.dot(tmp);
		rayDirection.x = 1f / rayDirection.x;
		rayDirection.y = 1f / rayDirection.y;
		rayDirection.z = 1f / rayDirection.z;
		// boolean sign_x = rayDirection.x < 0f;
		// boolean sign_y = rayDirection.y < 0f;
		// boolean sign_z = rayDirection.z < 0f;
		// #endif

		/* Quick pruning by quantized box */
		Vector3f rayAabbMin = VECTORS.get(raySource);
		Vector3f rayAabbMax = VECTORS.get(raySource);
		VectorUtil.setMin(rayAabbMin, rayTarget);
		VectorUtil.setMax(rayAabbMax, rayTarget);

		/* Add box cast extents to bounding box */
		rayAabbMin.add(aabbMin);
		rayAabbMax.add(aabbMax);

		long quantizedQueryAabbMin;
		long quantizedQueryAabbMax;
		quantizedQueryAabbMin = quantizeWithClamp(rayAabbMin);
		quantizedQueryAabbMax = quantizeWithClamp(rayAabbMax);

		Vector3f bounds_0 = VECTORS.get();
		Vector3f bounds_1 = VECTORS.get();
		Vector3f normal = VECTORS.get();

		float[] param = new float[1];

		while (curIndex < endNodeIndex) {

			// #define VISUALLY_ANALYZE_BVH 1
			// #ifdef VISUALLY_ANALYZE_BVH
			// //some code snippet to debugDraw aabb, to visually analyze bvh structure
			// static int drawPatch = 0;
			// //need some global access to a debugDrawer
			// extern btIDebugDraw* debugDrawerPtr;
			// if (curIndex==drawPatch)
			// {
			// btVector3 aabbMin,aabbMax;
			// aabbMin = unQuantize(rootNode->m_quantizedAabbMin);
			// aabbMax = unQuantize(rootNode->m_quantizedAabbMax);
			// btVector3 color(1,0,0);
			// debugDrawerPtr->drawAabb(aabbMin,aabbMax,color);
			// }
			// #endif//VISUALLY_ANALYZE_BVH

			// catch bugs in tree data
			assert walkIterations < subTreeSize;

			walkIterations++;
			// only interested if this is closer than any previous hit
			param[0] = 1f;
			rayBoxOverlap = false;
			boxBoxOverlap = testQuantizedAabbAgainstQuantizedAabb(quantizedQueryAabbMin, quantizedQueryAabbMax,
					rootNode.getQuantizedAabbMin(rootNode_idx), rootNode.getQuantizedAabbMax(rootNode_idx));
			isLeafNode = rootNode.isLeafNode(rootNode_idx);
			if (boxBoxOverlap) {
				unQuantize(bounds_0, rootNode.getQuantizedAabbMin(rootNode_idx));
				unQuantize(bounds_1, rootNode.getQuantizedAabbMax(rootNode_idx));
				/* Add box cast extents */
				bounds_0.add(aabbMin);
				bounds_1.add(aabbMax);
				// #if 0
				// bool ra2 = btRayAabb2 (raySource, rayDirection, sign, bounds, param, 0.0, lambda_max);
				// bool ra = btRayAabb (raySource, rayTarget, bounds[0], bounds[1], param, normal);
				// if (ra2 != ra)
				// {
				// printf("functions don't match\n");
				// }
				// #endif
				// #ifdef RAYAABB2
				// rayBoxOverlap = AabbUtil2.rayAabb2 (raySource, rayDirection, sign, bounds, param, 0.0, lambda_max);
				// #else
				rayBoxOverlap = AabbUtil2.rayAabb(raySource, rayTarget, bounds_0, bounds_1, param, normal);
				// #endif
			}

			if (isLeafNode && rayBoxOverlap) {
				nodeCallback.processNode(rootNode.getPartId(rootNode_idx), rootNode.getTriangleIndex(rootNode_idx));
			}

			if (rayBoxOverlap || isLeafNode) {
				rootNode_idx++;
				curIndex++;
			} else {
				escapeIndex = rootNode.getEscapeIndex(rootNode_idx);
				rootNode_idx += escapeIndex;
				curIndex += escapeIndex;
			}
		}

		if (maxIterations < walkIterations) { maxIterations = walkIterations; }

		VECTORS.release(normal, bounds_1, bounds_0, rayAabbMin, rayAabbMax, rayDirection, tmp);
	}

	/**
	 * Walk stackless quantized tree.
	 *
	 * @param nodeCallback
	 *            the node callback
	 * @param quantizedQueryAabbMin
	 *            the quantized query aabb min
	 * @param quantizedQueryAabbMax
	 *            the quantized query aabb max
	 * @param startNodeIndex
	 *            the start node index
	 * @param endNodeIndex
	 *            the end node index
	 */
	protected void walkStacklessQuantizedTree(final NodeOverlapCallback nodeCallback, final long quantizedQueryAabbMin,
			final long quantizedQueryAabbMax, final int startNodeIndex, final int endNodeIndex) {
		assert useQuantization;

		int curIndex = startNodeIndex;
		int walkIterations = 0;
		int subTreeSize = endNodeIndex - startNodeIndex;

		QuantizedBvhNodes rootNode = quantizedContiguousNodes;
		int rootNode_idx = startNodeIndex;
		int escapeIndex;

		boolean isLeafNode;
		boolean aabbOverlap;

		while (curIndex < endNodeIndex) {
			//// #define VISUALLY_ANALYZE_BVH 1
			// #ifdef VISUALLY_ANALYZE_BVH
			//// some code snippet to debugDraw aabb, to visually analyze bvh structure
			// static int drawPatch = 0;
			//// need some global access to a debugDrawer
			// extern btIDebugDraw* debugDrawerPtr;
			// if (curIndex==drawPatch)
			// {
			// btVector3 aabbMin,aabbMax;
			// aabbMin = unQuantize(rootNode->m_quantizedAabbMin);
			// aabbMax = unQuantize(rootNode->m_quantizedAabbMax);
			// btVector3 color(1,0,0);
			// debugDrawerPtr->drawAabb(aabbMin,aabbMax,color);
			// }
			// #endif//VISUALLY_ANALYZE_BVH

			// catch bugs in tree data
			assert walkIterations < subTreeSize;

			walkIterations++;
			aabbOverlap = testQuantizedAabbAgainstQuantizedAabb(quantizedQueryAabbMin, quantizedQueryAabbMax,
					rootNode.getQuantizedAabbMin(rootNode_idx), rootNode.getQuantizedAabbMax(rootNode_idx));
			isLeafNode = rootNode.isLeafNode(rootNode_idx);

			if (isLeafNode && aabbOverlap) {
				nodeCallback.processNode(rootNode.getPartId(rootNode_idx), rootNode.getTriangleIndex(rootNode_idx));
			}

			if (aabbOverlap || isLeafNode) {
				rootNode_idx++;
				curIndex++;
			} else {
				escapeIndex = rootNode.getEscapeIndex(rootNode_idx);
				rootNode_idx += escapeIndex;
				curIndex += escapeIndex;
			}
		}

		if (maxIterations < walkIterations) { maxIterations = walkIterations; }
	}

	/**
	 * Report ray overlapping nodex.
	 *
	 * @param nodeCallback
	 *            the node callback
	 * @param raySource
	 *            the ray source
	 * @param rayTarget
	 *            the ray target
	 */
	public void reportRayOverlappingNodex(final NodeOverlapCallback nodeCallback, final Vector3f raySource,
			final Vector3f rayTarget) {
		boolean fast_path = useQuantization && traversalMode == TraversalMode.STACKLESS;
		if (fast_path) {
			Vector3f tmp = VECTORS.get();
			tmp.set(0f, 0f, 0f);
			walkStacklessQuantizedTreeAgainstRay(nodeCallback, raySource, rayTarget, tmp, tmp, 0, curNodeIndex);
			VECTORS.release(tmp);
		} else {
			/* Otherwise fallback to AABB overlap test */
			Vector3f aabbMin = VECTORS.get(raySource);
			Vector3f aabbMax = VECTORS.get(raySource);
			VectorUtil.setMin(aabbMin, rayTarget);
			VectorUtil.setMax(aabbMax, rayTarget);
			reportAabbOverlappingNodex(nodeCallback, aabbMin, aabbMax);
			VECTORS.release(aabbMin, aabbMax);
		}
	}

	/**
	 * Report box cast overlapping nodex.
	 *
	 * @param nodeCallback
	 *            the node callback
	 * @param raySource
	 *            the ray source
	 * @param rayTarget
	 *            the ray target
	 * @param aabbMin
	 *            the aabb min
	 * @param aabbMax
	 *            the aabb max
	 */
	public void reportBoxCastOverlappingNodex(final NodeOverlapCallback nodeCallback, final Vector3f raySource,
			final Vector3f rayTarget, final Vector3f aabbMin, final Vector3f aabbMax) {
		boolean fast_path = useQuantization && traversalMode == TraversalMode.STACKLESS;
		if (fast_path) {
			walkStacklessQuantizedTreeAgainstRay(nodeCallback, raySource, rayTarget, aabbMin, aabbMax, 0, curNodeIndex);
		} else {
			/*
			 * Slow path: Construct the bounding box for the entire box cast and send that down the tree
			 */
			Vector3f qaabbMin = VECTORS.get(raySource);
			Vector3f qaabbMax = VECTORS.get(raySource);
			VectorUtil.setMin(qaabbMin, rayTarget);
			VectorUtil.setMax(qaabbMax, rayTarget);
			qaabbMin.add(aabbMin);
			qaabbMax.add(aabbMax);
			reportAabbOverlappingNodex(nodeCallback, qaabbMin, qaabbMax);
			VECTORS.release(qaabbMin, qaabbMax);
		}
	}

	/**
	 * Quantize with clamp.
	 *
	 * @param point
	 *            the point
	 * @return the long
	 */
	public long quantizeWithClamp(final Vector3f point) {
		assert useQuantization;

		Vector3f clampedPoint = VECTORS.get(point);
		VectorUtil.setMax(clampedPoint, bvhAabbMin);
		VectorUtil.setMin(clampedPoint, bvhAabbMax);

		Vector3f v = VECTORS.get();
		v.sub(clampedPoint, bvhAabbMin);
		VectorUtil.mul(v, v, bvhQuantization);

		int out0 = (int) (v.x + 0.5f) & 0xFFFF;
		int out1 = (int) (v.y + 0.5f) & 0xFFFF;
		int out2 = (int) (v.z + 0.5f) & 0xFFFF;

		VECTORS.release(clampedPoint, v);
		return out0 | (long) out1 << 16 | (long) out2 << 32;
	}

	/**
	 * Un quantize.
	 *
	 * @param vecOut
	 *            the vec out
	 * @param vecIn
	 *            the vec in
	 */
	public void unQuantize(final Vector3f vecOut, final long vecIn) {
		int vecIn0 = (int) (vecIn & 0x00000000FFFFL);
		int vecIn1 = (int) ((vecIn & 0x0000FFFF0000L) >>> 16);
		int vecIn2 = (int) ((vecIn & 0xFFFF00000000L) >>> 32);

		vecOut.x = vecIn0 / bvhQuantization.x;
		vecOut.y = vecIn1 / bvhQuantization.y;
		vecOut.z = vecIn2 / bvhQuantization.z;

		vecOut.add(bvhAabbMin);
	}

}
