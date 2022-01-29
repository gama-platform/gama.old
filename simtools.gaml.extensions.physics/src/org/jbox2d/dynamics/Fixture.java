/*******************************************************************************************************
 *
 * Fixture.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.broadphase.BroadPhase;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;

/**
 * A fixture is used to attach a shape to a body for collision detection. A fixture inherits its
 * transform from its parent. Fixtures hold additional non-geometric data such as friction,
 * collision filters, etc. Fixtures are created via Body::CreateFixture.
 * 
 * @warning you cannot reuse fixtures.
 * 
 * @author daniel
 */
public class Fixture {

  /** The m density. */
  public float m_density;

  /** The m next. */
  public Fixture m_next;
  
  /** The m body. */
  public Body m_body;

  /** The m shape. */
  public Shape m_shape;

  /** The m friction. */
  public float m_friction;
  
  /** The m restitution. */
  public float m_restitution;

  /** The m proxies. */
  public FixtureProxy[] m_proxies;
  
  /** The m proxy count. */
  public int m_proxyCount;

  /** The m filter. */
  public final Filter m_filter;

  /** The m is sensor. */
  public boolean m_isSensor;

  /** The m user data. */
  public Object m_userData;

  /**
   * Instantiates a new fixture.
   */
  public Fixture() {
    m_userData = null;
    m_body = null;
    m_next = null;
    m_proxies = null;
    m_proxyCount = 0;
    m_shape = null;
    m_filter = new Filter();
  }

  /**
   * Get the type of the child shape. You can use this to down cast to the concrete shape.
   * 
   * @return the shape type.
   */
  public ShapeType getType() {
    return m_shape.getType();
  }

  /**
   * Get the child shape. You can modify the child shape, however you should not change the number
   * of vertices because this will crash some collision caching mechanisms.
   * 
   * @return
   */
  public Shape getShape() {
    return m_shape;
  }

  /**
   * Is this fixture a sensor (non-solid)?
   * 
   * @return the true if the shape is a sensor.
   * @return
   */
  public boolean isSensor() {
    return m_isSensor;
  }

  /**
   * Set if this fixture is a sensor.
   * 
   * @param sensor
   */
  public void setSensor(boolean sensor) {
    if (sensor != m_isSensor) {
      m_body.setAwake(true);
      m_isSensor = sensor;
    }
  }

  /**
   * Set the contact filtering data. This is an expensive operation and should not be called
   * frequently. This will not update contacts until the next time step when either parent body is
   * awake. This automatically calls refilter.
   * 
   * @param filter
   */
  public void setFilterData(final Filter filter) {
    m_filter.set(filter);

    refilter();
  }

  /**
   * Get the contact filtering data.
   * 
   * @return
   */
  public Filter getFilterData() {
    return m_filter;
  }

  /**
   * Call this if you want to establish collision that was previously disabled by
   * ContactFilter::ShouldCollide.
   */
  public void refilter() {
    if (m_body == null) {
      return;
    }

    // Flag associated contacts for filtering.
    ContactEdge edge = m_body.getContactList();
    while (edge != null) {
      Contact contact = edge.contact;
      Fixture fixtureA = contact.getFixtureA();
      Fixture fixtureB = contact.getFixtureB();
      if (fixtureA == this || fixtureB == this) {
        contact.flagForFiltering();
      }
      edge = edge.next;
    }

    World world = m_body.getWorld();

    if (world == null) {
      return;
    }

    // Touch each proxy so that new pairs may be created
    BroadPhase broadPhase = world.m_contactManager.m_broadPhase;
    for (int i = 0; i < m_proxyCount; ++i) {
      broadPhase.touchProxy(m_proxies[i].proxyId);
    }
  }

  /**
   * Get the parent body of this fixture. This is NULL if the fixture is not attached.
   * 
   * @return the parent body.
   * @return
   */
  public Body getBody() {
    return m_body;
  }

