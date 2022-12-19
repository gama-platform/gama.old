/*******************************************************************************************************
 *
 * BvhDataArray.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
class BvhDataArray {

	/** The size. */
	private int size = 0;
	
	/** The bound. */
	float[] bound = new float[0];
	
	/** The data. */
	int[] data = new int[0];

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return size;
	}

	/**
	 * Resize.
	 *
	 * @param newSize the new size
	 */
	public void resize(int newSize) {
		float[] newBound = new float[newSize*6];
		int[] newData = new int[newSize];
		
		System.arraycopy(bound, 0, newBound, 0, size*6);
		System.arraycopy(data, 0, newData, 0, size);
		
		bound = newBound;
		data = newData;
		
		size = newSize;
	}
	
	/**
	 * Swap.
	 *
	 * @param idx1 the idx 1
	 * @param idx2 the idx 2
	 */
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
	
	/**
	 * Gets the bound.
	 *
	 * @param idx the idx
	 * @param out the out
	 * @return the bound
	 */
	public AABB getBound(int idx, AABB out) {
		int pos = idx*6;
		out.min.set(bound[pos+0], bound[pos+1], bound[pos+2]);
		out.max.set(bound[pos+3], bound[pos+4], bound[pos+5]);
		return out;
	}

	/**
	 * Gets the bound min.
	 *
	 * @param idx the idx
	 * @param out the out
	 * @return the bound min
	 */
	public Vector3f getBoundMin(int idx, Vector3f out) {
		int pos = idx*6;
		out.set(bound[pos+0], bound[pos+1], bound[pos+2]);
		return out;
	}

	/**
	 * Gets the bound max.
	 *
	 * @param idx the idx
	 * @param out the out
	 * @return the bound max
	 */
	public Vector3f getBoundMax(int idx, Vector3f out) {
		int pos = idx*6;
		out.set(bound[pos+3], bound[pos+4], bound[pos+5]);
		return out;
	}
	
	/**
	 * Sets the bound.
	 *
	 * @param idx the idx
	 * @param aabb the aabb
	 */
	public void setBound(int idx, AABB aabb) {
		int pos = idx*6;
		bound[pos+0] = aabb.min.x;
		bound[pos+1] = aabb.min.y;
		bound[pos+2] = aabb.min.z;
		bound[pos+3] = aabb.max.x;
		bound[pos+4] = aabb.max.y;
		bound[pos+5] = aabb.max.z;
	}
	
	/**
	 * Gets the data.
	 *
	 * @param idx the idx
	 * @return the data
	 */
	public int getData(int idx) {
		return data[idx];
	}
	
	/**
	 * Sets the data.
	 *
	 * @param idx the idx
	 * @param value the value
	 */
	public void setData(int idx, int value) {
		data[idx] = value;
	}
	
}
