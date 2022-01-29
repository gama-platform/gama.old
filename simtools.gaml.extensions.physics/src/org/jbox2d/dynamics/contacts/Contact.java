/*******************************************************************************************************
 *
 * Contact.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;


import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.ContactID;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.ManifoldPoint;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

/**
 * The class manages contact between two shapes. A contact exists for each overlapping AABB in the
 * broad-phase (except if filtered). Therefore a contact object may exist that has no contact
 * points.
 * 
 * @author daniel
 */
public abstract class Contact {

  // Flags stored in m_flags
  /** The Constant ISLAND_FLAG. */
  // Used when crawling contact graph when forming islands.
  public static final int ISLAND_FLAG = 0x0001;
  
  /** The Constant TOUCHING_FLAG. */
  // Set when the shapes are touching.
  public static final int TOUCHING_FLAG = 0x0002;
  
  /** The Constant ENABLED_FLAG. */
  // This contact can be disabled (by user)
  public static final int ENABLED_FLAG = 0x0004;
  
  /** The Constant FILTER_FLAG. */
  // This contact needs filtering because a fixture filter was changed.
  public static final int FILTER_FLAG = 0x0008;
  
  /** The Constant BULLET_HIT_FLAG. */
  // This bullet contact had a TOI event
  public static final int BULLET_HIT_FLAG = 0x0010;

  /** The Constant TOI_FLAG. */
  public static final int TOI_FLAG = 0x0020;

  /** The m flags. */
  public int m_flags;

  /** The m prev. */
  // World pool and list pointers.
  public Contact m_prev;
  
  /** The m next. */
  public Contact m_next;

  /** The m node A. */
  // Nodes for connecting bodies.
  public ContactEdge m_nodeA = null;
  
  /** The m node B. */
  public ContactEdge m_nodeB = null;

  /** The m fixture A. */
  public Fixture m_fixtureA;
  
  /** The m fixture B. */
  public Fixture m_fixtureB;

  /** The m index A. */
  public int m_indexA;
  
  /** The m index B. */
  public int m_indexB;

  /** The m manifold. */
  public final Manifold m_manifold;

  /** The m toi count. */
  public float m_toiCount;
  
  /** The m toi. */
  public float m_toi;

  /** The m friction. */
  public float m_friction;
  
  /** The m restitution. */
  public float m_restitution;

  /** The m tangent speed. */
  public float m_tangentSpeed;

  /** The pool. */
  protected final IWorldPool pool;

  /**
   * Instantiates a new contact.
   *
   * @param argPool the arg pool
   */
  protected Contact(IWorldPool argPool) {
    m_fixtureA = null;
    m_fixtureB = null;
    m_nodeA = new ContactEdge();
    m_nodeB = new ContactEdge();
    m_manifold = new Manifold();
    pool = argPool;
  }

  /** initialization for pooling */
  public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
    m_flags = ENABLED_FLAG;

    m_fixtureA = fA;
    m_fixtureB = fB;

    m_indexA = indexA;
    m_indexB = indexB;

    m_manifold.pointCount = 0;

    m_prev = null;
    m_next = null;

    m_nodeA.contact = null;
    m_nodeA.prev = null;
    m_nodeA.next = null;
    m_nodeA.other = null;

    m_nodeB.contact = null;
    m_nodeB.prev = null;
    m_nodeB.next = null;
    m_nodeB.other = null;

    m_toiCount = 0;
    m_friction = Contact.mixFriction(fA.m_friction, fB.m_friction);
    m_restitution = Contact.mixRestitution(fA.m_restitution, fB.m_restitution);

