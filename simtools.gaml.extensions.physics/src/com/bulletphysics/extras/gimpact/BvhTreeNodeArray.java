/*******************************************************************************************************
 *
 * BvhTreeNodeArray.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.extras.gimpact.BoxCollision.AABB;

/**
 *
 * @author jezek2
 */
class BvhTreeNodeArray {

	/** The size. */
	private int size = 0;
	
	/** The bound. */
	private float[] bound = new float[0];
	
	/** The escape index or data index. */
	private int[] escapeIndexOrDataIndex = new int[0];

	/**
	 * Clear.
	 */
	public void clear() {
		size = 0;
	}

	/**
	 * Resize.
	 *
	 * @param newSize the new size
	 */
	public void resize(int newSize) {
		float[] newBound = new float[newSize*6];
		int[] newEIODI = new int[newSize];
		
		System.arraycopy(bound, 0, newBound, 0, size*6);
		System.arraycopy(escapeIndexOrDataIndex, 0, newEIODI, 0, size);
		
		bound = newBound;
		escapeIndexOrDataIndex = newEIODI;
		
		size = newSize;
	}
	
	/**
	 * Sets the.
	 *
	 * @param destIdx the dest idx
	 * @param array the array
	 * @param srcIdx the src idx
	 */
	public void set(int destIdx, BvhTreeNodeArray array, int srcIdx) {
		int dpos = destIdx*6;
		int spos = srcIdx*6;
		
		bound[dpos+0] = array.bound[spos+0];
		bound[dpos+1] = array.bound[spos+1];
		bound[dpos+2] = array.bound[spos+2];
		bound[dpos+3] = array.bound[spos+3];
		bound[dpos+4] = array.bound[spos+4];
		bound[dpos+5] = array.bound[spos+5];
		escapeIndexOrDataIndex[destIdx] = array.escapeIndexOrDataIndex[srcIdx];
	}

	/**
	 * Sets the.
	 *
	 * @param destIdx the dest idx
	 * @param array the array
	 * @param srcIdx the src idx
	 */
	public void set(int destIdx, BvhDataArray array, int srcIdx) {
		int dpos = destIdx*6;
		int spos = srcIdx*6;
		
		bound[dpos+0] = array.bound[spos+0];
		bound[dpos+1] = array.bound[spos+1];
		bound[dpos+2] = array.bound[spos+2];
		bound[dpos+3] = array.bound[spos+3];
		bound[dpos+4] = array.bound[spos+4];
		bound[dpos+5] = array.bound[spos+5];
		escapeIndexOrDataIndex[destIdx] = array.data[srcIdx];
	}
	
	/**
	 * Gets the bound.
	 *
	 * @param nodeIndex the node index
	 * @param out the out
	 * @return the bound
	 */
	public AABB getBound(int nodeIndex, AABB out) {
		int pos = nodeIndex*6;
		out.min.set(bound[pos+0], bound[pos+1], bound[pos+2]);
		out.max.set(bound[pos+3], bound[pos+4], bound[pos+5]);
		return out;
	}
	
	/**
	 * Sets the bound.
	 *
	 * @param nodeIndex the node index
	 * @param aabb the aabb
	 */
	public void setBound(int nodeIndex, AABB aabb) {
		int pos = nodeIndex*6;
		bound[pos+0] = aabb.min.x;
		bound[pos+1] = aabb.min.y;
		bound[pos+2] = aabb.min.z;
		bound[pos+3] = aabb.max.x;
		bound[pos+4] = aabb.max.y;
		bound[pos+5] = aabb.max.z;
	}
	
	/**
	 * Checks if is leaf node.
	 *
	 * @param nodeIndex the node index
	 * @return true, if is leaf node
	 */
	public boolean isLeafNode(int nodeIndex) {
		// skipindex is negative (internal node), triangleindex >=0 (leafnode)
		return (escapeIndexOrDataIndex[nodeIndex] >= 0);
	}

	/**
	 * Gets the escape index.
	 *
	 * @param nodeIndex the node index
	 * @return the escape index
	 */
	public int getEscapeIndex(int nodeIndex) {
		//btAssert(m_escapeIndexOrDataIndex < 0);
		return -escapeIndexOrDataIndex[nodeIndex];
	}

	/**
	 * Sets the escape index.
	 *
	 * @param nodeIndex the node index
	 * @param index the index
	 */
	public void setEscapeIndex(int nodeIndex, int index) {
		escapeIndexOrDataIndex[nodeIndex] = -index;
	}

	/**
	 * Gets the data index.
	 *
	 * @param nodeIndex the node index
	 * @return the data index
	 */
	public int getDataIndex(int nodeIndex) {
		//btAssert(m_escapeIndexOrDataIndex >= 0);
		return escapeIndexOrDataIndex[nodeIndex];
	}

	/**
	 * Sets the data index.
	 *
	 * @param nodeIndex the node index
	 * @param index the index
	 */
	public void setDataIndex(int nodeIndex, int index) {
		escapeIndexOrDataIndex[nodeIndex] = index;
	}
	
}
