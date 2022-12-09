/*******************************************************************************************************
 *
 * DispatcherInfo.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

/**
 * Current state of {@link Dispatcher}.
 *
 * @author jezek2
 */
public class DispatcherInfo {

	/** The time step. */
	public float timeStep;
	
	/** The step count. */
	public int stepCount;
	
	/** The dispatch func. */
	public DispatchFunc dispatchFunc;
	
	/** The time of impact. */
	public float timeOfImpact;
	
	/** The use continuous. */
	public boolean useContinuous;
	
	/** The enable sat convex. */
	public boolean enableSatConvex;
	
	/** The enable SPU. */
	public boolean enableSPU = true;
	
	/** The use epa. */
	public boolean useEpa = true;
	
	/** The allowed ccd penetration. */
	public float allowedCcdPenetration = 0.04f;

	/**
	 * Instantiates a new dispatcher info.
	 */
	public DispatcherInfo() {
		dispatchFunc = DispatchFunc.DISPATCH_DISCRETE;
		timeOfImpact = 1f;
	}

}
