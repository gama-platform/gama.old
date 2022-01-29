/*******************************************************************************************************
 *
 * ParticleType.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.particle;

/**
 * The particle type. Can be combined with | operator. Zero means liquid.
 * 
 * @author dmurph
 */
public class ParticleType {
  
  /** The Constant b2_waterParticle. */
  public static final int b2_waterParticle = 0;
  /** removed after next step */
  public static final int b2_zombieParticle = 1 << 1;
  /** zero velocity */
  public static final int b2_wallParticle = 1 << 2;
  /** with restitution from stretching */
  public static final int b2_springParticle = 1 << 3;
  /** with restitution from deformation */
  public static final int b2_elasticParticle = 1 << 4;
  /** with viscosity */
  public static final int b2_viscousParticle = 1 << 5;
  /** without isotropic pressure */
  public static final int b2_powderParticle = 1 << 6;
  /** with surface tension */
  public static final int b2_tensileParticle = 1 << 7;
  /** mixing color between contacting particles */
  public static final int b2_colorMixingParticle = 1 << 8;
  /** call b2DestructionListener on destruction */
  public static final int b2_destructionListener = 1 << 9;
}
