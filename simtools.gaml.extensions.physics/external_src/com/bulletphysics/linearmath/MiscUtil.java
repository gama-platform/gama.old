/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

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

	public static int getListCapacityForHash(final List<?> list) {
		return getListCapacityForHash(list.size());
	}

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

	public static float GEN_clamped(final float a, final float lb, final float ub) {
		return a < lb ? lb : ub < a ? ub : a;
	}

}
