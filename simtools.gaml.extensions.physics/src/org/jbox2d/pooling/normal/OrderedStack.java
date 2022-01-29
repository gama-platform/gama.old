/*******************************************************************************************************
 *
 * OrderedStack.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 12:52:04 AM Jan 20, 2011
 */
package org.jbox2d.pooling.normal;

/**
 * @author Daniel Murphy
 */
public abstract class OrderedStack<E> {

  /** The pool. */
  private final Object[] pool;
  
  /** The index. */
  private int index;
  
  /** The size. */
  private final int size;
  
  /** The container. */
  private final Object[] container;

  /**
   * Instantiates a new ordered stack.
   *
   * @param argStackSize the arg stack size
   * @param argContainerSize the arg container size
   */
  public OrderedStack(int argStackSize, int argContainerSize) {
    size = argStackSize;
    pool = new Object[argStackSize];
    for (int i = 0; i < argStackSize; i++) {
      pool[i] = newInstance();
    }
    index = 0;
    container = new Object[argContainerSize];
  }

  /**
   * Pop.
   *
   * @return the e
   */
  @SuppressWarnings("unchecked")
  public final E pop() {
    assert (index < size) : "End of stack reached, there is probably a leak somewhere";
    return (E) pool[index++];
  }

  /**
   * Pop.
   *
   * @param argNum the arg num
   * @return the e[]
   */
  @SuppressWarnings("unchecked")
  public final E[] pop(int argNum) {
    assert (index + argNum < size) : "End of stack reached, there is probably a leak somewhere";
    assert (argNum <= container.length) : "Container array is too small";
    System.arraycopy(pool, index, container, 0, argNum);
    index += argNum;
    return (E[]) container;
  }

  /**
   * Push.
   *
   * @param argNum the arg num
   */
  public final void push(int argNum) {
    index -= argNum;
    assert (index >= 0) : "Beginning of stack reached, push/pops are unmatched";
  }

  /** Creates a new instance of the object contained by this stack. */
  protected abstract E newInstance();
}
