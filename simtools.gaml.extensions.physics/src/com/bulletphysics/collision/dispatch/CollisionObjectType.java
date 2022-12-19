/*******************************************************************************************************
 *
 * CollisionObjectType.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

/**
 * Collision object type.
 * 
 * @author jezek2
 */
public enum CollisionObjectType {
	
	/** The collision object. */
	COLLISION_OBJECT, 
 /** The rigid body. */
 // =1
	RIGID_BODY,
	// CO_GHOST_OBJECT keeps track of all objects overlapping its AABB and that pass its collision filter
	/** The ghost object. */
	// It is useful for collision sensors, explosion objects, character controller etc.
	GHOST_OBJECT,
	
	/** The soft body. */
	SOFT_BODY
}
