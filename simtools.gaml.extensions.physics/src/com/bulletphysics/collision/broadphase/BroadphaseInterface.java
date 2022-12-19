/*******************************************************************************************************
 *
 * BroadphaseInterface.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * BroadphaseInterface for AABB overlapping object pairs.
 *
 * @author jezek2
 */

public interface BroadphaseInterface {

	/**
	 * Creates the proxy.
	 *
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @param shapeType the shape type
	 * @param userPtr the user ptr
	 * @param collisionFilterGroup the collision filter group
	 * @param collisionFilterMask the collision filter mask
	 * @param dispatcher the dispatcher
	 * @param multiSapProxy the multi sap proxy
	 * @return the broadphase proxy
	 */
	BroadphaseProxy createProxy( Vector3f aabbMin, Vector3f aabbMax, BroadphaseNativeType shapeType,
			Object userPtr, short collisionFilterGroup, short collisionFilterMask, Dispatcher dispatcher,
			Object multiSapProxy);

	/**
	 * Destroy proxy.
	 *
	 * @param proxy the proxy
	 * @param dispatcher the dispatcher
	 */
	void destroyProxy( BroadphaseProxy proxy, Dispatcher dispatcher);

	/**
	 * Sets the aabb.
	 *
	 * @param proxy the proxy
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @param dispatcher the dispatcher
	 */
	void setAabb( BroadphaseProxy proxy, Vector3f aabbMin, Vector3f aabbMax, Dispatcher dispatcher);

	/**
	 * Calculate overlapping pairs.
	 *
	 * @param dispatcher the dispatcher
	 */
	/// calculateOverlappingPairs is optional: incremental algorithms (sweep and prune) might do it during the set aabb
	void calculateOverlappingPairs( Dispatcher dispatcher);

	/**
	 * Gets the overlapping pair cache.
	 *
	 * @return the overlapping pair cache
	 */
	OverlappingPairCache getOverlappingPairCache();

	/// getAabb returns the axis aligned bounding box in the 'global' coordinate frame
	/**
	 * Gets the broadphase aabb.
	 *
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @return the broadphase aabb
	 */
	/// will add some transform later
	void getBroadphaseAabb(Vector3f aabbMin, Vector3f aabbMax);

	/**
	 * Prints the stats.
	 */
	void printStats();

}
