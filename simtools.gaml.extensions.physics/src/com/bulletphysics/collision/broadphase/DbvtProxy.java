/*******************************************************************************************************
 *
 * DbvtProxy.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

// Dbvt implementation by Nathanael Presson

package com.bulletphysics.collision.broadphase;

/**
 *
 * @author jezek2
 */
public class DbvtProxy extends BroadphaseProxy {

	/** The aabb. */
	public final DbvtAabbMm aabb = new DbvtAabbMm();
	
	/** The leaf. */
	public Dbvt.Node leaf;
	
	/** The links. */
	public final DbvtProxy[] links = new DbvtProxy[2];
	
	/** The stage. */
	public int stage;

	/**
	 * Instantiates a new dbvt proxy.
	 *
	 * @param userPtr the user ptr
	 * @param collisionFilterGroup the collision filter group
	 * @param collisionFilterMask the collision filter mask
	 */
	public DbvtProxy(Object userPtr, short collisionFilterGroup, short collisionFilterMask) {
		super(userPtr, collisionFilterGroup, collisionFilterMask);
	}

}
