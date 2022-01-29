/*******************************************************************************************************
 *
 * PolygonContact.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

/**
 * The Class PolygonContact.
 */
public class PolygonContact extends Contact {

  /**
   * Instantiates a new polygon contact.
   *
   * @param argPool the arg pool
   */
  public PolygonContact(IWorldPool argPool) {
    super(argPool);
  }

  /**
   * Inits the.
   *
   * @param fixtureA the fixture A
   * @param fixtureB the fixture B
   */
  public void init(Fixture fixtureA, Fixture fixtureB) {
    super.init(fixtureA, 0, fixtureB, 0);
    assert (m_fixtureA.getType() == ShapeType.POLYGON);
    assert (m_fixtureB.getType() == ShapeType.POLYGON);
  }

  @Override
  public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
    pool.getCollision().collidePolygons(manifold, (PolygonShape) m_fixtureA.getShape(), xfA,
        (PolygonShape) m_fixtureB.getShape(), xfB);
  }
}
