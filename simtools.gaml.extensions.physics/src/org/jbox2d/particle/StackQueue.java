/*******************************************************************************************************
 *
 * StackQueue.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.particle;


/**
 * The Class StackQueue.
 *
 * @param <T> the generic type
 */
public class StackQueue<T> {

  /** The m buffer. */
  private T[] m_buffer;
  
  /** The m front. */
  private int m_front;
  
  /** The m back. */
  private int m_back;
  
  /** The m end. */
  private int m_end;

  /**
   * Instantiates a new stack queue.
   */
  public StackQueue() {}

  /**
   * Reset.
   *
   * @param buffer the buffer
   */
  public void reset(T[] buffer) {
    m_buffer = buffer;
    m_front = 0;
    m_back = 0;
    m_end = buffer.length;
  }

  /**
   * Push.
   *
   * @param task the task
   */
  public void push(T task) {
    if (m_back >= m_end) {
      System.arraycopy(m_buffer, m_front, m_buffer, 0, m_back - m_front);
      m_back -= m_front;
      m_front = 0;
      if (m_back >= m_end) {
        return;
      }
    }
    m_buffer[m_back++] = task;
  }

  /**
   * Pop.
   *
   * @return the t
   */
  public T pop() {
    assert (m_front < m_back);
    return m_buffer[m_front++];
  }

  /**
   * Empty.
   *
   * @return true, if successful
   */
  public boolean empty() {
    return m_front >= m_back;
  }

  /**
   * Front.
   *
   * @return the t
   */
  public T front() {
    return m_buffer[m_front];
  }
}
