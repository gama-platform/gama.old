/*******************************************************************************************************
 *
 * JointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;

/**
 * Joint definitions are used to construct joints.
 * @author Daniel Murphy
 */
public class JointDef {

	/**
	 * Instantiates a new joint def.
	 *
	 * @param type the joint type is set automatically for concrete joint types.
	 */
	public JointDef(JointType type){
		this.type = type;
		userData = null;
		bodyA = null;
		bodyB = null;
		collideConnected = false;
	}
	/**
	 * The joint type is set automatically for concrete joint types.
	 */
	public JointType type;
	
	/**
	 * Use this to attach application specific data to your joints.
	 */
	public Object userData;
	
	/**
	 * The first attached body.
	 */
	public Body bodyA;
	
	/**
	 * The second attached body.
	 */
	public Body bodyB;
	
	/**
	 * Set this flag to true if the attached bodies should collide.
	 */
	public boolean collideConnected;
}
