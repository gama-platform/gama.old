/*******************************************************************************************************
 *
 * IDynamicStack.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.pooling;

/**
 * Same functionality of a regular java.util stack.  Object
 * return order does not matter.
 * @author Daniel
 *
 * @param <E>
 */
public interface IDynamicStack<E> {

	/**
	 * Pops an item off the stack
	 * @return
	 */
	public E pop();

	/**
	 * Pushes an item back on the stack
	 * @param argObject
	 */
	public void push(E argObject);

}
