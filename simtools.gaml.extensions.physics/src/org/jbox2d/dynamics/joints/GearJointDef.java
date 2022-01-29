/*******************************************************************************************************
 *
 * GearJointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 5:20:39 AM Jan 22, 2011
 */
package org.jbox2d.dynamics.joints;

/**
 * Gear joint definition. This definition requires two existing revolute or prismatic joints (any
 * combination will work). The provided joints must attach a dynamic body to a static body.
 * 
 * @author Daniel Murphy
 */
public class GearJointDef extends JointDef {
  /**
   * The first revolute/prismatic joint attached to the gear joint.
   */
  public Joint joint1;

  /**
   * The second revolute/prismatic joint attached to the gear joint.
   */
  public Joint joint2;

  /**
   * Gear ratio.
   * 
   * @see GearJoint
   */
  public float ratio;

  /**
   * Instantiates a new gear joint def.
   */
  public GearJointDef() {
    super(JointType.GEAR);
    joint1 = null;
    joint2 = null;
  }
}
