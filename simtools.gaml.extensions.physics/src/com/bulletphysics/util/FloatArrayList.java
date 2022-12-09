/*******************************************************************************************************
 *
 * FloatArrayList.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.util;

/**
 *
 * @author jezek2
 */
public class FloatArrayList {

	/** The array. */
	private float[] array = new float[16];
	
	/** The size. */
	private int size;
	
	/**
	 * Adds the.
	 *
	 * @param value the value
	 */
	public void add(float value) {
		if (size == array.length) {
			expand();
		}
		
		array[size++] = value;
	}
	
	/**
	 * Expand.
	 */
	private void expand() {
		float[] newArray = new float[array.length << 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}

	/**
	 * Removes the.
	 *
	 * @param index the index
	 * @return the float
	 */
	public float remove(int index) {
		if (index >= size) throw new IndexOutOfBoundsException();
		float old = array[index];
		System.arraycopy(array, index+1, array, index, size - index - 1);
		size--;
		return old;
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the float
	 */
	public float get(int index) {
		if (index >= size) throw new IndexOutOfBoundsException();
		return array[index];
	}

	/**
	 * Sets the.
	 *
	 * @param index the index
	 * @param value the value
	 */
	public void set(int index, float value) {
		if (index >= size) throw new IndexOutOfBoundsException();
		array[index] = value;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return size;
	}

}
