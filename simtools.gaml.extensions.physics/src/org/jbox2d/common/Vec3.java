/*******************************************************************************************************
 *
 * Vec3.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * @author Daniel Murphy
 */
public class Vec3 implements Serializable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The z. */
  public float x, y, z;

  /**
   * Instantiates a new vec 3.
   */
  public Vec3() {
    x = y = z = 0f;
  }

  /**
   * Instantiates a new vec 3.
   *
   * @param argX the arg X
   * @param argY the arg Y
   * @param argZ the arg Z
   */
  public Vec3(float argX, float argY, float argZ) {
    x = argX;
    y = argY;
    z = argZ;
  }

  /**
   * Instantiates a new vec 3.
   *
   * @param copy the copy
   */
  public Vec3(Vec3 copy) {
    x = copy.x;
    y = copy.y;
    z = copy.z;
  }

  /**
   * Sets the.
   *
   * @param vec the vec
   * @return the vec 3
   */
  public Vec3 set(Vec3 vec) {
    x = vec.x;
    y = vec.y;
    z = vec.z;
    return this;
  }

  /**
   * Sets the.
   *
   * @param argX the arg X
   * @param argY the arg Y
   * @param argZ the arg Z
   * @return the vec 3
   */
  public Vec3 set(float argX, float argY, float argZ) {
    x = argX;
    y = argY;
    z = argZ;
    return this;
  }

  /**
   * Adds the local.
   *
   * @param argVec the arg vec
   * @return the vec 3
   */
  public Vec3 addLocal(Vec3 argVec) {
    x += argVec.x;
    y += argVec.y;
    z += argVec.z;
    return this;
  }

  /**
   * Adds the.
   *
   * @param argVec the arg vec
   * @return the vec 3
   */
  public Vec3 add(Vec3 argVec) {
    return new Vec3(x + argVec.x, y + argVec.y, z + argVec.z);
  }

  /**
   * Sub local.
   *
   * @param argVec the arg vec
   * @return the vec 3
   */
  public Vec3 subLocal(Vec3 argVec) {
    x -= argVec.x;
    y -= argVec.y;
    z -= argVec.z;
    return this;
  }

  /**
   * Sub.
   *
   * @param argVec the arg vec
   * @return the vec 3
   */
  public Vec3 sub(Vec3 argVec) {
    return new Vec3(x - argVec.x, y - argVec.y, z - argVec.z);
  }

  /**
   * Mul local.
   *
   * @param argScalar the arg scalar
   * @return the vec 3
   */
  public Vec3 mulLocal(float argScalar) {
    x *= argScalar;
    y *= argScalar;
    z *= argScalar;
    return this;
  }

  /**
   * Mul.
   *
   * @param argScalar the arg scalar
   * @return the vec 3
   */
  public Vec3 mul(float argScalar) {
    return new Vec3(x * argScalar, y * argScalar, z * argScalar);
  }

  /**
   * Negate.
   *
   * @return the vec 3
   */
  public Vec3 negate() {
    return new Vec3(-x, -y, -z);
  }

  /**
   * Negate local.
   *
   * @return the vec 3
   */
  public Vec3 negateLocal() {
    x = -x;
    y = -y;
    z = -z;
    return this;
  }

  /**
   * Sets the zero.
   */
  public void setZero() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Vec3 clone() {
    return new Vec3(this);
  }

  public String toString() {
    return "(" + x + "," + y + "," + z + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(x);
    result = prime * result + Float.floatToIntBits(y);
    result = prime * result + Float.floatToIntBits(z);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Vec3 other = (Vec3) obj;
    if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
    if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
    if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z)) return false;
    return true;
  }

  /**
   * Dot.
   *
   * @param a the a
   * @param b the b
   * @return the float
   */
  public final static float dot(Vec3 a, Vec3 b) {
    return a.x * b.x + a.y * b.y + a.z * b.z;
  }

  /**
   * Cross.
   *
   * @param a the a
   * @param b the b
   * @return the vec 3
   */
  public final static Vec3 cross(Vec3 a, Vec3 b) {
    return new Vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
  }

  /**
   * Cross to out.
   *
   * @param a the a
   * @param b the b
   * @param out the out
   */
  public final static void crossToOut(Vec3 a, Vec3 b, Vec3 out) {
    final float tempy = a.z * b.x - a.x * b.z;
    final float tempz = a.x * b.y - a.y * b.x;
    out.x = a.y * b.z - a.z * b.y;
    out.y = tempy;
    out.z = tempz;
  }
  
  /**
   * Cross to out unsafe.
   *
   * @param a the a
   * @param b the b
   * @param out the out
   */
  public final static void crossToOutUnsafe(Vec3 a, Vec3 b, Vec3 out) {
    assert(out != b);
    assert(out != a);
    out.x = a.y * b.z - a.z * b.y;
    out.y = a.z * b.x - a.x * b.z;
    out.z = a.x * b.y - a.y * b.x;
  }
}
