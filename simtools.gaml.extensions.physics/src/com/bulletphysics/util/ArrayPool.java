/*******************************************************************************************************
 *
 * ArrayPool.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Object pool for arrays.
 *
 * @author jezek2
 */
public class ArrayPool<T> {

	/** The component type. */
	private final Class componentType;
	
	/** The list. */
	private final ArrayList list = new ArrayList();
	
	/** The comparator. */
	private Comparator comparator;
	
	/** The key. */
	private final IntValue key = new IntValue();

	/**
	 * Creates object pool.
	 *
	 * @param componentType
	 */
	public ArrayPool(final Class componentType) {
		this.componentType = componentType;

		if (componentType == float.class) {
			comparator = floatComparator;
		} else if (componentType == int.class) {
			comparator = intComparator;
		} else if (!componentType.isPrimitive()) {
			comparator = objectComparator;
		} else
			throw new UnsupportedOperationException("unsupported type " + componentType);
	}

	/**
	 * Creates the.
	 *
	 * @param length the length
	 * @return the t
	 */
	@SuppressWarnings ("unchecked")
	private T create(final int length) {
		return (T) Array.newInstance(componentType, length);
	}

	/**
	 * Returns array of exactly the same length as demanded, or create one if not present in the pool.
	 *
	 * @param length
	 * @return array
	 */
	@SuppressWarnings ("unchecked")
	public T getFixed(final int length) {
		key.value = length;
		int index = Collections.binarySearch(list, key, comparator);
		if (index < 0) return create(length);
		return (T) list.remove(index);
	}

	/**
	 * Returns array that has same or greater length, or create one if not present in the pool.
	 *
	 * @param length
	 *            the minimum length required
	 * @return array
	 */
	@SuppressWarnings ("unchecked")
	public T getAtLeast(final int length) {
		key.value = length;
		int index = Collections.binarySearch(list, key, comparator);
		if (index < 0) {
			index = -index - 1;
			if (index < list.size())
				return (T) list.remove(index);
			else
				return create(length);
		}
		return (T) list.remove(index);
	}

	/**
	 * Releases array into object pool.
	 *
	 * @param array
	 *            previously obtained array from this pool
	 */
	@SuppressWarnings ("unchecked")
	public void release(final T array) {
		int index = Collections.binarySearch(list, array, comparator);
		if (index < 0) { index = -index - 1; }
		list.add(index, array);

		// remove references from object arrays:
		if (comparator == objectComparator) {
			Object[] objArray = (Object[]) array;
			for (int i = 0; i < objArray.length; i++) {
				objArray[i] = null;
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////

	/** The float comparator. */
	private static Comparator floatComparator = (o1, o2) -> {
		int len1 = o1 instanceof IntValue ? ((IntValue) o1).value : ((float[]) o1).length;
		int len2 = o2 instanceof IntValue ? ((IntValue) o2).value : ((float[]) o2).length;
		return len1 > len2 ? 1 : len1 < len2 ? -1 : 0;
	};

	/** The int comparator. */
	private static Comparator intComparator = (o1, o2) -> {
		int len1 = o1 instanceof IntValue ? ((IntValue) o1).value : ((int[]) o1).length;
		int len2 = o2 instanceof IntValue ? ((IntValue) o2).value : ((int[]) o2).length;
		return len1 > len2 ? 1 : len1 < len2 ? -1 : 0;
	};

	/** The object comparator. */
	private static Comparator objectComparator = (o1, o2) -> {
		int len1 = o1 instanceof IntValue ? ((IntValue) o1).value : ((Object[]) o1).length;
		int len2 = o2 instanceof IntValue ? ((IntValue) o2).value : ((Object[]) o2).length;
		return len1 > len2 ? 1 : len1 < len2 ? -1 : 0;
	};

	/**
	 * The Class IntValue.
	 */
	private static class IntValue {
		
		/** The value. */
		public int value;
	}

	////////////////////////////////////////////////////////////////////////////

	/** The thread local. */
	private static ThreadLocal<Map> threadLocal = new ThreadLocal<>() {
		@Override
		protected Map initialValue() {
			return new HashMap();
		}
	};

	/**
	 * Returns per-thread array pool for given type, or create one if it doesn't exist.
	 *
	 * @param cls
	 *            type
	 * @return object pool
	 */
	@SuppressWarnings ("unchecked")
	public static <T> ArrayPool<T> get(final Class cls) {
		Map map = threadLocal.get();

		ArrayPool<T> pool = (ArrayPool<T>) map.get(cls);
		if (pool == null) {
			pool = new ArrayPool<>(cls);
			map.put(cls, pool);
		}

		return pool;
	}

	/**
	 * Clean current thread.
	 */
	public static void cleanCurrentThread() {
		threadLocal.remove();
	}

}
