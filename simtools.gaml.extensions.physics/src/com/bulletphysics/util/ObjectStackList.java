/*******************************************************************************************************
 *
 * ObjectStackList.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.util;

/**
 * Stack-based object pool for arbitrary objects, returning not supported.
 * 
 * @author jezek2
 */
public class ObjectStackList<T> extends StackList<T> {

	/** The cls. */
	private Class<T> cls;
	
	/**
	 * Instantiates a new object stack list.
	 *
	 * @param cls the cls
	 */
	public ObjectStackList(Class<T> cls) {
		super(false);
		this.cls = cls;
	}

	@Override
	protected T create() {
		try {
			return cls.newInstance();
		}
		catch (InstantiationException e) {
			throw new IllegalStateException(e);
		}
		catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void copy(T dest, T src) {
		throw new UnsupportedOperationException();
	}
	
}
