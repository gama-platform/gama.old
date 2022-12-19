/*******************************************************************************************************
 *
 * StackList.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.util;

import java.util.ArrayList;

/**
 * Stack-based object pool, see the example for usage. You must use the {@link #returning} method for returning
 * stack-allocated instance.
 * <p>
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

	/** The list. */
	private final ArrayList<T> list = new ArrayList<>();
	
	/** The return obj. */
	private T returnObj;

	/** The stack. */
	private final int[] stack = new int[512];
	
	/** The stack count. */
	private int stackCount = 0;

	/** The pos. */
	private int pos = 0;

	/**
	 * Instantiates a new stack list.
	 */
	public StackList() {
		returnObj = create();
	}

	/**
	 * Instantiates a new stack list.
	 *
	 * @param unused the unused
	 */
	protected StackList(final boolean unused) {}

	/**
	 * Pushes the stack.
	 */
	public final void push() {
		/*
		 * if (stackCount == stack.length-1) { resizeStack(); }
		 */

		stack[stackCount++] = pos;
	}

	/**
	 * Pops the stack.
	 */
	public final void pop() {
		pos = stack[--stackCount];
	}

	/**
	 * Returns instance from stack pool, or create one if not present. The returned instance will be automatically
	 * reused when {@link #pop} is called.
	 *
	 * @return instance
	 */
	public T get() {
		// if (true) return create();

		if (pos == list.size()) { expand(); }

		return list.get(pos++);
	}

	/**
	 * Copies given instance into one slot static instance and returns it. It's essential that caller of method (that
	 * uses this method for returning instances) immediately copies it into own variable before any other usage.
	 *
	 * @param obj
	 *            stack-allocated instance
	 * @return one slot instance for returning purposes
	 */
	public final T returning(final T obj) {
		// if (true) { T ret = create(); copy(ret, obj); return ret; }

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

	/**
	 * Expand.
	 */
	private void expand() {
		list.add(create());
	}

}
