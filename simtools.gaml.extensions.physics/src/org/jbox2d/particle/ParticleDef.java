/*******************************************************************************************************
 *
 * ParticleDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * The Class ParticleDef.
 */
public class ParticleDef {
  /**
   * Specifies the type of particle. A particle may be more than one type. Multiple types are
   * chained by logical sums, for example: pd.flags = ParticleType.b2_elasticParticle |
   * ParticleType.b2_viscousParticle.
   */
  int flags;

  /** The world position of the particle. */
  public final Vec2 position = new Vec2();

  /** The linear velocity of the particle in world co-ordinates. */
  public final Vec2 velocity = new Vec2();

  /** The color of the particle. */
  public ParticleColor color;

  /** Use this to store application-specific body data. */
  public Object userData;
}
