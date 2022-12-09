/*******************************************************************************************************
 *
 * CollisionFlags.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.ContactAddedCallback;

/**
 * Flags for collision objects.
 * 
 * @author jezek2
 */
public class CollisionFlags {

	/** Sets this collision object as static. */
	public static final int STATIC_OBJECT            = 1;
	
	/** Sets this collision object as kinematic. */
	public static final int KINEMATIC_OBJECT         = 2;
	
	/** Disables contact response. */
	public static final int NO_CONTACT_RESPONSE      = 4;
	
	/**
	 * Enables calling {@link ContactAddedCallback} for collision objects. This
	 * allows per-triangle material (friction/restitution).
	 */
	public static final int CUSTOM_MATERIAL_CALLBACK = 8;
	
	/** The Constant CHARACTER_OBJECT. */
	public static final int CHARACTER_OBJECT         = 16;
	
}
