/*******************************************************************************************************
 *
 * CircleStack.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.pooling.normal;

import org.jbox2d.pooling.IOrderedStack;

/**
 * The Class CircleStack.
 *
 * @param <E> the element type
 */
public abstract class CircleStack<E> implements IOrderedStack<E>{

  /** The pool. */
  private final Object[] pool;
  
  /** The index. */
  private int index;
  
  /** The size. */
  private final int size;
  
  /** The container. */
  private final Object[] container;

  /**
   * Instantiates a new circle stack.
   *
   * @param argStackSize the arg stack size
   * @param argContainerSize the arg container size
   */
  public CircleStack(int argStackSize, int argContainerSize) {
    size = argStackSize;
    pool = new Object[argStackSize];
    for (int i = 0; i < argStackSize; i++) {
      pool[i] = newInstance();
    }
    index = 0;
    container = new Object[argContainerSize];
  }

  @SuppressWarnings("unchecked")
  public final E pop() {
    index++;
    if(index >= size){
      index = 0;
    }
    return (E) pool[index];
  }

  @SuppressWarnings("unchecked")
  public final E[] pop(int argNum) {
    assert (argNum <= container.length) : "Container array is too small";
    if(index + argNum < size){
      System.arraycopy(pool, index, container, 0, argNum);
      index += argNum;
    }else{
      int overlap = (index + argNum) - size;
      System.arraycopy(pool, index, container, 0, argNum - overlap);
      System.arraycopy(pool, 0, container, argNum - overlap, overlap);
      index = overlap;
    }
    return (E[]) container;
  }

  @Override
  public void push(int argNum) {}

  /** Creates a new instance of the object contained by this stack. */
  protected abstract E newInstance();
}
