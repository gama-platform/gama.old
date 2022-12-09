/*******************************************************************************************************
 *
 * TypedConstraintType.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.constraintsolver;

/**
 * Typed constraint type.
 * 
 * @author jezek2
 */
public enum TypedConstraintType {
	
	/** The point2point constraint type. */
	POINT2POINT_CONSTRAINT_TYPE,
	
	/** The hinge constraint type. */
	HINGE_CONSTRAINT_TYPE,
	
	/** The conetwist constraint type. */
	CONETWIST_CONSTRAINT_TYPE,
	
	/** The d6 constraint type. */
	D6_CONSTRAINT_TYPE,
	
	/** The vehicle constraint type. */
	VEHICLE_CONSTRAINT_TYPE,
	
	/** The slider constraint type. */
	SLIDER_CONSTRAINT_TYPE
}