  /**
   * Get the next fixture in the parent body's fixture list.
   * 
   * @return the next shape.
   * @return
   */
  public Fixture getNext() {
    return m_next;
  }

  /**
   * Sets the density.
   *
   * @param density the new density
   */
  public void setDensity(float density) {
    assert (density >= 0f);
    m_density = density;
  }

  /**
   * Gets the density.
   *
   * @return the density
   */
  public float getDensity() {
    return m_density;
  }

  /**
   * Get the user data that was assigned in the fixture definition. Use this to store your
   * application specific data.
   * 
   * @return
   */
  public Object getUserData() {
    return m_userData;
  }

  /**
   * Set the user data. Use this to store your application specific data.
   * 
   * @param data
   */
  public void setUserData(Object data) {
    m_userData = data;
  }

  /**
   * Test a point for containment in this fixture. This only works for convex shapes.
   * 
   * @param p a point in world coordinates.
   * @return
   */
  public boolean testPoint(final Vec2 p) {
    return m_shape.testPoint(m_body.m_xf, p);
  }

  /**
   * Cast a ray against this shape.
   * 
   * @param output the ray-cast results.
   * @param input the ray-cast input parameters.
   * @param output
   * @param input
   */
  public boolean raycast(RayCastOutput output, RayCastInput input, int childIndex) {
    return m_shape.raycast(output, input, m_body.m_xf, childIndex);
  }

  /**
   * Get the mass data for this fixture. The mass data is based on the density and the shape. The
   * rotational inertia is about the shape's origin.
   * 
   * @return
   */
  public void getMassData(MassData massData) {
    m_shape.computeMass(massData, m_density);
  }

  /**
   * Get the coefficient of friction.
   * 
   * @return
   */
  public float getFriction() {
    return m_friction;
  }

  /**
   * Set the coefficient of friction. This will _not_ change the friction of existing contacts.
   * 
   * @param friction
   */
  public void setFriction(float friction) {
    m_friction = friction;
  }

  /**
   * Get the coefficient of restitution.
   * 
   * @return
   */
  public float getRestitution() {
    return m_restitution;
  }

  /**
   * Set the coefficient of restitution. This will _not_ change the restitution of existing
   * contacts.
   * 
   * @param restitution
   */
  public void setRestitution(float restitution) {
    m_restitution = restitution;
  }

  /**
   * Get the fixture's AABB. This AABB may be enlarge and/or stale. If you need a more accurate
   * AABB, compute it using the shape and the body transform.
   * 
   * @return
   */
  public AABB getAABB(int childIndex) {
    assert (childIndex >= 0 && childIndex < m_proxyCount);
    return m_proxies[childIndex].aabb;
  }

  /**
   * Compute the distance from this fixture.
   * 
   * @param p a point in world coordinates.
   * @return distance
   */
  public float computeDistance(Vec2 p, int childIndex, Vec2 normalOut) {
    return m_shape.computeDistanceToOut(m_body.getTransform(), p, childIndex, normalOut);
  }

  // We need separation create/destroy functions from the constructor/destructor because
  // the destructor cannot access the allocator (no destructor arguments allowed by C++).

  /**
   * Creates the.
   *
   * @param body the body
   * @param def the def
   */
  public void create(Body body, FixtureDef def) {
    m_userData = def.userData;
    m_friction = def.friction;
    m_restitution = def.restitution;

    m_body = body;
    m_next = null;


    m_filter.set(def.filter);

    m_isSensor = def.isSensor;

    m_shape = def.shape.clone();

    // Reserve proxy space
    int childCount = m_shape.getChildCount();
    if (m_proxies == null) {
      m_proxies = new FixtureProxy[childCount];
      for (int i = 0; i < childCount; i++) {
        m_proxies[i] = new FixtureProxy();
        m_proxies[i].fixture = null;
        m_proxies[i].proxyId = BroadPhase.NULL_PROXY;
      }
    }

    if (m_proxies.length < childCount) {
      FixtureProxy[] old = m_proxies;
      int newLen = MathUtils.max(old.length * 2, childCount);
      m_proxies = new FixtureProxy[newLen];
      System.arraycopy(old, 0, m_proxies, 0, old.length);
      for (int i = 0; i < newLen; i++) {
        if (i >= old.length) {
          m_proxies[i] = new FixtureProxy();
        }
        m_proxies[i].fixture = null;
        m_proxies[i].proxyId = BroadPhase.NULL_PROXY;
      }
    }
    m_proxyCount = 0;

    m_density = def.density;
  }

