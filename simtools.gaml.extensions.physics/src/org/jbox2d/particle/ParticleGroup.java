/*******************************************************************************************************
 *
 * ParticleGroup.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.particle;

import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

/**
 * The Class ParticleGroup.
 */
public class ParticleGroup {

  /** The m system. */
  ParticleSystem m_system;
  
  /** The m first index. */
  int m_firstIndex;
  
  /** The m last index. */
  int m_lastIndex;
  
  /** The m group flags. */
  int m_groupFlags;
  
  /** The m strength. */
  float m_strength;
  
  /** The m prev. */
  ParticleGroup m_prev;
  
  /** The m next. */
  ParticleGroup m_next;

  /** The m timestamp. */
  int m_timestamp;
  
  /** The m mass. */
  float m_mass;
  
  /** The m inertia. */
  float m_inertia;
  
  /** The m center. */
  final Vec2 m_center = new Vec2();
  
  /** The m linear velocity. */
  final Vec2 m_linearVelocity = new Vec2();
  
  /** The m angular velocity. */
  float m_angularVelocity;
  
  /** The m transform. */
  final Transform m_transform = new Transform();

  /** The m destroy automatically. */
  boolean m_destroyAutomatically;
  
  /** The m to be destroyed. */
  boolean m_toBeDestroyed;
  
  /** The m to be split. */
  boolean m_toBeSplit;

  /** The m user data. */
  Object m_userData;

  /**
   * Instantiates a new particle group.
   */
  public ParticleGroup() {
    // m_system = null;
    m_firstIndex = 0;
    m_lastIndex = 0;
    m_groupFlags = 0;
    m_strength = 1.0f;

    m_timestamp = -1;
    m_mass = 0;
    m_inertia = 0;
    m_angularVelocity = 0;
    m_transform.setIdentity();

    m_destroyAutomatically = true;
    m_toBeDestroyed = false;
    m_toBeSplit = false;
  }

  /**
   * Gets the next.
   *
   * @return the next
   */
  public ParticleGroup getNext() {
    return m_next;
  }

  /**
   * Gets the particle count.
   *
   * @return the particle count
   */
  public int getParticleCount() {
    return m_lastIndex - m_firstIndex;
  }

  /**
   * Gets the buffer index.
   *
   * @return the buffer index
   */
  public int getBufferIndex() {
    return m_firstIndex;
  }

  /**
   * Gets the group flags.
   *
   * @return the group flags
   */
  public int getGroupFlags() {
    return m_groupFlags;
  }

  /**
   * Sets the group flags.
   *
   * @param flags the new group flags
   */
  public void setGroupFlags(int flags) {
    m_groupFlags = flags;
  }

  /**
   * Gets the mass.
   *
   * @return the mass
   */
  public float getMass() {
    updateStatistics();
    return m_mass;
  }

  /**
   * Gets the inertia.
   *
   * @return the inertia
   */
  public float getInertia() {
    updateStatistics();
    return m_inertia;
  }

  /**
   * Gets the center.
   *
   * @return the center
   */
  public Vec2 getCenter() {
    updateStatistics();
    return m_center;
  }

  /**
   * Gets the linear velocity.
   *
   * @return the linear velocity
   */
  public Vec2 getLinearVelocity() {
    updateStatistics();
    return m_linearVelocity;
  }

  /**
   * Gets the angular velocity.
   *
   * @return the angular velocity
   */
  public float getAngularVelocity() {
    updateStatistics();
    return m_angularVelocity;
  }

  /**
   * Gets the transform.
   *
   * @return the transform
   */
  public Transform getTransform() {
    return m_transform;
  }

  /**
   * Gets the position.
   *
   * @return the position
   */
  public Vec2 getPosition() {
    return m_transform.p;
  }

  /**
   * Gets the angle.
   *
   * @return the angle
   */
  public float getAngle() {
    return m_transform.q.getAngle();
  }

  /**
   * Gets the user data.
   *
   * @return the user data
   */
  public Object getUserData() {
    return m_userData;
  }

  /**
   * Sets the user data.
   *
   * @param data the new user data
   */
  public void setUserData(Object data) {
    m_userData = data;
  }
  
  

  /**
   * Update statistics.
   */
  public void updateStatistics() {
    if (m_timestamp != m_system.m_timestamp) {
      float m = m_system.getParticleMass();
      m_mass = 0;
      m_center.setZero();
      m_linearVelocity.setZero();
      for (int i = m_firstIndex; i < m_lastIndex; i++) {
        m_mass += m;
        Vec2 pos = m_system.m_positionBuffer.data[i];
        m_center.x += m * pos.x;
        m_center.y += m * pos.y;
        Vec2 vel = m_system.m_velocityBuffer.data[i];
        m_linearVelocity.x += m * vel.x;
        m_linearVelocity.y += m * vel.y;
      }
      if (m_mass > 0) {
        m_center.x *= 1 / m_mass;
        m_center.y *= 1 / m_mass;
        m_linearVelocity.x *= 1 / m_mass;
        m_linearVelocity.y *= 1 / m_mass;
      }
      m_inertia = 0;
      m_angularVelocity = 0;
      for (int i = m_firstIndex; i < m_lastIndex; i++) {
        Vec2 pos = m_system.m_positionBuffer.data[i];
        Vec2 vel = m_system.m_velocityBuffer.data[i];
        float px = pos.x - m_center.x;
        float py = pos.y - m_center.y;
        float vx = vel.x - m_linearVelocity.x;
        float vy = vel.y - m_linearVelocity.y;
        m_inertia += m * (px * px + py * py);
        m_angularVelocity += m * (px * vy - py * vx);
      }
      if (m_inertia > 0) {
        m_angularVelocity *= 1 / m_inertia;
      }
      m_timestamp = m_system.m_timestamp;
    }
  }
}
