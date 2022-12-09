/*******************************************************************************************************
 *
 * ObjectArrayList.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.RandomAccess;

/**
 *
 * @author jezek2
 */
public final class ObjectArrayList<T> extends AbstractList<T> implements RandomAccess, Externalizable {

	/** The array. */
	private T[] array;
	
	/** The size. */
	private int size;

	/**
	 * Instantiates a new object array list.
	 */
	public ObjectArrayList() {
		this(16);
	}

	/**
	 * Instantiates a new object array list.
	 *
	 * @param initialCapacity the initial capacity
	 */
	@SuppressWarnings ("unchecked")
	public ObjectArrayList(final int initialCapacity) {
		array = (T[]) new Object[initialCapacity];
	}

	@Override
	public boolean add(final T value) {
		if (size == array.length) { expand(); }

		array[size++] = value;
		return true;
	}

	@Override
	public void add(final int index, final T value) {
		if (size == array.length) { expand(); }

		int num = size - index;
		if (num > 0) { System.arraycopy(array, index, array, index + 1, num); }

		array[index] = value;
		size++;
	}

	@Override
	public T remove(final int index) {
		if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
		T prev = array[index];
		System.arraycopy(array, index + 1, array, index, size - index - 1);
		array[size - 1] = null;
		size--;
		return prev;
	}

	/**
	 * Expand.
	 */
	@SuppressWarnings ("unchecked")
	private void expand() {
		T[] newArray = (T[]) new Object[array.length << 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}

	/**
	 * Removes the quick.
	 *
	 * @param index the index
	 */
	public void removeQuick(final int index) {
		System.arraycopy(array, index + 1, array, index, size - index - 1);
		array[size - 1] = null;
		size--;
	}

	@Override
	public T get(final int index) {
		if (index >= size) throw new IndexOutOfBoundsException();
		return array[index];
	}

	/**
	 * Gets the quick.
	 *
	 * @param index the index
	 * @return the quick
	 */
	public T getQuick(final int index) {
		return array[index];
	}

	@Override
	public T set(final int index, final T value) {
		if (index >= size) throw new IndexOutOfBoundsException();
		T old = array[index];
		array[index] = value;
		return old;
	}

	/**
	 * Sets the quick.
	 *
	 * @param index the index
	 * @param value the value
	 */
	public void setQuick(final int index, final T value) {
		array[index] = value;
	}

	@Override
	public int size() {
		return size;
	}

	/**
	 * Capacity.
	 *
	 * @return the int
	 */
	public int capacity() {
		return array.length;
	}

	@Override
	public void clear() {
		size = 0;
	}

	@Override
	public int indexOf(final Object o) {
		int _size = size;
		T[] _array = array;
		for (int i = 0; i < _size; i++) { if (o == null ? _array[i] == null : o.equals(_array[i])) return i; }
		return -1;
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(size);
		for (int i = 0; i < size; i++) { out.writeObject(array[i]); }
	}

	@Override
	@SuppressWarnings ("unchecked")
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		size = in.readInt();
		int cap = 16;
		while (cap < size) { cap <<= 1; }
		array = (T[]) new Object[cap];
		for (int i = 0; i < size; i++) { array[i] = (T) in.readObject(); }
	}

}
