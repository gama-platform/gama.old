/*******************************************************************************************************
 *
 * ChainShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * A chain shape is a free form sequence of line segments. The chain has two-sided collision, so you
 * can use inside and outside collision. Therefore, you may use any winding order. Connectivity
 * information is used to create smooth collisions. WARNING: The chain will not collide properly if
 * there are self-intersections.
 * 
 * @author Daniel
 */
public class ChainShape extends Shape {

  /** The m vertices. */
  public Vec2[] m_vertices;
  
  /** The m count. */
  public int m_count;
  
  /** The m next vertex. */
  public final Vec2 m_prevVertex = new Vec2(), m_nextVertex = new Vec2();
  
  /** The m has next vertex. */
  public boolean m_hasPrevVertex = false, m_hasNextVertex = false;

  /** The pool 0. */
  private final EdgeShape pool0 = new EdgeShape();

  /**
   * Instantiates a new chain shape.
   */
  public ChainShape() {
    super(ShapeType.CHAIN);
    m_vertices = null;
    m_radius = Settings.polygonRadius;
    m_count = 0;
  }

  /**
   * Clear.
   */
  public void clear() {
    m_vertices = null;
    m_count = 0;
  }

  @Override
  public int getChildCount() {
    return m_count - 1;
  }

  /**
   * Get a child edge.
   */
  public void getChildEdge(EdgeShape edge, int index) {
    assert (0 <= index && index < m_count - 1);
    edge.m_radius = m_radius;

    final Vec2 v0 = m_vertices[index + 0];
    final Vec2 v1 = m_vertices[index + 1];
    edge.m_vertex1.x = v0.x;
    edge.m_vertex1.y = v0.y;
    edge.m_vertex2.x = v1.x;
    edge.m_vertex2.y = v1.y;

    if (index > 0) {
      Vec2 v = m_vertices[index - 1];
      edge.m_vertex0.x = v.x;
      edge.m_vertex0.y = v.y;
      edge.m_hasVertex0 = true;
    } else {
      edge.m_vertex0.x = m_prevVertex.x;
      edge.m_vertex0.y = m_prevVertex.y;
      edge.m_hasVertex0 = m_hasPrevVertex;
    }

    if (index < m_count - 2) {
      Vec2 v = m_vertices[index + 2];
      edge.m_vertex3.x = v.x;
      edge.m_vertex3.y = v.y;
      edge.m_hasVertex3 = true;
    } else {
      edge.m_vertex3.x = m_nextVertex.x;
      edge.m_vertex3.y = m_nextVertex.y;
      edge.m_hasVertex3 = m_hasNextVertex;
    }
  }

