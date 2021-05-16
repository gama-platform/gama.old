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

import javax.vecmath.Vector3f;

/**
 * BroadphaseInterface for AABB overlapping object pairs.
 * 
 * @author jezek2
 */
public abstract class BroadphaseInterface {

	public abstract BroadphaseProxy createProxy(Vector3f aabbMin, Vector3f aabbMax, BroadphaseNativeType shapeType, Object userPtr, short collisionFilterGroup, short collisionFilterMask, Dispatcher dispatcher, Object multiSapProxy);

	public abstract void destroyProxy(BroadphaseProxy proxy, Dispatcher dispatcher);

	public abstract void setAabb(BroadphaseProxy proxy, Vector3f aabbMin, Vector3f aabbMax, Dispatcher dispatcher);

	///calculateOverlappingPairs is optional: incremental algorithms (sweep and prune) might do it during the set aabb
	public abstract void calculateOverlappingPairs(Dispatcher dispatcher);

	public abstract OverlappingPairCache getOverlappingPairCache();
	
	///getAabb returns the axis aligned bounding box in the 'global' coordinate frame
	///will add some transform later
	public abstract void getBroadphaseAabb(Vector3f aabbMin, Vector3f aabbMax);

	public abstract void printStats();
	
}
