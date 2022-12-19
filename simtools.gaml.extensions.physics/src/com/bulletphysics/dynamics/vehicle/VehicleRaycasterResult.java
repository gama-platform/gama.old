/*******************************************************************************************************
 *
 * VehicleRaycasterResult.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.vehicle;

import javax.vecmath.Vector3f;

/**
 * Vehicle raycaster result.
 * 
 * @author jezek2
 */
public class VehicleRaycasterResult {
	
	/** The hit point in world. */
	public final Vector3f hitPointInWorld  = new Vector3f();
	
	/** The hit normal in world. */
	public final Vector3f hitNormalInWorld  = new Vector3f();
	
	/** The dist fraction. */
	public float distFraction = -1f;

}
