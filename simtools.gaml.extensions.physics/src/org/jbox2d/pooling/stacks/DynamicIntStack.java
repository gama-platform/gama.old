/*******************************************************************************************************
 *
 * DynamicIntStack.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.pooling.stacks;

/**
 * The Class DynamicIntStack.
 */
public class DynamicIntStack {

  /** The stack. */
  private int[] stack;
  
  /** The size. */
  private int size;
  
  /** The position. */
  private int position;

  /**
   * Instantiates a new dynamic int stack.
   *
   * @param initialSize the initial size
   */
  public DynamicIntStack(int initialSize) {
    stack = new int[initialSize];
    position = 0;
    size = initialSize;
  }

  /**
   * Reset.
   */
  public void reset() {
    position = 0;
  }

  /**
   * Pop.
   *
   * @return the int
   */
  public int pop() {
    assert (position > 0);
    return stack[--position];
  }

  /**
   * Push.
   *
   * @param i the i
   */
  public void push(int i) {
    if (position == size) {
      int[] old = stack;
      stack = new int[size * 2];
      size = stack.length;
      System.arraycopy(old, 0, stack, 0, old.length);
    }
    stack[position++] = i;
  }

  /**
   * Gets the count.
   *
   * @return the count
   */
  public int getCount() {
    return position;
  }
}
