/*******************************************************************************************************
 *
 * Vec2.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.common;

import java.io.Serializable;

/**
 * A 2D column vector
 */
public class Vec2 implements Serializable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The y. */
  public float x, y;

  /**
   * Instantiates a new vec 2.
   */
  public Vec2() {
    this(0, 0);
  }

  /**
   * Instantiates a new vec 2.
   *
   * @param x the x
   * @param y the y
   */
  public Vec2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Instantiates a new vec 2.
   *
   * @param toCopy the to copy
   */
  public Vec2(Vec2 toCopy) {
    this(toCopy.x, toCopy.y);
  }

  /** Zero out this vector. */
  public final void setZero() {
    x = 0.0f;
    y = 0.0f;
  }

  /** Set the vector component-wise. */
  public final Vec2 set(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }

  /** Set this vector to another vector. */
  public final Vec2 set(Vec2 v) {
    this.x = v.x;
    this.y = v.y;
    return this;
  }

  /** Return the sum of this vector and another; does not alter either one. */
  public final Vec2 add(Vec2 v) {
    return new Vec2(x + v.x, y + v.y);
  }



  /** Return the difference of this vector and another; does not alter either one. */
  public final Vec2 sub(Vec2 v) {
    return new Vec2(x - v.x, y - v.y);
  }

  /** Return this vector multiplied by a scalar; does not alter this vector. */
  public final Vec2 mul(float a) {
    return new Vec2(x * a, y * a);
  }

  /** Return the negation of this vector; does not alter this vector. */
  public final Vec2 negate() {
    return new Vec2(-x, -y);
  }

  /** Flip the vector and return it - alters this vector. */
  public final Vec2 negateLocal() {
    x = -x;
    y = -y;
    return this;
  }

  /** Add another vector to this one and returns result - alters this vector. */
  public final Vec2 addLocal(Vec2 v) {
    x += v.x;
    y += v.y;
    return this;
  }

  /** Adds values to this vector and returns result - alters this vector. */
  public final Vec2 addLocal(float x, float y) {
    this.x += x;
    this.y += y;
    return this;
  }

  /** Subtract another vector from this one and return result - alters this vector. */
  public final Vec2 subLocal(Vec2 v) {
    x -= v.x;
    y -= v.y;
    return this;
  }

  /** Multiply this vector by a number and return result - alters this vector. */
  public final Vec2 mulLocal(float a) {
    x *= a;
    y *= a;
    return this;
  }

  /** Get the skew vector such that dot(skew_vec, other) == cross(vec, other) */
  public final Vec2 skew() {
    return new Vec2(-y, x);
  }

  /** Get the skew vector such that dot(skew_vec, other) == cross(vec, other) */
  public final void skew(Vec2 out) {
    out.x = -y;
    out.y = x;
  }

  /** Return the length of this vector. */
  public final float length() {
    return MathUtils.sqrt(x * x + y * y);
  }

  /** Return the squared length of this vector. */
  public final float lengthSquared() {
    return (x * x + y * y);
  }

  /** Normalize this vector and return the length before normalization. Alters this vector. */
  public final float normalize() {
    float length = length();
    if (length < Settings.EPSILON) {
      return 0f;
    }

    float invLength = 1.0f / length;
    x *= invLength;
    y *= invLength;
    return length;
  }

  /** True if the vector represents a pair of valid, non-infinite floating point numbers. */
  public final boolean isValid() {
    return !Float.isNaN(x) && !Float.isInfinite(x) && !Float.isNaN(y) && !Float.isInfinite(y);
  }

  /** Return a new vector that has positive components. */
  public final Vec2 abs() {
    return new Vec2(MathUtils.abs(x), MathUtils.abs(y));
  }

  /**
   * Abs local.
   */
  public final void absLocal() {
    x = MathUtils.abs(x);
    y = MathUtils.abs(y);
  }

  // @Override // annotation omitted for GWT-compatibility
  /** Return a copy of this vector. */
  public final Vec2 clone() {
    return new Vec2(x, y);
  }

  @Override
  public final String toString() {
    return "(" + x + "," + y + ")";
  }

  /*
   * Static
   */

  /**
   * Abs.
   *
   * @param a the a
   * @return the vec 2
   */
  public final static Vec2 abs(Vec2 a) {
    return new Vec2(MathUtils.abs(a.x), MathUtils.abs(a.y));
  }

  /**
   * Abs to out.
   *
   * @param a the a
   * @param out the out
   */
  public final static void absToOut(Vec2 a, Vec2 out) {
    out.x = MathUtils.abs(a.x);
    out.y = MathUtils.abs(a.y);
  }

  /**
   * Dot.
   *
   * @param a the a
   * @param b the b
   * @return the float
   */
  public final static float dot(final Vec2 a, final Vec2 b) {
    return a.x * b.x + a.y * b.y;
  }

  /**
   * Cross.
   *
   * @param a the a
   * @param b the b
   * @return the float
   */
  public final static float cross(final Vec2 a, final Vec2 b) {
    return a.x * b.y - a.y * b.x;
  }

  /**
   * Cross.
   *
   * @param a the a
   * @param s the s
   * @return the vec 2
   */
  public final static Vec2 cross(Vec2 a, float s) {
    return new Vec2(s * a.y, -s * a.x);
  }

  /**
   * Cross to out.
   *
   * @param a the a
   * @param s the s
   * @param out the out
   */
  public final static void crossToOut(Vec2 a, float s, Vec2 out) {
    final float tempy = -s * a.x;
    out.x = s * a.y;
    out.y = tempy;
  }

  /**
   * Cross to out unsafe.
   *
   * @param a the a
   * @param s the s
   * @param out the out
   */
  public final static void crossToOutUnsafe(Vec2 a, float s, Vec2 out) {
    assert (out != a);
    out.x = s * a.y;
    out.y = -s * a.x;
  }

  /**
   * Cross.
   *
   * @param s the s
   * @param a the a
   * @return the vec 2
   */
  public final static Vec2 cross(float s, Vec2 a) {
    return new Vec2(-s * a.y, s * a.x);
  }

  /**
   * Cross to out.
   *
   * @param s the s
   * @param a the a
   * @param out the out
   */
  public final static void crossToOut(float s, Vec2 a, Vec2 out) {
    final float tempY = s * a.x;
    out.x = -s * a.y;
    out.y = tempY;
  }

  /**
   * Cross to out unsafe.
   *
   * @param s the s
   * @param a the a
   * @param out the out
   */
  public final static void crossToOutUnsafe(float s, Vec2 a, Vec2 out) {
    assert (out != a);
    out.x = -s * a.y;
    out.y = s * a.x;
  }

  /**
   * Negate to out.
   *
   * @param a the a
   * @param out the out
   */
  public final static void negateToOut(Vec2 a, Vec2 out) {
    out.x = -a.x;
    out.y = -a.y;
  }

  /**
   * Min.
   *
   * @param a the a
   * @param b the b
   * @return the vec 2
   */
  public final static Vec2 min(Vec2 a, Vec2 b) {
    return new Vec2(a.x < b.x ? a.x : b.x, a.y < b.y ? a.y : b.y);
  }

  /**
   * Max.
   *
   * @param a the a
   * @param b the b
   * @return the vec 2
   */
  public final static Vec2 max(Vec2 a, Vec2 b) {
    return new Vec2(a.x > b.x ? a.x : b.x, a.y > b.y ? a.y : b.y);
  }

  /**
   * Min to out.
   *
   * @param a the a
   * @param b the b
   * @param out the out
   */
  public final static void minToOut(Vec2 a, Vec2 b, Vec2 out) {
    out.x = a.x < b.x ? a.x : b.x;
    out.y = a.y < b.y ? a.y : b.y;
  }

  /**
   * Max to out.
   *
   * @param a the a
   * @param b the b
   * @param out the out
   */
  public final static void maxToOut(Vec2 a, Vec2 b, Vec2 out) {
    out.x = a.x > b.x ? a.x : b.x;
    out.y = a.y > b.y ? a.y : b.y;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() { // automatically generated by Eclipse
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(x);
    result = prime * result + Float.floatToIntBits(y);
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) { // automatically generated by Eclipse
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Vec2 other = (Vec2) obj;
    if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
    if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
    return true;
  }
}
