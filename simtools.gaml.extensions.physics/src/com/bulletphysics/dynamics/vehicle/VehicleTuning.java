/*******************************************************************************************************
 *
 * VehicleTuning.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.dynamics.vehicle;

/**
 * Vehicle tuning parameters.
 * 
 * @author jezek2
 */
public class VehicleTuning {

	/** The suspension stiffness. */
	public float suspensionStiffness = 5.88f;
	
	/** The suspension compression. */
	public float suspensionCompression = 0.83f;
	
	/** The suspension damping. */
	public float suspensionDamping = 0.88f;
	
	/** The max suspension travel cm. */
	public float maxSuspensionTravelCm = 500f;
	
	/** The max suspension force. */
	public float maxSuspensionForce = 6000f;
	
	/** The friction slip. */
	public float frictionSlip = 10.5f;
	
}