    m_tangentSpeed = 0;
  }

  /**
   * Get the contact manifold. Do not set the point count to zero. Instead call Disable.
   */
  public Manifold getManifold() {
    return m_manifold;
  }

  /**
   * Get the world manifold.
   */
  public void getWorldManifold(WorldManifold worldManifold) {
    final Body bodyA = m_fixtureA.getBody();
    final Body bodyB = m_fixtureB.getBody();
    final Shape shapeA = m_fixtureA.getShape();
    final Shape shapeB = m_fixtureB.getShape();

    worldManifold.initialize(m_manifold, bodyA.getTransform(), shapeA.m_radius,
        bodyB.getTransform(), shapeB.m_radius);
  }

  /**
   * Is this contact touching
   * 
   * @return
   */
  public boolean isTouching() {
    return (m_flags & TOUCHING_FLAG) == TOUCHING_FLAG;
  }

  /**
   * Enable/disable this contact. This can be used inside the pre-solve contact listener. The
   * contact is only disabled for the current time step (or sub-step in continuous collisions).
   * 
   * @param flag
   */
  public void setEnabled(boolean flag) {
    if (flag) {
      m_flags |= ENABLED_FLAG;
    } else {
      m_flags &= ~ENABLED_FLAG;
    }
  }

  /**
   * Has this contact been disabled?
   * 
   * @return
   */
  public boolean isEnabled() {
    return (m_flags & ENABLED_FLAG) == ENABLED_FLAG;
  }

  /**
   * Get the next contact in the world's contact list.
   * 
   * @return
   */
  public Contact getNext() {
    return m_next;
  }

  /**
   * Get the first fixture in this contact.
   * 
   * @return
   */
  public Fixture getFixtureA() {
    return m_fixtureA;
  }

  /**
   * Gets the child index A.
   *
   * @return the child index A
   */
  public int getChildIndexA() {
    return m_indexA;
  }

  /**
   * Get the second fixture in this contact.
   * 
   * @return
   */
  public Fixture getFixtureB() {
    return m_fixtureB;
  }

  /**
   * Gets the child index B.
   *
   * @return the child index B
   */
  public int getChildIndexB() {
    return m_indexB;
  }

  /**
   * Sets the friction.
   *
   * @param friction the new friction
   */
  public void setFriction(float friction) {
    m_friction = friction;
  }

  /**
   * Gets the friction.
   *
   * @return the friction
   */
  public float getFriction() {
    return m_friction;
  }

  /**
   * Reset friction.
   */
  public void resetFriction() {
    m_friction = Contact.mixFriction(m_fixtureA.m_friction, m_fixtureB.m_friction);
  }

  /**
   * Sets the restitution.
   *
   * @param restitution the new restitution
   */
  public void setRestitution(float restitution) {
    m_restitution = restitution;
  }

  /**
   * Gets the restitution.
   *
   * @return the restitution
   */
  public float getRestitution() {
    return m_restitution;
  }

  /**
   * Reset restitution.
   */
  public void resetRestitution() {
    m_restitution = Contact.mixRestitution(m_fixtureA.m_restitution, m_fixtureB.m_restitution);
  }

  /**
   * Sets the tangent speed.
   *
   * @param speed the new tangent speed
   */
  public void setTangentSpeed(float speed) {
    m_tangentSpeed = speed;
  }

  /**
   * Gets the tangent speed.
   *
   * @return the tangent speed
   */
  public float getTangentSpeed() {
    return m_tangentSpeed;
  }

  /**
   * Evaluate.
   *
   * @param manifold the manifold
   * @param xfA the xf A
   * @param xfB the xf B
   */
  public abstract void evaluate(Manifold manifold, Transform xfA, Transform xfB);

  /**
   * Flag this contact for filtering. Filtering will occur the next time step.
   */
  public void flagForFiltering() {
    m_flags |= FILTER_FLAG;
  }

  /** The old manifold. */
  // djm pooling
  private final Manifold oldManifold = new Manifold();

  /**
   * Update.
   *
   * @param listener the listener
   */
  public void update(ContactListener listener) {

    oldManifold.set(m_manifold);

    // Re-enable this contact.
    m_flags |= ENABLED_FLAG;

    boolean touching = false;
    boolean wasTouching = (m_flags & TOUCHING_FLAG) == TOUCHING_FLAG;

    boolean sensorA = m_fixtureA.isSensor();
    boolean sensorB = m_fixtureB.isSensor();
    boolean sensor = sensorA || sensorB;

    Body bodyA = m_fixtureA.getBody();
    Body bodyB = m_fixtureB.getBody();
    Transform xfA = bodyA.getTransform();
    Transform xfB = bodyB.getTransform();
    // log.debug("TransformA: "+xfA);
    // log.debug("TransformB: "+xfB);

    if (sensor) {
      Shape shapeA = m_fixtureA.getShape();
      Shape shapeB = m_fixtureB.getShape();
      touching = pool.getCollision().testOverlap(shapeA, m_indexA, shapeB, m_indexB, xfA, xfB);

      // Sensors don't generate manifolds.
      m_manifold.pointCount = 0;
    } else {
      evaluate(m_manifold, xfA, xfB);
      touching = m_manifold.pointCount > 0;

      // Match old contact ids to new contact ids and copy the
      // stored impulses to warm start the solver.
      for (int i = 0; i < m_manifold.pointCount; ++i) {
        ManifoldPoint mp2 = m_manifold.points[i];
        mp2.normalImpulse = 0.0f;
        mp2.tangentImpulse = 0.0f;
        ContactID id2 = mp2.id;

        for (int j = 0; j < oldManifold.pointCount; ++j) {
          ManifoldPoint mp1 = oldManifold.points[j];

          if (mp1.id.isEqual(id2)) {
            mp2.normalImpulse = mp1.normalImpulse;
            mp2.tangentImpulse = mp1.tangentImpulse;
            break;
          }
        }
      }

      if (touching != wasTouching) {
        bodyA.setAwake(true);
        bodyB.setAwake(true);
      }
    }

    if (touching) {
      m_flags |= TOUCHING_FLAG;
    } else {
      m_flags &= ~TOUCHING_FLAG;
    }

    if (listener == null) {
      return;
    }

    if (wasTouching == false && touching == true) {
      listener.beginContact(this);
    }

    if (wasTouching == true && touching == false) {
      listener.endContact(this);
    }

    if (sensor == false && touching) {
      listener.preSolve(this, oldManifold);
    }
  }

  /**
   * Friction mixing law. The idea is to allow either fixture to drive the restitution to zero. For
   * example, anything slides on ice.
   * 
   * @param friction1
   * @param friction2
   * @return
   */
  public static final float mixFriction(float friction1, float friction2) {
    return MathUtils.sqrt(friction1 * friction2);
  }

  /**
   * Restitution mixing law. The idea is allow for anything to bounce off an inelastic surface. For
   * example, a superball bounces on anything.
   * 
   * @param restitution1
   * @param restitution2
   * @return
   */
  public static final float mixRestitution(float restitution1, float restitution2) {
    return restitution1 > restitution2 ? restitution1 : restitution2;
  }
}
