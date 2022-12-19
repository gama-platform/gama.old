/*******************************************************************************************************
 *
 * MiscUtil.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

import java.util.List;

import com.bulletphysics.util.FloatArrayList;
import com.bulletphysics.util.IntArrayList;

/**
 * Miscellaneous utility functions.
 *
 * @author jezek2
 */
public class MiscUtil {

	/**
	 * Gets the list capacity for hash.
	 *
	 * @param list the list
	 * @return the list capacity for hash
	 */
	public static int getListCapacityForHash(final List<?> list) {
		return getListCapacityForHash(list.size());
	}

	/**
	 * Gets the list capacity for hash.
	 *
	 * @param size the size
	 * @return the list capacity for hash
	 */
	public static int getListCapacityForHash(final int size) {
		int n = 2;
		while (n < size) {
			n <<= 1;
		}
		return n;
	}

	/**
	 * Ensures valid index in provided list by filling list with provided values until the index is valid.
	 */
	public static <T> void ensureIndex(final List<T> list, final int index, final T value) {
		while (list.size() <= index) {
			list.add(value);
		}
	}

	/**
	 * Resizes list to exact size, filling with given value when expanding.
	 */
	public static void resize(final IntArrayList list, final int size, final int value) {
		while (list.size() < size) {
			list.add(value);
		}

		while (list.size() > size) {
			list.remove(list.size() - 1);
		}
	}

	/**
	 * Resizes list to exact size, filling with given value when expanding.
	 */
	public static void resize(final FloatArrayList list, final int size, final float value) {
		while (list.size() < size) {
			list.add(value);
		}

		while (list.size() > size) {
			list.remove(list.size() - 1);
		}
	}

	/**
	 * Resizes list to exact size, filling with new instances of given class type when expanding.
	 */
	public static <T> void resize(final List<T> list, final int size, final Class<T> valueCls) {
		try {
			while (list.size() < size) {
				list.add(valueCls != null ? valueCls.newInstance() : null);
			}

			while (list.size() > size) {
				list.remove(list.size() - 1);
			}
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Searches object in array.
	 *
	 * @return first index of match, or -1 when not found
	 */
	public static <T> int indexOf(final T[] array, final T obj) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == obj) return i;
		}
		return -1;
	}

	/**
	 * GE N clamped.
	 *
	 * @param a the a
	 * @param lb the lb
	 * @param ub the ub
	 * @return the float
	 */
	public static float GEN_clamped(final float a, final float lb, final float ub) {
		return a < lb ? lb : ub < a ? ub : a;
	}

}
