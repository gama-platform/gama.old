/*******************************************************************************************************
 *
 * CollisionFilterGroups.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

/**
 * Common collision filter groups.
 * 
 * @author jezek2
 */
public class CollisionFilterGroups {

	/** The Constant DEFAULT_FILTER. */
	public static final short DEFAULT_FILTER   = 1;
	
	/** The Constant STATIC_FILTER. */
	public static final short STATIC_FILTER    = 2;
	
	/** The Constant KINEMATIC_FILTER. */
	public static final short KINEMATIC_FILTER = 4;
	
	/** The Constant DEBRIS_FILTER. */
	public static final short DEBRIS_FILTER    = 8;
	
	/** The Constant SENSOR_TRIGGER. */
	public static final short SENSOR_TRIGGER   = 16;
	
	/** The Constant CHARACTER_FILTER. */
	public static final short CHARACTER_FILTER = 32;
	
	/** The Constant ALL_FILTER. */
	public static final short ALL_FILTER       = -1; // all bits sets: DefaultFilter | StaticFilter | KinematicFilter | DebrisFilter | SensorTrigger
	
}
