/*******************************************************************************************************
 *
 * Rot.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * Represents a rotation
 * 
 * @author Daniel
 */
public class Rot implements Serializable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The c. */
  public float s, c; // sin and cos

  /**
   * Instantiates a new rot.
   */
  public Rot() {
    setIdentity();
  }

  /**
   * Instantiates a new rot.
   *
   * @param angle the angle
   */
  public Rot(float angle) {
    set(angle);
  }

  /**
   * Gets the sin.
   *
   * @return the sin
   */
  public float getSin() {
    return s;
  }

  @Override
  public String toString() {
    return "Rot(s:" + s + ", c:" + c + ")";
  }

  /**
   * Gets the cos.
   *
   * @return the cos
   */
  public float getCos() {
    return c;
  }

  /**
   * Sets the.
   *
   * @param angle the angle
   * @return the rot
   */
  public Rot set(float angle) {
    s = MathUtils.sin(angle);
    c = MathUtils.cos(angle);
    return this;
  }

  /**
   * Sets the.
   *
   * @param other the other
   * @return the rot
   */
  public Rot set(Rot other) {
    s = other.s;
    c = other.c;
    return this;
  }

  /**
   * Sets the identity.
   *
   * @return the rot
   */
  public Rot setIdentity() {
    s = 0;
    c = 1;
    return this;
  }

  /**
   * Gets the angle.
   *
   * @return the angle
   */
  public float getAngle() {
    return MathUtils.atan2(s, c);
  }

  /**
   * Gets the x axis.
   *
   * @param xAxis the x axis
   * @return the x axis
   */
  public void getXAxis(Vec2 xAxis) {
    xAxis.set(c, s);
  }

  /**
   * Gets the y axis.
   *
   * @param yAxis the y axis
   * @return the y axis
   */
  public void getYAxis(Vec2 yAxis) {
    yAxis.set(-s, c);
  }

  // @Override // annotation omitted for GWT-compatibility
  public Rot clone() {
    Rot copy = new Rot();
    copy.s = s;
    copy.c = c;
    return copy;
  }

  /**
   * Mul.
   *
   * @param q the q
   * @param r the r
   * @param out the out
   */
  public static final void mul(Rot q, Rot r, Rot out) {
    float tempc = q.c * r.c - q.s * r.s;
    out.s = q.s * r.c + q.c * r.s;
    out.c = tempc;
  }

  /**
   * Mul unsafe.
   *
   * @param q the q
   * @param r the r
   * @param out the out
   */
  public static final void mulUnsafe(Rot q, Rot r, Rot out) {
    assert (r != out);
    assert (q != out);
    // [qc -qs] * [rc -rs] = [qc*rc-qs*rs -qc*rs-qs*rc]
    // [qs qc] [rs rc] [qs*rc+qc*rs -qs*rs+qc*rc]
    // s = qs * rc + qc * rs
    // c = qc * rc - qs * rs
    out.s = q.s * r.c + q.c * r.s;
    out.c = q.c * r.c - q.s * r.s;
  }

  /**
   * Mul trans.
   *
   * @param q the q
   * @param r the r
   * @param out the out
   */
  public static final void mulTrans(Rot q, Rot r, Rot out) {
    final float tempc = q.c * r.c + q.s * r.s;
    out.s = q.c * r.s - q.s * r.c;
    out.c = tempc;
  }

  /**
   * Mul trans unsafe.
   *
   * @param q the q
   * @param r the r
   * @param out the out
   */
  public static final void mulTransUnsafe(Rot q, Rot r, Rot out) {
    // [ qc qs] * [rc -rs] = [qc*rc+qs*rs -qc*rs+qs*rc]
    // [-qs qc] [rs rc] [-qs*rc+qc*rs qs*rs+qc*rc]
    // s = qc * rs - qs * rc
    // c = qc * rc + qs * rs
    out.s = q.c * r.s - q.s * r.c;
    out.c = q.c * r.c + q.s * r.s;
  }

  /**
   * Mul to out.
   *
   * @param q the q
   * @param v the v
   * @param out the out
   */
  public static final void mulToOut(Rot q, Vec2 v, Vec2 out) {
    float tempy = q.s * v.x + q.c * v.y;
    out.x = q.c * v.x - q.s * v.y;
    out.y = tempy;
  }

  /**
   * Mul to out unsafe.
   *
   * @param q the q
   * @param v the v
   * @param out the out
   */
  public static final void mulToOutUnsafe(Rot q, Vec2 v, Vec2 out) {
    out.x = q.c * v.x - q.s * v.y;
    out.y = q.s * v.x + q.c * v.y;
  }

  /**
   * Mul trans.
   *
   * @param q the q
   * @param v the v
   * @param out the out
   */
  public static final void mulTrans(Rot q, Vec2 v, Vec2 out) {
    final float tempy = -q.s * v.x + q.c * v.y;
    out.x = q.c * v.x + q.s * v.y;
    out.y = tempy;
  }

  /**
   * Mul trans unsafe.
   *
   * @param q the q
   * @param v the v
   * @param out the out
   */
  public static final void mulTransUnsafe(Rot q, Vec2 v, Vec2 out) {
    out.x = q.c * v.x + q.s * v.y;
    out.y = -q.s * v.x + q.c * v.y;
  }
}
