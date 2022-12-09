/*******************************************************************************************************
 *
 * GImpactBvh.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.AABBS;
import static com.bulletphysics.Pools.BBTCS;

import javax.vecmath.Vector3f;

import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import com.bulletphysics.extras.gimpact.BoxCollision.BoxBoxTransformCache;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.IntArrayList;

/**
 *
 * @author jezek2
 */
class GImpactBvh {

	/** The box tree. */
	protected BvhTree box_tree = new BvhTree();
	
	/** The primitive manager. */
	protected PrimitiveManagerBase primitive_manager;

	/**
	 * This constructor doesn't build the tree. you must call buildSet.
	 */
	public GImpactBvh() {
		primitive_manager = null;
	}

	/**
	 * This constructor doesn't build the tree. you must call buildSet.
	 */
	public GImpactBvh(final PrimitiveManagerBase primitive_manager) {
		this.primitive_manager = primitive_manager;
	}

	/**
	 * Gets the global box.
	 *
	 * @param out the out
	 * @return the global box
	 */
	public AABB getGlobalBox(final AABB out) {
		getNodeBound(0, out);
		return out;
	}

	/**
	 * Sets the primitive manager.
	 *
	 * @param primitive_manager the new primitive manager
	 */
	public void setPrimitiveManager(final PrimitiveManagerBase primitive_manager) {
		this.primitive_manager = primitive_manager;
	}

	/**
	 * Gets the primitive manager.
	 *
	 * @return the primitive manager
	 */
	public PrimitiveManagerBase getPrimitiveManager() {
		return primitive_manager;
	}

	/**
	 * Refit.
	 */
	// stackless refit
	protected void refit() {
		AABB leafbox = AABBS.get();
		AABB bound = AABBS.get();
		AABB temp_box = AABBS.get();

		int nodecount = getNodeCount();
		while (nodecount-- != 0) {
			if (isLeafNode(nodecount)) {
				primitive_manager.get_primitive_box(getNodeData(nodecount), leafbox);
				setNodeBound(nodecount, leafbox);
			} else {
				// const BT_BVH_TREE_NODE * nodepointer = get_node_pointer(nodecount);
				// get left bound
				bound.invalidate();

				int child_node = getLeftNode(nodecount);
				if (child_node != 0) {
					getNodeBound(child_node, temp_box);
					bound.merge(temp_box);
				}

				child_node = getRightNode(nodecount);
				if (child_node != 0) {
					getNodeBound(child_node, temp_box);
					bound.merge(temp_box);
				}

				setNodeBound(nodecount, bound);
			}
		}
	}

	/**
	 * This attemps to refit the box set.
	 */
	public void update() {
		refit();
	}

	/**
	 * This rebuild the entire set.
	 */
	public void buildSet() {
		// obtain primitive boxes
		BvhDataArray primitive_boxes = new BvhDataArray();
		primitive_boxes.resize(primitive_manager.get_primitive_count());

		AABB tmpAABB = AABBS.get();

		for (int i = 0; i < primitive_boxes.size(); i++) {
			// primitive_manager.get_primitive_box(i,primitive_boxes[i].bound);
			primitive_manager.get_primitive_box(i, tmpAABB);
			primitive_boxes.setBound(i, tmpAABB);

			primitive_boxes.setData(i, i);
		}

		box_tree.build_tree(primitive_boxes);
	}

	/**
	 * Returns the indices of the primitives in the primitive_manager field.
	 */
	public boolean boxQuery(final AABB box, final IntArrayList collided_results) {
		int curIndex = 0;
		int numNodes = getNodeCount();

		AABB bound = AABBS.get();

		while (curIndex < numNodes) {
			getNodeBound(curIndex, bound);

			// catch bugs in tree data

			boolean aabbOverlap = bound.has_collision(box);
			boolean isleafnode = isLeafNode(curIndex);

			if (isleafnode && aabbOverlap) { collided_results.add(getNodeData(curIndex)); }

			if (aabbOverlap || isleafnode) {
				// next subnode
				curIndex++;
			} else {
				// skip node
				curIndex += getEscapeNodeIndex(curIndex);
			}
		}
		if (collided_results.size() > 0) return true;
		return false;
	}

