/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371. email: projectileman@yahoo.com
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

	protected int num_nodes = 0;
	protected BvhTreeNodeArray node_array = new BvhTreeNodeArray();

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

	public void build_tree(final BvhDataArray primitive_boxes) {
		// initialize node count to 0
		num_nodes = 0;
		// allocate nodes
		node_array.resize(primitive_boxes.size() * 2);

		_build_sub_tree(primitive_boxes, 0, primitive_boxes.size());
	}

	public void clearNodes() {
		node_array.clear();
		num_nodes = 0;
	}

	public int getNodeCount() {
		return num_nodes;
	}

	/**
	 * Tells if the node is a leaf.
	 */
	public boolean isLeafNode(final int nodeindex) {
		return node_array.isLeafNode(nodeindex);
	}

	public int getNodeData(final int nodeindex) {
		return node_array.getDataIndex(nodeindex);
	}

	public void getNodeBound(final int nodeindex, final AABB bound) {
		node_array.getBound(nodeindex, bound);
	}

	public void setNodeBound(final int nodeindex, final AABB bound) {
		node_array.setBound(nodeindex, bound);
	}

	public int getLeftNode(final int nodeindex) {
		return nodeindex + 1;
	}

	public int getRightNode(final int nodeindex) {
		if (node_array.isLeafNode(nodeindex + 1)) return nodeindex + 2;
		return nodeindex + 1 + node_array.getEscapeIndex(nodeindex + 1);
	}

	public int getEscapeNodeIndex(final int nodeindex) {
		return node_array.getEscapeIndex(nodeindex);
	}

	public BvhTreeNodeArray get_node_pointer() {
		return node_array;
	}

}
