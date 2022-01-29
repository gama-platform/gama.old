/*******************************************************************************************************
 *
 * BodyType.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 3:59:59 AM Jul 7, 2010
 */
package org.jbox2d.dynamics;

// updated to rev 100

/**
 * The body type.
 * static: zero mass, zero velocity, may be manually moved
 * kinematic: zero mass, non-zero velocity set by user, moved by solver
 * dynamic: positive mass, non-zero velocity determined by forces, moved by solver
 * 
 * @author daniel
 */
public enum BodyType {
	
	/** The static. */
	STATIC, 
 /** The kinematic. */
 KINEMATIC, 
 /** The dynamic. */
 DYNAMIC
}
