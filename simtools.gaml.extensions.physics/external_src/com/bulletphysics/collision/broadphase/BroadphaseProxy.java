/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

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

	// Usually the client CollisionObject or Rigidbody class
	public Object clientObject;
	
	// TODO: mask
	public short collisionFilterGroup;
	public short collisionFilterMask;
	
	public Object multiSapParentProxy;
	
	public int uniqueId; // uniqueId is introduced for paircache. could get rid of this, by calculating the address offset etc.

	public BroadphaseProxy() {
	}
	
	public BroadphaseProxy(Object userPtr, short collisionFilterGroup, short collisionFilterMask) {
		this(userPtr, collisionFilterGroup, collisionFilterMask, null);
	}
	
	public BroadphaseProxy(Object userPtr, short collisionFilterGroup, short collisionFilterMask, Object multiSapParentProxy) {
		this.clientObject = userPtr;
		this.collisionFilterGroup = collisionFilterGroup;
		this.collisionFilterMask = collisionFilterMask;
		this.multiSapParentProxy = multiSapParentProxy;
	}

	public int getUid() {
		return uniqueId;
	}
	
}