	/**
	 * Returns the indices of the primitives in the primitive_manager field.
	 */
	public boolean boxQueryTrans(final AABB box, final Transform transform, final IntArrayList collided_results) {
		AABB transbox = AABBS.get(box);
		transbox.appy_transform(transform);
		return boxQuery(transbox, collided_results);
	}

	/**
	 * Returns the indices of the primitives in the primitive_manager field.
	 */
	public boolean rayQuery(final Vector3f ray_dir, final Vector3f ray_origin, final IntArrayList collided_results) {
		int curIndex = 0;
		int numNodes = getNodeCount();

		AABB bound = AABBS.get();

		while (curIndex < numNodes) {
			getNodeBound(curIndex, bound);

			// catch bugs in tree data

			boolean aabbOverlap = bound.collide_ray(ray_origin, ray_dir);
			boolean isleafnode = isLeafNode(curIndex);

			if (isleafnode && aabbOverlap) { collided_results.add(getNodeData(curIndex)); }

			if (aabbOverlap || isleafnode) {
				// next subnode
				curIndex++;
			} else {
				// skip node
				curIndex += getEscapeNodeIndex(curIndex);
			}
		}
		if (collided_results.size() > 0) return true;
		return false;
	}

	/**
	 * Tells if this set has hierarchy.
	 */
	public boolean hasHierarchy() {
		return true;
	}

	/**
	 * Tells if this set is a trimesh.
	 */
	public boolean isTrimesh() {
		return primitive_manager.is_trimesh();
	}

	/**
	 * Gets the node count.
	 *
	 * @return the node count
	 */
	public int getNodeCount() {
		return box_tree.getNodeCount();
	}

	/**
	 * Tells if the node is a leaf.
	 */
	public boolean isLeafNode(final int nodeindex) {
		return box_tree.isLeafNode(nodeindex);
	}

	/**
	 * Gets the node data.
	 *
	 * @param nodeindex the nodeindex
	 * @return the node data
	 */
	public int getNodeData(final int nodeindex) {
		return box_tree.getNodeData(nodeindex);
	}

	/**
	 * Gets the node bound.
	 *
	 * @param nodeindex the nodeindex
	 * @param bound the bound
	 * @return the node bound
	 */
	public void getNodeBound(final int nodeindex, final AABB bound) {
		box_tree.getNodeBound(nodeindex, bound);
	}

	/**
	 * Sets the node bound.
	 *
	 * @param nodeindex the nodeindex
	 * @param bound the bound
	 */
	public void setNodeBound(final int nodeindex, final AABB bound) {
		box_tree.setNodeBound(nodeindex, bound);
	}

	/**
	 * Gets the left node.
	 *
	 * @param nodeindex the nodeindex
	 * @return the left node
	 */
	public int getLeftNode(final int nodeindex) {
		return box_tree.getLeftNode(nodeindex);
	}

	/**
	 * Gets the right node.
	 *
	 * @param nodeindex the nodeindex
	 * @return the right node
	 */
	public int getRightNode(final int nodeindex) {
		return box_tree.getRightNode(nodeindex);
	}

	/**
	 * Gets the escape node index.
	 *
	 * @param nodeindex the nodeindex
	 * @return the escape node index
	 */
	public int getEscapeNodeIndex(final int nodeindex) {
		return box_tree.getEscapeNodeIndex(nodeindex);
	}

	/**
	 * Gets the node triangle.
	 *
	 * @param nodeindex the nodeindex
	 * @param triangle the triangle
	 * @return the node triangle
	 */
	public void getNodeTriangle(final int nodeindex, final PrimitiveTriangle triangle) {
		primitive_manager.get_primitive_triangle(getNodeData(nodeindex), triangle);
	}

	/**
	 * Gets the node pointer.
	 *
	 * @return the node pointer
	 */
	public BvhTreeNodeArray get_node_pointer() {
		return box_tree.get_node_pointer();
	}

