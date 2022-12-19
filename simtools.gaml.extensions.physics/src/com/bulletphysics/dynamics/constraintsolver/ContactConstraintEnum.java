/*******************************************************************************************************
 *
 * ContactConstraintEnum.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

/**
 * TODO: name
 * 
 * @author jezek2
 */
enum ContactConstraintEnum {
	
	/** The default contact solver type. */
	DEFAULT_CONTACT_SOLVER_TYPE,
	
	/** The contact solver type1. */
	CONTACT_SOLVER_TYPE1,
	
	/** The contact solver type2. */
	CONTACT_SOLVER_TYPE2,
	
	/** The user contact solver type1. */
	USER_CONTACT_SOLVER_TYPE1,
	
	/** The max contact solver types. */
	MAX_CONTACT_SOLVER_TYPES
}
