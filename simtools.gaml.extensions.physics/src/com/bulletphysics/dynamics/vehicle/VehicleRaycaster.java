/*******************************************************************************************************
 *
 * VehicleRaycaster.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * VehicleRaycaster is provides interface for between vehicle simulation and raycasting.
 * 
 * @author jezek2
 */
public abstract class VehicleRaycaster {

	/**
	 * Cast ray.
	 *
	 * @param from the from
	 * @param to the to
	 * @param result the result
	 * @return the object
	 */
	public abstract Object castRay(Vector3f from, Vector3f to, VehicleRaycasterResult result);
	
}
