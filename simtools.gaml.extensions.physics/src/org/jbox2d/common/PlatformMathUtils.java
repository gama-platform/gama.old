/*******************************************************************************************************
 *
 * PlatformMathUtils.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.common;

/**
 * Contains methods from MathUtils that rely on JVM features. These are separated out from
 * MathUtils so that they can be overridden when compiling for GWT.
 */
class PlatformMathUtils {

  /** The Constant SHIFT23. */
  private static final float SHIFT23 = 1 << 23;
  
  /** The Constant INV_SHIFT23. */
  private static final float INV_SHIFT23 = 1.0f / SHIFT23;

  /**
   * Fast pow.
   *
   * @param a the a
   * @param b the b
   * @return the float
   */
  public static final float fastPow(float a, float b) {
    float x = Float.floatToRawIntBits(a);
    x *= INV_SHIFT23;
    x -= 127;
    float y = x - (x >= 0 ? (int) x : (int) x - 1);
    b *= x + (y - y * y) * 0.346607f;
    y = b - (b >= 0 ? (int) b : (int) b - 1);
    y = (y - y * y) * 0.33971f;
    return Float.intBitsToFloat((int) ((b + 127 - y) * SHIFT23));
  }
}
