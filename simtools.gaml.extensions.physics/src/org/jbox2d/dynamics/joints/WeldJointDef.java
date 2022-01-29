/*******************************************************************************************************
 *
 * WeldJointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;

/**
 * Created at 3:38:52 AM Jan 15, 2011
 */

/**
 * @author Daniel Murphy
 */
public class WeldJointDef extends JointDef {
  /**
   * The local anchor point relative to body1's origin.
   */
  public final Vec2 localAnchorA;

  /**
   * The local anchor point relative to body2's origin.
   */
  public final Vec2 localAnchorB;

  /**
   * The body2 angle minus body1 angle in the reference state (radians).
   */
  public float referenceAngle;

  /**
   * The mass-spring-damper frequency in Hertz. Rotation only. Disable softness with a value of 0.
   */
  public float frequencyHz;

  /**
   * The damping ratio. 0 = no damping, 1 = critical damping.
   */
  public float dampingRatio;

  /**
   * Instantiates a new weld joint def.
   */
  public WeldJointDef() {
    super(JointType.WELD);
    localAnchorA = new Vec2();
    localAnchorB = new Vec2();
    referenceAngle = 0.0f;
  }

  /**
   * Initialize the bodies, anchors, and reference angle using a world anchor point.
   * 
   * @param bA
   * @param bB
   * @param anchor
   */
  public void initialize(Body bA, Body bB, Vec2 anchor) {
    bodyA = bA;
    bodyB = bB;
    bodyA.getLocalPointToOut(anchor, localAnchorA);
    bodyB.getLocalPointToOut(anchor, localAnchorB);
    referenceAngle = bodyB.getAngle() - bodyA.getAngle();
  }
}
