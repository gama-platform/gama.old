/*******************************************************************************************************
 *
 * DestructionListener.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 4:23:30 AM Jul 15, 2010
 */
package org.jbox2d.callbacks;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.Joint;

/**
 * Joints and fixtures are destroyed when their associated
 * body is destroyed. Implement this listener so that you
 * may nullify references to these joints and shapes.
 * @author Daniel Murphy
 */
public interface DestructionListener {
	
	/**
	 * Called when any joint is about to be destroyed due
	 * to the destruction of one of its attached bodies.
	 * @param joint
	 */
	void sayGoodbye(Joint joint);
	
	/**
	 * Called when any fixture is about to be destroyed due
	 * to the destruction of its parent body.
	 * @param fixture
	 */
	void sayGoodbye(Fixture fixture);
}
