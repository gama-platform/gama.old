/*******************************************************************************************************
 *
 * MotionState.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

/**
 * MotionState allows the dynamics world to synchronize the updated world transforms with graphics. For optimizations,
 * potentially only moving objects get synchronized (using {@link #setWorldTransform setWorldTransform} method).
 *
 * @author jezek2
 */

public interface MotionState {

	/**
	 * Returns world transform.
	 */
	Transform getWorldTransform(Transform out);

	/**
	 * Sets world transform. This method is called by JBullet whenever an active object represented by this MotionState
	 * is moved or rotated.
	 */
	void setWorldTransform(Transform worldTrans);

}
