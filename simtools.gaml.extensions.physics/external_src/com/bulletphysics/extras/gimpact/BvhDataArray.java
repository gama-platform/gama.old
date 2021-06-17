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
import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
class BvhDataArray {

	private int size = 0;
	
	float[] bound = new float[0];
	int[] data = new int[0];

	public int size() {
		return size;
	}

	public void resize(int newSize) {
		float[] newBound = new float[newSize*6];
		int[] newData = new int[newSize];
		
		System.arraycopy(bound, 0, newBound, 0, size*6);
		System.arraycopy(data, 0, newData, 0, size);
		
		bound = newBound;
		data = newData;
		
		size = newSize;
	}
	
	public void swap(int idx1, int idx2) {
		int pos1 = idx1*6;
		int pos2 = idx2*6;
		
		float b0 = bound[pos1+0];
		float b1 = bound[pos1+1];
		float b2 = bound[pos1+2];
		float b3 = bound[pos1+3];
		float b4 = bound[pos1+4];
		float b5 = bound[pos1+5];
		int d = data[idx1];
		
		bound[pos1+0] = bound[pos2+0];
		bound[pos1+1] = bound[pos2+1];
		bound[pos1+2] = bound[pos2+2];
		bound[pos1+3] = bound[pos2+3];
		bound[pos1+4] = bound[pos2+4];
		bound[pos1+5] = bound[pos2+5];
		data[idx1] = data[idx2];

		bound[pos2+0] = b0;
		bound[pos2+1] = b1;
		bound[pos2+2] = b2;
		bound[pos2+3] = b3;
		bound[pos2+4] = b4;
		bound[pos2+5] = b5;
		data[idx2] = d;
	}
	
	public AABB getBound(int idx, AABB out) {
		int pos = idx*6;
		out.min.set(bound[pos+0], bound[pos+1], bound[pos+2]);
		out.max.set(bound[pos+3], bound[pos+4], bound[pos+5]);
		return out;
	}

	public Vector3f getBoundMin(int idx, Vector3f out) {
		int pos = idx*6;
		out.set(bound[pos+0], bound[pos+1], bound[pos+2]);
		return out;
	}

	public Vector3f getBoundMax(int idx, Vector3f out) {
		int pos = idx*6;
		out.set(bound[pos+3], bound[pos+4], bound[pos+5]);
		return out;
	}
	
	public void setBound(int idx, AABB aabb) {
		int pos = idx*6;
		bound[pos+0] = aabb.min.x;
		bound[pos+1] = aabb.min.y;
		bound[pos+2] = aabb.min.z;
		bound[pos+3] = aabb.max.x;
		bound[pos+4] = aabb.max.y;
		bound[pos+5] = aabb.max.z;
	}
	
	public int getData(int idx) {
		return data[idx];
	}
	
	public void setData(int idx, int value) {
		data[idx] = value;
	}
	
}
