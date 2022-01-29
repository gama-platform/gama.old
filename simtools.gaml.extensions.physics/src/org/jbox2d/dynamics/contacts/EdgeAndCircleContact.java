/*******************************************************************************************************
 *
 * EdgeAndCircleContact.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

/**
 * The Class EdgeAndCircleContact.
 */
public class EdgeAndCircleContact extends Contact {

  /**
   * Instantiates a new edge and circle contact.
   *
   * @param argPool the arg pool
   */
  public EdgeAndCircleContact(IWorldPool argPool) {
    super(argPool);
  }

  @Override
  public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
    super.init(fA, indexA, fB, indexB);
    assert (m_fixtureA.getType() == ShapeType.EDGE);
    assert (m_fixtureB.getType() == ShapeType.CIRCLE);
  }

  @Override
  public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
    pool.getCollision().collideEdgeAndCircle(manifold, (EdgeShape) m_fixtureA.getShape(), xfA,
        (CircleShape) m_fixtureB.getShape(), xfB);
  }
}
