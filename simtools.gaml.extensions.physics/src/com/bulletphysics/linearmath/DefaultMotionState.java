/*******************************************************************************************************
 *
 * DefaultMotionState.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

/**
 * DefaultMotionState provides a common implementation to synchronize world transforms with offsets.
 *
 * @author jezek2
 */
public class DefaultMotionState implements MotionState {

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
	 * Creates a new DefaultMotionState with initial world transform and center of mass offset transform set to
	 * identity.
	 */
	public DefaultMotionState(final Transform startTrans) {
		this.graphicsWorldTrans.set(startTrans);
		centerOfMassOffset.setIdentity();
		this.startWorldTrans.set(startTrans);
	}

	/**
	 * Creates a new DefaultMotionState with initial world transform and center of mass offset transform.
	 */
	public DefaultMotionState(final Transform startTrans, final Transform centerOfMassOffset) {
		this.graphicsWorldTrans.set(startTrans);
		this.centerOfMassOffset.set(centerOfMassOffset);
		this.startWorldTrans.set(startTrans);
	}

	@Override
	public Transform getWorldTransform(final Transform out) {
		out.inverse(centerOfMassOffset);
		out.mul(graphicsWorldTrans);
		return out;
	}

	@Override
	public void setWorldTransform(final Transform centerOfMassWorldTrans) {
		graphicsWorldTrans.set(centerOfMassWorldTrans);
		graphicsWorldTrans.mul(centerOfMassOffset);
	}

}
