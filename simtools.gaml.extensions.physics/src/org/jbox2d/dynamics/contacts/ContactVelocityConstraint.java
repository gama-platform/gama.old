/*******************************************************************************************************
 *
 * ContactVelocityConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;

import org.jbox2d.common.Mat22;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;

/**
 * The Class ContactVelocityConstraint.
 */
public class ContactVelocityConstraint {
  
  /** The points. */
  public VelocityConstraintPoint[] points = new VelocityConstraintPoint[Settings.maxManifoldPoints];
  
  /** The normal. */
  public final Vec2 normal = new Vec2();
  
  /** The normal mass. */
  public final Mat22 normalMass = new Mat22();
  
  /** The k. */
  public final Mat22 K = new Mat22();
  
  /** The index A. */
  public int indexA;
  
  /** The index B. */
  public int indexB;
  
  /** The inv mass B. */
  public float invMassA, invMassB;
  
  /** The inv IB. */
  public float invIA, invIB;
  
  /** The friction. */
  public float friction;
  
  /** The restitution. */
  public float restitution;
  
  /** The tangent speed. */
  public float tangentSpeed;
  
  /** The point count. */
  public int pointCount;
  
  /** The contact index. */
  public int contactIndex;

  /**
   * Instantiates a new contact velocity constraint.
   */
  public ContactVelocityConstraint() {
    for (int i = 0; i < points.length; i++) {
      points[i] = new VelocityConstraintPoint();
    }
  }

  /**
   * The Class VelocityConstraintPoint.
   */
  public static class VelocityConstraintPoint {
    
    /** The r A. */
    public final Vec2 rA = new Vec2();
    
    /** The r B. */
    public final Vec2 rB = new Vec2();
    
    /** The normal impulse. */
    public float normalImpulse;
    
    /** The tangent impulse. */
    public float tangentImpulse;
    
    /** The normal mass. */
    public float normalMass;
    
    /** The tangent mass. */
    public float tangentMass;
    
    /** The velocity bias. */
    public float velocityBias;
  }
}
