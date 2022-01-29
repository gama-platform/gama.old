/*******************************************************************************************************
 *
 * DistanceInput.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.collision;

import org.jbox2d.collision.Distance.DistanceProxy;
import org.jbox2d.common.Transform;

/**
 * Input for Distance.
 * You have to option to use the shape radii
 * in the computation.
 *
 */
public class DistanceInput {
	
	/** The proxy A. */
	public DistanceProxy proxyA = new DistanceProxy();
	
	/** The proxy B. */
	public DistanceProxy proxyB = new DistanceProxy();
	
	/** The transform A. */
	public Transform transformA = new Transform();
	
	/** The transform B. */
	public Transform transformB = new Transform();
	
	/** The use radii. */
	public boolean useRadii;
}
