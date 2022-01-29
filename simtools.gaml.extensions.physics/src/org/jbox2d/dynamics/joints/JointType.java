/*******************************************************************************************************
 *
 * JointType.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.joints;

/**
 * The Enum JointType.
 */
public enum JointType {
  
  /** The unknown. */
  UNKNOWN, 
 /** The revolute. */
 REVOLUTE, 
 /** The prismatic. */
 PRISMATIC, 
 /** The distance. */
 DISTANCE, 
 /** The pulley. */
 PULLEY, 
 /** The mouse. */
 MOUSE, 
 /** The gear. */
 GEAR, 
 /** The wheel. */
 WHEEL, 
 /** The weld. */
 WELD, 
 /** The friction. */
 FRICTION, 
 /** The rope. */
 ROPE, 
 /** The constant volume. */
 CONSTANT_VOLUME, 
 /** The motor. */
 MOTOR
}
