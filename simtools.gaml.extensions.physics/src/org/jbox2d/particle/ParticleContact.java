/*******************************************************************************************************
 *
 * ParticleContact.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.particle;

import org.jbox2d.common.Vec2;

/**
 * The Class ParticleContact.
 */
public class ParticleContact {
  /** Indices of the respective particles making contact. */
  public int indexA, indexB;
  /** The logical sum of the particle behaviors that have been set. */
  public int flags;
  /** Weight of the contact. A value between 0.0f and 1.0f. */
  public float weight;
  /** The normalized direction from A to B. */
  public final Vec2 normal = new Vec2();
}
