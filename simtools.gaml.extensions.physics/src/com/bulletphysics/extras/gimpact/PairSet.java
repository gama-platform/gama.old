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

/**
 *
 * @author jezek2
 */
class PairSet {

	private Pair[] array;
	private int size = 0;
	
	public PairSet() {
		array = new Pair[32];
		for (int i=0; i<array.length; i++) {
			array[i] = new Pair();
		}
	}
	
	public void clear() {
		size = 0;
	}
	
	public int size() {
		return size;
	}
	
	public Pair get(int index) {
		if (index >= size) throw new IndexOutOfBoundsException();
		return array[index];
	}
	
	@SuppressWarnings("unchecked")
	private void expand() {
		Pair[] newArray = new Pair[array.length << 1];
		for (int i=array.length; i<newArray.length; i++) {
			newArray[i] = new Pair();
		}
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}

	public void push_pair(int index1, int index2) {
		if (size == array.length) {
			expand();
		}
		array[size].index1 = index1;
		array[size].index2 = index2;
		size++;
	}

	public void push_pair_inv(int index1, int index2) {
		if (size == array.length) {
			expand();
		}
		array[size].index1 = index2;
		array[size].index2 = index1;
		size++;
	}
	
}
