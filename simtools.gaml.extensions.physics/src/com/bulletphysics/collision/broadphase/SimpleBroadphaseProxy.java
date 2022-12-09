/*******************************************************************************************************
 *
 * SimpleBroadphaseProxy.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
class SimpleBroadphaseProxy extends BroadphaseProxy {

	/** The min. */
	protected final Vector3f min = new Vector3f();
	
	/** The max. */
	protected final Vector3f max = new Vector3f();
	
	/**
	 * Instantiates a new simple broadphase proxy.
	 */
	public SimpleBroadphaseProxy() {
	}

	/**
	 * Instantiates a new simple broadphase proxy.
	 *
	 * @param minpt the minpt
	 * @param maxpt the maxpt
	 * @param shapeType the shape type
	 * @param userPtr the user ptr
	 * @param collisionFilterGroup the collision filter group
	 * @param collisionFilterMask the collision filter mask
	 * @param multiSapProxy the multi sap proxy
	 */
	public SimpleBroadphaseProxy(Vector3f minpt, Vector3f maxpt, BroadphaseNativeType shapeType, Object userPtr, short collisionFilterGroup, short collisionFilterMask, Object multiSapProxy) {
		super(userPtr, collisionFilterGroup, collisionFilterMask, multiSapProxy);
		this.min.set(minpt);
		this.max.set(maxpt);
	}
	
}
