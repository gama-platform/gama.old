/*******************************************************************************************************
 *
 * IntArrayList.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
public class IntArrayList {

	/** The array. */
	private int[] array = new int[16];
	
	/** The size. */
	private int size;
	
	/**
	 * Adds the.
	 *
	 * @param value the value
	 */
	public void add(int value) {
		if (size == array.length) {
			expand();
		}
		
		array[size++] = value;
	}
	
	/**
	 * Expand.
	 */
	private void expand() {
		int[] newArray = new int[array.length << 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}

	/**
	 * Removes the.
	 *
	 * @param index the index
	 * @return the int
	 */
	public int remove(int index) {
		if (index >= size) throw new IndexOutOfBoundsException();
		int old = array[index];
		System.arraycopy(array, index+1, array, index, size - index - 1);
		size--;
		return old;
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the int
	 */
	public int get(int index) {
		if (index >= size) throw new IndexOutOfBoundsException();
		return array[index];
	}

	/**
	 * Sets the.
	 *
	 * @param index the index
	 * @param value the value
	 */
	public void set(int index, int value) {
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
	
	/**
	 * Clear.
	 */
	public void clear() {
		size = 0;
	}

}
