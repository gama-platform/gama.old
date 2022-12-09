/*******************************************************************************************************
 *
 * BroadphaseProxy.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;

/**
 * BroadphaseProxy is the main class that can be used with the Bullet broadphases.
 * It stores collision shape type information, collision filter information and
 * a client object, typically a {@link CollisionObject} or {@link RigidBody}.
 * 
 * @author jezek2
 */
public class BroadphaseProxy {

	/** The client object. */
	// Usually the client CollisionObject or Rigidbody class
	public Object clientObject;
	
	/** The collision filter group. */
	// TODO: mask
	public short collisionFilterGroup;
	
	/** The collision filter mask. */
	public short collisionFilterMask;
	
	/** The multi sap parent proxy. */
	public Object multiSapParentProxy;
	
	/** The unique id. */
	public int uniqueId; // uniqueId is introduced for paircache. could get rid of this, by calculating the address offset etc.

	/**
	 * Instantiates a new broadphase proxy.
	 */
	public BroadphaseProxy() {
	}
	
	/**
	 * Instantiates a new broadphase proxy.
	 *
	 * @param userPtr the user ptr
	 * @param collisionFilterGroup the collision filter group
	 * @param collisionFilterMask the collision filter mask
	 */
	public BroadphaseProxy(Object userPtr, short collisionFilterGroup, short collisionFilterMask) {
		this(userPtr, collisionFilterGroup, collisionFilterMask, null);
	}
	
	/**
	 * Instantiates a new broadphase proxy.
	 *
	 * @param userPtr the user ptr
	 * @param collisionFilterGroup the collision filter group
	 * @param collisionFilterMask the collision filter mask
	 * @param multiSapParentProxy the multi sap parent proxy
	 */
	public BroadphaseProxy(Object userPtr, short collisionFilterGroup, short collisionFilterMask, Object multiSapParentProxy) {
		this.clientObject = userPtr;
		this.collisionFilterGroup = collisionFilterGroup;
		this.collisionFilterMask = collisionFilterMask;
		this.multiSapParentProxy = multiSapParentProxy;
	}

	/**
	 * Gets the uid.
	 *
	 * @return the uid
	 */
	public int getUid() {
		return uniqueId;
	}
	
}
