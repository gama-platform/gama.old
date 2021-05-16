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

/**
 * Stack-based object pool, see the example for usage. You must use the {@link #returning}
 * method for returning stack-allocated instance.<p>
 * 
 * Example code:
 * 
 * <pre>
 * StackList&lt;Vector3f&gt; vectors;
 * ...
 * 
 * vectors.push();
 * try {
 *     Vector3f vec = vectors.get();
 *     ...
 *     return vectors.returning(vec);
 * }
 * finally {
 *     vectors.pop();
 * }
 * </pre>
 * 
 * @author jezek2
 */
public abstract class StackList<T> {

	private final ObjectArrayList<T> list = new ObjectArrayList<T>();
	private T returnObj;
	
	private int[] stack = new int[512];
	private int stackCount = 0;
	
	private int pos = 0;
	
	public StackList() {
		returnObj = create();
	}
	
	protected StackList(boolean unused) {
	}
	
	/**
	 * Pushes the stack.
	 */
	public final void push() {
		/*if (stackCount == stack.length-1) {
			resizeStack();
		}*/
		
		stack[stackCount++] = pos;
	}

	/**
	 * Pops the stack.
	 */
	public final void pop() {
		pos = stack[--stackCount];
	}
	
	/**
	 * Returns instance from stack pool, or create one if not present. The returned
	 * instance will be automatically reused when {@link #pop} is called.
	 * 
	 * @return instance
	 */
	public T get() {
		//if (true) return create();
		
		if (pos == list.size()) {
			expand();
		}
		
		return list.getQuick(pos++);
	}
	
	/**
	 * Copies given instance into one slot static instance and returns it. It's
	 * essential that caller of method (that uses this method for returning instances)
	 * immediately copies it into own variable before any other usage.
	 * 
	 * @param obj stack-allocated instance
	 * @return one slot instance for returning purposes
	 */
	public final T returning(T obj) {
		//if (true) { T ret = create(); copy(ret, obj); return ret; }
		
		copy(returnObj, obj);
		return returnObj;
	}
	
	/**
	 * Creates a new instance of type.
	 * 
	 * @return instance
	 */
	protected abstract T create();
	
	/**
	 * Copies data from one instance to another.
	 * 
	 * @param dest
	 * @param src
	 */
	protected abstract void copy(T dest, T src);

	private void expand() {
		list.add(create());
	}
	
}
