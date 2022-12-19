/*******************************************************************************************************
 *
 * WheelInfoConstructionInfo.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * 
 * @author jezek2
 */
public class WheelInfoConstructionInfo {

	/** The chassis connection CS. */
	public final Vector3f chassisConnectionCS = new Vector3f();
	
	/** The wheel direction CS. */
	public final Vector3f wheelDirectionCS = new Vector3f();
	
	/** The wheel axle CS. */
	public final Vector3f wheelAxleCS = new Vector3f();
	
	/** The suspension rest length. */
	public float suspensionRestLength;
	
	/** The max suspension travel cm. */
	public float maxSuspensionTravelCm;
	
	/** The max suspension force. */
	public float maxSuspensionForce;
	
	/** The wheel radius. */
	public float wheelRadius;
	
	/** The suspension stiffness. */
	public float suspensionStiffness;
	
	/** The wheels damping compression. */
	public float wheelsDampingCompression;
	
	/** The wheels damping relaxation. */
	public float wheelsDampingRelaxation;
	
	/** The friction slip. */
	public float frictionSlip;
	
	/** The b is front wheel. */
	public boolean bIsFrontWheel;
	
}
