/*******************************************************************************************************
 *
 * BvhSubtreeInfo.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import java.io.Serializable;

/**
 * BvhSubtreeInfo provides info to gather a subtree of limited size.
 * 
 * @author jezek2
 */
public class BvhSubtreeInfo implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The quantized aabb min. */
	public final /*unsigned*/ short[] quantizedAabbMin = new short[3];
	
	/** The quantized aabb max. */
	public final /*unsigned*/ short[] quantizedAabbMax = new short[3];
	
	/** The root node index. */
	// points to the root of the subtree
	public int rootNodeIndex;
	
	/** The subtree size. */
	public int subtreeSize;

	/**
	 * Sets the aabb from quantize node.
	 *
	 * @param quantizedNodes the quantized nodes
	 * @param nodeId the node id
	 */
	public void setAabbFromQuantizeNode(QuantizedBvhNodes quantizedNodes, int nodeId) {
		quantizedAabbMin[0] = (short)quantizedNodes.getQuantizedAabbMin(nodeId, 0);
		quantizedAabbMin[1] = (short)quantizedNodes.getQuantizedAabbMin(nodeId, 1);
		quantizedAabbMin[2] = (short)quantizedNodes.getQuantizedAabbMin(nodeId, 2);
		quantizedAabbMax[0] = (short)quantizedNodes.getQuantizedAabbMax(nodeId, 0);
		quantizedAabbMax[1] = (short)quantizedNodes.getQuantizedAabbMax(nodeId, 1);
		quantizedAabbMax[2] = (short)quantizedNodes.getQuantizedAabbMax(nodeId, 2);
	}

}
