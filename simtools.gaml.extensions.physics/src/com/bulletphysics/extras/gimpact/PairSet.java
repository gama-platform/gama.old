/*******************************************************************************************************
 *
 * PairSet.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

/**
 *
 * @author jezek2
 */
class PairSet {

	/** The array. */
	private Pair[] array;
	
	/** The size. */
	private int size = 0;
	
	/**
	 * Instantiates a new pair set.
	 */
	public PairSet() {
		array = new Pair[32];
		for (int i=0; i<array.length; i++) {
			array[i] = new Pair();
		}
	}
	
	/**
	 * Clear.
	 */
	public void clear() {
		size = 0;
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
	 * Gets the.
	 *
	 * @param index the index
	 * @return the pair
	 */
	public Pair get(int index) {
		if (index >= size) throw new IndexOutOfBoundsException();
		return array[index];
	}
	
	/**
	 * Expand.
	 */
	@SuppressWarnings("unchecked")
	private void expand() {
		Pair[] newArray = new Pair[array.length << 1];
		for (int i=array.length; i<newArray.length; i++) {
			newArray[i] = new Pair();
		}
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}

	/**
	 * Push pair.
	 *
	 * @param index1 the index 1
	 * @param index2 the index 2
	 */
	public void push_pair(int index1, int index2) {
		if (size == array.length) {
			expand();
		}
		array[size].index1 = index1;
		array[size].index2 = index2;
		size++;
	}

	/**
	 * Push pair inv.
	 *
	 * @param index1 the index 1
	 * @param index2 the index 2
	 */
	public void push_pair_inv(int index1, int index2) {
		if (size == array.length) {
			expand();
		}
		array[size].index1 = index2;
		array[size].index2 = index1;
		size++;
	}
	
}
