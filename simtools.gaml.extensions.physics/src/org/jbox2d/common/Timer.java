/*******************************************************************************************************
 *
 * Timer.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.common;

/**
 * Timer for profiling
 * 
 * @author Daniel
 */
public class Timer {

  /** The reset nanos. */
  private long resetNanos;

  /**
   * Instantiates a new timer.
   */
  public Timer() {
    reset();
  }

  /**
   * Reset.
   */
  public void reset() {
    resetNanos = System.nanoTime();
  }

  /**
   * Gets the milliseconds.
   *
   * @return the milliseconds
   */
  public float getMilliseconds() {
    return (System.nanoTime() - resetNanos) / 1000 * 1f / 1000;
  }
}
