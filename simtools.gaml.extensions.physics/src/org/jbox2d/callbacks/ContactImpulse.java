/*******************************************************************************************************
 *
 * ContactImpulse.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 3:43:53 AM Jul 7, 2010
 */
package org.jbox2d.callbacks;

import org.jbox2d.common.Settings;

/**
 * Contact impulses for reporting. Impulses are used instead of forces because sub-step forces may
 * approach infinity for rigid body collisions. These match up one-to-one with the contact points in
 * b2Manifold.
 * 
 * @author Daniel Murphy
 */
public class ContactImpulse {
  
  /** The normal impulses. */
  public float[] normalImpulses = new float[Settings.maxManifoldPoints];
  
  /** The tangent impulses. */
  public float[] tangentImpulses = new float[Settings.maxManifoldPoints];
  
  /** The count. */
  public int count;
}
