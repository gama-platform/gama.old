/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.collision.shapes;

import java.io.Serializable;

/**
 * BvhSubtreeInfo provides info to gather a subtree of limited size.
 * 
 * @author jezek2
 */
public class BvhSubtreeInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final /*unsigned*/ short[] quantizedAabbMin = new short[3];
	public final /*unsigned*/ short[] quantizedAabbMax = new short[3];
	// points to the root of the subtree
	public int rootNodeIndex;
	public int subtreeSize;

	public void setAabbFromQuantizeNode(QuantizedBvhNodes quantizedNodes, int nodeId) {
		quantizedAabbMin[0] = (short)quantizedNodes.getQuantizedAabbMin(nodeId, 0);
		quantizedAabbMin[1] = (short)quantizedNodes.getQuantizedAabbMin(nodeId, 1);
		quantizedAabbMin[2] = (short)quantizedNodes.getQuantizedAabbMin(nodeId, 2);
		quantizedAabbMax[0] = (short)quantizedNodes.getQuantizedAabbMax(nodeId, 0);
		quantizedAabbMax[1] = (short)quantizedNodes.getQuantizedAabbMax(nodeId, 1);
		quantizedAabbMax[2] = (short)quantizedNodes.getQuantizedAabbMax(nodeId, 2);
	}

}
