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

package com.bulletphysics.linearmath;

/**
 * DefaultMotionState provides a common implementation to synchronize world transforms
 * with offsets.
 * 
 * @author jezek2
 */
public class DefaultMotionState extends MotionState {

	/** Current interpolated world transform, used to draw object. */
	public final Transform graphicsWorldTrans = new Transform();
	
	/** Center of mass offset transform, used to adjust graphics world transform. */
	public final Transform centerOfMassOffset = new Transform();
	
	/** Initial world transform. */
	public final Transform startWorldTrans = new Transform();
	
	/**
	 * Creates a new DefaultMotionState with all transforms set to identity.
	 */
	public DefaultMotionState() {
		graphicsWorldTrans.setIdentity();
		centerOfMassOffset.setIdentity();
		startWorldTrans.setIdentity();
	}

	/**
	 * Creates a new DefaultMotionState with initial world transform and center
	 * of mass offset transform set to identity.
	 */
	public DefaultMotionState(Transform startTrans) {
		this.graphicsWorldTrans.set(startTrans);
		centerOfMassOffset.setIdentity();
		this.startWorldTrans.set(startTrans);
	}
	
	/**
	 * Creates a new DefaultMotionState with initial world transform and center
	 * of mass offset transform.
	 */
	public DefaultMotionState(Transform startTrans, Transform centerOfMassOffset) {
		this.graphicsWorldTrans.set(startTrans);
		this.centerOfMassOffset.set(centerOfMassOffset);
		this.startWorldTrans.set(startTrans);
	}
	
	public Transform getWorldTransform(Transform out) {
		out.inverse(centerOfMassOffset);
		out.mul(graphicsWorldTrans);
		return out;
	}

	public void setWorldTransform(Transform centerOfMassWorldTrans) {
		graphicsWorldTrans.set(centerOfMassWorldTrans);
		graphicsWorldTrans.mul(centerOfMassOffset);
	}

}
