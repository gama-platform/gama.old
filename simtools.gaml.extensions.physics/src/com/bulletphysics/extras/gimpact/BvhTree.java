/*******************************************************************************************************
 *
 * BvhTree.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.AABBS;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
class BvhTree {

	/** The num nodes. */
	protected int num_nodes = 0;
	
	/** The node array. */
	protected BvhTreeNodeArray node_array = new BvhTreeNodeArray();

	/**
	 * Calc splitting axis.
	 *
	 * @param primitive_boxes the primitive boxes
	 * @param startIndex the start index
	 * @param endIndex the end index
	 * @return the int
	 */
	protected int _calc_splitting_axis(final BvhDataArray primitive_boxes, final int startIndex, final int endIndex) {
		Vector3f means = VECTORS.get();
		means.set(0f, 0f, 0f);
		Vector3f variance = VECTORS.get();
		variance.set(0f, 0f, 0f);

		int numIndices = endIndex - startIndex;

		Vector3f center = VECTORS.get();
		Vector3f diff2 = VECTORS.get();

		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();

		for (int i = startIndex; i < endIndex; i++) {
			primitive_boxes.getBoundMax(i, tmp1);
			primitive_boxes.getBoundMin(i, tmp2);
			center.add(tmp1, tmp2);
			center.scale(0.5f);
			means.add(center);
		}
		means.scale(1f / numIndices);

		for (int i = startIndex; i < endIndex; i++) {
			primitive_boxes.getBoundMax(i, tmp1);
			primitive_boxes.getBoundMin(i, tmp2);
			center.add(tmp1, tmp2);
			center.scale(0.5f);
			diff2.sub(center, means);
			VectorUtil.mul(diff2, diff2, diff2);
			variance.add(diff2);
		}
		variance.scale(1f / (numIndices - 1));
		VECTORS.release(means, variance, tmp1, tmp2);
		return VectorUtil.maxAxis(variance);
	}

	/**
	 * Sort and calc splitting index.
	 *
	 * @param primitive_boxes the primitive boxes
	 * @param startIndex the start index
	 * @param endIndex the end index
	 * @param splitAxis the split axis
	 * @return the int
	 */
	protected int _sort_and_calc_splitting_index(final BvhDataArray primitive_boxes, final int startIndex,
			final int endIndex, final int splitAxis) {
		int splitIndex = startIndex;
		int numIndices = endIndex - startIndex;

		// average of centers
		float splitValue = 0.0f;

		Vector3f means = VECTORS.get();
		means.set(0f, 0f, 0f);

		Vector3f center = VECTORS.get();

		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();

		for (int i = startIndex; i < endIndex; i++) {
			primitive_boxes.getBoundMax(i, tmp1);
			primitive_boxes.getBoundMin(i, tmp2);
			center.add(tmp1, tmp2);
			center.scale(0.5f);
			means.add(center);
		}
		means.scale(1f / numIndices);

		splitValue = VectorUtil.getCoord(means, splitAxis);

		// sort leafNodes so all values larger then splitValue comes first, and smaller values start from 'splitIndex'.
		for (int i = startIndex; i < endIndex; i++) {
			primitive_boxes.getBoundMax(i, tmp1);
			primitive_boxes.getBoundMin(i, tmp2);
			center.add(tmp1, tmp2);
			center.scale(0.5f);

			if (VectorUtil.getCoord(center, splitAxis) > splitValue) {
				// swap
				primitive_boxes.swap(i, splitIndex);
				// swapLeafNodes(i,splitIndex);
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
		VECTORS.release(means, center, tmp1, tmp2);
		return splitIndex;
	}

	/**
	 * Builds the sub tree.
	 *
	 * @param primitive_boxes the primitive boxes
	 * @param startIndex the start index
	 * @param endIndex the end index
	 */
	protected void _build_sub_tree(final BvhDataArray primitive_boxes, final int startIndex, final int endIndex) {
		int curIndex = num_nodes;
		num_nodes++;

		assert endIndex - startIndex > 0;

		if (endIndex - startIndex == 1) {
			// We have a leaf node
			// setNodeBound(curIndex,primitive_boxes[startIndex].m_bound);
			// m_node_array[curIndex].setDataIndex(primitive_boxes[startIndex].m_data);
			node_array.set(curIndex, primitive_boxes, startIndex);

			return;
		}
		// calculate Best Splitting Axis and where to split it. Sort the incoming 'leafNodes' array within range
		// 'startIndex/endIndex'.

		// split axis
		int splitIndex = _calc_splitting_axis(primitive_boxes, startIndex, endIndex);

		splitIndex = _sort_and_calc_splitting_index(primitive_boxes, startIndex, endIndex, splitIndex);

		// calc this node bounding box

		AABB node_bound = AABBS.get();
		AABB tmpAABB = AABBS.get();

		node_bound.invalidate();

		for (int i = startIndex; i < endIndex; i++) {
			primitive_boxes.getBound(i, tmpAABB);
			node_bound.merge(tmpAABB);
		}

		setNodeBound(curIndex, node_bound);

		// build left branch
		_build_sub_tree(primitive_boxes, startIndex, splitIndex);

		// build right branch
		_build_sub_tree(primitive_boxes, splitIndex, endIndex);

		node_array.setEscapeIndex(curIndex, num_nodes - curIndex);
	}

	/**
	 * Builds the tree.
	 *
	 * @param primitive_boxes the primitive boxes
	 */
	public void build_tree(final BvhDataArray primitive_boxes) {
		// initialize node count to 0
		num_nodes = 0;
		// allocate nodes
		node_array.resize(primitive_boxes.size() * 2);

		_build_sub_tree(primitive_boxes, 0, primitive_boxes.size());
	}

	/**
	 * Clear nodes.
	 */
	public void clearNodes() {
		node_array.clear();
		num_nodes = 0;
	}

	/**
	 * Gets the node count.
	 *
	 * @return the node count
	 */
	public int getNodeCount() {
		return num_nodes;
	}

	/**
	 * Tells if the node is a leaf.
	 */
	public boolean isLeafNode(final int nodeindex) {
		return node_array.isLeafNode(nodeindex);
	}

	/**
	 * Gets the node data.
	 *
	 * @param nodeindex the nodeindex
	 * @return the node data
	 */
	public int getNodeData(final int nodeindex) {
		return node_array.getDataIndex(nodeindex);
	}

	/**
	 * Gets the node bound.
	 *
	 * @param nodeindex the nodeindex
	 * @param bound the bound
	 * @return the node bound
	 */
	public void getNodeBound(final int nodeindex, final AABB bound) {
		node_array.getBound(nodeindex, bound);
	}

	/**
	 * Sets the node bound.
	 *
	 * @param nodeindex the nodeindex
	 * @param bound the bound
	 */
	public void setNodeBound(final int nodeindex, final AABB bound) {
		node_array.setBound(nodeindex, bound);
	}

	/**
	 * Gets the left node.
	 *
	 * @param nodeindex the nodeindex
	 * @return the left node
	 */
	public int getLeftNode(final int nodeindex) {
		return nodeindex + 1;
	}

	/**
	 * Gets the right node.
	 *
	 * @param nodeindex the nodeindex
	 * @return the right node
	 */
	public int getRightNode(final int nodeindex) {
		if (node_array.isLeafNode(nodeindex + 1)) return nodeindex + 2;
		return nodeindex + 1 + node_array.getEscapeIndex(nodeindex + 1);
	}

	/**
	 * Gets the escape node index.
	 *
	 * @param nodeindex the nodeindex
	 * @return the escape node index
	 */
	public int getEscapeNodeIndex(final int nodeindex) {
		return node_array.getEscapeIndex(nodeindex);
	}

	/**
	 * Gets the node pointer.
	 *
	 * @return the node pointer
	 */
	public BvhTreeNodeArray get_node_pointer() {
		return node_array;
	}

}
