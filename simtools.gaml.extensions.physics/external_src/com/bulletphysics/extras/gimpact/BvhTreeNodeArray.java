/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371.
 * email: projectileman@yahoo.com
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

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.extras.gimpact.BoxCollision.AABB;

/**
 *
 * @author jezek2
 */
class BvhTreeNodeArray {

	private int size = 0;
	
	private float[] bound = new float[0];
	private int[] escapeIndexOrDataIndex = new int[0];

	public void clear() {
		size = 0;
	}

	public void resize(int newSize) {
		float[] newBound = new float[newSize*6];
		int[] newEIODI = new int[newSize];
		
		System.arraycopy(bound, 0, newBound, 0, size*6);
		System.arraycopy(escapeIndexOrDataIndex, 0, newEIODI, 0, size);
		
		bound = newBound;
		escapeIndexOrDataIndex = newEIODI;
		
		size = newSize;
	}
	
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
	
	public AABB getBound(int nodeIndex, AABB out) {
		int pos = nodeIndex*6;
		out.min.set(bound[pos+0], bound[pos+1], bound[pos+2]);
		out.max.set(bound[pos+3], bound[pos+4], bound[pos+5]);
		return out;
	}
	
	public void setBound(int nodeIndex, AABB aabb) {
		int pos = nodeIndex*6;
		bound[pos+0] = aabb.min.x;
		bound[pos+1] = aabb.min.y;
		bound[pos+2] = aabb.min.z;
		bound[pos+3] = aabb.max.x;
		bound[pos+4] = aabb.max.y;
		bound[pos+5] = aabb.max.z;
	}
	
	public boolean isLeafNode(int nodeIndex) {
		// skipindex is negative (internal node), triangleindex >=0 (leafnode)
		return (escapeIndexOrDataIndex[nodeIndex] >= 0);
	}

	public int getEscapeIndex(int nodeIndex) {
		//btAssert(m_escapeIndexOrDataIndex < 0);
		return -escapeIndexOrDataIndex[nodeIndex];
	}

	public void setEscapeIndex(int nodeIndex, int index) {
		escapeIndexOrDataIndex[nodeIndex] = -index;
	}

	public int getDataIndex(int nodeIndex) {
		//btAssert(m_escapeIndexOrDataIndex >= 0);
		return escapeIndexOrDataIndex[nodeIndex];
	}

	public void setDataIndex(int nodeIndex, int index) {
		escapeIndexOrDataIndex[nodeIndex] = index;
	}
	
}
