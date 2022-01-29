/*******************************************************************************************************
 *
 * TimeStep.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics;

//updated to rev 100
/**
 * This is an internal structure.
 */
public class TimeStep {
	
	/** time step */
	public float dt;
	
	/** inverse time step (0 if dt == 0). */
	public float inv_dt;
	
	/** dt * inv_dt0 */
	public float dtRatio;
	
	/** The velocity iterations. */
	public int velocityIterations;
	
	/** The position iterations. */
	public int positionIterations;
	
	/** The warm starting. */
	public boolean warmStarting;
}
