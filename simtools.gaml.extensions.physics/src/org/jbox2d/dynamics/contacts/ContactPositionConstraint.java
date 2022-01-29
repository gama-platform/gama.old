/*******************************************************************************************************
 *
 * ContactPositionConstraint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.Manifold.ManifoldType;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;

/**
 * The Class ContactPositionConstraint.
 */
public class ContactPositionConstraint {
  
  /** The local points. */
  Vec2[] localPoints = new Vec2[Settings.maxManifoldPoints];
  
  /** The local normal. */
  final Vec2 localNormal = new Vec2();
  
  /** The local point. */
  final Vec2 localPoint = new Vec2();
  
  /** The index A. */
  int indexA;
  
  /** The index B. */
  int indexB;
  
  /** The inv mass B. */
  float invMassA, invMassB;
  
  /** The local center A. */
  final Vec2 localCenterA = new Vec2();
  
  /** The local center B. */
  final Vec2 localCenterB = new Vec2();
  
  /** The inv IB. */
  float invIA, invIB;
  
  /** The type. */
  ManifoldType type;
  
  /** The radius B. */
  float radiusA, radiusB;
  
  /** The point count. */
  int pointCount;

  /**
   * Instantiates a new contact position constraint.
   */
  public ContactPositionConstraint() {
    for (int i = 0; i < localPoints.length; i++) {
      localPoints[i] = new Vec2();
    }
  }
}
