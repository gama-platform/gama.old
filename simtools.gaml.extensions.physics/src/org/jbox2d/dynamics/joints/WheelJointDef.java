/*******************************************************************************************************
 *
 * WheelJointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 7:27:31 AM Jan 21, 2011
 */
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Wheel joint definition. This requires defining a line of motion using an axis and an anchor
 * point. The definition uses local anchor points and a local axis so that the initial configuration
 * can violate the constraint slightly. The joint translation is zero when the local anchor points
 * coincide in world space. Using local anchors and a local axis helps when saving and loading a
 * game.
 * 
 * @author Daniel Murphy
 */
public class WheelJointDef extends JointDef {

  /**
   * The local anchor point relative to body1's origin.
   */
  public final Vec2 localAnchorA = new Vec2();

  /**
   * The local anchor point relative to body2's origin.
   */
  public final Vec2 localAnchorB = new Vec2();

  /**
   * The local translation axis in body1.
   */
  public final Vec2 localAxisA = new Vec2();

  /**
   * Enable/disable the joint motor.
   */
  public boolean enableMotor;

  /**
   * The maximum motor torque, usually in N-m.
   */
  public float maxMotorTorque;

  /**
   * The desired motor speed in radians per second.
   */
  public float motorSpeed;

  /**
   * Suspension frequency, zero indicates no suspension
   */
  public float frequencyHz;

  /**
   * Suspension damping ratio, one indicates critical damping
   */
  public float dampingRatio;

  /**
   * Instantiates a new wheel joint def.
   */
  public WheelJointDef() {
    super(JointType.WHEEL);
    localAxisA.set(1, 0);
    enableMotor = false;
    maxMotorTorque = 0f;
    motorSpeed = 0f;
  }

  /**
   * Initialize.
   *
   * @param b1 the b 1
   * @param b2 the b 2
   * @param anchor the anchor
   * @param axis the axis
   */
  public void initialize(Body b1, Body b2, Vec2 anchor, Vec2 axis) {
    bodyA = b1;
    bodyB = b2;
    b1.getLocalPointToOut(anchor, localAnchorA);
    b2.getLocalPointToOut(anchor, localAnchorB);
    bodyA.getLocalVectorToOut(axis, localAxisA);
  }
}
