/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
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

package com.bulletphysics.util;

import java.lang.reflect.Array;
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

	private Class componentType;
	private ObjectArrayList list = new ObjectArrayList();
	private Comparator comparator;
	private IntValue key = new IntValue();
	
	/**
	 * Creates object pool.
	 * 
	 * @param componentType
	 */
	public ArrayPool(Class componentType) {
		this.componentType = componentType;
		
		if (componentType == float.class) {
			comparator = floatComparator;
		}
		else if (componentType == int.class) {
			comparator = intComparator;
		}
		else if (!componentType.isPrimitive()) {
			comparator = objectComparator;
		}
		else {
			throw new UnsupportedOperationException("unsupported type "+componentType);
		}
	}
	
	@SuppressWarnings("unchecked")
	private T create(int length) {
		return (T)Array.newInstance(componentType, length);
	}
	
	/**
	 * Returns array of exactly the same length as demanded, or create one if not
	 * present in the pool.
	 * 
	 * @param length
	 * @return array
	 */
	@SuppressWarnings("unchecked")
	public T getFixed(int length) {
		key.value = length;
		int index = Collections.binarySearch(list, key, comparator);
		if (index < 0) {
			return create(length);
		}
		return (T)list.remove(index);
	}

	/**
	 * Returns array that has same or greater length, or create one if not present
	 * in the pool.
	 * 
	 * @param length the minimum length required
	 * @return array
	 */
	@SuppressWarnings("unchecked")
	public T getAtLeast(int length) {
		key.value = length;
		int index = Collections.binarySearch(list, key, comparator);
		if (index < 0) {
			index = -index - 1;
			if (index < list.size()) {
				return (T)list.remove(index);
			}
			else {
				return create(length);
			}
		}
		return (T)list.remove(index);
	}
	
	/**
	 * Releases array into object pool.
	 * 
	 * @param array previously obtained array from this pool
	 */
	@SuppressWarnings("unchecked")
	public void release(T array) {
		int index = Collections.binarySearch(list, array, comparator);
		if (index < 0) index = -index - 1;
		list.add(index, array);
		
		// remove references from object arrays:
		if (comparator == objectComparator) {
			Object[] objArray = (Object[])array;
			for (int i=0; i<objArray.length; i++) {
				objArray[i] = null;
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////

	private static Comparator floatComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			int len1 = (o1 instanceof IntValue)? ((IntValue)o1).value : ((float[])o1).length;
			int len2 = (o2 instanceof IntValue)? ((IntValue)o2).value : ((float[])o2).length;
			return len1 > len2? 1 : len1 < len2 ? -1 : 0;
		}
	};

	private static Comparator intComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			int len1 = (o1 instanceof IntValue)? ((IntValue)o1).value : ((int[])o1).length;
			int len2 = (o2 instanceof IntValue)? ((IntValue)o2).value : ((int[])o2).length;
			return len1 > len2? 1 : len1 < len2 ? -1 : 0;
		}
	};
	
	private static Comparator objectComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			int len1 = (o1 instanceof IntValue)? ((IntValue)o1).value : ((Object[])o1).length;
			int len2 = (o2 instanceof IntValue)? ((IntValue)o2).value : ((Object[])o2).length;
			return len1 > len2? 1 : len1 < len2 ? -1 : 0;
		}
	};
	
	private static class IntValue {
		public int value;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private static ThreadLocal<Map> threadLocal = new ThreadLocal<Map>() {
		@Override
		protected Map initialValue() {
			return new HashMap();
		}
	};
	
	/**
	 * Returns per-thread array pool for given type, or create one if it doesn't exist.
	 * 
	 * @param cls type
	 * @return object pool
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayPool<T> get(Class cls) {
		Map map = threadLocal.get();
		
		ArrayPool<T> pool = (ArrayPool<T>)map.get(cls);
		if (pool == null) {
			pool = new ArrayPool<T>(cls);
			map.put(cls, pool);
		}
		
		return pool;
	}

	public static void cleanCurrentThread() {
		threadLocal.remove();
	}

}
