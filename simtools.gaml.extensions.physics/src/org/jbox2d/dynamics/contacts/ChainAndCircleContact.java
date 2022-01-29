/*******************************************************************************************************
 *
 * ChainAndCircleContact.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

/**
 * The Class ChainAndCircleContact.
 */
public class ChainAndCircleContact extends Contact {

  /**
   * Instantiates a new chain and circle contact.
   *
   * @param argPool the arg pool
   */
  public ChainAndCircleContact(IWorldPool argPool) {
    super(argPool);
  }

  @Override
  public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
    super.init(fA, indexA, fB, indexB);
    assert (m_fixtureA.getType() == ShapeType.CHAIN);
    assert (m_fixtureB.getType() == ShapeType.CIRCLE);
  }

  /** The edge. */
  private final EdgeShape edge = new EdgeShape();

  @Override
  public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
    ChainShape chain = (ChainShape) m_fixtureA.getShape();
    chain.getChildEdge(edge, m_indexA);
    pool.getCollision().collideEdgeAndCircle(manifold, edge, xfA,
        (CircleShape) m_fixtureB.getShape(), xfB);
  }
}