  /**
   * Destroy.
   */
  public void destroy() {
    // The proxies must be destroyed before calling this.
    assert (m_proxyCount == 0);

    // Free the child shape.
    m_shape = null;
    m_proxies = null;
    m_next = null;

    // TODO pool shapes
    // TODO pool fixtures
  }

  /**
   * Creates the proxies.
   *
   * @param broadPhase the broad phase
   * @param xf the xf
   */
  // These support body activation/deactivation.
  public void createProxies(BroadPhase broadPhase, final Transform xf) {
    assert (m_proxyCount == 0);

    // Create proxies in the broad-phase.
    m_proxyCount = m_shape.getChildCount();

    for (int i = 0; i < m_proxyCount; ++i) {
      FixtureProxy proxy = m_proxies[i];
      m_shape.computeAABB(proxy.aabb, xf, i);
      proxy.proxyId = broadPhase.createProxy(proxy.aabb, proxy);
      proxy.fixture = this;
      proxy.childIndex = i;
    }
  }

  /**
   * Internal method
   * 
   * @param broadPhase
   */
  public void destroyProxies(BroadPhase broadPhase) {
    // Destroy proxies in the broad-phase.
    for (int i = 0; i < m_proxyCount; ++i) {
      FixtureProxy proxy = m_proxies[i];
      broadPhase.destroyProxy(proxy.proxyId);
      proxy.proxyId = BroadPhase.NULL_PROXY;
    }

    m_proxyCount = 0;
  }

  /** The pool 1. */
  private final AABB pool1 = new AABB();
  
  /** The pool 2. */
  private final AABB pool2 = new AABB();
  
  /** The displacement. */
  private final Vec2 displacement = new Vec2();

  /**
   * Internal method
   * 
   * @param broadPhase
   * @param xf1
   * @param xf2
   */
  protected void synchronize(BroadPhase broadPhase, final Transform transform1,
      final Transform transform2) {
    if (m_proxyCount == 0) {
      return;
    }

    for (int i = 0; i < m_proxyCount; ++i) {
      FixtureProxy proxy = m_proxies[i];

      // Compute an AABB that covers the swept shape (may miss some rotation effect).
      final AABB aabb1 = pool1;
      final AABB aab = pool2;
      m_shape.computeAABB(aabb1, transform1, proxy.childIndex);
      m_shape.computeAABB(aab, transform2, proxy.childIndex);

      proxy.aabb.lowerBound.x =
          aabb1.lowerBound.x < aab.lowerBound.x ? aabb1.lowerBound.x : aab.lowerBound.x;
      proxy.aabb.lowerBound.y =
          aabb1.lowerBound.y < aab.lowerBound.y ? aabb1.lowerBound.y : aab.lowerBound.y;
      proxy.aabb.upperBound.x =
          aabb1.upperBound.x > aab.upperBound.x ? aabb1.upperBound.x : aab.upperBound.x;
      proxy.aabb.upperBound.y =
          aabb1.upperBound.y > aab.upperBound.y ? aabb1.upperBound.y : aab.upperBound.y;
      displacement.x = transform2.p.x - transform1.p.x;
      displacement.y = transform2.p.y - transform1.p.y;

      broadPhase.moveProxy(proxy.proxyId, proxy.aabb, displacement);
    }
  }
}
