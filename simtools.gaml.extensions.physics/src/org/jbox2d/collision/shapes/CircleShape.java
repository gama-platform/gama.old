/*******************************************************************************************************
 *
 * CircleShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.collision.shapes;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

/**
 * A circle shape.
 */
public class CircleShape extends Shape {

  /** The m p. */
  public final Vec2 m_p;

  /**
   * Instantiates a new circle shape.
   */
  public CircleShape() {
    super(ShapeType.CIRCLE);
    m_p = new Vec2();
    m_radius = 0;
  }

  public final Shape clone() {
    CircleShape shape = new CircleShape();
    shape.m_p.x = m_p.x;
    shape.m_p.y = m_p.y;
    shape.m_radius = m_radius;
    return shape;
  }

  public final int getChildCount() {
    return 1;
  }

  /**
   * Get the supporting vertex index in the given direction.
   * 
   * @param d
   * @return
   */
  public final int getSupport(final Vec2 d) {
    return 0;
  }

  /**
   * Get the supporting vertex in the given direction.
   * 
   * @param d
   * @return
   */
  public final Vec2 getSupportVertex(final Vec2 d) {
    return m_p;
  }

  /**
   * Get the vertex count.
   * 
   * @return
   */
  public final int getVertexCount() {
    return 1;
  }

  /**
   * Get a vertex by index.
   * 
   * @param index
   * @return
   */
  public final Vec2 getVertex(final int index) {
    assert (index == 0);
    return m_p;
  }

  @Override
  public final boolean testPoint(final Transform transform, final Vec2 p) {
    // Rot.mulToOutUnsafe(transform.q, m_p, center);
    // center.addLocal(transform.p);
    //
    // final Vec2 d = center.subLocal(p).negateLocal();
    // return Vec2.dot(d, d) <= m_radius * m_radius;
    final Rot q = transform.q;
    final Vec2 tp = transform.p;
    float centerx = -(q.c * m_p.x - q.s * m_p.y + tp.x - p.x);
    float centery = -(q.s * m_p.x + q.c * m_p.y + tp.y - p.y);

    return centerx * centerx + centery * centery <= m_radius * m_radius;
  }

  @Override
  public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
    final Rot xfq = xf.q;
    float centerx = xfq.c * m_p.x - xfq.s * m_p.y + xf.p.x;
    float centery = xfq.s * m_p.x + xfq.c * m_p.y + xf.p.y;
    float dx = p.x - centerx;
    float dy = p.y - centery;
    float d1 = MathUtils.sqrt(dx * dx + dy * dy);
    normalOut.x = dx * 1 / d1;
    normalOut.y = dy * 1 / d1;
    return d1 - m_radius;
  }

  // Collision Detection in Interactive 3D Environments by Gino van den Bergen
  // From Section 3.1.2
  // x = s + a * r
  // norm(x) = radius
  @Override
  public final boolean raycast(RayCastOutput output, RayCastInput input, Transform transform,
      int childIndex) {

    final Vec2 inputp1 = input.p1;
    final Vec2 inputp2 = input.p2;
    final Rot tq = transform.q;
    final Vec2 tp = transform.p;

    // Rot.mulToOutUnsafe(transform.q, m_p, position);
    // position.addLocal(transform.p);
    final float positionx = tq.c * m_p.x - tq.s * m_p.y + tp.x;
    final float positiony = tq.s * m_p.x + tq.c * m_p.y + tp.y;

    final float sx = inputp1.x - positionx;
    final float sy = inputp1.y - positiony;
    // final float b = Vec2.dot(s, s) - m_radius * m_radius;
    final float b = sx * sx + sy * sy - m_radius * m_radius;

    // Solve quadratic equation.
    final float rx = inputp2.x - inputp1.x;
    final float ry = inputp2.y - inputp1.y;
    // final float c = Vec2.dot(s, r);
    // final float rr = Vec2.dot(r, r);
    final float c = sx * rx + sy * ry;
    final float rr = rx * rx + ry * ry;
    final float sigma = c * c - rr * b;

    // Check for negative discriminant and short segment.
    if (sigma < 0.0f || rr < Settings.EPSILON) {
      return false;
    }

    // Find the point of intersection of the line with the circle.
    float a = -(c + MathUtils.sqrt(sigma));

    // Is the intersection point on the segment?
    if (0.0f <= a && a <= input.maxFraction * rr) {
      a /= rr;
      output.fraction = a;
      output.normal.x = rx * a + sx;
      output.normal.y = ry * a + sy;
      output.normal.normalize();
      return true;
    }

    return false;
  }

  @Override
  public final void computeAABB(final AABB aabb, final Transform transform, int childIndex) {
    final Rot tq = transform.q;
    final Vec2 tp = transform.p;
    final float px = tq.c * m_p.x - tq.s * m_p.y + tp.x;
    final float py = tq.s * m_p.x + tq.c * m_p.y + tp.y;

    aabb.lowerBound.x = px - m_radius;
    aabb.lowerBound.y = py - m_radius;
    aabb.upperBound.x = px + m_radius;
    aabb.upperBound.y = py + m_radius;
  }

  @Override
  public final void computeMass(final MassData massData, final float density) {
    massData.mass = density * Settings.PI * m_radius * m_radius;
    massData.center.x = m_p.x;
    massData.center.y = m_p.y;

    // inertia about the local origin
    // massData.I = massData.mass * (0.5f * m_radius * m_radius + Vec2.dot(m_p, m_p));
    massData.I = massData.mass * (0.5f * m_radius * m_radius + (m_p.x * m_p.x + m_p.y * m_p.y));
  }
}