  @Override
  public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
    final EdgeShape edge = pool0;
    getChildEdge(edge, childIndex);
    return edge.computeDistanceToOut(xf, p, 0, normalOut);
  }

  @Override
  public boolean testPoint(Transform xf, Vec2 p) {
    return false;
  }

  @Override
  public boolean raycast(RayCastOutput output, RayCastInput input, Transform xf, int childIndex) {
    assert (childIndex < m_count);

    final EdgeShape edgeShape = pool0;

    int i1 = childIndex;
    int i2 = childIndex + 1;
    if (i2 == m_count) {
      i2 = 0;
    }
    Vec2 v = m_vertices[i1];
    edgeShape.m_vertex1.x = v.x;
    edgeShape.m_vertex1.y = v.y;
    Vec2 v1 = m_vertices[i2];
    edgeShape.m_vertex2.x = v1.x;
    edgeShape.m_vertex2.y = v1.y;

    return edgeShape.raycast(output, input, xf, 0);
  }

  @Override
  public void computeAABB(AABB aabb, Transform xf, int childIndex) {
    assert (childIndex < m_count);
    final Vec2 lower = aabb.lowerBound;
    final Vec2 upper = aabb.upperBound;

    int i1 = childIndex;
    int i2 = childIndex + 1;
    if (i2 == m_count) {
      i2 = 0;
    }

    final Vec2 vi1 = m_vertices[i1];
    final Vec2 vi2 = m_vertices[i2];
    final Rot xfq = xf.q;
    final Vec2 xfp = xf.p;
    float v1x = (xfq.c * vi1.x - xfq.s * vi1.y) + xfp.x;
    float v1y = (xfq.s * vi1.x + xfq.c * vi1.y) + xfp.y;
    float v2x = (xfq.c * vi2.x - xfq.s * vi2.y) + xfp.x;
    float v2y = (xfq.s * vi2.x + xfq.c * vi2.y) + xfp.y;

    lower.x = v1x < v2x ? v1x : v2x;
    lower.y = v1y < v2y ? v1y : v2y;
    upper.x = v1x > v2x ? v1x : v2x;
    upper.y = v1y > v2y ? v1y : v2y;
  }

  @Override
  public void computeMass(MassData massData, float density) {
    massData.mass = 0.0f;
    massData.center.setZero();
    massData.I = 0.0f;
  }

  @Override
  public Shape clone() {
    ChainShape clone = new ChainShape();
    clone.createChain(m_vertices, m_count);
    clone.m_prevVertex.set(m_prevVertex);
    clone.m_nextVertex.set(m_nextVertex);
    clone.m_hasPrevVertex = m_hasPrevVertex;
    clone.m_hasNextVertex = m_hasNextVertex;
    return clone;
  }

  /**
   * Create a loop. This automatically adjusts connectivity.
   * 
   * @param vertices an array of vertices, these are copied
   * @param count the vertex count
   */
  public void createLoop(final Vec2[] vertices, int count) {
    assert (m_vertices == null && m_count == 0);
    assert (count >= 3);
    m_count = count + 1;
    m_vertices = new Vec2[m_count];
    for (int i = 1; i < count; i++) {
      Vec2 v1 = vertices[i - 1];
      Vec2 v2 = vertices[i];
      // If the code crashes here, it means your vertices are too close together.
      if (MathUtils.distanceSquared(v1, v2) < Settings.linearSlop * Settings.linearSlop) {
        throw new RuntimeException("Vertices of chain shape are too close together");
      }
    }
    for (int i = 0; i < count; i++) {
      m_vertices[i] = new Vec2(vertices[i]);
    }
    m_vertices[count] = new Vec2(m_vertices[0]);
    m_prevVertex.set(m_vertices[m_count - 2]);
    m_nextVertex.set(m_vertices[1]);
    m_hasPrevVertex = true;
    m_hasNextVertex = true;
  }

  /**
   * Create a chain with isolated end vertices.
   * 
   * @param vertices an array of vertices, these are copied
   * @param count the vertex count
   */
  public void createChain(final Vec2 vertices[], int count) {
    assert (m_vertices == null && m_count == 0);
    assert (count >= 2);
    m_count = count;
    m_vertices = new Vec2[m_count];
    for (int i = 1; i < m_count; i++) {
      Vec2 v1 = vertices[i - 1];
      Vec2 v2 = vertices[i];
      // If the code crashes here, it means your vertices are too close together.
      if (MathUtils.distanceSquared(v1, v2) < Settings.linearSlop * Settings.linearSlop) {
        throw new RuntimeException("Vertices of chain shape are too close together");
      }
    }
    for (int i = 0; i < m_count; i++) {
      m_vertices[i] = new Vec2(vertices[i]);
    }
    m_hasPrevVertex = false;
    m_hasNextVertex = false;

    m_prevVertex.setZero();
    m_nextVertex.setZero();
  }

  /**
   * Establish connectivity to a vertex that precedes the first vertex. Don't call this for loops.
   * 
   * @param prevVertex
   */
  public void setPrevVertex(final Vec2 prevVertex) {
    m_prevVertex.set(prevVertex);
    m_hasPrevVertex = true;
  }

  /**
   * Establish connectivity to a vertex that follows the last vertex. Don't call this for loops.
   * 
   * @param nextVertex
   */
  public void setNextVertex(final Vec2 nextVertex) {
    m_nextVertex.set(nextVertex);
    m_hasNextVertex = true;
  }
}