	/**
	 * Node collision.
	 *
	 * @param boxset0 the boxset 0
	 * @param boxset1 the boxset 1
	 * @param trans_cache_1to0 the trans cache 1 to 0
	 * @param node0 the node 0
	 * @param node1 the node 1
	 * @param complete_primitive_tests the complete primitive tests
	 * @return true, if successful
	 */
	private static boolean _node_collision(final GImpactBvh boxset0, final GImpactBvh boxset1,
			final BoxBoxTransformCache trans_cache_1to0, final int node0, final int node1,
			final boolean complete_primitive_tests) {
		AABB box0 = AABBS.get();
		boxset0.getNodeBound(node0, box0);
		AABB box1 = AABBS.get();
		boxset1.getNodeBound(node1, box1);

		return box0.overlapping_trans_cache(box1, trans_cache_1to0, complete_primitive_tests);
		// box1.appy_transform_trans_cache(trans_cache_1to0);
		// return box0.has_collision(box1);
	}

	/**
	 * Stackless recursive collision routine.
	 */
	private static void _find_collision_pairs_recursive(final GImpactBvh boxset0, final GImpactBvh boxset1,
			final PairSet collision_pairs, final BoxBoxTransformCache trans_cache_1to0, final int node0,
			final int node1, final boolean complete_primitive_tests) {
		if (_node_collision(boxset0, boxset1, trans_cache_1to0, node0, node1, complete_primitive_tests) == false)
			return;// avoid colliding internal nodes
		if (boxset0.isLeafNode(node0)) {
			if (boxset1.isLeafNode(node1)) {
				// collision result
				collision_pairs.push_pair(boxset0.getNodeData(node0), boxset1.getNodeData(node1));
				return;
			} else {
				// collide left recursive
				_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0, node0,
						boxset1.getLeftNode(node1), false);

				// collide right recursive
				_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0, node0,
						boxset1.getRightNode(node1), false);
			}
		} else {
			if (boxset1.isLeafNode(node1)) {
				// collide left recursive
				_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0,
						boxset0.getLeftNode(node0), node1, false);

				// collide right recursive
				_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0,
						boxset0.getRightNode(node0), node1, false);
			} else {
				// collide left0 left1
				_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0,
						boxset0.getLeftNode(node0), boxset1.getLeftNode(node1), false);

				// collide left0 right1
				_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0,
						boxset0.getLeftNode(node0), boxset1.getRightNode(node1), false);

				// collide right0 left1
				_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0,
						boxset0.getRightNode(node0), boxset1.getLeftNode(node1), false);

				// collide right0 right1
				_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0,
						boxset0.getRightNode(node0), boxset1.getRightNode(node1), false);

			} // else if node1 is not a leaf
		} // else if node0 is not a leaf
	}

	// public static float getAverageTreeCollisionTime();

	/**
	 * Find collision.
	 *
	 * @param boxset0 the boxset 0
	 * @param trans0 the trans 0
	 * @param boxset1 the boxset 1
	 * @param trans1 the trans 1
	 * @param collision_pairs the collision pairs
	 */
	public static void find_collision(final GImpactBvh boxset0, final Transform trans0, final GImpactBvh boxset1,
			final Transform trans1, final PairSet collision_pairs) {
		if (boxset0.getNodeCount() == 0 || boxset1.getNodeCount() == 0) return;
		BoxBoxTransformCache trans_cache_1to0 = BBTCS.get();

		trans_cache_1to0.calc_from_homogenic(trans0, trans1);

		// #ifdef TRI_COLLISION_PROFILING
		// bt_begin_gim02_tree_time();
		// #endif //TRI_COLLISION_PROFILING

		_find_collision_pairs_recursive(boxset0, boxset1, collision_pairs, trans_cache_1to0, 0, 0, true);

		// #ifdef TRI_COLLISION_PROFILING
		// bt_end_gim02_tree_time();
		// #endif //TRI_COLLISION_PROFILING
		BBTCS.release(trans_cache_1to0);
	}

}
