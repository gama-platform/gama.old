/*******************************************************************************************************
 *
 * ContactAddedCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;

/**
 * Called when contact has been created between two collision objects. At least one of object must have
 * {@link CollisionFlags#CUSTOM_MATERIAL_CALLBACK} flag set.
 *
 * @see BulletGlobals#setContactAddedCallback
 * @author jezek2
 */
public interface ContactAddedCallback {

	/**
	 * Contact added.
	 *
	 * @param cp the cp
	 * @param colObj0 the col obj 0
	 * @param partId0 the part id 0
	 * @param index0 the index 0
	 * @param colObj1 the col obj 1
	 * @param partId1 the part id 1
	 * @param index1 the index 1
	 * @return true, if successful
	 */
	boolean contactAdded(ManifoldPoint cp, CollisionObject colObj0, int partId0, int index0, CollisionObject colObj1,
			int partId1, int index1);

}
