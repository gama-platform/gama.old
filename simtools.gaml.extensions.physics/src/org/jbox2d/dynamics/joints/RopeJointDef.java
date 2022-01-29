/*******************************************************************************************************
 *
 * RopeJointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;

/**
 * Rope joint definition. This requires two body anchor points and a maximum lengths. Note: by
 * default the connected objects will not collide. see collideConnected in b2JointDef.
 * 
 * @author Daniel Murphy
 */
public class RopeJointDef extends JointDef {

  /**
   * The local anchor point relative to bodyA's origin.
   */
  public final Vec2 localAnchorA = new Vec2();

  /**
   * The local anchor point relative to bodyB's origin.
   */
  public final Vec2 localAnchorB = new Vec2();

  /**
   * The maximum length of the rope. Warning: this must be larger than b2_linearSlop or the joint
   * will have no effect.
   */
  public float maxLength;

  /**
   * Instantiates a new rope joint def.
   */
  public RopeJointDef() {
    super(JointType.ROPE);
    localAnchorA.set(-1.0f, 0.0f);
    localAnchorB.set(1.0f, 0.0f);
  }
}
