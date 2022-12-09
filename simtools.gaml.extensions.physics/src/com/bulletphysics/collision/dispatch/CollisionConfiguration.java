/*******************************************************************************************************
 *
 * CollisionConfiguration.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;

/**
 * CollisionConfiguration allows to configure Bullet default collision algorithms.
 *
 * @author jezek2
 */
@FunctionalInterface
public interface CollisionConfiguration {

	/**
	 * Gets the collision algorithm create func.
	 *
	 * @param proxyType0 the proxy type 0
	 * @param proxyType1 the proxy type 1
	 * @return the collision algorithm create func
	 */
	CollisionAlgorithmCreateFunc getCollisionAlgorithmCreateFunc(BroadphaseNativeType proxyType0,
			BroadphaseNativeType proxyType1);

}
