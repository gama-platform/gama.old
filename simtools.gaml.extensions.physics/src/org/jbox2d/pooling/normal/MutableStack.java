/*******************************************************************************************************
 *
 * MutableStack.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.pooling.normal;

import org.jbox2d.pooling.IDynamicStack;

/**
 * The Class MutableStack.
 *
 * @param <E> the element type
 */
public abstract class MutableStack<E> implements IDynamicStack<E> {

  /** The stack. */
  private E[] stack;
  
  /** The index. */
  private int index;
  
  /** The size. */
  private int size;

  /**
   * Instantiates a new mutable stack.
   *
   * @param argInitSize the arg init size
   */
  public MutableStack(int argInitSize) {
    index = 0;
    stack = null;
    index = 0;
    extendStack(argInitSize);
  }

  /**
   * Extend stack.
   *
   * @param argSize the arg size
   */
  private void extendStack(int argSize) {
    E[] newStack = newArray(argSize);
    if (stack != null) {
      System.arraycopy(stack, 0, newStack, 0, size);
    }
    for (int i = 0; i < newStack.length; i++) {
      newStack[i] = newInstance();
    }
    stack = newStack;
    size = newStack.length;
  }

  public final E pop() {
    if (index >= size) {
      extendStack(size * 2);
    }
    return stack[index++];
  }

  public final void push(E argObject) {
    assert (index > 0);
    stack[--index] = argObject;
  }

  /** Creates a new instance of the object contained by this stack. */
  protected abstract E newInstance();
  
  /**
   * New array.
   *
   * @param size the size
   * @return the e[]
   */
  protected abstract E[] newArray(int size);
}
