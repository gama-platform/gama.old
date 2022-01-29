/*******************************************************************************************************
 *
 * ParticleColor.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.particle;

import org.jbox2d.common.Color3f;

/**
 * Small color object for each particle
 * 
 * @author dmurph
 */
public class ParticleColor {
  
  /** The a. */
  public byte r, g, b, a;

  /**
   * Instantiates a new particle color.
   */
  public ParticleColor() {
    r = (byte) 127;
    g = (byte) 127;
    b = (byte) 127;
    a = (byte) 50;
  }

  /**
   * Instantiates a new particle color.
   *
   * @param r the r
   * @param g the g
   * @param b the b
   * @param a the a
   */
  public ParticleColor(byte r, byte g, byte b, byte a) {
    set(r, g, b, a);
  }

  /**
   * Instantiates a new particle color.
   *
   * @param color the color
   */
  public ParticleColor(Color3f color) {
    set(color);
  }

  /**
   * Sets the.
   *
   * @param color the color
   */
  public void set(Color3f color) {
    r = (byte) (255 * color.x);
    g = (byte) (255 * color.y);
    b = (byte) (255 * color.z);
    a = (byte) 255;
  }
  
  /**
   * Sets the.
   *
   * @param color the color
   */
  public void set(ParticleColor color) {
    r = color.r;
    g = color.g;
    b = color.b;
    a = color.a;
  }
  
  /**
   * Checks if is zero.
   *
   * @return true, if is zero
   */
  public boolean isZero() {
    return r == 0 && g == 0 && b == 0 && a == 0;
  }

  /**
   * Sets the.
   *
   * @param r the r
   * @param g the g
   * @param b the b
   * @param a the a
   */
  public void set(byte r, byte g, byte b, byte a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }
}
