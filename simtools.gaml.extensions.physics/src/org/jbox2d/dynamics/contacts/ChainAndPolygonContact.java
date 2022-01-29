/*******************************************************************************************************
 *
 * ChainAndPolygonContact.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

/**
 * The Class ChainAndPolygonContact.
 */
public class ChainAndPolygonContact extends Contact {

  /**
   * Instantiates a new chain and polygon contact.
   *
   * @param argPool the arg pool
   */
  public ChainAndPolygonContact(IWorldPool argPool) {
    super(argPool);
  }

  @Override
  public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
    super.init(fA, indexA, fB, indexB);
    assert (m_fixtureA.getType() == ShapeType.CHAIN);
    assert (m_fixtureB.getType() == ShapeType.POLYGON);
  }

  /** The edge. */
  private final EdgeShape edge = new EdgeShape();

  @Override
  public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
    ChainShape chain = (ChainShape) m_fixtureA.getShape();
    chain.getChildEdge(edge, m_indexA);
    pool.getCollision().collideEdgeAndPolygon(manifold, edge, xfA,
        (PolygonShape) m_fixtureB.getShape(), xfB);
  }
}
